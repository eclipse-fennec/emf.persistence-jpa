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
 *   Data In Motion - initial API and implementation
 ********************************************************************/
package org.eclipse.fennec.persistence.diagnostic;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * The shared {@link Resource.Diagnostic} of the persistence resource layer (issue #19)
 * — one shape for every backend instead of per-backend ad-hoc classes, so consumers
 * can handle {@link Resource#getErrors()}/{@link Resource#getWarnings()} uniformly:
 * <ul>
 * <li>{@link #getSeverity() severity} uses the EMF {@link Diagnostic} constants and
 * matches the list the diagnostic is surfaced on ({@code ERROR} → errors,
 * {@code WARNING} → warnings);</li>
 * <li>{@link #getSource() source} is the reporting bundle's namespace — the same
 * convention as the mapping-pipeline diagnostics;</li>
 * <li>{@link #getLocation() location} is the resource URI;</li>
 * <li>{@link #getCause() cause} preserves the originating exception for callers that
 * want to unwrap the backend stack trace.</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 04.08.2026
 */
public final class PersistenceDiagnostic implements Resource.Diagnostic {

	private final int severity;
	private final String source;
	private final String message;
	private final String location;
	private final Throwable cause;

	private PersistenceDiagnostic(int severity, String source, String message, URI location,
			Throwable cause) {
		this.severity = severity;
		this.source = requireNonNull(source, "source must not be null");
		this.message = requireNonNull(message, "message must not be null");
		this.location = isNull(location) ? null : location.toString();
		this.cause = cause;
	}

	/**
	 * Creates an error diagnostic for {@link Resource#getErrors()}.
	 *
	 * @param source the reporting bundle's namespace
	 * @param message the problem description
	 * @param location the resource URI; may be {@code null}
	 * @return the diagnostic
	 */
	public static PersistenceDiagnostic error(String source, String message, URI location) {
		return new PersistenceDiagnostic(Diagnostic.ERROR, source, message, location, null);
	}

	/**
	 * Creates an error diagnostic carrying the originating exception.
	 *
	 * @param source the reporting bundle's namespace
	 * @param message the problem description
	 * @param location the resource URI; may be {@code null}
	 * @param cause the originating exception; may be {@code null}
	 * @return the diagnostic
	 */
	public static PersistenceDiagnostic error(String source, String message, URI location,
			Throwable cause) {
		return new PersistenceDiagnostic(Diagnostic.ERROR, source, message, location, cause);
	}

	/**
	 * Creates a warning diagnostic for {@link Resource#getWarnings()}.
	 *
	 * @param source the reporting bundle's namespace
	 * @param message the problem description
	 * @param location the resource URI; may be {@code null}
	 * @return the diagnostic
	 */
	public static PersistenceDiagnostic warning(String source, String message, URI location) {
		return new PersistenceDiagnostic(Diagnostic.WARNING, source, message, location, null);
	}

	/**
	 * Creates a warning diagnostic carrying the originating exception.
	 *
	 * @param source the reporting bundle's namespace
	 * @param message the problem description
	 * @param location the resource URI; may be {@code null}
	 * @param cause the originating exception; may be {@code null}
	 * @return the diagnostic
	 */
	public static PersistenceDiagnostic warning(String source, String message, URI location,
			Throwable cause) {
		return new PersistenceDiagnostic(Diagnostic.WARNING, source, message, location, cause);
	}

	/**
	 * @return the EMF {@link Diagnostic} severity constant
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * @return the reporting bundle's namespace
	 */
	public String getSource() {
		return source;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public int getLine() {
		return 0;
	}

	@Override
	public int getColumn() {
		return 0;
	}

	/**
	 * @return the originating exception or {@code null}
	 */
	public Throwable getCause() {
		return cause;
	}

	@Override
	public String toString() {
		return (severity == Diagnostic.ERROR ? "ERROR" : "WARNING") + " [" + source + "] " + message
				+ (isNull(location) ? "" : " (" + location + ")");
	}
}
