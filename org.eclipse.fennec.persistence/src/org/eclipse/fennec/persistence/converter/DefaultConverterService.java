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
package org.eclipse.fennec.persistence.converter;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Abstract base for {@link ConverterService} implementations. Holds the list of
 * registered {@link TypeConverter}s in a {@link CopyOnWriteArrayList} — reads
 * (the hot path: resolving a converter for a datatype or name) are lock-free,
 * and the rare writes (subclasses add or remove converters at runtime) pay the
 * array-copy cost without blocking readers.
 *
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
public abstract class DefaultConverterService implements ConverterService {

	protected final CopyOnWriteArrayList<TypeConverter> converters;

	public DefaultConverterService() {
		this.converters = new CopyOnWriteArrayList<>();
		// Add comprehensive converter first for priority handling of modern types
		converters.add(new ComprehensiveTypeConverter());
		// Keep existing converters for backwards compatibility and specialized cases
		converters.add(new ArrayConverter());
		converters.add(new DefaultConverter());
		converters.add(new XMLGregorianCalendarConverter());
		converters.add(new BigDecimalConverter());
		converters.add(new BigIntegerConverter());
		converters.add(new NonContainmentConverter());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.api.ConverterService#getConverter(org.eclipse.emf.ecore.EClassifier)
	 */
	@Override
	public TypeConverter getConverter(EClassifier eDataType) {
		requireNonNull(eDataType);
		return converters.stream()
				.filter(c -> c.isConverterForType(eDataType))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"The default converter was not found - this should never happen"));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.api.ConverterService#getConverter(java.lang.String)
	 */
	@Override
	public TypeConverter getConverter(String name) {
		requireNonNull(name);
		return converters.stream()
				.filter(c -> name.equals(c.getName()))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"The default converter was not found - this should never happen"));
	}

}
