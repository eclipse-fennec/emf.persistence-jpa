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

	/** The MongoDB document identifier field. */
	String ID_FIELD = "_id";

	/**
	 * Query option carrying the codec {@code ConfigurationResolver} the documents were
	 * written with — the type-predicate translation resolves the effective type
	 * discriminator configuration through it (issue #88).
	 */
	String OPTION_CODEC_RESOLVER = "mongo.codec.configuration.resolver";
}
