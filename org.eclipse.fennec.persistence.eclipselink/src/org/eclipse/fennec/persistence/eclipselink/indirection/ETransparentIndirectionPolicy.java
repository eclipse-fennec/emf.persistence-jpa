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
package org.eclipse.fennec.persistence.eclipselink.indirection;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.mappings.ETargetIdQuerySupport;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.indirection.QueryBasedValueHolder;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.DirectReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;

/**
 * Indirection policy for many-valued non-containment references whose mapping
 * implements {@link ETargetIdQuerySupport} (relation-table ManyToMany, OneToMany with
 * the FK in the target table).
 * <p>
 * Instead of deferring EclipseLink's standard selection query (which fully materialises
 * every target object, recursively triggering the targets' own reference loading), this
 * policy substitutes the mapping's ID-only {@link DirectReadQuery}. Each returned id is
 * turned into a lightweight EMF proxy (id attribute + {@code eProxyURI} of the form
 * {@code jpa://puName/TargetEClass#//refName/idAttr/idValue}) via
 * {@link #buildTargetProxy(Object)}. Target objects are never materialised while the
 * list is filled; resolution of a proxy happens on first access through the standard
 * EMF machinery ({@code ResourceSet.getEObject} → {@code JPAResourceImpl.getEObject}
 * → {@code em.find}).
 * <p>
 * The substituted value keeps the exact shape of the default flow (a
 * {@link QueryBasedValueHolder}), so UnitOfWork cloning, backup and merge behave
 * identically — only the deferred computation differs. Configurations the mapping cannot
 * serve (composite keys, missing EID attribute) fall back to the inherited behaviour,
 * i.e. full materialisation.
 *
 * @author Mark Hoffmann
 * @since 15.07.2026
 */
public class ETransparentIndirectionPolicy extends EBasicIndirectionPolicy {

	private static final Logger LOG = Logger.getLogger(ETransparentIndirectionPolicy.class.getName());

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** The mapping's ID-only query; built once on first use, then reused per row. */
	private transient volatile DirectReadQuery targetIdQuery;

	/**
	 * When {@code true} the whole collection is resolved in one {@code IN} query on first
	 * access (eorm {@code batch=true}); when {@code false} elements stay lightweight proxies
	 * resolved individually on navigation.
	 */
	private boolean batch;

	/**
	 * Creates a new instance.
	 */
	public ETransparentIndirectionPolicy(DatabaseMapping mapping, EReference reference, EDynamicType type) {
		super(mapping, reference, type);
	}

	/**
	 * Enables batch resolution: on first access all element ids are resolved in a single
	 * {@code SELECT ... WHERE idAttr IN (:ids)} and the collection is filled with the
	 * materialised targets instead of lightweight proxies. Driven by the eorm {@code batch}
	 * flag for lazy many-valued references.
	 *
	 * @param batch {@code true} to batch-resolve on first access
	 */
	public void setBatch(boolean batch) {
		this.batch = batch;
	}

