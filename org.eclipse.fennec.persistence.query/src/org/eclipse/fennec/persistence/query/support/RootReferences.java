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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.RootReference;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.persistence.query.QueryException;

/**
 * Resolve-then-inline for {@link RootReference} (issue #241).
 * <p>
 * OData's {@code $root} always names one object by a literal key, so the referenced value can be
 * read once and substituted before the query is translated. That is cheaper than a correlated
 * scalar subquery and — more importantly — it is expressible on backends that have no such
 * subquery at all: mongo cannot join across collections in {@code $match}, and this turns the
 * problem into an ordinary keyed read plus a literal.
 * <p>
 * The read itself belongs to the caller, because it is the resource that knows how to reach
 * another entity set; this class owns the substitution and the semantics around it:
 * <ul>
 * <li><b>no match yields {@code null}</b> — inlined as a {@code NullLiteral}, so the enclosing
 *     comparison goes UNKNOWN under the existing 3VL rules (issue #94) rather than needing a
 *     second error path;</li>
 * <li><b>more than one match is an error</b>, never a silent first-row pick — enforced by the
 *     reader, which is the only place that can count them;</li>
 * <li><b>the query is never mutated</b>: it belongs to the caller and may be reused. A copy is
 *     made only when there is something to substitute.</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 24.08.2026
 */
public final class RootReferences {

	private RootReferences() {
	}

	/**
	 * Reads the value one {@link RootReference} names.
	 * <p>
	 * Implemented by the resource, which is what can reach another entity set. Returning
	 * {@code null} means "no such object" and is a legal answer; finding more than one is not,
	 * and must throw.
	 */
	@FunctionalInterface
	public interface Reader {

		/**
		 * @param from the referenced entity set's type
		 * @param key the key predicate selecting exactly one object of that type
		 * @param path the feature to read from it
		 * @return the value, or {@code null} when no object matches
		 * @throws QueryException if more than one object matches, or the read fails
		 */
		Object read(EClass from, Expression key, PropertyPath path) throws QueryException;
	}

	/**
	 * @param query the query to inspect; may be {@code null}
	 * @return whether the query contains at least one {@link RootReference}
	 */
	public static boolean present(Query query) {
		if (isNull(query)) {
			return false;
		}
		for (Iterator<EObject> contents = query.eAllContents(); contents.hasNext();) {
			if (contents.next() instanceof RootReference) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a query with every {@link RootReference} replaced by the value {@code reader}
	 * produced for it.
	 *
	 * @param query the query to transform, must not be {@code null}
	 * @param reader the resource's keyed read
	 * @return a transformed copy, or {@code query} itself when it contains no root reference
	 * @throws QueryException if a reference cannot be resolved
	 */
	public static Query inline(Query query, Reader reader) throws QueryException {
		if (!present(query)) {
			return query;
		}
		Query copy = EcoreUtil.copy(query);
		List<RootReference> references = new ArrayList<>();
		for (Iterator<EObject> contents = copy.eAllContents(); contents.hasNext();) {
			if (contents.next() instanceof RootReference reference) {
				references.add(reference);
			}
		}
		for (RootReference reference : references) {
			Object value = reader.read(reference.getFrom(), reference.getKey(), reference.getPath());
			// a missing object is a null value, not a failure: the comparison around it goes
			// UNKNOWN, which is what an empty scalar subquery does in SQL
			Expression inlined = nonNull(value) ? Expressions.literal(value)
					: Expressions.literal(null);
			EcoreUtil.replace(reference, inlined);
		}
		return copy;
	}
}
