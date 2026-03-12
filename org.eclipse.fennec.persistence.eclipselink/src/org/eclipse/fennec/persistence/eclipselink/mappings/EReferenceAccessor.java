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

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeContext;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.indirection.UnitOfWorkValueHolder;
import org.eclipse.persistence.mappings.AttributeAccessor;

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
public class EReferenceAccessor extends AttributeAccessor {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private final EReference reference;

	public static AttributeAccessor create(EReference feature, EDynamicTypeContext cache) {
		return new EReferenceAccessor(feature, cache);
	}

	private EReferenceAccessor(EReference feature, EDynamicTypeContext cache) {
		this.reference = feature;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.mappings.AttributeAccessor#getAttributeValueFromObject(java.lang.Object)
	 */
	@Override
	public Object getAttributeValueFromObject(Object object) throws DescriptorException {
		if (object instanceof EObject && nonNull(reference)) {
			EObject eo = (EObject) object;
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
		if (object instanceof EObject &&
				nonNull(reference)) {
			EObject eObject = (EObject) object;
			value = unwrapValueHolder(value);
			//			value = calculateReferenceValue(eObject, value);
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
