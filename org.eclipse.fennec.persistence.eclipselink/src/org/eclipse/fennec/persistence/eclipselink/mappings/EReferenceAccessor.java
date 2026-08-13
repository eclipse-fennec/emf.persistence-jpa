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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.eclipse.fennec.persistence.orm.helper.MappingHelper.isContainmentChild;
import static org.eclipse.fennec.persistence.orm.helper.MappingHelper.isContainmentParentChild;
import static org.eclipse.fennec.persistence.orm.helper.MappingHelper.isNonContainmentOppositeRelation;
import static org.eclipse.fennec.persistence.orm.helper.MappingHelper.setValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeContext;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.dynamic.ValuesAccessor;
import org.eclipse.persistence.internal.indirection.UnitOfWorkValueHolder;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Handles getting and setting data from and to an {@link EObject} for a given reference.
 * 
 * We need a special handling when dealing with the clones of Eclipselink in bi-directional relationships
 * Usually EMF handles that, but with the clones we need consistent parent-chiöld relations in EMF.
 * 
 * The setting if a child is set before parent or vice versa is not predictable. Sometimes Eclipselink
 * starts setting parent first with the child as value, but sometime the object is the child and the 
 * value the parent. So we need to handle both cases
 * @author Mark Hoffmann
 * @since 09.12.2024
 */
public class EReferenceAccessor extends ValuesAccessor {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private final EReference reference;

	/**
	 * @param mapping the mapping the accessor will be set on — required because the accessor
	 *                extends {@link ValuesAccessor}, EclipseLink's dynamic-entity marker (the
	 *                JPA metamodel initialization branches on it; value access itself is fully
	 *                overridden with EMF semantics)
	 */
	public static AttributeAccessor create(DatabaseMapping mapping, EReference feature,
			EDynamicTypeContext cache) {
		return new EReferenceAccessor(mapping, feature, cache);
	}

	private EReferenceAccessor(DatabaseMapping mapping, EReference feature, EDynamicTypeContext cache) {
		super(mapping);
		this.reference = feature;
	}

