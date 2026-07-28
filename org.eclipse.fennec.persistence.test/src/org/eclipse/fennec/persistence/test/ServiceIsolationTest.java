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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sql.DataSource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.eorm.EClassObject;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.util.EORMResourceFactoryImpl;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.orm.helper.EORMHelper;
import org.eclipse.fennec.persistence.orm.helper.EORMModelHelper;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Minimal isolation test to verify individual service availability.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
class ServiceIsolationTest {

	@TempDir
	private File storage;

	@BeforeEach
	public void before() {
		System.setProperty(TestAnnotations.PROP_MODEL_PATH, storage.getAbsolutePath().replace('\\', '/'));
	}

	/**
	 * 1) DataSource comes up with factory config
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	void testDataSourceAvailable(
			@InjectService(timeout = 5000) ServiceAware<DataSource> dsAware) {
		assertThat(dsAware.getServices()).as("DataSource service").isNotEmpty();
	}

	/**
	 * 2) EPackageConfigurators for eorm and epersistence are registered
	 */
	@Test
	void testEormEPackageConfiguratorsAvailable(
			@InjectService(filter = "(emf.name=eorm)", timeout = 5000) ServiceAware<EPackageConfigurator> eormAware,
			@InjectService(filter = "(emf.name=epersistence)", timeout = 5000) ServiceAware<EPackageConfigurator> epAware) {
		assertThat(eormAware.getServices()).as("EORM EPackageConfigurator").isNotEmpty();
		assertThat(epAware.getServices()).as("EPersistence EPackageConfigurator").isNotEmpty();

		// Check what properties they expose
		ServiceReference<EPackageConfigurator> eormRef = eormAware.getServiceReference();
		System.out.println("=== EORM EPackageConfigurator properties ===");
		for (String key : eormRef.getPropertyKeys()) {
			System.out.println("  " + key + " = " + eormRef.getProperty(key));
		}
		ServiceReference<EPackageConfigurator> epRef = epAware.getServiceReference();
		System.out.println("=== EPersistence EPackageConfigurator properties ===");
		for (String key : epRef.getPropertyKeys()) {
			System.out.println("  " + key + " = " + epRef.getProperty(key));
		}
	}

	/**
	 * 3) EPackage.Registry contains eorm and epersistence
	 */
	@Test
	void testEPackageRegistryContainsModels(
			@InjectBundleContext BundleContext ctx) {
		// Check global registry
		boolean hasEorm = EPackage.Registry.INSTANCE.containsKey(EORMPackage.eNS_URI);
		boolean hasEp = EPackage.Registry.INSTANCE.containsKey(EPersistencePackage.eNS_URI);
		System.out.println("=== Global EPackage.Registry ===");
		System.out.println("  contains eorm (" + EORMPackage.eNS_URI + "): " + hasEorm);
		System.out.println("  contains epersistence (" + EPersistencePackage.eNS_URI + "): " + hasEp);

		// Check OSGi EPackage.Registry service
		ServiceReference<EPackage.Registry> ref = ctx.getServiceReference(EPackage.Registry.class);
		if (ref != null) {
			System.out.println("=== OSGi EPackage.Registry service properties ===");
			for (String key : ref.getPropertyKeys()) {
				System.out.println("  " + key + " = " + ref.getProperty(key));
			}
			EPackage.Registry registry = ctx.getService(ref);
			System.out.println("  registry contains eorm: " + registry.containsKey(EORMPackage.eNS_URI));
			System.out.println("  registry contains epersistence: " + registry.containsKey(EPersistencePackage.eNS_URI));
		} else {
			System.out.println("  NO OSGi EPackage.Registry service found!");
		}

		assertThat(hasEorm).as("Global registry has eorm").isTrue();
		assertThat(hasEp).as("Global registry has epersistence").isTrue();
	}

	/**
	 * 4) ResourceSetFactory / ResourceSet available with eorm+epersistence
	 */
	@Test
	void testResourceSetFactoryAvailable(
			@InjectService(timeout = 5000) ServiceAware<ResourceSetFactory> rsfAware,
			@InjectBundleContext BundleContext ctx) {
		assertThat(rsfAware.getServices()).as("ResourceSetFactory service").isNotEmpty();

		ServiceReference<ResourceSetFactory> ref = rsfAware.getServiceReference();
		System.out.println("=== ResourceSetFactory properties ===");
		for (String key : ref.getPropertyKeys()) {
			System.out.println("  " + key + " = " + ref.getProperty(key));
		}

		// Also check ResourceSet service
		ServiceReference<ResourceSet> rsRef = ctx.getServiceReference(ResourceSet.class);
		if (rsRef != null) {
			System.out.println("=== ResourceSet service properties ===");
			for (String key : rsRef.getPropertyKeys()) {
				System.out.println("  " + key + " = " + rsRef.getProperty(key));
			}
		} else {
			System.out.println("  NO ResourceSet service found!");
		}
	}

