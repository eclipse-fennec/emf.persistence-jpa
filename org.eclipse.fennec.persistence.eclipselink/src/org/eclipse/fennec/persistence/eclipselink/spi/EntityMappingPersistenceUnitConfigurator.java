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

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.eclipselink.spi.EntityManagerFactoryConfigurator.Builder;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import aQute.bnd.annotation.service.ServiceCapability;
import jakarta.persistence.EntityManagerFactory;

/**
 * Configurator component that creates an EntityManagerFactory from
 * an injected {@link EntityMappings} service.
 *
 * @author Mark Hoffmann
 * @since 10.12.2024
 */
@Designate(factory = true, ocd = EntityMappingPersistenceUnitConfigurator.PUConfig.class)
@Component(name = EntityMappingPersistenceUnitConfigurator.PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
@ServiceCapability(EntityManagerFactory.class)
@ServiceCapability(EntityMappings.class)
public class EntityMappingPersistenceUnitConfigurator extends AbstractPersistenceUnitConfigurator {

	private static final Logger LOG = Logger.getLogger(EntityMappingPersistenceUnitConfigurator.class.getName());

	public static final String PID = "fennec.jpa.EMPersistenceUnit";

	@Reference(name = "fennec.jpa.dataSource")
	private DataSource dataSource;
	@Reference(name = "fennec.jpa.mapping")
	private EntityMappings mappings;
	@Reference(name = "fennec.jpa.converter")
	private ConverterService converter;

	private EPersistenceContextImpl pctx;

	@ObjectClassDefinition
	public @interface PUConfig {

		public static final String PREFIX_ = PROPERTY_PREFIX;

		@AttributeDefinition(name = "Persistence unit name", description = "Only needed, if no persistence unit file is given")
		String persistenceUnitName();
	}

	@Activate
	void activate(BundleContext bctx, PUConfig config, Map<String, Object> properties)
			throws IOException, ConfigurationException {
		if (isNull(config.persistenceUnitName())) {
			throw new ConfigurationException("persistenceUnitName", "No persistence unit name was provided");
		}
		this.pctx = new EPersistenceContextImpl(
				config.persistenceUnitName(), Collections.singletonList(mappings));
		pctx.setMetadataURL(getMetadataURL(bctx));

		doActivate(bctx, properties);
	}

	@Deactivate
	void deactivate() {
		doDeactivate();
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected DataSource getDataSource() {
		return dataSource;
	}

	@Override
	protected ConverterService getConverterService() {
		return converter;
	}

	@Override
	protected String getPersistenceUnitName() {
		return pctx.getPersistenceUnitName();
	}

	@Override
	protected Builder createConfigBuilder(BundleContext bctx, Map<String, Object> emfProperties) {
		return Builder.create(bctx, null)
				.dataSource(dataSource)
				.context(pctx)
				.converter(converter)
				.properties(emfProperties);
	}
}
