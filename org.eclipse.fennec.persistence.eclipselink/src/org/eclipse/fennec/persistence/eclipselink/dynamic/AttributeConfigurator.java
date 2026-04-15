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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
				lockMapping.setAttributeAccessor(EFeatureAccessor.create(feature));
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
			EAttribute ea = (EAttribute) efo.getFeature();
			Class<?> originalTypeClass = ea.getEAttributeType().getInstanceClass();
			typeClass = originalTypeClass;
			LOG.log(Level.FINER, "[processBasic] Processing {0} with original typeClass: {1}", new Object[]{ea.getName(), originalTypeClass});

			// Map enums into Strings (EnumType.STRING is the default strategy)
			if (ea.getEAttributeType() instanceof EEnum) {
				typeClass = String.class;
				LOG.log(Level.FINER, "[processBasic] {0} is enum, mapping as String (EnumType.STRING)", ea.getName());
			}
			// Map array types as byte[] (BLOB) — arrays have no standard SQL column type
			else if (typeClass != null && typeClass.isArray()) {
				typeClass = byte[].class;
				LOG.log(Level.FINER, "[processBasic] {0} is array type, mapping as byte[] (BLOB)", ea.getName());
			}
			// Map non-standard types to their DB-friendly equivalent
			else if (typeClass != null && !isStandardDatabaseType(typeClass)) {
				typeClass = mapToDbFriendlyType(typeClass);
				LOG.log(Level.FINER, "[processBasic] {0} mapped to DB-friendly type: {1}", new Object[]{ea.getName(), typeClass});
			}
			// Fallback to String for custom types that don't have instance classes
			if (typeClass == null) {
				typeClass = String.class;
				LOG.log(Level.FINER, "[processBasic] {0} has null typeClass, setting to String.class", ea.getName());
			}

			LOG.log(Level.FINER, "[processBasic] Final typeClass for {0}: {1}", new Object[]{ea.getName(), typeClass});
		}
		Column c = basic.getColumn();
		String colName = nonNull(c) ? c.getName() : basic.getName();
		DirectToFieldMapping mapping = ops.addDirectMapping(basic.getName(), typeClass, colName);
		mapping.setIsLazy(false);
		mapping.setIsOptional(basic.isOptional());
		if (nonNull(c)) {
			mapping.setIsMutable(c.isSetUpdatable());
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
		}
		// If no explicit converter, try automatic detection for non-standard database types
		else if (feature instanceof EAttribute ea) {
			Class<?> originalTypeClass = ea.getEAttributeType().getInstanceClass();
			if (originalTypeClass != null && !isStandardDatabaseType(originalTypeClass)) {
				try {
					LOG.log(Level.FINER, "Trying to find converter for {0} (instanceClass: {1})",
						new Object[]{ea.getEAttributeType().getName(), originalTypeClass});
					converter = context.getConverter(ea.getEAttributeType());
					if (converter != null) {
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

		EFeatureAccessor efa = EFeatureAccessor.create(feature, converter);
		mapping.setAttributeAccessor(efa);
	}

	/**
	 * Check if the given class is a standard database type that doesn't need conversion.
	 */
	boolean isStandardDatabaseType(Class<?> clazz) {
		if (clazz == null) {
			return true;
		}
		if (clazz.isPrimitive() ||
			clazz == String.class ||
			clazz == Integer.class || clazz == Long.class || clazz == Double.class || clazz == Float.class ||
			clazz == Short.class || clazz == Byte.class || clazz == Character.class || clazz == Boolean.class ||
			clazz == java.sql.Date.class || clazz == java.sql.Time.class || clazz == java.sql.Timestamp.class ||
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
			return java.sql.Timestamp.class;
		}
		if (clazz == ZonedDateTime.class) {
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
