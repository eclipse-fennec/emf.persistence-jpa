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
package org.eclipse.fennec.persistence.eclipselink.mappings;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeContext;
import org.eclipse.fennec.persistence.eclipselink.indirection.EBasicIndirectionPolicy;
import org.eclipse.fennec.persistence.eclipselink.indirection.ETransparentIndirectionPolicy;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.FetchType;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.RelationTableMechanism;
import org.eclipse.persistence.queries.DirectReadQuery;

/**
 * Shared behaviour of the EMF-aware relationship mappings ({@code EOneToOneMapping},
 * {@code EManyToOneMapping}, {@code EOneToManyMapping}, {@code EManyToManyMapping}, …):
 * common EMF configuration, proxy-aware cascade traversals and the construction of the
 * ID-only queries backing element-level lazy loading (AP-47).
 *
 * @author Mark Hoffmann
 * @since 15.07.2026
 */
final class EMappingSupport {

	private static final Logger LOG = Logger.getLogger(EMappingSupport.class.getName());

	private EMappingSupport() {
	}

	/**
	 * Applies the EMF configuration driven by the eorm {@code fetch}/{@code batch} contract
	 * (the eorm is the source of truth). Every mapping gets no join fetching and EMF value
	 * access through {@link EReferenceAccessor}; the fetch mode then decides indirection:
	 * <ul>
	 * <li><b>containment</b> — EMF composition, always eager, no indirection. A containment
	 *     declared {@code LAZY} is inconsistent: a diagnostic warning is logged and it is
	 *     kept eager.</li>
	 * <li><b>non-containment {@code EAGER}</b> — materialised at owner read via EclipseLink's
	 *     native eager loading, no EMF proxies.</li>
	 * <li><b>non-containment {@code LAZY} + {@code batch}</b> (collections) — deferred; on
	 *     first access all elements are resolved in one {@code IN} query
	 *     ({@link ETransparentIndirectionPolicy} in batch mode).</li>
	 * <li><b>non-containment {@code LAZY}</b> — element-level lazy proxies
	 *     ({@link ETransparentIndirectionPolicy} for collections, {@link EBasicIndirectionPolicy}
	 *     for single-valued).</li>
	 * </ul>
	 *
	 * @param baseRef the eorm reference carrying {@code fetch}/{@code batch}; may be
	 *                {@code null}, in which case the structural default applies (containment
	 *                eager, non-containment lazy)
	 */
	static void configureEMF(ForeignReferenceMapping mapping, EReference reference, BaseRef baseRef,
			EDynamicType type, EDynamicTypeContext context) {
		mapping.setJoinFetch(ForeignReferenceMapping.NONE);
		mapping.setIsCascadeOnDeleteSetOnDatabase(false);
		mapping.setDerivesId(false);
		mapping.setIsPrivateOwned(false);
		mapping.setIsCacheable(true);
		mapping.setAttributeAccessor(EReferenceAccessor.create(mapping, reference, context));

		FetchType fetch = nonNull(baseRef) && nonNull(baseRef.getFetch())
				? baseRef.getFetch()
				: (reference.isContainment() ? FetchType.EAGER : FetchType.LAZY);
		boolean batch = nonNull(baseRef) && baseRef.isBatch();

		if (reference.isContainment()) {
			if (fetch == FetchType.LAZY) {
				LOG.log(Level.WARNING,
						"Setting a containment to LAZY doesn''t make sense, we keep it eager (reference ''{0}'')",
						reference.getName());
			}
			mapping.setIsLazy(false);
			mapping.dontUseIndirection();
			return;
		}

		if (fetch == FetchType.EAGER) {
			// Materialise the target(s) at owner read — EclipseLink native eager, no proxies.
			mapping.setIsLazy(false);
			mapping.dontUseIndirection();
			return;
		}

		// LAZY non-containment
		mapping.setIsLazy(true);
		if (mapping instanceof CollectionMapping) {
			ETransparentIndirectionPolicy policy = new ETransparentIndirectionPolicy(mapping, reference, type);
			policy.setBatch(batch);
			mapping.setIndirectionPolicy(policy);
		} else {
			mapping.setIndirectionPolicy(new EBasicIndirectionPolicy(mapping, reference, type));
		}
	}

