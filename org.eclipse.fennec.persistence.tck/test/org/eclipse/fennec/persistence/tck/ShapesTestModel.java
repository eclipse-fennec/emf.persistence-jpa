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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.geojson.Coordinates;
import org.geojson.GeoJsonFactory;
import org.geojson.GeoJsonPackage;
import org.geojson.Geometry;
import org.geojson.GeometryCollection;
import org.geojson.Hole;
import org.geojson.LineString;
import org.geojson.MultiLineString;
import org.geojson.MultiPoint;
import org.geojson.MultiPolygon;
import org.geojson.Point;
import org.geojson.Polygon;
import org.geojson.Ring;
import org.geojson.SimpleLineString;
import org.geojson.SimplePolygon;

/**
 * The model of issue #363: a dynamic package whose {@code Shape} holds a geometry of the GeoJSON
 * model ({@code org.geojson.model}) as a containment child — a class from another package, with
 * no id, whose state the model keeps in the volatile {@code data} attribute beside the transient
 * object view ({@code coordinates}, {@code geometries}).
 * <p>
 * One shape per geometry type, built through the object view the way the GeoJSON codec builds
 * them, and an assertion that compares through the object view as well — what a caller sees.
 *
 * @author Juergen Albert
 * @since 01.10.2026
 */
final class ShapesTestModel {

	static final String NS_URI = "urn:shapes:test/1.0";

	private static final GeoJsonFactory GEO = GeoJsonFactory.eINSTANCE;

	final EPackage ePackage;
	final EClass shapeClass;
	final EAttribute id;
	final EAttribute name;
	/** single-valued containment into the GeoJSON package */
	final EReference geometry;
	/** many-valued containment into the GeoJSON package */
	final EReference outlines;

	ShapesTestModel() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		ePackage = ecore.createEPackage();
		ePackage.setName("shapes");
		ePackage.setNsPrefix("shapes");
		ePackage.setNsURI(NS_URI);

