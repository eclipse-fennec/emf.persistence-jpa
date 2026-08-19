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
package org.eclipse.fennec.persistence.repository.spi;

import static java.util.Objects.nonNull;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.persistence.repository.RepositoryConstants;
import org.eclipse.fennec.persistence.repository.api.ReadRepository;
import org.eclipse.fennec.persistence.repository.api.Repository;
import org.eclipse.fennec.persistence.repository.api.RepositoryService;
import org.eclipse.fennec.persistence.repository.api.WriteRepository;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * Base for the per-flavour repository configuration components. One component instance =
 * one factory configuration = one registered repository. The registration is done
 * manually (not via DS {@code service=}) for two reasons: the offered interfaces depend
 * on the {@code readOnly} configuration — a read-only repository withholds
 * {@link WriteRepository} and {@link Repository} entirely — and instances are handed out
 * through a {@link PrototypeServiceFactory}, because a repository owns a non-thread-safe
 * {@link org.eclipse.emf.ecore.resource.ResourceSet}: every consumer gets its own
 * instance, disposed on unget.
 *
 * @since 18.08.2026
 */
public abstract class AbstractRepositoryComponent {

	private ServiceRegistration<?> registration;

	/** Creates a fresh repository instance; called once per consuming bundle/prototype request. */
	protected abstract AbstractRepository createRepository();

	/**
	 * Registers the repository services for one configuration.
	 *
	 * @param context the bundle context
	 * @param repositoryId the configured id, published as {@link RepositoryConstants#REPOSITORY_ID}
	 * @param baseUri the backend location, published as {@link RepositoryConstants#REPOSITORY_BASE_URI}
	 * @param readOnly whether to withhold the write interfaces
	 */
	protected final void register(BundleContext context, String repositoryId, URI baseUri, boolean readOnly) {
		if (repositoryId == null || repositoryId.isBlank()) {
			throw new IllegalStateException(
					"Repository configuration for " + baseUri + " carries no repositoryId");
		}
		String[] types = readOnly
				? new String[] { RepositoryService.class.getName(), ReadRepository.class.getName() }
				: new String[] { RepositoryService.class.getName(), ReadRepository.class.getName(),
						WriteRepository.class.getName(), Repository.class.getName() };
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(RepositoryConstants.REPOSITORY_ID, repositoryId);
		properties.put(RepositoryConstants.REPOSITORY_BASE_URI, baseUri.toString());
		properties.put(RepositoryConstants.REPOSITORY_BACKEND, baseUri.scheme());
		properties.put(RepositoryConstants.REPOSITORY_READ_ONLY, readOnly);
		registration = context.registerService(types, new PrototypeServiceFactory<AbstractRepository>() {

			@Override
			public AbstractRepository getService(Bundle bundle,
					ServiceRegistration<AbstractRepository> registration) {
				return createRepository();
			}

			@Override
			public void ungetService(Bundle bundle, ServiceRegistration<AbstractRepository> registration,
					AbstractRepository service) {
				service.dispose();
			}
		}, properties);
	}

	/** Unregisters the repository services; safe to call more than once. */
	protected final void unregister() {
		if (nonNull(registration)) {
			registration.unregister();
			registration = null;
		}
	}
}
