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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * Isolation test for the second failure {@code JpaEMapRoundTripTest} produced: an EClass with
 * <em>no</em> id attribute at all. {@code CompositeIdAnalyzer} answers that case with a synthetic
 * id ({@code pk_<name>}), so the question is whether that synthetic key survives into the
 * EclipseLink descriptor — independently of anything map-related.
 * <p>
 * A map entry class can never carry an id feature (the model belongs to the user, and EMF puts
 * only {@code key}/{@code value} in it), so if this case is broken the map case cannot be fixed
 * without fixing it first.
 * <p>
 * Measured: it does not. The unit fails to bootstrap with {@code There should be one
 * non-read-only mapping defined for the primary key field [NOTE.PK_NOTE]} — so the documented
 * synthetic-id fallback takes the whole persistence unit down, for any model that has one
 * id-less class in it.
 *
 * @author Mark Hoffmann
 * @since 19.08.2026
 */
@Disabled("#184 — the synthetic key has no mapping, so the persistence unit cannot boot")
class JpaSyntheticIdRoundTripTest {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "synthid";

	private EPackage ePackage;
	private EClass noteClass;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		noteClass = ecore.createEClass();
		noteClass.setName("Note");
		EAttribute text = ecore.createEAttribute();
		text.setName("text");
		text.setEType(EcorePackage.Literals.ESTRING);
		noteClass.getEStructuralFeatures().add(text);

		ePackage = ecore.createEPackage();
		ePackage.setName("synthid");
		ePackage.setNsURI("urn:synthid:test/1.0");
		ePackage.setNsPrefix("synthid");
		ePackage.getEClassifiers().add(noteClass);

		emf = JpaTckSupport.bootstrap(PU_NAME, List.<EClassifier>of(noteClass));
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
			emf = null;
		}
	}

	@Test
	void classWithoutIdAttributeRoundTrips() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject note = EcoreUtil.create(noteClass);
		note.eSet(noteClass.getEStructuralFeature("text"), "written down");
		Resource resource = writeSet.createResource(uriFor("Note"));
		resource.getContents().add(note);
		resource.save(null);

		emf.getCache().evictAll();
		Resource reloaded = resourceSet().createResource(uriFor("Note"));
		reloaded.load(null);
		assertThat(reloaded.getContents()).hasSize(1);
		assertThat(reloaded.getContents().get(0).eGet(noteClass.getEStructuralFeature("text")))
				.isEqualTo("written down");
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}
}
