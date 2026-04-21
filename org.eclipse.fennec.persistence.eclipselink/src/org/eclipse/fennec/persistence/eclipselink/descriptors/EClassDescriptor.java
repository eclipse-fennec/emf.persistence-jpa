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
package org.eclipse.fennec.persistence.eclipselink.descriptors;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.fennec.persistence.eclipselink.classloader.OSGiDynamicClassloader;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopyPolicy;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.orm.helper.EORMHelper;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;

/**
 * EMF {@link RelationalDescriptor}. Here we customize e.g. EMF proxy handling
 * @author Mark Hoffmann
 * @since 12.01.2025
 */
public class EClassDescriptor extends RelationalDescriptor {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private final Entity entity;
	
	/**
	 * Creates a new instance.
	 */
	public EClassDescriptor(Entity entity) {
		super();
		this.entity = entity;
		setCopyPolicy(new ECopyPolicy());
		setInstantiationPolicy(new EInstantiationPolicy());
		setObjectBuilder(new EObjectBuilder(this));
	}
	
	/**
	 * Returns the entity.
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.descriptors.ClassDescriptor#convertClassNamesToClasses(java.lang.ClassLoader)
	 */
	@Override
	public void convertClassNamesToClasses(ClassLoader classLoader) {
		EClass eClass = EORMHelper.getEClass(entity);
		requireNonNull(eClass);
		Class<?> javaClass = null;
		if (nonNull(eClass.getInstanceClass())) {
			javaClass = eClass.getInstanceClass();
		} else {
			String fqClassName = EORMHelper.getEClassName(entity);
			if (classLoader instanceof OSGiDynamicClassloader dcl) {
				javaClass = dcl.createDynamicClass(fqClassName, DynamicEObjectImpl.class);
			} else if (classLoader instanceof DynamicClassLoader dcl) {
				javaClass = dcl.createDynamicClass(fqClassName, DynamicEObjectImpl.class);
			}
		}
		if (nonNull(javaClass)) {
			setJavaClass(javaClass);
		} else {
			super.convertClassNamesToClasses(classLoader);
		}
	}
	
}
