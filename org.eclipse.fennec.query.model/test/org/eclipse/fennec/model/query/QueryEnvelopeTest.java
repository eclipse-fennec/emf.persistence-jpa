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
package org.eclipse.fennec.model.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.expression.IntegerLiteral;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.junit.jupiter.api.Test;

/**
 * Smoke test for the v2 query envelope around the expression model.
 *
 * @author Mark Hoffmann
 */
class QueryEnvelopeTest {

	private final QueryFactory factory = QueryFactory.eINSTANCE;
	private final ExpressionFactory expr = ExpressionFactory.eINSTANCE;

	private EAttribute age() {
		EAttribute age = EcoreFactory.eINSTANCE.createEAttribute();
		age.setName("age");
		age.setEType(EcorePackage.Literals.EINT);
		return age;
	}

	@Test
	void envelopeCarriesExpressionPredicate() {
		EClass person = EcoreFactory.eINSTANCE.createEClass();
		person.setName("Person");

		Comparison gte = expr.createComparison();
		gte.setOperator(ComparisonOperator.GE);
		PropertyPath path = expr.createPropertyPath();
		path.getSegments().add(age());
		gte.setLeft(path);
		IntegerLiteral eighteen = expr.createIntegerLiteral();
		eighteen.setValue(18);
		gte.setRight(eighteen);

		Query query = factory.createQuery();
		query.setFrom(person);
		query.setPredicate(gte);
		query.setTop(10);

		OrderBy order = factory.createOrderBy();
		PropertyPath orderPath = expr.createPropertyPath();
		orderPath.getSegments().add(age());
		order.setPath(orderPath);
		query.getOrderBy().add(order);

		assertThat(query.getPredicate()).isSameAs(gte);
		assertThat(gte.eContainer()).isSameAs(query);
		assertThat(order.getDirection()).isEqualTo(SortDirection.ASC);
		assertThat(query.getOrderBy().get(0).eContainer()).isSameAs(query);
	}

	@Test
	void pipelineStagesAreOrdered() {
		GroupByStage group = factory.createGroupByStage();
		Aggregate avg = factory.createAggregate();
		avg.setMethod(AggregateMethod.AVG);
		avg.setAlias("avgAge");
		PropertyPath path = expr.createPropertyPath();
		path.getSegments().add(age());
		avg.setPath(path);
		group.getAggregates().add(avg);

		TopStage top = factory.createTopStage();
		top.setCount(5);

		Pipeline pipeline = factory.createPipeline();
		pipeline.getStages().add(group);
		pipeline.getStages().add(top);

		Query query = factory.createQuery();
		query.setApply(pipeline);

		assertThat(pipeline.getStages()).containsExactly(group, top);
		assertThat(AggregateMethod.values()).hasSize(6);
		assertThat(query.getApply()).isSameAs(pipeline);
	}
}
