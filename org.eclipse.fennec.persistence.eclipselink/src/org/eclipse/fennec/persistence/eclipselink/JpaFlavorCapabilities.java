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

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.fennec.persistence.capabilities.CapabilityDeclaration;
import org.eclipse.fennec.persistence.capabilities.CommandCapabilities;
import org.eclipse.fennec.persistence.capabilities.CommandCapabilitiesBuilder;
import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryCapabilitiesBuilder;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.capabilities.StoreCapabilities;
import org.eclipse.fennec.persistence.capabilities.StoreCapabilitiesBuilder;
import org.eclipse.fennec.persistence.capabilities.StoreFeature;

/**
 * What each {@link JpaFlavor} declares (issue #172) — the relational counterpart of
 * {@code MongoFlavorCapabilities}, built the same way and for the same reason.
 * <p>
 * {@link #BASELINE} is what the JPQL translation can express and every targeted database
 * serves; a flavor is derived from it by <em>exclusion</em>. Restating a full set per flavor
 * would drift — a newly supported feature would have to be added in three places, and a
 * forgotten entry is invisible. Derived-by-exclusion makes a new feature available
 * everywhere by default, so a genuine gap has to be discovered and declared deliberately.
 * <p>
 * <b>All gap sets are currently empty</b>, and that is a statement rather than a placeholder:
 * every query feature this backend declares is served by H2, PostgreSQL and MariaDB alike —
 * the full TCK runs against all three in the matrix. The differences measured so far
 * (identifier quoting, collation, NULL ordering, division semantics — issues #158, #8)
 * live below the capability plane, in translation and DDL rather than in what can be asked.
 * The axis exists because the first real difference is known and waiting:
 * {@code STRING_MATCH_FUZZY} is servable on PostgreSQL and not on H2 (issue #167).
 *
 * @author Mark Hoffmann
 * @since 21.08.2026
 */
public final class JpaFlavorCapabilities {

	/** The backend id these declarations describe — the {@code jpa} query processor's id. */
	public static final String BACKEND = "jpa";

	/**
	 * Service property of a unit (and of its declaration) carrying the null-order fallback
	 * setting as a {@link Boolean} (issue #365); absent means on.
	 */
	public static final String NULL_ORDER_FALLBACK_PROPERTY = "persistence.nullOrderFallback";

	/**
	 * Everything the JPQL translation can express, served by every targeted database.
	 */
	public static final QueryCapabilities BASELINE = QueryCapabilitiesBuilder.create()
			.support(QueryFeature.WHERE_EQ, QueryFeature.WHERE_NE, QueryFeature.WHERE_COMPARISON,
					QueryFeature.WHERE_RANGE, QueryFeature.IS_NULL, QueryFeature.IN,
					QueryFeature.WHERE_STRING_MATCH, QueryFeature.STRING_MATCH_CASE_INSENSITIVE,
					QueryFeature.STRING_FUNCTIONS, QueryFeature.STRING_FUNCTIONS_EXTENDED,
					QueryFeature.ARITHMETIC, QueryFeature.NUMERIC_FUNCTIONS,
					QueryFeature.TEMPORAL_FUNCTIONS, QueryFeature.TYPE_CAST, QueryFeature.TYPE_CHECK,
					QueryFeature.COLLECTION_COUNT, QueryFeature.COLLECTION_COUNT_FILTERED,
					QueryFeature.PIPELINE, QueryFeature.PIPELINE_COMPUTE, QueryFeature.SORT_EXPRESSION,
					QueryFeature.GROUP_EXPRESSION,
					QueryFeature.FIELD_TO_FIELD,
					QueryFeature.LOGICAL_AND, QueryFeature.LOGICAL_OR,
					QueryFeature.LOGICAL_NOT, QueryFeature.EXISTS, QueryFeature.FOR_ALL, QueryFeature.SORT,
					// an explicit null placement either way (issue #365): nothing where it is the
					// database's own, NULLS FIRST/LAST where the platform has it, a CASE key else
					QueryFeature.SORT_NULLS_LOW, QueryFeature.SORT_NULLS_HIGH,
					QueryFeature.LIMIT, QueryFeature.SKIP, QueryFeature.DISTINCT, QueryFeature.COUNT,
					QueryFeature.PROJECTION, QueryFeature.PROJECTION_NESTED,
					QueryFeature.PROJECTION_EXPRESSION, QueryFeature.GROUP_BY,
					QueryFeature.AGG_AVG, QueryFeature.AGG_MIN, QueryFeature.AGG_MAX, QueryFeature.AGG_SUM,
					QueryFeature.AGG_COUNT, QueryFeature.AGG_COUNT_DISTINCT, QueryFeature.TYPE_FILTER,
					QueryFeature.PARAMETERS, QueryFeature.FEATUREPATH_NESTED, QueryFeature.MAP_VALUE,
					QueryFeature.INTERVAL_MATCH,
					// $root resolves to a value before translation (issue #241) — one keyed
					// read, so this is a capability of the resource rather than of JPQL
					QueryFeature.ROOT_REFERENCE,
					QueryFeature.EXPAND,
					// a filtered expansion becomes a keyed second query per chunk of roots, and
					// resolves exactly the proxies it matched (issue #238). EXPAND_PAGE stays
					// undeclared until the window-function slice serves per-parent top/skip.
					QueryFeature.EXPAND_FILTER,
					// per-parent top/skip through a window in a derived table, spliced with
					// EclipseLink's SQL() and filtered from the outside (issue #238, slice 3).
					// Still JPQL — no native query and no second pass; measured on h2, PostgreSQL
					// and MariaDB in JpaWindowFunctionSpikeTest.
					QueryFeature.EXPAND_PAGE,
					// representatives per group, through the same window mechanic (issue #259);
					// the earlier refusal rested on the premise that JPQL has no window functions
					QueryFeature.GROUP_REPRESENTATIVES)
			.maxFeaturePathDepth(-1)
			.build();