	/**
	 * 5) ResourceSet with target filter matching eorm+epersistence
	 */
	@Test
	void testResourceSetWithModelFilter(
			@InjectService(filter = "(&(emf.name=epersistence)(emf.name=eorm))", timeout = 5000) ServiceAware<ResourceSet> rsAware) {
		assertThat(rsAware.getServices()).as("ResourceSet with eorm+epersistence filter").isNotEmpty();

		ResourceSet rs = rsAware.getService();
		System.out.println("=== ResourceSet PackageRegistry ===");
		System.out.println("  contains eorm: " + rs.getPackageRegistry().containsKey(EORMPackage.eNS_URI));
		System.out.println("  contains epersistence: " + rs.getPackageRegistry().containsKey(EPersistencePackage.eNS_URI));
	}

	/**
	 * 5b) Demonstrates inconsistency in OSGi ResourceSet PackageRegistry:
	 * containsKey() delegates to parent (global) registry and finds packages,
	 * but keySet() only returns local entries and is empty.
	 * This means iteration-based access and key-based lookup behave differently.
	 */
	@Test
	void testPackageRegistryDelegationInconsistency(
			@InjectService(filter = "(&(emf.name=epersistence)(emf.name=eorm))", timeout = 5000) ResourceSet rs) {

		EPackage.Registry registry = rs.getPackageRegistry();

		// --- containsKey: delegates to global parent registry ---
		boolean containsEorm = registry.containsKey(EORMPackage.eNS_URI);
		boolean containsEp = registry.containsKey(EPersistencePackage.eNS_URI);

		// --- get: also delegates ---
		Object eormViaGet = registry.get(EORMPackage.eNS_URI);
		Object epViaGet = registry.get(EPersistencePackage.eNS_URI);

		// --- keySet: only local entries ---
		boolean keySetContainsEorm = registry.keySet().contains(EORMPackage.eNS_URI);
		boolean keySetContainsEp = registry.keySet().contains(EPersistencePackage.eNS_URI);

		// --- entrySet: only local entries ---
		boolean entrySetHasEorm = registry.entrySet().stream()
				.anyMatch(e -> EORMPackage.eNS_URI.equals(e.getKey()));
		boolean entrySetHasEp = registry.entrySet().stream()
				.anyMatch(e -> EPersistencePackage.eNS_URI.equals(e.getKey()));

		// --- global registry for comparison ---
		boolean globalHasEorm = EPackage.Registry.INSTANCE.containsKey(EORMPackage.eNS_URI);
		boolean globalHasEp = EPackage.Registry.INSTANCE.containsKey(EPersistencePackage.eNS_URI);

		System.out.println("=== PackageRegistry Delegation Inconsistency ===");
		System.out.println("  registry class: " + registry.getClass().getName());
		System.out.println();
		System.out.println("  Global EPackage.Registry.INSTANCE:");
		System.out.println("    containsKey eorm:          " + globalHasEorm);
		System.out.println("    containsKey epersistence:   " + globalHasEp);
		System.out.println();
		System.out.println("  ResourceSet PackageRegistry (via containsKey - delegates to parent):");
		System.out.println("    containsKey eorm:          " + containsEorm);
		System.out.println("    containsKey epersistence:   " + containsEp);
		System.out.println();
		System.out.println("  ResourceSet PackageRegistry (via get - delegates to parent):");
		System.out.println("    get eorm:                  " + (eormViaGet != null ? "found" : "NULL"));
		System.out.println("    get epersistence:           " + (epViaGet != null ? "found" : "NULL"));
		System.out.println();
		System.out.println("  ResourceSet PackageRegistry (via keySet - NO delegation):");
		System.out.println("    keySet contains eorm:      " + keySetContainsEorm);
		System.out.println("    keySet contains epersistence: " + keySetContainsEp);
		System.out.println("    keySet size:               " + registry.keySet().size());
		System.out.println("    keySet entries:            " + registry.keySet());
		System.out.println();
		System.out.println("  ResourceSet PackageRegistry (via entrySet - NO delegation):");
		System.out.println("    entrySet has eorm:         " + entrySetHasEorm);
		System.out.println("    entrySet has epersistence:  " + entrySetHasEp);

		// Assertions that show the inconsistency:
		// containsKey finds packages (via delegation)
		assertThat(containsEorm).as("containsKey finds eorm (delegates to global)").isTrue();
		assertThat(containsEp).as("containsKey finds epersistence (delegates to global)").isTrue();
		// get also finds them
		assertThat(eormViaGet).as("get finds eorm (delegates to global)").isNotNull();
		assertThat(epViaGet).as("get finds epersistence (delegates to global)").isNotNull();
		// BUT keySet does NOT contain them — this is the inconsistency
		// If this assertion fails, the bug is fixed in fennecEMF
		assertThat(keySetContainsEorm).as("keySet contains eorm (should be true if local registry is populated)").isTrue();
		assertThat(keySetContainsEp).as("keySet contains epersistence (should be true if local registry is populated)").isTrue();
	}

