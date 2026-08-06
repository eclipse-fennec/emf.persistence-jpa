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
package org.eclipse.fennec.persistence.mongo.query;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.GeoBox;
import org.eclipse.fennec.model.expression.GeoDistance;
import org.eclipse.fennec.model.expression.GeoPointLiteral;
import org.eclipse.fennec.model.expression.GeoPolygon;
import org.eclipse.fennec.model.expression.GeoShape;
import org.eclipse.fennec.model.expression.GeoSubject;
import org.eclipse.fennec.model.expression.GeoWithin;
import org.eclipse.fennec.persistence.query.QueryException;

import com.mongodb.client.model.Filters;

/**
 * Geo vocabulary translation for the mongo backend (issue #113, G-P2).
 * <p>
 * The canonical PACKED value shape is a GeoJSON point
 * ({@code {type: "Point", coordinates: [lon, lat]}}); planar shape queries go against
 * the {@code <point>.coordinates} legacy pair so box/polygon keep the reference
 * engine's planar lon/lat semantics (§5.3), while {@code GeoDistance} thresholds use
 * {@code $centerSphere} — the same great-circle math as the haversine reference, with
 * radians over the §5.4 mean earth radius. Split subjects translate boxes to plain
 * range filters and distances to an {@code $expr} haversine.
 * <p>
 * 3VL (§5.5, issue-#97 discipline): {@code $geoWithin} and positive range comparisons
 * exclude missing/null coordinates natively; negations and {@code $expr} forms carry
 * explicit guards so an UNKNOWN subject never matches, negated or not. Distance
 * {@code LT}/{@code LE} both map to the inclusive {@code $centerSphere} — the boundary
 * of a continuum is measure-zero (G5). Refused: polygons over split subjects (no Mongo
 * form) and distance {@code EQ}/{@code NE}.
 */
final class MongoGeoPredicates {

	/** Mean earth radius (meters) — the reference sphere of decision G5. */
	private static final double EARTH_RADIUS_METERS = 6_371_008.8d;

	private MongoGeoPredicates() {
	}

	/** Translates {@code GeoWithin}; {@code negated} adds the 3VL non-null guards. */
	static Bson geoWithin(GeoWithin geoWithin, boolean negated) throws QueryException {
		GeoSubject subject = geoWithin.getSubject();
		GeoShape shape = geoWithin.getShape();
		if (subject.getPathPoint() != null) {
			String pair = MongoFieldNames.render(subject.getPathPoint()) + ".coordinates";
			Bson within = shape instanceof GeoBox box ? packedBox(pair, box)
					: packedPolygon(pair, (GeoPolygon) shape);
			return negated ? Filters.and(Filters.nor(within), pairGuard(pair)) : within;
		}
		String latField = MongoFieldNames.render(subject.getPathLat());
		String lonField = MongoFieldNames.render(subject.getPathLon());
		if (!(shape instanceof GeoBox box)) {
			throw new QueryException("GeoPolygon over a split lat/lon subject has no mongo"
					+ " form — use the packed (GeoJSON point) binding");
		}
		Bson within = splitBox(latField, lonField, box);
		return negated
				? Filters.and(Filters.nor(within),
						Filters.ne(latField, null), Filters.ne(lonField, null))
				: within;
	}

	/**
	 * Translates a comparison over a {@code GeoDistance} value (meters) with the
	 * effective operator — the issue-#97 negation rewrite passes the inverse.
	 */
	static Bson geoDistance(GeoDistance distance, ComparisonOperator operator, Object bound)
			throws QueryException {
		if (operator == ComparisonOperator.EQ || operator == ComparisonOperator.NE) {
			throw new QueryException("GeoDistance supports range comparisons only (LT/LE/GT/GE)"
					+ " — equality on a continuous distance is not translatable");
		}
		if (!(bound instanceof Number meters)) {
			throw new QueryException("GeoDistance compares against a numeric bound (meters)");
		}
		GeoSubject subject = distance.getSubject();
		GeoPointLiteral center = distance.getPoint();
		if (subject.getPathPoint() != null) {
			String pair = MongoFieldNames.render(subject.getPathPoint()) + ".coordinates";
			Bson within = Filters.geoWithinCenterSphere(pair, center.getLon(), center.getLat(),
					meters.doubleValue() / EARTH_RADIUS_METERS);
			boolean inside = operator == ComparisonOperator.LT || operator == ComparisonOperator.LE;
			return inside ? within : Filters.and(Filters.nor(within), pairGuard(pair));
		}
		return splitDistance(subject, center, operator, meters.doubleValue());
	}

