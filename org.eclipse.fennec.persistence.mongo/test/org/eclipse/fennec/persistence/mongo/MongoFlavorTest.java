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
	@DisplayName("the DocumentDB gateway is detected by its own marker")
	void detectsDocumentDbGateway() {
		assertThat(MongoFlavor.detect(Map.of("version", "7.0.0", "documentdb", Map.of("version", "0.1"))))
				.contains(MongoFlavor.DOCUMENTDB_PG);
	}

	@Test
	@DisplayName("no marker means indistinguishable, not MongoDB")
	void reportsNoDetectionWithoutMarker() {
		// MONGO would be a claim; empty lets the caller keep the configured flavor and warn
		assertThat(MongoFlavor.detect(Map.of("version", "7.0.14"))).isEmpty();
		assertThat(MongoFlavor.detect(null)).isEmpty();
	}

	@Test
	@DisplayName("ids are stable — they are configuration and service-property values")
	void exposesStableIds() {
		assertThat(MongoFlavor.MONGO.id()).isEqualTo("mongo");
		assertThat(MongoFlavor.FERRETDB.id()).isEqualTo("ferretdb");
		assertThat(MongoFlavor.DOCUMENTDB_PG.id()).isEqualTo("documentdb-pg");
	}
}