	/**
	 * Returns whether batch resolution is enabled.
	 * @return the batch flag
	 */
	public boolean isBatch() {
		return batch;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.indirection.EBasicIndirectionPolicy#valueFromQuery(org.eclipse.persistence.queries.ReadQuery, org.eclipse.persistence.internal.sessions.AbstractRecord, java.lang.Object, org.eclipse.persistence.internal.sessions.AbstractSession)
	 */
	@Override
	public Object valueFromQuery(ReadQuery query, AbstractRecord row, Object sourceObject, AbstractSession session) {
		Object holder = proxyListValueHolder(row, session);
		if (nonNull(holder)) {
			return holder;
		}
		return super.valueFromQuery(query, row, sourceObject, session);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.indirection.EBasicIndirectionPolicy#valueFromQuery(org.eclipse.persistence.queries.ReadQuery, org.eclipse.persistence.internal.sessions.AbstractRecord, org.eclipse.persistence.internal.sessions.AbstractSession)
	 */
	@Override
	public Object valueFromQuery(ReadQuery query, AbstractRecord row, AbstractSession session) {
		Object holder = proxyListValueHolder(row, session);
		if (nonNull(holder)) {
			return holder;
		}
		return super.valueFromQuery(query, row, session);
	}

	/**
	 * Builds the deferred proxy-list value holder for the given source row. Returns
	 * {@code null} when this configuration cannot be served by the ID-only path —
	 * callers must then fall back to the default (full materialisation) flow.
	 */
	private QueryBasedValueHolder<Object> proxyListValueHolder(AbstractRecord row, AbstractSession session) {
		if (!usesIndirection() || isNull(row) || isNull(session)) {
			return null;
		}
		if (!(getMapping() instanceof ETargetIdQuerySupport)) {
			return null;
		}
		EAttribute idAttr = getReference().getEReferenceType().getEIDAttribute();
		if (isNull(idAttr)) {
			return null;
		}
		DirectReadQuery idQuery = targetIdQuery(session);
		if (isNull(idQuery)) {
			return null;
		}
		return new EProxyListValueHolder(idQuery, row, session);
	}

	/**
	 * Lazily obtains the mapping's ID-only query (see {@link ETargetIdQuerySupport}) and
	 * caches it for all subsequent rows. Returns {@code null} when the mapping cannot
	 * serve one — callers fall back to full materialisation.
	 */
	private DirectReadQuery targetIdQuery(AbstractSession session) {
		DirectReadQuery query = targetIdQuery;
		if (nonNull(query)) {
			return query;
		}
		synchronized (this) {
			if (nonNull(targetIdQuery)) {
				return targetIdQuery;
			}
			try {
				targetIdQuery = ((ETargetIdQuerySupport) getMapping()).buildTargetIdQuery(session);
			} catch (RuntimeException e) {
				LOG.log(Level.WARNING, e, () -> "Unable to build target id query for reference "
						+ getReference().getName() + " — falling back to full materialisation");
				return null;
			}
			return targetIdQuery;
		}
	}

	/**
	 * The default clone path wraps the value holder in a {@code UnitOfWorkQueryValueHolder}
	 * whose instantiation runs {@code buildCloneForPartObject}, registering every element
	 * in the UnitOfWork. Registering our unregistered proxy lightweights would make a
	 * subsequent {@code em.find} on the target id return the bare proxy instead of the
	 * materialised entity. Instead, the clone slot receives <em>fresh</em> proxies built
	 * from the source elements' ids. Sharing the source instances is not an option: EMF's
	 * opposite maintenance on a bidirectional reference would then link UnitOfWork clones
	 * into shared-cache originals (and vice versa), corrupting the cache.
	 */
	@Override
	public Object cloneAttribute(Object attributeValue, Object original, CacheKey cacheKey, Object clone,
			Integer refreshCascade, AbstractSession cloningSession, boolean buildDirectlyFromRow) {
		if (usesIndirection()) {
			if (attributeValue instanceof EProxyListValueHolder) {
				// Fresh from the row — the deferred ID-only query has not run yet; the
				// accessor's unwrap will trigger it for the clone slot.
				return attributeValue;
			}
			if (attributeValue instanceof ValueHolderInterface<?> vh
					&& vh.isInstantiated()
					&& vh.getValue() instanceof Collection<?> collection) {
				return new ValueHolder<>(proxifyElements(collection));
			}
			if (attributeValue instanceof Collection<?> collection) {
				return new ValueHolder<>(proxifyElements(collection));
			}
		}
		return super.cloneAttribute(attributeValue, original, cacheKey, clone, refreshCascade, cloningSession,
				buildDirectlyFromRow);
	}

	/**
	 * Maps each source element (materialised object or proxy) to a fresh lightweight
	 * proxy carrying only the id and the {@code eProxyURI}. Elements without a usable id
	 * are passed through unchanged.
	 */
	private List<Object> proxifyElements(Collection<?> source) {
		List<Object> proxies = new ArrayList<>(source.size());
		for (Object element : source) {
			Object replacement = element;
			if (element instanceof EObject eo) {
				EObject proxy = proxify(eo);
				if (nonNull(proxy)) {
					replacement = proxy;
				}
			}
			proxies.add(replacement);
		}
		return proxies;
	}

	/**
	 * The default backup path expects the collection elements to be UnitOfWork-registered
	 * clones and builds per-element backups — our proxies are unregistered lightweights,
	 * which leaves the backup list empty and makes commit's change comparison treat every
	 * element as newly added (duplicate relation-table INSERTs). Instead, snapshot the
	 * clone's collection into the backup: identical content in clone and backup yields
	 * "no change" unless the caller actually modified the list after registration.
	 */
	@Override
	public Object backupCloneAttribute(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
		if (attributeValue instanceof ValueHolderInterface<?> avh
				&& avh.isInstantiated()
				&& avh.getValue() instanceof Collection<?> collection) {
			return new ValueHolder<>(new ArrayList<>(collection));
		}
		if (attributeValue instanceof Collection<?> collection) {
			return new ValueHolder<>(new ArrayList<>(collection));
		}
		return super.backupCloneAttribute(attributeValue, clone, backup, unitOfWork);
	}

	/**
	 * Deferred value holder whose instantiation executes the ID-only relation-table
	 * query and maps each returned id to an EMF proxy. Extending
	 * {@link QueryBasedValueHolder} keeps UnitOfWork wrapping intact — its
	 * {@code instantiateForUnitOfWorkValueHolder} routes through
	 * {@link #instantiate(AbstractSession)} as well.
	 */
	private final class EProxyListValueHolder extends QueryBasedValueHolder<Object> {

		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		private EProxyListValueHolder(DirectReadQuery query, AbstractRecord row, AbstractSession session) {
			super(query, row, session);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.persistence.internal.indirection.QueryBasedValueHolder#instantiate(org.eclipse.persistence.internal.sessions.AbstractSession)
		 */
		@Override
		protected Object instantiate(AbstractSession session) {
			Object result = super.instantiate(session);
			if (!(result instanceof Collection<?> ids) || ids.isEmpty()) {
				return new ArrayList<>();
			}
			if (batch) {
				List<Object> materialised = batchResolve(new ArrayList<>(ids), session);
				if (nonNull(materialised)) {
					return materialised;
				}
				// Fall through to per-element proxies when the batch query cannot be served.
			}
			List<EObject> proxies = new ArrayList<>();
			for (Object id : ids) {
				EObject proxy = buildTargetProxy(id);
				if (nonNull(proxy)) {
					proxies.add(proxy);
				}
			}
			return proxies;
		}
	}

	/**
	 * Resolves all element ids in a single {@code SELECT ... WHERE idAttr IN (:ids)} and
	 * returns the materialised targets. Returns {@code null} when the configuration cannot
	 * be served (missing descriptor/EID attribute) or the query fails — the caller then
	 * falls back to per-element lightweight proxies.
	 */
	private List<Object> batchResolve(List<Object> ids, AbstractSession session) {
		ClassDescriptor targetDescriptor = getForeignReferenceMapping().getReferenceDescriptor();
		EAttribute idAttr = getReference().getEReferenceType().getEIDAttribute();
		if (isNull(targetDescriptor) || isNull(idAttr)) {
			return null;
		}
		try {
			ReadAllQuery query = new ReadAllQuery(targetDescriptor.getJavaClass());
			ExpressionBuilder builder = query.getExpressionBuilder();
			query.setSelectionCriteria(builder.get(idAttr.getName()).in(ids));
			Object result = session.executeQuery(query);
			return result instanceof List<?> list ? new ArrayList<>(list) : null;
		} catch (RuntimeException e) {
			LOG.log(Level.WARNING, e, () -> "Batch IN resolution failed for reference "
					+ getReference().getName() + " — falling back to per-element proxies");
			return null;
		}
	}
}
