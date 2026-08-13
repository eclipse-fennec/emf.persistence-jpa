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
package org.eclipse.fennec.persistence.eclipselink.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

/**
 * Remembers which rows were written as a <b>root of their own resource</b> — the JPA
 * counterpart of the {@code $ref} marker a Mongo document carries (issue #150).
 * <p>
 * A JPA row is identical whether its object was an ordinary containment child or, in
 * addition, a root of its own resource: the foreign key to the parent is there either way. So
 * a load cannot tell the two apart, and attaching every containment child to a resource of its
 * own would be wrong — plain containment must stay inline. This table is the one place that
 * fact can live.
 * <p>
 * Deliberately a side table rather than a column on the entity tables: residency is backend
 * bookkeeping, and the entity tables are generated from the eorm, which describes the model and
 * nothing else. Same reasoning as the Mongo backend's {@code _fennec_ownership}.
 * <p>
 * <b>Nobody pays for a feature they do not use.</b> {@link #inUse(EntityManager, String)} is
 * cached per unit, so a unit that never records residency answers the load path from memory
 * after the first check instead of querying on every load. The flag is set the moment a record
 * is written, and it is never cleared to {@code false} on deletion — an emptied table costing a
 * query per load is a far better failure mode than a stale {@code false} silently collapsing
 * children into their parent's resource.
 * <p>
 * <b>Provisional in one respect:</b> the table is created with native DDL from here rather than
 * generated as a dynamic entity type. The generated route would let EclipseLink handle dialects,
 * but it would add a synthetic type to every persistence unit's bootstrap; with only H2
 * exercised today and the {@code backend × flavor} matrix still ahead (#134), the narrower
 * change is the safer one. The DDL uses nothing beyond {@code VARCHAR} and a composite primary
 * key, and existence is verified after creation rather than relying on
 * {@code IF NOT EXISTS}, so it should port — untested beyond H2, and worth revisiting with the
 * matrix.
 *
 * @author Mark Hoffmann
 * @since 13.08.2026
 */
final class ResidencyRegistry {

	private static final Logger LOG = Logger.getLogger(ResidencyRegistry.class.getName());

	static final String TABLE = "FENNEC_RESIDENCY";

	/** Units whose table has been verified in this JVM — keeps the metadata probe off saves. */
	private static final Set<String> PREPARED = ConcurrentHashMap.newKeySet();

	/** Units known to hold at least one record; absent means "not asked yet". */
	private static final Set<String> IN_USE = ConcurrentHashMap.newKeySet();

	/** Units known to hold none, so the load path can skip without a query. */
	private static final Set<String> UNUSED = ConcurrentHashMap.newKeySet();

	private ResidencyRegistry() {
	}

	/**
	 * The cache key of the database behind an EntityManager: the identity of its factory.
	 * <p>
	 * Neither the persistence unit name nor the {@code JPAUnit} works. The name is shared by
	 * units addressing different databases — every TCK test does, with a fresh in-memory H2
	 * behind the same {@code jpa://xdoc/…}, and a shared key made the second unit believe the
	 * first one's table was its own. The {@code JPAUnit} is created per resource, so it would
	 * give every resource its own cache and defeat the point.
	 */
	static String keyOf(EntityManager em) {
		return String.valueOf(System.identityHashCode(em.getEntityManagerFactory()));
	}

	/** Whether this database's table is known to exist — the guard before touching it. */
	static boolean isPrepared(EntityManager em) {
		return PREPARED.contains(keyOf(em));
	}

	/**
	 * Whether this unit records residency at all. Answered from memory once known; a unit that
	 * never uses the cross-document shape therefore costs a single query per JVM rather than one
	 * per load.
	 */
	static boolean inUse(EntityManager em) {
		String unitKey = keyOf(em);
		if (IN_USE.contains(unitKey)) {
			return true;
		}
		if (UNUSED.contains(unitKey)) {
			return false;
		}
		if (!prepare(em)) {
			return false;
		}
		try {
			Object count = em.createNativeQuery("SELECT COUNT(*) FROM " + TABLE).getSingleResult();
			boolean used = ((Number) count).longValue() > 0;
			(used ? IN_USE : UNUSED).add(unitKey);
			return used;
		} catch (RuntimeException e) {
			LOG.log(Level.FINE, () -> "Could not read " + TABLE + ": " + e.getMessage());
			return false;
		}
	}

