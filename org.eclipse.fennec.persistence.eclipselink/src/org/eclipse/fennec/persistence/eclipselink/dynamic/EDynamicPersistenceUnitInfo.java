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

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitProperty;

import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;

/**
 * {@link PersistenceUnitInfo} implementation with dynamic support
 * @author Mark Hoffmann
 * @since 10.12.2024
 */
public class EDynamicPersistenceUnitInfo implements PersistenceUnitInfo {

	private static final Logger LOG = Logger.getLogger(EDynamicPersistenceUnitInfo.class.getName());

	protected DataSource jtaDataSource;
	protected DataSource nonJtaDataSource;
	protected List<String> mappingFiles;


    protected List<SEPersistenceUnitProperty> persistenceUnitProperties = new ArrayList<>();
    protected Properties properties;

    protected ClassLoader tempClassLoader;
    protected ClassLoader realClassLoader;
    private final URL persistenceUnitRootUrl;
	private final PersistenceUnit unitModel;
    
    public EDynamicPersistenceUnitInfo(PersistenceUnit unitModel, URL persistenceUnitRootUrl, Map<String, Object> props) {
		this.unitModel = unitModel;
		this.persistenceUnitRootUrl = persistenceUnitRootUrl;
		properties = new Properties();
        properties.putAll(props);
        unitModel.getProperties().getProperty().forEach(p->{
        	properties.put(p.getName(), p.getValue());
        });
        /*
         * Route the model's transaction type through the property channel: EclipseLink
         * gives PersistenceUnitProperties.TRANSACTION_TYPE precedence over
         * PersistenceUnitInfo#getTransactionType() on every deploy path (EclipseLink
         * bug 5867753 handling). This keeps the effective configuration off the
         * jakarta.persistence.spi enum that is deprecated for removal since JPA 3.2 —
         * only the SPI-mandated getter below still has to reference it.
         */
        if (Objects.nonNull(unitModel.getTransactionType())) {
            properties.putIfAbsent(PersistenceUnitProperties.TRANSACTION_TYPE,
                    unitModel.getTransactionType().name());
        }

        if (props.containsKey(PersistenceUnitProperties.CLASSLOADER)) {
            setClassLoader((ClassLoader) props.get(PersistenceUnitProperties.CLASSLOADER));
        }
    }

    @Override
    public String getPersistenceUnitName() {
        return unitModel.getName();
    }

    public void setPersistenceUnitName(String persistenceUnitName) {
        unitModel.setName(persistenceUnitName);
    }

    public List<SEPersistenceUnitProperty> getPersistenceUnitProperties() {
        return persistenceUnitProperties;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return unitModel.getProvider();
    }

    /**
     * SPI-mandated bridge: in Jakarta Persistence 3.2 this {@link PersistenceUnitInfo}
     * method is still declared with the {@code jakarta.persistence.spi} enum that is
     * deprecated for removal — the replacement {@code jakarta.persistence.PersistenceUnitTransactionType}
     * only arrives in the SPI with JPA 4. Implementing the interface therefore forces the
     * reference to the deprecated type; the import (and its IDE warning) is kept
     * deliberately as the visible reminder to migrate this method when moving to JPA 4.
     * EclipseLink itself prefers the {@code PersistenceUnitProperties.TRANSACTION_TYPE}
     * property (set in the constructor) over this getter, so this value is a consistent
     * fallback only.
     */
    @SuppressWarnings("removal")
	@Override
    public PersistenceUnitTransactionType getTransactionType() {
        return PersistenceUnitTransactionType.valueOf(unitModel.getTransactionType().name());
    }

    @Override
    public DataSource getJtaDataSource() {
        return jtaDataSource;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return nonJtaDataSource;
    }

    public void setNonJtaDataSource(DataSource nonJtaDataSource) {
        this.nonJtaDataSource = nonJtaDataSource;
    }

    @Override
    public List<String> getMappingFileNames() {
        return Collections.unmodifiableList(unitModel.getMappingFile());
    }

    /* 
     * (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getJarFileUrls()
     */
    @Override
    public List<URL> getJarFileUrls() {
    	List<URL> urls = unitModel.getJarFile().stream().map(s->{
			try {
				return new URI(s).toURL();
			} catch (Exception e) {
				LOG.log(Level.WARNING, "Failed to parse JAR file URL", e);
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
        return Collections.unmodifiableList(urls);
    }

    /* 
     * (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getPersistenceUnitRootUrl()
     */
    @Override
    public URL getPersistenceUnitRootUrl() {
        return persistenceUnitRootUrl;
    }

    /* 
     * (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getManagedClassNames()
     */
    @Override
    public List<String> getManagedClassNames() {
        return Collections.unmodifiableList(unitModel.getEntityClass());
    }

    /* 
     * (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#excludeUnlistedClasses()
     */
    @Override
    public boolean excludeUnlistedClasses() {
        return unitModel.isExcludeUnlistedClasses();
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    /* 
     * (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getClassLoader()
     */
    @Override
    public ClassLoader getClassLoader() {
        return realClassLoader;
    }

    /* 
     * (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()
     */
    @Override
    public ClassLoader getNewTempClassLoader() {
        return tempClassLoader;
    }

    public void setClassLoader(ClassLoader loader) {
        this.realClassLoader = loader;
    }

    /**
     * @return
     */
    public Collection<String> getJarFiles() {
        return Collections.unmodifiableList(unitModel.getJarFile());
    }

    /* 
     * (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getPersistenceXMLSchemaVersion()
     */
    @Override
    public String getPersistenceXMLSchemaVersion() {
    	return EPersistencePackage.eNS_URI.substring(EPersistencePackage.eNS_URI.lastIndexOf("/"));
    }

    /* 
     * (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getSharedCacheMode()
     */
    @Override
    public SharedCacheMode getSharedCacheMode() {
        return SharedCacheMode.valueOf(unitModel.getSharedCacheMode().name());
    }

    /* 
     * (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#getValidationMode()
     */
    @Override
    public ValidationMode getValidationMode() {
        return ValidationMode.valueOf(unitModel.getValidationMode().name());
    }

    /* 
     * (non-Javadoc)
     * @see jakarta.persistence.spi.PersistenceUnitInfo#addTransformer(jakarta.persistence.spi.ClassTransformer)
     */
    @Override
    public void addTransformer(ClassTransformer transformer) {
    	// Do nothing
    }

	/* 
	 * (non-Javadoc)
	 * @see jakarta.persistence.spi.PersistenceUnitInfo#getScopeAnnotationName()
	 */
	@Override
	public String getScopeAnnotationName() {
		return unitModel.getScope();
	}

	/* 
	 * (non-Javadoc)
	 * @see jakarta.persistence.spi.PersistenceUnitInfo#getQualifierAnnotationNames()
	 */
	@Override
	public List<String> getQualifierAnnotationNames() {
		return Collections.unmodifiableList(unitModel.getQualifier());
	}

}
