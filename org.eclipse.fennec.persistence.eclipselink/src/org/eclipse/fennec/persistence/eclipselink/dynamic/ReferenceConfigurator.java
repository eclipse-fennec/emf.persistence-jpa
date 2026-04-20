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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eclipselink.indirection.EBasicIndirectionPolicy;
import org.eclipse.fennec.persistence.eclipselink.mappings.EReferenceAccessor;
import org.eclipse.fennec.persistence.eorm.Attributes;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.CascadeType;
import org.eclipse.fennec.persistence.eorm.EFeatureObject;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.ForeignKey;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.JoinTable;
import org.eclipse.fennec.persistence.eorm.ManyToMany;
import org.eclipse.fennec.persistence.eorm.ManyToOne;
import org.eclipse.fennec.persistence.eorm.MappedByRef;
import org.eclipse.fennec.persistence.eorm.OneToMany;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.UnidirectionalOneToManyMapping;

/**
 * Configures relationship mappings (OneToOne, OneToMany, ManyToOne, ManyToMany)
 * on EclipseLink descriptors. Handles cascade, indirection, join tables,
 * join columns, and bidirectional mappedBy references.
 *
 * @author Mark Hoffmann
 * @since 15.04.2026
 */
class ReferenceConfigurator {

	private static final Logger LOG = Logger.getLogger(ReferenceConfigurator.class.getName());

	private final BuilderOperations ops;
	private final EDynamicTypeContext context;

	ReferenceConfigurator(BuilderOperations ops, EDynamicTypeContext context) {
		this.ops = ops;
		this.context = context;
	}

	/**
	 * Configures non-mappedBy references (owning side of relationships).
	 */
	void configureReferences() {
		Attributes attrs = getAttributes();
		attrs.getManyToOne().stream().filter(not(this::isMappedBy)).forEach(this::processManyToOne);
		attrs.getOneToOne().stream().filter(not(this::isMappedBy)).forEach(this::processOneToOne);
		attrs.getOneToMany().stream().filter(not(this::isMappedBy)).forEach(this::processOneToMany);
		attrs.getManyToMany().stream().filter(not(this::isMappedBy)).forEach(this::processManyToMany);
	}

	/**
	 * Configures mappedBy references (inverse side of relationships).
	 */
	void configureMappedByReferences() {
		Attributes attrs = getAttributes();
		attrs.getOneToOne().stream().filter(this::isMappedBy).forEach(this::processOneToOne);
		attrs.getOneToMany().stream().filter(this::isMappedBy).forEach(this::processOneToMany);
		attrs.getManyToMany().stream().filter(this::isMappedBy).forEach(this::processManyToMany);
	}

	private boolean isMappedBy(BaseRef ref) {
		return ref instanceof MappedByRef mbr &&
				Objects.nonNull(mbr.getMappedBy());
	}

	private Attributes getAttributes() {
		Entity entity = ops.getType().getEntity();
		return entity.getAttributes();
	}

	private void processOneToOne(OneToOne oneToOne) {
		EFeatureObject efo = (EFeatureObject) oneToOne.getAccessibleObject();
		String name = oneToOne.getName();
		String mappedBy = oneToOne.getMappedBy();
		EReference reference = (EReference) efo.getFeature();
		EClass refType = reference.getEReferenceType();
		EDynamicTypeBuilder refTypeBuilder = context.getETypeBuilder(refType);
		if (isNull(refTypeBuilder)) {
			LOG.log(Level.SEVERE, "No type builder available for EClass ''{0}''", refType.getName());
			return;
		}
		OneToOneMapping mapping = null;
		ForeignKey fk = oneToOne.getForeignKey();
		if (nonNull(mappedBy)) {
			mapping = new OneToOneMapping();
			mapping.setIsOneToOneRelationship(true);
			ops.addMapping(mapping);
			DatabaseMapping owningMapping = refTypeBuilder.getType().getDescriptor().getMappingForAttributeName(mappedBy);
			mapping.setAttributeName(name);
			mapping.setReferenceClass(refTypeBuilder.getType().getJavaClass());
			if (owningMapping instanceof OneToOneMapping owningO2O) {
				mapping.setSourceToTargetKeyFields(owningO2O.getTargetToSourceKeyFields());
				mapping.setTargetToSourceKeyFields(owningO2O.getSourceToTargetKeyFields());
			}
			mapping.setMappedBy(mappedBy);
		} else {
			if (nonNull(fk)) {
				mapping = ops.addOneToOneMapping(name, refTypeBuilder.getType(), fk.getName());
			}
		}
		if (isNull(mapping)) {
			return;
		}
		setOptional(mapping, oneToOne);
		setCascade(mapping, reference, oneToOne.getCascade());
		setIndirection(mapping, reference);
		setMappingDefaults(mapping, reference);
	}

