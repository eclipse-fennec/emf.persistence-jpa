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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.fennec.persistence.eclipselink.spi.PersistenceUnitConfigurator.PUConfig;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.helper.EORMModelHelper;
import org.junit.jupiter.api.Test;
import org.osgi.service.cm.ConfigurationException;

/**
 * Unit tests for {@link PersistenceUnitConfigurator#createPersistenceContext(PUConfig)}.
 * Focus is on config validation branches — ConfigurationException paths and the
 * persistenceUnitFile vs mappingFile dispatch.
 */
class PersistenceUnitConfiguratorTest {

	@Test
	void createPersistenceContext_missingBothNameAndFile_throwsForMissingName()
			throws Exception {
		PersistenceUnitConfigurator subject = new PersistenceUnitConfigurator();
		injectModelHelper(subject, mock(EORMModelHelper.class));

		PUConfig config = stubPUConfig(Map.of());

		assertThatThrownBy(() -> subject.createPersistenceContext(config))
				.isInstanceOf(ConfigurationException.class)
				.extracting(e -> ((ConfigurationException) e).getProperty())
				.isEqualTo("persistenceUnitName");
	}

	@Test
	void createPersistenceContext_nameWithoutMappingFile_throwsForMissingMappingFile()
			throws Exception {
		PersistenceUnitConfigurator subject = new PersistenceUnitConfigurator();
		injectModelHelper(subject, mock(EORMModelHelper.class));

		PUConfig config = stubPUConfig(Map.of("persistenceUnitName", "test-pu"));

		assertThatThrownBy(() -> subject.createPersistenceContext(config))
				.isInstanceOf(ConfigurationException.class)
				.extracting(e -> ((ConfigurationException) e).getProperty())
				.isEqualTo("mappingFile");
	}

	@Test
	void createPersistenceContext_validNameAndMappingFile_returnsContext() throws Exception {
		EORMModelHelper modelHelper = mock(EORMModelHelper.class);
		EntityMappings mappings = EORMFactory.eINSTANCE.createEntityMappings();
		when(modelHelper.loadMapping("mapping.eorm")).thenReturn(mappings);

		PersistenceUnitConfigurator subject = new PersistenceUnitConfigurator();
		injectModelHelper(subject, modelHelper);

		PUConfig config = stubPUConfig(Map.of(
				"persistenceUnitName", "test-pu",
				"mappingFile", "mapping.eorm"));

		EPersistenceContextImpl ctx = subject.createPersistenceContext(config);
		assertThat(ctx).isNotNull();
		assertThat(ctx.getPersistenceUnitName()).isEqualTo("test-pu");
		assertThat(ctx.getMappings()).hasSize(1);
	}

	@Test
	void createPersistenceContext_invalidMappingFile_wrapsInConfigurationException() throws Exception {
		EORMModelHelper modelHelper = mock(EORMModelHelper.class);
		when(modelHelper.loadMapping("bad.eorm")).thenThrow(new RuntimeException("boom"));

		PersistenceUnitConfigurator subject = new PersistenceUnitConfigurator();
		injectModelHelper(subject, modelHelper);

		PUConfig config = stubPUConfig(Map.of(
				"persistenceUnitName", "test-pu",
				"mappingFile", "bad.eorm"));

		assertThatThrownBy(() -> subject.createPersistenceContext(config))
				.isInstanceOf(ConfigurationException.class)
				.extracting(e -> ((ConfigurationException) e).getProperty())
				.isEqualTo("mappingFile");
	}

	@Test
	void createPersistenceContext_persistenceUnitFile_dispatchesToLoadPersistenceUnit() throws Exception {
		EORMModelHelper modelHelper = mock(EORMModelHelper.class);
		PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		pu.setName("file-pu");
		when(modelHelper.loadPersistenceUnit("pu.xmi")).thenReturn(pu);

		PersistenceUnitConfigurator subject = new PersistenceUnitConfigurator();
		injectModelHelper(subject, modelHelper);

		PUConfig config = stubPUConfig(Map.of("persistenceUnitFile", "pu.xmi"));

		EPersistenceContextImpl ctx = subject.createPersistenceContext(config);
		assertThat(ctx).isNotNull();
		assertThat(ctx.getPersistenceUnitName()).isEqualTo("file-pu");
	}

	@Test
	void createPersistenceContext_persistenceUnitFile_loadFailureWrapsInConfigurationException()
			throws Exception {
		EORMModelHelper modelHelper = mock(EORMModelHelper.class);
		when(modelHelper.loadPersistenceUnit("broken.xmi"))
				.thenThrow(new RuntimeException("parse failure"));

		PersistenceUnitConfigurator subject = new PersistenceUnitConfigurator();
		injectModelHelper(subject, modelHelper);

		PUConfig config = stubPUConfig(Map.of("persistenceUnitFile", "broken.xmi"));

		assertThatThrownBy(() -> subject.createPersistenceContext(config))
				.isInstanceOf(ConfigurationException.class)
				.extracting(e -> ((ConfigurationException) e).getProperty())
				.isEqualTo("persistenceUnitFile");
	}

	private static void injectModelHelper(PersistenceUnitConfigurator subject, EORMModelHelper helper)
			throws Exception {
		Field f = PersistenceUnitConfigurator.class.getDeclaredField("modelHelper");
		f.setAccessible(true);
		f.set(subject, helper);
	}

	private static PUConfig stubPUConfig(Map<String, Object> values) {
		return (PUConfig) Proxy.newProxyInstance(
				PUConfig.class.getClassLoader(),
				new Class<?>[] { PUConfig.class },
				new AnnotationStubHandler(PUConfig.class, values));
	}

	private static final class AnnotationStubHandler implements InvocationHandler {
		private final Class<? extends Annotation> annotationType;
		private final Map<String, Object> values;

		AnnotationStubHandler(Class<? extends Annotation> annotationType, Map<String, Object> values) {
			this.annotationType = annotationType;
			this.values = new HashMap<>(values);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) {
			if ("annotationType".equals(method.getName())) {
				return annotationType;
			}
			if (values.containsKey(method.getName())) {
				return values.get(method.getName());
			}
			Object defaultValue = method.getDefaultValue();
			if (defaultValue != null) {
				return defaultValue;
			}
			Class<?> rt = method.getReturnType();
			if (rt == int.class) return 0;
			if (rt == boolean.class) return false;
			if (rt == long.class) return 0L;
			return null;
		}
	}
}
