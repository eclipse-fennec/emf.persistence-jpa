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

import java.util.Map;
import java.util.UUID;

import org.bson.BsonDocument;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.persistence.helper.CompositeIds;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * The {@code OPTION_ID_CONFIG_FROM_MODEL} contract (issue #115): by default the mongo
 * resource's static composite policy ({@code STRUCTURED}+{@code BOTH}) overrides the
 * model — with the option, the model's codec configuration decides the serialization
 * plane (here observable through the codec's {@code ID_ONLY} default suppressing the id
 * component from the payload). The {@code _id} contract itself (compound sub-document)
 * is backend identity and holds in both modes.
 *
 * @author Mark Hoffmann
 * @since 07.08.2026
 */
class MongoIdConfigOptionTest {

	private EPackage idPackage;
	private EClass orderLineClass;

	private MongoClient client;
	private MongoDatabase database;
	private MetadataWhiteboard metadataService;
	private String databaseName;

	@BeforeEach
	void setUp() {
		String connectionString = MongoTestSupport.connectionString();
		assumeTrue(nonNull(connectionString),
				MongoTestSupport.unavailableMessage());
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		orderLineClass = ecore.createEClass();
		orderLineClass.setName("OrderLine");
		EAttribute orderId = ecore.createEAttribute();
		orderId.setName("orderId");
		orderId.setEType(EcorePackage.Literals.ESTRING);
		orderId.setID(true);
		orderLineClass.getEStructuralFeatures().add(orderId);
		EAttribute lineNo = ecore.createEAttribute();
		lineNo.setName("lineNo");
		lineNo.setEType(EcorePackage.Literals.EINT);
		orderLineClass.getEStructuralFeatures().add(lineNo);
		EAttribute quantity = ecore.createEAttribute();
		quantity.setName("quantity");
		quantity.setEType(EcorePackage.Literals.EINT);
		orderLineClass.getEStructuralFeatures().add(quantity);
		EAnnotation identity = ecore.createEAnnotation();
		identity.setSource(CompositeIds.ANNOTATION_SOURCE);
		identity.getDetails().put(CompositeIds.ID_FEATURES, "orderId,lineNo");
		orderLineClass.getEAnnotations().add(identity);

		idPackage = ecore.createEPackage();
		idPackage.setName("idopt");
		idPackage.setNsURI("urn:idconfigoption:test");
		idPackage.setNsPrefix("idopt");
		idPackage.getEClassifiers().add(orderLineClass);

		metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(idPackage);
		client = MongoClients.create(connectionString);
		databaseName = "idopt_" + UUID.randomUUID().toString().replace("-", "");
		database = client.getDatabase(databaseName);
	}

	@AfterEach
	void tearDown() {
		if (nonNull(database)) {
			database.drop();
		}
		if (nonNull(client)) {
			client.close();
		}
	}

	private Resource newOrderLineResource() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(idPackage.getNsURI(), idPackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("mongodb", new MongoResourceFactory(database, metadataService, null, null, client));
		Resource resource = resourceSet.createResource(
				URI.createURI("mongodb://" + databaseName + "/OrderLine"));
		EObject line = EcoreUtil.create(orderLineClass);
		line.eSet(orderLineClass.getEStructuralFeature("orderId"), "A");
		line.eSet(orderLineClass.getEStructuralFeature("lineNo"), 2);
		line.eSet(orderLineClass.getEStructuralFeature("quantity"), 20);
		resource.getContents().add(line);
		return resource;
	}

	private BsonDocument storedDocument() {
		return database.getCollection("OrderLine", BsonDocument.class).find().first();
	}

	@Test
	void byDefaultTheResourcePolicyOverridesTheModel() throws Exception {
		newOrderLineResource().save(null);
		BsonDocument stored = storedDocument();
		// static policy: compound structured _id AND (idKeyMode=BOTH) the components
		// stay addressable payload fields
		assertThat(stored.get("_id").isDocument()).isTrue();
		assertThat(stored.containsKey("orderId")).isTrue();
		assertThat(stored.containsKey("lineNo")).isTrue();
	}

	@Test
	void theOptionLetsTheModelConfigurationDecide() throws Exception {
		newOrderLineResource().save(Map.of(MongoPersistenceConstants.OPTION_ID_CONFIG_FROM_MODEL, true));
		BsonDocument stored = storedDocument();
		// no injected override: the codec's model-driven configuration applies — its
		// ID_ONLY default suppresses the id component (the single eID fallback,
		// emf.codec#99) from the payload; the backend _id contract still holds
		assertThat(stored.get("_id").isDocument()).isTrue();
		assertThat(stored.containsKey("orderId")).isFalse();
		assertThat(stored.containsKey("lineNo")).isTrue();
	}
}
