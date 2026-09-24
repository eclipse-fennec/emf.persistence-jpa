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
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * A write on a resource demand-loaded to resolve one fragment works on what the resource holds
 * (issue #307). {@code ResourceSet.getEObject(uri, true)} demand-loads the resource, and the
 * lazy population used to turn the following {@code delete} into a delete of the whole table.
 *
 * @author Mark Hoffmann
 * @since 24.09.2026
 */
class JpaFragmentWriteTest {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "fragwrite";

	private EPackage ePackage;
	private EClass tagClass;
	private EAttribute idAttr;
	private EAttribute nameAttr;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() throws Exception {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		tagClass = ecore.createEClass();
		tagClass.setName("FragTag");
		idAttr = ecore.createEAttribute();
		idAttr.setName("id");
		idAttr.setEType(EcorePackage.Literals.ELONG);
		idAttr.setID(true);
		tagClass.getEStructuralFeatures().add(idAttr);
		nameAttr = ecore.createEAttribute();
		nameAttr.setName("name");
		nameAttr.setEType(EcorePackage.Literals.ESTRING);
		tagClass.getEStructuralFeatures().add(nameAttr);

		ePackage = ecore.createEPackage();
		ePackage.setName("fragwrite");
		ePackage.setNsURI("urn:fragwrite:test/1.0");
		ePackage.setNsPrefix("fragwrite");
		ePackage.getEClassifiers().add(tagClass);

		emf = JpaTckSupport.bootstrap(PU_NAME, List.<EClassifier>of(tagClass));
		clear();
		Resource resource = resourceSet().createResource(tagsUri());
		for (long id = 1; id <= 3; id++) {
			EObject tag = EcoreUtil.create(tagClass);
			tag.eSet(idAttr, id);
			tag.eSet(nameAttr, "tag-" + id);
			resource.getContents().add(tag);
		}
		resource.save(null);
	}

	@AfterEach
	void tearDown() throws Exception {
		if (nonNull(emf)) {
			clear();
			emf.close();
			emf = null;
		}
	}

	@Test
	void deletingAFragmentResolvedObjectDeletesOnlyThatRow() throws Exception {
		EObject tag = resourceSet().getEObject(URI.createURI(tagsUri() + "#2"), true);

		((PersistenceResource) tag.eResource()).delete(null);

		assertThat(storedIds()).containsExactlyInAnyOrder(1L, 3L);
	}

	@Test
	void loadThenDeleteDeletesEveryRow() throws Exception {
		Resource resource = resourceSet().createResource(tagsUri());
		resource.load(null);

		((PersistenceResource) resource).delete(null);

		assertThat(storedIds()).isEmpty();
	}

	@Test
	void aSubsetTakenThroughTheContentsIsDeleted() throws Exception {
		Resource resource = resourceSet().createResource(tagsUri());
		resource.load(null);
		resource.getContents().removeIf(tag -> Long.valueOf(2L).equals(tag.eGet(idAttr)));

		((PersistenceResource) resource).delete(null);

		assertThat(storedIds()).containsExactly(2L);
	}

	@Test
	void savingAFragmentResolvedObjectWritesOnlyThatObject() throws Exception {
		EObject tag = resourceSet().getEObject(URI.createURI(tagsUri() + "#2"), true);
		tag.eSet(nameAttr, "renamed");

		tag.eResource().save(null);

		assertThat(storedIds()).containsExactlyInAnyOrder(1L, 2L, 3L);
		assertThat(resourceSet().getEObject(URI.createURI(tagsUri() + "#2"), true).eGet(nameAttr))
				.isEqualTo("renamed");
	}

	private List<Long> storedIds() throws Exception {
		emf.getCache().evictAll();
		Resource resource = resourceSet().createResource(tagsUri());
		resource.load(null);
		return resource.getContents().stream().map(tag -> (Long) tag.eGet(idAttr)).toList();
	}

	private void clear() throws Exception {
		Resource resource = resourceSet().createResource(tagsUri());
		resource.load(null);
		((PersistenceResource) resource).delete(null);
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI tagsUri() {
		return URI.createURI("jpa://" + PU_NAME + "/FragTag");
	}
}
