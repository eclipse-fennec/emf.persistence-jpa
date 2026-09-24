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
package org.eclipse.fennec.persistence.orm.processor;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.Keywords;
import org.eclipse.fennec.persistence.eorm.Base;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.FetchType;
import org.eclipse.fennec.persistence.eorm.SimpleBase;
import org.eclipse.fennec.persistence.eorm.TemporalType;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;

/**
 * Processor for {@link SimpleBase} elements
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public abstract class BaseProcessor<T extends SimpleBase> extends NamedBaseProcessor<T, EAttribute> {

	/**
	 * Creates a new instance.
	 * @param feature
	 * @param helper
	 */
	public BaseProcessor(EAttribute feature, MappingContext helper) {
		super(feature, helper);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#internalProcess()
	 */
	@Override
	protected boolean internalProcess() {
		createNamedBase();
		createBase();
		createSimpleBase();
		applyAnnotatedColumnFacets();
		return true;
	}
	
	/**
	 * Creates a {@link Base} out of an {@link EStructuralFeature}
	 * @param <T> the base type
	 * @return the {@link Base} instance
	 */
	T createBase() {
		return MappingHelper.createBase(target, source, isStrict(), context);
	}
	
	/**
	 * Creates a {@link SimpleBase} out of an {@link EAttribute}
	 * @param <T> the simple base extended type
	 * @return the {@link SimpleBase} instance
	 */
	T createSimpleBase() {
		target.setFetch(FetchType.EAGER);
		EDataType type = source.getEAttributeType();
		if (type instanceof EEnum eEnum) {
			target.setEnumerated(eEnum);
		}
		Class<?> typeClass = type.getInstanceClass();
		if (nonNull(typeClass)) {
			if (type.getInstanceClass().isAssignableFrom(Date.class) ||
					type.getInstanceClass().isAssignableFrom(java.util.Date.class) ||
					type.getInstanceClass().isAssignableFrom(LocalDateTime.class) ||
					type.getInstanceClass().isAssignableFrom(Instant.class)) {
				target.setTemporal(TemporalType.TIMESTAMP);
			}
			if (type.getInstanceClass().isAssignableFrom(Calendar.class) || 
					type.getInstanceClass().isAssignableFrom(LocalDate.class)) {
				target.setTemporal(TemporalType.DATE);
			}
			if (type.getInstanceClass().isAssignableFrom(LocalTime.class)) {
				target.setTemporal(TemporalType.TIME);
			}
		}
		return target;
	}
	
	/**
	 * Writes the column facets the persistence annotation of the attribute declares into the
	 * eorm (issue #319), so the eorm stays the single source the type mapping reads:
	 * <ul>
	 * <li>{@code length} = a positive integer → {@code Column.length}</li>
	 * <li>{@code columnDefinition} = a column definition → {@code Column.columnDefinition}</li>
	 * <li>{@code lob} = {@code true} → {@code Lob}</li>
	 * </ul>
	 * A value that cannot be used is reported as a warning and left out, never replaced by a
	 * guess.
	 */
	void applyAnnotatedColumnFacets() {
		EAnnotation annotation = source.getEAnnotation(Keywords.PERSISTENCE_ANNOTATION_SOURCE);
		if (isNull(annotation)) {
			return;
		}
		Column column = target.getColumn();
		String length = annotation.getDetails().get("length");
		if (nonNull(length) && nonNull(column)) {
			try {
				int value = Integer.parseInt(length.trim());
				if (value <= 0) {
					throw new NumberFormatException();
				}
				column.setLength(value);
			} catch (NumberFormatException e) {
				context.warning(MappingContext.DIAGNOSTIC_SOURCE, String.format(
						"Persistence annotation 'length' of '%s' must be a positive integer, but is '%s' — ignored",
						source.getName(), length), source);
			}
		}
		String columnDefinition = annotation.getDetails().get("columnDefinition");
		if (nonNull(columnDefinition) && !columnDefinition.isBlank() && nonNull(column)) {
			column.setColumnDefinition(columnDefinition);
		}
		String lob = annotation.getDetails().get("lob");
		if ("true".equalsIgnoreCase(lob)) {
			target.setLob(EORMFactory.eINSTANCE.createLob());
		} else if (nonNull(lob) && !"false".equalsIgnoreCase(lob)) {
			context.warning(MappingContext.DIAGNOSTIC_SOURCE, String.format(
					"Persistence annotation 'lob' of '%s' must be 'true' or 'false', but is '%s' — ignored",
					source.getName(), lob), source);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#doPostProcess()
	 */
	void doPostProcess() {
		// Nothing to do here
	}

}
