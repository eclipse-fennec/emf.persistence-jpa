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
package org.eclipse.fennec.persistence.query.expr;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.expression.BooleanLiteral;
import org.eclipse.fennec.model.expression.DurationLiteral;
import org.eclipse.fennec.model.expression.EnumLiteral;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.GuidLiteral;
import org.eclipse.fennec.model.expression.IntegerLiteral;
import org.eclipse.fennec.model.expression.Literal;
import org.eclipse.fennec.model.expression.NullLiteral;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.RealLiteral;
import org.eclipse.fennec.model.expression.StringLiteral;
import org.eclipse.fennec.model.expression.TemporalLiteral;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.eclipse.fennec.persistence.query.QueryException;

/**
 * Typed value resolution for the v2 expression IR — the successor of the v1
 * {@code QueryValues}/{@code QueryParameters} pair: literals are already typed in the
 * model (no string parsing), parameters are first-class {@link ParameterRef}s (no
 * {@code ":name"} convention), and the shared {@link ConverterService} still owns the
 * EMF→persistence conversion.
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
public final class ExpressionValues {

	private ExpressionValues() {
	}

	/**
	 * Returns the feature a {@code PropertyPath} finally addresses — its last segment.
	 *
	 * @param path the path; may be {@code null} or empty
	 * @return the last segment, or {@code null}
	 */
	public static EStructuralFeature targetFeature(PropertyPath path) {
		if (path == null || path.getSegments().isEmpty()) {
			return null;
		}
		return path.getSegments().get(path.getSegments().size() - 1);
	}

	/**
	 * Resolves a value expression (literal or parameter) to the typed backend value.
	 *
	 * @param expression the value expression
	 * @param target the feature the value is compared against; may be {@code null}
	 * @param parameters bound parameter values; may be {@code null}
	 * @param converter the shared converter service; may be {@code null}
	 * @return the typed backend value
	 * @throws QueryException if the expression is no value expression, a parameter is
	 *         unbound, or a literal does not fit the target feature
	 */
	public static Object resolve(Expression expression, EStructuralFeature target, Map<String, Object> parameters,
			ConverterService converter) throws QueryException {
		if (expression instanceof Literal literal) {
			return toPersistenceValue(literalValue(literal, target), target, converter);
		}
		if (expression instanceof ParameterRef parameter) {
			if (parameters == null || !parameters.containsKey(parameter.getName())) {
				throw new QueryException("Unbound query parameter '" + parameter.getName() + "' for feature '"
						+ (target == null ? "<unknown>" : target.getName()) + "' — bind it via the parameters map");
			}
			return toPersistenceValue(parameters.get(parameter.getName()), target, converter);
		}
		throw new QueryException("Expected a literal or parameter as comparison value, was "
				+ (expression == null ? "null" : expression.eClass().getName()));
	}

	/**
	 * Converts a typed literal to its EMF value, narrowing numbers to the target
	 * feature's instance class and resolving enum/temporal literals.
	 *
	 * @param literal the literal
	 * @param target the target feature; may be {@code null} (no narrowing)
	 * @return the EMF-typed value
	 * @throws QueryException if the literal cannot be resolved against the target
	 */
	public static Object literalValue(Literal literal, EStructuralFeature target) throws QueryException {
		if (literal instanceof NullLiteral) {
			return null;
		}
		if (literal instanceof StringLiteral string) {
			if (target != null && target.getEType() instanceof EEnum eEnum) {
				// OData transports enum values as quoted strings (issue #93)
				return resolveEnum(eEnum, string.getValue(), target);
			}
			return string.getValue();
		}
		if (literal instanceof BooleanLiteral bool) {
			return bool.isValue();
		}
		if (literal instanceof IntegerLiteral integer) {
			return narrowInteger(integer.getValue(), instanceClass(target));
		}
		if (literal instanceof RealLiteral real) {
			return narrowReal(real.getValue(), instanceClass(target));
		}
		if (literal instanceof EnumLiteral enumLiteral) {
			return resolveEnum(enumLiteral, target);
		}
		if (literal instanceof TemporalLiteral temporal) {
			return resolveTemporal(temporal, instanceClass(target));
		}
		if (literal instanceof GuidLiteral guid) {
			return resolveGuid(guid, instanceClass(target));
		}
		if (literal instanceof DurationLiteral duration) {
			return resolveDuration(duration, instanceClass(target));
		}
		throw new QueryException("Unsupported literal " + literal.eClass().getName());
	}

	/** Resolves a GUID literal against the target type: UUID by default, canonical text for String targets (issue #83). */
	private static Object resolveGuid(GuidLiteral literal, Class<?> type) throws QueryException {
		UUID uuid;
		try {
			uuid = UUID.fromString(literal.getValue());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new QueryException("Invalid GUID literal '" + literal.getValue() + "'", e);
		}
		return type == String.class ? uuid.toString() : uuid;
	}

	/**
	 * Resolves a duration literal against the target type: {@link Duration} by default,
	 * milliseconds for Long targets (the JPA column form), ISO text for String targets
	 * (issue #83).
	 */
	private static Object resolveDuration(DurationLiteral literal, Class<?> type) throws QueryException {
		Duration duration;
		try {
			duration = Duration.parse(literal.getIso8601());
		} catch (DateTimeParseException | NullPointerException e) {
			throw new QueryException("Invalid ISO-8601 duration literal '" + literal.getIso8601() + "'", e);
		}
		if (type == Long.class || type == long.class) {
			return duration.toMillis();
		}
		return type == String.class ? duration.toString() : duration;
	}

	/**
	 * Converts an EMF-typed value into its backend representation via the shared
	 * {@link ConverterService}; identity without a converter.
	 *
	 * @param emfValue the EMF value; {@code null} stays {@code null}
	 * @param target the target feature; may be {@code null}
	 * @param converter the converter service; may be {@code null}
	 * @return the persistence-side value
	 */
	public static Object toPersistenceValue(Object emfValue, EStructuralFeature target, ConverterService converter) {
		if (emfValue == null || converter == null || target == null) {
			return emfValue;
		}
		EClassifier type = target.getEType();
		TypeConverter typeConverter = converter.getConverter(type);
		if (typeConverter == null) {
			return emfValue;
		}
		return typeConverter.convertEMFToValue(type, emfValue);
	}

	private static Class<?> instanceClass(EStructuralFeature target) {
		return target == null || target.getEType() == null ? null : target.getEType().getInstanceClass();
	}

	private static Object narrowInteger(long value, Class<?> type) {
		if (type == Integer.class || type == int.class) {
			return Math.toIntExact(value);
		}
		if (type == Short.class || type == short.class) {
			return (short) value;
		}
		if (type == Byte.class || type == byte.class) {
			return (byte) value;
		}
		if (type == BigInteger.class) {
			return BigInteger.valueOf(value);
		}
		if (type == BigDecimal.class) {
			return BigDecimal.valueOf(value);
		}
		if (type == Double.class || type == double.class) {
			return (double) value;
		}
		if (type == Float.class || type == float.class) {
			return (float) value;
		}
		return value;
	}

	private static Object narrowReal(double value, Class<?> type) {
		if (type == Float.class || type == float.class) {
			return (float) value;
		}
		if (type == BigDecimal.class) {
			return BigDecimal.valueOf(value);
		}
		return value;
	}

	private static Object resolveEnum(EnumLiteral literal, EStructuralFeature target) throws QueryException {
		if (target == null || !(target.getEType() instanceof EEnum eEnum)) {
			// without an enum-typed target the name is the best representation
			return literal.getLiteralName();
		}
		return resolveEnum(eEnum, literal.getLiteralName(), target);
	}

	private static Object resolveEnum(EEnum eEnum, String literalName, EStructuralFeature target)
			throws QueryException {
		EEnumLiteral resolved = eEnum.getEEnumLiteral(literalName);
		if (resolved == null) {
			throw new QueryException("Enum " + eEnum.getName() + " has no literal '" + literalName
					+ "' (feature '" + target.getName() + "')");
		}
		return resolved.getInstance() != null ? resolved.getInstance() : resolved;
	}

	private static Object resolveTemporal(TemporalLiteral literal, Class<?> type) throws QueryException {
		try {
			Object parsed = switch (literal.getKind()) {
			case DATE -> LocalDate.parse(literal.getValue());
			case TIME -> LocalTime.parse(literal.getValue());
			case DATE_TIME -> LocalDateTime.parse(literal.getValue());
			case INSTANT -> Instant.parse(literal.getValue());
			};
			return adaptTemporal(parsed, type);
		} catch (DateTimeParseException e) {
			throw new QueryException("Cannot parse temporal literal '" + literal.getValue() + "' as "
					+ literal.getKind() + ": " + e.getMessage(), e);
		}
	}

	private static Object adaptTemporal(Object parsed, Class<?> type) {
		if (type == null || type.isInstance(parsed)) {
			return parsed;
		}
		Instant instant = switch (parsed) {
		case Instant i -> i;
		case LocalDate d -> d.atStartOfDay(ZoneOffset.UTC).toInstant();
		case LocalDateTime dt -> dt.toInstant(ZoneOffset.UTC);
		default -> null;
		};
		if (instant == null) {
			return parsed;
		}
		if (type == Date.class) {
			return Date.from(instant);
		}
		if (type == Instant.class) {
			return instant;
		}
		if (type == Long.class || type == long.class) {
			return instant.toEpochMilli();
		}
		return parsed;
	}
}
