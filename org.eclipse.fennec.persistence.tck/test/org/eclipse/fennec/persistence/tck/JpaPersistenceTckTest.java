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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.capabilities.CommandCapabilitiesBuilder;
import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.capabilities.StoreCapabilitiesBuilder;
import org.eclipse.fennec.persistence.capabilities.StoreFeature;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavor;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavorCapabilities;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;

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

	/**
	 * The declaration of the backend and flavor under test, read from the backend rather
	 * than assembled here (issue #172): the binding names the flavor, the backend answers
	 * what it serves. Before this, the query set came from the processor and the command and
	 * store sets were restated in the test — a second place to keep in sync, and one that
	 * could disagree with what the runtime declares.
	 * <p>
	 * Answerable without a connection, which is what makes it the gate for
	 * {@link RequiresCapabilities}; {@code effectiveCapabilitiesNeverExceedTheDeclaration}
	 * holds it against the live resource.
	 */
	@Override
	protected PersistenceCapabilities declaredCapabilities() {
		return JpaFlavorCapabilities.persistenceCapabilities(flavor());
	}

	/**
	 * The database flavor under test, from {@code -Djpa.test.flavor}. Unknown ids fail
	 * loudly: silently testing H2 while believing it is PostgreSQL would make a green run
	 * meaningless — the same rule the mongo binding follows.
	 */
	private static JpaFlavor flavor() {
		return JpaFlavor.byId(JpaTestSupport.flavor())
				.orElseThrow(() -> new IllegalArgumentException(
						"Unknown -Djpa.test.flavor=" + JpaTestSupport.flavor()));
	}

	/**
	 * A relational store has a schema, so a URI naming a type it does not map is a load the
	 * backend cannot answer: no descriptor, nothing to read (issue #197).
	 */
	@Override
	protected Resource provokeLoadDiagnostic() throws Exception {
		Resource resource = createBackendResourceSet()
				.createResource(URI.createURI("jpa://" + PU_NAME + "/NoSuchEntity"));
		try {
			resource.load(null);
			// both backends populate lazily: load() only marks the request, and the work —
			// including whatever it has to report — happens on first contents access
			resource.getContents();
		} catch (IOException | RuntimeException signalled) {
			// conforming: the diagnostics stay on the resource either way
		}
		return resource;
	}

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
		eClasses.add(tckPackage.getEClassifier("Vehicle"));
		eClasses.add(tckPackage.getEClassifier("Car"));
		eClasses.add(tckPackage.getEClassifier("Motorcycle"));
		eClasses.add(tckPackage.getEClassifier("OrderLine"));
		// the map entry classes (issue #185) — a map is a containment reference like any other,
		// so its target type has to be bootstrapped with the rest
		eClasses.add(tckPackage.getEClassifier("StringToStringMapEntry"));
		eClasses.add(tckPackage.getEClassifier("IntToStringMapEntry"));
		// Place/GeoPoint were reachable only from the geo tests, which mongo alone runs — so
		// single-valued containment and the model's multi-valued attribute had never been
		// mapped relationally at all (issue #174, §8)
		eClasses.add(tckPackage.getEClassifier("Place"));
		eClasses.add(tckPackage.getEClassifier("GeoPoint"));
		// carries the EMF-semantics features §8 found untested: a plain multi-valued
		// attribute, an unsettable one, and one with a default (issue #174)
		eClasses.add(tckPackage.getEClassifier("Profile"));
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
	protected void evictBackendCaches() {
		// EclipseLink's shared cache sits on the factory and outlives any ResourceSet, so
		// without this a "fresh ResourceSet" read can be answered from memory.
		emf.getCache().evictAll();
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
