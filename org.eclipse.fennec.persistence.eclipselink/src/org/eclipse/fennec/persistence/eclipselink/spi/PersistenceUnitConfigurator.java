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

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.eclipselink.spi.EntityManagerFactoryConfigurator.Builder;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.helper.EORMModelHelper;
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

/**
 * Configurator component that creates an EntityManagerFactory from a
 * persistence unit file or mapping file configuration.
 *
 * @author Mark Hoffmann
 * @since 10.12.2024
 */
@Designate(factory = true, ocd = PersistenceUnitConfigurator.PUConfig.class)
@Component(name = PersistenceUnitConfigurator.PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class PersistenceUnitConfigurator extends AbstractPersistenceUnitConfigurator {

	private static final Logger LOG = Logger.getLogger(PersistenceUnitConfigurator.class.getName());

	public static final String PID = "fennec.jpa.PersistenceUnit";
	public static final String EPERSISTENCE_MODEL_TARGET = "(&(" + EMFNamespaces.EMF_NAME + "=" + EPersistencePackage.eNAME + ")(" + EMFNamespaces.EMF_NAME + "=" + EORMPackage.eNAME + "))";

	@Reference(target = EPERSISTENCE_MODEL_TARGET)
	private ResourceSet resourceSet;
	@Reference(name = "fennec.jpa.dataSource")
	private DataSource dataSource;
	@Reference(name = "fennec.jpa.model")
	private EPackage modelPackage;
	@Reference(name = "fennec.jpa.converter")
	private ConverterService converter;

	private EORMModelHelper modelHelper;
	private EPersistenceContextImpl pctx;

	@ObjectClassDefinition
	public @interface PUConfig {

		public static final String PREFIX_ = PROPERTY_PREFIX;

		@AttributeDefinition(name = "Persistence unit file", description = "Optional attribute, if a single mapping file is given")
		String persistenceUnitFile();

		@AttributeDefinition(name = "Entity mapping file")
		String mappingFile();

		@AttributeDefinition(name = "Persistence unit name", description = "Only needed, if no persistence unit file is given")
		String persistenceUnitName();

		@AttributeDefinition(name = "JDBC batch writing mode",
				description = "EclipseLink eclipselink.jdbc.batch-writing value (e.g. JDBC, BUFFERED, NONE). Empty = not set.",
				required = false)
		String batchWriting() default "";

		@AttributeDefinition(name = "JDBC batch size",
				description = "EclipseLink eclipselink.jdbc.batch-writing.size value. 0 = not set.",
				required = false)
		int batchSize() default 0;
	}

	@Activate
	void activate(BundleContext bctx, PUConfig config, Map<String, Object> properties)
			throws IOException, ConfigurationException {
		modelHelper = new EORMModelHelper(resourceSet);
		this.pctx = createPersistenceContext(config);
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
		return Builder.create(bctx, resourceSet)
				.dataSource(dataSource)
				.context(pctx)
				.converter(converter)
				.properties(emfProperties);
	}

	EPersistenceContextImpl createPersistenceContext(PUConfig config) throws ConfigurationException {
		if (nonNull(config.persistenceUnitFile())) {
			try {
				PersistenceUnit pu = modelHelper.loadPersistenceUnit(config.persistenceUnitFile());
				return new EPersistenceContextImpl(pu);
			} catch (Exception e) {
				throw new ConfigurationException("persistenceUnitFile",
						String.format("The file from this uri '%s' cannot be loaded", config.persistenceUnitFile()), e);
			}
		} else {
			if (isNull(config.persistenceUnitName())) {
				throw new ConfigurationException("persistenceUnitName", "No persistence unit name was provided");
			}
			if (isNull(config.mappingFile())) {
				throw new ConfigurationException("mappingFile", "No mapping file path was provided");
			}
			try {
				EntityMappings mapping = modelHelper.loadMapping(config.mappingFile());
				return new EPersistenceContextImpl(config.persistenceUnitName(), Collections.singletonList(mapping));
			} catch (Exception e) {
				throw new ConfigurationException("mappingFile",
						String.format("The file from this uri '%s' cannot be loaded", config.mappingFile()), e);
			}
		}
	}

}
