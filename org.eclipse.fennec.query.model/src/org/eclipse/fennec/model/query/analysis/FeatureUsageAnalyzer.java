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
package org.eclipse.fennec.model.query.analysis;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.ExpressionPackage;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.RootReference;
import org.eclipse.fennec.model.expression.TypeCheck;
import org.eclipse.fennec.model.query.ComputeStage;
import org.eclipse.fennec.model.query.Expand;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.Stage;

/**
 * Determines which model features and types a query touches, and in which role (issue #292).
 * Pure function over the model — no backend, no registry, no validation: an invalid query is
 * analyzed as written.
 * <p>
 * The role of a path is the query-model slot it sits under, found by climbing out of the
 * expression tree. Every expression construct — quantifiers, geo and interval subjects, map
 * values, counts, {@code RootReference} — is therefore covered without a visitor per node
 * type, and a path inside a projected {@code CollectionCount} predicate still counts as
 * {@link FeatureRole#PROJECTION}: its value shapes a delivered column.
 * <p>
 * What a query hands out without naming it is reported separately, see
 * {@link FeatureUsage#deliveredTypes()}.
 *
 * @author Mark Hoffmann
 * @since 23.09.2026
 */
public final class FeatureUsageAnalyzer {

	/**
	 * Role per expression-valued containment slot of the query model. A slot missing here fails
	 * the analysis instead of dropping its paths — a query model extension has to decide its
	 * role, and {@code FeatureUsageAnalyzerTest} checks that every slot has one.
	 */
	private static final Map<EReference, FeatureRole> ROLES = Map.ofEntries(
			entry(QueryPackage.Literals.QUERY__PREDICATE, FeatureRole.FILTER),
			entry(QueryPackage.Literals.FILTER_STAGE__PREDICATE, FeatureRole.FILTER),
			entry(QueryPackage.Literals.EXPAND__FILTER, FeatureRole.FILTER),
			entry(QueryPackage.Literals.ORDER_BY__PATH, FeatureRole.SORT),
			entry(QueryPackage.Literals.ORDER_BY__KEY, FeatureRole.SORT),
			entry(QueryPackage.Literals.SELECTION__PATH, FeatureRole.PROJECTION),
			entry(QueryPackage.Literals.SELECTION__KEY, FeatureRole.PROJECTION),
			entry(QueryPackage.Literals.GROUP_BY_STAGE__PATHS, FeatureRole.GROUP),
			entry(QueryPackage.Literals.GROUP_KEY__EXPRESSION, FeatureRole.GROUP),
			entry(QueryPackage.Literals.AGGREGATE__PATH, FeatureRole.AGGREGATE),
			entry(QueryPackage.Literals.AGGREGATE__SOURCE, FeatureRole.AGGREGATE),
			entry(QueryPackage.Literals.COMPUTATION__EXPRESSION, FeatureRole.COMPUTE),
			entry(QueryPackage.Literals.EXPAND__PATH, FeatureRole.EXPAND),
			entry(QueryPackage.Literals.REPRESENTATIVE_SPEC__COUNT, FeatureRole.PAGE),
			entry(QueryPackage.Literals.REPRESENTATIVE_SPEC__OFFSET, FeatureRole.PAGE));

	private FeatureUsageAnalyzer() {
	}

	/**
	 * Analyzes a query envelope.
	 *
	 * @param query the query to analyze, must not be {@code null}
	 * @return the feature usage
	 */
	public static FeatureUsage analyze(Query query) {
		if (query == null) {
			throw new IllegalArgumentException("query must not be null");
		}
		List<PathUse> paths = new ArrayList<>();
		Set<EClass> types = new LinkedHashSet<>();
		addType(types, query.getFrom());
		collect(query.eAllContents(), null, paths, types);

		boolean deliversObjects = deliversObjects(query);
		Set<EClass> delivered = new LinkedHashSet<>();
		if (!query.isCountOnly()) {
			if (deliversObjects) {
				addClosure(delivered, query.getFrom());
				for (Expand expand : query.getExpand()) {
					addExpanded(delivered, expand);
				}
			}
			for (Selection selection : query.getSelect()) {
				// a projected reference hands out its target — counted as a whole object,
				// the conservative reading for a consumer that asks what leaves the store
				addReferenceTarget(delivered, selection.getPath());
			}
		}
		return new FeatureUsage(query.getFrom(), deliversObjects, paths, types, delivered);
	}

