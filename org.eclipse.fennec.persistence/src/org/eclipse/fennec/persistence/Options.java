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

import static java.util.Objects.isNull;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;

/**
 * Constants and utility methods for persistence load/save options.
 * Options are passed via EMF's {@code resourceSet.getLoadOptions()} and
 * {@code resourceSet.getSaveOptions()} maps.
 *
 * @author Mark Hoffmann
 * @since 26.03.2022
 */
public interface Options {

	/**
	 * If set to an {@link EClass}, an additional filter will be created that filters
	 * against the EClass type when loading entities.
	 *
	 * <pre>{@code
	 * resourceSet.getLoadOptions().put(Options.READ_FILTER_ECLASS, MyPackage.Literals.MY_ENTITY);
	 * }</pre>
	 *
	 * Value type: {@link EClass}
	 */
	String READ_FILTER_ECLASS = "FILTER_ECLASS";

	/**
	 * If set to an {@link EClass} or a {@link String}, the persistence layer uses the
	 * given value as database table name.
	 *
	 * <pre>{@code
	 * resourceSet.getSaveOptions().put(Options.OPTION_TABLE_NAME, MyPackage.Literals.MY_ENTITY);
	 * resourceSet.getLoadOptions().put(Options.OPTION_TABLE_NAME, "my_table");
	 * }</pre>
	 *
	 * Value type: {@link EClass} or {@link String}
	 */
	String OPTION_TABLE_NAME = "TABLE_NAME";

	/**
	 * Page size for paginated loads. If set to a positive {@link Integer},
	 * the resource loads entities in pages via {@code setFirstResult}/{@code setMaxResults}
	 * instead of a single query returning the full result set.
	 * Zero or a missing value disables pagination (default: load all).
	 *
	 * <pre>{@code
	 * resourceSet.getLoadOptions().put(Options.OPTION_PAGE_SIZE, 500);
	 * }</pre>
	 *
	 * Value type: {@link Integer}
	 */
	String OPTION_PAGE_SIZE = "fennec.jpa.page-size";

	/**
	 * If set to {@link Boolean#FALSE}, newly persisted objects are not registered in
	 * EclipseLink's shared identity map at commit time. This reduces memory pressure
	 * on bulk-insert workloads where the inserted objects are not needed in the
	 * second-level cache afterwards. Applies to {@code doSave()} only.
	 *
	 * <pre>{@code
	 * resourceSet.getSaveOptions().put(Options.OPTION_CACHE_NEW_OBJECTS, Boolean.FALSE);
	 * }</pre>
	 *
	 * Value type: {@link Boolean}
	 */
	String OPTION_CACHE_NEW_OBJECTS = "fennec.jpa.cache-new-objects";

	/**
	 * Returns the cache-new-objects setting from the options, or {@code null} if unset.
	 */
	static Boolean getCacheNewObjects(Map<?, ?> options) {
		if (isNull(options)) {
			return null;
		}
		Object value = options.get(OPTION_CACHE_NEW_OBJECTS);
		if (value instanceof Boolean b) {
			return b;
		}
		if (value instanceof String s) {
			return Boolean.parseBoolean(s);
		}
		return null;
	}

	/**
	 * Returns the page size from the options, or {@code 0} if unset/invalid.
	 */
	static int getPageSize(Map<?, ?> options) {
		if (isNull(options)) {
			return 0;
		}
		Object value = options.get(OPTION_PAGE_SIZE);
		if (value instanceof Integer i) {
			return i > 0 ? i : 0;
		}
		if (value instanceof Number n) {
			int i = n.intValue();
			return i > 0 ? i : 0;
		}
		if (value instanceof String s) {
			try {
				int i = Integer.parseInt(s);
				return i > 0 ? i : 0;
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}

	/**
	 * Returns the {@link EClass} filter from the options, or {@code null}.
	 * @throws IllegalStateException if the value is not an {@link EClass}
	 */
	static EClass getFilterEClass(Map<?, ?> options) {
		if (isNull(options)) {
			return null;
		}
		Object result = options.getOrDefault(READ_FILTER_ECLASS, null);
		if (isNull(result)) {
			return null;
		}
		if (result instanceof EClass eClass) {
			return eClass;
		}
		throw new IllegalStateException(
				"The property OPTION_FILTER_ECLASS is expected to have a value of type EClass but was: "
						+ result.getClass().getName());
	}

	/**
	 * Returns the table {@link EClass} from the options, or {@code null}.
	 */
	static EClass getTableEClass(Map<?, ?> options) {
		if (isNull(options)) {
			return null;
		}
		Object alias = getTableObject(options);
		if (alias instanceof EClass eClass) {
			return eClass;
		}
		return null;
	}

	/**
	 * Returns the raw table option value (may be {@link EClass} or {@link String}).
	 */
	static Object getTableObject(Map<?, ?> options) {
		if (isNull(options)) {
			return null;
		}
		return options.getOrDefault(Options.OPTION_TABLE_NAME, null);
	}

	/**
	 * Returns the table name from the options, or {@code null}.
	 * If the value is an {@link EClass}, its name is returned.
	 */
	static String getTableName(Map<?, ?> options) {
		if (isNull(options)) {
			return null;
		}
		Object alias = getTableObject(options);
		if (alias instanceof EClass eClass) {
			return eClass.getName();
		}
		return isNull(alias) ? null : alias.toString();
	}
}
