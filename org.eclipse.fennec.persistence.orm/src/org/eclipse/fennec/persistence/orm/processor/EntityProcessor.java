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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.helper.EMaps;
import org.eclipse.fennec.persistence.eorm.AccessType;
import org.eclipse.fennec.persistence.eorm.Attributes;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.DiscriminatorColumn;
import org.eclipse.fennec.persistence.eorm.DiscriminatorType;
import org.eclipse.fennec.persistence.eorm.EClassObject;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.GeneratedValue;
import org.eclipse.fennec.persistence.eorm.GenerationType;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.eorm.Inheritance;
import org.eclipse.fennec.persistence.eorm.InheritanceType;
import org.eclipse.fennec.persistence.eorm.SequenceGenerator;
import org.eclipse.fennec.persistence.eorm.Table;
import org.eclipse.fennec.persistence.Keywords;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;
import org.eclipse.fennec.persistence.processor.ProcessorImpl;

/**
 * Processor for {@link Entity} mapping
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public class EntityProcessor extends ProcessorImpl<MappingContext, Entity, EClass>{

	public static EntityProcessor create(EClass eClass) {
		requireNonNull(eClass);
		return new EntityProcessor(eClass, new MappingContext());
	}

	/**
	 * Creates a new instance.
	 */
	public EntityProcessor(EClass eClass, MappingContext context) {
		super(eClass, context);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#createTarget()
	 */
	@Override
	protected Entity createTarget() {
		return EORMFactory.eINSTANCE.createEntity();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#canProcess()
	 */
	@Override
	public boolean canProcess() {
		return !context.containsEntity(source);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#doProcess()
	 */
	protected void doProcess() {
		EClassObject eco = EORMFactory.eINSTANCE.createEClassObject();
		eco.setEclass(source);
		// The instance class name is the entity class only when it names a real generated
		// type. A map entry class carries "java.util.Map$Entry" (issue #183) — required, or
		// EMF hands out a list instead of an EMap — and taking it literally would name every
		// entry class in the unit identically and point at an interface. Those get the
		// model-derived name and a generated dynamic class, like any other dynamic EClass.
		String className = EMaps.isMapEntry(source) ? null : source.getInstanceClassName();
		if (isNull(className)) {
			className = source.getEPackage().getName() + "." + source.getName();
		}
		eco.setName(className);
		target.setAccessibleObject(eco);
		target.setClass(source);
		target.setAccess(AccessType.FIELD);
		String name = MappingHelper.checkReservedName(source.getName(), "Entity", context, source);
		target.setName(name);
		String documentation = EcoreUtil.getAnnotation(source, "http://www.eclipse.org/emf/2002/GenModel", "documentation");
		if (nonNull(documentation)) {
			target.setDescription(documentation);
		}
		Table table = EORMFactory.eINSTANCE.createTable();
		String tableName = name;
		if (context.isUseNamesFromExtendedMetaData()) {
			tableName = MappingHelper.getTableName(source, true);
		}
		// SINGLE_TABLE and JOINED children reuse the root entity's table name
		// (EclipseLink's setJoinedStrategy() reassigns per-subclass tables later).
		// TABLE_PER_CLASS and MappedSuperclass subclasses keep their own table
		// name — each concrete subclass owns an independent table with all
		// inherited columns.
		boolean independentTable = childOwnsTable();
		if (hasMappedSuperType() && !independentTable) {
			EClass root = getMappedRoot();
			tableName = root.getName();
		}
		table.setName(tableName.toUpperCase());
		target.setTable(table);
		reportUnusedExtendedMetaDataNames();
		Attributes attrs = EORMFactory.eINSTANCE.createAttributes();
		target.setAttributes(attrs);
		// Root entities always get IDs. SINGLE_TABLE/JOINED children inherit the
		// root's ID via EclipseLink's InheritancePolicy.initialize(). TABLE_PER_CLASS
		// and MappedSuperclass children need their own non-read-only ID mapping
		// because each subclass lives in its own table.
		if (!hasMappedSuperType() || independentTable) {
			createIds().forEach(attrs.getId()::add);
		}
		// store entity in a map
		context.putEntity(source, target);
		// configure inheritance
		configureInheritance();
	}

	/**
	 * Configures JPA inheritance on the entity based on EClass hierarchy.
	 * <ul>
	 * <li>Root of hierarchy (has sub-classes, no super-type): sets Inheritance + DiscriminatorColumn</li>
	 * <li>Child (has super-type): sets DiscriminatorValue only</li>
	 * <li>Standalone (no super-type, no sub-classes): no inheritance config</li>
	 * </ul>
	 */
	private void configureInheritance() {
		if (hasMappedSuperType() && isMappedSuperclassRoot(getMappedRoot())) {
			// MappedSuperclass child: behaves like a standalone entity — no
			// JPA-level inheritance linkage (no discriminator, no parent class).
			return;
		}
		if (hasMappedSuperType()) {
			// Child entity — set discriminator value
			target.setDiscriminatorValue(source.getName());
		} else if (hasSubClasses()) {
			// Root of hierarchy — set inheritance strategy + discriminator column
			target.setDiscriminatorValue(source.getName());
			Inheritance inheritance = EORMFactory.eINSTANCE.createInheritance();
			inheritance.setStrategy(rootInheritanceStrategy());
			target.setInheritance(inheritance);
			// Discriminator column is needed for SINGLE_TABLE and JOINED (optional for JOINED)
			if (inheritance.getStrategy() != InheritanceType.TABLEPERCLASS) {
				DiscriminatorColumn dc = EORMFactory.eINSTANCE.createDiscriminatorColumn();
				dc.setName("DTYPE");
				dc.setDiscriminatorType(DiscriminatorType.STRING);
				target.setDiscriminatorColumn(dc);
			}
		}
		// Standalone entity (no hierarchy) — nothing to configure
	}

	/**
	 * Resolves the inheritance strategy for this entity's hierarchy. The
	 * strategy is read from the {@linkplain Keywords#PERSISTENCE_ANNOTATION_SOURCE
	 * persistence EAnnotation} on the root of the hierarchy — children inherit
	 * the strategy transparently. Values: "JOINED", "TABLE_PER_CLASS",
	 * "SINGLE_TABLE" (default).
	 */
	private InheritanceType rootInheritanceStrategy() {
		EClass root = hasMappedSuperType() ? getMappedRoot() : source;
		return Optional.ofNullable(root.getEAnnotation(Keywords.PERSISTENCE_ANNOTATION_SOURCE))
				.map(a -> a.getDetails().get("inheritance"))
				.map(String::toUpperCase)
				.map(s -> switch (s) {
					case "JOINED" -> InheritanceType.JOINED;
					case "TABLE_PER_CLASS" -> InheritanceType.TABLEPERCLASS;
					default -> InheritanceType.SINGLETABLE;
				})
				.orElse(InheritanceType.SINGLETABLE);
	}

	/**
	 * Returns {@code true} when this child should get its own table (and own
	 * ID mapping) rather than sharing the root table. Both TABLE_PER_CLASS
	 * and MappedSuperclass hierarchies behave this way.
	 */
	private boolean childOwnsTable() {
		if (!hasMappedSuperType()) {
			return false;
		}
		return rootInheritanceStrategy() == InheritanceType.TABLEPERCLASS
				|| isMappedSuperclassRoot(getMappedRoot());
	}

	/**
	 * Returns {@code true} when the given root EClass carries the
	 * {@code mappedSuperclass="true"} EAnnotation.
	 */
	private boolean isMappedSuperclassRoot(EClass root) {
		return Optional.ofNullable(root.getEAnnotation(Keywords.PERSISTENCE_ANNOTATION_SOURCE))
				.map(a -> a.getDetails().get("mappedSuperclass"))
				.map("true"::equalsIgnoreCase)
				.orElse(false);
	}

	/**
	 * Checks whether any EClass in the context's source list has this EClass as a super type.
	 */
	private boolean hasSubClasses() {
		return context.getAllEClasses().stream()
				.anyMatch(ec -> ec.getESuperTypes().contains(source));
	}

	/**
	 * Checks whether this EClass has a super type that is also part of the mapping.
	 * Only super types in the allEClasses list count — external super types are ignored.
	 */
	private boolean hasMappedSuperType() {
		List<EClass> allEClasses = context.getAllEClasses();
		return source.getESuperTypes().stream().anyMatch(allEClasses::contains);
	}

	/**
	 * Finds the root of the inheritance hierarchy within the mapped EClasses.
	 */
	private EClass getMappedRoot() {
		List<EClass> allEClasses = context.getAllEClasses();
		EClass current = source;
		while (current.getESuperTypes().stream().anyMatch(allEClasses::contains)) {
			current = current.getESuperTypes().stream()
					.filter(allEClasses::contains)
					.findFirst()
					.orElse(current);
		}
		return current;
	}

	/**
	 * Returns the {@link EClass}
	 * @return the {@link EClass}
	 */
	EClass getEClass() {
		return getSource();
	}

	/**
	 * Returns the {@link Entity}
	 * @return the {@link Entity}
	 */
	Entity getEntity() {
		return getTarget();
	}

	/**
	 * Creates an Id or an empty {@link Optional}
	 * @param eClass the {@link EClass} to create the {@link Id}
	 * @return an Id or an empty {@link Optional}
	 */
	Optional<Id> createId() {
		EAttribute eidAttribute = source.getEIDAttribute();
		if (isNull(eidAttribute)) {
			Id id = EORMFactory.eINSTANCE.createId();
			id.setName("pk_" + source.getName());
			Column column = EORMFactory.eINSTANCE.createColumn();
			column.setName(id.getName().toUpperCase());
			column.setNullable(false);
			column.setUnique(true);
			column.setInsertable(true);
			column.setUpdatable(true);
			id.setColumn(column);
			if (!isStrict()) {
				SequenceGenerator seqGen = EORMFactory.eINSTANCE.createSequenceGenerator();
				seqGen.setName("SEQ_" + source.getName().toUpperCase() + "_" + column.getName() + "_ID");
				seqGen.setSequenceName(seqGen.getName());
				id.setSequenceGenerator(seqGen);
			}
			return Optional.of(id);
		}
		Class<?> idType = eidAttribute.getEAttributeType().getInstanceClass();
		Id id = EORMFactory.eINSTANCE.createId();
		MappingHelper.createBase(id, eidAttribute, isStrict(), context);
		if (!isStrict() && 
				(Number.class.isAssignableFrom(idType) || 
				Long.TYPE.isAssignableFrom(idType) || 
				Integer.TYPE.isAssignableFrom(idType))) {
			SequenceGenerator seqGen = EORMFactory.eINSTANCE.createSequenceGenerator();
			seqGen.setName("SEQ_" + source.getName().toUpperCase() + "_" + eidAttribute.getName().toUpperCase() + "_ID");
			seqGen.setSequenceName(seqGen.getName());
			//			seqGen.setSchema(eClass.getEPackage().getName().toUpperCase());
			id.setSequenceGenerator(seqGen);
		}
		if (!isStrict() && 
				String.class.isAssignableFrom(idType)) {
			SequenceGenerator seqGen = EORMFactory.eINSTANCE.createSequenceGenerator();
			seqGen.setName("SEQ_" + source.getName().toUpperCase() + "_" + eidAttribute.getName().toUpperCase() + "_ID");
			seqGen.setSequenceName(seqGen.getName());
			seqGen.setSchema(source.getEPackage().getName().toUpperCase());
			id.setSequenceGenerator(seqGen);
			GeneratedValue gv = EORMFactory.eINSTANCE.createGeneratedValue();
			gv.setStrategy(GenerationType.UUID);
			//			gv.setGenerator("UUID");
			id.setGeneratedValue(gv);
		}
		return Optional.of(id);
	}

	/**
	 * Creates ID mappings using enhanced composite ID support.
	 * 
	 * This method uses the new CompositeIdProcessor to handle:
	 * - Single ID attributes (backward compatible)
	 * - Multiple ID attributes (composite keys)
	 * - Reference-based ID classes
	 * - Synthetic ID generation
	 * 
	 * @return list of ID mappings (may contain multiple IDs for composite keys)
	 * @since 28.01.2025
	 */
	private List<Id> createIds() {
		// Use enhanced composite ID processor
		CompositeIdProcessor compositeProcessor = new CompositeIdProcessor(isStrict());
		List<Id> ids = compositeProcessor.createIds(source, context);
		
		// If no IDs were created by the composite processor, fall back to original logic
		if (ids.isEmpty()) {
			Optional<Id> fallbackId = createId();
			if (fallbackId.isPresent()) {
				return List.of(fallbackId.get());
			}
		}
		
		return ids;
	}

	/**
	 * Names an EClass's {@code ExtendedMetaData} names that the mapping leaves unused, once per
	 * EClass (issue #314). They were table and column names until the switch became opt-in — a
	 * store created before sees new columns under {@code create-or-extend-tables}, and this is
	 * where that surfaces. An info, since a model made for XML/JSON carries such names
	 * legitimately — a warning when one of them escapes a reserved word.
	 */
	private void reportUnusedExtendedMetaDataNames() {
		if (context.isUseNamesFromExtendedMetaData()) {
			return;
		}
		List<String> unused = new ArrayList<>();
		boolean escapesReservedWord = collectUnused(source.getName(),
				MappingHelper.getTableName(source, true), unused);
		for (EStructuralFeature feature : source.getEStructuralFeatures()) {
			escapesReservedWord |= collectUnused(feature.getName(),
					MappingHelper.getColumnName(feature, true), unused);
		}
		if (unused.isEmpty()) {
			return;
		}
		String message = String.format(
				"ExtendedMetaData names of '%s' are not used as table/column names: %s. "
						+ "Enable useNamesFromExtendedMetaData, or declare the names in the eorm mapping.",
				source.getName(), unused);
		// a model name that is a reserved word, annotated with one that is not, reads as the
		// escape the reserved-word warning used to recommend — without the switch the DDL fails
		if (escapesReservedWord) {
			context.warning(MappingContext.DIAGNOSTIC_SOURCE, message, source);
		} else {
			context.info(MappingContext.DIAGNOSTIC_SOURCE, message, source);
		}
	}

	/**
	 * Adds {@code name=annotated} to {@code unused} when the two differ.
	 * @return {@code true} if the annotated name escapes a reserved word
	 */
	private static boolean collectUnused(String name, String annotated, List<String> unused) {
		if (annotated.equals(name)) {
			return false;
		}
		unused.add(name + "=" + annotated);
		return MappingHelper.isReservedName(name) && !MappingHelper.isReservedName(annotated);
	}

}
