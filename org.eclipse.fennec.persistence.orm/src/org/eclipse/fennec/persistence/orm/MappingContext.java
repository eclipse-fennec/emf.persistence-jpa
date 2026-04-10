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
package org.eclipse.fennec.persistence.orm;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.MappedByRef;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;
import org.eclipse.fennec.persistence.orm.processor.BaseReferenceProcessor;
import org.eclipse.fennec.persistence.orm.processor.BasicProcessor;
import org.eclipse.fennec.persistence.orm.processor.ElementCollectionProcessor;
import org.eclipse.fennec.persistence.processor.ProcessingContext;

/**
 * Helper class and cache for the {@link EntityMapper}
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public class MappingContext implements ProcessingContext {

	public enum MappingType {
		ONE_TO_ONE,
		ONE_TO_MANY,
		MANY_TO_MANY,
		MANY_TO_ONE
	}

	public class MappedBy {
		EReference reference;
		MappingType mappingType = null;
		public String mappedByName = null;
	}

	/*
	 * Map that holds EClassifiers and their Entities
	 */
	private Map<EClassifier, Entity> entityMap = new HashMap<>();
	/*
	 * Map that holds all reference mappings
	 */
	private Map<EReference, BaseReferenceProcessor<? extends BaseRef>> refProcessorMap = new HashMap<>();
	/*
	 * Map that holds all reference mappings
	 */
	private Map<EReference, BaseRef> refMappingMap = new HashMap<>();
	/*
	 * Map that hold foreign EReferences to a EClass.
	 */
	private Map<EClass, List<EReference>> eClassToRefMap = new HashMap<>();
	/*
	 * Map that hold the opposite of a key as values
	 */
	private Map<EReference, EReference> oppositeRefMap = new HashMap<>();
	/*
	 * Map that holds mapped by information for a reference. This can be used for
	 * other bi-directional mappings
	 */
	private Map<EReference, MappedBy> mappedByMap = new HashMap<>();

	public BasicProcessor createBasicAccessor(EAttribute attribute) {
		return new BasicProcessor(attribute, this);
	}
	
	public ElementCollectionProcessor createElementCollectionAccessor(EAttribute attribute) {
		return new ElementCollectionProcessor(attribute, this);
	}
	
	
	public synchronized <T extends BaseRef>  void registerRefMapping(EReference reference, BaseReferenceProcessor<T> baseReferenceProcessor) {
		refProcessorMap.put(reference, baseReferenceProcessor);
//		registerRefMapping(reference, baseReferenceProcessor.getMapping());
	}

	public synchronized <T extends BaseRef> T registerRefMapping(EReference reference, T baseRef) {
		if (isNull(baseRef) || isNull(reference)) {
			return null;
		}
		// keep the mapping
		refMappingMap.put(reference, baseRef);
		// create mapped by information, if it no mappedBy mapping
		if (!isMappedByMapping(baseRef)) {
			MappedBy mb = calculateMappedBy(reference);
			if (nonNull(mb)) {
				mappedByMap.put(reference, mb);
			}
		}
		// create  a link to the reference and its type
		EClass refType = reference.getEReferenceType();
		eClassToRefMap.computeIfAbsent(refType, E->new LinkedList<>()).add(reference);
		/*
		 * We only register opposites, in one direction using first come, dirst win.
		 * For containments the containers will be mapped first.
		 */
		if (nonNull(reference.getEOpposite()) && 
				!isMappedByMapping(baseRef) && 
				!oppositeRefMap.containsValue(reference)) {
			oppositeRefMap.putIfAbsent(reference, reference.getEOpposite());
		}
		return baseRef;
	}
	
	
	public MappedBy getMappedBy(EReference reference) {
		return mappedByMap.get(reference);
	}
	
	/**
	 * Returns <code>true</code>, if the given mapping is a {@link MappedByRef} mapping
	 * and the mappedMy value is set.
	 * @param mapping the mapping
	 * @return <code>true</code>, if the given mapping is a {@link MappedByRef} mapping
	 */
	private boolean isMappedByMapping(BaseRef mapping) {
		return nonNull(mapping) &&
				mapping instanceof MappedByRef && 
				mapping.eIsSet(EORMPackage.eINSTANCE.getMappedByRef_MappedBy());
	}

	/**
	 * Creates the mappedBy name
	 * @param reference the {@link EReference}
	 * @return the name or <code>null</code>
	 */
	public String calculateMappedByName(EReference reference) {
		requireNonNull(reference);
		BaseRef mapping = refMappingMap.get(reference);
		if (nonNull(mapping)) {
			return mapping.getName();
		}
		return null;
	}

	/**
	 * Calculates the {@link MappedBy} instance for a {@link EReference}
	 * @param reference the {@link EReference}
	 */
	private MappedBy calculateMappedBy(EReference reference) {
		requireNonNull(reference);
		EReference mappedByRef = reference.getEOpposite();
		if (isNull(mappedByRef)) {
			return null;
		}
		MappedBy mappedBy = new MappedBy();
		mappedBy.reference = reference;
		mappedBy.mappingType = calculateMappingType(reference);
		mappedBy.mappedByName = calculateMappedByName(reference);
		return mappedBy;
	}

	/**
	 * Returns the expected mapping type for the given {@link EReference}.
	 * We can return Many-To-Many for all non-containments, because One-To-Many or One-To-One are handles with 
	 * join-tables / mapping-tables.
	 * For containments we can distinguish between One-To-One or One-To-Many
	 * @param reference the {@link EReference}
	 * @return the {@link MappingType}
	 */
	private MappingType getMappingType(EReference reference) {
		requireNonNull(reference);
		return reference.isContainment() ? 
				(reference.isMany() ? MappingType.ONE_TO_MANY : MappingType.ONE_TO_ONE) : 
					MappingType.MANY_TO_MANY;
	}
	
	public BaseRef createMapping(EReference reference) {
		requireNonNull(reference);
		MappingType mappingType = getMappingType(reference);
		BaseRef mapping = switch (mappingType) {
			case MANY_TO_MANY -> {
				yield EORMFactory.eINSTANCE.createManyToMany();
			}
			case MANY_TO_ONE -> {
				yield EORMFactory.eINSTANCE.createManyToOne();
			}
			case ONE_TO_ONE -> {
				yield EORMFactory.eINSTANCE.createOneToOne();
			}
			case ONE_TO_MANY -> {
				yield EORMFactory.eINSTANCE.createOneToMany();
			}
		};
		MappingHelper.createBaseRef(mapping, reference);
		return mapping;
	}

	/**
	 * Returns the expected mapping type for the given {@link EReference} in
	 * respect to the opposite, if there is one.
	 * @param reference the {@link EReference}
	 * @return the {@link MappingType}
	 */
	private MappingType calculateMappingType(EReference reference) {
		requireNonNull(reference);
		MappingType mappingType = getMappingType(reference);
		// early break
		if (isNull(reference.getEOpposite()) || reference.isContainment()) {
			return mappingType;
		}
		EReference oppositeRef = reference.getEOpposite();
		// are we in an children relationship ? 
		boolean isChild = oppositeRef.isContainment(); 
		MappingType oppositeMappingType = getMappingType(oppositeRef);
		/* 
		 * Opposite reference is a containment reference. Only in a parent-child relationship
		 * Many-To-One should be used. For containments the relations are equal and have no 
		 * parent-child status.
		 */
		if (isChild) {
			if (reference.isMany()) {
				throw new IllegalStateException(String.format("The opposite of this reference is a containment. So we are their child and so cannot have a many reference and therefore many parents. [%s]", oppositeMappingType));
			} else {
				return switch (oppositeMappingType) {
					case ONE_TO_MANY -> {
						yield MappingType.MANY_TO_ONE;
					}
					case ONE_TO_ONE -> {
						yield MappingType.ONE_TO_ONE;
					}
					default -> throw new IllegalStateException(String.format("The opposite of this reference is a containment. So we are their child, which cannot end up with having opposite mapping type: '%s'", oppositeMappingType));
				};
			}
		} else {
			/*
			 * Non-containment bidirectional: determine type directly from cardinality
			 * of both sides. getMappingType() returns MANY_TO_MANY for all non-containment
			 * references regardless of cardinality, so we check isMany() directly.
			 */
			if (reference.isMany()) {
				return oppositeRef.isMany() ? MappingType.MANY_TO_MANY : MappingType.ONE_TO_MANY;
			} else {
				return oppositeRef.isMany() ? MappingType.MANY_TO_ONE : MappingType.ONE_TO_ONE;
			}
		}
	}

	public boolean containsOpposite(EReference oppositeRef) {
		return oppositeRefMap.containsValue(oppositeRef);
	}
	
	public Collection<EReference> getOppositeReferences() {
		return Collections.unmodifiableCollection(oppositeRefMap.values());
	}
	
	public boolean containsMapping(EReference reference) {
		return refMappingMap.containsKey(reference);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BaseRef> T getMapping(EReference reference) {
		return (T) refMappingMap.get(reference);
	}

	public Entity getEntity(EClassifier eClassifier) {
		return entityMap.get(eClassifier);
	}

	public boolean containsEntity(EClassifier eClassifier) {
		return entityMap.containsKey(eClassifier);
	}

	public void putEntity(EClassifier eClassifier, Entity entity) {
		entityMap.put(eClassifier, entity);
	}

}
