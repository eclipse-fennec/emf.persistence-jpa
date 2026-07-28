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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceFactory;

import jakarta.persistence.EntityManagerFactory;

/**
 * TCK binding for the JPA/EclipseLink backend (bootstrap via {@link JpaTckSupport}).
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
class JpaPersistenceTckTest extends AbstractPersistenceTCK {

	private static final String PU_NAME = "tck";

	private EntityManagerFactory emf;

	@Override
	protected void setUpBackend(EPackage tckPackage) {
		// Deliberately pass the owning single side (Person.employer) BEFORE the many
		// side (Company.employees): this order used to break the eorm mapper's
		// opposite wiring — kept as regression coverage for the stage-5 normalization
		// in MappingProcessor.createOppositeMapping.
		List<EClassifier> eClasses = new ArrayList<>();
		eClasses.add(tckPackage.getEClassifier("Person"));
		eClasses.add(tckPackage.getEClassifier("Address"));
		eClasses.add(tckPackage.getEClassifier("Company"));
		emf = JpaTckSupport.bootstrap(PU_NAME, eClasses);
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
		resourceSet.getPackageRegistry().put(tckPackage.getNsURI(), tckPackage);
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
