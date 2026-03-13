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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Objects;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicPackage;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.util.EORMResourceFactoryImpl;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.annotations.RequireConfigurationAdmin;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@RequireConfigurationAdmin
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public abstract class EPersistenceModelBase {

	@TempDir
	protected File modelPath;
	protected File modelFile;
	private ServiceRegistration<EPackageConfigurator> testConfiguratorRegistration;
	private ServiceRegistration<EPackage> testModelRegistration;
	protected ResourceSet rs;
	protected BundleContext bctx;

	@BeforeEach
	public void before(@InjectBundleContext BundleContext ctx) {
		this.bctx = ctx;
		// initialize ResourceSet
		if (Objects.isNull(rs)) {
			rs = initResourceSet();
		}
		// load local ecore model
		EPackage modelPackage = loadModelPackage(ctx);


		beforeModelRegistered(modelPackage);
		registerEPackageService(ctx, modelPackage);
		afterModelRegistered(modelPackage);
	}
	
	abstract void beforeModelRegistered(EPackage modelPackage);
	abstract void afterModelRegistered(EPackage modelPackage);

	/**
	 * Returns the modelFile.
	 * @return the modelFile
	 */
	public File getModelFile() {
		return modelFile;
	}

	@AfterEach
	public void after() {
		testConfiguratorRegistration.unregister();
		testModelRegistration.unregister();
	}
	
	protected ResourceSet initResourceSet() {

		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		rs.getPackageRegistry().put(EORMPackage.eNS_URI, EORMPackage.eINSTANCE);
		rs.getPackageRegistry().put(EPersistencePackage.eNS_URI, EPersistencePackage.eINSTANCE);
		rs.getPackageRegistry().put(BasicPackage.eNS_URI, BasicPackage.eINSTANCE);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("eorm", new EORMResourceFactoryImpl());
		return rs;
	}
	
	protected String getModelEntryPath() {
		return "/data/model.ecore";
	}

	protected EPackage loadModelPackage(BundleContext ctx) {
		URL entry = ctx.getBundle().getEntry(getModelEntryPath());
		Resource modelResource = rs.createResource(URI.createURI("https://projects.eclipse.org/projects/modeling.fennec/fpm"));
		assertNotNull(modelResource);
		try {
			modelResource.load(entry.openStream(), null);
		} catch (IOException e) {
			fail(e);
		}
		assertFalse(modelResource.getContents().isEmpty());
		EObject eObject = modelResource.getContents().get(0);
		assertNotNull(eObject);
		assertInstanceOf(EPackage.class, eObject);
		EPackage modelPackage = (EPackage) eObject;
		if (!modelPackage.getNsURI().equals(modelResource.getURI().toString())) {
			modelResource.setURI(URI.createURI(modelPackage.getNsURI()));
		}
		assertNotNull(rs);
		rs.getPackageRegistry().put(modelPackage.getNsURI(), modelPackage);
		return modelPackage;
	}

	protected void registerEPackageService(BundleContext ctx, EPackage modelPackage) {
		EPackageConfigurator c = new EPackageConfigurator() {

			@Override
			public void unconfigureEPackage(Registry registry) {
				registry.remove(modelPackage.getNsURI());
			}

			@Override
			public void configureEPackage(Registry registry) {
				registry.put(modelPackage.getNsURI(), modelPackage);
			}
		};
		Dictionary<String, Object> props = Dictionaries.dictionaryOf(EMFNamespaces.EMF_NAME, modelPackage.getName(),
				EMFNamespaces.EMF_MODEL_NSURI, modelPackage.getNsURI(),
				EMFNamespaces.EMF_MODEL_REGISTRATION, EMFNamespaces.MODEL_REGISTRATION_PROVIDED,
				EMFNamespaces.EMF_MODEL_SCOPE, EMFNamespaces.EMF_MODEL_SCOPE_RESOURCE_SET);
		testConfiguratorRegistration = ctx.registerService(EPackageConfigurator.class, c, props);
		testModelRegistration = ctx.registerService(EPackage.class, modelPackage, props);
	}

}
