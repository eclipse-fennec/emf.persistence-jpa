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
package org.eclipse.fennec.persistence.eclipselink;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.eclipse.fennec.persistence.capabilities.CapabilityDeclaration;
import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.capabilities.StoreFeature;
import org.eclipse.fennec.persistence.eclipselink.query.JpaQueryProcessor;
import org.junit.jupiter.api.Test;

/**
 * The relational declaration per flavor (issue #172): derived from one baseline by
 * exclusion, and complete — query, command and store in one answer, so no consumer has to
 * restate half of it.
 */
class JpaFlavorCapabilitiesTest {

	@Test
	void everyFlavorStaysWithinTheBaseline() {
		for (JpaFlavor flavor : JpaFlavor.values()) {
			assertThat(JpaFlavorCapabilities.of(flavor).supported())
					.as("flavor %s", flavor.id())
					.isSubsetOf(JpaFlavorCapabilities.BASELINE.supported());
		}
	}

	@Test
	void gapsAreAppliedExactly() {
		for (JpaFlavor flavor : JpaFlavor.values()) {
			for (QueryFeature gap : JpaFlavorCapabilities.gapsOf(flavor)) {
				assertThat(JpaFlavorCapabilities.of(flavor).supports(gap))
						.as("flavor %s must not declare its gap %s", flavor.id(), gap.getName())
						.isFalse();
			}
		}
	}

	/**
	 * No measured gaps today, and that is the statement: the full TCK runs against H2,
	 * PostgreSQL and MariaDB. The axis exists for the first difference that is already known
	 * to be coming, not because one is hidden here.
	 */
	@Test
	void noRelationalFlavorHasAMeasuredQueryGapYet() {
		assertThat(JpaFlavorCapabilities.gapsOf(JpaFlavor.H2)).isEmpty();
		assertThat(JpaFlavorCapabilities.gapsOf(JpaFlavor.POSTGRES)).isEmpty();
		assertThat(JpaFlavorCapabilities.gapsOf(JpaFlavor.MARIADB)).isEmpty();
	}

	@Test
	void unknownFlavorDeclaresTheBaseline() {
		assertThat(JpaFlavorCapabilities.of(JpaFlavor.UNKNOWN).supported())
				.isEqualTo(JpaFlavorCapabilities.BASELINE.supported());
		assertThat(JpaFlavorCapabilities.of(null).supported())
				.isEqualTo(JpaFlavorCapabilities.BASELINE.supported());
	}

	@Test
	void theDeclarationCarriesCommandAndStoreToo() {
		PersistenceCapabilities capabilities =
				JpaFlavorCapabilities.persistenceCapabilities(JpaFlavor.POSTGRES);

		assertThat(capabilities.command().supports(CommandFeature.INSERT)).isTrue();
		assertThat(capabilities.command().supports(CommandFeature.DELETE_BY_SELECTOR)).isTrue();
		assertThat(capabilities.command().supports(CommandFeature.UPDATE_BY_SELECTOR)).isTrue();
		// a relational connection is transactional wherever this backend runs — unlike mongo,
		// where the bracket depends on the deployment
		assertThat(capabilities.store().supports(StoreFeature.TRANSACTION_BRACKET)).isTrue();
	}

	@Test
	void declarationNamesBothAxes() {
		CapabilityDeclaration declaration = JpaFlavorCapabilities.declaration(JpaFlavor.MARIADB);

		assertThat(declaration.backend()).isEqualTo("jpa");
		assertThat(declaration.flavor()).isEqualTo("mariadb");
		assertThat(declaration.capabilities().query().supports(QueryFeature.WHERE_EQ)).isTrue();
	}

	/**
	 * The processor declares what its flavor declares — one mechanism, so a consumer reading
	 * the processor and a consumer reading the declaration cannot get different answers.
	 */
	@Test
	void theProcessorDeclaresItsFlavorsCapabilities() {
		assertThat(new JpaQueryProcessor(JpaFlavor.POSTGRES).capabilities().supported())
				.isEqualTo(JpaFlavorCapabilities.of(JpaFlavor.POSTGRES).supported());
		assertThat(new JpaQueryProcessor().flavor()).isEqualTo(JpaFlavor.UNKNOWN);
		assertThat(new JpaQueryProcessor(null).flavor()).isEqualTo(JpaFlavor.UNKNOWN);
	}

	@Test
	void declarationRejectsMissingParts() {
		assertThatThrownBy(() -> CapabilityDeclaration.of(null, "h2",
				JpaFlavorCapabilities.persistenceCapabilities(JpaFlavor.H2)))
				.isInstanceOf(NullPointerException.class);
		assertThatThrownBy(() -> CapabilityDeclaration.of("jpa", null,
				JpaFlavorCapabilities.persistenceCapabilities(JpaFlavor.H2)))
				.isInstanceOf(NullPointerException.class);
		assertThatThrownBy(() -> CapabilityDeclaration.of("jpa", "h2", null))
				.isInstanceOf(NullPointerException.class);
	}

	/**
	 * The null-order fallback (issue #365): on, every flavor declares both placements; off,
	 * exactly the placements that would need the CASE key drop out.
	 */
	@Test
	void theNullOrderFallbackDecidesWhatACaseKeyPlacementDeclares() {
		for (JpaFlavor flavor : JpaFlavor.values()) {
			assertThat(JpaFlavorCapabilities.of(flavor, true).supported())
					.as(flavor.id()).isEqualTo(JpaFlavorCapabilities.of(flavor).supported())
					.contains(QueryFeature.SORT_NULLS_LOW, QueryFeature.SORT_NULLS_HIGH);
		}
		assertThat(JpaFlavorCapabilities.of(JpaFlavor.H2, false).supported())
				.contains(QueryFeature.SORT_NULLS_LOW, QueryFeature.SORT_NULLS_HIGH);
		assertThat(JpaFlavorCapabilities.of(JpaFlavor.POSTGRES, false).supported())
				.contains(QueryFeature.SORT_NULLS_LOW, QueryFeature.SORT_NULLS_HIGH);
		assertThat(JpaFlavorCapabilities.of(JpaFlavor.MARIADB, false).supported())
				.contains(QueryFeature.SORT_NULLS_LOW).doesNotContain(QueryFeature.SORT_NULLS_HIGH);
		assertThat(JpaFlavorCapabilities.of(JpaFlavor.UNKNOWN, false).supported())
				.doesNotContain(QueryFeature.SORT_NULLS_LOW, QueryFeature.SORT_NULLS_HIGH);

		// configuration, not a measured gap
		assertThat(JpaFlavorCapabilities.gapsOf(JpaFlavor.MARIADB)).doesNotContain(QueryFeature.SORT_NULLS_HIGH);
		assertThat(JpaFlavorCapabilities.declaration(JpaFlavor.MARIADB, false).capabilities().query().supported())
				.doesNotContain(QueryFeature.SORT_NULLS_HIGH);
	}
}
