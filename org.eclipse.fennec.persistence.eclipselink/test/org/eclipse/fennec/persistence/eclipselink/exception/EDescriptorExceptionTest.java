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
package org.eclipse.fennec.persistence.eclipselink.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopyPolicy;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Every {@link EDescriptorException} names its reason, the offending value and the descriptor
 * (issue #426: the codes 700 and 701 were looked up in EclipseLink's message resource, which
 * does not know them, and came out with an empty message).
 */
class EDescriptorExceptionTest {

	private RelationalDescriptor descriptor;

	@BeforeEach
	void setUp() {
		descriptor = new RelationalDescriptor();
		descriptor.setJavaClass(DynamicEObjectImpl.class);
	}

	@Nested
	class Factories {

		@Test
		void valueIsNoEObjectNamesValueAndType() {
			DescriptorException e = EDescriptorException.valueIsNoEObject("plain", descriptor);

			assertThat(e.getErrorCode()).isEqualTo(EDescriptorException.VALUE_IS_NO_EOBJECT);
			assertThat(e.getMessage())
					.contains("is no EObject", "plain", String.class.getName(), DynamicEObjectImpl.class.getName())
					.doesNotContain("no translation");
		}

		@Test
		void valueIsNoEObjectToleratesNull() {
			DescriptorException e = EDescriptorException.valueIsNoEObject(null, null);

			assertThat(e.getErrorCode()).isEqualTo(EDescriptorException.VALUE_IS_NO_EOBJECT);
			assertThat(e.getMessage()).contains("is no EObject", "null");
		}

		@Test
		void noEInstantiationPolicySetNamesDescriptorAndPolicy() {
			DescriptorException e = EDescriptorException.noEInstantiationPolicySet(null, descriptor);

			assertThat(e.getErrorCode()).isEqualTo(EDescriptorException.NO_EINSTANTIATION_POLICY);
			assertThat(e.getMessage())
					.contains("EInstantiationPolicy", DynamicEObjectImpl.class.getName(),
							descriptor.getInstantiationPolicy().getClass().getName());
		}

		@Test
		void noEInstantiationPolicySetNamesTheObject() {
			DescriptorException e = EDescriptorException.noEInstantiationPolicySet("domain", descriptor);

			assertThat(e.getMessage()).contains("domain");
		}

		@Test
		void noEClassForInstantiationNamesDescriptor() {
			DescriptorException e = EDescriptorException.noEClassForInstantiation(descriptor);

			assertThat(e.getErrorCode()).isEqualTo(EDescriptorException.NO_ECLASS_FOR_INSTANTIATION);
			assertThat(e.getMessage()).contains("No EClass", DynamicEObjectImpl.class.getName());
		}

		@Test
		void eObjectInstantiationFailedKeepsCause() {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Person");
			IllegalStateException cause = new IllegalStateException("boom");

			DescriptorException e = EDescriptorException.eObjectInstantiationFailed(DynamicEObjectImpl.class, eClass, descriptor, cause);

			assertThat(e.getErrorCode()).isEqualTo(EDescriptorException.EOBJECT_INSTANTIATION_FAILED);
			assertThat(e.getMessage()).contains("Cannot instantiate", "Person", "boom");
			assertThat(e.getInternalException()).isSameAs(cause);
		}
	}

	@Nested
	class CallSites {

		@Test
		void createInstanceWithoutEInstantiationPolicy() {
			assertThatThrownBy(() -> EDynamicHelper.createInstance(descriptor))
					.isInstanceOfSatisfying(EDescriptorException.class, e -> {
						assertThat(e.getErrorCode()).isEqualTo(EDescriptorException.NO_EINSTANTIATION_POLICY);
						assertThat(e.getMessage()).contains("EInstantiationPolicy", DynamicEObjectImpl.class.getName());
					});
		}

		@Test
		void createCloneOfNoEObject() {
			assertThatThrownBy(() -> EDynamicHelper.createClone(42, descriptor, null))
					.isInstanceOfSatisfying(EDescriptorException.class, e -> {
						assertThat(e.getErrorCode()).isEqualTo(EDescriptorException.VALUE_IS_NO_EOBJECT);
						assertThat(e.getMessage()).contains("42", Integer.class.getName());
					});
		}

		@Test
		void copyPolicyWithoutEInstantiationPolicy() {
			ECopyPolicy policy = new ECopyPolicy();
			policy.setDescriptor(descriptor);

			assertThatThrownBy(() -> policy.buildClone("domain", null))
					.isInstanceOfSatisfying(EDescriptorException.class, e -> {
						assertThat(e.getErrorCode()).isEqualTo(EDescriptorException.NO_EINSTANTIATION_POLICY);
						assertThat(e.getMessage()).contains("domain", DynamicEObjectImpl.class.getName());
					});
		}
	}

}