	/**
	 * Returns {@code true} if the object is an unresolved EMF proxy — the persisted
	 * representation of an <em>existing</em> row that must never be treated as a new
	 * entity by cascade-persist traversals.
	 */
	static boolean isUnresolvedProxy(Object object) {
		return object instanceof EObject eo && eo.eIsProxy();
	}

	/**
	 * Returns {@code true} if the single-valued attribute of {@code object} currently
	 * holds an unresolved EMF proxy.
	 */
	static boolean holdsUnresolvedProxy(ForeignReferenceMapping mapping, Object object) {
		Object attributeValue = mapping.getAttributeValueFromObject(object);
		if (isNull(attributeValue) || !mapping.getIndirectionPolicy().objectIsInstantiated(attributeValue)) {
			return false;
		}
		return isUnresolvedProxy(mapping.getIndirectionPolicy().getRealAttributeValueFromObject(object, attributeValue));
	}

	/**
	 * Proxy-aware variant of {@code CollectionMapping.cascadeDiscoverAndPersistUnregisteredNewObjects}:
	 * runs the element traversal but skips unresolved proxies. Returns {@code false} when
	 * the collection is not instantiated — the caller must then delegate to its default
	 * implementation (deferred-changes handling).
	 */
	@SuppressWarnings("rawtypes")
	static boolean cascadeDiscoverSkippingProxies(CollectionMapping mapping, Object object, Map newObjects,
			Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow, Set cascadeErrors) {
		Object attributeValue = mapping.getAttributeValueFromObject(object);
		if (isNull(attributeValue) || !mapping.getIndirectionPolicy().objectIsInstantiated(attributeValue)) {
			return false;
		}
		ContainerPolicy cp = mapping.getContainerPolicy();
		Object collection = mapping.getRealCollectionAttributeValueFromObject(object, uow);
		Object iterator = cp.iteratorFor(collection);
		boolean cascade = mapping.isCascadePersist();
		while (cp.hasNext(iterator)) {
			Object wrappedObject = cp.nextEntry(iterator, uow);
			Object nextObject = cp.unwrapIteratorResult(wrappedObject);
			if (isUnresolvedProxy(nextObject)) {
				continue;
			}
			if (mapping.isCandidateForPrivateOwnedRemoval()) {
				uow.removePrivateOwnedObject(mapping, nextObject);
			}
			uow.discoverAndPersistUnregisteredNewObjects(nextObject, cascade, newObjects,
					unregisteredExistingObjects, visitedObjects, cascadeErrors);
			cp.cascadeDiscoverAndPersistUnregisteredNewObjects(wrappedObject, newObjects,
					unregisteredExistingObjects, visitedObjects, uow, cascadeErrors);
		}
		return true;
	}

	/**
	 * Proxy-aware variant of {@code CollectionMapping.cascadeRegisterNewIfRequired}:
	 * registers non-proxy elements for persist, skips unresolved proxies. Returns
	 * {@code false} when nothing needs to be done or the collection is not instantiated
	 * and the owner is not new — mirroring the default guard conditions.
	 */
	@SuppressWarnings("rawtypes")
	static boolean cascadeRegisterNewSkippingProxies(CollectionMapping mapping, Object object, UnitOfWorkImpl uow,
			Map visitedObjects) {
		if (!mapping.isCascadePersist()) {
			return true;
		}
		Object attributeValue = mapping.getAttributeValueFromObject(object);
		if (isNull(attributeValue)
				|| (!mapping.getIndirectionPolicy().objectIsInstantiated(attributeValue) && !uow.isCloneNewObject(object))) {
			return true;
		}
		ContainerPolicy cp = mapping.getContainerPolicy();
		Object collection = mapping.getRealCollectionAttributeValueFromObject(object, uow);
		Object iterator = cp.iteratorFor(collection);
		boolean shouldAddPrivateOwnedObject = mapping.isCandidateForPrivateOwnedRemoval()
				&& uow.shouldDiscoverNewObjects() && uow.isCloneNewObject(object);
		while (cp.hasNext(iterator)) {
			Object wrappedObject = cp.nextEntry(iterator, uow);
			Object nextObject = cp.unwrapIteratorResult(wrappedObject);
			if (isUnresolvedProxy(nextObject)) {
				continue;
			}
			if (shouldAddPrivateOwnedObject && nonNull(nextObject)) {
				uow.addPrivateOwnedObject(mapping, nextObject);
			}
			uow.registerNewObjectForPersist(nextObject, visitedObjects);
			cp.cascadeRegisterNewIfRequired(wrappedObject, uow, visitedObjects);
		}
		return true;
	}

