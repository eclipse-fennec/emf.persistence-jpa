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
package org.eclipse.fennec.persistence.query.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.eclipse.fennec.model.query.builder.Expressions.div;
import static org.eclipse.fennec.model.query.builder.Expressions.path;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.AggregateMethod;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.Pipeline;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.capabilities.QueryCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryCapabilitiesBuilder;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link QueryValidator} capability checking over the expression IR.
 *
 * @author Mark Hoffmann
 */
class QueryValidatorTest {

	private EClass person;
	private EAttribute name;
	private EAttribute age;
	private EReference address;
	private EAttribute street;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		person = ecore.createEClass();
		person.setName("Person");
		name = ecore.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		person.getEStructuralFeatures().add(name);
		age = ecore.createEAttribute();
		age.setName("age");
		age.setEType(EcorePackage.Literals.EINT);
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
		person.getEStructuralFeatures().add(address);
	}

	private Query eqQuery(Object value, EStructuralFeature... segments) {
		Query query = QueryFactory.eINSTANCE.createQuery();
		query.setFrom(person);
		query.setPredicate(path(segments).eq(value));
		return query;
	}

	private QueryCapabilities capabilities(QueryFeature... supported) {
		return QueryCapabilitiesBuilder.create()
				.support(QueryFeature.TYPE_FILTER)
				.support(supported)
				.build();
	}

	@Test
	void supportedQueryValidatesOk() {
		Diagnostic diagnostic = QueryValidator.validate(eqQuery(42, name), person,
				capabilities(QueryFeature.WHERE_EQ));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.OK);
	}

	@Test
	void unsupportedFeatureYieldsErrorNamingTheFeature() {
		Diagnostic diagnostic = QueryValidator.validate(eqQuery(42, name), person, capabilities());
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren()).hasSize(1);

		Diagnostic child = diagnostic.getChildren().get(0);
		assertThat(child.getSource()).isEqualTo(QueryValidator.DIAGNOSTIC_SOURCE);
		assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_UNSUPPORTED_FEATURE);
		assertThat(child.getMessage()).contains("WHERE_EQ").contains("Person");
		assertThat(child.getData().contains(QueryFeature.WHERE_EQ)).isTrue();
	}

	@Test
	void scoreRequiresTheRankingCapability() {
		// issue #100: only ranking backends declare SCORE — everyone else refuses
		Query query = QueryFactory.eINSTANCE.createQuery();
		query.setFrom(person);
		query.setPredicate(Expressions.score().ge(0.5));
		Diagnostic diagnostic = QueryValidator.validate(query, person,
				capabilities(QueryFeature.WHERE_COMPARISON));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren().get(0).getMessage()).contains("SCORE");
	}

	@Test
	void bareAliasSortOutsideRowShapeIsRefused() {
		// issue #102: an output-column sort needs a projection or aggregation
		Query query = QueryBuilder.from(person)
				.orderByDesc(Expressions.aliasRef("total").toExpression())
				.build();
		Diagnostic diagnostic = QueryValidator.validate(query, person,
				capabilities(QueryFeature.SORT, QueryFeature.TYPE_FILTER));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		Diagnostic child = diagnostic.getChildren().get(0);
		assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_INVALID_SORT);
		assertThat(child.getMessage()).contains("total").contains("row-shaped");
	}

	@Test
	void malformedFuzzyMatchIsRefused() {
		// issue #167: fuzzy parameters are only meaningful for kind FUZZY, and the edit
		// budget is 1 or 2 — both refused by shape, not silently ignored
		org.eclipse.fennec.model.expression.StringMatch misplaced =
				Expressions.path(name).contains("x");
		misplaced.setMaxEdits(1);
		Diagnostic diagnostic = QueryValidator.validate(
				QueryBuilder.from(person).where(misplaced).build(), person,
				capabilities(QueryFeature.WHERE_STRING_MATCH, QueryFeature.TYPE_FILTER));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.anySatisfy(child -> {
					assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_INVALID_STRING_MATCH);
					assertThat(child.getMessage()).contains("only meaningful");
				});

		Diagnostic budget = QueryValidator.validate(
				QueryBuilder.from(person).where(Expressions.path(name).fuzzy("x", 3)).build(), person,
				capabilities(QueryFeature.WHERE_STRING_MATCH, QueryFeature.STRING_MATCH_FUZZY,
						QueryFeature.TYPE_FILTER));
		assertThat(budget.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(budget.getChildren())
				.anySatisfy(child -> {
					assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_INVALID_STRING_MATCH);
					assertThat(child.getMessage()).contains("1 or 2");
				});
	}

	@Test
	void nonConstantRepresentativeCountIsRefused() {
		// issue #214: a backend serving this natively constructs its search with the number
		// known, so the window bounds must be constants
		Query query = QueryBuilder.from(person)
				.groupBy(age).countOf("cnt").representatives("top", 2).build();
		GroupByStage groupBy = (GroupByStage) query.getApply().getStages().get(0);
		groupBy.getRepresentatives().setCount(Expressions.path(age).plus(1).toExpression());
		Diagnostic diagnostic = QueryValidator.validate(query, person,
				capabilities(QueryFeature.GROUP_REPRESENTATIVES, QueryFeature.GROUP_BY,
						QueryFeature.AGG_COUNT, QueryFeature.TYPE_FILTER, QueryFeature.ARITHMETIC));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.anySatisfy(child -> {
					assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_INVALID_REPRESENTATIVES);
					assertThat(child.getMessage()).contains("literal or a parameter");
				});
	}

	@Test
	void representativeCountOfZeroIsRefused() {
		Query query = QueryBuilder.from(person)
				.groupBy(age).countOf("cnt").representatives("top", 0).build();
		Diagnostic diagnostic = QueryValidator.validate(query, person,
				capabilities(QueryFeature.GROUP_REPRESENTATIVES, QueryFeature.GROUP_BY,
						QueryFeature.AGG_COUNT, QueryFeature.TYPE_FILTER));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.anySatisfy(child -> assertThat(child.getMessage()).contains("asks for no rows"));
	}

	@Test
	void undeclaredGroupRepresentativesAreRefused() {
		Query query = QueryBuilder.from(person)
				.groupBy(age).countOf("cnt").representatives("top", 2).build();
		Diagnostic diagnostic = QueryValidator.validate(query, person,
				capabilities(QueryFeature.GROUP_BY, QueryFeature.AGG_COUNT, QueryFeature.TYPE_FILTER));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.anySatisfy(child -> assertThat(child.getMessage()).contains("GROUP_REPRESENTATIVES"));
	}

	@Test
	void malformedIntervalIsRefused() {
		// issue #215: an inverted query interval can never match — a static error, not a run
		Query query = QueryBuilder.from(person)
				.where(Expressions.intersects(
						Expressions.intervalSubject(Expressions.propertyPath(age),
								Expressions.propertyPath(age)),
						30, 10))
				.build();
		Diagnostic diagnostic = QueryValidator.validate(query, person,
				capabilities(QueryFeature.INTERVAL_MATCH, QueryFeature.TYPE_FILTER));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren().get(0).getCode())
				.isEqualTo(QueryValidator.CODE_INVALID_INTERVAL);
		assertThat(diagnostic.getChildren().get(0).getMessage()).contains("inverted");
	}

	@Test
	void undeclaredIntervalMatchIsRefused() {
		Query query = QueryBuilder.from(person)
				.where(Expressions.intervalAt(
						Expressions.intervalSubject(Expressions.propertyPath(age),
								Expressions.propertyPath(age)),
						5))
				.build();
		Diagnostic diagnostic = QueryValidator.validate(query, person,
				capabilities(QueryFeature.TYPE_FILTER));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.anySatisfy(child -> assertThat(child.getMessage()).contains("INTERVAL_MATCH"));
	}

	@Test
	void malformedGeoStructureIsRefused() {
		// issue #101: a GeoSubject must bind exactly one form (pair XOR point)
		org.eclipse.fennec.model.expression.GeoSubject dual =
				org.eclipse.fennec.model.expression.ExpressionFactory.eINSTANCE.createGeoSubject();
		dual.setPathLat(Expressions.propertyPath(name));
		dual.setPathLon(Expressions.propertyPath(name));
		dual.setPathPoint(Expressions.propertyPath(name));
		Query query = QueryBuilder.from(person)
				.where(Expressions.geoWithin(dual,
						Expressions.geoBox(Expressions.geoPoint(10, 50), Expressions.geoPoint(13, 52))))
				.build();
		Diagnostic diagnostic = QueryValidator.validate(query, person,
				capabilities(QueryFeature.GEO_WITHIN, QueryFeature.TYPE_FILTER));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren().get(0).getCode()).isEqualTo(QueryValidator.CODE_INVALID_GEO);
		assertThat(diagnostic.getChildren().get(0).getMessage()).contains("GeoSubject must bind");
	}

	@Test
	void everyUnsupportedFeatureIsReported() {
		Query query = eqQuery(42, name);
		query.setTop(10);
		query.setDistinct(true);

		Diagnostic diagnostic = QueryValidator.validate(query, person, capabilities());
		assertThat(diagnostic.getChildren()).hasSize(3);
		assertThat(diagnostic.getChildren())
				.allSatisfy(child -> assertThat(child.getSeverity()).isEqualTo(Diagnostic.ERROR));
	}

	@Test
	void depthBeyondCapabilityYieldsError() {
		QueryCapabilities depthOne = QueryCapabilitiesBuilder.create()
				.support(QueryFeature.WHERE_EQ, QueryFeature.FEATUREPATH_NESTED, QueryFeature.TYPE_FILTER)
				.maxFeaturePathDepth(1)
				.build();

		Diagnostic diagnostic = QueryValidator.validate(eqQuery("x", address, street), person, depthOne);
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.anySatisfy(child -> assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_DEPTH_EXCEEDED));
	}

	@Test
	void unlimitedDepthAcceptsDeepPaths() {
		QueryCapabilities unlimited = QueryCapabilitiesBuilder.create()
				.support(QueryFeature.WHERE_EQ, QueryFeature.FEATUREPATH_NESTED, QueryFeature.TYPE_FILTER)
				.maxFeaturePathDepth(-1)
				.build();

		Diagnostic diagnostic = QueryValidator.validate(eqQuery("x", address, street), person, unlimited);
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.OK);
	}

	@Test
	void divisionByLiteralZeroYieldsError() {
		Query query = QueryFactory.eINSTANCE.createQuery();
		query.setFrom(person);
		query.setPredicate(div(path(name).length(), 0).gt(1));

		Diagnostic diagnostic = QueryValidator.validate(query, person,
				capabilities(QueryFeature.WHERE_COMPARISON, QueryFeature.ARITHMETIC,
						QueryFeature.STRING_FUNCTIONS));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.anySatisfy(child -> assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_DIVISION_BY_ZERO));
	}

	@Test
	void malformedProjectionYieldsError() {
		// issue #189: exactly one of path/key, and an expression column needs its alias
		Query bothSet = QueryFactory.eINSTANCE.createQuery();
		bothSet.setFrom(person);
		Selection both = QueryFactory.eINSTANCE.createSelection();
		both.setPath(Expressions.propertyPath(name));
		both.setKey(Expressions.propertyPath(name));
		both.setAlias("both");
		bothSet.getSelect().add(both);

		Diagnostic diagnostic = QueryValidator.validate(bothSet, person,
				capabilities(QueryFeature.PROJECTION, QueryFeature.PROJECTION_EXPRESSION));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.anySatisfy(child -> assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_INVALID_PROJECTION));
	}

	@Test
	void expressionProjectionNeedsTheCapability() {
		Query query = QueryBuilder.from(person)
				.selectAs("shouted", path(name).toUpper().toExpression())
				.build();

		Diagnostic refused = QueryValidator.validate(query, person,
				capabilities(QueryFeature.PROJECTION, QueryFeature.STRING_FUNCTIONS));
		assertThat(refused.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(refused.getChildren())
				.anySatisfy(child -> assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_UNSUPPORTED_FEATURE));

		Diagnostic served = QueryValidator.validate(query, person,
				capabilities(QueryFeature.PROJECTION, QueryFeature.PROJECTION_EXPRESSION,
						QueryFeature.STRING_FUNCTIONS));
		assertThat(served.getSeverity()).isEqualTo(Diagnostic.OK);
	}

	@Test
	void aggregateWithBothPathAndSourceYieldsError() {
		Diagnostic diagnostic = QueryValidator.validate(groupQuery(aggregate(AggregateMethod.SUM, true, true)),
				person, groupCapabilities());
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.anySatisfy(child -> assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_INVALID_AGGREGATE));
	}

	@Test
	void aggregateWithoutPathOrSourceYieldsErrorUnlessCount() {
		Diagnostic sum = QueryValidator.validate(groupQuery(aggregate(AggregateMethod.SUM, false, false)),
				person, groupCapabilities());
		assertThat(sum.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(sum.getChildren())
				.anySatisfy(child -> assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_INVALID_AGGREGATE));

		Diagnostic count = QueryValidator.validate(groupQuery(aggregate(AggregateMethod.COUNT, false, false)),
				person, groupCapabilities());
		assertThat(count.getSeverity()).isEqualTo(Diagnostic.OK);
	}

	/**
	 * A second {@code GroupByStage} is expressible and served by nobody, so validation says so
	 * (issue #239).
	 * <p>
	 * Before this, the three backends disagreed on the same well-formed query: JPA threw while
	 * translating, the memory engine silently dropped the second grouping, and mongo appended a
	 * {@code $group} whose columns nothing had registered. Two plausible wrong answers, which §5
	 * forbids more strongly than a refusal.
	 * <p>
	 * The code is {@code CODE_UNSUPPORTED_FEATURE}, not one of the {@code INVALID_*} ones: the
	 * query is well-formed and simply cannot be executed anywhere, so a consumer routing on the
	 * code should answer "this service cannot" rather than "your request is malformed".
	 */
	@Test
	void aSecondGroupByStageIsReportedAsUnsupported() {
		Query query = QueryFactory.eINSTANCE.createQuery();
		query.setFrom(person);
		Pipeline pipeline = QueryFactory.eINSTANCE.createPipeline();
		pipeline.getStages().add(countingGroup("perName"));
		pipeline.getStages().add(countingGroup("overall"));
		query.setApply(pipeline);

		Diagnostic diagnostic = QueryValidator.validate(query, person, capabilities(QueryFeature.GROUP_BY, QueryFeature.AGG_COUNT));
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getChildren())
				.as("a well-formed query nobody can serve is unsupported, not invalid")
				.anySatisfy(child -> {
					assertThat(child.getCode()).isEqualTo(QueryValidator.CODE_UNSUPPORTED_FEATURE);
					assertThat(child.getMessage()).contains("groups 2 times");
				});
	}

	/** One grouping stays valid — the check must not refuse the shape every pipeline has. */
	@Test
	void aSingleGroupByStageStaysValid() {
		Query query = QueryFactory.eINSTANCE.createQuery();
		query.setFrom(person);
		Pipeline pipeline = QueryFactory.eINSTANCE.createPipeline();
		pipeline.getStages().add(countingGroup("perName"));
		query.setApply(pipeline);

		assertThat(QueryValidator.validate(query, person, capabilities(QueryFeature.GROUP_BY, QueryFeature.AGG_COUNT)).getSeverity())
				.isEqualTo(Diagnostic.OK);
	}

	private GroupByStage countingGroup(String alias) {
		GroupByStage group = QueryFactory.eINSTANCE.createGroupByStage();
		group.getPaths().add(Expressions.propertyPath(name));
		Aggregate count = QueryFactory.eINSTANCE.createAggregate();
		count.setMethod(AggregateMethod.COUNT);
		count.setAlias(alias);
		group.getAggregates().add(count);
		return group;
	}

	private Query groupQuery(Aggregate aggregate) {
		Query query = QueryFactory.eINSTANCE.createQuery();
		query.setFrom(person);
		GroupByStage group = QueryFactory.eINSTANCE.createGroupByStage();
		group.getPaths().add(Expressions.propertyPath(name));
		group.getAggregates().add(aggregate);
		Pipeline pipeline = QueryFactory.eINSTANCE.createPipeline();
		pipeline.getStages().add(group);
		query.setApply(pipeline);
		return query;
	}

	private Aggregate aggregate(AggregateMethod method, boolean withPath, boolean withSource) {
		Aggregate aggregate = QueryFactory.eINSTANCE.createAggregate();
		aggregate.setMethod(method);
		aggregate.setAlias("agg");
		if (withPath) {
			aggregate.setPath(Expressions.propertyPath(name));
		}
		if (withSource) {
			aggregate.setSource(Expressions.propertyPath(name));
		}
		return aggregate;
	}

	private QueryCapabilities groupCapabilities() {
		return capabilities(QueryFeature.GROUP_BY, QueryFeature.GROUP_EXPRESSION, QueryFeature.AGG_SUM,
				QueryFeature.AGG_COUNT, QueryFeature.FEATUREPATH_NESTED);
	}

	@Test
	void nullArgumentsAreRejected() {
		QueryCapabilities none = capabilities();
		assertThatIllegalArgumentException()
				.isThrownBy(() -> QueryValidator.validate((QueryAnalysis) null, person, none));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> QueryValidator.validate(eqQuery(42, name), person, null));
	}
}
