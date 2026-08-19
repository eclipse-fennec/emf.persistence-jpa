/**
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
package org.eclipse.fennec.model.expression.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.fennec.model.expression.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ExpressionFactoryImpl extends EFactoryImpl implements ExpressionFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ExpressionFactory init() {
		try {
			ExpressionFactory theExpressionFactory = (ExpressionFactory)EPackage.Registry.INSTANCE.getEFactory(ExpressionPackage.eNS_URI);
			if (theExpressionFactory != null) {
				return theExpressionFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ExpressionFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExpressionFactoryImpl() {
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
			case ExpressionPackage.AND: return createAnd();
			case ExpressionPackage.OR: return createOr();
			case ExpressionPackage.NOT: return createNot();
			case ExpressionPackage.COMPARISON: return createComparison();
			case ExpressionPackage.IS_NULL: return createIsNull();
			case ExpressionPackage.BETWEEN: return createBetween();
			case ExpressionPackage.IN: return createIn();
			case ExpressionPackage.STRING_MATCH: return createStringMatch();
			case ExpressionPackage.EXISTS: return createExists();
			case ExpressionPackage.FOR_ALL: return createForAll();
			case ExpressionPackage.PROPERTY_PATH: return createPropertyPath();
			case ExpressionPackage.VARIABLE: return createVariable();
			case ExpressionPackage.VARIABLE_REF: return createVariableRef();
			case ExpressionPackage.ALIAS_REF: return createAliasRef();
			case ExpressionPackage.GEO_POINT_LITERAL: return createGeoPointLiteral();
			case ExpressionPackage.GEO_SUBJECT: return createGeoSubject();
			case ExpressionPackage.GEO_BOX: return createGeoBox();
			case ExpressionPackage.GEO_POLYGON: return createGeoPolygon();
			case ExpressionPackage.GEO_WITHIN: return createGeoWithin();
			case ExpressionPackage.GEO_DISTANCE: return createGeoDistance();
			case ExpressionPackage.SCORE: return createScore();
			case ExpressionPackage.PARAMETER_REF: return createParameterRef();
			case ExpressionPackage.STRING_LITERAL: return createStringLiteral();
			case ExpressionPackage.INTEGER_LITERAL: return createIntegerLiteral();
			case ExpressionPackage.REAL_LITERAL: return createRealLiteral();
			case ExpressionPackage.BOOLEAN_LITERAL: return createBooleanLiteral();
			case ExpressionPackage.NULL_LITERAL: return createNullLiteral();
			case ExpressionPackage.ENUM_LITERAL: return createEnumLiteral();
			case ExpressionPackage.TEMPORAL_LITERAL: return createTemporalLiteral();
			case ExpressionPackage.GUID_LITERAL: return createGuidLiteral();
			case ExpressionPackage.DURATION_LITERAL: return createDurationLiteral();
			case ExpressionPackage.STRING_FUNCTION: return createStringFunction();
			case ExpressionPackage.ARITHMETIC: return createArithmetic();
			case ExpressionPackage.NEGATE: return createNegate();
			case ExpressionPackage.CONCAT: return createConcat();
			case ExpressionPackage.INDEX_OF: return createIndexOf();
			case ExpressionPackage.MAP_VALUE: return createMapValue();
			case ExpressionPackage.COLLECTION_COUNT: return createCollectionCount();
			case ExpressionPackage.TYPE_CHECK: return createTypeCheck();
			case ExpressionPackage.NUMERIC_FUNCTION: return createNumericFunction();
			case ExpressionPackage.TEMPORAL_FUNCTION: return createTemporalFunction();
			case ExpressionPackage.SUBSTRING: return createSubstring();
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
			case ExpressionPackage.COMPARISON_OPERATOR:
				return createComparisonOperatorFromString(eDataType, initialValue);
			case ExpressionPackage.STRING_MATCH_KIND:
				return createStringMatchKindFromString(eDataType, initialValue);
			case ExpressionPackage.STRING_FUNCTION_KIND:
				return createStringFunctionKindFromString(eDataType, initialValue);
			case ExpressionPackage.TEMPORAL_FUNCTION_KIND:
				return createTemporalFunctionKindFromString(eDataType, initialValue);
			case ExpressionPackage.NUMERIC_FUNCTION_KIND:
				return createNumericFunctionKindFromString(eDataType, initialValue);
			case ExpressionPackage.ARITHMETIC_OPERATOR:
				return createArithmeticOperatorFromString(eDataType, initialValue);
			case ExpressionPackage.TEMPORAL_KIND:
				return createTemporalKindFromString(eDataType, initialValue);
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
			case ExpressionPackage.COMPARISON_OPERATOR:
				return convertComparisonOperatorToString(eDataType, instanceValue);
			case ExpressionPackage.STRING_MATCH_KIND:
				return convertStringMatchKindToString(eDataType, instanceValue);
			case ExpressionPackage.STRING_FUNCTION_KIND:
				return convertStringFunctionKindToString(eDataType, instanceValue);
			case ExpressionPackage.TEMPORAL_FUNCTION_KIND:
				return convertTemporalFunctionKindToString(eDataType, instanceValue);
			case ExpressionPackage.NUMERIC_FUNCTION_KIND:
				return convertNumericFunctionKindToString(eDataType, instanceValue);
			case ExpressionPackage.ARITHMETIC_OPERATOR:
				return convertArithmeticOperatorToString(eDataType, instanceValue);
			case ExpressionPackage.TEMPORAL_KIND:
				return convertTemporalKindToString(eDataType, instanceValue);
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
	public Comparison createComparison() {
		ComparisonImpl comparison = new ComparisonImpl();
		return comparison;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IsNull createIsNull() {
		IsNullImpl isNull = new IsNullImpl();
		return isNull;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Between createBetween() {
		BetweenImpl between = new BetweenImpl();
		return between;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public In createIn() {
		InImpl in = new InImpl();
		return in;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public StringMatch createStringMatch() {
		StringMatchImpl stringMatch = new StringMatchImpl();
		return stringMatch;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Exists createExists() {
		ExistsImpl exists = new ExistsImpl();
		return exists;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ForAll createForAll() {
		ForAllImpl forAll = new ForAllImpl();
		return forAll;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertyPath createPropertyPath() {
		PropertyPathImpl propertyPath = new PropertyPathImpl();
		return propertyPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Variable createVariable() {
		VariableImpl variable = new VariableImpl();
		return variable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public VariableRef createVariableRef() {
		VariableRefImpl variableRef = new VariableRefImpl();
		return variableRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AliasRef createAliasRef() {
		AliasRefImpl aliasRef = new AliasRefImpl();
		return aliasRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeoPointLiteral createGeoPointLiteral() {
		GeoPointLiteralImpl geoPointLiteral = new GeoPointLiteralImpl();
		return geoPointLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeoSubject createGeoSubject() {
		GeoSubjectImpl geoSubject = new GeoSubjectImpl();
		return geoSubject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeoBox createGeoBox() {
		GeoBoxImpl geoBox = new GeoBoxImpl();
		return geoBox;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeoPolygon createGeoPolygon() {
		GeoPolygonImpl geoPolygon = new GeoPolygonImpl();
		return geoPolygon;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeoWithin createGeoWithin() {
		GeoWithinImpl geoWithin = new GeoWithinImpl();
		return geoWithin;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeoDistance createGeoDistance() {
		GeoDistanceImpl geoDistance = new GeoDistanceImpl();
		return geoDistance;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Score createScore() {
		ScoreImpl score = new ScoreImpl();
		return score;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ParameterRef createParameterRef() {
		ParameterRefImpl parameterRef = new ParameterRefImpl();
		return parameterRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public StringLiteral createStringLiteral() {
		StringLiteralImpl stringLiteral = new StringLiteralImpl();
		return stringLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IntegerLiteral createIntegerLiteral() {
		IntegerLiteralImpl integerLiteral = new IntegerLiteralImpl();
		return integerLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public RealLiteral createRealLiteral() {
		RealLiteralImpl realLiteral = new RealLiteralImpl();
		return realLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BooleanLiteral createBooleanLiteral() {
		BooleanLiteralImpl booleanLiteral = new BooleanLiteralImpl();
		return booleanLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NullLiteral createNullLiteral() {
		NullLiteralImpl nullLiteral = new NullLiteralImpl();
		return nullLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EnumLiteral createEnumLiteral() {
		EnumLiteralImpl enumLiteral = new EnumLiteralImpl();
		return enumLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TemporalLiteral createTemporalLiteral() {
		TemporalLiteralImpl temporalLiteral = new TemporalLiteralImpl();
		return temporalLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GuidLiteral createGuidLiteral() {
		GuidLiteralImpl guidLiteral = new GuidLiteralImpl();
		return guidLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DurationLiteral createDurationLiteral() {
		DurationLiteralImpl durationLiteral = new DurationLiteralImpl();
		return durationLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public StringFunction createStringFunction() {
		StringFunctionImpl stringFunction = new StringFunctionImpl();
		return stringFunction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Arithmetic createArithmetic() {
		ArithmeticImpl arithmetic = new ArithmeticImpl();
		return arithmetic;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Negate createNegate() {
		NegateImpl negate = new NegateImpl();
		return negate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Concat createConcat() {
		ConcatImpl concat = new ConcatImpl();
		return concat;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IndexOf createIndexOf() {
		IndexOfImpl indexOf = new IndexOfImpl();
		return indexOf;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MapValue createMapValue() {
		MapValueImpl mapValue = new MapValueImpl();
		return mapValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CollectionCount createCollectionCount() {
		CollectionCountImpl collectionCount = new CollectionCountImpl();
		return collectionCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TypeCheck createTypeCheck() {
		TypeCheckImpl typeCheck = new TypeCheckImpl();
		return typeCheck;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NumericFunction createNumericFunction() {
		NumericFunctionImpl numericFunction = new NumericFunctionImpl();
		return numericFunction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TemporalFunction createTemporalFunction() {
		TemporalFunctionImpl temporalFunction = new TemporalFunctionImpl();
		return temporalFunction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Substring createSubstring() {
		SubstringImpl substring = new SubstringImpl();
		return substring;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComparisonOperator createComparisonOperatorFromString(EDataType eDataType, String initialValue) {
		ComparisonOperator result = ComparisonOperator.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertComparisonOperatorToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StringMatchKind createStringMatchKindFromString(EDataType eDataType, String initialValue) {
		StringMatchKind result = StringMatchKind.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertStringMatchKindToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StringFunctionKind createStringFunctionKindFromString(EDataType eDataType, String initialValue) {
		StringFunctionKind result = StringFunctionKind.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertStringFunctionKindToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TemporalFunctionKind createTemporalFunctionKindFromString(EDataType eDataType, String initialValue) {
		TemporalFunctionKind result = TemporalFunctionKind.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTemporalFunctionKindToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NumericFunctionKind createNumericFunctionKindFromString(EDataType eDataType, String initialValue) {
		NumericFunctionKind result = NumericFunctionKind.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertNumericFunctionKindToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ArithmeticOperator createArithmeticOperatorFromString(EDataType eDataType, String initialValue) {
		ArithmeticOperator result = ArithmeticOperator.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertArithmeticOperatorToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TemporalKind createTemporalKindFromString(EDataType eDataType, String initialValue) {
		TemporalKind result = TemporalKind.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTemporalKindToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ExpressionPackage getExpressionPackage() {
		return (ExpressionPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ExpressionPackage getPackage() {
		return ExpressionPackage.eINSTANCE;
	}

} //ExpressionFactoryImpl
