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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.diagnostic.PersistenceDiagnostic;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.fennec.persistence.repository.spi.AbstractRepository;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.JpaHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Delete with the EclipseLink shared cache off (issue #369). A consumer that keeps rows apart
 * per principal (PostgreSQL row level security) has to turn the shared cache off, or it would
 * serve one principal's rows to another. There the object a repository hands out is detached
 * and lives in no cache at all — and {@code delete} merged it: the existence check asked only
 * the cache, found nothing, registered the object as new, and the remove of a new object just
 * unregistered it. No {@code DELETE}, no error.
 * <p>
 * Every assertion reads the store through {@link JpaStoreProbe}, past the ORM.
 *
 * @author Mark Hoffmann
 * @since 12.10.2026
 */
class JpaNoSharedCacheDeleteTest {

	static {
		// Same doctrine as AbstractPersistenceTCK (issue #79): H2 caches the JVM zone
		// statically at first use, and this class does not extend the abstract TCK.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "nocache";

	private EPackage notePackage;
	private EClass noteClass;
	private EClass remarkClass;
	private EReference noteRemarks;

	private EntityManagerFactory emf;
	private JpaStoreProbe probe;

	@BeforeEach
	void setUp() {
		buildModel();
		List<EClassifier> eClasses = new ArrayList<>();
		eClasses.add(noteClass);
		eClasses.add(remarkClass);
		Map<String, Object> jdbcProperties = new HashMap<>(JpaTestSupport.jdbcProperties(PU_NAME));
		jdbcProperties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
		emf = JpaTckSupport.bootstrap(PU_NAME, new EntityMapper().createMappings(eClasses), jdbcProperties,
				"create-or-extend-tables");
		probe = new JpaStoreProbe(JpaHelper.getServerSession(emf), jdbcProperties);
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
		remarkClass = ecore.createEClass();
		remarkClass.setName("Remark");
		addAttribute(remarkClass, "rid", true);
		addAttribute(remarkClass, "body", false);

		noteClass = ecore.createEClass();
		noteClass.setName("Note");
		addAttribute(noteClass, "nid", true);
		addAttribute(noteClass, "text", false);
		noteRemarks = ecore.createEReference();
		noteRemarks.setName("remarks");
		noteRemarks.setEType(remarkClass);
		noteRemarks.setContainment(true);
		noteRemarks.setUpperBound(-1);
		noteClass.getEStructuralFeatures().add(noteRemarks);

		notePackage = ecore.createEPackage();
		notePackage.setName("nocache");
		notePackage.setNsURI("urn:nocache:jpa/1.0");
		notePackage.setNsPrefix("nocache");
		notePackage.getEClassifiers().add(noteClass);
		notePackage.getEClassifiers().add(remarkClass);
	}

	private static void addAttribute(EClass eClass, String name, boolean id) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		attribute.setID(id);
		eClass.getEStructuralFeatures().add(attribute);
	}

	// ------------------------------------------------------------------ the shape

	/** The unit really runs without a shared cache — otherwise every case below is moot. */
	@Test
	void theUnitRunsWithoutASharedCache() throws Exception {
		saveNote("n1", "r1");

		assertThat(emf.getCache().contains(JpaHelper.getServerSession(emf)
				.getDescriptorForAlias("Note").getJavaClass(), "n1"))
				.as("nothing may be in the shared cache after the save")
				.isFalse();
		assertThat(probe.ids(noteClass)).containsExactly("n1");
		assertThat(probe.ids(remarkClass)).containsExactly("r1");
	}

	// ------------------------------------------------------------------ delete

	/** The consumer's flow: read through the repository, delete through the repository. */
	@Test
	void repositoryDeleteRemovesTheRowAndItsContainedChildren() throws Exception {
		saveNote("n1", "r1", "r2");
		saveNote("n2");
		AbstractRepository repository = repository();

		EObject note = repository.getEObject(noteClass, "n1");
		assertThat(note).isNotNull();
		repository.delete(note);

		assertThat(probe.ids(noteClass)).as("the deleted row is gone, the other stays").containsExactly("n2");
		assertThat(probe.ids(remarkClass)).as("contained children go with their owner").isEmpty();
	}

	/** Same through the URI overload, which loads and deletes in one call. */
	@Test
	void repositoryDeleteByUriRemovesTheRow() throws Exception {
		saveNote("n1");

		repository().delete(uriFor("Note").appendFragment("n1"));

		assertThat(probe.ids(noteClass)).isEmpty();
	}

	/** Resource level, with an object loaded by a resource of its own. */
	@Test
	void resourceDeleteOfALoadedObjectRemovesTheRow() throws Exception {
		saveNote("n1", "r1");

		Resource resource = resourceSet().getResource(uriFor("Note"), true);
		assertThat(resource.getContents()).hasSize(1);
		((PersistenceResource) resource).delete(null);

		assertThat(probe.ids(noteClass)).isEmpty();
		assertThat(probe.ids(remarkClass)).isEmpty();
		assertThat(resource.getContents()).isEmpty();
	}

	/**
	 * A hand-built object that only carries the id of a stored row — never loaded, so never
	 * managed. The delete identifies the row by its key, not by the instance.
	 */
	@Test
	void resourceDeleteOfAHandBuiltObjectRemovesTheRow() throws Exception {
		saveNote("n1", "r1");

		Resource resource = resourceSet().createResource(uriFor("Note"));
		resource.getContents().add(note("n1"));
		((PersistenceResource) resource).delete(null);

		assertThat(probe.ids(noteClass)).isEmpty();
		assertThat(probe.ids(remarkClass)).isEmpty();
	}

	/**
	 * An object without a row is nothing to delete — the same as on Mongo, where the delete
	 * is idempotent — but it is no longer swallowed: the resource says which one it was.
	 */
	@Test
	void deletingAnObjectWithoutARowWarnsAndDeletesTheOthers() throws Exception {
		saveNote("n1");

		Resource resource = resourceSet().createResource(uriFor("Note"));
		resource.getContents().add(note("n1"));
		resource.getContents().add(note("missing"));
		((PersistenceResource) resource).delete(null);

		assertThat(probe.ids(noteClass)).isEmpty();
		assertThat(resource.getWarnings())
				.singleElement()
				.satisfies(warning -> {
					assertThat(warning.getMessage()).contains("Note", "missing");
					assertThat(((PersistenceDiagnostic) warning).code()).isEqualTo(PersistenceDiagnostic.CODE_NOT_FOUND);
				});
		assertThat(resource.getContents()).isEmpty();
	}

	// ------------------------------------------------------------------ merge

	/**
	 * The plain JPA contract the descriptors now follow: merging a detached object checks the
	 * database, so it is updated, not inserted a second time — and removing the merged
	 * instance removes the row.
	 */
	@Test
	void mergeOfADetachedObjectChecksTheDatabase() throws Exception {
		saveNote("n1");
		Class<?> noteType = JpaHelper.getServerSession(emf).getDescriptorForAlias("Note").getJavaClass();
		EObject detached;
		try (EntityManager em = emf.createEntityManager()) {
			detached = (EObject) em.find(noteType, "n1");
		}
		detached.eSet(noteClass.getEStructuralFeature("text"), "changed");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			EObject merged = (EObject) em.merge(detached);
			em.getTransaction().commit();
			assertThat(merged.eGet(noteClass.getEStructuralFeature("text"))).isEqualTo("changed");
		}
		try (EntityManager em = emf.createEntityManager()) {
			assertThat(((EObject) em.find(noteType, "n1")).eGet(noteClass.getEStructuralFeature("text")))
					.as("the merge updated the row")
					.isEqualTo("changed");
			em.getTransaction().begin();
			em.remove(em.merge(detached));
			em.getTransaction().commit();
		}

		assertThat(probe.ids(noteClass)).as("remove of a merged detached object deletes the row").isEmpty();
	}

	// ------------------------------------------------------------------ helpers

	private AbstractRepository repository() {
		return new AbstractRepository("nocache", uriFor("Note").trimSegments(1), this::resourceSet, null, null,
				null) {
			// all behaviour is generic
		};
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(notePackage.getNsURI(), notePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}

	private EObject note(String id) {
		EObject note = EcoreUtil.create(noteClass);
		note.eSet(noteClass.getEStructuralFeature("nid"), id);
		note.eSet(noteClass.getEStructuralFeature("text"), "text of " + id);
		return note;
	}

	@SuppressWarnings("unchecked")
	private void saveNote(String id, String... remarkIds) throws Exception {
		EObject note = note(id);
		for (String remarkId : remarkIds) {
			EObject remark = EcoreUtil.create(remarkClass);
			remark.eSet(remarkClass.getEStructuralFeature("rid"), remarkId);
			remark.eSet(remarkClass.getEStructuralFeature("body"), "body of " + remarkId);
			((List<EObject>) note.eGet(noteRemarks)).add(remark);
		}
		Resource resource = resourceSet().createResource(uriFor("Note"));
		resource.getContents().add(note);
		resource.save(null);
	}
}