	/**
	 * Creates the table unless it already exists, verifying afterwards. Never relies on
	 * {@code CREATE TABLE IF NOT EXISTS}, which is not portable.
	 */
	static boolean prepare(EntityManager em) {
		String unitKey = keyOf(em);
		if (PREPARED.contains(unitKey)) {
			return true;
		}
		if (exists(em)) {
			PREPARED.add(unitKey);
			return true;
		}
		try {
			em.createNativeQuery("CREATE TABLE " + TABLE
					+ " (ENTITY_NAME VARCHAR(255) NOT NULL, ENTITY_ID VARCHAR(255) NOT NULL,"
					+ " PRIMARY KEY (ENTITY_NAME, ENTITY_ID))").executeUpdate();
		} catch (RuntimeException e) {
			// possibly a concurrent creator — the verification below decides
			LOG.log(Level.FINE, () -> "Creation of " + TABLE + " did not apply: " + e.getMessage());
		}
		boolean ready = exists(em);
		if (ready) {
			PREPARED.add(unitKey);
		} else {
			// Deliberately NOT marked prepared, and reported: a swallowed failure here would
			// make every later statement fail inside the caller's transaction and poison it.
			LOG.log(Level.WARNING, () -> TABLE + " is unavailable — cross-document residency "
					+ "will not be recorded for this unit");
		}
		return ready;
	}

	/**
	 * Whether the table can be read. Probed with a trivial query rather than through
	 * {@code DatabaseMetaData}: {@code unwrap(Connection.class)} yields nothing outside an
	 * active transaction, which is exactly where this runs — the preparation is kept out of
	 * the caller's transaction on purpose, so a failing probe cannot poison it.
	 */
	private static boolean exists(EntityManager em) {
		try {
			em.createNativeQuery("SELECT COUNT(*) FROM " + TABLE).getSingleResult();
			return true;
		} catch (RuntimeException e) {
			LOG.log(Level.FINE, () -> TABLE + " not readable: " + e.getMessage());
			return false;
		}
	}

	/** Records that these ids of {@code entityName} are roots of their own resource. */
	static void record(EntityManager em, String entityName, Collection<String> ids) {
		if (ids.isEmpty()) {
			return;
		}
		String unitKey = keyOf(em);
		forget(em, entityName, ids);
		for (String id : ids) {
			em.createNativeQuery("INSERT INTO " + TABLE + " (ENTITY_NAME, ENTITY_ID) VALUES (?, ?)")
					.setParameter(1, entityName)
					.setParameter(2, id)
					.executeUpdate();
		}
		IN_USE.add(unitKey);
		UNUSED.remove(unitKey);
	}

	/** Drops the residency records of these ids. */
	static void forget(EntityManager em, String entityName, Collection<String> ids) {
		if (ids.isEmpty()) {
			return;
		}
		Query query = em.createNativeQuery("DELETE FROM " + TABLE
				+ " WHERE ENTITY_NAME = ? AND ENTITY_ID IN " + placeholders(ids.size()));
		bind(query, entityName, ids);
		query.executeUpdate();
	}

	/** The subset of {@code ids} recorded as residents of their own resource. */
	static Set<String> resident(EntityManager em, String entityName, Collection<String> ids) {
		if (ids.isEmpty()) {
			return Set.of();
		}
		Query query = em.createNativeQuery("SELECT ENTITY_ID FROM " + TABLE
				+ " WHERE ENTITY_NAME = ? AND ENTITY_ID IN " + placeholders(ids.size()));
		bind(query, entityName, ids);
		Set<String> resident = new HashSet<>();
		for (Object row : query.getResultList()) {
			resident.add(String.valueOf(row));
		}
		return resident;
	}

	private static void bind(Query query, String entityName, Collection<String> ids) {
		query.setParameter(1, entityName);
		int index = 2;
		for (String id : ids) {
			query.setParameter(index++, id);
		}
	}

	private static String placeholders(int count) {
		List<String> marks = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			marks.add("?");
		}
		return "(" + String.join(", ", marks) + ")";
	}
}
