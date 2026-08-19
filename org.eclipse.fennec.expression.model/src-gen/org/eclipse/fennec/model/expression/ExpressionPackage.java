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


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.fennec.emf.osgi.annotation.provide.EPackage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * The Fennec Expression Model — a backend-neutral, typed expression-tree IR for predicates over EMF features. Structurally informed by Essential OCL (the documented semantic reference per construct) but deliberately its own world: no type-level dependency on any OCL model, and a curated construct subset — what the model cannot express, no backend has to refuse. Explicit constructs (Comparison with an operator enum, StringMatch with a case-insensitivity flag, Exists/ForAll iterators, first-class ParameterRef, typed literals) instead of generic operation calls. See docs/unified-persistence/query-ir-redesign.md, decisions R1-R5.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.expression.ExpressionFactory
 * @model kind="package"
 *        annotation="Version value='1.0'"
 * @generated
 */
@ProviderType
@EPackage(uri = ExpressionPackage.eNS_URI, fingerprint = "fp1:b30e5cef5bba05b705bc4de08105cf5930d63a4433dc6dc9ce4f876f64e883a9", genModel = "/model/expression.genmodel", genModelSourceLocations = {"model/expression.genmodel","org.eclipse.fennec.expression.model/model/expression.genmodel"}, ecore = "/model/expression.ecore", ecoreSourceLocations = "/model/expression.ecore")
public interface ExpressionPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "expression";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/expression/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "expr";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ExpressionPackage eINSTANCE = org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.ExpressionImpl <em>Expression</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getExpression()
	 * @generated
	 */
	int EXPRESSION = 0;

	/**
	 * The number of structural features of the '<em>Expression</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPRESSION_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Expression</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPRESSION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.JunctionImpl <em>Junction</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.JunctionImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getJunction()
	 * @generated
	 */
	int JUNCTION = 1;

	/**
	 * The feature id for the '<em><b>Operands</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JUNCTION__OPERANDS = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Junction</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JUNCTION_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Junction</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JUNCTION_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.AndImpl <em>And</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.AndImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getAnd()
	 * @generated
	 */
	int AND = 2;

	/**
	 * The feature id for the '<em><b>Operands</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AND__OPERANDS = JUNCTION__OPERANDS;

	/**
	 * The number of structural features of the '<em>And</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AND_FEATURE_COUNT = JUNCTION_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>And</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AND_OPERATION_COUNT = JUNCTION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.OrImpl <em>Or</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.OrImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getOr()
	 * @generated
	 */
	int OR = 3;

	/**
	 * The feature id for the '<em><b>Operands</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OR__OPERANDS = JUNCTION__OPERANDS;

	/**
	 * The number of structural features of the '<em>Or</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OR_FEATURE_COUNT = JUNCTION_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Or</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OR_OPERATION_COUNT = JUNCTION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.NotImpl <em>Not</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.NotImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getNot()
	 * @generated
	 */
	int NOT = 4;

	/**
	 * The feature id for the '<em><b>Operand</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOT__OPERAND = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Not</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Not</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOT_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.ComparisonImpl <em>Comparison</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.ComparisonImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getComparison()
	 * @generated
	 */
	int COMPARISON = 5;

	/**
	 * The feature id for the '<em><b>Operator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPARISON__OPERATOR = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Left</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPARISON__LEFT = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Right</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPARISON__RIGHT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Comparison</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPARISON_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Comparison</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPARISON_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.IsNullImpl <em>Is Null</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.IsNullImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getIsNull()
	 * @generated
	 */
	int IS_NULL = 6;

	/**
	 * The feature id for the '<em><b>Negated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IS_NULL__NEGATED = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IS_NULL__SOURCE = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Is Null</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IS_NULL_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Is Null</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IS_NULL_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.BetweenImpl <em>Between</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.BetweenImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getBetween()
	 * @generated
	 */
	int BETWEEN = 7;

	/**
	 * The feature id for the '<em><b>Lower Included</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETWEEN__LOWER_INCLUDED = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Upper Included</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETWEEN__UPPER_INCLUDED = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETWEEN__SOURCE = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Lower</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETWEEN__LOWER = EXPRESSION_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Upper</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETWEEN__UPPER = EXPRESSION_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Between</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETWEEN_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 5;

	/**
	 * The number of operations of the '<em>Between</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETWEEN_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.InImpl <em>In</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.InImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getIn()
	 * @generated
	 */
	int IN = 8;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IN__SOURCE = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Values</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IN__VALUES = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>In</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IN_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>In</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IN_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.StringMatchImpl <em>String Match</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.StringMatchImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getStringMatch()
	 * @generated
	 */
	int STRING_MATCH = 9;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_MATCH__KIND = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Case Insensitive</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_MATCH__CASE_INSENSITIVE = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Max Edits</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_MATCH__MAX_EDITS = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Prefix Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_MATCH__PREFIX_LENGTH = EXPRESSION_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_MATCH__SOURCE = EXPRESSION_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Pattern</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_MATCH__PATTERN = EXPRESSION_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>String Match</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_MATCH_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 6;

	/**
	 * The number of operations of the '<em>String Match</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_MATCH_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.QuantifierImpl <em>Quantifier</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.QuantifierImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getQuantifier()
	 * @generated
	 */
	int QUANTIFIER = 10;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUANTIFIER__SOURCE = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Variable</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUANTIFIER__VARIABLE = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUANTIFIER__PREDICATE = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Quantifier</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUANTIFIER_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Quantifier</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUANTIFIER_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.ExistsImpl <em>Exists</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.ExistsImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getExists()
	 * @generated
	 */
	int EXISTS = 11;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXISTS__SOURCE = QUANTIFIER__SOURCE;

	/**
	 * The feature id for the '<em><b>Variable</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXISTS__VARIABLE = QUANTIFIER__VARIABLE;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXISTS__PREDICATE = QUANTIFIER__PREDICATE;

	/**
	 * The number of structural features of the '<em>Exists</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXISTS_FEATURE_COUNT = QUANTIFIER_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Exists</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXISTS_OPERATION_COUNT = QUANTIFIER_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.ForAllImpl <em>For All</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.ForAllImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getForAll()
	 * @generated
	 */
	int FOR_ALL = 12;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FOR_ALL__SOURCE = QUANTIFIER__SOURCE;

	/**
	 * The feature id for the '<em><b>Variable</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FOR_ALL__VARIABLE = QUANTIFIER__VARIABLE;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FOR_ALL__PREDICATE = QUANTIFIER__PREDICATE;

	/**
	 * The number of structural features of the '<em>For All</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FOR_ALL_FEATURE_COUNT = QUANTIFIER_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>For All</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FOR_ALL_OPERATION_COUNT = QUANTIFIER_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.PropertyPathImpl <em>Property Path</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.PropertyPathImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getPropertyPath()
	 * @generated
	 */
	int PROPERTY_PATH = 13;

	/**
	 * The feature id for the '<em><b>Segments</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_PATH__SEGMENTS = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Base</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_PATH__BASE = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Cast Base</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_PATH__CAST_BASE = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Property Path</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_PATH_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Property Path</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_PATH_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.VariableImpl <em>Variable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.VariableImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getVariable()
	 * @generated
	 */
	int VARIABLE = 14;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__NAME = 0;

	/**
	 * The number of structural features of the '<em>Variable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Variable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.VariableRefImpl <em>Variable Ref</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.VariableRefImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getVariableRef()
	 * @generated
	 */
	int VARIABLE_REF = 15;

	/**
	 * The feature id for the '<em><b>Variable</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE_REF__VARIABLE = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Variable Ref</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE_REF_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Variable Ref</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE_REF_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.AliasRefImpl <em>Alias Ref</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.AliasRefImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getAliasRef()
	 * @generated
	 */
	int ALIAS_REF = 16;

	/**
	 * The feature id for the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALIAS_REF__ALIAS = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Alias Ref</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALIAS_REF_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Alias Ref</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALIAS_REF_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.LiteralImpl <em>Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.LiteralImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getLiteral()
	 * @generated
	 */
	int LITERAL = 26;

	/**
	 * The number of structural features of the '<em>Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LITERAL_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LITERAL_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.GeoPointLiteralImpl <em>Geo Point Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.GeoPointLiteralImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoPointLiteral()
	 * @generated
	 */
	int GEO_POINT_LITERAL = 17;

	/**
	 * The feature id for the '<em><b>Lon</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_POINT_LITERAL__LON = LITERAL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Lat</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_POINT_LITERAL__LAT = LITERAL_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Geo Point Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_POINT_LITERAL_FEATURE_COUNT = LITERAL_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Geo Point Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_POINT_LITERAL_OPERATION_COUNT = LITERAL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.GeoSubjectImpl <em>Geo Subject</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.GeoSubjectImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoSubject()
	 * @generated
	 */
	int GEO_SUBJECT = 18;

	/**
	 * The feature id for the '<em><b>Path Lat</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_SUBJECT__PATH_LAT = 0;

	/**
	 * The feature id for the '<em><b>Path Lon</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_SUBJECT__PATH_LON = 1;

	/**
	 * The feature id for the '<em><b>Path Point</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_SUBJECT__PATH_POINT = 2;

	/**
	 * The number of structural features of the '<em>Geo Subject</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_SUBJECT_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Geo Subject</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_SUBJECT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.GeoShapeImpl <em>Geo Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.GeoShapeImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoShape()
	 * @generated
	 */
	int GEO_SHAPE = 19;

	/**
	 * The number of structural features of the '<em>Geo Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_SHAPE_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Geo Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_SHAPE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.GeoBoxImpl <em>Geo Box</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.GeoBoxImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoBox()
	 * @generated
	 */
	int GEO_BOX = 20;

	/**
	 * The feature id for the '<em><b>South West</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_BOX__SOUTH_WEST = GEO_SHAPE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>North East</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_BOX__NORTH_EAST = GEO_SHAPE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Geo Box</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_BOX_FEATURE_COUNT = GEO_SHAPE_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Geo Box</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_BOX_OPERATION_COUNT = GEO_SHAPE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.GeoPolygonImpl <em>Geo Polygon</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.GeoPolygonImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoPolygon()
	 * @generated
	 */
	int GEO_POLYGON = 21;

	/**
	 * The feature id for the '<em><b>Points</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_POLYGON__POINTS = GEO_SHAPE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Geo Polygon</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_POLYGON_FEATURE_COUNT = GEO_SHAPE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Geo Polygon</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_POLYGON_OPERATION_COUNT = GEO_SHAPE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.GeoWithinImpl <em>Geo Within</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.GeoWithinImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoWithin()
	 * @generated
	 */
	int GEO_WITHIN = 22;

	/**
	 * The feature id for the '<em><b>Subject</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_WITHIN__SUBJECT = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Shape</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_WITHIN__SHAPE = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Geo Within</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_WITHIN_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Geo Within</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_WITHIN_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.GeoDistanceImpl <em>Geo Distance</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.GeoDistanceImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoDistance()
	 * @generated
	 */
	int GEO_DISTANCE = 23;

	/**
	 * The feature id for the '<em><b>Subject</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_DISTANCE__SUBJECT = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Point</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_DISTANCE__POINT = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Geo Distance</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_DISTANCE_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Geo Distance</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GEO_DISTANCE_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.ScoreImpl <em>Score</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.ScoreImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getScore()
	 * @generated
	 */
	int SCORE = 24;

	/**
	 * The number of structural features of the '<em>Score</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCORE_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Score</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCORE_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.ParameterRefImpl <em>Parameter Ref</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.ParameterRefImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getParameterRef()
	 * @generated
	 */
	int PARAMETER_REF = 25;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_REF__NAME = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Parameter Ref</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_REF_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Parameter Ref</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_REF_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.StringLiteralImpl <em>String Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.StringLiteralImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getStringLiteral()
	 * @generated
	 */
	int STRING_LITERAL = 27;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_LITERAL__VALUE = LITERAL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>String Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_LITERAL_FEATURE_COUNT = LITERAL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>String Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_LITERAL_OPERATION_COUNT = LITERAL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.IntegerLiteralImpl <em>Integer Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.IntegerLiteralImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getIntegerLiteral()
	 * @generated
	 */
	int INTEGER_LITERAL = 28;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTEGER_LITERAL__VALUE = LITERAL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Integer Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTEGER_LITERAL_FEATURE_COUNT = LITERAL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Integer Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTEGER_LITERAL_OPERATION_COUNT = LITERAL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.RealLiteralImpl <em>Real Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.RealLiteralImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getRealLiteral()
	 * @generated
	 */
	int REAL_LITERAL = 29;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REAL_LITERAL__VALUE = LITERAL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Real Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REAL_LITERAL_FEATURE_COUNT = LITERAL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Real Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REAL_LITERAL_OPERATION_COUNT = LITERAL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.BooleanLiteralImpl <em>Boolean Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.BooleanLiteralImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getBooleanLiteral()
	 * @generated
	 */
	int BOOLEAN_LITERAL = 30;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_LITERAL__VALUE = LITERAL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Boolean Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_LITERAL_FEATURE_COUNT = LITERAL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Boolean Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_LITERAL_OPERATION_COUNT = LITERAL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.NullLiteralImpl <em>Null Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.NullLiteralImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getNullLiteral()
	 * @generated
	 */
	int NULL_LITERAL = 31;

	/**
	 * The number of structural features of the '<em>Null Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NULL_LITERAL_FEATURE_COUNT = LITERAL_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Null Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NULL_LITERAL_OPERATION_COUNT = LITERAL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.EnumLiteralImpl <em>Enum Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.EnumLiteralImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getEnumLiteral()
	 * @generated
	 */
	int ENUM_LITERAL = 32;

	/**
	 * The feature id for the '<em><b>Literal Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_LITERAL__LITERAL_NAME = LITERAL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Enum Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_LITERAL_FEATURE_COUNT = LITERAL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Enum Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_LITERAL_OPERATION_COUNT = LITERAL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.TemporalLiteralImpl <em>Temporal Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.TemporalLiteralImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getTemporalLiteral()
	 * @generated
	 */
	int TEMPORAL_LITERAL = 33;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEMPORAL_LITERAL__VALUE = LITERAL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEMPORAL_LITERAL__KIND = LITERAL_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Temporal Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEMPORAL_LITERAL_FEATURE_COUNT = LITERAL_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Temporal Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEMPORAL_LITERAL_OPERATION_COUNT = LITERAL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.GuidLiteralImpl <em>Guid Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.GuidLiteralImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGuidLiteral()
	 * @generated
	 */
	int GUID_LITERAL = 34;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUID_LITERAL__VALUE = LITERAL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Guid Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUID_LITERAL_FEATURE_COUNT = LITERAL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Guid Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUID_LITERAL_OPERATION_COUNT = LITERAL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.DurationLiteralImpl <em>Duration Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.DurationLiteralImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getDurationLiteral()
	 * @generated
	 */
	int DURATION_LITERAL = 35;

	/**
	 * The feature id for the '<em><b>Iso8601</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DURATION_LITERAL__ISO8601 = LITERAL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Duration Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DURATION_LITERAL_FEATURE_COUNT = LITERAL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Duration Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DURATION_LITERAL_OPERATION_COUNT = LITERAL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.StringFunctionImpl <em>String Function</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.StringFunctionImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getStringFunction()
	 * @generated
	 */
	int STRING_FUNCTION = 36;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_FUNCTION__KIND = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_FUNCTION__SOURCE = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>String Function</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_FUNCTION_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>String Function</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_FUNCTION_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.ArithmeticImpl <em>Arithmetic</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.ArithmeticImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getArithmetic()
	 * @generated
	 */
	int ARITHMETIC = 37;

	/**
	 * The feature id for the '<em><b>Operator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARITHMETIC__OPERATOR = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Left</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARITHMETIC__LEFT = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Right</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARITHMETIC__RIGHT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Arithmetic</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARITHMETIC_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Arithmetic</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARITHMETIC_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.NegateImpl <em>Negate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.NegateImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getNegate()
	 * @generated
	 */
	int NEGATE = 38;

	/**
	 * The feature id for the '<em><b>Operand</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEGATE__OPERAND = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Negate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEGATE_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Negate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEGATE_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.ConcatImpl <em>Concat</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.ConcatImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getConcat()
	 * @generated
	 */
	int CONCAT = 39;

	/**
	 * The feature id for the '<em><b>Parts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCAT__PARTS = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Concat</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCAT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Concat</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCAT_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.IndexOfImpl <em>Index Of</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.IndexOfImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getIndexOf()
	 * @generated
	 */
	int INDEX_OF = 40;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INDEX_OF__SOURCE = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Search</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INDEX_OF__SEARCH = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Index Of</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INDEX_OF_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Index Of</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INDEX_OF_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.MapValueImpl <em>Map Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.MapValueImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getMapValue()
	 * @generated
	 */
	int MAP_VALUE = 41;

	/**
	 * The feature id for the '<em><b>Map</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_VALUE__MAP = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_VALUE__KEY = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Map Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_VALUE_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Map Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_VALUE_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.CollectionCountImpl <em>Collection Count</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.CollectionCountImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getCollectionCount()
	 * @generated
	 */
	int COLLECTION_COUNT = 42;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_COUNT__SOURCE = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Variable</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_COUNT__VARIABLE = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_COUNT__PREDICATE = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Collection Count</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_COUNT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Collection Count</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_COUNT_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.TypeCheckImpl <em>Type Check</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.TypeCheckImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getTypeCheck()
	 * @generated
	 */
	int TYPE_CHECK = 43;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_CHECK__SOURCE = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_CHECK__TYPE = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Type Check</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_CHECK_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Type Check</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_CHECK_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.NumericFunctionImpl <em>Numeric Function</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.NumericFunctionImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getNumericFunction()
	 * @generated
	 */
	int NUMERIC_FUNCTION = 44;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_FUNCTION__KIND = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_FUNCTION__SOURCE = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Numeric Function</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_FUNCTION_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Numeric Function</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_FUNCTION_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.TemporalFunctionImpl <em>Temporal Function</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.TemporalFunctionImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getTemporalFunction()
	 * @generated
	 */
	int TEMPORAL_FUNCTION = 45;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEMPORAL_FUNCTION__KIND = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEMPORAL_FUNCTION__SOURCE = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Temporal Function</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEMPORAL_FUNCTION_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Temporal Function</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEMPORAL_FUNCTION_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.impl.SubstringImpl <em>Substring</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.impl.SubstringImpl
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getSubstring()
	 * @generated
	 */
	int SUBSTRING = 46;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBSTRING__SOURCE = EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Start</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBSTRING__START = EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Length</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBSTRING__LENGTH = EXPRESSION_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Substring</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBSTRING_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Substring</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBSTRING_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.ComparisonOperator <em>Comparison Operator</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.ComparisonOperator
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getComparisonOperator()
	 * @generated
	 */
	int COMPARISON_OPERATOR = 47;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.StringMatchKind <em>String Match Kind</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.StringMatchKind
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getStringMatchKind()
	 * @generated
	 */
	int STRING_MATCH_KIND = 48;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.StringFunctionKind <em>String Function Kind</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.StringFunctionKind
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getStringFunctionKind()
	 * @generated
	 */
	int STRING_FUNCTION_KIND = 49;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.TemporalFunctionKind <em>Temporal Function Kind</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.TemporalFunctionKind
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getTemporalFunctionKind()
	 * @generated
	 */
	int TEMPORAL_FUNCTION_KIND = 50;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.NumericFunctionKind <em>Numeric Function Kind</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.NumericFunctionKind
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getNumericFunctionKind()
	 * @generated
	 */
	int NUMERIC_FUNCTION_KIND = 51;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.ArithmeticOperator <em>Arithmetic Operator</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.ArithmeticOperator
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getArithmeticOperator()
	 * @generated
	 */
	int ARITHMETIC_OPERATOR = 52;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.expression.TemporalKind <em>Temporal Kind</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.expression.TemporalKind
	 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getTemporalKind()
	 * @generated
	 */
	int TEMPORAL_KIND = 53;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Expression <em>Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Expression</em>'.
	 * @see org.eclipse.fennec.model.expression.Expression
	 * @generated
	 */
	EClass getExpression();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Junction <em>Junction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Junction</em>'.
	 * @see org.eclipse.fennec.model.expression.Junction
	 * @generated
	 */
	EClass getJunction();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.expression.Junction#getOperands <em>Operands</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Operands</em>'.
	 * @see org.eclipse.fennec.model.expression.Junction#getOperands()
	 * @see #getJunction()
	 * @generated
	 */
	EReference getJunction_Operands();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.And <em>And</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>And</em>'.
	 * @see org.eclipse.fennec.model.expression.And
	 * @generated
	 */
	EClass getAnd();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Or <em>Or</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Or</em>'.
	 * @see org.eclipse.fennec.model.expression.Or
	 * @generated
	 */
	EClass getOr();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Not <em>Not</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Not</em>'.
	 * @see org.eclipse.fennec.model.expression.Not
	 * @generated
	 */
	EClass getNot();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Not#getOperand <em>Operand</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Operand</em>'.
	 * @see org.eclipse.fennec.model.expression.Not#getOperand()
	 * @see #getNot()
	 * @generated
	 */
	EReference getNot_Operand();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Comparison <em>Comparison</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Comparison</em>'.
	 * @see org.eclipse.fennec.model.expression.Comparison
	 * @generated
	 */
	EClass getComparison();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.Comparison#getOperator <em>Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operator</em>'.
	 * @see org.eclipse.fennec.model.expression.Comparison#getOperator()
	 * @see #getComparison()
	 * @generated
	 */
	EAttribute getComparison_Operator();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Comparison#getLeft <em>Left</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Left</em>'.
	 * @see org.eclipse.fennec.model.expression.Comparison#getLeft()
	 * @see #getComparison()
	 * @generated
	 */
	EReference getComparison_Left();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Comparison#getRight <em>Right</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Right</em>'.
	 * @see org.eclipse.fennec.model.expression.Comparison#getRight()
	 * @see #getComparison()
	 * @generated
	 */
	EReference getComparison_Right();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.IsNull <em>Is Null</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Is Null</em>'.
	 * @see org.eclipse.fennec.model.expression.IsNull
	 * @generated
	 */
	EClass getIsNull();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.IsNull#isNegated <em>Negated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Negated</em>'.
	 * @see org.eclipse.fennec.model.expression.IsNull#isNegated()
	 * @see #getIsNull()
	 * @generated
	 */
	EAttribute getIsNull_Negated();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.IsNull#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.IsNull#getSource()
	 * @see #getIsNull()
	 * @generated
	 */
	EReference getIsNull_Source();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Between <em>Between</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Between</em>'.
	 * @see org.eclipse.fennec.model.expression.Between
	 * @generated
	 */
	EClass getBetween();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.Between#isLowerIncluded <em>Lower Included</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lower Included</em>'.
	 * @see org.eclipse.fennec.model.expression.Between#isLowerIncluded()
	 * @see #getBetween()
	 * @generated
	 */
	EAttribute getBetween_LowerIncluded();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.Between#isUpperIncluded <em>Upper Included</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Upper Included</em>'.
	 * @see org.eclipse.fennec.model.expression.Between#isUpperIncluded()
	 * @see #getBetween()
	 * @generated
	 */
	EAttribute getBetween_UpperIncluded();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Between#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.Between#getSource()
	 * @see #getBetween()
	 * @generated
	 */
	EReference getBetween_Source();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Between#getLower <em>Lower</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Lower</em>'.
	 * @see org.eclipse.fennec.model.expression.Between#getLower()
	 * @see #getBetween()
	 * @generated
	 */
	EReference getBetween_Lower();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Between#getUpper <em>Upper</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Upper</em>'.
	 * @see org.eclipse.fennec.model.expression.Between#getUpper()
	 * @see #getBetween()
	 * @generated
	 */
	EReference getBetween_Upper();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.In <em>In</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>In</em>'.
	 * @see org.eclipse.fennec.model.expression.In
	 * @generated
	 */
	EClass getIn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.In#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.In#getSource()
	 * @see #getIn()
	 * @generated
	 */
	EReference getIn_Source();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.expression.In#getValues <em>Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Values</em>'.
	 * @see org.eclipse.fennec.model.expression.In#getValues()
	 * @see #getIn()
	 * @generated
	 */
	EReference getIn_Values();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.StringMatch <em>String Match</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String Match</em>'.
	 * @see org.eclipse.fennec.model.expression.StringMatch
	 * @generated
	 */
	EClass getStringMatch();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.StringMatch#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.fennec.model.expression.StringMatch#getKind()
	 * @see #getStringMatch()
	 * @generated
	 */
	EAttribute getStringMatch_Kind();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.StringMatch#isCaseInsensitive <em>Case Insensitive</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Case Insensitive</em>'.
	 * @see org.eclipse.fennec.model.expression.StringMatch#isCaseInsensitive()
	 * @see #getStringMatch()
	 * @generated
	 */
	EAttribute getStringMatch_CaseInsensitive();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.StringMatch#getMaxEdits <em>Max Edits</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Edits</em>'.
	 * @see org.eclipse.fennec.model.expression.StringMatch#getMaxEdits()
	 * @see #getStringMatch()
	 * @generated
	 */
	EAttribute getStringMatch_MaxEdits();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.StringMatch#getPrefixLength <em>Prefix Length</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Prefix Length</em>'.
	 * @see org.eclipse.fennec.model.expression.StringMatch#getPrefixLength()
	 * @see #getStringMatch()
	 * @generated
	 */
	EAttribute getStringMatch_PrefixLength();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.StringMatch#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.StringMatch#getSource()
	 * @see #getStringMatch()
	 * @generated
	 */
	EReference getStringMatch_Source();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.StringMatch#getPattern <em>Pattern</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Pattern</em>'.
	 * @see org.eclipse.fennec.model.expression.StringMatch#getPattern()
	 * @see #getStringMatch()
	 * @generated
	 */
	EReference getStringMatch_Pattern();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Quantifier <em>Quantifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Quantifier</em>'.
	 * @see org.eclipse.fennec.model.expression.Quantifier
	 * @generated
	 */
	EClass getQuantifier();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Quantifier#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.Quantifier#getSource()
	 * @see #getQuantifier()
	 * @generated
	 */
	EReference getQuantifier_Source();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Quantifier#getVariable <em>Variable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Variable</em>'.
	 * @see org.eclipse.fennec.model.expression.Quantifier#getVariable()
	 * @see #getQuantifier()
	 * @generated
	 */
	EReference getQuantifier_Variable();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Quantifier#getPredicate <em>Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Predicate</em>'.
	 * @see org.eclipse.fennec.model.expression.Quantifier#getPredicate()
	 * @see #getQuantifier()
	 * @generated
	 */
	EReference getQuantifier_Predicate();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Exists <em>Exists</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Exists</em>'.
	 * @see org.eclipse.fennec.model.expression.Exists
	 * @generated
	 */
	EClass getExists();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.ForAll <em>For All</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>For All</em>'.
	 * @see org.eclipse.fennec.model.expression.ForAll
	 * @generated
	 */
	EClass getForAll();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.PropertyPath <em>Property Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property Path</em>'.
	 * @see org.eclipse.fennec.model.expression.PropertyPath
	 * @generated
	 */
	EClass getPropertyPath();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.model.expression.PropertyPath#getSegments <em>Segments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Segments</em>'.
	 * @see org.eclipse.fennec.model.expression.PropertyPath#getSegments()
	 * @see #getPropertyPath()
	 * @generated
	 */
	EReference getPropertyPath_Segments();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.expression.PropertyPath#getBase <em>Base</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Base</em>'.
	 * @see org.eclipse.fennec.model.expression.PropertyPath#getBase()
	 * @see #getPropertyPath()
	 * @generated
	 */
	EReference getPropertyPath_Base();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.expression.PropertyPath#getCastBase <em>Cast Base</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Cast Base</em>'.
	 * @see org.eclipse.fennec.model.expression.PropertyPath#getCastBase()
	 * @see #getPropertyPath()
	 * @generated
	 */
	EReference getPropertyPath_CastBase();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Variable <em>Variable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Variable</em>'.
	 * @see org.eclipse.fennec.model.expression.Variable
	 * @generated
	 */
	EClass getVariable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.Variable#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.expression.Variable#getName()
	 * @see #getVariable()
	 * @generated
	 */
	EAttribute getVariable_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.VariableRef <em>Variable Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Variable Ref</em>'.
	 * @see org.eclipse.fennec.model.expression.VariableRef
	 * @generated
	 */
	EClass getVariableRef();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.expression.VariableRef#getVariable <em>Variable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Variable</em>'.
	 * @see org.eclipse.fennec.model.expression.VariableRef#getVariable()
	 * @see #getVariableRef()
	 * @generated
	 */
	EReference getVariableRef_Variable();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.AliasRef <em>Alias Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Alias Ref</em>'.
	 * @see org.eclipse.fennec.model.expression.AliasRef
	 * @generated
	 */
	EClass getAliasRef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.AliasRef#getAlias <em>Alias</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Alias</em>'.
	 * @see org.eclipse.fennec.model.expression.AliasRef#getAlias()
	 * @see #getAliasRef()
	 * @generated
	 */
	EAttribute getAliasRef_Alias();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.GeoPointLiteral <em>Geo Point Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Geo Point Literal</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoPointLiteral
	 * @generated
	 */
	EClass getGeoPointLiteral();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.GeoPointLiteral#getLon <em>Lon</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lon</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoPointLiteral#getLon()
	 * @see #getGeoPointLiteral()
	 * @generated
	 */
	EAttribute getGeoPointLiteral_Lon();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.GeoPointLiteral#getLat <em>Lat</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lat</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoPointLiteral#getLat()
	 * @see #getGeoPointLiteral()
	 * @generated
	 */
	EAttribute getGeoPointLiteral_Lat();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.GeoSubject <em>Geo Subject</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Geo Subject</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoSubject
	 * @generated
	 */
	EClass getGeoSubject();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.GeoSubject#getPathLat <em>Path Lat</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Path Lat</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoSubject#getPathLat()
	 * @see #getGeoSubject()
	 * @generated
	 */
	EReference getGeoSubject_PathLat();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.GeoSubject#getPathLon <em>Path Lon</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Path Lon</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoSubject#getPathLon()
	 * @see #getGeoSubject()
	 * @generated
	 */
	EReference getGeoSubject_PathLon();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.GeoSubject#getPathPoint <em>Path Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Path Point</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoSubject#getPathPoint()
	 * @see #getGeoSubject()
	 * @generated
	 */
	EReference getGeoSubject_PathPoint();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.GeoShape <em>Geo Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Geo Shape</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoShape
	 * @generated
	 */
	EClass getGeoShape();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.GeoBox <em>Geo Box</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Geo Box</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoBox
	 * @generated
	 */
	EClass getGeoBox();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.GeoBox#getSouthWest <em>South West</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>South West</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoBox#getSouthWest()
	 * @see #getGeoBox()
	 * @generated
	 */
	EReference getGeoBox_SouthWest();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.GeoBox#getNorthEast <em>North East</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>North East</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoBox#getNorthEast()
	 * @see #getGeoBox()
	 * @generated
	 */
	EReference getGeoBox_NorthEast();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.GeoPolygon <em>Geo Polygon</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Geo Polygon</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoPolygon
	 * @generated
	 */
	EClass getGeoPolygon();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.expression.GeoPolygon#getPoints <em>Points</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Points</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoPolygon#getPoints()
	 * @see #getGeoPolygon()
	 * @generated
	 */
	EReference getGeoPolygon_Points();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.GeoWithin <em>Geo Within</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Geo Within</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoWithin
	 * @generated
	 */
	EClass getGeoWithin();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.GeoWithin#getSubject <em>Subject</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Subject</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoWithin#getSubject()
	 * @see #getGeoWithin()
	 * @generated
	 */
	EReference getGeoWithin_Subject();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.GeoWithin#getShape <em>Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Shape</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoWithin#getShape()
	 * @see #getGeoWithin()
	 * @generated
	 */
	EReference getGeoWithin_Shape();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.GeoDistance <em>Geo Distance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Geo Distance</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoDistance
	 * @generated
	 */
	EClass getGeoDistance();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.GeoDistance#getSubject <em>Subject</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Subject</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoDistance#getSubject()
	 * @see #getGeoDistance()
	 * @generated
	 */
	EReference getGeoDistance_Subject();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.GeoDistance#getPoint <em>Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Point</em>'.
	 * @see org.eclipse.fennec.model.expression.GeoDistance#getPoint()
	 * @see #getGeoDistance()
	 * @generated
	 */
	EReference getGeoDistance_Point();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Score <em>Score</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Score</em>'.
	 * @see org.eclipse.fennec.model.expression.Score
	 * @generated
	 */
	EClass getScore();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.ParameterRef <em>Parameter Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter Ref</em>'.
	 * @see org.eclipse.fennec.model.expression.ParameterRef
	 * @generated
	 */
	EClass getParameterRef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.ParameterRef#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.expression.ParameterRef#getName()
	 * @see #getParameterRef()
	 * @generated
	 */
	EAttribute getParameterRef_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Literal <em>Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Literal</em>'.
	 * @see org.eclipse.fennec.model.expression.Literal
	 * @generated
	 */
	EClass getLiteral();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.StringLiteral <em>String Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String Literal</em>'.
	 * @see org.eclipse.fennec.model.expression.StringLiteral
	 * @generated
	 */
	EClass getStringLiteral();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.StringLiteral#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.model.expression.StringLiteral#getValue()
	 * @see #getStringLiteral()
	 * @generated
	 */
	EAttribute getStringLiteral_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.IntegerLiteral <em>Integer Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Integer Literal</em>'.
	 * @see org.eclipse.fennec.model.expression.IntegerLiteral
	 * @generated
	 */
	EClass getIntegerLiteral();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.IntegerLiteral#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.model.expression.IntegerLiteral#getValue()
	 * @see #getIntegerLiteral()
	 * @generated
	 */
	EAttribute getIntegerLiteral_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.RealLiteral <em>Real Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Real Literal</em>'.
	 * @see org.eclipse.fennec.model.expression.RealLiteral
	 * @generated
	 */
	EClass getRealLiteral();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.RealLiteral#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.model.expression.RealLiteral#getValue()
	 * @see #getRealLiteral()
	 * @generated
	 */
	EAttribute getRealLiteral_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.BooleanLiteral <em>Boolean Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Boolean Literal</em>'.
	 * @see org.eclipse.fennec.model.expression.BooleanLiteral
	 * @generated
	 */
	EClass getBooleanLiteral();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.BooleanLiteral#isValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.model.expression.BooleanLiteral#isValue()
	 * @see #getBooleanLiteral()
	 * @generated
	 */
	EAttribute getBooleanLiteral_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.NullLiteral <em>Null Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Null Literal</em>'.
	 * @see org.eclipse.fennec.model.expression.NullLiteral
	 * @generated
	 */
	EClass getNullLiteral();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.EnumLiteral <em>Enum Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Enum Literal</em>'.
	 * @see org.eclipse.fennec.model.expression.EnumLiteral
	 * @generated
	 */
	EClass getEnumLiteral();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.EnumLiteral#getLiteralName <em>Literal Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Literal Name</em>'.
	 * @see org.eclipse.fennec.model.expression.EnumLiteral#getLiteralName()
	 * @see #getEnumLiteral()
	 * @generated
	 */
	EAttribute getEnumLiteral_LiteralName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.TemporalLiteral <em>Temporal Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Temporal Literal</em>'.
	 * @see org.eclipse.fennec.model.expression.TemporalLiteral
	 * @generated
	 */
	EClass getTemporalLiteral();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.TemporalLiteral#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.model.expression.TemporalLiteral#getValue()
	 * @see #getTemporalLiteral()
	 * @generated
	 */
	EAttribute getTemporalLiteral_Value();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.TemporalLiteral#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.fennec.model.expression.TemporalLiteral#getKind()
	 * @see #getTemporalLiteral()
	 * @generated
	 */
	EAttribute getTemporalLiteral_Kind();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.GuidLiteral <em>Guid Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Guid Literal</em>'.
	 * @see org.eclipse.fennec.model.expression.GuidLiteral
	 * @generated
	 */
	EClass getGuidLiteral();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.GuidLiteral#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.model.expression.GuidLiteral#getValue()
	 * @see #getGuidLiteral()
	 * @generated
	 */
	EAttribute getGuidLiteral_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.DurationLiteral <em>Duration Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Duration Literal</em>'.
	 * @see org.eclipse.fennec.model.expression.DurationLiteral
	 * @generated
	 */
	EClass getDurationLiteral();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.DurationLiteral#getIso8601 <em>Iso8601</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Iso8601</em>'.
	 * @see org.eclipse.fennec.model.expression.DurationLiteral#getIso8601()
	 * @see #getDurationLiteral()
	 * @generated
	 */
	EAttribute getDurationLiteral_Iso8601();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.StringFunction <em>String Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String Function</em>'.
	 * @see org.eclipse.fennec.model.expression.StringFunction
	 * @generated
	 */
	EClass getStringFunction();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.StringFunction#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.fennec.model.expression.StringFunction#getKind()
	 * @see #getStringFunction()
	 * @generated
	 */
	EAttribute getStringFunction_Kind();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.StringFunction#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.StringFunction#getSource()
	 * @see #getStringFunction()
	 * @generated
	 */
	EReference getStringFunction_Source();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Arithmetic <em>Arithmetic</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Arithmetic</em>'.
	 * @see org.eclipse.fennec.model.expression.Arithmetic
	 * @generated
	 */
	EClass getArithmetic();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.Arithmetic#getOperator <em>Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operator</em>'.
	 * @see org.eclipse.fennec.model.expression.Arithmetic#getOperator()
	 * @see #getArithmetic()
	 * @generated
	 */
	EAttribute getArithmetic_Operator();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Arithmetic#getLeft <em>Left</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Left</em>'.
	 * @see org.eclipse.fennec.model.expression.Arithmetic#getLeft()
	 * @see #getArithmetic()
	 * @generated
	 */
	EReference getArithmetic_Left();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Arithmetic#getRight <em>Right</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Right</em>'.
	 * @see org.eclipse.fennec.model.expression.Arithmetic#getRight()
	 * @see #getArithmetic()
	 * @generated
	 */
	EReference getArithmetic_Right();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Negate <em>Negate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Negate</em>'.
	 * @see org.eclipse.fennec.model.expression.Negate
	 * @generated
	 */
	EClass getNegate();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Negate#getOperand <em>Operand</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Operand</em>'.
	 * @see org.eclipse.fennec.model.expression.Negate#getOperand()
	 * @see #getNegate()
	 * @generated
	 */
	EReference getNegate_Operand();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Concat <em>Concat</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Concat</em>'.
	 * @see org.eclipse.fennec.model.expression.Concat
	 * @generated
	 */
	EClass getConcat();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.expression.Concat#getParts <em>Parts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parts</em>'.
	 * @see org.eclipse.fennec.model.expression.Concat#getParts()
	 * @see #getConcat()
	 * @generated
	 */
	EReference getConcat_Parts();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.IndexOf <em>Index Of</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Index Of</em>'.
	 * @see org.eclipse.fennec.model.expression.IndexOf
	 * @generated
	 */
	EClass getIndexOf();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.IndexOf#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.IndexOf#getSource()
	 * @see #getIndexOf()
	 * @generated
	 */
	EReference getIndexOf_Source();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.IndexOf#getSearch <em>Search</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Search</em>'.
	 * @see org.eclipse.fennec.model.expression.IndexOf#getSearch()
	 * @see #getIndexOf()
	 * @generated
	 */
	EReference getIndexOf_Search();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.MapValue <em>Map Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Map Value</em>'.
	 * @see org.eclipse.fennec.model.expression.MapValue
	 * @generated
	 */
	EClass getMapValue();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.MapValue#getMap <em>Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map</em>'.
	 * @see org.eclipse.fennec.model.expression.MapValue#getMap()
	 * @see #getMapValue()
	 * @generated
	 */
	EReference getMapValue_Map();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.MapValue#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Key</em>'.
	 * @see org.eclipse.fennec.model.expression.MapValue#getKey()
	 * @see #getMapValue()
	 * @generated
	 */
	EReference getMapValue_Key();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.CollectionCount <em>Collection Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Collection Count</em>'.
	 * @see org.eclipse.fennec.model.expression.CollectionCount
	 * @generated
	 */
	EClass getCollectionCount();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.CollectionCount#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.CollectionCount#getSource()
	 * @see #getCollectionCount()
	 * @generated
	 */
	EReference getCollectionCount_Source();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.CollectionCount#getVariable <em>Variable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Variable</em>'.
	 * @see org.eclipse.fennec.model.expression.CollectionCount#getVariable()
	 * @see #getCollectionCount()
	 * @generated
	 */
	EReference getCollectionCount_Variable();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.CollectionCount#getPredicate <em>Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Predicate</em>'.
	 * @see org.eclipse.fennec.model.expression.CollectionCount#getPredicate()
	 * @see #getCollectionCount()
	 * @generated
	 */
	EReference getCollectionCount_Predicate();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.TypeCheck <em>Type Check</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Type Check</em>'.
	 * @see org.eclipse.fennec.model.expression.TypeCheck
	 * @generated
	 */
	EClass getTypeCheck();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.TypeCheck#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.TypeCheck#getSource()
	 * @see #getTypeCheck()
	 * @generated
	 */
	EReference getTypeCheck_Source();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.expression.TypeCheck#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Type</em>'.
	 * @see org.eclipse.fennec.model.expression.TypeCheck#getType()
	 * @see #getTypeCheck()
	 * @generated
	 */
	EReference getTypeCheck_Type();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.NumericFunction <em>Numeric Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Numeric Function</em>'.
	 * @see org.eclipse.fennec.model.expression.NumericFunction
	 * @generated
	 */
	EClass getNumericFunction();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.NumericFunction#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.fennec.model.expression.NumericFunction#getKind()
	 * @see #getNumericFunction()
	 * @generated
	 */
	EAttribute getNumericFunction_Kind();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.NumericFunction#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.NumericFunction#getSource()
	 * @see #getNumericFunction()
	 * @generated
	 */
	EReference getNumericFunction_Source();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.TemporalFunction <em>Temporal Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Temporal Function</em>'.
	 * @see org.eclipse.fennec.model.expression.TemporalFunction
	 * @generated
	 */
	EClass getTemporalFunction();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.expression.TemporalFunction#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.fennec.model.expression.TemporalFunction#getKind()
	 * @see #getTemporalFunction()
	 * @generated
	 */
	EAttribute getTemporalFunction_Kind();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.TemporalFunction#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.TemporalFunction#getSource()
	 * @see #getTemporalFunction()
	 * @generated
	 */
	EReference getTemporalFunction_Source();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.expression.Substring <em>Substring</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Substring</em>'.
	 * @see org.eclipse.fennec.model.expression.Substring
	 * @generated
	 */
	EClass getSubstring();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Substring#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.fennec.model.expression.Substring#getSource()
	 * @see #getSubstring()
	 * @generated
	 */
	EReference getSubstring_Source();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Substring#getStart <em>Start</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Start</em>'.
	 * @see org.eclipse.fennec.model.expression.Substring#getStart()
	 * @see #getSubstring()
	 * @generated
	 */
	EReference getSubstring_Start();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.expression.Substring#getLength <em>Length</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Length</em>'.
	 * @see org.eclipse.fennec.model.expression.Substring#getLength()
	 * @see #getSubstring()
	 * @generated
	 */
	EReference getSubstring_Length();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.expression.ComparisonOperator <em>Comparison Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Comparison Operator</em>'.
	 * @see org.eclipse.fennec.model.expression.ComparisonOperator
	 * @generated
	 */
	EEnum getComparisonOperator();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.expression.StringMatchKind <em>String Match Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>String Match Kind</em>'.
	 * @see org.eclipse.fennec.model.expression.StringMatchKind
	 * @generated
	 */
	EEnum getStringMatchKind();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.expression.StringFunctionKind <em>String Function Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>String Function Kind</em>'.
	 * @see org.eclipse.fennec.model.expression.StringFunctionKind
	 * @generated
	 */
	EEnum getStringFunctionKind();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.expression.TemporalFunctionKind <em>Temporal Function Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Temporal Function Kind</em>'.
	 * @see org.eclipse.fennec.model.expression.TemporalFunctionKind
	 * @generated
	 */
	EEnum getTemporalFunctionKind();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.expression.NumericFunctionKind <em>Numeric Function Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Numeric Function Kind</em>'.
	 * @see org.eclipse.fennec.model.expression.NumericFunctionKind
	 * @generated
	 */
	EEnum getNumericFunctionKind();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.expression.ArithmeticOperator <em>Arithmetic Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Arithmetic Operator</em>'.
	 * @see org.eclipse.fennec.model.expression.ArithmeticOperator
	 * @generated
	 */
	EEnum getArithmeticOperator();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.expression.TemporalKind <em>Temporal Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Temporal Kind</em>'.
	 * @see org.eclipse.fennec.model.expression.TemporalKind
	 * @generated
	 */
	EEnum getTemporalKind();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ExpressionFactory getExpressionFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.ExpressionImpl <em>Expression</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getExpression()
		 * @generated
		 */
		EClass EXPRESSION = eINSTANCE.getExpression();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.JunctionImpl <em>Junction</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.JunctionImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getJunction()
		 * @generated
		 */
		EClass JUNCTION = eINSTANCE.getJunction();

		/**
		 * The meta object literal for the '<em><b>Operands</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference JUNCTION__OPERANDS = eINSTANCE.getJunction_Operands();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.AndImpl <em>And</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.AndImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getAnd()
		 * @generated
		 */
		EClass AND = eINSTANCE.getAnd();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.OrImpl <em>Or</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.OrImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getOr()
		 * @generated
		 */
		EClass OR = eINSTANCE.getOr();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.NotImpl <em>Not</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.NotImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getNot()
		 * @generated
		 */
		EClass NOT = eINSTANCE.getNot();

		/**
		 * The meta object literal for the '<em><b>Operand</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NOT__OPERAND = eINSTANCE.getNot_Operand();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.ComparisonImpl <em>Comparison</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.ComparisonImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getComparison()
		 * @generated
		 */
		EClass COMPARISON = eINSTANCE.getComparison();

		/**
		 * The meta object literal for the '<em><b>Operator</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPARISON__OPERATOR = eINSTANCE.getComparison_Operator();

		/**
		 * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPARISON__LEFT = eINSTANCE.getComparison_Left();

		/**
		 * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPARISON__RIGHT = eINSTANCE.getComparison_Right();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.IsNullImpl <em>Is Null</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.IsNullImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getIsNull()
		 * @generated
		 */
		EClass IS_NULL = eINSTANCE.getIsNull();

		/**
		 * The meta object literal for the '<em><b>Negated</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IS_NULL__NEGATED = eINSTANCE.getIsNull_Negated();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference IS_NULL__SOURCE = eINSTANCE.getIsNull_Source();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.BetweenImpl <em>Between</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.BetweenImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getBetween()
		 * @generated
		 */
		EClass BETWEEN = eINSTANCE.getBetween();

		/**
		 * The meta object literal for the '<em><b>Lower Included</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BETWEEN__LOWER_INCLUDED = eINSTANCE.getBetween_LowerIncluded();

		/**
		 * The meta object literal for the '<em><b>Upper Included</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BETWEEN__UPPER_INCLUDED = eINSTANCE.getBetween_UpperIncluded();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BETWEEN__SOURCE = eINSTANCE.getBetween_Source();

		/**
		 * The meta object literal for the '<em><b>Lower</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BETWEEN__LOWER = eINSTANCE.getBetween_Lower();

		/**
		 * The meta object literal for the '<em><b>Upper</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BETWEEN__UPPER = eINSTANCE.getBetween_Upper();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.InImpl <em>In</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.InImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getIn()
		 * @generated
		 */
		EClass IN = eINSTANCE.getIn();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference IN__SOURCE = eINSTANCE.getIn_Source();

		/**
		 * The meta object literal for the '<em><b>Values</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference IN__VALUES = eINSTANCE.getIn_Values();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.StringMatchImpl <em>String Match</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.StringMatchImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getStringMatch()
		 * @generated
		 */
		EClass STRING_MATCH = eINSTANCE.getStringMatch();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_MATCH__KIND = eINSTANCE.getStringMatch_Kind();

		/**
		 * The meta object literal for the '<em><b>Case Insensitive</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_MATCH__CASE_INSENSITIVE = eINSTANCE.getStringMatch_CaseInsensitive();

		/**
		 * The meta object literal for the '<em><b>Max Edits</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_MATCH__MAX_EDITS = eINSTANCE.getStringMatch_MaxEdits();

		/**
		 * The meta object literal for the '<em><b>Prefix Length</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_MATCH__PREFIX_LENGTH = eINSTANCE.getStringMatch_PrefixLength();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference STRING_MATCH__SOURCE = eINSTANCE.getStringMatch_Source();

		/**
		 * The meta object literal for the '<em><b>Pattern</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference STRING_MATCH__PATTERN = eINSTANCE.getStringMatch_Pattern();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.QuantifierImpl <em>Quantifier</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.QuantifierImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getQuantifier()
		 * @generated
		 */
		EClass QUANTIFIER = eINSTANCE.getQuantifier();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUANTIFIER__SOURCE = eINSTANCE.getQuantifier_Source();

		/**
		 * The meta object literal for the '<em><b>Variable</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUANTIFIER__VARIABLE = eINSTANCE.getQuantifier_Variable();

		/**
		 * The meta object literal for the '<em><b>Predicate</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUANTIFIER__PREDICATE = eINSTANCE.getQuantifier_Predicate();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.ExistsImpl <em>Exists</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.ExistsImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getExists()
		 * @generated
		 */
		EClass EXISTS = eINSTANCE.getExists();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.ForAllImpl <em>For All</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.ForAllImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getForAll()
		 * @generated
		 */
		EClass FOR_ALL = eINSTANCE.getForAll();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.PropertyPathImpl <em>Property Path</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.PropertyPathImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getPropertyPath()
		 * @generated
		 */
		EClass PROPERTY_PATH = eINSTANCE.getPropertyPath();

		/**
		 * The meta object literal for the '<em><b>Segments</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROPERTY_PATH__SEGMENTS = eINSTANCE.getPropertyPath_Segments();

		/**
		 * The meta object literal for the '<em><b>Base</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROPERTY_PATH__BASE = eINSTANCE.getPropertyPath_Base();

		/**
		 * The meta object literal for the '<em><b>Cast Base</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROPERTY_PATH__CAST_BASE = eINSTANCE.getPropertyPath_CastBase();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.VariableImpl <em>Variable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.VariableImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getVariable()
		 * @generated
		 */
		EClass VARIABLE = eINSTANCE.getVariable();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VARIABLE__NAME = eINSTANCE.getVariable_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.VariableRefImpl <em>Variable Ref</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.VariableRefImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getVariableRef()
		 * @generated
		 */
		EClass VARIABLE_REF = eINSTANCE.getVariableRef();

		/**
		 * The meta object literal for the '<em><b>Variable</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIABLE_REF__VARIABLE = eINSTANCE.getVariableRef_Variable();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.AliasRefImpl <em>Alias Ref</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.AliasRefImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getAliasRef()
		 * @generated
		 */
		EClass ALIAS_REF = eINSTANCE.getAliasRef();

		/**
		 * The meta object literal for the '<em><b>Alias</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ALIAS_REF__ALIAS = eINSTANCE.getAliasRef_Alias();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.GeoPointLiteralImpl <em>Geo Point Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.GeoPointLiteralImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoPointLiteral()
		 * @generated
		 */
		EClass GEO_POINT_LITERAL = eINSTANCE.getGeoPointLiteral();

		/**
		 * The meta object literal for the '<em><b>Lon</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GEO_POINT_LITERAL__LON = eINSTANCE.getGeoPointLiteral_Lon();

		/**
		 * The meta object literal for the '<em><b>Lat</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GEO_POINT_LITERAL__LAT = eINSTANCE.getGeoPointLiteral_Lat();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.GeoSubjectImpl <em>Geo Subject</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.GeoSubjectImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoSubject()
		 * @generated
		 */
		EClass GEO_SUBJECT = eINSTANCE.getGeoSubject();

		/**
		 * The meta object literal for the '<em><b>Path Lat</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GEO_SUBJECT__PATH_LAT = eINSTANCE.getGeoSubject_PathLat();

		/**
		 * The meta object literal for the '<em><b>Path Lon</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GEO_SUBJECT__PATH_LON = eINSTANCE.getGeoSubject_PathLon();

		/**
		 * The meta object literal for the '<em><b>Path Point</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GEO_SUBJECT__PATH_POINT = eINSTANCE.getGeoSubject_PathPoint();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.GeoShapeImpl <em>Geo Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.GeoShapeImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoShape()
		 * @generated
		 */
		EClass GEO_SHAPE = eINSTANCE.getGeoShape();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.GeoBoxImpl <em>Geo Box</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.GeoBoxImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoBox()
		 * @generated
		 */
		EClass GEO_BOX = eINSTANCE.getGeoBox();

		/**
		 * The meta object literal for the '<em><b>South West</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GEO_BOX__SOUTH_WEST = eINSTANCE.getGeoBox_SouthWest();

		/**
		 * The meta object literal for the '<em><b>North East</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GEO_BOX__NORTH_EAST = eINSTANCE.getGeoBox_NorthEast();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.GeoPolygonImpl <em>Geo Polygon</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.GeoPolygonImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoPolygon()
		 * @generated
		 */
		EClass GEO_POLYGON = eINSTANCE.getGeoPolygon();

		/**
		 * The meta object literal for the '<em><b>Points</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GEO_POLYGON__POINTS = eINSTANCE.getGeoPolygon_Points();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.GeoWithinImpl <em>Geo Within</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.GeoWithinImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoWithin()
		 * @generated
		 */
		EClass GEO_WITHIN = eINSTANCE.getGeoWithin();

		/**
		 * The meta object literal for the '<em><b>Subject</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GEO_WITHIN__SUBJECT = eINSTANCE.getGeoWithin_Subject();

		/**
		 * The meta object literal for the '<em><b>Shape</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GEO_WITHIN__SHAPE = eINSTANCE.getGeoWithin_Shape();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.GeoDistanceImpl <em>Geo Distance</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.GeoDistanceImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGeoDistance()
		 * @generated
		 */
		EClass GEO_DISTANCE = eINSTANCE.getGeoDistance();

		/**
		 * The meta object literal for the '<em><b>Subject</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GEO_DISTANCE__SUBJECT = eINSTANCE.getGeoDistance_Subject();

		/**
		 * The meta object literal for the '<em><b>Point</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GEO_DISTANCE__POINT = eINSTANCE.getGeoDistance_Point();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.ScoreImpl <em>Score</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.ScoreImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getScore()
		 * @generated
		 */
		EClass SCORE = eINSTANCE.getScore();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.ParameterRefImpl <em>Parameter Ref</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.ParameterRefImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getParameterRef()
		 * @generated
		 */
		EClass PARAMETER_REF = eINSTANCE.getParameterRef();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_REF__NAME = eINSTANCE.getParameterRef_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.LiteralImpl <em>Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.LiteralImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getLiteral()
		 * @generated
		 */
		EClass LITERAL = eINSTANCE.getLiteral();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.StringLiteralImpl <em>String Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.StringLiteralImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getStringLiteral()
		 * @generated
		 */
		EClass STRING_LITERAL = eINSTANCE.getStringLiteral();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_LITERAL__VALUE = eINSTANCE.getStringLiteral_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.IntegerLiteralImpl <em>Integer Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.IntegerLiteralImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getIntegerLiteral()
		 * @generated
		 */
		EClass INTEGER_LITERAL = eINSTANCE.getIntegerLiteral();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTEGER_LITERAL__VALUE = eINSTANCE.getIntegerLiteral_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.RealLiteralImpl <em>Real Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.RealLiteralImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getRealLiteral()
		 * @generated
		 */
		EClass REAL_LITERAL = eINSTANCE.getRealLiteral();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REAL_LITERAL__VALUE = eINSTANCE.getRealLiteral_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.BooleanLiteralImpl <em>Boolean Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.BooleanLiteralImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getBooleanLiteral()
		 * @generated
		 */
		EClass BOOLEAN_LITERAL = eINSTANCE.getBooleanLiteral();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BOOLEAN_LITERAL__VALUE = eINSTANCE.getBooleanLiteral_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.NullLiteralImpl <em>Null Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.NullLiteralImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getNullLiteral()
		 * @generated
		 */
		EClass NULL_LITERAL = eINSTANCE.getNullLiteral();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.EnumLiteralImpl <em>Enum Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.EnumLiteralImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getEnumLiteral()
		 * @generated
		 */
		EClass ENUM_LITERAL = eINSTANCE.getEnumLiteral();

		/**
		 * The meta object literal for the '<em><b>Literal Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENUM_LITERAL__LITERAL_NAME = eINSTANCE.getEnumLiteral_LiteralName();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.TemporalLiteralImpl <em>Temporal Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.TemporalLiteralImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getTemporalLiteral()
		 * @generated
		 */
		EClass TEMPORAL_LITERAL = eINSTANCE.getTemporalLiteral();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TEMPORAL_LITERAL__VALUE = eINSTANCE.getTemporalLiteral_Value();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TEMPORAL_LITERAL__KIND = eINSTANCE.getTemporalLiteral_Kind();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.GuidLiteralImpl <em>Guid Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.GuidLiteralImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getGuidLiteral()
		 * @generated
		 */
		EClass GUID_LITERAL = eINSTANCE.getGuidLiteral();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GUID_LITERAL__VALUE = eINSTANCE.getGuidLiteral_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.DurationLiteralImpl <em>Duration Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.DurationLiteralImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getDurationLiteral()
		 * @generated
		 */
		EClass DURATION_LITERAL = eINSTANCE.getDurationLiteral();

		/**
		 * The meta object literal for the '<em><b>Iso8601</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DURATION_LITERAL__ISO8601 = eINSTANCE.getDurationLiteral_Iso8601();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.StringFunctionImpl <em>String Function</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.StringFunctionImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getStringFunction()
		 * @generated
		 */
		EClass STRING_FUNCTION = eINSTANCE.getStringFunction();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_FUNCTION__KIND = eINSTANCE.getStringFunction_Kind();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference STRING_FUNCTION__SOURCE = eINSTANCE.getStringFunction_Source();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.ArithmeticImpl <em>Arithmetic</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.ArithmeticImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getArithmetic()
		 * @generated
		 */
		EClass ARITHMETIC = eINSTANCE.getArithmetic();

		/**
		 * The meta object literal for the '<em><b>Operator</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARITHMETIC__OPERATOR = eINSTANCE.getArithmetic_Operator();

		/**
		 * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARITHMETIC__LEFT = eINSTANCE.getArithmetic_Left();

		/**
		 * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARITHMETIC__RIGHT = eINSTANCE.getArithmetic_Right();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.NegateImpl <em>Negate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.NegateImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getNegate()
		 * @generated
		 */
		EClass NEGATE = eINSTANCE.getNegate();

		/**
		 * The meta object literal for the '<em><b>Operand</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NEGATE__OPERAND = eINSTANCE.getNegate_Operand();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.ConcatImpl <em>Concat</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.ConcatImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getConcat()
		 * @generated
		 */
		EClass CONCAT = eINSTANCE.getConcat();

		/**
		 * The meta object literal for the '<em><b>Parts</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONCAT__PARTS = eINSTANCE.getConcat_Parts();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.IndexOfImpl <em>Index Of</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.IndexOfImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getIndexOf()
		 * @generated
		 */
		EClass INDEX_OF = eINSTANCE.getIndexOf();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INDEX_OF__SOURCE = eINSTANCE.getIndexOf_Source();

		/**
		 * The meta object literal for the '<em><b>Search</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INDEX_OF__SEARCH = eINSTANCE.getIndexOf_Search();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.MapValueImpl <em>Map Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.MapValueImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getMapValue()
		 * @generated
		 */
		EClass MAP_VALUE = eINSTANCE.getMapValue();

		/**
		 * The meta object literal for the '<em><b>Map</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MAP_VALUE__MAP = eINSTANCE.getMapValue_Map();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MAP_VALUE__KEY = eINSTANCE.getMapValue_Key();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.CollectionCountImpl <em>Collection Count</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.CollectionCountImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getCollectionCount()
		 * @generated
		 */
		EClass COLLECTION_COUNT = eINSTANCE.getCollectionCount();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COLLECTION_COUNT__SOURCE = eINSTANCE.getCollectionCount_Source();

		/**
		 * The meta object literal for the '<em><b>Variable</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COLLECTION_COUNT__VARIABLE = eINSTANCE.getCollectionCount_Variable();

		/**
		 * The meta object literal for the '<em><b>Predicate</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COLLECTION_COUNT__PREDICATE = eINSTANCE.getCollectionCount_Predicate();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.TypeCheckImpl <em>Type Check</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.TypeCheckImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getTypeCheck()
		 * @generated
		 */
		EClass TYPE_CHECK = eINSTANCE.getTypeCheck();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TYPE_CHECK__SOURCE = eINSTANCE.getTypeCheck_Source();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TYPE_CHECK__TYPE = eINSTANCE.getTypeCheck_Type();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.NumericFunctionImpl <em>Numeric Function</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.NumericFunctionImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getNumericFunction()
		 * @generated
		 */
		EClass NUMERIC_FUNCTION = eINSTANCE.getNumericFunction();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NUMERIC_FUNCTION__KIND = eINSTANCE.getNumericFunction_Kind();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NUMERIC_FUNCTION__SOURCE = eINSTANCE.getNumericFunction_Source();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.TemporalFunctionImpl <em>Temporal Function</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.TemporalFunctionImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getTemporalFunction()
		 * @generated
		 */
		EClass TEMPORAL_FUNCTION = eINSTANCE.getTemporalFunction();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TEMPORAL_FUNCTION__KIND = eINSTANCE.getTemporalFunction_Kind();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TEMPORAL_FUNCTION__SOURCE = eINSTANCE.getTemporalFunction_Source();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.impl.SubstringImpl <em>Substring</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.impl.SubstringImpl
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getSubstring()
		 * @generated
		 */
		EClass SUBSTRING = eINSTANCE.getSubstring();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SUBSTRING__SOURCE = eINSTANCE.getSubstring_Source();

		/**
		 * The meta object literal for the '<em><b>Start</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SUBSTRING__START = eINSTANCE.getSubstring_Start();

		/**
		 * The meta object literal for the '<em><b>Length</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SUBSTRING__LENGTH = eINSTANCE.getSubstring_Length();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.ComparisonOperator <em>Comparison Operator</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.ComparisonOperator
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getComparisonOperator()
		 * @generated
		 */
		EEnum COMPARISON_OPERATOR = eINSTANCE.getComparisonOperator();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.StringMatchKind <em>String Match Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.StringMatchKind
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getStringMatchKind()
		 * @generated
		 */
		EEnum STRING_MATCH_KIND = eINSTANCE.getStringMatchKind();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.StringFunctionKind <em>String Function Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.StringFunctionKind
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getStringFunctionKind()
		 * @generated
		 */
		EEnum STRING_FUNCTION_KIND = eINSTANCE.getStringFunctionKind();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.TemporalFunctionKind <em>Temporal Function Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.TemporalFunctionKind
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getTemporalFunctionKind()
		 * @generated
		 */
		EEnum TEMPORAL_FUNCTION_KIND = eINSTANCE.getTemporalFunctionKind();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.NumericFunctionKind <em>Numeric Function Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.NumericFunctionKind
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getNumericFunctionKind()
		 * @generated
		 */
		EEnum NUMERIC_FUNCTION_KIND = eINSTANCE.getNumericFunctionKind();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.ArithmeticOperator <em>Arithmetic Operator</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.ArithmeticOperator
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getArithmeticOperator()
		 * @generated
		 */
		EEnum ARITHMETIC_OPERATOR = eINSTANCE.getArithmeticOperator();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.expression.TemporalKind <em>Temporal Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.expression.TemporalKind
		 * @see org.eclipse.fennec.model.expression.impl.ExpressionPackageImpl#getTemporalKind()
		 * @generated
		 */
		EEnum TEMPORAL_KIND = eINSTANCE.getTemporalKind();

	}

} //ExpressionPackage
