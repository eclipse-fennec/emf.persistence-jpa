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
 * A request-style consumer: prototype-scoped itself, holding the repository through a
 * {@link ReferenceScope#PROTOTYPE_REQUIRED} field.
 */
@Component(scope = ServiceScope.PROTOTYPE, service = RepositoryConsumer.class,
		property = "consumer=field")
public class FieldRepositoryConsumer implements RepositoryConsumer {

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED, target = "(persistence.repository.id=proto-repo)")
	private Repository repository;

	@Override
	public ReadRepository[] repositories() {
		return new ReadRepository[] { repository };
	}
}