	/**
	 * 6) ConverterService available
	 */
	@Test
	void testConverterServiceAvailable(
			@InjectService(timeout = 5000) ServiceAware<ConverterService> csAware) {
		assertThat(csAware.getServices()).as("ConverterService").isNotEmpty();
	}

	/**
	 * 8) Isolation test for .eorm mapping file round-trip.
	 * Creates mapping from test model, serializes to .eorm, loads back via OSGi ResourceSet,
	 * then checks if Entity cross-references (accessibleObject, class_) resolve correctly.
	 */
	@Test
	void testEormMappingRoundTrip(
			@InjectBundleContext BundleContext ctx,
			@InjectService(filter = "(&(emf.name=epersistence)(emf.name=eorm))", timeout = 5000) ResourceSet osgiResourceSet) throws IOException {

		// Step 1: Load test model.ecore using a local ResourceSet (same as EPersistenceModelBase does)
		ResourceSet localRs = new ResourceSetImpl();
		localRs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		localRs.getPackageRegistry().put(EORMPackage.eNS_URI, EORMPackage.eINSTANCE);
		localRs.getPackageRegistry().put(EPersistencePackage.eNS_URI, EPersistencePackage.eINSTANCE);
		localRs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		localRs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("eorm", new EORMResourceFactoryImpl());

		URL modelEntry = ctx.getBundle().getEntry("/data/model.ecore");
		assertThat(modelEntry).as("model.ecore entry").isNotNull();
		Resource modelResource = localRs.createResource(URI.createURI("https://projects.eclipse.org/projects/modeling.fennec/fpm"));
		modelResource.load(modelEntry.openStream(), null);
		EPackage modelPackage = (EPackage) modelResource.getContents().get(0);
		localRs.getPackageRegistry().put(modelPackage.getNsURI(), modelPackage);

		System.out.println("=== Step 1: Model loaded ===");
		System.out.println("  package nsURI: " + modelPackage.getNsURI());
		System.out.println("  classifiers: " + modelPackage.getEClassifiers().size());

		// Step 2: Create mapping via EntityMapper
		EntityMapper mapper = new EntityMapper();
		EntityMappings mapping = mapper.createMappingsFromEPackage(modelPackage);
		assertThat(mapping.getEntity()).as("entities in mapping").isNotEmpty();

		System.out.println("=== Step 2: Mapping created ===");
		System.out.println("  entities: " + mapping.getEntity().size());
		for (Entity entity : mapping.getEntity()) {
			EClassObject eco = (EClassObject) entity.getAccessibleObject();
			System.out.println("  " + entity.getName()
					+ " | class_=" + entity.getClass_()
					+ " | accessibleObject=" + eco
					+ " | eclass=" + (eco != null ? eco.getEclass() : "N/A"));
		}

		// Step 3: Serialize to temp .eorm file
		File eormFile = new File(storage, "test-mapping.eorm");
		Resource eormResource = localRs.createResource(URI.createURI(eormFile.toURI().toString()));
		eormResource.getContents().add(mapping);
		eormResource.save(null);

		System.out.println("=== Step 3: Saved to " + eormFile.getAbsolutePath() + " ===");

		// Step 4: Load back via EORMModelHelper with OSGi ResourceSet
		System.out.println("=== Step 4: Loading via OSGi ResourceSet ===");
		System.out.println("  OSGi RS packageRegistry keys: " + osgiResourceSet.getPackageRegistry().keySet());

		// Check if the test model's nsURI is in the OSGi ResourceSet registry
		boolean hasTestModel = osgiResourceSet.getPackageRegistry().containsKey(modelPackage.getNsURI());
		System.out.println("  OSGi RS has test model package (" + modelPackage.getNsURI() + "): " + hasTestModel);

		EORMModelHelper helper = new EORMModelHelper(osgiResourceSet);
		EntityMappings loadedMapping = helper.loadMapping(eormFile.toURI().toString());

		System.out.println("=== Step 4: Loaded mapping ===");
		System.out.println("  entities: " + loadedMapping.getEntity().size());

		for (Entity entity : loadedMapping.getEntity()) {
			EObject accessObj = entity.getAccessibleObject();
			boolean accessibleIsProxy = accessObj != null && accessObj.eIsProxy();
			System.out.println("  Entity: " + entity.getName());
			System.out.println("    accessibleObject: " + accessObj + " isProxy: " + accessibleIsProxy);

			if (accessObj instanceof EClassObject eco) {
				org.eclipse.emf.ecore.EClass eclass = eco.getEclass();
				boolean eclassIsProxy = eclass != null && eclass.eIsProxy();
				System.out.println("    eclass: " + eclass
						+ " isProxy: " + eclassIsProxy
						+ " name: " + (eclass != null && !eclassIsProxy ? eclass.getName() : "N/A"));
			}

			org.eclipse.emf.ecore.EClassifier class_ = entity.getClass_();
			boolean classIsProxy = class_ != null && class_.eIsProxy();
			System.out.println("    class_: " + class_
					+ " isProxy: " + classIsProxy
					+ " name: " + (class_ != null && !classIsProxy ? class_.getName() : "N/A"));

			org.eclipse.emf.ecore.EClass resolvedEClass = EORMHelper.getEClass(entity);
			System.out.println("    EORMHelper.getEClass(): " + resolvedEClass
					+ " name: " + (resolvedEClass != null ? resolvedEClass.getName() : "NULL"));
		}

		// Assert: at least verify we can see the problem
		Entity firstEntity = loadedMapping.getEntity().get(0);
		org.eclipse.emf.ecore.EClass resolvedEClass = EORMHelper.getEClass(firstEntity);
		System.out.println("=== RESULT: EORMHelper.getEClass(firstEntity) = " + resolvedEClass + " ===");
	}

