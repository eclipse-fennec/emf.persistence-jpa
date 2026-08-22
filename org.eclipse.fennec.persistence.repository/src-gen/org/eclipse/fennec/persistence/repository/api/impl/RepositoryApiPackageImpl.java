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
package org.eclipse.fennec.persistence.repository.api.impl;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.model.command.CommandPackage;

import org.eclipse.fennec.model.expression.ExpressionPackage;

import org.eclipse.fennec.model.query.QueryPackage;

import org.eclipse.fennec.model.stream.StreamPackage;

import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;

import org.eclipse.fennec.persistence.query.api.QueryApiPackage;

import org.eclipse.fennec.persistence.repository.api.PreparedQuery;
import org.eclipse.fennec.persistence.repository.api.ReadRepository;
import org.eclipse.fennec.persistence.repository.api.Repository;
import org.eclipse.fennec.persistence.repository.api.RepositoryApiFactory;
import org.eclipse.fennec.persistence.repository.api.RepositoryApiPackage;
import org.eclipse.fennec.persistence.repository.api.RepositoryService;
import org.eclipse.fennec.persistence.repository.api.WriteRepository;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RepositoryApiPackageImpl extends EPackageImpl implements RepositoryApiPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass repositoryServiceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass readRepositoryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass preparedQueryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass writeRepositoryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass repositoryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType uriEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType persistenceCapabilitiesEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType eObjectCollectionEDataType = null;

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
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryApiPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private RepositoryApiPackageImpl() {
		super(eNS_URI, RepositoryApiFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link RepositoryApiPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static RepositoryApiPackage init() {
		if (isInited) return (RepositoryApiPackage)EPackage.Registry.INSTANCE.getEPackage(RepositoryApiPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredRepositoryApiPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		RepositoryApiPackageImpl theRepositoryApiPackage = registeredRepositoryApiPackage instanceof RepositoryApiPackageImpl ? (RepositoryApiPackageImpl)registeredRepositoryApiPackage : new RepositoryApiPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		QueryApiPackage.eINSTANCE.eClass();
		QueryPackage.eINSTANCE.eClass();
		ExpressionPackage.eINSTANCE.eClass();
		CommandPackage.eINSTANCE.eClass();
		StreamPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theRepositoryApiPackage.createPackageContents();

		// Initialize created meta-data
		theRepositoryApiPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theRepositoryApiPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(RepositoryApiPackage.eNS_URI, theRepositoryApiPackage);
		return theRepositoryApiPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getRepositoryService() {
		return repositoryServiceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__Id() {
		return repositoryServiceEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__BaseUri() {
		return repositoryServiceEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__Capabilities() {
		return repositoryServiceEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__IsDisposed() {
		return repositoryServiceEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__Dispose() {
		return repositoryServiceEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__GetResourceSet() {
		return repositoryServiceEClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__CreateResourceSet() {
		return repositoryServiceEClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__CreateUri__EObject() {
		return repositoryServiceEClass.getEOperations().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__CreateUri__EObject_Map() {
		return repositoryServiceEClass.getEOperations().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__CreateUri__EClass_Object() {
		return repositoryServiceEClass.getEOperations().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__CreateProxy__EClass_Object() {
		return repositoryServiceEClass.getEOperations().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__Proxify__EObject() {
		return repositoryServiceEClass.getEOperations().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__Attach__EObject() {
		return repositoryServiceEClass.getEOperations().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__Attach__EObject_Map() {
		return repositoryServiceEClass.getEOperations().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getRepositoryService__Detach__EObject() {
		return repositoryServiceEClass.getEOperations().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getReadRepository() {
		return readRepositoryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__GetResource__URI_boolean() {
		return readRepositoryEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__GetEObject__URI() {
		return readRepositoryEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__GetEObject__URI_Map() {
		return readRepositoryEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__GetEObject__EClass_Object() {
		return readRepositoryEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__GetEObject__EClass_Object_Map() {
		return readRepositoryEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__GetAllEObjects__EClass() {
		return readRepositoryEClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__GetAllEObjects__EClass_Map() {
		return readRepositoryEClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__Count__EClass() {
		return readRepositoryEClass.getEOperations().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__Count__EClass_Map() {
		return readRepositoryEClass.getEOperations().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__Exist__URI() {
		return readRepositoryEClass.getEOperations().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__Exist__EClass_Object() {
		return readRepositoryEClass.getEOperations().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__Reload__EObject() {
		return readRepositoryEClass.getEOperations().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__Find__Query() {
		return readRepositoryEClass.getEOperations().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__Find__Query_Map_Map() {
		return readRepositoryEClass.getEOperations().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__Find__String_Map_Map() {
		return readRepositoryEClass.getEOperations().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__Count__Query() {
		return readRepositoryEClass.getEOperations().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__Prepare__Query() {
		return readRepositoryEClass.getEOperations().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getReadRepository__Prepare__String() {
		return readRepositoryEClass.getEOperations().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPreparedQuery() {
		return preparedQueryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getPreparedQuery__Name() {
		return preparedQueryEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getPreparedQuery__Query() {
		return preparedQueryEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getPreparedQuery__ParameterDeclarations() {
		return preparedQueryEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getPreparedQuery__Execute__Map() {
		return preparedQueryEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getPreparedQuery__Execute__Map_Map() {
		return preparedQueryEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getWriteRepository() {
		return writeRepositoryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__Save__EObject() {
		return writeRepositoryEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__Save__EObject_Map() {
		return writeRepositoryEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__Save__EObject_URI() {
		return writeRepositoryEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__Save__EObject_URI_Map() {
		return writeRepositoryEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__SaveAll__Collection() {
		return writeRepositoryEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__SaveAll__Collection_Map() {
		return writeRepositoryEClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__Delete__EObject() {
		return writeRepositoryEClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__Delete__EObject_Map() {
		return writeRepositoryEClass.getEOperations().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__Delete__URI() {
		return writeRepositoryEClass.getEOperations().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__Delete__URI_Map() {
		return writeRepositoryEClass.getEOperations().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__Execute__Command() {
		return writeRepositoryEClass.getEOperations().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWriteRepository__Execute__Command_Map_Map() {
		return writeRepositoryEClass.getEOperations().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getRepository() {
		return repositoryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getUri() {
		return uriEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getPersistenceCapabilities() {
		return persistenceCapabilitiesEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getEObjectCollection() {
		return eObjectCollectionEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public RepositoryApiFactory getRepositoryApiFactory() {
		return (RepositoryApiFactory)getEFactoryInstance();
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

		// Create classes and their features
		repositoryServiceEClass = createEClass(REPOSITORY_SERVICE);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___ID);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___BASE_URI);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___CAPABILITIES);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___IS_DISPOSED);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___DISPOSE);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___GET_RESOURCE_SET);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___CREATE_RESOURCE_SET);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___CREATE_URI__EOBJECT);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___CREATE_URI__EOBJECT_MAP);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___CREATE_URI__ECLASS_OBJECT);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___CREATE_PROXY__ECLASS_OBJECT);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___PROXIFY__EOBJECT);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___ATTACH__EOBJECT);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___ATTACH__EOBJECT_MAP);
		createEOperation(repositoryServiceEClass, REPOSITORY_SERVICE___DETACH__EOBJECT);

		readRepositoryEClass = createEClass(READ_REPOSITORY);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___GET_RESOURCE__URI_BOOLEAN);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___GET_EOBJECT__URI);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___GET_EOBJECT__URI_MAP);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___GET_EOBJECT__ECLASS_OBJECT);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___GET_EOBJECT__ECLASS_OBJECT_MAP);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___GET_ALL_EOBJECTS__ECLASS);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___GET_ALL_EOBJECTS__ECLASS_MAP);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___COUNT__ECLASS);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___COUNT__ECLASS_MAP);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___EXIST__URI);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___EXIST__ECLASS_OBJECT);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___RELOAD__EOBJECT);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___FIND__QUERY);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___FIND__QUERY_MAP_MAP);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___FIND__STRING_MAP_MAP);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___COUNT__QUERY);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___PREPARE__QUERY);
		createEOperation(readRepositoryEClass, READ_REPOSITORY___PREPARE__STRING);

		preparedQueryEClass = createEClass(PREPARED_QUERY);
		createEOperation(preparedQueryEClass, PREPARED_QUERY___NAME);
		createEOperation(preparedQueryEClass, PREPARED_QUERY___QUERY);
		createEOperation(preparedQueryEClass, PREPARED_QUERY___PARAMETER_DECLARATIONS);
		createEOperation(preparedQueryEClass, PREPARED_QUERY___EXECUTE__MAP);
		createEOperation(preparedQueryEClass, PREPARED_QUERY___EXECUTE__MAP_MAP);

		writeRepositoryEClass = createEClass(WRITE_REPOSITORY);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___SAVE__EOBJECT);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___SAVE__EOBJECT_MAP);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___SAVE__EOBJECT_URI);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___SAVE__EOBJECT_URI_MAP);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___SAVE_ALL__COLLECTION);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___SAVE_ALL__COLLECTION_MAP);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___DELETE__EOBJECT);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___DELETE__EOBJECT_MAP);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___DELETE__URI);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___DELETE__URI_MAP);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___EXECUTE__COMMAND);
		createEOperation(writeRepositoryEClass, WRITE_REPOSITORY___EXECUTE__COMMAND_MAP_MAP);

		repositoryEClass = createEClass(REPOSITORY);

		// Create data types
		uriEDataType = createEDataType(URI);
		persistenceCapabilitiesEDataType = createEDataType(PERSISTENCE_CAPABILITIES);
		eObjectCollectionEDataType = createEDataType(EOBJECT_COLLECTION);
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

		// Obtain other dependent packages
		QueryApiPackage theQueryApiPackage = (QueryApiPackage)EPackage.Registry.INSTANCE.getEPackage(QueryApiPackage.eNS_URI);
		QueryPackage theQueryPackage = (QueryPackage)EPackage.Registry.INSTANCE.getEPackage(QueryPackage.eNS_URI);
		CommandPackage theCommandPackage = (CommandPackage)EPackage.Registry.INSTANCE.getEPackage(CommandPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		repositoryServiceEClass.getESuperTypes().add(theQueryApiPackage.getAutoCloseable());
		readRepositoryEClass.getESuperTypes().add(this.getRepositoryService());
		writeRepositoryEClass.getESuperTypes().add(this.getRepositoryService());
		repositoryEClass.getESuperTypes().add(this.getReadRepository());
		repositoryEClass.getESuperTypes().add(this.getWriteRepository());

		// Initialize classes, features, and operations; add parameters
		initEClass(repositoryServiceEClass, RepositoryService.class, "RepositoryService", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getRepositoryService__Id(), ecorePackage.getEString(), "id", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getRepositoryService__BaseUri(), this.getUri(), "baseUri", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getRepositoryService__Capabilities(), this.getPersistenceCapabilities(), "capabilities", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getRepositoryService__IsDisposed(), ecorePackage.getEBoolean(), "isDisposed", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getRepositoryService__Dispose(), null, "dispose", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getRepositoryService__GetResourceSet(), ecorePackage.getEResourceSet(), "getResourceSet", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getRepositoryService__CreateResourceSet(), ecorePackage.getEResourceSet(), "createResourceSet", 0, 1, IS_UNIQUE, IS_ORDERED);

		EOperation op = initEOperation(getRepositoryService__CreateUri__EObject(), this.getUri(), "createUri", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getRepositoryService__CreateUri__EObject_Map(), this.getUri(), "createUri", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getRepositoryService__CreateUri__EClass_Object(), this.getUri(), "createUri", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEJavaObject(), "id", 1, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getRepositoryService__CreateProxy__EClass_Object(), ecorePackage.getEObject(), "createProxy", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEJavaObject(), "id", 1, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getRepositoryService__Proxify__EObject(), null, "proxify", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getRepositoryService__Attach__EObject(), ecorePackage.getEResource(), "attach", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getRepositoryService__Attach__EObject_Map(), ecorePackage.getEResource(), "attach", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getRepositoryService__Detach__EObject(), ecorePackage.getEObject(), "detach", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(readRepositoryEClass, ReadRepository.class, "ReadRepository", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		op = initEOperation(getReadRepository__GetResource__URI_boolean(), ecorePackage.getEResource(), "getResource", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getUri(), "uri", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "loadOnDemand", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__GetEObject__URI(), ecorePackage.getEObject(), "getEObject", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getUri(), "uri", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__GetEObject__URI_Map(), ecorePackage.getEObject(), "getEObject", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getUri(), "uri", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__GetEObject__EClass_Object(), ecorePackage.getEObject(), "getEObject", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEJavaObject(), "id", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__GetEObject__EClass_Object_Map(), ecorePackage.getEObject(), "getEObject", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEJavaObject(), "id", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__GetAllEObjects__EClass(), theQueryApiPackage.getEObjectStream(), "getAllEObjects", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__GetAllEObjects__EClass_Map(), theQueryApiPackage.getEObjectStream(), "getAllEObjects", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__Count__EClass(), ecorePackage.getELong(), "count", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__Count__EClass_Map(), ecorePackage.getELong(), "count", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__Exist__URI(), ecorePackage.getEBoolean(), "exist", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getUri(), "uri", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__Exist__EClass_Object(), ecorePackage.getEBoolean(), "exist", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEJavaObject(), "id", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__Reload__EObject(), null, "reload", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__Find__Query(), theQueryApiPackage.getQueryResult(), "find", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryPackage.getQuery(), "query", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__Find__Query_Map_Map(), theQueryApiPackage.getQueryResult(), "find", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryPackage.getQuery(), "query", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getParameterMap(), "parameters", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__Find__String_Map_Map(), theQueryApiPackage.getQueryResult(), "find", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "name", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getParameterMap(), "parameters", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__Count__Query(), ecorePackage.getELong(), "count", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryPackage.getQuery(), "query", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__Prepare__Query(), this.getPreparedQuery(), "prepare", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryPackage.getQuery(), "query", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getReadRepository__Prepare__String(), this.getPreparedQuery(), "prepare", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "name", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		initEClass(preparedQueryEClass, PreparedQuery.class, "PreparedQuery", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getPreparedQuery__Name(), ecorePackage.getEString(), "name", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getPreparedQuery__Query(), theQueryPackage.getQuery(), "query", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getPreparedQuery__ParameterDeclarations(), theQueryPackage.getParameterDecl(), "parameterDeclarations", 0, -1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getPreparedQuery__Execute__Map(), theQueryApiPackage.getQueryResult(), "execute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getParameterMap(), "parameters", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getPreparedQuery__Execute__Map_Map(), theQueryApiPackage.getQueryResult(), "execute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getParameterMap(), "parameters", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		initEClass(writeRepositoryEClass, WriteRepository.class, "WriteRepository", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		op = initEOperation(getWriteRepository__Save__EObject(), null, "save", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getWriteRepository__Save__EObject_Map(), null, "save", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getWriteRepository__Save__EObject_URI(), null, "save", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getUri(), "uri", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getWriteRepository__Save__EObject_URI_Map(), null, "save", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getUri(), "uri", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getWriteRepository__SaveAll__Collection(), null, "saveAll", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getEObjectCollection(), "objects", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getWriteRepository__SaveAll__Collection_Map(), null, "saveAll", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getEObjectCollection(), "objects", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getWriteRepository__Delete__EObject(), null, "delete", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getWriteRepository__Delete__EObject_Map(), null, "delete", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getWriteRepository__Delete__URI(), null, "delete", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getUri(), "uri", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getWriteRepository__Delete__URI_Map(), null, "delete", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getUri(), "uri", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getWriteRepository__Execute__Command(), ecorePackage.getELong(), "execute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theCommandPackage.getCommand(), "command", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		op = initEOperation(getWriteRepository__Execute__Command_Map_Map(), ecorePackage.getELong(), "execute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theCommandPackage.getCommand(), "command", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getParameterMap(), "parameters", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryApiPackage.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theQueryApiPackage.getIOException());

		initEClass(repositoryEClass, Repository.class, "Repository", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Initialize data types
		initEDataType(uriEDataType, org.eclipse.emf.common.util.URI.class, "Uri", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(persistenceCapabilitiesEDataType, PersistenceCapabilities.class, "PersistenceCapabilities", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(eObjectCollectionEDataType, Collection.class, "EObjectCollection", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.Collection<org.eclipse.emf.ecore.EObject>");

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

} //RepositoryApiPackageImpl
