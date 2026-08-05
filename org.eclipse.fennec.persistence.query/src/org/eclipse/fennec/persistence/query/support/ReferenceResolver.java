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
package org.eclipse.fennec.persistence.query.support;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.query.QueryException;

/**
 * Resolves the id of a reference target to the object to bind — the backend's keyed-find
 * contract, handed to the {@link ChangeTemplates patch-apply engine} for reference
 * patching (issue #107). Implementations resolve against the backend the command runs on
 * (JPA: {@code em.find} in the command's transaction; Mongo: a keyed find in the
 * target's collection; memory: the candidate universe).
 *
 * @author Mark Hoffmann
 * @since 05.08.2026
 */
@FunctionalInterface
public interface ReferenceResolver {

	/**
	 * Resolves the given id to the target object to bind for the reference.
	 *
	 * @param reference the reference being patched; its {@link EReference#getEReferenceType()
	 *        target type} scopes the lookup
	 * @param id the target's id, as carried by the change entry; never {@code null}
	 * @return the object to bind, or {@code null} if no such target exists — the engine
	 *         turns that into the dangling-target refusal
	 * @throws QueryException if the lookup itself fails
	 */
	EObject resolve(EReference reference, String id) throws QueryException;
}
