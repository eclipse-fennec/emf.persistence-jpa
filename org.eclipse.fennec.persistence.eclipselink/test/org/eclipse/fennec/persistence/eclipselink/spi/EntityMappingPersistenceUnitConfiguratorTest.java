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

import org.eclipse.fennec.persistence.eclipselink.spi.EntityMappingPersistenceUnitConfigurator.PUConfig;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;

class EntityMappingPersistenceUnitConfiguratorTest {

	@Test
	void activate_nullPersistenceUnitName_throwsConfigurationException() throws Exception {
		CapturingConfigurator subject = new CapturingConfigurator();
		injectMappings(subject, EORMFactory.eINSTANCE.createEntityMappings());

		PUConfig config = stubPUConfig(Map.of()); // persistenceUnitName = null
		BundleContext bctx = mock(BundleContext.class);

		assertThatThrownBy(() -> subject.activate(bctx, config, new HashMap<>()))
				.isInstanceOf(ConfigurationException.class)
				.extracting(e -> ((ConfigurationException) e).getProperty())
				.isEqualTo("persistenceUnitName");
	}

	@Test
	void activate_validConfig_setsPctxAndCallsDoActivate() throws Exception {
		EntityMappings mappings = EORMFactory.eINSTANCE.createEntityMappings();
		CapturingConfigurator subject = new CapturingConfigurator();
		injectMappings(subject, mappings);

		PUConfig config = stubPUConfig(Map.of("persistenceUnitName", "em-pu"));
		BundleContext bctx = mock(BundleContext.class);
		Bundle bundle = mock(Bundle.class);
		when(bctx.getBundle()).thenReturn(bundle); // getMetadataURL calls bctx.getBundle().getEntry(...)
		Map<String, Object> props = new HashMap<>();
		subject.activate(bctx, config, props);

		assertThat(subject.doActivateCalled).isTrue();
		assertThat(subject.capturedProperties).isSameAs(props);
		assertThat(subject.getPersistenceUnitName()).isEqualTo("em-pu");
	}

	/**
	 * Subclass that overrides {@link AbstractPersistenceUnitConfigurator#doActivate(BundleContext, Map)}
	 * to avoid triggering the EntityManagerFactory bootstrap.
	 */
	private static final class CapturingConfigurator extends EntityMappingPersistenceUnitConfigurator {
		boolean doActivateCalled;
		Map<String, Object> capturedProperties;

		@Override
		protected void doActivate(BundleContext bctx, Map<String, Object> properties) {
			this.doActivateCalled = true;
			this.capturedProperties = properties;
		}
	}

	private static void injectMappings(EntityMappingPersistenceUnitConfigurator subject,
			EntityMappings mappings) throws Exception {
		Field f = EntityMappingPersistenceUnitConfigurator.class.getDeclaredField("mappings");
		f.setAccessible(true);
		f.set(subject, mappings);
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
