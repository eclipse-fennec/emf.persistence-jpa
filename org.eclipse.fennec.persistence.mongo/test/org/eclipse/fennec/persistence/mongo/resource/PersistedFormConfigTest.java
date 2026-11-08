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
package org.eclipse.fennec.persistence.mongo.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PersistedFormConfig} (issue #363): the codec is told to write and read exactly
 * the features EMF persists although the codec skips them by default.
 */
class PersistedFormConfigTest {

	private final PersistedFormConfig<EAttribute> attributes = new PersistedFormConfig<>(EAttribute.class);
	private final PersistedFormConfig<EReference> references = new PersistedFormConfig<>(EReference.class);

	@Test
	void aVolatileFeatureThatIsNotTransientIsForced() {
		EAttribute data = attribute(false, true, true, true);

		Map<String, Object> config = attributes.get(data);

		assertThat(config).containsEntry(ConfigProperty.FORCE_WRITE.getKey(), Boolean.TRUE)
				.containsEntry(ConfigProperty.FORCE_READ.getKey(), Boolean.TRUE);
		assertThat(attributes.containsKey(data)).isTrue();
		assertThat(attributes.entrySet()).extracting(Map.Entry::getKey).containsExactly(data);
	}

	@Test
	void aPlainFeatureNeedsNothing() {
		// the codec writes it anyway — a configuration would only override what is already right
		assertThat(attributes.get(attribute(false, false, false, true))).isNull();
	}

	@Test
	void aComputedFeatureStaysOut() {
		assertThat(attributes.get(attribute(true, true, true, true))).as("transient").isNull();
		assertThat(attributes.get(attribute(false, true, true, false))).as("not changeable").isNull();
		assertThat(attributes.entrySet()).isEmpty();
	}

	@Test
	void eachMapAnswersForItsFeatureTypeOnly() {
		EAttribute data = attribute(false, true, true, true);
		EReference view = EcoreFactory.eINSTANCE.createEReference();
		view.setName("view");
		view.setVolatile(true);

		assertThat(references.get(data)).isNull();
		assertThat(references.get(view)).isNotNull();
		assertThat(attributes.get(view)).isNull();
		assertThat(attributes.get("data")).isNull();
	}

	/**
	 * An unsettable feature writes a value equal to its default (issue #404): written means set,
	 * missing means unset — the codec skips an unset one before it compares with the default.
	 */
	@Test
	void anUnsettableFeatureKeepsItsDefault() {
		EAttribute tint = attribute(false, false, false, true);
		tint.setUnsettable(true);

		assertThat(attributes.get(tint)).containsExactly(Map.entry(ConfigProperty.SERIALIZE_DEFAULT.getKey(), Boolean.TRUE));
	}

	@Test
	void anUnsettableVolatileFeatureIsForcedAndKeepsItsDefault() {
		EAttribute data = attribute(false, true, true, true);
		data.setUnsettable(true);

		assertThat(attributes.get(data)).containsEntry(ConfigProperty.FORCE_WRITE.getKey(), Boolean.TRUE)
				.containsEntry(ConfigProperty.FORCE_READ.getKey(), Boolean.TRUE)
				.containsEntry(ConfigProperty.SERIALIZE_DEFAULT.getKey(), Boolean.TRUE);
	}

	@Test
	void aManyValuedOrTransientUnsettableFeatureNeedsNothing() {
		EAttribute many = attribute(false, false, false, true);
		many.setUnsettable(true);
		many.setUpperBound(-1);
		EAttribute computed = attribute(true, false, false, true);
		computed.setUnsettable(true);

		assertThat(attributes.get(many)).as("many-valued: an empty list is the codec's serializeEmpty").isNull();
		assertThat(attributes.get(computed)).as("transient").isNull();
	}

	private static EAttribute attribute(boolean isTransient, boolean derived, boolean isVolatile, boolean changeable) {
		EClass owner = EcoreFactory.eINSTANCE.createEClass();
		owner.setName("Owner");
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName("data");
		attribute.setEType(EcorePackage.Literals.ESTRING);
		attribute.setTransient(isTransient);
		attribute.setDerived(derived);
		attribute.setVolatile(isVolatile);
		attribute.setChangeable(changeable);
		owner.getEStructuralFeatures().add(attribute);
		return attribute;
	}
}
