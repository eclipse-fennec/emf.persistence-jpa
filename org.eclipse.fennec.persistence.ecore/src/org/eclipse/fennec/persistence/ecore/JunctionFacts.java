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
package org.eclipse.fennec.persistence.ecore;

import org.eclipse.emf.ecore.EReference;

/**
 * A junction table that became a ManyToMany pair instead of a class (issue #295).
 *
 * @param schema the schema the table lives in
 * @param name the junction table name
 * @param sourceKey the foreign key towards the owner of {@code reference}
 * @param targetKey the foreign key towards the type of {@code reference}
 * @param reference the owning side, on the class of {@code sourceKey}'s target
 * @param opposite the other side, on the class of {@code targetKey}'s target
 * @author Mark Hoffmann
 * @since 23.09.2026
 */
public record JunctionFacts(String schema, String name, TableFacts.ForeignKey sourceKey,
		TableFacts.ForeignKey targetKey, EReference reference, EReference opposite) {
}
