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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Modifier;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
		Class<?> javaClass = entityClassOf(eClass, EORMHelper.getEClassName(entity), classLoader);
		if (nonNull(javaClass)) {
			setJavaClass(javaClass);
		} else {
			super.convertClassNamesToClasses(classLoader);
		}
		// the alias is the EClass name — the entity name in JPQL and the key every lookup by
		// type uses — and no longer the class's simple name, which is WpOwnerImpl for a
		// generated implementation (issue #311)
		setAlias(eClass.getName());
	}

	/**
	 * The entity class of an EClass (issue #311).
	 * <ul>
	 * <li>A generated model's class is read back as what its own factory creates: for a
	 * concrete class the generated implementation behind the interface, for an abstract one
	 * the implementation the generator emits beside it ({@code <package>.impl.<Name>Impl}),
	 * failing that the closest common superclass of its concrete subtypes' implementations.
	 * A generated class is never mapped onto a dynamic class — that is what put two classes
	 * of one name into the runtime and made every cast to the generated interface fail.</li>
	 * <li>A dynamic EClass gets a dynamic class. EMF instantiates a dynamic subtype of a
	 * generated class as that class's implementation, carrying the dynamic EClass
	 * ({@code EFactoryImpl.create}); its entity class extends the implementation, so the
	 * two types stay apart for a store that keys its descriptors by Java class.</li>
	 * <li>A map entry needs {@link EMapEntryEObject}, an instance class that is a plain,
	 * instantiable class serves as it is (issue #183).</li>
	 * </ul>
	 *
	 * @return the class, or {@code null} when the class loader cannot define a dynamic one
	 */
	static Class<?> entityClassOf(EClass eClass, String dynamicClassName, ClassLoader classLoader) {
		if (EMaps.isMapEntry(eClass)) {
			// an EcoreEMap stores its entries in an array of BasicEMap.Entry, and EMF's own
			// entry class is final and shared (issue #183/#185)
			return dynamicClass(dynamicClassName, EMapEntryEObject.class, classLoader);
		}
		Class<?> instanceClass = eClass.getInstanceClass();
		if (nonNull(instanceClass)) {
			if (canBeAnEntityClass(instanceClass)) {
				return instanceClass;
			}
			Class<?> generated = generatedImplementationOf(eClass);
			if (isNull(generated)) {
				throw new IllegalStateException("No implementation class found for the generated EClass "
						+ eClass.getName() + " (" + instanceClass.getName() + "): neither its factory nor "
						+ instanceClass.getPackageName() + ".impl." + instanceClass.getSimpleName()
						+ "Impl nor its concrete subtypes provide one");
			}
			return generated;
		}
		Class<?> parent = DynamicEObjectImpl.class;
		for (EClass superType : eClass.getESuperTypes()) {
			if (nonNull(superType.getInstanceClass())) {
				Class<?> generated = generatedImplementationOf(superType);
				if (nonNull(generated)) {
					parent = generated;
				}
				break;
			}
		}
		return dynamicClass(dynamicClassName, parent, classLoader);
	}

	private static Class<?> dynamicClass(String className, Class<?> parent, ClassLoader classLoader) {
		return classLoader instanceof DynamicClassLoader dcl ? dcl.createDynamicClass(className, parent) : null;
	}

	/** The generated implementation of a generated EClass, or {@code null} when none is reachable. */
	private static Class<?> generatedImplementationOf(EClass eClass) {
		Class<?> instanceClass = eClass.getInstanceClass();
		if (!eClass.isAbstract()) {
			EObject created = EcoreUtil.create(eClass);
			return !(created instanceof DynamicEObjectImpl) && instanceClass.isInstance(created)
					? created.getClass()
					: null;
		}
		String implName = instanceClass.getPackageName() + ".impl." + instanceClass.getSimpleName() + "Impl";
		try {
			Class<?> impl = Class.forName(implName, false, instanceClass.getClassLoader());
			if (!impl.isInterface() && instanceClass.isAssignableFrom(impl)) {
				return impl;
			}
		} catch (ClassNotFoundException | LinkageError differentlyGenerated) {
			// not generated with the default naming: derived from the subtypes below
		}
		return commonImplementationOfSubtypes(eClass);
	}

	/** The closest common superclass of the concrete subtypes' implementations that implements the interface. */
	private static Class<?> commonImplementationOfSubtypes(EClass eClass) {
		Class<?> common = null;
		for (EClassifier classifier : eClass.getEPackage().getEClassifiers()) {
			if (!(classifier instanceof EClass candidate) || candidate.isAbstract() || !eClass.isSuperTypeOf(candidate)
					|| isNull(candidate.getInstanceClass())) {
				continue;
			}
			Class<?> impl = generatedImplementationOf(candidate);
			if (isNull(impl)) {
				continue;
			}
			if (isNull(common)) {
				common = impl;
			}
			while (nonNull(common) && !common.isAssignableFrom(impl)) {
				common = common.getSuperclass();
			}
		}
		return nonNull(common) && eClass.getInstanceClass().isAssignableFrom(common) ? common : null;
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
