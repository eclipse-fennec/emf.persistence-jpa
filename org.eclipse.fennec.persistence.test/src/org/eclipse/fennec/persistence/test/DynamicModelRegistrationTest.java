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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Tests that a dynamically registered EPackageConfigurator propagates
 * the EPackage into injected ResourceSet instances via the
 * DefaultEPackageRegistryComponent.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
class DynamicModelRegistrationTest {

	private static final String FPM_NS_URI = "https://projects.eclipse.org/projects/modeling.fennec/fpm";
	private static final String FPM_NAME = "fennec.persistence.model";

	private ServiceRegistration<EPackageConfigurator> configuratorReg;
	private ServiceRegistration<EPackage> packageReg;
	private EPackage modelPackage;

	@BeforeEach
	void before(@InjectBundleContext BundleContext ctx) throws IOException {
		// Load test model.ecore
		ResourceSet localRs = new ResourceSetImpl();
		localRs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		localRs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

		URL entry = ctx.getBundle().getEntry("/data/model.ecore");
		assertThat(entry).as("model.ecore bundle entry").isNotNull();

		Resource resource = localRs.createResource(URI.createURI(FPM_NS_URI));
		resource.load(entry.openStream(), null);
		modelPackage = (EPackage) resource.getContents().get(0);

		// Register as OSGi services (same as EPersistenceModelBase)
		EPackageConfigurator configurator = new EPackageConfigurator() {
			@Override
			public void configureEPackage(Registry registry) {
				registry.put(modelPackage.getNsURI(), modelPackage);
			}
			@Override
			public void unconfigureEPackage(Registry registry) {
				registry.remove(modelPackage.getNsURI());
			}
		};

		Dictionary<String, Object> props = Dictionaries.dictionaryOf(
				EMFNamespaces.EMF_NAME, modelPackage.getName(),
				EMFNamespaces.EMF_MODEL_NSURI, modelPackage.getNsURI(),
				EMFNamespaces.EMF_MODEL_REGISTRATION, EMFNamespaces.MODEL_REGISTRATION_PROVIDED,
				EMFNamespaces.EMF_MODEL_SCOPE, EMFNamespaces.EMF_MODEL_SCOPE_RESOURCE_SET);

		System.out.println("=== Registering test model ===");
		System.out.println("  name: " + modelPackage.getName());
		System.out.println("  nsURI: " + modelPackage.getNsURI());
		System.out.println("  properties: " + props);

		configuratorReg = ctx.registerService(EPackageConfigurator.class, configurator, props);
		packageReg = ctx.registerService(EPackage.class, modelPackage, props);
	}

	@AfterEach
	void after() {
		if (configuratorReg != null) {
			configuratorReg.unregister();
		}
		if (packageReg != null) {
			packageReg.unregister();
		}
	}

	/**
	 * Test: Can we get a ResourceSet that contains the dynamically registered test model?
	 * Uses target filter on emf.name to wait for the model to be propagated.
	 */
	@Test
	void testDynamicModelInResourceSet(
			@InjectService(filter = "(emf.name=" + FPM_NAME + ")", timeout = 10000) ServiceAware<ResourceSet> rsAware) {

		System.out.println("=== testDynamicModelInResourceSet ===");
		System.out.println("  ResourceSet services found: " + rsAware.getServices().size());

		assertThat(rsAware.getServices()).as("ResourceSet with fpm model").isNotEmpty();

		ResourceSet rs = rsAware.getService();
		EPackage.Registry registry = rs.getPackageRegistry();

		boolean containsViaKey = registry.containsKey(FPM_NS_URI);
		boolean inKeySet = registry.keySet().contains(FPM_NS_URI);
		Object viaGet = registry.get(FPM_NS_URI);

		System.out.println("  registry class: " + registry.getClass().getName());
		System.out.println("  containsKey(fpm): " + containsViaKey);
		System.out.println("  keySet has fpm:   " + inKeySet);
		System.out.println("  get(fpm):         " + (viaGet != null ? "found" : "NULL"));
		System.out.println("  keySet:           " + registry.keySet());

		assertThat(containsViaKey).as("containsKey finds fpm in ResourceSet registry").isTrue();
		assertThat(viaGet).as("get finds fpm in ResourceSet registry").isNotNull();
	}

	/**
	 * Test: Is the EPackage service itself available with correct filter?
	 */
	@Test
	void testDynamicModelEPackageService(
			@InjectService(filter = "(emf.name=" + FPM_NAME + ")", timeout = 5000) ServiceAware<EPackage> pkgAware) {

		assertThat(pkgAware.getServices()).as("EPackage service for fpm").isNotEmpty();
		EPackage pkg = pkgAware.getService();
		System.out.println("=== testDynamicModelEPackageService ===");
		System.out.println("  name: " + pkg.getName());
		System.out.println("  nsURI: " + pkg.getNsURI());
		assertThat(pkg.getNsURI()).isEqualTo(FPM_NS_URI);
	}
}
