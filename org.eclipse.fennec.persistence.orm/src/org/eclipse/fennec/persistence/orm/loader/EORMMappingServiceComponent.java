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
package org.eclipse.fennec.persistence.orm.loader;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.ORMConstants;
import org.eclipse.fennec.persistence.orm.helper.EORMModelHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Component to load an register a {@link EntityMappings} as a service
 * @author Mark Hoffmann
 * @since 10.01.2025
 */
@Component(name = ORMConstants.ORM_LOADER_SERVICE_PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class EORMMappingServiceComponent {
	
	@ObjectClassDefinition
	public @interface EORMMappingConfig {
		public static final String PREFIX_ = ORMConstants.PROPERTY_PREFIX;
		@AttributeDefinition(name = "Entity Mapping Name", description="Mendatory Entity Mapping name")
		String name();
		@AttributeDefinition(name = "Entity Mapping resource Uri", description="Mendatory Entity Mapping resource uri")
		String uri();
	}
	
	@Reference(target = ORMConstants.EPERSISTENCE_MODEL_TARGET)
	private ResourceSet resourceSet;
	@Reference(name = "fennec.jpa.eorm.model")
	private EPackage modelPackage;
	private ServiceRegistration<EntityMappings> serviceRegistration;
	
	@Activate
	public void activate(EORMMappingConfig config, BundleContext context) throws ConfigurationException {
		if (isNull(config.name())) {
			throw new ConfigurationException("EORM Name", String.format("No Entity Mapping name / persistence unit name was provided"));
		}
		if (isNull(config.uri())) {
			throw new ConfigurationException("EORM resource uri", String.format("No Entity Mapping resource uri was provided"));
		}
		try {
			EORMModelHelper modelHelper = new EORMModelHelper(resourceSet);
			final EntityMappings mapping = modelHelper.loadMapping(config.uri());
			Map<String, Object> properties = Map.of("fennec.jpa.orm.mapping.name", config.name());
			serviceRegistration = context.registerService(EntityMappings.class, new PrototypeServiceFactory<EntityMappings>() {

				@Override
				public EntityMappings getService(Bundle bundle, ServiceRegistration<EntityMappings> registration) {
					return EcoreUtil.copy(mapping);
				}

				@Override
				public void ungetService(Bundle bundle, ServiceRegistration<EntityMappings> registration,
						EntityMappings service) {
					// no unget needed
				}
			}, FrameworkUtil.asDictionary(properties));
		} catch (Exception e) {
			throw new ConfigurationException("EORM EntityMappings", String.format("The Entity Mapping from this uri '%s' cannot be loaded", config.uri()), e);
		}
	}
	
	@Deactivate
	public void deactivate() {
		if (nonNull(serviceRegistration)) {
			serviceRegistration.unregister();
		}
	}

}
