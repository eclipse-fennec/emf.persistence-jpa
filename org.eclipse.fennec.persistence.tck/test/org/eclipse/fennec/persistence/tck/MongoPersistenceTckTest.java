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
package org.eclipse.fennec.persistence.tck;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.persistence.capabilities.CommandCapabilitiesBuilder;
import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.capabilities.StoreCapabilitiesBuilder;
import org.eclipse.fennec.persistence.capabilities.StoreFeature;
import org.eclipse.fennec.persistence.mongo.MongoFlavor;
import org.eclipse.fennec.persistence.mongo.MongoFlavorCapabilities;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * TCK binding for the MongoDB backend. Requires a reachable MongoDB — either via
 * {@code -Dmongo.uri} / {@code MONGO_URI} or a Docker/Podman-started container
 * (see {@link MongoTestSupport}); otherwise the tests are skipped.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
class MongoPersistenceTckTest extends AbstractPersistenceTCK {

	/**
	 * The Mongo declaration (issue #160), answerable without a connection. The query
	 * vocabulary is the measured flavor declaration (issues #118/#119/#122) — the find-sort
	 * cannot order by expressions (issue #84) and expand hints are refused (issue #95), so
	 * neither appears in any flavor's baseline. The transaction bracket follows the
	 * deployment the flavor implies: FerretDB presents a standalone server and cannot serve
	 * multi-document transactions, MongoDB (single-node replica set container) and the
	 * DocumentDB gateway (announces itself as mongos) genuinely do — the runtime probe in
	 * {@code MongoResourceImpl} narrows to exactly the same answer, which
	 * {@code effectiveCapabilitiesNeverExceedTheDeclaration} asserts.
	 */
	/**
	 * Disabled on mongo by a codec defect, not by a capability: a subtype read through its
	 * supertype's collection comes back as the supertype, because the decode path lets the
	 * expected-type hint outrank the stored {@code _type} discriminator — reported as
	 * <a href="https://github.com/eclipse-fennec/emf.codec/issues/160">emf.codec#160</a>.
	 * <p>
	 * Deliberately an override rather than a capability gate (issue #174): a core case cannot
	 * be declined, so this has to read as "known defect, tracked" and disappear again the
	 * moment the codec resolves it. Queries are unaffected — they match on the stored
	 * discriminator, which is why {@code queryTypeCheckAndTreat} stays green here.
	 */
	@Test
	@Disabled("emf.codec#160 — decode prefers EXPECTED_TYPE over the stored _type discriminator")
	@Override
	public void polymorphicRoundTrip() throws Exception {
		super.polymorphicRoundTrip();
	}

	@Override
	protected PersistenceCapabilities declaredCapabilities() {
		return MongoFlavorCapabilities.persistenceCapabilities(flavor());
	}

	// composite ids map to a compound structured _id via the codec id plane since
	// issue #110 — core, exercised unconditionally

	/**
	 * The server flavor under test, from {@code -Dmongo.test.flavor} (issue #118). Unknown
	 * ids fail loudly: silently testing MongoDB while believing it is FerretDB would make a
	 * green run meaningless.
	 */
	private static MongoFlavor flavor() {
		return MongoFlavor.byId(MongoTestSupport.flavor())
				.orElseThrow(() -> new IllegalArgumentException(
						"Unknown -Dmongo.test.flavor=" + MongoTestSupport.flavor()));
	}

	// command transactions run for real since issue #112: the factory carries the
	// session-capable client and the test container is a single-node replica set

	private MongoClient client;
	private MongoDatabase database;
	private MetadataWhiteboard metadataService;
	private String databaseName;

	@Override
	protected void setUpBackend(EPackage tckPackage) {
		String connectionString = MongoTestSupport.connectionString();
		assumeTrue(nonNull(connectionString),
				MongoTestSupport.unavailableMessage());
		metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(tckPackage);
		client = MongoClients.create(connectionString);
		databaseName = "tck_" + UUID.randomUUID().toString().replace("-", "");
		database = client.getDatabase(databaseName);
	}

	@Override
	protected void tearDownBackend() {
		if (nonNull(database)) {
			database.drop();
			database = null;
		}
		if (nonNull(client)) {
			client.close();
			client = null;
		}
	}

	@Override
	protected ResourceSet createBackendResourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(tckPackage.getNsURI(), tckPackage);
		// the session-capable client unlocks command transactions (issue #112) — the
		// test container runs a single-node replica set. The flavor (issue #118) narrows the
		// declared query capabilities to what this server actually serves.
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("mongodb", new MongoResourceFactory(database, metadataService, null, null, client, flavor()));
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		return resourceSet;
	}

	@Override
	protected URI uriFor(String typeName) {
		return URI.createURI("mongodb://" + databaseName + "/" + typeName);
	}

	/**
	 * Documented backend contract for the int-id model: Mongo can only generate ids for
	 * String-typed id attributes (ObjectId hex) — for numeric ids the save fails with a
	 * clear error instead of inventing values. The String-id binding
	 * ({@link MongoStringIdPersistenceTckTest}) inherits the regular generation test.
	 */
	@Override
	@Test
	public void idGenerationOnSaveAssignsAndWritesBackId() throws Exception {
		if (personClass.getEIDAttribute().getEAttributeType().getInstanceClass() != String.class) {
			EObject person = newPersonWithoutId("Generated", 33);
			ResourceSet writeSet = createBackendResourceSet();
			Resource resource = writeSet.createResource(uriFor("Person"));
			resource.getContents().add(person);
			assertThatThrownBy(() -> resource.save(null))
					.isInstanceOf(IOException.class)
					.hasMessageContaining("cannot generate an ObjectId");
			return;
		}
		super.idGenerationOnSaveAssignsAndWritesBackId();
	}
}
