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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.mongo.query.MongoQueryProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.mongodb.client.MongoDatabase;

/**
 * Tests the {@code mongodb} whiteboard {@link Resource.Factory} (issue #90): alias
 * dispatch, lazy service resolution, the unavailable-alias contract and the handover
 * of {@link QueryProcessor}/{@link CodecValueRegistry} to created resources.
 *
 * @author Mark Hoffmann
 * @since 04.08.2026
 */
@ExtendWith(MockitoExtension.class)
class MongoResourceFactoryComponentTest {

	@Mock
	BundleContext ctx;
	@Mock
	MetadataService metadataService;
	@Mock
	ServiceReference<MongoDatabase> reference;
	@Mock
	MongoDatabase database;

	MongoResourceFactoryComponent component;

	@BeforeEach
	void setUp() {
		component = new MongoResourceFactoryComponent(ctx, metadataService);
	}

	private void bindDatabase(String alias, ServiceReference<MongoDatabase> ref, MongoDatabase db) {
		when(ref.getProperty(MongoPersistenceConstants.DATABASE_ALIAS)).thenReturn(alias);
		if (db != null) {
			when(ctx.getService(ref)).thenReturn(db);
		}
		component.addDatabase(ref);
	}

	@Test
	void dispatchesByAliasAndResolvesLazily() {
		when(reference.getProperty(MongoPersistenceConstants.DATABASE_ALIAS)).thenReturn("app");
		component.addDatabase(reference);
		// tracking alone must not touch the service
		verify(ctx, never()).getService(reference);

		when(ctx.getService(reference)).thenReturn(database);
		Resource first = component.createResource(URI.createURI("mongodb://app/Person"));
		Resource second = component.createResource(URI.createURI("mongodb://app/Address"));

		assertThat(first).isInstanceOf(MongoResourceImpl.class);
		assertThat(second).isInstanceOf(MongoResourceImpl.class);
		// resolved once, cached afterwards
		verify(ctx, times(1)).getService(reference);
	}

	@Test
	void unknownAliasYieldsResourceFailingWithDiagnostic() {
		Resource resource = component.createResource(URI.createURI("mongodb://ghost/Person"));

		assertThat(resource).isNotNull();
		assertThatThrownBy(() -> resource.load(Map.of()))
				.isInstanceOf(IOException.class)
				.cause()
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("ghost");
		assertThat(resource.getErrors()).hasSize(1);
	}

	@Test
	void missingAuthorityYieldsResourceFailingWithDiagnostic() {
		Resource resource = component.createResource(URI.createURI("mongodb:///Person"));

		assertThat(resource).isNotNull();
		assertThatThrownBy(() -> resource.load(Map.of()))
				.isInstanceOf(IOException.class)
				.cause()
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("does not name a database alias");
	}

	@Test
	void queryProcessorIsHandedToCreatedResources(@Mock QueryProcessor processor) {
		bindDatabase("app", reference, database);
		component.bindQueryProcessor(processor);

		MongoResourceImpl resource = (MongoResourceImpl) component
				.createResource(URI.createURI("mongodb://app/Person"));
		assertThat(resource.queryProcessor()).isSameAs(processor);

		component.unbindQueryProcessor(processor);
		MongoResourceImpl fallback = (MongoResourceImpl) component
				.createResource(URI.createURI("mongodb://app/Person"));
		assertThat(fallback.queryProcessor()).isInstanceOf(MongoQueryProcessor.class);
	}

	@Test
	void greedyRebindKeepsReplacementProcessor(@Mock QueryProcessor departing, @Mock QueryProcessor replacement) {
		component.bindQueryProcessor(departing);
		// greedy rebind: DS binds the replacement before unbinding the old service
		component.bindQueryProcessor(replacement);
		component.unbindQueryProcessor(departing);

		bindDatabase("app", reference, database);
		MongoResourceImpl resource = (MongoResourceImpl) component
				.createResource(URI.createURI("mongodb://app/Person"));
		assertThat(resource.queryProcessor()).isSameAs(replacement);
	}

	@Test
	void valueRegistryIsCopiedPerResource(@Mock CodecValueRegistry registry, @Mock CodecValueRegistry copy) {
		when(registry.copy()).thenReturn(copy);
		bindDatabase("app", reference, database);
		component.bindValueRegistry(registry);

		Resource resource = component.createResource(URI.createURI("mongodb://app/Person"));

		assertThat(resource).isInstanceOf(MongoResourceImpl.class);
		verify(registry).copy();
	}

	@Test
	void duplicateAliasLastWins(@Mock ServiceReference<MongoDatabase> newer, @Mock MongoDatabase newerDb) {
		bindDatabase("app", reference, database);
		component.createResource(URI.createURI("mongodb://app/Person"));

		bindDatabase("app", newer, newerDb);
		// the replaced reference is released together with its cached service
		verify(ctx).ungetService(reference);

		component.createResource(URI.createURI("mongodb://app/Person"));
		verify(ctx, times(1)).getService(newer);
	}

	@Test
	void removeReleasesResolvedService() {
		bindDatabase("app", reference, database);
		component.createResource(URI.createURI("mongodb://app/Person"));

		component.removeDatabase(reference);
		verify(ctx).ungetService(reference);

		Resource resource = component.createResource(URI.createURI("mongodb://app/Person"));
		assertThatThrownBy(() -> resource.load(Map.of()))
				.isInstanceOf(IOException.class)
				.cause()
				.hasMessageContaining("app");
	}

	@Test
	void staleReferenceIsDropped() {
		when(reference.getProperty(MongoPersistenceConstants.DATABASE_ALIAS)).thenReturn("app");
		when(ctx.getService(reference)).thenReturn(null);
		component.addDatabase(reference);

		Resource resource = component.createResource(URI.createURI("mongodb://app/Person"));

		assertThatThrownBy(() -> resource.load(Map.of()))
				.isInstanceOf(IOException.class)
				.cause()
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("app");
	}

	@Test
	void referenceWithoutAliasIsIgnored() {
		when(reference.getProperty(MongoPersistenceConstants.DATABASE_ALIAS)).thenReturn(null);
		component.addDatabase(reference);
		component.removeDatabase(reference);

		verify(ctx, never()).getService(reference);
	}

	@Test
	void deactivateReleasesAllReferences(@Mock ServiceReference<MongoDatabase> unresolved) {
		bindDatabase("app", reference, database);
		component.createResource(URI.createURI("mongodb://app/Person"));
		when(unresolved.getProperty(MongoPersistenceConstants.DATABASE_ALIAS)).thenReturn("other");
		component.addDatabase(unresolved);

		component.deactivate();

		verify(ctx).ungetService(reference);
		verify(ctx).ungetService(unresolved);
	}
}
