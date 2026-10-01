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

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.persistence.helper.EMFHelper;

/**
 * The per-feature codec configuration that makes a document keep the persisted form the model
 * declares (issue #363): a {@code derived} or {@code volatile} feature that is not transient is
 * written and read like any other.
 * <p>
 * The codec skips such features unless they are forced, which suits a JSON API but not a store:
 * the GeoJSON model keeps the coordinates of a geometry in the volatile {@code data} attribute,
 * and without it a stored geometry came back as type and structure only. Which features need it
 * is decided by {@link EMFHelper#isPersisted(EStructuralFeature)}, the rule the relational store
 * applies as well.
 * <p>
 * The codec's resolver looks a feature up in this map ({@code eAttributeConfig} or
 * {@code eReferenceConfig}) when it first resolves the feature, which may be a class of any
 * package — a GeoJSON {@code Point} met as the child of a document is known only once it is
 * decoded. So the map is not filled in advance but answers by the rule: {@link #get(Object)}
 * computes the configuration and remembers it, and {@link #entrySet()} shows what has been
 * answered so far. The resolver copies its property maps shallowly, so the instance it holds is
 * this one.
 *
 * @param <K> the feature type, {@code EAttribute} or {@code EReference}
 * @author Juergen Albert
 * @since 01.10.2026
 */
final class PersistedFormConfig<K extends EStructuralFeature> extends AbstractMap<K, Map<String, Object>> {

	private static final Map<String, Object> FORCED = Map.of(
			ConfigProperty.FORCE_WRITE.getKey(), Boolean.TRUE,
			ConfigProperty.FORCE_READ.getKey(), Boolean.TRUE);

	private final Class<K> featureType;
	private final Map<K, Map<String, Object>> answered = new ConcurrentHashMap<>();

	/**
	 * @param featureType the feature type the map is keyed by
	 */
	PersistedFormConfig(Class<K> featureType) {
		this.featureType = featureType;
	}

	/**
	 * Whether the codec must be told to write and read the feature: it is part of the persisted
	 * form, and the codec would skip it by default.
	 *
	 * @param feature the feature
	 * @return {@code true} if the feature has to be forced
	 */
	static boolean isForced(EStructuralFeature feature) {
		return (feature.isDerived() || feature.isVolatile()) && EMFHelper.isPersisted(feature);
	}

	@Override
	public Map<String, Object> get(Object key) {
		if (!featureType.isInstance(key)) {
			return null;
		}
		K feature = featureType.cast(key);
		return isForced(feature) ? answered.computeIfAbsent(feature, f -> FORCED) : null;
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	@Override
	public Set<Entry<K, Map<String, Object>>> entrySet() {
		return Collections.unmodifiableSet(answered.entrySet());
	}
}
