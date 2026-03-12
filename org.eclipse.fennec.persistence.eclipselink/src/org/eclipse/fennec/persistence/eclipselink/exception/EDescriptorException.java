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

	/**
	 * Creates a new instance.
	 * @param theMessage
	 * @param descriptor
	 */
	protected EDescriptorException(String theMessage, ClassDescriptor descriptor) {
		super(theMessage, descriptor);
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

}
