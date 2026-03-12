/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.eclipselink.copying;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.persistence.eclipselink.descriptors.EClassDescriptor;
import org.eclipse.fennec.persistence.eclipselink.descriptors.EInstantiationPolicy;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.exception.EDescriptorException;
import org.eclipse.persistence.descriptors.copying.AbstractCopyPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.Session;

/**
 * Copy policy for {@link EObject}. It can be used to create clones. We use the
 * EMF way to clone objects This is used when creating an {@link EDynamicType}
 * 
 * @author Mark Hoffmann
 * @since 12.01.2025
 */
public class ECopyPolicy extends AbstractCopyPolicy {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance.
	 */
	public ECopyPolicy() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.persistence.descriptors.copying.CopyPolicy#buildsNewInstance()
	 */
	@Override
	public boolean buildsNewInstance() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.persistence.descriptors.copying.AbstractCopyPolicy#buildClone(
	 * java.lang.Object, org.eclipse.persistence.sessions.Session)
	 */
	@Override
	public Object buildClone(Object domainObject, Session session) throws DescriptorException {
		if (!(getDescriptor().getInstantiationPolicy() instanceof EInstantiationPolicy)) {
			throw EDescriptorException.noEInstantiationPolicySet(domainObject, getDescriptor());
		}
		EObject eo = null;
		if (nonNull(domainObject) && 
				domainObject instanceof EObject deo) {
			/*
			 * If we have a UnitOfWorkImpl as session, there is a cloneMap, that already
			 * contains empty instances for all our domain objects. We have to use them,
			 * instead of creating an own instance. So we copy into them, without
			 * containment mappings, because the references clones are provided via
			 * EReferenceAccessor#set
			 */
			if (session instanceof UnitOfWorkImpl uow) {
				Object clone = uow.getCloneMapping().get(domainObject);
				Object originalClone = uow.getCloneToOriginals().get(domainObject);
				if (!uow.getCloneToOriginals().containsKey(domainObject) && clone instanceof EObject cloneEO
						&& cloneEO.eClass().equals(deo.eClass())) {
					if (!domainObject.equals(cloneEO)) {
						eo = cloneEO;
					}
				}
				if (isNull(eo) && 
						nonNull(originalClone) && 
						!domainObject.equals(originalClone) && 
						originalClone instanceof EObject oceo &&
						!originalClone.equals(clone)) {
					eo = oceo;
				}
				if (isNull(eo)) {
					eo = (EObject) getDescriptor().getInstantiationPolicy().buildNewInstance();
				}
			}
			return eo;
		}
		return null;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.descriptors.copying.AbstractCopyPolicy#buildWorkingCopyClone(java.lang.Object, org.eclipse.persistence.sessions.Session)
	 */
	@Override
	public Object buildWorkingCopyClone(Object domainObject, Session session) throws DescriptorException {
		if (session instanceof UnitOfWorkImpl uow && 
				!uow.getCloneMapping().containsKey(domainObject)) {
			return getDescriptor().getInstantiationPolicy().buildNewInstance();
		}
		return super.buildWorkingCopyClone(domainObject, session);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.descriptors.copying.AbstractCopyPolicy#getDescriptor()
	 */
	@Override
	protected EClassDescriptor getDescriptor() {
		return (EClassDescriptor) super.getDescriptor();
	}

}
