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

import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.capabilities.CapabilityDeclaration;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavor;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavorCapabilities;
import org.eclipse.fennec.persistence.eclipselink.spi.EntityManagerFactoryConfigurator.Builder;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit.Lease;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetDatabase;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import jakarta.persistence.EntityManagerFactory;

/**
 * Base class for persistence unit configurators. Extracts the common lifecycle,
 * property handling and service registration logic shared by all configurator variants.
 * <p>
 * One configuration (factory PID instance) yields one {@link JPAUnit} — the narrow,
 * lazily-managed persistence-unit capability (issue #20). Activation is cheap: the
 * expensive EclipseLink deploy is deferred until the unit is first used. Two services are
 * registered, both carrying {@code osgi.unit.name}:
 * <ul>
 * <li>{@link JPAUnit} — the primary capability, consumed by the {@code jpa://} whiteboard
 *     resource factory;</li>
 * <li>{@link EntityManagerFactory} — interop registration via an OSGi
 *     {@link ServiceFactory}: {@code getService} opens a lease on the unit and hands out
 *     the <em>real</em> EclipseLink factory (so {@code JpaHelper} casts work),
 *     {@code ungetService} closes the lease. While any consumer holds the service the
 *     unit will not idle-close.</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 14.04.2026
 */
public abstract class AbstractPersistenceUnitConfigurator {

	public static final String PROPERTY_PREFIX = "fennec.jpa.";
	public static final String PROPERTY_PREFIX_EXT = PROPERTY_PREFIX + "ext.";
	public static final String CONFIG_BATCH_WRITING = "batchWriting";
	public static final String CONFIG_BATCH_SIZE = "batchSize";
	/** Config key (unprefixed) for the idle timeout of the lazily-managed factory, in seconds. */
	public static final String CONFIG_EMF_IDLE_TIMEOUT = "emfIdleTimeout";
	/** Default idle timeout in seconds: close the real factory after 60s without any use. */
	public static final long DEFAULT_EMF_IDLE_TIMEOUT_SECONDS = 60L;

