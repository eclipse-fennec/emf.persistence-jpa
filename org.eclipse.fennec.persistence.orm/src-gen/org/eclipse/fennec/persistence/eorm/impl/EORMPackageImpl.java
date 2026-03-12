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
package org.eclipse.fennec.persistence.eorm.impl;

import java.io.IOException;

import java.net.URL;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.EORMPackage;

import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;

import org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EORMPackageImpl extends EPackageImpl implements EORMPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected String packageFilename = "eorm.ecore";

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass associationOverrideEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass attributeOverrideEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass attributesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass basicEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass cascadeTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass collectionTableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass columnEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass columnResultEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass constructorResultEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass convertEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass converterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass discriminatorColumnEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass documentRootEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass elementCollectionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass embeddableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass embeddableAttributesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass embeddedEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass embeddedIdEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass emptyTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass entityEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass entityListenerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass entityListenersEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass entityMappingsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass entityResultEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass fieldResultEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass foreignKeyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass generatedValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eNamedBaseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass baseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass baseRefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass idEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass idClassEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass indexEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass inheritanceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass joinColumnEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass joinTableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass lobEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass manyToManyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass manyToOneEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mapKeyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mapKeyClassEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mapKeyColumnEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mapKeyJoinColumnEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mappedSuperclassEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass namedAttributeNodeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass namedEntityGraphEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass namedNativeQueryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass namedQueryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass namedStoredProcedureQueryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass namedSubgraphEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass oneToManyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass oneToOneEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass orderColumnEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass persistenceUnitDefaultsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass persistenceUnitMetadataEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass postLoadEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass postPersistEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass postRemoveEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass postUpdateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass prePersistEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass preRemoveEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass preUpdateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass primaryKeyJoinColumnEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass queryHintEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass secondaryTableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass sequenceGeneratorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass simpleBaseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass sqlResultSetMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass storedProcedureParameterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tableGeneratorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass transientEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass uniqueConstraintEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass versionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mappedByRefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass baseColumnEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eAccessorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eFeatureObjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eClassObjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eormElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum accessTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum constraintModeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum discriminatorTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum enumTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum fetchTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum generationTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum inheritanceTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum lockModeTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum parameterModeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum temporalTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType accessTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType constraintModeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType discriminatorTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType discriminatorValueEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType enumeratedEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType enumTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType fetchTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType generationTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType inheritanceTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType lockModeTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType orderByEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType parameterModeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType temporalEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType temporalTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType versionTypeEDataType = null;

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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private EORMPackageImpl() {
		super(eNS_URI, EORMFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link EORMPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @generated
	 */
	public static EORMPackage init() {
		if (isInited) return (EORMPackage)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredEORMPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		EORMPackageImpl theEORMPackage = registeredEORMPackage instanceof EORMPackageImpl ? (EORMPackageImpl)registeredEORMPackage : new EORMPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(EPersistencePackage.eNS_URI);
		EPersistencePackageImpl theEPersistencePackage = (EPersistencePackageImpl)(registeredPackage instanceof EPersistencePackageImpl ? registeredPackage : EPersistencePackage.eINSTANCE);

		// Load packages
		theEORMPackage.loadPackage();

		// Create package meta-data objects
		theEPersistencePackage.createPackageContents();

		// Initialize created meta-data
		theEPersistencePackage.initializePackageContents();

		// Fix loaded packages
		theEORMPackage.fixPackageContents();

		// Mark meta-data to indicate it can't be changed
		theEORMPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(EORMPackage.eNS_URI, theEORMPackage);
		return theEORMPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAssociationOverride() {
		if (associationOverrideEClass == null) {
			associationOverrideEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(2);
		}
		return associationOverrideEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAssociationOverride_Description() {
        return (EAttribute)getAssociationOverride().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAssociationOverride_JoinColumn() {
        return (EReference)getAssociationOverride().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAssociationOverride_ForeignKey() {
        return (EReference)getAssociationOverride().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAssociationOverride_JoinTable() {
        return (EReference)getAssociationOverride().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAssociationOverride_Name() {
        return (EAttribute)getAssociationOverride().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAttributeOverride() {
		if (attributeOverrideEClass == null) {
			attributeOverrideEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(3);
		}
		return attributeOverrideEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAttributeOverride_Description() {
        return (EAttribute)getAttributeOverride().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributeOverride_Column() {
        return (EReference)getAttributeOverride().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAttributeOverride_Name() {
        return (EAttribute)getAttributeOverride().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAttributes() {
		if (attributesEClass == null) {
			attributesEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(4);
		}
		return attributesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAttributes_Description() {
        return (EAttribute)getAttributes().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributes_Id() {
        return (EReference)getAttributes().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributes_EmbeddedId() {
        return (EReference)getAttributes().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributes_Basic() {
        return (EReference)getAttributes().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributes_Version() {
        return (EReference)getAttributes().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributes_ManyToOne() {
        return (EReference)getAttributes().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributes_OneToMany() {
        return (EReference)getAttributes().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributes_OneToOne() {
        return (EReference)getAttributes().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributes_ManyToMany() {
        return (EReference)getAttributes().getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributes_ElementCollection() {
        return (EReference)getAttributes().getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributes_Embedded() {
        return (EReference)getAttributes().getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributes_Transient() {
        return (EReference)getAttributes().getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBasic() {
		if (basicEClass == null) {
			basicEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(5);
		}
		return basicEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBasic_Optional() {
        return (EAttribute)getBasic().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCascadeType() {
		if (cascadeTypeEClass == null) {
			cascadeTypeEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(6);
		}
		return cascadeTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCascadeType_CascadeAll() {
        return (EReference)getCascadeType().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCascadeType_CascadePersist() {
        return (EReference)getCascadeType().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCascadeType_CascadeMerge() {
        return (EReference)getCascadeType().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCascadeType_CascadeRemove() {
        return (EReference)getCascadeType().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCascadeType_CascadeRefresh() {
        return (EReference)getCascadeType().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCascadeType_CascadeDetach() {
        return (EReference)getCascadeType().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCollectionTable() {
		if (collectionTableEClass == null) {
			collectionTableEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(7);
		}
		return collectionTableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollectionTable_JoinColumn() {
        return (EReference)getCollectionTable().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollectionTable_ForeignKey() {
        return (EReference)getCollectionTable().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getColumn() {
		if (columnEClass == null) {
			columnEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(8);
		}
		return columnEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getColumn_Length() {
        return (EAttribute)getColumn().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getColumn_Precision() {
        return (EAttribute)getColumn().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getColumn_Scale() {
        return (EAttribute)getColumn().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getColumn_Table() {
        return (EAttribute)getColumn().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getColumn_Unique() {
        return (EAttribute)getColumn().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getColumnResult() {
		if (columnResultEClass == null) {
			columnResultEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(9);
		}
		return columnResultEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getColumnResult_Class() {
        return (EAttribute)getColumnResult().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getColumnResult_Name() {
        return (EAttribute)getColumnResult().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getConstructorResult() {
		if (constructorResultEClass == null) {
			constructorResultEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(12);
		}
		return constructorResultEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConstructorResult_Column() {
        return (EReference)getConstructorResult().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getConstructorResult_TargetClass() {
        return (EAttribute)getConstructorResult().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getConvert() {
		if (convertEClass == null) {
			convertEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(13);
		}
		return convertEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getConvert_Description() {
        return (EAttribute)getConvert().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getConvert_AttributeName() {
        return (EAttribute)getConvert().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getConvert_Converter() {
        return (EAttribute)getConvert().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getConvert_DisableConversion() {
        return (EAttribute)getConvert().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getConverter() {
		if (converterEClass == null) {
			converterEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(14);
		}
		return converterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getConverter_Description() {
        return (EAttribute)getConverter().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getConverter_AutoApply() {
        return (EAttribute)getConverter().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConverter_Class() {
        return (EReference)getConverter().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDiscriminatorColumn() {
		if (discriminatorColumnEClass == null) {
			discriminatorColumnEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(15);
		}
		return discriminatorColumnEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDiscriminatorColumn_ColumnDefinition() {
        return (EAttribute)getDiscriminatorColumn().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDiscriminatorColumn_DiscriminatorType() {
        return (EAttribute)getDiscriminatorColumn().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDiscriminatorColumn_Length() {
        return (EAttribute)getDiscriminatorColumn().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDiscriminatorColumn_Name() {
        return (EAttribute)getDiscriminatorColumn().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDocumentRoot() {
		if (documentRootEClass == null) {
			documentRootEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(19);
		}
		return documentRootEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDocumentRoot_Mixed() {
        return (EAttribute)getDocumentRoot().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_XMLNSPrefixMap() {
        return (EReference)getDocumentRoot().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_XSISchemaLocation() {
        return (EReference)getDocumentRoot().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_EntityMappings() {
        return (EReference)getDocumentRoot().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getElementCollection() {
		if (elementCollectionEClass == null) {
			elementCollectionEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(20);
		}
		return elementCollectionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getElementCollection_OrderBy() {
        return (EAttribute)getElementCollection().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getElementCollection_OrderColumn() {
        return (EReference)getElementCollection().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getElementCollection_MapKey() {
        return (EReference)getElementCollection().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getElementCollection_MapKeyClass() {
        return (EReference)getElementCollection().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getElementCollection_MapKeyTemporal() {
        return (EAttribute)getElementCollection().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getElementCollection_MapKeyEnumerated() {
        return (EAttribute)getElementCollection().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getElementCollection_MapKeyAttributeOverride() {
        return (EReference)getElementCollection().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getElementCollection_MapKeyConvert() {
        return (EReference)getElementCollection().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getElementCollection_MapKeyColumn() {
        return (EReference)getElementCollection().getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getElementCollection_MapKeyJoinColumn() {
        return (EReference)getElementCollection().getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getElementCollection_MapKeyForeignKey() {
        return (EReference)getElementCollection().getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getElementCollection_AttributeOverride() {
        return (EReference)getElementCollection().getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getElementCollection_AssociationOverride() {
        return (EReference)getElementCollection().getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getElementCollection_CollectionTable() {
        return (EReference)getElementCollection().getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getElementCollection_TargetClass() {
        return (EAttribute)getElementCollection().getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEmbeddable() {
		if (embeddableEClass == null) {
			embeddableEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(21);
		}
		return embeddableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEmbeddable_Description() {
        return (EAttribute)getEmbeddable().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbeddable_Attributes() {
        return (EReference)getEmbeddable().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEmbeddable_Access() {
        return (EAttribute)getEmbeddable().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbeddable_Class() {
        return (EReference)getEmbeddable().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEmbeddable_MetadataComplete() {
        return (EAttribute)getEmbeddable().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEmbeddableAttributes() {
		if (embeddableAttributesEClass == null) {
			embeddableAttributesEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(22);
		}
		return embeddableAttributesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbeddableAttributes_Basic() {
        return (EReference)getEmbeddableAttributes().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbeddableAttributes_ManyToOne() {
        return (EReference)getEmbeddableAttributes().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbeddableAttributes_OneToMany() {
        return (EReference)getEmbeddableAttributes().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbeddableAttributes_OneToOne() {
        return (EReference)getEmbeddableAttributes().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbeddableAttributes_ManyToMany() {
        return (EReference)getEmbeddableAttributes().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbeddableAttributes_ElementCollection() {
        return (EReference)getEmbeddableAttributes().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbeddableAttributes_Embedded() {
        return (EReference)getEmbeddableAttributes().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbeddableAttributes_Transient() {
        return (EReference)getEmbeddableAttributes().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEmbedded() {
		if (embeddedEClass == null) {
			embeddedEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(23);
		}
		return embeddedEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbedded_AttributeOverride() {
        return (EReference)getEmbedded().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbedded_AssociationOverride() {
        return (EReference)getEmbedded().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbedded_Convert() {
        return (EReference)getEmbedded().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEmbedded_Access() {
        return (EAttribute)getEmbedded().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEmbedded_Name() {
        return (EAttribute)getEmbedded().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEmbeddedId() {
		if (embeddedIdEClass == null) {
			embeddedIdEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(24);
		}
		return embeddedIdEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEmbeddedId_AttributeOverride() {
        return (EReference)getEmbeddedId().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEmbeddedId_Access() {
        return (EAttribute)getEmbeddedId().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEmbeddedId_Name() {
        return (EAttribute)getEmbeddedId().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEmptyType() {
		if (emptyTypeEClass == null) {
			emptyTypeEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(25);
		}
		return emptyTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEntity() {
		if (entityEClass == null) {
			entityEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(26);
		}
		return entityEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntity_Description() {
        return (EAttribute)getEntity().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_Table() {
        return (EReference)getEntity().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_SecondaryTable() {
        return (EReference)getEntity().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_PrimaryKeyJoinColumn() {
        return (EReference)getEntity().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_PrimaryKeyForeignKey() {
        return (EReference)getEntity().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_IdClass() {
        return (EReference)getEntity().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_Inheritance() {
        return (EReference)getEntity().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntity_DiscriminatorValue() {
        return (EAttribute)getEntity().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_DiscriminatorColumn() {
        return (EReference)getEntity().getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_SequenceGenerator() {
        return (EReference)getEntity().getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_TableGenerator() {
        return (EReference)getEntity().getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_NamedQuery() {
        return (EReference)getEntity().getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_NamedNativeQuery() {
        return (EReference)getEntity().getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_NamedStoredProcedureQuery() {
        return (EReference)getEntity().getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_SqlResultSetMapping() {
        return (EReference)getEntity().getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_ExcludeDefaultListeners() {
        return (EReference)getEntity().getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_ExcludeSuperclassListeners() {
        return (EReference)getEntity().getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_EntityListeners() {
        return (EReference)getEntity().getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_PrePersist() {
        return (EReference)getEntity().getEStructuralFeatures().get(18);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_PostPersist() {
        return (EReference)getEntity().getEStructuralFeatures().get(19);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_PreRemove() {
        return (EReference)getEntity().getEStructuralFeatures().get(20);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_PostRemove() {
        return (EReference)getEntity().getEStructuralFeatures().get(21);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_PreUpdate() {
        return (EReference)getEntity().getEStructuralFeatures().get(22);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_PostUpdate() {
        return (EReference)getEntity().getEStructuralFeatures().get(23);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_PostLoad() {
        return (EReference)getEntity().getEStructuralFeatures().get(24);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_AttributeOverride() {
        return (EReference)getEntity().getEStructuralFeatures().get(25);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_AssociationOverride() {
        return (EReference)getEntity().getEStructuralFeatures().get(26);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_Convert() {
        return (EReference)getEntity().getEStructuralFeatures().get(27);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_NamedEntityGraph() {
        return (EReference)getEntity().getEStructuralFeatures().get(28);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_Attributes() {
        return (EReference)getEntity().getEStructuralFeatures().get(29);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntity_Access() {
        return (EAttribute)getEntity().getEStructuralFeatures().get(30);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntity_Cacheable() {
        return (EAttribute)getEntity().getEStructuralFeatures().get(31);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntity_MetadataComplete() {
        return (EAttribute)getEntity().getEStructuralFeatures().get(32);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntity_Name() {
        return (EAttribute)getEntity().getEStructuralFeatures().get(33);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntity_Class() {
        return (EReference)getEntity().getEStructuralFeatures().get(34);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEntityListener() {
		if (entityListenerEClass == null) {
			entityListenerEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(27);
		}
		return entityListenerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntityListener_Description() {
        return (EAttribute)getEntityListener().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityListener_PrePersist() {
        return (EReference)getEntityListener().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityListener_PostPersist() {
        return (EReference)getEntityListener().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityListener_PreRemove() {
        return (EReference)getEntityListener().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityListener_PostRemove() {
        return (EReference)getEntityListener().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityListener_PreUpdate() {
        return (EReference)getEntityListener().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityListener_PostUpdate() {
        return (EReference)getEntityListener().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityListener_PostLoad() {
        return (EReference)getEntityListener().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityListener_Class() {
        return (EReference)getEntityListener().getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEntityListeners() {
		if (entityListenersEClass == null) {
			entityListenersEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(28);
		}
		return entityListenersEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityListeners_EntityListener() {
        return (EReference)getEntityListeners().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEntityMappings() {
		if (entityMappingsEClass == null) {
			entityMappingsEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(29);
		}
		return entityMappingsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntityMappings_Description() {
        return (EAttribute)getEntityMappings().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityMappings_PersistenceUnitMetadata() {
        return (EReference)getEntityMappings().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntityMappings_Package() {
        return (EAttribute)getEntityMappings().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntityMappings_Schema() {
        return (EAttribute)getEntityMappings().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntityMappings_Catalog() {
        return (EAttribute)getEntityMappings().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntityMappings_Access() {
        return (EAttribute)getEntityMappings().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityMappings_SequenceGenerator() {
        return (EReference)getEntityMappings().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityMappings_TableGenerator() {
        return (EReference)getEntityMappings().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityMappings_NamedQuery() {
        return (EReference)getEntityMappings().getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityMappings_NamedNativeQuery() {
        return (EReference)getEntityMappings().getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityMappings_NamedStoredProcedureQuery() {
        return (EReference)getEntityMappings().getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityMappings_SqlResultSetMapping() {
        return (EReference)getEntityMappings().getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityMappings_MappedSuperclass() {
        return (EReference)getEntityMappings().getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityMappings_Entity() {
        return (EReference)getEntityMappings().getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityMappings_Embeddable() {
        return (EReference)getEntityMappings().getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityMappings_Converter() {
        return (EReference)getEntityMappings().getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntityMappings_Version() {
        return (EAttribute)getEntityMappings().getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntityMappings_Name() {
        return (EAttribute)getEntityMappings().getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEntityResult() {
		if (entityResultEClass == null) {
			entityResultEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(30);
		}
		return entityResultEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEntityResult_FieldResult() {
        return (EReference)getEntityResult().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntityResult_DiscriminatorColumn() {
        return (EAttribute)getEntityResult().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEntityResult_EntityClass() {
        return (EAttribute)getEntityResult().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFieldResult() {
		if (fieldResultEClass == null) {
			fieldResultEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(36);
		}
		return fieldResultEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFieldResult_Column() {
        return (EAttribute)getFieldResult().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFieldResult_Name() {
        return (EAttribute)getFieldResult().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getForeignKey() {
		if (foreignKeyEClass == null) {
			foreignKeyEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(37);
		}
		return foreignKeyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getForeignKey_Description() {
        return (EAttribute)getForeignKey().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getForeignKey_ConstraintMode() {
        return (EAttribute)getForeignKey().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getForeignKey_ForeignKeyDefinition() {
        return (EAttribute)getForeignKey().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getForeignKey_Name() {
        return (EAttribute)getForeignKey().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGeneratedValue() {
		if (generatedValueEClass == null) {
			generatedValueEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(38);
		}
		return generatedValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGeneratedValue_Generator() {
        return (EAttribute)getGeneratedValue().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGeneratedValue_Strategy() {
        return (EAttribute)getGeneratedValue().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getENamedBase() {
		if (eNamedBaseEClass == null) {
			eNamedBaseEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(41);
		}
		return eNamedBaseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getENamedBase_Access() {
        return (EAttribute)getENamedBase().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getENamedBase_Name() {
        return (EAttribute)getENamedBase().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBase() {
		if (baseEClass == null) {
			baseEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(42);
		}
		return baseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getBase_Column() {
        return (EReference)getBase().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBase_Temporal() {
        return (EAttribute)getBase().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBaseRef() {
		if (baseRefEClass == null) {
			baseRefEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(43);
		}
		return baseRefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getBaseRef_Cascade() {
        return (EReference)getBaseRef().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseRef_Fetch() {
        return (EAttribute)getBaseRef().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getBaseRef_ForeignKey() {
        return (EReference)getBaseRef().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getBaseRef_JoinTable() {
        return (EReference)getBaseRef().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseRef_Optional() {
        return (EAttribute)getBaseRef().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getId() {
		if (idEClass == null) {
			idEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(44);
		}
		return idEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getId_GeneratedValue() {
        return (EReference)getId().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getId_TableGenerator() {
        return (EReference)getId().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getId_SequenceGenerator() {
        return (EReference)getId().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIdClass() {
		if (idClassEClass == null) {
			idClassEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(45);
		}
		return idClassEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getIdClass_Class() {
        return (EReference)getIdClass().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIndex() {
		if (indexEClass == null) {
			indexEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(46);
		}
		return indexEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIndex_Description() {
        return (EAttribute)getIndex().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIndex_ColumnList() {
        return (EAttribute)getIndex().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIndex_Name() {
        return (EAttribute)getIndex().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIndex_Unique() {
        return (EAttribute)getIndex().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInheritance() {
		if (inheritanceEClass == null) {
			inheritanceEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(47);
		}
		return inheritanceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInheritance_Strategy() {
        return (EAttribute)getInheritance().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getJoinColumn() {
		if (joinColumnEClass == null) {
			joinColumnEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(50);
		}
		return joinColumnEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getJoinColumn_ColumnDefinition() {
        return (EAttribute)getJoinColumn().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getJoinColumn_Insertable() {
        return (EAttribute)getJoinColumn().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getJoinColumn_Name() {
        return (EAttribute)getJoinColumn().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getJoinColumn_Nullable() {
        return (EAttribute)getJoinColumn().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getJoinColumn_ReferencedColumnName() {
        return (EAttribute)getJoinColumn().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getJoinColumn_Table() {
        return (EAttribute)getJoinColumn().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getJoinColumn_Unique() {
        return (EAttribute)getJoinColumn().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getJoinColumn_Updatable() {
        return (EAttribute)getJoinColumn().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getJoinTable() {
		if (joinTableEClass == null) {
			joinTableEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(51);
		}
		return joinTableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getJoinTable_JoinColumn() {
        return (EReference)getJoinTable().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getJoinTable_ForeignKey() {
        return (EReference)getJoinTable().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getJoinTable_InverseJoinColumn() {
        return (EReference)getJoinTable().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getJoinTable_InverseForeignKey() {
        return (EReference)getJoinTable().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getJoinTable_UniqueConstraint() {
        return (EReference)getJoinTable().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getJoinTable_Index() {
        return (EReference)getJoinTable().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getJoinTable_Catalog() {
        return (EAttribute)getJoinTable().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getJoinTable_Name() {
        return (EAttribute)getJoinTable().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getJoinTable_Schema() {
        return (EAttribute)getJoinTable().getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLob() {
		if (lobEClass == null) {
			lobEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(52);
		}
		return lobEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getManyToMany() {
		if (manyToManyEClass == null) {
			manyToManyEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(55);
		}
		return manyToManyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getManyToMany_OrderBy() {
        return (EAttribute)getManyToMany().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getManyToMany_OrderColumn() {
        return (EReference)getManyToMany().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getManyToMany_MapKey() {
        return (EReference)getManyToMany().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getManyToMany_MapKeyClass() {
        return (EReference)getManyToMany().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getManyToMany_MapKeyTemporal() {
        return (EAttribute)getManyToMany().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getManyToMany_MapKeyEnumerated() {
        return (EAttribute)getManyToMany().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getManyToMany_MapKeyAttributeOverride() {
        return (EReference)getManyToMany().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getManyToMany_MapKeyConvert() {
        return (EReference)getManyToMany().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getManyToMany_MapKeyColumn() {
        return (EReference)getManyToMany().getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getManyToMany_MapKeyJoinColumn() {
        return (EReference)getManyToMany().getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getManyToMany_MapKeyForeignKey() {
        return (EReference)getManyToMany().getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getManyToMany_TargetEntity() {
        return (EAttribute)getManyToMany().getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getManyToOne() {
		if (manyToOneEClass == null) {
			manyToOneEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(56);
		}
		return manyToOneEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getManyToOne_JoinColumn() {
        return (EReference)getManyToOne().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getManyToOne_Id() {
        return (EAttribute)getManyToOne().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getManyToOne_MapsId() {
        return (EAttribute)getManyToOne().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getManyToOne_TargetEntity() {
        return (EAttribute)getManyToOne().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMapKey() {
		if (mapKeyEClass == null) {
			mapKeyEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(57);
		}
		return mapKeyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKey_Name() {
        return (EAttribute)getMapKey().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMapKeyClass() {
		if (mapKeyClassEClass == null) {
			mapKeyClassEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(58);
		}
		return mapKeyClassEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMapKeyClass_Class() {
        return (EReference)getMapKeyClass().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMapKeyColumn() {
		if (mapKeyColumnEClass == null) {
			mapKeyColumnEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(59);
		}
		return mapKeyColumnEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyColumn_ColumnDefinition() {
        return (EAttribute)getMapKeyColumn().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyColumn_Insertable() {
        return (EAttribute)getMapKeyColumn().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyColumn_Length() {
        return (EAttribute)getMapKeyColumn().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyColumn_Name() {
        return (EAttribute)getMapKeyColumn().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyColumn_Nullable() {
        return (EAttribute)getMapKeyColumn().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyColumn_Precision() {
        return (EAttribute)getMapKeyColumn().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyColumn_Scale() {
        return (EAttribute)getMapKeyColumn().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyColumn_Table() {
        return (EAttribute)getMapKeyColumn().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyColumn_Unique() {
        return (EAttribute)getMapKeyColumn().getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyColumn_Updatable() {
        return (EAttribute)getMapKeyColumn().getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMapKeyJoinColumn() {
		if (mapKeyJoinColumnEClass == null) {
			mapKeyJoinColumnEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(60);
		}
		return mapKeyJoinColumnEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyJoinColumn_ColumnDefinition() {
        return (EAttribute)getMapKeyJoinColumn().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyJoinColumn_Insertable() {
        return (EAttribute)getMapKeyJoinColumn().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyJoinColumn_Name() {
        return (EAttribute)getMapKeyJoinColumn().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyJoinColumn_Nullable() {
        return (EAttribute)getMapKeyJoinColumn().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyJoinColumn_ReferencedColumnName() {
        return (EAttribute)getMapKeyJoinColumn().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyJoinColumn_Table() {
        return (EAttribute)getMapKeyJoinColumn().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyJoinColumn_Unique() {
        return (EAttribute)getMapKeyJoinColumn().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapKeyJoinColumn_Updatable() {
        return (EAttribute)getMapKeyJoinColumn().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMappedSuperclass() {
		if (mappedSuperclassEClass == null) {
			mappedSuperclassEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(61);
		}
		return mappedSuperclassEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMappedSuperclass_Description() {
        return (EAttribute)getMappedSuperclass().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_IdClass() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_ExcludeDefaultListeners() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_ExcludeSuperclassListeners() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_EntityListeners() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_PrePersist() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_PostPersist() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_PreRemove() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_PostRemove() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_PreUpdate() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_PostUpdate() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_PostLoad() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_Attributes() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMappedSuperclass_Access() {
        return (EAttribute)getMappedSuperclass().getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappedSuperclass_Class() {
        return (EReference)getMappedSuperclass().getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMappedSuperclass_MetadataComplete() {
        return (EAttribute)getMappedSuperclass().getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getNamedAttributeNode() {
		if (namedAttributeNodeEClass == null) {
			namedAttributeNodeEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(62);
		}
		return namedAttributeNodeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedAttributeNode_KeySubgraph() {
        return (EAttribute)getNamedAttributeNode().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedAttributeNode_Name() {
        return (EAttribute)getNamedAttributeNode().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedAttributeNode_Subgraph() {
        return (EAttribute)getNamedAttributeNode().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getNamedEntityGraph() {
		if (namedEntityGraphEClass == null) {
			namedEntityGraphEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(63);
		}
		return namedEntityGraphEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getNamedEntityGraph_NamedAttributeNode() {
        return (EReference)getNamedEntityGraph().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getNamedEntityGraph_Subgraph() {
        return (EReference)getNamedEntityGraph().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getNamedEntityGraph_SubclassSubgraph() {
        return (EReference)getNamedEntityGraph().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedEntityGraph_IncludeAllAttributes() {
        return (EAttribute)getNamedEntityGraph().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedEntityGraph_Name() {
        return (EAttribute)getNamedEntityGraph().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getNamedNativeQuery() {
		if (namedNativeQueryEClass == null) {
			namedNativeQueryEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(64);
		}
		return namedNativeQueryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedNativeQuery_Description() {
        return (EAttribute)getNamedNativeQuery().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedNativeQuery_Query() {
        return (EAttribute)getNamedNativeQuery().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getNamedNativeQuery_Hint() {
        return (EReference)getNamedNativeQuery().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedNativeQuery_Name() {
        return (EAttribute)getNamedNativeQuery().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedNativeQuery_ResultClass() {
        return (EAttribute)getNamedNativeQuery().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedNativeQuery_ResultSetMapping() {
        return (EAttribute)getNamedNativeQuery().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getNamedQuery() {
		if (namedQueryEClass == null) {
			namedQueryEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(65);
		}
		return namedQueryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedQuery_Description() {
        return (EAttribute)getNamedQuery().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedQuery_Query() {
        return (EAttribute)getNamedQuery().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedQuery_LockMode() {
        return (EAttribute)getNamedQuery().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getNamedQuery_Hint() {
        return (EReference)getNamedQuery().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedQuery_Name() {
        return (EAttribute)getNamedQuery().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getNamedStoredProcedureQuery() {
		if (namedStoredProcedureQueryEClass == null) {
			namedStoredProcedureQueryEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(66);
		}
		return namedStoredProcedureQueryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedStoredProcedureQuery_Description() {
        return (EAttribute)getNamedStoredProcedureQuery().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getNamedStoredProcedureQuery_Parameter() {
        return (EReference)getNamedStoredProcedureQuery().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedStoredProcedureQuery_ResultClass() {
        return (EAttribute)getNamedStoredProcedureQuery().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedStoredProcedureQuery_ResultSetMapping() {
        return (EAttribute)getNamedStoredProcedureQuery().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getNamedStoredProcedureQuery_Hint() {
        return (EReference)getNamedStoredProcedureQuery().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedStoredProcedureQuery_Name() {
        return (EAttribute)getNamedStoredProcedureQuery().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedStoredProcedureQuery_ProcedureName() {
        return (EAttribute)getNamedStoredProcedureQuery().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getNamedSubgraph() {
		if (namedSubgraphEClass == null) {
			namedSubgraphEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(67);
		}
		return namedSubgraphEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getNamedSubgraph_NamedAttributeNode() {
        return (EReference)getNamedSubgraph().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedSubgraph_Class() {
        return (EAttribute)getNamedSubgraph().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNamedSubgraph_Name() {
        return (EAttribute)getNamedSubgraph().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOneToMany() {
		if (oneToManyEClass == null) {
			oneToManyEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(68);
		}
		return oneToManyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOneToMany_OrderBy() {
        return (EAttribute)getOneToMany().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToMany_OrderColumn() {
        return (EReference)getOneToMany().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToMany_MapKey() {
        return (EReference)getOneToMany().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToMany_MapKeyClass() {
        return (EReference)getOneToMany().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOneToMany_MapKeyTemporal() {
        return (EAttribute)getOneToMany().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOneToMany_MapKeyEnumerated() {
        return (EAttribute)getOneToMany().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToMany_MapKeyAttributeOverride() {
        return (EReference)getOneToMany().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToMany_MapKeyConvert() {
        return (EReference)getOneToMany().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToMany_MapKeyColumn() {
        return (EReference)getOneToMany().getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToMany_MapKeyJoinColumn() {
        return (EReference)getOneToMany().getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToMany_MapKeyForeignKey() {
        return (EReference)getOneToMany().getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToMany_JoinColumn() {
        return (EReference)getOneToMany().getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOneToMany_OrphanRemoval() {
        return (EAttribute)getOneToMany().getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOneToMany_TargetEntity() {
        return (EAttribute)getOneToMany().getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOneToOne() {
		if (oneToOneEClass == null) {
			oneToOneEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(69);
		}
		return oneToOneEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToOne_PrimaryKeyJoinColumn() {
        return (EReference)getOneToOne().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToOne_PrimaryKeyForeignKey() {
        return (EReference)getOneToOne().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOneToOne_JoinColumn() {
        return (EReference)getOneToOne().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOneToOne_Id() {
        return (EAttribute)getOneToOne().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOneToOne_MapsId() {
        return (EAttribute)getOneToOne().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOneToOne_OrphanRemoval() {
        return (EAttribute)getOneToOne().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOneToOne_TargetEntity() {
        return (EAttribute)getOneToOne().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOrderColumn() {
		if (orderColumnEClass == null) {
			orderColumnEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(71);
		}
		return orderColumnEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPersistenceUnitDefaults() {
		if (persistenceUnitDefaultsEClass == null) {
			persistenceUnitDefaultsEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(74);
		}
		return persistenceUnitDefaultsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnitDefaults_Description() {
        return (EAttribute)getPersistenceUnitDefaults().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnitDefaults_Schema() {
        return (EAttribute)getPersistenceUnitDefaults().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnitDefaults_Catalog() {
        return (EAttribute)getPersistenceUnitDefaults().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPersistenceUnitDefaults_DelimitedIdentifiers() {
        return (EReference)getPersistenceUnitDefaults().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnitDefaults_Access() {
        return (EAttribute)getPersistenceUnitDefaults().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPersistenceUnitDefaults_CascadePersist() {
        return (EReference)getPersistenceUnitDefaults().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPersistenceUnitDefaults_EntityListeners() {
        return (EReference)getPersistenceUnitDefaults().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPersistenceUnitMetadata() {
		if (persistenceUnitMetadataEClass == null) {
			persistenceUnitMetadataEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(75);
		}
		return persistenceUnitMetadataEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnitMetadata_Description() {
        return (EAttribute)getPersistenceUnitMetadata().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPersistenceUnitMetadata_XmlMappingMetadataComplete() {
        return (EReference)getPersistenceUnitMetadata().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPersistenceUnitMetadata_PersistenceUnitDefaults() {
        return (EReference)getPersistenceUnitMetadata().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPostLoad() {
		if (postLoadEClass == null) {
			postLoadEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(76);
		}
		return postLoadEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPostLoad_Description() {
        return (EAttribute)getPostLoad().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPostLoad_MethodName() {
        return (EAttribute)getPostLoad().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPostPersist() {
		if (postPersistEClass == null) {
			postPersistEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(77);
		}
		return postPersistEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPostPersist_Description() {
        return (EAttribute)getPostPersist().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPostPersist_MethodName() {
        return (EAttribute)getPostPersist().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPostRemove() {
		if (postRemoveEClass == null) {
			postRemoveEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(78);
		}
		return postRemoveEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPostRemove_Description() {
        return (EAttribute)getPostRemove().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPostRemove_MethodName() {
        return (EAttribute)getPostRemove().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPostUpdate() {
		if (postUpdateEClass == null) {
			postUpdateEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(79);
		}
		return postUpdateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPostUpdate_Description() {
        return (EAttribute)getPostUpdate().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPostUpdate_MethodName() {
        return (EAttribute)getPostUpdate().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPrePersist() {
		if (prePersistEClass == null) {
			prePersistEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(80);
		}
		return prePersistEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPrePersist_Description() {
        return (EAttribute)getPrePersist().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPrePersist_MethodName() {
        return (EAttribute)getPrePersist().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPreRemove() {
		if (preRemoveEClass == null) {
			preRemoveEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(81);
		}
		return preRemoveEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPreRemove_Description() {
        return (EAttribute)getPreRemove().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPreRemove_MethodName() {
        return (EAttribute)getPreRemove().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPreUpdate() {
		if (preUpdateEClass == null) {
			preUpdateEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(82);
		}
		return preUpdateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPreUpdate_Description() {
        return (EAttribute)getPreUpdate().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPreUpdate_MethodName() {
        return (EAttribute)getPreUpdate().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPrimaryKeyJoinColumn() {
		if (primaryKeyJoinColumnEClass == null) {
			primaryKeyJoinColumnEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(83);
		}
		return primaryKeyJoinColumnEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPrimaryKeyJoinColumn_ColumnDefinition() {
        return (EAttribute)getPrimaryKeyJoinColumn().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPrimaryKeyJoinColumn_Name() {
        return (EAttribute)getPrimaryKeyJoinColumn().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPrimaryKeyJoinColumn_ReferencedColumnName() {
        return (EAttribute)getPrimaryKeyJoinColumn().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQueryHint() {
		if (queryHintEClass == null) {
			queryHintEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(84);
		}
		return queryHintEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQueryHint_Description() {
        return (EAttribute)getQueryHint().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQueryHint_Name() {
        return (EAttribute)getQueryHint().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQueryHint_Value() {
        return (EAttribute)getQueryHint().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSecondaryTable() {
		if (secondaryTableEClass == null) {
			secondaryTableEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(85);
		}
		return secondaryTableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSecondaryTable_PrimaryKeyJoinColumn() {
        return (EReference)getSecondaryTable().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSecondaryTable_PrimaryKeyForeignKey() {
        return (EReference)getSecondaryTable().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSecondaryTable_UniqueConstraint() {
        return (EReference)getSecondaryTable().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSecondaryTable_Index() {
        return (EReference)getSecondaryTable().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecondaryTable_Catalog() {
        return (EAttribute)getSecondaryTable().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecondaryTable_Name() {
        return (EAttribute)getSecondaryTable().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecondaryTable_Schema() {
        return (EAttribute)getSecondaryTable().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSequenceGenerator() {
		if (sequenceGeneratorEClass == null) {
			sequenceGeneratorEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(86);
		}
		return sequenceGeneratorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSequenceGenerator_Description() {
        return (EAttribute)getSequenceGenerator().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSequenceGenerator_AllocationSize() {
        return (EAttribute)getSequenceGenerator().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSequenceGenerator_Catalog() {
        return (EAttribute)getSequenceGenerator().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSequenceGenerator_InitialValue() {
        return (EAttribute)getSequenceGenerator().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSequenceGenerator_Name() {
        return (EAttribute)getSequenceGenerator().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSequenceGenerator_Schema() {
        return (EAttribute)getSequenceGenerator().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSequenceGenerator_SequenceName() {
        return (EAttribute)getSequenceGenerator().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSimpleBase() {
		if (simpleBaseEClass == null) {
			simpleBaseEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(87);
		}
		return simpleBaseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSimpleBase_Lob() {
        return (EReference)getSimpleBase().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSimpleBase_Convert() {
        return (EReference)getSimpleBase().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSimpleBase_Fetch() {
        return (EAttribute)getSimpleBase().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSimpleBase_Enumerated() {
        return (EReference)getSimpleBase().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSqlResultSetMapping() {
		if (sqlResultSetMappingEClass == null) {
			sqlResultSetMappingEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(88);
		}
		return sqlResultSetMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSqlResultSetMapping_Description() {
        return (EAttribute)getSqlResultSetMapping().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSqlResultSetMapping_EntityResult() {
        return (EReference)getSqlResultSetMapping().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSqlResultSetMapping_ConstructorResult() {
        return (EReference)getSqlResultSetMapping().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSqlResultSetMapping_ColumnResult() {
        return (EReference)getSqlResultSetMapping().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSqlResultSetMapping_Name() {
        return (EAttribute)getSqlResultSetMapping().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStoredProcedureParameter() {
		if (storedProcedureParameterEClass == null) {
			storedProcedureParameterEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(89);
		}
		return storedProcedureParameterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStoredProcedureParameter_Description() {
        return (EAttribute)getStoredProcedureParameter().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getStoredProcedureParameter_Class() {
        return (EReference)getStoredProcedureParameter().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStoredProcedureParameter_Mode() {
        return (EAttribute)getStoredProcedureParameter().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStoredProcedureParameter_Name() {
        return (EAttribute)getStoredProcedureParameter().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTable() {
		if (tableEClass == null) {
			tableEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(90);
		}
		return tableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTable_UniqueConstraint() {
        return (EReference)getTable().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTable_Index() {
        return (EReference)getTable().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTable_Catalog() {
        return (EAttribute)getTable().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTable_Name() {
        return (EAttribute)getTable().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTable_Schema() {
        return (EAttribute)getTable().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTableGenerator() {
		if (tableGeneratorEClass == null) {
			tableGeneratorEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(91);
		}
		return tableGeneratorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTableGenerator_Description() {
        return (EAttribute)getTableGenerator().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTableGenerator_UniqueConstraint() {
        return (EReference)getTableGenerator().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTableGenerator_Index() {
        return (EReference)getTableGenerator().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTableGenerator_AllocationSize() {
        return (EAttribute)getTableGenerator().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTableGenerator_Catalog() {
        return (EAttribute)getTableGenerator().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTableGenerator_InitialValue() {
        return (EAttribute)getTableGenerator().getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTableGenerator_Name() {
        return (EAttribute)getTableGenerator().getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTableGenerator_PkColumnName() {
        return (EAttribute)getTableGenerator().getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTableGenerator_PkColumnValue() {
        return (EAttribute)getTableGenerator().getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTableGenerator_Schema() {
        return (EAttribute)getTableGenerator().getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTableGenerator_Table() {
        return (EAttribute)getTableGenerator().getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTableGenerator_ValueColumnName() {
        return (EAttribute)getTableGenerator().getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTransient() {
		if (transientEClass == null) {
			transientEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(95);
		}
		return transientEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTransient_Name() {
        return (EAttribute)getTransient().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getUniqueConstraint() {
		if (uniqueConstraintEClass == null) {
			uniqueConstraintEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(96);
		}
		return uniqueConstraintEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUniqueConstraint_ColumnName() {
        return (EAttribute)getUniqueConstraint().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUniqueConstraint_Name() {
        return (EAttribute)getUniqueConstraint().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getVersion() {
		if (versionEClass == null) {
			versionEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(97);
		}
		return versionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getVersion_Column() {
        return (EReference)getVersion().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getVersion_Temporal() {
        return (EAttribute)getVersion().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getVersion_Access() {
        return (EAttribute)getVersion().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getVersion_Name() {
        return (EAttribute)getVersion().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMappedByRef() {
		if (mappedByRefEClass == null) {
			mappedByRefEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(99);
		}
		return mappedByRefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMappedByRef_MappedBy() {
        return (EAttribute)getMappedByRef().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBaseColumn() {
		if (baseColumnEClass == null) {
			baseColumnEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(100);
		}
		return baseColumnEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseColumn_ColumnDefinition() {
        return (EAttribute)getBaseColumn().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseColumn_Insertable() {
        return (EAttribute)getBaseColumn().getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseColumn_Name() {
        return (EAttribute)getBaseColumn().getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseColumn_Nullable() {
        return (EAttribute)getBaseColumn().getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseColumn_Updatable() {
        return (EAttribute)getBaseColumn().getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEAccessor() {
		if (eAccessorEClass == null) {
			eAccessorEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(101);
		}
		return eAccessorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEAccessor_Name() {
        return (EAttribute)getEAccessor().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEFeatureObject() {
		if (eFeatureObjectEClass == null) {
			eFeatureObjectEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(102);
		}
		return eFeatureObjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEFeatureObject_Feature() {
        return (EReference)getEFeatureObject().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEClassObject() {
		if (eClassObjectEClass == null) {
			eClassObjectEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(103);
		}
		return eClassObjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEClassObject_Eclass() {
        return (EReference)getEClassObject().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEORMElement() {
		if (eormElementEClass == null) {
			eormElementEClass = (EClass)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(104);
		}
		return eormElementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEORMElement_AccessibleObject() {
        return (EReference)getEORMElement().getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getAccessType() {
		if (accessTypeEEnum == null) {
			accessTypeEEnum = (EEnum)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(0);
		}
		return accessTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getConstraintMode() {
		if (constraintModeEEnum == null) {
			constraintModeEEnum = (EEnum)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(10);
		}
		return constraintModeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getDiscriminatorType() {
		if (discriminatorTypeEEnum == null) {
			discriminatorTypeEEnum = (EEnum)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(16);
		}
		return discriminatorTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getEnumType() {
		if (enumTypeEEnum == null) {
			enumTypeEEnum = (EEnum)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(32);
		}
		return enumTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getFetchType() {
		if (fetchTypeEEnum == null) {
			fetchTypeEEnum = (EEnum)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(34);
		}
		return fetchTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getGenerationType() {
		if (generationTypeEEnum == null) {
			generationTypeEEnum = (EEnum)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(39);
		}
		return generationTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getInheritanceType() {
		if (inheritanceTypeEEnum == null) {
			inheritanceTypeEEnum = (EEnum)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(48);
		}
		return inheritanceTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getLockModeType() {
		if (lockModeTypeEEnum == null) {
			lockModeTypeEEnum = (EEnum)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(53);
		}
		return lockModeTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getParameterMode() {
		if (parameterModeEEnum == null) {
			parameterModeEEnum = (EEnum)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(72);
		}
		return parameterModeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getTemporalType() {
		if (temporalTypeEEnum == null) {
			temporalTypeEEnum = (EEnum)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(93);
		}
		return temporalTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getAccessTypeObject() {
		if (accessTypeObjectEDataType == null) {
			accessTypeObjectEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(1);
		}
		return accessTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getConstraintModeObject() {
		if (constraintModeObjectEDataType == null) {
			constraintModeObjectEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(11);
		}
		return constraintModeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getDiscriminatorTypeObject() {
		if (discriminatorTypeObjectEDataType == null) {
			discriminatorTypeObjectEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(17);
		}
		return discriminatorTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getDiscriminatorValue() {
		if (discriminatorValueEDataType == null) {
			discriminatorValueEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(18);
		}
		return discriminatorValueEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getEnumerated() {
		if (enumeratedEDataType == null) {
			enumeratedEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(31);
		}
		return enumeratedEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getEnumTypeObject() {
		if (enumTypeObjectEDataType == null) {
			enumTypeObjectEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(33);
		}
		return enumTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getFetchTypeObject() {
		if (fetchTypeObjectEDataType == null) {
			fetchTypeObjectEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(35);
		}
		return fetchTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getGenerationTypeObject() {
		if (generationTypeObjectEDataType == null) {
			generationTypeObjectEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(40);
		}
		return generationTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getInheritanceTypeObject() {
		if (inheritanceTypeObjectEDataType == null) {
			inheritanceTypeObjectEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(49);
		}
		return inheritanceTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getLockModeTypeObject() {
		if (lockModeTypeObjectEDataType == null) {
			lockModeTypeObjectEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(54);
		}
		return lockModeTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getOrderBy() {
		if (orderByEDataType == null) {
			orderByEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(70);
		}
		return orderByEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getParameterModeObject() {
		if (parameterModeObjectEDataType == null) {
			parameterModeObjectEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(73);
		}
		return parameterModeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTemporal() {
		if (temporalEDataType == null) {
			temporalEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(92);
		}
		return temporalEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTemporalTypeObject() {
		if (temporalTypeObjectEDataType == null) {
			temporalTypeObjectEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(94);
		}
		return temporalTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getVersionType() {
		if (versionTypeEDataType == null) {
			versionTypeEDataType = (EDataType)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI).getEClassifiers().get(98);
		}
		return versionTypeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EORMFactory getEORMFactory() {
		return (EORMFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isLoaded = false;

	/**
	 * Loads the package and any sub-packages from their serialized form.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void loadPackage() {
		if (isLoaded) return;
		isLoaded = true;

		URL url = getClass().getResource(packageFilename);
		if (url == null) {
			throw new RuntimeException("Missing serialized package: " + packageFilename);
		}
		URI uri = URI.createURI(url.toString());
		Resource resource = new EcoreResourceFactoryImpl().createResource(uri);
		try {
			resource.load(null);
		}
		catch (IOException exception) {
			throw new WrappedException(exception);
		}
		initializeFromLoadedEPackage(this, (EPackage)resource.getContents().get(0));
		createResource(eNS_URI);
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isFixed = false;

	/**
	 * Fixes up the loaded package, to make it appear as if it had been programmatically built.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void fixPackageContents() {
		if (isFixed) return;
		isFixed = true;
		fixEClassifiers();
	}

	/**
	 * Sets the instance class on the given classifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void fixInstanceClass(EClassifier eClassifier) {
		if (eClassifier.getInstanceClassName() == null) {
			eClassifier.setInstanceClassName("org.eclipse.fennec.persistence.eorm." + eClassifier.getName());
			setGeneratedClassName(eClassifier);
		}
	}

} //EORMPackageImpl
