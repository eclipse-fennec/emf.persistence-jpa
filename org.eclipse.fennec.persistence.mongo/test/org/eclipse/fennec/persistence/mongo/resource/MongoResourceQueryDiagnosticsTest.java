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
package org.eclipse.fennec.persistence.mongo.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;

import com.mongodb.client.MongoDatabase;

/**
 * What a failing {@code query(..)} tells its caller (issue #282) — all of it about the
 * diagnostics a consumer reading in {@code @Activate} actually sees: the wrapper must carry
 * the cause's message, a query that brings its own root type must not depend on the package
 * registry having caught up, and no failure may leave {@code query(..)} unwrapped.
 *
 * @since 08.09.2026
 */
@ExtendWith(MockitoExtension.class)
class MongoResourceQueryDiagnosticsTest {

	@Mock
	BundleContext ctx;
	@Mock
	MetadataService metadataService;

	private EClass partitionedDocument;

	/**
	 * A resource over an alias that resolves to nothing — the state a consumer hits when it
	 * queries before the database is known, and the shortest way to a failing collection
	 * access.
	 */
	private MongoResourceImpl unavailableResource() throws Exception {
		when(ctx.getServiceReferences(MongoDatabase.class, null)).thenReturn(List.of());
		MongoResourceFactoryComponent factory = new MongoResourceFactoryComponent(ctx, metadataService);
		Resource resource = factory.createResource(
				URI.createURI(MongoPersistenceConstants.URI_SCHEME + "://nsc/PartitionedDocument"));
		return (MongoResourceImpl) resource;
	}

	@BeforeEach
	void setUp() {
		// deliberately not registered in any EPackage.Registry: the model bundle's
		// EPackageConfigurator has not run yet
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		partitionedDocument = ecore.createEClass();
		partitionedDocument.setName("PartitionedDocument");
		EAttribute id = ecore.createEAttribute();
		id.setName("id");
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		partitionedDocument.getEStructuralFeatures().add(id);
	}

	@Test
	@DisplayName("the wrapper of an execution failure names the cause, not just the collection")
	void executionFailureCarriesTheCauseMessage() throws Exception {
		MongoResourceImpl resource = unavailableResource();

		assertThatThrownBy(() -> resource.query(QueryBuilder.from(partitionedDocument).build()))
				.isInstanceOf(IOException.class)
				// a consumer logging e.getMessage() alone must still learn what went wrong
				.hasMessageContaining("PartitionedDocument")
				.hasMessageContaining("No MongoDB database with alias 'nsc' is available");
	}

	@Test
	@DisplayName("a query brings its own root type — the package registry need not have caught up")
	void theQuerysOwnFromIsUsedAsRootType() throws Exception {
		MongoResourceImpl resource = unavailableResource();

		// before the fix this failed with a raw IllegalArgumentException from QueryContexts,
		// escaping query(..) unwrapped and breaking activation outright
		assertThatThrownBy(() -> resource.query(QueryBuilder.from(partitionedDocument).build()))
				.isInstanceOf(IOException.class)
				.cause()
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	@DisplayName("a query without a root type is refused as an IOException, not as an IllegalArgumentException")
	void aQueryWithoutARootTypeIsRefusedAsIOException() throws Exception {
		MongoResourceImpl resource = unavailableResource();
		Query rootless = QueryFactory.eINSTANCE.createQuery();

		assertThatThrownBy(() -> resource.query(rootless))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("PartitionedDocument");
		assertThat(resource.getErrors()).isNotEmpty();
	}

	@Test
	@DisplayName("a runtime failure inside the translation is wrapped like every other query failure")
	void aRuntimeTranslationFailureIsWrapped(@Mock QueryProcessor broken, @Mock MongoDatabase database) {
		when(broken.validate(any(), any())).thenThrow(new IllegalArgumentException("something deep in the IR"));
		MongoResourceImpl resource = (MongoResourceImpl) new MongoResourceFactory(database,
				metadataService, null, broken).createResource(
						URI.createURI(MongoPersistenceConstants.URI_SCHEME + "://nsc/PartitionedDocument"));

		assertThatThrownBy(() -> resource.query(QueryBuilder.from(partitionedDocument).build()))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("PartitionedDocument")
				.hasMessageContaining("something deep in the IR")
				.cause()
				.isInstanceOf(IllegalArgumentException.class);
		assertThat(resource.getErrors()).isNotEmpty();
	}
}
