/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.persistence.orm;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.helper.EORMHelper;
import org.eclipse.fennec.persistence.orm.processor.BasicProcessor;
import org.eclipse.fennec.persistence.orm.processor.ElementCollectionProcessor;
import org.eclipse.fennec.persistence.orm.processor.EntityProcessor;
import org.eclipse.fennec.persistence.orm.processor.ManyToManyProcessor;
import org.eclipse.fennec.persistence.orm.processor.ManyToOneProcessor;
import org.eclipse.fennec.persistence.orm.processor.MappingProcessor;
import org.eclipse.fennec.persistence.orm.processor.OneToManyProcessor;
import org.eclipse.fennec.persistence.orm.processor.OneToOneProcessor;

/**
 * Helper class to create default {@link EntityMappings} out of an {@link EPackage} or a {@link List}
 * of {@link EClassifier}
 * @author Mark Hoffmann
 * @since 13.12.2024
 */
public class EntityMapper {

	private final MappingContext context = new MappingContext();
	private boolean strict = false;
	
	/**
	 * Sets the strict-mapping mode. This means the mapping will not guess anything or
	 * generate column names. Instead it takes the given model as it is.
	 * @param strict the strict to set
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}
	
	/**
	 * Returns the strict.
	 * @return the strict
	 */
	public boolean isStrict() {
		return strict;
	}

	/**
	 * Creates an {@link EntityMappings} out of an {@link EPackage}.
	 * The {@link EntityMappings} instance might be empty, if no valid {@link EClass} 
	 * have been found within the {@link EPackage}.
	 * @param ePackage the {@link EPackage}
	 * @return the {@link EntityMappings}
	 */
	public EntityMappings createMappingsFromEPackage(EPackage ePackage) {
		EntityMappings mapping = EORMFactory.eINSTANCE.createEntityMappings();
		if (isNull(ePackage)) {
			return mapping;
		}
		return createMappings(ePackage.getEClassifiers());
	}
	
	/**
	 * Creates an {@link EntityMappings} out of an {@link EClass}.
	 * The {@link EntityMappings} instance might be empty, if no valid {@link EClass} is provided
	 * @param eClass the {@link EClass}
	 * @return the {@link EntityMappings}
	 */
	public EntityMappings createMappingsFromEClass(EClass eClass) {
		EntityMappings mapping = EORMFactory.eINSTANCE.createEntityMappings();
		if (isNull(eClass)) {
			return mapping;
		}
		return createMappings(Collections.singletonList(eClass));
	}

	/**
	 * Creates an {@link EntityMappings} out of a {@link EClassifier} list.
	 * The {@link EntityMappings} instance might be empty, if no valid {@link EClass} 
	 * have been found within the {@link EClassifier} list.
	 * Mapping of {@link EClassifier}s is done like this
	 * 
	 * Entity mapping:
	 *  - Map {@link EClass} and Id's  (Stage 1)
	 * 
	 * Attribute mapping:
	 * 	- Map single and multi-valued {@link EAttribute} (Stage 2)
	 * 
	 * Reference Mapping:
	 *  - Containment: (Stage 3)
	 *		- One-To-One: One Person has a Skill, a Skill is not reused between Person(s). This is configured, having the Join-Column on the 
	 *		  container side. This means a Person table gets a join-column that points to the Skill id.
	 *		- One-to-Many: One Person Has Many Skills, a Skill is not reused between Person(s). A Person can directly reference Skills via 
	 *		  its List. This is configured using a join-column on the containing type. This means that the Skill table has a join-column
	 *		  that points to the conainer, the Person. 
	 * 		- Many-To-Many is not possible with containments, because containing instance can only have one container
	 * 		- Bi-Directionality: Each "child" Skill has a single pointer back up to the Person. 
	 * 		  This bi-directional handling is completely done by EMF! 
	 *	- Non-containment: (Stage 4)
	 *		- One-To-One: One Person has a Skill, a Skill can be re-used between Person(s). This is configured using a join-table. This should
	 *		  hold the id's of the related Person and Skill.
	 *		- One-To-Many: One Person has many Skills, these Skills can be re-used between Person(s). This is configured using a join-table. This should
	 *		  hold the id's of the related Person and Skills. 
	 *		- Many-to-Many: Many Persons can have many Skills, a Skill is reused between Person(s). This is configured using a join-table. This should
	 *		- EOpposite references are identified are stored for later handling in Stage 5 and ignored to avoid duplicate mappings
	 * 	- Bidirectional Mappings: (Stage 5): 
	 * 		- Only non-containment EOpposites are taken into account, where both sides are non-containment references. These have been identified in Stage 4
	 * 		- Only the non-mapped sides are now mapped.
	 * 		- The configuration is done using the 'mappedBy' feature, that points to the already existing mappings from Stage 4  
	 * @param classifier the {@link EClassifier} list
	 * @return the {@link EntityMappings}
	 */
	public EntityMappings createMappings(List<EClassifier> classifier) {
		EntityMappings mapping = EORMFactory.eINSTANCE.createEntityMappings();
		if (isNull(classifier) || classifier.isEmpty()) {
			return mapping;
		}
		Collection<EClass> eClasses = EORMHelper.filterEClasses(classifier);
		MappingProcessor processor = isStrict() ? MappingProcessor.createStrict(new ArrayList<>(eClasses)) : MappingProcessor.create(new ArrayList<>(eClasses));
		processor.process();
		return processor.getTarget();
	}
	
	EntityProcessor createEntityProcessor(EClass eClass) {
		return new EntityProcessor(eClass, context);
	}
	
	BasicProcessor createBasicProcessor(EAttribute attribute) {
		return new BasicProcessor(attribute, context);
	}
	
	ElementCollectionProcessor createElementCollectionProcessor(EAttribute attribute) {
		return new ElementCollectionProcessor(attribute, context);
	}
	
	OneToOneProcessor createOneToOneProcessor(EReference reference) {
		return new OneToOneProcessor(reference, context);
	}
	
	OneToManyProcessor createOneToManyProcessor(EReference reference) {
		return new OneToManyProcessor(reference, context);
	}
	
	ManyToManyProcessor createManyToManyProcessor(EReference reference) {
		return new ManyToManyProcessor(reference, context);
	}
	
	ManyToOneProcessor createManyToOneProcessor(EReference reference) {
		return new ManyToOneProcessor(reference, context);
	}

}
