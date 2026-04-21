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
package org.eclipse.fennec.persistence.eclipselink.resource;

import static java.util.Objects.isNull;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * EMF {@link Resource.Diagnostic} surfaced on {@link Resource#getErrors()} and
 * {@link Resource#getWarnings()} for JPA-backed resources. The optional
 * {@link #getCause() cause} preserves the originating exception for callers
 * that want to unwrap the JPA stack trace.
 */
final class JPADiagnostic implements Resource.Diagnostic {

	private final String message;
	private final String location;
	private final Throwable cause;

	JPADiagnostic(String message, URI location) {
		this(message, location, null);
	}

	JPADiagnostic(String message, URI location, Throwable cause) {
		this.message = message;
		this.location = isNull(location) ? null : location.toString();
		this.cause = cause;
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

	Throwable getCause() {
		return cause;
	}
}
