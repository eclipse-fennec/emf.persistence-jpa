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
package org.eclipse.fennec.persistence.eclipselink.descriptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.fennec.persistence.eclipselink.exception.EDescriptorException;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The failures of {@link EInstantiationPolicy#buildNewInstance()} are descriptor exceptions with
 * their own error codes and keep their cause (issue #422: the policy threw the cache
 * coordination's {@code DiscoveryException}, which EclipseLink 5 moved).
 */
class EInstantiationPolicyTest {

	private EClass eClass;

	@BeforeEach
	void setUp() {
		eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName("Person");
	}

	@Test
	void buildsDynamicEObjectOfItsEClass() {
		EInstantiationPolicy policy = policyFor(new EInstantiationPolicy(eClass), DynamicEObjectImpl.class);

		Object instance = policy.buildNewInstance();

		assertThat(instance).isInstanceOf(DynamicEObjectImpl.class);
		assertThat(((EObject) instance).eClass()).isSameAs(eClass);
	}

	@Test
	void missingEClassIsADescriptorException() {
		EInstantiationPolicy policy = policyFor(new EInstantiationPolicy(), DynamicEObjectImpl.class);

		assertThatThrownBy(policy::buildNewInstance)
				.isInstanceOfSatisfying(EDescriptorException.class, e -> {
					assertThat(e.getErrorCode()).isEqualTo(EDescriptorException.NO_ECLASS_FOR_INSTANTIATION);
					assertThat(e.getMessage()).contains("No EClass was provided", DynamicEObjectImpl.class.getName());
				});
	}

	@Test
	void failingDynamicConstructorKeepsItsCause() {
		EInstantiationPolicy policy = policyFor(new EInstantiationPolicy(eClass), FailingDynamicEObject.class);

		assertThatThrownBy(policy::buildNewInstance)
				.isInstanceOfSatisfying(EDescriptorException.class, e -> {
					assertThat(e.getErrorCode()).isEqualTo(EDescriptorException.EOBJECT_INSTANTIATION_FAILED);
					assertThat(e.getMessage()).contains("Cannot instantiate", FailingDynamicEObject.class.getName(), "Person");
					assertThat(e.getInternalException()).isNotNull();
				});
	}

	@Test
	void failingGeneratedStyleConstructorKeepsItsCause() {
		EInstantiationPolicy policy = policyFor(new EInstantiationPolicy(eClass), NoDefaultConstructorEObject.class);

		assertThatThrownBy(policy::buildNewInstance)
				.isInstanceOfSatisfying(EDescriptorException.class, e -> {
					assertThat(e.getErrorCode()).isEqualTo(EDescriptorException.EOBJECT_INSTANTIATION_FAILED);
					assertThat(e.getInternalException()).isInstanceOf(NoSuchMethodException.class);
				});
	}

	private static EInstantiationPolicy policyFor(EInstantiationPolicy policy, Class<?> javaClass) {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClass(javaClass);
		policy.setDescriptor(descriptor);
		return policy;
	}

	/** A dynamic EObject whose constructor fails. */
	public static class FailingDynamicEObject extends DynamicEObjectImpl {
		public FailingDynamicEObject() {
			throw new IllegalStateException("constructor failed");
		}
	}

	/** An EObject that is no dynamic one and has no default constructor. */
	public static class NoDefaultConstructorEObject extends MinimalEObjectImpl.Container {
		public NoDefaultConstructorEObject(String unused) {
		}
	}

}
