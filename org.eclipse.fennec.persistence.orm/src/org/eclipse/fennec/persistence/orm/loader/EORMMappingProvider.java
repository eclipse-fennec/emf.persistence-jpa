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
package org.eclipse.fennec.persistence.orm.loader;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EORMMappingCustomizer;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.orm.ORMConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.annotations.RequireConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * 
 * @author Mark Hoffmann
 * @since 25.09.2025
 */
@RequireConfigurationAdmin
@RequireEMF
@Designate(factory = true, ocd = EORMMappingProvider.ORMMappingConfig.class)
@Component(name=ORMConstants.ORM_MAPPING_SERVICE_PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class EORMMappingProvider {
	
	private static final Logger LOG = Logger.getLogger(EORMMappingProvider.class.getName());
	
	@ObjectClassDefinition
	public @interface ORMMappingConfig {

		public static final String PREFIX_ = ORMConstants.PROPERTY_PREFIX;

		@AttributeDefinition(name = "EClass names", description="Array of EClass names to be registered. Left out, every EClass of the referenced EPackage is mapped", required = false)
		String[] eClasses() default {};

		@AttributeDefinition(name = "Entity mapping name")
		String mappingName();
		
		@AttributeDefinition(name = "Does no reserved name checking if set to true. It takes names as they are in the Ecore / ExtendedMetaData annotation")
		boolean strict() default false;

	}
	
	@Reference(name = ORMConstants.PROPERTY_PREFIX + "customizer")
	private EORMMappingCustomizer customizer;
	@Reference(name = ORMConstants.PROPERTY_PREFIX + "model")
	private EPackage mappingEPackage;
	@Reference(target = ORMConstants.EPERSISTENCE_MODEL_TARGET)
	private ResourceSet resourceSet;
	private ServiceRegistration<EntityMappings> mappingRegistration = null;
	
	@Activate
	public void activate(BundleContext bctx, ORMMappingConfig config) {
		EntityMapper mapper = new EntityMapper();
		mapper.setStrict(config.strict());
		EntityMappings mappings = createMappings(mapper, config.eClasses());
		mappings = customizer.customizeMapping(mappings);
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(ORMConstants.PROPERTY_PREFIX + "mapping", config.mappingName());
		properties.put(ORMConstants.PROPERTY_PREFIX + "model", mappingEPackage.getName());
		mappingRegistration = bctx.registerService(EntityMappings.class, mappings, properties);
	}
	
	/**
	 * Maps the configured {@link EClass}es, or every {@link EClass} of the referenced
	 * {@link EPackage} when no name is configured at all.
	 * @param mapper the {@link EntityMapper} to map with
	 * @param eClassNames the configured EClass names, may be <code>null</code> or empty
	 * @return the {@link EntityMappings}, never <code>null</code>
	 */
	private EntityMappings createMappings(EntityMapper mapper, String[] eClassNames) {
		if (isNull(eClassNames) || eClassNames.length == 0) {
			return mapper.createMappingsFromEPackage(mappingEPackage);
		}
		List<EClassifier> eClasses = new LinkedList<>();
		for (String eClassName : eClassNames) {
			EClassifier eClassifier = mappingEPackage.getEClassifier(eClassName);
			if (eClassifier instanceof EClass eClass) {
				eClasses.add(eClass);
			} else {
				LOG.warning(() -> String.format(
						"Configured name '%s' is no EClass of EPackage '%s' and is left out of the mapping",
						eClassName, mappingEPackage.getNsURI()));
			}
		}
		return mapper.createMappings(eClasses);
	}
	
	@Deactivate
	public void deactivate() {
		if (nonNull(mappingRegistration)) {
			mappingRegistration.unregister();
		}
	}

}
