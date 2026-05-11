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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopier;
import org.eclipse.fennec.persistence.eclipselink.descriptors.EClassDescriptor;
import org.eclipse.fennec.persistence.eclipselink.descriptors.EInstantiationPolicy;
import org.eclipse.fennec.persistence.eclipselink.exception.EDescriptorException;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.tools.schemaframework.DynamicSchemaManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Eclipselink dynamic context for OSGi 
 * @author Mark Hoffmann
 * @since 13.01.2023
 */
public class EDynamicHelper extends JPADynamicHelper {

	private static final Logger LOG = Logger.getLogger(EDynamicHelper.class.getName());

	/**
	 * Creates a {@link EObject} instance from an {@link EClassDescriptor} 
	 * @param descriptor the {@link EClassDescriptor}
	 * @return the instance or throws an exception
	 */
	public static EObject createInstance(ClassDescriptor descriptor) {
		requireNonNull(descriptor, "No class descriptor provided to create an instance");
		InstantiationPolicy ip = descriptor.getInstantiationPolicy();
		if (ip instanceof EInstantiationPolicy eip) {
			return (EObject) eip.buildNewInstance();
		} else {
			throw EDescriptorException.noEInstantiationPolicySet(null, descriptor);
		}
	}
	
	public static EObject createClone(Object source, ClassDescriptor descriptor, EDynamicTypeContext context) {
		requireNonNull(descriptor, "No class descriptor provided to create a clone");
		if (source instanceof EObject eSource) {
			EObject clone = createInstance(descriptor);
			return new ECopier(clone, context).copy(eSource);
		} else {
			throw EDescriptorException.valueIsNoEObject(source, descriptor);
		}
	}
	
	final DynamicClassLoader dcl;

	/**
	 * Creates a new instance.
	 * @param emf {@link EntityManagerFactory}
	 */
	public EDynamicHelper(EntityManagerFactory emf, DynamicClassLoader dcl) {
		super(emf);
		this.dcl = dcl;
	}
	
	/**
	 * Creates a new instance.
	 * @param em {@link EntityManager}
	 */
	public EDynamicHelper(EntityManager em, DynamicClassLoader dcl) {
		super(em);
		this.dcl = dcl;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.dynamic.DynamicHelper#getDynamicClassLoader()
	 */
	@Override
	public DynamicClassLoader getDynamicClassLoader() {
		ConversionManager cm = null;

        if (isNull(session)) {
            cm = ConversionManager.getDefaultManager();
        } else {
            cm = session.getPlatform().getConversionManager();
        }

        if (cm.getLoader() instanceof DynamicClassLoader dcLoader) {
            return dcLoader;
        }

        DynamicClassLoader _dcl = isNull(this.dcl) ? new DynamicClassLoader(getClass().getClassLoader()) : this.dcl;
        cm.setLoader(_dcl);

        if (isNull(session)) {
            ConversionManager.setDefaultLoader(_dcl);
        }

        return _dcl;
	}
	
    /**
     * Add a {@link List} {@link EDynamicType} instances to a session and optionally generate
     * needed tables with or without FK constraints. Equivalent to
     * {@link #addETypes(DdlAction, boolean, List)} with {@link DdlAction#CREATE_TABLES} or
     * {@link DdlAction#NONE} depending on {@code createMissingTables}.
     */
    public void addETypes(boolean createMissingTables, boolean generateFKConstraints, List<EDynamicType> eTypes) {
        addETypes(createMissingTables ? DdlAction.CREATE_TABLES : DdlAction.NONE,
                generateFKConstraints, eTypes);
    }

    /**
     * Add a {@link List} of {@link EDynamicType} instances to a session and perform the
     * requested schema-generation {@code action} for their tables.
     */
    public void addETypes(DdlAction action, boolean generateFKConstraints, List<EDynamicType> eTypes) {
        if (Objects.isNull(eTypes) || eTypes.isEmpty()) {
            throw new IllegalArgumentException("No types provided");
        }
        Collection<ClassDescriptor> descriptors = new ArrayList<>(eTypes.size());
        for (EDynamicType eType : eTypes) {
            ClassDescriptor classDescriptor = eType.getDescriptor();
            descriptors.add(classDescriptor);
            if (!classDescriptor.requiresInitialization((AbstractSession) session)) {
                classDescriptor.getInstantiationPolicy().initialize((AbstractSession) session);
            }
        }
        session.addDescriptors(descriptors);
        for (ClassDescriptor desc : descriptors) {
            if (nonNull(desc.getJavaClassName())) {
                fqClassnameToDescriptor.put(desc.getJavaClassName(), desc);
            }
        }

        if (action == null || action == DdlAction.NONE) {
            return;
        }
        if (!getSession().isConnected()) {
            getSession().login();
        }
        DynamicSchemaManager mgr = new DynamicSchemaManager(session);
        switch (action) {
            case CREATE_TABLES:
                createMissingSchemas(descriptors);
                mgr.createTables(generateFKConstraints);
                break;
            case DROP_AND_CREATE_TABLES:
                createMissingSchemas(descriptors);
                mgr.replaceDefaultTables(false, generateFKConstraints);
                break;
            case CREATE_OR_EXTEND_TABLES:
                createMissingSchemas(descriptors);
                mgr.extendDefaultTables(generateFKConstraints);
                break;
            default:
                break;
        }
    }

    private void createMissingSchemas(Collection<ClassDescriptor> descriptors) {
        descriptors.stream()
            .flatMap(d -> d.getTables().stream())
            .map(DatabaseTable::getTableQualifier)
            .filter(s -> s != null && !s.isBlank())
            .distinct()
            .forEach(schema -> {
                try {
                    session.executeNonSelectingSQL(
                            "CREATE SCHEMA IF NOT EXISTS " + schema);
                } catch (Exception e) {
                    LOG.warning("Could not create schema '" + schema + "': " + e.getMessage());
                }
            });
    }

}
