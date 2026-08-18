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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.eclipse.fennec.persistence.eclipselink.mappings.EFeatureAccessor;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.Convert;
import org.eclipse.fennec.persistence.eorm.EFeatureObject;
import org.eclipse.fennec.persistence.eorm.Version;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

/**
 * Configures @Basic attribute mappings and @Version optimistic locking
 * on EclipseLink descriptors. Handles type mapping, converter detection,
 * and EFeatureAccessor setup.
 *
 * @author Mark Hoffmann
 * @since 15.04.2026
 */
class AttributeConfigurator {

	private static final Logger LOG = Logger.getLogger(AttributeConfigurator.class.getName());

	/**
	 * DDL defaults for {@code BigDecimal} columns without explicit EORM facets: 38 total
	 * digits (the common NUMERIC maximum), 19 of them fraction digits — a symmetric split
	 * that keeps typical monetary/measurement values lossless. Override per attribute via
	 * an EORM {@code Column} with precision/scale.
	 */
	static final int DEFAULT_DECIMAL_PRECISION = 38;
	static final int DEFAULT_DECIMAL_SCALE = 19;

	private final BuilderOperations ops;
	private final EDynamicTypeContext context;

	AttributeConfigurator(BuilderOperations ops, EDynamicTypeContext context) {
		this.ops = ops;
		this.context = context;
	}

	/**
	 * Configures {@link Basic} (single value) mappings.
	 */
	void configureSingleAttributes(EDynamicType eType, List<Basic> basics) {
		if (isNull(basics) || basics.isEmpty()) {
			return;
		}
		basics.forEach(this::processBasic);
	}

	/**
	 * Configures @Version attributes for optimistic locking.
	 * Patches the lock mapping accessor to use EFeatureAccessor instead of
	 * EclipseLink's default ValuesAccessor (which casts to DynamicEntityImpl).
	 */
	void configureVersionAttributes(List<Version> versions) {
		if (isNull(versions) || versions.isEmpty()) {
			return;
		}
		EClass eClass = ops.getType().getEClass();
		for (Version version : versions) {
			Column col = version.getColumn();
			String colName = nonNull(col) ? col.getName() : version.getName().toUpperCase();
			// Determine the Java type from the EStructuralFeature (EInt→int, ELong→long, etc.)
			EStructuralFeature feature = eClass.getEStructuralFeature(version.getName());
			Class<?> versionType = Long.class;
			if (feature instanceof EAttribute ea) {
				Class<?> instanceClass = ea.getEAttributeType().getInstanceClass();
				if (nonNull(instanceClass)) {
					versionType = instanceClass;
				}
			}
			// Add a direct mapping for the version field
			ops.addDirectMapping(version.getName(), versionType, colName);
			// Configure EclipseLink optimistic locking on the descriptor
			ClassDescriptor descriptor = ops.getType().getDescriptor();
			descriptor.useVersionLocking(colName, false);
			// Patch the lock mapping's accessor: EclipseLink's default ValuesAccessor
			// casts to DynamicEntityImpl which is incompatible with our DynamicEObjectImpl.
			// Replace it with EFeatureAccessor that uses EObject.eGet()/eSet().
			DatabaseMapping lockMapping = descriptor.getMappingForAttributeName(version.getName());
			if (nonNull(lockMapping) && nonNull(feature)) {
				lockMapping.setAttributeAccessor(EFeatureAccessor.create(lockMapping, feature));
				LOG.log(Level.FINE, "Patched version lock mapping accessor for {0} to EFeatureAccessor", version.getName());
			}
		}
	}

	private void processBasic(Basic basic) {
		if (isNull(basic)) {
			return;
		}
		EFeatureObject efo = (EFeatureObject) basic.getAccessibleObject();
		EStructuralFeature feature = efo.getFeature();
		Class<?> typeClass;
		if (feature instanceof EReference) {
			typeClass = String.class;
		} else {
			typeClass = columnType((EAttribute) efo.getFeature());
		}
		Column c = basic.getColumn();
		String colName = nonNull(c) ? c.getName() : basic.getName();
		DirectToFieldMapping mapping = ops.addDirectMapping(basic.getName(), typeClass, colName);
		mapping.setIsLazy(false);
		mapping.setIsOptional(basic.isOptional());
		if (nonNull(c)) {
			mapping.setIsMutable(c.isSetUpdatable());
		}
		if (typeClass == BigDecimal.class) {
			// Without an explicit precision/scale the generated DDL falls back to the DB
			// default for NUMERIC — scale 0 on H2 and most databases — silently rounding
			// fraction digits away on insert. Explicit EORM column facets win; otherwise a
			// documented default keeps decimals lossless for typical value ranges.
			mapping.getField().setPrecision(
					nonNull(c) && c.isSetPrecision() ? c.getPrecision() : DEFAULT_DECIMAL_PRECISION);
			mapping.getField().setScale(
					nonNull(c) && c.isSetScale() ? c.getScale() : DEFAULT_DECIMAL_SCALE);
		}
		/**
		 * Converter handling - both explicit and automatic
		 */
		TypeConverter converter = null;

		// First check for explicit converter configuration
		Convert convert = basic.getConvert();
		if (nonNull(convert) && nonNull(convert.getConverter())) {
			String name = convert.getConverter();
			converter = context.getConverter(name);
			// the service answers absence with null (issue #164); an explicitly configured
			// name that resolves to nothing is a mapping error and must not fall through
			// to an unconverted column silently
			if (isNull(converter)) {
				throw new IllegalStateException("The eorm mapping of '" + feature.getName()
						+ "' names converter '" + name + "', but no such converter is registered");
			}
		}
		// If no explicit converter, try automatic detection for non-standard database types
		else if (feature instanceof EAttribute ea) {
			Class<?> originalTypeClass = ea.getEAttributeType().getInstanceClass();
			if (nonNull(originalTypeClass) && !isStandardDatabaseType(originalTypeClass)) {
				try {
					LOG.log(Level.FINER, "Trying to find converter for {0} (instanceClass: {1})",
						new Object[]{ea.getEAttributeType().getName(), originalTypeClass});
					converter = context.getConverter(ea.getEAttributeType());
					if (nonNull(converter)) {
						LOG.log(Level.FINER, "Found converter: {0} for {1}",
							new Object[]{converter.getName(), ea.getEAttributeType().getName()});
					} else {
						LOG.log(Level.FINER, "No converter found for {0}", ea.getEAttributeType().getName());
					}
				} catch (Exception e) {
					LOG.log(Level.WARNING, e, () -> "Exception finding converter for " + ea.getEAttributeType().getName());
				}
			}
		}

		EFeatureAccessor efa = EFeatureAccessor.create(mapping, feature, converter);
		mapping.setAttributeAccessor(efa);
	}

