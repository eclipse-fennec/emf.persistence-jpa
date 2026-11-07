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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.fennec.model.command.CommandFactory;
import org.eclipse.fennec.model.command.UpdateEntry;
import org.eclipse.fennec.model.command.UpdateKind;
import org.eclipse.fennec.model.command.UpdateTemplate;
import org.eclipse.fennec.persistence.query.QueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link ChangeTemplates} patch-apply engine against a dynamic model.
 *
 * @author Mark Hoffmann
 */
class ChangeTemplatesTest {

	private EClass personClass;
	private EAttribute pid;
	private EAttribute name;
	private EAttribute age;
	private EAttribute nicknames;
	private EReference friend;
	private EReference friends;
	private EReference home;
	private EObject person;
	private EObject bob;
	private EObject carl;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		personClass = ecore.createEClass();
		personClass.setName("Person");
		name = ecore.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		age = ecore.createEAttribute();
		age.setName("age");
		age.setEType(EcorePackage.Literals.EINT);
		nicknames = ecore.createEAttribute();
		nicknames.setName("nicknames");
		nicknames.setEType(EcorePackage.Literals.ESTRING);
		nicknames.setUpperBound(-1);
		pid = ecore.createEAttribute();
		pid.setName("pid");
		pid.setEType(EcorePackage.Literals.ESTRING);
		pid.setID(true);
		friend = ecore.createEReference();
		friend.setName("friend");
		friend.setEType(personClass);
		friends = ecore.createEReference();
		friends.setName("friends");
		friends.setEType(personClass);
		friends.setUpperBound(-1);
		home = ecore.createEReference();
		home.setName("home");
		home.setEType(personClass);
		home.setContainment(true);
		personClass.getEStructuralFeatures().add(pid);
		personClass.getEStructuralFeatures().add(name);
		personClass.getEStructuralFeatures().add(age);
		personClass.getEStructuralFeatures().add(nicknames);
		personClass.getEStructuralFeatures().add(friend);
		personClass.getEStructuralFeatures().add(friends);
		personClass.getEStructuralFeatures().add(home);
		// the dynamic EFactory needs a containing package
		EPackage ePackage = ecore.createEPackage();
		ePackage.setName("tck");
		ePackage.setNsURI("urn:changetemplates:test");
		ePackage.setNsPrefix("tck");
		ePackage.getEClassifiers().add(personClass);

