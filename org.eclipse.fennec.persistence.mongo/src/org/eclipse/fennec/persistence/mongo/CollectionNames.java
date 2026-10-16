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
package org.eclipse.fennec.persistence.mongo;

import static java.util.Objects.isNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The collection a type is stored in, where it is not named after the type (issue #290).
 * <p>
 * Everything above the database speaks in <em>logical</em> names: the URI segment of
 * {@code mongodb://<alias>/<EClassName>}, the reference form {@code /<EClassName>#<id>}
 * written into {@code _ref}, the ownership records. This mapping is applied only where a
 * collection is actually opened, so a collection created outside this backend —
 * {@code bikeparking} for the EClass {@code BikeParking} — is read and written under its own
 * name while every URI, reference and lookup keeps the type's name. It is configured per
 * database, the way an eorm table name is per persistence unit on the JPA side.
 * <p>
 * Entries are {@code <EClassName>=<collection>}. A malformed entry, a type mapped twice, two
 * types mapped onto one collection, or a mapping touching one of the backend's own
 * collections is refused when the mapping is built — a wrong mapping would otherwise read
 * an empty or a foreign collection without a word.
 *
 * @since 16.10.2026
 */
public final class CollectionNames {

	/** No type is renamed: every collection is named after its type. */
	public static final CollectionNames NONE = new CollectionNames(Map.of());

	/** Collections the backend owns; neither side of a mapping may name them. */
	private static final Set<String> RESERVED = Set.of("_fennec_ownership", "fennec.queries");

	private final Map<String, String> byType;

	private CollectionNames(Map<String, String> byType) {
		this.byType = byType;
	}

	/**
	 * Parses {@code <EClassName>=<collection>} entries.
	 *
	 * @param entries the entries, may be {@code null} or empty
	 * @return the mapping, {@link #NONE} for no entries
	 * @throws IllegalArgumentException for a malformed, duplicate or reserved entry
	 */
	public static CollectionNames parse(String... entries) {
		// Arrays.asList, not List.of: a configuration array may hold a null, which is a
		// malformed entry to report, not a NullPointerException
		return isNull(entries) ? NONE : parse(Arrays.asList(entries));
	}

	/**
	 * Parses {@code <EClassName>=<collection>} entries.
	 *
	 * @param entries the entries, may be {@code null} or empty
	 * @return the mapping, {@link #NONE} for no entries
	 * @throws IllegalArgumentException for a malformed, duplicate or reserved entry
	 */
	public static CollectionNames parse(Collection<String> entries) {
		if (isNull(entries) || entries.isEmpty()) {
			return NONE;
		}
		Map<String, String> byType = new LinkedHashMap<>();
		Map<String, String> byCollection = new LinkedHashMap<>();
		for (String entry : entries) {
			int separator = isNull(entry) ? -1 : entry.indexOf('=');
			String type = separator < 0 ? "" : entry.substring(0, separator).strip();
			String collection = separator < 0 ? "" : entry.substring(separator + 1).strip();
			if (type.isEmpty() || collection.isEmpty()) {
				throw new IllegalArgumentException("Collection mapping '" + entry
						+ "' is not of the form <EClassName>=<collection>");
			}
			if (RESERVED.contains(type) || RESERVED.contains(collection)) {
				throw new IllegalArgumentException("Collection mapping '" + entry
						+ "' names a collection the backend owns: " + RESERVED);
			}
			String previous = byType.putIfAbsent(type, collection);
			if (!isNull(previous)) {
				throw new IllegalArgumentException("Type '" + type + "' is mapped twice, to '" + previous
						+ "' and to '" + collection + "'");
			}
			String other = byCollection.putIfAbsent(collection, type);
			if (!isNull(other)) {
				throw new IllegalArgumentException("Types '" + other + "' and '" + type
						+ "' are both mapped to collection '" + collection + "'");
			}
		}
		return new CollectionNames(Map.copyOf(byType));
	}

	/**
	 * The collection that holds a logical name: the mapped collection, or the name itself.
	 *
	 * @param logical the URI segment, i.e. the type name
	 * @return the collection to open, never {@code null} for a non-null name
	 */
	public String collection(String logical) {
		return byType.getOrDefault(logical, logical);
	}

	/**
	 * @param logical the URI segment, i.e. the type name
	 * @return whether the type is stored under a name of its own
	 */
	public boolean isMapped(String logical) {
		return byType.containsKey(logical);
	}

	/** @return whether no type is renamed */
	public boolean isEmpty() {
		return byType.isEmpty();
	}

	/** @return the entries in {@code <EClassName>=<collection>} form, for a service property */
	public String[] toEntries() {
		return byType.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).toArray(String[]::new);
	}

	@Override
	public String toString() {
		return "CollectionNames" + byType;
	}
}
