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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * EPersistence Descriptor Exception
 * @author Mark Hoffmann
 * @since 12.01.2025
 */
public class EDescriptorException extends DescriptorException {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	public final static int VALUE_IS_NO_EOBJECT = 700;
	public final static int NO_EINSTANTIATION_POLICY = 701;
	public final static int NO_ECLASS_FOR_INSTANTIATION = 702;
	public final static int EOBJECT_INSTANTIATION_FAILED = 703;

	/**
	 * Creates a new instance.
	 * @param theMessage
	 * @param descriptor
	 */
	protected EDescriptorException(String theMessage, ClassDescriptor descriptor) {
		super(theMessage, descriptor);
	}

	/**
	 * Creates a new instance carrying the exception that caused it.
	 * @param theMessage the message
	 * @param descriptor the descriptor
	 * @param cause the cause
	 */
	protected EDescriptorException(String theMessage, ClassDescriptor descriptor, Throwable cause) {
		super(theMessage, descriptor, cause);
	}
	
	public static DescriptorException valueIsNoEObject(Object object, ClassDescriptor descriptor) {
		Object[] args = { object };
		
		DescriptorException exception = new EDescriptorException(ExceptionMessageGenerator.buildMessage(DescriptorException.class,VALUE_IS_NO_EOBJECT, args), descriptor);
		exception.setErrorCode(VALUE_IS_NO_EOBJECT);
		return exception;
	}
	
    public static DescriptorException noEInstantiationPolicySet(Object object, ClassDescriptor descriptor) {
        Object[] args = { object };

        DescriptorException exception = new EDescriptorException(ExceptionMessageGenerator.buildMessage(DescriptorException.class, NO_EINSTANTIATION_POLICY, args), descriptor);
        exception.setErrorCode(NO_EINSTANTIATION_POLICY);
        return exception;
    }

	/**
	 * The instantiation policy of a descriptor has no {@link EClass} to create instances of.
	 * @param descriptor the descriptor
	 * @return the exception
	 */
	public static DescriptorException noEClassForInstantiation(ClassDescriptor descriptor) {
		DescriptorException exception = new EDescriptorException(String.format(
				"No EClass was provided to the instantiation policy of descriptor '%s'",
				descriptor == null ? null : descriptor.getJavaClassName()), descriptor);
		exception.setErrorCode(NO_ECLASS_FOR_INSTANTIATION);
		return exception;
	}

	/**
	 * Creating an instance of an entity class for its {@link EClass} failed.
	 * @param javaClass the entity class
	 * @param eClass the EClass
	 * @param descriptor the descriptor
	 * @param cause the failure
	 * @return the exception
	 */
	public static DescriptorException eObjectInstantiationFailed(Class<?> javaClass, EClass eClass, ClassDescriptor descriptor, Throwable cause) {
		DescriptorException exception = new EDescriptorException(String.format("Cannot instantiate %s for %s: %s",
				javaClass.getName(), eClass.getName(), cause.getMessage()), descriptor, cause);
		exception.setErrorCode(EOBJECT_INSTANTIATION_FAILED);
		return exception;
	}

}
