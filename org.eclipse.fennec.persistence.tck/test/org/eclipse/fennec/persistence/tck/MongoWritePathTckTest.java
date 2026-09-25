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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.mongo.MongoFlavor;
import org.eclipse.fennec.persistence.mongo.MongoFlavorCapabilities;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * The write-path conformance suite (issue #329) on the Mongo backend, for the flavor selected by
 * {@code -Dmongo.test.flavor}. Every case gets a fresh database, and its store probe reads the raw
 * documents with the driver.
 */
class MongoWritePathTckTest extends AbstractWritePathTCK {

	private MetadataWhiteboard metadataService;
	private MongoClient client;
	private MongoDatabase database;
	private String databaseName;

	@Override
	protected PersistenceCapabilities declaredCapabilities() {
		return MongoFlavorCapabilities.persistenceCapabilities(flavor());
	}

	private static MongoFlavor flavor() {
		return MongoFlavor.byId(MongoTestSupport.flavor())
				.orElseThrow(() -> new IllegalArgumentException("Unknown -Dmongo.test.flavor=" + MongoTestSupport.flavor()));
	}

	@Override
	protected void setUpBackend(EPackage writePathPackage) {
		String connectionString = MongoTestSupport.connectionString();
		assumeTrue(nonNull(connectionString), MongoTestSupport.unavailableMessage());
		metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(writePathPackage);
		client = MongoClients.create(connectionString);
		databaseName = "wp_" + UUID.randomUUID().toString().replace("-", "");
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
		resourceSet.getPackageRegistry().put(wp.getNsURI(), wp);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("mongodb", new MongoResourceFactory(database, metadataService, null, null, client, flavor()));
		return resourceSet;
	}

	@Override
	protected URI uriFor(String typeName) {
		return URI.createURI("mongodb://" + databaseName + "/" + typeName);
	}

	@Override
	protected StoreProbe storeProbe() {
		return new MongoStoreProbe(database);
	}

	/**
	 * Issue #345: the delete path looks up the documents whose reference field points at the
	 * deleted object — to pull an n:m link, or to refuse — and that field is indexed on first use.
	 */
	@Test
	@SuppressWarnings("unchecked")
	void deletingIndexesTheReferenceFieldItLooksUp() throws Exception {
		EClass student = (EClass) wp.getEClassifier("WpStudent");
		EClass course = (EClass) wp.getEClassifier("WpCourse");
		EObject s1 = EcoreUtil.create(student);
		s1.eSet(student.getEIDAttribute(), "s1");
		EObject k1 = EcoreUtil.create(course);
		k1.eSet(course.getEIDAttribute(), "k1");
		((List<EObject>) s1.eGet(student.getEStructuralFeature("courses"))).add(k1);
		ResourceSet resourceSet = createBackendResourceSet();
		Resource courses = resourceSet.createResource(uriFor("WpCourse"));
		courses.getContents().add(k1);
		Resource students = resourceSet.createResource(uriFor("WpStudent"));
		students.getContents().add(s1);
		courses.save(null);
		students.save(null);

		((PersistenceResource) students).delete(null);

		List<String> indexed = new ArrayList<>();
		database.getCollection("WpCourse").listIndexes()
				.forEach(index -> indexed.addAll(index.get("key", Document.class).keySet()));
		assertThat(indexed).contains("students." + MongoPersistenceConstants.REF_FIELD);
	}
}
