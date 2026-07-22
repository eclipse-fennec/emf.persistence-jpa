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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link QueryResults} factories and {@link QueryResultRows}.
 *
 * @author Mark Hoffmann
 */
class QueryResultsTest {

	@Test
	void objectsResultExposesStreamAndGuardsOtherAccessors() {
		EObject any = EcoreFactory.eINSTANCE.createEClass();
		try (QueryResult result = QueryResults.objects(Stream.of(any))) {
			assertThat(result.shape()).isEqualTo(QueryShape.OBJECTS);
			assertThat(result.objects()).containsExactly(any);
			assertThatIllegalStateException().isThrownBy(result::rows);
			assertThatIllegalStateException().isThrownBy(result::count);
		}
	}

	@Test
	void rowsResultExposesStreamAndGuardsOtherAccessors() {
		QueryResultRow row = QueryResultRows.of(List.of("name"), List.of("smith"));
		try (QueryResult result = QueryResults.rows(QueryShape.PROJECTION, Stream.of(row))) {
			assertThat(result.shape()).isEqualTo(QueryShape.PROJECTION);
			assertThat(result.rows()).containsExactly(row);
			assertThatIllegalStateException().isThrownBy(result::objects);
			assertThatIllegalStateException().isThrownBy(result::count);
		}
	}

	@Test
	void rowsResultRejectsNonRowShapes() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> QueryResults.rows(QueryShape.OBJECTS, Stream.empty()));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> QueryResults.rows(QueryShape.COUNT, Stream.empty()));
	}

	@Test
	void countResultExposesCountAndGuardsOtherAccessors() {
		try (QueryResult result = QueryResults.count(42)) {
			assertThat(result.shape()).isEqualTo(QueryShape.COUNT);
			assertThat(result.count()).isEqualTo(42);
			assertThatIllegalStateException().isThrownBy(result::objects);
			assertThatIllegalStateException().isThrownBy(result::rows);
		}
	}

	@Test
	void closeClosesTheUnderlyingStreamOnce() {
		AtomicInteger closes = new AtomicInteger();
		QueryResult result = QueryResults.objects(Stream.<EObject> empty().onClose(closes::incrementAndGet));
		result.close();
		result.close();
		assertThat(closes.get()).isEqualTo(1);
	}

	@Test
	void closeNeverThrows() {
		QueryResult result = QueryResults.objects(Stream.<EObject> empty().onClose(() -> {
			throw new IllegalStateException("backend release failed");
		}));
		result.close(); // must not propagate
	}

	@Test
	void repeatedAccessorCallsReturnTheSameStream() {
		QueryResult result = QueryResults.objects(Stream.empty());
		assertThat(result.objects()).isSameAs(result.objects());
		result.close();
	}

	@Test
	void rowAccessByAliasOrdinalAndValues() {
		QueryResultRow row = QueryResultRows.of(Arrays.asList("name", null), Arrays.asList("smith", 42));
		assertThat(row.get("name")).isEqualTo("smith");
		assertThat(row.get("unknown")).isNull();
		assertThat(row.get((String) null)).isNull();
		assertThat(row.get(1)).isEqualTo(42);
		assertThat(row.values()).containsExactly("smith", 42);
		assertThatThrownBy(() -> row.get(2)).isInstanceOf(IndexOutOfBoundsException.class);
		assertThatThrownBy(() -> row.values().add("x")).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void rowWithoutAliasesIsOrdinalOnly() {
		QueryResultRow row = QueryResultRows.of(null, List.of(1, 2));
		assertThat(row.get("anything")).isNull();
		assertThat(row.get(0)).isEqualTo(1);
	}

	@Test
	void rowAliasSizeMismatchIsRejected() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> QueryResultRows.of(List.of("a"), List.of(1, 2)));
	}

	@Test
	void rowValuesMayContainNulls() {
		QueryResultRow row = QueryResultRows.of(List.of("a", "b"), Arrays.asList(null, "x"));
		assertThat(row.get("a")).isNull();
		assertThat(row.get(0)).isNull();
	}
}
