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
package org.eclipse.fennec.model.query.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.fennec.model.query.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.model.query.QueryPackage
 * @generated
 */
public class QuerySwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static QueryPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public QuerySwitch() {
		if (modelPackage == null) {
			modelPackage = QueryPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case QueryPackage.QUERY: {
				Query query = (Query)theEObject;
				T result = caseQuery(query);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.QOBJECT: {
				QObject qObject = (QObject)theEObject;
				T result = caseQObject(qObject);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.QSUBJECT: {
				QSubject qSubject = (QSubject)theEObject;
				T result = caseQSubject(qSubject);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.QWHERE: {
				QWhere qWhere = (QWhere)theEObject;
				T result = caseQWhere(qWhere);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.CHAINING: {
				Chaining chaining = (Chaining)theEObject;
				T result = caseChaining(chaining);
				if (result == null) result = caseQWhere(chaining);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.OR: {
				Or or = (Or)theEObject;
				T result = caseOr(or);
				if (result == null) result = caseChaining(or);
				if (result == null) result = caseQWhere(or);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.NOT: {
				Not not = (Not)theEObject;
				T result = caseNot(not);
				if (result == null) result = caseChaining(not);
				if (result == null) result = caseQWhere(not);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.AND: {
				And and = (And)theEObject;
				T result = caseAnd(and);
				if (result == null) result = caseChaining(and);
				if (result == null) result = caseQWhere(and);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.STRING_OPERATION: {
				StringOperation stringOperation = (StringOperation)theEObject;
				T result = caseStringOperation(stringOperation);
				if (result == null) result = caseOperation(stringOperation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.TO_LOWER_CASE: {
				ToLowerCase toLowerCase = (ToLowerCase)theEObject;
				T result = caseToLowerCase(toLowerCase);
				if (result == null) result = caseStringOperation(toLowerCase);
				if (result == null) result = caseOperation(toLowerCase);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.TO_UPPER_CASE: {
				ToUpperCase toUpperCase = (ToUpperCase)theEObject;
				T result = caseToUpperCase(toUpperCase);
				if (result == null) result = caseStringOperation(toUpperCase);
				if (result == null) result = caseOperation(toUpperCase);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.NUMBER_OPERATION: {
				NumberOperation numberOperation = (NumberOperation)theEObject;
				T result = caseNumberOperation(numberOperation);
				if (result == null) result = caseOperation(numberOperation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.AVERAGE: {
				Average average = (Average)theEObject;
				T result = caseAverage(average);
				if (result == null) result = caseNumberOperation(average);
				if (result == null) result = caseOperation(average);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.COMPARATOR: {
				Comparator comparator = (Comparator)theEObject;
				T result = caseComparator(comparator);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.SIMPLE_VALUE_COMPARATOR: {
				SimpleValueComparator simpleValueComparator = (SimpleValueComparator)theEObject;
				T result = caseSimpleValueComparator(simpleValueComparator);
				if (result == null) result = caseComparator(simpleValueComparator);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.STRING_COMPARATOR: {
				StringComparator stringComparator = (StringComparator)theEObject;
				T result = caseStringComparator(stringComparator);
				if (result == null) result = caseSimpleValueComparator(stringComparator);
				if (result == null) result = caseComparator(stringComparator);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.ENDS_WITH: {
				EndsWith endsWith = (EndsWith)theEObject;
				T result = caseEndsWith(endsWith);
				if (result == null) result = caseStringComparator(endsWith);
				if (result == null) result = caseSimpleValueComparator(endsWith);
				if (result == null) result = caseComparator(endsWith);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.START_WITH: {
				StartWith startWith = (StartWith)theEObject;
				T result = caseStartWith(startWith);
				if (result == null) result = caseStringComparator(startWith);
				if (result == null) result = caseSimpleValueComparator(startWith);
				if (result == null) result = caseComparator(startWith);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.CONTAINS: {
				Contains contains = (Contains)theEObject;
				T result = caseContains(contains);
				if (result == null) result = caseStringComparator(contains);
				if (result == null) result = caseSimpleValueComparator(contains);
				if (result == null) result = caseComparator(contains);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.LIKE: {
				Like like = (Like)theEObject;
				T result = caseLike(like);
				if (result == null) result = caseStringComparator(like);
				if (result == null) result = caseSimpleValueComparator(like);
				if (result == null) result = caseComparator(like);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.DATE_COMPARATOR: {
				DateComparator dateComparator = (DateComparator)theEObject;
				T result = caseDateComparator(dateComparator);
				if (result == null) result = caseSimpleValueComparator(dateComparator);
				if (result == null) result = caseComparator(dateComparator);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.IS_BEFORE: {
				IsBefore isBefore = (IsBefore)theEObject;
				T result = caseIsBefore(isBefore);
				if (result == null) result = caseDateComparator(isBefore);
				if (result == null) result = caseSimpleValueComparator(isBefore);
				if (result == null) result = caseComparator(isBefore);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.IS_AFTER: {
				IsAfter isAfter = (IsAfter)theEObject;
				T result = caseIsAfter(isAfter);
				if (result == null) result = caseDateComparator(isAfter);
				if (result == null) result = caseSimpleValueComparator(isAfter);
				if (result == null) result = caseComparator(isAfter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.IS_BEFORE_OR_EQUAL: {
				IsBeforeOrEqual isBeforeOrEqual = (IsBeforeOrEqual)theEObject;
				T result = caseIsBeforeOrEqual(isBeforeOrEqual);
				if (result == null) result = caseDateComparator(isBeforeOrEqual);
				if (result == null) result = caseSimpleValueComparator(isBeforeOrEqual);
				if (result == null) result = caseComparator(isBeforeOrEqual);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.IS_AFTER_OR_EQUAL: {
				IsAfterOrEqual isAfterOrEqual = (IsAfterOrEqual)theEObject;
				T result = caseIsAfterOrEqual(isAfterOrEqual);
				if (result == null) result = caseDateComparator(isAfterOrEqual);
				if (result == null) result = caseSimpleValueComparator(isAfterOrEqual);
				if (result == null) result = caseComparator(isAfterOrEqual);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.IS_IN_RANGE: {
				IsInRange isInRange = (IsInRange)theEObject;
				T result = caseIsInRange(isInRange);
				if (result == null) result = caseComparator(isInRange);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.NUMBER_COMPARATOR: {
				NumberComparator numberComparator = (NumberComparator)theEObject;
				T result = caseNumberComparator(numberComparator);
				if (result == null) result = caseSimpleValueComparator(numberComparator);
				if (result == null) result = caseComparator(numberComparator);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.LT: {
				Lt lt = (Lt)theEObject;
				T result = caseLt(lt);
				if (result == null) result = caseNumberComparator(lt);
				if (result == null) result = caseSimpleValueComparator(lt);
				if (result == null) result = caseComparator(lt);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.LTE: {
				Lte lte = (Lte)theEObject;
				T result = caseLte(lte);
				if (result == null) result = caseNumberComparator(lte);
				if (result == null) result = caseSimpleValueComparator(lte);
				if (result == null) result = caseComparator(lte);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.GTE: {
				Gte gte = (Gte)theEObject;
				T result = caseGte(gte);
				if (result == null) result = caseNumberComparator(gte);
				if (result == null) result = caseSimpleValueComparator(gte);
				if (result == null) result = caseComparator(gte);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.GT: {
				Gt gt = (Gt)theEObject;
				T result = caseGt(gt);
				if (result == null) result = caseNumberComparator(gt);
				if (result == null) result = caseSimpleValueComparator(gt);
				if (result == null) result = caseComparator(gt);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.EQ: {
				Eq eq = (Eq)theEObject;
				T result = caseEq(eq);
				if (result == null) result = caseNumberComparator(eq);
				if (result == null) result = caseSimpleValueComparator(eq);
				if (result == null) result = caseComparator(eq);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.BOOL_COMPARATOR: {
				BoolComparator boolComparator = (BoolComparator)theEObject;
				T result = caseBoolComparator(boolComparator);
				if (result == null) result = caseSimpleValueComparator(boolComparator);
				if (result == null) result = caseComparator(boolComparator);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.IS_BOOL: {
				IsBool isBool = (IsBool)theEObject;
				T result = caseIsBool(isBool);
				if (result == null) result = caseBoolComparator(isBool);
				if (result == null) result = caseSimpleValueComparator(isBool);
				if (result == null) result = caseComparator(isBool);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.SORT_ENTITY: {
				SortEntity sortEntity = (SortEntity)theEObject;
				T result = caseSortEntity(sortEntity);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.OPERATION: {
				Operation operation = (Operation)theEObject;
				T result = caseOperation(operation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.ENUM_COMPARATOR: {
				EnumComparator enumComparator = (EnumComparator)theEObject;
				T result = caseEnumComparator(enumComparator);
				if (result == null) result = caseSimpleValueComparator(enumComparator);
				if (result == null) result = caseComparator(enumComparator);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case QueryPackage.IS_LITERAL: {
				IsLiteral isLiteral = (IsLiteral)theEObject;
				T result = caseIsLiteral(isLiteral);
				if (result == null) result = caseEnumComparator(isLiteral);
				if (result == null) result = caseSimpleValueComparator(isLiteral);
				if (result == null) result = caseComparator(isLiteral);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Query</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Query</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseQuery(Query object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>QObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>QObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseQObject(QObject object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>QSubject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>QSubject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseQSubject(QSubject object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>QWhere</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>QWhere</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseQWhere(QWhere object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Chaining</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Chaining</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChaining(Chaining object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Or</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Or</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOr(Or object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Not</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Not</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNot(Not object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>And</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>And</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAnd(And object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String Operation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String Operation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringOperation(StringOperation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>To Lower Case</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>To Lower Case</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseToLowerCase(ToLowerCase object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>To Upper Case</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>To Upper Case</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseToUpperCase(ToUpperCase object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Number Operation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Number Operation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNumberOperation(NumberOperation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Average</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Average</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAverage(Average object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Comparator</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Comparator</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseComparator(Comparator object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Simple Value Comparator</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Simple Value Comparator</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSimpleValueComparator(SimpleValueComparator object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String Comparator</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String Comparator</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringComparator(StringComparator object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Ends With</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Ends With</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEndsWith(EndsWith object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Start With</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Start With</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStartWith(StartWith object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Contains</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Contains</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseContains(Contains object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Like</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Like</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLike(Like object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Date Comparator</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Date Comparator</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDateComparator(DateComparator object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Is Before</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Is Before</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIsBefore(IsBefore object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Is After</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Is After</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIsAfter(IsAfter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Is Before Or Equal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Is Before Or Equal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIsBeforeOrEqual(IsBeforeOrEqual object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Is After Or Equal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Is After Or Equal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIsAfterOrEqual(IsAfterOrEqual object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Is In Range</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Is In Range</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIsInRange(IsInRange object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Number Comparator</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Number Comparator</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNumberComparator(NumberComparator object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Lt</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Lt</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLt(Lt object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Lte</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Lte</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLte(Lte object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Gte</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Gte</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGte(Gte object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Gt</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Gt</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGt(Gt object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Eq</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Eq</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEq(Eq object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bool Comparator</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bool Comparator</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBoolComparator(BoolComparator object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Is Bool</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Is Bool</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIsBool(IsBool object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sort Entity</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sort Entity</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSortEntity(SortEntity object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Operation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Operation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOperation(Operation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Enum Comparator</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Enum Comparator</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEnumComparator(EnumComparator object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Is Literal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Is Literal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIsLiteral(IsLiteral object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //QuerySwitch
