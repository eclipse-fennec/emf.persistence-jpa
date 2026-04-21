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
package org.eclipse.fennec.persistence.eclipselink.spi;

import static java.util.Objects.isNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.epersistence.Properties;
import org.eclipse.fennec.persistence.orm.helper.EORMHelper;

/**
 * 
 * @author Mark Hoffmann
 * @since 12.12.2024
 */
public class EPersistenceContextImpl implements EPersistenceContext {
	
	private final PersistenceUnit persistenceUnit;
	private final List<EClassifier> eClassifiers = new ArrayList<>();
	private final List<EPackage> ePackages = new ArrayList<>();
	private final String id;
	private URL metadataURL;

	/**
	 * Creates a new instance.
	 */
	public EPersistenceContextImpl(String name, List<EntityMappings> mappings ) {
		persistenceUnit = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		persistenceUnit.setName(name);
		Properties props = EPersistenceFactory.eINSTANCE.createProperties();
		persistenceUnit.setProperties(props);
		mappings.stream().map(EcoreUtil::copy).map(EntityMappings.class::cast).forEach(persistenceUnit.getEntityMappings()::add);
		updatePackages();
		id = UUID.randomUUID().toString();
	}
	
	/**
	 * Creates a new instance.
	 */
	public EPersistenceContextImpl(PersistenceUnit persistenceUnit ) {
		this.persistenceUnit = EcoreUtil.copy(persistenceUnit);
		if (isNull(this.persistenceUnit.getProperties())) {
			Properties props = EPersistenceFactory.eINSTANCE.createProperties();
			this.persistenceUnit.setProperties(props);
		}
		updatePackages();
		id = UUID.randomUUID().toString();
	}
	
	/**
	 * Sets the metadataURL.
	 * @param metadataURL the metadataURL to set
	 */
	void setMetadataURL(URL metadataURL) {
		this.metadataURL = metadataURL;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.EPersistenceContext#getPersistenceUnit()
	 */
	@Override
	public PersistenceUnit getPersistenceUnit() {
		return persistenceUnit;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.EPersistenceContext#getMappings()
	 */
	@Override
	public List<EntityMappings> getMappings() {
		return persistenceUnit.getEntityMappings();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.EPersistenceContext#getPersistenceUnitName()
	 */
	@Override
	public String getPersistenceUnitName() {
		return persistenceUnit.getName();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.EPersistenceContext#getMetadataUrl()
	 */
	@Override
	public URL getMetadataUrl() {
		return metadataURL;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.EPersistenceContext#getClassifiers()
	 */
	@Override
	public List<EClassifier> getClassifiers() {
		return Collections.unmodifiableList(eClassifiers);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.EPersistenceContext#getEPackages()
	 */
	@Override
	public List<EPackage> getEPackages() {
		return Collections.unmodifiableList(ePackages);
	}
	
	private void updatePackages() {
		eClassifiers.addAll(EORMHelper.getEClassifier(persistenceUnit));
		ePackages.addAll(EORMHelper.getEPackages(eClassifiers));
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.EPersistenceContext#getId()
	 */
	@Override
	public String getId() {
		return id;
	}
	
}
