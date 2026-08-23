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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.expression.AliasRef;
import org.eclipse.fennec.model.expression.Arithmetic;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.CollectionCount;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.Concat;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.GeoDistance;
import org.eclipse.fennec.model.expression.GeoSubject;
import org.eclipse.fennec.model.expression.GeoWithin;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IndexOf;
import org.eclipse.fennec.model.expression.IntervalMatch;
import org.eclipse.fennec.model.expression.IntervalSubject;
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
import org.eclipse.fennec.model.query.RepresentativeSpec;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.SkipStage;
import org.eclipse.fennec.model.query.Stage;
import org.eclipse.fennec.model.query.TopStage;
import org.eclipse.fennec.persistence.capabilities.QueryCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryCapabilitiesBuilder;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.query.QueryConstants;
import org.eclipse.fennec.persistence.helper.EMaps;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.eclipse.fennec.persistence.query.api.QueryPlan;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.expr.ExpressionAnalyzer;
import org.eclipse.fennec.persistence.query.expr.ExpressionValues;
import org.eclipse.fennec.persistence.query.support.QueryValidator;
import org.osgi.service.component.annotations.Component;

/**
 * {@link QueryProcessor} for the {@code memory} backend: evaluates the canonical query
 * <b>in memory</b> against caller-provided EObjects — the plan carries no store, see
 * {@link MemoryQueryPlan#execute(java.util.Collection)}.
 * <p>
 * Two roles (issue #62): the <em>reference oracle</em> — its capability set is
 * near-complete (everything except the reserved temporal features), so every query the
 * database backends serve can be cross-checked against in-memory semantics — and the
 * third execution option for IR consumers such as the OData layer, which gets
 * {@code jpa}, {@code mongo} or {@code memory} through one SPI.
 * <p>
 * All structural validation and literal/parameter resolution happens at translation
 * time; execution never throws. Values stay in EMF space (no {@code ConverterService}
 * involved) because the evaluation runs against EMF objects.
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
@Component(service = QueryProcessor.class, property = QueryConstants.BACKEND_PROPERTY + "=" + MemoryQueryProcessor.BACKEND)
public class MemoryQueryProcessor implements QueryProcessor {

	/** The backend id of this processor. */
	public static final String BACKEND = "memory";

	private static final QueryCapabilities CAPABILITIES = QueryCapabilitiesBuilder.create()
			// SCORE stays undeclared by design (issue #100): a relevance score without a
			// text-scoring model has no reference semantics — the first feature the
			// reference engine deliberately does not implement
			.support(EnumSet.complementOf(
					EnumSet.of(QueryFeature.AS_OF, QueryFeature.SCORE))
					.toArray(QueryFeature[]::new))
			.maxFeaturePathDepth(-1)
			.build();

	@Override
	public String backend() {
		return BACKEND;
	}

	@Override
	public QueryCapabilities capabilities() {
		return CAPABILITIES;
	}

	@Override
	public Diagnostic validate(Query query, EClass rootEClass) {
		return QueryValidator.validate(ExpressionAnalyzer.analyze(query), rootEClass, CAPABILITIES);
	}

	@Override
	public QueryPlan translate(Query query, QueryContext context) throws QueryException {
		QueryShape shape = ExpressionAnalyzer.analyze(query).shape();
		if (query.getFrom() == null) {
			throw new QueryException("The query carries no root type (from) — cannot evaluate in memory");
		}
		if (query.getApply() != null && !query.getSelect().isEmpty()) {
			throw new QueryException("select and apply are mutually exclusive — aggregation defines its own columns");
		}
		Resolution resolution = new Resolution(context);
		resolution.walk(query.getPredicate(), Set.of());

		List<String> rowKeys = new ArrayList<>();
		List<String> rowAliases = new ArrayList<>();
		if (shape == QueryShape.PROJECTION) {
			for (Selection selection : query.getSelect()) {
				if (selection.getKey() != null) {
					// expression projection (issue #189): a value expression in object
					// space, resolved like a sort key — the alias is the column name
					resolution.operand(selection.getKey(), null, Set.of());
					registerKey(selection.getAlias(), selection.getAlias(), rowKeys, rowAliases);
					continue;
				}
				resolution.rootPath(selection.getPath());
				registerKey(outputKey(selection.getAlias(), selection.getPath()), selection.getAlias(),
						rowKeys, rowAliases);
			}
		}
		if (query.getApply() != null) {
			resolution.walkPipeline(query, rowKeys, rowAliases);
		}
		for (PropertyPath expand : query.getExpand()) {
			resolution.rootPath(expand); // objects are in memory — expand is a no-op
		}
		for (OrderBy orderBy : query.getOrderBy()) {
			if (orderBy.getKey() != null) {
				// arbitrary sort expressions (issue #84): object space uses the operand
				// vocabulary, row space the alias-addressing one
				if (shape == QueryShape.OBJECTS) {
					resolution.operand(orderBy.getKey(), null, Set.of());
				} else if (shape != QueryShape.COUNT) {
					resolution.rowExpression(orderBy.getKey());
				}
			} else if (shape == QueryShape.OBJECTS) {
				resolution.rootPath(orderBy.getPath());
			} else if (shape != QueryShape.COUNT) {
				rowKey(orderBy.getPath(), rowKeys);
			}
		}
		return new MemoryQueryPlan(query, shape,
				new MemoryPredicate(query.getPredicate(), resolution.values), rowKeys, rowAliases);
	}

	// ------------------------------------------------------- translation walk

	/** Validates the trees and resolves every literal/parameter operand once. */
	private static final class Resolution {

		private final QueryContext context;
		private final Map<Expression, Object> values = new IdentityHashMap<>();
		/** Aliases bound by pre-group computes — the scope of object-space AliasRefs (issue #87). */
		private final Set<String> computeAliases = new HashSet<>();

		private Resolution(QueryContext context) {
			this.context = context;
		}

		private void walk(Expression expression, Set<Variable> scope) throws QueryException {
			if (expression == null) {
				return;
			}
			if (expression instanceof Junction junction) {
				for (Expression operand : junction.getOperands()) {
					walk(operand, scope);
				}
				return;
			}
			if (expression instanceof Not not) {
				walk(not.getOperand(), scope);
				return;
			}
			if (expression instanceof Comparison comparison) {
				EStructuralFeature target = targetOf(comparison.getLeft(), comparison.getRight());
				operand(comparison.getLeft(), target, scope);
				operand(comparison.getRight(), target, scope);
				return;
			}
			if (expression instanceof IsNull isNull) {
				operand(isNull.getSource(), null, scope);
				return;
			}
			if (expression instanceof Between between) {
				EStructuralFeature target = targetOf(between.getSource(), null);
				operand(between.getSource(), target, scope);
				operand(between.getLower(), target, scope);
				operand(between.getUpper(), target, scope);
				return;
			}
			if (expression instanceof In in) {
				EStructuralFeature target = targetOf(in.getSource(), null);
				operand(in.getSource(), target, scope);
				for (Expression option : in.getValues()) {
					operand(option, target, scope);
				}
				return;
			}
			if (expression instanceof StringMatch match) {
				operand(match.getSource(), null, scope);
				Expression pattern = match.getPattern();
				if (!(pattern instanceof Literal) && !(pattern instanceof ParameterRef)) {
					throw new QueryException("String match patterns must be literals or bound parameters");
				}
				values.put(pattern, ExpressionValues.resolve(pattern, null, context.parameters(), null));
				return;
			}
			if (expression instanceof Quantifier quantifier) {
				collectionPath(quantifier.getSource(), scope);
				Set<Variable> inner = new HashSet<>(scope);
				inner.add(quantifier.getVariable());
				walk(quantifier.getPredicate(), inner);
				return;
			}
			if (expression instanceof TypeCheck typeCheck) {
				if (typeCheck.getSource() != null) {
					path(typeCheck.getSource(), scope);
				}
				return;
			}
			if (expression instanceof GeoWithin geoWithin) {
				// shape points are model literals read directly at evaluation (issue #101)
				geoSubject(geoWithin.getSubject(), scope);
				return;
			}
			if (expression instanceof IntervalMatch interval) {
				intervalSubject(interval.getSubject(), scope);
				// the query bounds convert against the subject's lower bound feature — both
				// bound paths share one domain, which the analyzer has already checked
				EStructuralFeature target = interval.getSubject() == null ? null
						: targetOf(interval.getSubject().getPathLower(), null);
				operand(interval.getLower(), target, scope);
				operand(interval.getUpper(), target, scope);
				return;
			}
			throw new QueryException("Unsupported predicate " + expression.eClass().getName());
		}

		private void geoSubject(GeoSubject subject, Set<Variable> scope) throws QueryException {
			if (subject == null) {
				return;
			}
			if (subject.getPathLat() != null) {
				path(subject.getPathLat(), scope);
			}
			if (subject.getPathLon() != null) {
				path(subject.getPathLon(), scope);
			}
			if (subject.getPathPoint() != null) {
				path(subject.getPathPoint(), scope);
			}
		}

		private void intervalSubject(IntervalSubject subject, Set<Variable> scope) throws QueryException {
			if (subject == null) {
				return;
			}
			if (subject.getPathLower() != null) {
				path(subject.getPathLower(), scope);
			}
			if (subject.getPathUpper() != null) {
				path(subject.getPathUpper(), scope);
			}
		}

		private void operand(Expression expression, EStructuralFeature target, Set<Variable> scope)
				throws QueryException {
			if (expression instanceof AliasRef aliasRef) {
				// object-space AliasRefs address pre-group compute aliases (issue #87)
				if (!computeAliases.contains(aliasRef.getAlias())) {
					throw new QueryException("Alias '" + aliasRef.getAlias()
							+ "' does not address a pre-group compute alias (available: "
							+ computeAliases + ")");
				}
				return;
			}
			if (expression instanceof PropertyPath path) {
				path(path, scope);
				return;
			}
			if (expression instanceof StringFunction function) {
				operand(function.getSource(), target, scope);
				return;
			}
			if (expression instanceof CollectionCount count) {
				collectionPath(count.getSource(), scope);
				if (count.getPredicate() != null) {
					Set<Variable> inner = new HashSet<>(scope);
					inner.add(count.getVariable());
					walk(count.getPredicate(), inner);
				}
				return;
			}
			if (expression instanceof MapValue mapValue) {
				// the map navigation resolves like any path; the key is a constant that has to
				// be typed against the entry's key feature, not against the comparison target
				path(mapValue.getMap(), scope);
				values.put(mapValue.getKey(), ExpressionValues.resolve(mapValue.getKey(),
						EMaps.keyFeature(EMaps.entryClass(ExpressionValues.targetFeature(mapValue.getMap()))),
						context.parameters(), null));
				return;
			}
			if (expression instanceof Arithmetic arithmetic) {
				operand(arithmetic.getLeft(), target, scope);
				operand(arithmetic.getRight(), target, scope);
				return;
			}
			if (expression instanceof Negate negate) {
				operand(negate.getOperand(), target, scope);
				return;
			}
			if (expression instanceof GeoDistance geoDistance) {
				// a value expression (issue #101, G3) — only the subject paths resolve
				geoSubject(geoDistance.getSubject(), scope);
				return;
			}
			if (expression instanceof NumericFunction numericFunction) {
				operand(numericFunction.getSource(), target, scope);
				return;
			}
			if (expression instanceof TemporalFunction temporalFunction) {
				operand(temporalFunction.getSource(), target, scope);
				return;
			}
			if (expression instanceof Concat concatenation) {
				for (Expression part : concatenation.getParts()) {
					operand(part, target, scope);
				}
				return;
			}
			if (expression instanceof IndexOf indexOf) {
				operand(indexOf.getSource(), target, scope);
				operand(indexOf.getSearch(), target, scope);
				return;
			}
			if (expression instanceof Substring substring) {
				operand(substring.getSource(), target, scope);
				operand(substring.getStart(), target, scope);
				if (substring.getLength() != null) {
					operand(substring.getLength(), target, scope);
				}
				return;
			}
			values.put(expression, ExpressionValues.resolve(expression, target, context.parameters(), null));
		}

		private void walkPipeline(Query query, List<String> rowKeys, List<String> rowAliases)
				throws QueryException {
			List<Stage> stages = query.getApply().getStages();
			boolean groupsSomewhere = stages.stream().anyMatch(GroupByStage.class::isInstance);
			boolean rowSpace = false;
			for (Stage stage : stages) {
				if (stage instanceof GroupByStage groupBy) {
					if (rowSpace) {
						throw new QueryException("Multiple GroupBy stages are not supported");
					}
					rowSpace = true;
					for (PropertyPath path : groupBy.getPaths()) {
						rootPath(path);
						String key = outputKey(null, path);
						registerKey(key, key, rowKeys, rowAliases);
					}
					for (GroupKey key : groupBy.getKeys()) {
						// expression-valued group keys (issue #87): object-space
						// vocabulary plus pre-group compute aliases
						operand(key.getExpression(), null, Set.of());
						registerKey(key.getAlias(), key.getAlias(), rowKeys, rowAliases);
					}
					for (Aggregate aggregate : groupBy.getAggregates()) {
						if (aggregate.getPath() != null) {
							rootPath(aggregate.getPath());
						}
						if (aggregate.getSource() != null) {
							// expression-valued aggregate sources (issue #87)
							operand(aggregate.getSource(), null, Set.of());
						}
						registerKey(aggregate.getAlias(), aggregate.getAlias(), rowKeys, rowAliases);
					}
					RepresentativeSpec representatives = groupBy.getRepresentatives();
					if (representatives != null) {
						// one more column, holding the group's own documents (issue #214).
						// The window bounds are operands like any other, so they have to be
						// resolved here — that is what puts their value in the plan's table
						operand(representatives.getCount(), null, Set.of());
						if (representatives.getOffset() != null) {
							operand(representatives.getOffset(), null, Set.of());
						}
						for (OrderBy within : representatives.getOrderBy()) {
							if (within.getPath() != null) {
								rootPath(within.getPath());
							} else if (within.getKey() != null) {
								operand(within.getKey(), null, Set.of());
							}
						}
						registerKey(representatives.getAlias(), representatives.getAlias(),
								rowKeys, rowAliases);
					}
				} else if (stage instanceof FilterStage filter) {
					if (rowSpace) {
						// HAVING / post-compute filter — row-space vocabulary (issue #82)
						rowExpression(filter.getPredicate());
					} else {
						walk(filter.getPredicate(), Set.of());
					}
				} else if (stage instanceof ComputeStage compute) {
					if (!rowSpace && groupsSomewhere) {
						// pre-group compute (issue #87): object-space vocabulary; the
						// aliases feed group keys/aggregate sources, not result columns
						for (Computation computation : compute.getComputations()) {
							operand(computation.getExpression(), null, Set.of());
							computeAliases.add(computation.getAlias());
						}
					} else {
						if (!rowSpace) {
							// terminal computes: one row per entity, attributes first
							for (EAttribute attribute : query.getFrom().getEAllAttributes()) {
								if (!attribute.isMany()) {
									registerKey(attribute.getName(), attribute.getName(), rowKeys, rowAliases);
								}
							}
							rowSpace = true;
						}
						for (Computation computation : compute.getComputations()) {
							rowExpression(computation.getExpression());
							registerKey(computation.getAlias(), computation.getAlias(), rowKeys, rowAliases);
						}
					}
				} else if (!(stage instanceof TopStage) && !(stage instanceof SkipStage)) {
					throw new QueryException("Unsupported pipeline stage " + stage.eClass().getName());
				}
			}
		}

		/**
		 * Validates a row-space expression (HAVING / compute, issue #82) and resolves its
		 * literals: alias references and paths address output columns, arithmetic and
		 * numeric functions compute over them — everything else is refused.
		 */
		private void rowExpression(Expression expression) throws QueryException {
			if (expression instanceof Junction junction) {
				for (Expression operand : junction.getOperands()) {
					rowExpression(operand);
				}
			} else if (expression instanceof Not not) {
				rowExpression(not.getOperand());
			} else if (expression instanceof Comparison comparison) {
				rowExpression(comparison.getLeft());
				rowExpression(comparison.getRight());
			} else if (expression instanceof IsNull isNull) {
				rowExpression(isNull.getSource());
			} else if (expression instanceof Between between) {
				rowExpression(between.getSource());
				rowExpression(between.getLower());
				rowExpression(between.getUpper());
			} else if (expression instanceof In in) {
				rowExpression(in.getSource());
				for (Expression option : in.getValues()) {
					rowExpression(option);
				}
			} else if (expression instanceof Arithmetic arithmetic) {
				rowExpression(arithmetic.getLeft());
				rowExpression(arithmetic.getRight());
			} else if (expression instanceof Negate negate) {
				rowExpression(negate.getOperand());
			} else if (expression instanceof NumericFunction numericFunction) {
				rowExpression(numericFunction.getSource());
			} else if (expression instanceof AliasRef || expression instanceof PropertyPath) {
				// resolved against the row keys at execution
			} else if (expression instanceof Literal || expression instanceof ParameterRef) {
				values.put(expression, ExpressionValues.resolve(expression, null, context.parameters(), null));
			} else {
				throw new QueryException("Unsupported row-space expression " + expression.eClass().getName()
						+ " — HAVING/compute expressions address pipeline output columns");
			}
		}

		private void rootPath(PropertyPath path) throws QueryException {
			if (path.getBase() != null) {
				throw new QueryException("Variable-based paths are only valid inside quantifier predicates");
			}
			path(path, Set.of());
		}

		private void path(PropertyPath path, Set<Variable> scope) throws QueryException {
			checkScope(path, scope);
			List<EStructuralFeature> segments = path.getSegments();
			for (int i = 0; i < segments.size() - 1; i++) {
				if (segments.get(i).isMany()) {
					throw new QueryException("Path navigates through the many-valued feature '"
							+ segments.get(i).getName() + "' — address collections with exists/forAll");
				}
			}
		}

		private void collectionPath(PropertyPath path, Set<Variable> scope) throws QueryException {
			checkScope(path, scope);
			List<EStructuralFeature> segments = path.getSegments();
			if (segments.isEmpty() || !segments.get(segments.size() - 1).isMany()) {
				throw new QueryException("Quantifier sources must end in a many-valued feature");
			}
			for (int i = 0; i < segments.size() - 1; i++) {
				if (segments.get(i).isMany()) {
					throw new QueryException("Quantifier source navigates through the many-valued feature '"
							+ segments.get(i).getName() + "' — nest quantifiers instead");
				}
			}
		}

		private void checkScope(PropertyPath path, Set<Variable> scope) throws QueryException {
			if (path.getBase() != null && !scope.contains(path.getBase())) {
				throw new QueryException("Variable '" + path.getBase().getName() + "' is not in scope");
			}
		}

		private EStructuralFeature targetOf(Expression left, Expression right) {
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
	}

	// -------------------------------------------------------------- row keys

	static String rowKey(PropertyPath path, List<String> rowKeys) throws QueryException {
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
}
