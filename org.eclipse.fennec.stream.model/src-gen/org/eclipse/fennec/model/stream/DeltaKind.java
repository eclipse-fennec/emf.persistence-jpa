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
package org.eclipse.fennec.model.stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Delta Kind</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Delta kinds per concept §5.2. Additive-evolution discipline: new literals may be appended, existing values are never renumbered or rebound.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.stream.StreamPackage#getDeltaKind()
 * @model
 * @generated
 */
@ProviderType
public enum DeltaKind implements Enumerator {
	/**
	 * The '<em><b>CREATE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Genesis of an object: objectId + classId; initial values follow as SET/ADD/PUT entries in the same batch (§5.3). Inverse: DELETE.
	 * <!-- end-model-doc -->
	 * @see #CREATE_VALUE
	 * @generated
	 * @ordered
	 */
	CREATE(0, "CREATE", "CREATE"),

	/**
	 * The '<em><b>SET</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Single-valued feature set (also: whole-array replacement in ATOMIC mode, §5.4i). Inverse: SET with swapped values / UNSET.
	 * <!-- end-model-doc -->
	 * @see #SET_VALUE
	 * @generated
	 * @ordered
	 */
	SET(1, "SET", "SET"),

	/**
	 * The '<em><b>UNSET</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Single-valued feature unset. Inverse: SET restoring valueOld.
	 * <!-- end-model-doc -->
	 * @see #UNSET_VALUE
	 * @generated
	 * @ordered
	 */
	UNSET(2, "UNSET", "UNSET"),

	/**
	 * The '<em><b>ADD</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Many-valued feature: value added at 'index' (ordered) or logically (unordered — commutes, §5.4b). Inverse: REMOVE.
	 * <!-- end-model-doc -->
	 * @see #ADD_VALUE
	 * @generated
	 * @ordered
	 */
	ADD(3, "ADD", "ADD"),

	/**
	 * The '<em><b>REMOVE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Many-valued feature: value removed at 'index' (always by index — unique=false lists may hold duplicates, §5.4b). Inverse: ADD.
	 * <!-- end-model-doc -->
	 * @see #REMOVE_VALUE
	 * @generated
	 * @ordered
	 */
	REMOVE(4, "REMOVE", "REMOVE"),

	/**
	 * The '<em><b>MOVE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ordered list: element moved from 'index' to 'toIndex'. Inverse: MOVE back.
	 * <!-- end-model-doc -->
	 * @see #MOVE_VALUE
	 * @generated
	 * @ordered
	 */
	MOVE(5, "MOVE", "MOVE"),

	/**
	 * The '<em><b>PUT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Map: value stored under 'key' (§5.4h). PUTs on distinct keys commute. Inverse: PUT with swapped values, or REMOVE_KEY if the key was absent.
	 * <!-- end-model-doc -->
	 * @see #PUT_VALUE
	 * @generated
	 * @ordered
	 */
	PUT(6, "PUT", "PUT"),

	/**
	 * The '<em><b>REMOVE KEY</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Map: key removed. Inverse: PUT restoring valueOld.
	 * <!-- end-model-doc -->
	 * @see #REMOVE_KEY_VALUE
	 * @generated
	 * @ordered
	 */
	REMOVE_KEY(7, "REMOVE_KEY", "REMOVE_KEY"),

	/**
	 * The '<em><b>SET AT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Array (ELEMENT_WISE): cell at 'coords' set (§5.4i). SET_ATs on distinct coordinates commute. Inverse: SET_AT with swapped values.
	 * <!-- end-model-doc -->
	 * @see #SET_AT_VALUE
	 * @generated
	 * @ordered
	 */
	SET_AT(8, "SET_AT", "SET_AT"),

	/**
	 * The '<em><b>RESHAPE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Array (ELEMENT_WISE): dimensionality changed; old/new dimension vectors travel encoded in valueOld/valueNew. Conflicts with every cell — forces strict fault handling (§11). Inverse: RESHAPE back.
	 * <!-- end-model-doc -->
	 * @see #RESHAPE_VALUE
	 * @generated
	 * @ordered
	 */
	RESHAPE(9, "RESHAPE", "RESHAPE"),

	/**
	 * The '<em><b>DELETE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Tombstone: the object leaves the stream. Cascades are explicit — the batch carries tombstones for the whole containment subtree, child-first (§5.4d). Inverse: CREATE + replay, or keyframe restore.
	 * <!-- end-model-doc -->
	 * @see #DELETE_VALUE
	 * @generated
	 * @ordered
	 */
	DELETE(10, "DELETE", "DELETE"),

