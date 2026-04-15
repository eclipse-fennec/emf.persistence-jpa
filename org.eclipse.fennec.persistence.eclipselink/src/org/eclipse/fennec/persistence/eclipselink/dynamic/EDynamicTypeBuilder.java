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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.persistence.eclipselink.descriptors.EClassDescriptor;
import org.eclipse.fennec.persistence.eorm.Attributes;
import org.eclipse.fennec.persistence.eorm.DiscriminatorColumn;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.Inheritance;
import org.eclipse.fennec.persistence.eorm.SecondaryTable;
import org.eclipse.fennec.persistence.eorm.Table;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sequencing.Sequence;

/**
 * Extended {@link DynamicTypeBuilder} for {@link EClass} handling.
 * Orchestrates delegate configurators for IDs, attributes, collections,
 * and references.
 *
 * @author Mark Hoffmann
 * @since 16.12.2024
 */
public class EDynamicTypeBuilder extends JPADynamicTypeBuilder implements BuilderOperations {

	private static final Logger LOG = Logger.getLogger(EDynamicTypeBuilder.class.getName());

	private final EDynamicTypeContext context;
	private final IdConfigurator idConfigurator;
	private final AttributeConfigurator attributeConfigurator;
	private final CollectionConfigurator collectionConfigurator;
	private final ReferenceConfigurator referenceConfigurator;

	/**
	 * Creates a new instance.
	 */
	public EDynamicTypeBuilder(Entity entity, Class<?> javaClass, DynamicType parentType, String[] tableNames, EDynamicTypeContext context) {
		super(javaClass, parentType);
		this.context = context;
		this.idConfigurator = new IdConfigurator(this);
		this.attributeConfigurator = new AttributeConfigurator(this, context);
		this.collectionConfigurator = new CollectionConfigurator(this, context);
		this.referenceConfigurator = new ReferenceConfigurator(this, context);

		EClassDescriptor descriptor = initClassDescriptor(entity);
		initDynamicType(descriptor);
		configureEntity(entity);
	}

	/**
	 * Creates a new instance.
	 */
	public EDynamicTypeBuilder(Entity entity, String[] tableNames, EDynamicTypeContext context) {
		this(entity, Object.class, null, tableNames, context);
	}

	/**
	 * Creates a new instance.
	 */
	public EDynamicTypeBuilder(Entity entity, EDynamicTypeContext context) {
		this(entity, Object.class, null, null, context);
	}

	@Override
	public EDynamicType getType() {
		return (EDynamicType) super.getType();
	}

	/**
	 * Configures the entity — delegates to specialized configurators.
	 */
	protected void configureEntity(Entity entity) {
		String tableName = isNull(entity.getTable()) ? entity.getName().toUpperCase() : entity.getTable().getName();
		configure(getType().getDescriptor(), tableName);

		Attributes attributes = entity.getAttributes();
		configureDatabase(getType());
		idConfigurator.configureIds(getType(), attributes.getId());
		attributeConfigurator.configureSingleAttributes(getType(), attributes.getBasic());
		attributeConfigurator.configureVersionAttributes(attributes.getVersion());
		collectionConfigurator.configureManyAttributes(getType(), attributes.getElementCollection());
	}

	/**
	 * Configures JPA inheritance on the EclipseLink descriptor.
	 * Must be called after all entities are created (so parent/child descriptors exist).
	 */
	public void configureInheritance() {
		Entity entity = getType().getEntity();
		Inheritance inheritance = entity.getInheritance();
		String discriminatorValue = entity.getDiscriminatorValue();

		if (nonNull(inheritance)) {
			// Root entity — configure InheritancePolicy with strategy + discriminator column
			InheritancePolicy ip = getType().getDescriptor().getInheritancePolicy();
			switch (inheritance.getStrategy()) {
				case SINGLETABLE -> ip.setSingleTableStrategy();
				case JOINED -> ip.setJoinedStrategy();
				case TABLEPERCLASS -> { /* TABLE_PER_CLASS — no special config needed */ }
			}
			DiscriminatorColumn dc = entity.getDiscriminatorColumn();
			if (nonNull(dc)) {
				ip.setClassIndicatorFieldName(dc.getName());
			}
			// Register root class indicator (both directions: class↔value)
			if (nonNull(discriminatorValue)) {
				ip.addClassIndicator(getType().getJavaClass(), discriminatorValue);
			}
			ip.setShouldReadSubclasses(true);
			LOG.log(Level.FINE, "Configured inheritance root: {0} strategy={1}",
					new Object[]{entity.getName(), inheritance.getStrategy()});
		} else if (nonNull(discriminatorValue)) {
			// Child entity — set parent class + register indicator on parent
			EClass eClass = (EClass) entity.getClass_();
			if (!eClass.getESuperTypes().isEmpty()) {
				EClass parentEClass = eClass.getESuperTypes().get(0);
				EDynamicTypeBuilder parentBuilder = context.getETypeBuilder(parentEClass);
				if (nonNull(parentBuilder)) {
					getType().getDescriptor().getInheritancePolicy()
						.setParentClass(parentBuilder.getType().getJavaClass());
					// Register this child's class indicator on the parent (both directions)
					parentBuilder.getType().getDescriptor().getInheritancePolicy()
						.addClassIndicator(getType().getJavaClass(), discriminatorValue);
					LOG.log(Level.FINE, "Configured inheritance child: {0} parent={1} discriminator={2}",
							new Object[]{entity.getName(), parentEClass.getName(), discriminatorValue});
				}
			}
		}
	}

