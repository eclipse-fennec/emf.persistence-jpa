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

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * The JPA counterpart of {@code MongoCascadeDeleteTest} — the nested shape was measured on
 * Mongo only, which left the transitive case untested on JPA even though that is where the
 * orphan is hardest to find:
 *
 * <pre>
 * Library                       own row, root of jpa://cascade/Library
 *  └─ section    containment    own row, FK to Library — an ordinary containment child
 *      └─ archive containment   own row AND root of jpa://cascade/Archive
 * </pre>
 *
 * Dropping {@code section} from the library is an ordinary root update. The archive is owned
 * <em>transitively</em> by the removed subtree, so it has to go with it — and no future save
 * of the library can rediscover it, because the node that referenced it is gone.
 * <p>
 * The model is built in code rather than added to {@code tck.ecore}: the shared model has no
 * two-level containment chain, and extending it would touch both id variants and every
 * binding's bootstrap list for one test class.
 *
 * @author Mark Hoffmann
 * @since 13.08.2026
 */
class JpaCascadeDeleteTest {

	static {
		// Same doctrine as AbstractPersistenceTCK (issue #79): H2 caches the JVM zone
		// statically at first use, and this class does not extend the abstract TCK.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "cascade";

	private EPackage cascadePackage;
	private EClass libraryClass;
	private EClass sectionClass;
	private EClass archiveClass;
	private EReference librarySection;
	private EReference sectionArchive;

	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() {
		buildModel();
		List<EClassifier> eClasses = new ArrayList<>();
		eClasses.add(libraryClass);
		eClasses.add(sectionClass);
		eClasses.add(archiveClass);
		emf = JpaTckSupport.bootstrap(PU_NAME, eClasses);
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
			emf = null;
		}
	}

	private void buildModel() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		archiveClass = ecore.createEClass();
		archiveClass.setName("Archive");
		addId(archiveClass, "arid");
		addString(archiveClass, "label");

		sectionClass = ecore.createEClass();
		sectionClass.setName("Section");
		addId(sectionClass, "sid");
		addString(sectionClass, "title");
		sectionArchive = reference("archive", archiveClass);
		sectionClass.getEStructuralFeatures().add(sectionArchive);

		libraryClass = ecore.createEClass();
		libraryClass.setName("Library");
		addId(libraryClass, "lid");
		addString(libraryClass, "name");
		librarySection = reference("section", sectionClass);
		libraryClass.getEStructuralFeatures().add(librarySection);

		cascadePackage = ecore.createEPackage();
		cascadePackage.setName("cascade");
		cascadePackage.setNsURI("urn:cascade:jpa/1.0");
		cascadePackage.setNsPrefix("cascade");
		cascadePackage.getEClassifiers().add(libraryClass);
		cascadePackage.getEClassifiers().add(sectionClass);
		cascadePackage.getEClassifiers().add(archiveClass);
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

