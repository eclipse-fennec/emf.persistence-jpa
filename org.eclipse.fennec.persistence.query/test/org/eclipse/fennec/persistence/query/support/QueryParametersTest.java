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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.query.QueryException;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link QueryParameters} placeholder resolution.
 *
 * @author Mark Hoffmann
 */
class QueryParametersTest {

	private EAttribute attribute(String name, EClassifier type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		return attribute;
	}

	@Test
	void placeholderDetection() {
		assertThat(QueryParameters.isPlaceholder(":name")).isTrue();
		assertThat(QueryParameters.isPlaceholder("::escaped")).isFalse();
		assertThat(QueryParameters.isPlaceholder("literal")).isFalse();
		assertThat(QueryParameters.isPlaceholder(null)).isFalse();
	}

	@Test
	void parameterNameStripsPrefix() {
		assertThat(QueryParameters.parameterName(":name")).isEqualTo("name");
		assertThatIllegalArgumentException().isThrownBy(() -> QueryParameters.parameterName("literal"));
		assertThatIllegalArgumentException().isThrownBy(() -> QueryParameters.parameterName(":"));
	}

	@Test
	void boundPlaceholderResolvesToTypedValue() throws QueryException {
		EAttribute age = attribute("age", EcorePackage.Literals.EINT);
		Object resolved = QueryParameters.resolve(":age", age, Map.of("age", 42), null);
		assertThat(resolved).isEqualTo(42);
	}

	@Test
	void boundNullParameterResolvesToNull() throws QueryException {
		EAttribute name = attribute("name", EcorePackage.Literals.ESTRING);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("name", null);
		assertThat(QueryParameters.resolve(":name", name, parameters, null)).isNull();
	}

	@Test
	void unboundPlaceholderFailsWithQueryException() {
		EAttribute age = attribute("age", EcorePackage.Literals.EINT);
		assertThatThrownBy(() -> QueryParameters.resolve(":age", age, Map.of(), null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining(":age")
				.hasMessageContaining("age");
		assertThatThrownBy(() -> QueryParameters.resolve(":age", age, null, null))
				.isInstanceOf(QueryException.class);
	}

	@Test
	void literalIsParsedAsTypedValue() throws QueryException {
		EAttribute age = attribute("age", EcorePackage.Literals.EINT);
		assertThat(QueryParameters.resolve("42", age, Map.of(), null)).isEqualTo(42);
	}

	@Test
	void escapedLiteralKeepsSingleColon() throws QueryException {
		EAttribute name = attribute("name", EcorePackage.Literals.ESTRING);
		assertThat(QueryParameters.resolve("::notAParameter", name, Map.of(), null)).isEqualTo(":notAParameter");
	}

	@Test
	void nullValueStaysNull() throws QueryException {
		EAttribute name = attribute("name", EcorePackage.Literals.ESTRING);
		assertThat(QueryParameters.resolve(null, name, Map.of(), null)).isNull();
	}
}
