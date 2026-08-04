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
package org.eclipse.fennec.persistence.eclipselink.spi.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link EPersistenceContextImpl} constructor correctness.
 */
class EPersistenceContextImplTest {

	@Test
	@DisplayName("Second constructor sets properties on the copy, not the parameter")
	void testCopyConstructorSetsPropertiesOnCopy() {
		PersistenceUnit original = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		original.setName("test-pu");
		// Intentionally NO properties set on original

		EPersistenceContextImpl ctx = new EPersistenceContextImpl(original);

		// The context's PU must have properties set (the bug was setting on the parameter)
		assertThat(ctx.getPersistenceUnit().getProperties()).isNotNull();
		// The original must NOT be modified (it was copied)
		assertThat(original.getProperties()).isNull();
	}

	@Test
	@DisplayName("Second constructor creates independent copy")
	void testCopyConstructorCreatesIndependentCopy() {
		PersistenceUnit original = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		original.setName("test-pu");

		EPersistenceContextImpl ctx = new EPersistenceContextImpl(original);

		// Modifying original should not affect context
		original.setName("modified");
		assertThat(ctx.getPersistenceUnitName()).isEqualTo("test-pu");
	}

	@Test
	@DisplayName("Context generates unique ID")
	void testUniqueId() {
		PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		pu.setName("test");

		EPersistenceContextImpl ctx1 = new EPersistenceContextImpl(pu);
		EPersistenceContextImpl ctx2 = new EPersistenceContextImpl(pu);

		assertThat(ctx1.getId()).isNotNull();
		assertThat(ctx2.getId()).isNotNull();
		assertThat(ctx1.getId()).isNotEqualTo(ctx2.getId());
	}

	@Test
	@DisplayName("First constructor with name and mappings sets properties")
	void testNameConstructorSetsProperties() {
		EPersistenceContextImpl ctx = new EPersistenceContextImpl("test-pu", List.of());

		assertThat(ctx.getPersistenceUnit()).isNotNull();
		assertThat(ctx.getPersistenceUnitName()).isEqualTo("test-pu");
		assertThat(ctx.getPersistenceUnit().getProperties()).isNotNull();
	}
}
