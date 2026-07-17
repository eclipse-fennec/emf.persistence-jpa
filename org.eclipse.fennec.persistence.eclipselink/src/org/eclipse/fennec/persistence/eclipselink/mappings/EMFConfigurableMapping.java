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

import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeContext;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * Implemented by the EMF-aware relationship mappings. Owning the EMF configuration
 * (value accessor, indirection policy, laziness) keeps that concern out of the
 * type-building code — a builder only wires the relational structure and then calls
 * {@link #configureEMF(EReference, BaseRef, EDynamicType, EDynamicTypeContext)}.
 *
 * @author Mark Hoffmann
 * @since 15.07.2026
 */
public interface EMFConfigurableMapping {

	/**
	 * Applies the EMF semantics for the given reference to this mapping: the
	 * {@link EReferenceAccessor}, the reference-kind-specific indirection policy and
	 * the shared mapping defaults.
	 *
	 * @param reference the EReference this mapping represents
	 * @param baseRef the eorm reference carrying {@code fetch}/{@code batch}; may be {@code null}
	 * @param type the owning dynamic type
	 * @param context the dynamic type context
	 */
	default void configureEMF(EReference reference, BaseRef baseRef, EDynamicType type,
			EDynamicTypeContext context) {
		EMappingSupport.configureEMF((ForeignReferenceMapping) this, reference, baseRef, type, context);
	}
}
