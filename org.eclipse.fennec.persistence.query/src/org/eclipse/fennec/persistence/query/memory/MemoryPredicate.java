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
package org.eclipse.fennec.persistence.query.memory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.AliasRef;
import org.eclipse.fennec.model.expression.Arithmetic;
import org.eclipse.fennec.model.expression.ArithmeticOperator;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.CollectionCount;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.Concat;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.GeoBox;
import org.eclipse.fennec.model.expression.GeoDistance;
import org.eclipse.fennec.model.expression.GeoPointLiteral;
import org.eclipse.fennec.model.expression.GeoPolygon;
import org.eclipse.fennec.model.expression.GeoShape;
import org.eclipse.fennec.model.expression.GeoSubject;
import org.eclipse.fennec.model.expression.GeoWithin;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IndexOf;
import org.eclipse.fennec.model.expression.IntervalMatch;
import org.eclipse.fennec.model.expression.IntervalSubject;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.MapValue;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.Negate;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.NumericFunction;
import org.eclipse.fennec.model.expression.NumericFunctionKind;
import org.eclipse.fennec.model.expression.StringMatchKind;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.Substring;
import org.eclipse.fennec.model.expression.TemporalFunction;
import org.eclipse.fennec.model.expression.TypeCheck;
import org.eclipse.fennec.model.expression.Variable;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;

