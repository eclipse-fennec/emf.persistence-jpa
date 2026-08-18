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
/**
 * Reusable repository implementation for backend flavour bundles: {@link
 * org.eclipse.fennec.persistence.repository.spi.AbstractRepository} carries all generic
 * logic (URI arithmetic, resource lifecycle, query/command delegation); a flavour bundle
 * contributes only a configuration component extending {@link
 * org.eclipse.fennec.persistence.repository.spi.AbstractRepositoryComponent} that binds
 * the backend's lifecycle service and derives the base URI. Exported deliberately —
 * downstream backends (e.g. emf.search's Lucene flavour) build their repository on it.
 */
@org.osgi.annotation.bundle.Export
@org.osgi.annotation.versioning.Version("1.0.0")
package org.eclipse.fennec.persistence.repository.spi;
