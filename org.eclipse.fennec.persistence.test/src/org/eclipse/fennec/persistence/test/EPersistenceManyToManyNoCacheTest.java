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

import java.io.IOException;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.junit.jupiter.api.Test;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;

import jakarta.persistence.EntityManagerFactory;

/**
 * Same test like the super class but with a no-cache configuration. This should lead to same results
 * @author Mark Hoffmann
 * @since 21.01.2025
 */
public class EPersistenceManyToManyNoCacheTest extends EPersistenceManyToManyTest {

	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testManyToManyUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testManyToManyUni(dataSourceAware, modelPackageAware, emfAware);
	}
	
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testManyToManyNoEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testManyToManyNoEOpposite(dataSourceAware, modelPackageAware, emfAware);
	}
	
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testManyToManyEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testManyToManyEOpposite(dataSourceAware, modelPackageAware, emfAware);
	}

}
