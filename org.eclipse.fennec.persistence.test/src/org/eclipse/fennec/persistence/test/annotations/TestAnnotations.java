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
package org.eclipse.fennec.persistence.test.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.XADataSource;

import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.TemplateArgument;
import org.osgi.test.common.annotation.Property.ValueSource;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;

/**
 * 
 * @author mark
 * @since 20.12.2024
 */
public class TestAnnotations {

	public static final String PROP_DB_PATH = "dbPath";
	public static final String PROP_MODEL_PATH = "tmpModelFile";
	public static final String PROP_MODEL_FILE_PATH = "tmpModelFilePath";
	public static final String PROP_MODEL_FILE = "model.eorm";
	/**
	 * Constant for the {@link org.osgi.framework.Constants#SERVICE_PID} of a
	 * {@link DataSource} and {@link XADataSource} and
	 * {@link ConnectionPoolDataSource} - Service.
	 */
	public static final String PID_DATASOURCE = "daanse.jdbc.datasource.h2.DataSource";
	/**
	 * Constant for Properties of the Service that could be configured using the
	 * {@link PID_DATASOURCE}.
	 *
	 * {@link org.eclipse.daanse.jdbc.datasource.h2.impl.H2BaseConfig#identifier()}
	 */
	public static final String DATASOURCE_PROPERTY_IDENTIFIER = "identifier";
	/**
	 * Constant for Properties of the Service that could be configured using the
	 * {@link Constants#PID_DATASOURCE}.
	 *
	 * {@link org.eclipse.daanse.jdbc.datasource.h2.impl.H2BaseConfig#plugableFilesystem()}
	 */
	public static final String DATASOURCE_PROPERTY_PLUGABLE_FILESYSTEM = "plugableFilesystem";
	/**
	 * Constant for the Option 'file' of the Properties
	 * {@link Constants#DATASOURCE_PROPERTY_PLUGABLE_FILESYSTEM}
	 */
	public static final String OPTION_PLUGABLE_FILESYSTEM_FILE = "file";
	
	@WithFactoryConfiguration(factoryPid = PID_DATASOURCE, name = "1", location = "?", properties = 
			@Property(key = DATASOURCE_PROPERTY_IDENTIFIER, value = "%s/h2/%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_PATH), 
					@TemplateArgument(source = ValueSource.TestMethod)
			}))
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DefaultH2Configuration {}

	/**
	 * Default setup for all tests. The default shared EntityManagerFactory cache is used
	 * @author Mark
	 * @since 21.01.2025
	 */
	@WithFactoryConfiguration(factoryPid = PID_DATASOURCE, name = "1", location = "?", properties = 
			@Property(key = DATASOURCE_PROPERTY_IDENTIFIER, value = "%s/h2/%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_PATH), 
					@TemplateArgument(source = ValueSource.TestMethod)
			}))
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "test", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)
			}),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "person"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DefaultEPersistenceConfiguration {}
	
	/**
	 * Default setup for all tests. The default shared EntityManagerFactory cache is used
	 * @author Mark
	 * @since 21.01.2025
	 */
	@WithFactoryConfiguration(factoryPid = PID_DATASOURCE, name = "1", location = "?", properties = 
			@Property(key = DATASOURCE_PROPERTY_IDENTIFIER, value = "%s/h2/%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_PATH), 
					@TemplateArgument(source = ValueSource.TestMethod)
			}))
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "test", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)
			}),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "person"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.JPARepository", name = "test", properties = {
			@Property(key = "entityManager.target", value = "(osgi.unit.name=person)"),
			@Property(key = "sharedEM", value = "true"),
			@Property(key = "repo_id", value = "test"),
			@Property(key = "base_uri", value = "test")
	})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DefaultRepositoryConfiguration {}

	/**
	 * Same setup like above, but with no cache enabled
	 * @author Mark Hoffmann
	 * @since 21.01.2025
	 */
	@WithFactoryConfiguration(factoryPid = PID_DATASOURCE, name = "1", location = "?", properties = 
			@Property(key = DATASOURCE_PROPERTY_IDENTIFIER, value = "%s/h2/%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_PATH), 
					@TemplateArgument(source = ValueSource.TestMethod)
			}))
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "test", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)
			}),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "person"),
			@Property(key = "fennec.jpa.ext.eclipselink.cache.shared.default", value = "false"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface NoCacheEPersistenceConfiguration {}

	@WithFactoryConfiguration(factoryPid = "fennec.jpa.EORMLoader", name = "test", properties = {
			@Property(key = "fennec.jpa.eorm.model.target", value = "(emf.name=basic)"),
			@Property(key = "fennec.jpa.eorm.name", value = "person"),
			@Property(key = "fennec.jpa.eorm.uri", value = "%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)
			})
	})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface EORMLoaderConfiguration {}


	@DefaultEPersistenceSetup
	@EORMLoaderConfiguration
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.EMPersistenceUnit", name = "test", properties = {
			@Property(key = "fennec.jpa.mapping.target", value = "(fennec.jpa.orm.mapping.name=person)"),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "person")
	})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface EORMLoaderEPersistenceConfiguration {}

	@WithFactoryConfiguration(factoryPid = PID_DATASOURCE, name = "1", location = "?", properties = 
			@Property(key = DATASOURCE_PROPERTY_IDENTIFIER, value = "%s/h2/%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_PATH), 
					@TemplateArgument(source = ValueSource.TestMethod) }))
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DefaultEPersistenceSetup {}

	@WithFactoryConfiguration(factoryPid = PID_DATASOURCE, name = "1", location = "?", properties =
			@Property(key = DATASOURCE_PROPERTY_IDENTIFIER, value = "%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_DB_PATH),
			}))
	@Retention(RetentionPolicy.RUNTIME)
	public @interface CitizenEPersistenceSetup {}

	@WithFactoryConfiguration(factoryPid = PID_DATASOURCE, name = "1", location = "?", properties =
			@Property(key = DATASOURCE_PROPERTY_IDENTIFIER, value = "%s/h2/%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_PATH),
					@TemplateArgument(source = ValueSource.TestMethod)
			}))
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "test", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=citizen)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments = {
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)
			}),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "citizen"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface CitizenEPersistenceConfiguration {}
}
