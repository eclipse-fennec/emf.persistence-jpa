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

import java.util.Map;
import java.util.Optional;

/**
 * The concrete server implementation behind the MongoDB wire protocol (issue #118).
 * <p>
 * The wire protocol is no longer served only by MongoDB: FerretDB speaks it on top of
 * PostgreSQL with the DocumentDB extension ({@code pg_documentdb_core} /
 * {@code pg_documentdb_api}, originally Microsoft, now under the Linux Foundation), which
 * stores documents in a native Postgres {@code bson} type; Microsoft ships its own gateway
 * in front of the same engine. All of them are reached through the same Mongo Java driver,
 * so the backend id stays {@code mongo} and the URI scheme stays
 * {@code mongodb://<alias>/<collection>} — only the <em>query capability set</em> differs.
 * <p>
 * Flavors are named after the server implementation, not the storage engine. Deliberately
 * no {@code postgres} flavor: the capability boundary is drawn by the DocumentDB extension,
 * not by Postgres, and {@link #FERRETDB} and {@link #DOCUMENTDB_PG} are two gateways in
 * front of that same engine. Deliberately no bare {@code documentdb} either — Amazon
 * DocumentDB is an unrelated product with different gaps, and the short name would invite
 * confusing the two.
 * <p>
 * A native PostgreSQL backend (JDBC, {@code jsonb}/{@code bson}, TimescaleDB hypertables)
 * is <em>not</em> a flavor: it uses a different driver, different configuration and a
 * different resource implementation, and therefore belongs in its own backend.
 *
 * @author Mark Hoffmann
 * @since 09.08.2026
 */
public enum MongoFlavor {

	/** MongoDB itself — the baseline; every capability the translation can express. */
	MONGO("mongo"),

	/** FerretDB 2.x: a wire-protocol gateway over PostgreSQL + DocumentDB extension. */
	FERRETDB("ferretdb"),

	/** The Microsoft/Linux-Foundation DocumentDB gateway over the same Postgres extension. */
	DOCUMENTDB_PG("documentdb-pg");

	/**
	 * {@code buildInfo} field carrying FerretDB's own version. FerretDB reports a MongoDB
	 * version in {@code version} (measured: {@code 7.0.77}), so only this nested document
	 * distinguishes it — a version check would silently conclude "real MongoDB".
	 */
	private static final String BUILD_INFO_FERRETDB = "ferretdb";

	/**
	 * {@code buildInfo} field every real MongoDB build reports, and no measured gateway does.
	 * <p>
	 * The DocumentDB gateway announces itself nowhere: its whole reply is
	 * {@code {version, versionArray, bits, maxBsonObjectSize, ok}} (measured against
	 * {@code documentdb-local}, reporting version {@code 7.0.0}). There is no positive marker
	 * to match, so the only available signal is the <em>absence</em> of the build metadata a
	 * genuine server always carries. That is deliberately used to answer "is this MongoDB?"
	 * — never to claim "this is DocumentDB", which the reply simply does not say.
	 */
	private static final String BUILD_INFO_MONGO_BUILD = "gitVersion";

	private final String id;

	MongoFlavor(String id) {
		this.id = id;
	}

	/** @return the stable configuration/service-property id of this flavor */
	public String id() {
		return id;
	}

	/**
	 * Resolves a flavor from its configured {@link #id()}, case-insensitively.
	 *
	 * @param id the configured id; {@code null} or blank yields {@link #MONGO}
	 * @return the flavor, or {@link Optional#empty()} for an unknown non-blank id — callers
	 *         report that rather than silently degrading to a wrong capability set
	 */
	public static Optional<MongoFlavor> byId(String id) {
		if (id == null || id.isBlank()) {
			return Optional.of(MONGO);
		}
		String normalized = id.trim().toLowerCase();
		for (MongoFlavor flavor : values()) {
			if (flavor.id.equals(normalized)) {
				return Optional.of(flavor);
			}
		}
		return Optional.empty();
	}

	/**
	 * Detects the flavor from a {@code buildInfo} reply — the handshake counterpart of the
	 * configured flavor, used to verify configuration against reality rather than to
	 * replace it (the capability set must be known before any connection exists).
	 *
	 * @param buildInfo the reply of {@code db.adminCommand({buildInfo: 1})} (an
	 *            {@code org.bson.Document} is a {@code Map}); may be {@code null}
	 * @return the detected flavor, or {@link Optional#empty()} when the reply names no
	 *         gateway — which means "did not say", not "MongoDB". Only FerretDB can be
	 *         identified positively; see {@link #looksLikeMongoDb(Map)} for the other
	 *         direction.
	 */
	public static Optional<MongoFlavor> detect(Map<String, Object> buildInfo) {
		if (buildInfo == null) {
			return Optional.empty();
		}
		if (buildInfo.get(BUILD_INFO_FERRETDB) != null) {
			return Optional.of(FERRETDB);
		}
		return Optional.empty();
	}

	/**
	 * Whether a {@code buildInfo} reply looks like a genuine MongoDB build.
	 * <p>
	 * Used for the direction {@link #detect(Map)} cannot serve: the DocumentDB gateway
	 * publishes no marker, so a configured {@code documentdb-pg} can only be cross-checked by
	 * asking whether the server looks like real MongoDB instead. Measured basis: MongoDB
	 * reports {@code gitVersion} (plus {@code sysInfo}, {@code buildEnvironment}, …), the
	 * gateway reports none of it.
	 * <p>
	 * A negative signal by design, so it is used only to warn, never to switch a capability
	 * set: a future server might omit the field for unrelated reasons.
	 *
	 * @param buildInfo the reply; may be {@code null}
	 * @return {@code true} if the reply carries MongoDB's build metadata
	 */
	public static boolean looksLikeMongoDb(Map<String, Object> buildInfo) {
		return buildInfo != null && buildInfo.get(BUILD_INFO_MONGO_BUILD) != null;
	}

	@Override
	public String toString() {
		return id;
	}
}
