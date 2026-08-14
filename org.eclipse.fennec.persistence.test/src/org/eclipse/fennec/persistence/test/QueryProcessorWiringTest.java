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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.model.command.CommandFactory;
import org.eclipse.fennec.model.command.DeleteCommand;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.capabilities.QueryCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryCapabilitiesBuilder;
import org.eclipse.fennec.persistence.query.QueryConstants;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.eclipse.fennec.persistence.query.api.QueryPlan;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;

/**
 * OSGi integration test for the QueryProcessor service wiring (issue #61):
 * <ul>
 * <li>both backend processors are registered as services carrying the
 *     {@link QueryConstants#BACKEND_PROPERTY} for selection;</li>
 * <li>the {@code jpa} whiteboard factory hands the (greedily rebound) service to every
 *     resource it creates — proven by registering a higher-ranked marker processor whose
 *     refusal surfaces through a resource created afterwards.</li>
 * </ul>
 */
public class QueryProcessorWiringTest {

	@Test
	public void backendProcessorsAreRegisteredAsServices(
			@InjectService(filter = "(" + QueryConstants.BACKEND_PROPERTY + "=jpa)", timeout = 5000)
			ServiceAware<QueryProcessor> jpaAware,
			@InjectService(filter = "(" + QueryConstants.BACKEND_PROPERTY + "=mongo)", timeout = 5000)
			ServiceAware<QueryProcessor> mongoAware) {
		QueryProcessor jpa = jpaAware.getService();
		assertNotNull(jpa, "The jpa QueryProcessor must be registered");
		assertEquals("jpa", jpa.backend());
		assertNotNull(jpa.capabilities());

		QueryProcessor mongo = mongoAware.getService();
		assertNotNull(mongo, "The mongo QueryProcessor must be registered");
		assertEquals("mongo", mongo.backend());
		assertNotNull(mongo.capabilities());
	}

	/**
	 * A marker processor registered with a higher ranking must win the factory's greedy
	 * optional reference: a resource created afterwards refuses every command with the
	 * marker message. Command translation happens before any persistence-unit lease, so
	 * no database setup is needed.
	 */
	@Test
	public void whiteboardFactoryHandsTheServiceToCreatedResources(
			@InjectService(filter = "(emf.protocol=jpa)", timeout = 5000) ServiceAware<Resource.Factory> factoryAware,
			@InjectBundleContext BundleContext ctx) throws Exception {
		Resource.Factory jpaFactory = factoryAware.getService();
		assertNotNull(jpaFactory, "The jpa whiteboard factory must be registered");

		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(QueryConstants.BACKEND_PROPERTY, "jpa");
		properties.put(Constants.SERVICE_RANKING, 1000);
		ServiceRegistration<QueryProcessor> marker = ctx.registerService(
				QueryProcessor.class, new MarkerProcessor(), properties);
		try {
			// let DS rebind the factory's greedy optional reference
			Thread.sleep(200);
			Resource resource = jpaFactory.createResource(URI.createURI("jpa://anyUnit/Person"));
			assertTrue(resource instanceof CommandResource,
					"jpa resources must implement CommandResource");

			DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
			delete.setSelector(QueryBuilder.from(EcorePackage.Literals.ECLASS).build());
			IOException refusal = assertThrows(IOException.class,
					() -> ((CommandResource) resource).execute(delete));
			assertTrue(refusal.getMessage().contains(MarkerProcessor.MARKER),
					"The injected marker processor must have refused the command, but was: "
							+ refusal.getMessage());
		} finally {
			marker.unregister();
		}
	}

	/** Refuses every translation with a recognizable message. */
	private static final class MarkerProcessor implements QueryProcessor {

		static final String MARKER = "marker-processor-refusal";

		@Override
		public String backend() {
			return "jpa";
		}

		@Override
		public QueryCapabilities capabilities() {
			return QueryCapabilitiesBuilder.create().build();
		}

		@Override
		public Diagnostic validate(Query query, EClass rootEClass) {
			return Diagnostic.OK_INSTANCE;
		}

		@Override
		public QueryPlan translate(Query query, QueryContext context) throws QueryException {
			throw new QueryException(MARKER);
		}
	}
}
