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
import static org.eclipse.fennec.persistence.test.annotations.TestAnnotations.PROP_MODEL_PATH;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit.Lease;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.Test;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.TemplateArgument;
import org.osgi.test.common.annotation.Property.ValueSource;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * OSGi integration test for the {@code jpa} whiteboard architecture (issue #20):
 * <ul>
 * <li>one {@code Resource.Factory} service for the {@code jpa} scheme dispatches by URI
 *     authority to the matching {@link JPAUnit} service — two persistence units on two
 *     databases stay isolated;</li>
 * <li>an unknown unit yields a resource that fails with a diagnostic instead of silently
 *     using another unit;</li>
 * <li>the real EclipseLink factory inside a unit is usage-counted: it stays open while a
 *     lease is held, closes after the configured idle timeout and is transparently
 *     recreated on the next use.</li>
 * </ul>
 */
public class JPAUnitWhiteboardTest extends EPersistenceBase {

	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		EClass personEClass = (EClass) ePackage.getEClassifier("Person");
		assertNotNull(personEClass);
		return mapper.createMappings(List.of(personEClass));
	}

	private ResourceSet newJpaResourceSet(Resource.Factory jpaFactory) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("jpa", jpaFactory);
		return resourceSet;
	}

	private EObject newPerson(JPAUnit unit, String id, String name) {
		try (Lease lease = unit.lease()) {
			ClassDescriptor descriptor = lease.getServerSession().getDescriptorForAlias("Person");
			assertNotNull(descriptor);
			EObject person = (EObject) descriptor.getInstantiationPolicy().buildNewInstance();
			person.eSet(person.eClass().getEStructuralFeature("id"), id);
			person.eSet(person.eClass().getEStructuralFeature("stringDefault"), name);
			return person;
		}
	}

	private static boolean containsId(Resource resource, String id) {
		return resource.getContents().stream()
				.anyMatch(eo -> id.equals(eo.eGet(eo.eClass().getEStructuralFeature("id"))));
	}

	/**
	 * Two persistence units ("unitA", "unitB") on two H2 databases. The single whiteboard
	 * factory must route {@code jpa://unitA/...} and {@code jpa://unitB/...} to their own
	 * backends, and an unknown authority must fail with a diagnostic.
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	@WithFactoryConfiguration(factoryPid = TestAnnotations.PID_DATASOURCE, name = "2", location = "?", properties =
			@Property(key = TestAnnotations.DATASOURCE_PROPERTY_IDENTIFIER, value = "%s/h2/unitB", templateArguments =
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_PATH)))
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "unitA", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments =
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "unitA"),
			@Property(key = "fennec.jpa.dataSource.target", value = "(identifier=%s/h2/%s)", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_PATH),
					@TemplateArgument(source = ValueSource.TestMethod)
			}),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "unitB", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments =
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "unitB"),
			@Property(key = "fennec.jpa.dataSource.target", value = "(identifier=%s/h2/unitB)", templateArguments =
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_PATH)),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	public void testUriDispatchToMultipleUnits(
			@InjectService(filter = "(emf.protocol=jpa)", timeout = 5000) ServiceAware<Resource.Factory> factoryAware,
			@InjectService(filter = "(osgi.unit.name=unitA)", timeout = 5000) ServiceAware<JPAUnit> unitAAware)
			throws Exception {
		Resource.Factory jpaFactory = factoryAware.getService();
		assertNotNull(jpaFactory, "The single jpa whiteboard factory must be registered");

		// Persist into unitA through the whiteboard-routed resource.
		EObject alice = newPerson(unitAAware.getService(), "alice-A", "Alice");
		ResourceSet rs = newJpaResourceSet(jpaFactory);
		Resource resA = rs.createResource(URI.createURI("jpa://unitA/Person"));
		assertNotNull(resA);
		resA.getContents().add(alice);
		resA.save(null);

		// unitB is a different database — Alice must not be visible there.
		Resource resB = rs.createResource(URI.createURI("jpa://unitB/Person"));
		resB.load(null);
		assertFalse(containsId(resB, "alice-A"), "unitB must not see unitA's data");

		// A fresh view of unitA does see her (round-trip through the whiteboard).
		Resource resA2 = newJpaResourceSet(jpaFactory).createResource(URI.createURI("jpa://unitA/Person"));
		resA2.load(null);
		assertTrue(containsId(resA2, "alice-A"), "unitA must see its own data");

		// Unknown unit: resource is created, but access fails with a clear diagnostic.
		Resource resX = rs.createResource(URI.createURI("jpa://doesNotExist/Person"));
		assertNotNull(resX);
		resX.load(null); // lazy — no DB access yet (issue #17)
		assertThrows(RuntimeException.class, resX::getContents);
		assertFalse(resX.getErrors().isEmpty(), "missing unit must surface as diagnostic");
	}

	/**
	 * Usage-count lifecycle: with {@code emfIdleTimeout=1} the real factory stays open
	 * while a lease is held, closes about a second after the last lease is returned, and
	 * the next lease transparently builds a fresh factory.
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "lifecycle", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments =
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "lifecycleUnit"),
			@Property(key = "fennec.jpa.emfIdleTimeout", value = "1"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	public void testIdleCloseAndRecreate(
			@InjectService(filter = "(osgi.unit.name=lifecycleUnit)", timeout = 5000) ServiceAware<JPAUnit> unitAware)
			throws Exception {
		JPAUnit unit = unitAware.getService();
		assertNotNull(unit);

		// First lease builds the real factory.
		Lease l1 = unit.lease();
		EntityManagerFactory f1 = l1.getEntityManagerFactory();
		assertTrue(f1.isOpen(), "factory is built and open on first lease");

		// Usage keeps it alive across the idle window.
		Thread.sleep(1600);
		assertTrue(f1.isOpen(), "an open lease must keep the factory open past the idle timeout");
		assertSame(f1, l1.getEntityManagerFactory());
		l1.close();

		// After the last lease is returned, the idle timeout closes the factory.
		long deadline = System.currentTimeMillis() + 10_000;
		while (f1.isOpen() && System.currentTimeMillis() < deadline) {
			Thread.sleep(50);
		}
		assertFalse(f1.isOpen(), "factory must close after the idle timeout");

		// The next lease transparently builds a fresh, working factory.
		try (Lease l2 = unit.lease()) {
			EntityManagerFactory f2 = l2.getEntityManagerFactory();
			assertNotSame(f1, f2, "a new factory instance must be built after the idle close");
			assertTrue(f2.isOpen());
			try (EntityManager em = l2.createEntityManager()) {
				assertNotNull(em.createQuery("SELECT COUNT(p) FROM Person p", Long.class).getSingleResult(),
						"the recreated factory must be fully functional");
			}
		}
	}
}
