/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
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
package org.eclipse.fennec.persistence.engine;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.persistence.Options;
import org.eclipse.fennec.persistence.helper.EMFHelper;
import org.eclipse.fennec.persistence.resource.PersistenceResource;

/**
 * This is a base component class for input and output streams
 * @param <DRIVER> Driver, Table or Collection type, whatever is the base to do something on the database
 * @param <QT> the query object type of you implementation
 * @param <RT> the result type {@link java.sql.ResultSet} for jdbc or a FindIterable for MongoDB
 * @param <ENGINE> the native query engine
 * @param <MAPPER> an mapper for result types to {@link EObject} and {@link EObject} to input type
 * @author Mark Hoffmann
 * @since 08.04.2022
 */
public abstract class BasicPersistenceEngine implements PersistenceEngine {
	
//	private ConverterService converterService;
//	private volatile Map<String, PrimaryKeyFactory> idFactories = new ConcurrentHashMap<>();
	/** handlerList mapper for the result type to EObject using an optional mapper*/
//	private volatile List<InputContentHandler<RESULTTYPE, MAPPER>> handlerList = new CopyOnWriteArrayList<>();
	private final Map<Object, Object> mergedOptions = new HashMap<>();
	private final Map<Object, Object> engineProperties = new HashMap<>();
	private PersistenceResource resource;
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.old.old.engine.PersistenceEngine#setResource(org.eclipse.fennec.persistence.old.old.resource.PersistenceResource)
	 */
	@Override
	public void setResource(PersistenceResource resource) {
		this.resource = resource;
		normalizeOptions(engineProperties);
	}
	
	/**
	 * Sets the converter service
	 * @param converterService the converter service to set
	 */
//	public void setConverterService(ConverterService converterService) {
//		this.converterService = converterService;
//	}
	
	/**
	 * Returns the converterService.
	 * @return the converterService
	 */
//	public ConverterService getConverterService() {
//		return converterService;
//	}

	/**
	 * Sets the query engine
	 * @param queryEngine the query engine to set
	 */
//	public void setQueryEngine(QueryEngine<QUERYTYPE, QUERYENGINE> queryEngine) {
//		this.queryEngine = queryEngine;
//	}
	
	/**
	 * Returns the queryEngine.
	 * @return the queryEngine
	 */
//	public QueryEngine<QUERYTYPE, QUERYENGINE> getQueryEngine() {
//		return queryEngine;
//	}
	
	/**
	 * Returns the resource.
	 * @return the resource
	 */
	public PersistenceResource getResource() {
		return resource;
	}
	
	/**
	 * Returns the properties.
	 * @return the properties
	 */
	public Map<Object, Object> getProperties() {
		return engineProperties;
	}
	
	/**
	 * Returns the mergedOptions.
	 * @return the mergedOptions
	 */
	public Map<Object, Object> getMergedOptions() {
		return mergedOptions;
	}
	
	public Map<Object, Object> getResponse() {
		return EMFHelper.getResponse(getMergedOptions());
	}
	
	/**
	 * Normalizes the load options
	 * @param options the original options
	 */
	protected void normalizeOptions(Map<Object, Object> options) {
		mergedOptions.putAll(options);
		EClass collectionEClass = Options.getTableEClass(options);
		if (collectionEClass != null && !options.containsKey(Options.READ_FILTER_ECLASS)) {
			mergedOptions.put(Options.READ_FILTER_ECLASS, collectionEClass);
		}
	}
	
}
