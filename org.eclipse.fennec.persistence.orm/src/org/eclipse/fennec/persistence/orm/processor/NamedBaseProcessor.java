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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.eorm.ConstraintMode;
import org.eclipse.fennec.persistence.eorm.ENamedBase;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.ForeignKey;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.JoinTable;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;
import org.eclipse.fennec.persistence.processor.ProcessorImpl;
import org.eclipse.persistence.internal.cache.Processor;

/**
 * An basic implementation for the mapping processor
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public abstract class NamedBaseProcessor<T extends ENamedBase, F extends EStructuralFeature> extends ProcessorImpl<MappingContext, T, F> implements EFeatureProcessor<T, F> {

	private static final Logger LOG = Logger.getLogger(NamedBaseProcessor.class.getName());

	/**
	 * Creates a new instance.
	 */
	public NamedBaseProcessor(F feature, MappingContext context) {
		super(feature, context);
	}
	
	/**
	 * Creates the mapping instance
	 * @return the mapping instance
	 */
	abstract T createMapping();

	/**
	 * Executes the post-processing
	 */
	abstract void doPostProcess();

	/**
	 * Add the mapping to the entity
	 * @param entity the {@link Entity} to add the mapping to
	 */
	abstract void addMappingToEntity(Entity entity);
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#createTarget()
	 */
	@Override
	protected final T createTarget() {
		return createMapping();
	}

	/**
	 * Does internal processing. This is called before doProcess.
	 * Only if it returns <code>true</code>, doProcess will be called.
	 * @return <code>true</code>, if the internal processing was successful
	 */
	protected boolean internalProcess() {
		createNamedBase();
		return true;
	}

	/**
	 * Does internal re-processing. This is called before doReProcess.
	 * Only if it returns <code>true</code>, doReProcess will be called.
	 * @return <code>true</code>, if the internal re-sprocessing was successful
	 */
	protected boolean internalReProcess() {
		return true;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.EFeatureProcessor#getFeature()
	 */
	@Override
	public F getFeature() {
		return getSource();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#process()
	 */
	@Override
	public EFeatureProcessor<T, F> process() {
		if (isProcessed()) {
			return this;
		}
		if (canProcess() && internalProcess()) {
			doProcess();
			doPostProcess();
			registerMapping();
			processed = true;
		}
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#reProcess()
	 */
	@Override
	public EFeatureProcessor<T, F> reProcess() {
		if (isProcessed()) {
			reprocess();
		}
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.EFeatureProcessor#getMapping()
	 */
	@Override
	public T getMapping() {
		return getTarget(); 
	}

	/**
	 * Register this mapping and {@link Processor} to the context.
	 * <p>
	 * Attributes declared on a non-mapped super type (e.g. {@code ENamedElement}
	 * when only {@code EReference} is mapped) are silently dropped — preserving
	 * the long-standing behaviour that the framework only materialises
	 * mappings for EClasses that were asked to be mapped.
	 * <p>
	 * Otherwise, the entity currently being iterated by the orchestrating
	 * {@code MappingProcessor} stage (see {@link MappingContext#getCurrentEntity()})
	 * takes precedence over the declaring EClass. This is essential for
	 * TABLE_PER_CLASS and MappedSuperclass hierarchies: an inherited attribute
	 * must be registered on the CHILD entity, not on the (abstract) parent
	 * that declared it — otherwise the parent accumulates duplicate mappings
	 * and the child gets none.
	 */
	void registerMapping() {
		if (isDelegate()) {
			return;
		}
		EClass declaringClass = source.getEContainingClass();
		if (isNull(declaringClass)) {
			return;
		}
		Entity currentEntity = context.getCurrentEntity();
		if (nonNull(currentEntity)) {
			// Stage 2-5 orchestration path: register on the entity currently
			// being iterated. Inherited attributes are kept when their
			// declaring EClass is part of the mapping (as Entity OR
			// MappedSuperclass root); inherited attributes from non-mapped
			// super types (e.g. ENamedElement when only EReference is
			// mapped) are silently dropped.
			if (context.getAllEClasses().contains(declaringClass)) {
				addMappingToEntity(currentEntity);
			}
			return;
		}
		// Legacy path (direct processor invocation, typically from unit tests):
		// look up the entity via the declaring class.
		Entity entity = context.getEntity(declaringClass);
		if (nonNull(entity)) {
			addMappingToEntity(entity);
		}
	}

	/**
	 * Creates a {@link ENamedBase} out of an {@link EStructuralFeature}
	 * @param <T> the base type
	 * @return the {@link ENamedBase} instance
	 */
	T createNamedBase() {
		return MappingHelper.createNamedBase(target, source, context);
	}

	String getSchema() {
		return source.getEContainingClass().getName();
	}

	String getTableName() {
		return source.getEContainingClass().getName().toUpperCase();
	}

	/**
	 * Creates a {@link JoinColumn} for the given {@link EClass}
	 * @param eClass the {@link EClass}
	 * @return the {@link JoinColumn} or <code>null</code>
	 */
	JoinColumn createJoinColumn(EClass eClass) {
		return createJoinColumn(eClass, isStrict() ? 
					MappingHelper.getFeatureName(source) : null);
	}
	
	/**
	 * Creates a {@link JoinColumn} for the given {@link EClass}
	 * @param eClass the {@link EClass}
	 * @param defaultColumnName an optional default join column name. Can be <code>null</code>
	 * @return the {@link JoinColumn} or <code>null</code>
	 */
	JoinColumn createJoinColumn(EClass eClass, String defaultColumnName) {
		if (isNull(eClass)) {
			return null;
		}
		Entity idEntity = context.getEntity(eClass);
		if (nonNull(idEntity) && !idEntity.getAttributes().getId().isEmpty()) {
			JoinColumn jc = EORMFactory.eINSTANCE.createJoinColumn();
			Id id = idEntity.getAttributes().getId().get(0);
			String defaultFKFieldName = nonNull(defaultColumnName) ? defaultColumnName : idEntity.getName().toUpperCase() + "_" + id.getName().toUpperCase();
			jc.setReferencedColumnName(idEntity.getTable().getName() + "." + id.getColumn().getName());
			jc.setName(defaultFKFieldName);
			return jc;
		}
		return null;
	}

	/**
	 * Creates a {@link JoinTable}
	 * @param reference the {@link EReference}
	 * @return the {@link JoinTable}
	 */
	JoinTable createJoinTable(EReference reference) {
		EClass c = reference.getEContainingClass();
		JoinColumn jc = createJoinColumn(c);
		JoinColumn ijc = createJoinColumn(reference.getEReferenceType());
		JoinTable jt = EORMFactory.eINSTANCE.createJoinTable();
		String joinTableName = c.getName().toUpperCase() + "__" + reference.getEReferenceType().getName().toUpperCase();
		jt.setName(joinTableName);
		if (nonNull(jc)) {
			jt.getJoinColumn().add(jc);
			jt.setForeignKey(createForeignKey(jc.getName()));
		}
		if (nonNull(ijc)) {
			jt.getInverseJoinColumn().add(ijc);
			jt.setInverseForeignKey(createForeignKey(ijc.getName()));
		}
		return jt;
	}

	/**
	 * Creates a {@link ForeignKey} out of the foreign key reference
	 * @param idRefName the reference column
	 * @return the {@link ForeignKey} or <code>null</code>
	 */
	ForeignKey createForeignKey(String idRefName) {
		if (isNull(idRefName)) {
			return null;
		}
		ForeignKey fk = EORMFactory.eINSTANCE.createForeignKey();
		fk.setConstraintMode(ConstraintMode.CONSTRAINT);
		fk.setForeignKeyDefinition(idRefName);
		fk.setName(idRefName);
		return fk;
	}

	private void reprocess() {
		if (!processed) {
			return;
		}
		try {
			if (internalProcess()) {
				doReProcess();
			}
		} catch (Exception e) {
			context.warning(MappingContext.DIAGNOSTIC_SOURCE,
					String.format("Error re-processing feature '%s', continuing with previous state: %s",
							source.getName(), e.getMessage()),
					source, e);
		}
	}

}
