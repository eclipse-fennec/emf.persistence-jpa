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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

/**
 * Answers what a store holds, read past the backend under test (issue #329).
 * <p>
 * A write-path case must not verify through the stack that wrote — that stack can answer from
 * its own memory, hide a {@code NULL} foreign key or re-insert a deleted row unseen. A binding
 * implements this with the store's native access: plain JDBC for a relational store, the raw
 * driver documents for Mongo. Ids are the objects' EMF ids in their string form.
 */
public interface StoreProbe {

	/**
	 * The ids of the objects stored as top-level objects of the type, including its subtypes.
	 * An object stored only inside its container (an embedded document) is not one.
	 *
	 * @param type the type
	 * @return the stored ids, never {@code null}
	 */
	Set<String> ids(EClass type);

	/**
	 * The target a single-valued reference of a stored object points to in the store.
	 *
	 * @param type the owner's type
	 * @param id the owner's id
	 * @param reference a single-valued reference of the type
	 * @return the target's id, or empty when the store holds no target
	 */
	Optional<String> reference(EClass type, String id, EReference reference);

	/**
	 * The targets a many-valued reference of a stored object holds in the store, in the stored
	 * order where the store keeps one.
	 *
	 * @param type the owner's type
	 * @param id the owner's id
	 * @param reference a many-valued reference of the type
	 * @return the target ids, never {@code null}
	 */
	List<String> references(EClass type, String id, EReference reference);

	/**
	 * Whether contained objects are stored on their own (rows of their own table) rather than
	 * inside their container. When they are, {@link #ids(EClass)} of a contained type reveals
	 * orphans: stored children no container holds any more.
	 *
	 * @return {@code true} for a relational store
	 */
	boolean storesContainedObjectsSeparately();
}
