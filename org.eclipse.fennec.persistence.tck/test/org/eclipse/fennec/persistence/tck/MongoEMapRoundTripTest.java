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

import java.util.UUID;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * The mongo half of the EMap measurement (issue #171, §9.2) — same model, same four cases as
 * {@code JpaEMapRoundTripTest}, plus an assertion on the stored document, because the codec's
 * {@code EMapHelper} writes a map as a BSON sub-document {@code {key: value}} rather than as an
 * array of entries. That shape is what makes a map queryable here and it is also where a key
 * containing {@code .} or {@code $} would become a problem.
 *
 * @author Mark Hoffmann
 * @since 19.08.2026
 */
class MongoEMapRoundTripTest {

	private EMapTestModel model;
	private MongoClient client;
	private MongoDatabase database;
	private MetadataWhiteboard metadataService;
	private String databaseName;

	@BeforeEach
	void setUp() {
		String connectionString = MongoTestSupport.connectionString();
		assumeTrue(nonNull(connectionString), MongoTestSupport.unavailableMessage());
		model = new EMapTestModel();
		metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(model.ePackage);
		client = MongoClients.create(connectionString);
		databaseName = "emap_" + UUID.randomUUID().toString().replace("-", "");
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

	@Test
	void stringKeyedMapRoundTrips() throws Exception {
		EObject catalog = model.newCatalog("c1", "Spring");
		EMap<Object, Object> map = map(catalog, "attributes");
		map.put("color", "red");
		map.put("size", "L");
		save(catalog);

		EObject loaded = reload("c1");
		EMap<Object, Object> reloaded = map(loaded, "attributes");
		assertThat(reloaded).as("both entries come back").hasSize(2);
		assertThat(reloaded.get("color")).isEqualTo("red");
		assertThat(reloaded.get("size")).isEqualTo("L");
	}

	@Test
	void stringKeyedMapIsStoredAsASubDocument() throws Exception {
		EObject catalog = model.newCatalog("c1", "Spring");
		EMap<Object, Object> map = map(catalog, "attributes");
		map.put("color", "red");
		save(catalog);

		BsonDocument stored = rawDocument("Catalog", "c1");
		assertThat(stored).isNotNull();
		assertThat(stored.containsKey("attributes")).isTrue();
		assertThat(stored.get("attributes").isDocument())
				.as("a map is a sub-document keyed by the map key, not an array of entries")
				.isTrue();
		assertThat(stored.get("attributes").asDocument().getString("color").getValue())
				.isEqualTo("red");
	}

	/**
	 * The write is correct ({@code intKeyedMapStoredShape} passes); the read drops every entry,
	 * because {@code deserializeEMap} assigns the raw field name to the key feature and the
	 * resulting {@code eSet} of a String on an EInt feature is caught and logged. Tracked as
	 * eclipse-fennec/emf.codec#154.
	 */
	@Disabled("emf.codec#154 — a non-string key is dropped on read, the map comes back empty")
	@Test
	void intKeyedMapRoundTrips() throws Exception {
		EObject catalog = model.newCatalog("c2", "Summer");
		EMap<Object, Object> map = map(catalog, "counts");
		map.put(1, "one");
		map.put(42, "answer");
		save(catalog);

		EObject loaded = reload("c2");
		EMap<Object, Object> reloaded = map(loaded, "counts");
		assertThat(reloaded).as("both entries come back").hasSize(2);
		assertThat(reloaded.get(1)).as("an int key must stay an int key").isEqualTo("one");
		assertThat(reloaded.get(42)).isEqualTo("answer");
	}

	/**
	 * Which half of the int-key case is broken — the write or the read. The codec turns a map
	 * key into a BSON field name via {@code toString()}, so the write may well be fine and the
	 * loss happen on the way back, where {@code deserializeEMap} sets the raw field name on the
	 * key feature.
	 */
	@Test
	void intKeyedMapStoredShape() throws Exception {
		EObject catalog = model.newCatalog("c2", "Summer");
		EMap<Object, Object> map = map(catalog, "counts");
		map.put(1, "one");
		save(catalog);

		BsonDocument stored = rawDocument("Catalog", "c2");
		assertThat(stored).isNotNull();
		assertThat(stored.containsKey("counts"))
				.as("stored document was: %s", stored.toJson())
				.isTrue();
		assertThat(stored.get("counts").asDocument().getString("1").getValue())
				.as("stored document was: %s", stored.toJson())
				.isEqualTo("one");
	}

	@Test
	void eObjectValuedMapRoundTrips() throws Exception {
		EObject catalog = model.newCatalog("c3", "Autumn");
		EMap<Object, Object> map = map(catalog, "parts");
		map.put("engine", model.newPart("p1", "V8"));
		map.put("wheel", model.newPart("p2", "Alloy"));
		save(catalog);

		EObject loaded = reload("c3");
		EMap<Object, Object> reloaded = map(loaded, "parts");
		assertThat(reloaded).as("both entries come back").hasSize(2);
		EObject engine = (EObject) reloaded.get("engine");
		assertThat(engine).isNotNull();
		assertThat(engine.eIsProxy()).as("the value must be resolved, not a proxy").isFalse();
		assertThat(engine.eGet(model.partClass.getEStructuralFeature("label"))).isEqualTo("V8");
	}

	@Test
	void puttingTheSameKeyTwiceReplacesTheValue() throws Exception {
		EObject catalog = model.newCatalog("c4", "Winter");
		EMap<Object, Object> map = map(catalog, "attributes");
		map.put("color", "red");
		map.put("color", "blue");
		save(catalog);

		EObject loaded = reload("c4");
		EMap<Object, Object> reloaded = map(loaded, "attributes");
		assertThat(reloaded).as("one key, one entry").hasSize(1);
		assertThat(reloaded.get("color")).isEqualTo("blue");
	}

	/**
	 * The end-to-end proof for {@code MapValue} (issue #186): a map that really is stored as a
	 * sub-document, queried by one entry. The translation tests pin the BSON; this pins that
	 * the BSON matches what the write path produced — the two halves that drift apart when
	 * only one of them is tested.
	 */
	@Test
	void mapValueQueriesOneEntry() throws Exception {
		EObject red = model.newCatalog("c1", "Spring");
		map(red, "attributes").put("color", "red");
		save(red);
		EObject blue = model.newCatalog("c2", "Summer");
		map(blue, "attributes").put("color", "blue");
		save(blue);

		Query query = QueryBuilder.from(model.catalogClass)
				.where(Expressions.mapValue(model.attributes, "color").eq("red"))
				.build();
		QueryableResource resource = (QueryableResource) resourceSet().createResource(uriFor("Catalog"));
		try (QueryResult result = resource.query(query)) {
			assertThat(result.objects()
					.map(catalog -> catalog.eGet(model.catalogClass.getEStructuralFeature("cid"))))
					.containsExactly("c1");
		}
	}

	// ------------------------------------------------------------------ helpers

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EMapTestModel.NS_URI, model.ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("mongodb", new MongoResourceFactory(database, metadataService, null, null, client));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("mongodb://" + databaseName + "/" + typeName);
	}

	private void save(EObject catalog) throws Exception {
		Resource resource = resourceSet().createResource(uriFor("Catalog"));
		resource.getContents().add(catalog);
		resource.save(null);
	}

	private EObject reload(String id) throws Exception {
		Resource resource = resourceSet().createResource(uriFor("Catalog"));
		resource.load(null);
		return resource.getContents().stream()
				.filter(object -> id.equals(object.eGet(model.catalogClass.getEStructuralFeature("cid"))))
				.findFirst()
				.orElseThrow(() -> new AssertionError("catalog '" + id + "' was not loaded back"));
	}

	private BsonDocument rawDocument(String collection, String id) {
		return database.getCollection(collection, BsonDocument.class)
				.find(new BsonDocument("_id", new BsonString(id))).first();
	}

	@SuppressWarnings("unchecked")
	private EMap<Object, Object> map(EObject owner, String feature) {
		Object value = owner.eGet(owner.eClass().getEStructuralFeature(feature));
		assertThat(value)
				.as("EMF must hand out an EMap for '%s' — otherwise the model is not a map at all",
						feature)
				.isInstanceOf(EMap.class);
		return (EMap<Object, Object>) value;
	}
}
