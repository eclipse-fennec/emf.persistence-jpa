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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.fennec.model.query.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class QueryFactoryImpl extends EFactoryImpl implements QueryFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static QueryFactory init() {
		try {
			QueryFactory theQueryFactory = (QueryFactory)EPackage.Registry.INSTANCE.getEFactory(QueryPackage.eNS_URI);
			if (theQueryFactory != null) {
				return theQueryFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new QueryFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public QueryFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case QueryPackage.QUERY: return createQuery();
			case QueryPackage.QOBJECT: return createQObject();
			case QueryPackage.QSUBJECT: return createQSubject();
			case QueryPackage.OR: return createOr();
			case QueryPackage.NOT: return createNot();
			case QueryPackage.AND: return createAnd();
			case QueryPackage.TO_LOWER_CASE: return createToLowerCase();
			case QueryPackage.TO_UPPER_CASE: return createToUpperCase();
			case QueryPackage.AVERAGE: return createAverage();
			case QueryPackage.MIN: return createMin();
			case QueryPackage.MAX: return createMax();
			case QueryPackage.SUM: return createSum();
			case QueryPackage.COUNT_OPERATION: return createCountOperation();
			case QueryPackage.ENDS_WITH: return createEndsWith();
			case QueryPackage.START_WITH: return createStartWith();
			case QueryPackage.CONTAINS: return createContains();
			case QueryPackage.LIKE: return createLike();
			case QueryPackage.IS_BEFORE: return createIsBefore();
			case QueryPackage.IS_AFTER: return createIsAfter();
			case QueryPackage.IS_BEFORE_OR_EQUAL: return createIsBeforeOrEqual();
			case QueryPackage.IS_AFTER_OR_EQUAL: return createIsAfterOrEqual();
			case QueryPackage.IS_IN_RANGE: return createIsInRange();
			case QueryPackage.LT: return createLt();
			case QueryPackage.LTE: return createLte();
			case QueryPackage.GTE: return createGte();
			case QueryPackage.GT: return createGt();
			case QueryPackage.EQ: return createEq();
			case QueryPackage.BOOL_COMPARATOR: return createBoolComparator();
			case QueryPackage.IS_BOOL: return createIsBool();
			case QueryPackage.SORT_ENTITY: return createSortEntity();
			case QueryPackage.IS_LITERAL: return createIsLiteral();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case QueryPackage.SORT_ORDER:
				return createSortOrderFromString(eDataType, initialValue);
			case QueryPackage.SUITABLE_TYPE:
				return createSuitableTypeFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case QueryPackage.SORT_ORDER:
				return convertSortOrderToString(eDataType, instanceValue);
			case QueryPackage.SUITABLE_TYPE:
				return convertSuitableTypeToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Query createQuery() {
		QueryImpl query = new QueryImpl();
		return query;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public QObject createQObject() {
		QObjectImpl qObject = new QObjectImpl();
		return qObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public QSubject createQSubject() {
		QSubjectImpl qSubject = new QSubjectImpl();
		return qSubject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Or createOr() {
		OrImpl or = new OrImpl();
		return or;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Not createNot() {
		NotImpl not = new NotImpl();
		return not;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public And createAnd() {
		AndImpl and = new AndImpl();
		return and;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ToLowerCase createToLowerCase() {
		ToLowerCaseImpl toLowerCase = new ToLowerCaseImpl();
		return toLowerCase;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ToUpperCase createToUpperCase() {
		ToUpperCaseImpl toUpperCase = new ToUpperCaseImpl();
		return toUpperCase;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Average createAverage() {
		AverageImpl average = new AverageImpl();
		return average;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Min createMin() {
		MinImpl min = new MinImpl();
		return min;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Max createMax() {
		MaxImpl max = new MaxImpl();
		return max;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Sum createSum() {
		SumImpl sum = new SumImpl();
		return sum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CountOperation createCountOperation() {
		CountOperationImpl countOperation = new CountOperationImpl();
		return countOperation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EndsWith createEndsWith() {
		EndsWithImpl endsWith = new EndsWithImpl();
		return endsWith;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public StartWith createStartWith() {
		StartWithImpl startWith = new StartWithImpl();
		return startWith;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Contains createContains() {
		ContainsImpl contains = new ContainsImpl();
		return contains;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Like createLike() {
		LikeImpl like = new LikeImpl();
		return like;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IsBefore createIsBefore() {
		IsBeforeImpl isBefore = new IsBeforeImpl();
		return isBefore;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IsAfter createIsAfter() {
		IsAfterImpl isAfter = new IsAfterImpl();
		return isAfter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IsBeforeOrEqual createIsBeforeOrEqual() {
		IsBeforeOrEqualImpl isBeforeOrEqual = new IsBeforeOrEqualImpl();
		return isBeforeOrEqual;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IsAfterOrEqual createIsAfterOrEqual() {
		IsAfterOrEqualImpl isAfterOrEqual = new IsAfterOrEqualImpl();
		return isAfterOrEqual;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IsInRange createIsInRange() {
		IsInRangeImpl isInRange = new IsInRangeImpl();
		return isInRange;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Lt createLt() {
		LtImpl lt = new LtImpl();
		return lt;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Lte createLte() {
		LteImpl lte = new LteImpl();
		return lte;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Gte createGte() {
		GteImpl gte = new GteImpl();
		return gte;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Gt createGt() {
		GtImpl gt = new GtImpl();
		return gt;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Eq createEq() {
		EqImpl eq = new EqImpl();
		return eq;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BoolComparator createBoolComparator() {
		BoolComparatorImpl boolComparator = new BoolComparatorImpl();
		return boolComparator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IsBool createIsBool() {
		IsBoolImpl isBool = new IsBoolImpl();
		return isBool;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SortEntity createSortEntity() {
		SortEntityImpl sortEntity = new SortEntityImpl();
		return sortEntity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IsLiteral createIsLiteral() {
		IsLiteralImpl isLiteral = new IsLiteralImpl();
		return isLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SortOrder createSortOrderFromString(EDataType eDataType, String initialValue) {
		SortOrder result = SortOrder.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSortOrderToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SuitableType createSuitableTypeFromString(EDataType eDataType, String initialValue) {
		SuitableType result = SuitableType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSuitableTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public QueryPackage getQueryPackage() {
		return (QueryPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static QueryPackage getPackage() {
		return QueryPackage.eINSTANCE;
	}

} //QueryFactoryImpl
