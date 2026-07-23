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
package org.eclipse.fennec.model.stream;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Smoke test for the promoted stream model.
 *
 * @author Mark Hoffmann
 */
class StreamModelTest {

	private final StreamFactory factory = StreamFactory.eINSTANCE;

	@Test
	void changeSetWithEntriesBuilds() {
		ChangeSet changeSet = factory.createChangeSet();
		changeSet.setId("cs-1");
		changeSet.setStreamId("s42");
		changeSet.setSequence(7);

		ChangeEntry set = factory.createChangeEntry();
		set.setObjectId("42");
		set.setKind(DeltaKind.SET);
		set.setFeatureId(1);
		set.setValueOld("12");
		set.setValueNew("14");
		changeSet.getEntries().add(set);

		assertThat(changeSet.getEntries()).hasSize(1);
		assertThat(set.eContainer()).isSameAs(changeSet);
		assertThat(set.getKind()).isEqualTo(DeltaKind.SET);
	}

	@Test
	void deltaKindCataloqueIsComplete() {
		assertThat(DeltaKind.values()).hasSize(15);
		assertThat(DeltaKind.valueOf("MIGRATE_OUT")).isNotNull();
	}

	@Test
	void packageMetadata() {
		assertThat(StreamPackage.eINSTANCE.getNsURI()).isEqualTo("https://org.eclipse/fennec/stream/1.0.0");
	}
}
