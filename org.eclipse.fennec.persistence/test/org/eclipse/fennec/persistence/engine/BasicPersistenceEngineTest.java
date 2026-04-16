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
package org.eclipse.fennec.persistence.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BasicPersistenceEngine} lifecycle and dispose behavior.
 */
class BasicPersistenceEngineTest {

	/** Minimal concrete subclass for testing the abstract base. */
	private static class TestEngine extends BasicPersistenceEngine {}

	private TestEngine engine;

	@BeforeEach
	void setUp() {
		engine = new TestEngine();
	}

	@Test
	@DisplayName("setResource stores resource and getResource returns it")
	void testSetAndGetResource() {
		PersistenceResource res = mock(PersistenceResource.class);
		engine.setResource(res);
		assertThat(engine.getResource()).isSameAs(res);
	}

	@Test
	@DisplayName("dispose clears resource, options and properties")
	void testDisposeClearsState() {
		PersistenceResource res = mock(PersistenceResource.class);
		engine.setResource(res);
		engine.getProperties().put("key", "value");
		engine.getMergedOptions().put("opt", "val");

		engine.dispose();

		assertThat(engine.getResource()).isNull();
		assertThat(engine.getProperties()).isEmpty();
		assertThat(engine.getMergedOptions()).isEmpty();
	}

	@Test
	@DisplayName("dispose is idempotent")
	void testDisposeIdempotent() {
		engine.setResource(mock(PersistenceResource.class));

		engine.dispose();
		engine.dispose();

		assertThat(engine.getResource()).isNull();
		assertThat(engine.getProperties()).isEmpty();
		assertThat(engine.getMergedOptions()).isEmpty();
	}

	@Test
	@DisplayName("dispose on fresh engine is safe")
	void testDisposeOnFreshEngine() {
		engine.dispose();

		assertThat(engine.getResource()).isNull();
		assertThat(engine.getProperties()).isEmpty();
		assertThat(engine.getMergedOptions()).isEmpty();
	}
}
