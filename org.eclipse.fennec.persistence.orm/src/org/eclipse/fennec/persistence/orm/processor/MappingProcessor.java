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
package org.eclipse.fennec.persistence.orm.processor;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.Keywords;
import org.eclipse.fennec.persistence.eorm.Attributes;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.ENamedBase;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.Version;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.orm.helper.EORMHelper;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;
import org.eclipse.fennec.persistence.processor.Processor;
import org.eclipse.fennec.persistence.processor.ProcessorImpl;

/**
 * 
 * @author mark
 * @since 30.12.2024
 */
public class MappingProcessor extends ProcessorImpl<MappingContext, EntityMappings, List<EClass>> {

	private static final Logger LOG = Logger.getLogger(MappingProcessor.class.getName());

	public static MappingProcessor create(EPackage ePackage) {
		requireNonNull(ePackage);
		List<EClass> eClasses = ePackage.
				getEClassifiers().
				stream().
				filter(EClass.class::isInstance).
				map(EClass.class::cast).
				toList();
		return create(eClasses);
	}

	public static MappingProcessor create(EClass eClass) {
		return create(Collections.singletonList(eClass));
	}

	public static MappingProcessor create(List<EClass> eClasses) {
		return new MappingProcessor(eClasses);
	}
	
	public static MappingProcessor createStrict(List<EClass> eClasses) {
		MappingProcessor processor =  new MappingProcessor(eClasses);
		processor.setStrict(true);
		return processor;
	}

	/**
	 * Creates a new instance.
	 * @param source
	 */
	MappingProcessor(List<EClass> source) {
		super(source, new MappingContext());
	}

