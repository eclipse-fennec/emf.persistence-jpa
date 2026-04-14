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

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.eclipselink.spi.EntityManagerFactoryConfigurator.Builder;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetDatabase;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.promise.PromiseFactory;

import jakarta.persistence.EntityManagerFactory;

/**
 * Base class for persistence unit configurators. Extracts the common lifecycle,
 * property handling and EMF registration logic shared by all configurator variants.
 *
 * @author Mark Hoffmann
 * @since 14.04.2026
 */
public abstract class AbstractPersistenceUnitConfigurator {

	public static final String PROPERTY_PREFIX = "fennec.jpa.";
	public static final String PROPERTY_PREFIX_EXT = PROPERTY_PREFIX + "ext.";

	private volatile ServiceRegistration<EntityManagerFactory> emfRegistration;
	private volatile EntityManagerFactory emf;
	private ExecutorService executor;

	/**
	 * Returns the logger for the concrete subclass.
	 */
	protected abstract Logger getLogger();

	/**
	 * Returns the DataSource to use for the persistence unit.
	 */
	protected abstract DataSource getDataSource();

	/**
	 * Returns the ConverterService to use for type conversion.
	 */
	protected abstract ConverterService getConverterService();

	/**
	 * Builds the configurator for the EntityManagerFactory. Subclasses provide
	 * the persistence context and any additional builder configuration.
	 *
	 * @param bctx the bundle context
	 * @param emfProperties the forwarded properties
	 * @return the configured builder
	 */
	protected abstract Builder createConfigBuilder(BundleContext bctx, Map<String, Object> emfProperties);

	/**
	 * Returns the persistence unit name for service registration.
	 */
	protected abstract String getPersistenceUnitName();

	/**
	 * Common activation logic: forwards properties, sets EMF defaults,
	 * creates the EntityManagerFactory asynchronously and registers it as OSGi service.
	 */
	protected void doActivate(BundleContext bctx, Map<String, Object> properties) {
		Map<String, Object> emfProperties = createForwardedProperties(properties);
		setEMFProperties(emfProperties);

		try {
			Builder configBuilder = createConfigBuilder(bctx, emfProperties);

			executor = Executors.newSingleThreadExecutor();
			PromiseFactory pf = new PromiseFactory(executor);
			pf.submit(() -> {
				emf = configBuilder.build().configure();
				Dictionary<String, Object> serviceProps = new Hashtable<>();
				serviceProps.put("osgi.unit.name", getPersistenceUnitName());
				serviceProps.put("osgi.unit.version", bctx.getBundle().getVersion().toString());
				serviceProps.put("osgi.unit.provider", PersistenceProvider.class.getName());
				emfRegistration = bctx.registerService(EntityManagerFactory.class, emf, serviceProps);
				return emfRegistration;
			}).onFailure(t -> getLogger().log(Level.SEVERE, "Failed to create EntityManagerFactory", t));

		} catch (Exception e) {
			throw new IllegalStateException("Error configuring persistence unit", e);
		}
	}

	/**
	 * Common deactivation logic: shuts down executor, unregisters service, closes EMF.
	 */
	protected void doDeactivate() {
		if (nonNull(executor)) {
			executor.shutdownNow();
			try {
				executor.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		if (nonNull(emfRegistration)) {
			emfRegistration.unregister();
		}
		if (nonNull(emf) && emf.isOpen()) {
			emf.close();
		}
	}

	/**
	 * Extracts forwarded properties (prefixed with {@code fennec.jpa.ext.}) from
	 * the configuration and strips the prefix.
	 */
	protected Map<String, Object> createForwardedProperties(Map<String, Object> properties) {
		Map<String, Object> emfProperties = new HashMap<>();
		properties.forEach((k, v) -> {
			if (k.startsWith(PROPERTY_PREFIX_EXT)) {
				emfProperties.put(k.replace(PROPERTY_PREFIX_EXT, ""), v);
			}
		});
		return emfProperties;
	}

	/**
	 * Sets default EclipseLink/JPA properties. Subclasses may override to customize.
	 */
	protected void setEMFProperties(Map<String, Object> properties) {
		requireNonNull(properties);
		properties.put(PersistenceUnitProperties.WEAVING, "false");
		properties.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
		properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, getDataSource());
		if (!properties.containsKey(PersistenceUnitProperties.TARGET_DATABASE)) {
			properties.put(PersistenceUnitProperties.TARGET_DATABASE, TargetDatabase.Auto);
		}
		properties.put(PersistenceUnitProperties.THROW_EXCEPTIONS, "true");
		properties.put(PersistenceUnitProperties.CONNECTION_POOL_MIN, 1);
		if (!properties.containsKey(PersistenceUnitProperties.DDL_GENERATION)) {
			properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
		}
		properties.put("eclipselink.logging.level", "WARNING");
		properties.put("eclipselink.logging.timestamp", "false");
		properties.put("eclipselink.logging.thread", "false");
		properties.put("eclipselink.logging.exceptions", "true");
	}

	/**
	 * Resolves the persistence.xml metadata URL from the bundle.
	 */
	protected URL getMetadataURL(BundleContext bctx) {
		return bctx.getBundle().getEntry("META-INF/persistence.xml");
	}
}
