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

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.Diagnostic;

/**
 * Bridge from EMF {@link Diagnostic}s to {@code java.util.logging}.
 * <p>
 * Diagnostics are the framework's error-reporting contract; logging is derived from
 * them — never produced alongside — and only at boundaries where the diagnostics would
 * otherwise be dropped (legacy non-result APIs, the internal persistence-unit deploy
 * path). APIs that hand the diagnostics to the caller do not log them.
 *
 * @author Mark Hoffmann
 * @since 21.07.2026
 */
public final class Diagnostics {

	private Diagnostics() {
	}

	/**
	 * Logs the diagnostic: severity mapped to the JUL level ({@code ERROR+} →
	 * {@code SEVERE}, {@code WARNING} → {@code WARNING}, else {@code INFO}), message
	 * as-is, the diagnostic's exception — if any — as thrown.
	 */
	public static void log(Logger logger, Diagnostic diagnostic) {
		requireNonNull(logger, "Logger must not be null");
		requireNonNull(diagnostic, "Diagnostic must not be null");
		logger.log(toLevel(diagnostic.getSeverity()), diagnostic.getMessage(), diagnostic.getException());
	}

	/**
	 * Logs every diagnostic of the list in order, see {@link #log(Logger, Diagnostic)}.
	 */
	public static void log(Logger logger, List<Diagnostic> diagnostics) {
		requireNonNull(diagnostics, "Diagnostics must not be null");
		diagnostics.forEach(diagnostic -> log(logger, diagnostic));
	}

	/**
	 * Returns the JUL level corresponding to the given {@link Diagnostic} severity.
	 */
	public static Level toLevel(int severity) {
		if (severity >= Diagnostic.ERROR) {
			return Level.SEVERE;
		}
		return severity == Diagnostic.WARNING ? Level.WARNING : Level.INFO;
	}
}