	private void processOneToMany(OneToMany oneToMany) {
		EFeatureObject efo = (EFeatureObject) oneToMany.getAccessibleObject();
		String name = oneToMany.getName();
		String mappedBy = oneToMany.getMappedBy();
		EReference reference = (EReference) efo.getFeature();
		EClass refType = reference.getEReferenceType();
		EDynamicTypeBuilder refTypeBuilder = context.getETypeBuilder(refType);
		if (isNull(refTypeBuilder)) {
			LOG.log(Level.SEVERE, "No type builder available for EClass ''{0}''", refType.getName());
			return;
		}
		CollectionMapping mapping = null;
		ForeignKey fk = oneToMany.getForeignKey();
		JoinTable jt = oneToMany.getJoinTable();
		List<JoinColumn> joinColumns = oneToMany.getJoinColumn();

		if (nonNull(mappedBy)) {
			mapping = new OneToManyMapping();
			ops.addMapping(mapping);
			DatabaseMapping owningMapping = refTypeBuilder.getType().getDescriptor().getMappingForAttributeName(mappedBy);
			mapping.setAttributeName(name);
			if (owningMapping instanceof OneToOneMapping owningO2O) {
				Map<DatabaseField, DatabaseField> keys = owningO2O.getSourceToTargetKeyFields();
				for (DatabaseField fkField : keys.keySet()) {
					DatabaseField pkField = keys.get(fkField);
					mapping.addTargetForeignKeyField(fkField, pkField);
				}
			}
			mapping.setIsLazy(true);
			mapping.setMappedBy(mappedBy);
		} else {
			if (nonNull(jt)) {
				ManyToManyMapping m2mMapping = new ManyToManyMapping();
				m2mMapping.setAttributeName(name);
				ops.addMapping(m2mMapping);
				m2mMapping.setDefinedAsOneToManyMapping(true);
				mapping = createM2MJoinTable(m2mMapping, jt, refTypeBuilder.getType());
			} else if (nonNull(joinColumns) && !joinColumns.isEmpty()) {
				mapping = createOneToManyWithJoinColumns(name, refTypeBuilder.getType(), joinColumns);
			} else if (nonNull(fk)) {
				mapping = ops.addOneToManyMapping(name, refTypeBuilder.getType(), fk.getName());
			} else {
				mapping = new OneToManyMapping();
				mapping.setAttributeName(name);
				ops.addMapping(mapping);
			}
		}
		if (isNull(mapping)) {
			return;
		}
		mapping.setReferenceClass(refTypeBuilder.getType().getJavaClass());
		setOptional(mapping, oneToMany);
		setCascade(mapping, reference, oneToMany.getCascade());
		setIndirection(mapping, reference);
		setMappingDefaults(mapping, reference);
	}