	/**
	 * Check if the given class is a standard database type that doesn't need conversion.
	 */
	/**
	 * The Java type an attribute's column is mapped with — the whole decision in one place, so it
	 * can be asserted without building a persistence unit.
	 * <p>
	 * Enums become Strings ({@code EnumType.STRING} is the default strategy) and arrays become
	 * {@code byte[]}, since an array has no standard SQL column type.
	 * <p>
	 * {@code java.util.Date} is normalised to {@link Timestamp} (issue #157). It looks like a type
	 * every platform handles — the base {@code DatabasePlatform} and {@code H2Platform} both map it
	 * to {@code TIMESTAMP} — but {@code PostgreSQLPlatform} rebuilds the field-type table and lists
	 * only {@code java.sql.Date}, so the column silently degraded to {@code VARCHAR} there and
	 * every temporal function failed on it. {@code Timestamp} is mapped by every platform and is
	 * what an {@code EDate} value carries anyway, which is the same normalisation the
	 * {@code java.time} types already get.
	 *
	 * @param ea the attribute to map
	 * @return the Java type to hand to {@code addDirectMapping}, never {@code null}
	 */
	Class<?> columnType(EAttribute ea) {
		Class<?> typeClass = ea.getEAttributeType().getInstanceClass();
		LOG.log(Level.FINER, "[columnType] Processing {0} with original typeClass: {1}",
				new Object[] { ea.getName(), typeClass });
		if (ea.getEAttributeType() instanceof EEnum) {
			typeClass = String.class;
			LOG.log(Level.FINER, "[columnType] {0} is enum, mapping as String (EnumType.STRING)", ea.getName());
		} else if (nonNull(typeClass) && typeClass.isArray()) {
			typeClass = byte[].class;
			LOG.log(Level.FINER, "[columnType] {0} is array type, mapping as byte[] (BLOB)", ea.getName());
		} else if (typeClass == java.util.Date.class) {
			typeClass = Timestamp.class;
			LOG.log(Level.FINER, "[columnType] {0} is java.util.Date, mapping as Timestamp — not every"
					+ " platform maps java.util.Date (issue #157)", ea.getName());
		} else if (nonNull(typeClass) && !isStandardDatabaseType(typeClass)) {
			typeClass = mapToDbFriendlyType(typeClass);
			LOG.log(Level.FINER, "[columnType] {0} mapped to DB-friendly type: {1}",
					new Object[] { ea.getName(), typeClass });
		}
		if (isNull(typeClass)) {
			// a custom data type without an instance class — a string is the only safe carrier
			typeClass = String.class;
			LOG.log(Level.FINER, "[columnType] {0} has null typeClass, setting to String.class", ea.getName());
		}
		LOG.log(Level.FINER, "[columnType] Final typeClass for {0}: {1}",
				new Object[] { ea.getName(), typeClass });
		return typeClass;
	}

	boolean isStandardDatabaseType(Class<?> clazz) {
		if (isNull(clazz)) {
			return true;
		}
		if (clazz.isPrimitive() ||
			clazz == String.class ||
			clazz == Integer.class || clazz == Long.class || clazz == Double.class || clazz == Float.class ||
			clazz == Short.class || clazz == Byte.class || clazz == Character.class || clazz == Boolean.class ||
			clazz == java.sql.Date.class || clazz == Time.class || clazz == Timestamp.class ||
			clazz == java.util.Date.class ||
			clazz == byte[].class) {
			return true;
		}
		return false;
	}

	/**
	 * Maps a non-standard Java type to a DB-friendly type that EclipseLink can persist natively.
	 */
	Class<?> mapToDbFriendlyType(Class<?> clazz) {
		if (clazz == Instant.class ||
			clazz == LocalDateTime.class ||
			clazz == LocalDate.class ||
			clazz == LocalTime.class) {
			return Timestamp.class;
		}
		if (clazz == ZonedDateTime.class || clazz == OffsetDateTime.class) {
			return String.class;
		}
		if (clazz == Duration.class) {
			return Long.class;
		}
		if (clazz == UUID.class) {
			return String.class;
		}
		if (clazz == BigDecimal.class || clazz == BigInteger.class) {
			return clazz;
		}
		return String.class;
	}
}