	/**
	 * The '<em><b>KEYFRAME</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Full tracked state of the object at this sequence, as SlotValue list in 'state' (§10). Replay/retention/resync anchor; informational, not inverted.
	 * <!-- end-model-doc -->
	 * @see #KEYFRAME_VALUE
	 * @generated
	 * @ordered
	 */
	KEYFRAME(11, "KEYFRAME", "KEYFRAME"),

	/**
	 * The '<em><b>TOUCH</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Audit mode TOUCHED_FEATURES (tracking aspect): the feature was modified, values deliberately elided (data minimization). Informational; not invertible, excluded from conflict detection.
	 * <!-- end-model-doc -->
	 * @see #TOUCH_VALUE
	 * @generated
	 * @ordered
	 */
	TOUCH(12, "TOUCH", "TOUCH"),

	/**
	 * The '<em><b>MIGRATE OUT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Object leaves THIS stream because it moved to another aggregate (§5.4e). Tombstone variant: valueNew carries the target streamId (forward reference). One entry per migrated object — subtrees migrate with explicit per-object pairs, child-first; linked to the target-stream batch via ChangeSet.transactionId. Inverse: the mirrored MIGRATE_IN/OUT pair.
	 * <!-- end-model-doc -->
	 * @see #MIGRATE_OUT_VALUE
	 * @generated
	 * @ordered
	 */
	MIGRATE_OUT(13, "MIGRATE_OUT", "MIGRATE_OUT"),

	/**
	 * The '<em><b>MIGRATE IN</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Object enters THIS stream by migration (§5.4e). Genesis variant: valueOld carries the origin 'streamId#sequence' (origin reference); followed by a KEYFRAME of the object so the receiving stream replays standalone. Parent-first for subtrees; may open a brand-new stream (child promoted to aggregate root). Inverse: the mirrored pair.
	 * <!-- end-model-doc -->
	 * @see #MIGRATE_IN_VALUE
	 * @generated
	 * @ordered
	 */
	MIGRATE_IN(14, "MIGRATE_IN", "MIGRATE_IN");

