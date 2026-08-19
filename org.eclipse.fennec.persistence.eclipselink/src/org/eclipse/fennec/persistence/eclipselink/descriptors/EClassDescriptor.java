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

import java.lang.reflect.Modifier;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.fennec.persistence.eclipselink.classloader.OSGiDynamicClassloader;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopyPolicy;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EMapEntryEObject;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.helper.EMaps;
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
		if (nonNull(eClass.getInstanceClass()) && canBeAnEntityClass(eClass.getInstanceClass())) {
			javaClass = eClass.getInstanceClass();
		} else {
			String fqClassName = EORMHelper.getEClassName(entity);
			// A map entry needs BasicEMap.Entry — an EcoreEMap stores its entries in an array
			// of that type, and EMF's own entry class is final and shared (issue #183/#185).
			Class<?> parent = EMaps.isMapEntry(eClass) ? EMapEntryEObject.class : DynamicEObjectImpl.class;
			if (classLoader instanceof OSGiDynamicClassloader dcl) {
				javaClass = dcl.createDynamicClass(fqClassName, parent);
			} else if (classLoader instanceof DynamicClassLoader dcl) {
				javaClass = dcl.createDynamicClass(fqClassName, parent);
			}
		}
		if (nonNull(javaClass)) {
			setJavaClass(javaClass);
		} else {
			super.convertClassNamesToClasses(classLoader);
		}
	}

	/**
	 * Whether an EClass's instance class can serve as the entity class (issue #183).
	 * <p>
	 * A generated POJO can; the one instance class Ecore itself puts on a user's model cannot.
	 * A map entry class carries {@code instanceClassName = "java.util.Map$Entry"} — and has to,
	 * because {@code EStructuralFeatureImpl} picks the {@code EMAP} setting delegate on
	 * {@code dataClass == Map.Entry.class} and without it the feature is a plain list rather
	 * than an {@code EMap}. Taking that name literally produced two failures at once: an
	 * interface has no constructor to instantiate ({@code EclipseLink-63}), and every entry
	 * class in the unit collapsed onto the same Java class, so EclipseLink resolved the wrong
	 * descriptor for a table ({@code EclipseLink-93}).
	 * <p>
	 * The test is general rather than a check for that one name: an interface, an abstract
	 * class or one without an accessible no-arg constructor cannot be instantiated on load, so
	 * a dynamic class is generated instead. {@code Map.Entry} is simply the first instance
	 * class that is <em>required</em> to hit it.
	 */
	private static boolean canBeAnEntityClass(Class<?> instanceClass) {
		if (instanceClass.isInterface() || Modifier.isAbstract(instanceClass.getModifiers())) {
			return false;
		}
		try {
			instanceClass.getDeclaredConstructor();
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

}
