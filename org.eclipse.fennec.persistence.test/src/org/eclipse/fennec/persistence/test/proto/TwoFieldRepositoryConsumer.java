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
import org.eclipse.fennec.persistence.repository.api.Repository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * Two prototype references onto the same registration (read and full interface), as a
 * resource that separates reads from writes would declare them. SCR hands both fields the
 * same instance, so the release has to cope with one instance under two references.
 */
@Component(scope = ServiceScope.PROTOTYPE, service = RepositoryConsumer.class,
		property = "consumer=twofields")
public class TwoFieldRepositoryConsumer implements RepositoryConsumer {

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED, target = "(persistence.repository.id=proto-repo)")
	private ReadRepository readRepository;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED, target = "(persistence.repository.id=proto-repo)")
	private Repository repository;

	@Override
	public ReadRepository[] repositories() {
		return new ReadRepository[] { readRepository, repository };
	}
}
