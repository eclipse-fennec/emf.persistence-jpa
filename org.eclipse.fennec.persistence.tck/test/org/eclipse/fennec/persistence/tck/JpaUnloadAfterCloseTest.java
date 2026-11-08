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
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.repository.spi.AbstractRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * Unloading a resource that was asked to load but has not populated its contents yet (issue
 * #407). EMF collects the contents it notifies as unloaded through {@code getContents()}, and a
 * JPA resource populates on that call — a full load on the way out, which fails once the unit
 * has closed its entity manager. This is what a component holding prototype repositories meets
 * when it is deactivated after the configuration of its unit went away.
 *
 * @author Mark Hoffmann
 * @since 08.11.2026
 */
class JpaUnloadAfterCloseTest {

	static {
		// Same doctrine as AbstractPersistenceTCK (issue #79): H2 caches the JVM zone
		// statically at first use, and this class does not extend the abstract TCK.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "unload";

	private EPackage thingPackage;
	private EClass thingClass;
	private EAttribute thingId;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() throws Exception {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		thingClass = ecore.createEClass();
		thingClass.setName("Thing");
		thingId = ecore.createEAttribute();
		thingId.setName("tid");
		thingId.setEType(EcorePackage.Literals.ESTRING);
		thingId.setID(true);
		thingClass.getEStructuralFeatures().add(thingId);
		thingPackage = ecore.createEPackage();
		thingPackage.setName("unload");
		thingPackage.setNsURI("urn:unload:jpa/1.0");
		thingPackage.setNsPrefix("unload");
		thingPackage.getEClassifiers().add(thingClass);

		List<EClassifier> eClasses = List.of(thingClass);
		emf = JpaTckSupport.bootstrap(PU_NAME, new EntityMapper().createMappings(List.copyOf(eClasses)),
				JpaTestSupport.jdbcProperties(PU_NAME), "create-tables");

		Resource resource = resourceSet().createResource(uriFor("Thing"));
		for (String id : List.of("t1", "t2")) {
			EObject thing = EcoreUtil.create(thingClass);
			thing.eSet(thingId, id);
			resource.getContents().add(thing);
		}
		resource.save(null);
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf) && emf.isOpen()) {
			emf.close();
		}
	}

	/** A loaded, not yet populated resource unloads without a load once its unit is closed. */
	@Test
	void unloadingAResourceAfterItsUnitClosedDoesNotLoadIt() throws Exception {
		Resource resource = resourceSet().getResource(uriFor("Thing"), true);
		assertThat(resource.isLoaded()).isTrue();
		emf.close();

		assertThatCode(resource::unload).doesNotThrowAnyException();

		assertThat(resource.isLoaded()).isFalse();
	}

	/**
	 * The issue's flow: a collection resource of the repository asked to load — as resolving a
	 * reference into it does — then the unit closed, then the repository disposed.
	 */
	@Test
	void disposingARepositoryAfterItsUnitClosedDoesNotLoad() throws Exception {
		AbstractRepository repository = new AbstractRepository(PU_NAME, uriFor("Thing").trimSegments(1),
				this::resourceSet, null, null, null) {
			// all behaviour is generic
		};
		// not after a keyed read: an object it attaches flips isLoaded, and no load is asked then
		assertThat(repository.getResource(uriFor("Thing"), true).isLoaded()).isTrue();
		emf.close();

		assertThatCode(repository::dispose).doesNotThrowAnyException();

		assertThat(repository.isDisposed()).isTrue();
	}

	/** Unloading still detaches what was populated — the raw list is the whole list then. */
	@Test
	void unloadingAPopulatedResourceStillEmptiesIt() throws Exception {
		Resource resource = resourceSet().getResource(uriFor("Thing"), true);
		List<EObject> loaded = List.copyOf(resource.getContents());
		assertThat(loaded).hasSize(2);

		resource.unload();

		assertThat(resource.isLoaded()).isFalse();
		assertThat(loaded).allSatisfy(thing -> assertThat(thing.eResource()).isNull());
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(thingPackage.getNsURI(), thingPackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}
}