	/**
	 * The '<em><b>CREATE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Genesis of an object: objectId + classId; initial values follow as SET/ADD/PUT entries in the same batch (§5.3). Inverse: DELETE.
	 * <!-- end-model-doc -->
	 * @see #CREATE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CREATE_VALUE = 0;

	/**
	 * The '<em><b>SET</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Single-valued feature set (also: whole-array replacement in ATOMIC mode, §5.4i). Inverse: SET with swapped values / UNSET.
	 * <!-- end-model-doc -->
	 * @see #SET
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SET_VALUE = 1;

	/**
	 * The '<em><b>UNSET</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Single-valued feature unset. Inverse: SET restoring valueOld.
	 * <!-- end-model-doc -->
	 * @see #UNSET
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int UNSET_VALUE = 2;

	/**
	 * The '<em><b>ADD</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Many-valued feature: value added at 'index' (ordered) or logically (unordered — commutes, §5.4b). Inverse: REMOVE.
	 * <!-- end-model-doc -->
	 * @see #ADD
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ADD_VALUE = 3;

	/**
	 * The '<em><b>REMOVE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Many-valued feature: value removed at 'index' (always by index — unique=false lists may hold duplicates, §5.4b). Inverse: ADD.
	 * <!-- end-model-doc -->
	 * @see #REMOVE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int REMOVE_VALUE = 4;

	/**
	 * The '<em><b>MOVE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ordered list: element moved from 'index' to 'toIndex'. Inverse: MOVE back.
	 * <!-- end-model-doc -->
	 * @see #MOVE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int MOVE_VALUE = 5;

	/**
	 * The '<em><b>PUT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Map: value stored under 'key' (§5.4h). PUTs on distinct keys commute. Inverse: PUT with swapped values, or REMOVE_KEY if the key was absent.
	 * <!-- end-model-doc -->
	 * @see #PUT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int PUT_VALUE = 6;

	/**
	 * The '<em><b>REMOVE KEY</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Map: key removed. Inverse: PUT restoring valueOld.
	 * <!-- end-model-doc -->
	 * @see #REMOVE_KEY
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int REMOVE_KEY_VALUE = 7;

	/**
	 * The '<em><b>SET AT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Array (ELEMENT_WISE): cell at 'coords' set (§5.4i). SET_ATs on distinct coordinates commute. Inverse: SET_AT with swapped values.
	 * <!-- end-model-doc -->
	 * @see #SET_AT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SET_AT_VALUE = 8;

	/**
	 * The '<em><b>RESHAPE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Array (ELEMENT_WISE): dimensionality changed; old/new dimension vectors travel encoded in valueOld/valueNew. Conflicts with every cell — forces strict fault handling (§11). Inverse: RESHAPE back.
	 * <!-- end-model-doc -->
	 * @see #RESHAPE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int RESHAPE_VALUE = 9;

	/**
	 * The '<em><b>DELETE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Tombstone: the object leaves the stream. Cascades are explicit — the batch carries tombstones for the whole containment subtree, child-first (§5.4d). Inverse: CREATE + replay, or keyframe restore.
	 * <!-- end-model-doc -->
	 * @see #DELETE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int DELETE_VALUE = 10;

	/**
	 * The '<em><b>KEYFRAME</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Full tracked state of the object at this sequence, as SlotValue list in 'state' (§10). Replay/retention/resync anchor; informational, not inverted.
	 * <!-- end-model-doc -->
	 * @see #KEYFRAME
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int KEYFRAME_VALUE = 11;

	/**
	 * The '<em><b>TOUCH</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Audit mode TOUCHED_FEATURES (tracking aspect): the feature was modified, values deliberately elided (data minimization). Informational; not invertible, excluded from conflict detection.
	 * <!-- end-model-doc -->
	 * @see #TOUCH
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TOUCH_VALUE = 12;

	/**
	 * The '<em><b>MIGRATE OUT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Object leaves THIS stream because it moved to another aggregate (§5.4e). Tombstone variant: valueNew carries the target streamId (forward reference). One entry per migrated object — subtrees migrate with explicit per-object pairs, child-first; linked to the target-stream batch via ChangeSet.transactionId. Inverse: the mirrored MIGRATE_IN/OUT pair.
	 * <!-- end-model-doc -->
	 * @see #MIGRATE_OUT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int MIGRATE_OUT_VALUE = 13;

	/**
	 * The '<em><b>MIGRATE IN</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Object enters THIS stream by migration (§5.4e). Genesis variant: valueOld carries the origin 'streamId#sequence' (origin reference); followed by a KEYFRAME of the object so the receiving stream replays standalone. Parent-first for subtrees; may open a brand-new stream (child promoted to aggregate root). Inverse: the mirrored pair.
	 * <!-- end-model-doc -->
	 * @see #MIGRATE_IN
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int MIGRATE_IN_VALUE = 14;

	/**
	 * An array of all the '<em><b>Delta Kind</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final DeltaKind[] VALUES_ARRAY =
		new DeltaKind[] {
			CREATE,
			SET,
			UNSET,
			ADD,
			REMOVE,
			MOVE,
			PUT,
			REMOVE_KEY,
			SET_AT,
			RESHAPE,
			DELETE,
			KEYFRAME,
			TOUCH,
			MIGRATE_OUT,
			MIGRATE_IN,
		};

	/**
	 * A public read-only list of all the '<em><b>Delta Kind</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<DeltaKind> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Delta Kind</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static DeltaKind get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DeltaKind result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Delta Kind</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static DeltaKind getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DeltaKind result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Delta Kind</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static DeltaKind get(int value) {
		switch (value) {
			case CREATE_VALUE: return CREATE;
			case SET_VALUE: return SET;
			case UNSET_VALUE: return UNSET;
			case ADD_VALUE: return ADD;
			case REMOVE_VALUE: return REMOVE;
			case MOVE_VALUE: return MOVE;
			case PUT_VALUE: return PUT;
			case REMOVE_KEY_VALUE: return REMOVE_KEY;
			case SET_AT_VALUE: return SET_AT;
			case RESHAPE_VALUE: return RESHAPE;
			case DELETE_VALUE: return DELETE;
			case KEYFRAME_VALUE: return KEYFRAME;
			case TOUCH_VALUE: return TOUCH;
			case MIGRATE_OUT_VALUE: return MIGRATE_OUT;
			case MIGRATE_IN_VALUE: return MIGRATE_IN;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private DeltaKind(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getValue() {
	  return value;
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
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //DeltaKind