	/**
	 * Configures primary and secondary database tables.
	 */
	public void configureDatabase(EDynamicType eDynamicType) {
		requireNonNull(eDynamicType);
		EClass eClassifier = eDynamicType.getEClass();
		Entity entity = eDynamicType.getEntity();
		ClassDescriptor classDescriptor = eDynamicType.getDescriptor();

		DatabaseTable primaryDatabaseTable = new DatabaseTable();
		if (nonNull(entity.getTable())) {
			Table table = entity.getTable();
			if (nonNull(table.getName())) {
				primaryDatabaseTable.setName(table.getName());
			}
			if (nonNull(table.getSchema())) {
				primaryDatabaseTable.setTableQualifier(table.getSchema());
			}
		} else {
			primaryDatabaseTable.setName(eClassifier.getName());
		}

		List<DatabaseTable> secondaryDatabaseTables = new ArrayList<>();
		if (!entity.getSecondaryTable().isEmpty()) {
			EList<SecondaryTable> secondaryTables = entity.getSecondaryTable();
			for (SecondaryTable secondaryTable : secondaryTables) {
				DatabaseTable secondaryDatabaseTable = new DatabaseTable();
				secondaryDatabaseTable.setName(secondaryTable.getName());
				String schema = secondaryTable.getSchema();
				if (nonNull(schema)) {
					secondaryDatabaseTable.setTableQualifier(schema);
					secondaryDatabaseTables.add(secondaryDatabaseTable);
				}
			}
		}
		classDescriptor.setDefaultTable(primaryDatabaseTable);
		secondaryDatabaseTables.forEach(classDescriptor::addTable);
	}

	public void configureReferences() {
		referenceConfigurator.configureReferences();
	}

	public void configureMappedByReferences() {
		referenceConfigurator.configureMappedByReferences();
	}

	// ── Initialization ───────────────────────────────────────────────────

	private EClassDescriptor initClassDescriptor(Entity entity) {
		requireNonNull(entity, "An Entity is required to create a EClassDescriptor");
		EClassDescriptor descriptor = new EClassDescriptor(entity);
		descriptor.convertClassNamesToClasses(context.getClassloader());
		return descriptor;
	}

	private void initDynamicType(EClassDescriptor descriptor) {
		EDynamicTypeImpl edti = new EDynamicTypeImpl(descriptor, null, context);
		edti.setDynamicPropertiesManager(this.entityType.getDynamicPropertiesManager());
		this.entityType = edti;
	}

	// ── BuilderOperations (delegate to inherited JPADynamicTypeBuilder) ──

	@Override
	public DirectToFieldMapping addDirectMapping(String name, Class<?> type, String columnName) {
		return super.addDirectMapping(name, type, columnName);
	}

	@Override
	public DatabaseMapping addMapping(DatabaseMapping mapping) {
		return super.addMapping(mapping);
	}

	@Override
	public OneToOneMapping addOneToOneMapping(String name, DynamicType targetType, String fkName) {
		return super.addOneToOneMapping(name, targetType, fkName);
	}

	@Override
	public CollectionMapping addOneToManyMapping(String name, DynamicType targetType, String fkName) {
		return super.addOneToManyMapping(name, targetType, fkName);
	}

	@Override
	public DirectCollectionMapping addDirectCollectionMapping(String name, String tableName,
			String valueColumn, Class<?> typeClass, String foreignKeyName) {
		return super.addDirectCollectionMapping(name, tableName, valueColumn, typeClass, foreignKeyName);
	}

	@Override
	public void setPrimaryKeyFields(String... fieldNames) {
		super.setPrimaryKeyFields(fieldNames);
	}

	@Override
	public void configureSequencing(String seqName, String fieldName) {
		super.configureSequencing(seqName, fieldName);
	}

	@Override
	public void configureSequencing(Sequence sequence, String seqName, String fieldName) {
		super.configureSequencing(sequence, seqName, fieldName);
	}
}
