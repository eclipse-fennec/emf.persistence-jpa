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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * The eorm column facets of a basic attribute on every flavor (issue #312): {@code Lob} carries
 * values far beyond 255 characters or bytes, {@code length} and {@code columnDefinition} reach
 * the column. PostgreSQL is the flavor the issue was found on — without the facets every String
 * there is {@code VARCHAR(255)} — and MariaDB is the one where the case-sensitive collation
 * definition must not swallow them.
 *
 * @author Mark Hoffmann
 * @since 24.09.2026
 */
class JpaColumnFacetsRoundTripTest {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "facets";

	private EPackage ePackage;
	private EClass noteClass;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() {
		noteClass = EcoreFactory.eINSTANCE.createEClass();
		noteClass.setName("FacetNote");
		addAttribute("id", EcorePackage.Literals.ESTRING).setID(true);
		addAttribute("code", EcorePackage.Literals.ESTRING);
		addAttribute("summary", EcorePackage.Literals.ESTRING);
		addAttribute("body", EcorePackage.Literals.ESTRING);
		addAttribute("payload", EcorePackage.Literals.EBYTE_ARRAY);

		ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("facets");
		ePackage.setNsURI("urn:facets:test/1.0");
		ePackage.setNsPrefix("facets");
		ePackage.getEClassifiers().add(noteClass);

		EntityMappings mappings = new EntityMapper().createMappings(new ArrayList<EClassifier>(List.of(noteClass)));
		basic(mappings, "code").getColumn().setLength(10);
		basic(mappings, "summary").getColumn().setColumnDefinition("VARCHAR(2000)");
		basic(mappings, "body").setLob(EORMFactory.eINSTANCE.createLob());
		basic(mappings, "payload").setLob(EORMFactory.eINSTANCE.createLob());
		emf = JpaTckSupport.bootstrap(PU_NAME, mappings, JpaTestSupport.jdbcProperties(PU_NAME),
				"create-or-extend-tables");
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
			emf = null;
		}
	}

	@Test
	void lobStringRoundTripsBeyond255Characters() throws Exception {
		String text = "GeoJSON ".repeat(10_000);
		save(note("n1", n -> n.eSet(feature("body"), text)));

		assertThat(reload("n1").eGet(feature("body"))).isEqualTo(text);
	}

	@Test
	void lobBytesRoundTrip() throws Exception {
		byte[] bytes = new byte[50_000];
		Arrays.fill(bytes, (byte) 42);
		save(note("n2", n -> n.eSet(feature("payload"), bytes)));

		assertThat((byte[]) reload("n2").eGet(feature("payload"))).isEqualTo(bytes);
	}

	@Test
	void columnDefinitionCarriesItsLength() throws Exception {
		String text = "s".repeat(1_500);
		save(note("n3", n -> n.eSet(feature("summary"), text)));

		assertThat(reload("n3").eGet(feature("summary"))).isEqualTo(text);
	}

	@Test
	void lengthIsTheColumnLength() throws Exception {
		save(note("n4", n -> n.eSet(feature("code"), "1234567890")));
		assertThat(reload("n4").eGet(feature("code"))).isEqualTo("1234567890");

		EObject tooLong = note("n5", n -> n.eSet(feature("code"), "12345678901"));
		assertThatThrownBy(() -> save(tooLong)).isNotNull();
	}

	// ── Helpers ─────────────────────────────────────────────────────────────

	private interface Filler {
		void fill(EObject note);
	}

	private EAttribute addAttribute(String name, EDataType type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		noteClass.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private EAttribute feature(String name) {
		return (EAttribute) noteClass.getEStructuralFeature(name);
	}

	private Basic basic(EntityMappings mappings, String name) {
		return mappings.getEntity().get(0).getAttributes().getBasic().stream()
				.filter(b -> name.equals(b.getName()))
				.findFirst()
				.orElseThrow();
	}

	private EObject note(String id, Filler filler) {
		EObject note = EcoreUtil.create(noteClass);
		note.eSet(feature("id"), id);
		filler.fill(note);
		return note;
	}

	private void save(EObject note) throws Exception {
		Resource resource = resourceSet().createResource(uriFor());
		resource.getContents().add(note);
		resource.save(null);
	}

	private EObject reload(String id) throws Exception {
		emf.getCache().evictAll();
		Resource resource = resourceSet().createResource(uriFor());
		resource.load(null);
		return resource.getContents().stream()
				.filter(n -> id.equals(n.eGet(feature("id"))))
				.findFirst()
				.orElseThrow(() -> new AssertionError("No note " + id + " was stored"));
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor() {
		return URI.createURI("jpa://" + PU_NAME + "/FacetNote");
	}
}
