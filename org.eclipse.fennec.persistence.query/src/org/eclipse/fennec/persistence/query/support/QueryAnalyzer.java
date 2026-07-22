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
package org.eclipse.fennec.persistence.query.support;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.fennec.model.query.And;
import org.eclipse.fennec.model.query.Average;
import org.eclipse.fennec.model.query.BoolComparator;
import org.eclipse.fennec.model.query.Comparator;
import org.eclipse.fennec.model.query.DateComparator;
import org.eclipse.fennec.model.query.EnumComparator;
import org.eclipse.fennec.model.query.Eq;
import org.eclipse.fennec.model.query.IsInRange;
import org.eclipse.fennec.model.query.Not;
import org.eclipse.fennec.model.query.NumberComparator;
import org.eclipse.fennec.model.query.Operation;
import org.eclipse.fennec.model.query.Or;
import org.eclipse.fennec.model.query.QObject;
import org.eclipse.fennec.model.query.QSubject;
import org.eclipse.fennec.model.query.QWhere;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.SimpleValueComparator;
import org.eclipse.fennec.model.query.StringComparator;
import org.eclipse.fennec.model.query.ToLowerCase;
import org.eclipse.fennec.model.query.ToUpperCase;
import org.eclipse.fennec.model.utilities.FeaturePath;
import org.eclipse.fennec.persistence.query.api.QueryFeature;
import org.eclipse.fennec.persistence.query.api.QueryShape;

