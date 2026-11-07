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
package org.eclipse.fennec.model.command;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.QueryPackage;
import org.junit.jupiter.api.Test;

/**
 * Smoke test for the command model: CUD as commands over selector + template.
 *
 * @author Mark Hoffmann
 */
class CommandModelTest {

	private final CommandFactory factory = CommandFactory.eINSTANCE;

	/**
	 * A command can name itself, exactly as a query can (issue #201). That is what makes it
	 * depositable in a named-operation catalog on the same terms — and why no wrapper model is
	 * needed around either: the registry keys on the name the object carries.
	 */
	@Test
	void everyCommandKindCanCarryAName() {
		InsertCommand insert = CommandFactory.eINSTANCE.createInsertCommand();
		insert.setName("importPeople");
		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setName("purgeMinors");
		UpdateCommand update = CommandFactory.eINSTANCE.createUpdateCommand();
		update.setName("ageEveryone");

		assertThat(insert.getName()).isEqualTo("importPeople");
		assertThat(delete.getName()).isEqualTo("purgeMinors");
		assertThat(update.getName()).isEqualTo("ageEveryone");
	}

	/**
	 * The name is optional: a command executed directly needs none, and nothing may start
	 * requiring one.
	 */
	@Test
	void aCommandWithoutANameIsStillValid() {
		assertThat(CommandFactory.eINSTANCE.createInsertCommand().getName()).isNull();
	}

	/**
	 * Named the same way on both planes — the write side reuses the query side's shape rather
	 * than inventing a second convention (issue #163).
	 */
	@Test
	void theNameIsTheIdOnBothPlanes() {
		assertThat(CommandPackage.eINSTANCE.getCommand_Name().isID())
				.as("a command's name identifies it, as a query's does").isTrue();
		assertThat(QueryPackage.eINSTANCE.getQuery_Name().isID()).isTrue();
	}

	@Test
	void insertCarriesContainedPayload() {
		InsertCommand insert = factory.createInsertCommand();
		insert.getObjects().add(EcoreFactory.eINSTANCE.createEClass());
		assertThat(insert.getObjects()).hasSize(1);
		assertThat(insert.getObjects().get(0).eContainer()).isSameAs(insert);
	}

	@Test
	void deleteIsASelector() {
		Query selector = QueryFactory.eINSTANCE.createQuery();
		DeleteCommand delete = factory.createDeleteCommand();
		delete.setSelector(selector);
		assertThat(delete.getSelector()).isSameAs(selector);
		assertThat(selector.eContainer()).isSameAs(delete);
	}

	@Test
	void updateIsSelectorPlusUpdateTemplate() {
		UpdateCommand update = factory.createUpdateCommand();
		update.setSelector(QueryFactory.eINSTANCE.createQuery());

		UpdateTemplate template = factory.createUpdateTemplate();
		UpdateEntry entry = factory.createUpdateEntry();
		entry.setKind(UpdateKind.SET);
		entry.setFeature(EcorePackage.Literals.ENAMED_ELEMENT__NAME);
		entry.setValue("42");
		template.getEntries().add(entry);
		update.setTemplate(template);

		assertThat(update.getTemplate().getEntries()).hasSize(1);
		assertThat(entry.getIndex()).as("no position given").isEqualTo(-1);
		assertThat(entry.getToIndex()).isEqualTo(-1);
		assertThat(entry.getFeature()).as("a reference, not a copy")
				.isSameAs(EcorePackage.Literals.ENAMED_ELEMENT__NAME);
		assertThat(template.eContainer()).isSameAs(update);
	}
}
