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

import org.eclipse.emf.ecore.EAnnotation;

/**
 * Keywords that are used in the persistence framework
 * @author Mark Hoffmann
 * @since 26.03.2022
 */
public interface Keywords {
	
	/**
	 * ID field identifier. Not intended to be used by clients.
	 */
	public static final String ID_KEY = "_id";
	/**
	 * ProxyURI field identifier. Not intended to be used by clients.
	 */
	public static final String PROXY_KEY = "_eProxyURI";
	/**
	 * eClass field identifier. Not intended to be used by clients.
	 */
	public static final String ECLASS_TYPE_KEY = "_eType";
	/**
	 * super-type array field identifier. Not intended to be used by clients.
	 */
	public static final String ECLASS_SUPER_TYPES_KEY = "_eSuperTypes";
	/**
	 * Extrinsic ID field identifier. Not intended to be used by clients.
	 */
	public static final String EXTRINSIC_ID_KEY = "_eId";
	/**
	 * Timestamp field identifier. Not intended to be used by clients.
	 */
	public static final String TIMESTAMP_KEY = "_timeStamp";
	/**
	 * Source for persistence {@link EAnnotation}.
	 */
	public static final String PERSISTENCE_ANNOTATION_SOURCE = "https://eclipse.org/fennec/persistence";
	/**
	 * Name for persistence {@link EAnnotation} to define alias database, table or column name.
	 */
	public static final String PERSISTENCE_ANNOTATION_NAME = "name";

	/**
	 * OSGi capability namespace for the core persistence service family.
	 */
	public static final String CAPABILITY_NAMESPACE = "org.eclipse.fennec.persistence";

	/**
	 * OSGi capability namespace for persistence extensions (converters, engines,
	 * accessors) that plug into the core services.
	 */
	public static final String CAPABILITY_EXTENSION_NAMESPACE = "org.eclipse.fennec.persistence.extension";

	/**
	 * Version advertised by the persistence capability — used as the
	 * {@code version:Version} attribute on {@code Provide-Capability} declarations.
	 */
	public static final String CAPABILITY_VERSION = "1.0";

}
