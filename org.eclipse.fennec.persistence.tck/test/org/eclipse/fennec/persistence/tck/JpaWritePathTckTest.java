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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavor;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavorCapabilities;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.jpa.JpaHelper;

import jakarta.persistence.EntityManagerFactory;

/**
 * The write-path conformance suite (issue #329) on the JPA backend, for the flavor selected by
 * {@code -Djpa.test.flavor}. Every case gets a fresh database (h2) or schema (PostgreSQL,
 * MariaDB), and its store probe reads it over JDBC.
 */
class JpaWritePathTckTest extends AbstractWritePathTCK {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "wp";

	private EntityManagerFactory emf;
	private Map<String, Object> jdbcProperties;

	@Override
	protected PersistenceCapabilities declaredCapabilities() {
		return JpaFlavorCapabilities.persistenceCapabilities(JpaFlavor.byId(JpaTestSupport.flavor())
				.orElseThrow(() -> new IllegalArgumentException("Unknown -Djpa.test.flavor=" + JpaTestSupport.flavor())));
	}

	@Override
	protected void setUpBackend(EPackage writePathPackage) {
		List<EClassifier> eClasses = new ArrayList<>();
		writePathPackage.getEClassifiers().stream().filter(EClass.class::isInstance).forEach(eClasses::add);
		EntityMappings mappings = new EntityMapper().createMappings(eClasses);
		jdbcProperties = JpaTestSupport.jdbcProperties(PU_NAME);
		emf = JpaTckSupport.bootstrap(PU_NAME, mappings, jdbcProperties, "create-or-extend-tables");
	}

	@Override
	protected void tearDownBackend() {
		if (emf != null) {
			emf.close();
			emf = null;
		}
	}

	@Override
	protected void evictBackendCaches() {
		emf.getCache().evictAll();
	}

	@Override
	protected ResourceSet createBackendResourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(wp.getNsURI(), wp);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	@Override
	protected URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}

	@Override
	protected StoreProbe storeProbe() {
		return new JpaStoreProbe(JpaHelper.getServerSession(emf), jdbcProperties);
	}
}
