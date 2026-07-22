/*
 * Copyright (c) 2012 - 2026 Data In Motion and others.
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
package org.eclipse.fennec.model.query;

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.model.query.QueryPackage
 * @generated
 */
@ProviderType
public interface QueryFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	QueryFactory eINSTANCE = org.eclipse.fennec.model.query.impl.QueryFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Query</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Query</em>'.
	 * @generated
	 */
	Query createQuery();

	/**
	 * Returns a new object of class '<em>QObject</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>QObject</em>'.
	 * @generated
	 */
	QObject createQObject();

	/**
	 * Returns a new object of class '<em>QSubject</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>QSubject</em>'.
	 * @generated
	 */
	QSubject createQSubject();

	/**
	 * Returns a new object of class '<em>Or</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Or</em>'.
	 * @generated
	 */
	Or createOr();

	/**
	 * Returns a new object of class '<em>Not</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Not</em>'.
	 * @generated
	 */
	Not createNot();

	/**
	 * Returns a new object of class '<em>And</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>And</em>'.
	 * @generated
	 */
	And createAnd();

	/**
	 * Returns a new object of class '<em>To Lower Case</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>To Lower Case</em>'.
	 * @generated
	 */
	ToLowerCase createToLowerCase();

	/**
	 * Returns a new object of class '<em>To Upper Case</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>To Upper Case</em>'.
	 * @generated
	 */
	ToUpperCase createToUpperCase();

	/**
	 * Returns a new object of class '<em>Average</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Average</em>'.
	 * @generated
	 */
	Average createAverage();

	/**
	 * Returns a new object of class '<em>Min</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Min</em>'.
	 * @generated
	 */
	Min createMin();

	/**
	 * Returns a new object of class '<em>Max</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Max</em>'.
	 * @generated
	 */
	Max createMax();

	/**
	 * Returns a new object of class '<em>Sum</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Sum</em>'.
	 * @generated
	 */
	Sum createSum();

	/**
	 * Returns a new object of class '<em>Count Operation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Count Operation</em>'.
	 * @generated
	 */
	CountOperation createCountOperation();

	/**
	 * Returns a new object of class '<em>Ends With</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Ends With</em>'.
	 * @generated
	 */
	EndsWith createEndsWith();

	/**
	 * Returns a new object of class '<em>Start With</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Start With</em>'.
	 * @generated
	 */
	StartWith createStartWith();

	/**
	 * Returns a new object of class '<em>Contains</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Contains</em>'.
	 * @generated
	 */
	Contains createContains();

	/**
	 * Returns a new object of class '<em>Like</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Like</em>'.
	 * @generated
	 */
	Like createLike();

	/**
	 * Returns a new object of class '<em>Is Before</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Is Before</em>'.
	 * @generated
	 */
	IsBefore createIsBefore();

	/**
	 * Returns a new object of class '<em>Is After</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Is After</em>'.
	 * @generated
	 */
	IsAfter createIsAfter();

	/**
	 * Returns a new object of class '<em>Is Before Or Equal</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Is Before Or Equal</em>'.
	 * @generated
	 */
	IsBeforeOrEqual createIsBeforeOrEqual();

	/**
	 * Returns a new object of class '<em>Is After Or Equal</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Is After Or Equal</em>'.
	 * @generated
	 */
	IsAfterOrEqual createIsAfterOrEqual();

	/**
	 * Returns a new object of class '<em>Is In Range</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Is In Range</em>'.
	 * @generated
	 */
	IsInRange createIsInRange();

	/**
	 * Returns a new object of class '<em>Lt</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Lt</em>'.
	 * @generated
	 */
	Lt createLt();

	/**
	 * Returns a new object of class '<em>Lte</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Lte</em>'.
	 * @generated
	 */
	Lte createLte();

	/**
	 * Returns a new object of class '<em>Gte</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Gte</em>'.
	 * @generated
	 */
	Gte createGte();

	/**
	 * Returns a new object of class '<em>Gt</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Gt</em>'.
	 * @generated
	 */
	Gt createGt();

	/**
	 * Returns a new object of class '<em>Eq</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Eq</em>'.
	 * @generated
	 */
	Eq createEq();

	/**
	 * Returns a new object of class '<em>Bool Comparator</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Bool Comparator</em>'.
	 * @generated
	 */
	BoolComparator createBoolComparator();

	/**
	 * Returns a new object of class '<em>Is Bool</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Is Bool</em>'.
	 * @generated
	 */
	IsBool createIsBool();

	/**
	 * Returns a new object of class '<em>Sort Entity</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Sort Entity</em>'.
	 * @generated
	 */
	SortEntity createSortEntity();

	/**
	 * Returns a new object of class '<em>Is Literal</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Is Literal</em>'.
	 * @generated
	 */
	IsLiteral createIsLiteral();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	QueryPackage getQueryPackage();

} //QueryFactory
