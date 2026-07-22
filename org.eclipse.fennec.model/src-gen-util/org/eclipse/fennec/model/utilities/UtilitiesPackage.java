/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 */
package org.eclipse.fennec.model.utilities;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
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
 * @see org.eclipse.fennec.model.utilities.UtilitiesFactory
 * @model kind="package"
 *        annotation="Version value='2.2'"
 * @generated
 */
@ProviderType
@EPackage(uri = UtilitiesPackage.eNS_URI, genModel = "/model/utilities.genmodel", genModelSourceLocations = {"model/utilities.genmodel","org.eclipse.fennec.model/model/utilities.genmodel"}, ecore = "/model/utilities.ecore", ecoreSourceLocations = "/model/utilities.ecore")
public interface UtilitiesPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "utilities";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/utils/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "util";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	UtilitiesPackage eINSTANCE = org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.utilities.impl.FeaturePathImpl <em>Feature Path</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.utilities.impl.FeaturePathImpl
	 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getFeaturePath()
	 * @generated
	 */
	int FEATURE_PATH = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_PATH__NAME = 0;

	/**
	 * The feature id for the '<em><b>Feature</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_PATH__FEATURE = 1;

	/**
	 * The number of structural features of the '<em>Feature Path</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_PATH_FEATURE_COUNT = 2;

	/**
	 * The operation id for the '<em>Get Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_PATH___GET_VALUE__EOBJECT = 0;

	/**
	 * The operation id for the '<em>Is Valid</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_PATH___IS_VALID__EOBJECT = 1;

	/**
	 * The number of operations of the '<em>Feature Path</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_PATH_OPERATION_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.utilities.impl.ConverterImpl <em>Converter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.utilities.impl.ConverterImpl
	 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getConverter()
	 * @generated
	 */
	int CONVERTER = 1;

	/**
	 * The feature id for the '<em><b>Converter Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERTER__CONVERTER_ID = 0;

	/**
	 * The feature id for the '<em><b>From Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERTER__FROM_TYPE = 1;

	/**
	 * The feature id for the '<em><b>To Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERTER__TO_TYPE = 2;

	/**
	 * The number of structural features of the '<em>Converter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERTER_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Converter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERTER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.utilities.impl.LatLngImpl <em>Lat Lng</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.utilities.impl.LatLngImpl
	 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getLatLng()
	 * @generated
	 */
	int LAT_LNG = 2;

	/**
	 * The feature id for the '<em><b>Latitude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAT_LNG__LATITUDE = 0;

	/**
	 * The feature id for the '<em><b>Longitude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAT_LNG__LONGITUDE = 1;

	/**
	 * The number of structural features of the '<em>Lat Lng</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAT_LNG_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Lat Lng</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAT_LNG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.utilities.impl.StringToStringMapImpl <em>String To String Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.utilities.impl.StringToStringMapImpl
	 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getStringToStringMap()
	 * @generated
	 */
	int STRING_TO_STRING_MAP = 3;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_STRING_MAP__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_STRING_MAP__VALUE = 1;

	/**
	 * The number of structural features of the '<em>String To String Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_STRING_MAP_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>String To String Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_STRING_MAP_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.utilities.impl.RequestImpl <em>Request</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.utilities.impl.RequestImpl
	 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getRequest()
	 * @generated
	 */
	int REQUEST = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST__ID = 0;

	/**
	 * The feature id for the '<em><b>Object Id</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST__OBJECT_ID = 1;

	/**
	 * The feature id for the '<em><b>From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST__FROM = 2;

	/**
	 * The feature id for the '<em><b>To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST__TO = 3;

	/**
	 * The feature id for the '<em><b>Page</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST__PAGE = 4;

	/**
	 * The feature id for the '<em><b>Page Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST__PAGE_SIZE = 5;

	/**
	 * The feature id for the '<em><b>Return Result Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST__RETURN_RESULT_SIZE = 6;

	/**
	 * The feature id for the '<em><b>Sorting</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST__SORTING = 7;

	/**
	 * The feature id for the '<em><b>Filtering</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST__FILTERING = 8;

	/**
	 * The feature id for the '<em><b>Projection</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST__PROJECTION = 9;

	/**
	 * The feature id for the '<em><b>Query</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST__QUERY = 10;

	/**
	 * The number of structural features of the '<em>Request</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_FEATURE_COUNT = 11;

	/**
	 * The number of operations of the '<em>Request</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.utilities.impl.ResponseImpl <em>Response</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.utilities.impl.ResponseImpl
	 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getResponse()
	 * @generated
	 */
	int RESPONSE = 5;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__TIMESTAMP = 0;

	/**
	 * The feature id for the '<em><b>Result Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__RESULT_SIZE = 1;

	/**
	 * The feature id for the '<em><b>Response Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__RESPONSE_CODE = 2;

	/**
	 * The feature id for the '<em><b>Response Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__RESPONSE_MESSAGE = 3;

	/**
	 * The feature id for the '<em><b>Data</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__DATA = 4;

	/**
	 * The number of structural features of the '<em>Response</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Response</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.utilities.impl.SortImpl <em>Sort</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.utilities.impl.SortImpl
	 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getSort()
	 * @generated
	 */
	int SORT = 6;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SORT__INDEX = 0;

	/**
	 * The feature id for the '<em><b>Field</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SORT__FIELD = 1;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SORT__TYPE = 2;

	/**
	 * The number of structural features of the '<em>Sort</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SORT_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Sort</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SORT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.utilities.impl.FilterImpl <em>Filter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.utilities.impl.FilterImpl
	 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getFilter()
	 * @generated
	 */
	int FILTER = 7;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTER__INDEX = 0;

	/**
	 * The feature id for the '<em><b>Field</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTER__FIELD = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTER__VALUE = 2;

	/**
	 * The number of structural features of the '<em>Filter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTER_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Filter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.utilities.SortType <em>Sort Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.utilities.SortType
	 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getSortType()
	 * @generated
	 */
	int SORT_TYPE = 8;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.utilities.FeaturePath <em>Feature Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Feature Path</em>'.
	 * @see org.eclipse.fennec.model.utilities.FeaturePath
	 * @generated
	 */
	EClass getFeaturePath();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.FeaturePath#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.utilities.FeaturePath#getName()
	 * @see #getFeaturePath()
	 * @generated
	 */
	EAttribute getFeaturePath_Name();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.model.utilities.FeaturePath#getFeature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Feature</em>'.
	 * @see org.eclipse.fennec.model.utilities.FeaturePath#getFeature()
	 * @see #getFeaturePath()
	 * @generated
	 */
	EReference getFeaturePath_Feature();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.utilities.FeaturePath#getValue(org.eclipse.emf.ecore.EObject) <em>Get Value</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Value</em>' operation.
	 * @see org.eclipse.fennec.model.utilities.FeaturePath#getValue(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getFeaturePath__GetValue__EObject();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.utilities.FeaturePath#isValid(org.eclipse.emf.ecore.EObject) <em>Is Valid</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Is Valid</em>' operation.
	 * @see org.eclipse.fennec.model.utilities.FeaturePath#isValid(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getFeaturePath__IsValid__EObject();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.utilities.Converter <em>Converter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Converter</em>'.
	 * @see org.eclipse.fennec.model.utilities.Converter
	 * @generated
	 */
	EClass getConverter();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Converter#getConverterId <em>Converter Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Converter Id</em>'.
	 * @see org.eclipse.fennec.model.utilities.Converter#getConverterId()
	 * @see #getConverter()
	 * @generated
	 */
	EAttribute getConverter_ConverterId();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.utilities.Converter#getFromType <em>From Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>From Type</em>'.
	 * @see org.eclipse.fennec.model.utilities.Converter#getFromType()
	 * @see #getConverter()
	 * @generated
	 */
	EReference getConverter_FromType();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.utilities.Converter#getToType <em>To Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>To Type</em>'.
	 * @see org.eclipse.fennec.model.utilities.Converter#getToType()
	 * @see #getConverter()
	 * @generated
	 */
	EReference getConverter_ToType();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.utilities.LatLng <em>Lat Lng</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Lat Lng</em>'.
	 * @see org.eclipse.fennec.model.utilities.LatLng
	 * @generated
	 */
	EClass getLatLng();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.LatLng#getLatitude <em>Latitude</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Latitude</em>'.
	 * @see org.eclipse.fennec.model.utilities.LatLng#getLatitude()
	 * @see #getLatLng()
	 * @generated
	 */
	EAttribute getLatLng_Latitude();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.LatLng#getLongitude <em>Longitude</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Longitude</em>'.
	 * @see org.eclipse.fennec.model.utilities.LatLng#getLongitude()
	 * @see #getLatLng()
	 * @generated
	 */
	EAttribute getLatLng_Longitude();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>String To String Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String To String Map</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDataType="org.eclipse.emf.ecore.EString"
	 * @generated
	 */
	EClass getStringToStringMap();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToStringMap()
	 * @generated
	 */
	EAttribute getStringToStringMap_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToStringMap()
	 * @generated
	 */
	EAttribute getStringToStringMap_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.utilities.Request <em>Request</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Request</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request
	 * @generated
	 */
	EClass getRequest();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Request#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request#getId()
	 * @see #getRequest()
	 * @generated
	 */
	EAttribute getRequest_Id();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.utilities.Request#getObjectId <em>Object Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Object Id</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request#getObjectId()
	 * @see #getRequest()
	 * @generated
	 */
	EAttribute getRequest_ObjectId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Request#getFrom <em>From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>From</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request#getFrom()
	 * @see #getRequest()
	 * @generated
	 */
	EAttribute getRequest_From();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Request#getTo <em>To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>To</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request#getTo()
	 * @see #getRequest()
	 * @generated
	 */
	EAttribute getRequest_To();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Request#getPage <em>Page</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Page</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request#getPage()
	 * @see #getRequest()
	 * @generated
	 */
	EAttribute getRequest_Page();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Request#getPageSize <em>Page Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Page Size</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request#getPageSize()
	 * @see #getRequest()
	 * @generated
	 */
	EAttribute getRequest_PageSize();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Request#isReturnResultSize <em>Return Result Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Return Result Size</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request#isReturnResultSize()
	 * @see #getRequest()
	 * @generated
	 */
	EAttribute getRequest_ReturnResultSize();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.utilities.Request#getSorting <em>Sorting</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Sorting</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request#getSorting()
	 * @see #getRequest()
	 * @generated
	 */
	EReference getRequest_Sorting();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.utilities.Request#getFiltering <em>Filtering</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Filtering</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request#getFiltering()
	 * @see #getRequest()
	 * @generated
	 */
	EReference getRequest_Filtering();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.utilities.Request#getProjection <em>Projection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Projection</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request#getProjection()
	 * @see #getRequest()
	 * @generated
	 */
	EAttribute getRequest_Projection();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Request#getQuery <em>Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Query</em>'.
	 * @see org.eclipse.fennec.model.utilities.Request#getQuery()
	 * @see #getRequest()
	 * @generated
	 */
	EAttribute getRequest_Query();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.utilities.Response <em>Response</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Response</em>'.
	 * @see org.eclipse.fennec.model.utilities.Response
	 * @generated
	 */
	EClass getResponse();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Response#getTimestamp <em>Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Timestamp</em>'.
	 * @see org.eclipse.fennec.model.utilities.Response#getTimestamp()
	 * @see #getResponse()
	 * @generated
	 */
	EAttribute getResponse_Timestamp();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Response#getResultSize <em>Result Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Result Size</em>'.
	 * @see org.eclipse.fennec.model.utilities.Response#getResultSize()
	 * @see #getResponse()
	 * @generated
	 */
	EAttribute getResponse_ResultSize();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Response#getResponseCode <em>Response Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Response Code</em>'.
	 * @see org.eclipse.fennec.model.utilities.Response#getResponseCode()
	 * @see #getResponse()
	 * @generated
	 */
	EAttribute getResponse_ResponseCode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Response#getResponseMessage <em>Response Message</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Response Message</em>'.
	 * @see org.eclipse.fennec.model.utilities.Response#getResponseMessage()
	 * @see #getResponse()
	 * @generated
	 */
	EAttribute getResponse_ResponseMessage();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.utilities.Response#getData <em>Data</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Data</em>'.
	 * @see org.eclipse.fennec.model.utilities.Response#getData()
	 * @see #getResponse()
	 * @generated
	 */
	EReference getResponse_Data();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.utilities.Sort <em>Sort</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sort</em>'.
	 * @see org.eclipse.fennec.model.utilities.Sort
	 * @generated
	 */
	EClass getSort();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Sort#getIndex <em>Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Index</em>'.
	 * @see org.eclipse.fennec.model.utilities.Sort#getIndex()
	 * @see #getSort()
	 * @generated
	 */
	EAttribute getSort_Index();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Sort#getField <em>Field</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Field</em>'.
	 * @see org.eclipse.fennec.model.utilities.Sort#getField()
	 * @see #getSort()
	 * @generated
	 */
	EAttribute getSort_Field();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Sort#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.fennec.model.utilities.Sort#getType()
	 * @see #getSort()
	 * @generated
	 */
	EAttribute getSort_Type();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.utilities.Filter <em>Filter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Filter</em>'.
	 * @see org.eclipse.fennec.model.utilities.Filter
	 * @generated
	 */
	EClass getFilter();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Filter#getIndex <em>Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Index</em>'.
	 * @see org.eclipse.fennec.model.utilities.Filter#getIndex()
	 * @see #getFilter()
	 * @generated
	 */
	EAttribute getFilter_Index();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.utilities.Filter#getField <em>Field</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Field</em>'.
	 * @see org.eclipse.fennec.model.utilities.Filter#getField()
	 * @see #getFilter()
	 * @generated
	 */
	EAttribute getFilter_Field();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.utilities.Filter#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Value</em>'.
	 * @see org.eclipse.fennec.model.utilities.Filter#getValue()
	 * @see #getFilter()
	 * @generated
	 */
	EAttribute getFilter_Value();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.utilities.SortType <em>Sort Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Sort Type</em>'.
	 * @see org.eclipse.fennec.model.utilities.SortType
	 * @generated
	 */
	EEnum getSortType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	UtilitiesFactory getUtilitiesFactory();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.utilities.impl.FeaturePathImpl <em>Feature Path</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.utilities.impl.FeaturePathImpl
		 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getFeaturePath()
		 * @generated
		 */
		EClass FEATURE_PATH = eINSTANCE.getFeaturePath();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_PATH__NAME = eINSTANCE.getFeaturePath_Name();

		/**
		 * The meta object literal for the '<em><b>Feature</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_PATH__FEATURE = eINSTANCE.getFeaturePath_Feature();

		/**
		 * The meta object literal for the '<em><b>Get Value</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation FEATURE_PATH___GET_VALUE__EOBJECT = eINSTANCE.getFeaturePath__GetValue__EObject();

		/**
		 * The meta object literal for the '<em><b>Is Valid</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation FEATURE_PATH___IS_VALID__EOBJECT = eINSTANCE.getFeaturePath__IsValid__EObject();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.utilities.impl.ConverterImpl <em>Converter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.utilities.impl.ConverterImpl
		 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getConverter()
		 * @generated
		 */
		EClass CONVERTER = eINSTANCE.getConverter();

		/**
		 * The meta object literal for the '<em><b>Converter Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONVERTER__CONVERTER_ID = eINSTANCE.getConverter_ConverterId();

		/**
		 * The meta object literal for the '<em><b>From Type</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONVERTER__FROM_TYPE = eINSTANCE.getConverter_FromType();

		/**
		 * The meta object literal for the '<em><b>To Type</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONVERTER__TO_TYPE = eINSTANCE.getConverter_ToType();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.utilities.impl.LatLngImpl <em>Lat Lng</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.utilities.impl.LatLngImpl
		 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getLatLng()
		 * @generated
		 */
		EClass LAT_LNG = eINSTANCE.getLatLng();

		/**
		 * The meta object literal for the '<em><b>Latitude</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAT_LNG__LATITUDE = eINSTANCE.getLatLng_Latitude();

		/**
		 * The meta object literal for the '<em><b>Longitude</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAT_LNG__LONGITUDE = eINSTANCE.getLatLng_Longitude();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.utilities.impl.StringToStringMapImpl <em>String To String Map</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.utilities.impl.StringToStringMapImpl
		 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getStringToStringMap()
		 * @generated
		 */
		EClass STRING_TO_STRING_MAP = eINSTANCE.getStringToStringMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_TO_STRING_MAP__KEY = eINSTANCE.getStringToStringMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_TO_STRING_MAP__VALUE = eINSTANCE.getStringToStringMap_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.utilities.impl.RequestImpl <em>Request</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.utilities.impl.RequestImpl
		 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getRequest()
		 * @generated
		 */
		EClass REQUEST = eINSTANCE.getRequest();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST__ID = eINSTANCE.getRequest_Id();

		/**
		 * The meta object literal for the '<em><b>Object Id</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST__OBJECT_ID = eINSTANCE.getRequest_ObjectId();

		/**
		 * The meta object literal for the '<em><b>From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST__FROM = eINSTANCE.getRequest_From();

		/**
		 * The meta object literal for the '<em><b>To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST__TO = eINSTANCE.getRequest_To();

		/**
		 * The meta object literal for the '<em><b>Page</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST__PAGE = eINSTANCE.getRequest_Page();

		/**
		 * The meta object literal for the '<em><b>Page Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST__PAGE_SIZE = eINSTANCE.getRequest_PageSize();

		/**
		 * The meta object literal for the '<em><b>Return Result Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST__RETURN_RESULT_SIZE = eINSTANCE.getRequest_ReturnResultSize();

		/**
		 * The meta object literal for the '<em><b>Sorting</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUEST__SORTING = eINSTANCE.getRequest_Sorting();

		/**
		 * The meta object literal for the '<em><b>Filtering</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUEST__FILTERING = eINSTANCE.getRequest_Filtering();

		/**
		 * The meta object literal for the '<em><b>Projection</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST__PROJECTION = eINSTANCE.getRequest_Projection();

		/**
		 * The meta object literal for the '<em><b>Query</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST__QUERY = eINSTANCE.getRequest_Query();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.utilities.impl.ResponseImpl <em>Response</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.utilities.impl.ResponseImpl
		 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getResponse()
		 * @generated
		 */
		EClass RESPONSE = eINSTANCE.getResponse();

		/**
		 * The meta object literal for the '<em><b>Timestamp</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESPONSE__TIMESTAMP = eINSTANCE.getResponse_Timestamp();

		/**
		 * The meta object literal for the '<em><b>Result Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESPONSE__RESULT_SIZE = eINSTANCE.getResponse_ResultSize();

		/**
		 * The meta object literal for the '<em><b>Response Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESPONSE__RESPONSE_CODE = eINSTANCE.getResponse_ResponseCode();

		/**
		 * The meta object literal for the '<em><b>Response Message</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESPONSE__RESPONSE_MESSAGE = eINSTANCE.getResponse_ResponseMessage();

		/**
		 * The meta object literal for the '<em><b>Data</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE__DATA = eINSTANCE.getResponse_Data();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.utilities.impl.SortImpl <em>Sort</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.utilities.impl.SortImpl
		 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getSort()
		 * @generated
		 */
		EClass SORT = eINSTANCE.getSort();

		/**
		 * The meta object literal for the '<em><b>Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SORT__INDEX = eINSTANCE.getSort_Index();

		/**
		 * The meta object literal for the '<em><b>Field</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SORT__FIELD = eINSTANCE.getSort_Field();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SORT__TYPE = eINSTANCE.getSort_Type();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.utilities.impl.FilterImpl <em>Filter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.utilities.impl.FilterImpl
		 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getFilter()
		 * @generated
		 */
		EClass FILTER = eINSTANCE.getFilter();

		/**
		 * The meta object literal for the '<em><b>Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILTER__INDEX = eINSTANCE.getFilter_Index();

		/**
		 * The meta object literal for the '<em><b>Field</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILTER__FIELD = eINSTANCE.getFilter_Field();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILTER__VALUE = eINSTANCE.getFilter_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.utilities.SortType <em>Sort Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.utilities.SortType
		 * @see org.eclipse.fennec.model.utilities.impl.UtilitiesPackageImpl#getSortType()
		 * @generated
		 */
		EEnum SORT_TYPE = eINSTANCE.getSortType();

	}

} //UtilitiesPackage
