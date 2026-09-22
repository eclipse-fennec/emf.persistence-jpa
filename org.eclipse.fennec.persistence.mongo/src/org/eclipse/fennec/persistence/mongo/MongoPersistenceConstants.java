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

/**
 * Constants of the MongoDB persistence backend.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
public interface MongoPersistenceConstants {

	/** URI scheme handled by the Mongo backend: {@code mongodb://<db>/<collection>[/<id>]}. */
	String URI_SCHEME = "mongodb";

	/** Factory PID for {@code MongoClient} configurations. */
	String CLIENT_PID = "persistence.mongo.client";

	/** Factory PID for {@code MongoDatabase} configurations. */
	String DATABASE_PID = "persistence.mongo.database";

	/** Service property carrying the client identifier. */
	String CLIENT_IDENT = "mongo.client.ident";

	/** Service property carrying the database alias. */
	String DATABASE_ALIAS = "mongo.database.alias";

	/**
	 * Service property carrying the {@link MongoFlavor#id() flavor id} of the server behind
	 * a client or database service (issue #118). Configured once on the client — the server
	 * is a property of the connection, not of a database — and propagated from there, so the
	 * resource factory can derive the query capabilities per alias. Absent means
	 * {@link MongoFlavor#MONGO}.
	 */
	String FLAVOR = "mongo.flavor";

	/** The MongoDB document identifier field. */
	String ID_FIELD = "_id";

	/**
	 * The field a non-containment reference target is written under (issue #277).
	 * <p>
	 * The codec's own default is {@code $ref} — the JSON Reference convention, and unusable
	 * in BSON: MongoDB reserves a {@code $ref} field for DBRefs and requires a {@code $id}
	 * beside it. Every root is saved with a {@code ReplaceOneModel}, and a replacement
	 * document is exactly where the server enforces that rule, so a bare {@code $ref} makes
	 * the whole write fail ({@code The DBRef $ref field must be followed by a $id field},
	 * code 55). MongoDB 5.0 relaxed {@code $}-prefixed field names, which hides this on a
	 * recent server and not on a 4.x one.
	 * <p>
	 * So the Mongo layer pins this key on the codec's resource plane, next to the {@code _id}
	 * and id-format settings it already pins — it is the same BSON key policy, and it governs
	 * reads and writes together. It matches the codec metadata model's own default
	 * ({@code BaseReferenceConfig.refKey}).
	 */
	String REF_FIELD = "_ref";

	/**
	 * Query option carrying the codec {@code ConfigurationResolver} the documents were
	 * written with — the type-predicate translation resolves the effective type
	 * discriminator configuration through it (issue #88).
	 */
	String OPTION_CODEC_RESOLVER = "mongo.codec.configuration.resolver";

	/**
	 * Boolean load/save option (issue #115): take the per-EClass id serialization
	 * configuration from the model's codec annotations instead of the resource's static
	 * composite policy ({@code idFormat=STRUCTURED}, {@code idKeyMode=BOTH}). By default
	 * the resource overrides the model — mongo documents are usually written by this
	 * backend, and model annotations may serve JSON de/serialization concerns instead.
	 * The option governs the serialization plane only; the resource's {@code _id}
	 * contract (compound sub-document for composite classes) is backend identity and
	 * stays. Use it consistently from the resource's first operation — the codec caches
	 * the resolved id configuration per EClass.
	 */
	String OPTION_ID_CONFIG_FROM_MODEL = "mongo.codec.id.fromModel";
}
