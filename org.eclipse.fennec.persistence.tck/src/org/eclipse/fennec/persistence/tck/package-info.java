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
 * Backend-agnostic compatibility test suite (TCK) for EMF persistence backends.
 * <p>
 * {@link org.eclipse.fennec.persistence.tck.AbstractPersistenceTCK} is subclass API
 * (issue #99): a backend binding — in this workspace or an external one — extends it,
 * implements the backend SPI hooks ({@code setUpBackend}, {@code createBackendResourceSet},
 * {@code uriFor}) and answers {@code declaredCapabilities()} with its connection-free
 * capability declaration (issue #160). Gating is declarative: every non-core case carries
 * {@link org.eclipse.fennec.persistence.tck.RequiresCapabilities}, and
 * {@link org.eclipse.fennec.persistence.tck.CapabilityGate} skips a case whose required
 * features the binding does not declare — the skip reason names them.
 * The TCK models ({@code tck.ecore}, {@code tck-string.ecore}) ship as resources next to
 * the class. The in-repo JPA/Mongo bindings live in this project's test folder.
 */
@org.osgi.annotation.bundle.Export
@org.osgi.annotation.versioning.Version("1.0.0")
package org.eclipse.fennec.persistence.tck;
