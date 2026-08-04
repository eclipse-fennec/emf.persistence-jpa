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
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.persistence.query.api.QueryCapabilities;
import org.eclipse.fennec.persistence.query.api.QueryFeature;
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
