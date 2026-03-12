/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import static java.util.Objects.nonNull;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.persistence.eclipselink.descriptors.EClassDescriptor;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.orm.helper.EORMHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;

/**
 * EMF Implementation of a {@link DynamicType}
 * @author mark
 * @since 16.12.2024
 */
class EDynamicTypeImpl extends DynamicTypeImpl implements EDynamicType {
	
	private final Entity entity;
	private final EClass eClass;
	private final EDynamicTypeContext context;
	private URI baseURI;
	
	/**
	 * Creates a new instance.
	 */
	EDynamicTypeImpl(EClassDescriptor descriptor, EDynamicTypeContext context) {
		this(descriptor, null, context);
	}
	
	/**
	 * Creates a new instance.
	 */
	EDynamicTypeImpl(EClassDescriptor descriptor, DynamicType parent, EDynamicTypeContext context) {
		super(descriptor, parent);
		this.context = context;
		this.entity = descriptor.getEntity();
		this.eClass = EORMHelper.getEClass(entity);
		initBaseUri(context.getBaseURI());
	}
	
	/**
	 * Initialized the base URI
	 * @param baseUri the typed base URI
	 */
	private void initBaseUri(URI baseUri) {
		if (nonNull(baseUri)) {
			this.baseURI = baseUri.appendSegment(getName());
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType#getEClass()
	 */
	public EClass getEClass() {
		return eClass;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType#getEntity()
	 */
	public Entity getEntity() {
		return entity;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType#getBaseURI()
	 */
	@Override
	public URI getBaseURI() {
		return baseURI;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType#getContext()
	 */
	@Override
	public EDynamicTypeContext getContext() {
		return context;
	}
}
