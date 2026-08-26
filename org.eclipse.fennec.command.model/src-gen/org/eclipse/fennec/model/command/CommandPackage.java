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
package org.eclipse.fennec.model.command;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
 * Write commands for the unified persistence layer — CUD deliberately lives here, not in the query language (docs/unified-persistence/concept.md §14): Delete is a query selector, Update is a selector plus a ChangeSet template from the stream model, Insert carries payload objects. Commands are EMF objects and persist/travel like everything else (dogfooding).
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.command.CommandFactory
 * @model kind="package"
 *        annotation="Version value='1.0'"
 * @generated
 */
@ProviderType
@EPackage(uri = CommandPackage.eNS_URI, fingerprint = "fp1:70a97c518570c62223f2828fb24af875242d655c5adbae077e5a311ec444cc5d", genModel = "/model/command.genmodel", genModelSourceLocations = {"model/command.genmodel","org.eclipse.fennec.command.model/model/command.genmodel"}, ecore = "/model/command.ecore", ecoreSourceLocations = "/model/command.ecore")
public interface CommandPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "command";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/command/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "cmd";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CommandPackage eINSTANCE = org.eclipse.fennec.model.command.impl.CommandPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.command.impl.CommandImpl <em>Command</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.command.impl.CommandImpl
	 * @see org.eclipse.fennec.model.command.impl.CommandPackageImpl#getCommand()
	 * @generated
	 */
	int COMMAND = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND__NAME = 0;

	/**
	 * The number of structural features of the '<em>Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.command.impl.InsertCommandImpl <em>Insert Command</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.command.impl.InsertCommandImpl
	 * @see org.eclipse.fennec.model.command.impl.CommandPackageImpl#getInsertCommand()
	 * @generated
	 */
	int INSERT_COMMAND = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_COMMAND__NAME = COMMAND__NAME;

	/**
	 * The feature id for the '<em><b>Objects</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_COMMAND__OBJECTS = COMMAND_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Insert Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_COMMAND_FEATURE_COUNT = COMMAND_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Insert Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_COMMAND_OPERATION_COUNT = COMMAND_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.command.impl.DeleteCommandImpl <em>Delete Command</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.command.impl.DeleteCommandImpl
	 * @see org.eclipse.fennec.model.command.impl.CommandPackageImpl#getDeleteCommand()
	 * @generated
	 */
	int DELETE_COMMAND = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE_COMMAND__NAME = COMMAND__NAME;

	/**
	 * The feature id for the '<em><b>Selector</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE_COMMAND__SELECTOR = COMMAND_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Delete Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE_COMMAND_FEATURE_COUNT = COMMAND_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Delete Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE_COMMAND_OPERATION_COUNT = COMMAND_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.command.impl.UpdateCommandImpl <em>Update Command</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.command.impl.UpdateCommandImpl
	 * @see org.eclipse.fennec.model.command.impl.CommandPackageImpl#getUpdateCommand()
	 * @generated
	 */
	int UPDATE_COMMAND = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_COMMAND__NAME = COMMAND__NAME;

	/**
	 * The feature id for the '<em><b>Selector</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_COMMAND__SELECTOR = COMMAND_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Template</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_COMMAND__TEMPLATE = COMMAND_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Update Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_COMMAND_FEATURE_COUNT = COMMAND_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Update Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_COMMAND_OPERATION_COUNT = COMMAND_OPERATION_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.command.Command <em>Command</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Command</em>'.
	 * @see org.eclipse.fennec.model.command.Command
	 * @generated
	 */
	EClass getCommand();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.command.Command#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.command.Command#getName()
	 * @see #getCommand()
	 * @generated
	 */
	EAttribute getCommand_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.command.InsertCommand <em>Insert Command</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Insert Command</em>'.
	 * @see org.eclipse.fennec.model.command.InsertCommand
	 * @generated
	 */
	EClass getInsertCommand();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.command.InsertCommand#getObjects <em>Objects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Objects</em>'.
	 * @see org.eclipse.fennec.model.command.InsertCommand#getObjects()
	 * @see #getInsertCommand()
	 * @generated
	 */
	EReference getInsertCommand_Objects();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.command.DeleteCommand <em>Delete Command</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Delete Command</em>'.
	 * @see org.eclipse.fennec.model.command.DeleteCommand
	 * @generated
	 */
	EClass getDeleteCommand();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.command.DeleteCommand#getSelector <em>Selector</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Selector</em>'.
	 * @see org.eclipse.fennec.model.command.DeleteCommand#getSelector()
	 * @see #getDeleteCommand()
	 * @generated
	 */
	EReference getDeleteCommand_Selector();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.command.UpdateCommand <em>Update Command</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Update Command</em>'.
	 * @see org.eclipse.fennec.model.command.UpdateCommand
	 * @generated
	 */
	EClass getUpdateCommand();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.command.UpdateCommand#getSelector <em>Selector</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Selector</em>'.
	 * @see org.eclipse.fennec.model.command.UpdateCommand#getSelector()
	 * @see #getUpdateCommand()
	 * @generated
	 */
	EReference getUpdateCommand_Selector();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.command.UpdateCommand#getTemplate <em>Template</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Template</em>'.
	 * @see org.eclipse.fennec.model.command.UpdateCommand#getTemplate()
	 * @see #getUpdateCommand()
	 * @generated
	 */
	EReference getUpdateCommand_Template();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CommandFactory getCommandFactory();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.command.impl.CommandImpl <em>Command</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.command.impl.CommandImpl
		 * @see org.eclipse.fennec.model.command.impl.CommandPackageImpl#getCommand()
		 * @generated
		 */
		EClass COMMAND = eINSTANCE.getCommand();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMMAND__NAME = eINSTANCE.getCommand_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.command.impl.InsertCommandImpl <em>Insert Command</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.command.impl.InsertCommandImpl
		 * @see org.eclipse.fennec.model.command.impl.CommandPackageImpl#getInsertCommand()
		 * @generated
		 */
		EClass INSERT_COMMAND = eINSTANCE.getInsertCommand();

		/**
		 * The meta object literal for the '<em><b>Objects</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INSERT_COMMAND__OBJECTS = eINSTANCE.getInsertCommand_Objects();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.command.impl.DeleteCommandImpl <em>Delete Command</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.command.impl.DeleteCommandImpl
		 * @see org.eclipse.fennec.model.command.impl.CommandPackageImpl#getDeleteCommand()
		 * @generated
		 */
		EClass DELETE_COMMAND = eINSTANCE.getDeleteCommand();

		/**
		 * The meta object literal for the '<em><b>Selector</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DELETE_COMMAND__SELECTOR = eINSTANCE.getDeleteCommand_Selector();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.command.impl.UpdateCommandImpl <em>Update Command</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.command.impl.UpdateCommandImpl
		 * @see org.eclipse.fennec.model.command.impl.CommandPackageImpl#getUpdateCommand()
		 * @generated
		 */
		EClass UPDATE_COMMAND = eINSTANCE.getUpdateCommand();

		/**
		 * The meta object literal for the '<em><b>Selector</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UPDATE_COMMAND__SELECTOR = eINSTANCE.getUpdateCommand_Selector();

		/**
		 * The meta object literal for the '<em><b>Template</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UPDATE_COMMAND__TEMPLATE = eINSTANCE.getUpdateCommand_Template();

	}

} //CommandPackage
