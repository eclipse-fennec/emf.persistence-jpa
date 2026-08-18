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
package org.eclipse.fennec.persistence.capabilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Store Feature</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * A single store-dependent capability that is not query vocabulary and not a command verb (issue #134, contract §5a). This is where power that the store either has or has not belongs - the write path included - so reaching such a statement never requires holding a query or a command role. Literal values are grouped with gaps: transactional 0+, streaming 20+, indexing 40+.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.persistence.capabilities.CapabilitiesPackage#getStoreFeature()
 * @model
 * @generated
 */
@ProviderType
public enum StoreFeature implements Enumerator {
	/**
	 * The '<em><b>TRANSACTION BRACKET</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * begin(): an atomic multi-operation bracket (issue #108). Moved here from CommandFeature, because it was never a command capability - the contract already uses it to explain the cascade-delete convergence window on the save path (§4a), and OwnershipMaintenance refers to it. May depend on the deployment rather than just the backend - mongo answers per hello-probe, replica set versus standalone - so the probe narrows it per resource instance while the declaration states what the flavor can do at all.
	 * <!-- end-model-doc -->
	 * @see #TRANSACTION_BRACKET_VALUE
	 * @generated
	 * @ordered
	 */
	TRANSACTION_BRACKET(0, "TRANSACTION_BRACKET", "TRANSACTION_BRACKET"),

	/**
	 * The '<em><b>SERVER CURSORS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The QueryResult lifetime contract (issue #162). Declared: the streams of a QueryResult remain valid until close() and fetch incrementally out of a live store handle - a server-side cursor, or its embedded analogue like a held searcher lease. Undeclared: results may be fully materialized at call time and close() is a no-op. Both are conforming; the feature only tells the consumer which resource-lifetime contract query(...) hands out. First declarer: the Lucene backend (emf.search), which holds the unit's searcher lease from query(...) to close().
	 * <!-- end-model-doc -->
	 * @see #SERVER_CURSORS_VALUE
	 * @generated
	 * @ordered
	 */
	SERVER_CURSORS(20, "SERVER_CURSORS", "SERVER_CURSORS");

	/**
	 * The '<em><b>TRANSACTION BRACKET</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * begin(): an atomic multi-operation bracket (issue #108). Moved here from CommandFeature, because it was never a command capability - the contract already uses it to explain the cascade-delete convergence window on the save path (§4a), and OwnershipMaintenance refers to it. May depend on the deployment rather than just the backend - mongo answers per hello-probe, replica set versus standalone - so the probe narrows it per resource instance while the declaration states what the flavor can do at all.
	 * <!-- end-model-doc -->
	 * @see #TRANSACTION_BRACKET
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TRANSACTION_BRACKET_VALUE = 0;

	/**
	 * The '<em><b>SERVER CURSORS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The QueryResult lifetime contract (issue #162). Declared: the streams of a QueryResult remain valid until close() and fetch incrementally out of a live store handle - a server-side cursor, or its embedded analogue like a held searcher lease. Undeclared: results may be fully materialized at call time and close() is a no-op. Both are conforming; the feature only tells the consumer which resource-lifetime contract query(...) hands out. First declarer: the Lucene backend (emf.search), which holds the unit's searcher lease from query(...) to close().
	 * <!-- end-model-doc -->
	 * @see #SERVER_CURSORS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SERVER_CURSORS_VALUE = 20;

	/**
	 * An array of all the '<em><b>Store Feature</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final StoreFeature[] VALUES_ARRAY =
		new StoreFeature[] {
			TRANSACTION_BRACKET,
			SERVER_CURSORS,
		};

	/**
	 * A public read-only list of all the '<em><b>Store Feature</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<StoreFeature> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Store Feature</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static StoreFeature get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			StoreFeature result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Store Feature</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static StoreFeature getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			StoreFeature result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Store Feature</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static StoreFeature get(int value) {
		switch (value) {
			case TRANSACTION_BRACKET_VALUE: return TRANSACTION_BRACKET;
			case SERVER_CURSORS_VALUE: return SERVER_CURSORS;
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
	private StoreFeature(int value, String name, String literal) {
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
	
} //StoreFeature
