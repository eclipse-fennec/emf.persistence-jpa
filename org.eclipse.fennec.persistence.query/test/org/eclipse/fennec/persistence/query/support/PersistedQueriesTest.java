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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.eclipse.fennec.model.query.builder.Expressions.and;
import static org.eclipse.fennec.model.query.builder.Expressions.any;
import static org.eclipse.fennec.model.query.builder.Expressions.param;
import static org.eclipse.fennec.model.query.builder.Expressions.path;
import static org.eclipse.fennec.model.query.builder.Expressions.propertyPath;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.query.QueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Round-trip tests for the persisted-query XMI payload ({@code saveQuery}).
 *
 * @author Juergen Albert
 */
class PersistedQueriesTest {

	private EPackage tckPackage;
	private EClass person;
	private EAttribute name;
	private EAttribute age;
	private EReference addresses;
	private EAttribute street;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		tckPackage = ecore.createEPackage();
		tckPackage.setName("tck");
		tckPackage.setNsPrefix("tck");
		tckPackage.setNsURI("https://eclipse.org/fennec/test/persisted-queries");

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

		tckPackage.getEClassifiers().add(person);
		tckPackage.getEClassifiers().add(address);
	}

	private EPackage.Registry registry() {
		EPackage.Registry registry = new EPackageRegistryImpl();
		registry.put(tckPackage.getNsURI(), tckPackage);
		return registry;
	}

	@Test
	void catalogNameRequiresSaveQueryAndName() throws QueryException {
		assertThat(PersistedQueries.catalogName(QueryBuilder.from(person).build())).isNull();
		assertThat(PersistedQueries.catalogName(
				QueryBuilder.from(person).named("adults").build())).isEqualTo("adults");

		Query unnamed = QueryBuilder.from(person).build();
		unnamed.setSaveQuery(true);
		assertThatThrownBy(() -> PersistedQueries.catalogName(unnamed))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("name");
	}

	@Test
	void roundTripRestoresStructureAndMetamodelReferences() throws QueryException {
		Query query = QueryBuilder.from(person)
				.where(and(
						path(age).ge(param("minAge")),
						any(propertyPath(addresses), a -> a.path(street).startsWith("Main"))))
				.parameter("minAge", null)
				.orderByAsc(age)
				.top(5)
				.named("adults")
				.build();

		String xmi = PersistedQueries.toXmi(query);
		assertThat(xmi).contains(tckPackage.getNsURI());

		Query loaded = PersistedQueries.fromXmi("adults", xmi, registry());
		assertThat(loaded.getFrom()).isSameAs(person);
		assertThat(loaded.getName()).isEqualTo("adults");
		// deliberately NOT carried over (issue #163): a query that comes out of a catalog is
		// already deposited, and keeping the flag would write it back on every execution —
		// wasteful against a writable catalog, a failure against a read-only one
		assertThat(loaded.isSaveQuery())
				.as("a loaded query must not ask to be stored again").isFalse();
		assertThat(loaded.getTop()).isEqualTo(5);
		assertThat(loaded.getParameters()).hasSize(1);

		And root = (And) loaded.getPredicate();
		Comparison ge = (Comparison) root.getOperands().get(0);
		assertThat(((PropertyPath) ge.getLeft()).getSegments()).containsExactly(age);
		assertThat(((ParameterRef) ge.getRight()).getName()).isEqualTo("minAge");

		Exists exists = (Exists) root.getOperands().get(1);
		assertThat(exists.getSource().getSegments()).containsExactly(addresses);
		PropertyPath inner = (PropertyPath) ((org.eclipse.fennec.model.expression.StringMatch) exists
				.getPredicate()).getSource();
		assertThat(inner.getSegments()).containsExactly(street);
		assertThat(inner.getBase()).isSameAs(exists.getVariable());
	}

	@Test
	void unresolvableMetamodelIsRefused() throws QueryException {
		Query query = QueryBuilder.from(person)
				.where(path(age).ge(18))
				.named("adults")
				.build();
		String xmi = PersistedQueries.toXmi(query);
		assertThatThrownBy(() -> PersistedQueries.fromXmi("adults", xmi, new EPackageRegistryImpl()))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("adults");
	}
}
