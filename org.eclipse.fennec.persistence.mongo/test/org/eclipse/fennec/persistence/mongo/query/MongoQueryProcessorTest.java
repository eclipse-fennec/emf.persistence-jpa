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
import static org.eclipse.fennec.model.query.builder.Expressions.all;
import static org.eclipse.fennec.model.query.builder.Expressions.concat;
import static org.eclipse.fennec.model.query.builder.Expressions.and;
import static org.eclipse.fennec.model.query.builder.Expressions.any;
import static org.eclipse.fennec.model.query.builder.Expressions.literal;
import static org.eclipse.fennec.model.query.builder.Expressions.not;
import static org.eclipse.fennec.model.query.builder.Expressions.or;
import static org.eclipse.fennec.model.query.builder.Expressions.param;
import static org.eclipse.fennec.model.query.builder.Expressions.path;
import static org.eclipse.fennec.model.query.builder.Expressions.propertyPath;

import java.util.Map;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.query.FilterStage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.TopStage;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
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
		assertThat(render(grouped.filter())).isEqualTo(BsonDocument.parse(
				"{'$and': [{'$or': [{'age': {'$gte': 18}}, {'age': {'$ne': 65}}]},"
						+ " {'$nor': [{'name': null}]}]}"));
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
