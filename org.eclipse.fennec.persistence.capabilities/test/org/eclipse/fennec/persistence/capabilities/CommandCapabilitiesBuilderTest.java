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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.Test;

/**
 * The two-level capability contract of issue #114: {@code supports(feature)} is the
 * backend-wide answer, {@code supports(feature, eClass)} the routing truth.
 */
class CommandCapabilitiesBuilderTest {

	private final EClass materialized = named("Materialized");
	private final EClass mappedOnly = named("MappedOnly");

	private static EClass named(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		return eClass;
	}

	@Test
	void unnarrowedFeaturesAnswerPerEClassLikeBackendWide() {
		CommandCapabilities capabilities = CommandCapabilitiesBuilder.create()
				.support(CommandFeature.INSERT, CommandFeature.DELETE_BY_SELECTOR)
				.build();
		assertThat(capabilities.supports(CommandFeature.INSERT)).isTrue();
		assertThat(capabilities.supports(CommandFeature.INSERT, materialized)).isTrue();
		assertThat(capabilities.supports(CommandFeature.TRANSACTION_BRACKET)).isFalse();
		assertThat(capabilities.supports(CommandFeature.TRANSACTION_BRACKET, materialized)).isFalse();
		assertThat(capabilities.supported())
				.containsExactlyInAnyOrder(CommandFeature.INSERT, CommandFeature.DELETE_BY_SELECTOR);
	}

	@Test
	void narrowedFeatureStaysBackendWideSupportedButRoutesPerEClass() {
		// the emf.search materialization case: update only where the mapping can
		// reconstruct the whole document
		CommandCapabilities capabilities = CommandCapabilitiesBuilder.create()
				.narrow(CommandFeature.UPDATE_BY_SELECTOR, materialized::equals)
				.build();
		assertThat(capabilities.supports(CommandFeature.UPDATE_BY_SELECTOR)).isTrue();
		assertThat(capabilities.supported()).contains(CommandFeature.UPDATE_BY_SELECTOR);
		assertThat(capabilities.supports(CommandFeature.UPDATE_BY_SELECTOR, materialized)).isTrue();
		assertThat(capabilities.supports(CommandFeature.UPDATE_BY_SELECTOR, mappedOnly)).isFalse();
	}

	@Test
	void supportedSetIsImmutable() {
		CommandCapabilities capabilities = CommandCapabilitiesBuilder.create()
				.support(CommandFeature.INSERT)
				.build();
		assertThatThrownBy(() -> capabilities.supported().add(CommandFeature.TRANSACTION_BRACKET))
				.isInstanceOf(UnsupportedOperationException.class);
	}
}