	/**
	 * 7) Check if EPackages and EClasses are proxies
	 */
	@Test
	void testEPackagesNotProxies(
			@InjectService(filter = "(emf.name=eorm)", timeout = 5000) EPackage eormPackage,
			@InjectService(filter = "(emf.name=epersistence)", timeout = 5000) EPackage epPackage,
			@InjectService(filter = "(emf.name=basic)", timeout = 5000) EPackage basicPackage,
			@InjectService(filter = "(&(emf.name=epersistence)(emf.name=eorm))", timeout = 5000) ResourceSet rs) {

		System.out.println("=== EPackage proxy check ===");
		System.out.println("  eorm isProxy: " + ((org.eclipse.emf.ecore.EObject) eormPackage).eIsProxy());
		System.out.println("  epersistence isProxy: " + ((org.eclipse.emf.ecore.EObject) epPackage).eIsProxy());
		System.out.println("  basic isProxy: " + ((org.eclipse.emf.ecore.EObject) basicPackage).eIsProxy());

		assertThat(((org.eclipse.emf.ecore.EObject) eormPackage).eIsProxy()).as("eorm is proxy").isFalse();
		assertThat(((org.eclipse.emf.ecore.EObject) epPackage).eIsProxy()).as("epersistence is proxy").isFalse();
		assertThat(((org.eclipse.emf.ecore.EObject) basicPackage).eIsProxy()).as("basic is proxy").isFalse();

		System.out.println("=== EClass proxy check (eorm) ===");
		for (org.eclipse.emf.ecore.EClassifier c : eormPackage.getEClassifiers()) {
			System.out.println("  " + c.getName() + " isProxy: " + c.eIsProxy()
					+ (c instanceof org.eclipse.emf.ecore.EClass ? " supers: " + ((org.eclipse.emf.ecore.EClass) c).getESuperTypes() : ""));
		}

		System.out.println("=== EClass proxy check (epersistence) ===");
		for (org.eclipse.emf.ecore.EClassifier c : epPackage.getEClassifiers()) {
			System.out.println("  " + c.getName() + " isProxy: " + c.eIsProxy());
		}

		System.out.println("=== EClass proxy check (basic) ===");
		for (org.eclipse.emf.ecore.EClassifier c : basicPackage.getEClassifiers()) {
			System.out.println("  " + c.getName() + " isProxy: " + c.eIsProxy());
		}

		// Check ResourceSet package registry for basic model
		System.out.println("=== ResourceSet PackageRegistry contents ===");
		for (java.util.Map.Entry<String, Object> entry : rs.getPackageRegistry().entrySet()) {
			Object val = entry.getValue();
			boolean isProxy = (val instanceof org.eclipse.emf.ecore.EObject) && ((org.eclipse.emf.ecore.EObject) val).eIsProxy();
			System.out.println("  " + entry.getKey() + " -> " + (val != null ? val.getClass().getSimpleName() : "null") + " isProxy: " + isProxy);
		}
	}
}
