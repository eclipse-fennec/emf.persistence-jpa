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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;

/**
 * The superclass of every generated entity class for a map entry (issue #183/#185) — the
 * subclassable equivalent of {@code DynamicEObjectImpl.BasicEMapEntry}, which EMF declares
 * {@code final}.
 * <p>
 * Two constraints meet here and only this class satisfies both:
 * <ul>
 * <li>An {@code EcoreEMap} stores its entries in an array of {@link BasicEMap.Entry}, so an
 * object that does not implement that interface cannot go into a map — the symptom is an
 * {@code ArrayStoreException} while copying, far away from the cause.</li>
 * <li>Every entry EClass needs its <em>own</em> Java class, or two entry types in one
 * persistence unit collapse onto one descriptor (#183). EMF's own entry class is one class for
 * all of them and final on top.</li>
 * </ul>
 * So the dynamic class generated per entry EClass extends this, and EMF's map machinery accepts
 * the result. The key/value features are resolved in {@link #eSetClass(EClass)} exactly as EMF
 * does it, because that is the only hook that runs for a dynamically created instance.
 *
 * @author Mark Hoffmann
 * @since 19.08.2026
 */
public class EMapEntryEObject extends DynamicEObjectImpl implements BasicEMap.Entry<Object, Object> {

	private int hash = -1;
	private EStructuralFeature keyFeature;
	private EStructuralFeature valueFeature;

	/** Creates an entry without a class — EclipseLink instantiates through the no-arg form. */
	public EMapEntryEObject() {
		super();
	}

	/**
	 * Creates an entry of the given class.
	 *
	 * @param eClass the entry class
	 */
	public EMapEntryEObject(EClass eClass) {
		super(eClass);
	}

	@Override
	public Object getKey() {
		return eGet(keyFeature);
	}

	@Override
	public void setKey(Object key) {
		eSet(keyFeature, key);
	}

	@Override
	public int getHash() {
		if (hash == -1) {
			Object key = getKey();
			hash = key == null ? 0 : key.hashCode();
		}
		return hash;
	}

	@Override
	public void setHash(int hash) {
		this.hash = hash;
	}

	@Override
	public Object getValue() {
		return eGet(valueFeature);
	}

	@Override
	public Object setValue(Object value) {
		Object previous = eGet(valueFeature);
		eSet(valueFeature, value);
		return previous;
	}

	@Override
	public void eSetClass(EClass eClass) {
		super.eSetClass(eClass);
		keyFeature = eClass.getEStructuralFeature("key");
		valueFeature = eClass.getEStructuralFeature("value");
	}
}
