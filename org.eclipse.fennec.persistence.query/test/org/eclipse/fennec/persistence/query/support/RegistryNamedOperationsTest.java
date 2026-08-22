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

import java.io.IOException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistries;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryWriter;
import org.eclipse.fennec.model.command.CommandFactory;
import org.eclipse.fennec.model.command.DeleteCommand;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The shared named-operation catalog over the EObject registry (issue #203) — one lookup for
 * both planes, instead of a private convention per backend.
 */
class RegistryNamedOperationsTest {

	private EObjectRegistryWriter writer;
	private NamedOperations catalog;

	@BeforeEach
	void setUp() {
		writer = EObjectRegistries.createRegistry("test.named-operations");
		catalog = new RegistryNamedOperations(writer);
	}

	@Test
	void storesAndLooksUpAQuery() throws IOException {
		Query query = namedQuery("adults");
		catalog.store("adults", query);

		assertThat(catalog.lookup("adults")).containsSame(query);
	}

	/**
	 * The write plane goes in the same catalog under the same rules — that is what
	 * {@code Command.name} (issue #201) was for, and why neither needs a wrapper.
	 */
	@Test
	void storesAndLooksUpACommand() throws IOException {
		DeleteCommand command = CommandFactory.eINSTANCE.createDeleteCommand();
		command.setName("purgeMinors");
		catalog.store("purgeMinors", command);

		assertThat(catalog.lookup("purgeMinors")).containsSame(command);
	}

	@Test
	void anUnknownNameIsEmptyRatherThanAnError() throws IOException {
		// a caller may consult several catalogs, so "not here" is an answer, not a failure
		assertThat(catalog.lookup("nothing-stored-here")).isEmpty();
	}

	@Test
	void storingUnderAnExistingNameReplaces() throws IOException {
		catalog.store("adults", namedQuery("adults"));
		Query replacement = namedQuery("adults");
		catalog.store("adults", replacement);

		assertThat(catalog.lookup("adults")).containsSame(replacement);
	}

	@Test
	void removeTakesItOutAndIsIdempotent() throws IOException {
		catalog.store("adults", namedQuery("adults"));
		catalog.remove("adults");
		assertThat(catalog.lookup("adults")).isEmpty();

		// removing an absent name is not an error — the end state is what was asked for
		catalog.remove("adults");
		assertThat(catalog.lookup("adults")).isEmpty();
	}

	/**
	 * A catalog built over a bare registry can be read but not written, and says so — a
	 * silently dropped write would be discovered much later, by a lookup that finds nothing.
	 */
	@Test
	void aReadOnlyCatalogRefusesWritesInsteadOfDroppingThem() throws IOException {
		catalog.store("adults", namedQuery("adults"));
		NamedOperations readOnly = new RegistryNamedOperations(writer.getRegistry());

		assertThat(readOnly.lookup("adults")).isPresent();
		assertThatThrownBy(() -> readOnly.store("more", namedQuery("more")))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("read-only");
		assertThatThrownBy(() -> readOnly.remove("adults"))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("read-only");
	}

	@Test
	void entriesAreAttributedToTheirSource() throws IOException {
		NamedOperations mine = new RegistryNamedOperations(writer, "someone.else");
		mine.store("adults", namedQuery("adults"));

		assertThat(writer.getRegistry().getEntry("adults"))
				.hasValueSatisfying(entry -> assertThat(entry.source()).isEqualTo("someone.else"));
	}

	private static Query namedQuery(String name) {
		Query query = QueryFactory.eINSTANCE.createQuery();
		EClass root = EcoreFactory.eINSTANCE.createEClass();
		root.setName("Person");
		query.setFrom(root);
		query.setName(name);
		return query;
	}
}
