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
package org.eclipse.fennec.persistence.helper;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.fennec.persistence.helper.ReservedWords.Dialect;
import org.eclipse.fennec.persistence.helper.ReservedWords.Kind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * The reserved-word lists and the escape rule of derived names (issue #399). Whether the lists
 * match the databases is measured by the TCK ({@code JpaReservedWordsTest}) per flavor.
 */
class ReservedWordsTest {

	@Test
	void theWordsOfTheIssueAreReservedWhereTheyBreak() {
		assertThat(ReservedWords.isReserved("value", Kind.COLUMN, Dialect.H2)).isTrue();
		assertThat(ReservedWords.isReserved("value", Kind.COLUMN, Dialect.POSTGRESQL)).isFalse();
		assertThat(ReservedWords.isReserved("index", Kind.COLUMN, Dialect.MARIADB)).isTrue();
		assertThat(ReservedWords.isReserved("index", Kind.COLUMN, Dialect.H2)).isFalse();
		assertThat(ReservedWords.reservingDialects("key", Kind.COLUMN))
				.containsExactlyInAnyOrder(Dialect.H2, Dialect.MARIADB);
		assertThat(ReservedWords.reservingDialects("order", Kind.COLUMN))
				.containsExactlyInAnyOrder(Dialect.values());
	}

	@ParameterizedTest
	@ValueSource(strings = { "value", "VALUE", "Value", "vAlUe" })
	void theCaseOfANameDoesNotMatter(String name) {
		assertThat(ReservedWords.isReserved(name, Kind.COLUMN)).isTrue();
	}

	/** MariaDB reads {@code position (} as a call: a reserved table name, a free column name. */
	@Test
	void tablesAndColumnsAreListedApart() {
		assertThat(ReservedWords.isReserved("position", Kind.TABLE)).isTrue();
		assertThat(ReservedWords.isReserved("position", Kind.COLUMN)).isFalse();
		assertThat(ReservedWords.escape("position", Kind.TABLE)).isEqualTo("position_");
		assertThat(ReservedWords.escape("position", Kind.COLUMN)).isEqualTo("position");
		// H2 refuses 'top' only as a column
		assertThat(ReservedWords.isReserved("top", Kind.COLUMN, Dialect.H2)).isTrue();
		assertThat(ReservedWords.isReserved("top", Kind.TABLE, Dialect.H2)).isFalse();
	}

	@ParameterizedTest
	@ValueSource(strings = { "name", "type", "timestamp", "date", "status", "sequence", "view", "featureId" })
	void ordinaryNamesAreKept(String name) {
		assertThat(ReservedWords.isReserved(name, Kind.COLUMN)).isFalse();
		assertThat(ReservedWords.isReserved(name, Kind.TABLE)).isFalse();
		assertThat(ReservedWords.escape(name, Kind.COLUMN)).isSameAs(name);
	}

	@Test
	void theEscapeKeepsTheCaseAndAppendsTheSuffix() {
		assertThat(ReservedWords.escape("value", Kind.COLUMN)).isEqualTo("value_");
		assertThat(ReservedWords.escape("ORDER", Kind.TABLE)).isEqualTo("ORDER_");
		assertThat(ReservedWords.escape("rowNum", Kind.COLUMN)).isEqualTo("rowNum_");
	}

	/** {@code _rowid} is free, {@code _rowid_} is H2's: the escape must not produce a reserved word. */
	@Test
	void theEscapedNameIsNeverReservedItself() {
		assertThat(ReservedWords.escape("_rowid", Kind.COLUMN)).isEqualTo("_rowid");
		assertThat(ReservedWords.escape("_rowid_", Kind.COLUMN)).isEqualTo("_rowid__");
		for (Kind kind : Kind.values()) {
			for (Dialect dialect : Dialect.values()) {
				for (String word : ReservedWords.words(dialect, kind)) {
					assertThat(ReservedWords.isReserved(ReservedWords.escape(word, kind), kind))
							.as("%s %s", kind, word).isFalse();
				}
			}
		}
	}

	@Test
	void nullIsNoReservedWord() {
		assertThat(ReservedWords.isReserved(null, Kind.COLUMN)).isFalse();
		assertThat(ReservedWords.reservingDialects(null, Kind.TABLE)).isEmpty();
		assertThat(ReservedWords.escape(null, Kind.COLUMN)).isNull();
	}

	@Test
	void theListsAreLowerCaseWords() {
		for (Kind kind : Kind.values()) {
			for (Dialect dialect : Dialect.values()) {
				assertThat(ReservedWords.words(dialect, kind)).isNotEmpty()
						.allSatisfy(word -> assertThat(word).matches("[a-z_][a-z0-9_]*"));
			}
		}
	}
}
