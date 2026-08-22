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

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistries;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryWriter;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceImpl;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.fennec.persistence.query.support.NamedOperations;
import org.eclipse.fennec.persistence.query.support.RegistryNamedOperations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * A named query resolved through the shared catalog instead of the backend's own table
 * (issue #203).
 * <p>
 * The point is what the query does <b>not</b> touch: it never goes through
 * {@code FENNEC_QUERIES}, so the same name works on a backend whose store knows nothing
 * about it. That is what replaces the per-backend conventions — mongo's collection, JPA's
 * table, and the third one the Lucene backend was about to invent.
 */
class JpaSharedCatalogTest {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "sharedcatalog";

	private EMapTestModel model;
	private EntityManagerFactory emf;
	private EObjectRegistryWriter registry;
	private NamedOperations catalog;

	@BeforeEach
	void setUp() {
		model = new EMapTestModel();
		emf = JpaTckSupport.bootstrap(PU_NAME, model.classifiers());
		registry = EObjectRegistries.createRegistry("shared-catalog-test");
		catalog = new RegistryNamedOperations(registry);
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
			emf = null;
		}
	}

	@Test
	void aQueryDepositedInTheCatalogRunsByName() throws Exception {
		EObject spring = model.newCatalog("c1", "Spring");
		EObject summer = model.newCatalog("c2", "Summer");
		Resource written = resourceSet(catalog).createResource(uriFor("Catalog"));
		written.getContents().add(spring);
		written.getContents().add(summer);
		written.save(null);

		// deposited directly — this query is never executed with saveQuery, so nothing
		// could have written it into the backend's own table
		Query byName = QueryBuilder.from(model.catalogClass)
				.where(Expressions.path(model.catalogClass.getEStructuralFeature("name"))
						.eq(Expressions.param("wanted")))
				.parameter("wanted", null)
				.build();
		catalog.store("byName", byName);

		emf.getCache().evictAll();
		QueryableResource resource = queryable(catalog);
		try (QueryResult result = resource.query("byName", Map.of("wanted", "Summer"), null)) {
			assertThat(result.objects()
					.map(c -> c.eGet(model.catalogClass.getEStructuralFeature("cid"))))
					.containsExactly("c2");
		}
	}

	/**
	 * Without the catalog the same name is unknown — which is the proof that the previous
	 * case really resolved through it and not through the store.
	 */
	@Test
	void withoutTheCatalogTheNameIsUnknown() throws Exception {
		catalog.store("byName", QueryBuilder.from(model.catalogClass).build());

		QueryableResource plain = queryable(null);
		assertThatThrownBy(() -> plain.query("byName", Map.of(), null).close())
				.isInstanceOf(IOException.class);
	}

	private QueryableResource queryable(NamedOperations operations) {
		return (QueryableResource) resourceSet(operations).createResource(uriFor("Catalog"));
	}

	private ResourceSet resourceSet(NamedOperations operations) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EMapTestModel.NS_URI, model.ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf) {
					@Override
					public Resource createResource(URI uri) {
						Resource resource = super.createResource(uri);
						if (nonNull(operations) && resource instanceof JPAResourceImpl jpa) {
							jpa.setNamedOperations(operations);
						}
						return resource;
					}
				});
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}
}
