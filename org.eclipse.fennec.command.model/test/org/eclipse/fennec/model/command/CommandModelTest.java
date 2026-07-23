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
import org.eclipse.fennec.model.stream.ChangeEntry;
import org.eclipse.fennec.model.stream.ChangeSet;
import org.eclipse.fennec.model.stream.DeltaKind;
import org.eclipse.fennec.model.stream.StreamFactory;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.junit.jupiter.api.Test;

/**
 * Smoke test for the command model: CUD as commands over selector + template.
 *
 * @author Mark Hoffmann
 */
class CommandModelTest {

	private final CommandFactory factory = CommandFactory.eINSTANCE;

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
	void updateIsSelectorPlusChangeSetTemplate() {
		UpdateCommand update = factory.createUpdateCommand();
		update.setSelector(QueryFactory.eINSTANCE.createQuery());

		ChangeSet template = StreamFactory.eINSTANCE.createChangeSet();
		ChangeEntry entry = StreamFactory.eINSTANCE.createChangeEntry();
		entry.setKind(DeltaKind.SET);
		entry.setFeatureId(1);
		entry.setValueNew("42");
		template.getEntries().add(entry);
		update.setTemplate(template);

		assertThat(update.getTemplate().getEntries()).hasSize(1);
		assertThat(template.eContainer()).isSameAs(update);
	}
}