	private OneToManyMapping createOneToManyWithJoinColumns(String attributeName, EDynamicType targetType, List<JoinColumn> joinColumns) {
		UnidirectionalOneToManyMapping mapping = new UnidirectionalOneToManyMapping();
		mapping.setAttributeName(attributeName);
		ops.addMapping(mapping);

		for (JoinColumn joinColumn : joinColumns) {
			String fkColumnName = joinColumn.getName();
			String pkColumnName = joinColumn.getReferencedColumnName();

			if (nonNull(pkColumnName) && pkColumnName.contains(".")) {
				pkColumnName = pkColumnName.substring(pkColumnName.indexOf(".") + 1);
			}

			if (isNull(pkColumnName) || pkColumnName.trim().isEmpty()) {
				DatabaseField primaryKeyField = ops.getType().getDescriptor().getPrimaryKeyFields().get(0);
				pkColumnName = primaryKeyField.getName();
			}

			DatabaseField sourceField = new DatabaseField(pkColumnName);
			sourceField.setTable(ops.getType().getDescriptor().getDefaultTable());

			DatabaseField targetField = new DatabaseField(fkColumnName);
			targetField.setTable(targetType.getDescriptor().getDefaultTable());

			mapping.addTargetForeignKeyField(targetField, sourceField);
		}

		return mapping;
	}

	private void processManyToOne(ManyToOne manyToOne) {
		EFeatureObject efo = (EFeatureObject) manyToOne.getAccessibleObject();
		String name = manyToOne.getName();
		EReference reference = (EReference) efo.getFeature();
		EClass refType = reference.getEReferenceType();
		EDynamicTypeBuilder refTypeBuilder = context.getETypeBuilder(refType);
		if (isNull(refTypeBuilder)) {
			LOG.log(Level.SEVERE, "No type builder available for EClass ''{0}''", refType.getName());
			return;
		}
		ManyToOneMapping mapping = new ManyToOneMapping();
		ops.addMapping(mapping);
		mapping.setAttributeName(name);
		mapping.setReferenceClass(refTypeBuilder.getType().getJavaClass());
		ForeignKey fk = manyToOne.getForeignKey();
		if (nonNull(fk)) {
			List<DatabaseField> pkFields = refTypeBuilder.getType().getDescriptor().getPrimaryKeyFields();
			DatabaseField pkField = pkFields.get(0);
			DatabaseTable pkTable = refTypeBuilder.getType().getDescriptor().getDefaultTable();
			pkField.setTableName(pkTable.getName());
			DatabaseField fkField = new DatabaseField(fk.getName());
			fkField.setNameForComparisons(fk.getName().toUpperCase());
			DatabaseTable fkTable = ops.getType().getDescriptor().getDefaultTable();
			fkField.setTable(fkTable);
			mapping.addForeignKeyField(fkField, pkField);
			mapping.setIsReadOnly(false);
		}
		setMappingDefaults(mapping, reference);
		mapping.setUsesIndirection(false);
		mapping.dontUseIndirection();
	}

	private void processManyToMany(ManyToMany manyToMany) {
		EFeatureObject efo = (EFeatureObject) manyToMany.getAccessibleObject();
		String name = manyToMany.getName();
		String mappedBy = manyToMany.getMappedBy();
		EReference reference = (EReference) efo.getFeature();
		EClass refType = reference.getEReferenceType();
		EDynamicTypeBuilder refTypeBuilder = context.getETypeBuilder(refType);
		if (isNull(refTypeBuilder)) {
			LOG.log(Level.SEVERE, "No type builder available for EClass ''{0}''", refType.getName());
			return;
		}
		ManyToManyMapping mapping = new ManyToManyMapping();
		ops.addMapping(mapping);
		mapping.setAttributeName(name);
		mapping.setReferenceClass(refTypeBuilder.getType().getJavaClass());
		JoinTable jt = manyToMany.getJoinTable();
		if (nonNull(mappedBy)) {
			ClassDescriptor owningDescriptor = refTypeBuilder.getType().getDescriptor();
			ManyToManyMapping owningMapping = (ManyToManyMapping) owningDescriptor.getMappingForAttributeName(mappedBy);
			mapping.setRelationTable(owningMapping.getRelationTable());
			mapping.setSourceKeyFields(owningMapping.getTargetKeyFields());
			mapping.setSourceRelationKeyFields(owningMapping.getTargetRelationKeyFields());
			mapping.setTargetKeyFields(owningMapping.getSourceKeyFields());
			mapping.setTargetRelationKeyFields(owningMapping.getSourceRelationKeyFields());
			mapping.setMappedBy(mappedBy);
			mapping.setIsReadOnly(true);
		} else {
			createM2MJoinTable(mapping, jt, refTypeBuilder.getType());
		}
		setOptional(mapping, manyToMany);
		setMappingDefaults(mapping, reference);
		setCascade(mapping, reference, manyToMany.getCascade());
		setIndirection(mapping, reference);
	}

