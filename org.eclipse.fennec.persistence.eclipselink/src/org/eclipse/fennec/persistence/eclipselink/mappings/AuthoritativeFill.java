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
package org.eclipse.fennec.persistence.eclipselink.mappings;

/**
 * Marks, per thread, that attribute values are currently being filled <b>from a database
 * row</b> — a build or refresh in {@code EObjectBuilder.buildAttributesIntoObject} — as
 * opposed to the accumulating fills of merge, backup-clone and indirection bookkeeping
 * (issue #144).
 * <p>
 * {@link EReferenceAccessor} needs the distinction because EclipseLink's
 * {@code AttributeAccessor} API does not carry it: a collection value arriving during a row
 * fill is the store's complete truth for that reference, so members absent from it are stale
 * and must be dropped — while the very same call during merge or backup building may carry
 * partial content, where dropping absentees would destroy real state. Without the split, a
 * refresh could never shrink a collection: EclipseLink's cache invalidation keeps object
 * identity ({@code ObjectBuilder.buildObject} refreshes {@code cacheKey.getObject()}
 * itself), so children deleted in the database were resurrected on every read.
 * <p>
 * A depth counter rather than a flag, because row fills nest: building a parent's
 * containment children builds each child within the parent's fill.
 */
public final class AuthoritativeFill {

	private static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[1]);

	private AuthoritativeFill() {
	}

	/** Enters a row fill; must be balanced by {@link #exit()} in a {@code finally}. */
	public static void enter() {
		DEPTH.get()[0]++;
	}

	/** Leaves a row fill. */
	public static void exit() {
		DEPTH.get()[0]--;
	}

	/** @return {@code true} while the current thread is filling attributes from a row */
	public static boolean active() {
		return DEPTH.get()[0] > 0;
	}
}
