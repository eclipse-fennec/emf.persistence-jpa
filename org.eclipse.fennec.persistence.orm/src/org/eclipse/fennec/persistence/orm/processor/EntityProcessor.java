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

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eorm.AccessType;
import org.eclipse.fennec.persistence.eorm.Attributes;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.EClassObject;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.GeneratedValue;
import org.eclipse.fennec.persistence.eorm.GenerationType;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.eorm.SequenceGenerator;
import org.eclipse.fennec.persistence.eorm.Table;
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
		if (source.isAbstract()) {
			return false;
		}
		return !context.containsEntity(source);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#doProcess()
	 */
	protected void doProcess() {
		EClassObject eco = EORMFactory.eINSTANCE.createEClassObject();
		eco.setEclass(source);
		String className = source.getInstanceClassName();
		if (isNull(className)) {
			className = source.getEPackage().getName() + "." + source.getName();
		}
		eco.setName(className);
		target.setAccessibleObject(eco);
		target.setClass(source);
		target.setAccess(AccessType.FIELD);
		String name = MappingHelper.checkReservedName(source.getName(), "Entity");
		target.setName(name);
		String documentation = EcoreUtil.getAnnotation(source, "http://www.eclipse.org/emf/2002/GenModel", "documentation");
		if (nonNull(documentation)) {
			target.setDescription(documentation);
		}
		Table table = EORMFactory.eINSTANCE.createTable();
		String altName = EcoreUtil.getAnnotation(source, "http:///org/eclipse/emf/ecore/util/ExtendedMetaData", "name");
		if (nonNull(altName)) {
			name = altName;
		}
		//		table.setSchema(eClass.getEPackage().getName().toUpperCase());
		table.setName(name.toUpperCase());
		target.setTable(table);
		Attributes attrs = EORMFactory.eINSTANCE.createAttributes();
		target.setAttributes(attrs);
		// set id field(s), including composite ID support
		createIds().forEach(attrs.getId()::add);
		// store entity in a map
		context.putEntity(source, target);
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
		MappingHelper.createBase(id, eidAttribute, isStrict());
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

}