	private volatile ServiceRegistration<JPAUnit> unitRegistration;
	private volatile ServiceRegistration<EntityManagerFactory> emfRegistration;
	private volatile ServiceRegistration<CapabilityDeclaration> declarationRegistration;
	private volatile LazyJPAUnit unit;

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
	 * Common activation logic: forwards properties, sets EMF defaults, creates the cheap
	 * {@link LazyJPAUnit} and registers the {@link JPAUnit} and {@link EntityManagerFactory}
	 * services. No EclipseLink deploy happens here — the real factory is built on first use
	 * of the unit (issue #20).
	 */
	protected void doActivate(BundleContext bctx, Map<String, Object> properties) {
		Map<String, Object> emfProperties = createForwardedProperties(properties);
		setEMFProperties(emfProperties);

		try {
			long idleTimeoutMillis = readIdleTimeoutMillis(properties);
			// The supplier runs on every (re)build so a closed factory is replaced by a
			// completely fresh EclipseLink deploy.
			String unitName = getPersistenceUnitName();
			unit = new LazyJPAUnit(unitName, () -> {
				try {
					return createConfigBuilder(bctx, emfProperties).build().configure();
				} catch (Exception e) {
					throw new IllegalStateException(
							"Failed to create EntityManagerFactory for unit '" + unitName + "'", e);
				}
			}, idleTimeoutMillis);

			// Probe the database once, here: the data source is bound and open, while the
			// EclipseLink factory is deliberately not built yet (issue #172). Asking the
			// driver is the only answer that cannot disagree with reality — a configured
			// flavor could.
			JpaFlavor flavor = JpaFlavor.detect(getDataSource());
			getLogger().info(() -> String.format("Persistence unit '%s' runs on flavor '%s'",
					getPersistenceUnitName(), flavor.id()));

			Dictionary<String, Object> serviceProps = new Hashtable<>();
			serviceProps.put(JPAUnit.UNIT_NAME, getPersistenceUnitName());
			serviceProps.put("osgi.unit.version", bctx.getBundle().getVersion().toString());
			serviceProps.put("osgi.unit.provider", PersistenceProvider.class.getName());
			serviceProps.put(CapabilityDeclaration.FLAVOR_PROPERTY, flavor.id());
			unitRegistration = bctx.registerService(JPAUnit.class, unit, serviceProps);
			emfRegistration = bctx.registerService(EntityManagerFactory.class,
					new LeasedEntityManagerFactoryFactory(unit, getLogger()), serviceProps);

			// What this unit's database declares, published for consumers that must decide
			// without opening a connection of their own (issue #172).
			Dictionary<String, Object> declarationProps = new Hashtable<>();
			declarationProps.put(CapabilityDeclaration.BACKEND_PROPERTY, JpaFlavorCapabilities.BACKEND);
			declarationProps.put(CapabilityDeclaration.FLAVOR_PROPERTY, flavor.id());
			declarationProps.put(JPAUnit.UNIT_NAME, getPersistenceUnitName());
			declarationRegistration = bctx.registerService(CapabilityDeclaration.class,
					JpaFlavorCapabilities.declaration(flavor), declarationProps);
		} catch (Exception e) {
			throw new IllegalStateException("Error configuring persistence unit", e);
		}
	}

	/**
	 * Common deactivation logic: unregisters both services and disposes the unit (closing
	 * the real factory if it is currently built).
	 */
	protected void doDeactivate() {
		if (nonNull(declarationRegistration)) {
			declarationRegistration.unregister();
			declarationRegistration = null;
		}
		if (nonNull(emfRegistration)) {
			emfRegistration.unregister();
			emfRegistration = null;
		}
		if (nonNull(unitRegistration)) {
			unitRegistration.unregister();
			unitRegistration = null;
		}
		if (nonNull(unit)) {
			unit.dispose();
			unit = null;
		}
	}

	/**
	 * Reads the idle timeout (seconds) from the configuration — prefixed
	 * ({@code fennec.jpa.emfIdleTimeout}) or unprefixed key. Semantics: {@code > 0} close
	 * the real factory after that many seconds without use, {@code 0} close immediately on
	 * last release, {@code < 0} never auto-close. Defaults to
	 * {@value #DEFAULT_EMF_IDLE_TIMEOUT_SECONDS}s.
	 */
	private static long readIdleTimeoutMillis(Map<String, Object> properties) {
		Object value = readPrefixedOrPlain(properties, CONFIG_EMF_IDLE_TIMEOUT);
		long seconds = DEFAULT_EMF_IDLE_TIMEOUT_SECONDS;
		if (value instanceof Number n) {
			seconds = n.longValue();
		} else if (value instanceof String s && !s.isEmpty()) {
			try {
				seconds = Long.parseLong(s.trim());
			} catch (NumberFormatException e) {
				// keep default
			}
		}
		return seconds < 0 ? -1L : seconds * 1000L;
	}

	/**
	 * {@link ServiceFactory} backing the interop {@link EntityManagerFactory} registration.
	 * Registration itself is cheap; the first {@code getService} of a consuming bundle
	 * opens a lease (building the real factory if needed) and hands out the live
	 * EclipseLink instance, {@code ungetService} closes that lease again. The framework
	 * calls these per consuming bundle, so every holder keeps the unit from idle-closing.
	 */
	private static final class LeasedEntityManagerFactoryFactory implements ServiceFactory<EntityManagerFactory> {

		private final JPAUnit unit;
		private final Logger logger;
		private final Map<Bundle, Lease> leases = new ConcurrentHashMap<>();

		private LeasedEntityManagerFactoryFactory(JPAUnit unit, Logger logger) {
			this.unit = unit;
			this.logger = logger;
		}

		@Override
		public EntityManagerFactory getService(Bundle bundle, ServiceRegistration<EntityManagerFactory> registration) {
			try {
				Lease lease = unit.lease();
				leases.put(bundle, lease);
				return lease.getEntityManagerFactory();
			} catch (RuntimeException e) {
				logger.log(Level.SEVERE, "Failed to create EntityManagerFactory", e);
				return null;
			}
		}

		@Override
		public void ungetService(Bundle bundle, ServiceRegistration<EntityManagerFactory> registration,
				EntityManagerFactory service) {
			Lease lease = leases.remove(bundle);
			if (nonNull(lease)) {
				lease.close();
			}
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
		translateBatchWriting(properties, emfProperties);
		return emfProperties;
	}

	/**
	 * Maps the typed OCD properties {@code batchWriting} / {@code batchSize} onto the
	 * corresponding EclipseLink properties. Both are read prefixed
	 * ({@code fennec.jpa.batchWriting}, as the object class definition declares them) or
	 * unprefixed, like the idle timeout. Explicit
	 * {@code fennec.jpa.ext.eclipselink.jdbc.batch-writing*} entries take precedence —
	 * they are forwarded first and we do not overwrite them.
	 */
	private static void translateBatchWriting(Map<String, Object> src, Map<String, Object> dst) {
		Object mode = readPrefixedOrPlain(src, CONFIG_BATCH_WRITING);
		if (mode instanceof String s && !s.isEmpty()) {
			dst.putIfAbsent(PersistenceUnitProperties.BATCH_WRITING, s);
		}
		Object size = readPrefixedOrPlain(src, CONFIG_BATCH_SIZE);
		int sizeValue = asPositiveInt(size);
		if (sizeValue > 0) {
			dst.putIfAbsent(PersistenceUnitProperties.BATCH_WRITING_SIZE, String.valueOf(sizeValue));
		}
	}

	/**
	 * Reads a configuration value under its prefixed key, falling back to the unprefixed one.
	 * @param properties the raw configuration properties
	 * @param key the unprefixed configuration key
	 * @return the value or <code>null</code>, if neither key is set
	 */
	private static Object readPrefixedOrPlain(Map<String, Object> properties, String key) {
		Object value = properties.get(PROPERTY_PREFIX + key);
		return isNull(value) ? properties.get(key) : value;
	}

	private static int asPositiveInt(Object value) {
		if (value instanceof Integer i) {
			return i > 0 ? i : 0;
		}
		if (value instanceof Number n) {
			int i = n.intValue();
			return i > 0 ? i : 0;
		}
		if (value instanceof String s && !s.isEmpty()) {
			try {
				int i = Integer.parseInt(s);
				return i > 0 ? i : 0;
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
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
