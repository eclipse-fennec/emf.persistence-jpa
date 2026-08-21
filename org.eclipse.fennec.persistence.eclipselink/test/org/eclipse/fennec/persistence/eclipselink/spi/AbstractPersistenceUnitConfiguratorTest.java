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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.eclipselink.spi.EntityManagerFactoryConfigurator.Builder;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetDatabase;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;

class AbstractPersistenceUnitConfiguratorTest {

	private final AbstractPersistenceUnitConfigurator subject = new StubConfigurator();

	@Test
	void createForwardedProperties_stripsFennecJpaExtPrefix() {
		Map<String, Object> raw = new HashMap<>();
		raw.put("fennec.jpa.ext.eclipselink.logging.level", "FINE");
		raw.put("fennec.jpa.ext.eclipselink.weaving", "false");
		raw.put("ignored.unrelated", "value");

		Map<String, Object> forwarded = subject.createForwardedProperties(raw);

		assertThat(forwarded)
				.containsEntry("eclipselink.logging.level", "FINE")
				.containsEntry("eclipselink.weaving", "false")
				.doesNotContainKey("ignored.unrelated")
				.doesNotContainKey("fennec.jpa.ext.eclipselink.logging.level");
	}

	@Test
	void createForwardedProperties_translatesBatchWritingOcdProperty() {
		Map<String, Object> raw = new HashMap<>();
		raw.put(AbstractPersistenceUnitConfigurator.CONFIG_BATCH_WRITING, "JDBC");
		raw.put(AbstractPersistenceUnitConfigurator.CONFIG_BATCH_SIZE, 500);

		Map<String, Object> forwarded = subject.createForwardedProperties(raw);

		assertThat(forwarded)
				.containsEntry(PersistenceUnitProperties.BATCH_WRITING, "JDBC")
				.containsEntry(PersistenceUnitProperties.BATCH_WRITING_SIZE, "500");
	}

	@Test
	void createForwardedProperties_translatesPrefixedBatchWriting() {
		Map<String, Object> raw = new HashMap<>();
		raw.put(AbstractPersistenceUnitConfigurator.PROPERTY_PREFIX
				+ AbstractPersistenceUnitConfigurator.CONFIG_BATCH_WRITING, "JDBC");
		raw.put(AbstractPersistenceUnitConfigurator.PROPERTY_PREFIX
				+ AbstractPersistenceUnitConfigurator.CONFIG_BATCH_SIZE, 500);

		Map<String, Object> forwarded = subject.createForwardedProperties(raw);

		assertThat(forwarded)
				.containsEntry(PersistenceUnitProperties.BATCH_WRITING, "JDBC")
				.containsEntry(PersistenceUnitProperties.BATCH_WRITING_SIZE, "500");
	}

	@Test
	void createForwardedProperties_prefixedBatchWritingWinsOverUnprefixed() {
		Map<String, Object> raw = new HashMap<>();
		raw.put(AbstractPersistenceUnitConfigurator.PROPERTY_PREFIX
				+ AbstractPersistenceUnitConfigurator.CONFIG_BATCH_WRITING, "JDBC");
		raw.put(AbstractPersistenceUnitConfigurator.CONFIG_BATCH_WRITING, "BUFFERED");

		Map<String, Object> forwarded = subject.createForwardedProperties(raw);

		assertThat(forwarded).containsEntry(PersistenceUnitProperties.BATCH_WRITING, "JDBC");
	}

	@Test
	void createForwardedProperties_batchSizeFromString() {
		Map<String, Object> raw = new HashMap<>();
		raw.put(AbstractPersistenceUnitConfigurator.CONFIG_BATCH_WRITING, "BUFFERED");
		raw.put(AbstractPersistenceUnitConfigurator.CONFIG_BATCH_SIZE, "250");

		Map<String, Object> forwarded = subject.createForwardedProperties(raw);

		assertThat(forwarded)
				.containsEntry(PersistenceUnitProperties.BATCH_WRITING, "BUFFERED")
				.containsEntry(PersistenceUnitProperties.BATCH_WRITING_SIZE, "250");
	}

	@Test
	void createForwardedProperties_emptyBatchWriting_isIgnored() {
		Map<String, Object> raw = new HashMap<>();
		raw.put(AbstractPersistenceUnitConfigurator.CONFIG_BATCH_WRITING, "");
		raw.put(AbstractPersistenceUnitConfigurator.CONFIG_BATCH_SIZE, 0);

		Map<String, Object> forwarded = subject.createForwardedProperties(raw);

		assertThat(forwarded)
				.doesNotContainKey(PersistenceUnitProperties.BATCH_WRITING)
				.doesNotContainKey(PersistenceUnitProperties.BATCH_WRITING_SIZE);
	}

