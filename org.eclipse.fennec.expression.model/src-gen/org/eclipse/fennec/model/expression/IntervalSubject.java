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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Interval Subject</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The interval binding of an IntervalMatch (issue #215) — the pair of features that together form one interval: a validity period, a price band, a measurement range. Deliberately structural, following the GeoSubject precedent (decisions G1/G2): the QUERY names the pair, the mapping layer decides how it is indexed, and a model aspect may later derive the paths. The three convention attributes describe the DATA rather than the question, because an index encodes them at write time — a range-field mapping whose conventions disagree with the subject of an incoming query returns wrong answers silently and has to be refused by name. Not an Expression: an argument, like GeoSubject. A packed binding (one column holding a range value, the PostgreSQL range-type shape) is the analogue of GeoSubject.pathPoint and stays additive until a backend forces it.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.IntervalSubject#isLowerIncluded <em>Lower Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.IntervalSubject#isUpperIncluded <em>Upper Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.IntervalSubject#isNullMeansUnbounded <em>Null Means Unbounded</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.IntervalSubject#getPathLower <em>Path Lower</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.IntervalSubject#getPathUpper <em>Path Upper</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalSubject()
 * @model
 * @generated
 */
@ProviderType
public interface IntervalSubject extends EObject {
	/**
	 * Returns the value of the '<em><b>Lower Included</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether the stored lower bound belongs to the interval — the convention of the DATA. Default closed.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Lower Included</em>' attribute.
	 * @see #setLowerIncluded(boolean)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalSubject_LowerIncluded()
	 * @model default="true"
	 * @generated
	 */
	boolean isLowerIncluded();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IntervalSubject#isLowerIncluded <em>Lower Included</em>}' attribute.
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
	 * Whether the stored upper bound belongs to the interval — the convention of the DATA. Default closed; set false for the half-open [from, to) shape temporal models use so that adjacent periods do not overlap. Deliberately not expressible from the query side: adjacency is a question about the subject's own upper bound, and no bound on the query interval can answer it.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Upper Included</em>' attribute.
	 * @see #setUpperIncluded(boolean)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalSubject_UpperIncluded()
	 * @model default="true"
	 * @generated
	 */
	boolean isUpperIncluded();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IntervalSubject#isUpperIncluded <em>Upper Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Upper Included</em>' attribute.
	 * @see #isUpperIncluded()
	 * @generated
	 */
	void setUpperIncluded(boolean value);

	/**
	 * Returns the value of the '<em><b>Null Means Unbounded</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * What an unset bound means. Default false: a null bound makes the predicate UNKNOWN, the 3VL discipline of issues #94/#97 unchanged — excluded positively, excluded under not(...), guarded explicitly in every push-down. True makes a null lower bound minus-infinity and a null upper bound plus-infinity, which is the normal shape of temporal models where an unset validTo means "still valid". Both readings are real and opposite — a missing measurement bound is missing data — which is why this is declared rather than assumed. An index encodes it at write time (an unbounded end becomes the extremal representable value), so a mapping that disagrees with the query is a silent-wrong-answer defect.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Null Means Unbounded</em>' attribute.
	 * @see #setNullMeansUnbounded(boolean)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalSubject_NullMeansUnbounded()
	 * @model default="false"
	 * @generated
	 */
	boolean isNullMeansUnbounded();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IntervalSubject#isNullMeansUnbounded <em>Null Means Unbounded</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Null Means Unbounded</em>' attribute.
	 * @see #isNullMeansUnbounded()
	 * @generated
	 */
	void setNullMeansUnbounded(boolean value);

	/**
	 * Returns the value of the '<em><b>Path Lower</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path Lower</em>' containment reference.
	 * @see #setPathLower(PropertyPath)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalSubject_PathLower()
	 * @model containment="true" required="true"
	 * @generated
	 */
	PropertyPath getPathLower();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IntervalSubject#getPathLower <em>Path Lower</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path Lower</em>' containment reference.
	 * @see #getPathLower()
	 * @generated
	 */
	void setPathLower(PropertyPath value);

	/**
	 * Returns the value of the '<em><b>Path Upper</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path Upper</em>' containment reference.
	 * @see #setPathUpper(PropertyPath)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalSubject_PathUpper()
	 * @model containment="true" required="true"
	 * @generated
	 */
	PropertyPath getPathUpper();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IntervalSubject#getPathUpper <em>Path Upper</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path Upper</em>' containment reference.
	 * @see #getPathUpper()
	 * @generated
	 */
	void setPathUpper(PropertyPath value);

} // IntervalSubject
