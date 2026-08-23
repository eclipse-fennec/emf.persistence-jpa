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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Interval Match</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * How the subject interval relates to the interval given by lower/upper (issue #215) — the concept a hand-wired pair of comparisons cannot express: nothing validates that the two features belong together, nothing pushes them down as one predicate, and every consumer picks its own boundary convention. Neighbour construct: Between asks whether a POINT lies in an interval, this asks how two INTERVALS relate. "Valid at t" is relation CONTAINS with a degenerate query interval (lower == upper, both bounds included) — deliberately not a fourth relation. An inverted query interval over two literals is a static validation error; an inverted subject row is the empty interval and matches no relation, including WITHIN. Capability INTERVAL_MATCH.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.IntervalMatch#getRelation <em>Relation</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.IntervalMatch#isLowerIncluded <em>Lower Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.IntervalMatch#isUpperIncluded <em>Upper Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.IntervalMatch#getSubject <em>Subject</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.IntervalMatch#getLower <em>Lower</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.IntervalMatch#getUpper <em>Upper</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalMatch()
 * @model
 * @generated
 */
@ProviderType
public interface IntervalMatch extends Expression {
	/**
	 * Returns the value of the '<em><b>Relation</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.expression.IntervalRelation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Relation</em>' attribute.
	 * @see org.eclipse.fennec.model.expression.IntervalRelation
	 * @see #setRelation(IntervalRelation)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalMatch_Relation()
	 * @model required="true"
	 * @generated
	 */
	IntervalRelation getRelation();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IntervalMatch#getRelation <em>Relation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Relation</em>' attribute.
	 * @see org.eclipse.fennec.model.expression.IntervalRelation
	 * @see #getRelation()
	 * @generated
	 */
	void setRelation(IntervalRelation value);

	/**
	 * Returns the value of the '<em><b>Lower Included</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether the query interval's lower bound belongs to it — the convention of the QUESTION, mirroring Between. Semantics, not a hint: a backend that cannot render an exclusive bound refuses the query, it never rounds it to the inclusive one.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Lower Included</em>' attribute.
	 * @see #setLowerIncluded(boolean)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalMatch_LowerIncluded()
	 * @model default="true"
	 * @generated
	 */
	boolean isLowerIncluded();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IntervalMatch#isLowerIncluded <em>Lower Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lower Included</em>' attribute.
	 * @see #isLowerIncluded()
	 * @generated
	 */
	void setLowerIncluded(boolean value);

	/**
	 * Returns the value of the '<em><b>Upper Included</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether the query interval's upper bound belongs to it — the convention of the QUESTION, mirroring Between.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Upper Included</em>' attribute.
	 * @see #setUpperIncluded(boolean)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalMatch_UpperIncluded()
	 * @model default="true"
	 * @generated
	 */
	boolean isUpperIncluded();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IntervalMatch#isUpperIncluded <em>Upper Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Upper Included</em>' attribute.
	 * @see #isUpperIncluded()
	 * @generated
	 */
	void setUpperIncluded(boolean value);

	/**
	 * Returns the value of the '<em><b>Subject</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subject</em>' containment reference.
	 * @see #setSubject(IntervalSubject)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalMatch_Subject()
	 * @model containment="true" required="true"
	 * @generated
	 */
	IntervalSubject getSubject();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IntervalMatch#getSubject <em>Subject</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Subject</em>' containment reference.
	 * @see #getSubject()
	 * @generated
	 */
	void setSubject(IntervalSubject value);

	/**
	 * Returns the value of the '<em><b>Lower</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The query interval's lower bound — a literal or a parameter. The same ordered domain (numeric or temporal) as both subject paths; a mixed pair is a validation error, not a runtime surprise.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Lower</em>' containment reference.
	 * @see #setLower(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalMatch_Lower()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getLower();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IntervalMatch#getLower <em>Lower</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lower</em>' containment reference.
	 * @see #getLower()
	 * @generated
	 */
	void setLower(Expression value);

	/**
	 * Returns the value of the '<em><b>Upper</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The query interval's upper bound — a literal or a parameter. Equal to lower for the degenerate "at t" form.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Upper</em>' containment reference.
	 * @see #setUpper(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalMatch_Upper()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getUpper();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IntervalMatch#getUpper <em>Upper</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Upper</em>' containment reference.
	 * @see #getUpper()
	 * @generated
	 */
	void setUpper(Expression value);

} // IntervalMatch
