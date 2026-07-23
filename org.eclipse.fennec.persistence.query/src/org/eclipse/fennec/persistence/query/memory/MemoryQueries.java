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
package org.eclipse.fennec.persistence.query.memory;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.support.QueryContexts;

/**
 * Convenience facade of the {@code memory} backend: validate, translate and evaluate a
 * canonical query against in-memory objects in one call — the counterpart of the
 * database backends' {@code JpaQueries}/{@code MongoQueries} helpers, plus execution
 * (there is no resource in between).
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
public final class MemoryQueries {

	private static final MemoryQueryProcessor PROCESSOR = new MemoryQueryProcessor();

	private MemoryQueries() {
	}

	/**
	 * Validates and translates the query for in-memory evaluation.
	 *
	 * @param query the canonical query, must not be {@code null}
	 * @param parameters bound placeholder values; may be {@code null}
	 * @return the executable plan
	 * @throws QueryException if validation reports errors or translation fails
	 */
	public static MemoryQueryPlan translate(Query query, Map<String, Object> parameters)
			throws QueryException {
		Diagnostic diagnostic = PROCESSOR.validate(query, query.getFrom());
		if (diagnostic.getSeverity() == Diagnostic.ERROR) {
			throw new QueryException(text(diagnostic), null, diagnostic);
		}
		return (MemoryQueryPlan) PROCESSOR.translate(query,
				QueryContexts.of(query.getFrom(), null, parameters, null));
	}

	/**
	 * Validates, translates and evaluates the query against the candidates.
	 *
	 * @param query the canonical query, must not be {@code null}
	 * @param candidates the in-memory objects to query, must not be {@code null}
	 * @param parameters bound placeholder values; may be {@code null}
	 * @return the result in the query's shape
	 * @throws QueryException if validation reports errors or translation fails
	 */
	public static QueryResult execute(Query query, Collection<? extends EObject> candidates,
			Map<String, Object> parameters) throws QueryException {
		return translate(query, parameters).execute(candidates);
	}

	private static String text(Diagnostic diagnostic) {
		StringBuilder text = new StringBuilder(diagnostic.getMessage() == null ? "" : diagnostic.getMessage());
		diagnostic.getChildren().forEach(child -> text.append("; ").append(child.getMessage()));
		return text.toString();
	}
}
