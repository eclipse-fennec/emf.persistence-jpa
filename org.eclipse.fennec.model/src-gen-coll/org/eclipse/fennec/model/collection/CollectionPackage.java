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
package org.eclipse.fennec.model.collection;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
 * @see org.eclipse.fennec.model.collection.CollectionFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel suppressInterfaces='false' containmentProxies='true' basePackage='org.eclipse.fennec.model' complianceLevel='21.0' copyrightText='Copyright (c) 2026 Contributors to the Eclipse Foundation.\n\nThis program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/\n\nSPDX-License-Identifier: EPL-2.0\n\nContributors:\n  Data In Motion Consulting - initial implementation' oSGiCompatible='true' resource='XMI'"
 *        annotation="Version value='2.2'"
 * @generated
 */
@ProviderType
@EPackage(uri = CollectionPackage.eNS_URI, genModel = "/model/collections.genmodel", genModelSourceLocations = {"model/collections.genmodel","org.eclipse.fennec.model/model/collections.genmodel"}, ecore = "/model/collections.ecore", ecoreSourceLocations = "/model/collections.ecore")
public interface CollectionPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "collection";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://org.eclipse/fennec/collection/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "collection";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CollectionPackage eINSTANCE = org.eclipse.fennec.model.collection.impl.CollectionPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.collection.ECollection <em>ECollection</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.collection.ECollection
	 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getECollection()
	 * @generated
	 */
	int ECOLLECTION = 0;

	/**
	 * The number of structural features of the '<em>ECollection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECOLLECTION_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Get Values</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECOLLECTION___GET_VALUES = 0;

	/**
	 * The number of operations of the '<em>ECollection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECOLLECTION_OPERATION_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.collection.impl.EContainmentCollectionImpl <em>EContainment Collection</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.collection.impl.EContainmentCollectionImpl
	 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEContainmentCollection()
	 * @generated
	 */
	int ECONTAINMENT_COLLECTION = 1;

	/**
	 * The feature id for the '<em><b>Values</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECONTAINMENT_COLLECTION__VALUES = ECOLLECTION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EContainment Collection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECONTAINMENT_COLLECTION_FEATURE_COUNT = ECOLLECTION_FEATURE_COUNT + 1;

	/**
	 * The operation id for the '<em>Get Values</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECONTAINMENT_COLLECTION___GET_VALUES = ECOLLECTION___GET_VALUES;

	/**
	 * The number of operations of the '<em>EContainment Collection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECONTAINMENT_COLLECTION_OPERATION_COUNT = ECOLLECTION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.collection.impl.EReferenceCollectionImpl <em>EReference Collection</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.collection.impl.EReferenceCollectionImpl
	 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEReferenceCollection()
	 * @generated
	 */
	int EREFERENCE_COLLECTION = 2;

	/**
	 * The feature id for the '<em><b>Values</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFERENCE_COLLECTION__VALUES = ECOLLECTION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EReference Collection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFERENCE_COLLECTION_FEATURE_COUNT = ECOLLECTION_FEATURE_COUNT + 1;

	/**
	 * The operation id for the '<em>Get Values</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFERENCE_COLLECTION___GET_VALUES = ECOLLECTION___GET_VALUES;

	/**
	 * The number of operations of the '<em>EReference Collection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFERENCE_COLLECTION_OPERATION_COUNT = ECOLLECTION_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link java.lang.Iterable <em>EIterable Interface</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.Iterable
	 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEIterableInterface()
	 * @generated
	 */
	int EITERABLE_INTERFACE = 4;

	/**
	 * The number of structural features of the '<em>EIterable Interface</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EITERABLE_INTERFACE_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Iterator</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EITERABLE_INTERFACE___ITERATOR = 0;

	/**
	 * The number of operations of the '<em>EIterable Interface</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EITERABLE_INTERFACE_OPERATION_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.collection.impl.EIterableImpl <em>EIterable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.collection.impl.EIterableImpl
	 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEIterable()
	 * @generated
	 */
	int EITERABLE = 3;

	/**
	 * The feature id for the '<em><b>Delegate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EITERABLE__DELEGATE = EITERABLE_INTERFACE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EIterable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EITERABLE_FEATURE_COUNT = EITERABLE_INTERFACE_FEATURE_COUNT + 1;

	/**
	 * The operation id for the '<em>Iterator</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EITERABLE___ITERATOR = EITERABLE_INTERFACE___ITERATOR;

	/**
	 * The number of operations of the '<em>EIterable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EITERABLE_OPERATION_COUNT = EITERABLE_INTERFACE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '<em>EList</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.EList
	 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEList()
	 * @generated
	 */
	int ELIST = 5;

	/**
	 * The meta object id for the '<em>EIterator</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.Iterator
	 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEIterator()
	 * @generated
	 */
	int EITERATOR = 6;

	/**
	 * The meta object id for the '<em>Iterable</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.Iterable
	 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getIterable()
	 * @generated
	 */
	int ITERABLE = 7;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.collection.ECollection <em>ECollection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ECollection</em>'.
	 * @see org.eclipse.fennec.model.collection.ECollection
	 * @generated
	 */
	EClass getECollection();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.collection.ECollection#getValues() <em>Get Values</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Values</em>' operation.
	 * @see org.eclipse.fennec.model.collection.ECollection#getValues()
	 * @generated
	 */
	EOperation getECollection__GetValues();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.collection.EContainmentCollection <em>EContainment Collection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EContainment Collection</em>'.
	 * @see org.eclipse.fennec.model.collection.EContainmentCollection
	 * @generated
	 */
	EClass getEContainmentCollection();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.collection.EContainmentCollection#getValues <em>Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Values</em>'.
	 * @see org.eclipse.fennec.model.collection.EContainmentCollection#getValues()
	 * @see #getEContainmentCollection()
	 * @generated
	 */
	EReference getEContainmentCollection_Values();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.collection.EReferenceCollection <em>EReference Collection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EReference Collection</em>'.
	 * @see org.eclipse.fennec.model.collection.EReferenceCollection
	 * @generated
	 */
	EClass getEReferenceCollection();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.model.collection.EReferenceCollection#getValues <em>Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Values</em>'.
	 * @see org.eclipse.fennec.model.collection.EReferenceCollection#getValues()
	 * @see #getEReferenceCollection()
	 * @generated
	 */
	EReference getEReferenceCollection_Values();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.collection.EIterable <em>EIterable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EIterable</em>'.
	 * @see org.eclipse.fennec.model.collection.EIterable
	 * @generated
	 */
	EClass getEIterable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.collection.EIterable#getDelegate <em>Delegate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Delegate</em>'.
	 * @see org.eclipse.fennec.model.collection.EIterable#getDelegate()
	 * @see #getEIterable()
	 * @generated
	 */
	EAttribute getEIterable_Delegate();

	/**
	 * Returns the meta object for class '{@link java.lang.Iterable <em>EIterable Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EIterable Interface</em>'.
	 * @see java.lang.Iterable
	 * @model instanceClass="java.lang.Iterable&lt;org.eclipse.emf.ecore.EObject&gt;"
	 * @generated
	 */
	EClass getEIterableInterface();

	/**
	 * Returns the meta object for the '{@link java.lang.Iterable#iterator() <em>Iterator</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Iterator</em>' operation.
	 * @see java.lang.Iterable#iterator()
	 * @generated
	 */
	EOperation getEIterableInterface__Iterator();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.EList <em>EList</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>EList</em>'.
	 * @see org.eclipse.emf.common.util.EList
	 * @model instanceClass="org.eclipse.emf.common.util.EList&lt;org.eclipse.emf.ecore.EObject&gt;"
	 * @generated
	 */
	EDataType getEList();

	/**
	 * Returns the meta object for data type '{@link java.util.Iterator <em>EIterator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>EIterator</em>'.
	 * @see java.util.Iterator
	 * @model instanceClass="java.util.Iterator&lt;org.eclipse.emf.ecore.EObject&gt;"
	 * @generated
	 */
	EDataType getEIterator();

	/**
	 * Returns the meta object for data type '{@link java.lang.Iterable <em>Iterable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Iterable</em>'.
	 * @see java.lang.Iterable
	 * @model instanceClass="java.lang.Iterable&lt;org.eclipse.emf.ecore.EObject&gt;"
	 * @generated
	 */
	EDataType getIterable();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CollectionFactory getCollectionFactory();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.collection.ECollection <em>ECollection</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.collection.ECollection
		 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getECollection()
		 * @generated
		 */
		EClass ECOLLECTION = eINSTANCE.getECollection();

		/**
		 * The meta object literal for the '<em><b>Get Values</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ECOLLECTION___GET_VALUES = eINSTANCE.getECollection__GetValues();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.collection.impl.EContainmentCollectionImpl <em>EContainment Collection</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.collection.impl.EContainmentCollectionImpl
		 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEContainmentCollection()
		 * @generated
		 */
		EClass ECONTAINMENT_COLLECTION = eINSTANCE.getEContainmentCollection();

		/**
		 * The meta object literal for the '<em><b>Values</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ECONTAINMENT_COLLECTION__VALUES = eINSTANCE.getEContainmentCollection_Values();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.collection.impl.EReferenceCollectionImpl <em>EReference Collection</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.collection.impl.EReferenceCollectionImpl
		 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEReferenceCollection()
		 * @generated
		 */
		EClass EREFERENCE_COLLECTION = eINSTANCE.getEReferenceCollection();

		/**
		 * The meta object literal for the '<em><b>Values</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EREFERENCE_COLLECTION__VALUES = eINSTANCE.getEReferenceCollection_Values();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.collection.impl.EIterableImpl <em>EIterable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.collection.impl.EIterableImpl
		 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEIterable()
		 * @generated
		 */
		EClass EITERABLE = eINSTANCE.getEIterable();

		/**
		 * The meta object literal for the '<em><b>Delegate</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EITERABLE__DELEGATE = eINSTANCE.getEIterable_Delegate();

		/**
		 * The meta object literal for the '{@link java.lang.Iterable <em>EIterable Interface</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.Iterable
		 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEIterableInterface()
		 * @generated
		 */
		EClass EITERABLE_INTERFACE = eINSTANCE.getEIterableInterface();

		/**
		 * The meta object literal for the '<em><b>Iterator</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation EITERABLE_INTERFACE___ITERATOR = eINSTANCE.getEIterableInterface__Iterator();

		/**
		 * The meta object literal for the '<em>EList</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.common.util.EList
		 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEList()
		 * @generated
		 */
		EDataType ELIST = eINSTANCE.getEList();

		/**
		 * The meta object literal for the '<em>EIterator</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.Iterator
		 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getEIterator()
		 * @generated
		 */
		EDataType EITERATOR = eINSTANCE.getEIterator();

		/**
		 * The meta object literal for the '<em>Iterable</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.Iterable
		 * @see org.eclipse.fennec.model.collection.impl.CollectionPackageImpl#getIterable()
		 * @generated
		 */
		EDataType ITERABLE = eINSTANCE.getIterable();

	}

} //CollectionPackage
