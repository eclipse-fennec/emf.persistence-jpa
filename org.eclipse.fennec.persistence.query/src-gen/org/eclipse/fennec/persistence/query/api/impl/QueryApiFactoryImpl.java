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
package org.eclipse.fennec.persistence.query.api.impl;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.stream.Stream;

import org.eclipse.emf.common.util.Diagnostic;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.fennec.persistence.api.ConverterService;

import org.eclipse.fennec.persistence.query.QueryException;

import org.eclipse.fennec.persistence.query.api.QueryApiFactory;
import org.eclipse.fennec.persistence.query.api.QueryApiPackage;
import org.eclipse.fennec.persistence.query.api.QueryFeature;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryShape;

import org.eclipse.fennec.persistence.query.support.CommandTransaction;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class QueryApiFactoryImpl extends EFactoryImpl implements QueryApiFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static QueryApiFactory init() {
		try {
			QueryApiFactory theQueryApiFactory = (QueryApiFactory)EPackage.Registry.INSTANCE.getEFactory(QueryApiPackage.eNS_URI);
			if (theQueryApiFactory != null) {
				return theQueryApiFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new QueryApiFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public QueryApiFactoryImpl() {
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
			case QueryApiPackage.QUERY_SHAPE:
				return createQueryShapeFromString(eDataType, initialValue);
			case QueryApiPackage.QUERY_FEATURE:
				return createQueryFeatureFromString(eDataType, initialValue);
			case QueryApiPackage.DIAGNOSTIC:
				return createDiagnosticFromString(eDataType, initialValue);
			case QueryApiPackage.CONVERTER_SERVICE:
				return createConverterServiceFromString(eDataType, initialValue);
			case QueryApiPackage.QUERY_EXCEPTION:
				return createQueryExceptionFromString(eDataType, initialValue);
			case QueryApiPackage.COMMAND_TRANSACTION:
				return createCommandTransactionFromString(eDataType, initialValue);
			case QueryApiPackage.IO_EXCEPTION:
				return createIOExceptionFromString(eDataType, initialValue);
			case QueryApiPackage.EOBJECT_STREAM:
				return createEObjectStreamFromString(eDataType, initialValue);
			case QueryApiPackage.ROW_STREAM:
				return createRowStreamFromString(eDataType, initialValue);
			case QueryApiPackage.OBJECT_LIST:
				return createObjectListFromString(eDataType, initialValue);
			case QueryApiPackage.QUERY_FEATURE_SET:
				return createQueryFeatureSetFromString(eDataType, initialValue);
			case QueryApiPackage.PARAMETER_MAP:
				return createParameterMapFromString(eDataType, initialValue);
			case QueryApiPackage.OPTIONS_MAP:
				return createOptionsMapFromString(eDataType, initialValue);
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
			case QueryApiPackage.QUERY_SHAPE:
				return convertQueryShapeToString(eDataType, instanceValue);
			case QueryApiPackage.QUERY_FEATURE:
				return convertQueryFeatureToString(eDataType, instanceValue);
			case QueryApiPackage.DIAGNOSTIC:
				return convertDiagnosticToString(eDataType, instanceValue);
			case QueryApiPackage.CONVERTER_SERVICE:
				return convertConverterServiceToString(eDataType, instanceValue);
			case QueryApiPackage.QUERY_EXCEPTION:
				return convertQueryExceptionToString(eDataType, instanceValue);
			case QueryApiPackage.COMMAND_TRANSACTION:
				return convertCommandTransactionToString(eDataType, instanceValue);
			case QueryApiPackage.IO_EXCEPTION:
				return convertIOExceptionToString(eDataType, instanceValue);
			case QueryApiPackage.EOBJECT_STREAM:
				return convertEObjectStreamToString(eDataType, instanceValue);
			case QueryApiPackage.ROW_STREAM:
				return convertRowStreamToString(eDataType, instanceValue);
			case QueryApiPackage.OBJECT_LIST:
				return convertObjectListToString(eDataType, instanceValue);
			case QueryApiPackage.QUERY_FEATURE_SET:
				return convertQueryFeatureSetToString(eDataType, instanceValue);
			case QueryApiPackage.PARAMETER_MAP:
				return convertParameterMapToString(eDataType, instanceValue);
			case QueryApiPackage.OPTIONS_MAP:
				return convertOptionsMapToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public QueryShape createQueryShapeFromString(EDataType eDataType, String initialValue) {
		QueryShape result = QueryShape.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertQueryShapeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public QueryFeature createQueryFeatureFromString(EDataType eDataType, String initialValue) {
		QueryFeature result = QueryFeature.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertQueryFeatureToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Diagnostic createDiagnosticFromString(EDataType eDataType, String initialValue) {
		return (Diagnostic)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDiagnosticToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConverterService createConverterServiceFromString(EDataType eDataType, String initialValue) {
		return (ConverterService)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertConverterServiceToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public QueryException createQueryExceptionFromString(EDataType eDataType, String initialValue) {
		return (QueryException)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertQueryExceptionToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CommandTransaction createCommandTransactionFromString(EDataType eDataType, String initialValue) {
		return (CommandTransaction)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertCommandTransactionToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IOException createIOExceptionFromString(EDataType eDataType, String initialValue) {
		return (IOException)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertIOExceptionToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public Stream<EObject> createEObjectStreamFromString(EDataType eDataType, String initialValue) {
		return (Stream<EObject>)super.createFromString(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEObjectStreamToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public Stream<QueryResultRow> createRowStreamFromString(EDataType eDataType, String initialValue) {
		return (Stream<QueryResultRow>)super.createFromString(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertRowStreamToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public List<Object> createObjectListFromString(EDataType eDataType, String initialValue) {
		return (List<Object>)super.createFromString(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertObjectListToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public Set<QueryFeature> createQueryFeatureSetFromString(EDataType eDataType, String initialValue) {
		return (Set<QueryFeature>)super.createFromString(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertQueryFeatureSetToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> createParameterMapFromString(EDataType eDataType, String initialValue) {
		return (Map<String, Object>)super.createFromString(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertParameterMapToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map<?, ?> createOptionsMapFromString(EDataType eDataType, String initialValue) {
		return (Map<?, ?>)super.createFromString(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertOptionsMapToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public QueryApiPackage getQueryApiPackage() {
		return (QueryApiPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static QueryApiPackage getPackage() {
		return QueryApiPackage.eINSTANCE;
	}

} //QueryApiFactoryImpl
