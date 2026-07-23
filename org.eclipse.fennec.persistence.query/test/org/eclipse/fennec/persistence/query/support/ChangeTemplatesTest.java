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
	private EAttribute name;
	private EAttribute age;
	private EAttribute nicknames;
	private EReference friend;
	private EObject person;

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
		friend = ecore.createEReference();
		friend.setName("friend");
		friend.setEType(personClass);
		personClass.getEStructuralFeatures().add(name);
		personClass.getEStructuralFeatures().add(age);
		personClass.getEStructuralFeatures().add(nicknames);
		personClass.getEStructuralFeatures().add(friend);
		// the dynamic EFactory needs a containing package
		EPackage ePackage = ecore.createEPackage();
		ePackage.setName("tck");
		ePackage.setNsURI("urn:changetemplates:test");
		ePackage.setNsPrefix("tck");
		ePackage.getEClassifiers().add(personClass);

		person = ePackage.getEFactoryInstance().create(personClass);
		person.eSet(name, "Alice");
		person.eSet(age, 30);
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
	void referencesAndUnknownFeaturesAreRefused() {
		ChangeEntry onReference = StreamFactory.eINSTANCE.createChangeEntry();
		onReference.setKind(DeltaKind.SET);
		onReference.setFeatureId(personClass.getFeatureID(friend));
		assertThatThrownBy(() -> ChangeTemplates.validate(template(onReference), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("reference");

		ChangeEntry unknown = StreamFactory.eINSTANCE.createChangeEntry();
		unknown.setKind(DeltaKind.SET);
		unknown.setFeatureId(999);
		assertThatThrownBy(() -> ChangeTemplates.validate(template(unknown), personClass))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("Unknown feature id");
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
