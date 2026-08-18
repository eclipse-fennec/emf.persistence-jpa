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
package org.eclipse.fennec.persistence.query.expr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.Arithmetic;
import org.eclipse.fennec.model.expression.ArithmeticOperator;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.expression.IntegerLiteral;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Negate;
import org.eclipse.fennec.model.expression.Or;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.RealLiteral;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringFunctionKind;
import org.eclipse.fennec.model.expression.StringLiteral;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.StringMatchKind;
import org.eclipse.fennec.model.expression.Variable;
import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.AggregateMethod;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.GroupKey;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.Pipeline;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.TopStage;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.support.QueryAnalysis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link ExpressionAnalyzer} feature detection, depth tracking and shape
 * derivation over the v2 envelope.
 *
 * @author Mark Hoffmann
 */
class ExpressionAnalyzerTest {

	private final ExpressionFactory expr = ExpressionFactory.eINSTANCE;
	private final QueryFactory factory = QueryFactory.eINSTANCE;

	private EClass person;
	private EAttribute name;
	private EAttribute age;
	private EReference addresses;
	private EAttribute street;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		person = ecore.createEClass();
		person.setName("Person");
		name = ecore.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		age = ecore.createEAttribute();
		age.setName("age");
		age.setEType(EcorePackage.Literals.EINT);
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
	}

	private PropertyPath path(EStructuralFeature... segments) {
		PropertyPath path = expr.createPropertyPath();
		for (EStructuralFeature segment : segments) {
			path.getSegments().add(segment);
		}
		return path;
	}

	private Comparison comparison(ComparisonOperator operator, PropertyPath left, Expression right) {
		Comparison comparison = expr.createComparison();
		comparison.setOperator(operator);
		comparison.setLeft(left);
		comparison.setRight(right);
		return comparison;
	}

	private IntegerLiteral intLit(long value) {
		IntegerLiteral literal = expr.createIntegerLiteral();
		literal.setValue(value);
		return literal;
	}

	private Query query(Expression predicate) {
		Query query = factory.createQuery();
		query.setFrom(person);
		query.setPredicate(predicate);
		return query;
	}

	@Test
	void nullQueryIsRejected() {
		assertThatIllegalArgumentException().isThrownBy(() -> ExpressionAnalyzer.analyze(null));
	}

	@Test
	void groupedTreeDetectsAllBranches() {
		// (age >= 18 OR age NE 65) AND name IS NOT NULL — the shape the v1 IR could not express
		Or or = expr.createOr();
		or.getOperands().add(comparison(ComparisonOperator.GE, path(age), intLit(18)));
		or.getOperands().add(comparison(ComparisonOperator.NE, path(age), intLit(65)));

		IsNull notNull = expr.createIsNull();
		notNull.setNegated(true);
		notNull.setSource(path(name));

		And and = expr.createAnd();
		and.getOperands().add(or);
		and.getOperands().add(notNull);

		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query(and));
		assertThat(analysis.features()).contains(QueryFeature.LOGICAL_AND, QueryFeature.LOGICAL_OR,
				QueryFeature.WHERE_COMPARISON, QueryFeature.WHERE_NE, QueryFeature.IS_NULL,
				QueryFeature.TYPE_FILTER);
		assertThat(analysis.shape()).isEqualTo(QueryShape.OBJECTS);
	}

	@Test
	void quantifierAndDepthAreDetected() {
		Exists exists = expr.createExists();
		exists.setSource(path(addresses));
		Variable a = expr.createVariable();
		a.setName("a");
		exists.setVariable(a);
		PropertyPath streetPath = path(street);
		streetPath.setBase(a);
		StringMatch match = expr.createStringMatch();
		match.setKind(StringMatchKind.CONTAINS);
		match.setCaseInsensitive(true);
		match.setSource(streetPath);
		StringLiteral main = expr.createStringLiteral();
		main.setValue("Main");
		match.setPattern(main);
		exists.setPredicate(match);

		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query(exists));
		assertThat(analysis.features()).contains(QueryFeature.EXISTS, QueryFeature.WHERE_STRING_MATCH,
				QueryFeature.STRING_MATCH_CASE_INSENSITIVE);
	}

	@Test
	void nestedPathTracksDepth() {
		Comparison deep = comparison(ComparisonOperator.EQ, path(addresses, street), intLit(1));
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query(deep));
		assertThat(analysis.features()).contains(QueryFeature.FEATUREPATH_NESTED);
		assertThat(analysis.maxFeaturePathDepth()).isEqualTo(2);
	}

	@Test
	void fieldToFieldIsDetected() {
		Comparison direct = comparison(ComparisonOperator.EQ, path(name), path(name));
		assertThat(ExpressionAnalyzer.analyze(query(direct)).features())
				.contains(QueryFeature.FIELD_TO_FIELD);

		StringFunction lower = expr.createStringFunction();
		lower.setKind(StringFunctionKind.TO_LOWER);
		lower.setSource(path(name));
		Comparison throughFunction = comparison(ComparisonOperator.EQ, path(name), lower);
		assertThat(ExpressionAnalyzer.analyze(query(throughFunction)).features())
				.contains(QueryFeature.FIELD_TO_FIELD, QueryFeature.STRING_FUNCTIONS);

		Comparison literalOnly = comparison(ComparisonOperator.EQ, path(name), intLit(1));
		assertThat(ExpressionAnalyzer.analyze(query(literalOnly)).features())
				.doesNotContain(QueryFeature.FIELD_TO_FIELD);
	}

	@Test
	void parametersAreDetectedFromRefsAndDecls() {
		ParameterRef ref = expr.createParameterRef();
		ref.setName("who");
		Comparison eq = comparison(ComparisonOperator.EQ, path(name), ref);
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query(eq));
		assertThat(analysis.features()).contains(QueryFeature.PARAMETERS, QueryFeature.WHERE_EQ);
	}

	@Test
	void envelopeShapingFlags() {
		Query query = query(null);
		OrderBy orderBy = factory.createOrderBy();
		orderBy.setPath(path(age));
		query.getOrderBy().add(orderBy);
		query.setTop(10);
		query.setSkip(5);
		query.setDistinct(true);
		query.getExpand().add(path(addresses));

		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.SORT, QueryFeature.LIMIT, QueryFeature.SKIP,
				QueryFeature.DISTINCT, QueryFeature.EXPAND);
		assertThat(analysis.shape()).isEqualTo(QueryShape.OBJECTS);
	}

	@Test
	void countOnlyWinsShape() {
		Query query = query(null);
		query.setCountOnly(true);
		assertThat(ExpressionAnalyzer.analyze(query).shape()).isEqualTo(QueryShape.COUNT);
	}

	@Test
	void selectionMakesProjection() {
		Query query = query(null);
		Selection selection = factory.createSelection();
		selection.setPath(path(addresses, street));
		selection.setAlias("street");
		query.getSelect().add(selection);

		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.PROJECTION, QueryFeature.PROJECTION_NESTED);
		assertThat(analysis.shape()).isEqualTo(QueryShape.PROJECTION);
	}

	@Test
	void groupByPipelineMakesAggregation() {
		GroupByStage group = factory.createGroupByStage();
		group.getPaths().add(path(name));
		Aggregate avg = factory.createAggregate();
		avg.setMethod(AggregateMethod.AVG);
		avg.setAlias("avgAge");
		avg.setPath(path(age));
		group.getAggregates().add(avg);
		Aggregate distinct = factory.createAggregate();
		distinct.setMethod(AggregateMethod.COUNT_DISTINCT);
		distinct.setAlias("names");
		distinct.setPath(path(name));
		group.getAggregates().add(distinct);

		Pipeline pipeline = factory.createPipeline();
		pipeline.getStages().add(group);
		Query query = query(null);
		query.setApply(pipeline);

		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.GROUP_BY, QueryFeature.AGG_AVG,
				QueryFeature.AGG_COUNT_DISTINCT);
		assertThat(analysis.features()).doesNotContain(QueryFeature.PIPELINE);
		assertThat(analysis.shape()).isEqualTo(QueryShape.AGGREGATION);
	}

	@Test
	void groupKeysAndAggregateSourcesAreDetected() {
		GroupByStage group = factory.createGroupByStage();
		GroupKey key = factory.createGroupKey();
		key.setAlias("bucket");
		key.setExpression(path(age));
		group.getKeys().add(key);
		Aggregate sum = factory.createAggregate();
		sum.setMethod(AggregateMethod.SUM);
		sum.setAlias("total");
		sum.setSource(path(age));
		group.getAggregates().add(sum);

		Pipeline pipeline = factory.createPipeline();
		pipeline.getStages().add(group);
		Query query = query(null);
		query.setApply(pipeline);

		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.GROUP_BY, QueryFeature.GROUP_EXPRESSION,
				QueryFeature.AGG_SUM);
		assertThat(analysis.invalidAggregate()).isNull();
	}

	@Test
	void malformedAggregatesAreFlagged() {
		GroupByStage group = factory.createGroupByStage();
		Aggregate both = factory.createAggregate();
		both.setMethod(AggregateMethod.SUM);
		both.setAlias("total");
		both.setPath(path(age));
		both.setSource(path(age));
		group.getAggregates().add(both);

		Pipeline pipeline = factory.createPipeline();
		pipeline.getStages().add(group);
		Query query = query(null);
		query.setApply(pipeline);

		assertThat(ExpressionAnalyzer.analyze(query).invalidAggregate()).contains("both path and source");

		both.setPath(null);
		both.setSource(null);
		assertThat(ExpressionAnalyzer.analyze(query).invalidAggregate()).contains("needs a path or a source");

		both.setMethod(AggregateMethod.COUNT);
		assertThat(ExpressionAnalyzer.analyze(query).invalidAggregate()).isNull();
	}

	@Test
	void arithmeticIsDetected() {
		Arithmetic plus = expr.createArithmetic();
		plus.setOperator(ArithmeticOperator.ADD);
		plus.setLeft(path(age));
		plus.setRight(intLit(2));
		Comparison comparison = expr.createComparison();
		comparison.setOperator(ComparisonOperator.GT);
		comparison.setLeft(plus);
		comparison.setRight(intLit(50));

		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query(comparison));
		assertThat(analysis.features()).contains(QueryFeature.ARITHMETIC, QueryFeature.WHERE_COMPARISON);
		assertThat(analysis.divisionByLiteralZero()).isFalse();
	}

	@Test
	void negateIsDetected() {
		Negate negate = expr.createNegate();
		negate.setOperand(path(age));
		Comparison comparison = expr.createComparison();
		comparison.setOperator(ComparisonOperator.LT);
		comparison.setLeft(negate);
		comparison.setRight(intLit(0));

		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query(comparison));
		assertThat(analysis.features()).contains(QueryFeature.ARITHMETIC);
	}

	@Test
	void literalZeroDivisorIsFlagged() {
		assertThat(ExpressionAnalyzer.analyze(query(divides(ArithmeticOperator.DIV, intLit(0))))
				.divisionByLiteralZero()).isTrue();

		RealLiteral realZero = expr.createRealLiteral();
		realZero.setValue(0.0d);
		assertThat(ExpressionAnalyzer.analyze(query(divides(ArithmeticOperator.MOD, realZero)))
				.divisionByLiteralZero()).isTrue();

		assertThat(ExpressionAnalyzer.analyze(query(divides(ArithmeticOperator.DIV, intLit(2))))
				.divisionByLiteralZero()).isFalse();
	}

	private Comparison divides(ArithmeticOperator operator, Expression divisor) {
		Arithmetic arithmetic = expr.createArithmetic();
		arithmetic.setOperator(operator);
		arithmetic.setLeft(path(age));
		arithmetic.setRight(divisor);
		Comparison comparison = expr.createComparison();
		comparison.setOperator(ComparisonOperator.GT);
		comparison.setLeft(arithmetic);
		comparison.setRight(intLit(1));
		return comparison;
	}

	@Test
	void extendedStringFunctionsAreDetected() {
		QueryAnalysis concat = ExpressionAnalyzer.analyze(query(
				Expressions.concat(Expressions.path(name), "!").eq("Alice!")));
		assertThat(concat.features()).contains(QueryFeature.STRING_FUNCTIONS_EXTENDED);

		QueryAnalysis indexOf = ExpressionAnalyzer.analyze(query(
				Expressions.path(name).indexOf("o").ge(0)));
		assertThat(indexOf.features()).contains(QueryFeature.STRING_FUNCTIONS_EXTENDED);

		QueryAnalysis substring = ExpressionAnalyzer.analyze(query(
				Expressions.path(name).substring(0, 3).eq("Ali")));
		assertThat(substring.features()).contains(QueryFeature.STRING_FUNCTIONS_EXTENDED);

		// substring/concat over paths count as navigating for FIELD_TO_FIELD
		QueryAnalysis fieldToField = ExpressionAnalyzer.analyze(query(
				Expressions.path(name).substring(0, 3).eq(Expressions.path(name))));
		assertThat(fieldToField.features()).contains(QueryFeature.FIELD_TO_FIELD);
	}

	@Test
	void numericFunctionsAreDetected() {
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query(
				Expressions.path(age).dividedBy(4).round().eq(8)));
		assertThat(analysis.features()).contains(QueryFeature.NUMERIC_FUNCTIONS, QueryFeature.ARITHMETIC);
	}

	@Test
	void temporalFunctionsAreDetected() {
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query(
				Expressions.path(age).year().eq(1990)));
		assertThat(analysis.features()).contains(QueryFeature.TEMPORAL_FUNCTIONS);
	}

	@Test
	void typePredicatesAreDetected() {
		QueryAnalysis check = ExpressionAnalyzer.analyze(query(Expressions.isOf(person)));
		assertThat(check.features()).contains(QueryFeature.TYPE_CHECK);

		QueryAnalysis cast = ExpressionAnalyzer.analyze(query(
				Expressions.pathAs(person, age).gt(18)));
		assertThat(cast.features()).contains(QueryFeature.TYPE_CAST);
	}

	@Test
	void collectionCountsAreDetected() {
		QueryAnalysis plain = ExpressionAnalyzer.analyze(query(
				Expressions.count(Expressions.propertyPath(addresses)).ge(2)));
		assertThat(plain.features()).contains(QueryFeature.COLLECTION_COUNT);
		assertThat(plain.features()).doesNotContain(QueryFeature.COLLECTION_COUNT_FILTERED);

		QueryAnalysis filtered = ExpressionAnalyzer.analyze(query(
				Expressions.count(Expressions.propertyPath(addresses),
						a -> a.path(street).startsWith("Main")).ge(1)));
		assertThat(filtered.features()).contains(QueryFeature.COLLECTION_COUNT_FILTERED);
	}

	@Test
	void computeStagesAreDetected() {
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(
				QueryBuilder.from(person)
						.sum("total", age)
						.countOf("cnt")
						.computeAs("avgAge", Expressions.div(Expressions.aliasRef("total"),
								Expressions.aliasRef("cnt")).toExpression())
						.having(Expressions.aliasRef("avgAge").ge(30))
						.build());
		assertThat(analysis.features()).contains(QueryFeature.PIPELINE, QueryFeature.PIPELINE_COMPUTE,
				QueryFeature.GROUP_BY);
		assertThat(analysis.shape()).isEqualTo(QueryShape.AGGREGATION);
	}

	@Test
	void sortExpressionsAreDetected() {
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(
				QueryBuilder.from(person)
						.orderByAsc(Expressions.neg(Expressions.path(age)).toExpression())
						.build());
		assertThat(analysis.features()).contains(QueryFeature.SORT, QueryFeature.SORT_EXPRESSION,
				QueryFeature.ARITHMETIC);
	}

	@Test
	void bareScoreSortIsScoreNotSortExpression() {
		// relevance order is the canonical use of SCORE (issue #165): a bare score()
		// key must not demand arbitrary-expression sorting from the backend
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(
				QueryBuilder.from(person)
						.orderByDesc(Expressions.score().toExpression())
						.build());
		assertThat(analysis.features()).contains(QueryFeature.SORT, QueryFeature.SCORE)
				.doesNotContain(QueryFeature.SORT_EXPRESSION);
		assertThat(analysis.invalidSort()).isNull();

		// a composed key stays an arbitrary sort expression — and still flags SCORE
		QueryAnalysis composed = ExpressionAnalyzer.analyze(
				QueryBuilder.from(person)
						.orderByDesc(Expressions.score().times(2).toExpression())
						.build());
		assertThat(composed.features()).contains(QueryFeature.SORT, QueryFeature.SORT_EXPRESSION,
				QueryFeature.SCORE, QueryFeature.ARITHMETIC);
	}

	@Test
	void bareAliasSortIsPlainSortNotSortExpression() {
		// a bare AliasRef key is an output-column sort (issue #102) — plain SORT
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(
				QueryBuilder.from(person)
						.groupBy(name)
						.avg("avgAge", age)
						.orderByDesc(Expressions.aliasRef("avgAge").toExpression())
						.build());
		assertThat(analysis.features()).contains(QueryFeature.SORT)
				.doesNotContain(QueryFeature.SORT_EXPRESSION);
		assertThat(analysis.invalidSort()).isNull();

		// computed keys (even over aliases) stay SORT_EXPRESSION
		QueryAnalysis computed = ExpressionAnalyzer.analyze(
				QueryBuilder.from(person)
						.groupBy(name)
						.avg("avgAge", age)
						.orderByDesc(Expressions.neg(Expressions.aliasRef("avgAge").toExpression())
								.toExpression())
						.build());
		assertThat(computed.features()).contains(QueryFeature.SORT_EXPRESSION);

		// outside a row shape the alias addresses nothing — structural finding
		QueryAnalysis invalid = ExpressionAnalyzer.analyze(
				QueryBuilder.from(person)
						.orderByDesc(Expressions.aliasRef("avgAge").toExpression())
						.build());
		assertThat(invalid.invalidSort()).contains("avgAge");
	}

	@Test
	void geoVocabularyIsDetectedAndStructurallyValidated() {
		// issue #101: GeoWithin/GeoDistance register their capabilities and paths
		QueryAnalysis within = ExpressionAnalyzer.analyze(QueryBuilder.from(person)
				.where(Expressions.geoWithin(
						Expressions.geoSubject(Expressions.propertyPath(age), Expressions.propertyPath(age)),
						Expressions.geoBox(Expressions.geoPoint(10, 50), Expressions.geoPoint(13, 52))))
				.build());
		assertThat(within.features()).contains(QueryFeature.GEO_WITHIN);
		assertThat(within.invalidGeo()).isNull();

		QueryAnalysis distance = ExpressionAnalyzer.analyze(QueryBuilder.from(person)
				.where(Expressions.geoDistance(
						Expressions.geoSubject(Expressions.propertyPath(age), Expressions.propertyPath(age)),
						Expressions.geoPoint(11.5, 50.9)).le(500))
				.build());
		assertThat(distance.features()).contains(QueryFeature.GEO_DISTANCE);

		// structural findings: out-of-range coordinate, degenerate polygon
		QueryAnalysis outOfRange = ExpressionAnalyzer.analyze(QueryBuilder.from(person)
				.where(Expressions.geoWithin(
						Expressions.geoSubject(Expressions.propertyPath(age), Expressions.propertyPath(age)),
						Expressions.geoBox(Expressions.geoPoint(10, 95), Expressions.geoPoint(13, 96))))
				.build());
		assertThat(outOfRange.invalidGeo()).contains("out of range");

		QueryAnalysis degenerate = ExpressionAnalyzer.analyze(QueryBuilder.from(person)
				.where(Expressions.geoWithin(
						Expressions.geoSubject(Expressions.propertyPath(age), Expressions.propertyPath(age)),
						Expressions.geoPolygon(Expressions.geoPoint(10, 50), Expressions.geoPoint(10, 50),
								Expressions.geoPoint(10, 50))))
				.build());
		assertThat(degenerate.invalidGeo()).contains("distinct");
	}

	@Test
	void scoreIsDetected() {
		// the relevance sort key (issue #100) requires the SCORE capability — and since
		// issue #165 only SCORE, not SORT_EXPRESSION (see bareScoreSortIsScoreNotSortExpression)
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(
				QueryBuilder.from(person)
						.orderByDesc(Expressions.score().toExpression())
						.build());
		assertThat(analysis.features()).contains(QueryFeature.SORT, QueryFeature.SCORE);

		QueryAnalysis predicate = ExpressionAnalyzer.analyze(
				QueryBuilder.from(person)
						.where(Expressions.score().ge(0.5))
						.build());
		assertThat(predicate.features()).contains(QueryFeature.SCORE);
	}

	@Test
	void withScoresEnvelopeFlagRequiresScore() {
		// requesting per-hit scores is IR, not an option, precisely so this flag is
		// capability-validated (issue #165)
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(
				QueryBuilder.from(person).withScores().build());
		assertThat(analysis.features()).contains(QueryFeature.SCORE);
	}

	@Test
	void multiStagePipelineIsFlagged() {
		GroupByStage group = factory.createGroupByStage();
		Aggregate count = factory.createAggregate();
		count.setMethod(AggregateMethod.COUNT);
		count.setAlias("cnt");
		group.getAggregates().add(count);
		TopStage top = factory.createTopStage();
		top.setCount(3);

		Pipeline pipeline = factory.createPipeline();
		pipeline.getStages().add(group);
		pipeline.getStages().add(top);
		Query query = query(null);
		query.setApply(pipeline);

		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.PIPELINE, QueryFeature.GROUP_BY,
				QueryFeature.AGG_COUNT);
	}
}
