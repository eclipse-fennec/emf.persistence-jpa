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
package org.eclipse.fennec.persistence.ecore;

import static java.util.Objects.requireNonNull;

import java.util.List;

/**
 * The settings of one parser run outside OSGi — the same options the
 * {@code fennec.ecore.DatabaseParser} component takes as configuration.
 *
 * @param packageName the base package name
 * @param uriPrefix the package URI prefix
 * @param version the package version
 * @param namespaces the namespaces to parse — schemas, or databases on MariaDB/MySQL; empty
 *            for the connection's current one
 * @param includeViews whether views become read-only classes
 * @param transformNames whether database names are turned into Java naming conventions
 * @author Mark Hoffmann
 * @since 23.09.2026
 */
public record ParserSettings(String packageName, String uriPrefix, String version, List<String> namespaces,
		boolean includeViews, boolean transformNames) {

	/**
	 * Creates the settings.
	 */
	public ParserSettings {
		requireNonNull(packageName, "packageName must not be null");
		requireNonNull(uriPrefix, "uriPrefix must not be null");
		requireNonNull(version, "version must not be null");
		namespaces = List.copyOf(namespaces);
	}

	/**
	 * Returns the defaults of the component configuration: version {@code 1.0}, the current
	 * namespace, no views, transformed names.
	 *
	 * @param packageName the base package name
	 * @param uriPrefix the package URI prefix
	 * @return the settings
	 */
	public static ParserSettings of(String packageName, String uriPrefix) {
		return new ParserSettings(packageName, uriPrefix, "1.0", List.of(), false, true);
	}

	/**
	 * Returns a copy parsing the given namespaces.
	 *
	 * @param names the schemas, or databases on MariaDB/MySQL
	 * @return the settings
	 */
	public ParserSettings withNamespaces(String... names) {
		return new ParserSettings(packageName, uriPrefix, version, List.of(names), includeViews, transformNames);
	}
}
