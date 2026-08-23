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
 * The query capabilities of each {@link MongoFlavor} (issue #118).
 * <p>
 * {@link MongoFlavor#MONGO} is the baseline — everything the Mongo translation can express.
 * Every other flavor is derived from it by <em>exclusion</em>. Restating a full set per
 * flavor would drift: a newly supported feature would have to be added in three places, and
 * a forgotten entry is invisible. Derived-by-exclusion makes a new feature available
 * everywhere by default, so a genuine gap has to be discovered and declared deliberately.
 * <p>
 * Public so that tests, the documented capability matrix and consumers choosing a server can
 * read the declaration without opening a connection.
 *
 * @author Mark Hoffmann
 * @since 09.08.2026
 */
public final class MongoFlavorCapabilities {

	/** The backend id these declarations describe — the {@code mongo} query processor's id. */
	public static final String BACKEND = "mongo";

	/**
	 * Everything the Mongo translation can express, served by MongoDB itself.
	 * <p>
	 * Paths are unlimited within a document ({@code maxFeaturePathDepth = -1}) — nested
	 * containment is embedded, and cross-document paths are refused by the processor's own
	 * validation rather than by a depth limit.
	 */
	public static final QueryCapabilities BASELINE = QueryCapabilitiesBuilder.create()
			.support(QueryFeature.WHERE_EQ, QueryFeature.WHERE_NE, QueryFeature.WHERE_COMPARISON,
					QueryFeature.WHERE_RANGE, QueryFeature.IS_NULL, QueryFeature.IN,
					QueryFeature.WHERE_STRING_MATCH, QueryFeature.STRING_MATCH_CASE_INSENSITIVE,
					QueryFeature.STRING_FUNCTIONS, QueryFeature.STRING_FUNCTIONS_EXTENDED,
					QueryFeature.ARITHMETIC, QueryFeature.NUMERIC_FUNCTIONS,
					QueryFeature.TEMPORAL_FUNCTIONS, QueryFeature.COLLECTION_COUNT,
					QueryFeature.COLLECTION_COUNT_FILTERED,
					QueryFeature.PIPELINE_COMPUTE, QueryFeature.GROUP_EXPRESSION,
					QueryFeature.TYPE_CAST, QueryFeature.TYPE_CHECK,
					QueryFeature.FIELD_TO_FIELD,
					QueryFeature.LOGICAL_AND, QueryFeature.LOGICAL_OR, QueryFeature.LOGICAL_NOT,
					QueryFeature.EXISTS, QueryFeature.FOR_ALL, QueryFeature.SORT, QueryFeature.LIMIT,
					QueryFeature.SKIP, QueryFeature.DISTINCT, QueryFeature.COUNT, QueryFeature.PROJECTION,
					QueryFeature.PROJECTION_NESTED, QueryFeature.PROJECTION_EXPRESSION,
					QueryFeature.GROUP_BY, QueryFeature.PIPELINE,
					QueryFeature.AGG_AVG, QueryFeature.AGG_MIN, QueryFeature.AGG_MAX, QueryFeature.AGG_SUM,
					QueryFeature.AGG_COUNT, QueryFeature.AGG_COUNT_DISTINCT, QueryFeature.TYPE_FILTER,
					QueryFeature.PARAMETERS, QueryFeature.FEATUREPATH_NESTED,
					QueryFeature.GEO_WITHIN, QueryFeature.GEO_DISTANCE, QueryFeature.MAP_VALUE,
					QueryFeature.INTERVAL_MATCH, QueryFeature.GROUP_REPRESENTATIVES)
			.maxFeaturePathDepth(-1)
			.build();

	/**
	 * FerretDB gaps — <strong>empty</strong>, and that is a measurement, not an omission.
	 * <p>
	 * The full query TCK was run against {@code ghcr.io/ferretdb/ferretdb-eval:2}
	 * (FerretDB 2.7.0 / DocumentDB extension) for issue #119 and passed in its entirety,
	 * including the cases suspected up front to be gaps: 2dsphere geo predicates,
	 * {@code $convert}/{@code $type}, {@code $filter}+{@code $size} collection counts,
	 * temporal and extended string functions, and count-distinct aggregation.
	 * <p>
	 * The one behavioural difference is not a query feature: the gateway is a single logical
	 * server, so multi-document transactions are unavailable. That needs nothing here — the
	 * runtime probe in {@code MongoResourceImpl.capabilities()} already leaves
	 * {@code StoreFeature.TRANSACTION_BRACKET} undeclared for a standalone deployment and
	 * refuses {@code begin()} with a Diagnostic (issues #112/#114).
	 * <p>
	 * Scope of the claim: "no gap in what the TCK exercises". The suite is the measuring
	 * instrument, so a feature it does not reach is untested rather than proven.
	 * <p>
	 * Entries are only ever added from a measurement, and drift matters in both directions: a
	 * gap the server has since closed would make us refuse queries it would serve — a false
	 * negative nobody notices, because nothing fails. The matrix job (issue #121) therefore
	 * fails on unexpected successes too.
	 */
	public static final Set<QueryFeature> FERRETDB_GAPS = gaps();

	/**
	 * DocumentDB gateway gaps — also <strong>empty</strong>, and also measured (issue #122):
	 * the full TCK passes against {@code documentdb-local} (PostgreSQL 17 + DocumentDB
	 * extension + {@code documentdb_gateway}).
	 * <p>
	 * Identical to {@link #FERRETDB_GAPS} on the query plane, which is unsurprising — the same
	 * Postgres extension does the query work. The two flavors are <em>not</em> interchangeable
	 * though, and the difference is outside this class: the DocumentDB gateway announces itself
	 * as mongos ({@code hello.msg=isdbgrid}) and genuinely serves client-session transactions,
	 * while FerretDB presents a standalone server and cannot. Command capabilities are probed
	 * per resource at runtime (issues #112/#114), so that distinction needs no declaration
	 * here — but it is the measured reason the flavors stay separate.
	 */
	public static final Set<QueryFeature> DOCUMENTDB_PG_GAPS = gaps();

	private static final Map<MongoFlavor, QueryCapabilities> BY_FLAVOR = Map.of(
			MongoFlavor.MONGO, BASELINE,
			MongoFlavor.FERRETDB, QueryCapabilitiesBuilder.from(BASELINE).excludeAll(FERRETDB_GAPS).build(),
			MongoFlavor.DOCUMENTDB_PG, QueryCapabilitiesBuilder.from(BASELINE).excludeAll(DOCUMENTDB_PG_GAPS).build());

	private MongoFlavorCapabilities() {
	}

	/**
	 * @param flavor the server flavor; {@code null} means {@link MongoFlavor#MONGO}
	 * @return the capability declaration for {@code flavor}
	 */
	public static QueryCapabilities of(MongoFlavor flavor) {
		return BY_FLAVOR.get(flavor == null ? MongoFlavor.MONGO : flavor);
	}

	/**
	 * @param flavor the server flavor; {@code null} means {@link MongoFlavor#MONGO}
	 * @return the features {@code flavor} does not serve, relative to {@link #BASELINE}
	 */
	public static Set<QueryFeature> gapsOf(MongoFlavor flavor) {
		return switch (flavor == null ? MongoFlavor.MONGO : flavor) {
			case MONGO -> Collections.unmodifiableSet(EnumSet.noneOf(QueryFeature.class));
			case FERRETDB -> FERRETDB_GAPS;
			case DOCUMENTDB_PG -> DOCUMENTDB_PG_GAPS;
		};
	}

	private static Set<QueryFeature> gaps(QueryFeature... features) {
		EnumSet<QueryFeature> set = EnumSet.noneOf(QueryFeature.class);
		set.addAll(Arrays.asList(features));
		return Collections.unmodifiableSet(set);
	}

	/**
	 * The write verbs, identical on every flavor: the command plane goes through the same
	 * codec and the same collection operations everywhere.
	 */
	private static final CommandCapabilities COMMANDS = CommandCapabilitiesBuilder.create()
			.support(CommandFeature.INSERT, CommandFeature.DELETE_BY_SELECTOR,
					CommandFeature.UPDATE_BY_SELECTOR)
			.build();

	/**
	 * Store capabilities per flavor — the one place the flavors genuinely differ outside the
	 * query plane (issues #112/#119/#122): a transaction bracket needs a session-capable
	 * deployment, which MongoDB (replica set) and the DocumentDB gateway (announces itself as
	 * mongos) provide and FerretDB, a standalone server, does not.
	 * <p>
	 * This is the <em>declaration</em>. Whether a concrete deployment really serves it is
	 * probed per resource at runtime and can only narrow this, never exceed it.
	 */
	private static StoreCapabilities storeOf(MongoFlavor flavor) {
		StoreCapabilitiesBuilder store = StoreCapabilitiesBuilder.create();
		if (flavor != MongoFlavor.FERRETDB) {
			store.support(StoreFeature.TRANSACTION_BRACKET);
		}
		return store.build();
	}

	/**
	 * @param flavor the server flavor; {@code null} means {@link MongoFlavor#MONGO}
	 * @return everything {@code flavor} declares — query, command and store (issue #172)
	 */
	public static PersistenceCapabilities persistenceCapabilities(MongoFlavor flavor) {
		MongoFlavor resolved = flavor == null ? MongoFlavor.MONGO : flavor;
		return PersistenceCapabilities.of(of(resolved), COMMANDS, storeOf(resolved));
	}

	/**
	 * @param flavor the server flavor; {@code null} means {@link MongoFlavor#MONGO}
	 * @return the declaration of this backend and flavor, ready to be registered as a service
	 *         or read directly (issue #172)
	 */
	public static CapabilityDeclaration declaration(MongoFlavor flavor) {
		MongoFlavor resolved = flavor == null ? MongoFlavor.MONGO : flavor;
		return CapabilityDeclaration.of(BACKEND, resolved.id(), persistenceCapabilities(resolved));
	}
}
