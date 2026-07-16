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
package org.eclipse.fennec.persistence.tck;

/**
 * The MongoDB TCK binding against the String-id variant of the TCK model — same suite,
 * String-typed EMF id attributes (including ObjectId generation with write-back).
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
class MongoStringIdPersistenceTckTest extends MongoPersistenceTckTest {

	@Override
	protected String tckModelPath() {
		return "data/tck-string.ecore";
	}
}
