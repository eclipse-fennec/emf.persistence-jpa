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
package org.eclipse.fennec.persistence.test.proto;

import org.eclipse.fennec.persistence.repository.api.ReadRepository;

/**
 * What a prototype-scoped consumer of a prototype-scoped repository exposes to the test:
 * the repository instances it was handed, so the test can see whether they were disposed.
 */
public interface RepositoryConsumer {

	/** The repository instances this consumer holds; two entries if it has two references. */
	ReadRepository[] repositories();
}
