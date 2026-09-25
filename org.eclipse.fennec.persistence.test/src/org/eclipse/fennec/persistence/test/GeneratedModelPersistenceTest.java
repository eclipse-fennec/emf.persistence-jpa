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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicFactory;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicPackage;
import org.eclipse.fennec.emf.osgi.example.model.basic.Person;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.util.EORMResourceFactoryImpl;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;

/**
 * Issue #311 in the runtime it was reported from: a <em>generated</em> model (the emf.osgi
 * example {@code basic}, living in its own bundle) mapped through the eorm loader and an
 * {@code EMPersistenceUnit}, saved and read back through the {@code jpa://} whiteboard. What
 * comes back has to be the generated class — castable to {@link Person} across the bundle
 * boundary — single- and many-valued references included, and deletable.
 */
public class GeneratedModelPersistenceTest {

	@TempDir
	private File modelPath;

	@BeforeEach
	public void before() {
		EntityMappings mapping = new EntityMapper().createMappingsFromEClass(BasicPackage.Literals.PERSON);
		File modelFile = new File(modelPath, "person.eorm");
		String fileUri = modelFile.toURI().toString();
		System.setProperty(TestAnnotations.PROP_MODEL_PATH, modelPath.getAbsolutePath().replace('\\', '/'));
		System.setProperty(TestAnnotations.PROP_MODEL_FILE_PATH, fileUri);
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		rs.getPackageRegistry().put(EORMPackage.eNS_URI, EORMPackage.eINSTANCE);
		rs.getPackageRegistry().put(EPersistencePackage.eNS_URI, EPersistencePackage.eINSTANCE);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("eorm", new EORMResourceFactoryImpl());
		Resource resource = rs.createResource(URI.createURI(fileUri));
		resource.getContents().add(mapping);
		try {
			resource.save(null);
		} catch (IOException e) {
			fail("Failed save mapping in temp file", e);
		}
	}

	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	@TestAnnotations.EORMLoaderConfiguration
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.EMPersistenceUnit", name = "generated", properties = {
			@Property(key = "fennec.jpa.mapping.target", value = "(fennec.jpa.orm.mapping.name=person)"),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "person"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	public void generatedObjectsComeBackAsTheirGeneratedClass(
			@InjectService(filter = "(emf.protocol=jpa)", timeout = 5000) ServiceAware<Resource.Factory> factoryAware,
			@InjectService(filter = "(osgi.unit.name=person)", timeout = 10000) ServiceAware<JPAUnit> unitAware)
			throws Exception {
		Resource.Factory jpaFactory = factoryAware.getService();
		assertNotNull(jpaFactory);
		assertNotNull(unitAware.getService(), "the unit over the generated model is up");

		Person alice = BasicFactory.eINSTANCE.createPerson();
		alice.setId("alice");
		alice.setFirstName("Alice");
		Person bob = BasicFactory.eINSTANCE.createPerson();
		bob.setId("bob");
		bob.setFirstName("Bob");
		bob.getRelatives().add(alice);
		Resource persons = newJpaResourceSet(jpaFactory).createResource(URI.createURI("jpa://person/Person"));
		persons.getContents().add(alice);
		persons.getContents().add(bob);
		persons.save(null);

		// by URI, in a fresh resource set: the cast that used to fail with two classes of one name
		ResourceSet readSet = newJpaResourceSet(jpaFactory);
		EObject read = readSet.getEObject(URI.createURI("jpa://person/Person#bob"), true);
		assertNotNull(read);
		assertSame(BasicPackage.Literals.PERSON, read.eClass());
		assertEquals(BasicFactory.eINSTANCE.createPerson().getClass(), read.getClass(),
				"read back as the generated implementation");
		Person bobRead = assertInstanceOf(Person.class, read);
		assertEquals("Bob", bobRead.getFirstName());
		assertEquals(1, bobRead.getRelatives().size());
		Person aliceRead = bobRead.getRelatives().get(0);
		assertFalse(aliceRead.eIsProxy());
		assertEquals("Alice", aliceRead.getFirstName());
		assertEquals(BasicFactory.eINSTANCE.createPerson().getClass(), aliceRead.getClass());

		// the whole collection too
		Resource loaded = newJpaResourceSet(jpaFactory).createResource(URI.createURI("jpa://person/Person"));
		loaded.load(null);
		assertEquals(2, loaded.getContents().size());
		loaded.getContents().forEach(o -> assertInstanceOf(Person.class, o));

		// and gone again — bob first, since he references alice. Resolved afresh, because
		// navigating his relatives above attached alice to the same collection resource and
		// delete removes what the resource holds. A resource resolved by fragment holds the
		// resolved object only, as long as nobody asks for getContents(): that runs the
		// deferred full population of the collection (#17, #307)
		EObject bobAlone = newJpaResourceSet(jpaFactory).getEObject(URI.createURI("jpa://person/Person#bob"), true);
		((PersistenceResource) bobAlone.eResource()).delete(null);
		Resource remaining = newJpaResourceSet(jpaFactory).createResource(URI.createURI("jpa://person/Person"));
		remaining.load(null);
		assertEquals(1, remaining.getContents().size(),
				"remaining: " + remaining.getContents().stream().map(o -> ((Person) o).getId()).toList());
		assertTrue(remaining.getContents().get(0) instanceof Person p && "alice".equals(p.getId()));
	}

	private static ResourceSet newJpaResourceSet(Resource.Factory jpaFactory) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("jpa", jpaFactory);
		return resourceSet;
	}
}
