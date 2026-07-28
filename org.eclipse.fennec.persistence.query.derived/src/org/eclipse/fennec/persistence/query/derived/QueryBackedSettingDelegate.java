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
package org.eclipse.fennec.persistence.query.derived;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicSettingDelegate;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.fennec.persistence.query.memory.MemoryQueries;

/**
 * The pushdown delegate of one derived reference (concept §4): routing is
 * object-specific, decided per access via {@code owner.eResource()} — owners attached to
 * a {@link QueryableResource} whose base reference holds unresolved proxies query the
 * backend natively (id-IN correlation, P4); everything else evaluates the same predicate
 * locally over the base reference through the {@code memory} query processor, which
 * shares its semantics with the backends by the TCK differential contract.
 *
 * @author Juergen Albert
 * @since 28.07.2026
 */
public class QueryBackedSettingDelegate extends BasicSettingDelegate.Stateless {

	private final DerivedPlan.Pushdown plan;
	private final int maxCorrelationIds;

	/**
	 * @param feature the derived feature this delegate serves
	 * @param plan the compiled pushdown plan
	 * @param maxCorrelationIds the id-IN correlation limit (D4)
	 */
	public QueryBackedSettingDelegate(EStructuralFeature feature, DerivedPlan.Pushdown plan,
			int maxCorrelationIds) {
		super(feature);
		this.plan = Objects.requireNonNull(plan, "plan must not be null");
		this.maxCorrelationIds = maxCorrelationIds;
	}

	@Override
	protected Object get(InternalEObject owner, boolean resolve, boolean coreType) {
		try {
			if (pushdownPossible(owner)) {
				return pushdown(owner);
			}
			return local(owner);
		} catch (QueryException | IOException e) {
			throw new WrappedException(new RuntimeException("Cannot compute derived reference '"
					+ eStructuralFeature.getName() + "' of " + owner.eClass().getName() + ": "
					+ e.getMessage(), e));
		}
	}

	@Override
	protected boolean isSet(InternalEObject owner) {
		return false; // volatile — the value is the query
	}

	private boolean pushdownPossible(InternalEObject owner) {
		if (!(owner.eResource() instanceof QueryableResource) || plan.baseReference().isContainment()) {
			return false;
		}
		return storedList(owner).stream().anyMatch(EObject::eIsProxy);
	}

	/**
	 * The base reference's stored value WITHOUT proxy resolution — a resolving list
	 * would fetch every element one by one and defeat the pushdown.
	 */
	@SuppressWarnings("unchecked")
	private List<EObject> storedList(InternalEObject owner) {
		Object stored = owner.eGet(plan.baseReference(), false);
		if (stored instanceof InternalEList<?> internal) {
			return (List<EObject>) internal.basicList();
		}
		return stored instanceof List ? (List<EObject>) stored : List.of();
	}

	/** Local evaluation over the (resolved) base reference — memory processor semantics. */
	private Object local(InternalEObject owner) throws QueryException {
		Object stored = owner.eGet(plan.baseReference(), true);
		@SuppressWarnings("unchecked")
		Collection<? extends EObject> candidates = stored instanceof Collection
				? (Collection<? extends EObject>) stored
				: List.of();
		Query query = QueryBuilder.from(plan.baseReference().getEReferenceType())
				.where(EcoreUtil.copy(plan.predicate()))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, candidates, null)) {
			return ECollections.unmodifiableEList(new BasicEList<>(result.objects().toList()));
		}
	}

	/** Backend query with id-IN correlation over the owner's stored reference ids (P4). */
	private Object pushdown(InternalEObject owner) throws QueryException, IOException {
		EClass target = plan.baseReference().getEReferenceType();
		EAttribute idAttribute = target.getEIDAttribute();
		if (idAttribute == null) {
			throw new QueryException("Pushdown of '" + eStructuralFeature.getName() + "' needs an id"
					+ " attribute on " + target.getName());
		}
		List<EObject> stored = storedList(owner);
		List<Object> ids = new ArrayList<>(stored.size());
		for (EObject element : stored) {
			Object id = element.eIsProxy()
					? proxyId((InternalEObject) element, idAttribute)
					: element.eGet(idAttribute);
			if (id != null) {
				ids.add(id);
			}
		}
		if (ids.isEmpty()) {
			return ECollections.emptyEList();
		}
		if (ids.size() > maxCorrelationIds) {
			throw new QueryException("Derived reference '" + eStructuralFeature.getName() + "' correlates "
					+ ids.size() + " ids, exceeding the limit of " + maxCorrelationIds
					+ " — restructure the model or raise the limit (concept D4)");
		}
		Expression predicate = EcoreUtil.copy(plan.predicate());
		Query query = QueryBuilder.from(target)
				.where(Expressions.and(Expressions.path(idAttribute).in(ids.toArray()), predicate))
				.build();
		try (QueryResult result = ((QueryableResource) targetResource(owner, target)).query(query)) {
			return ECollections.unmodifiableEList(new BasicEList<>(result.objects().toList()));
		}
	}

	/**
	 * The id carried by a backend proxy. The JPA backend encodes fragments as
	 * {@code //<reference>/<idAttribute>/<idValue>}, Mongo carries the plain target id —
	 * the id value is the last fragment segment either way.
	 */
	private static Object proxyId(InternalEObject proxy, EAttribute idAttribute) {
		String fragment = proxy.eProxyURI() == null ? null : proxy.eProxyURI().fragment();
		if (fragment == null || fragment.isEmpty()) {
			return null;
		}
		String idText = fragment.substring(fragment.lastIndexOf('/') + 1);
		return idText.isEmpty() ? null
				: EcoreUtil.createFromString(idAttribute.getEAttributeType(), idText);
	}

	/** The target-type resource next to the owner's: same store, type segment swapped. */
	private Resource targetResource(InternalEObject owner, EClass target) throws QueryException {
		Resource ownerResource = owner.eResource();
		if (owner.eClass() == target || ownerResource.getResourceSet() == null) {
			if (ownerResource instanceof QueryableResource) {
				return ownerResource;
			}
			throw new QueryException("Owner resource cannot execute queries: " + ownerResource.getURI());
		}
		URI targetUri = ownerResource.getURI().trimSegments(1).appendSegment(target.getName());
		Resource resource = ownerResource.getResourceSet().createResource(targetUri);
		if (!(resource instanceof QueryableResource)) {
			throw new QueryException("Resource for " + targetUri + " cannot execute queries");
		}
		return resource;
	}
}
