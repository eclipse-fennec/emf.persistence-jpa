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
package org.eclipse.fennec.persistence;

/**
 * Special exception for the EMF persistence
 * @author Mark Hoffmann
 * @since 15.11.2022
 */
public class PersistenceException extends Exception {

	/** serialVersionUID */
	private static final long serialVersionUID = 4945756658686122188L;
	
	public PersistenceException(String message) {
		super(message);
	}
	
	public PersistenceException(Throwable reason) {
		super(reason);
	}
	
	public PersistenceException(String message, Throwable reason) {
		super(message, reason);
	}

}
