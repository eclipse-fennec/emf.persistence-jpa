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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class OptionsTest {

	@Test
	void getPageSize_nullOptions_returnsZero() {
		assertThat(Options.getPageSize(null)).isZero();
	}

	@Test
	void getPageSize_missingKey_returnsZero() {
		assertThat(Options.getPageSize(Map.of())).isZero();
	}

	@Test
	void getPageSize_integerValue_returnsValue() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_PAGE_SIZE, 500);
		assertThat(Options.getPageSize(opts)).isEqualTo(500);
	}

	@Test
	void getPageSize_longValue_returnsIntValue() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_PAGE_SIZE, 250L);
		assertThat(Options.getPageSize(opts)).isEqualTo(250);
	}

	@Test
	void getPageSize_stringValue_parsesInteger() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_PAGE_SIZE, "100");
		assertThat(Options.getPageSize(opts)).isEqualTo(100);
	}

	@Test
	void getPageSize_zeroOrNegative_returnsZero() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_PAGE_SIZE, 0);
		assertThat(Options.getPageSize(opts)).isZero();
		opts.put(Options.OPTION_PAGE_SIZE, -5);
		assertThat(Options.getPageSize(opts)).isZero();
	}

	@Test
	void getPageSize_invalidString_returnsZero() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_PAGE_SIZE, "not-a-number");
		assertThat(Options.getPageSize(opts)).isZero();
	}

	@Test
	void getCacheNewObjects_nullOptions_returnsNull() {
		assertThat(Options.getCacheNewObjects(null)).isNull();
	}

	@Test
	void getCacheNewObjects_missingKey_returnsNull() {
		assertThat(Options.getCacheNewObjects(Map.of())).isNull();
	}

	@Test
	void getCacheNewObjects_booleanTrue() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_CACHE_NEW_OBJECTS, Boolean.TRUE);
		assertThat(Options.getCacheNewObjects(opts)).isTrue();
	}

	@Test
	void getCacheNewObjects_booleanFalse() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_CACHE_NEW_OBJECTS, Boolean.FALSE);
		assertThat(Options.getCacheNewObjects(opts)).isFalse();
	}

	@Test
	void getCacheNewObjects_stringTrue() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_CACHE_NEW_OBJECTS, "true");
		assertThat(Options.getCacheNewObjects(opts)).isTrue();
	}

	@Test
	void getCacheNewObjects_stringFalse() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_CACHE_NEW_OBJECTS, "false");
		assertThat(Options.getCacheNewObjects(opts)).isFalse();
	}

	// ---------------------------------------------- write chunk size (issue #227)

	@Test
	void getWriteChunkSize_unsetIsTheActiveDefault() {
		assertThat(Options.getWriteChunkSize(null)).isEqualTo(Options.DEFAULT_WRITE_CHUNK_SIZE);
		assertThat(Options.getWriteChunkSize(new HashMap<>()))
				.as("unlike page size, an absent value must not mean 'no chunking' — the failure "
						+ "this prevents is an OOM")
				.isEqualTo(Options.DEFAULT_WRITE_CHUNK_SIZE);
	}

	@Test
	void getWriteChunkSize_positiveValueWins() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_WRITE_CHUNK_SIZE, 250);
		assertThat(Options.getWriteChunkSize(opts)).isEqualTo(250);
	}

	@Test
	void getWriteChunkSize_zeroDisablesChunking() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_WRITE_CHUNK_SIZE, 0);
		assertThat(Options.getWriteChunkSize(opts))
				.as("an explicit 0 is 'load everything', expressed as a chunk nothing reaches")
				.isEqualTo(Integer.MAX_VALUE);
		opts.put(Options.OPTION_WRITE_CHUNK_SIZE, -5);
		assertThat(Options.getWriteChunkSize(opts)).isEqualTo(Integer.MAX_VALUE);
	}

	@Test
	void getWriteChunkSize_stringIsParsed() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_WRITE_CHUNK_SIZE, " 64 ");
		assertThat(Options.getWriteChunkSize(opts)).isEqualTo(64);
	}

	@Test
	void getWriteChunkSize_garbageFallsBackToTheDefault() {
		Map<Object, Object> opts = new HashMap<>();
		opts.put(Options.OPTION_WRITE_CHUNK_SIZE, "not a number");
		assertThat(Options.getWriteChunkSize(opts))
				.as("a typo must not silently turn the protection off")
				.isEqualTo(Options.DEFAULT_WRITE_CHUNK_SIZE);
	}
}
