/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.persistence.mongo.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.eclipse.fennec.model.query.builder.Expressions.add;
import static org.eclipse.fennec.model.query.builder.Expressions.aliasRef;
import static org.eclipse.fennec.model.query.builder.Expressions.all;
import static org.eclipse.fennec.model.query.builder.Expressions.concat;
import static org.eclipse.fennec.model.query.builder.Expressions.count;
import static org.eclipse.fennec.model.query.builder.Expressions.and;
import static org.eclipse.fennec.model.query.builder.Expressions.any;
import static org.eclipse.fennec.model.query.builder.Expressions.isOf;
import static org.eclipse.fennec.model.query.builder.Expressions.literal;
import static org.eclipse.fennec.model.query.builder.Expressions.not;
import static org.eclipse.fennec.model.query.builder.Expressions.or;
import static org.eclipse.fennec.model.query.builder.Expressions.param;
import static org.eclipse.fennec.model.query.builder.Expressions.path;
import static org.eclipse.fennec.model.query.builder.Expressions.pathAs;
import static org.eclipse.fennec.model.query.builder.Expressions.propertyPath;

import java.util.Map;
import java.util.UUID;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.query.FilterStage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.TopStage;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.support.QueryContexts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.MongoClientSettings;

/**
 * Translation tests for the {@link MongoQueryProcessor} over the expression IR — pure,
 * no database.
 *
 * @author Mark Hoffmann
 */
class MongoQueryProcessorTest {

	private final MongoQueryProcessor processor = new MongoQueryProcessor();

	private EClass person;
	private EAttribute id;
	private EAttribute name;
	private EAttribute age;
	private EReference addresses;
	private EReference friend;
	private EAttribute street;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		person = ecore.createEClass();
		person.setName("Person");
		id = ecore.createEAttribute();
		id.setName("personId");
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		name = ecore.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		age = ecore.createEAttribute();
		age.setName("age");
		age.setEType(EcorePackage.Literals.EINT);
		person.getEStructuralFeatures().add(id);
		person.getEStructuralFeatures().add(name);
		person.getEStructuralFeatures().add(age);

		EClass address = ecore.createEClass();
		address.setName("Address");
		street = ecore.createEAttribute();
		street.setName("street");
		street.setEType(EcorePackage.Literals.ESTRING);
		address.getEStructuralFeatures().add(street);

		addresses = ecore.createEReference();
		addresses.setName("addresses");
		addresses.setEType(address);
		addresses.setUpperBound(-1);
		addresses.setContainment(true);
		person.getEStructuralFeatures().add(addresses);

