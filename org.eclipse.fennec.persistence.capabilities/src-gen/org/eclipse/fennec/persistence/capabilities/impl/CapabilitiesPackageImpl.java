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
package org.eclipse.fennec.persistence.capabilities.impl;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.persistence.capabilities.CapabilitiesFactory;
import org.eclipse.fennec.persistence.capabilities.CapabilitiesPackage;
import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.capabilities.StoreFeature;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class CapabilitiesPackageImpl extends EPackageImpl implements CapabilitiesPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum queryFeatureEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum commandFeatureEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum storeFeatureEEnum = null;

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
	 * @see org.eclipse.fennec.persistence.capabilities.CapabilitiesPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private CapabilitiesPackageImpl() {
		super(eNS_URI, CapabilitiesFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link CapabilitiesPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static CapabilitiesPackage init() {
		if (isInited) return (CapabilitiesPackage)EPackage.Registry.INSTANCE.getEPackage(CapabilitiesPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredCapabilitiesPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		CapabilitiesPackageImpl theCapabilitiesPackage = registeredCapabilitiesPackage instanceof CapabilitiesPackageImpl ? (CapabilitiesPackageImpl)registeredCapabilitiesPackage : new CapabilitiesPackageImpl();

		isInited = true;

		// Create package meta-data objects
		theCapabilitiesPackage.createPackageContents();

		// Initialize created meta-data
		theCapabilitiesPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theCapabilitiesPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(CapabilitiesPackage.eNS_URI, theCapabilitiesPackage);
		return theCapabilitiesPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getQueryFeature() {
		return queryFeatureEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getCommandFeature() {
		return commandFeatureEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getStoreFeature() {
		return storeFeatureEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CapabilitiesFactory getCapabilitiesFactory() {
		return (CapabilitiesFactory)getEFactoryInstance();
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

		// Create enums
		queryFeatureEEnum = createEEnum(QUERY_FEATURE);
		commandFeatureEEnum = createEEnum(COMMAND_FEATURE);
		storeFeatureEEnum = createEEnum(STORE_FEATURE);
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

		// Initialize enums and add enum literals
		initEEnum(queryFeatureEEnum, QueryFeature.class, "QueryFeature");
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.WHERE_EQ);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.WHERE_COMPARISON);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.WHERE_STRING_MATCH);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.WHERE_RANGE);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.LOGICAL_AND);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.LOGICAL_OR);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.LOGICAL_NOT);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.WHERE_NE);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.IS_NULL);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.IN);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.EXISTS);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.FOR_ALL);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.STRING_MATCH_CASE_INSENSITIVE);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.FIELD_TO_FIELD);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.SORT);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.LIMIT);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.SKIP);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.DISTINCT);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.COUNT);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.PROJECTION);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.PROJECTION_NESTED);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.EXPAND);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.PIPELINE);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.GROUP_BY);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.AGG_AVG);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.AGG_MIN);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.AGG_MAX);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.AGG_SUM);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.AGG_COUNT);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.AGG_COUNT_DISTINCT);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.STRING_FUNCTIONS);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.ARITHMETIC);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.STRING_FUNCTIONS_EXTENDED);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.NUMERIC_FUNCTIONS);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.TEMPORAL_FUNCTIONS);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.TYPE_CAST);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.TYPE_CHECK);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.COLLECTION_COUNT);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.COLLECTION_COUNT_FILTERED);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.PIPELINE_COMPUTE);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.SORT_EXPRESSION);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.GROUP_EXPRESSION);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.SCORE);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.GEO_WITHIN);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.GEO_DISTANCE);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.FEATUREPATH_NESTED);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.TYPE_FILTER);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.PARAMETERS);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.AS_OF);
		addEEnumLiteral(queryFeatureEEnum, QueryFeature.SERIES_RANGE);

		initEEnum(commandFeatureEEnum, CommandFeature.class, "CommandFeature");
		addEEnumLiteral(commandFeatureEEnum, CommandFeature.INSERT);
		addEEnumLiteral(commandFeatureEEnum, CommandFeature.DELETE_BY_SELECTOR);
		addEEnumLiteral(commandFeatureEEnum, CommandFeature.UPDATE_BY_SELECTOR);

		initEEnum(storeFeatureEEnum, StoreFeature.class, "StoreFeature");
		addEEnumLiteral(storeFeatureEEnum, StoreFeature.TRANSACTION_BRACKET);

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

} //CapabilitiesPackageImpl
