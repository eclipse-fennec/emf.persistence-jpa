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
package org.eclipse.fennec.persistence.eclipselink.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.persistence.capabilities.CapabilityDeclaration;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavor;
import org.eclipse.fennec.persistence.eclipselink.query.JpaQueryProcessor;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Issue #313: the {@code JPAUnit} registration is what activates the repository component, so a
 * consumer can query through this factory while DS still owes it the {@code addUnit} callback for
 * that very service. The registry — not the whiteboard cache — is the authority on which units
 * exist. The JPA counterpart of the Mongo fix for issue #282.
 */
@ExtendWith(MockitoExtension.class)
class JPAResourceFactoryComponentTest {

	@Mock
	private BundleContext ctx;
	@Mock
	private ServiceReference<JPAUnit> reference;
	@Mock
	private JPAUnit unit;

	private JPAResourceFactoryComponent component;

	@BeforeEach
	void setUp() {
		component = new JPAResourceFactoryComponent(ctx);
	}

	@Test
	void aUnitRegisteredButNotYetBoundResolvesFromTheRegistry() throws Exception {
		when(reference.getProperty(JPAUnit.UNIT_NAME)).thenReturn("bath");
		when(ctx.getServiceReferences(JPAUnit.class, null)).thenReturn(List.of(reference));
		when(ctx.getService(reference)).thenReturn(unit);

		JPAResourceImpl resource = (JPAResourceImpl) component.createResource(URI.createURI("jpa://bath/Asset"));

		assertThat(resource.unit()).isSameAs(unit);
	}

	@Test
	void theFlavorOfANotYetBoundUnitIsHonoured() throws Exception {
		when(reference.getProperty(JPAUnit.UNIT_NAME)).thenReturn("bath");
		when(reference.getProperty(CapabilityDeclaration.FLAVOR_PROPERTY)).thenReturn(JpaFlavor.POSTGRES.id());
		when(ctx.getServiceReferences(JPAUnit.class, null)).thenReturn(List.of(reference));
		when(ctx.getService(reference)).thenReturn(unit);

		JPAResourceImpl resource = (JPAResourceImpl) component.createResource(URI.createURI("jpa://bath/Asset"));

		// the discovered reference carries the flavor, so the resource must not silently fall
		// back to the portable baseline
		assertThat(((JpaQueryProcessor) resource.queryProcessor()).flavor()).isEqualTo(JpaFlavor.POSTGRES);
	}

	@Test
	void theLateBindOfAnAlreadyDiscoveredReferenceChangesNothing() throws Exception {
		when(reference.getProperty(JPAUnit.UNIT_NAME)).thenReturn("bath");
		when(ctx.getServiceReferences(JPAUnit.class, null)).thenReturn(List.of(reference));
		when(ctx.getService(reference)).thenReturn(unit);
		component.createResource(URI.createURI("jpa://bath/Asset"));

		// DS catches up with the callback for the same service
		component.addUnit(reference);
		JPAResourceImpl resource = (JPAResourceImpl) component.createResource(URI.createURI("jpa://bath/Asset"));

		assertThat(resource.unit()).isSameAs(unit);
		verify(ctx, never()).ungetService(any());
		verify(ctx, times(1)).getService(reference);
	}

	@Test
	void aBoundUnitDoesNotConsultTheRegistry() throws Exception {
		when(reference.getProperty(JPAUnit.UNIT_NAME)).thenReturn("bath");
		when(ctx.getService(reference)).thenReturn(unit);
		component.addUnit(reference);

		JPAResourceImpl resource = (JPAResourceImpl) component.createResource(URI.createURI("jpa://bath/Asset"));

		assertThat(resource.unit()).isSameAs(unit);
		verify(ctx, never()).getServiceReferences(JPAUnit.class, null);
	}

	@Test
	void aUnitTheRegistryDoesNotKnowIsUnavailable() throws Exception {
		@SuppressWarnings("unchecked")
		ServiceReference<JPAUnit> other = mock(ServiceReference.class);
		lenient().when(other.getProperty(JPAUnit.UNIT_NAME)).thenReturn("city");
		when(ctx.getServiceReferences(JPAUnit.class, null)).thenReturn(List.of(other));

		JPAResourceImpl resource = (JPAResourceImpl) component.createResource(URI.createURI("jpa://bath/Asset"));

		assertThatThrownBy(() -> resource.unit().lease())
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("No persistence unit 'bath'");
	}
}
