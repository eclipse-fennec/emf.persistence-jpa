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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

/**
 * Tests for {@link JPAResourceImpl} transaction safety and lifecycle.
 */
class JPAResourceImplTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;
	private JPAResourceImpl resource;

	@BeforeEach
	void setUp() {
		emf = mock(EntityManagerFactory.class);
		em = mock(EntityManager.class);
		tx = mock(EntityTransaction.class);
		when(emf.createEntityManager()).thenReturn(em);
		when(em.getTransaction()).thenReturn(tx);

		resource = new JPAResourceImpl(URI.createURI("jpa://test/Person"), emf);
	}

	/** Creates a real EObject that can be added to EMF resource contents. */
	private EObject createEObject() {
		return new DynamicEObjectImpl(EcoreFactory.eINSTANCE.createEClass());
	}

	@Nested
	@DisplayName("doSave transaction safety")
	class SaveTests {

		@Test
		@DisplayName("Successful save commits transaction")
		void testSaveCommits() throws IOException {
			EObject eo = createEObject();
			resource.getContents().add(eo);

			resource.save(null);

			verify(tx).begin();
			verify(em).merge(eo);
			verify(tx).commit();
			verify(tx, never()).rollback();
			verify(em).close();
		}

		@Test
		@DisplayName("Failed merge rolls back transaction")
		void testSaveRollbackOnMergeFailure() {
			EObject eo = createEObject();
			resource.getContents().add(eo);
			when(em.merge(any())).thenThrow(new PersistenceException("merge failed"));
			when(tx.isActive()).thenReturn(true);

			assertThatThrownBy(() -> resource.save(null))
					.isInstanceOf(IOException.class)
					.hasMessageContaining("Failed to save")
					.hasCauseInstanceOf(PersistenceException.class);

			verify(tx).rollback();
			verify(tx, never()).commit();
			verify(em).close();
		}

		@Test
		@DisplayName("Failed commit rolls back transaction")
		void testSaveRollbackOnCommitFailure() {
			EObject eo = createEObject();
			resource.getContents().add(eo);
			doThrow(new PersistenceException("commit failed")).when(tx).commit();
			when(tx.isActive()).thenReturn(true);

			assertThatThrownBy(() -> resource.save(null))
					.isInstanceOf(IOException.class)
					.hasCauseInstanceOf(PersistenceException.class);

			verify(tx).rollback();
			verify(em).close();
		}

		@Test
		@DisplayName("Rollback not called when transaction is not active")
		void testSaveNoRollbackWhenTxInactive() {
			EObject eo = createEObject();
			resource.getContents().add(eo);
			when(em.merge(any())).thenThrow(new PersistenceException("merge failed"));
			when(tx.isActive()).thenReturn(false);

			assertThatThrownBy(() -> resource.save(null))
					.isInstanceOf(IOException.class);

			verify(tx, never()).rollback();
			verify(em).close();
		}

		@Test
		@DisplayName("Save with empty contents still commits")
		void testSaveEmptyContents() throws IOException {
			resource.save(null);

			verify(tx).begin();
			verify(tx).commit();
			verify(em, never()).merge(any());
			verify(em).close();
		}
	}

	@Nested
	@DisplayName("delete transaction safety")
	class DeleteTests {

		@Test
		@DisplayName("Successful delete commits and clears contents")
		void testDeleteCommitsAndClears() throws IOException {
			EObject eo = createEObject();
			resource.getContents().add(eo);
			when(em.merge(eo)).thenReturn(eo);

			resource.delete(null);

			verify(tx).begin();
			verify(em).merge(eo);
			verify(em).remove(eo);
			verify(tx).commit();
			verify(tx, never()).rollback();
			verify(em).close();
			assertThat(resource.getContents()).isEmpty();
		}

		@Test
		@DisplayName("Failed delete rolls back and preserves contents")
		void testDeleteRollbackPreservesContents() {
			EObject eo = createEObject();
			resource.getContents().add(eo);
			when(em.merge(any())).thenThrow(new PersistenceException("remove failed"));
			when(tx.isActive()).thenReturn(true);

			assertThatThrownBy(() -> resource.delete(null))
					.isInstanceOf(IOException.class)
					.hasMessageContaining("Failed to delete");

			verify(tx).rollback();
			verify(tx, never()).commit();
			verify(em).close();
			// Contents must be preserved on failure
			assertThat(resource.getContents()).hasSize(1);
		}
	}

	@Nested
	@DisplayName("doUnload lifecycle")
	class UnloadTests {

		@Test
		@DisplayName("Unload clears contents")
		void testUnloadClearsContents() {
			EObject eo = createEObject();
			resource.getContents().add(eo);

			resource.unload();

			assertThat(resource.getContents()).isEmpty();
		}
	}

	@Nested
	@DisplayName("getEObject fragment resolution")
	class GetEObjectTests {

		@Test
		@DisplayName("Non-proxy fragment without // prefix delegates to super")
		void testPlainFragment() {
			EObject result = resource.getEObject("plainId");
			assertThat(result).isNull();
		}

		@Test
		@DisplayName("Incomplete fragment delegates to super")
		void testIncompleteFragment() {
			// Only 2 parts instead of required 3
			EObject result = resource.getEObject("//refName/idAttr");
			assertThat(result).isNull();
		}

		@Test
		@DisplayName("Resolved object is added to resource contents")
		void testResolvedObjectAddedToContents() throws Exception {
			EObject resolved = createEObject();

			// Create a testable subclass that bypasses EclipseLink descriptor lookup
			ClassDescriptor descriptor = mock(ClassDescriptor.class);
			doReturn(EObject.class).when(descriptor).getJavaClass();
			DatabaseField pkField = new DatabaseField("id");
			pkField.setType(String.class);
			when(descriptor.getPrimaryKeyFields()).thenReturn(List.of(pkField));

			when(em.find(eq(EObject.class), eq("42"))).thenReturn(resolved);

			try (JPAResourceImpl testResource = new JPAResourceImpl(
					URI.createURI("jpa://test/Person"), emf) {
				@Override
				ClassDescriptor getDescriptor(String entityName) {
					return descriptor;
				}
			}) {
				EObject result = testResource.getEObject("//personRef/id/42");

				assertThat(result).isSameAs(resolved);
				// Core assertion: resolved object is IN the resource contents
				assertThat(testResource.getContents()).contains(resolved);
				// And has this resource as its eResource
				assertThat(resolved.eResource()).isSameAs(testResource);
				verify(em).close();
			}
		}

		@Test
		@DisplayName("Already-contained object is not duplicated")
		void testAlreadyContainedNotDuplicated() throws Exception {
			EObject existing = createEObject();

			ClassDescriptor descriptor = mock(ClassDescriptor.class);
			doReturn(EObject.class).when(descriptor).getJavaClass();
			DatabaseField pkField = new DatabaseField("id");
			pkField.setType(String.class);
			when(descriptor.getPrimaryKeyFields()).thenReturn(List.of(pkField));

			// em.find returns the same object that's already in contents
			when(em.find(eq(EObject.class), eq("42"))).thenReturn(existing);

			try (JPAResourceImpl testResource = new JPAResourceImpl(
					URI.createURI("jpa://test/Person"), emf) {
				@Override
				ClassDescriptor getDescriptor(String entityName) {
					return descriptor;
				}
			}) {
				testResource.getContents().add(existing);

				EObject result = testResource.getEObject("//personRef/id/42");

				assertThat(result).isSameAs(existing);
				// Must not be duplicated
				assertThat(testResource.getContents()).hasSize(1);
				verify(em).close();
			}
		}

		@Test
		@DisplayName("Unknown entity returns null")
		void testUnknownEntityReturnsNull() throws Exception {
			try (JPAResourceImpl testResource = new JPAResourceImpl(
					URI.createURI("jpa://test/Unknown"), emf) {
				@Override
				ClassDescriptor getDescriptor(String entityName) {
					return null;
				}
			}) {
				EObject result = testResource.getEObject("//ref/id/42");
				assertThat(result).isNull();
			}
		}
	}
}
