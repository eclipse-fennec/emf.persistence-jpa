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
 * A representation of the model object '<em><b>String Match</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * String pattern matching. The caseInsensitive flag replaces the v1 toLower/toUpper predicate workaround — backends translate it natively (LOWER() both sides, regex i-option).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.StringMatch#getKind <em>Kind</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.StringMatch#isCaseInsensitive <em>Case Insensitive</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.StringMatch#getMaxEdits <em>Max Edits</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.StringMatch#getPrefixLength <em>Prefix Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.StringMatch#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.StringMatch#getPattern <em>Pattern</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getStringMatch()
 * @model
 * @generated
 */
@ProviderType
public interface StringMatch extends Expression {
	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.expression.StringMatchKind}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.fennec.model.expression.StringMatchKind
	 * @see #setKind(StringMatchKind)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getStringMatch_Kind()
	 * @model required="true"
	 * @generated
	 */
	StringMatchKind getKind();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.StringMatch#getKind <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.fennec.model.expression.StringMatchKind
	 * @see #getKind()
	 * @generated
	 */
	void setKind(StringMatchKind value);

	/**
	 * Returns the value of the '<em><b>Case Insensitive</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Case Insensitive</em>' attribute.
	 * @see #setCaseInsensitive(boolean)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getStringMatch_CaseInsensitive()
	 * @model default="false"
	 * @generated
	 */
	boolean isCaseInsensitive();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.StringMatch#isCaseInsensitive <em>Case Insensitive</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Case Insensitive</em>' attribute.
	 * @see #isCaseInsensitive()
	 * @generated
	 */
	void setCaseInsensitive(boolean value);

	/**
	 * Returns the value of the '<em><b>Max Edits</b></em>' attribute.
	 * The default value is <code>"2"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The Damerau-Levenshtein edit budget for kind FUZZY (issue #167): 1 or 2, default 2 (mirroring Lucene). Meaningful only for FUZZY — set on any other kind the validator refuses the query by shape. Unsettable so that "explicitly configured" is distinguishable from the default.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Max Edits</em>' attribute.
	 * @see #isSetMaxEdits()
	 * @see #unsetMaxEdits()
	 * @see #setMaxEdits(int)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getStringMatch_MaxEdits()
	 * @model default="2" unsettable="true"
	 * @generated
	 */
	int getMaxEdits();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.StringMatch#getMaxEdits <em>Max Edits</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Edits</em>' attribute.
	 * @see #isSetMaxEdits()
	 * @see #unsetMaxEdits()
	 * @see #getMaxEdits()
	 * @generated
	 */
	void setMaxEdits(int value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.expression.StringMatch#getMaxEdits <em>Max Edits</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMaxEdits()
	 * @see #getMaxEdits()
	 * @see #setMaxEdits(int)
	 * @generated
	 */
	void unsetMaxEdits();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.expression.StringMatch#getMaxEdits <em>Max Edits</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Max Edits</em>' attribute is set.
	 * @see #unsetMaxEdits()
	 * @see #getMaxEdits()
	 * @see #setMaxEdits(int)
	 * @generated
	 */
	boolean isSetMaxEdits();

	/**
	 * Returns the value of the '<em><b>Prefix Length</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Leading characters that must match exactly for kind FUZZY (issue #167) — the knob that keeps fuzzy matching from exploding on short inputs. Default 0. Meaningful only for FUZZY, like maxEdits.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Prefix Length</em>' attribute.
	 * @see #isSetPrefixLength()
	 * @see #unsetPrefixLength()
	 * @see #setPrefixLength(int)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getStringMatch_PrefixLength()
	 * @model default="0" unsettable="true"
	 * @generated
	 */
	int getPrefixLength();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.StringMatch#getPrefixLength <em>Prefix Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Prefix Length</em>' attribute.
	 * @see #isSetPrefixLength()
	 * @see #unsetPrefixLength()
	 * @see #getPrefixLength()
	 * @generated
	 */
	void setPrefixLength(int value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.expression.StringMatch#getPrefixLength <em>Prefix Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPrefixLength()
	 * @see #getPrefixLength()
	 * @see #setPrefixLength(int)
	 * @generated
	 */
	void unsetPrefixLength();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.expression.StringMatch#getPrefixLength <em>Prefix Length</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Prefix Length</em>' attribute is set.
	 * @see #unsetPrefixLength()
	 * @see #getPrefixLength()
	 * @see #setPrefixLength(int)
	 * @generated
	 */
	boolean isSetPrefixLength();

	/**
	 * Returns the value of the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' containment reference.
	 * @see #setSource(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getStringMatch_Source()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getSource();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.StringMatch#getSource <em>Source</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' containment reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(Expression value);

	/**
	 * Returns the value of the '<em><b>Pattern</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The pattern value (literal or parameter). For LIKE the SQL wildcards % and _ apply; for the other kinds the value matches verbatim.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Pattern</em>' containment reference.
	 * @see #setPattern(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getStringMatch_Pattern()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getPattern();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.StringMatch#getPattern <em>Pattern</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pattern</em>' containment reference.
	 * @see #getPattern()
	 * @generated
	 */
	void setPattern(Expression value);

} // StringMatch