/**
 * Analyzes a canonical {@link Query} and reports which {@link QueryFeature}s it uses,
 * the maximum {@code FeaturePath} depth it traverses and the {@link QueryShape} of its
 * result. The analysis is a pure function of the query — it never touches a backend.
 * <p>
 * Chaining semantics: the first {@code where} entry is the base predicate; every further
 * entry chains with the semantics of its concrete type ({@link And}/{@link Or}). A
 * {@link Not} negates its own predicate wherever it appears.
 * <p>
 * Placeholder convention: a comparator value starting with {@code ":"} is a named
 * parameter reference and registers {@link QueryFeature#PARAMETERS}.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class QueryAnalyzer {

	/** Prefix marking a comparator value as a named parameter placeholder. */
	public static final String PARAMETER_PREFIX = ":";

	private QueryAnalyzer() {
	}

	/**
	 * Analyzes the given query.
	 *
	 * @param query the query to analyze, must not be {@code null}
	 * @return the analysis result
	 */
	public static QueryAnalysis analyze(Query query) {
		if (query == null) {
			throw new IllegalArgumentException("query must not be null");
		}
		Set<QueryFeature> features = EnumSet.noneOf(QueryFeature.class);
		int maxDepth = 0;

		// --- where tree ---
		int index = 0;
		for (QWhere where : query.getWhere()) {
			if (index > 0) {
				if (where instanceof Or) {
					features.add(QueryFeature.LOGICAL_OR);
				} else if (where instanceof And) {
					features.add(QueryFeature.LOGICAL_AND);
				}
			}
			if (where instanceof Not) {
				features.add(QueryFeature.LOGICAL_NOT);
			}
			analyzeComparator(where.getComparator(), features);
			analyzeOperation(where.getOperation(), false, features);
			int depth = depthOf(where.getFeaturePath());
			maxDepth = Math.max(maxDepth, depth);
			if (depth > 1) {
				features.add(QueryFeature.FEATUREPATH_NESTED);
			}
			index++;
		}

		// --- aggregation / grouping ---
		boolean grouped = !query.getGroupBy().isEmpty();
		if (grouped) {
			features.add(QueryFeature.GROUP_BY);
			for (FeaturePath path : query.getGroupBy()) {
				int depth = depthOf(path);
				maxDepth = Math.max(maxDepth, depth);
				if (depth > 1) {
					features.add(QueryFeature.FEATUREPATH_NESTED);
				}
			}
		}

		// --- subjects (projection) ---
		boolean aggregating = false;
		for (QSubject subject : query.getSubject()) {
			features.add(QueryFeature.PROJECTION);
			int depth = depthOf(subject.getFeaturePath());
			maxDepth = Math.max(maxDepth, depth);
			if (depth > 1) {
				features.add(QueryFeature.PROJECTION_NESTED);
			}
			aggregating |= analyzeOperation(subject.getOperation(), grouped, features);
		}

		// --- shaping ---
		if (!query.getSortBy().isEmpty()) {
			features.add(QueryFeature.SORT);
		}
		if (query.getLimit() > 0) {
			features.add(QueryFeature.LIMIT);
		}
		if (query.getSkip() > 0) {
			features.add(QueryFeature.SKIP);
		}
		if (query.isDistinct()) {
			features.add(QueryFeature.DISTINCT);
		}
		if (query.isCount()) {
			features.add(QueryFeature.COUNT);
		}

		// --- type filter ---
		for (QObject from : query.getFrom()) {
			if (from.getRootEClass() != null) {
				features.add(QueryFeature.TYPE_FILTER);
			}
		}

		QueryShape shape = deriveShape(query, grouped, aggregating);
		return new QueryAnalysis(features, maxDepth, shape);
	}

	private static QueryShape deriveShape(Query query, boolean grouped, boolean aggregating) {
		if (query.isCount()) {
			return QueryShape.COUNT;
		}
		if (grouped || aggregating) {
			return QueryShape.AGGREGATION;
		}
		if (!query.getSubject().isEmpty()) {
			return QueryShape.PROJECTION;
		}
		return QueryShape.OBJECTS;
	}

	private static void analyzeComparator(Comparator comparator, Set<QueryFeature> features) {
		if (comparator == null) {
			return;
		}
		// Order matters: Eq extends NumberComparator, IsLiteral extends EnumComparator, ...
		if (comparator instanceof Eq) {
			features.add(QueryFeature.WHERE_EQ);
		} else if (comparator instanceof NumberComparator) {
			features.add(QueryFeature.WHERE_COMPARISON);
		} else if (comparator instanceof StringComparator) {
			features.add(QueryFeature.WHERE_STRING_MATCH);
		} else if (comparator instanceof DateComparator) {
			features.add(QueryFeature.WHERE_DATE);
		} else if (comparator instanceof EnumComparator) {
			features.add(QueryFeature.WHERE_ENUM);
		} else if (comparator instanceof BoolComparator) {
			features.add(QueryFeature.WHERE_BOOL);
		} else if (comparator instanceof IsInRange range) {
			features.add(QueryFeature.WHERE_RANGE);
			if (isParameter(range.getStartValue()) || isParameter(range.getEndValue())) {
				features.add(QueryFeature.PARAMETERS);
			}
			return;
		}
		if (comparator instanceof SimpleValueComparator simple && isParameter(simple.getValue())) {
			features.add(QueryFeature.PARAMETERS);
		}
	}

	/**
	 * @return {@code true} if the operation is an aggregate in the given grouping context
	 */
	private static boolean analyzeOperation(Operation operation, boolean grouped, Set<QueryFeature> features) {
		if (operation == null) {
			return false;
		}
		if (operation instanceof ToLowerCase) {
			features.add(QueryFeature.OP_TO_LOWER);
		} else if (operation instanceof ToUpperCase) {
			features.add(QueryFeature.OP_TO_UPPER);
		} else if (operation instanceof Average) {
			if (grouped) {
				features.add(QueryFeature.AGG_AVG);
				return true;
			}
			features.add(QueryFeature.OP_AVERAGE);
		}
		return false;
	}

	private static int depthOf(FeaturePath path) {
		return path == null ? 0 : path.getFeature().size();
	}

	private static boolean isParameter(String value) {
		return value != null && value.startsWith(PARAMETER_PREFIX);
	}
}
