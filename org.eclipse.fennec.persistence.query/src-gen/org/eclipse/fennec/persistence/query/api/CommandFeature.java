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
package org.eclipse.fennec.persistence.query.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Command Feature</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * A single, backend-neutral write-command capability (issue #114) — the command-side mirror of QueryFeature, deliberately a separate enum: QueryCapabilities is the query-validate contract and stays untouched. Literal values are grouped with gaps: commands 0+, structural 20+.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getCommandFeature()
 * @model
 * @generated
 */
@ProviderType
public enum CommandFeature implements Enumerator {
	/**
	 * The '<em><b>INSERT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * InsertCommand: persist the payload objects.
	 * <!-- end-model-doc -->
	 * @see #INSERT_VALUE
	 * @generated
	 * @ordered
	 */
	INSERT(0, "INSERT", "INSERT"),

	/**
	 * The '<em><b>DELETE BY SELECTOR</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * DeleteCommand: remove every object the plain-filter selector matches.
	 * <!-- end-model-doc -->
	 * @see #DELETE_BY_SELECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	DELETE_BY_SELECTOR(1, "DELETE_BY_SELECTOR", "DELETE_BY_SELECTOR"),

	/**
	 * The '<em><b>UPDATE BY SELECTOR</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * UpdateCommand: apply the ChangeSet template to every object the selector matches. Backends without partial writes (e.g. a search index that must rewrite whole documents) may serve this per EClass only — see the two-level supports contract.
	 * <!-- end-model-doc -->
	 * @see #UPDATE_BY_SELECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	UPDATE_BY_SELECTOR(2, "UPDATE_BY_SELECTOR", "UPDATE_BY_SELECTOR"),

	/**
	 * The '<em><b>TRANSACTION BRACKET</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * begin(): an atomic multi-command bracket (issue #108). May depend on the deployment, not just the backend — mongo answers per hello-probe (replica set vs. standalone) — so capabilities are per resource instance.
	 * <!-- end-model-doc -->
	 * @see #TRANSACTION_BRACKET_VALUE
	 * @generated
	 * @ordered
	 */
	TRANSACTION_BRACKET(20, "TRANSACTION_BRACKET", "TRANSACTION_BRACKET");

	/**
	 * The '<em><b>INSERT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * InsertCommand: persist the payload objects.
	 * <!-- end-model-doc -->
	 * @see #INSERT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int INSERT_VALUE = 0;

	/**
	 * The '<em><b>DELETE BY SELECTOR</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * DeleteCommand: remove every object the plain-filter selector matches.
	 * <!-- end-model-doc -->
	 * @see #DELETE_BY_SELECTOR
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int DELETE_BY_SELECTOR_VALUE = 1;

	/**
	 * The '<em><b>UPDATE BY SELECTOR</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * UpdateCommand: apply the ChangeSet template to every object the selector matches. Backends without partial writes (e.g. a search index that must rewrite whole documents) may serve this per EClass only — see the two-level supports contract.
	 * <!-- end-model-doc -->
	 * @see #UPDATE_BY_SELECTOR
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int UPDATE_BY_SELECTOR_VALUE = 2;

	/**
	 * The '<em><b>TRANSACTION BRACKET</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * begin(): an atomic multi-command bracket (issue #108). May depend on the deployment, not just the backend — mongo answers per hello-probe (replica set vs. standalone) — so capabilities are per resource instance.
	 * <!-- end-model-doc -->
	 * @see #TRANSACTION_BRACKET
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TRANSACTION_BRACKET_VALUE = 20;

	/**
	 * An array of all the '<em><b>Command Feature</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final CommandFeature[] VALUES_ARRAY =
		new CommandFeature[] {
			INSERT,
			DELETE_BY_SELECTOR,
			UPDATE_BY_SELECTOR,
			TRANSACTION_BRACKET,
		};

	/**
	 * A public read-only list of all the '<em><b>Command Feature</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<CommandFeature> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Command Feature</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static CommandFeature get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			CommandFeature result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Command Feature</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static CommandFeature getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			CommandFeature result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Command Feature</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static CommandFeature get(int value) {
		switch (value) {
			case INSERT_VALUE: return INSERT;
			case DELETE_BY_SELECTOR_VALUE: return DELETE_BY_SELECTOR;
			case UPDATE_BY_SELECTOR_VALUE: return UPDATE_BY_SELECTOR;
			case TRANSACTION_BRACKET_VALUE: return TRANSACTION_BRACKET;
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
	private CommandFeature(int value, String name, String literal) {
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
	
} //CommandFeature
