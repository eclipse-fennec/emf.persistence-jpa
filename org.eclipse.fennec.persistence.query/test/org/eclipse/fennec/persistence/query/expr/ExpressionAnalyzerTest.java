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
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.expression.IntegerLiteral;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Or;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.StringLiteral;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.StringMatchKind;
import org.eclipse.fennec.model.expression.Variable;
import org.eclipse.fennec.model2.query.Aggregate;
import org.eclipse.fennec.model2.query.AggregateMethod;
import org.eclipse.fennec.model2.query.GroupByStage;
import org.eclipse.fennec.model2.query.OrderBy;
import org.eclipse.fennec.model2.query.Pipeline;
import org.eclipse.fennec.model2.query.Query;
import org.eclipse.fennec.model2.query.QueryFactory;
import org.eclipse.fennec.model2.query.Selection;
import org.eclipse.fennec.model2.query.TopStage;
import org.eclipse.fennec.persistence.query.api.QueryFeature;
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
