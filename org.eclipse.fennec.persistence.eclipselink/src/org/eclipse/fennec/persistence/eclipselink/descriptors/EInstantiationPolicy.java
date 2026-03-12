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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.orm.helper.EORMHelper;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.DiscoveryException;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;

/**
 * Instantiation policy for EMF objects
 * @author Stefan Bischaif, Mark Hoffmann
 * @since 14.12.2024
 */
public class EInstantiationPolicy extends InstantiationPolicy {

    private static final long serialVersionUID = 1L;
    private transient EClass eClass;
    
    public EInstantiationPolicy() {
    	super();
    }

    public EInstantiationPolicy(EClass eClass) {
        super();
        this.eClass = eClass;
    }
//    
//    /* 
//     * (non-Javadoc)
//     * @see org.eclipse.persistence.internal.descriptors.InstantiationPolicy#buildNewInstance()
//     */
//    @Override
//    public EObject buildNewInstance() throws DescriptorException {
//    	EObject eObject = EcoreUtil.create(eClass);
//    	if (DynamicEObjectImpl.class.isAssignableFrom(javaClass)) {
//    		try {
//    			DynamicEObjectImpl wrapper = (DynamicEObjectImpl) javaClass.getConstructor().newInstance();
//    			wrapper.eSetClass(eClass);
//    			return wrapper;
//    		} catch (Exception e) {
//    			throw new DiscoveryException(e.getMessage());
//    		}
//    	}
//    	return eObject;
//    }

    /* 
     * (non-Javadoc)
     * @see org.eclipse.persistence.internal.descriptors.InstantiationPolicy#buildNewInstance()
     */
    @Override
    public Object buildNewInstance() throws DescriptorException {
    	if (isNull(eClass)) {
    		throw new DiscoveryException(String.format("No EClass was provided to this instanciation policy for descriptor '%s'", getDescriptor().getJavaClassName()));
    	}
    	Class<?> javaClass = getDescriptor().getJavaClass();
        if (DynamicEObjectImpl.class.isAssignableFrom(javaClass)) {
        	try {
        		DynamicEObjectImpl wrapper = (DynamicEObjectImpl) javaClass.getConstructor().newInstance();
        		wrapper.eSetClass(eClass);
        		return wrapper;
        	} catch (Exception e) {
        		throw new DiscoveryException(e.getMessage());
        	}
        } else if (EObject.class.isAssignableFrom(javaClass) && 
        		nonNull(eClass) && 
        		nonNull(eClass.getInstanceClass())) {
        	return EcoreUtil.create(eClass);
        }
        return super.buildNewInstance();
    }
    
    /* 
     * (non-Javadoc)
     * @see org.eclipse.persistence.internal.descriptors.InstantiationPolicy#setDescriptor(org.eclipse.persistence.descriptors.ClassDescriptor)
     */
    @Override
    public void setDescriptor(ClassDescriptor descriptor) {
    	if (descriptor instanceof EClassDescriptor ecd) {
    		eClass = EORMHelper.getEClass(ecd.getEntity());
    	}
    	super.setDescriptor(descriptor);
    }

}