		friend = ecore.createEReference();
		friend.setName("friend");
		friend.setEType(person);
		friend.setContainment(false);
		person.getEStructuralFeatures().add(friend);
	}

	private BsonDocument render(Bson bson) {
		return bson.toBsonDocument(BsonDocument.class, MongoClientSettings.getDefaultCodecRegistry());
	}

	private MongoQueryPlan translate(Query query) throws QueryException {
		return translate(query, QueryContexts.of(person, null));
	}

	private MongoQueryPlan translate(Query query, QueryContext context) throws QueryException {
		return (MongoQueryPlan) processor.translate(query, context);
	}

	@Test
	void idAndEqAndGroupedLogic() throws QueryException {
		MongoQueryPlan byId = translate(QueryBuilder.from(person).where(path(id).eq("abc")).build());
		assertThat(render(byId.filter())).isEqualTo(BsonDocument.parse("{'_id': 'abc'}"));

		MongoQueryPlan grouped = translate(QueryBuilder.from(person)
				.where(and(
						or(path(age).ge(18), path(age).ne(65)),
						not(path(name).isNull())))
				.build());
		// NE carries the $ne null guard, not(isNull) flips to the two-valued probe (issue #97)
		assertThat(render(grouped.filter())).isEqualTo(BsonDocument.parse(
				"{'$and': [{'$or': [{'age': {'$gte': 18}},"
						+ " {'$and': [{'age': {'$ne': 65}}, {'age': {'$ne': null}}]}]},"
						+ " {'name': {'$ne': null}}]}"));
	}

	@Test
	void negationPushesDownInsteadOfNor() throws QueryException {
		// not(age = 65) → the inverse operator with the $ne null guard — never $nor,
		// which would select documents whose comparison is UNKNOWN (issue #97)
		MongoQueryPlan notEq = translate(QueryBuilder.from(person)
				.where(not(path(age).eq(65))).build());
		assertThat(render(notEq.filter())).isEqualTo(BsonDocument.parse(
				"{'$and': [{'age': {'$ne': 65}}, {'age': {'$ne': null}}]}"));

		// double negation cancels
		MongoQueryPlan doubled = translate(QueryBuilder.from(person)
				.where(not(not(path(age).eq(65)))).build());
		assertThat(render(doubled.filter())).isEqualTo(BsonDocument.parse("{'age': 65}"));

		// De Morgan over junctions, inversion at the leaves
		MongoQueryPlan deMorgan = translate(QueryBuilder.from(person)
				.where(not(and(path(age).ge(40), path(name).eq("Alice")))).build());
		assertThat(render(deMorgan.filter())).isEqualTo(BsonDocument.parse(
				"{'$or': [{'age': {'$lt': 40}},"
						+ " {'$and': [{'name': {'$ne': 'Alice'}}, {'name': {'$ne': null}}]}]}"));

		// ¬between → outside the range with flipped inclusivity
		MongoQueryPlan notBetween = translate(QueryBuilder.from(person)
				.where(not(path(age).between(18, 65, true, false))).build());
		assertThat(render(notBetween.filter())).isEqualTo(BsonDocument.parse(
				"{'$or': [{'age': {'$lt': 18}}, {'age': {'$gte': 65}}]}"));
	}

	@Test
	void negatedInMatchAndQuantifiersCarryGuards() throws QueryException {
		// ¬IN → $nin plus the non-null guard
		MongoQueryPlan notIn = translate(QueryBuilder.from(person)
				.where(not(path(age).in(1, 2, 3))).build());
		assertThat(render(notIn.filter())).isEqualTo(BsonDocument.parse(
				"{'$and': [{'age': {'$nin': [1, 2, 3]}}, {'age': {'$ne': null}}]}"));

		// ¬IN with a null option can never be TRUE in SQL
		MongoQueryPlan notInNull = translate(QueryBuilder.from(person)
				.where(not(path(age).in(1, null))).build());
		assertThat(render(notInNull.filter())).isEqualTo(BsonDocument.parse("{'$expr': false}"));

		// ¬contains → $not regex plus the non-null guard
		MongoQueryPlan notMatch = translate(QueryBuilder.from(person)
				.where(not(path(name).contains("mit"))).build());
		String json = render(notMatch.filter()).toJson();
		assertThat(json).contains("$not").contains("mit").contains("{\"name\": {\"$ne\": null}}");

		// ¬∃p = ∀¬p — the forAll shape around the negated (inverted) inner predicate
		MongoQueryPlan notExists = translate(QueryBuilder.from(person)
				.where(not(any(propertyPath(addresses), a -> a.path(street).eq("Main St")))).build());
		assertThat(render(notExists.filter())).isEqualTo(BsonDocument.parse(
				"{'$nor': [{'addresses': {'$elemMatch': {'$nor': ["
						+ "{'$and': [{'street': {'$ne': 'Main St'}}, {'street': {'$ne': null}}]}]}}}]}"));

		// ¬∀p = ∃¬p — a plain $elemMatch of the negated inner predicate
		MongoQueryPlan notForAll = translate(QueryBuilder.from(person)
				.where(not(all(propertyPath(addresses), a -> a.path(street).eq("Main St")))).build());
		assertThat(render(notForAll.filter())).isEqualTo(BsonDocument.parse(
				"{'addresses': {'$elemMatch': "
						+ "{'$and': [{'street': {'$ne': 'Main St'}}, {'street': {'$ne': null}}]}}}"));
	}

	@Test
	void isNullBetweenIn() throws QueryException {
		MongoQueryPlan plan = translate(QueryBuilder.from(person)
				.where(and(
						path(name).isNotNull(),
						path(age).between(18, 65, true, false),
						path(age).in(1, 2, 3)))
				.build());
		assertThat(render(plan.filter())).isEqualTo(BsonDocument.parse(
				"{'$and': [{'name': {'$ne': null}},"
						+ " {'$and': [{'age': {'$gte': 18}}, {'age': {'$lt': 65}}]},"
						+ " {'age': {'$in': [1, 2, 3]}}]}"));
	}

	@Test
	void stringMatchersAndCaseInsensitivity() throws QueryException {
		MongoQueryPlan contains = translate(QueryBuilder.from(person)
				.where(path(name).contains("mit")).build());
		assertThat(render(contains.filter()).getRegularExpression("name").getPattern()).contains("mit");

		MongoQueryPlan ci = translate(QueryBuilder.from(person)
				.where(path(name).startsWithIgnoreCase("Sm")).build());
		assertThat(render(ci.filter()).getRegularExpression("name").getOptions()).isEqualTo("i");

		MongoQueryPlan like = translate(QueryBuilder.from(person)
				.where(path(name).like("sm_th%")).build());
		assertThat(render(like.filter()).getRegularExpression("name").getPattern()).isEqualTo("^sm.th.*$");
	}

	@Test
	void quantifiersBecomeElemMatch() throws QueryException {
		MongoQueryPlan exists = translate(QueryBuilder.from(person)
				.where(any(propertyPath(addresses), a -> a.path(street).eq("Main St")))
				.build());
		assertThat(render(exists.filter())).isEqualTo(BsonDocument.parse(
				"{'addresses': {'$elemMatch': {'street': 'Main St'}}}"));

		MongoQueryPlan forAll = translate(QueryBuilder.from(person)
				.where(all(propertyPath(addresses), a -> a.path(street).isNotNull()))
				.build());
		assertThat(render(forAll.filter())).isEqualTo(BsonDocument.parse(
				"{'$nor': [{'addresses': {'$elemMatch': {'$nor': [{'street': {'$ne': null}}]}}}]}"));
	}

	@Test
	void arithmeticBecomesExprWithNullGuards() throws QueryException {
		MongoQueryPlan plan = translate(QueryBuilder.from(person)
				.where(path(age).plus(10).times(2).gt(90))
				.build());
		String json = render(plan.filter()).toJson();
		assertThat(json).contains("$expr").contains("$multiply").contains("$add").contains("$gt");
		// the referenced field carries a $ne null guard like FIELD_TO_FIELD
		assertThat(json).contains("\"$ne\": [\"$age\", null]");

		MongoQueryPlan negate = translate(QueryBuilder.from(person)
				.where(path(age).negated().lt(0))
				.build());
		assertThat(render(negate.filter()).toJson()).contains("$multiply").contains("-1");
	}

	@Test
	void extendedStringFunctionsRenderCpOperators() throws QueryException {
		MongoQueryPlan concat = translate(QueryBuilder.from(person)
				.where(concat(path(name), "!").eq("Bob!"))
				.build());
		assertThat(render(concat.filter()).toJson()).contains("$concat");

		MongoQueryPlan indexOf = translate(QueryBuilder.from(person)
				.where(path(name).indexOf("o").eq(1))
				.build());
		assertThat(render(indexOf.filter()).toJson()).contains("$indexOfCP");

		MongoQueryPlan substring = translate(QueryBuilder.from(person)
				.where(path(name).substring(-2).eq("ol"))
				.build());
		String json = render(substring.filter()).toJson();
		assertThat(json).contains("$substrCP").contains("$strLenCP").contains("$cond");
	}

	@Test
	void roundEmulatesHalfAwayFromZero() throws QueryException {
		// ROUND must not use $round (half to even) — the contract is half away from zero
		MongoQueryPlan round = translate(QueryBuilder.from(person)
				.where(path(age).dividedBy(4).round().eq(13))
				.build());
		String json = render(round.filter()).toJson();
		assertThat(json).contains("$cond").contains("$floor").contains("$ceil").doesNotContain("$round");

		MongoQueryPlan floor = translate(QueryBuilder.from(person)
				.where(path(age).dividedBy(4).floor().eq(7))
				.build());
		assertThat(render(floor.filter()).toJson()).contains("$floor");
	}

	@Test
	void temporalFunctionsRenderNativeDateOperators() throws QueryException {
		MongoQueryPlan year = translate(QueryBuilder.from(person)
				.where(path(age).year().eq(1990))
				.build());
		assertThat(render(year.filter()).toJson()).contains("$year");

		MongoQueryPlan second = translate(QueryBuilder.from(person)
				.where(path(age).second().eq(30))
				.build());
		assertThat(render(second.filter()).toJson()).contains("$second");
	}

	@Test
	void typePredicatesTranslateAgainstTheCodecDiscriminator() throws QueryException {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		EClass vehicle = ecore.createEClass();
		vehicle.setName("Vehicle");
		EClass car = ecore.createEClass();
		car.setName("Car");
		car.getESuperTypes().add(vehicle);
		EPackage pkg = ecore.createEPackage();
		pkg.setName("garage");
		pkg.setNsURI("urn:mongo:type:test");
		pkg.getEClassifiers().add(vehicle);
		pkg.getEClassifiers().add(car);
		// packages attached to their nsURI resource yield the full nsURI#//Car form —
		// like generated production models
		new ResourceImpl(URI.createURI(pkg.getNsURI())).getContents().add(pkg);
		EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
		try {

		// default config: closure over the concrete subtypes, URI values on _type
		Query isCar = QueryBuilder.from(vehicle).where(isOf(car)).build();
		MongoQueryPlan closure = (MongoQueryPlan) processor.translate(isCar,
				QueryContexts.of(vehicle, null));
		String closureJson = render(closure.filter()).toJson();
		assertThat(closureJson).contains("_type").contains("urn:mongo:type:test#//Car");

		// serialized supertypes: direct match on _type or the _supertype array
		ConfigurationResolver withSupertypes = ConfigurationResolver.builder()
				.optionsProperties(Map.of(CodecOptions.CODEC_SUPERTYPE_SERIALIZE, true))
				.build();
		MongoQueryPlan supertype = (MongoQueryPlan) processor.translate(isCar,
				QueryContexts.of(vehicle, null, null,
						Map.of(MongoPersistenceConstants.OPTION_CODEC_RESOLVER, withSupertypes)));
		assertThat(render(supertype.filter()).toJson()).contains("_supertype");

		// treat: the cast path guards the field predicate with the type filter
		EAttribute horsepower = ecore.createEAttribute();
		horsepower.setName("horsepower");
		horsepower.setEType(EcorePackage.Literals.EINT);
		car.getEStructuralFeatures().add(horsepower);
		MongoQueryPlan treat = (MongoQueryPlan) processor.translate(
				QueryBuilder.from(vehicle).where(pathAs(car, horsepower).gt(100)).build(),
				QueryContexts.of(vehicle, null));
		String treatJson = render(treat.filter()).toJson();
		assertThat(treatJson).contains("_type").contains("horsepower");

		// a configuration without a stored discriminator is refused
		ConfigurationResolver withoutType = ConfigurationResolver.builder()
				.optionsProperties(Map.of(CodecOptions.CODEC_TYPE_INCLUDE, false))
				.build();
		assertThatThrownBy(() -> processor.translate(isCar,
				QueryContexts.of(vehicle, null, null,
						Map.of(MongoPersistenceConstants.OPTION_CODEC_RESOLVER, withoutType))))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("discriminator");
		} finally {
			EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
		}
	}

	@Test
	void filteredCollectionCountsRenderSizeOverFilter() throws QueryException {
		// $size($filter) with $$it element references and a $ne-null guard (issue #86)
		MongoQueryPlan plan = translate(QueryBuilder.from(person)
				.where(count(propertyPath(addresses),
						a -> a.path(street).startsWith("Main")).eq(1))
				.build());
		String json = render(plan.filter()).toJson();
		assertThat(json).contains("$size").contains("$filter").contains("$$it.street")
				.contains("$regexMatch");

		// nested functions inside the cond are refused (v1 vocabulary)
		Query nested = QueryBuilder.from(person)
				.where(count(propertyPath(addresses),
						a -> a.path(street).length().gt(3)).ge(1))
				.build();
		assertThatThrownBy(() -> translate(nested))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("filtered collection count");
	}

	@Test
	void arithmeticInsideQuantifierIsRefused() {
		Query query = QueryBuilder.from(person)
				.where(any(propertyPath(addresses),
						a -> add(a.path(street).length(), 1).gt(3)))
				.build();
		assertThatThrownBy(() -> translate(query))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("quantifier");
	}

	@Test
	void sortSkipTopAndCount() throws QueryException {
		MongoQueryPlan plan = translate(QueryBuilder.from(person)
				.where(path(age).ge(18))
				.orderByAsc(age)
				.orderByDesc(name)
				.skip(5)
				.top(10)
				.build());
		assertThat(render(plan.sort())).isEqualTo(BsonDocument.parse("{'age': 1, 'name': -1}"));
		assertThat(plan.skip()).isEqualTo(5);
		assertThat(plan.limit()).isEqualTo(10);

		MongoQueryPlan count = translate(QueryBuilder.from(person)
				.where(path(age).gt(30)).countOnly().build());
		assertThat(count.shape()).isEqualTo(QueryShape.COUNT);
		assertThat(render(count.filter())).isEqualTo(BsonDocument.parse("{'age': {'$gt': 30}}"));
	}

	@Test
	void projectionAndDistinctPipeline() throws QueryException {
		MongoQueryPlan plan = translate(QueryBuilder.from(person)
				.where(path(age).ge(18))
				.selectAs("n", name)
				.select(addresses, street)
				.build());
		assertThat(plan.shape()).isEqualTo(QueryShape.PROJECTION);
		assertThat(plan.rowKeys()).containsExactly("n", "addresses_street");
		assertThat(render(plan.pipeline().get(1))).isEqualTo(BsonDocument.parse(
				"{'$project': {'_id': 0, 'n': '$name', 'addresses_street': '$addresses.street'}}"));

		MongoQueryPlan distinct = translate(QueryBuilder.from(person).selectAs("n", name).distinct().build());
		assertThat(render(distinct.pipeline().get(0)))
				.isEqualTo(BsonDocument.parse("{'$group': {'_id': {'n': '$name'}}}"));
	}

	@Test
	void groupByAggregationWithCountDistinct() throws QueryException {
		MongoQueryPlan plan = translate(QueryBuilder.from(person)
				.groupBy(name)
				.avg("avgAge", age)
				.countOf("cnt")
				.countDistinct("streets", addresses, street)
				.build());
		assertThat(plan.shape()).isEqualTo(QueryShape.AGGREGATION);
		assertThat(render(plan.pipeline().get(0))).isEqualTo(BsonDocument.parse(
				"{'$group': {'_id': {'name': '$name'}, 'avgAge': {'$avg': '$age'}, 'cnt': {'$sum': 1},"
						+ " 'streets': {'$addToSet': '$addresses.street'}}}"));
		assertThat(render(plan.pipeline().get(1))).isEqualTo(BsonDocument.parse(
				"{'$project': {'_id': 0, 'name': '$_id.name', 'avgAge': 1, 'cnt': 1,"
						+ " 'streets': {'$size': '$streets'}}}"));
		assertThat(plan.rowKeys()).containsExactly("name", "avgAge", "cnt", "streets");
	}

	@Test
	void aggregationAliasSortSortsTheOutputField() throws QueryException {
		// a bare AliasRef sort key is a plain $sort on the flattened output field
		// (issue #102) — native after $group, no SORT_EXPRESSION involved
		MongoQueryPlan plan = translate(QueryBuilder.from(person)
				.groupBy(name)
				.avg("avgAge", age)
				.orderByDesc(aliasRef("avgAge").toExpression())
				.build());
		assertThat(render(plan.pipeline().get(plan.pipeline().size() - 1))).isEqualTo(
				BsonDocument.parse("{'$sort': {'avgAge': -1}}"));

		Query unknown = QueryBuilder.from(person)
				.groupBy(name)
				.avg("avgAge", age)
				.orderByDesc(aliasRef("nope").toExpression())
				.build();
		assertThatThrownBy(() -> translate(unknown))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("nope");
	}

	@Test
	void multiStagePipelineIsServedNatively() throws QueryException {
		// Mongo declares PIPELINE — filter stage + groupBy + top compose in order
		QueryFactory factory = QueryFactory.eINSTANCE;
		Query query = QueryBuilder.from(person)
				.groupBy(name)
				.countOf("cnt")
				.build();
		FilterStage filter = factory.createFilterStage();
		Comparison adult = ExpressionFactory.eINSTANCE.createComparison();
		adult.setOperator(ComparisonOperator.GE);
		adult.setLeft(propertyPath(age));
		adult.setRight(literal(18));
		filter.setPredicate(adult);
		query.getApply().getStages().add(0, filter);
		TopStage top = factory.createTopStage();
		top.setCount(3);
		query.getApply().getStages().add(top);

		assertThat(processor.validate(query, person).getSeverity()).isEqualTo(Diagnostic.OK);
		MongoQueryPlan plan = translate(query);
		assertThat(render(plan.pipeline().get(0)))
				.isEqualTo(BsonDocument.parse("{'$match': {'age': {'$gte': 18}}}"));
		assertThat(render(plan.pipeline().get(plan.pipeline().size() - 1)))
				.isEqualTo(BsonDocument.parse("{'$limit': 3}"));
	}

	@Test
	void parameterBindingAndRefusal() throws QueryException {
		Query query = QueryBuilder.from(person).where(path(age).eq(param("minAge"))).build();
		MongoQueryPlan plan = translate(query, QueryContexts.of(person, null, Map.of("minAge", 21), null));
		assertThat(render(plan.filter())).isEqualTo(BsonDocument.parse("{'age': 21}"));

		assertThatThrownBy(() -> translate(query)).isInstanceOf(QueryException.class)
				.hasMessageContaining("minAge");
	}

	/**
	 * With a {@code ConverterService} in the context (issue #164), a parameter over a
	 * converter-claimed type lands in the filter in its persistence form — the same form
	 * the codec writes into the document — while plain types stay untouched.
	 */
	@Test
	void parameterOverConvertedTypeFiltersInThePersistenceForm() throws QueryException {
		EDataType uuidType = EcoreFactory.eINSTANCE.createEDataType();
		uuidType.setName("UUID");
		uuidType.setInstanceClass(UUID.class);
		EAttribute uid = EcoreFactory.eINSTANCE.createEAttribute();
		uid.setName("uid");
		uid.setEType(uuidType);
		person.getEStructuralFeatures().add(uid);

		UUID wanted = UUID.randomUUID();
		Query query = QueryBuilder.from(person).where(path(uid).eq(param("wanted"))).build();
		MongoQueryPlan plan = translate(query, QueryContexts.of(person,
				new DefaultConverterService(), Map.of("wanted", wanted), null));
		assertThat(render(plan.filter()))
				.isEqualTo(BsonDocument.parse("{'uid': '" + wanted + "'}"));
	}

	@Test
	void crossDocumentPathsAreRefusedByValidate() {
		Query query = QueryBuilder.from(person).where(path(friend, name).eq("smith")).build();
		Diagnostic diagnostic = processor.validate(query, person);
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren()).anySatisfy(
				child -> assertThat(child.getCode()).isEqualTo(MongoQueryProcessor.CODE_NON_EMBEDDED_PATH));

		Query nonEmbeddedQuantifier = QueryBuilder.from(person)
				.where(any(propertyPath(friend), a -> a.path(name).eq("x")))
				.build();
		assertThat(processor.validate(nonEmbeddedQuantifier, person).getSeverity()).isEqualTo(Diagnostic.ERROR);
	}

	@Test
	void distinctWithoutProjectionAndExpandAreRefused() {
		Query distinct = QueryBuilder.from(person).where(path(age).eq(1)).distinct().build();
		Diagnostic diagnostic = processor.validate(distinct, person);
		assertThat(diagnostic.getChildren()).anySatisfy(child -> assertThat(child.getCode())
				.isEqualTo(MongoQueryProcessor.CODE_DISTINCT_WITHOUT_PROJECTION));

		Query expand = QueryBuilder.from(person).expand(addresses).build();
		// EXPAND is not declared — validate refuses it via the capability mechanism
		assertThat(processor.validate(expand, person).getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThatThrownBy(() -> translate(expand)).isInstanceOf(QueryException.class);
	}

	@Test
	void fieldToFieldComparisonUsesExprWithNullGuards() throws QueryException {
		Query query = QueryBuilder.from(person).where(path(name).eq(path(id))).build();
		MongoQueryPlan plan = translate(query);
		assertThat(render(plan.filter())).isEqualTo(BsonDocument.parse(
				"{'$expr': {'$and': [{'$ne': ['$name', null]}, {'$ne': ['$_id', null]},"
						+ " {'$eq': ['$name', '$_id']}]}}"));

		// self-comparison: the guard is deduplicated
		Query same = QueryBuilder.from(person).where(path(name).ne(propertyPath(name))).build();
		assertThat(render(translate(same).filter())).isEqualTo(BsonDocument.parse(
				"{'$expr': {'$and': [{'$ne': ['$name', null]}, {'$ne': ['$name', '$name']}]}}"));
	}

	@Test
	void stringFunctionsUseExpr() throws QueryException {
		Query lower = QueryBuilder.from(person).where(path(name).toLower().eq("bob")).build();
		assertThat(render(translate(lower).filter())).isEqualTo(BsonDocument.parse(
				"{'$expr': {'$and': [{'$ne': ['$name', null]},"
						+ " {'$eq': [{'$toLower': '$name'}, {'$literal': 'bob'}]}]}}"));

		Query length = QueryBuilder.from(person).where(path(name).length().gt(3)).build();
		assertThat(render(translate(length).filter())).isEqualTo(BsonDocument.parse(
				"{'$expr': {'$and': [{'$ne': ['$name', null]},"
						+ " {'$gt': [{'$strLenCP': '$name'}, {'$literal': {'$numberLong': '3'}}]}]}}"));

		Query chained = QueryBuilder.from(person).where(path(name).trim().toUpper().eq("BOB")).build();
		assertThat(render(translate(chained).filter())).isEqualTo(BsonDocument.parse(
				"{'$expr': {'$and': [{'$ne': ['$name', null]},"
						+ " {'$eq': [{'$toUpper': {'$trim': {'input': '$name'}}}, {'$literal': 'BOB'}]}]}}"));
	}

	@Test
	void exprComparisonsInsideQuantifiersAreRefused() {
		Query query = QueryBuilder.from(person)
				.where(any(propertyPath(addresses), a -> a.path(street).toLower().eq("main st")))
				.build();
		assertThatThrownBy(() -> translate(query)).isInstanceOf(QueryException.class)
				.hasMessageContaining("quantifier");
	}

	@Test
	void rowSortAddressesOutputKeys() throws QueryException {
		EAttribute avgAge = EcoreFactory.eINSTANCE.createEAttribute();
		avgAge.setName("avgAge");
		avgAge.setEType(EcorePackage.Literals.EDOUBLE);
		Query query = QueryBuilder.from(person)
				.groupBy(name)
				.avg("avgAge", age)
				.orderByDesc(avgAge)
				.build();
		MongoQueryPlan plan = translate(query);
		assertThat(render(plan.sort())).isEqualTo(BsonDocument.parse("{'avgAge': -1}"));

		Query bad = QueryBuilder.from(person).avg("avgAge", age).orderByAsc(name).build();
		assertThatThrownBy(() -> translate(bad)).isInstanceOf(QueryException.class)
				.hasMessageContaining("output key");
	}

	@Test
	void likeRegexTranslation() {
		assertThat(MongoQueryProcessor.likeToRegex("a%b_c")).isEqualTo("^a.*b.c$");
		assertThat(MongoQueryProcessor.likeToRegex("100$")).isEqualTo("^100\\$$");
	}
}
