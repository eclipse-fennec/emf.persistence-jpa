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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.diagnostic.PersistenceDiagnostic;
import org.eclipse.fennec.persistence.mongo.CollectionNames;
import org.eclipse.fennec.persistence.mongo.MongoFlavor;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Types stored in collections that are not named after them (issue #290), checked in the
 * store: {@code BikeParking} lives in {@code bikeparking}, {@code Station} in
 * {@code stations}. URIs, stored references and the delete-path lookups keep speaking the
 * type name; only the collection opened behind it changes. Every assertion about where a
 * document went reads the database with the driver, not back through the resource.
 *
 * @since 16.10.2026
 */
class MongoCollectionNamesTest {

	private static final String BIKES = "bikeparking";
	private static final String STATIONS = "stations";
	private static final CollectionNames MAPPING = CollectionNames.parse("BikeParking=" + BIKES,
			"Station=" + STATIONS);

	private EPackage parkingPackage;
	private EClass stationClass;
	private EClass bikeClass;
	private EAttribute bikeName;
	private EReference bikeStation;

	private MongoClient client;
	private MongoDatabase database;
	private MetadataWhiteboard metadataService;
	private String databaseName;

	@BeforeEach
	void setUp() {
		String connectionString = MongoTestSupport.connectionString();
		assumeTrue(nonNull(connectionString), MongoTestSupport.unavailableMessage());
		buildModel();
		metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(parkingPackage);
		client = MongoClients.create(connectionString);
		databaseName = "names_" + UUID.randomUUID().toString().replace("-", "");
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

	private void buildModel() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		stationClass = ecore.createEClass();
		stationClass.setName("Station");
		attribute(stationClass, "sid", true);
		attribute(stationClass, "name", false);

		bikeClass = ecore.createEClass();
		bikeClass.setName("BikeParking");
		attribute(bikeClass, "bid", true);
		bikeName = attribute(bikeClass, "name", false);
		bikeStation = ecore.createEReference();
		bikeStation.setName("station");
		bikeStation.setEType(stationClass);
		bikeClass.getEStructuralFeatures().add(bikeStation);

		parkingPackage = ecore.createEPackage();
		parkingPackage.setName("parking");
		parkingPackage.setNsURI("urn:parking:test/1.0");
		parkingPackage.setNsPrefix("parking");
		parkingPackage.getEClassifiers().add(stationClass);
		parkingPackage.getEClassifiers().add(bikeClass);
	}

	private static EAttribute attribute(EClass eClass, String name, boolean id) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		attribute.setID(id);
		eClass.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	// ------------------------------------------------------------------ helpers

	private ResourceSet resourceSet(CollectionNames names) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(parkingPackage.getNsURI(), parkingPackage);
		MongoFlavor flavor = MongoFlavor.byId(MongoTestSupport.flavor()).orElse(MongoFlavor.MONGO);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("mongodb", new MongoResourceFactory(database, metadataService, null, null, client, flavor,
						null, names));
		return resourceSet;
	}

	private ResourceSet resourceSet() {
		return resourceSet(MAPPING);
	}

	private URI uriFor(String typeName) {
		return URI.createURI("mongodb://" + databaseName + "/" + typeName);
	}

	private EObject station(String id, String name) {
		EObject station = EcoreUtil.create(stationClass);
		station.eSet(stationClass.getEStructuralFeature("sid"), id);
		station.eSet(stationClass.getEStructuralFeature("name"), name);
		return station;
	}

	private EObject bike(String id, String name) {
		EObject bike = EcoreUtil.create(bikeClass);
		bike.eSet(bikeClass.getEStructuralFeature("bid"), id);
		bike.eSet(bikeName, name);
		return bike;
	}

	private Resource save(ResourceSet resourceSet, String typeName, EObject... objects) throws IOException {
		Resource resource = resourceSet.createResource(uriFor(typeName));
		resource.getContents().addAll(List.of(objects));
		resource.save(null);
		return resource;
	}

	private Resource load(ResourceSet resourceSet, String typeName) throws IOException {
		Resource resource = resourceSet.createResource(uriFor(typeName));
		resource.load(null);
		return resource;
	}

	private List<String> collections() {
		return database.listCollectionNames().into(new ArrayList<>());
	}

	private BsonDocument raw(String collection, String id) {
		return database.getCollection(collection, BsonDocument.class)
				.find(new BsonDocument("_id", new BsonString(id))).first();
	}

	private static String value(EObject object, String feature) {
		return (String) object.eGet(object.eClass().getEStructuralFeature(feature));
	}

	/** The fixture: two stations and two bike parkings, one of them at a station. */
	private void saveFixture() throws IOException {
		ResourceSet writeSet = resourceSet();
		EObject central = station("s1", "Hauptbahnhof");
		EObject west = station("s2", "Westbahnhof");
		EObject atCentral = bike("b1", "Hauptbahnhof Nord");
		atCentral.eSet(bikeStation, central);
		EObject alone = bike("b2", "Marktplatz");
		save(writeSet, "Station", central, west);
		save(writeSet, "BikeParking", atCentral, alone);
	}

	// ------------------------------------------------------------------ reads of foreign data

	/**
	 * The case of the issue: documents written by hand into a collection that is not named
	 * after the type, without any {@code _type}, read through the type's URI.
	 */
	@Test
	void documentsWrittenOutsideTheBackendAreReadFromTheMappedCollection() throws Exception {
		database.getCollection(BIKES, BsonDocument.class).insertMany(List.of(
				new BsonDocument("_id", new BsonString("b1")).append("name", new BsonString("Hauptbahnhof Nord")),
				new BsonDocument("_id", new BsonString("b2")).append("name", new BsonString("Marktplatz"))));

		Resource resource = load(resourceSet(), "BikeParking");

		assertThat(resource.getContents()).extracting(bike -> value(bike, "name"))
				.containsExactlyInAnyOrder("Hauptbahnhof Nord", "Marktplatz");
		assertThat(resource.getContents()).allSatisfy(bike -> assertThat(bike.eClass()).isSameAs(bikeClass));
	}

	@Test
	void withoutTheMappingTheSameDataIsNotFound() throws Exception {
		database.getCollection(BIKES, BsonDocument.class)
				.insertOne(new BsonDocument("_id", new BsonString("b1")).append("name", new BsonString("x")));

		Resource resource = load(resourceSet(CollectionNames.NONE), "BikeParking");

		assertThat(resource.getContents())
				.as("the control: it is the mapping that finds the data, nothing else")
				.isEmpty();
	}

	// ------------------------------------------------------------------ writes

	@Test
	void aSaveWritesIntoTheMappedCollectionOnly() throws Exception {
		saveFixture();

		assertThat(collections()).contains(BIKES, STATIONS).doesNotContain("BikeParking", "Station");
		assertThat(raw(BIKES, "b1").getString("name").getValue()).isEqualTo("Hauptbahnhof Nord");
		assertThat(raw(STATIONS, "s2").getString("name").getValue()).isEqualTo("Westbahnhof");
	}

	/**
	 * The stored reference keeps the type name: it is a URI, and URIs do not know about
	 * collections. That is what keeps a reference valid across a change of the mapping.
	 */
	@Test
	void aStoredReferenceNamesTheTypeAndResolvesThroughTheMapping() throws Exception {
		saveFixture();

		String stored = raw(BIKES, "b1").getDocument("station").getString(MongoPersistenceConstants.REF_FIELD)
				.getValue();
		assertThat(stored).endsWith("Station#s1").doesNotContain(STATIONS);

		Resource bikes = load(resourceSet(), "BikeParking");
		EObject atCentral = bikes.getContents().stream().filter(bike -> "b1".equals(value(bike, "bid")))
				.findFirst().orElseThrow();
		EObject station = (EObject) atCentral.eGet(bikeStation, true);
		assertThat(station.eIsProxy()).isFalse();
		assertThat(value(station, "name")).isEqualTo("Hauptbahnhof");
	}

	// ------------------------------------------------------------------ keyed, query, count

	@Test
	void aKeyedReadOpensTheMappedCollection() throws Exception {
		saveFixture();

		EObject station = resourceSet().getEObject(uriFor("Station").appendFragment("s2"), true);

		assertThat(station).isNotNull();
		assertThat(value(station, "name")).isEqualTo("Westbahnhof");
	}

	@Test
	void aQueryRunsAgainstTheMappedCollection() throws Exception {
		saveFixture();

		Resource resource = resourceSet().createResource(uriFor("BikeParking"));
		try (QueryResult result = ((QueryableResource) resource).query(QueryBuilder.from(bikeClass)
				.where(Expressions.path(bikeName).eq("Marktplatz")).build())) {
			assertThat(result.objects().toList()).extracting(bike -> value(bike, "bid")).containsExactly("b2");
		}
	}

	@Test
	void countAndExistAskTheMappedCollection() throws Exception {
		saveFixture();
		ResourceSet resourceSet = resourceSet();

		PersistenceResource bikes = (PersistenceResource) resourceSet.createResource(uriFor("BikeParking"));
		assertThat(bikes.count()).isEqualTo(2);
		assertThat(bikes.exist()).isTrue();
	}

	// ------------------------------------------------------------------ delete

	/**
	 * The delete path looks for references in the collections of the referring types — here
	 * {@code BikeParking}, i.e. {@code bikeparking}. Looking in a collection named after the
	 * type would find nothing and let the delete through.
	 */
	@Test
	void aReferencedObjectIsStillRefusedWhenTheReferrerIsMapped() throws Exception {
		saveFixture();

		Resource stations = load(resourceSet(), "Station");
		EObject central = stations.getContents().stream().filter(s -> "s1".equals(value(s, "sid")))
				.findFirst().orElseThrow();
		stations.getContents().retainAll(List.of(central));

		assertThatThrownBy(() -> ((PersistenceResource) stations).delete(null)).isInstanceOf(IOException.class);
		assertThat(stations.getErrors()).anySatisfy(diagnostic -> assertThat(diagnostic)
				.isInstanceOfSatisfying(PersistenceDiagnostic.class,
						d -> assertThat(d.code()).isEqualTo(PersistenceDiagnostic.CODE_REFERENTIAL_INTEGRITY)));
		assertThat(raw(STATIONS, "s1")).as("nothing was removed").isNotNull();
	}

	@Test
	void anUnreferencedObjectIsDeletedFromTheMappedCollection() throws Exception {
		saveFixture();

		Resource stations = load(resourceSet(), "Station");
		stations.getContents().removeIf(s -> !"s2".equals(value(s, "sid")));
		((PersistenceResource) stations).delete(null);

		assertThat(raw(STATIONS, "s2")).isNull();
		assertThat(raw(STATIONS, "s1")).isNotNull();
	}

	// ------------------------------------------------------------------ absence

	/**
	 * A mapped collection that does not exist is a configuration error — reported once, as a
	 * warning, while the read itself answers empty as before.
	 */
	@Test
	void aMissingMappedCollectionIsWarnedAboutOnce() throws Exception {
		List<LogRecord> records = new ArrayList<>();
		Handler handler = new Handler() {
			@Override
			public void publish(LogRecord record) {
				records.add(record);
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() {
			}
		};
		Logger logger = Logger.getLogger("org.eclipse.fennec.persistence.mongo.resource.MongoResourceImpl");
		logger.addHandler(handler);
		try {
			assertThat(load(resourceSet(), "Station").getContents()).isEmpty();
			assertThat(load(resourceSet(), "Station").getContents()).isEmpty();
			assertThat(load(resourceSet(), "BikeParking").getContents()).isEmpty();
		} finally {
			logger.removeHandler(handler);
		}

		List<LogRecord> warnings = records.stream()
				.filter(r -> r.getLevel() == Level.WARNING && r.getParameters() != null)
				.filter(r -> List.of(r.getParameters()).contains(databaseName))
				.toList();
		assertThat(warnings).extracting(r -> r.getParameters()[0])
				.as("one warning per missing collection, not one per read")
				.containsExactlyInAnyOrder(STATIONS, BIKES);
	}
}
