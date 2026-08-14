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
package org.eclipse.fennec.persistence.capabilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * Covers the aggregate of issue #134: one root that answers for query, command and store, so a
 * capability statement is reachable without holding a query or command role.
 *
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
class PersistenceCapabilitiesTest {

	private static final QueryCapabilities QUERY = QueryCapabilitiesBuilder.create()
			.support(QueryFeature.WHERE_EQ)
			.build();
	private static final CommandCapabilities COMMAND = CommandCapabilitiesBuilder.create()
			.support(CommandFeature.INSERT)
			.build();
	private static final StoreCapabilities STORE = StoreCapabilitiesBuilder.create()
			.support(StoreFeature.TRANSACTION_BRACKET)
			.build();

	@Test
	void theThreeViewsAreHandedBackUnchanged() {
		PersistenceCapabilities capabilities = PersistenceCapabilities.of(QUERY, COMMAND, STORE);
		assertThat(capabilities.query()).isSameAs(QUERY);
		assertThat(capabilities.command()).isSameAs(COMMAND);
		assertThat(capabilities.store()).isSameAs(STORE);
	}

	/**
	 * The transaction bracket is answered by the store view — the point of the move in #134. A
	 * caller asking whether writes are bracketed atomically needs no command role for it.
	 */
	@Test
	void theTransactionBracketIsAStoreFeature() {
		PersistenceCapabilities capabilities = PersistenceCapabilities.of(QUERY, COMMAND, STORE);
		assertThat(capabilities.store().supports(StoreFeature.TRANSACTION_BRACKET)).isTrue();
		assertThat(capabilities.command().supported())
				.noneMatch(feature -> "TRANSACTION_BRACKET".equals(feature.getName()));
	}

	/**
	 * A backend that serves no commands declares an empty view, which is a statement;
	 * {@code null} would be the absence of one and must not pass.
	 */
	@Test
	void aMissingViewIsRefusedRatherThanTreatedAsEmpty() {
		assertThatThrownBy(() -> PersistenceCapabilities.of(QUERY, null, STORE))
				.isInstanceOf(NullPointerException.class)
				.hasMessageContaining("command");
		assertThatThrownBy(() -> PersistenceCapabilities.of(null, COMMAND, STORE))
				.isInstanceOf(NullPointerException.class)
				.hasMessageContaining("query");
		assertThatThrownBy(() -> PersistenceCapabilities.of(QUERY, COMMAND, null))
				.isInstanceOf(NullPointerException.class)
				.hasMessageContaining("store");
	}

	@Test
	void anEmptyCommandViewIsAValidStatement() {
		PersistenceCapabilities readOnly = PersistenceCapabilities.of(QUERY,
				CommandCapabilitiesBuilder.create().build(),
				StoreCapabilitiesBuilder.create().build());
		assertThat(readOnly.command().supported()).isEmpty();
		assertThat(readOnly.command().supports(CommandFeature.INSERT)).isFalse();
		assertThat(readOnly.store().supported()).isEmpty();
	}
}
