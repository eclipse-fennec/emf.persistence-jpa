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
package org.eclipse.fennec.persistence.diagnostic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link PersistenceDiagnostic} factories (issue #19).
 */
class PersistenceDiagnosticTest {

	private static final String SOURCE = "org.eclipse.fennec.persistence.test";
	private static final URI LOCATION = URI.createURI("jpa://test/Person/4711");

	@Test
	void errorCarriesSeveritySourceMessageAndLocation() {
		PersistenceDiagnostic diagnostic = PersistenceDiagnostic.error(SOURCE, "boom", LOCATION);

		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(diagnostic.getSource()).isEqualTo(SOURCE);
		assertThat(diagnostic.getMessage()).isEqualTo("boom");
		assertThat(diagnostic.getLocation()).isEqualTo(LOCATION.toString());
		assertThat(diagnostic.getCause()).isNull();
	}

	@Test
	void warningCarriesSeverity() {
		PersistenceDiagnostic diagnostic = PersistenceDiagnostic.warning(SOURCE, "careful", LOCATION);

		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.WARNING);
		assertThat(diagnostic.getMessage()).isEqualTo("careful");
	}

	@Test
	void causePreservesTheOriginatingException() {
		IllegalStateException cause = new IllegalStateException("db down");

		assertThat(PersistenceDiagnostic.error(SOURCE, "boom", LOCATION, cause).getCause())
				.isSameAs(cause);
		assertThat(PersistenceDiagnostic.warning(SOURCE, "careful", LOCATION, cause).getCause())
				.isSameAs(cause);
	}

	@Test
	void nullLocationIsAllowed() {
		PersistenceDiagnostic diagnostic = PersistenceDiagnostic.error(SOURCE, "boom", null);

		assertThat(diagnostic.getLocation()).isNull();
		assertThat(diagnostic.toString()).doesNotContain("(");
	}

	@Test
	void lineAndColumnAreUnusedInResourceContext() {
		PersistenceDiagnostic diagnostic = PersistenceDiagnostic.warning(SOURCE, "careful", LOCATION);

		assertThat(diagnostic.getLine()).isZero();
		assertThat(diagnostic.getColumn()).isZero();
	}

	@Test
	void sourceAndMessageAreMandatory() {
		assertThatThrownBy(() -> PersistenceDiagnostic.error(null, "boom", LOCATION))
				.isInstanceOf(NullPointerException.class)
				.hasMessageContaining("source");
		assertThatThrownBy(() -> PersistenceDiagnostic.error(SOURCE, null, LOCATION))
				.isInstanceOf(NullPointerException.class)
				.hasMessageContaining("message");
	}

	@Test
	void toStringNamesSeveritySourceAndLocation() {
		assertThat(PersistenceDiagnostic.error(SOURCE, "boom", LOCATION).toString())
				.startsWith("ERROR [" + SOURCE + "] boom")
				.contains(LOCATION.toString());
		assertThat(PersistenceDiagnostic.warning(SOURCE, "careful", null).toString())
				.startsWith("WARNING [" + SOURCE + "] careful");
	}
}
