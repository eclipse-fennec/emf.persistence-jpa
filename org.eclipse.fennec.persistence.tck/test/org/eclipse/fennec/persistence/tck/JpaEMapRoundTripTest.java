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

import java.util.TimeZone;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * What is JPA-specific about a map (contract §9.2) — the round trip itself is core and lives in
 * {@code AbstractPersistenceTCK}, where every flavor runs it.
 * <p>
 * Two things only this backend can be asked: that an {@code EObject} value survives the entry
 * table, and that map semantics is a <em>schema</em> constraint — a second row for the same
 * {@code (owner, key)} is rejected by the database, not merely absent because the EMap in memory
 * replaced it (issue #185).
 *
 * @author Mark Hoffmann
 * @since 19.08.2026
 */
class JpaEMapRoundTripTest {

	static {
		// H2 caches the JVM zone statically at first use (issue #79); this class does not
		// extend the abstract TCK, so it repeats the doctrine.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "emap";

	private EMapTestModel model;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() {
		model = new EMapTestModel();
		emf = JpaTckSupport.bootstrap(PU_NAME, model.classifiers());
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
			emf = null;
		}
	}

	@Test
	void eObjectValuedMapRoundTrips() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject catalog = model.newCatalog("c3", "Autumn");
		EMap<Object, Object> map = map(catalog, "parts");
		map.put("engine", model.newPart("p1", "V8"));
		map.put("wheel", model.newPart("p2", "Alloy"));

		Resource resource = writeSet.createResource(uriFor("Catalog"));
		resource.getContents().add(catalog);
		resource.save(null);

		EObject loaded = reload("c3");
		EMap<Object, Object> reloaded = map(loaded, "parts");
		assertThat(reloaded).as("both entries come back").hasSize(2);
		EObject engine = (EObject) reloaded.get("engine");
		assertThat(engine).isNotNull();
		assertThat(engine.eIsProxy()).as("the value must be resolved, not a proxy").isFalse();
		assertThat(engine.eGet(model.partClass.getEStructuralFeature("label"))).isEqualTo("V8");
	}

	/**
	 * Map semantics as a schema constraint, not only as an in-memory promise (issue #185). The
	 * entry table is an ordinary containment table, so without a unique constraint over
	 * {@code (owner, key)} it would hold a list wearing a map's interface — and the divergence
	 * would only show after a reload.
	 */
	@Test
	void theEntryTableConstrainsOneKeyPerOwner() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject catalog = model.newCatalog("c5", "Constraint");
		map(catalog, "attributes").put("color", "red");
		Resource resource = writeSet.createResource(uriFor("Catalog"));
		resource.getContents().add(catalog);
		resource.save(null);

		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			assertThatThrownBy(() -> em.createNativeQuery("INSERT INTO STRINGTOSTRINGMAPENTRY"
					+ " (PK_STRINGTOSTRINGMAPENTRY, MAP_KEY, MAP_VALUE, catalog_id)"
					+ " VALUES ('duplicate', 'color', 'blue', 'c5')").executeUpdate())
					.as("a second row for the same (owner, key) must be rejected by the schema")
					.hasMessageContaining("MAP_KEY");
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}
	}

	// ------------------------------------------------------------------ helpers

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EMapTestModel.NS_URI, model.ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}

	/** Reads through a fresh ResourceSet with the shared cache evicted — no in-memory answers. */
	private EObject reload(String id) throws Exception {
		emf.getCache().evictAll();
		Resource resource = resourceSet().createResource(uriFor("Catalog"));
		resource.load(null);
		return resource.getContents().stream()
				.filter(object -> id.equals(object.eGet(model.catalogClass.getEStructuralFeature("cid"))))
				.findFirst()
				.orElseThrow(() -> new AssertionError("catalog '" + id + "' was not loaded back"));
	}

	@SuppressWarnings("unchecked")
	private EMap<Object, Object> map(EObject owner, String feature) {
		Object value = owner.eGet(owner.eClass().getEStructuralFeature(feature));
		assertThat(value)
				.as("EMF must hand out an EMap for '%s' — otherwise the model is not a map at all",
						feature)
				.isInstanceOf(EMap.class);
		return (EMap<Object, Object>) value;
	}
}