		shapeClass = ecore.createEClass();
		shapeClass.setName("Shape");
		id = ecore.createEAttribute();
		id.setName("id");
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		name = ecore.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		geometry = ecore.createEReference();
		geometry.setName("geometry");
		geometry.setEType(GeoJsonPackage.Literals.GEOMETRY);
		geometry.setContainment(true);
		outlines = ecore.createEReference();
		outlines.setName("outlines");
		outlines.setEType(GeoJsonPackage.Literals.GEOMETRY);
		outlines.setContainment(true);
		outlines.setUpperBound(-1);
		shapeClass.getEStructuralFeatures().addAll(List.of(id, name, geometry, outlines));
		ePackage.getEClassifiers().add(shapeClass);
	}

	/** The classifiers a unit is derived from: the shapes package only, as in the issue. */
	List<EClassifier> classifiers() {
		return new ArrayList<>(ePackage.getEClassifiers());
	}

	EObject newShape(String shapeId, Geometry shapeGeometry) {
		EObject shape = EcoreUtil.create(shapeClass);
		shape.eSet(id, shapeId);
		shape.eSet(name, shapeId + " shape");
		shape.eSet(geometry, shapeGeometry);
		return shape;
	}

	/** One factory per GeoJSON geometry type, keyed by the shape id the tests use. */
	static List<Object[]> geometries() {
		return List.of(
				new Object[] { "point", (Supplier<Geometry>) ShapesTestModel::point },
				new Object[] { "multipoint", (Supplier<Geometry>) ShapesTestModel::multiPoint },
				new Object[] { "linestring", (Supplier<Geometry>) ShapesTestModel::lineString },
				new Object[] { "multilinestring", (Supplier<Geometry>) ShapesTestModel::multiLineString },
				new Object[] { "polygon", (Supplier<Geometry>) ShapesTestModel::polygon },
				new Object[] { "multipolygon", (Supplier<Geometry>) ShapesTestModel::multiPolygon },
				new Object[] { "geometrycollection", (Supplier<Geometry>) ShapesTestModel::geometryCollection });
	}

	static Point point() {
		Point point = GEO.createPoint();
		point.setCoordinates(coordinates(7.5, 50.9));
		return point;
	}

	static MultiPoint multiPoint() {
		MultiPoint multiPoint = GEO.createMultiPoint();
		multiPoint.getCoordinates().addAll(List.of(coordinates(1, 2), coordinates(3, 4)));
		return multiPoint;
	}

	static LineString lineString() {
		LineString lineString = GEO.createLineString();
		lineString.getCoordinates().addAll(List.of(coordinates(0, 0), coordinates(1, 1), coordinates(2, 0)));
		return lineString;
	}

	static MultiLineString multiLineString() {
		MultiLineString multiLineString = GEO.createMultiLineString();
		SimpleLineString first = GEO.createSimpleLineString();
		first.getCoordinates().addAll(List.of(coordinates(0, 0), coordinates(1, 1)));
		SimpleLineString second = GEO.createSimpleLineString();
		second.getCoordinates().addAll(List.of(coordinates(5, 5), coordinates(6, 6)));
		multiLineString.getLinesStrings().addAll(List.of(first, second));
		return multiLineString;
	}

	static Polygon polygon() {
		Polygon polygon = GEO.createPolygon();
		polygon.setExteriorRing(ring(0, 0, 10, 10));
		Hole hole = GEO.createHole();
		hole.getCoordinates().addAll(square(2, 2, 4, 4));
		polygon.getInteriorHoles().add(hole);
		return polygon;
	}

	static MultiPolygon multiPolygon() {
		MultiPolygon multiPolygon = GEO.createMultiPolygon();
		SimplePolygon first = GEO.createSimplePolygon();
		first.setExteriorRing(ring(0, 0, 1, 1));
		SimplePolygon second = GEO.createSimplePolygon();
		second.setExteriorRing(ring(5, 5, 6, 6));
		multiPolygon.getPolygons().addAll(List.of(first, second));
		return multiPolygon;
	}

	static GeometryCollection geometryCollection() {
		GeometryCollection collection = GEO.createGeometryCollection();
		collection.getGeometries().addAll(List.of(point(), lineString()));
		return collection;
	}

	/**
	 * Asserts that the read geometry is the written one: same type, same coordinates — compared
	 * through the persisted form ({@code data}), which is derived from the object view, and
	 * recursively for the members of a collection.
	 */
	static void assertSameGeometry(Geometry actual, Geometry expected) {
		assertThat(actual).as("the geometry must come back").isNotNull();
		assertThat(actual.eClass()).isSameAs(expected.eClass());
		if (expected instanceof GeometryCollection expectedCollection) {
			List<Geometry> actualMembers = ((GeometryCollection) actual).getGeometries();
			assertThat(actualMembers).hasSameSizeAs(expectedCollection.getGeometries());
			for (int index = 0; index < actualMembers.size(); index++) {
				assertSameGeometry(actualMembers.get(index), expectedCollection.getGeometries().get(index));
			}
			return;
		}
		Object actualData = data(actual);
		Object expectedData = data(expected);
		assertThat(actualData).as("coordinates of the %s", expected.eClass().getName()).isNotNull();
		assertThat(actualData).as("coordinates of the %s", expected.eClass().getName())
				.usingRecursiveComparison().isEqualTo(expectedData);
	}

	/** The persisted form of a geometry — the volatile {@code data} attribute. */
	static Object data(Geometry geometry) {
		return geometry.eGet(geometry.eClass().getEStructuralFeature("data"));
	}

	private static Ring ring(double x1, double y1, double x2, double y2) {
		Ring ring = GEO.createRing();
		ring.getCoordinates().addAll(square(x1, y1, x2, y2));
		return ring;
	}

	private static List<Coordinates> square(double x1, double y1, double x2, double y2) {
		return List.of(coordinates(x1, y1), coordinates(x2, y1), coordinates(x2, y2), coordinates(x1, y2),
				coordinates(x1, y1));
	}

	private static Coordinates coordinates(double longitude, double latitude) {
		Coordinates coordinates = GEO.createCoordinates();
		coordinates.setLongitude(longitude);
		coordinates.setLatitude(latitude);
		return coordinates;
	}
}
