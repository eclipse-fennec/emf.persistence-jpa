/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.eclipselink.descriptors;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopier;
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

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.descriptors.ObjectBuilder#buildBackupClone(java.lang.Object, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl)
	 */
	@Override
	public Object buildBackupClone(Object clone, UnitOfWorkImpl unitOfWork) {
		if (clone instanceof EObject eClone) {
			// The copy policy builds clone    .
			EClassDescriptor descriptor = (EClassDescriptor) this.descriptor;
			EObject backup = (EObject) descriptor.getCopyPolicy().buildClone(clone, unitOfWork);
			new ECopier(backup, null).copy(eClone);
			// PERF: Avoid synchronized enumerator as is concurrency bottleneck.
//			List<DatabaseMapping> mappings = getRelationshipMappings();
//			int size = mappings.size();
//			for (int index = 0; index < size; index++) {
//				DatabaseMapping mapping = mappings.get(index);
//				mapping.buildBackupClone(clone, backup, unitOfWork);
//			}
			return backup;
		}
		return super.buildBackupClone(clone, unitOfWork);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.descriptors.ObjectBuilder#mergeIntoObject(java.lang.Object, org.eclipse.persistence.internal.sessions.ObjectChangeSet, boolean, java.lang.Object, org.eclipse.persistence.internal.sessions.MergeManager, org.eclipse.persistence.internal.sessions.AbstractSession, boolean, boolean, boolean)
	 */
	public void mergeIntoObject(Object target, ObjectChangeSet changeSet, boolean isUnInitialized, Object source, MergeManager mergeManager, AbstractSession targetSession, boolean cascadeOnly, boolean isTargetCloneOfOriginal, boolean shouldMergeFetchGroup) {
		if (isTargetCloneOfOriginal) {
			// nothing to do
			return;
		}
		if (isUnInitialized && target instanceof EObject teo && source instanceof EObject seo) {
			new ECopier(teo, null).copy(seo);
			List<DatabaseMapping> mappings = this.descriptor.getMappings();
			int size = mappings.size();
			for (int index = 0; index < size; index++) {
				DatabaseMapping mapping = mappings.get(index);
				if (mapping.isForeignReferenceMapping() && 
						((!cascadeOnly && !isTargetCloneOfOriginal)
								|| (cascadeOnly && mapping.isForeignReferenceMapping())
								|| (isTargetCloneOfOriginal && mapping.isCloningRequired()))) {
					mapping.mergeIntoObject(target, isUnInitialized, source, mergeManager, targetSession);
				}
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
