/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.converter;

import static java.util.Objects.nonNull;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import aQute.bnd.annotation.service.ServiceCapability;

/**
 * Converter whiteboard to register the {@link ConverterService}. The service properties are updated, if a new converter is registered or removed
 * @author Mmark Hoffmann
 * @since 14.01.2025
 */
@Component(immediate = true)
@ServiceCapability(ConverterService.class)
public class ConverterWhiteboard extends DefaultConverterService {

	private ServiceRegistration<ConverterService> converterRegistration = null;

	@Activate
	public void activate(BundleContext ctx) {
		converterRegistration = ctx.registerService(ConverterService.class, this, getServiceProperties());
	}

	@Deactivate
	public void deactivate() {
		if (nonNull(converterRegistration)) {
			converterRegistration.unregister();
		}
	}

	/**
	 * Adds the converter and makes it available for consideration when serializing and de-serializing an object.
	 * Converters are considered in the order in which they are added with the last one added being first. The
	 * default converter is added by the constructor and will therefore be considered last. The first converter
	 * where isConverterForType() returns true is the one used to convert the value.
	 * 
	 * @param converter the converter to add
	 */
	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void addConverter(TypeConverter converter) {
		synchronized (converters) {
			converters.add(converter);
		}
		updateServiceProperties();
	}

	/**
	 * Removes the converter and the converter will no longer be considered during serialization and de-serialization of an object.
	 * 
	 * @param converter the converter to remove
	 */
	public void removeConverter(TypeConverter converter) {
		synchronized (converters) {
			if (converters.remove(converter)) {
				updateServiceProperties();
			}
		}
	}

	/**
	 * Updates the converter service properties
	 */
	private void updateServiceProperties() {
		if (nonNull(converterRegistration)) {
			converterRegistration.setProperties(getServiceProperties());
		}
	}

	/**
	 * Build the current service properties
	 * @return the service properties
	 */
	private Dictionary<String, Object> getServiceProperties() {
		Dictionary<String, Object> props = new Hashtable<>();
		List<String> names;
		synchronized (converters) {
			names = converters.stream().
					map(TypeConverter::getName).
					filter(Objects::nonNull).
					toList();
		}
		props.put(PROP_CONVERTER_NAME, names);
		return props;
	}

}
