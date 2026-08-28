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

import org.bson.BsonDocument;
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
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;

/**
 * Measures what issue #254 is for: that an expansion resolves a whole level in one query
 * instead of one per proxy.
 * <p>
 * A non-containment reference arrives as a lightweight proxy. Navigating {@code MANY} of them
 * without expand is {@code MANY} keyed finds — the N+1 the issue names. With
 * {@code expand(employer)} the roots' targets are read with a single {@code $in}, attached to
 * their own resource, and the later navigation is satisfied from memory.
 * <p>
 * Counting happens on the driver through a {@link CommandListener}, like
 * {@code MongoDeleteWalkCostTest}, so it measures what goes over the wire rather than what the
 * code appears to do. The assertion is on the <em>shape</em> of the cost — constant in the
 * number of roots — not on an exact number, so it does not become a tripwire for unrelated
 * query-count changes.
 *
 * @author Mark Hoffmann
 */
class MongoExpandCostTest {

	/** Enough roots that O(n) and O(1) cannot be confused; small enough for the normal build. */
	private static final int MANY = 20;

	private EPackage model;
	private EClass personClass;
	private EClass companyClass;
	private EReference personEmployer;

	private MongoClient client;
	private MongoDatabase database;
	private MetadataWhiteboard metadataService;
	private String databaseName;

	/** One entry per {@code find} that went over the wire, by collection. */
	private final List<String> wireFinds = new ArrayList<>();

	@BeforeEach
	void setUp() {
		String connectionString = MongoTestSupport.connectionString();
		assumeTrue(nonNull(connectionString), MongoTestSupport.unavailableMessage());
		buildModel();
		metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(model);
		client = MongoClients.create(MongoClientSettings.builder()
				.applyConnectionString(new ConnectionString(connectionString))
				.addCommandListener(new CommandListener() {
					@Override
					public void commandStarted(CommandStartedEvent event) {
						if (!"find".equals(event.getCommandName())) {
							return;
						}
						BsonDocument command = event.getCommand();
						synchronized (wireFinds) {
							wireFinds.add(command.getString("find").getValue());
						}
					}
				})
				.build());
		databaseName = "expandcost_" + UUID.randomUUID().toString().replace("-", "");
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

	/**
	 * The measurement. Without expand the employer of each person costs its own find; with
	 * expand the whole level costs one.
	 */
	@Test
	void expandResolvesALevelInOneQueryInsteadOfOnePerProxy() throws Exception {
		saveFixture();

		int lazyFinds = employerFindsFor(QueryBuilder.from(personClass).build());
		int expandedFinds = employerFindsFor(QueryBuilder.from(personClass).expand(personEmployer).build());

		assertThat(lazyFinds)
				.as("without expand every proxy is its own keyed find — the N+1 of issue #254")
				.isGreaterThan(1);
		assertThat(expandedFinds)
				.as("with expand the level is read in one $in, whatever the number of roots")
				.isEqualTo(1);
		assertThat(expandedFinds)
				.as("and that is the whole point: constant, not proportional")
				.isLessThan(lazyFinds);
	}

	/** Runs the query, navigates every employer, and counts the finds on the Company collection. */
	private int employerFindsFor(Query query) throws Exception {
		ResourceSet readSet = resourceSet();
		synchronized (wireFinds) {
			wireFinds.clear();
		}
		try (QueryResult result = ((QueryableResource) readSet
				.createResource(uriFor("Person"))).query(query)) {
			List<EObject> persons = result.objects().toList();
			assertThat(persons).hasSize(MANY);
			for (EObject person : persons) {
				EObject employer = (EObject) person.eGet(personEmployer);
				assertThat(employer).isNotNull();
				EObject resolved = employer.eIsProxy()
						? EcoreUtil.resolve(employer, readSet)
						: employer;
				assertThat(resolved.eIsProxy())
						.as("every employer must end up resolvable either way")
						.isFalse();
			}
		}
		synchronized (wireFinds) {
			return (int) wireFinds.stream().filter("Company"::equals).count();
		}
	}

	/** {@code MANY} persons, each employed by a company of its own — one target per root. */
	private void saveFixture() throws Exception {
		ResourceSet writeSet = resourceSet();
		Resource companies = writeSet.createResource(uriFor("Company"));
		Resource persons = writeSet.createResource(uriFor("Person"));
		for (int i = 0; i < MANY; i++) {
			EObject company = create(companyClass, "cid", "c" + i, "name", "Company " + i);
			companies.getContents().add(company);
			EObject person = create(personClass, "pid", "p" + i, "name", "Person " + i);
			person.eSet(personEmployer, company);
			persons.getContents().add(person);
		}
		companies.save(null);
		persons.save(null);
	}

	private void buildModel() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		companyClass = ecore.createEClass();
		companyClass.setName("Company");
		addId(companyClass, "cid");
		addString(companyClass, "name");

		personClass = ecore.createEClass();
		personClass.setName("Person");
		addId(personClass, "pid");
		addString(personClass, "name");
		personEmployer = ecore.createEReference();
		personEmployer.setName("employer");
		personEmployer.setEType(companyClass);
		personEmployer.setContainment(false);
		personEmployer.setUpperBound(1);
		personClass.getEStructuralFeatures().add(personEmployer);

		model = ecore.createEPackage();
		model.setName("expandcost");
		model.setNsURI("urn:expandcost:test/1.0");
		model.setNsPrefix("ec");
		model.getEClassifiers().add(personClass);
		model.getEClassifiers().add(companyClass);
	}

	private static void addId(EClass eClass, String name) {
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName(name);
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		eClass.getEStructuralFeatures().add(id);
	}

	private static void addString(EClass eClass, String name) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		eClass.getEStructuralFeatures().add(attribute);
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(model.getNsURI(), model);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("mongodb", new MongoResourceFactory(database, metadataService, null, null, client));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("mongodb://" + databaseName + "/" + typeName);
	}

	private EObject create(EClass eClass, String idFeature, String id, String nameFeature, String value) {
		EObject object = EcoreUtil.create(eClass);
		object.eSet(eClass.getEStructuralFeature(idFeature), id);
		object.eSet(eClass.getEStructuralFeature(nameFeature), value);
		return object;
	}
}
