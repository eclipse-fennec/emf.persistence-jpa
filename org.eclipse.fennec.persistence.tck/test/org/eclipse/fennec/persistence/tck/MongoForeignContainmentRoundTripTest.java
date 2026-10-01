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

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.geojson.GeoJsonPackage;
import org.geojson.Geometry;
import org.geojson.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * A GeoJSON geometry as the containment child of a {@code Shape} of another package keeps its
 * coordinates on mongo (issue #363).
 * <p>
 * The child is embedded in its parent's document, as any containment child is. What it lost was
 * its persisted form: the GeoJSON model keeps the coordinates in the volatile {@code data}
 * attribute, which the codec skips by default — the document held {@code _type} and the plain
 * containments, the read gave a geometry without coordinates. A feature that is not transient is
 * part of the persisted form and is stored, derived or volatile alike.
 * <p>
 * Every read goes through a fresh resource set — reading through the writing set returns its own
 * objects and hides any loss.
 *
 * @author Juergen Albert
 * @since 01.10.2026
 */
class MongoForeignContainmentRoundTripTest {

	private ShapesTestModel model;
	private MongoClient client;
	private MongoDatabase database;
	private MetadataWhiteboard metadataService;
	private String databaseName;

	@BeforeEach
	void setUp() {
		String connectionString = MongoTestSupport.connectionString();
		assumeTrue(nonNull(connectionString), MongoTestSupport.unavailableMessage());
		model = new ShapesTestModel();
		metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(model.ePackage);
		metadataService.registerPackage(GeoJsonPackage.eINSTANCE);
		client = MongoClients.create(connectionString);
		databaseName = "shapes_" + UUID.randomUUID().toString().replace("-", "");
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

	/** The stored document carries the coordinates, in the shape the model declares for them. */
	@Test
	void theDocumentKeepsThePersistedForm() throws Exception {
		save(model.newShape("point", ShapesTestModel.point()));

		BsonDocument stored = rawDocument("point");
		assertThat(stored).isNotNull();
		BsonDocument geometry = stored.getDocument("geometry");
		assertThat(geometry.containsKey("data"))
				.as("the volatile 'data' attribute is the persisted form — stored document was: %s", stored.toJson())
				.isTrue();
		assertThat(geometry.getArray("data").getValues())
				.extracting(value -> value.asDouble().getValue())
				.containsExactly(7.5, 50.9);
		assertThat(geometry.containsKey("coordinates"))
				.as("the transient object view stays out — stored document was: %s", stored.toJson())
				.isFalse();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("org.eclipse.fennec.persistence.tck.ShapesTestModel#geometries")
	void everyGeometryTypeRoundTrips(String shapeId, Supplier<Geometry> factory) throws Exception {
		Geometry written = factory.get();
		save(model.newShape(shapeId, written));

		EObject loaded = reload(shapeId);
		assertThat(loaded.eGet(model.name)).isEqualTo(shapeId + " shape");
		ShapesTestModel.assertSameGeometry((Geometry) loaded.eGet(model.geometry), written);
	}

	@Test
	void aManyValuedChildListRoundTrips() throws Exception {
		EObject shape = model.newShape("outlined", ShapesTestModel.point());
		@SuppressWarnings("unchecked")
		List<Geometry> outlines = (List<Geometry>) shape.eGet(model.outlines);
		outlines.add(ShapesTestModel.polygon());
		outlines.add(ShapesTestModel.lineString());
		save(shape);

		EObject loaded = reload("outlined");
		@SuppressWarnings("unchecked")
		List<Geometry> reloaded = (List<Geometry>) loaded.eGet(model.outlines);
		assertThat(reloaded).hasSize(2);
		ShapesTestModel.assertSameGeometry(reloaded.get(0), ShapesTestModel.polygon());
		ShapesTestModel.assertSameGeometry(reloaded.get(1), ShapesTestModel.lineString());
	}

	@Test
	void aChildChangedInPlaceIsUpdated() throws Exception {
		save(model.newShape("nudged", ShapesTestModel.point()));

		Resource resource = resourceSet().createResource(uriFor());
		resource.load(null);
		EObject shape = find(resource, "nudged");
		Point point = (Point) shape.eGet(model.geometry);
		point.getCoordinates().setLatitude(-33.9);
		resource.save(null);

		Point expected = ShapesTestModel.point();
		expected.getCoordinates().setLatitude(-33.9);
		ShapesTestModel.assertSameGeometry((Geometry) reload("nudged").eGet(model.geometry), expected);
	}

	// ------------------------------------------------------------------ helpers

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ShapesTestModel.NS_URI, model.ePackage);
		resourceSet.getPackageRegistry().put(GeoJsonPackage.eNS_URI, GeoJsonPackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("mongodb", new MongoResourceFactory(database, metadataService, null, null, client));
		return resourceSet;
	}

	private URI uriFor() {
		return URI.createURI("mongodb://" + databaseName + "/Shape");
	}

	private void save(EObject shape) throws Exception {
		Resource resource = resourceSet().createResource(uriFor());
		resource.getContents().add(shape);
		resource.save(null);
	}

	private EObject reload(String id) throws Exception {
		Resource resource = resourceSet().createResource(uriFor());
		resource.load(null);
		return find(resource, id);
	}

	private EObject find(Resource resource, String id) {
		return resource.getContents().stream()
				.filter(object -> id.equals(object.eGet(model.id)))
				.findFirst()
				.orElseThrow(() -> new AssertionError("shape '" + id + "' was not loaded back"));
	}

	private BsonDocument rawDocument(String id) {
		return database.getCollection("Shape", BsonDocument.class)
				.find(new BsonDocument("_id", new BsonString(id))).first();
	}
}
