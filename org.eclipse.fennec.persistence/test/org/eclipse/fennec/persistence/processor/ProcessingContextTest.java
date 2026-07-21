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
package org.eclipse.fennec.persistence.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EClass;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the diagnostic channel of {@link ProcessingContext}.
 */
class ProcessingContextTest {

	private ProcessingContext newContext() {
		List<Diagnostic> diagnostics = new ArrayList<>();
		return () -> diagnostics;
	}

	@Test
	void convenienceMethodsReportSeveritySourceMessageAndData() {
		ProcessingContext context = newContext();
		EClass element = EcoreFactory.eINSTANCE.createEClass();
		element.setName("Person");

		context.info("test.source", "informational");
		context.warning("test.source", "corrected something", element);
		Exception cause = new IllegalStateException("boom");
		context.error("test.source", "broken", element, cause);

		assertThat(context.getDiagnostics()).hasSize(3);

		Diagnostic info = context.getDiagnostics().get(0);
		assertThat(info.getSeverity()).isEqualTo(Diagnostic.INFO);
		assertThat(info.getSource()).isEqualTo("test.source");
		assertThat(info.getMessage()).isEqualTo("informational");
		assertThat(info.getCode()).isZero();
		assertThat(info.getData()).isEmpty();

		Diagnostic warning = context.getDiagnostics().get(1);
		assertThat(warning.getSeverity()).isEqualTo(Diagnostic.WARNING);
		assertThat(warning.getData()).isEqualTo(List.of(element));

		Diagnostic error = context.getDiagnostics().get(2);
		assertThat(error.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(error.getData()).isEqualTo(List.of(element, cause));
		assertThat(error.getException()).isSameAs(cause);
	}

	@Test
	void severityIsTheMaximumOfAllDiagnostics() {
		ProcessingContext context = newContext();
		assertThat(context.getSeverity()).isEqualTo(Diagnostic.OK);

		context.info("s", "i");
		assertThat(context.getSeverity()).isEqualTo(Diagnostic.INFO);
		context.warning("s", "w");
		assertThat(context.getSeverity()).isEqualTo(Diagnostic.WARNING);
		context.error("s", "e");
		assertThat(context.getSeverity()).isEqualTo(Diagnostic.ERROR);
		context.info("s", "i2");
		assertThat(context.getSeverity()).isEqualTo(Diagnostic.ERROR);
	}

	@Test
	void addDiagnosticRejectsNull() {
		assertThatThrownBy(() -> newContext().addDiagnostic(null))
				.isInstanceOf(NullPointerException.class);
	}
}