	// ------------------------------------------------------------------- packed

	/** A valid pair is present: {@code coordinates[1]} exists (missing → UNKNOWN → no match). */
	private static Bson pairGuard(String pair) {
		return Filters.exists(pair + ".1", true);
	}

	private static Bson packedBox(String pair, GeoBox box) {
		double south = box.getSouthWest().getLat();
		double north = box.getNorthEast().getLat();
		double west = box.getSouthWest().getLon();
		double east = box.getNorthEast().getLon();
		if (west <= east) {
			return Filters.geoWithinBox(pair, west, south, east, north);
		}
		// west > east is the legal antimeridian wrap-around box (§5.3) — two halves
		return Filters.or(Filters.geoWithinBox(pair, west, south, 180d, north),
				Filters.geoWithinBox(pair, -180d, south, east, north));
	}

	private static Bson packedPolygon(String pair, GeoPolygon polygon) {
		List<List<Double>> points = new ArrayList<>(polygon.getPoints().size());
		for (GeoPointLiteral point : polygon.getPoints()) {
			points.add(List.of(point.getLon(), point.getLat()));
		}
		return Filters.geoWithinPolygon(pair, points);
	}

	// -------------------------------------------------------------------- split

	private static Bson splitBox(String latField, String lonField, GeoBox box) {
		Bson latRange = Filters.and(Filters.gte(latField, box.getSouthWest().getLat()),
				Filters.lte(latField, box.getNorthEast().getLat()));
		double west = box.getSouthWest().getLon();
		double east = box.getNorthEast().getLon();
		Bson lonRange = west <= east
				? Filters.and(Filters.gte(lonField, west), Filters.lte(lonField, east))
				: Filters.or(Filters.gte(lonField, west), Filters.lte(lonField, east));
		return Filters.and(latRange, lonRange);
	}

	/**
	 * Haversine in {@code $expr} for split subjects. Aggregation comparisons rank null
	 * below numbers (a null distance would satisfy {@code $lte}!) — both fields carry
	 * explicit non-null guards for every operator.
	 */
	private static Bson splitDistance(GeoSubject subject, GeoPointLiteral center,
			ComparisonOperator operator, double meters) {
		String latField = MongoFieldNames.render(subject.getPathLat());
		String lonField = MongoFieldNames.render(subject.getPathLon());
		Object distance = haversineExpr("$" + latField, "$" + lonField,
				center.getLat(), center.getLon());
		String comparator = switch (operator) {
		case LT -> "$lt";
		case LE -> "$lte";
		case GT -> "$gt";
		default -> "$gte";
		};
		return Filters.and(
				Filters.ne(latField, null), Filters.ne(lonField, null),
				Filters.expr(new Document(comparator, List.of(distance, meters))));
	}

	/** {@code 2·R·atan2(√a, √(1−a))}, {@code a = sin²(Δφ/2) + cosφ1·cosφ2·sin²(Δλ/2)}. */
	private static Object haversineExpr(Object latRef, Object lonRef, double lat2, double lon2) {
		Object phi1 = new Document("$degreesToRadians", latRef);
		Object phi2 = new Document("$degreesToRadians", lat2);
		Object dPhiHalf = new Document("$divide", List.of(
				new Document("$degreesToRadians", new Document("$subtract", List.of(lat2, latRef))), 2));
		Object dLambdaHalf = new Document("$divide", List.of(
				new Document("$degreesToRadians", new Document("$subtract", List.of(lon2, lonRef))), 2));
		Object sinPhi = new Document("$sin", dPhiHalf);
		Object sinLambda = new Document("$sin", dLambdaHalf);
		Object a = new Document("$add", List.of(
				new Document("$multiply", List.of(sinPhi, sinPhi)),
				new Document("$multiply", List.of(new Document("$cos", phi1),
						new Document("$cos", phi2),
						new Document("$multiply", List.of(sinLambda, sinLambda))))));
		Object angle = new Document("$atan2", List.of(
				new Document("$sqrt", a),
				new Document("$sqrt", new Document("$subtract", List.of(1, a)))));
		return new Document("$multiply", List.of(2 * EARTH_RADIUS_METERS, angle));
	}
}
