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
package org.eclipse.fennec.persistence.orm.helper;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;

/**
 * Helper class to handle loading of orm data
 * @author Mark Hoffmann
 * @since 12.12.2024
 */
public class EORMModelHelper {

	private final ResourceSet resourceSet;

	/**
	 * Creates a new instance.
	 */
	public EORMModelHelper(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

	/**
	 * Returns the resourceSet.
	 * @return the resourceSet
	 */
	public ResourceSet getResourceSet() {
		return resourceSet;
	}

	public <T extends EObject> T loadFromInput(InputStream data, String fileExt, ResourceSet resourceSet, Class<T> clazz) {
		requireNonNull(data);
		requireNonNull(fileExt);
		requireNonNull(resourceSet);
		String uriString = UUID.randomUUID().toString() + "." + fileExt;
		Resource resource = resourceSet.createResource(URI.createURI(uriString));
		requireNonNull(resource, String.format("No resource factory registered for '%s'", uriString));
		try {
			resource.load(data, null);
			if (resource.getContents().isEmpty()) {
				throw new IllegalStateException(String.format("Content is empty for uri '%s'",uriString));
			}
			return clazz.cast(resource.getContents().get(0));
		} catch (IOException e) {
			throw new IllegalStateException(String.format("Cannot load data from input stream for uri '%s'", uriString), e);
		}
	}

	/**
	 * Loads the {@link PersistenceUnit} from a string. If it fails an {@link IllegalStateException} will be thrown
	 * @param urlString the url string  to load from
	 * @return the {@link PersistenceUnit}
	 */
	public PersistenceUnit loadPersistenceUnit(String urlString) {
		requireNonNull(urlString);
		try {
			java.net.URI uri = java.net.URI.create(urlString);
			URL url = uri.toURL();
			return loadPersistenceUnit(url);
		} catch (IllegalStateException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Cannot create persistence unit file URI from string '%s'", urlString), e);
		}
	}

	/**
	 * Loads the {@link PersistenceUnit} from a URL. If it fails an {@link IllegalStateException} will be thrown
	 * @param url the url to load from
	 * @return the {@link PersistenceUnit}
	 */
	public PersistenceUnit loadPersistenceUnit(URL url) {
		try {
			return loadFromInput(url.openStream(), EPersistencePackage.eNAME, resourceSet, PersistenceUnit.class);
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Cannot load persistence unit from url '%s'", url.toString()), e);
		}
	}

	/**
	 * Loads the {@link EntityMappings} from a string. If it fails an {@link IllegalStateException} will be thrown
	 * @param urlString the url String to load from
	 * @return the {@link EntityMappings}
	 */
	public EntityMappings loadMapping(String urlString) {
		requireNonNull(urlString);
		Resource resource = null;
		try {
			java.net.URI uri = java.net.URI.create(urlString);
			URL url = uri.toURL();
			EntityMappings mapping = loadMapping(url);
			EcoreUtil.resolveAll(mapping);
			resource = mapping.eResource();
			return mapping;
		} catch (IllegalStateException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Cannot create entity mapping file URI from string '%s'", urlString), e);
		} finally {
			if (nonNull(resource)) {
				resource.getContents().clear();
				resourceSet.getResources().remove(resource);
			}
		}
	}


	/**
	 * Loads the {@link EntityMappings} from a URL. If it fails an {@link IllegalStateException} will be thrown
	 * @param url the url to load from
	 * @return the {@link EntityMappings}
	 */
	public EntityMappings loadMapping(URL url) {
		try {
			return loadFromInput(url.openStream(), EORMPackage.eNAME, resourceSet, EntityMappings.class);
		} catch (IOException e) {
			throw new IllegalStateException(String.format("Cannot load mapping from url '%s'", url.toString()), e);
		}
	}

}
