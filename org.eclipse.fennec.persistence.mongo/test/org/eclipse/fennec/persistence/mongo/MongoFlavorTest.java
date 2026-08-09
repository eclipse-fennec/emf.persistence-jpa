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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Flavor vocabulary and handshake detection (issue #118).
 *
 * @author Mark Hoffmann
 * @since 09.08.2026
 */
class MongoFlavorTest {

	@Test
	@DisplayName("configured ids resolve, case-insensitively and trimmed")
	void resolvesConfiguredIds() {
		assertThat(MongoFlavor.byId("mongo")).contains(MongoFlavor.MONGO);
		assertThat(MongoFlavor.byId("ferretdb")).contains(MongoFlavor.FERRETDB);
		assertThat(MongoFlavor.byId("documentdb-pg")).contains(MongoFlavor.DOCUMENTDB_PG);
		assertThat(MongoFlavor.byId("  FerretDB ")).contains(MongoFlavor.FERRETDB);
	}

	@Test
	@DisplayName("no configuration means MongoDB")
	void defaultsToMongo() {
		assertThat(MongoFlavor.byId(null)).contains(MongoFlavor.MONGO);
		assertThat(MongoFlavor.byId("")).contains(MongoFlavor.MONGO);
		assertThat(MongoFlavor.byId("   ")).contains(MongoFlavor.MONGO);
	}

	@Test
	@DisplayName("an unknown id is reported, not silently downgraded")
	void refusesUnknownId() {
		// silently falling back would hand out a capability set that does not describe the
		// server — the caller has to see the misconfiguration
		assertThat(MongoFlavor.byId("postgres")).isEmpty();
		assertThat(MongoFlavor.byId("documentdb")).isEmpty();
	}

	@Test
	@DisplayName("FerretDB is detected by its buildInfo marker, not by the reported version")
	void detectsFerretDbDespiteMongoVersion() {
		// measured against ferretdb-eval:2 — it reports a MongoDB version, so a version
		// check would conclude "real MongoDB"
		Map<String, Object> buildInfo = Map.of(
				"version", "7.0.77",
				"gitVersion", "0d5eb3343a50b800634cc08ace608bb6f3a3f74b",
				"ferretdb", Map.of("version", "v2.7.0", "package", "docker-eval"));

		assertThat(MongoFlavor.detect(buildInfo)).contains(MongoFlavor.FERRETDB);
	}

	@Test
	@DisplayName("the DocumentDB gateway announces nothing, so nothing is detected")
	void doesNotDetectDocumentDbGateway() {
		// measured against documentdb-local: this is the gateway's ENTIRE buildInfo reply —
		// no marker to match, so claiming DOCUMENTDB_PG here would be a guess (issue #122)
		Map<String, Object> buildInfo = Map.of(
				"version", "7.0.0",
				"versionArray", List.of(7, 0, 0, 0),
				"bits", 64,
				"maxBsonObjectSize", 16777216);

		assertThat(MongoFlavor.detect(buildInfo)).isEmpty();
		// but it is recognisably NOT MongoDB, which is the usable signal
		assertThat(MongoFlavor.looksLikeMongoDb(buildInfo)).isFalse();
	}

	@Test
	@DisplayName("no marker means indistinguishable, not MongoDB")
	void reportsNoDetectionWithoutMarker() {
		// MONGO would be a claim; empty lets the caller keep the configured flavor and warn
		assertThat(MongoFlavor.detect(Map.of("version", "7.0.14"))).isEmpty();
		assertThat(MongoFlavor.detect(null)).isEmpty();
	}

	@Test
	@DisplayName("MongoDB is recognised by its build metadata")
	void recognisesRealMongoDb() {
		assertThat(MongoFlavor.looksLikeMongoDb(Map.of("version", "7.0.14", "gitVersion", "abc123"))).isTrue();
		// FerretDB reports a MongoDB version but no gitVersion
		assertThat(MongoFlavor.looksLikeMongoDb(Map.of("version", "7.0.77", "ferretdb", Map.of("version", "v2.7.0"))))
				.isFalse();
		assertThat(MongoFlavor.looksLikeMongoDb(null)).isFalse();
	}

	@Test
	@DisplayName("ids are stable — they are configuration and service-property values")
	void exposesStableIds() {
		assertThat(MongoFlavor.MONGO.id()).isEqualTo("mongo");
		assertThat(MongoFlavor.FERRETDB.id()).isEqualTo("ferretdb");
		assertThat(MongoFlavor.DOCUMENTDB_PG.id()).isEqualTo("documentdb-pg");
	}
}