/**
 * Interprets an expression tree against candidate {@link EObject}s. All values of
 * literals and bound parameters were resolved at translation time (see
 * {@link MemoryQueryProcessor}) — evaluation itself never throws.
 * <p>
 * Comparison semantics follow SQL's Kleene three-valued logic (issue #94): a comparison
 * over a {@code null} operand is UNKNOWN (internally the {@code null} {@link Boolean}),
 * {@code Not}/junctions propagate it ({@code NOT UNKNOWN} stays UNKNOWN), and a row is
 * only selected when the root predicate is <em>true</em> — UNKNOWN excludes like false.
 * {@code IsNull} is the explicit two-valued null probe, {@code ForAll} is vacuously
 * true on empty collections. Numbers compare numerically across their boxed types,
 * enums by their model value.
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
final class MemoryPredicate {

	/** Total order used for sorting and MIN/MAX: nulls last, numbers numeric. */
	static final Comparator<Object> VALUE_ORDER = (left, right) -> {
		if (left == null && right == null) {
			return 0;
		}
		if (left == null) {
			return 1;
		}
		if (right == null) {
			return -1;
		}
		Integer compared = tryCompare(left, right);
		return compared == null ? 0 : compared;
	};

	private final Expression root;
	/** Literal/parameter operand → resolved value, filled at translation time. */
	private final Map<Expression, Object> values;

	MemoryPredicate(Expression root, Map<Expression, Object> values) {
		this.root = root;
		this.values = values;
	}

	/** Evaluates the query's root predicate ({@code true} if the query has none). */
	boolean test(EObject candidate) {
		return root == null || test(root, candidate);
	}

	/** Evaluates any translated expression (e.g. a pipeline FilterStage predicate). */
	boolean test(Expression expression, EObject candidate) {
		return Boolean.TRUE.equals(eval(expression, candidate, new HashMap<>()));
	}

	/**
	 * Kleene evaluation (issue #94): the {@code null} Boolean is UNKNOWN — a predicate
	 * poisoned by a null operand. Junctions and {@code Not} propagate it; the public
	 * entry points collapse it to "not selected".
	 */
	private Boolean eval(Expression expression, EObject candidate, Map<Variable, Object> bindings) {
		if (expression instanceof Junction junction) {
			boolean and = junction instanceof And;
			Boolean result = and;
			for (Expression operand : junction.getOperands()) {
				Boolean value = eval(operand, candidate, bindings);
				if (value == null) {
					result = null;
				} else if (value != and) {
					// FALSE dominates AND, TRUE dominates OR — over UNKNOWN as well
					return !and;
				}
			}
			return result;
		}
		if (expression instanceof Not not) {
			Boolean operand = eval(not.getOperand(), candidate, bindings);
			return operand == null ? null : !operand;
		}
		if (expression instanceof Comparison comparison) {
			Object left = operand(comparison.getLeft(), candidate, bindings);
			Object right = operand(comparison.getRight(), candidate, bindings);
			if (left == null || right == null) {
				return null;
			}
			return switch (comparison.getOperator()) {
			case EQ -> equal(left, right);
			case NE -> !equal(left, right);
			case LT -> lessThan(left, right, false);
			case LE -> lessThan(left, right, true);
			case GT -> lessThan(right, left, false);
			case GE -> lessThan(right, left, true);
			};
		}
		if (expression instanceof IsNull isNull) {
			Object value = operand(isNull.getSource(), candidate, bindings);
			return isNull.isNegated() ? value != null : value == null;
		}
		if (expression instanceof Between between) {
			Object value = operand(between.getSource(), candidate, bindings);
			Object lower = operand(between.getLower(), candidate, bindings);
			Object upper = operand(between.getUpper(), candidate, bindings);
			if (value == null || lower == null || upper == null) {
				return null;
			}
			return lessThan(lower, value, between.isLowerIncluded())
					&& lessThan(value, upper, between.isUpperIncluded());
		}
		if (expression instanceof In in) {
			// x IN (a, b) ≡ x=a OR x=b — a null option keeps a miss UNKNOWN (SQL)
			Object value = operand(in.getSource(), candidate, bindings);
			if (value == null) {
				return null;
			}
			boolean unknown = false;
			for (Expression option : in.getValues()) {
				Object resolved = operand(option, candidate, bindings);
				if (resolved == null) {
					unknown = true;
				} else if (equal(value, resolved)) {
					return true;
				}
			}
			return unknown ? null : false;
		}
		if (expression instanceof StringMatch match) {
			return evalMatch(match, candidate, bindings);
		}
		if (expression instanceof GeoWithin geoWithin) {
			// reference semantics (issue #101): null coordinates are UNKNOWN (3VL)
			double[] position = position(geoWithin.getSubject(), candidate, bindings);
			if (position == null) {
				return null;
			}
			return contains(geoWithin.getShape(), position[0], position[1]);
		}
		if (expression instanceof IntervalMatch interval) {
			return evalInterval(interval, candidate, bindings);
		}
		if (expression instanceof Quantifier quantifier) {
			return evalQuantifier(quantifier, candidate, bindings);
		}
		if (expression instanceof TypeCheck typeCheck) {
			// kind-of semantics (issue #80): the subject's EClass is the type or a subtype
			Object subject = typeCheck.getSource() == null ? candidate
					: pathValue(typeCheck.getSource(), candidate, bindings);
			return subject instanceof EObject object && typeCheck.getType().isSuperTypeOf(object.eClass());
		}
		// unreachable: translation refused everything else
		return false;
	}

	private Boolean evalMatch(StringMatch match, EObject candidate, Map<Variable, Object> bindings) {
		Object source = operand(match.getSource(), candidate, bindings);
		Object pattern = values.get(match.getPattern());
		if (source == null || pattern == null) {
			return null; // LIKE over a null operand is UNKNOWN
		}
		if (!(source instanceof String text)) {
			return false;
		}
		String raw = String.valueOf(pattern);
		if (match.isCaseInsensitive() && match.getKind() != StringMatchKind.LIKE) {
			text = text.toLowerCase();
			raw = raw.toLowerCase();
		}
		return switch (match.getKind()) {
		case CONTAINS -> text.contains(raw);
		case STARTS_WITH -> text.startsWith(raw);
		case ENDS_WITH -> text.endsWith(raw);
		case LIKE -> likePattern(raw, match.isCaseInsensitive()).matcher(text).matches();
		case FUZZY -> fuzzyMatch(text, raw, match);
		};
	}

	/**
	 * Reference semantics for kind FUZZY (issue #167): whole-value edit distance within the
	 * {@code maxEdits} budget, after the {@code prefixLength} leading characters matched
	 * exactly (excluded from edit counting, like Lucene's prefix). Case folding happened in
	 * {@link #evalMatch} when {@code caseInsensitive} is set — the distance is over the
	 * folded strings.
	 */
	private static boolean fuzzyMatch(String text, String pattern, StringMatch match) {
		int prefix = match.getPrefixLength();
		if (text.length() < prefix || pattern.length() < prefix
				|| !text.regionMatches(0, pattern, 0, prefix)) {
			return false;
		}
		String textTail = text.substring(prefix);
		String patternTail = pattern.substring(prefix);
		int budget = match.getMaxEdits();
		if (Math.abs(textTail.length() - patternTail.length()) > budget) {
			return false;
		}
		return damerauLevenshtein(textTail, patternTail) <= budget;
	}

	/**
	 * Optimal-string-alignment Damerau-Levenshtein — insert, delete, substitute and adjacent
	 * transposition each cost 1, the distance Lucene's {@code FuzzyQuery} counts.
	 */
	private static int damerauLevenshtein(String a, String b) {
		int[][] d = new int[a.length() + 1][b.length() + 1];
		for (int i = 0; i <= a.length(); i++) {
			d[i][0] = i;
		}
		for (int j = 0; j <= b.length(); j++) {
			d[0][j] = j;
		}
		for (int i = 1; i <= a.length(); i++) {
			for (int j = 1; j <= b.length(); j++) {
				int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
				d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + cost);
				if (i > 1 && j > 1 && a.charAt(i - 1) == b.charAt(j - 2)
						&& a.charAt(i - 2) == b.charAt(j - 1)) {
					d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + 1);
				}
			}
		}
		return d[a.length()][b.length()];
	}

	private Boolean evalQuantifier(Quantifier quantifier, EObject candidate, Map<Variable, Object> bindings) {
		Object collection = pathValue(quantifier.getSource(), candidate, bindings);
		boolean exists = quantifier instanceof Exists;
		if (!(collection instanceof Collection<?> elements) || elements.isEmpty()) {
			return !exists; // exists: false on empty, forAll: vacuously true
		}
		Boolean result = !exists;
		for (Object element : elements) {
			bindings.put(quantifier.getVariable(), element);
			Boolean matches = eval(quantifier.getPredicate(), candidate, bindings);
			bindings.remove(quantifier.getVariable());
			if (matches == null) {
				result = null;
			} else if (matches == exists) {
				return exists;
			}
		}
		return result;
	}

	/** Resolves a comparison operand: navigations and functions per candidate, values from the map. */
	private Object operand(Expression expression, EObject candidate, Map<Variable, Object> bindings) {
		if (expression instanceof PropertyPath path) {
			return pathValue(path, candidate, bindings);
		}
		if (expression instanceof StringFunction function) {
			Object inner = operand(function.getSource(), candidate, bindings);
			if (inner == null) {
				return null;
			}
			String text = String.valueOf(inner);
			return switch (function.getKind()) {
			case TO_LOWER -> text.toLowerCase();
			case TO_UPPER -> text.toUpperCase();
			case TRIM -> text.trim();
			case LENGTH -> text.length();
			};
		}
		if (expression instanceof MapValue mapValue) {
			// the reference semantics of map access (issue #186): EMF hands out an EMap, so
			// this is a plain get — no store shape in the way
			Object map = pathValue(mapValue.getMap(), candidate, bindings);
			if (!(map instanceof EMap<?, ?> entries)) {
				return null;
			}
			return entries.get(values.get(mapValue.getKey()));
		}
		if (expression instanceof CollectionCount count) {
			Object value = pathValue(count.getSource(), candidate, bindings);
			if (!(value instanceof Collection<?> elements)) {
				return null;
			}
			if (count.getPredicate() == null) {
				return elements.size();
			}
			int matches = 0;
			for (Object element : elements) {
				bindings.put(count.getVariable(), element);
				// only TRUE counts — UNKNOWN elements don't (SQL COUNT semantics)
				if (Boolean.TRUE.equals(eval(count.getPredicate(), candidate, bindings))) {
					matches++;
				}
			}
			bindings.remove(count.getVariable());
			return matches;
		}
		if (expression instanceof Arithmetic arithmetic) {
			Object left = operand(arithmetic.getLeft(), candidate, bindings);
			Object right = operand(arithmetic.getRight(), candidate, bindings);
			return arithmetic(arithmetic.getOperator(), left, right);
		}
		if (expression instanceof Negate negate) {
			return negate(operand(negate.getOperand(), candidate, bindings));
		}
		if (expression instanceof Concat concatenation) {
			StringBuilder text = new StringBuilder();
			for (Expression part : concatenation.getParts()) {
				Object value = operand(part, candidate, bindings);
				if (value == null) {
					return null; // null parts poison the concatenation (Mongo/|| semantics)
				}
				text.append(value);
			}
			return text.toString();
		}
		if (expression instanceof IndexOf indexOf) {
			Object source = operand(indexOf.getSource(), candidate, bindings);
			Object search = operand(indexOf.getSearch(), candidate, bindings);
			if (source == null || search == null) {
				return null;
			}
			return String.valueOf(source).indexOf(String.valueOf(search));
		}
		if (expression instanceof Substring substring) {
			return substring(substring, candidate, bindings);
		}
		if (expression instanceof TemporalFunction function) {
			ZonedDateTime value = toUtc(operand(function.getSource(), candidate, bindings));
			if (value == null) {
				return null;
			}
			// UTC-normative part extraction (issue #79); SECOND is integral
			return switch (function.getKind()) {
			case YEAR -> value.getYear();
			case MONTH -> value.getMonthValue();
			case DAY -> value.getDayOfMonth();
			case HOUR -> value.getHour();
			case MINUTE -> value.getMinute();
			case SECOND -> value.getSecond();
			// DATE and TIME yield a VALUE rather than a component (issue #240). The reference
			// representation is the java.time one; each backend stores what it can compare and
			// sort, which is why the TCK asserts on query results and not on representations.
			case DATE -> value.toLocalDate();
			case TIME -> value.toLocalTime();
			};
		}
		if (expression instanceof NumericFunction function) {
			return rounded(function.getKind(), operand(function.getSource(), candidate, bindings));
		}
		if (expression instanceof GeoDistance geoDistance) {
			// reference formula (issue #101): haversine over the mean earth radius,
			// meters; null coordinates poison the enclosing comparison (UNKNOWN)
			double[] position = position(geoDistance.getSubject(), candidate, bindings);
			if (position == null) {
				return null;
			}
			return haversineMeters(position[1], position[0],
					geoDistance.getPoint().getLat(), geoDistance.getPoint().getLon());
		}
		return values.get(expression);
	}

	// -------------------------------------------------------- intervals (issue #215)

	/**
	 * Reference semantics for interval predicates (issue #215, concept §A.5). An unset bound
	 * is the infinity the subject declares when {@code nullMeansUnbounded} is set, and
	 * UNKNOWN otherwise.
	 * <p>
	 * UNKNOWN belongs to the single bound comparison, not to the predicate as a whole: the
	 * conjunction then decides under ordinary 3VL, so a row whose end is unknown still
	 * answers FALSE where its start already rules the question out. That is what SQL does
	 * with the same two comparisons, and making it the reference is what keeps the
	 * translations free of negation surgery — the alternative cannot be expressed in SQL
	 * without one, because a FALSE conjunct swallows the UNKNOWN.
	 * <p>
	 * An empty subject row (bounds inverted, or equal with an exclusive end) matches no
	 * relation, {@code WITHIN} included: vacuous truth is the answer nobody wants.
	 */
	private Boolean evalInterval(IntervalMatch interval, EObject candidate, Map<Variable, Object> bindings) {
		IntervalSubject subject = interval.getSubject();
		if (subject == null || subject.getPathLower() == null || subject.getPathUpper() == null) {
			return null;
		}
		Object subjectLower = pathValue(subject.getPathLower(), candidate, bindings);
		Object subjectUpper = pathValue(subject.getPathUpper(), candidate, bindings);
		Object queryLower = operand(interval.getLower(), candidate, bindings);
		Object queryUpper = operand(interval.getUpper(), candidate, bindings);
		if (queryLower == null || queryUpper == null) {
			return null;
		}
		Boolean empty = isEmptyInterval(subjectLower, subjectUpper, subject);
		if (empty == null) {
			return null;
		}
		if (empty.booleanValue()) {
			return Boolean.FALSE;
		}
		boolean subjectLowerIncluded = subject.isLowerIncluded();
		boolean subjectUpperIncluded = subject.isUpperIncluded();
		boolean queryLowerIncluded = interval.isLowerIncluded();
		boolean queryUpperIncluded = interval.isUpperIncluded();
		boolean unbounded = subject.isNullMeansUnbounded();
		return switch (interval.getRelation()) {
		// they overlap iff neither ends before the other starts; a shared endpoint counts
		// only when BOTH sides include it
		case INTERSECTS -> and3(
				atOrBefore(subjectLower, queryUpper, subjectLowerIncluded && queryUpperIncluded,
						unbounded, Boolean.TRUE),
				atOrAfter(subjectUpper, queryLower, subjectUpperIncluded && queryLowerIncluded,
						unbounded, Boolean.TRUE));
		// the subject sits inside: at a shared endpoint it is enough that the query includes
		// it, or that the subject does not reach it — hence the disjunction
		case WITHIN -> and3(
				atOrAfter(subjectLower, queryLower, queryLowerIncluded || !subjectLowerIncluded,
						unbounded, Boolean.FALSE),
				atOrBefore(subjectUpper, queryUpper, queryUpperIncluded || !subjectUpperIncluded,
						unbounded, Boolean.FALSE));
		// the mirror of WITHIN with the roles swapped
		case CONTAINS -> and3(
				atOrBefore(subjectLower, queryLower, subjectLowerIncluded || !queryLowerIncluded,
						unbounded, Boolean.TRUE),
				atOrAfter(subjectUpper, queryUpper, subjectUpperIncluded || !queryUpperIncluded,
						unbounded, Boolean.TRUE));
		};
	}

	/**
	 * Whether the subject row denotes the empty interval — inverted bounds, or coincident
	 * bounds with at least one of them excluded. An unbounded end is never empty.
	 */
	private static Boolean isEmptyInterval(Object lower, Object upper, IntervalSubject subject) {
		if (lower == null || upper == null) {
			return Boolean.FALSE;
		}
		Integer compared = compareBounds(lower, upper);
		if (compared == null) {
			return null;
		}
		return compared > 0
				|| (compared == 0 && !(subject.isLowerIncluded() && subject.isUpperIncluded()));
	}

	/**
	 * Whether {@code value} lies before {@code limit}, with coincidence counting as decided
	 * by the caller — the inclusion rule differs per relation. An absent value is the
	 * declared infinity ({@code whenAbsent}) where the subject declares one, and UNKNOWN
	 * otherwise.
	 */
	private static Boolean atOrBefore(Object value, Object limit, boolean equalCounts,
			boolean unbounded, Boolean whenAbsent) {
		if (value == null) {
			return unbounded ? whenAbsent : null;
		}
		Integer compared = compareBounds(value, limit);
		return compared == null ? null : compared < 0 || (compared == 0 && equalCounts);
	}

	/** The mirror of {@link #atOrBefore(Object, Object, boolean, boolean, Boolean)}. */
	private static Boolean atOrAfter(Object value, Object limit, boolean equalCounts,
			boolean unbounded, Boolean whenAbsent) {
		if (value == null) {
			return unbounded ? whenAbsent : null;
		}
		Integer compared = compareBounds(value, limit);
		return compared == null ? null : compared > 0 || (compared == 0 && equalCounts);
	}

	/** Signum comparison of two bound values, {@code null} when they are incomparable. */
	private static Integer compareBounds(Object left, Object right) {
		Integer compared = tryCompare(left, right);
		return compared == null ? null : Integer.valueOf(Integer.signum(compared));
	}

	/** Three-valued conjunction: FALSE wins over UNKNOWN, UNKNOWN over TRUE. */
	private static Boolean and3(Boolean left, Boolean right) {
		if (Boolean.FALSE.equals(left) || Boolean.FALSE.equals(right)) {
			return Boolean.FALSE;
		}
		return left == null || right == null ? null : Boolean.TRUE;
	}

	// ------------------------------------------------------------- geo (issue #101)

	/** Mean earth radius (meters) — the reference sphere of decision G5. */
	private static final double EARTH_RADIUS_METERS = 6_371_008.8d;

	/**
	 * The subject's position as {@code [lon, lat]}, or {@code null} when a coordinate
	 * is null/non-numeric (→ UNKNOWN). Packed subjects carry the canonical GeoJSON
	 * point shape (issue #113, G-P2).
	 */
	private double[] position(GeoSubject subject, EObject candidate, Map<Variable, Object> bindings) {
		if (subject == null) {
			return null;
		}
		if (subject.getPathPoint() != null) {
			return packedPosition(pathValue(subject.getPathPoint(), candidate, bindings));
		}
		if (subject.getPathLat() == null || subject.getPathLon() == null) {
			return null;
		}
		Object lat = pathValue(subject.getPathLat(), candidate, bindings);
		Object lon = pathValue(subject.getPathLon(), candidate, bindings);
		if (!(lat instanceof Number latitude) || !(lon instanceof Number longitude)) {
			return null;
		}
		return new double[] { longitude.doubleValue(), latitude.doubleValue() };
	}

	/**
	 * Reads the canonical PACKED value shape (issue #113, G-P2): a GeoJSON-style point,
	 * i.e. an EObject exposing a many-valued numeric {@code coordinates} feature in
	 * {@code [lon, lat]} order. Any other value is UNKNOWN — the packed analogue of a
	 * null coordinate.
	 */
	private static double[] packedPosition(Object value) {
		if (!(value instanceof EObject point)) {
			return null;
		}
		EStructuralFeature coordinates = point.eClass().getEStructuralFeature("coordinates");
		if (coordinates == null || !coordinates.isMany()
				|| !(point.eGet(coordinates) instanceof List<?> components) || components.size() < 2) {
			return null;
		}
		if (components.get(0) instanceof Number lon && components.get(1) instanceof Number lat) {
			return new double[] { lon.doubleValue(), lat.doubleValue() };
		}
		return null;
	}

	private static boolean contains(GeoShape shape, double lon, double lat) {
		if (shape instanceof GeoBox box) {
			if (lat < box.getSouthWest().getLat() || lat > box.getNorthEast().getLat()) {
				return false;
			}
			double west = box.getSouthWest().getLon();
			double east = box.getNorthEast().getLon();
			// west > east is the legal antimeridian wrap-around box (§5.3)
			return west <= east ? lon >= west && lon <= east : lon >= west || lon <= east;
		}
		if (shape instanceof GeoPolygon polygon) {
			// ray casting on the lon/lat plane — polygons never cross the antimeridian
			// (validated structurally), implicitly closed
			List<GeoPointLiteral> points = polygon.getPoints();
			boolean inside = false;
			for (int i = 0, j = points.size() - 1; i < points.size(); j = i++) {
				double xi = points.get(i).getLon();
				double yi = points.get(i).getLat();
				double xj = points.get(j).getLon();
				double yj = points.get(j).getLat();
				if ((yi > lat) != (yj > lat)
						&& lon < (xj - xi) * (lat - yi) / (yj - yi) + xi) {
					inside = !inside;
				}
			}
			return inside;
		}
		return false;
	}

	/** Haversine distance in meters over the mean earth radius (reference, decision G5). */
	private static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
		double phi1 = Math.toRadians(lat1);
		double phi2 = Math.toRadians(lat2);
		double dPhi = Math.toRadians(lat2 - lat1);
		double dLambda = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
				+ Math.cos(phi1) * Math.cos(phi2) * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);
		return 2 * EARTH_RADIUS_METERS * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}

	/**
	 * Substring per [OData-URL] 5.1.1.7: 0-based; a negative start counts from the end
	 * (clamped to 0), a start beyond the end and a negative length yield the empty
	 * string. Mirrors the OData in-memory reference and the {@code $substrCP} clamping.
	 */
	private Object substring(Substring substring, EObject candidate, Map<Variable, Object> bindings) {
		Object source = operand(substring.getSource(), candidate, bindings);
		Object start = operand(substring.getStart(), candidate, bindings);
		if (!(source instanceof String value) || !(start instanceof Number requested)) {
			return null;
		}
		int startIndex = clampToInt(requested);
		int effectiveStart = startIndex < 0
				? Math.max(0, value.length() + startIndex)
				: Math.min(startIndex, value.length());
		if (substring.getLength() == null) {
			return value.substring(effectiveStart);
		}
		Object length = operand(substring.getLength(), candidate, bindings);
		if (!(length instanceof Number bound)) {
			return null;
		}
		int end = (int) Math.min(value.length(),
				Math.max(effectiveStart, (long) effectiveStart + clampToInt(bound)));
		return value.substring(effectiveStart, end);
	}

	/** ROUND is half away from zero (OData semantics); the result is integral (issue #78). */
	private static Object rounded(NumericFunctionKind kind, Object inner) {
		if (!(inner instanceof Number number)) {
			return null;
		}
		BigDecimal value = decimal(number);
		return switch (kind) {
		case ROUND -> value.setScale(0, RoundingMode.HALF_UP).longValue();
		case FLOOR -> value.setScale(0, RoundingMode.FLOOR).longValue();
		case CEILING -> value.setScale(0, RoundingMode.CEILING).longValue();
		};
	}

	// ------------------------------------------------------------- row space (issue #82)

	/** Evaluates a post-grouping pipeline predicate against a result row. */
	boolean testRow(Expression expression, QueryResultRow row) {
		return Boolean.TRUE.equals(evalRow(expression, row));
	}

	/** Row-space Kleene evaluation — same UNKNOWN propagation as {@link #eval} (issue #94). */
	private Boolean evalRow(Expression expression, QueryResultRow row) {
		if (expression instanceof Junction junction) {
			boolean and = junction instanceof And;
			Boolean result = and;
			for (Expression operand : junction.getOperands()) {
				Boolean value = evalRow(operand, row);
				if (value == null) {
					result = null;
				} else if (value != and) {
					return !and;
				}
			}
			return result;
		}
		if (expression instanceof Not not) {
			Boolean operand = evalRow(not.getOperand(), row);
			return operand == null ? null : !operand;
		}
		if (expression instanceof Comparison comparison) {
			Object left = rowValue(comparison.getLeft(), row);
			Object right = rowValue(comparison.getRight(), row);
			if (left == null || right == null) {
				return null;
			}
			return switch (comparison.getOperator()) {
			case EQ -> equal(left, right);
			case NE -> !equal(left, right);
			case LT -> lessThan(left, right, false);
			case LE -> lessThan(left, right, true);
			case GT -> lessThan(right, left, false);
			case GE -> lessThan(right, left, true);
			};
		}
		if (expression instanceof IsNull isNull) {
			Object value = rowValue(isNull.getSource(), row);
			return isNull.isNegated() ? value != null : value == null;
		}
		if (expression instanceof Between between) {
			Object value = rowValue(between.getSource(), row);
			Object lower = rowValue(between.getLower(), row);
			Object upper = rowValue(between.getUpper(), row);
			if (value == null || lower == null || upper == null) {
				return null;
			}
			return lessThan(lower, value, between.isLowerIncluded())
					&& lessThan(value, upper, between.isUpperIncluded());
		}
		if (expression instanceof In in) {
			Object value = rowValue(in.getSource(), row);
			if (value == null) {
				return null;
			}
			boolean unknown = false;
			for (Expression option : in.getValues()) {
				Object resolved = rowValue(option, row);
				if (resolved == null) {
					unknown = true;
				} else if (equal(value, resolved)) {
					return true;
				}
			}
			return unknown ? null : false;
		}
		// unreachable: the row-space translation refused everything else
		return false;
	}

	/**
	 * Evaluates a row-space value expression: AliasRef and PropertyPath address output
	 * columns, arithmetic/numeric functions compute over them, literals and bound
	 * parameters come from the translation-time value map.
	 */
	Object rowValue(Expression expression, QueryResultRow row) {
		if (expression instanceof AliasRef aliasRef) {
			return row.get(aliasRef.getAlias());
		}
		if (expression instanceof PropertyPath path) {
			StringBuilder key = new StringBuilder();
			path.getSegments().forEach(segment -> {
				if (key.length() > 0) {
					key.append('_');
				}
				key.append(segment.getName());
			});
			return row.get(key.toString());
		}
		if (expression instanceof Arithmetic arithmetic) {
			return arithmetic(arithmetic.getOperator(),
					rowValue(arithmetic.getLeft(), row), rowValue(arithmetic.getRight(), row));
		}
		if (expression instanceof Negate negate) {
			return negate(rowValue(negate.getOperand(), row));
		}
		if (expression instanceof NumericFunction function) {
			return rounded(function.getKind(), rowValue(function.getSource(), row));
		}
		return values.get(expression);
	}

	/** Views any supported temporal value as a UTC instant; local values are UTC wall-clock. */
	private static ZonedDateTime toUtc(Object value) {
		if (value instanceof Date date) {
			return date.toInstant().atZone(ZoneOffset.UTC);
		}
		if (value instanceof Instant instant) {
			return instant.atZone(ZoneOffset.UTC);
		}
		if (value instanceof LocalDate localDate) {
			return localDate.atStartOfDay(ZoneOffset.UTC);
		}
		if (value instanceof LocalDateTime localDateTime) {
			return localDateTime.atZone(ZoneOffset.UTC);
		}
		if (value instanceof LocalTime localTime) {
			return localTime.atDate(LocalDate.EPOCH).atZone(ZoneOffset.UTC);
		}
		if (value instanceof ZonedDateTime zoned) {
			return zoned.withZoneSameInstant(ZoneOffset.UTC);
		}
		if (value instanceof OffsetDateTime offset) {
			return offset.atZoneSameInstant(ZoneOffset.UTC);
		}
		return null;
	}

	/** Saturates a numeric offset/length to the int range (no silent overflow wrap). */
	private static int clampToInt(Number number) {
		long value = number.longValue();
		if (value > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		if (value < Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		}
		return (int) value;
	}

	/**
	 * Arithmetic per issue #76: type-preserving Java promotion — integral operands stay
	 * integral, one floating operand widens the result, BigDecimal wins over both.
	 * DIV is the exception and always divides floating-point (DECIMAL64 for decimals).
	 * Null operands and a zero divisor yield {@code null} (the enclosing comparison is
	 * false) — the database backends surface their own division error instead.
	 */
	private static Object arithmetic(ArithmeticOperator operator, Object left, Object right) {
		if (!(left instanceof Number a) || !(right instanceof Number b)) {
			return null;
		}
		if (operator == ArithmeticOperator.DIV) {
			if (a instanceof BigDecimal || b instanceof BigDecimal) {
				BigDecimal divisor = decimal(b);
				return divisor.signum() == 0 ? null : decimal(a).divide(divisor, MathContext.DECIMAL64);
			}
			double divisor = b.doubleValue();
			return divisor == 0.0d ? null : a.doubleValue() / divisor;
		}
		if (a instanceof BigDecimal || b instanceof BigDecimal) {
			BigDecimal l = decimal(a);
			BigDecimal r = decimal(b);
			return switch (operator) {
			case ADD -> l.add(r);
			case SUB -> l.subtract(r);
			case MUL -> l.multiply(r);
			case MOD -> r.signum() == 0 ? null : l.remainder(r);
			default -> null; // DIV handled above
			};
		}
		if (isFloating(a) || isFloating(b)) {
			double l = a.doubleValue();
			double r = b.doubleValue();
			return switch (operator) {
			case ADD -> l + r;
			case SUB -> l - r;
			case MUL -> l * r;
			case MOD -> r == 0.0d ? null : l % r;
			default -> null;
			};
		}
		long l = a.longValue();
		long r = b.longValue();
		return switch (operator) {
		case ADD -> l + r;
		case SUB -> l - r;
		case MUL -> l * r;
		case MOD -> r == 0L ? null : l % r;
		default -> null;
		};
	}

	private static Object negate(Object value) {
		if (value instanceof BigDecimal decimal) {
			return decimal.negate();
		}
		if (value instanceof Number number) {
			return isFloating(number) ? -number.doubleValue() : -number.longValue();
		}
		return null;
	}

	private static boolean isFloating(Number number) {
		return number instanceof Double || number instanceof Float;
	}

	private static BigDecimal decimal(Number number) {
		return number instanceof BigDecimal decimal ? decimal : new BigDecimal(number.toString());
	}

	/** Navigates a root-based path (used by the plan for sort/projection/grouping). */
	Object pathValue(PropertyPath path, EObject candidate) {
		return pathValue(path, candidate, Map.of());
	}

	/** Evaluates a value expression against a candidate (used for sort keys, issue #84). */
	Object value(Expression expression, EObject candidate) {
		return operand(expression, candidate, Map.of());
	}

	/**
	 * Evaluates a value expression against a candidate with a pre-group compute alias
	 * environment (issue #87): AliasRef resolves to the computed value, arithmetic and
	 * numeric functions recurse with the environment, everything else evaluates in
	 * plain object space.
	 */
	Object value(Expression expression, EObject candidate, Map<String, Object> aliasValues) {
		if (expression instanceof AliasRef aliasRef) {
			return aliasValues.get(aliasRef.getAlias());
		}
		if (expression instanceof Arithmetic arithmetic) {
			return arithmetic(arithmetic.getOperator(),
					value(arithmetic.getLeft(), candidate, aliasValues),
					value(arithmetic.getRight(), candidate, aliasValues));
		}
		if (expression instanceof Negate negate) {
			return negate(value(negate.getOperand(), candidate, aliasValues));
		}
		if (expression instanceof NumericFunction function) {
			return rounded(function.getKind(), value(function.getSource(), candidate, aliasValues));
		}
		return operand(expression, candidate, Map.of());
	}

	private Object pathValue(PropertyPath path, EObject candidate, Map<Variable, Object> bindings) {
		Object current = path.getBase() == null ? candidate : bindings.get(path.getBase());
		if (path.getCastBase() != null) {
			// treat semantics (issue #80): a non-instance yields null, comparisons are false
			if (!(current instanceof EObject origin) || !path.getCastBase().isSuperTypeOf(origin.eClass())) {
				return null;
			}
		}
		for (EStructuralFeature segment : path.getSegments()) {
			if (!(current instanceof EObject object)) {
				return null;
			}
			EStructuralFeature actual = object.eClass().getEStructuralFeature(segment.getName());
			if (actual == null) {
				return null;
			}
			current = object.eGet(actual);
		}
		return current;
	}

	// ------------------------------------------------------------- value logic

	private static boolean equal(Object left, Object right) {
		if (left instanceof Number a && right instanceof Number b) {
			return Double.compare(a.doubleValue(), b.doubleValue()) == 0;
		}
		if (left instanceof Enumerator a && right instanceof Enumerator b) {
			return a.getValue() == b.getValue();
		}
		return Objects.equals(left, right);
	}

	private static boolean lessThan(Object left, Object right, boolean orEqual) {
		Integer compared = tryCompare(left, right);
		if (compared == null) {
			return false;
		}
		return orEqual ? compared <= 0 : compared < 0;
	}

	@SuppressWarnings("unchecked")
	private static Integer tryCompare(Object left, Object right) {
		if (left instanceof Number a && right instanceof Number b) {
			return Double.compare(a.doubleValue(), b.doubleValue());
		}
		if (left instanceof Enumerator a && right instanceof Enumerator b) {
			return Integer.compare(a.getValue(), b.getValue());
		}
		if (left instanceof Comparable comparable && left.getClass().isInstance(right)) {
			return comparable.compareTo(right);
		}
		return null;
	}

	/** Translates a LIKE pattern ({@code %}, {@code _}, {@code \} escape) into a regex. */
	private static Pattern likePattern(String like, boolean caseInsensitive) {
		StringBuilder regex = new StringBuilder();
		boolean escaped = false;
		for (int i = 0; i < like.length(); i++) {
			char c = like.charAt(i);
			if (escaped) {
				regex.append(Pattern.quote(String.valueOf(c)));
				escaped = false;
			} else if (c == '\\') {
				escaped = true;
			} else if (c == '%') {
				regex.append(".*");
			} else if (c == '_') {
				regex.append('.');
			} else {
				regex.append(Pattern.quote(String.valueOf(c)));
			}
		}
		return Pattern.compile(regex.toString(),
				caseInsensitive ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0);
	}
}