	/** Preserves the pre-{@link ValuesAccessor} behavior ({@code Object.class}). */
	@Override
	public Class<?> getAttributeClass() {
		return Object.class;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.mappings.AttributeAccessor#getAttributeValueFromObject(java.lang.Object)
	 */
	@Override
	public Object getAttributeValueFromObject(Object object) throws DescriptorException {
		if (object instanceof EObject eo && nonNull(reference)) {
			return eo.eGet(reference);
		}
		return object;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.mappings.AttributeAccessor#setAttributeValueInObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
		if (object instanceof EObject eObject &&
				nonNull(reference)) {
			value = unwrapValueHolder(value);
			//			value = calculateReferenceValue(eObject, value);
			if (value instanceof Collection<?> collection &&
					reference.isMany() &&
					AuthoritativeFill.active() &&
					isMaterialized(collection)) {
				/*
				 * Row fill (build or refresh, issue #144): the incoming collection is the
				 * store's complete truth for this reference, so this is the one write that
				 * may also SHRINK the list. Everything below accumulates only — correct for
				 * merge/backup/indirection bookkeeping, but a refresh routed through it could
				 * never drop a member, and EclipseLink refreshes into the SAME cached
				 * instance, so children deleted in the database were resurrected on read.
				 */
				reconcileById(eObject, collection);
				return;
			}
			if (nonNull(value) &&
					value instanceof Collection<?> collection &&
					reference.isMany() &&
					!reference.isContainment()) {
				/*
				 * Collection writes accumulate here (EclipseLink writes the value back on
				 * many read paths — indirection normalisation, merge, backup). EMF's
				 * unique-add only filters identical instances, so an element arriving as a
				 * different instance of an already-present entity (AP-47 proxy lists are
				 * rebuilt per clone) must be filtered by EMF id — mirroring the relation
				 * table's primary key, which cannot hold the pair twice either.
				 */
				addCollectionValueById(eObject, collection);
				return;
			}
			if (nonNull(value) &&
					value instanceof EObject eValue &&
					isContainmentChild(reference)) {
				/*
				 * If we are on the child side, our eValue is the parent
				 * and the eObject is the child
				 */
				handleBidirectionalContainmentChild(eValue, eObject);
			} else if (isContainmentParentChild(eObject, value)) {
				/*
				 * The parent-child relation is correct
				 */
				return;
			} else if (isNonContainmentOppositeRelation(reference)) {
				/*
				 * If we are in a non-containment bi-directional mapping
				 */
				handleBidirectionalNonContainment(eObject, value);
			} else {
				setValue(eObject, value, reference);
			}
		}
	}

	/**
	 * An uninstantiated indirect container must not be reconciled — iterating it would
	 * trigger the very query the indirection defers. It cannot be authoritative content
	 * anyway, since nothing has been read for it yet.
	 */
	private static boolean isMaterialized(Collection<?> collection) {
		return !(collection instanceof IndirectContainer<?> container) || container.isInstantiated();
	}

	/**
	 * Brings the list in line with the incoming row content: members whose EMF id (or
	 * instance) is absent from the incoming collection are stale and removed; incoming
	 * elements not yet present are added. A member matched by id stays as the instance the
	 * list already holds — consumers keep their object identity, and the member's own
	 * attribute state is the concern of its own cache entry, not of this collection.
	 * <p>
	 * Removal and addition mirror the two fill styles below: containment members leave and
	 * join with full EMF semantics (their {@code eContainer} must follow), non-containment
	 * members via {@code basicRemove}/{@code basicAdd} for the reasons documented on
	 * {@link #addCollectionValueById}.
	 */
	private void reconcileById(EObject eObject, Collection<?> incoming) {
		@SuppressWarnings("unchecked")
		InternalEList<Object> current = (InternalEList<Object>) eObject.eGet(reference);
		if (current == incoming) {
			return;
		}
		Set<String> incomingIds = new HashSet<>();
		Set<Object> incomingInstances = Collections.newSetFromMap(new IdentityHashMap<>());
		for (Object element : incoming) {
			incomingInstances.add(element);
			if (element instanceof EObject eo) {
				String id = EcoreUtil.getID(eo);
				if (nonNull(id)) {
					incomingIds.add(id);
				}
			}
		}
		for (Object member : new ArrayList<>(current.basicList())) {
			if (incomingInstances.contains(member)) {
				continue;
			}
			String id = member instanceof EObject eo ? EcoreUtil.getID(eo) : null;
			if (nonNull(id) && incomingIds.contains(id)) {
				continue; // same row, arriving as a rebuilt twin — keep our instance
			}
			if (reference.isContainment()) {
				current.remove(member);
			} else {
				current.basicRemove(member, null);
			}
		}
		if (reference.isContainment()) {
			for (Object element : incoming) {
				if (element instanceof EObject eo) {
					String id = EcoreUtil.getID(eo);
					if (nonNull(id) && containsId(current.basicList(), eo, id)) {
						continue;
					}
				}
				if (!current.contains(element)) {
					current.add(element);
				}
			}
		} else {
			addCollectionValueById(eObject, incoming);
		}
	}

	/**
	 * Adds the incoming elements to the many-valued reference, skipping elements whose
	 * EMF id is already present in the list; elements without a usable id are added
	 * as-is (duplicate instances are filtered by EMF's unique-add).
	 * <p>
	 * Elements are added via {@link InternalEList#basicAdd} — without EMF opposite
	 * maintenance. This is a framework-internal fill: the other side of a bidirectional
	 * reference loads its own state from its own row, and inverse handling on shared
	 * lightweight proxies would steal elements between clone, backup and cache copies
	 * (a single-valued opposite may only point to one owner). User-level modifications
	 * through the EMF API keep the full opposite semantics.
	 */
	private void addCollectionValueById(EObject eObject, Collection<?> newContent) {
		@SuppressWarnings("unchecked")
		InternalEList<Object> current = (InternalEList<Object>) eObject.eGet(reference);
		if (current == newContent) {
			return;
		}
		for (Object element : newContent) {
			if (element instanceof EObject eo) {
				String id = EcoreUtil.getID(eo);
				if (nonNull(id) && containsId(current.basicList(), eo, id)) {
					continue;
				}
				if (!current.basicList().contains(element)) {
					current.basicAdd(element, null);
				}
				continue;
			}
			current.add(element);
		}
	}

	/** Non-resolving containment check by EMF id, ignoring the incoming instance itself. */
	private static boolean containsId(List<?> list, Object incoming, String id) {
		for (Object element : list) {
			if (element != incoming && element instanceof EObject eo && id.equals(EcoreUtil.getID(eo))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Extracts data from {@link ValueHolder}, if we use indirection
	 * @param value the object to unwrap
	 * @return the unwrapped object
	 */
	private Object unwrapValueHolder(Object value) {
		if (value instanceof UnitOfWorkValueHolder<?> uowvh) {
			return isNull(uowvh.getWrappedValueHolder()) ? null : uowvh.getValue();
		}
		if (value instanceof ValueHolderInterface<?> vh) {
			return vh.getValue();
		}
		return value;
	}

	/**
	 * Returns the reference.
	 * @return the reference
	 */
	public EReference getReference() {
		return reference;
	}

	/**
	 * Handle setting of values if we are a containment child with opposites. 
	 * In EMF we have to set the value to the parent. Then the child container is set correctly.
	 * If you set the parent to the child, this will not happen. So we need to check, if we 
	 * already have parents or children set and need to create consisten clone setups 
	 * @param parent the parent object
	 * @param child the child to be set
	 */
	private void handleBidirectionalContainmentChild(EObject parent, EObject child) {
		if (isNull(parent) || nonNull(child) && 
				parent.equals(child.eContainer())) {
			return;
		}
		EReference oppositeRef = reference.getEOpposite();
		setValue(parent, child, oppositeRef);
	}

	/**
	 * Handle setting of values if we are a containment child with opposites. 
	 * In EMF we have to set the value to the parent. Then the child container is set correctly.
	 * If you set the parent to the child, this will not happen. So we need to check, if we 
	 * already have parents or children set and need to create consistent clone setups.
	 * The child might be a {@link List} for many relations 
	 * @param object the parent object
	 * @param value the child to be set
	 */
	private void handleBidirectionalNonContainment(EObject object, Object value) {
		// we cannot set something without parent
		if (isNull(object)) {
			return;
		}
		EReference oppositeRef = reference.getEOpposite();
		List<EObject> eValues = MappingHelper.unwrapEObject(value);
		/*
		 * Check, if the value and objects already belong together
		 */
		if (!eValues.isEmpty() && 
				MappingHelper.refValueEqualsAll(eValues, object, reference) && 
				MappingHelper.refValueEqualsAll(object, value, oppositeRef)) {
			return;
		}
		setValue(object, value, reference);
		//		eValues.forEach(v->setValue(v, object, oppositeRef));
	}

}
