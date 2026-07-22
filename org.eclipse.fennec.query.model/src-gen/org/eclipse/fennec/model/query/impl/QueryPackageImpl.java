/**
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
package org.eclipse.fennec.model.query.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.model.query.And;
import org.eclipse.fennec.model.query.Average;
import org.eclipse.fennec.model.query.BoolComparator;
import org.eclipse.fennec.model.query.Chaining;
import org.eclipse.fennec.model.query.Comparator;
import org.eclipse.fennec.model.query.Contains;
import org.eclipse.fennec.model.query.DateComparator;
import org.eclipse.fennec.model.query.EndsWith;
import org.eclipse.fennec.model.query.EnumComparator;
import org.eclipse.fennec.model.query.Eq;
import org.eclipse.fennec.model.query.Gt;
import org.eclipse.fennec.model.query.Gte;
import org.eclipse.fennec.model.query.IsAfter;
import org.eclipse.fennec.model.query.IsAfterOrEqual;
import org.eclipse.fennec.model.query.IsBefore;
import org.eclipse.fennec.model.query.IsBeforeOrEqual;
import org.eclipse.fennec.model.query.IsBool;
import org.eclipse.fennec.model.query.IsInRange;
import org.eclipse.fennec.model.query.IsLiteral;
import org.eclipse.fennec.model.query.Like;
import org.eclipse.fennec.model.query.Lt;
import org.eclipse.fennec.model.query.Lte;
import org.eclipse.fennec.model.query.Not;
import org.eclipse.fennec.model.query.NumberComparator;
import org.eclipse.fennec.model.query.NumberOperation;
import org.eclipse.fennec.model.query.Operation;
import org.eclipse.fennec.model.query.Or;
import org.eclipse.fennec.model.query.QObject;
import org.eclipse.fennec.model.query.QSubject;
import org.eclipse.fennec.model.query.QWhere;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.model.query.SimpleValueComparator;
import org.eclipse.fennec.model.query.SortEntity;
import org.eclipse.fennec.model.query.SortOrder;
import org.eclipse.fennec.model.query.StartWith;
import org.eclipse.fennec.model.query.StringComparator;
import org.eclipse.fennec.model.query.StringOperation;
import org.eclipse.fennec.model.query.SuitableType;
import org.eclipse.fennec.model.query.ToLowerCase;
import org.eclipse.fennec.model.query.ToUpperCase;

import org.eclipse.fennec.model.utilities.UtilitiesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class QueryPackageImpl extends EPackageImpl implements QueryPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass queryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass qObjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass qSubjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass qWhereEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass chainingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass orEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass notEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass andEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stringOperationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass toLowerCaseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass toUpperCaseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass numberOperationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass averageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass comparatorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass simpleValueComparatorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stringComparatorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass endsWithEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass startWithEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass containsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass likeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dateComparatorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass isBeforeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass isAfterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass isBeforeOrEqualEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass isAfterOrEqualEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass isInRangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass numberComparatorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ltEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass lteEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass gteEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass gtEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eqEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass boolComparatorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass isBoolEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass sortEntityEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass operationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass enumComparatorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass isLiteralEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum sortOrderEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum suitableTypeEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.fennec.model.query.QueryPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private QueryPackageImpl() {
		super(eNS_URI, QueryFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link QueryPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static QueryPackage init() {
		if (isInited) return (QueryPackage)EPackage.Registry.INSTANCE.getEPackage(QueryPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredQueryPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		QueryPackageImpl theQueryPackage = registeredQueryPackage instanceof QueryPackageImpl ? (QueryPackageImpl)registeredQueryPackage : new QueryPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		UtilitiesPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theQueryPackage.createPackageContents();

		// Initialize created meta-data
		theQueryPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theQueryPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(QueryPackage.eNS_URI, theQueryPackage);
		return theQueryPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQuery() {
		return queryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_Subject() {
		return (EReference)queryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_From() {
		return (EReference)queryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_Where() {
		return (EReference)queryEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_GroupBy() {
		return (EReference)queryEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_SortBy() {
		return (EReference)queryEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_Count() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_Distinct() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_Limit() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_Skip() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_SaveQuery() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_Name() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQObject() {
		return qObjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQObject_RootEClass() {
		return (EReference)qObjectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQSubject() {
		return qSubjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQSubject_FeaturePath() {
		return (EReference)qSubjectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQSubject_IsExclude() {
		return (EAttribute)qSubjectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQSubject_Alias() {
		return (EAttribute)qSubjectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQSubject_AliasFeature() {
		return (EReference)qSubjectEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQSubject_Operation() {
		return (EReference)qSubjectEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQWhere() {
		return qWhereEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQWhere_FeaturePath() {
		return (EReference)qWhereEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQWhere_Comparator() {
		return (EReference)qWhereEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQWhere_Operation() {
		return (EReference)qWhereEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQWhere__Execute() {
		return qWhereEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getChaining() {
		return chainingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOr() {
		return orEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getNot() {
		return notEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAnd() {
		return andEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStringOperation() {
		return stringOperationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getToLowerCase() {
		return toLowerCaseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getToUpperCase() {
		return toUpperCaseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getNumberOperation() {
		return numberOperationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAverage() {
		return averageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getComparator() {
		return comparatorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getComparator_SuitableForType() {
		return (EAttribute)comparatorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComparator_Where() {
		return (EReference)comparatorEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getComparator__Compare() {
		return comparatorEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSimpleValueComparator() {
		return simpleValueComparatorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSimpleValueComparator_Value() {
		return (EAttribute)simpleValueComparatorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStringComparator() {
		return stringComparatorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEndsWith() {
		return endsWithEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStartWith() {
		return startWithEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getContains() {
		return containsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLike() {
		return likeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDateComparator() {
		return dateComparatorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIsBefore() {
		return isBeforeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIsAfter() {
		return isAfterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIsBeforeOrEqual() {
		return isBeforeOrEqualEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIsAfterOrEqual() {
		return isAfterOrEqualEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIsInRange() {
		return isInRangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIsInRange_StartValue() {
		return (EAttribute)isInRangeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIsInRange_EndValue() {
		return (EAttribute)isInRangeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIsInRange_StartIncluded() {
		return (EAttribute)isInRangeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIsInRange_EndIncluded() {
		return (EAttribute)isInRangeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getNumberComparator() {
		return numberComparatorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLt() {
		return ltEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLte() {
		return lteEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGte() {
		return gteEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGt() {
		return gtEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEq() {
		return eqEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBoolComparator() {
		return boolComparatorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIsBool() {
		return isBoolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSortEntity() {
		return sortEntityEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSortEntity_SortOrder() {
		return (EAttribute)sortEntityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSortEntity_SortFeature() {
		return (EReference)sortEntityEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOperation() {
		return operationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOperation_SuitableForType() {
		return (EAttribute)operationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getOperation__Execute() {
		return operationEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEnumComparator() {
		return enumComparatorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIsLiteral() {
		return isLiteralEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getSortOrder() {
		return sortOrderEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getSuitableType() {
		return suitableTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public QueryFactory getQueryFactory() {
		return (QueryFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		queryEClass = createEClass(QUERY);
		createEReference(queryEClass, QUERY__SUBJECT);
		createEReference(queryEClass, QUERY__FROM);
		createEReference(queryEClass, QUERY__WHERE);
		createEReference(queryEClass, QUERY__GROUP_BY);
		createEReference(queryEClass, QUERY__SORT_BY);
		createEAttribute(queryEClass, QUERY__COUNT);
		createEAttribute(queryEClass, QUERY__DISTINCT);
		createEAttribute(queryEClass, QUERY__LIMIT);
		createEAttribute(queryEClass, QUERY__SKIP);
		createEAttribute(queryEClass, QUERY__SAVE_QUERY);
		createEAttribute(queryEClass, QUERY__NAME);

		qObjectEClass = createEClass(QOBJECT);
		createEReference(qObjectEClass, QOBJECT__ROOT_ECLASS);

		qSubjectEClass = createEClass(QSUBJECT);
		createEReference(qSubjectEClass, QSUBJECT__FEATURE_PATH);
		createEAttribute(qSubjectEClass, QSUBJECT__IS_EXCLUDE);
		createEAttribute(qSubjectEClass, QSUBJECT__ALIAS);
		createEReference(qSubjectEClass, QSUBJECT__ALIAS_FEATURE);
		createEReference(qSubjectEClass, QSUBJECT__OPERATION);

		qWhereEClass = createEClass(QWHERE);
		createEReference(qWhereEClass, QWHERE__FEATURE_PATH);
		createEReference(qWhereEClass, QWHERE__COMPARATOR);
		createEReference(qWhereEClass, QWHERE__OPERATION);
		createEOperation(qWhereEClass, QWHERE___EXECUTE);

		chainingEClass = createEClass(CHAINING);

		orEClass = createEClass(OR);

		notEClass = createEClass(NOT);

		andEClass = createEClass(AND);

		stringOperationEClass = createEClass(STRING_OPERATION);

		toLowerCaseEClass = createEClass(TO_LOWER_CASE);

		toUpperCaseEClass = createEClass(TO_UPPER_CASE);

		numberOperationEClass = createEClass(NUMBER_OPERATION);

		averageEClass = createEClass(AVERAGE);

		comparatorEClass = createEClass(COMPARATOR);
		createEAttribute(comparatorEClass, COMPARATOR__SUITABLE_FOR_TYPE);
		createEReference(comparatorEClass, COMPARATOR__WHERE);
		createEOperation(comparatorEClass, COMPARATOR___COMPARE);

		simpleValueComparatorEClass = createEClass(SIMPLE_VALUE_COMPARATOR);
		createEAttribute(simpleValueComparatorEClass, SIMPLE_VALUE_COMPARATOR__VALUE);

		stringComparatorEClass = createEClass(STRING_COMPARATOR);

		endsWithEClass = createEClass(ENDS_WITH);

		startWithEClass = createEClass(START_WITH);

		containsEClass = createEClass(CONTAINS);

		likeEClass = createEClass(LIKE);

		dateComparatorEClass = createEClass(DATE_COMPARATOR);

		isBeforeEClass = createEClass(IS_BEFORE);

		isAfterEClass = createEClass(IS_AFTER);

		isBeforeOrEqualEClass = createEClass(IS_BEFORE_OR_EQUAL);

		isAfterOrEqualEClass = createEClass(IS_AFTER_OR_EQUAL);

		isInRangeEClass = createEClass(IS_IN_RANGE);
		createEAttribute(isInRangeEClass, IS_IN_RANGE__START_VALUE);
		createEAttribute(isInRangeEClass, IS_IN_RANGE__END_VALUE);
		createEAttribute(isInRangeEClass, IS_IN_RANGE__START_INCLUDED);
		createEAttribute(isInRangeEClass, IS_IN_RANGE__END_INCLUDED);

		numberComparatorEClass = createEClass(NUMBER_COMPARATOR);

		ltEClass = createEClass(LT);

		lteEClass = createEClass(LTE);

		gteEClass = createEClass(GTE);

		gtEClass = createEClass(GT);

		eqEClass = createEClass(EQ);

		boolComparatorEClass = createEClass(BOOL_COMPARATOR);

		isBoolEClass = createEClass(IS_BOOL);

		sortEntityEClass = createEClass(SORT_ENTITY);
		createEAttribute(sortEntityEClass, SORT_ENTITY__SORT_ORDER);
		createEReference(sortEntityEClass, SORT_ENTITY__SORT_FEATURE);

		operationEClass = createEClass(OPERATION);
		createEAttribute(operationEClass, OPERATION__SUITABLE_FOR_TYPE);
		createEOperation(operationEClass, OPERATION___EXECUTE);

		enumComparatorEClass = createEClass(ENUM_COMPARATOR);

		isLiteralEClass = createEClass(IS_LITERAL);

		// Create enums
		sortOrderEEnum = createEEnum(SORT_ORDER);
		suitableTypeEEnum = createEEnum(SUITABLE_TYPE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		UtilitiesPackage theUtilitiesPackage = (UtilitiesPackage)EPackage.Registry.INSTANCE.getEPackage(UtilitiesPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		chainingEClass.getESuperTypes().add(this.getQWhere());
		orEClass.getESuperTypes().add(this.getChaining());
		notEClass.getESuperTypes().add(this.getChaining());
		andEClass.getESuperTypes().add(this.getChaining());
		stringOperationEClass.getESuperTypes().add(this.getOperation());
		toLowerCaseEClass.getESuperTypes().add(this.getStringOperation());
		toUpperCaseEClass.getESuperTypes().add(this.getStringOperation());
		numberOperationEClass.getESuperTypes().add(this.getOperation());
		averageEClass.getESuperTypes().add(this.getNumberOperation());
		simpleValueComparatorEClass.getESuperTypes().add(this.getComparator());
		stringComparatorEClass.getESuperTypes().add(this.getSimpleValueComparator());
		endsWithEClass.getESuperTypes().add(this.getStringComparator());
		startWithEClass.getESuperTypes().add(this.getStringComparator());
		containsEClass.getESuperTypes().add(this.getStringComparator());
		likeEClass.getESuperTypes().add(this.getStringComparator());
		dateComparatorEClass.getESuperTypes().add(this.getSimpleValueComparator());
		isBeforeEClass.getESuperTypes().add(this.getDateComparator());
		isAfterEClass.getESuperTypes().add(this.getDateComparator());
		isBeforeOrEqualEClass.getESuperTypes().add(this.getDateComparator());
		isAfterOrEqualEClass.getESuperTypes().add(this.getDateComparator());
		isInRangeEClass.getESuperTypes().add(this.getComparator());
		numberComparatorEClass.getESuperTypes().add(this.getSimpleValueComparator());
		ltEClass.getESuperTypes().add(this.getNumberComparator());
		lteEClass.getESuperTypes().add(this.getNumberComparator());
		gteEClass.getESuperTypes().add(this.getNumberComparator());
		gtEClass.getESuperTypes().add(this.getNumberComparator());
		eqEClass.getESuperTypes().add(this.getNumberComparator());
		boolComparatorEClass.getESuperTypes().add(this.getSimpleValueComparator());
		isBoolEClass.getESuperTypes().add(this.getBoolComparator());
		enumComparatorEClass.getESuperTypes().add(this.getSimpleValueComparator());
		isLiteralEClass.getESuperTypes().add(this.getEnumComparator());

		// Initialize classes, features, and operations; add parameters
		initEClass(queryEClass, Query.class, "Query", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getQuery_Subject(), this.getQSubject(), null, "subject", null, 0, -1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQuery_From(), this.getQObject(), null, "from", null, 0, -1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQuery_Where(), this.getQWhere(), null, "where", null, 0, -1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQuery_GroupBy(), theUtilitiesPackage.getFeaturePath(), null, "groupBy", null, 0, -1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQuery_SortBy(), this.getSortEntity(), null, "sortBy", null, 0, -1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_Count(), ecorePackage.getEBoolean(), "count", null, 1, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_Distinct(), ecorePackage.getEBoolean(), "distinct", null, 1, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_Limit(), ecorePackage.getEInt(), "limit", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_Skip(), ecorePackage.getEInt(), "skip", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_SaveQuery(), ecorePackage.getEBoolean(), "saveQuery", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_Name(), ecorePackage.getEString(), "name", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(qObjectEClass, QObject.class, "QObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getQObject_RootEClass(), ecorePackage.getEClass(), null, "rootEClass", null, 0, 1, QObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(qSubjectEClass, QSubject.class, "QSubject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getQSubject_FeaturePath(), theUtilitiesPackage.getFeaturePath(), null, "featurePath", null, 1, 1, QSubject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQSubject_IsExclude(), ecorePackage.getEBoolean(), "isExclude", "false", 0, 1, QSubject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQSubject_Alias(), ecorePackage.getEString(), "alias", null, 0, 1, QSubject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQSubject_AliasFeature(), ecorePackage.getEStructuralFeature(), null, "aliasFeature", null, 0, 1, QSubject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQSubject_Operation(), this.getOperation(), null, "operation", null, 0, 1, QSubject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(qWhereEClass, QWhere.class, "QWhere", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getQWhere_FeaturePath(), theUtilitiesPackage.getFeaturePath(), null, "featurePath", null, 1, 1, QWhere.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQWhere_Comparator(), this.getComparator(), this.getComparator_Where(), "comparator", null, 1, 1, QWhere.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQWhere_Operation(), this.getOperation(), null, "operation", null, 0, 1, QWhere.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEOperation(getQWhere__Execute(), ecorePackage.getEBoolean(), "execute", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(chainingEClass, Chaining.class, "Chaining", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(orEClass, Or.class, "Or", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(notEClass, Not.class, "Not", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(andEClass, And.class, "And", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(stringOperationEClass, StringOperation.class, "StringOperation", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(toLowerCaseEClass, ToLowerCase.class, "ToLowerCase", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(toUpperCaseEClass, ToUpperCase.class, "ToUpperCase", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(numberOperationEClass, NumberOperation.class, "NumberOperation", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(averageEClass, Average.class, "Average", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(comparatorEClass, Comparator.class, "Comparator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getComparator_SuitableForType(), this.getSuitableType(), "suitableForType", null, 1, 1, Comparator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComparator_Where(), this.getQWhere(), this.getQWhere_Comparator(), "where", null, 0, 1, Comparator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEOperation(getComparator__Compare(), ecorePackage.getEBoolean(), "compare", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(simpleValueComparatorEClass, SimpleValueComparator.class, "SimpleValueComparator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSimpleValueComparator_Value(), ecorePackage.getEString(), "value", null, 0, 1, SimpleValueComparator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stringComparatorEClass, StringComparator.class, "StringComparator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(endsWithEClass, EndsWith.class, "EndsWith", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(startWithEClass, StartWith.class, "StartWith", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(containsEClass, Contains.class, "Contains", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(likeEClass, Like.class, "Like", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(dateComparatorEClass, DateComparator.class, "DateComparator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(isBeforeEClass, IsBefore.class, "IsBefore", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(isAfterEClass, IsAfter.class, "IsAfter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(isBeforeOrEqualEClass, IsBeforeOrEqual.class, "IsBeforeOrEqual", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(isAfterOrEqualEClass, IsAfterOrEqual.class, "IsAfterOrEqual", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(isInRangeEClass, IsInRange.class, "IsInRange", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIsInRange_StartValue(), ecorePackage.getEString(), "startValue", null, 0, 1, IsInRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIsInRange_EndValue(), ecorePackage.getEString(), "endValue", null, 0, 1, IsInRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIsInRange_StartIncluded(), ecorePackage.getEBoolean(), "startIncluded", null, 0, 1, IsInRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIsInRange_EndIncluded(), ecorePackage.getEBoolean(), "endIncluded", null, 0, 1, IsInRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(numberComparatorEClass, NumberComparator.class, "NumberComparator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(ltEClass, Lt.class, "Lt", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(lteEClass, Lte.class, "Lte", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(gteEClass, Gte.class, "Gte", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(gtEClass, Gt.class, "Gt", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eqEClass, Eq.class, "Eq", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(boolComparatorEClass, BoolComparator.class, "BoolComparator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(isBoolEClass, IsBool.class, "IsBool", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(sortEntityEClass, SortEntity.class, "SortEntity", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSortEntity_SortOrder(), this.getSortOrder(), "sortOrder", "DESC", 1, 1, SortEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSortEntity_SortFeature(), ecorePackage.getEStructuralFeature(), null, "sortFeature", null, 1, 1, SortEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(operationEClass, Operation.class, "Operation", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getOperation_SuitableForType(), this.getSuitableType(), "suitableForType", null, 1, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEOperation(getOperation__Execute(), ecorePackage.getEObject(), "execute", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(enumComparatorEClass, EnumComparator.class, "EnumComparator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(isLiteralEClass, IsLiteral.class, "IsLiteral", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Initialize enums and add enum literals
		initEEnum(sortOrderEEnum, SortOrder.class, "SortOrder");
		addEEnumLiteral(sortOrderEEnum, SortOrder.DESC);
		addEEnumLiteral(sortOrderEEnum, SortOrder.ASC);

		initEEnum(suitableTypeEEnum, SuitableType.class, "SuitableType");
		addEEnumLiteral(suitableTypeEEnum, SuitableType.NUMERIC);
		addEEnumLiteral(suitableTypeEEnum, SuitableType.DATE);
		addEEnumLiteral(suitableTypeEEnum, SuitableType.STRING);
		addEEnumLiteral(suitableTypeEEnum, SuitableType.BOOLEAN);
		addEEnumLiteral(suitableTypeEEnum, SuitableType.ENUM);
		addEEnumLiteral(suitableTypeEEnum, SuitableType.MIXED);
		addEEnumLiteral(suitableTypeEEnum, SuitableType.OTHER);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// Version
		createVersionAnnotations();
	}

	/**
	 * Initializes the annotations for <b>Version</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createVersionAnnotations() {
		String source = "Version";
		addAnnotation
		  (this,
		   source,
		   new String[] {
			   "value", "1.0"
		   });
	}

} //QueryPackageImpl