	/**
	 * Builds the ID-only query for a relation-table mapping: selects the target-key
	 * column of the relation (join) table restricted by the source key — mirrors
	 * EclipseLink's {@code RelationTableMechanism.initializeLockRelationTableQuery}
	 * pattern with the target-table join omitted. Returns {@code null} for composite
	 * relation keys (callers fall back to full materialisation).
	 */
	static DirectReadQuery buildRelationTableIdQuery(ManyToManyMapping mapping, AbstractSession session) {
		RelationTableMechanism mechanism = mapping.getRelationTableMechanism();
		if (isNull(mechanism)
				|| mechanism.getTargetRelationKeyFields().size() != 1
				|| mechanism.getSourceRelationKeyFields().size() != 1) {
			return null;
		}
		Expression criteria = mechanism.buildSelectionCriteriaAndAddFieldsToQueryInternal(
				mapping, null, false, false);
		SQLSelectStatement statement = new SQLSelectStatement();
		statement.addTable(mechanism.getRelationTable());
		statement.addField(mechanism.getTargetRelationKeyFields().get(0).clone());
		statement.setWhereClause(criteria);
		statement.normalize(session, null);
		DirectReadQuery idQuery = new DirectReadQuery();
		idQuery.setSQLStatement(statement);
		idQuery.setSessionName(session.getName());
		return idQuery;
	}

	/**
	 * Builds the ID-only query for a target-foreign-key mapping (OneToMany with the FK
	 * in the target table): selects only the target's primary-key column restricted by
	 * the target FK — no object materialisation, no recursion into the targets'
	 * references. Returns {@code null} for composite target primary keys.
	 */
	static DirectReadQuery buildTargetPkQuery(OneToManyMapping mapping, AbstractSession session) {
		if (isNull(mapping.getReferenceDescriptor())
				|| mapping.getReferenceDescriptor().getPrimaryKeyFields().size() != 1) {
			return null;
		}
		Map<DatabaseField, DatabaseField> targetFkToSourceKey = mapping.getTargetForeignKeysToSourceKeys();
		if (isNull(targetFkToSourceKey) || targetFkToSourceKey.isEmpty()) {
			return null;
		}
		Expression criteria = null;
		ExpressionBuilder builder = new ExpressionBuilder();
		for (Iterator<DatabaseField> keys = targetFkToSourceKey.keySet().iterator(); keys.hasNext();) {
			DatabaseField targetForeignKey = keys.next();
			DatabaseField sourceKey = targetFkToSourceKey.get(targetForeignKey);
			Expression expression = builder.getField(targetForeignKey).equal(builder.getParameter(sourceKey));
			criteria = expression.and(criteria);
		}
		DatabaseField targetPk = mapping.getReferenceDescriptor().getPrimaryKeyFields().get(0);
		SQLSelectStatement statement = new SQLSelectStatement();
		statement.addTable(mapping.getReferenceDescriptor().getDefaultTable());
		statement.addField(targetPk.clone());
		statement.setWhereClause(criteria);
		statement.normalize(session, null);
		DirectReadQuery idQuery = new DirectReadQuery();
		idQuery.setSQLStatement(statement);
		idQuery.setSessionName(session.getName());
		return idQuery;
	}
}
