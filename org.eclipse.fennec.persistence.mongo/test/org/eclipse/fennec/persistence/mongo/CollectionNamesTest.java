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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Parsing of the per-database collection mapping (issue #290). A mapping that is accepted
 * must be unambiguous in both directions; everything else is refused with the entry named.
 *
 * @since 16.10.2026
 */
class CollectionNamesTest {

	@Test
	void aMappedTypeOpensItsCollectionAndEveryOtherNameItself() {
		CollectionNames names = CollectionNames.parse("BikeParking=bikeparking");

		assertThat(names.collection("BikeParking")).isEqualTo("bikeparking");
		assertThat(names.isMapped("BikeParking")).isTrue();
		assertThat(names.collection("Station")).isEqualTo("Station");
		assertThat(names.isMapped("Station")).isFalse();
		// the mapping is by type name, exactly — a collection URI spelled like the target is
		// not a type and stays what it says
		assertThat(names.collection("bikeparking")).isEqualTo("bikeparking");
		assertThat(names.isMapped("bikeparking")).isFalse();
	}

	@Test
	void whitespaceAroundNamesIsDropped() {
		CollectionNames names = CollectionNames.parse("  BikeParking =  bikeparking ");

		assertThat(names.collection("BikeParking")).isEqualTo("bikeparking");
		assertThat(names.toEntries()).containsExactly("BikeParking=bikeparking");
	}

	@Test
	void noEntriesIsNone() {
		assertThat(CollectionNames.parse()).isSameAs(CollectionNames.NONE);
		assertThat(CollectionNames.parse((String[]) null)).isSameAs(CollectionNames.NONE);
		assertThat(CollectionNames.parse(List.of())).isSameAs(CollectionNames.NONE);
		assertThat(CollectionNames.NONE.isEmpty()).isTrue();
		assertThat(CollectionNames.NONE.toEntries()).isEmpty();
	}

	@Test
	void entriesRoundTripThroughTheServiceProperty() {
		CollectionNames names = CollectionNames.parse("BikeParking=bikeparking", "Station=stations");

		CollectionNames again = CollectionNames.parse(names.toEntries());

		assertThat(again.collection("BikeParking")).isEqualTo("bikeparking");
		assertThat(again.collection("Station")).isEqualTo("stations");
		assertThat(again.toEntries()).containsExactlyInAnyOrder(names.toEntries());
	}

	@Test
	void anEntryWithoutBothSidesIsRefused() {
		for (String entry : new String[] { "BikeParking", "=bikeparking", "BikeParking=", " = ", "" }) {
			assertThatThrownBy(() -> CollectionNames.parse(entry))
					.as(entry)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("<EClassName>=<collection>");
		}
		assertThatThrownBy(() -> CollectionNames.parse(new String[] { null }))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void aTypeMappedTwiceIsRefused() {
		assertThatThrownBy(() -> CollectionNames.parse("BikeParking=a", "BikeParking=b"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("'BikeParking' is mapped twice");
	}

	@Test
	void twoTypesOntoOneCollectionAreRefused() {
		assertThatThrownBy(() -> CollectionNames.parse("BikeParking=parking", "CarParking=parking"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("'BikeParking' and 'CarParking'")
				.hasMessageContaining("'parking'");
	}

	@Test
	void theBackendsOwnCollectionsAreRefusedOnEitherSide() {
		for (String entry : new String[] { "BikeParking=_fennec_ownership", "BikeParking=fennec.queries",
				"_fennec_ownership=x", "fennec.queries=x" }) {
			assertThatThrownBy(() -> CollectionNames.parse(entry))
					.as(entry)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("the backend owns");
		}
	}
}