	/** Single-valued containment — the arity that makes the nesting readable. */
	private static EReference reference(String name, EClass type) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(name);
		reference.setEType(type);
		reference.setContainment(true);
		reference.setUpperBound(1);
		return reference;
	}

	// ------------------------------------------------------------------ helpers

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(cascadePackage.getNsURI(), cascadePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}

	private EObject create(EClass eClass, String idFeature, String id, String nameFeature, String value) {
		EObject object = EcoreUtil.create(eClass);
		object.eSet(eClass.getEStructuralFeature(idFeature), id);
		object.eSet(eClass.getEStructuralFeature(nameFeature), value);
		return object;
	}

	private long countRows(String entityAlias) {
		try (EntityManager em = emf.createEntityManager()) {
			return em.createQuery("SELECT COUNT(e) FROM " + entityAlias + " e", Long.class)
					.getSingleResult();
		}
	}

	private static Object value(EObject object, String feature) {
		return object.eGet(object.eClass().getEStructuralFeature(feature));
	}

	/**
	 * Builds {@code Library → section → archive} with the archive as a root of its own
	 * resource and writes both. Parent-first, the order that works today — child-first is
	 * blocked by #130.
	 */
	private ResourceSet saveNestedFixture() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject library = create(libraryClass, "lid", "l1", "name", "Central");
		EObject section = create(sectionClass, "sid", "s1", "title", "Rare Books");
		EObject archive = create(archiveClass, "arid", "a1", "label", "Vault");
		library.eSet(librarySection, section);
		section.eSet(sectionArchive, archive);

		Resource archiveResource = writeSet.createResource(uriFor("Archive"));
		archiveResource.getContents().add(archive);
		// the cross-document shape: owned by the section, resident in its own resource
		assertThat(((InternalEObject) archive).eDirectResource()).isSameAs(archiveResource);
		assertThat(archive.eContainer()).isSameAs(section);

		Resource libraryResource = writeSet.createResource(uriFor("Library"));
		libraryResource.getContents().add(library);
		libraryResource.save(null);
		archiveResource.save(null);
		return writeSet;
	}

	// -------------------------------------------------------------------- the shape

	/**
	 * The fixture itself, so the cascade cases below cannot be dismissed as a broken setup:
	 * one row per level, the nesting intact on reload.
	 */
	@Test
	void nestedCrossDocumentContainmentRoundTrips() throws Exception {
		saveNestedFixture();

		assertThat(countRows("Library")).isEqualTo(1);
		assertThat(countRows("Section")).isEqualTo(1);
		assertThat(countRows("Archive")).as("grandchild has its own row").isEqualTo(1);

		emf.getCache().evictAll();

		ResourceSet readSet = resourceSet();
		EObject library = readSet.getResource(uriFor("Library"), true).getContents().get(0);
		EObject section = (EObject) library.eGet(librarySection);
		assertThat(value(section, "sid")).isEqualTo("s1");
		EObject archive = (EObject) section.eGet(sectionArchive);
		assertThat(archive).as("nested cross-document child resolved").isNotNull();
		assertThat(archive.eIsProxy()).isFalse();
		assertThat(value(archive, "label")).isEqualTo("Vault");
	}

	// ------------------------------------------------------------------ cascade delete

	/**
	 * The transitive case: the archive is owned by the section, and the section is dropped
	 * from the library. Nothing references either afterwards, so both rows must go — and the
	 * archive is the interesting one, since its owner no longer exists to be asked.
	 */
	@Test
	void droppingIntermediateContainmentDeletesTheCrossDocumentGrandchild() throws Exception {
		ResourceSet writeSet = saveNestedFixture();
		Resource libraryResource = writeSet.getResource(uriFor("Library"), false);
		EObject library = libraryResource.getContents().get(0);

		library.eSet(librarySection, null);
		libraryResource.save(null);

		assertThat(countRows("Section")).as("the dropped subtree's own row is gone").isZero();
		assertThat(countRows("Archive"))
				.as("the transitively owned grandchild must be deleted with the subtree")
				.isZero();
		assertThat(countRows("Library")).as("the root itself stays").isEqualTo(1);
	}

	/**
	 * Deleting the root has to take the whole owned tree with it, across the resource
	 * boundary — the same ownership rule triggered through {@code delete} rather than an
	 * update.
	 */
	@Test
	void deletingTheRootDeletesTheWholeOwnedTree() throws Exception {
		saveNestedFixture();
		emf.getCache().evictAll();

		// Load first, then delete — the flow AbstractPersistenceTCK.deleteRemovesPersistedObjects
		// uses too. delete() runs em.merge on the contents, which requires managed entities;
		// handing it the hand-built EObjects of the write ResourceSet fails with a bare
		// IllegalArgumentException, where doSave copes via toManagedEntity. Asymmetry worth
		// its own issue, out of scope here.
		ResourceSet readSet = resourceSet();
		Resource libraryResource = readSet.getResource(uriFor("Library"), true);
		assertThat(libraryResource.getContents()).hasSize(1);

		((PersistenceResource) libraryResource).delete(null);

		assertThat(countRows("Library")).isZero();
		assertThat(countRows("Section")).as("owned child row gone").isZero();
		assertThat(countRows("Archive"))
				.as("the owned grandchild row must go with its root")
				.isZero();
	}

}