	ManyToManyMapping createM2MJoinTable(ManyToManyMapping mapping, JoinTable joinTable, EDynamicType refType) {
		mapping.setRelationTableName(joinTable.getName().toUpperCase());
		for (DatabaseField sourcePK : ops.getType().getDescriptor().getPrimaryKeyFields()) {
			mapping.addSourceRelationKeyFieldName(sourcePK.getName(), sourcePK.getQualifiedName());
		}
		for (DatabaseField targetPK : refType.getDescriptor().getPrimaryKeyFields()) {
			String relField = targetPK.getName();
			if (mapping.getSourceRelationKeyFieldNames().contains(relField)) {
				relField = refType.getEClass().getName() + "_" + relField;
			}
			mapping.addTargetRelationKeyFieldName(relField, targetPK.getQualifiedName());
		}
		mapping.setShouldExtendPessimisticLockScope(true);
		return mapping;
	}

	void setMappingDefaults(ForeignReferenceMapping mapping, EReference reference) {
		mapping.setJoinFetch(ForeignReferenceMapping.NONE);
		mapping.setIsCascadeOnDeleteSetOnDatabase(false);
		mapping.setDerivesId(false);
		mapping.setIsPrivateOwned(false);
		mapping.setIsCacheable(true);
		// Containment: eager — EMF-Komposition erfordert, dass das Ziel mit dem Besitzer
		// materialisiert wird. Non-Containment: lazy — EBasicIndirectionPolicy stellt einen
		// EMF-Proxy ins Attribut, damit nur FK gelesen wird und Target-Load erst on demand
		// via ResourceSet.getEObject → JPAResourceImpl.getEObject läuft.
		mapping.setIsLazy(!reference.isContainment());
		mapping.setAttributeAccessor(EReferenceAccessor.create(reference, context));
	}

	void setIndirection(ForeignReferenceMapping mapping, EReference reference) {
		requireNonNull(reference);
		requireNonNull(mapping);
		if (reference.isContainment()) {
			mapping.dontUseIndirection();
		} else {
			mapping.setIndirectionPolicy(new EBasicIndirectionPolicy(mapping, reference, ops.getType()));
		}
	}

	void setCascade(ForeignReferenceMapping mapping, EReference reference, CascadeType cascadeType) {
		requireNonNull(mapping);
		requireNonNull(reference);
		if (isNull(cascadeType)) {
			if (reference.isContainment()) {
				mapping.setCascadeAll(true);
			}
		} else {
			if (nonNull(cascadeType.getCascadeAll())) {
				mapping.setCascadeAll(nonNull(cascadeType.getCascadeAll()));
			} else {
				mapping.setCascadeDetach(nonNull(cascadeType.getCascadeDetach()));
				mapping.setCascadeMerge(nonNull(cascadeType.getCascadeMerge()));
				mapping.setCascadePersist(nonNull(cascadeType.getCascadePersist()));
				mapping.setCascadeRefresh(nonNull(cascadeType.getCascadeRefresh()));
				mapping.setCascadeRemove(nonNull(cascadeType.getCascadeRemove()));
			}
		}
	}

	void setOptional(DatabaseMapping mapping, BaseRef baseRef) {
		requireNonNull(mapping);
		requireNonNull(baseRef);
		mapping.setIsOptional(baseRef.isOptional());
	}
}
