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

import java.util.Map;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.SortOrder;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.support.QueryContexts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.MongoClientSettings;

/**
 * Translation tests for the {@link MongoQueryProcessor} find path — pure, no database.
 *
 * @author Mark Hoffmann
 */
class MongoQueryProcessorTest {

	private final MongoQueryProcessor processor = new MongoQueryProcessor();

	private EClass person;
	private EAttribute id;
	private EAttribute name;
	private EAttribute age;
	private EReference address;
	private EReference friend;
	private EAttribute street;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		EPackage pkg = ecore.createEPackage();
		pkg.setName("test");
		pkg.setNsURI("http://test/query/1.0");

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

		EClass addressClass = ecore.createEClass();
		addressClass.setName("Address");
		street = ecore.createEAttribute();
		street.setName("street");
		street.setEType(EcorePackage.Literals.ESTRING);
		addressClass.getEStructuralFeatures().add(street);

		address = ecore.createEReference();
		address.setName("address");
		address.setEType(addressClass);
		address.setContainment(true);
		person.getEStructuralFeatures().add(address);

		friend = ecore.createEReference();
		friend.setName("friend");
		friend.setEType(person);
		friend.setContainment(false);
		person.getEStructuralFeatures().add(friend);

		pkg.getEClassifiers().add(person);
		pkg.getEClassifiers().add(addressClass);
	}

	private QueryContext context() {
		return QueryContexts.of(person, null);
	}

	private BsonDocument render(Bson bson) {
		return bson.toBsonDocument(BsonDocument.class, MongoClientSettings.getDefaultCodecRegistry());
	}

	private MongoQueryPlan translate(Query query) throws QueryException {
		return translate(query, context());
	}

	private MongoQueryPlan translate(Query query, QueryContext ctx) throws QueryException {
		return (MongoQueryPlan) processor.translate(query, ctx);
	}

	@Test
	void backendAndCapabilities() {
		assertThat(processor.backend()).isEqualTo("mongo");
		assertThat(processor.capabilities().maxFeaturePathDepth()).isEqualTo(-1);
	}

	@Test
	void emptyQueryTranslatesToMatchAll() throws QueryException {
		MongoQueryPlan plan = translate(QueryBuilder.create().build());
		assertThat(plan.shape()).isEqualTo(QueryShape.OBJECTS);
		assertThat(plan.filter()).isNull();
		assertThat(plan.sort()).isNull();
	}

	@Test
	void eqOnTypedFeature() throws QueryException {
		MongoQueryPlan plan = translate(QueryBuilder.create().where(age).eq(42).build());
		assertThat(render(plan.filter())).isEqualTo(BsonDocument.parse("{'age': 42}"));
	}

	@Test
	void idAttributeMapsToUnderscoreId() throws QueryException {
		MongoQueryPlan plan = translate(QueryBuilder.create().where(id).eq("abc").build());
		assertThat(render(plan.filter())).isEqualTo(BsonDocument.parse("{'_id': 'abc'}"));
	}

	@Test
	void chainedAndOrNot() throws QueryException {
		Query query = QueryBuilder.create()
				.where(age).gte(18)
				.and(age).lt(65)
				.or(name).eq("smith")
				.not(name).eq("test")
				.build();
		BsonDocument filter = render(translate(query).filter());

		BsonDocument expected = BsonDocument.parse(
				"{'$and': [{'$or': [{'$and': [{'age': {'$gte': 18}}, {'age': {'$lt': 65}}]}, {'name': 'smith'}]},"
						+ " {'$nor': [{'name': 'test'}]}]}");
		assertThat(filter).isEqualTo(expected);
	}

	@Test
	void stringMatchers() throws QueryException {
		BsonDocument contains = render(translate(
				QueryBuilder.create().where(name).contains("mit").build()).filter());
		assertThat(contains.getRegularExpression("name").getPattern()).contains("mit");

		BsonDocument like = render(translate(
				QueryBuilder.create().where(name).like("sm_th%").build()).filter());
		assertThat(like.getRegularExpression("name").getPattern()).isEqualTo("^sm.th.*$");
	}

	@Test
	void caseInsensitiveEqUsesAnchoredRegex() throws QueryException {
		BsonDocument filter = render(translate(
				QueryBuilder.create().where(name).toLower().eq("Smith").build()).filter());
		assertThat(filter.getRegularExpression("name").getPattern()).startsWith("^\\Q").endsWith("\\E$");
		assertThat(filter.getRegularExpression("name").getOptions()).isEqualTo("i");
	}

	@Test
	void rangeWithBounds() throws QueryException {
		BsonDocument filter = render(translate(
				QueryBuilder.create().where(age).inRange(18, 65, true, false).build()).filter());
		BsonDocument expected = BsonDocument.parse(
				"{'$and': [{'age': {'$gte': 18}}, {'age': {'$lt': 65}}]}");
		assertThat(filter).isEqualTo(expected);
	}

	@Test
	void nestedContainmentPathUsesDotNotation() throws QueryException {
		BsonDocument filter = render(translate(
				QueryBuilder.create().where(address, street).eq("Main St").build()).filter());
		assertThat(filter).isEqualTo(BsonDocument.parse("{'address.street': 'Main St'}"));
	}

	@Test
	void parameterPlaceholderIsResolved() throws QueryException {
		Query query = QueryBuilder.create().where(age).eqParam("minAge").build();
		QueryContext ctx = QueryContexts.of(person, null, Map.of("minAge", 21), null);
		BsonDocument filter = render(translate(query, ctx).filter());
		assertThat(filter).isEqualTo(BsonDocument.parse("{'age': 21}"));
	}

	@Test
	void unboundParameterFailsTranslation() {
		Query query = QueryBuilder.create().where(age).eqParam("minAge").build();
		assertThatThrownBy(() -> translate(query))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining(":minAge");
	}

	@Test
	void sortSkipLimitAndCount() throws QueryException {
		Query query = QueryBuilder.create()
				.where(age).gte(18)
				.sortBy(age, SortOrder.ASC)
				.sortBy(name, SortOrder.DESC)
				.skip(5)
				.limit(10)
				.build();
		MongoQueryPlan plan = translate(query);
		assertThat(render(plan.sort()))
				.isEqualTo(BsonDocument.parse("{'age': 1, 'name': -1}"));
		assertThat(plan.skip()).isEqualTo(5);
		assertThat(plan.limit()).isEqualTo(10);

		MongoQueryPlan countPlan = translate(QueryBuilder.create().where(age).gte(18).count().build());
		assertThat(countPlan.shape()).isEqualTo(QueryShape.COUNT);
		assertThat(render(countPlan.filter())).isEqualTo(BsonDocument.parse("{'age': {'$gte': 18}}"));
	}

	@Test
	void typeFilterIsSatisfiedByCollectionLayout() throws QueryException {
		Query query = QueryBuilder.create().from(person).where(age).eq(42).build();
		assertThat(processor.validate(query, person).getSeverity()).isEqualTo(Diagnostic.OK);
		// from() adds no filter — the collection is the type
		assertThat(render(translate(query).filter())).isEqualTo(BsonDocument.parse("{'age': 42}"));
	}

	@Test
	void crossDocumentPathIsRefusedByValidate() {
		Query query = QueryBuilder.create().where(friend, name).eq("smith").build();
		Diagnostic diagnostic = processor.validate(query, person);
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.anySatisfy(child -> {
					assertThat(child.getCode()).isEqualTo(MongoQueryProcessor.CODE_NON_EMBEDDED_PATH);
					assertThat(child.getMessage()).contains("friend.name");
				});
	}

	@Test
	void projectionTranslatesToMatchAndProject() throws QueryException {
		Query query = QueryBuilder.create()
				.where(age).gte(18)
				.selectAs("personName", name)
				.select(address, street)
				.build();
		MongoQueryPlan plan = translate(query);

		assertThat(plan.shape()).isEqualTo(QueryShape.PROJECTION);
		assertThat(plan.aggregation()).isTrue();
		assertThat(plan.rowKeys()).containsExactly("personName", "address_street");
		assertThat(plan.rowAliases()).containsExactly("personName", null);
		assertThat(plan.pipeline()).hasSize(2);
		assertThat(render(plan.pipeline().get(0)))
				.isEqualTo(BsonDocument.parse("{'$match': {'age': {'$gte': 18}}}"));
		assertThat(render(plan.pipeline().get(1))).isEqualTo(BsonDocument.parse(
				"{'$project': {'_id': 0, 'personName': '$name', 'address_street': '$address.street'}}"));
	}

	@Test
	void distinctProjectionGroupsOverKeys() throws QueryException {
		Query query = QueryBuilder.create().selectAs("n", name).distinct().build();
		MongoQueryPlan plan = translate(query);

		assertThat(plan.pipeline()).hasSize(2);
		assertThat(render(plan.pipeline().get(0)))
				.isEqualTo(BsonDocument.parse("{'$group': {'_id': {'n': '$name'}}}"));
		assertThat(render(plan.pipeline().get(1)))
				.isEqualTo(BsonDocument.parse("{'$project': {'_id': 0, 'n': '$_id.n'}}"));
	}

	@Test
	void groupedAggregationBuildsGroupAndProject() throws QueryException {
		Query query = QueryBuilder.create()
				.select(name)
				.avg("avgAge", age)
				.countOf("cnt", age)
				.groupBy(name)
				.build();
		MongoQueryPlan plan = translate(query);

		assertThat(plan.shape()).isEqualTo(QueryShape.AGGREGATION);
		assertThat(plan.pipeline()).hasSize(2);
		assertThat(render(plan.pipeline().get(0))).isEqualTo(BsonDocument.parse(
				"{'$group': {'_id': {'name': '$name'}, 'avgAge': {'$avg': '$age'}, 'cnt': {'$sum': 1}}}"));
		assertThat(render(plan.pipeline().get(1))).isEqualTo(BsonDocument.parse(
				"{'$project': {'_id': 0, 'name': '$_id.name', 'avgAge': 1, 'cnt': 1}}"));
	}

	@Test
	void ungroupedAggregateGroupsOverNull() throws QueryException {
		Query query = QueryBuilder.create().max("maxAge", age).build();
		MongoQueryPlan plan = translate(query);

		assertThat(render(plan.pipeline().get(0))).isEqualTo(BsonDocument.parse(
				"{'$group': {'_id': null, 'maxAge': {'$max': '$age'}}}"));
	}

	@Test
	void pipelineSortSkipLimitAddressOutputKeys() throws QueryException {
		EAttribute avgAge = EcoreFactory.eINSTANCE.createEAttribute();
		avgAge.setName("avgAge");
		avgAge.setEType(EcorePackage.Literals.EDOUBLE);

		Query query = QueryBuilder.create()
				.select(name)
				.avg("avgAge", age)
				.groupBy(name)
				.sortBy(avgAge, SortOrder.DESC)
				.skip(2)
				.limit(5)
				.build();
		MongoQueryPlan plan = translate(query);

		assertThat(plan.pipeline()).hasSize(5);
		assertThat(render(plan.pipeline().get(2))).isEqualTo(BsonDocument.parse("{'$sort': {'avgAge': -1}}"));
		assertThat(render(plan.pipeline().get(3))).isEqualTo(BsonDocument.parse("{'$skip': 2}"));
		assertThat(render(plan.pipeline().get(4))).isEqualTo(BsonDocument.parse("{'$limit': 5}"));
	}

	@Test
	void sortOnNonOutputKeyIsRefused() {
		Query query = QueryBuilder.create()
				.avg("avgAge", age)
				.sortBy(name, SortOrder.ASC)
				.build();
		assertThatThrownBy(() -> translate(query))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("name");
	}

	@Test
	void plainSubjectOutsideGroupByIsRefused() {
		Query query = QueryBuilder.create()
				.select(name)
				.avg("avgAge", age)
				.groupBy(age)
				.build();
		assertThatThrownBy(() -> translate(query))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("name");
	}

	@Test
	void distinctWithoutProjectionIsRefusedByValidate() {
		Query distinct = QueryBuilder.create().where(age).eq(1).distinct().build();
		Diagnostic diagnostic = processor.validate(distinct, person);
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.anySatisfy(child -> assertThat(child.getCode())
						.isEqualTo(MongoQueryProcessor.CODE_DISTINCT_WITHOUT_PROJECTION));
	}

	@Test
	void likeRegexTranslation() {
		assertThat(MongoQueryProcessor.likeToRegex("a%b_c")).isEqualTo("^a.*b.c$");
		assertThat(MongoQueryProcessor.likeToRegex("100$")).isEqualTo("^100\\$$");
		assertThat(MongoQueryProcessor.likeToRegex("a.b")).isEqualTo("^a\\.b$");
	}
}