	/**
	 * Analyzes a bare expression — the selector of a write command or a derivation. Every path
	 * counts as {@link FeatureRole#FILTER}, and nothing is delivered.
	 *
	 * @param expression the expression to analyze, must not be {@code null}
	 * @return the feature usage, with {@code from} unset
	 */
	public static FeatureUsage analyze(Expression expression) {
		if (expression == null) {
			throw new IllegalArgumentException("expression must not be null");
		}
		List<PathUse> paths = new ArrayList<>();
		Set<EClass> types = new LinkedHashSet<>();
		visit(expression, expression, paths, types);
		collect(expression.eAllContents(), expression, paths, types);
		return new FeatureUsage(null, false, paths, types, Set.of());
	}

	/**
	 * Returns the role of a query-model slot.
	 *
	 * @param slot the containment reference
	 * @return the role, or {@code null} when the slot has none
	 */
	static FeatureRole roleOf(EReference slot) {
		return ROLES.get(slot);
	}

	private static void collect(Iterator<EObject> contents, EObject root, List<PathUse> paths,
			Set<EClass> types) {
		while (contents.hasNext()) {
			visit(contents.next(), root, paths, types);
		}
	}

	private static void visit(EObject object, EObject root, List<PathUse> paths, Set<EClass> types) {
		if (object instanceof PropertyPath path) {
			paths.add(new PathUse(roleOf(path, root), path.getSegments()));
			addType(types, path.getCastBase());
		} else if (object instanceof TypeCheck typeCheck) {
			addType(types, typeCheck.getType());
		} else if (object instanceof RootReference rootReference) {
			addType(types, rootReference.getFrom());
		}
	}

	/**
	 * Climbs out of the expression tree to the query-model slot the path sits under. The
	 * climb stops at {@code root} for a bare expression, which is all filter.
	 */
	private static FeatureRole roleOf(PropertyPath path, EObject root) {
		EObject child = path;
		EObject container = path.eContainer();
		while (child != root && container != null
				&& container.eClass().getEPackage() == ExpressionPackage.eINSTANCE) {
			child = container;
			container = container.eContainer();
		}
		if (child == root || container == null) {
			return FeatureRole.FILTER;
		}
		EReference slot = child.eContainmentFeature();
		FeatureRole role = ROLES.get(slot);
		if (role == null) {
			throw new IllegalStateException("No feature role for the query slot "
					+ slot.getEContainingClass().getName() + "." + slot.getName());
		}
		return role;
	}

	/**
	 * Whether root objects are handed out: an {@code OBJECTS} result — no count, no
	 * projection, no row-shaping stage — or representatives per group, which put the grouped
	 * documents themselves into a row cell.
	 */
	private static boolean deliversObjects(Query query) {
		if (query.isCountOnly()) {
			return false;
		}
		boolean rowShaped = !query.getSelect().isEmpty();
		if (query.getApply() != null) {
			for (Stage stage : query.getApply().getStages()) {
				if (stage instanceof GroupByStage groupBy) {
					if (groupBy.getRepresentatives() != null) {
						return true;
					}
					rowShaped = true;
				} else if (stage instanceof ComputeStage) {
					rowShaped = true;
				}
			}
		}
		return !rowShaped;
	}

	private static void addExpanded(Set<EClass> delivered, Expand expand) {
		addReferenceTarget(delivered, expand.getPath());
		for (Expand nested : expand.getExpand()) {
			addExpanded(delivered, nested);
		}
	}

	private static void addReferenceTarget(Set<EClass> delivered, PropertyPath path) {
		if (path == null || path.getSegments().isEmpty()) {
			return;
		}
		EStructuralFeature last = path.getSegments().get(path.getSegments().size() - 1);
		if (last instanceof EReference reference) {
			addClosure(delivered, reference.getEReferenceType());
		}
	}

	/** A delivered object carries its contained children with it. */
	private static void addClosure(Set<EClass> delivered, EClass type) {
		if (type == null || !delivered.add(type)) {
			return;
		}
		for (EReference containment : type.getEAllContainments()) {
			addClosure(delivered, containment.getEReferenceType());
		}
	}

	private static void addType(Set<EClass> types, EClass type) {
		if (type != null) {
			types.add(type);
		}
	}

}
