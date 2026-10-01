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

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Supplier;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.converter.ContainedObjectConverter;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.geojson.GeoJsonPackage;
import org.geojson.Geometry;
import org.geojson.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import jakarta.persistence.EntityManagerFactory;

/**
 * A containment child whose class is not part of the unit — a GeoJSON geometry under a
 * {@code Shape} of another package — round-trips on JPA (issue #363).
 * <p>
 * The unit is derived from the shapes package alone, exactly as the repository facade derives it;
 * the GeoJSON package is never admitted. The geometry used to be skipped while the unit was built
 * ("target EClass 'Geometry' is not part of this persistence unit"), the table got no column for
 * it, and the read gave {@code null}. It is now one encoded column of its parent.
 * <p>
 * Every read goes through a fresh resource set with the shared cache evicted — reading through
 * the writing set returns its own objects and hides any loss.
 *
 * @author Juergen Albert
 * @since 01.10.2026
 */
class JpaForeignContainmentRoundTripTest {

	static {
		// H2 caches the JVM zone statically at first use (issue #79); this class does not
		// extend the abstract TCK, so it repeats the doctrine.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "shapes";

	private ShapesTestModel model;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() {
		model = new ShapesTestModel();
		emf = JpaTckSupport.bootstrap(PU_NAME, model.classifiers());
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
			emf = null;
		}
	}

