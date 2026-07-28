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
package org.eclipse.fennec.persistence.query.derived;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.model.expression.Expression;

/**
 * The prepared plan of one derived-reference annotation, compiled once per feature by
 * the {@link DerivedReferenceCompiler} (concept §5).
 *
 * @author Juergen Albert
 * @since 28.07.2026
 */
public sealed interface DerivedPlan {

	/**
	 * The derivation matches the v1 pushdown shape
	 * {@code self.<manyReference>->select(v | <predicate>)}: the select body is bridged
	 * into the expression IR, rooted at the target type.
	 *
	 * @param baseReference the many-valued reference the select iterates
	 * @param predicate the bridged select body — a prototype, copy before containment
	 */
	record Pushdown(EReference baseReference, Expression predicate) implements DerivedPlan {
	}

	/**
	 * The derivation is valid OCL but outside the pushdown shape — it evaluates through
	 * the standard in-memory OCL delegate (concept P2/P5).
	 *
	 * @param reason why the derivation cannot push down, for diagnostics
	 */
	record Memory(String reason) implements DerivedPlan {
	}
}
