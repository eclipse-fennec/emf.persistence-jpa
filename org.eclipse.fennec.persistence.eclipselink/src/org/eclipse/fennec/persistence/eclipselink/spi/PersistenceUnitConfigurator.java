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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.eclipselink.spi.EntityManagerFactoryConfigurator.Builder;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.helper.EORMModelHelper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
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

import jakarta.persistence.EntityManagerFactory;

/**
 * Configurator component 
 * @author Mark Hoffmann
 * @since 10.12.2024
 */
@Designate(factory = true, ocd = PersistenceUnitConfigurator.PUConfig.class)
@Component(name = PersistenceUnitConfigurator.PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class PersistenceUnitConfigurator {

	private static final Logger LOG = Logger.getLogger(PersistenceUnitConfigurator.class.getName());

	public static final String PID = "fennec.jpa.PersistenceUnit";
	public static final String EPERSISTENCE_MODEL_TARGET = "(&(" + EMFNamespaces.EMF_NAME + "=" + EPersistencePackage.eNAME + ")(" + EMFNamespaces.EMF_NAME + "=" + EORMPackage.eNAME + "))";
	public static final String PROPERTY_PREFIX = "fennec.jpa.";
	public static final String PROPERTY_PREFIX_EXT = PROPERTY_PREFIX + "ext.";

	@Reference(target = EPERSISTENCE_MODEL_TARGET)
	private ResourceSet resourceSet;
    @Reference(name = "fennec.jpa.dataSource")
    private DataSource dataSource;
    @Reference(name = "fennec.jpa.model")
    private EPackage modelPackage;
	@Reference(name = "fennec.jpa.converter")
	private ConverterService converter;
	private EORMModelHelper modelHelper;
	private ServiceRegistration<EntityManagerFactory> emfRegistration;
	private EntityManagerFactory emf;

	@ObjectClassDefinition
	public @interface PUConfig {

		public static final String PREFIX_ = PROPERTY_PREFIX;

		@AttributeDefinition(name = "Persistence unit file", description="Optional attribute, if a single mapping file is given")
		String persistenceUnitFile();

		@AttributeDefinition(name = "Entity mapping file")
		String mappingFile();

		@AttributeDefinition(name = "Persistence unit name", description = "Only needed, if no persistence unit file is given")
		String persistenceUnitName();

	}

	@Activate
	void activate(BundleContext bctx, PUConfig config, Map<String, Object> properties)
			throws IOException, ConfigurationException {
		modelHelper = new EORMModelHelper(resourceSet);
		EPersistenceContextImpl pctx = createPersistenceContext(config);
		
		URL url = bctx.getBundle().getEntry("META-INF/persistence.xml");
		pctx.setMetadataURL(url);
		// Forward prefixed properties
		Map<String, Object> emfProperties = createForwardedProperties(properties);
		setEMFProperties(emfProperties);
		
		try {
			Builder configBuilder = Builder.
					create(bctx, resourceSet).
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
			}).onFailure(t -> LOG.log(Level.SEVERE, "Failed to create EntityManagerFactory", t));

		} catch (Exception e) {
			throw new IllegalStateException("Error configuring persistence unit", e);
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
		if (nonNull(config.persistenceUnitFile())) {
			try {
				PersistenceUnit pu = modelHelper.loadPersistenceUnit(config.persistenceUnitFile());
				pctx = new EPersistenceContextImpl(pu);
			} catch (Exception e) {
				throw new ConfigurationException("persistenceUnitFile", String.format("The file from this uri '%s' cannot be loaded", config.persistenceUnitFile()), e);
			}
		} else {
			if (isNull(config.persistenceUnitName())) {
				throw new ConfigurationException("persistenceUnitName", String.format("No persistence unit name was provided"));
			}
			if (isNull(config.mappingFile())) {
				throw new ConfigurationException("mappingFile", String.format("No mapping file path was provided"));
			}
			try {
				EntityMappings mapping = modelHelper.loadMapping(config.mappingFile());
				pctx = new EPersistenceContextImpl(config.persistenceUnitName(), Collections.singletonList(mapping));
			} catch (Exception e) {
				throw new ConfigurationException("mappingFile", String.format("The file from this uri '%s' cannot be loaded", config.mappingFile()), e);
			}
		}
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
		properties.put(PersistenceUnitProperties.TARGET_DATABASE, H2Platform.class.getName());
		properties.put(PersistenceUnitProperties.THROW_EXCEPTIONS, "true");
		properties.put(PersistenceUnitProperties.CONNECTION_POOL_MIN, 1);
//		properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
		properties.put("eclipselink.logging.level", "FINE");
		properties.put("eclipselink.logging.timestamp", "false");
		properties.put("eclipselink.logging.thread", "false");
		properties.put("eclipselink.logging.exceptions", "true");
//        properties.put("eclipselink.ddl-generation.output-mode", "database");
//        properties.put("eclipselink.ddl-generation", "none");
//        properties.put("eclipselink.cache.shared.default", "false");
	}

}
