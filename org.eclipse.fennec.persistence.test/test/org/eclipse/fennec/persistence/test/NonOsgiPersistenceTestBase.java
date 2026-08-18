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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicPersistenceUnitInfo;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeGenerator;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.AfterEach;

import jakarta.persistence.EntityManagerFactory;

/**
 * Base class for Non-OSGi integration tests against an in-memory H2 instance.
 * <p>
 * Subclasses load an Ecore model and call {@link #bootstrapPersistence(String, Collection)}
 * (or an overload) to obtain a ready-to-use {@link EntityManagerFactory} with dynamic
 * types registered in the EclipseLink session.
 */
public abstract class NonOsgiPersistenceTestBase {

	protected ResourceSet rs;
	protected DynamicClassLoader dcl;
	protected EntityManagerFactory emf;
	protected Server serverSession;
	protected String persistenceUnitName;

	protected ResourceSet initResourceSet() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		return rs;
	}

	/**
	 * Loads an Ecore file into the managed {@link ResourceSet} and registers its
	 * {@link EPackage} with the package registry.
	 */
	protected EPackage loadEcore(String ecorePath) {
		if (rs == null) {
			rs = initResourceSet();
		}
		File ecoreFile = new File(ecorePath);
		assertThat(ecoreFile).as("Ecore file must exist: %s", ecorePath).exists();
		Resource ecoreResource = rs.createResource(URI.createFileURI(ecoreFile.getAbsolutePath()));
		try {
			ecoreResource.load(null);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to load Ecore: " + ecorePath, e);
		}
		EPackage pkg = (EPackage) ecoreResource.getContents().get(0);
		rs.getPackageRegistry().put(pkg.getNsURI(), pkg);
		return pkg;
	}

	/**
	 * Returns the default H2-in-memory properties. Subclasses can override to add or
	 * change properties. The JDBC URL uses a random database name so parallel tests
	 * do not collide.
	 */
	protected Map<String, Object> defaultProperties() {
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.DDL_GENERATION, "create-or-extend-tables");
		props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, "database");
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:mem:fennec_" + UUID.randomUUID());
		props.put(PersistenceUnitProperties.JDBC_USER, "sa");
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
		return props;
	}

	/**
	 * Bootstraps an in-memory {@link EntityManagerFactory} for the given EClassifiers
	 * using defaults from {@link #defaultProperties()}.
	 */
	protected void bootstrapPersistence(String puName, Collection<? extends EClassifier> eClasses) {
		bootstrapPersistence(puName, eClasses, Map.of());
	}

	/**
	 * Bootstraps an in-memory {@link EntityManagerFactory} for all {@link EClass}es of
	 * the given EPackage.
	 */
	protected void bootstrapPersistenceFromPackage(String puName, EPackage modelPackage) {
		List<EClassifier> classes = modelPackage.getEClassifiers().stream()
				.filter(EClass.class::isInstance)
				.map(EClassifier.class::cast)
				.toList();
		bootstrapPersistence(puName, classes, Map.of());
	}

	/**
	 * Bootstraps an in-memory {@link EntityManagerFactory} with custom overrides on top
	 * of {@link #defaultProperties()}.
	 */
	protected void bootstrapPersistence(String puName, Collection<? extends EClassifier> eClasses,
			Map<String, Object> extraProps) {
		this.persistenceUnitName = puName;
		if (rs == null) {
			rs = initResourceSet();
		}

		EntityMapper mapper = new EntityMapper();
		configureMapper(mapper);
		EntityMappings mappings = mapper.createMappings(new ArrayList<>(eClasses));

		dcl = new DynamicClassLoader(getClass().getClassLoader());
		Map<String, Object> props = new HashMap<>(defaultProperties());
		props.putAll(extraProps);
		props.put(PersistenceUnitProperties.CLASSLOADER, dcl);

		PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		pu.setName(puName);
		pu.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
		URL puRoot = getClass().getProtectionDomain().getCodeSource().getLocation();
		EDynamicPersistenceUnitInfo pui = new EDynamicPersistenceUnitInfo(pu, puRoot, props);

		PersistenceProvider provider = new PersistenceProvider();
		emf = provider.createContainerEntityManagerFactory(pui, props);
		serverSession = JpaHelper.getServerSession(emf);

		ConverterService converter = new DefaultConverterService();
		EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl, serverSession, puName, converter);
		List<EDynamicType> types = generator.createFromMappings(List.of(mappings));

		EDynamicHelper helper = new EDynamicHelper(emf, dcl);
		helper.addETypes(true, true, types);
	}

	/**
	 * Hook for subclasses to customize the {@link EntityMapper} (e.g. toggle
	 * {@code strict} mode). Default is a no-op.
	 */
	protected void configureMapper(EntityMapper mapper) {
		// no-op by default
	}

	/**
	 * Builds a ResourceSet wired with the {@link JPAResourceFactory} for this test's EMF
	 * and an XMI factory for file:-URIs. Lazy non-containment proxies resolve via this RS.
	 */
	protected ResourceSet newJpaResourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		return resourceSet;
	}

	/**
	 * Loads the {@code jpa://puName/entityName} resource and returns the first EObject
	 * whose EMF id equals the string form of {@code id}. A fresh {@link ResourceSet} is
	 * created per call — use {@link #findViaResource(ResourceSet, String, Object)} to
	 * share a ResourceSet across lookups (needed for cross-resource identity).
	 */
	protected EObject findViaResource(String entityName, Object id) {
		return findViaResource(newJpaResourceSet(), entityName, id);
	}

	/**
	 * Loads the resource for {@code entityName} in the supplied {@link ResourceSet}
	 * (or re-uses it if already loaded) and returns the EObject with the given id.
	 */
	protected EObject findViaResource(ResourceSet resourceSet, String entityName, Object id) {
		URI uri = URI.createURI("jpa://" + persistenceUnitName + "/" + entityName);
		Resource resource = resourceSet.getResource(uri, false);
		if (resource == null) {
			resource = resourceSet.createResource(uri);
		}
		if (!resource.isLoaded()) {
			try {
				resource.load(null);
			} catch (Exception e) {
				throw new IllegalStateException("Failed to load resource for " + entityName, e);
			}
		}
		String expected = String.valueOf(id);
		for (EObject eo : resource.getContents()) {
			if (expected.equals(EcoreUtil.getID(eo))) {
				return eo;
			}
		}
		return null;
	}

	@AfterEach
	public void tearDownPersistence() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
		emf = null;
		serverSession = null;
	}
}
