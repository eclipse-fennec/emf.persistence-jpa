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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.expression.AliasRef;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.Arithmetic;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.CollectionCount;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.Concat;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IndexOf;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.Literal;
import org.eclipse.fennec.model.expression.MapValue;
import org.eclipse.fennec.model.expression.Negate;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.NumericFunction;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.Substring;
import org.eclipse.fennec.model.expression.TemporalFunction;
import org.eclipse.fennec.model.expression.TypeCheck;
import org.eclipse.fennec.model.expression.Variable;
import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.Computation;
import org.eclipse.fennec.model.query.ComputeStage;
import org.eclipse.fennec.model.query.FilterStage;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.GroupKey;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.SkipStage;
import org.eclipse.fennec.model.query.SortDirection;
import org.eclipse.fennec.model.query.Stage;
import org.eclipse.fennec.model.query.TopStage;
import org.eclipse.fennec.persistence.capabilities.QueryCapabilities;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavor;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavorCapabilities;
import org.eclipse.fennec.persistence.helper.EMaps;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.query.QueryConstants;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.eclipse.fennec.persistence.query.api.QueryPlan;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.expr.ExpressionAnalyzer;
import org.eclipse.fennec.persistence.query.expr.ExpressionValues;
import org.eclipse.fennec.persistence.query.support.QueryAnalysis;
import org.eclipse.fennec.persistence.query.support.QueryValidator;
import org.osgi.service.component.annotations.Component;

