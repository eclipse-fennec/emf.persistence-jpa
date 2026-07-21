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

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;

/**
 * Base interface for the processors context.
 * <p>
 * The context is also the diagnostic channel of a processing run: every problem or
 * silent correction discovered while processing is reported here as an EMF
 * {@link Diagnostic} (never only logged), following the conventions used across
 * Fennec (m2x, codec): {@code source} is the reporting bundle's namespace,
 * {@code code} is unused ({@code 0}), and the affected model element — if any — is the
 * first entry of {@link Diagnostic#getData()}.
 *
 * @author Mark Hoffmann
 * @since 30.12.2024
 */
public interface ProcessingContext {

	/**
	 * Returns the diagnostics collected during this processing run, in report order.
	 * The list is live and owned by the context; never {@code null}.
	 */
	List<Diagnostic> getDiagnostics();

	/**
	 * Adds a diagnostic to this run.
	 */
	default void addDiagnostic(Diagnostic diagnostic) {
		getDiagnostics().add(requireNonNull(diagnostic, "Diagnostic must not be null"));
	}

	/**
	 * Reports an informational diagnostic.
	 *
	 * @param source the reporting bundle's namespace
	 * @param message the finished message text
	 * @param data optional context, the affected model element first; a causing
	 *        {@link Throwable} may be included
	 */
	default void info(String source, String message, Object... data) {
		addDiagnostic(new BasicDiagnostic(Diagnostic.INFO, source, 0, message, dataOrNull(data)));
	}

	/**
	 * Reports a warning: the input was silently corrected or processing continues in a
	 * degraded way.
	 *
	 * @param source the reporting bundle's namespace
	 * @param message the finished message text
	 * @param data optional context, the affected model element first; a causing
	 *        {@link Throwable} may be included
	 */
	default void warning(String source, String message, Object... data) {
		addDiagnostic(new BasicDiagnostic(Diagnostic.WARNING, source, 0, message, dataOrNull(data)));
	}

	/**
	 * Reports an error: the affected element cannot be processed correctly.
	 *
	 * @param source the reporting bundle's namespace
	 * @param message the finished message text
	 * @param data optional context, the affected model element first; a causing
	 *        {@link Throwable} may be included
	 */
	default void error(String source, String message, Object... data) {
		addDiagnostic(new BasicDiagnostic(Diagnostic.ERROR, source, 0, message, dataOrNull(data)));
	}

	/**
	 * Returns the highest severity of the collected diagnostics, {@link Diagnostic#OK}
	 * when none were reported.
	 */
	default int getSeverity() {
		return getDiagnostics().stream().mapToInt(Diagnostic::getSeverity).max().orElse(Diagnostic.OK);
	}

	private static Object[] dataOrNull(Object[] data) {
		return data.length == 0 ? null : data;
	}
}
