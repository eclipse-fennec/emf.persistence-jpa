/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicPackage;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.util.EORMResourceFactoryImpl;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * 
 * @author mark
 * @since 12.12.2024
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class ORMModelTest {
	
	@TempDir
	private File storage;
	
//	@Test
	public void testSaveMapping() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		rs.getPackageRegistry().put(EORMPackage.eNS_URI, EORMPackage.eINSTANCE);
		rs.getPackageRegistry().put(EPersistencePackage.eNS_URI, EPersistencePackage.eINSTANCE);
		rs.getPackageRegistry().put(BasicPackage.eNS_URI, BasicPackage.eINSTANCE);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("eorm", new EORMResourceFactoryImpl());
		
		String path = "/home/mark/tmp/person.eorm";
//		String path = storage.getAbsolutePath() + "/person.eorm";
		System.out.println(path);
		Resource resource = rs.createResource(URI.createURI(path));
		assertNotNull(resource);
		
		EntityMapper mapper = new EntityMapper();
		EntityMappings mapping = mapper.createMappings(List.of(BasicPackage.Literals.PERSON));
//		EntityMappings mapping = mapper.createMappings(List.of(BasicPackage.Literals.PERSON, 
//				BasicPackage.Literals.CONTACT,
//				BasicPackage.Literals.ADDRESS));
		resource.getContents().add(mapping);
		assertDoesNotThrow(()->resource.save(null));
	}

}
