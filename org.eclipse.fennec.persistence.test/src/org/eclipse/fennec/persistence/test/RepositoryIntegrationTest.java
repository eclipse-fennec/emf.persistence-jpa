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
package org.eclipse.fennec.persistence.test;

import static org.eclipse.fennec.persistence.test.annotations.TestAnnotations.PROP_MODEL_FILE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.repository.api.PreparedQuery;
import org.eclipse.fennec.persistence.repository.api.ReadRepository;
import org.eclipse.fennec.persistence.repository.api.Repository;
import org.eclipse.fennec.persistence.repository.api.RepositoryService;
import org.eclipse.fennec.persistence.repository.api.WriteRepository;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceObjects;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.TemplateArgument;
import org.osgi.test.common.annotation.Property.ValueSource;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;

/**
 * OSGi integration test for the repository facade over the JPA flavour
 * ({@code fennec.repository.jpa}): one factory configuration binds the persistence unit
 * and registers the repository services; the repository does URI handling and resource
 * lifecycle so the test only touches EObjects, canonical queries and prepared queries.
 * The read-only variant must withhold the write interfaces entirely.
 */
public class RepositoryIntegrationTest extends EPersistenceBase {

	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		EClass personEClass = (EClass) ePackage.getEClassifier("Person");
		assertNotNull(personEClass);
		return mapper.createMappings(List.of(personEClass));
	}

	private static EObject newPerson(EPackage model, String id, String name) {
		EClass personClass = (EClass) model.getEClassifier("Person");
		EObject person = model.getEFactoryInstance().create(personClass);
		person.eSet(personClass.getEStructuralFeature("id"), id);
		person.eSet(personClass.getEStructuralFeature("stringDefault"), name);
		return person;
	}

	/**
	 * Full round-trip through the facade: save/saveAll, keyed read, count/exist,
	 * enumeration, parameterized canonical query, prepared query, and delete.
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "repoUnit", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments =
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "repoUnit"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.repository.jpa", name = "repo", properties = {
			@Property(key = "repositoryId", value = "test-repo"),
			@Property(key = "unit.target", value = "(osgi.unit.name=repoUnit)")
	})
	public void testRepositoryRoundTrip(
			@InjectService(filter = "(persistence.repository.id=test-repo)", timeout = 10000)
			ServiceAware<Repository> repositoryAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)", timeout = 5000)
			ServiceAware<EPackage> modelAware) throws Exception {
		Repository repository = repositoryAware.getService();
		assertNotNull(repository, "the configured repository must be registered");
		EPackage model = modelAware.getService();
		EClass personClass = (EClass) model.getEClassifier("Person");
		EAttribute nameAttribute = (EAttribute) personClass.getEStructuralFeature("stringDefault");

		assertEquals("test-repo", repository.id());
		assertEquals(URI.createURI("jpa://repoUnit"), repository.baseUri());
		assertNotNull(repository.capabilities(), "effective capabilities must be answered");

		// save one object plus a bulk save
		EObject alice = newPerson(model, "p1", "Alice");
		EObject bob = newPerson(model, "p2", "Bob");
		repository.save(alice);
		repository.saveAll(List.of(bob));

		assertEquals(2, repository.count(personClass));
		assertTrue(repository.exist(personClass, "p1"));

		// keyed read through the repository URI handling (jpa://repoUnit/Person#p1)
		EObject loaded = repository.getEObject(personClass, "p1");
		assertNotNull(loaded, "keyed read must find the saved object");
		assertEquals("Alice", loaded.eGet(nameAttribute));

		// enumeration: lazy stream over an unfiltered canonical query
		try (var all = repository.getAllEObjects(personClass)) {
			assertEquals(2, all.count());
		}

		// parameterized canonical query
		Query byName = QueryBuilder.from(personClass)
				.where(Expressions.path(nameAttribute).eq(Expressions.param("wanted")))
				.build();
		try (QueryResult result = repository.find(byName, Map.of("wanted", "Bob"), null)) {
			assertEquals(List.of("Bob"), result.objects().map(person -> person.eGet(nameAttribute)).toList());
		}

		// prepared query: validated once, executed with just the parameters
		PreparedQuery prepared = repository.prepare(byName);
		try (QueryResult result = prepared.execute(Map.of("wanted", "Alice"))) {
			assertEquals(List.of("Alice"), result.objects().map(person -> person.eGet(nameAttribute)).toList());
		}

		// count convenience over a canonical query
		assertEquals(2, repository.count(QueryBuilder.from(personClass).build()));

		// isolated save: both objects modified in memory, only Alice saved — the sibling
		// sharing the loaded collection resource must not be written
		EObject bobLoaded = repository.getEObject(personClass, "p2");
		loaded.eSet(nameAttribute, "Alice2");
		bobLoaded.eSet(nameAttribute, "Bob2");
		repository.save(loaded);
		ResourceSet verification = repository.createResourceSet();
		Resource persons = verification.createResource(URI.createURI("jpa://repoUnit/Person"));
		persons.load(null);
		assertEquals(Set.of("Alice2", "Bob"),
				persons.getContents().stream().map(person -> person.eGet(nameAttribute))
						.collect(Collectors.toSet()),
				"only the saved object's change may reach the store");

		// reload: the unsaved local modification is discarded, in place
		repository.reload(bobLoaded);
		assertEquals("Bob", bobLoaded.eGet(nameAttribute));
		assertNotNull(bobLoaded.eResource(), "attachment must survive the reload");

		// delete isolates the object — Bob must survive
		repository.delete(loaded);
		assertEquals(1, repository.count(personClass));
		try (QueryResult result = repository.find(byName, Map.of("wanted", "Alice2"), null)) {
			assertEquals(0, result.objects().count(), "deleted object must be gone");
		}
	}

	/**
	 * Every consumer gets its own repository instance: a repository owns a
	 * non-thread-safe ResourceSet, so the services are registered with prototype scope —
	 * and a released instance is disposed.
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "protoUnit", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments =
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "protoUnit"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.repository.jpa", name = "proto", properties = {
			@Property(key = "repositoryId", value = "proto-repo"),
			@Property(key = "unit.target", value = "(osgi.unit.name=protoUnit)")
	})
	public void testEachConsumerGetsItsOwnInstance(
			@InjectService(filter = "(persistence.repository.id=proto-repo)", timeout = 10000)
			ServiceAware<Repository> repositoryAware,
			@InjectBundleContext BundleContext context) throws Exception {
		ServiceObjects<Repository> serviceObjects =
				context.getServiceObjects(repositoryAware.getServiceReference());
		Repository first = serviceObjects.getService();
		Repository second = serviceObjects.getService();
		assertNotSame(first, second, "prototype scope: every consumer gets an own instance");
		assertNotSame(first.getResourceSet(), second.getResourceSet(),
				"each instance owns its own ResourceSet");
		assertEquals(first.id(), second.id());

		serviceObjects.ungetService(second);
		assertTrue(second.isDisposed(), "a released instance is disposed");
		assertFalse(first.isDisposed(), "other instances stay untouched");
		serviceObjects.ungetService(first);
		assertTrue(first.isDisposed());
	}

	/**
	 * Invalid configurations must never yield a service: a unit target matching nothing
	 * keeps the component unsatisfied; a configuration without a repositoryId is refused
	 * at activation.
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "invalidUnit", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments =
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "invalidUnit"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.repository.jpa", name = "dangling", properties = {
			@Property(key = "repositoryId", value = "dangling-repo"),
			@Property(key = "unit.target", value = "(osgi.unit.name=doesNotExist)")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.repository.jpa", name = "noid", properties = {
			@Property(key = "unit.target", value = "(osgi.unit.name=invalidUnit)")
	})
	public void testInvalidConfigurationsYieldNoService(
			@InjectService(filter = "(osgi.unit.name=invalidUnit)", timeout = 10000)
			ServiceAware<org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit> unitAware,
			@InjectService(cardinality = 0, filter = "(persistence.repository.id=dangling-repo)")
			ServiceAware<RepositoryService> danglingAware,
			@InjectService(cardinality = 0, filter = "(persistence.repository.baseUri=jpa://invalidUnit)")
			ServiceAware<RepositoryService> noIdAware) throws Exception {
		assertNotNull(unitAware.getService(), "the valid unit itself must be up");
		assertNull(danglingAware.waitForService(1000),
				"a unit target matching nothing must keep the repository unsatisfied");
		assertNull(noIdAware.waitForService(1000),
				"a configuration without repositoryId must be refused at activation");
	}

	/**
	 * A {@code readOnly=true} configuration registers only the read side: the write
	 * interfaces are withheld from the service registry instead of failing at call time.
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "roUnit", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments =
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "roUnit"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.repository.jpa", name = "ro", properties = {
			@Property(key = "repositoryId", value = "ro-repo"),
			@Property(key = "readOnly", value = "true"),
			@Property(key = "unit.target", value = "(osgi.unit.name=roUnit)")
	})
	public void testReadOnlyWithholdsWriteInterfaces(
			@InjectService(filter = "(persistence.repository.id=ro-repo)", timeout = 10000)
			ServiceAware<ReadRepository> readAware,
			@InjectService(cardinality = 0, filter = "(persistence.repository.id=ro-repo)")
			ServiceAware<WriteRepository> writeAware,
			@InjectService(cardinality = 0, filter = "(persistence.repository.id=ro-repo)")
			ServiceAware<Repository> fullAware) throws Exception {
		ReadRepository repository = readAware.getService();
		assertNotNull(repository, "the read side must be registered");
		assertEquals("ro-repo", repository.id());
		assertEquals("jpa", repository.baseUri().scheme());
		assertNull(writeAware.getService(), "readOnly must withhold WriteRepository");
		assertNull(fullAware.getService(), "readOnly must withhold Repository");
	}
}