	/** H2 gaps — none measured; the full TCK passes. */
	public static final Set<QueryFeature> H2_GAPS = gaps();

	/** PostgreSQL gaps — none measured; the full TCK passes (issue #134 matrix). */
	public static final Set<QueryFeature> POSTGRES_GAPS = gaps();

	/** MariaDB gaps — none measured; the full TCK passes (issue #158). */
	public static final Set<QueryFeature> MARIADB_GAPS = gaps();

	/**
	 * The write verbs, identical on every flavor: the command plane is JPQL and entity
	 * manager operations, which every targeted database serves alike.
	 */
	private static final CommandCapabilities COMMANDS = CommandCapabilitiesBuilder.create()
			.support(CommandFeature.INSERT, CommandFeature.DELETE_BY_SELECTOR,
					CommandFeature.UPDATE_BY_SELECTOR)
			.build();

	/**
	 * Store capabilities, identical on every flavor: a relational connection is
	 * transactional, so the bracket is available wherever this backend runs — unlike the
	 * mongo side, where it depends on the deployment. Deleting clears references on request,
	 * but never ignores them: the foreign keys forbid a dangling reference (issue #347).
	 */
	private static final StoreCapabilities STORE = StoreCapabilitiesBuilder.create()
			.support(StoreFeature.TRANSACTION_BRACKET, StoreFeature.DELETE_CLEAR_REFERENCES)
			.build();

	private static final Map<JpaFlavor, QueryCapabilities> BY_FLAVOR = Map.of(
			JpaFlavor.H2, QueryCapabilitiesBuilder.from(BASELINE).excludeAll(H2_GAPS).build(),
			JpaFlavor.POSTGRES, QueryCapabilitiesBuilder.from(BASELINE).excludeAll(POSTGRES_GAPS).build(),
			JpaFlavor.MARIADB, QueryCapabilitiesBuilder.from(BASELINE).excludeAll(MARIADB_GAPS).build(),
			JpaFlavor.UNKNOWN, BASELINE);

	private JpaFlavorCapabilities() {
	}

	/**
	 * @param flavor the database flavor; {@code null} means {@link JpaFlavor#UNKNOWN}
	 * @return the query capability declaration for {@code flavor}
	 */
	public static QueryCapabilities of(JpaFlavor flavor) {
		return BY_FLAVOR.get(flavor == null ? JpaFlavor.UNKNOWN : flavor);
	}

