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
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.PropertyPath;
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
import org.eclipse.fennec.model.query.SortDirection;
import org.eclipse.fennec.model.query.Stage;
import org.eclipse.fennec.model.query.TopStage;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryPlan;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.support.QueryResultRows;
import org.eclipse.fennec.persistence.query.support.QueryResults;

/**
 * The executable plan of the {@code memory} backend: unlike the database plans it has no
 * store — {@link #execute(Collection)} evaluates the query against a caller-provided set
 * of candidate objects (typically {@code Resource.getContents()} or any in-memory EMF
 * collection). Every structural question was settled at translation time; execution is a
 * pure stream over the candidates.
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
public final class MemoryQueryPlan implements QueryPlan {

	private final Query source;
	private final QueryShape shape;
	private final MemoryPredicate predicate;
	private final List<String> rowKeys;
	private final List<String> rowAliases;

	MemoryQueryPlan(Query source, QueryShape shape, MemoryPredicate predicate,
			List<String> rowKeys, List<String> rowAliases) {
		this.source = source;
		this.shape = shape;
		this.predicate = predicate;
		this.rowKeys = List.copyOf(rowKeys);
		this.rowAliases = List.copyOf(rowAliases);
	}

	@Override
	public Query source() {
		return source;
	}

	@Override
	public QueryShape shape() {
		return shape;
	}

	/** The result keys of the row shapes, in column order (empty for OBJECTS/COUNT). */
	public List<String> rowKeys() {
		return rowKeys;
	}

	/** The addressable aliases matching {@link #rowKeys()} positionally. */
	public List<String> rowAliases() {
		return rowAliases;
	}

	/**
	 * Evaluates the plan against the candidates. Objects that are not instances of the
	 * query's root type are ignored.
	 *
	 * @param candidates the in-memory objects to query, must not be {@code null}
	 * @return the result in the plan's shape
	 */
	public QueryResult execute(Collection<? extends EObject> candidates) {
		Objects.requireNonNull(candidates, "candidates must not be null");
		return execute(candidates.stream());
	}

	/**
	 * Stream variant of {@link #execute(Collection)}; the stream is consumed entirely.
	 */
	public QueryResult execute(Stream<? extends EObject> candidates) {
		Objects.requireNonNull(candidates, "candidates must not be null");
		Stream<EObject> matching = candidates
				.filter(Objects::nonNull)
				.map(EObject.class::cast)
				.filter(candidate -> source.getFrom().isSuperTypeOf(candidate.eClass()))
				.filter(predicate::test);
		if (shape == QueryShape.COUNT) {
			return QueryResults.count(matching.count());
		}
		if (source.getApply() != null) {
			return executePipeline(matching);
		}
		if (shape == QueryShape.PROJECTION) {
			return rows(project(sortObjects(matching)));
		}
		return QueryResults.objects(page(sortObjects(matching)
				.filter(distinctObjects())));
	}

	// ------------------------------------------------------------- object space

	private Stream<EObject> sortObjects(Stream<EObject> objects) {
		if (source.getOrderBy().isEmpty() || shape != QueryShape.OBJECTS) {
			return objects;
		}
		Comparator<EObject> comparator = null;
		for (OrderBy orderBy : source.getOrderBy()) {
			Comparator<EObject> next = orderBy.getKey() != null
					// arbitrary sort expressions evaluate per object (issue #84)
					? Comparator.comparing(object -> predicate.value(orderBy.getKey(), object),
							MemoryPredicate.VALUE_ORDER)
					: Comparator.comparing(object -> predicate.pathValue(orderBy.getPath(), object),
							MemoryPredicate.VALUE_ORDER);
			if (orderBy.getDirection() == SortDirection.DESC) {
				next = next.reversed();
			}
			comparator = comparator == null ? next : comparator.thenComparing(next);
		}
		return objects.sorted(comparator);
	}

	private java.util.function.Predicate<EObject> distinctObjects() {
		if (!source.isDistinct()) {
			return object -> true;
		}
		java.util.Set<EObject> seen = new LinkedHashSet<>();
		return seen::add;
	}

	private <T> Stream<T> page(Stream<T> stream) {
		Stream<T> paged = stream;
		if (source.getSkip() > 0) {
			paged = paged.skip(source.getSkip());
		}
		if (source.getTop() > 0) {
			paged = paged.limit(source.getTop());
		}
		return paged;
	}

	// -------------------------------------------------------------- projection

	private Stream<QueryResultRow> project(Stream<EObject> objects) {
		Stream<QueryResultRow> rows = objects.map(this::projectionRow);
		if (source.isDistinct()) {
			java.util.Set<List<Object>> seen = new LinkedHashSet<>();
			rows = rows.filter(row -> seen.add(values(row)));
		}
		return page(sortRows(rows));
	}

	private QueryResultRow projectionRow(EObject object) {
		List<Object> values = new ArrayList<>(source.getSelect().size());
		for (Selection selection : source.getSelect()) {
			// an expression projection (issue #189) evaluates per object, a path navigates
			values.add(selection.getKey() != null
					? predicate.value(selection.getKey(), object)
					: predicate.pathValue(selection.getPath(), object));
		}
		return QueryResultRows.of(rowAliases, values);
	}

	// -------------------------------------------------------------- pipeline

	private QueryResult executePipeline(Stream<EObject> objects) {
		Stream<EObject> current = objects;
		List<Stage> stages = source.getApply().getStages();
		boolean groupsSomewhere = stages.stream().anyMatch(GroupByStage.class::isInstance);
		List<Computation> preComputations = new ArrayList<>();
		for (int i = 0; i < stages.size(); i++) {
			Stage stage = stages.get(i);
			if (stage instanceof FilterStage filter) {
				current = current.filter(object -> predicate.test(filter.getPredicate(), object));
			} else if (stage instanceof TopStage top) {
				current = current.limit(top.getCount());
			} else if (stage instanceof SkipStage skip) {
				current = current.skip(skip.getCount());
			} else if (stage instanceof GroupByStage groupBy) {
				int width = groupBy.getPaths().size() + groupBy.getKeys().size()
						+ groupBy.getAggregates().size();
				return rows(rowStages(aggregate(groupBy, current, preComputations), width, stages, i + 1));
			} else if (stage instanceof ComputeStage compute) {
				if (groupsSomewhere) {
					// pre-group compute (issue #87): the aliases feed group keys and
					// aggregate sources, evaluated per object at grouping time
					preComputations.addAll(compute.getComputations());
				} else {
					// terminal computes: one row per entity, single-valued attributes
					// first (issue #82)
					int width = (int) source.getFrom().getEAllAttributes().stream()
							.filter(attribute -> !attribute.isMany()).count();
					return rows(rowStages(current.map(object -> terminalRow(object, width)),
							width, stages, i));
				}
			}
		}
		// no GroupBy stage: the pipeline stayed in object space
		return QueryResults.objects(page(sortObjects(current).filter(distinctObjects())));
	}

	/** Row-space stages after the grouping/terminal switch: HAVING filters, computes, paging. */
	private Stream<QueryResultRow> rowStages(Stream<QueryResultRow> rows, int initialWidth,
			List<Stage> stages, int fromIndex) {
		Stream<QueryResultRow> current = rows;
		int width = initialWidth;
		List<Stage> paging = new ArrayList<>();
		for (int i = fromIndex; i < stages.size(); i++) {
			Stage stage = stages.get(i);
			if (stage instanceof TopStage || stage instanceof SkipStage) {
				// row-space paging is sort-then-limit — deferred behind the sort so all
				// backends page the same (deterministically ordered) window
				paging.add(stage);
			} else if (stage instanceof FilterStage filter) {
				current = current.filter(row -> predicate.testRow(filter.getPredicate(), row));
			} else if (stage instanceof ComputeStage compute) {
				int base = width;
				current = current.map(row -> extendRow(row, compute, base));
				width += compute.getComputations().size();
			}
		}
		Stream<QueryResultRow> sorted = sortRows(current);
		for (Stage stage : paging) {
			sorted = stage instanceof TopStage top ? sorted.limit(top.getCount())
					: sorted.skip(((SkipStage) stage).getCount());
		}
		return page(sorted);
	}

	private QueryResultRow terminalRow(EObject object, int width) {
		List<Object> values = new ArrayList<>(width);
		for (EAttribute attribute : source.getFrom().getEAllAttributes()) {
			if (attribute.isMany()) {
				continue;
			}
			values.add(object.eGet(attribute));
		}
		return QueryResultRows.of(rowAliases.subList(0, width), values);
	}

	/** Appends the computed columns — the aliases were registered at translation. */
	private QueryResultRow extendRow(QueryResultRow row, ComputeStage compute, int width) {
		List<Object> values = new ArrayList<>(width + compute.getComputations().size());
		for (int i = 0; i < width; i++) {
			values.add(row.get(i));
		}
		for (Computation computation : compute.getComputations()) {
			values.add(predicate.rowValue(computation.getExpression(), row));
		}
		return QueryResultRows.of(rowAliases.subList(0, values.size()), values);
	}

	/** A group member with its pre-group compute alias environment (issue #87). */
	private record ComputedMember(EObject object, Map<String, Object> aliasValues) {
	}

	private Stream<QueryResultRow> aggregate(GroupByStage groupBy, Stream<EObject> objects,
			List<Computation> preComputations) {
		Map<List<Object>, List<ComputedMember>> groups = new LinkedHashMap<>();
		objects.forEach(object -> {
			Map<String, Object> aliasValues = aliasValues(object, preComputations);
			List<Object> key = new ArrayList<>(groupBy.getPaths().size() + groupBy.getKeys().size());
			for (PropertyPath path : groupBy.getPaths()) {
				key.add(predicate.pathValue(path, object));
			}
			for (GroupKey groupKey : groupBy.getKeys()) {
				// expression-valued keys (issue #87), e.g. over pre-group compute aliases
				key.add(predicate.value(groupKey.getExpression(), object, aliasValues));
			}
			groups.computeIfAbsent(key, any -> new ArrayList<>()).add(new ComputedMember(object, aliasValues));
		});
		if (groups.isEmpty() && groupBy.getPaths().isEmpty() && groupBy.getKeys().isEmpty()) {
			// whole-set aggregation always yields one row, even over no matches
			groups.put(List.of(), List.of());
		}
		return groups.entrySet().stream().map(group -> {
			List<Object> values = new ArrayList<>(rowKeys.size());
			values.addAll(group.getKey());
			for (Aggregate aggregate : groupBy.getAggregates()) {
				values.add(aggregateValue(aggregate, group.getValue()));
			}
			if (groupBy.getRepresentatives() != null) {
				values.add(representatives(groupBy.getRepresentatives(), group.getValue()));
			}
			// later compute stages extend the row — the aliases beyond this width are theirs
			return QueryResultRows.of(rowAliases.subList(0, values.size()), values);
		});
	}

	/**
	 * The group's own documents as one cell (issue #214, decision R1): a {@code List<EObject>}
	 * in the declared within-group order, windowed by offset and count. Never {@code null} —
	 * an offset past the end of the group yields an empty list, and the group's row with its
	 * keys and aggregates still appears. The group's full size is not repeated here: that is
	 * an ordinary COUNT aggregate, which is what makes a truncated group recognisable.
	 */
	private List<EObject> representatives(RepresentativeSpec spec, List<ComputedMember> members) {
		Comparator<EObject> within = withinGroupOrder(spec);
		Stream<EObject> ordered = members.stream().map(ComputedMember::object);
		if (within != null) {
			ordered = ordered.sorted(within);
		}
		int offset = windowBound(spec.getOffset(), 0);
		int count = windowBound(spec.getCount(), 0);
		return ordered.skip(offset).limit(count).toList();
	}

	/**
	 * The order within a group — the spec's own, and nothing else. There is deliberately no
	 * fallback to the envelope's {@code orderBy}: a query with representatives is grouped, so
	 * its envelope ordering addresses output columns, not the documents inside a group. An
	 * undeclared within-group order leaves the window unspecified.
	 */
	private Comparator<EObject> withinGroupOrder(RepresentativeSpec spec) {
		Comparator<EObject> comparator = null;
		for (OrderBy orderBy : spec.getOrderBy()) {
			if (orderBy.getPath() == null && orderBy.getKey() == null) {
				continue;
			}
			Comparator<EObject> next = orderBy.getKey() != null
					? Comparator.comparing(object -> predicate.value(orderBy.getKey(), object),
							MemoryPredicate.VALUE_ORDER)
					: Comparator.comparing(object -> predicate.pathValue(orderBy.getPath(), object),
							MemoryPredicate.VALUE_ORDER);
			if (orderBy.getDirection() == SortDirection.DESC) {
				next = next.reversed();
			}
			comparator = comparator == null ? next : comparator.thenComparing(next);
		}
		return comparator;
	}

	/** A window bound: a literal, or a bound parameter resolved like any other value. */
	private int windowBound(Expression bound, int absent) {
		if (bound == null) {
			return absent;
		}
		Object resolved = predicate.value(bound, null);
		return resolved instanceof Number number ? number.intValue() : absent;
	}

	/** Evaluates the pre-group computations per object; later aliases see earlier ones. */
	private Map<String, Object> aliasValues(EObject object, List<Computation> preComputations) {
		if (preComputations.isEmpty()) {
			return Map.of();
		}
		Map<String, Object> aliasValues = new LinkedHashMap<>();
		for (Computation computation : preComputations) {
			aliasValues.put(computation.getAlias(),
					predicate.value(computation.getExpression(), object, aliasValues));
		}
		return aliasValues;
	}

	private Object aggregateValue(Aggregate aggregate, List<ComputedMember> members) {
		if (aggregate.getPath() == null && aggregate.getSource() == null) {
			// COUNT over the group itself
			return (long) members.size();
		}
		List<Object> values = members.stream()
				.map(member -> aggregate.getSource() != null
						? predicate.value(aggregate.getSource(), member.object(), member.aliasValues())
						: predicate.pathValue(aggregate.getPath(), member.object()))
				.filter(Objects::nonNull)
				.toList();
		return switch (aggregate.getMethod()) {
		case COUNT -> (long) values.size();
		case COUNT_DISTINCT -> (long) values.stream().distinct().count();
		case AVG -> values.isEmpty() ? null
				: values.stream().mapToDouble(value -> ((Number) value).doubleValue()).average().orElseThrow();
		case SUM -> sum(values);
		case MIN -> values.stream().min(MemoryPredicate.VALUE_ORDER).orElse(null);
		case MAX -> values.stream().max(MemoryPredicate.VALUE_ORDER).orElse(null);
		};
	}

	private Object sum(List<Object> values) {
		if (values.isEmpty()) {
			return null;
		}
		boolean integral = values.stream()
				.allMatch(value -> value instanceof Integer || value instanceof Long
						|| value instanceof Short || value instanceof Byte);
		if (integral) {
			return values.stream().mapToLong(value -> ((Number) value).longValue()).sum();
		}
		return values.stream().mapToDouble(value -> ((Number) value).doubleValue()).sum();
	}

	// -------------------------------------------------------------- row shapes

	private Stream<QueryResultRow> sortRows(Stream<QueryResultRow> rows) {
		if (source.getOrderBy().isEmpty()) {
			return rows;
		}
		Comparator<QueryResultRow> comparator = null;
		for (OrderBy orderBy : source.getOrderBy()) {
			Comparator<QueryResultRow> next;
			if (orderBy.getKey() != null) {
				// arbitrary sort expressions evaluate in row space (issue #84)
				next = Comparator.comparing(
						row -> predicate.rowValue(orderBy.getKey(), row), MemoryPredicate.VALUE_ORDER);
			} else {
				int index = rowIndex(orderBy);
				next = Comparator.comparing(row -> row.get(index), MemoryPredicate.VALUE_ORDER);
			}
			if (orderBy.getDirection() == SortDirection.DESC) {
				next = next.reversed();
			}
			comparator = comparator == null ? next : comparator.thenComparing(next);
		}
		return rows.sorted(comparator);
	}

	private int rowIndex(OrderBy orderBy) {
		try {
			// key membership was already validated at translation
			return rowKeys.indexOf(MemoryQueryProcessor.rowKey(orderBy.getPath(), rowKeys));
		} catch (QueryException e) {
			throw new IllegalStateException("Sort key vanished after translation", e);
		}
	}

	private QueryResult rows(Stream<QueryResultRow> rows) {
		return QueryResults.rows(shape, rows);
	}

	private List<Object> values(QueryResultRow row) {
		List<Object> values = new ArrayList<>(rowKeys.size());
		for (int i = 0; i < rowKeys.size(); i++) {
			values.add(row.get(i));
		}
		return values;
	}
}
