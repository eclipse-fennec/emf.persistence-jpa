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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.fennec.persistence.capabilities.QueryCapabilities;
import org.eclipse.fennec.persistence.capabilities.CapabilityDeclaration;
import org.eclipse.fennec.persistence.capabilities.CommandCapabilities;
import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.capabilities.StoreFeature;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The per-flavor capability declarations (issue #118): derived from one baseline by
 * exclusion, so a newly supported feature does not have to be added per flavor.
 *
 * @author Mark Hoffmann
 * @since 09.08.2026
 */
class MongoFlavorCapabilitiesTest {

	@Test
	@DisplayName("every flavor is declared and is a subset of the baseline")
	void declaresEveryFlavorWithinBaseline() {
		for (MongoFlavor flavor : MongoFlavor.values()) {
			QueryCapabilities capabilities = MongoFlavorCapabilities.of(flavor);

			assertThat(capabilities).as("declaration for %s", flavor).isNotNull();
			// derivation by exclusion can only remove — a flavor claiming more than the
			// translation can express would be a declaration no code backs
			assertThat(capabilities.supported()).as("features of %s", flavor)
					.isSubsetOf(MongoFlavorCapabilities.BASELINE.supported());
			assertThat(capabilities.maxFeaturePathDepth()).as("path depth of %s", flavor)
					.isEqualTo(MongoFlavorCapabilities.BASELINE.maxFeaturePathDepth());
		}
	}

	@Test
	@DisplayName("a flavor supports exactly the baseline minus its declared gaps")
	void appliesGapsExactly() {
		for (MongoFlavor flavor : MongoFlavor.values()) {
			Set<QueryFeature> expected = EnumSet.copyOf(MongoFlavorCapabilities.BASELINE.supported());
			expected.removeAll(MongoFlavorCapabilities.gapsOf(flavor));

			assertThat(MongoFlavorCapabilities.of(flavor).supported()).as("features of %s", flavor)
					.isEqualTo(expected);
		}
	}

	@Test
	@DisplayName("MongoDB is the baseline itself")
	void mongoIsTheBaseline() {
		assertThat(MongoFlavorCapabilities.of(MongoFlavor.MONGO)).isSameAs(MongoFlavorCapabilities.BASELINE);
		assertThat(MongoFlavorCapabilities.gapsOf(MongoFlavor.MONGO)).isEmpty();
	}

	@Test
	@DisplayName("no flavor argument means MongoDB")
	void defaultsToMongo() {
		assertThat(MongoFlavorCapabilities.of(null)).isSameAs(MongoFlavorCapabilities.BASELINE);
		assertThat(MongoFlavorCapabilities.gapsOf(null)).isEmpty();
	}

	@Test
	@DisplayName("FerretDB declares no query gap — measured, and the matrix job keeps it honest")
	void ferretDbHasNoMeasuredQueryGaps() {
		// The full TCK passed against ferretdb-eval:2 (issue #119), including geo,
		// $convert/$type, $filter+$size, temporal/extended string functions and
		// count-distinct. This test pins the measurement: should a future FerretDB release
		// lose a feature, the gap has to be added here deliberately rather than discovered
		// by a user through a driver error.
		assertThat(MongoFlavorCapabilities.FERRETDB_GAPS).isEmpty();
		assertThat(MongoFlavorCapabilities.of(MongoFlavor.FERRETDB).supported())
				.isEqualTo(MongoFlavorCapabilities.BASELINE.supported());
	}

	@Test
	@DisplayName("gap sets are immutable — a caller must not edit a declaration")
	void exposesImmutableGaps() {
		for (MongoFlavor flavor : MongoFlavor.values()) {
			Set<QueryFeature> gaps = MongoFlavorCapabilities.gapsOf(flavor);
			assertThat(gaps.getClass().getName()).contains("Unmodifiable");
		}
		assertThat(MongoFlavorCapabilities.BASELINE.supported().getClass().getName()).contains("Unmodifiable");
	}

	/**
	 * The declaration answers query, command and store together (issue #172) — and store is
	 * where the mongo flavors genuinely differ: a transaction bracket needs a session-capable
	 * deployment, which FerretDB, a standalone server, is not. This used to live in the TCK
	 * binding, where nothing held it against the production declaration.
	 */
	@Test
	void storeCapabilitiesFollowTheFlavor() {
		assertThat(MongoFlavorCapabilities.persistenceCapabilities(MongoFlavor.MONGO).store()
				.supports(StoreFeature.TRANSACTION_BRACKET)).isTrue();
		assertThat(MongoFlavorCapabilities.persistenceCapabilities(MongoFlavor.DOCUMENTDB_PG).store()
				.supports(StoreFeature.TRANSACTION_BRACKET)).isTrue();
		assertThat(MongoFlavorCapabilities.persistenceCapabilities(MongoFlavor.FERRETDB).store()
				.supports(StoreFeature.TRANSACTION_BRACKET)).isFalse();
	}

	@Test
	void everyFlavorDeclaresTheSameWriteVerbs() {
		for (MongoFlavor flavor : MongoFlavor.values()) {
			CommandCapabilities commands =
					MongoFlavorCapabilities.persistenceCapabilities(flavor).command();
			assertThat(commands.supports(CommandFeature.INSERT)).as("flavor %s", flavor.id()).isTrue();
			assertThat(commands.supports(CommandFeature.DELETE_BY_SELECTOR)).isTrue();
			assertThat(commands.supports(CommandFeature.UPDATE_BY_SELECTOR)).isTrue();
		}
	}

	@Test
	void declarationNamesBothAxes() {
		CapabilityDeclaration declaration = MongoFlavorCapabilities.declaration(MongoFlavor.FERRETDB);

		assertThat(declaration.backend()).isEqualTo("mongo");
		assertThat(declaration.flavor()).isEqualTo("ferretdb");
		assertThat(declaration.capabilities().query().supported())
				.isEqualTo(MongoFlavorCapabilities.of(MongoFlavor.FERRETDB).supported());
	}

	@Test
	void nullFlavorDeclaresMongo() {
		assertThat(MongoFlavorCapabilities.persistenceCapabilities(null).query().supported())
				.isEqualTo(MongoFlavorCapabilities.of(MongoFlavor.MONGO).supported());
		assertThat(MongoFlavorCapabilities.declaration(null).flavor()).isEqualTo(MongoFlavor.MONGO.id());
	}
}