/**
 * {@link QueryProcessor} for JPA/EclipseLink over the expression IR.
 * <p>
 * Translates to <b>JPQL</b> (pure string translation, injection-safe: identifiers are
 * model names, every value is a named parameter). Grouped logic renders parenthesised;
 * {@code Exists}/{@code ForAll} become correlated {@code [NOT] EXISTS} subqueries over
 * the collection path with a unique alias per quantifier; case-insensitive matching
 * wraps both sides in {@code LOWER}; string functions map to
 * {@code LOWER/UPPER/TRIM/LENGTH}.
 * <p>
 * Aggregation: a single {@code GroupByStage} pipeline translates to
 * {@code GROUP BY} + aggregate functions. Multi-stage pipelines
 * ({@link QueryFeature#PIPELINE}) are served as far as one JPQL statement reaches:
 * filters before the grouping become {@code WHERE}, after it {@code HAVING}, computes
 * bind aliases or output columns, and {@code Top}/{@code Skip} compose a window
 * <em>after</em> the grouping. Object-space paging before the grouping and a second
 * {@code GroupByStage} are refused. Sorting in row shapes addresses
 * the result variables. {@code expand} hints translate to aliased {@code LEFT JOIN
 * FETCH} chains for single-valued segments and batch-fetch hints from the first
 * to-many segment on (issue #95).
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
@Component(service = QueryProcessor.class, property = QueryConstants.BACKEND_PROPERTY + "=" + JpaQueryProcessor.BACKEND)
public class JpaQueryProcessor implements QueryProcessor {

	/** The backend id of this processor. */
	public static final String BACKEND = "jpa";

	/** The JPQL root alias. */
	static final String ALIAS = "e";

	private final JpaFlavor flavor;
	private final QueryCapabilities capabilities;

	/**
	 * Creates a processor declaring the portable baseline — the flavor-less form the
	 * declarative-services registration uses, and the right answer before any database has
	 * been probed.
	 */
	public JpaQueryProcessor() {
		this(JpaFlavor.UNKNOWN);
	}

	/**
	 * Creates a processor whose capability declaration matches {@code flavor} (issue #172).
	 * Translation itself is flavor-independent — every targeted database speaks the same
	 * JPQL — so the flavor only selects what is declared, never how a query is rendered.
	 *
	 * @param flavor the database flavor; {@code null} is treated as {@link JpaFlavor#UNKNOWN}
	 */
	public JpaQueryProcessor(JpaFlavor flavor) {
		this.flavor = flavor == null ? JpaFlavor.UNKNOWN : flavor;
		this.capabilities = JpaFlavorCapabilities.of(this.flavor);
	}

	/** @return the database flavor this processor declares capabilities for */
	public JpaFlavor flavor() {
		return flavor;
	}

	@Override
	public String backend() {
		return BACKEND;
	}

	@Override
	public QueryCapabilities capabilities() {
		return capabilities;
	}

	@Override
	public Diagnostic validate(Query query, EClass rootEClass) {
		return QueryValidator.validate(ExpressionAnalyzer.analyze(query), rootEClass, capabilities);
	}

	@Override
	public QueryPlan translate(Query query, QueryContext context) throws QueryException {
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query);
		QueryShape shape = analysis.shape();
		if (query.getFrom() == null) {
			throw new QueryException("The query carries no root type (from) — cannot derive the entity name");
		}
		if (query.getApply() != null && !query.getSelect().isEmpty()) {
			throw new QueryException("select and apply are mutually exclusive — aggregation defines its own columns");
		}
		Translation translation = new Translation(context);
		translation.rootEClass = query.getFrom();
		String where = query.getPredicate() == null ? "" : translation.render(query.getPredicate());

		StringBuilder jpql = new StringBuilder("SELECT ");
		List<String> rowKeys = new ArrayList<>();
		List<String> rowAliases = new ArrayList<>();
		String entity = query.getFrom().getName();

		TranslatedPipeline pipeline = shape == QueryShape.AGGREGATION
				? translation.translatePipeline(query, rowKeys, rowAliases)
				: null;
		switch (shape) {
		case COUNT -> jpql.append("COUNT(").append(ALIAS).append(')');
		case OBJECTS -> {
			if (query.isDistinct()) {
				jpql.append("DISTINCT ");
			}
			jpql.append(ALIAS);
		}
		case PROJECTION -> {
			if (query.isDistinct()) {
				jpql.append("DISTINCT ");
			}
			jpql.append(projectionColumns(query, rowKeys, rowAliases, translation));
		}
		case AGGREGATION -> jpql.append(pipeline.columns);
		}
		jpql.append(" FROM ").append(entity).append(' ').append(ALIAS);
		List<String> batchFetchPaths = appendFetchJoins(jpql, query);
		// map-entry joins collected while rendering the grouping (issue #190)
		translation.mapJoins.forEach(jpql::append);
		String conjuncts = pipeline == null ? where
				: Stream.concat(where.isEmpty() ? Stream.empty() : Stream.of(where),
						pipeline.preFilters.stream())
					.map(conjunct -> "(" + conjunct + ")")
					.collect(Collectors.joining(" AND "));
		if (!conjuncts.isEmpty()) {
			jpql.append(" WHERE ").append(conjuncts);
		}
		if (pipeline != null) {
			jpql.append(pipeline.groupBy).append(pipeline.having);
		}
		if (shape != QueryShape.COUNT) {
			appendOrderBy(jpql, query, shape, rowKeys, translation);
		}
		int skip = Math.max(0, query.getSkip());
		int top = Math.max(0, query.getTop());
		if (pipeline != null && (pipeline.skip > 0 || pipeline.top > 0)) {
			// pipeline paging applies first, envelope paging pages that window —
			// both ride the single setFirstResult/setMaxResults pair of the query
			int combinedSkip = pipeline.skip + skip;
			int combinedTop = pipeline.top;
			if (combinedTop > 0) {
				combinedTop -= skip;
				if (combinedTop <= 0) {
					throw new QueryException("The envelope skip exhausts the pipeline top window"
							+ " — the composition can never yield a row");
				}
				if (top > 0) {
					combinedTop = Math.min(combinedTop, top);
				}
			} else {
				combinedTop = top;
			}
			skip = combinedSkip;
			top = combinedTop;
		}
		return new JpaQueryPlan(query, shape, jpql.toString(), translation.parameters,
				skip, top, rowKeys, rowAliases, batchFetchPaths, translation.requiresInlineLiterals);
	}

	/** The JPQL fragments of a translated pipeline (issue #82). */
	private static final class TranslatedPipeline {
		String columns = "";
		final List<String> preFilters = new ArrayList<>();
		String groupBy = "";
		String having = "";
		/** Row-space paging window (0 = unset) — sort-then-limit semantics. */
		int skip;
		int top;
	}

	// -------------------------------------------------- select / group / order

	private String projectionColumns(Query query, List<String> rowKeys, List<String> rowAliases,
			Translation translation) throws QueryException {
		StringBuilder columns = new StringBuilder();
		for (Selection selection : query.getSelect()) {
			// an expression projection (issue #189) renders inline under its mandatory
			// alias, exactly like an expression sort key; a path keeps its derived key
			boolean computed = selection.getKey() != null;
			String key = computed ? selection.getAlias() : outputKey(selection.getAlias(), selection.getPath());
			registerKey(key, selection.getAlias(), rowKeys, rowAliases);
			if (columns.length() > 0) {
				columns.append(", ");
			}
			String rendered = computed
					? translation.operand(selection.getKey(), null)
					: rootPath(selection.getPath());
			columns.append(rendered).append(" AS ").append(key);
		}
		return columns.toString();
	}

	private static String aggregateFunction(Aggregate aggregate, String argument) {
		return switch (aggregate.getMethod()) {
		case SUM -> "SUM(" + argument + ")";
		case MIN -> "MIN(" + argument + ")";
		case MAX -> "MAX(" + argument + ")";
		case AVG -> "AVG(" + argument + ")";
		case COUNT -> "COUNT(" + argument + ")";
		case COUNT_DISTINCT -> "COUNT(DISTINCT " + argument + ")";
		};
	}

	/**
	 * Renders the expand prefetch hints (issue #95). The single-valued prefix of each
	 * expand path becomes an aliased {@code LEFT JOIN FETCH} chain (EclipseLink JPQL
	 * extension); shared prefixes reuse their alias. From the first to-many segment on,
	 * a fetch join would multiply rows and break {@code setMaxResults} counting — those
	 * levels are returned as dotted batch-fetch paths instead, one per level, applied
	 * as {@code eclipselink.batch} hints with {@code BATCH_TYPE = IN} at execution.
	 *
	 * @return the batch-fetch attribute paths ({@code e.a.b} notation), deduplicated
	 */
	private List<String> appendFetchJoins(StringBuilder jpql, Query query) throws QueryException {
		Map<String, String> aliases = new LinkedHashMap<>();
		Set<String> batchPaths = new LinkedHashSet<>();
		for (PropertyPath expand : query.getExpand()) {
			if (expand.getSegments().isEmpty()) {
				throw new QueryException("expand requires at least one reference segment");
			}
			if (expand.getCastBase() != null) {
				throw new QueryException("expand does not support cast paths (castBase)");
			}
			String parentAlias = ALIAS;
			StringBuilder dotted = new StringBuilder(ALIAS);
			boolean batching = false;
			for (EStructuralFeature segment : expand.getSegments()) {
				if (!(segment instanceof EReference reference)) {
					throw new QueryException("expand path segment '" + segment.getName()
							+ "' is not a reference — only references can be prefetched");
				}
				dotted.append('.').append(reference.getName());
				if (batching || reference.isMany()) {
					batching = true;
					batchPaths.add(dotted.toString());
					continue;
				}
				String alias = aliases.get(dotted.toString());
				if (alias == null) {
					alias = "f" + aliases.size();
					aliases.put(dotted.toString(), alias);
					jpql.append(" LEFT JOIN FETCH ").append(parentAlias).append('.')
							.append(reference.getName()).append(' ').append(alias);
				}
				parentAlias = alias;
			}
		}
		return List.copyOf(batchPaths);
	}

	private void appendOrderBy(StringBuilder jpql, Query query, QueryShape shape, List<String> rowKeys,
			Translation translation) throws QueryException {
		if (query.getOrderBy().isEmpty()) {
			return;
		}
		jpql.append(" ORDER BY ");
		for (int i = 0; i < query.getOrderBy().size(); i++) {
			OrderBy orderBy = query.getOrderBy().get(i);
			if (i > 0) {
				jpql.append(", ");
			}
			if (orderBy.getKey() instanceof AliasRef aliasRef) {
				// a bare AliasRef is a plain output-column sort (issue #102): JPQL
				// result variables ARE addressable in ORDER BY (unlike HAVING)
				if (!rowKeys.contains(aliasRef.getAlias())) {
					throw new QueryException("Sort key '" + aliasRef.getAlias()
							+ "' does not address an output key of the projection/aggregation (keys: "
							+ rowKeys + ") — alias the column accordingly");
				}
				jpql.append(aliasRef.getAlias());
			} else if (orderBy.getKey() != null) {
				// arbitrary sort expressions render inline (issue #84); AliasRef
				// inside a computed key re-renders the pipeline column
				jpql.append(translation.operand(orderBy.getKey(), null));
			} else if (shape == QueryShape.OBJECTS) {
				jpql.append(rootPath(orderBy.getPath()));
			} else {
				jpql.append(rowKey(orderBy.getPath(), rowKeys));
			}
			jpql.append(orderBy.getDirection() == SortDirection.DESC ? " DESC" : " ASC");
		}
	}

	private String rowKey(PropertyPath path, List<String> rowKeys) throws QueryException {
		String flattened = outputKey(null, path);
		if (rowKeys.contains(flattened)) {
			return flattened;
		}
		if (path.getSegments().size() == 1 && rowKeys.contains(path.getSegments().get(0).getName())) {
			return path.getSegments().get(0).getName();
		}
		throw new QueryException("Sort path '" + flattened
				+ "' does not address an output key of the projection/aggregation (keys: " + rowKeys
				+ ") — alias the column accordingly");
	}

	private static void registerKey(String key, String alias, List<String> rowKeys, List<String> rowAliases)
			throws QueryException {
		if (rowKeys.contains(key)) {
			throw new QueryException("Duplicate result key '" + key + "' — use distinct aliases");
		}
		rowKeys.add(key);
		rowAliases.add(alias);
	}

	private static String outputKey(String alias, PropertyPath path) {
		if (alias != null && !alias.isBlank()) {
			return alias;
		}
		StringBuilder key = new StringBuilder();
		path.getSegments().forEach(segment -> {
			if (key.length() > 0) {
				key.append('_');
			}
			key.append(segment.getName());
		});
		return key.toString();
	}

	private static String rootPath(PropertyPath path) throws QueryException {
		if (path.getBase() != null) {
			throw new QueryException("Variable-based paths are only valid inside quantifier predicates");
		}
		return pathFrom(ALIAS, path);
	}

	private static String pathFrom(String alias, PropertyPath path) {
		// castBase downcasts the navigation origin (issue #80) — JPQL TREAT resolves
		// by entity name, sidestepping the deliberately flat dynamic Java classes.
		// A cast that cannot narrow anything is dropped: TREAT over an entity without an
		// inheritance hierarchy is rejected by the database, and the cast is a no-op anyway
		// (issue #175 found this through the command × selector cross product).
		StringBuilder rendered = new StringBuilder(castsWithinAHierarchy(path)
				? "TREAT(" + alias + " AS " + path.getCastBase().getName() + ")"
				: alias);
		path.getSegments().forEach(segment -> rendered.append('.').append(segment.getName()));
		return rendered.toString();
	}

	/**
	 * Whether a downcast has a hierarchy to downcast <em>in</em>.
	 * <p>
	 * {@code TREAT} resolves through the entity's type discriminator, which only exists where
	 * there is inheritance. Casting a type that has no supertype cannot narrow anything, and
	 * asking the database for it fails instead of answering — found by the command × selector
	 * cross product (issue #175), where a cast to the root's own type reached the database for
	 * the first time.
	 */
	private static boolean castsWithinAHierarchy(PropertyPath path) {
		return path.getCastBase() != null && !path.getCastBase().getESuperTypes().isEmpty();
	}

	// -------------------------------------------------- predicate rendering

	/** Carries the parameter map and quantifier alias scope through one translation. */
	private final class Translation {

		private final QueryContext context;
		private final Map<String, Object> parameters = new LinkedHashMap<>();

		/**
		 * Set once an expression-valued group key has been rendered — see
		 * {@link #groupKeyExpression(Expression)}. Travels to the plan, which turns it into the
		 * EclipseLink hint that keeps the two renderings of that expression identical in SQL.
		 */
		private boolean requiresInlineLiterals;
		private final Map<Variable, String> aliases = new HashMap<>();
		private int aliasCounter = 0;

		/** Pipeline output columns (alias → rendered JPQL) for AliasRef resolution (issue #82). */
		private final Map<String, String> columnExpressions = new LinkedHashMap<>();

		/**
		 * Map accesses rendered as joins instead of correlated subselects (issue #190):
		 * {@code <entry path>|<key>} → join alias, plus the join clauses in FROM order.
		 * One join per distinct map-and-key pair, so the SELECT list and the GROUP BY of
		 * the same key address the very same alias.
		 */
		private final Map<String, String> mapJoinAliases = new LinkedHashMap<>();
		private final List<String> mapJoins = new ArrayList<>();

		/** While set, a {@code MapValue} renders as a join reference — see {@link #mapJoin}. */
		private boolean joinMapAccess;

		/** The query's root type — what {@code ALIAS} statically is (issue #175). */
		private EClass rootEClass;

		private Translation(QueryContext context) {
			this.context = context;
		}

		/**
		 * Translates the stage pipeline: pre-group filters fold into WHERE, one GroupBy
		 * renders keys + aggregates, post-group computes render over the named columns
		 * (AliasRef), trailing filters become HAVING with the columns re-rendered —
		 * JPQL result variables are not addressable there. Without a GroupBy the
		 * computes are terminal: one row per entity, single-valued attributes first.
		 * A compute before the grouping (issue #87) only binds aliases for group keys
		 * and aggregate sources — its expressions are re-rendered inline on use, the
		 * aliases are no output columns.
		 */
		private TranslatedPipeline translatePipeline(Query query, List<String> rowKeys,
				List<String> rowAliases) throws QueryException {
			TranslatedPipeline result = new TranslatedPipeline();
			List<Stage> stages = query.getApply().getStages();
			boolean groupsSomewhere = stages.stream().anyMatch(GroupByStage.class::isInstance);
			GroupByStage group = null;
			List<Computation> computations = new ArrayList<>();
			List<Expression> havingPredicates = new ArrayList<>();
			for (Stage stage : stages) {
				if (stage instanceof GroupByStage groupBy) {
					if (group != null) {
						throw new QueryException("Multiple GroupBy stages are not supported");
					}
					group = groupBy;
				} else if (stage instanceof FilterStage filter) {
					if (group == null) {
						result.preFilters.add(render(filter.getPredicate()));
					} else {
						havingPredicates.add(filter.getPredicate());
					}
				} else if (stage instanceof ComputeStage compute) {
					if (group == null && groupsSomewhere) {
						// pre-group compute (issue #87): bind the alias for inline
						// re-rendering; expressions may reference earlier aliases
						for (Computation computation : compute.getComputations()) {
							columnExpressions.put(computation.getAlias(),
									operand(computation.getExpression(), null));
						}
					} else {
						computations.addAll(compute.getComputations());
					}
				} else if (stage instanceof SkipStage skip && group != null) {
					// row-space paging: sort-then-limit — compose the window sequentially
					result.skip += skip.getCount();
					if (result.top > 0) {
						result.top -= skip.getCount();
						if (result.top <= 0) {
							throw new QueryException("The pipeline skip exhausts the preceding"
									+ " top window — the composition can never yield a row");
						}
					}
				} else if (stage instanceof TopStage top && group != null) {
					result.top = result.top == 0 ? top.getCount() : Math.min(result.top, top.getCount());
				} else {
					throw new QueryException("Unsupported pipeline stage " + stage.eClass().getName()
							+ " before the grouping on the JPA processor — object-space paging is"
							+ " not expressible in one JPQL statement");
				}
			}
			StringBuilder columns = new StringBuilder();
			if (group != null) {
				// from the grouping on, every map access renders as a join (issue #190): the
				// subselect form would reference the grouped row from GROUP BY. It stays on
				// for HAVING and the trailing ORDER BY, which re-render the same expressions;
				// the pre-group filters were rendered above and keep the subselect form,
				// where a correlated reference is perfectly legal.
				joinMapAccess = true;
				List<String> groupByItems = new ArrayList<>();
				for (PropertyPath path : group.getPaths()) {
					String key = outputKey(null, path);
					// group keys are alias-addressable under their derived name
					registerKey(key, key, rowKeys, rowAliases);
					String rendered = rootPath(path);
					columnExpressions.put(key, rendered);
					appendColumn(columns, rendered + " AS " + key);
					groupByItems.add(rendered);
				}
				for (GroupKey key : group.getKeys()) {
					// expression-valued group key (issue #87): JPQL result variables
					// are not addressable in GROUP BY — re-render the expression there
					String alias = key.getAlias();
					String rendered = groupKeyExpression(key.getExpression());
					registerKey(alias, alias, rowKeys, rowAliases);
					columnExpressions.put(alias, rendered);
					appendColumn(columns, rendered + " AS " + alias);
					groupByItems.add(rendered);
				}
				for (Aggregate aggregate : group.getAggregates()) {
					String key = aggregate.getAlias();
					registerKey(key, key, rowKeys, rowAliases);
					String rendered = aggregateFunction(aggregate, aggregateArgument(aggregate));
					columnExpressions.put(key, rendered);
					appendColumn(columns, rendered + " AS " + key);
				}
				if (!groupByItems.isEmpty()) {
					result.groupBy = " GROUP BY " + String.join(", ", groupByItems);
				}
			} else {
				if (computations.isEmpty()) {
					throw new QueryException("The pipeline carries neither a GroupBy nor a Compute stage");
				}
				// terminal compute: one row per entity, single-valued attributes first
				for (EAttribute attribute : query.getFrom().getEAllAttributes()) {
					if (attribute.isMany()) {
						continue;
					}
					String key = attribute.getName();
					registerKey(key, key, rowKeys, rowAliases);
					String rendered = ALIAS + "." + key;
					columnExpressions.put(key, rendered);
					appendColumn(columns, rendered + " AS " + key);
				}
			}
			for (Computation computation : computations) {
				String key = computation.getAlias();
				String rendered = operand(computation.getExpression(), null);
				registerKey(key, key, rowKeys, rowAliases);
				columnExpressions.put(key, rendered);
				appendColumn(columns, rendered + " AS " + key);
			}
			result.columns = columns.toString();
			if (!havingPredicates.isEmpty()) {
				StringBuilder having = new StringBuilder(" HAVING ");
				for (int i = 0; i < havingPredicates.size(); i++) {
					if (i > 0) {
						having.append(" AND ");
					}
					having.append('(').append(render(havingPredicates.get(i))).append(')');
				}
				result.having = having.toString();
			}
			return result;
		}

		private static void appendColumn(StringBuilder columns, String column) {
			if (columns.length() > 0) {
				columns.append(", ");
			}
			columns.append(column);
		}

		/**
		 * The rendered argument of an aggregate function: an expression-valued source
		 * (issue #87, e.g. an AliasRef to a pre-group compute), a persisted path, or
		 * the bare group members for COUNT.
		 */
		private String aggregateArgument(Aggregate aggregate) throws QueryException {
			if (aggregate.getSource() != null) {
				return "(" + operand(aggregate.getSource(), null) + ")";
			}
			return aggregate.getPath() == null ? ALIAS : rootPath(aggregate.getPath());
		}

		private String render(Expression expression) throws QueryException {
			if (expression instanceof Junction junction) {
				StringBuilder rendered = new StringBuilder("(");
				String operator = junction instanceof And ? " AND " : " OR ";
				for (int i = 0; i < junction.getOperands().size(); i++) {
					if (i > 0) {
						rendered.append(operator);
					}
					rendered.append(render(junction.getOperands().get(i)));
				}
				return rendered.append(')').toString();
			}
			if (expression instanceof Not not) {
				return "NOT (" + render(not.getOperand()) + ")";
			}
			if (expression instanceof Comparison comparison) {
				String operator = switch (comparison.getOperator()) {
				case EQ -> " = ";
				case NE -> " <> ";
				case LT -> " < ";
				case LE -> " <= ";
				case GT -> " > ";
				case GE -> " >= ";
				};
				EStructuralFeature target = targetOf(comparison.getLeft(), comparison.getRight());
				return operand(comparison.getLeft(), target) + operator + operand(comparison.getRight(), target);
			}
			if (expression instanceof IsNull isNull) {
				return operand(isNull.getSource(), null) + (isNull.isNegated() ? " IS NOT NULL" : " IS NULL");
			}
			if (expression instanceof Between between) {
				EStructuralFeature target = targetOf(between.getSource(), null);
				String source = operand(between.getSource(), target);
				String lower = operand(between.getLower(), target);
				String upper = operand(between.getUpper(), target);
				return "(" + source + (between.isLowerIncluded() ? " >= " : " > ") + lower + " AND " + source
						+ (between.isUpperIncluded() ? " <= " : " < ") + upper + ")";
			}
			if (expression instanceof In in) {
				EStructuralFeature target = targetOf(in.getSource(), null);
				StringBuilder rendered = new StringBuilder(operand(in.getSource(), target)).append(" IN (");
				for (int i = 0; i < in.getValues().size(); i++) {
					if (i > 0) {
						rendered.append(", ");
					}
					rendered.append(operand(in.getValues().get(i), target));
				}
				return rendered.append(')').toString();
			}
			if (expression instanceof StringMatch match) {
				return renderMatch(match);
			}
			if (expression instanceof Quantifier quantifier) {
				return renderQuantifier(quantifier);
			}
			if (expression instanceof TypeCheck typeCheck) {
				return renderTypeCheck(typeCheck);
			}
			throw new QueryException("Unsupported predicate " + expression.eClass().getName());
		}

		/**
		 * Kind-of type test (issue #80): {@code TYPE(x) IN (concrete subtypes)} by
		 * entity name — the dynamic Java classes are deliberately flat, so Java
		 * assignability cannot identify subtypes; the EClass hierarchy can.
		 */
		private String renderTypeCheck(TypeCheck typeCheck) throws QueryException {
			String subject = typeCheck.getSource() == null ? ALIAS
					: operand(typeCheck.getSource(), null);
			List<String> concrete = new ArrayList<>();
			for (EClassifier classifier : typeCheck.getType().getEPackage().getEClassifiers()) {
				if (classifier instanceof EClass candidate && !candidate.isAbstract()
						&& typeCheck.getType().isSuperTypeOf(candidate)) {
					concrete.add(candidate.getName());
				}
			}
			if (concrete.isEmpty()) {
				// no concrete subtype can ever match (abstract-only hierarchy)
				return "1 = 0";
			}
			if (typeCheck.getSource() == null && rootEClass != null
					&& typeCheck.getType().isSuperTypeOf(rootEClass)) {
				// every row of this extent passes the test — asking the database for a TYPE
				// discriminator it may not have would fail rather than answer (issue #175)
				return "1 = 1";
			}
			return "TYPE(" + subject + ") IN (" + String.join(", ", concrete) + ")";
		}

		private String renderMatch(StringMatch match) throws QueryException {
			String source = operand(match.getSource(), null);
			String raw = text(match.getPattern());
			String pattern = switch (match.getKind()) {
			case CONTAINS -> "%" + escapeLike(raw) + "%";
			case STARTS_WITH -> escapeLike(raw) + "%";
			case ENDS_WITH -> "%" + escapeLike(raw);
			case LIKE -> raw;
			// unreachable: STRING_MATCH_FUZZY is undeclared, validation refused already (issue #167)
			case FUZZY -> throw new QueryException("FUZZY matching is not served by the jpa backend");
			};
			String parameter = bind(pattern);
			if (match.isCaseInsensitive()) {
				return "LOWER(" + source + ") LIKE LOWER(" + parameter + ") ESCAPE '\\'";
			}
			return source + " LIKE " + parameter + " ESCAPE '\\'";
		}

		private String renderQuantifier(Quantifier quantifier) throws QueryException {
			String alias = "it" + aliasCounter++;
			aliases.put(quantifier.getVariable(), alias);
			String collection = pathFrom(ALIAS, quantifier.getSource());
			String predicate = render(quantifier.getPredicate());
			aliases.remove(quantifier.getVariable());
			if (quantifier instanceof Exists) {
				return "EXISTS (SELECT " + alias + " FROM " + collection + " " + alias + " WHERE " + predicate + ")";
			}
			return "NOT EXISTS (SELECT " + alias + " FROM " + collection + " " + alias + " WHERE NOT (" + predicate
					+ "))";
		}

		/** Renders a comparison operand: navigations/functions as expressions, values as parameters. */
		private String operand(Expression expression, EStructuralFeature target) throws QueryException {
			if (expression instanceof PropertyPath path) {
				String base = path.getBase() == null ? ALIAS : alias(path.getBase());
				return pathFrom(base, path);
			}
			if (expression instanceof StringFunction function) {
				String inner = operand(function.getSource(), target);
				return switch (function.getKind()) {
				case TO_LOWER -> "LOWER(" + inner + ")";
				case TO_UPPER -> "UPPER(" + inner + ")";
				case TRIM -> "TRIM(" + inner + ")";
				case LENGTH -> "LENGTH(" + inner + ")";
				};
			}
			if (expression instanceof Arithmetic arithmetic) {
				String left = operand(arithmetic.getLeft(), target);
				String right = operand(arithmetic.getRight(), target);
				return switch (arithmetic.getOperator()) {
				case ADD -> "(" + left + " + " + right + ")";
				case SUB -> "(" + left + " - " + right + ")";
				case MUL -> "(" + left + " * " + right + ")";
				// DIV is floating-point by contract — * 1.0 defeats SQL integer division
				case DIV -> "(" + left + " * 1.0 / " + right + ")";
				case MOD -> "MOD(" + left + ", " + right + ")";
				};
			}
			if (expression instanceof Negate negate) {
				return "(-" + operand(negate.getOperand(), target) + ")";
			}
			if (expression instanceof AliasRef aliasRef) {
				String column = columnExpressions.get(aliasRef.getAlias());
				if (column == null) {
					throw new QueryException("Alias '" + aliasRef.getAlias()
							+ "' does not address a pipeline output column (available: "
							+ columnExpressions.keySet() + ")");
				}
				return "(" + column + ")";
			}
			if (expression instanceof CollectionCount count) {
				// plain: JPQL SIZE; filtered: a correlated COUNT subquery — the JPQL
				// text path is free of the criteria-API SubQueryImpl comparison gotcha
				if (count.getPredicate() == null) {
					return "SIZE(" + rootPath(count.getSource()) + ")";
				}
				String alias = "it" + aliasCounter++;
				aliases.put(count.getVariable(), alias);
				String collection = pathFrom(ALIAS, count.getSource());
				String predicate = render(count.getPredicate());
				aliases.remove(count.getVariable());
				return "(SELECT COUNT(" + alias + ") FROM " + collection + " " + alias
						+ " WHERE " + predicate + ")";
			}
			if (expression instanceof MapValue mapValue) {
				// a map is an entry table here (contract §9.2), so one entry is a correlated
				// subselect keyed on the entry's key column — the same shape the filtered
				// CollectionCount above uses, projecting value instead of counting. In a
				// grouped query that subselect is illegal, and a join takes its place (#190)
				return joinMapAccess ? mapJoin(mapValue) : mapValueSubselect(mapValue);
			}
			if (expression instanceof NumericFunction numericFunction) {
				String inner = operand(numericFunction.getSource(), target);
				return switch (numericFunction.getKind()) {
				// H2 ROUND is half away from zero — the contract (OData semantics)
				case ROUND -> "ROUND(" + inner + ", 0)";
				case FLOOR -> "FLOOR(" + inner + ")";
				case CEILING -> "CEILING(" + inner + ")";
				};
			}
			if (expression instanceof TemporalFunction temporalFunction) {
				String inner = operand(temporalFunction.getSource(), target);
				return switch (temporalFunction.getKind()) {
				case YEAR -> "EXTRACT(YEAR FROM " + inner + ")";
				case MONTH -> "EXTRACT(MONTH FROM " + inner + ")";
				case DAY -> "EXTRACT(DAY FROM " + inner + ")";
				case HOUR -> "EXTRACT(HOUR FROM " + inner + ")";
				case MINUTE -> "EXTRACT(MINUTE FROM " + inner + ")";
				// EXTRACT(SECOND) is fractional (EclipseLink types it Double) — the
				// contract is the integral second
				case SECOND -> "FLOOR(EXTRACT(SECOND FROM " + inner + "))";
				};
			}
			if (expression instanceof Concat concatenation) {
				StringBuilder rendered = new StringBuilder("CONCAT(");
				for (int i = 0; i < concatenation.getParts().size(); i++) {
					if (i > 0) {
						rendered.append(", ");
					}
					rendered.append(operand(concatenation.getParts().get(i), target));
				}
				return rendered.append(')').toString();
			}
			if (expression instanceof IndexOf indexOf) {
				// the IR is 0-based with -1 for absent, JPQL LOCATE 1-based with 0
				return "(LOCATE(" + operand(indexOf.getSearch(), target) + ", "
						+ operand(indexOf.getSource(), target) + ") - 1)";
			}
			if (expression instanceof Substring substring) {
				String source = operand(substring.getSource(), target);
				String start = integerOperand(substring.getStart(), target);
				// [OData-URL] 5.1.1.7: 0-based; a negative start counts from the end of
				// the string, clamped to position 1. One flat CASE (first match wins) —
				// EclipseLink mistranslates a CASE nested inside ELSE
				String position = "CASE"
						+ " WHEN " + start + " >= 0 THEN (" + start + " + 1)"
						+ " WHEN (LENGTH(" + source + ") + " + start + " + 1) > 0"
						+ " THEN (LENGTH(" + source + ") + " + start + " + 1)"
						+ " ELSE 1 END";
				if (substring.getLength() != null) {
					return "SUBSTRING(" + source + ", " + position + ", "
							+ integerOperand(substring.getLength(), target) + ")";
				}
				return "SUBSTRING(" + source + ", " + position + ")";
			}
			return bind(ExpressionValues.resolve(expression, target, context.parameters(), context.converter()));
		}

		private String alias(Variable variable) throws QueryException {
			String alias = aliases.get(variable);
			if (alias == null) {
				throw new QueryException("Variable '" + variable.getName() + "' is not in scope");
			}
			return alias;
		}

		/**
		 * {@code (SELECT e.value FROM <owner>.<map> e WHERE e.key = :p)} — the entry-table
		 * rendering of a map access (issue #186). The key is bound as a parameter like every
		 * other value; it is constant by contract, which the analyzer has already enforced.
		 */
		private String mapValueSubselect(MapValue mapValue) throws QueryException {
			PropertyPath map = mapValue.getMap();
			EClass entryClass = EMaps.entryClass(ExpressionValues.targetFeature(map));
			if (entryClass == null) {
				throw new QueryException("MapValue does not address a map: "
						+ pathFrom(ALIAS, map));
			}
			EStructuralFeature keyFeature = EMaps.keyFeature(entryClass);
			EStructuralFeature valueFeature = EMaps.valueFeature(entryClass);
			Object key = ExpressionValues.resolve(mapValue.getKey(), keyFeature, context.parameters(),
					context.converter());
			String base = map.getBase() == null ? ALIAS : alias(map.getBase());
			String alias = "me" + aliasCounter++;
			return "(SELECT " + alias + "." + valueFeature.getName() + " FROM " + pathFrom(base, map)
					+ " " + alias + " WHERE " + alias + "." + keyFeature.getName() + " = " + bind(key) + ")";
		}

		/**
		 * {@code mj0.value} against a {@code LEFT JOIN <owner>.<map> mj0 ON mj0.key = :p} —
		 * the grouped rendering of a map access (issue #190).
		 * <p>
		 * The correlated subselect this replaces references the grouped row, which no database
		 * accepts in {@code GROUP BY}. A join lifts the entry into the FROM clause, where the
		 * value is an ordinary column that SELECT and GROUP BY can both address. The join is
		 * <b>outer</b> and carries the key in its {@code ON} clause rather than in {@code WHERE}:
		 * an owner without that key then groups under {@code null} instead of dropping out of the
		 * result, which is what the mongo backend does for a missing sub-document field.
		 * <p>
		 * One join per distinct map-and-key pair — a query grouping and aggregating over the same
		 * entry joins once.
		 */
		private String mapJoin(MapValue mapValue) throws QueryException {
			PropertyPath map = mapValue.getMap();
			EClass entryClass = EMaps.entryClass(ExpressionValues.targetFeature(map));
			if (entryClass == null) {
				throw new QueryException("MapValue does not address a map: " + pathFrom(ALIAS, map));
			}
			EStructuralFeature keyFeature = EMaps.keyFeature(entryClass);
			EStructuralFeature valueFeature = EMaps.valueFeature(entryClass);
			Object key = ExpressionValues.resolve(mapValue.getKey(), keyFeature, context.parameters(),
					context.converter());
			String base = map.getBase() == null ? ALIAS : alias(map.getBase());
			String entryPath = pathFrom(base, map);
			String joinKey = entryPath + "|" + key;
			String alias = mapJoinAliases.get(joinKey);
			if (alias == null) {
				alias = "mj" + aliasCounter++;
				mapJoinAliases.put(joinKey, alias);
				mapJoins.add(" LEFT JOIN " + entryPath + " " + alias + " ON " + alias + "."
						+ keyFeature.getName() + " = " + bind(key));
			}
			return alias + "." + valueFeature.getName();
		}

		private EStructuralFeature targetOf(Expression left, Expression right) {
			if (left instanceof MapValue || right instanceof MapValue) {
				// a map access types its peer literal against the map's value feature
				return ExpressionValues.targetFeature(left instanceof MapValue ? left : right);
			}
			if (left instanceof PropertyPath path) {
				return ExpressionValues.targetFeature(path);
			}
			if (right instanceof PropertyPath path) {
				return ExpressionValues.targetFeature(path);
			}
			if (left instanceof StringFunction function && function.getSource() instanceof PropertyPath path) {
				return ExpressionValues.targetFeature(path);
			}
			PropertyPath numericPath = firstPath(left);
			if (numericPath == null) {
				numericPath = firstPath(right);
			}
			return numericPath == null ? null : ExpressionValues.targetFeature(numericPath);
		}

		/** The first navigated path inside an arithmetic tree — types its literal peers. */
		private PropertyPath firstPath(Expression expression) {
			if (expression instanceof Arithmetic arithmetic) {
				PropertyPath left = firstPath(arithmetic.getLeft());
				return left != null ? left : firstPath(arithmetic.getRight());
			}
			if (expression instanceof Negate negate) {
				return firstPath(negate.getOperand());
			}
			return expression instanceof PropertyPath path ? path : null;
		}

		private String text(Expression pattern) throws QueryException {
			Object value = pattern instanceof Literal || pattern instanceof ParameterRef
					? ExpressionValues.resolve(pattern, null, context.parameters(), null)
					: null;
			if (value == null && !(pattern instanceof Literal)) {
				throw new QueryException("String match patterns must be literals or bound parameters");
			}
			return value == null ? "" : String.valueOf(value);
		}

		private String bind(Object value) {
			String name = "p" + parameters.size();
			parameters.put(name, value);
			return ":" + name;
		}

		/**
		 * Renders an expression-valued group key with its constants inlined rather than bound
		 * (issue #156).
		 * <p>
		 * The same rendering appears twice — once in the select list, once in {@code GROUP BY},
		 * because JPQL result variables are not addressable there. With bind parameters that
		 * duplication is fatal: EclipseLink expands one named parameter into a separate {@code ?}
		 * per occurrence, and PostgreSQL then sees two different expressions, so the column inside
		 * them counts as ungrouped ({@code must appear in the GROUP BY clause}). H2 accepts it,
		 * which is why the flavor axis of #134 was needed to find this.
		 * <p>
		 * Inlining makes both occurrences textually identical. It costs statement-cache reuse for
		 * group keys only — everything else keeps its parameters.
		 *
		 * @param expression the group key expression
		 * @return the rendering to use in both the select list and the GROUP BY clause
		 * @throws QueryException if the expression carries a constant that cannot be inlined
		 */
		private String groupKeyExpression(Expression expression) throws QueryException {
			requiresInlineLiterals = true;
			return operand(expression, null);
		}


		/**
		 * Renders an operand that a SQL string function expects as an {@code int} (issue #155).
		 * <p>
		 * The IR carries substring offsets as long-typed literals, so binding them unchanged sends
		 * {@code bigint} — and PostgreSQL declares only {@code substr(text, int, int)}, refusing to
		 * narrow implicitly during function resolution. H2 narrows, which is why this only surfaced
		 * with the flavor axis of #134. Narrowing here rather than casting in SQL keeps the
		 * generated statement readable and costs nothing at execution time.
		 *
		 * @param expression the offset or length expression
		 * @param target the feature the surrounding comparison targets, for value conversion
		 * @return the rendered operand, with any bound integral value narrowed to {@code Integer}
		 * @throws QueryException if the expression cannot be rendered
		 */
		private String integerOperand(Expression expression, EStructuralFeature target)
				throws QueryException {
			if (expression instanceof PropertyPath || expression instanceof StringFunction) {
				// a column or a function over one — nothing is bound, so nothing to narrow
				return operand(expression, target);
			}
			Object value = ExpressionValues.resolve(expression, target, context.parameters(),
					context.converter());
			if (value instanceof Long || value instanceof Short || value instanceof Byte) {
				return bind(((Number) value).intValue());
			}
			return bind(value);
		}
	}

	/** Escapes LIKE wildcards so CONTAINS/STARTS_WITH/ENDS_WITH match verbatim. */
	static String escapeLike(String value) {
		return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	}
}
