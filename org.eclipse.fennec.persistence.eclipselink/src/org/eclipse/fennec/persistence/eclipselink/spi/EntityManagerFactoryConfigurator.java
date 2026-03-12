/*
* Copyright (c) 2024 Contributors to the Eclipse Foundation.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*   SmartCity Jena - initial
*   Stefan Bischof (bipolis.org) - initial
*/
package org.eclipse.fennec.persistence.eclipselink.spi;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.eclipselink.classloader.OSGiDynamicClassloader;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicPersistenceUnitInfo;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeGenerator;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.helper.EORMModelHelper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.sessions.server.Server;
import org.osgi.framework.BundleContext;

import jakarta.persistence.EntityManagerFactory;

/**
 * Configurator for the {@link EntityManagerFactory} setup for EMF 
 * @author Mark Hoffmann
 * @since 10.12.2024
 */
public class EntityManagerFactoryConfigurator {

	private final BundleContext ctx;
	private final DynamicClassLoader dcl;
	private final EORMModelHelper modelHelper;
	
	private final List<EPackage> ePackages = new ArrayList<>();
	private final List<EClassifier> eClassifiers = new ArrayList<>();
	private final Map<String, Object> properties = new HashMap<>();
	private final List<EntityMappings> mappings = new ArrayList<>();
	private EPersistenceContext context;
	private ConverterService converters;
    private DataSource dataSource;
    
    /**
     * Builder for the configurator
     * @author Mark Hoffmann
     * @since 12.12.2024
     */
    public static class Builder {
    	
    	private final EntityManagerFactoryConfigurator configurator;

    	public static Builder create(BundleContext ctx, ResourceSet resourceSet) {
    		return new Builder(ctx, resourceSet);
    	}
    	
    	private Builder(BundleContext ctx, ResourceSet resourceSet) {
    		this.configurator = new EntityManagerFactoryConfigurator(ctx, resourceSet);
    	}
    	
    	public Builder context(EPersistenceContext context) {
    		this.configurator.context = context;
    		this.configurator.mappings.addAll(context.getMappings());
    		return this;
    	}
    	
    	public Builder registerEPackages(List<EPackage> ePackageList) {
    		if (nonNull(ePackageList) && ! ePackageList.isEmpty()) {
    			configurator.ePackages.addAll(ePackageList);
    		}
    		return this;
    	}
    	
    	public Builder registerEClasses(List<EClassifier> eClassifierList) {
    		if (nonNull(eClassifierList) && ! eClassifierList.isEmpty()) {
    			configurator.eClassifiers.addAll(eClassifierList);
    		}
    		return this;
    	}
    	
    	public Builder converter(ConverterService converter) {
    		configurator.converters = converter;
    		return this;
    	}
    	
    	public Builder dataSource(DataSource datasource) {
    		configurator.dataSource = datasource;
    		return this;
    	}
    	
    	public Builder properties(Map<String, Object> properties) {
    		if (nonNull(properties)) {
    			configurator.properties.putAll(properties);
    		}
    		return this;
    	}
    	
    	public Builder enityMappings(List<EntityMappings> mappings) {
    		configurator.addMappings(mappings);
    		return this;
    	}
    	
    	public EntityManagerFactoryConfigurator build() {
    		if (isNull(configurator.context)) {
    			throw new IllegalStateException("Configuration setup is invalid. Verify if a EPersistenceContext was given");
    		}
    		return configurator;
    	}

    }
    
    /**
	 * Creates a new instance.
	 */
	EntityManagerFactoryConfigurator(BundleContext ctx, ResourceSet resourceSet) {
		this.ctx = ctx;
		dcl = new OSGiDynamicClassloader(ctx);
		modelHelper = new EORMModelHelper(resourceSet);
	}
    
    protected EntityManagerFactory configure()
            throws IOException {
    	

        PersistenceProvider persistenceProvider = new org.eclipse.persistence.jpa.PersistenceProvider();
        URL url = ctx.getBundle().getEntry("META-INF/persistence.xml");

        if (isNull(url)) {
        	url = context.getMetadataUrl();
        }
        properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
//        properties.put(PersistenceUnitProperties.METADATA_SOURCE, new EORMMetadataSource(mappings));
        
        /*
         * We provide out persistence unit information and bypass them
         */
        PersistenceUnit persistenceUnit = context.getPersistenceUnit();
        EDynamicPersistenceUnitInfo pui;
        if (nonNull(persistenceUnit)) {
        	pui = new EDynamicPersistenceUnitInfo(persistenceUnit, url, properties);
        } else {
        	PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
        	pu.setName(context.getPersistenceUnitName());
        	pui = new EDynamicPersistenceUnitInfo(pu, url, properties);
        }
        /*
         * We create an empty EntityManagerFactory here
         */
        EntityManagerFactory emf = persistenceProvider.createContainerEntityManagerFactory(pui, properties);
        Server serverSession = JpaHelper.getServerSession(emf);
        /*
         * 
         */
        EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl, serverSession, context.getPersistenceUnitName(), converters);
    	List<EDynamicType> eTypes = new ArrayList<>();
    	if (nonNull(mappings)) {
    		eTypes.addAll(generator.createFromMappings(mappings));
    	}
        /*
         * Now we add our configuration to it. We add our dynamic types for the EMF stuff!
         */
        EDynamicHelper helper = new EDynamicHelper(emf, dcl);
        helper.addETypes(true, true, eTypes);
        return emf;

    }

	/**
	 * Returns the dataSource.
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Returns the resourceSet.
	 * @return the resourceSet
	 */
	public ResourceSet getResourceSet() {
		return modelHelper.getResourceSet();
	}
	
	/**
	 * Add the mappings.
	 * @param mappings the mappings to set
	 */
	void addMappings(List<EntityMappings> mappings) {
		this.mappings.addAll(mappings);
	}
	
}
