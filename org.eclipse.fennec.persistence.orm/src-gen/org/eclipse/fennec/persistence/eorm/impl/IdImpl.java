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
package org.eclipse.fennec.persistence.eorm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.GeneratedValue;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.eorm.SequenceGenerator;
import org.eclipse.fennec.persistence.eorm.TableGenerator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Id</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.IdImpl#getGeneratedValue <em>Generated Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.IdImpl#getTableGenerator <em>Table Generator</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.IdImpl#getSequenceGenerator <em>Sequence Generator</em>}</li>
 * </ul>
 *
 * @generated
 */
public class IdImpl extends BaseImpl implements Id {
	/**
	 * The cached value of the '{@link #getGeneratedValue() <em>Generated Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGeneratedValue()
	 * @generated
	 * @ordered
	 */
	protected GeneratedValue generatedValue;

	/**
	 * The cached value of the '{@link #getTableGenerator() <em>Table Generator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTableGenerator()
	 * @generated
	 * @ordered
	 */
	protected TableGenerator tableGenerator;

	/**
	 * The cached value of the '{@link #getSequenceGenerator() <em>Sequence Generator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSequenceGenerator()
	 * @generated
	 * @ordered
	 */
	protected SequenceGenerator sequenceGenerator;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IdImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getId();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeneratedValue getGeneratedValue() {
		return generatedValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGeneratedValue(GeneratedValue newGeneratedValue, NotificationChain msgs) {
		GeneratedValue oldGeneratedValue = generatedValue;
		generatedValue = newGeneratedValue;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ID__GENERATED_VALUE, oldGeneratedValue, newGeneratedValue);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setGeneratedValue(GeneratedValue newGeneratedValue) {
		if (newGeneratedValue != generatedValue) {
			NotificationChain msgs = null;
			if (generatedValue != null)
				msgs = ((InternalEObject)generatedValue).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ID__GENERATED_VALUE, null, msgs);
			if (newGeneratedValue != null)
				msgs = ((InternalEObject)newGeneratedValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ID__GENERATED_VALUE, null, msgs);
			msgs = basicSetGeneratedValue(newGeneratedValue, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ID__GENERATED_VALUE, newGeneratedValue, newGeneratedValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TableGenerator getTableGenerator() {
		return tableGenerator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTableGenerator(TableGenerator newTableGenerator, NotificationChain msgs) {
		TableGenerator oldTableGenerator = tableGenerator;
		tableGenerator = newTableGenerator;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ID__TABLE_GENERATOR, oldTableGenerator, newTableGenerator);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTableGenerator(TableGenerator newTableGenerator) {
		if (newTableGenerator != tableGenerator) {
			NotificationChain msgs = null;
			if (tableGenerator != null)
				msgs = ((InternalEObject)tableGenerator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ID__TABLE_GENERATOR, null, msgs);
			if (newTableGenerator != null)
				msgs = ((InternalEObject)newTableGenerator).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ID__TABLE_GENERATOR, null, msgs);
			msgs = basicSetTableGenerator(newTableGenerator, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ID__TABLE_GENERATOR, newTableGenerator, newTableGenerator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SequenceGenerator getSequenceGenerator() {
		return sequenceGenerator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSequenceGenerator(SequenceGenerator newSequenceGenerator, NotificationChain msgs) {
		SequenceGenerator oldSequenceGenerator = sequenceGenerator;
		sequenceGenerator = newSequenceGenerator;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ID__SEQUENCE_GENERATOR, oldSequenceGenerator, newSequenceGenerator);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSequenceGenerator(SequenceGenerator newSequenceGenerator) {
		if (newSequenceGenerator != sequenceGenerator) {
			NotificationChain msgs = null;
			if (sequenceGenerator != null)
				msgs = ((InternalEObject)sequenceGenerator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ID__SEQUENCE_GENERATOR, null, msgs);
			if (newSequenceGenerator != null)
				msgs = ((InternalEObject)newSequenceGenerator).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ID__SEQUENCE_GENERATOR, null, msgs);
			msgs = basicSetSequenceGenerator(newSequenceGenerator, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ID__SEQUENCE_GENERATOR, newSequenceGenerator, newSequenceGenerator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.ID__GENERATED_VALUE:
				return basicSetGeneratedValue(null, msgs);
			case EORMPackage.ID__TABLE_GENERATOR:
				return basicSetTableGenerator(null, msgs);
			case EORMPackage.ID__SEQUENCE_GENERATOR:
				return basicSetSequenceGenerator(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EORMPackage.ID__GENERATED_VALUE:
				return getGeneratedValue();
			case EORMPackage.ID__TABLE_GENERATOR:
				return getTableGenerator();
			case EORMPackage.ID__SEQUENCE_GENERATOR:
				return getSequenceGenerator();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case EORMPackage.ID__GENERATED_VALUE:
				setGeneratedValue((GeneratedValue)newValue);
				return;
			case EORMPackage.ID__TABLE_GENERATOR:
				setTableGenerator((TableGenerator)newValue);
				return;
			case EORMPackage.ID__SEQUENCE_GENERATOR:
				setSequenceGenerator((SequenceGenerator)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case EORMPackage.ID__GENERATED_VALUE:
				setGeneratedValue((GeneratedValue)null);
				return;
			case EORMPackage.ID__TABLE_GENERATOR:
				setTableGenerator((TableGenerator)null);
				return;
			case EORMPackage.ID__SEQUENCE_GENERATOR:
				setSequenceGenerator((SequenceGenerator)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case EORMPackage.ID__GENERATED_VALUE:
				return generatedValue != null;
			case EORMPackage.ID__TABLE_GENERATOR:
				return tableGenerator != null;
			case EORMPackage.ID__SEQUENCE_GENERATOR:
				return sequenceGenerator != null;
		}
		return super.eIsSet(featureID);
	}

} //IdImpl
