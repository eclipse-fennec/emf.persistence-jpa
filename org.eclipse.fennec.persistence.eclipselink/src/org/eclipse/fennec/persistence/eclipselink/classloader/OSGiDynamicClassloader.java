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
package org.eclipse.fennec.persistence.eclipselink.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Mark Hoffmann
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

    /**
     * The parent classes of the dynamic classes defined here, by name (issue #311): a dynamic
     * subtype of a generated class extends the generated implementation, which lives in the
     * model's bundle — the JVM resolves that superclass through this loader, and this loader's
     * parent is the persistence bundle's, which cannot see it.
     */
    private final Map<String, Class<?>> parents = new ConcurrentHashMap<>();

    @Override
    public Class<?> createDynamicClass(String className, Class<?> parentClass) {
    	parents.put(parentClass.getName(), parentClass);
    	return super.createDynamicClass(className, parentClass);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
    	if (className.equals(DynamicEObjectImpl.class.getName())) {
    		return DynamicEObjectImpl.class;
    	}
    	Class<?> parent = parents.get(className);
    	if (parent != null) {
    		return parent;
    	}
        return super.findClass(className);
    }
}
