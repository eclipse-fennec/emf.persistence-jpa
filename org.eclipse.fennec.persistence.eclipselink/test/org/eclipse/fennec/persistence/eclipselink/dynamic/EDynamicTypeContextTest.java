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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link EDynamicTypeContext} — verifies composition-based
 * builder/entity management and that Map internals are not exposed.
 */
class EDynamicTypeContextTest {

	private EDynamicTypeContext context;
	private Entity entity;
	private EClass eClass;

	@BeforeEach
	void setUp() {
		context = new EDynamicTypeContext();
		eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName("Person");
		entity = EORMFactory.eINSTANCE.createEntity();
		entity.setClass(eClass);
	}

	@Test
	@DisplayName("is not a Map — composition, not inheritance")
	void testNotAMap() {
		assertThat(context).isNotInstanceOf(Map.class);
	}

	@Test
	@DisplayName("put and get store and retrieve builders")
	void testPutAndGet() {
		EDynamicTypeBuilder builder = mock(EDynamicTypeBuilder.class);
		context.put(entity, builder);

		assertThat(context.get(entity)).isSameAs(builder);
	}

	@Test
	@DisplayName("put maintains entityMap for EClassifier lookup")
	void testPutMaintainsEntityMap() {
		EDynamicTypeBuilder builder = mock(EDynamicTypeBuilder.class);
		context.put(entity, builder);

		assertThat(context.getEntity(eClass)).isSameAs(entity);
		assertThat(context.getETypeBuilder(eClass)).isSameAs(builder);
	}

	@Test
	@DisplayName("computeIfAbsent creates builder lazily")
	void testComputeIfAbsent() {
		EDynamicTypeBuilder builder = mock(EDynamicTypeBuilder.class);
		EDynamicTypeBuilder result = context.computeIfAbsent(entity, e -> builder);

		assertThat(result).isSameAs(builder);
		assertThat(context.get(entity)).isSameAs(builder);
		assertThat(context.getEntity(eClass)).isSameAs(entity);
	}

	@Test
	@DisplayName("computeIfAbsent does not replace existing builder")
	void testComputeIfAbsentExisting() {
		EDynamicTypeBuilder first = mock(EDynamicTypeBuilder.class);
		EDynamicTypeBuilder second = mock(EDynamicTypeBuilder.class);
		context.put(entity, first);

		EDynamicTypeBuilder result = context.computeIfAbsent(entity, e -> second);

		assertThat(result).isSameAs(first);
	}

	@Test
	@DisplayName("remove clears both maps")
	void testRemove() {
		EDynamicTypeBuilder builder = mock(EDynamicTypeBuilder.class);
		context.put(entity, builder);

		context.remove(entity);

		assertThat(context.get(entity)).isNull();
		assertThat(context.getEntity(eClass)).isNull();
	}

	@Test
	@DisplayName("clear removes all entries")
	void testClear() {
		context.put(entity, mock(EDynamicTypeBuilder.class));

		context.clear();

		assertThat(context.get(entity)).isNull();
		assertThat(context.getEntity(eClass)).isNull();
		assertThat(context.getETypeBuilder(eClass)).isNull();
	}

	@Test
	@DisplayName("getOptionalETypeBuilder returns Optional")
	void testGetOptionalETypeBuilder() {
		assertThat(context.getOptionalETypeBuilder(eClass)).isEmpty();

		context.put(entity, mock(EDynamicTypeBuilder.class));
		assertThat(context.getOptionalETypeBuilder(eClass)).isPresent();
	}
}