	/**
	 * Creates a new instance.
	 * @param source
	 * @param context
	 */
	MappingProcessor(List<EClass> source, MappingContext context) {
		super(source, context);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#canProcess()
	 */
	@Override
	public boolean canProcess() {
		return super.canProcess();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#createTarget()
	 */
	@Override
	protected EntityMappings createTarget() {
		return EORMFactory.eINSTANCE.createEntityMappings();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#doProcess()
	 */
	@Override
	protected void doProcess() {
		if (source.isEmpty()) {
			return;
		}
		@SuppressWarnings("unchecked")
		List<EClass> eClasses = (List<EClass>)(List<?>) new ArrayList<>(source);
		context.setAllEClasses(eClasses);
		Collection<EPackage> ePackages = EORMHelper.getEPackages(eClasses);
		if (ePackages.size() == 1) {
			EPackage ePackage = ePackages.iterator().next();
			target.setName(ePackage.getName());
			target.setPackage(ePackage.getNsURI());
			target.setSchema(ePackage.getName().toUpperCase());
		}
		/*
		 * Stage 1: map all entities with id and EAttributes
		 */
		eClasses.stream().map(this::createEntityProcessor).map(Processor::process).map(Processor::getTarget).forEach(target.getEntity()::add);
		/*
		 * Stage 2: map all EAttributes
		 */
		eClasses.stream().map(context::getEntity).forEach(this::mapAttributes);
		/*
		 * Stage 3: map containment EReferences
		 *  - with bi-directional mappings, references can also just be one-to-one or many-to-one, 
		 *    so the bidi / EOpposite handling is done by EMF
		 */
		eClasses.stream().
		map(context::getEntity).
		filter(Objects::nonNull).
		forEach(this::mapContainmentReferences);
		/*
		 * Stage 4: map non-containment EReferences
		 *  - uni-directional first
		 */
		eClasses.stream().
		map(context::getEntity).
		filter(Objects::nonNull).
		forEach(this::mapNonContainmentReferences);
		/*
		 * Stage 5: assign bi-directional references
		 * We take only references that already have been identified and cached
		 */
		List<EReference> opposites = new ArrayList<>(context.getOppositeReferences());
		opposites.
		stream().
		filter(not(EStructuralFeature::isTransient)).
		filter(not(EStructuralFeature::isDerived)).
		filter(not(EStructuralFeature::isVolatile)).
		forEach(this::createOppositeMapping);
	}

	/**
	 * Mapping used in Stage 2
	 * Maps the {@link EAttribute}s of the given {@link EClass}
	 * @param eClass the {@link EClass}
	 * @param attrs the {@link Attributes} holder
	 */
	private void mapAttributes(Entity entity) {
		requireNonNull(entity);
		EClass eClass = (EClass) entity.getClass_();
		List<EAttribute> attributes = getEffectiveAttributes(eClass).
				stream().
				filter(not(EStructuralFeature::isTransient)).
				filter(not(EStructuralFeature::isDerived)).
				filter(not(EStructuralFeature::isVolatile)).
				toList();
		// map and set single valued attributes (excluding version attributes)
		attributes.
		stream().
		filter(not(EStructuralFeature::isMany)).
		filter(not(this::isVersionAttribute)).
		map(this::createBasicProcessor).
		forEach(EFeatureProcessor::process);
		// map version attributes
		attributes.
		stream().
		filter(not(EStructuralFeature::isMany)).
		filter(this::isVersionAttribute).
		forEach(attr -> createVersionMapping(entity, attr));
		// map and set many valued attributes
		attributes.
		stream().
		filter(EStructuralFeature::isMany).
		map(this::createECProcessor).
		forEach(EFeatureProcessor::process);
	}

	/**
	 * Mapping used in Stage 3
	 * Containment:
	 * - One-To-One / One-to-Many: One Person Has Many Skills, a Skill is not reused between Person(s)
	 * 		- Unidirectional: A Person can directly reference Skills via its Set
	 * - Many-To-One or Many-To-Many is not possible in containments, because of the parent-child relation.
	 * @param entity the {@link Entity}
	 */
	private void mapContainmentReferences(Entity entity) {
		requireNonNull(entity);
		EClass eClass = (EClass) entity.getClass_();
		// only containment references
		List<EReference> references = getEffectiveReferences(eClass).
				stream().
				filter(not(EStructuralFeature::isTransient)).
				filter(not(EStructuralFeature::isDerived)).
				filter(not(EStructuralFeature::isVolatile)).
				filter(EReference::isContainment).
				toList();
		// map and set one to one containment references unidirectional 
		references.
		stream().
		filter(not(EStructuralFeature::isMany)).
		map(this::createO2OProcessor).
		forEach(Processor::process);
		// map and set one to many containment references unidirectional
		references.
		stream().
		filter(EStructuralFeature::isMany).
		map(this::createO2MProcessor).
		forEach(Processor::process);
	}

	/**
	 * Mapping used in Stage 4
	 * Non-containment:
	 * - Many-to-Many: One Person Has Many Skills, a Skill is reused between Person(s)
	 * 		- Unidirectional: A Person can directly reference Skills via its Set
	 * @param entity the {@link Entity}
	 */
	private void mapNonContainmentReferences(Entity entity) {
		requireNonNull(entity);
		EClass eClass = (EClass) entity.getClass_();
		// only non-containment references
		List<EReference> references = getEffectiveReferences(eClass).
				stream().
				filter(not(EStructuralFeature::isTransient)).
				filter(not(EStructuralFeature::isDerived)).
				filter(not(EStructuralFeature::isVolatile)).
				filter(not(EReference::isContainment)).
				toList();
		// map and set one to one non-containment references unidirectional
		references.
		stream().
		filter(not(EStructuralFeature::isMany)).
		map(this::createO2OProcessor).
		forEach(Processor::process);
		// map and set one to many non-containment references unidirectional
		references.
		stream().
		filter(EStructuralFeature::isMany).
		map(this::createO2MProcessor).
		forEach(Processor::process);
		// map and set many to one non-containment references unidirectional
		references.
		stream().
		filter(not(EStructuralFeature::isMany)).
		filter(MappingHelper::isOppositeRelation).
		map(this::createM2OProcessor).
		forEach(Processor::process);
		// map and set many to many non-containment references unidirectional
		references.
		stream().
		filter(EStructuralFeature::isMany).
		map(this::createM2MProcessor).
		forEach(Processor::process);
	}

	/**
	 * Mapping used in Stage 5
	 * Containment:
	 * - One-To-One / One-to-Many: One Person Has Many Skills, a Skill is not reused between Person(s)
	 * 		- Bidirectional: Each "child" Skill has a single pointer back up to the Person (which is not shown in your code)
	 * Non-containment:
	 * - Many-to-Many: One Person Has Many Skills, a Skill is reused between Person(s)
	 * 		- Bidirectional: A Skill has a Set of Person(s) which relate to it.
	 * @param entity the {@link Entity}
	 */
	private BaseRef createOppositeMapping(EReference reference) {

		requireNonNull(reference);
		requireNonNull(reference.getEOpposite());
		EReference opposite = reference.getEOpposite();
		if (opposite.isContainment()) {
			if (reference.isMany()) {
				LOG.log(Level.SEVERE, "Cannot create opposite mapping: many-valued reference with containment opposite is not possible for {0}", reference.getName());
				return null;
			}
			// we are non containment and unary children
			if (opposite.isMany()) {
				/*
				 * opposite / parent is one-to-many
				 * we should map many-to-one
				 */
				return createM2OProcessor(reference).withOppositeMapping().process().getMapping();
			} else {
				return createO2OProcessor(reference).withOppositeMapping().process().getMapping();
			} 
		} else {
			if (reference.isMany()) {
				if (opposite.isMany()) {
					return createM2MProcessor(reference).withOppositeMapping().process().getMapping();
				} else {
					return createO2MProcessor(reference).withOppositeMapping().process().getMapping();
				}
			} else {
				if (opposite.isMany()) {
					return createM2OProcessor(reference).withOppositeMapping().process().getMapping();
				} else {
					return createO2OProcessor(reference).withOppositeMapping().process().getMapping();
				}
			}
		}
	}

	private EntityProcessor createEntityProcessor(EClass eClass) {
		EntityProcessor processor = new EntityProcessor(eClass, context);
		processor.setStrict(isStrict());
		return processor;
	}

	private BasicProcessor createBasicProcessor(EAttribute attribute) {
		return createProcessor(attribute, BasicProcessor.class);
	}

	private ElementCollectionProcessor createECProcessor(EAttribute attribute) {
		return createProcessor(attribute, ElementCollectionProcessor.class);
	}

	/**
	 * Checks whether an EAttribute is marked as a @Version attribute via EAnnotation.
	 * The annotation source is {@link Keywords#PERSISTENCE_ANNOTATION_SOURCE} with key "version" = "true".
	 */
	private boolean isVersionAttribute(EAttribute attr) {
		return Optional.ofNullable(attr.getEAnnotation(Keywords.PERSISTENCE_ANNOTATION_SOURCE))
				.map(a -> a.getDetails().get("version"))
				.map("true"::equalsIgnoreCase)
				.orElse(false);
	}

	/**
	 * Creates a Version mapping for the given attribute and adds it to the entity.
	 */
	private void createVersionMapping(Entity entity, EAttribute attr) {
		Version version = EORMFactory.eINSTANCE.createVersion();
		version.setName(attr.getName());
		Column column = EORMFactory.eINSTANCE.createColumn();
		column.setName(attr.getName().toUpperCase());
		version.setColumn(column);
		entity.getAttributes().getVersion().add(version);
	}

	private OneToOneProcessor createO2OProcessor(EReference reference) {
		return createProcessor(reference, OneToOneProcessor.class);
	}

	private OneToManyProcessor createO2MProcessor(EReference reference) {
		return createProcessor(reference, OneToManyProcessor.class);
	}

	private ManyToManyProcessor createM2MProcessor(EReference reference) {
		return createProcessor(reference, ManyToManyProcessor.class);
	}
	
	private ManyToOneProcessor createM2OProcessor(EReference reference) {
		return createProcessor(reference, ManyToOneProcessor.class);
	}

	private <P extends ProcessorImpl<MappingContext, T, S>, T extends ENamedBase, S extends EObject> P createProcessor(S source, Class<P> processorType) {
		requireNonNull(source);
		requireNonNull(context);
		requireNonNull(processorType);
		try {
			Class<?>[] interfaces = source.getClass().getInterfaces();
			Constructor<P> constructor = processorType.getDeclaredConstructor(interfaces[0], MappingContext.class);
			requireNonNull(constructor);
			P processor = constructor.newInstance(source, context);
			if (isStrict()) {
				processor.setStrict(true);
			}
			return processor;
		} catch (Exception e) {
			throw new IllegalStateException("Error creating processor", e);
		}
	}

	/**
	 * Returns the effective attributes for an EClass considering inheritance.
	 * For child entities (with super type), returns only locally declared attributes.
	 * For root/standalone entities, returns all attributes (including inherited).
	 */
	private List<EAttribute> getEffectiveAttributes(EClass eClass) {
		if (hasMappedSuperType(eClass)) {
			return eClass.getEAttributes();
		}
		return eClass.getEAllAttributes();
	}

	/**
	 * Returns the effective references for an EClass considering inheritance.
	 * For child entities (with mapped super type), returns only locally declared references.
	 * For root/standalone entities, returns all references (including inherited).
	 */
	private List<EReference> getEffectiveReferences(EClass eClass) {
		if (hasMappedSuperType(eClass)) {
			return eClass.getEReferences();
		}
		return eClass.getEAllReferences();
	}

	/**
	 * Checks whether the EClass has a super type that is also being mapped.
	 */
	private boolean hasMappedSuperType(EClass eClass) {
		return eClass.getESuperTypes().stream().anyMatch(context.getAllEClasses()::contains);
	}

}
