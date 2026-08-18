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
package org.eclipse.fennec.persistence.eclipselink.query;

import java.util.Map;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.support.QueryContexts;

/**
 * Validate-then-translate helper for the JPA resource. Lives outside the resource class
 * because {@code Resource.Diagnostic} (inherited into every resource body) shadows
 * {@code org.eclipse.emf.common.util.Diagnostic} there.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class JpaQueries {

	private JpaQueries() {
	}

	/**
	 * Validates the query against the processor's capabilities and translates it.
	 *
	 * @param processor the JPA query processor
	 * @param query the canonical query
	 * @param rootEClass the resolved root type
	 * @param converters the shared converter service for literal/parameter values
	 *        (issue #164); may be {@code null} for identity
	 * @param parameters bound placeholder values; may be {@code null}
	 * @param options backend options; may be {@code null}
	 * @return the executable plan
	 * @throws QueryException if validation reports errors (the diagnostic is attached)
	 *         or translation fails
	 */
	public static JpaQueryPlan translate(QueryProcessor processor, Query query, EClass rootEClass,
			ConverterService converters, Map<String, Object> parameters, Map<?, ?> options)
			throws QueryException {
		Diagnostic diagnostic = processor.validate(query, rootEClass);
		if (diagnostic.getSeverity() == Diagnostic.ERROR) {
			throw new QueryException(text(diagnostic), null, diagnostic);
		}
		return (JpaQueryPlan) processor.translate(query,
				QueryContexts.of(rootEClass, converters, parameters, options));
	}

	private static String text(Diagnostic diagnostic) {
		StringBuilder text = new StringBuilder(diagnostic.getMessage() == null ? "" : diagnostic.getMessage());
		diagnostic.getChildren().forEach(child -> text.append("; ").append(child.getMessage()));
		return text.toString();
	}
}
