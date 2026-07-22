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
package org.eclipse.fennec.persistence.query;

import org.eclipse.emf.common.util.Diagnostic;

/**
 * Signals that a query could not be validated or translated by a {@code QueryProcessor}
 * (see the generated API model, {@code query-api.ecore}).
 * <p>
 * When it results from a failed {@code QueryProcessor.validate(...)}, the originating
 * {@link Diagnostic} is carried via {@link #getDiagnostic()} so the caller can surface
 * the exact unsupported constructs.
 * <p>
 * Deliberately hand-written (exceptions are not modelled in EMF); the API model wraps it
 * as an {@code EDataType} for use in {@code eExceptions}.
 *
 * @author Mark Hoffmann
 * @since 22.07.2026
 */
public class QueryException extends Exception {

	private static final long serialVersionUID = 1L;

	private final transient Diagnostic diagnostic;

	/**
	 * @param message the detail message
	 */
	public QueryException(String message) {
		this(message, null, null);
	}

	/**
	 * @param message the detail message
	 * @param cause the underlying cause
	 */
	public QueryException(String message, Throwable cause) {
		this(message, cause, null);
	}

	/**
	 * @param diagnostic the validation diagnostic that caused this exception
	 */
	public QueryException(Diagnostic diagnostic) {
		this(diagnostic == null ? null : diagnostic.getMessage(), null, diagnostic);
	}

	/**
	 * @param message the detail message
	 * @param cause the underlying cause
	 * @param diagnostic the validation diagnostic that caused this exception, may be {@code null}
	 */
	public QueryException(String message, Throwable cause, Diagnostic diagnostic) {
		super(message, cause);
		this.diagnostic = diagnostic;
	}

	/**
	 * @return the validation diagnostic that caused this exception, or {@code null} if none
	 */
	public Diagnostic getDiagnostic() {
		return diagnostic;
	}
}
