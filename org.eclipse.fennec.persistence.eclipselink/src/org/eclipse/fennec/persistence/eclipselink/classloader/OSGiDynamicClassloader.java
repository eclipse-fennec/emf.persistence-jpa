/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.eclipselink.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author mark
 * @since 11.12.2024
 */
public class OSGiDynamicClassloader extends DynamicClassLoader {
	
	private final BundleContext ctx;

	/**
	 * Creates a new instance.
	 */
	public OSGiDynamicClassloader(BundleContext ctx) {
		super(DynamicClassLoader.class.getClassLoader());
		this.ctx = ctx;
	}
	
	@Override
    public URL getResource(String name) {
        return super.getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return super.getResourceAsStream(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if ("META-INF/persistence.xml".equals(name)) {
            return ctx.getBundle().getResources(name);
        }
        return super.getResources(name);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
    	if (className.equals(DynamicEObjectImpl.class.getName())) {
    		return DynamicEObjectImpl.class;
    	}
        return super.findClass(className);
    }
}
