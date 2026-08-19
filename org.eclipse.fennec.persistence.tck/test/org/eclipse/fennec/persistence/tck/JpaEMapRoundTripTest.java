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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * Measures what the JPA backend does with an {@code EMap} today (issue #171, §9.2): three
 * maps, one round trip each, no fixes attempted. The contract froze maps as "out of contract"
 * on the assumption that neither backend has a mapping — {@code MongoEMapRoundTripTest} is the
 * same measurement on the other side, and the codec's {@code EMapHelper} says the assumption
 * does not hold there.
 * <p>
 * Nothing here is a capability probe: an EMap is a containment-many reference in Ecore, so the
 * generic mapping path takes it whether or not anybody designed for it. The question these
 * cases answer is which parts of the map's <em>semantics</em> survive that path — key
 * uniqueness, key type, and an EObject value.
 * <p>
 * The measurement: every case fails in {@code @BeforeEach}, before a single map is written,
 * on two independent defects — #183 (the entry class gets {@code java.util.Map$Entry} as its
 * entity class, an interface shared by all three entry classes) and #184 (an entry class has no
 * id attribute, and the synthetic key has no mapping). Disabled on those rather than deleted:
 * the cases are what #185 turns green.
 *
 * @author Mark Hoffmann
 * @since 19.08.2026
 */
@Disabled("#183 and #184 break the bootstrap before any map is written — enabled by #185")
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
	void stringKeyedMapRoundTrips() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject catalog = model.newCatalog("c1", "Spring");
		EMap<Object, Object> map = map(catalog, "attributes");
		map.put("color", "red");
		map.put("size", "L");

		Resource resource = writeSet.createResource(uriFor("Catalog"));
		resource.getContents().add(catalog);
		resource.save(null);

		EObject loaded = reload("c1");
		EMap<Object, Object> reloaded = map(loaded, "attributes");
		assertThat(reloaded).as("both entries come back").hasSize(2);
		assertThat(reloaded.get("color")).isEqualTo("red");
		assertThat(reloaded.get("size")).isEqualTo("L");
	}

	@Test
	void intKeyedMapRoundTrips() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject catalog = model.newCatalog("c2", "Summer");
		EMap<Object, Object> map = map(catalog, "counts");
		map.put(1, "one");
		map.put(42, "answer");

		Resource resource = writeSet.createResource(uriFor("Catalog"));
		resource.getContents().add(catalog);
		resource.save(null);

		EObject loaded = reload("c2");
		EMap<Object, Object> reloaded = map(loaded, "counts");
		assertThat(reloaded).as("both entries come back").hasSize(2);
		assertThat(reloaded.get(1)).as("an int key must stay an int key").isEqualTo("one");
		assertThat(reloaded.get(42)).isEqualTo("answer");
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
	 * Map semantics, not list semantics: putting the same key twice replaces the value. This is
	 * the one thing a containment list cannot do on its own, and it is where a missing unique
	 * constraint on {@code (owner, key)} would show up as two rows.
	 */
	@Test
	void puttingTheSameKeyTwiceReplacesTheValue() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject catalog = model.newCatalog("c4", "Winter");
		EMap<Object, Object> map = map(catalog, "attributes");
		map.put("color", "red");
		map.put("color", "blue");

		Resource resource = writeSet.createResource(uriFor("Catalog"));
		resource.getContents().add(catalog);
		resource.save(null);

		EObject loaded = reload("c4");
		EMap<Object, Object> reloaded = map(loaded, "attributes");
		assertThat(reloaded).as("one key, one entry").hasSize(1);
		assertThat(reloaded.get("color")).isEqualTo("blue");
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
