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
package org.eclipse.fennec.model.expression;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Property Path</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Feature navigation from the context object (or from an iterator variable when base is set), root feature first. Intermediate segments navigate references; the last segment addresses the compared feature.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.PropertyPath#getSegments <em>Segments</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.PropertyPath#getBase <em>Base</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.PropertyPath#getCastBase <em>Cast Base</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getPropertyPath()
 * @model
 * @generated
 */
@ProviderType
public interface PropertyPath extends Expression {
	/**
	 * Returns the value of the '<em><b>Segments</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EStructuralFeature}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Segments</em>' reference list.
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getPropertyPath_Segments()
	 * @model required="true"
	 * @generated
	 */
	EList<EStructuralFeature> getSegments();

	/**
	 * Returns the value of the '<em><b>Base</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Optional iterator variable this path starts from; unset = the query root.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Base</em>' reference.
	 * @see #setBase(Variable)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getPropertyPath_Base()
	 * @model
	 * @generated
	 */
	Variable getBase();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.PropertyPath#getBase <em>Base</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Base</em>' reference.
	 * @see #getBase()
	 * @generated
	 */
	void setBase(Variable value);

	/**
	 * Returns the value of the '<em><b>Cast Base</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Optional downcast of the navigation origin to a subtype before the first segment (issue #80, the v1 cut matching OData's Ns.SubType/prop limit — full cast-capable segments stay additive). JPA renders TREAT(e AS Sub); on objects that are not instances of the subtype the path yields null (three-valued exclusion, verified EclipseLink behaviour inside OR).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Cast Base</em>' reference.
	 * @see #setCastBase(EClass)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getPropertyPath_CastBase()
	 * @model
	 * @generated
	 */
	EClass getCastBase();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.PropertyPath#getCastBase <em>Cast Base</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Cast Base</em>' reference.
	 * @see #getCastBase()
	 * @generated
	 */
	void setCastBase(EClass value);

} // PropertyPath
