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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavor;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavorCapabilities;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.junit.jupiter.api.Test;

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
		// the interval fixture (issue #215): two nullable bounds that form one interval
		eClasses.add(tckPackage.getEClassifier("Booking"));
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

	/**
	 * {@code Between}/{@code In} over a {@code time()} source bind their values through the
	 * same peer-aware route as a comparison's (issue #267) — without it, EclipseLink's
	 * TIMESTAMP-on-1970-01-01 binding made both silently empty, the shape of issue #265.
	 * The fluent builder cannot spell these (no {@code between}/{@code in} on the function
	 * step), but the model can, and OData's {@code in} operator reaches it through the OCL
	 * bridge — so they are built through the factory here, the way that consumer arrives.
	 * <p>
	 * Driver-local rather than a TCK row: the mongo find vocabulary refuses a computed
	 * {@code Between}/{@code In} source loudly (a {@code QueryException}, not a missing
	 * capability), so a shared row would fail there instead of skipping — the capability
	 * surface question of issue #134.
	 */
	@Test
	public void queryTimeExtractionBetweenAndIn() throws Exception {
		saveQueryFixture();
		// time-of-day fixture: Alice 10:30:45, Bob 23:59:59, Carol 00:00:05 (all UTC)
		Between between = ExpressionFactory.eINSTANCE.createBetween();
		between.setSource(Expressions.path(personBirthday).time().toExpression());
		between.setLower(Expressions.literal(LocalTime.of(10, 0)));
		between.setUpper(Expressions.literal(LocalTime.of(12, 0)));
		between.setLowerIncluded(true);
		between.setUpperIncluded(true);
		Query betweenQuery = QueryBuilder.from(personClass).where(between).build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(betweenQuery)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.as("BETWEEN bounds beside time() bind as SQL TIME, not as a timestamp")
					.containsExactly("Alice");
		}
		In in = ExpressionFactory.eINSTANCE.createIn();
		in.setSource(Expressions.path(personBirthday).time().toExpression());
		in.getValues().add(Expressions.literal(LocalTime.of(23, 59, 59)));
		in.getValues().add(Expressions.literal(LocalTime.of(0, 0, 5)));
		Query inQuery = QueryBuilder.from(personClass).where(in).build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(inQuery)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.as("IN options beside time() bind as SQL TIME, not as a timestamp")
					.containsExactlyInAnyOrder("Bob", "Carol");
		}
	}
}