		person = ePackage.getEFactoryInstance().create(personClass);
		person.eSet(pid, "1");
		person.eSet(name, "Alice");
		person.eSet(age, 30);
		bob = ePackage.getEFactoryInstance().create(personClass);
		bob.eSet(pid, "2");
		bob.eSet(name, "Bob");
		carl = ePackage.getEFactoryInstance().create(personClass);
		carl.eSet(pid, "3");
		carl.eSet(name, "Carl");
	}

	/** Map-backed keyed find — the memory shape of the backend resolvers (issue #107). */
	private ReferenceResolver resolver() {
		Map<String, EObject> universe = Map.of("2", bob, "3", carl);
		return (reference, id) -> universe.get(id);
	}

	private UpdateEntry refEntry(UpdateKind kind, EReference reference) {
		return entry(kind, reference);
	}

	private UpdateTemplate template(UpdateEntry... entries) {
		UpdateTemplate template = CommandFactory.eINSTANCE.createUpdateTemplate();
		for (UpdateEntry entry : entries) {
			template.getEntries().add(entry);
		}
		return template;
	}

	private UpdateEntry entry(UpdateKind kind, EStructuralFeature feature) {
		UpdateEntry entry = CommandFactory.eINSTANCE.createUpdateEntry();
		entry.setKind(kind);
		entry.setFeature(feature);
		return entry;
	}

	@Test
	void setDecodesTheLiteralIntoTheAttributeType() throws QueryException {
		UpdateEntry set = entry(UpdateKind.SET, age);
		set.setValue("31");
		ChangeTemplates.apply(template(set), person);
		assertThat(person.eGet(age)).isEqualTo(31);
	}

	@Test
	void setWithNullLiteralClearsTheValue() throws QueryException {
		UpdateEntry set = entry(UpdateKind.SET, name);
		ChangeTemplates.apply(template(set), person);
		assertThat(person.eGet(name)).isNull();
	}

	@Test
	void unsetRestoresTheDefault() throws QueryException {
		ChangeTemplates.apply(template(entry(UpdateKind.UNSET, age)), person);
		assertThat(person.eGet(age)).isEqualTo(0);
		assertThat(person.eIsSet(age)).isFalse();
	}

	@Test
	void entriesApplyInTemplateOrder() throws QueryException {
		UpdateEntry first = entry(UpdateKind.SET, name);
		first.setValue("Bob");
		UpdateEntry second = entry(UpdateKind.SET, name);
		second.setValue("Carol");
		ChangeTemplates.apply(template(first, second), person);
		assertThat(person.eGet(name)).isEqualTo("Carol");
	}

	@Test
	void addAppendsAndInsertsIntoManyValued() throws QueryException {
		UpdateEntry append = entry(UpdateKind.ADD, nicknames);
		append.setValue("Ally");
		UpdateEntry insert = entry(UpdateKind.ADD, nicknames);
		insert.setValue("Al");
		insert.setIndex(0);
		ChangeTemplates.apply(template(append, insert), person);
		assertThat(person.eGet(nicknames)).isEqualTo(List.of("Al", "Ally"));
	}

	@Test
	void removeByIndexAndByValue() throws QueryException {
		listOf(nicknames).addAll(List.of("Al", "Ally", "Lissy"));
		UpdateEntry byIndex = entry(UpdateKind.REMOVE, nicknames);
		byIndex.setIndex(0);
		UpdateEntry byValue = entry(UpdateKind.REMOVE, nicknames);
		byValue.setOldValue("Lissy");
		ChangeTemplates.apply(template(byIndex, byValue), person);
		assertThat(person.eGet(nicknames)).isEqualTo(List.of("Ally"));

		UpdateEntry missing = entry(UpdateKind.REMOVE, nicknames);
		missing.setOldValue("Nobody");
		assertThatThrownBy(() -> ChangeTemplates.apply(template(missing), person))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("not present");
	}

	@Test
	void moveReordersTheList() throws QueryException {
		listOf(nicknames).addAll(List.of("Al", "Ally", "Lissy"));
		UpdateEntry move = entry(UpdateKind.MOVE, nicknames);
		move.setIndex(2);
		move.setToIndex(0);
		ChangeTemplates.apply(template(move), person);
		assertThat(person.eGet(nicknames)).isEqualTo(List.of("Lissy", "Al", "Ally"));
	}

	@Test
	void multiplicityMismatchesAreRefused() {
		UpdateEntry setOnMany = entry(UpdateKind.SET, nicknames);
		assertThatThrownBy(() -> ChangeTemplates.validate(template(setOnMany), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("many-valued");

		UpdateEntry addOnSingle = entry(UpdateKind.ADD, age);
		assertThatThrownBy(() -> ChangeTemplates.validate(template(addOnSingle), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("single-valued");
	}

	@Test
	void entriesWithoutAFeatureAreRefused() {
		UpdateEntry missing = CommandFactory.eINSTANCE.createUpdateEntry();
		missing.setKind(UpdateKind.SET);
		missing.setValue("x");
		assertThatThrownBy(() -> ChangeTemplates.validate(template(missing), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("names no feature");
	}

	@Test
	void featuresOfAnotherTypeAreRefused() {
		// same name and position as Person.name, but a different feature — never matched by name
		EClass dogClass = EcoreFactory.eINSTANCE.createEClass();
		dogClass.setName("Dog");
		EAttribute dogPid = EcoreFactory.eINSTANCE.createEAttribute();
		dogPid.setName("pid");
		dogPid.setEType(EcorePackage.Literals.ESTRING);
		EAttribute dogName = EcoreFactory.eINSTANCE.createEAttribute();
		dogName.setName("name");
		dogName.setEType(EcorePackage.Literals.ESTRING);
		dogClass.getEStructuralFeatures().add(dogPid);
		dogClass.getEStructuralFeatures().add(dogName);

		UpdateEntry foreign = entry(UpdateKind.SET, dogName);
		foreign.setValue("Rex");
		assertThatThrownBy(() -> ChangeTemplates.validate(template(foreign), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("'Dog.name' is not a feature of type 'Person'");
		assertThatThrownBy(() -> ChangeTemplates.apply(template(foreign), person))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("not a feature of type 'Person'");
		assertThat(person.eGet(name)).isEqualTo("Alice");
	}

	@Test
	void inheritedFeaturesApplyToASubtype() throws QueryException {
		EClass employeeClass = EcoreFactory.eINSTANCE.createEClass();
		employeeClass.setName("Employee");
		employeeClass.getESuperTypes().add(personClass);
		EAttribute salary = EcoreFactory.eINSTANCE.createEAttribute();
		salary.setName("salary");
		salary.setEType(EcorePackage.Literals.EINT);
		employeeClass.getEStructuralFeatures().add(salary);
		personClass.getEPackage().getEClassifiers().add(employeeClass);
		EObject employee = personClass.getEPackage().getEFactoryInstance().create(employeeClass);

		// a feature declared on the supertype addresses the subtype as well
		UpdateEntry rename = entry(UpdateKind.SET, name);
		rename.setValue("Dora");
		UpdateEntry raise = entry(UpdateKind.SET, salary);
		raise.setValue("5000");
		ChangeTemplates.validate(template(rename, raise), employeeClass);
		ChangeTemplates.apply(template(rename, raise), employee);
		assertThat(employee.eGet(name)).isEqualTo("Dora");
		assertThat(employee.eGet(salary)).isEqualTo(5000);

		// a subtype's own feature does not belong to the supertype a selector names
		UpdateEntry onSubtype = entry(UpdateKind.SET, salary);
		onSubtype.setValue("1");
		assertThatThrownBy(() -> ChangeTemplates.validate(template(onSubtype), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("'Employee.salary' is not a feature of type 'Person'");
	}

	@Test
	void anUnresolvedFeatureIsRefused() {
		// a command read back without its model carries the feature as a proxy
		EAttribute proxy = EcoreFactory.eINSTANCE.createEAttribute();
		((InternalEObject) proxy).eSetProxyURI(URI.createURI("urn:missing#//Person/name"));
		UpdateEntry unresolved = entry(UpdateKind.SET, proxy);
		unresolved.setValue("x");
		assertThatThrownBy(() -> ChangeTemplates.validate(template(unresolved), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("unresolved feature urn:missing#//Person/name");
	}

	@Test
	void referenceSetBindsTheResolvedTarget() throws QueryException {
		// reference values are target ids, resolved via the keyed-find contract (issue #107)
		UpdateEntry set = refEntry(UpdateKind.SET, friend);
		set.setValue("2");
		ChangeTemplates.validate(template(set), personClass);
		ChangeTemplates.apply(template(set), person, resolver());
		assertThat(person.eGet(friend)).isSameAs(bob);

		// a null value clears without touching the resolver
		UpdateEntry clear = refEntry(UpdateKind.SET, friend);
		ChangeTemplates.apply(template(clear), person, resolver());
		assertThat(person.eGet(friend)).isNull();
	}

	@Test
	void referenceSetWithDanglingTargetIsRefused() {
		UpdateEntry set = refEntry(UpdateKind.SET, friend);
		set.setValue("99");
		assertThatThrownBy(() -> ChangeTemplates.apply(template(set), person, resolver()))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("no Person with id '99'");
	}

	@Test
	void referenceUnsetClears() throws QueryException {
		person.eSet(friend, bob);
		ChangeTemplates.apply(template(refEntry(UpdateKind.UNSET, friend)), person, resolver());
		assertThat(person.eGet(friend)).isNull();
	}

	@Test
	void manyReferenceAddsAndRemovesById() throws QueryException {
		UpdateEntry add = refEntry(UpdateKind.ADD, friends);
		add.setValue("2");
		UpdateEntry addMore = refEntry(UpdateKind.ADD, friends);
		addMore.setValue("3");
		ChangeTemplates.apply(template(add, addMore), person, resolver());
		assertThat(person.eGet(friends)).asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.LIST)
				.containsExactly(bob, carl);

		// REMOVE is by id identity, never by index (issue #107)
		UpdateEntry remove = refEntry(UpdateKind.REMOVE, friends);
		remove.setOldValue("2");
		ChangeTemplates.apply(template(remove), person, resolver());
		assertThat(person.eGet(friends)).asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.LIST)
				.containsExactly(carl);

		UpdateEntry missing = refEntry(UpdateKind.REMOVE, friends);
		missing.setOldValue("2");
		assertThatThrownBy(() -> ChangeTemplates.apply(template(missing), person, resolver()))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("no member with id '2'");

		UpdateEntry byIndex = refEntry(UpdateKind.REMOVE, friends);
		byIndex.setIndex(0);
		assertThatThrownBy(() -> ChangeTemplates.apply(template(byIndex), person, resolver()))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("by id");
	}

	@Test
	void containmentAndMoveOnReferencesStayRefused() {
		UpdateEntry containment = refEntry(UpdateKind.SET, home);
		containment.setValue("2");
		assertThatThrownBy(() -> ChangeTemplates.validate(template(containment), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("containment");

		UpdateEntry move = refEntry(UpdateKind.MOVE, friends);
		move.setIndex(0);
		move.setToIndex(1);
		assertThatThrownBy(() -> ChangeTemplates.validate(template(move), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("MOVE");
	}

	@SuppressWarnings("unchecked")
	@Test
	void insertBindingResolvesExternalTargetsAndKeepsPayloadInternalOnes() throws QueryException {
		// alice's friend is EXTERNAL (bob, exists), friends mixes a payload pal and carl
		EObject pal = person.eClass().getEPackage().getEFactoryInstance().create(personClass);
		pal.eSet(pid, "11");
		person.eSet(friend, bob);
		((List<Object>) person.eGet(friends)).add(pal);
		((List<Object>) person.eGet(friends)).add(carl);

		org.eclipse.emf.ecore.util.EcoreUtil.Copier copier = new org.eclipse.emf.ecore.util.EcoreUtil.Copier();
		List<EObject> copies = new ArrayList<>(copier.copyAll(List.of(person, pal)));
		copier.copyReferences();
		ChangeTemplates.bindInsertReferences(copier, resolver());

		EObject personCopy = copies.get(0);
		assertThat(personCopy.eGet(friend)).as("external single ref binds the resolved target").isSameAs(bob);
		assertThat(personCopy.eGet(friends)).asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.LIST)
				.as("payload-internal member stays the copy, external member resolves")
				.containsExactly(copier.get(pal), carl);
	}

	@Test
	void insertBindingRefusesDanglingAndIdLessTargets() {
		EObject stub = person.eClass().getEPackage().getEFactoryInstance().create(personClass);
		stub.eSet(pid, "99");
		person.eSet(friend, stub);
		org.eclipse.emf.ecore.util.EcoreUtil.Copier dangling = new org.eclipse.emf.ecore.util.EcoreUtil.Copier();
		dangling.copyAll(List.of(person));
		dangling.copyReferences();
		assertThatThrownBy(() -> ChangeTemplates.bindInsertReferences(dangling, resolver()))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("no Person with id '99'");

		EObject idLess = person.eClass().getEPackage().getEFactoryInstance().create(personClass);
		person.eSet(friend, idLess);
		org.eclipse.emf.ecore.util.EcoreUtil.Copier noId = new org.eclipse.emf.ecore.util.EcoreUtil.Copier();
		noId.copyAll(List.of(person));
		noId.copyReferences();
		assertThatThrownBy(() -> ChangeTemplates.bindInsertReferences(noId, resolver()))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("carries no id");
	}

	@Test
	void referenceEntriesWithoutResolverAreRefused() {
		UpdateEntry set = refEntry(UpdateKind.SET, friend);
		set.setValue("2");
		assertThatThrownBy(() -> ChangeTemplates.apply(template(set), person))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("ReferenceResolver");
	}

	@Test
	void undecodableLiteralsAndEmptyTemplatesAreRefused() {
		UpdateEntry bad = entry(UpdateKind.SET, age);
		bad.setValue("not-a-number");
		assertThatThrownBy(() -> ChangeTemplates.apply(template(bad), person))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("Cannot decode");

		assertThatThrownBy(() -> ChangeTemplates.validate(template(), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("no entries");
	}

	@SuppressWarnings("unchecked")
	private List<String> listOf(EAttribute attribute) {
		return (List<String>) person.eGet(attribute);
	}
}
