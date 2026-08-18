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

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.stream.ChangeEntry;
import org.eclipse.fennec.model.stream.ChangeSet;
import org.eclipse.fennec.model.stream.DeltaKind;
import org.eclipse.fennec.model.stream.StreamFactory;
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

	private ChangeEntry refEntry(DeltaKind kind, EReference reference) {
		ChangeEntry entry = StreamFactory.eINSTANCE.createChangeEntry();
		entry.setKind(kind);
		entry.setFeatureId(personClass.getFeatureID(reference));
		return entry;
	}

	private ChangeSet template(ChangeEntry... entries) {
		ChangeSet template = StreamFactory.eINSTANCE.createChangeSet();
		for (ChangeEntry entry : entries) {
			template.getEntries().add(entry);
		}
		return template;
	}

	private ChangeEntry entry(DeltaKind kind, EAttribute feature) {
		ChangeEntry entry = StreamFactory.eINSTANCE.createChangeEntry();
		entry.setKind(kind);
		entry.setFeatureId(personClass.getFeatureID(feature));
		return entry;
	}

	@Test
	void setDecodesTheLiteralIntoTheAttributeType() throws QueryException {
		ChangeEntry set = entry(DeltaKind.SET, age);
		set.setValueNew("31");
		ChangeTemplates.apply(template(set), person);
		assertThat(person.eGet(age)).isEqualTo(31);
	}

	@Test
	void setWithNullLiteralClearsTheValue() throws QueryException {
		ChangeEntry set = entry(DeltaKind.SET, name);
		ChangeTemplates.apply(template(set), person);
		assertThat(person.eGet(name)).isNull();
	}

	@Test
	void unsetRestoresTheDefault() throws QueryException {
		ChangeTemplates.apply(template(entry(DeltaKind.UNSET, age)), person);
		assertThat(person.eGet(age)).isEqualTo(0);
		assertThat(person.eIsSet(age)).isFalse();
	}

	@Test
	void entriesApplyInTemplateOrder() throws QueryException {
		ChangeEntry first = entry(DeltaKind.SET, name);
		first.setValueNew("Bob");
		ChangeEntry second = entry(DeltaKind.SET, name);
		second.setValueNew("Carol");
		ChangeTemplates.apply(template(first, second), person);
		assertThat(person.eGet(name)).isEqualTo("Carol");
	}

	@Test
	void addAppendsAndInsertsIntoManyValued() throws QueryException {
		ChangeEntry append = entry(DeltaKind.ADD, nicknames);
		append.setValueNew("Ally");
		ChangeEntry insert = entry(DeltaKind.ADD, nicknames);
		insert.setValueNew("Al");
		insert.setIndex(0);
		ChangeTemplates.apply(template(append, insert), person);
		assertThat(person.eGet(nicknames)).isEqualTo(List.of("Al", "Ally"));
	}

	@Test
	void removeByIndexAndByValue() throws QueryException {
		listOf(nicknames).addAll(List.of("Al", "Ally", "Lissy"));
		ChangeEntry byIndex = entry(DeltaKind.REMOVE, nicknames);
		byIndex.setIndex(0);
		ChangeEntry byValue = entry(DeltaKind.REMOVE, nicknames);
		byValue.setValueOld("Lissy");
		ChangeTemplates.apply(template(byIndex, byValue), person);
		assertThat(person.eGet(nicknames)).isEqualTo(List.of("Ally"));

		ChangeEntry missing = entry(DeltaKind.REMOVE, nicknames);
		missing.setValueOld("Nobody");
		assertThatThrownBy(() -> ChangeTemplates.apply(template(missing), person))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("not present");
	}

	@Test
	void moveReordersTheList() throws QueryException {
		listOf(nicknames).addAll(List.of("Al", "Ally", "Lissy"));
		ChangeEntry move = entry(DeltaKind.MOVE, nicknames);
		move.setIndex(2);
		move.setToIndex(0);
		ChangeTemplates.apply(template(move), person);
		assertThat(person.eGet(nicknames)).isEqualTo(List.of("Lissy", "Al", "Ally"));
	}

	@Test
	void multiplicityMismatchesAreRefused() {
		ChangeEntry setOnMany = entry(DeltaKind.SET, nicknames);
		assertThatThrownBy(() -> ChangeTemplates.validate(template(setOnMany), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("many-valued");

		ChangeEntry addOnSingle = entry(DeltaKind.ADD, age);
		assertThatThrownBy(() -> ChangeTemplates.validate(template(addOnSingle), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("single-valued");
	}

	@Test
	void lifecycleAndAddressedKindsAreRefused() {
		for (DeltaKind kind : List.of(DeltaKind.CREATE, DeltaKind.DELETE, DeltaKind.PUT,
				DeltaKind.SET_AT, DeltaKind.KEYFRAME)) {
			ChangeEntry entry = entry(kind, age);
			assertThatThrownBy(() -> ChangeTemplates.validate(template(entry), personClass))
					.as("kind %s", kind)
					.isInstanceOf(QueryException.class)
					.hasMessageContaining("Unsupported template kind");
		}
	}

	@Test
	void unknownFeaturesAreRefused() {
		ChangeEntry unknown = StreamFactory.eINSTANCE.createChangeEntry();
		unknown.setKind(DeltaKind.SET);
		unknown.setFeatureId(999);
		assertThatThrownBy(() -> ChangeTemplates.validate(template(unknown), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("Unknown feature id");
	}

	@Test
	void referenceSetBindsTheResolvedTarget() throws QueryException {
		// reference values are target ids, resolved via the keyed-find contract (issue #107)
		ChangeEntry set = refEntry(DeltaKind.SET, friend);
		set.setValueNew("2");
		ChangeTemplates.validate(template(set), personClass);
		ChangeTemplates.apply(template(set), person, resolver());
		assertThat(person.eGet(friend)).isSameAs(bob);

		// a null valueNew clears without touching the resolver
		ChangeEntry clear = refEntry(DeltaKind.SET, friend);
		ChangeTemplates.apply(template(clear), person, resolver());
		assertThat(person.eGet(friend)).isNull();
	}

	@Test
	void referenceSetWithDanglingTargetIsRefused() {
		ChangeEntry set = refEntry(DeltaKind.SET, friend);
		set.setValueNew("99");
		assertThatThrownBy(() -> ChangeTemplates.apply(template(set), person, resolver()))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("no Person with id '99'");
	}

	@Test
	void referenceUnsetClears() throws QueryException {
		person.eSet(friend, bob);
		ChangeTemplates.apply(template(refEntry(DeltaKind.UNSET, friend)), person, resolver());
		assertThat(person.eGet(friend)).isNull();
	}

	@Test
	void manyReferenceAddsAndRemovesById() throws QueryException {
		ChangeEntry add = refEntry(DeltaKind.ADD, friends);
		add.setValueNew("2");
		ChangeEntry addMore = refEntry(DeltaKind.ADD, friends);
		addMore.setValueNew("3");
		ChangeTemplates.apply(template(add, addMore), person, resolver());
		assertThat(person.eGet(friends)).asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.LIST)
				.containsExactly(bob, carl);

		// REMOVE is by id identity, never by index (issue #107)
		ChangeEntry remove = refEntry(DeltaKind.REMOVE, friends);
		remove.setValueOld("2");
		ChangeTemplates.apply(template(remove), person, resolver());
		assertThat(person.eGet(friends)).asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.LIST)
				.containsExactly(carl);

		ChangeEntry missing = refEntry(DeltaKind.REMOVE, friends);
		missing.setValueOld("2");
		assertThatThrownBy(() -> ChangeTemplates.apply(template(missing), person, resolver()))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("no member with id '2'");

		ChangeEntry byIndex = refEntry(DeltaKind.REMOVE, friends);
		byIndex.setIndex(0);
		assertThatThrownBy(() -> ChangeTemplates.apply(template(byIndex), person, resolver()))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("by id");
	}

	@Test
	void containmentAndMoveOnReferencesStayRefused() {
		ChangeEntry containment = refEntry(DeltaKind.SET, home);
		containment.setValueNew("2");
		assertThatThrownBy(() -> ChangeTemplates.validate(template(containment), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("containment");

		ChangeEntry move = refEntry(DeltaKind.MOVE, friends);
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
		List<EObject> copies = new java.util.ArrayList<>(copier.copyAll(List.of(person, pal)));
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
		ChangeEntry set = refEntry(DeltaKind.SET, friend);
		set.setValueNew("2");
		assertThatThrownBy(() -> ChangeTemplates.apply(template(set), person))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("ReferenceResolver");
	}

	@Test
	void undecodableLiteralsAndEmptyTemplatesAreRefused() {
		ChangeEntry bad = entry(DeltaKind.SET, age);
		bad.setValueNew("not-a-number");
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
