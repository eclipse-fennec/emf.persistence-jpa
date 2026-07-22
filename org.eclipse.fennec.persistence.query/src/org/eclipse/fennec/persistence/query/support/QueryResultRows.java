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
package org.eclipse.fennec.persistence.query.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.fennec.persistence.query.api.QueryResultRow;

/**
 * Factory for immutable {@link QueryResultRow}s. A row pairs the projected cell values
 * (in subject order) with their subject aliases; cells without an alias are reachable
 * by ordinal only.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class QueryResultRows {

	private QueryResultRows() {
	}

	/**
	 * Creates a row.
	 *
	 * @param aliases the subject aliases in subject order; entries may be {@code null}
	 *        (ordinal access only); the list itself may be {@code null} when no subject
	 *        carries an alias
	 * @param values the cell values in subject order, must not be {@code null}
	 * @return the immutable row
	 * @throws IllegalArgumentException if aliases are given and sizes do not match
	 */
	public static QueryResultRow of(List<String> aliases, List<Object> values) {
		Objects.requireNonNull(values, "values must not be null");
		if (aliases != null && aliases.size() != values.size()) {
			throw new IllegalArgumentException(
					"aliases size " + aliases.size() + " does not match values size " + values.size());
		}
		// no List.copyOf: alias entries may legitimately be null (ordinal-only cells)
		List<String> aliasCopy = aliases == null ? Collections.emptyList()
				: Collections.unmodifiableList(new ArrayList<>(aliases));
		return new Row(aliasCopy, new ArrayList<>(values));
	}

	private static final class Row implements QueryResultRow {

		private final List<String> aliases;
		private final List<Object> values;

		private Row(List<String> aliases, List<Object> values) {
			this.aliases = aliases;
			this.values = Collections.unmodifiableList(values);
		}

		@Override
		public Object get(String alias) {
			if (alias == null) {
				return null;
			}
			int index = aliases.indexOf(alias);
			return index < 0 ? null : values.get(index);
		}

		@Override
		public Object get(int index) {
			return values.get(index);
		}

		@Override
		public List<Object> values() {
			return values;
		}

		@Override
		public String toString() {
			return "QueryResultRow" + values;
		}
	}
}