	@Test
	void createForwardedProperties_explicitExtPropertyWinsOverOcd() {
		Map<String, Object> raw = new HashMap<>();
		raw.put(AbstractPersistenceUnitConfigurator.CONFIG_BATCH_WRITING, "JDBC");
		raw.put(AbstractPersistenceUnitConfigurator.CONFIG_BATCH_SIZE, 500);
		raw.put("fennec.jpa.ext." + PersistenceUnitProperties.BATCH_WRITING, "BUFFERED");
		raw.put("fennec.jpa.ext." + PersistenceUnitProperties.BATCH_WRITING_SIZE, "1000");

		Map<String, Object> forwarded = subject.createForwardedProperties(raw);

		assertThat(forwarded)
				.containsEntry(PersistenceUnitProperties.BATCH_WRITING, "BUFFERED")
				.containsEntry(PersistenceUnitProperties.BATCH_WRITING_SIZE, "1000");
	}

	@Test
	void createForwardedProperties_negativeBatchSize_isIgnored() {
		Map<String, Object> raw = new HashMap<>();
		raw.put(AbstractPersistenceUnitConfigurator.CONFIG_BATCH_WRITING, "JDBC");
		raw.put(AbstractPersistenceUnitConfigurator.CONFIG_BATCH_SIZE, -5);

		Map<String, Object> forwarded = subject.createForwardedProperties(raw);

		assertThat(forwarded)
				.containsEntry(PersistenceUnitProperties.BATCH_WRITING, "JDBC")
				.doesNotContainKey(PersistenceUnitProperties.BATCH_WRITING_SIZE);
	}

	@Test
	void createForwardedProperties_emptyMap_returnsEmpty() {
		Map<String, Object> forwarded = subject.createForwardedProperties(new HashMap<>());
		assertThat(forwarded).isEmpty();
	}

	@Test
	void createForwardedProperties_onlyIgnoredKeys_returnsEmpty() {
		Map<String, Object> raw = new HashMap<>();
		raw.put("unrelated.a", 1);
		raw.put("fennec.jpa.someSetting", "x"); // fennec.jpa. prefix but NOT fennec.jpa.ext.

		Map<String, Object> forwarded = subject.createForwardedProperties(raw);
		assertThat(forwarded).isEmpty();
	}

	@Test
	void setEMFProperties_appliesEclipseLinkDefaults() {
		Map<String, Object> props = new HashMap<>();
		subject.setEMFProperties(props);

		assertThat(props)
				.containsEntry(PersistenceUnitProperties.WEAVING, "false")
				.containsEntry(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL")
				.containsEntry(PersistenceUnitProperties.THROW_EXCEPTIONS, "true")
				.containsEntry(PersistenceUnitProperties.CONNECTION_POOL_MIN, 1)
				.containsKey(PersistenceUnitProperties.NON_JTA_DATASOURCE)
				.containsEntry(PersistenceUnitProperties.TARGET_DATABASE, TargetDatabase.Auto)
				.containsEntry(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE)
				.containsEntry("eclipselink.logging.level", "WARNING")
				.containsEntry("eclipselink.logging.timestamp", "false")
				.containsEntry("eclipselink.logging.thread", "false")
				.containsEntry("eclipselink.logging.exceptions", "true");
	}

	@Test
	void setEMFProperties_doesNotOverrideExplicitDdlGeneration() {
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
		subject.setEMFProperties(props);

		assertThat(props).containsEntry(PersistenceUnitProperties.DDL_GENERATION,
				PersistenceUnitProperties.CREATE_OR_EXTEND);
	}

	@Test
	void setEMFProperties_doesNotOverrideExplicitTargetDatabase() {
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "HSQL");
		subject.setEMFProperties(props);

		assertThat(props).containsEntry(PersistenceUnitProperties.TARGET_DATABASE, "HSQL");
	}

	@Test
	void setEMFProperties_nullProperties_throwsNPE() {
		// Contractually, setEMFProperties uses requireNonNull — null must fail fast
		assertThatThrownBy(() -> subject.setEMFProperties(null))
				.isInstanceOf(NullPointerException.class);
	}

	private static final class StubConfigurator extends AbstractPersistenceUnitConfigurator {
		@Override
		protected Logger getLogger() {
			return Logger.getLogger(StubConfigurator.class.getName());
		}

		@Override
		protected DataSource getDataSource() {
			return null;
		}

		@Override
		protected ConverterService getConverterService() {
			return null;
		}

		@Override
		protected Builder createConfigBuilder(BundleContext bctx, Map<String, Object> emfProperties) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected String getPersistenceUnitName() {
			return "stub";
		}
	}
}
