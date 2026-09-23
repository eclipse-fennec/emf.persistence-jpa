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
package org.eclipse.fennec.model.query.analysis;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Which model features and types a query touches, and in which role (issue #292) — the result
 * of {@link FeatureUsageAnalyzer}.
 * <p>
 * Two views answer two different questions. {@link #paths()} and {@link #features(FeatureRole...)}
 * list what the query <em>names</em>. {@link #deliveredTypes()} lists what it hands out as
 * whole objects without naming a single feature — an {@code OBJECTS} query with an empty
 * {@code select} delivers everything its root type holds. {@link #exposedFeatures()} combines
 * both into the set of features whose values leave the store.
 * <p>
 * Delivered features follow the static types only. Instances of a subtype carry additional
 * features; widening {@link #deliveredTypes()} by the subtypes known to the caller's package
 * registry is the caller's decision, since the query model has no registry to consult.
 *
 * @param from the root type of the query, {@code null} for an analyzed bare expression
 * @param deliversObjects whether the query hands out root objects — an {@code OBJECTS} result
 *            or representatives per group
 * @param paths every feature path occurrence, in tree order
 * @param types the types the query names: the root, downcasts, type checks and
 *            {@code RootReference} origins
 * @param deliveredTypes the types handed out as whole objects, including their containment
 *            closure
 * @author Mark Hoffmann
 * @since 23.09.2026
 */
public record FeatureUsage(EClass from, boolean deliversObjects, List<PathUse> paths, Set<EClass> types,
		Set<EClass> deliveredTypes) {

	private static final Set<FeatureRole> OUTPUT_ROLES = Collections.unmodifiableSet(EnumSet.of(
			FeatureRole.PROJECTION, FeatureRole.GROUP, FeatureRole.AGGREGATE, FeatureRole.COMPUTE));

	/**
	 * Creates a feature usage.
	 */
	public FeatureUsage {
		paths = List.copyOf(paths);
		types = Collections.unmodifiableSet(new LinkedHashSet<>(types));
		deliveredTypes = Collections.unmodifiableSet(new LinkedHashSet<>(deliveredTypes));
	}

	/**
	 * Returns every feature any path of the query navigates or addresses.
	 *
	 * @return the features in tree order, never {@code null}
	 */
	public Set<EStructuralFeature> features() {
		Set<EStructuralFeature> features = new LinkedHashSet<>();
		for (PathUse path : paths) {
			features.addAll(path.segments());
		}
		return Collections.unmodifiableSet(features);
	}

	/**
	 * Returns the features that paths in one of the given roles navigate or address.
	 *
	 * @param roles the roles to include
	 * @return the features in tree order, never {@code null}
	 */
	public Set<EStructuralFeature> features(FeatureRole... roles) {
		Set<FeatureRole> wanted = EnumSet.noneOf(FeatureRole.class);
		Collections.addAll(wanted, roles);
		return features(wanted);
	}

	/**
	 * Returns every feature of the {@link #deliveredTypes()}, inherited ones included.
	 *
	 * @return the delivered features, never {@code null}
	 */
	public Set<EStructuralFeature> deliveredFeatures() {
		Set<EStructuralFeature> features = new LinkedHashSet<>();
		for (EClass type : deliveredTypes) {
			features.addAll(type.getEAllStructuralFeatures());
		}
		return Collections.unmodifiableSet(features);
	}

	/**
	 * Returns the features whose values leave the store: the {@link #deliveredFeatures()} plus
	 * everything a projection, grouping key, aggregate or computed column reads. Filter and
	 * sort paths are not part of it — they decide which values are delivered, they are not
	 * delivered themselves.
	 *
	 * @return the exposed features, never {@code null}
	 */
	public Set<EStructuralFeature> exposedFeatures() {
		Set<EStructuralFeature> features = new LinkedHashSet<>(deliveredFeatures());
		features.addAll(features(OUTPUT_ROLES));
		return Collections.unmodifiableSet(features);
	}

	private Set<EStructuralFeature> features(Set<FeatureRole> roles) {
		Set<EStructuralFeature> features = new LinkedHashSet<>();
		for (PathUse path : paths) {
			if (roles.contains(path.role())) {
				features.addAll(path.segments());
			}
		}
		return Collections.unmodifiableSet(features);
	}

}
