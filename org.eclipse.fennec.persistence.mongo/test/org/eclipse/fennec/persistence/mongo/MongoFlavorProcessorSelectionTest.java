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
import static org.mockito.Mockito.mock;

import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.persistence.mongo.query.MongoQueryProcessor;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoDatabase;

/**
 * How the flavor and an externally supplied {@link QueryProcessor} are reconciled
 * (issue #118), the one place in the flavor wiring with a real branch.
 *
 * @author Mark Hoffmann
 * @since 09.08.2026
 */
class MongoFlavorProcessorSelectionTest {

	@Test
	@DisplayName("a stock processor carries the flavor it was created for")
	void stockProcessorDeclaresItsFlavor() {
		assertThat(new MongoQueryProcessor().flavor()).isEqualTo(MongoFlavor.MONGO);
		assertThat(new MongoQueryProcessor(MongoFlavor.FERRETDB).flavor()).isEqualTo(MongoFlavor.FERRETDB);
		assertThat(new MongoQueryProcessor(null).flavor()).isEqualTo(MongoFlavor.MONGO);
	}

	@Test
	@DisplayName("the backend id stays 'mongo' for every flavor — one translation, several servers")
	void keepsOneBackendId() {
		// the flavor must not fragment the backend: URIs, service filters and the
		// QueryProcessor registry all key on the backend, not on the server product
		for (MongoFlavor flavor : MongoFlavor.values()) {
			assertThat(new MongoQueryProcessor(flavor).backend()).isEqualTo(MongoQueryProcessor.BACKEND);
		}
	}

	@Test
	@DisplayName("a flavored processor declares that flavor's capabilities")
	void flavoredProcessorDeclaresFlavorCapabilities() {
		for (MongoFlavor flavor : MongoFlavor.values()) {
			assertThat(new MongoQueryProcessor(flavor).capabilities().supported())
					.as("capabilities of %s", flavor)
					.isEqualTo(MongoFlavorCapabilities.of(flavor).supported());
		}
	}

	@Test
	@DisplayName("a foreign processor keeps its own capabilities — the flavor does not override it")
	void respectsForeignProcessor() {
		// issue #61 lets a consumer decorate or replace the processor; silently swapping it
		// for a flavored stock one would undo that deliberate customization
		QueryProcessor foreign = mock(QueryProcessor.class);

		MongoResourceFactory factory = new MongoResourceFactory(mock(MongoDatabase.class), mock(MetadataService.class), null, foreign, null,
				MongoFlavor.FERRETDB);

		assertThat(factory.queryProcessor()).isSameAs(foreign);
	}

	@Test
	@DisplayName("no processor plus a gateway flavor yields a flavored stock processor")
	void createsFlavoredProcessorWhenNoneSupplied() {
		MongoResourceFactory factory = new MongoResourceFactory(mock(MongoDatabase.class), mock(MetadataService.class), null, null, null,
				MongoFlavor.FERRETDB);

		assertThat(factory.queryProcessor()).isInstanceOf(MongoQueryProcessor.class);
		assertThat(((MongoQueryProcessor) factory.queryProcessor()).flavor()).isEqualTo(MongoFlavor.FERRETDB);
	}

	@Test
	@DisplayName("a stock processor of the wrong flavor is replaced by the right one")
	void replacesStockProcessorOfWrongFlavor() {
		// the OSGi-registered stock service is the MONGO one; a ferretdb-configured
		// database must not inherit MongoDB's capability claims
		MongoResourceFactory factory = new MongoResourceFactory(mock(MongoDatabase.class), mock(MetadataService.class), null, new MongoQueryProcessor(),
				null, MongoFlavor.FERRETDB);

		assertThat(((MongoQueryProcessor) factory.queryProcessor()).flavor()).isEqualTo(MongoFlavor.FERRETDB);
	}

	@Test
	@DisplayName("for MongoDB the supplied processor is passed through untouched")
	void passesThroughForMongo() {
		QueryProcessor supplied = new MongoQueryProcessor();

		MongoResourceFactory factory = new MongoResourceFactory(mock(MongoDatabase.class), mock(MetadataService.class), null, supplied, null,
				MongoFlavor.MONGO);

		assertThat(factory.queryProcessor()).isSameAs(supplied);
	}
}
