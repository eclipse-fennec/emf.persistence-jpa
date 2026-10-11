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
package org.eclipse.fennec.persistence.test;

import static org.eclipse.fennec.persistence.test.annotations.TestAnnotations.PROP_MODEL_FILE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.repository.api.ReadRepository;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.eclipse.fennec.persistence.test.proto.RepositoryConsumer;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceObjects;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.TemplateArgument;
import org.osgi.test.common.annotation.Property.ValueSource;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;

/**
 * A prototype-scoped repository held by a prototype-scoped DS component (the request-resource
 * shape): releasing the component must dispose the repository exactly once, and the framework
 * must not log {@code Error ungetting service} while doing so. Covers the three ways a
 * component can hold the reference (field, constructor, two fields on one registration) and
 * both a consumer from the test bundle and one from another bundle, as a whiteboard would be.
 */
public class RepositoryPrototypeConsumerTest extends EPersistenceBase {

	private static final String UNGET_ERROR = "Error ungetting service";

	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		EClass personEClass = (EClass) ePackage.getEClassifier("Person");
		assertNotNull(personEClass);
		return mapper.createMappings(List.of(personEClass));
	}

	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "protoUnit", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments =
					@TemplateArgument(source = ValueSource.SystemProperty, value = PROP_MODEL_FILE_PATH)),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "protoUnit"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.repository.jpa", name = "proto", properties = {
			@Property(key = "repositoryId", value = "proto-repo"),
			@Property(key = "unit.target", value = "(osgi.unit.name=protoUnit)")
	})
	public void testReleasingAPrototypeConsumerDisposesItsRepositoryOnce(
			@InjectService(filter = "(consumer=field)", timeout = 10000)
			ServiceAware<RepositoryConsumer> fieldAware,
			@InjectService(filter = "(consumer=constructor)", timeout = 10000)
			ServiceAware<RepositoryConsumer> constructorAware,
			@InjectService(filter = "(consumer=twofields)", timeout = 10000)
			ServiceAware<RepositoryConsumer> twoFieldsAware,
			@InjectBundleContext BundleContext context) throws Exception {
		BundleContext foreign = context.getBundle(0).getBundleContext();
		for (ServiceAware<RepositoryConsumer> aware : List.of(fieldAware, constructorAware, twoFieldsAware)) {
			String consumer = String.valueOf(aware.getServiceReference().getProperty("consumer"));
			releaseAndCheck(consumer + " from the test bundle",
					context.getServiceObjects(aware.getServiceReference()));
			releaseAndCheck(consumer + " from another bundle",
					foreign.getServiceObjects(aware.getServiceReference()));
		}
	}

	private static void releaseAndCheck(String label, ServiceObjects<RepositoryConsumer> serviceObjects) {
		RepositoryConsumer first = serviceObjects.getService();
		RepositoryConsumer second = serviceObjects.getService();
		assertNotSame(first, second, label + ": prototype component, one instance per request");
		ReadRepository[] firstRepositories = first.repositories();
		ReadRepository[] secondRepositories = second.repositories();
		for (ReadRepository repository : firstRepositories) {
			assertNotNull(repository, label + ": repository bound");
			assertFalse(repository.isDisposed(), label + ": repository alive while bound");
			assertNotSame(repository, secondRepositories[0], label + ": each component owns its repository");
		}
		if (firstRepositories.length == 2) {
			assertSame(firstRepositories[0], firstRepositories[1],
					label + ": two references on one registration share the instance");
		}

		String output = capturingOutput(() -> serviceObjects.ungetService(first));
		assertTrue(firstRepositories[0].isDisposed(), label + ": releasing the component disposes its repository");
		assertFalse(secondRepositories[0].isDisposed(), label + ": the other instance is untouched");
		assertEquals("", output, label + ": releasing must not make the framework log '" + UNGET_ERROR + "'");

		output = capturingOutput(() -> serviceObjects.ungetService(second));
		assertTrue(secondRepositories[0].isDisposed(), label);
		assertEquals("", output, label + ": releasing must not make the framework log '" + UNGET_ERROR + "'");
	}

	/** Runs the action while capturing stdout and stderr; returns the lines mentioning the unget error. */
	private static String capturingOutput(Runnable action) {
		PrintStream out = System.out;
		PrintStream err = System.err;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream capture = new PrintStream(buffer, true, StandardCharsets.UTF_8);
		System.setOut(capture);
		System.setErr(capture);
		try {
			action.run();
		} finally {
			System.setOut(out);
			System.setErr(err);
		}
		String captured = buffer.toString(StandardCharsets.UTF_8);
		out.print(captured);
		return captured.lines().filter(line -> line.contains(UNGET_ERROR))
				.reduce("", (a, b) -> a.isEmpty() ? b : a + "\n" + b);
	}
}
