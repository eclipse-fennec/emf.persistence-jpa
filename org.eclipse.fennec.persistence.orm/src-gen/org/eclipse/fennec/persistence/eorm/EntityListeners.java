/*
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
 */
package org.eclipse.fennec.persistence.eorm;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Entity Listeners</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({TYPE}) @Retention(RUNTIME)
 *         public @interface EntityListeners {
 *           Class[] value();
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityListeners#getEntityListener <em>Entity Listener</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityListeners()
 * @model extendedMetaData="name='entity-listeners' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface EntityListeners extends EObject {
	/**
	 * Returns the value of the '<em><b>Entity Listener</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.EntityListener}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entity Listener</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityListeners_EntityListener()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='entity-listener' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<EntityListener> getEntityListener();

} // EntityListeners
