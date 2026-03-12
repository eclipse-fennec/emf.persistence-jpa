/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   SmartCity Jena - initial
 *   Stefan Bischof (bipolis.org) - initial
 */
package org.eclipse.fennec.persistence.eclipselink.spi;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.eclipselink.spi.EntityManagerFactoryConfigurator.Builder;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetDatabase;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.util.promise.PromiseFactory;

import aQute.bnd.annotation.service.ServiceCapability;
import jakarta.persistence.EntityManagerFactory;

/**
 * Configurator component 
 * @author Mark Hoffmann
 * @since 10.12.2024
 */
@Designate(factory = true, ocd = EntityMappingPersistenceUnitConfigurator.PUConfig.class)
@Component(name = EntityMappingPersistenceUnitConfigurator.PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
@ServiceCapability(EntityManagerFactory.class)
@ServiceCapability(EntityMappings.class)
public class EntityMappingPersistenceUnitConfigurator {

	public static final String PID = "fennec.jpa.EMPersistenceUnit";
	public static final String PROPERTY_PREFIX = "fennec.jpa.";
	public static final String PROPERTY_PREFIX_EXT = PROPERTY_PREFIX + "ext.";

	@Reference(name = "fennec.jpa.dataSource")
	private DataSource dataSource;
	@Reference(name = "fennec.jpa.mapping")
	private EntityMappings mappings;
	@Reference(name = "fennec.jpa.converter")
	private ConverterService converter;
	private ServiceRegistration<EntityManagerFactory> emfRegistration;
	private EntityManagerFactory emf;

	@ObjectClassDefinition
	public @interface PUConfig {

		public static final String PREFIX_ = PROPERTY_PREFIX;

		@AttributeDefinition(name = "Persistence unit name", description = "Only needed, if no persistence unit file is given")
		String persistenceUnitName();

	}

	@Activate
	void activate(BundleContext bctx, PUConfig config, Map<String, Object> properties)
			throws IOException, ConfigurationException {
		EPersistenceContextImpl pctx = createPersistenceContext(config);

		URL url = bctx.getBundle().getEntry("META-INF/persistence.xml");
		pctx.setMetadataURL(url);
		// Forward prefixed properties
		Map<String, Object> emfProperties = createForwardedProperties(properties);
		setEMFProperties(emfProperties);

		try {
			Builder configBuilder = Builder.
					create(bctx, null).
					dataSource(dataSource).
					context(pctx).
					converter(converter).
					properties(emfProperties);

			PromiseFactory pf = new PromiseFactory(Executors.newSingleThreadExecutor());
			pf.submit(()->{
				emf = configBuilder.build().configure();
				Dictionary<String, Object> entityMapperProps = new Hashtable<>();
				entityMapperProps.put("osgi.unit.name", pctx.getPersistenceUnitName());
				entityMapperProps.put("osgi.unit.version", bctx.getBundle().getVersion().toString());
				entityMapperProps.put("osgi.unit.provider", PersistenceProvider.class.getName());
				emfRegistration = bctx.registerService(EntityManagerFactory.class, emf, entityMapperProps);
				return emfRegistration;
			}).onFailure(Throwable::printStackTrace);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Deactivate
	void deactivate() {
		if (nonNull(emfRegistration)) {
			emfRegistration.unregister();
			emf.close();
		}
	}

	/**
	 * Validates the configration and creates a {@link EPersistenceContextImpl} out of it.
	 * @param config the configuration
	 * @return the {@link EPersistenceContextImpl}
	 * @throws ConfigurationException thrown, if an invalid configuration was provided
	 */
	private EPersistenceContextImpl createPersistenceContext(PUConfig config) throws ConfigurationException {
		EPersistenceContextImpl pctx;
		if (isNull(config.persistenceUnitName())) {
			throw new ConfigurationException("persistenceUnitName", String.format("No persistence unit name was provided"));
		}
		pctx = new EPersistenceContextImpl(config.persistenceUnitName(), Collections.singletonList(mappings));
		return pctx;
	}

	/**
	 * Takes the configuration properties and extracts forwarded properties and puts
	 * them in an own property map
	 * @param properties the configuration properties
	 * @return a new property map, with the replaced properties
	 */
	private Map<String, Object> createForwardedProperties(Map<String, Object> properties) {
		Map<String, Object> emfProperties = new HashMap<>();
		properties.forEach((k,v)->{
			if (k.startsWith(PROPERTY_PREFIX_EXT)) {
				emfProperties.put(k.replace(PROPERTY_PREFIX_EXT, ""), v);
			}
		});
		return emfProperties;
	}

	/**
	 * Sets some connection and {@link EntityManagerFactory} properties
	 * @param properties the properties map
	 */
	private void setEMFProperties(Map<String, Object> properties) {
		requireNonNull(properties);
		//		properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		properties.put(PersistenceUnitProperties.WEAVING, "static");
		properties.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
		properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dataSource);
		if (!properties.containsKey(PersistenceUnitProperties.TARGET_DATABASE)) {
			properties.put(PersistenceUnitProperties.TARGET_DATABASE, TargetDatabase.Auto);
		}
		properties.put(PersistenceUnitProperties.THROW_EXCEPTIONS, "true");
		properties.put(PersistenceUnitProperties.CONNECTION_POOL_MIN, 1);
		if (!properties.containsKey(PersistenceUnitProperties.DDL_GENERATION)) {
			properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
		}
		// properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
		properties.put("eclipselink.logging.level", "FINE");
		properties.put("eclipselink.logging.timestamp", "false");
		properties.put("eclipselink.logging.thread", "false");
		properties.put("eclipselink.logging.exceptions", "true");
		// properties.put("eclipselink.ddl-generation.output-mode", "database");
		// properties.put("eclipselink.ddl-generation", "none");
		// properties.put("eclipselink.cache.shared.default", "false");
	}

}
