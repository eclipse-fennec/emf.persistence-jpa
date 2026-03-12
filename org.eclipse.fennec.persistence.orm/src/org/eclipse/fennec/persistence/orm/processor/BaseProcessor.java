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

import static java.util.Objects.nonNull;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.eorm.Base;
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
		return true;
	}
	
	/**
	 * Creates a {@link Base} out of an {@link EStructuralFeature}
	 * @param <T> the base type
	 * @return the {@link Base} instance
	 */
	T createBase() {
		return MappingHelper.createBase(target, source, isStrict());
	}
	
	/**
	 * Creates a {@link SimpleBase} out of an {@link EAttribute}
	 * @param <T> the simple base extended type
	 * @return the {@link SimpleBase} instance
	 */
	T createSimpleBase() {
		target.setFetch(FetchType.EAGER);
		EDataType type = source.getEAttributeType();
		if (type instanceof EEnum) {
			target.setEnumerated((EEnum)source.getEAttributeType());
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
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#doPostProcess()
	 */
	void doPostProcess() {
		// Nothing to do here
	}

}
