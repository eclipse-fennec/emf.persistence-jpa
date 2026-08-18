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
import java.util.Map;
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

	/**
	 * The scored result (issue #165): hits() pairs object and score in stream order,
	 * objects() is a view of the same cursor, and scores() is the immutable metadata view
	 * that exists before anything is consumed.
	 */
	@Test
	void scoredResultPairsHitsAndDerivesTheViews() {
		EObject first = EcoreFactory.eINSTANCE.createEClass();
		EObject second = EcoreFactory.eINSTANCE.createEClass();
		try (QueryResult scored = QueryResults.hits(
				Stream.of(QueryResults.hit(first, 4.7), QueryResults.hit(second, 1.2)),
				Map.of("1", 4.7, "2", 1.2))) {
			assertThat(scored.shape()).isEqualTo(QueryShape.OBJECTS);
			// the metadata view is complete before the stream is touched
			assertThat(scored.scores()).containsEntry("1", 4.7).containsEntry("2", 1.2);
			assertThatThrownBy(() -> scored.scores().put("3", 1.0))
					.isInstanceOf(UnsupportedOperationException.class);
			// pairing in rank order
			assertThat(scored.hits()).satisfiesExactly(
					hit -> {
						assertThat(hit.object()).isSameAs(first);
						assertThat(hit.score()).isEqualTo(4.7);
					},
					hit -> {
						assertThat(hit.object()).isSameAs(second);
						assertThat(hit.score()).isEqualTo(1.2);
					});
		}
	}

	@Test
	void objectsIsAViewOfTheHitStream() {
		EObject any = EcoreFactory.eINSTANCE.createEClass();
		try (QueryResult scored = QueryResults.hits(
				Stream.of(QueryResults.hit(any, 0.5)), Map.of("1", 0.5))) {
			assertThat(scored.objects()).containsExactly(any);
		}
	}

	/**
	 * The two sides of the accessor contract (issue #165): hits() is a hard accessor like
	 * the shape accessors — invalid without withScores; scores() is the soft side channel —
	 * empty on unscored results, never throws.
	 */
	@Test
	void unscoredResultsRefuseHitsButAnswerScoresEmpty() {
		EObject any = EcoreFactory.eINSTANCE.createEClass();
		try (QueryResult unscored = QueryResults.objects(Stream.of(any))) {
			assertThat(unscored.scores()).isEmpty();
			assertThatIllegalStateException().isThrownBy(unscored::hits);
		}
		try (QueryResult counted = QueryResults.count(3)) {
			assertThat(counted.scores()).isEmpty();
			assertThatIllegalStateException().isThrownBy(counted::hits);
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
