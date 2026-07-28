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
package org.eclipse.fennec.persistence.query.derived;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.IntegerLiteral;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.persistence.query.QueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Shape recognition and bridging of derivation annotations (#70).
 *
 * @author Juergen Albert
 */
class DerivedReferenceCompilerTest {

	private final DerivedReferenceCompiler compiler = new DerivedReferenceCompiler();

	private EPackage tckPackage;
	private EClass person;
	private EAttribute age;
	private EReference friends;
	private EReference addresses;
	private EAttribute street;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		tckPackage = ecore.createEPackage();
		tckPackage.setName("derived");
		tckPackage.setNsPrefix("derived");
		tckPackage.setNsURI("https://eclipse.org/fennec/test/derived");

		person = ecore.createEClass();
		person.setName("Person");
		age = ecore.createEAttribute();
		age.setName("age");
		age.setEType(EcorePackage.Literals.EINT);
		person.getEStructuralFeatures().add(age);

		EClass address = ecore.createEClass();
		address.setName("Address");
		street = ecore.createEAttribute();
		street.setName("street");
		street.setEType(EcorePackage.Literals.ESTRING);
		address.getEStructuralFeatures().add(street);

		friends = ecore.createEReference();
		friends.setName("friends");
		friends.setEType(person);
		friends.setUpperBound(-1);
		person.getEStructuralFeatures().add(friends);

		addresses = ecore.createEReference();
		addresses.setName("addresses");
		addresses.setEType(address);
		addresses.setUpperBound(-1);
		addresses.setContainment(true);
		person.getEStructuralFeatures().add(addresses);

		tckPackage.getEClassifiers().add(person);
		tckPackage.getEClassifiers().add(address);
	}

	private EReference derived(String derivation) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName("derivedFriends");
		reference.setEType(person);
		reference.setUpperBound(-1);
		reference.setTransient(true);
		reference.setVolatile(true);
		reference.setDerived(true);
		reference.setChangeable(false);
		person.getEStructuralFeatures().add(reference);
		if (derivation != null) {
			EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(DerivedReferenceCompiler.DELEGATE_URI);
			annotation.getDetails().put(DerivedReferenceCompiler.DETAIL_DERIVATION, derivation);
			reference.getEAnnotations().add(annotation);
		}
		return reference;
	}

	@Test
	void selectOverReferenceCompilesToPushdown() throws QueryException {
		DerivedPlan plan = compiler.compile(derived("self.friends->select(f | f.age >= 18)"));
		DerivedPlan.Pushdown pushdown = (DerivedPlan.Pushdown) plan;
		assertThat(pushdown.baseReference()).isSameAs(friends);
		Comparison comparison = (Comparison) pushdown.predicate();
		assertThat(comparison.getOperator()).isEqualTo(ComparisonOperator.GE);
		PropertyPath left = (PropertyPath) comparison.getLeft();
		assertThat(left.getSegments()).containsExactly(age);
		assertThat(left.getBase()).as("iterator-based paths are rootified").isNull();
		assertThat(((IntegerLiteral) comparison.getRight()).getValue()).isEqualTo(18L);
	}

	@Test
	void nestedQuantifierStaysScopedWhileOuterPathsRootify() throws QueryException {
		DerivedPlan plan = compiler.compile(derived(
				"self.friends->select(f | f.addresses->exists(a | a.street = 'Main'))"));
		DerivedPlan.Pushdown pushdown = (DerivedPlan.Pushdown) plan;
		Exists exists = (Exists) pushdown.predicate();
		assertThat(exists.getSource().getBase()).as("outer navigation rootified").isNull();
		assertThat(exists.getSource().getSegments()).containsExactly(addresses);
		Comparison inner = (Comparison) exists.getPredicate();
		assertThat(((PropertyPath) inner.getLeft()).getBase())
				.as("inner iterator variable keeps its scope").isSameAs(exists.getVariable());
	}

	@Test
	void nonSelectShapesDegradeToMemory() throws QueryException {
		assertThat(compiler.compile(derived("self.friends->reject(f | f.age >= 18)")))
				.isInstanceOf(DerivedPlan.Memory.class);
		assertThat(compiler.compile(derived("self.friends")))
				.isInstanceOf(DerivedPlan.Memory.class);
	}

	@Test
	void unbridgeableBodyDegradesToMemory() throws QueryException {
		DerivedPlan plan = compiler.compile(derived("self.friends->select(f | f.friends->isEmpty())"));
		assertThat(plan).isInstanceOf(DerivedPlan.Memory.class);
		assertThat(((DerivedPlan.Memory) plan).reason()).isNotBlank();
	}

	@Test
	void missingAnnotationAndBrokenOclFailFast() {
		assertThatThrownBy(() -> compiler.compile(derived(null)))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("derivation");
		assertThatThrownBy(() -> compiler.compile(derived("self.friends->select(")))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("parse");
	}
}
