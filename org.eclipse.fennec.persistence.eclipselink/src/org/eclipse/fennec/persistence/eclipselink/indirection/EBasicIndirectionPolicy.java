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
package org.eclipse.fennec.persistence.eclipselink.indirection;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopier;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * 
 * @author mark
 * @since 12.01.2025
 */
public class EBasicIndirectionPolicy extends BasicIndirectionPolicy {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private final EReference reference;
	private final EDynamicType type;
	private final boolean indirection;

	/**
	 * Creates a new instance.
	 */
	public EBasicIndirectionPolicy(DatabaseMapping mapping, EReference reference, EDynamicType type) {
		requireNonNull(mapping);
		requireNonNull(reference);
		this.type = type;
		this.reference = reference;
		this.indirection = !MappingHelper.isContainmentReference(reference);
		setMapping(mapping);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#buildIndirectObject(org.eclipse.persistence.indirection.ValueHolderInterface)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object buildIndirectObject(ValueHolderInterface valueHolder) {
		if (isNull(valueHolder)) {
			return null;
		}
		if (nonNull(valueHolder.getValue()) && 
				valueHolder.getValue() instanceof EObject eValue && 
				!eValue.eIsProxy()) {
			// We leave the data as they are, but give the EObject an EProxy
			URI baseURI = type.getBaseURI();
			String id = EcoreUtil.getID(eValue);
			EAttribute idAttribute = eValue.eClass().getEIDAttribute();
			if (nonNull(id) && 
					nonNull(idAttribute)) {
				baseURI = baseURI.appendFragment("//" + reference.getName() + "/" + idAttribute.getName() + "/" + id);
			}
			((InternalEObject)eValue).eSetProxyURI(baseURI);
		}
		return valueHolder;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#backupCloneAttribute(java.lang.Object, java.lang.Object, java.lang.Object, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl)
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public Object backupCloneAttribute(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
		requireNonNull(reference);
		Object attrObject = attributeValue instanceof ValueHolderInterface<?> avh ? avh.getValue() : attributeValue;
		if (attrObject instanceof EObject attrEO && 
				clone instanceof EObject cloneEO && 
				backup instanceof EObject backupEO && 
				MappingHelper.areInContainmentRelation(cloneEO, attrEO, reference)) {
			EObject backupAttr = (EObject) getForeignReferenceMapping().getReferenceDescriptor().getCopyPolicy().buildClone(attrObject, unitOfWork);
			new ECopier(backupAttr, null).copy(attrEO);
			if (usesIndirection()) {
				if (attributeValue instanceof ValueHolderInterface avh) {
					avh.setValue(backupAttr);
				} else {
					return new ValueHolder<>(backupAttr);
				}
			} else {
				return backupAttr;
			}
		}
		if (!usesIndirection()) {
			return mapping.buildBackupCloneForPartObject(attributeValue, clone, backup, unitOfWork);
		} else {
			return super.backupCloneAttribute(attributeValue, clone, backup, unitOfWork);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#validateAttributeOfInstantiatedObject(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object validateAttributeOfInstantiatedObject(Object attributeValue) {
		if (!usesIndirection()) {
			return attributeValue;
		}
		if (attributeValue instanceof EObject eo) {
			return new ValueHolder<EObject>(eo);
		}
		if (attributeValue instanceof Collection attrCollection) {
			List<?> valueList = new LinkedList<>();
			valueList.addAll(attrCollection);
			return new ValueHolder<>(valueList);
		}
		return super.validateAttributeOfInstantiatedObject(attributeValue);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#objectIsInstantiated(java.lang.Object)
	 */
	@Override
	public boolean objectIsInstantiated(Object object) {
		if (!usesIndirection()) {
			return true;
		}
		return super.objectIsInstantiated(object);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.IndirectionPolicy#usesTransparentIndirection()
	 */
	@Override
	public boolean usesTransparentIndirection() {
		return true;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.IndirectionPolicy#usesIndirection()
	 */
	@Override
	public boolean usesIndirection() {
		return indirection;
		//		return super.usesIndirection();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#reset(java.lang.Object)
	 */
	@Override
	public void reset(Object target) {
		if (!usesIndirection()) {
			return;
		}
		super.reset(target);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#setRealAttributeValueInObject(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setRealAttributeValueInObject(Object target, Object attributeValue) {
		if (!usesIndirection()) {
			if (!MappingHelper.areInContainmentRelation((EObject)target, (EObject)attributeValue, reference)) {
				mapping.setAttributeValueInObject(target, attributeValue);
			}
		} else {
			Object attrValue = this.mapping.getAttributeValueFromObject(target);
			if (attributeValue instanceof ValueHolderInterface vh) {
				vh.setValue(attributeValue);
			} else {
				attrValue = new ValueHolder<>(attributeValue);
			}
			mapping.setAttributeValueInObject(target, attrValue);
		} 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#cloneAttribute(java.lang.Object, java.lang.Object, org.eclipse.persistence.internal.identitymaps.CacheKey, java.lang.Object, java.lang.Integer, org.eclipse.persistence.internal.sessions.AbstractSession, boolean)
	 */
	@Override
	public Object cloneAttribute(Object attributeValue, Object original, CacheKey cacheKey, Object clone,
			Integer refreshCascade, AbstractSession cloningSession, boolean buildDirectlyFromRow) {
		if (!usesIndirection()) {
			// Since valueFromRow was called with the UnitOfWork, attributeValue
	        // is already a registered result.
	        if (buildDirectlyFromRow) {
	            return attributeValue;
	        }
	        if (!cloningSession.isUnitOfWork()){
	            return mapping.buildContainerClone(attributeValue, cloningSession);
	        }
	        boolean isExisting = !cloningSession.isUnitOfWork() || (((UnitOfWorkImpl) cloningSession).isObjectRegistered(clone) && (!(((UnitOfWorkImpl)cloningSession).isOriginalNewObject(original))));
	        return this.getMapping().buildCloneForPartObject(attributeValue, original, cacheKey, clone, cloningSession, refreshCascade, isExisting, isExisting);// only assume from shared cache if it is existing
		}
		return super.cloneAttribute(attributeValue, original, cacheKey, clone, refreshCascade, cloningSession,
				buildDirectlyFromRow);
	}

}
