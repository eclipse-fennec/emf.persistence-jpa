/********************************************************************
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
 ********************************************************************/
package org.eclipse.fennec.persistence;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;

/**
 * An interface that is optionally implemented by the input streams returned from 
 * {@link URIConverter#createInputStream(URI)} and {@link URIConverter#createInputStream(URI, Map)}.
 * It is responsible for existence checks or counting elements
 * @author Mark Hoffmann
 * @since 30.05.2022
 */
public interface Countable {
	
	/**
	 * This option can be set to tell the count to create a filter over the uri-type column and in super-class columns, if available
	 * Only instances of of the given EClass URI are counted then. This may be used in combination 
	 * with {@link Options#CAP_ECLASS_URI} or {@link Options#CAP_ECLASS}. If none of these two hints are provided,
	 * This option will have no effect.
	 * 
	 * This enables to filter the count certain EClass types in tables where no table per class inheritance is used. 
	 * Provided queries are applied in addition to that filter.
	 * 
	 * WARNING: If this options is set {@link Countable#CAP_COUNT_ID_FIELD} has then no effect
	 * 
	 * value type: Boolean, default is <code>false</code>
	 */
	public static final String CAP_COUNT_ECLASS_TYPE_FILTER = "COUNT_ECLASS_TYPE_FILTER";
	
	/**
	 * This option can be set to tell the count operation to count over the primary-key field instead of all fields
	 * like a SELECT COUNT(*). This may be used in combination with {@link Options#CAP_ID_FIELD_NAME},
	 * {@link Options#CAP_ECLASS_URI} or {@link Options#CAP_ECLASS}.
	 * {@link Options#CAP_ID_FIELD_NAME} provided the name of the id-column directly. the both EClass hints
	 * would provide information about the EClass. The IDAttribute will be extracted then from this EClass
	 * 
	 * WARNING: If none of the hints are set, this option will have no effect. If {@link Countable#CAP_COUNT_ECLASS_TYPE_FILTER}
	 * is set, this capability is useless, because the count will run over the type-columns instead
	 * 
	 * value type: Boolean, default is <code>false</code>
	 */
	public static final String CAP_COUNT_ID_FIELD = "COUNT_ID_FIELD";
	
	/**
	 * This option can be set to tell the count operation over which field to be counted.
	 * 
	 * If this capability is set in addition to {@link Countable#CAP_COUNT_ID_FIELD}, then this options
	 * has a higher rank and the count will not be executed using the id field. It will be counted over the 
	 * given count attribute.
	 * 
	 * WARNING:If {@link Countable#CAP_COUNT_ECLASS_TYPE_FILTER} is set, this capability is useless, 
	 * because the count will run over the type-columns instead
	 * 
	 * value type: String
	 */
	public static final String CAP_COUNT_FIELD = "COUNT_FIELD";
	
	/**
	 * Counts the elements that match the given {@link URI} and options
	 * @param properties additional count properties
	 * @throws PersistenceException thrown on lower level errors 
	 */
	long count(Map<Object, Object> properties) throws PersistenceException;
	
	/**
	 * Checks if an element exists 
	 * @param properties additional exist properties
	 * @return <code>true</code> if at least an element exists, otherwise <code>false</code>
	 * @throws PersistenceException thrown on lower level errors 
	 */
	boolean exist(Map<Object, Object> properties) throws PersistenceException;
	
}
