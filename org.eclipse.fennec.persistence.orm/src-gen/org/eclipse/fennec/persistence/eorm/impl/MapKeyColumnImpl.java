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

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.MapKeyColumn;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Map Key Column</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl#getColumnDefinition <em>Column Definition</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl#isInsertable <em>Insertable</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl#getLength <em>Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl#isNullable <em>Nullable</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl#getPrecision <em>Precision</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl#getScale <em>Scale</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl#getTable <em>Table</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl#isUnique <em>Unique</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl#isUpdatable <em>Updatable</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MapKeyColumnImpl extends MinimalEObjectImpl.Container implements MapKeyColumn {
	/**
	 * The default value of the '{@link #getColumnDefinition() <em>Column Definition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColumnDefinition()
	 * @generated
	 * @ordered
	 */
	protected static final String COLUMN_DEFINITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getColumnDefinition() <em>Column Definition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColumnDefinition()
	 * @generated
	 * @ordered
	 */
	protected String columnDefinition = COLUMN_DEFINITION_EDEFAULT;

	/**
	 * The default value of the '{@link #isInsertable() <em>Insertable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isInsertable()
	 * @generated
	 * @ordered
	 */
	protected static final boolean INSERTABLE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isInsertable() <em>Insertable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isInsertable()
	 * @generated
	 * @ordered
	 */
	protected boolean insertable = INSERTABLE_EDEFAULT;

	/**
	 * This is true if the Insertable attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean insertableESet;

	/**
	 * The default value of the '{@link #getLength() <em>Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLength()
	 * @generated
	 * @ordered
	 */
	protected static final int LENGTH_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getLength() <em>Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLength()
	 * @generated
	 * @ordered
	 */
	protected int length = LENGTH_EDEFAULT;

	/**
	 * This is true if the Length attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean lengthESet;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #isNullable() <em>Nullable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isNullable()
	 * @generated
	 * @ordered
	 */
	protected static final boolean NULLABLE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isNullable() <em>Nullable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isNullable()
	 * @generated
	 * @ordered
	 */
	protected boolean nullable = NULLABLE_EDEFAULT;

	/**
	 * This is true if the Nullable attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean nullableESet;

	/**
	 * The default value of the '{@link #getPrecision() <em>Precision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrecision()
	 * @generated
	 * @ordered
	 */
	protected static final int PRECISION_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getPrecision() <em>Precision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrecision()
	 * @generated
	 * @ordered
	 */
	protected int precision = PRECISION_EDEFAULT;

	/**
	 * This is true if the Precision attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean precisionESet;

	/**
	 * The default value of the '{@link #getScale() <em>Scale</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScale()
	 * @generated
	 * @ordered
	 */
	protected static final int SCALE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getScale() <em>Scale</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScale()
	 * @generated
	 * @ordered
	 */
	protected int scale = SCALE_EDEFAULT;

	/**
	 * This is true if the Scale attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean scaleESet;

	/**
	 * The default value of the '{@link #getTable() <em>Table</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTable()
	 * @generated
	 * @ordered
	 */
	protected static final String TABLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTable() <em>Table</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTable()
	 * @generated
	 * @ordered
	 */
	protected String table = TABLE_EDEFAULT;

	/**
	 * The default value of the '{@link #isUnique() <em>Unique</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUnique()
	 * @generated
	 * @ordered
	 */
	protected static final boolean UNIQUE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isUnique() <em>Unique</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUnique()
	 * @generated
	 * @ordered
	 */
	protected boolean unique = UNIQUE_EDEFAULT;

	/**
	 * This is true if the Unique attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean uniqueESet;

	/**
	 * The default value of the '{@link #isUpdatable() <em>Updatable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUpdatable()
	 * @generated
	 * @ordered
	 */
	protected static final boolean UPDATABLE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isUpdatable() <em>Updatable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUpdatable()
	 * @generated
	 * @ordered
	 */
	protected boolean updatable = UPDATABLE_EDEFAULT;

	/**
	 * This is true if the Updatable attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean updatableESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MapKeyColumnImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getMapKeyColumn();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getColumnDefinition() {
		return columnDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setColumnDefinition(String newColumnDefinition) {
		String oldColumnDefinition = columnDefinition;
		columnDefinition = newColumnDefinition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAP_KEY_COLUMN__COLUMN_DEFINITION, oldColumnDefinition, columnDefinition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isInsertable() {
		return insertable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setInsertable(boolean newInsertable) {
		boolean oldInsertable = insertable;
		insertable = newInsertable;
		boolean oldInsertableESet = insertableESet;
		insertableESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAP_KEY_COLUMN__INSERTABLE, oldInsertable, insertable, !oldInsertableESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetInsertable() {
		boolean oldInsertable = insertable;
		boolean oldInsertableESet = insertableESet;
		insertable = INSERTABLE_EDEFAULT;
		insertableESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.MAP_KEY_COLUMN__INSERTABLE, oldInsertable, INSERTABLE_EDEFAULT, oldInsertableESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetInsertable() {
		return insertableESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getLength() {
		return length;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLength(int newLength) {
		int oldLength = length;
		length = newLength;
		boolean oldLengthESet = lengthESet;
		lengthESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAP_KEY_COLUMN__LENGTH, oldLength, length, !oldLengthESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetLength() {
		int oldLength = length;
		boolean oldLengthESet = lengthESet;
		length = LENGTH_EDEFAULT;
		lengthESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.MAP_KEY_COLUMN__LENGTH, oldLength, LENGTH_EDEFAULT, oldLengthESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetLength() {
		return lengthESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAP_KEY_COLUMN__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNullable(boolean newNullable) {
		boolean oldNullable = nullable;
		nullable = newNullable;
		boolean oldNullableESet = nullableESet;
		nullableESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAP_KEY_COLUMN__NULLABLE, oldNullable, nullable, !oldNullableESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetNullable() {
		boolean oldNullable = nullable;
		boolean oldNullableESet = nullableESet;
		nullable = NULLABLE_EDEFAULT;
		nullableESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.MAP_KEY_COLUMN__NULLABLE, oldNullable, NULLABLE_EDEFAULT, oldNullableESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetNullable() {
		return nullableESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getPrecision() {
		return precision;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPrecision(int newPrecision) {
		int oldPrecision = precision;
		precision = newPrecision;
		boolean oldPrecisionESet = precisionESet;
		precisionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAP_KEY_COLUMN__PRECISION, oldPrecision, precision, !oldPrecisionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetPrecision() {
		int oldPrecision = precision;
		boolean oldPrecisionESet = precisionESet;
		precision = PRECISION_EDEFAULT;
		precisionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.MAP_KEY_COLUMN__PRECISION, oldPrecision, PRECISION_EDEFAULT, oldPrecisionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetPrecision() {
		return precisionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getScale() {
		return scale;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setScale(int newScale) {
		int oldScale = scale;
		scale = newScale;
		boolean oldScaleESet = scaleESet;
		scaleESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAP_KEY_COLUMN__SCALE, oldScale, scale, !oldScaleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetScale() {
		int oldScale = scale;
		boolean oldScaleESet = scaleESet;
		scale = SCALE_EDEFAULT;
		scaleESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.MAP_KEY_COLUMN__SCALE, oldScale, SCALE_EDEFAULT, oldScaleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetScale() {
		return scaleESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTable() {
		return table;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTable(String newTable) {
		String oldTable = table;
		table = newTable;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAP_KEY_COLUMN__TABLE, oldTable, table));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isUnique() {
		return unique;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUnique(boolean newUnique) {
		boolean oldUnique = unique;
		unique = newUnique;
		boolean oldUniqueESet = uniqueESet;
		uniqueESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAP_KEY_COLUMN__UNIQUE, oldUnique, unique, !oldUniqueESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetUnique() {
		boolean oldUnique = unique;
		boolean oldUniqueESet = uniqueESet;
		unique = UNIQUE_EDEFAULT;
		uniqueESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.MAP_KEY_COLUMN__UNIQUE, oldUnique, UNIQUE_EDEFAULT, oldUniqueESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetUnique() {
		return uniqueESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isUpdatable() {
		return updatable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUpdatable(boolean newUpdatable) {
		boolean oldUpdatable = updatable;
		updatable = newUpdatable;
		boolean oldUpdatableESet = updatableESet;
		updatableESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAP_KEY_COLUMN__UPDATABLE, oldUpdatable, updatable, !oldUpdatableESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetUpdatable() {
		boolean oldUpdatable = updatable;
		boolean oldUpdatableESet = updatableESet;
		updatable = UPDATABLE_EDEFAULT;
		updatableESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.MAP_KEY_COLUMN__UPDATABLE, oldUpdatable, UPDATABLE_EDEFAULT, oldUpdatableESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetUpdatable() {
		return updatableESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EORMPackage.MAP_KEY_COLUMN__COLUMN_DEFINITION:
				return getColumnDefinition();
			case EORMPackage.MAP_KEY_COLUMN__INSERTABLE:
				return isInsertable();
			case EORMPackage.MAP_KEY_COLUMN__LENGTH:
				return getLength();
			case EORMPackage.MAP_KEY_COLUMN__NAME:
				return getName();
			case EORMPackage.MAP_KEY_COLUMN__NULLABLE:
				return isNullable();
			case EORMPackage.MAP_KEY_COLUMN__PRECISION:
				return getPrecision();
			case EORMPackage.MAP_KEY_COLUMN__SCALE:
				return getScale();
			case EORMPackage.MAP_KEY_COLUMN__TABLE:
				return getTable();
			case EORMPackage.MAP_KEY_COLUMN__UNIQUE:
				return isUnique();
			case EORMPackage.MAP_KEY_COLUMN__UPDATABLE:
				return isUpdatable();
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
			case EORMPackage.MAP_KEY_COLUMN__COLUMN_DEFINITION:
				setColumnDefinition((String)newValue);
				return;
			case EORMPackage.MAP_KEY_COLUMN__INSERTABLE:
				setInsertable((Boolean)newValue);
				return;
			case EORMPackage.MAP_KEY_COLUMN__LENGTH:
				setLength((Integer)newValue);
				return;
			case EORMPackage.MAP_KEY_COLUMN__NAME:
				setName((String)newValue);
				return;
			case EORMPackage.MAP_KEY_COLUMN__NULLABLE:
				setNullable((Boolean)newValue);
				return;
			case EORMPackage.MAP_KEY_COLUMN__PRECISION:
				setPrecision((Integer)newValue);
				return;
			case EORMPackage.MAP_KEY_COLUMN__SCALE:
				setScale((Integer)newValue);
				return;
			case EORMPackage.MAP_KEY_COLUMN__TABLE:
				setTable((String)newValue);
				return;
			case EORMPackage.MAP_KEY_COLUMN__UNIQUE:
				setUnique((Boolean)newValue);
				return;
			case EORMPackage.MAP_KEY_COLUMN__UPDATABLE:
				setUpdatable((Boolean)newValue);
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
			case EORMPackage.MAP_KEY_COLUMN__COLUMN_DEFINITION:
				setColumnDefinition(COLUMN_DEFINITION_EDEFAULT);
				return;
			case EORMPackage.MAP_KEY_COLUMN__INSERTABLE:
				unsetInsertable();
				return;
			case EORMPackage.MAP_KEY_COLUMN__LENGTH:
				unsetLength();
				return;
			case EORMPackage.MAP_KEY_COLUMN__NAME:
				setName(NAME_EDEFAULT);
				return;
			case EORMPackage.MAP_KEY_COLUMN__NULLABLE:
				unsetNullable();
				return;
			case EORMPackage.MAP_KEY_COLUMN__PRECISION:
				unsetPrecision();
				return;
			case EORMPackage.MAP_KEY_COLUMN__SCALE:
				unsetScale();
				return;
			case EORMPackage.MAP_KEY_COLUMN__TABLE:
				setTable(TABLE_EDEFAULT);
				return;
			case EORMPackage.MAP_KEY_COLUMN__UNIQUE:
				unsetUnique();
				return;
			case EORMPackage.MAP_KEY_COLUMN__UPDATABLE:
				unsetUpdatable();
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
			case EORMPackage.MAP_KEY_COLUMN__COLUMN_DEFINITION:
				return COLUMN_DEFINITION_EDEFAULT == null ? columnDefinition != null : !COLUMN_DEFINITION_EDEFAULT.equals(columnDefinition);
			case EORMPackage.MAP_KEY_COLUMN__INSERTABLE:
				return isSetInsertable();
			case EORMPackage.MAP_KEY_COLUMN__LENGTH:
				return isSetLength();
			case EORMPackage.MAP_KEY_COLUMN__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case EORMPackage.MAP_KEY_COLUMN__NULLABLE:
				return isSetNullable();
			case EORMPackage.MAP_KEY_COLUMN__PRECISION:
				return isSetPrecision();
			case EORMPackage.MAP_KEY_COLUMN__SCALE:
				return isSetScale();
			case EORMPackage.MAP_KEY_COLUMN__TABLE:
				return TABLE_EDEFAULT == null ? table != null : !TABLE_EDEFAULT.equals(table);
			case EORMPackage.MAP_KEY_COLUMN__UNIQUE:
				return isSetUnique();
			case EORMPackage.MAP_KEY_COLUMN__UPDATABLE:
				return isSetUpdatable();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (columnDefinition: ");
		result.append(columnDefinition);
		result.append(", insertable: ");
		if (insertableESet) result.append(insertable); else result.append("<unset>");
		result.append(", length: ");
		if (lengthESet) result.append(length); else result.append("<unset>");
		result.append(", name: ");
		result.append(name);
		result.append(", nullable: ");
		if (nullableESet) result.append(nullable); else result.append("<unset>");
		result.append(", precision: ");
		if (precisionESet) result.append(precision); else result.append("<unset>");
		result.append(", scale: ");
		if (scaleESet) result.append(scale); else result.append("<unset>");
		result.append(", table: ");
		result.append(table);
		result.append(", unique: ");
		if (uniqueESet) result.append(unique); else result.append("<unset>");
		result.append(", updatable: ");
		if (updatableESet) result.append(updatable); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //MapKeyColumnImpl
