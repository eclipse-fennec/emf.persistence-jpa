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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * Same as {@link FieldRepositoryConsumer}, but the repository arrives through the constructor:
 * no field and no unbind method, so SCR takes its other release path.
 */
@Component(scope = ServiceScope.PROTOTYPE, service = RepositoryConsumer.class,
		property = "consumer=constructor")
public class ConstructorRepositoryConsumer implements RepositoryConsumer {

	private final Repository repository;

	@Activate
	public ConstructorRepositoryConsumer(
			@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED, target = "(persistence.repository.id=proto-repo)")
			Repository repository) {
		this.repository = repository;
	}

	@Override
	public ReadRepository[] repositories() {
		return new ReadRepository[] { repository };
	}
}
