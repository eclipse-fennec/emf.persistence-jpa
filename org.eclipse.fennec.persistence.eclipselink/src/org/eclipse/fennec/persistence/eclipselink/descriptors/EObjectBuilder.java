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
package org.eclipse.fennec.persistence.eclipselink.descriptors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopier;
import org.eclipse.fennec.persistence.eclipselink.mappings.AuthoritativeFill;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.fennec.persistence.eclipselink.indirection.ETransparentIndirectionPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * Object builder for handling {@link EObject}
 * @author Mark Hoffmann
 * @since 13.01.2025
 */
public class EObjectBuilder extends ObjectBuilder {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance.
	 * @param descriptor
	 */
	public EObjectBuilder(ClassDescriptor descriptor) {
		super(descriptor);
	}

	/**
	 * The one funnel through which an object's attributes are filled <b>from a database
	 * row</b> — both the initial build and the refresh of an invalidated cache hit
	 * ({@code refreshObjectIfRequired}) arrive here. Marked as an authoritative fill so
	 * {@code EReferenceAccessor} may treat incoming collection content as the store's truth
	 * and drop stale members (issue #144). The clone, backup and merge paths below do
	 * <em>not</em> set the mark: their collection writes may carry partial content and must
	 * keep accumulating.
	 */
	@Override
	public void buildAttributesIntoObject(Object domainObject, CacheKey cacheKey,
			AbstractRecord databaseRow, ObjectBuildingQuery query, JoinedAttributeManager joinManager,
			FetchGroup executionFetchGroup, boolean forRefresh, AbstractSession targetSession)
			throws DatabaseException {
		AuthoritativeFill.enter();
		try {
			super.buildAttributesIntoObject(domainObject, cacheKey, databaseRow, query, joinManager,
					executionFetchGroup, forRefresh, targetSession);
		} finally {
			AuthoritativeFill.exit();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.descriptors.ObjectBuilder#buildBackupClone(java.lang.Object, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl)
	 */
	@Override
	public Object buildBackupClone(Object clone, UnitOfWorkImpl unitOfWork) {
		if (clone instanceof EObject eClone) {
			// A guaranteed-fresh instance, deliberately NOT the copy policy: ECopyPolicy's
			// cloneToOriginals shortcut hands back the CACHE ORIGINAL, which made the backup
			// the original — every commit then compared the clone against the merge products
			// of the previous commit. Change comparison keys on instance identity
			// (ContainerPolicy.compareCollectionsForChange uses an IdentityHashMap), so every
			// untouched child read as removed-plus-added, and private ownership (#142) turned
			// the removes into DELETEs of kept children (#143). It also meant the ECopier
			// below wrote the clone's attributes onto the shared-cache original mid-transaction.
			EObject backup = (EObject) this.descriptor.getInstantiationPolicy().buildNewInstance();
			new ECopier(backup, null).copy(eClone);
			// EclipseLink's backup contract for references: a snapshot holding the SAME
			// instances as the clone, so an untouched slot compares as unchanged and a
			// genuine removal (or unset) surfaces with the real instance. EMF forbids a
			// plain add/set — containment would steal the child from the clone, and an
			// eOpposite would wire the backup into live objects — hence the raw,
			// inverse-free writes. Relation-table collections (AP-47) are excluded here:
			// the mapping loop below snapshots them through their indirection policy.
			snapshotReferencesInto(eClone, backup, transparentAttributeNames());
			// ECopier.copy only covers attributes and containments — cross references
			// stay empty in the backup. For relation-table collections (AP-47) an empty
			// backup makes commit's change comparison treat every element as newly
			// added, re-INSERTing existing join rows. Snapshot those collections into
			// the backup via the mapping so an untouched list compares as unchanged.
			List<DatabaseMapping> mappings = getRelationshipMappings();
			int size = mappings.size();
			for (int index = 0; index < size; index++) {
				DatabaseMapping mapping = mappings.get(index);
				if (mapping instanceof ForeignReferenceMapping frm
						&& frm.getIndirectionPolicy() instanceof ETransparentIndirectionPolicy) {
					mapping.buildBackupClone(clone, backup, unitOfWork);
				}
			}
			return backup;
		}
		return super.buildBackupClone(clone, unitOfWork);
	}

	/** The attribute names whose backup is built through their transparent indirection. */
	private Set<String> transparentAttributeNames() {
		Set<String> names = new HashSet<>();
		for (DatabaseMapping mapping : getRelationshipMappings()) {
			if (mapping instanceof ForeignReferenceMapping frm
					&& frm.getIndirectionPolicy() instanceof ETransparentIndirectionPolicy) {
				names.add(mapping.getAttributeName());
			}
		}
		return names;
	}

	/**
	 * Copies the clone's reference slots into the backup by <em>reference</em>, without
	 * EMF inverse maintenance: {@code basicAdd} for lists, a raw settings write for
	 * single-valued features. The instances stay where they are — the backup is
	 * EclipseLink bookkeeping and never leaves the unit of work.
	 */
	private static void snapshotReferencesInto(EObject clone, EObject backup,
			Set<String> handledByIndirection) {
		for (EReference ref : clone.eClass().getEAllReferences()) {
			if (ref.isDerived() || ref.isTransient() || !ref.isChangeable()
					|| handledByIndirection.contains(ref.getName())) {
				continue;
			}
			if (ref.isMany()) {
				@SuppressWarnings("unchecked")
				InternalEList<Object> backupList = (InternalEList<Object>) backup.eGet(ref);
				for (Object child : ((InternalEList<?>) clone.eGet(ref)).basicList()) {
					backupList.basicAdd(child, null);
				}
			} else {
				Object child = ((InternalEObject) clone).eGet(ref, false);
				if (child != null && backup instanceof DynamicEObjectImpl dynamicBackup) {
					// dynamicSet writes the settings slot directly — no inverse, no
					// container move; featureID is the dynamic index for dynamic EObjects
					dynamicBackup.dynamicSet(backup.eClass().getFeatureID(ref), child);
				}
			}
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.descriptors.ObjectBuilder#mergeIntoObject(java.lang.Object, org.eclipse.persistence.internal.sessions.ObjectChangeSet, boolean, java.lang.Object, org.eclipse.persistence.internal.sessions.MergeManager, org.eclipse.persistence.internal.sessions.AbstractSession, boolean, boolean, boolean)
	 */
	@Override
	public void mergeIntoObject(Object target, ObjectChangeSet changeSet, boolean isUnInitialized, Object source, MergeManager mergeManager, AbstractSession targetSession, boolean cascadeOnly, boolean isTargetCloneOfOriginal, boolean shouldMergeFetchGroup) {
		// Copy EObject attributes from source to target via ECopier.
		// We use ECopier (not EclipseLink's DirectMapping.mergeIntoObject) because
		// EclipseLink would set raw DB types (e.g. java.sql.Date) that are incompatible
		// with EMF's expected types (e.g. java.time.LocalDate).
		if (target instanceof EObject teo && source instanceof EObject seo) {
			if (!isTargetCloneOfOriginal) {
				new ECopier(teo, null).copy(seo);
			}
		}
		// Merge foreign reference mappings via EclipseLink's standard logic
		// (handles cascading, relationship maintenance, etc.)
		List<DatabaseMapping> mappings = this.descriptor.getMappings();
		int size = mappings.size();
		for (int index = 0; index < size; index++) {
			DatabaseMapping mapping = mappings.get(index);
			if (mapping.isForeignReferenceMapping()
					&& ((!cascadeOnly && !isTargetCloneOfOriginal)
							|| (cascadeOnly && mapping.isForeignReferenceMapping())
							|| (isTargetCloneOfOriginal && mapping.isCloningRequired()))) {
				mapping.mergeIntoObject(target, isUnInitialized, source, mergeManager, targetSession);
			}
		}
		// PERF: Avoid events if no listeners.
		if (this.descriptor.getEventManager().hasAnyEventListeners()) {
			DescriptorEvent event = new DescriptorEvent(target);
			event.setSession(mergeManager.getSession());
			event.setOriginalObject(source);
			event.setChangeSet(changeSet);
			event.setEventCode(DescriptorEventManager.PostMergeEvent);
			this.descriptor.getEventManager().executeEvent(event);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.descriptors.ObjectBuilder#populateAttributesForClone(java.lang.Object, org.eclipse.persistence.internal.identitymaps.CacheKey, java.lang.Object, java.lang.Integer, org.eclipse.persistence.internal.sessions.AbstractSession)
	 */
	@Override
	public void populateAttributesForClone(Object original, CacheKey cacheKey, Object clone, Integer refreshCascade,
			AbstractSession cloningSession) {
		if (clone instanceof EObject teo && original instanceof EObject seo) {
			new ECopier(teo, null).copy(seo);
			List<DatabaseMapping> mappings = getRelationshipMappings();
			int size = mappings.size();
			for (int index = 0; index < size; index++) {
				DatabaseMapping mapping = mappings.get(index);
				mapping.buildClone(original, cacheKey, clone, refreshCascade, cloningSession);
			}
		}

		// PERF: Avoid events if no listeners.
		if (this.descriptor.getEventManager().hasAnyEventListeners()) {
			DescriptorEvent event = new DescriptorEvent(clone);
			event.setSession(cloningSession);
			event.setOriginalObject(original);
			event.setDescriptor(descriptor);
			event.setEventCode(DescriptorEventManager.PostCloneEvent);
			cloningSession.deferEvent(event);
		}
	}
	
}
