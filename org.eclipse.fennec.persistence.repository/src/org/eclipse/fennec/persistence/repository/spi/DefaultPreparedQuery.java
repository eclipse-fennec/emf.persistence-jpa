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
package org.eclipse.fennec.persistence.repository.spi;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.fennec.model.query.ParameterDecl;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.repository.api.PreparedQuery;

/**
 * {@link PreparedQuery} handle over an {@link AbstractRepository}: either holds the
 * validated canonical query (prepare(Query)), or delegates by name to the backend's
 * query catalog (prepare(name)) — in the latter case {@link #query()} answers
 * {@code null} because the catalog has no load-back API yet, and the declared
 * parameters are unknown.
 *
 * @since 18.08.2026
 */
public class DefaultPreparedQuery implements PreparedQuery {

	private final AbstractRepository repository;
	private final Query query;
	private final String name;

	DefaultPreparedQuery(AbstractRepository repository, Query query) {
		this.repository = requireNonNull(repository);
		this.query = requireNonNull(query);
		this.name = query.getName();
	}

	DefaultPreparedQuery(AbstractRepository repository, String name) {
		this.repository = requireNonNull(repository);
		this.query = null;
		this.name = requireNonNull(name);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Query query() {
		return query;
	}

	@Override
	public EList<ParameterDecl> parameterDeclarations() {
		return isNull(query) ? ECollections.emptyEList() : ECollections.unmodifiableEList(query.getParameters());
	}

	@Override
	public QueryResult execute(Map<String, Object> parameters) throws IOException {
		return execute(parameters, null);
	}

	@Override
	public QueryResult execute(Map<String, Object> parameters, Map<?, ?> options) throws IOException {
		return isNull(query)
				? repository.find(name, parameters, options)
				: repository.find(query, parameters, options);
	}
}
