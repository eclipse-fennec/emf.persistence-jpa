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
package org.eclipse.fennec.persistence.query.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link QueryContexts} factory.
 *
 * @author Mark Hoffmann
 */
class QueryContextsTest {

	private EClass person() {
		EClass person = EcoreFactory.eINSTANCE.createEClass();
		person.setName("Person");
		return person;
	}

	@Test
	void minimalContextHasEmptyMaps() {
		QueryContext context = QueryContexts.of(person(), null);
		assertThat(context.parameters()).isEmpty();
		assertThat(context.options()).isEmpty();
		assertThat(context.converter()).isNull();
		assertThat(context.rootEClass().getName()).isEqualTo("Person");
	}

	@Test
	void rootEClassIsMandatory() {
		assertThatIllegalArgumentException().isThrownBy(() -> QueryContexts.of(null, null));
	}

	@Test
	void mapsAreDefensivelyCopiedAndImmutable() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("age", 42);
		QueryContext context = QueryContexts.of(person(), null, parameters, Map.of("page", 10));

		parameters.put("age", 99);
		assertThat(context.parameters()).containsEntry("age", 42);
		assertThat(context.options().get("page")).isEqualTo(10);
		assertThatThrownBy(() -> context.parameters().put("x", 1))
				.isInstanceOf(UnsupportedOperationException.class);
	}
}
