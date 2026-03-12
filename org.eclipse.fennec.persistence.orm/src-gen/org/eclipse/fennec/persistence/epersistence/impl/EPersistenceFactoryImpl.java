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
package org.eclipse.fennec.persistence.epersistence.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.eclipse.fennec.persistence.epersistence.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EPersistenceFactoryImpl extends EFactoryImpl implements EPersistenceFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static EPersistenceFactory init() {
		try {
			EPersistenceFactory theEPersistenceFactory = (EPersistenceFactory)EPackage.Registry.INSTANCE.getEFactory(EPersistencePackage.eNS_URI);
			if (theEPersistenceFactory != null) {
				return theEPersistenceFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new EPersistenceFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EPersistenceFactoryImpl() {
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
			case EPersistencePackage.PERSISTENCE_UNIT: return createPersistenceUnit();
			case EPersistencePackage.PROPERTIES: return createProperties();
			case EPersistencePackage.PROPERTY: return createProperty();
			case EPersistencePackage.PERSISTENCE: return createPersistence();
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
			case EPersistencePackage.PERSISTENCE_UNIT_VALIDATION_MODE:
				return createPersistenceUnitValidationModeFromString(eDataType, initialValue);
			case EPersistencePackage.PERSISTENCE_UNIT_CACHING_TYPE:
				return createPersistenceUnitCachingTypeFromString(eDataType, initialValue);
			case EPersistencePackage.PERSISTENCE_UNIT_TRANSACTION_TYPE:
				return createPersistenceUnitTransactionTypeFromString(eDataType, initialValue);
			case EPersistencePackage.VERSION:
				return createVersionFromString(eDataType, initialValue);
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
			case EPersistencePackage.PERSISTENCE_UNIT_VALIDATION_MODE:
				return convertPersistenceUnitValidationModeToString(eDataType, instanceValue);
			case EPersistencePackage.PERSISTENCE_UNIT_CACHING_TYPE:
				return convertPersistenceUnitCachingTypeToString(eDataType, instanceValue);
			case EPersistencePackage.PERSISTENCE_UNIT_TRANSACTION_TYPE:
				return convertPersistenceUnitTransactionTypeToString(eDataType, instanceValue);
			case EPersistencePackage.VERSION:
				return convertVersionToString(eDataType, instanceValue);
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
	public PersistenceUnit createPersistenceUnit() {
		PersistenceUnitImpl persistenceUnit = new PersistenceUnitImpl();
		return persistenceUnit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Properties createProperties() {
		PropertiesImpl properties = new PropertiesImpl();
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Property createProperty() {
		PropertyImpl property = new PropertyImpl();
		return property;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Persistence createPersistence() {
		PersistenceImpl persistence = new PersistenceImpl();
		return persistence;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PersistenceUnitValidationMode createPersistenceUnitValidationModeFromString(EDataType eDataType, String initialValue) {
		PersistenceUnitValidationMode result = PersistenceUnitValidationMode.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPersistenceUnitValidationModeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PersistenceUnitCachingType createPersistenceUnitCachingTypeFromString(EDataType eDataType, String initialValue) {
		PersistenceUnitCachingType result = PersistenceUnitCachingType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPersistenceUnitCachingTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PersistenceUnitTransactionType createPersistenceUnitTransactionTypeFromString(EDataType eDataType, String initialValue) {
		PersistenceUnitTransactionType result = PersistenceUnitTransactionType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPersistenceUnitTransactionTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String createVersionFromString(EDataType eDataType, String initialValue) {
		return (String)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.TOKEN, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertVersionToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.TOKEN, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EPersistencePackage getEPersistencePackage() {
		return (EPersistencePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static EPersistencePackage getPackage() {
		return EPersistencePackage.eINSTANCE;
	}

} //EPersistenceFactoryImpl
