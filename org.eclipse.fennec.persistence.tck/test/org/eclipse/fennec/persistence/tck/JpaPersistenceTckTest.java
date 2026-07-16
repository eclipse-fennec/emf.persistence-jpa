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
package org.eclipse.fennec.persistence.tck;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicPersistenceUnitInfo;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeGenerator;
import org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceFactory;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;

import jakarta.persistence.EntityManagerFactory;

/**
 * TCK binding for the JPA/EclipseLink backend — bootstrap mirrors the non-OSGi setup of
 * {@code NonOsgiPersistenceTestBase} in {@code org.eclipse.fennec.persistence.test}.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
class JpaPersistenceTckTest extends AbstractPersistenceTCK {

	private static final String PU_NAME = "tck";

	private EntityManagerFactory emf;
	private DynamicClassLoader dcl;

	@Override
	protected void setUpBackend(EPackage tckPackage) {
		// Order matters for eOpposite pairs in the eorm mapper: the many side of a
		// bidirectional pair must be processed before the owning single side.
		List<EClassifier> eClasses = new ArrayList<>();
		eClasses.add(tckPackage.getEClassifier("Company"));
		eClasses.add(tckPackage.getEClassifier("Address"));
		eClasses.add(tckPackage.getEClassifier("Person"));
		EntityMapper mapper = new EntityMapper();
		EntityMappings mappings = mapper.createMappings(eClasses);

		dcl = new DynamicClassLoader(getClass().getClassLoader());
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.DDL_GENERATION, "create-or-extend-tables");
		props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, "database");
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:mem:tck_" + UUID.randomUUID());
		props.put(PersistenceUnitProperties.JDBC_USER, "sa");
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
		props.put(PersistenceUnitProperties.CLASSLOADER, dcl);

		PersistenceUnit persistenceUnit = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		persistenceUnit.setName(PU_NAME);
		persistenceUnit.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
		URL puRoot = getClass().getProtectionDomain().getCodeSource().getLocation();
		EDynamicPersistenceUnitInfo unitInfo = new EDynamicPersistenceUnitInfo(persistenceUnit, puRoot, props);

		PersistenceProvider provider = new PersistenceProvider();
		emf = provider.createContainerEntityManagerFactory(unitInfo, props);

		ConverterService converter = new DefaultConverterService() { /* concrete instance */ };
		EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl,
				JpaHelper.getServerSession(emf), PU_NAME, converter);
		List<EDynamicType> types = generator.createFromMappings(List.of(mappings));

		EDynamicHelper helper = new EDynamicHelper(emf, dcl);
		helper.addETypes(true, true, types);
	}

	@Override
	protected void tearDownBackend() {
		if (emf != null) {
			emf.close();
			emf = null;
		}
	}

	@Override
	protected ResourceSet createBackendResourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		return resourceSet;
	}

	@Override
	protected URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}
}