	/**
	 * The declaration for {@code flavor} under the unit's null-order fallback setting
	 * (issue #365). With the fallback on — the default — a null placement the database cannot
	 * spell is rendered as a {@code CASE} key, which keeps it correct but takes the sort off
	 * any index. With it off, such a placement is not declared and therefore refused with a
	 * Diagnostic: on MariaDB {@code SORT_NULLS_HIGH}, on an unprobed database both.
	 * <p>
	 * This is configuration, not a measured gap, so it stays out of {@link #gapsOf(JpaFlavor)}.
	 *
	 * @param flavor the database flavor; {@code null} means {@link JpaFlavor#UNKNOWN}
	 * @param nullOrderFallback whether the {@code CASE} key may stand in
	 * @return the query capability declaration
	 */
	public static QueryCapabilities of(JpaFlavor flavor, boolean nullOrderFallback) {
		QueryCapabilities declared = of(flavor);
		if (nullOrderFallback) {
			return declared;
		}
		return QueryCapabilitiesBuilder.from(declared).excludeAll(caseKeyPlacements(flavor)).build();
	}

	/**
	 * @param flavor the database flavor; {@code null} means {@link JpaFlavor#UNKNOWN}
	 * @return the null placements {@code flavor} can render only through the {@code CASE} key
	 */
	public static Set<QueryFeature> caseKeyPlacements(JpaFlavor flavor) {
		JpaFlavor resolved = flavor == null ? JpaFlavor.UNKNOWN : flavor;
		EnumSet<QueryFeature> placements = EnumSet.noneOf(QueryFeature.class);
		if (resolved.needsNullCaseKey(true)) {
			placements.add(QueryFeature.SORT_NULLS_LOW);
		}
		if (resolved.needsNullCaseKey(false)) {
			placements.add(QueryFeature.SORT_NULLS_HIGH);
		}
		return Collections.unmodifiableSet(placements);
	}

	/**
	 * @param flavor the database flavor; {@code null} means {@link JpaFlavor#UNKNOWN}
	 * @return the features {@code flavor} does not serve, relative to {@link #BASELINE}
	 */
	public static Set<QueryFeature> gapsOf(JpaFlavor flavor) {
		return switch (flavor == null ? JpaFlavor.UNKNOWN : flavor) {
			case H2 -> H2_GAPS;
			case POSTGRES -> POSTGRES_GAPS;
			case MARIADB -> MARIADB_GAPS;
			case UNKNOWN -> Collections.unmodifiableSet(EnumSet.noneOf(QueryFeature.class));
		};
	}

	/**
	 * @param flavor the database flavor; {@code null} means {@link JpaFlavor#UNKNOWN}
	 * @return everything {@code flavor} declares — query, command and store (issue #172)
	 */
	public static PersistenceCapabilities persistenceCapabilities(JpaFlavor flavor) {
		return persistenceCapabilities(flavor, true);
	}

	/**
	 * @param flavor the database flavor; {@code null} means {@link JpaFlavor#UNKNOWN}
	 * @param nullOrderFallback whether the {@code CASE} key may stand in, see
	 *        {@link #of(JpaFlavor, boolean)}
	 * @return everything {@code flavor} declares under that setting
	 */
	public static PersistenceCapabilities persistenceCapabilities(JpaFlavor flavor, boolean nullOrderFallback) {
		return PersistenceCapabilities.of(of(flavor, nullOrderFallback), COMMANDS, STORE);
	}

	/**
	 * @param flavor the database flavor; {@code null} means {@link JpaFlavor#UNKNOWN}
	 * @return the declaration of this backend and flavor, ready to be registered as a service
	 *         or read directly (issue #172)
	 */
	public static CapabilityDeclaration declaration(JpaFlavor flavor) {
		return declaration(flavor, true);
	}

	/**
	 * @param flavor the database flavor; {@code null} means {@link JpaFlavor#UNKNOWN}
	 * @param nullOrderFallback whether the {@code CASE} key may stand in, see
	 *        {@link #of(JpaFlavor, boolean)}
	 * @return the declaration of this backend and flavor under that setting
	 */
	public static CapabilityDeclaration declaration(JpaFlavor flavor, boolean nullOrderFallback) {
		JpaFlavor resolved = flavor == null ? JpaFlavor.UNKNOWN : flavor;
		return CapabilityDeclaration.of(BACKEND, resolved.id(),
				persistenceCapabilities(resolved, nullOrderFallback));
	}

	private static Set<QueryFeature> gaps(QueryFeature... features) {
		EnumSet<QueryFeature> set = EnumSet.noneOf(QueryFeature.class);
		set.addAll(Arrays.asList(features));
		return Collections.unmodifiableSet(set);
	}
}
