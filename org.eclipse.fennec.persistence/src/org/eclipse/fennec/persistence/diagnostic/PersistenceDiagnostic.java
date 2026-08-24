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

	/**
	 * No machine-readable classification — the diagnostic says what happened in its message
	 * only. The default for everything that nothing has had to route on yet.
	 */
	public static final int CODE_NONE = 0;

	/**
	 * The refusal to delete an object something still references (issue #195, §4c of
	 * {@code conformance-and-capabilities.md}), reported by every backend on every path
	 * (issue #229).
	 * <p>
	 * This is a <b>client-visible conflict</b>, not a server fault: the caller can delete the
	 * referrer or re-point it and try again. Consumers mapping onto a protocol — {@code emf.odata}
	 * onto HTTP 409 rather than 500 — switch on this instead of matching message text, which is
	 * the point: JPA and the two mongo paths word the same refusal three different ways, and a
	 * fourth wording must not break a consumer.
	 */
	public static final int CODE_REFERENTIAL_INTEGRITY = 1;

	private final int severity;
	private final int code;
	private final String source;
	private final String message;
	private final String location;
	private final Throwable cause;

	private PersistenceDiagnostic(int severity, int code, String source, String message,
			URI location, Throwable cause) {
		this.severity = severity;
		this.code = code;
		this.source = requireNonNull(source, "source must not be null");
		this.message = requireNonNull(message, "message must not be null");
		this.location = isNull(location) ? null : location.toString();
		this.cause = cause;
	}

	/**
	 * The machine-readable classification of this diagnostic, or {@link #CODE_NONE} when it
	 * carries none (issue #229).
	 * <p>
	 * {@link Resource.Diagnostic} has no {@code getCode()}, so this sits next to the interface
	 * members and consumers reach it after an {@code instanceof PersistenceDiagnostic}. The
	 * message stays human-readable and may be reworded at any time; the code is the part that
	 * is a contract.
	 *
	 * @return the code, or {@link #CODE_NONE}
	 */
	public int code() {
		return code;
	}

	/**
	 * Creates a classified error diagnostic (issue #229) — same as
	 * {@link #error(String, String, URI, Throwable)} plus a code consumers can route on.
	 *
	 * @param code one of the {@code CODE_*} constants
	 * @param source the reporting bundle's namespace
	 * @param message the problem description
	 * @param location the resource URI; may be {@code null}
	 * @param cause the originating exception; may be {@code null}
	 * @return the diagnostic
	 */
	public static PersistenceDiagnostic error(int code, String source, String message, URI location,
			Throwable cause) {
		return new PersistenceDiagnostic(Diagnostic.ERROR, code, source, message, location, cause);
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
		return new PersistenceDiagnostic(Diagnostic.ERROR, CODE_NONE, source, message, location, null);
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
		return new PersistenceDiagnostic(Diagnostic.ERROR, CODE_NONE, source, message, location, cause);
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
		return new PersistenceDiagnostic(Diagnostic.WARNING, CODE_NONE, source, message, location, null);
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
		return new PersistenceDiagnostic(Diagnostic.WARNING, CODE_NONE, source, message, location, cause);
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