	/**
	 * The derivation maps the child as a column of its parent: a converted Lob, named for the
	 * cardinality, and no entity for any GeoJSON class.
	 */
	@Test
	void theChildIsAnEncodedColumnOfItsParent() {
		EntityMappings mappings = new EntityMapper().createMappings(model.classifiers());

		assertThat(mappings.getEntity()).extracting(Entity::getClass_).containsExactly(model.shapeClass);
		List<Basic> basics = mappings.getEntity().get(0).getAttributes().getBasic();
		Basic geometry = basic(basics, "geometry");
		assertThat(geometry.getLob()).isNotNull();
		assertThat(geometry.getConvert().getConverter()).isEqualTo(ContainedObjectConverter.NAME);
		Basic outlines = basic(basics, "outlines");
		assertThat(outlines.getLob()).isNotNull();
		assertThat(outlines.getConvert().getConverter()).isEqualTo(ContainedObjectConverter.NAME_MANY);
		assertThat(mappings.getEntity().get(0).getAttributes().getOneToOne()).isEmpty();
		assertThat(mappings.getEntity().get(0).getAttributes().getOneToMany()).isEmpty();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("org.eclipse.fennec.persistence.tck.ShapesTestModel#geometries")
	void everyGeometryTypeRoundTrips(String shapeId, Supplier<Geometry> factory) throws Exception {
		Geometry written = factory.get();
		save(model.newShape(shapeId, written));

		EObject loaded = reload(shapeId);
		assertThat(loaded.eGet(model.name)).isEqualTo(shapeId + " shape");
		ShapesTestModel.assertSameGeometry((Geometry) loaded.eGet(model.geometry), written);
		assertThat(((EObject) loaded.eGet(model.geometry)).eContainer())
				.as("the child is contained by its parent again").isSameAs(loaded);
	}

	@Test
	void aManyValuedChildListRoundTrips() throws Exception {
		EObject shape = model.newShape("outlined", ShapesTestModel.point());
		@SuppressWarnings("unchecked")
		List<Geometry> outlines = (List<Geometry>) shape.eGet(model.outlines);
		outlines.add(ShapesTestModel.polygon());
		outlines.add(ShapesTestModel.lineString());
		save(shape);

		EObject loaded = reload("outlined");
		@SuppressWarnings("unchecked")
		List<Geometry> reloaded = (List<Geometry>) loaded.eGet(model.outlines);
		assertThat(reloaded).hasSize(2);
		ShapesTestModel.assertSameGeometry(reloaded.get(0), ShapesTestModel.polygon());
		ShapesTestModel.assertSameGeometry(reloaded.get(1), ShapesTestModel.lineString());
	}

	@Test
	void anEmptyChildListAndAMissingChildReadBackEmpty() throws Exception {
		save(model.newShape("bare", null));

		EObject loaded = reload("bare");
		assertThat(loaded.eGet(model.geometry)).isNull();
		assertThat((List<?>) loaded.eGet(model.outlines)).isEmpty();
	}

	/**
	 * Saving an existing row again takes the new child: the upsert must replace the stored value,
	 * not keep the old one because the child has no id to be matched by.
	 */
	@Test
	void aReplacedChildIsUpdated() throws Exception {
		save(model.newShape("moved", ShapesTestModel.point()));

		ResourceSet updateSet = resourceSet();
		Resource resource = updateSet.createResource(uriFor());
		resource.load(null);
		EObject shape = find(resource, "moved");
		Point moved = ShapesTestModel.point();
		moved.getCoordinates().setLongitude(-122.4);
		shape.eSet(model.geometry, moved);
		resource.save(null);

		ShapesTestModel.assertSameGeometry((Geometry) reload("moved").eGet(model.geometry), moved);
	}

	/**
	 * A child changed in place — no new object, only new coordinates — is written as well: the
	 * stored value is compared, not the child's identity.
	 */
	@Test
	void aChildChangedInPlaceIsUpdated() throws Exception {
		save(model.newShape("nudged", ShapesTestModel.point()));

		ResourceSet updateSet = resourceSet();
		Resource resource = updateSet.createResource(uriFor());
		resource.load(null);
		EObject shape = find(resource, "nudged");
		Point point = (Point) shape.eGet(model.geometry);
		point.getCoordinates().setLatitude(-33.9);
		resource.save(null);

		Point expected = ShapesTestModel.point();
		expected.getCoordinates().setLatitude(-33.9);
		ShapesTestModel.assertSameGeometry((Geometry) reload("nudged").eGet(model.geometry), expected);
	}

	/** The column itself is queryable for presence: a shape without geometry is told apart. */
	@Test
	void theChildCanBeTestedForNull() throws Exception {
		save(model.newShape("with", ShapesTestModel.point()));
		save(model.newShape("without", null));

		emf.getCache().evictAll();
		Query query = QueryBuilder.from(model.shapeClass)
				.where(Expressions.path(model.geometry).isNull())
				.build();
		QueryableResource queryable = (QueryableResource) resourceSet().createResource(uriFor());
		try (QueryResult result = queryable.query(query)) {
			assertThat(result.objects().map(shape -> shape.eGet(model.id))).containsExactly("without");
		}
	}

	/**
	 * The child is an atomic value: a predicate into it has no column to address. It must fail
	 * loudly — never answer from the parent's rows as if it had matched or not.
	 */
	@Test
	void aQueryIntoTheChildFails() throws Exception {
		save(model.newShape("point", ShapesTestModel.point()));

		Query query = QueryBuilder.from(model.shapeClass)
				.where(Expressions.path(model.geometry, GeoJsonPackage.Literals.GEO_JSON_OBJECT__BBOX).isNull())
				.build();
		QueryableResource queryable = (QueryableResource) resourceSet().createResource(uriFor());
		assertThatThrownBy(() -> {
			try (QueryResult result = queryable.query(query)) {
				result.objects().toList();
			}
		}).isInstanceOf(IOException.class).hasMessageContaining("Shape");
	}

	// ------------------------------------------------------------------ helpers

	private static Basic basic(List<Basic> basics, String name) {
		return basics.stream()
				.filter(basic -> name.equals(basic.getName()))
				.findFirst()
				.orElseThrow(() -> new AssertionError("no basic mapping for '" + name + "' in " + basics));
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ShapesTestModel.NS_URI, model.ePackage);
		resourceSet.getPackageRegistry().put(GeoJsonPackage.eNS_URI, GeoJsonPackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor() {
		return URI.createURI("jpa://" + PU_NAME + "/Shape");
	}

	private void save(EObject shape) throws Exception {
		Resource resource = resourceSet().createResource(uriFor());
		resource.getContents().add(shape);
		resource.save(null);
	}

	/** Reads through a fresh ResourceSet with the shared cache evicted — no in-memory answers. */
	private EObject reload(String id) throws Exception {
		emf.getCache().evictAll();
		Resource resource = resourceSet().createResource(uriFor());
		resource.load(null);
		return find(resource, id);
	}

	private EObject find(Resource resource, String id) {
		return resource.getContents().stream()
				.filter(object -> id.equals(object.eGet(model.id)))
				.findFirst()
				.orElseThrow(() -> new AssertionError("shape '" + id + "' was not loaded back"));
	}
}
