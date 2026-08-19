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
import org.eclipse.fennec.persistence.eorm.InheritanceType;
import org.eclipse.fennec.persistence.eorm.SecondaryTable;
import org.eclipse.fennec.persistence.eorm.Table;
import org.eclipse.fennec.persistence.eorm.UniqueConstraint;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.TablePerClassPolicy;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.fennec.persistence.eclipselink.mappings.EOneToManyMapping;
import org.eclipse.fennec.persistence.eclipselink.mappings.EOneToOneMapping;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
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
	 * <p>
	 * SINGLE_TABLE and JOINED use {@link InheritancePolicy} with a class-indicator
	 * (discriminator) column on the root and subclass mappings registered there.
	 * TABLE_PER_CLASS uses {@link TablePerClassPolicy} — each subclass keeps its
	 * own table, there is no discriminator, and the descriptors are linked via
	 * {@code addParentDescriptor} / {@code addChildDescriptor} on that policy.
	 */
	public void configureInheritance() {
		Entity entity = getType().getEntity();
		Inheritance inheritance = entity.getInheritance();
		String discriminatorValue = entity.getDiscriminatorValue();

		if (nonNull(inheritance)) {
			// Root entity of a hierarchy
			ClassDescriptor descriptor = getType().getDescriptor();
			if (inheritance.getStrategy() == InheritanceType.TABLEPERCLASS) {
				descriptor.setTablePerClassPolicy(new TablePerClassPolicy(descriptor));
			} else {
				InheritancePolicy ip = descriptor.getInheritancePolicy();
				switch (inheritance.getStrategy()) {
					case SINGLETABLE -> ip.setSingleTableStrategy();
					case JOINED -> ip.setJoinedStrategy();
					default -> {
						/* handled above */
					}
				}
				DiscriminatorColumn dc = entity.getDiscriminatorColumn();
				if (nonNull(dc)) {
					ip.setClassIndicatorFieldName(dc.getName());
				}
				if (nonNull(discriminatorValue)) {
					ip.addClassIndicator(getType().getJavaClass(), discriminatorValue);
				}
				ip.setShouldReadSubclasses(true);
			}
			LOG.log(Level.FINE, "Configured inheritance root: {0} strategy={1}",
					new Object[]{entity.getName(), inheritance.getStrategy()});
		} else if (nonNull(discriminatorValue)) {
			// Child entity — attach to parent via the strategy chosen on the root
			EClass eClass = (EClass) entity.getClass_();
			EClass parentEClass = eClass.getESuperTypes().stream().findFirst().orElse(null);
			if (isNull(parentEClass)) {
				return;
			}
			EDynamicTypeBuilder parentBuilder = context.getETypeBuilder(parentEClass);
			if (isNull(parentBuilder)) {
				return;
			}
			EDynamicTypeBuilder rootBuilder = findInheritanceRoot(parentBuilder);
			ClassDescriptor childDescriptor = getType().getDescriptor();
			ClassDescriptor parentDescriptor = parentBuilder.getType().getDescriptor();
			ClassDescriptor rootDescriptor = rootBuilder.getType().getDescriptor();

			if (rootDescriptor.hasTablePerClassPolicy()) {
				// TABLE_PER_CLASS: each subclass owns its tables and full mapping
				// (including ID). Parent/child linkage goes through the policy.
				if (!childDescriptor.hasTablePerClassPolicy()) {
					childDescriptor.setTablePerClassPolicy(new TablePerClassPolicy(childDescriptor));
				}
				childDescriptor.getTablePerClassPolicy().addParentDescriptor(parentDescriptor);
				parentDescriptor.getTablePerClassPolicy().addChildDescriptor(childDescriptor);
			} else {
				// SINGLE_TABLE / JOINED: parent class + root-level class indicator.
				// EclipseLink inherits parent mappings (including ID) in
				// InheritancePolicy.initialize() (concatenateVectors).
				childDescriptor.getInheritancePolicy()
						.setParentClass(parentBuilder.getType().getJavaClass());
				rootDescriptor.getInheritancePolicy()
						.addClassIndicator(getType().getJavaClass(), discriminatorValue);
			}
			LOG.log(Level.FINE, "Configured inheritance child: {0} parent={1} root={2}",
					new Object[]{entity.getName(), parentEClass.getName(),
							rootBuilder.getType().getEClass().getName()});
		}
	}

	/**
	 * Carries the eorm's unique constraints into the descriptor's table, so DDL generation
	 * emits them ({@code DefaultTableGenerator} reads {@code DatabaseTable.getUniqueConstraints}).
	 * <p>
	 * The first declarer is a map's entry table (issue #185): {@code (owner_fk, MAP_KEY)} is
	 * what makes "one key, one entry" true in the store rather than only in the EMap in memory.
	 * Until now nothing read {@code Table.uniqueConstraint} at all — a declaration the model
	 * carried and the schema never saw.
	 */
	private void addUniqueConstraints(DatabaseTable databaseTable, Table table) {
		int index = 0;
		for (UniqueConstraint constraint : table.getUniqueConstraint()) {
			if (constraint.getColumnName().isEmpty()) {
				continue;
			}
			String name = nonNull(constraint.getName()) ? constraint.getName()
					: "UQ_" + table.getName() + "_" + index;
			databaseTable.addUniqueConstraints(name, new ArrayList<>(constraint.getColumnName()));
			index++;
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
			addUniqueConstraints(primaryDatabaseTable, table);
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
		// setDefaultTable() in EclipseLink 4.x only updates the defaultTable field;
		// it does NOT replace tables[0], which is what SQL generation uses.
		// Explicitly replace the first entry in the tables list so the schema
		// qualifier is visible to the query engine.
		List<DatabaseTable> existingTables = classDescriptor.getTables();
		if (existingTables.isEmpty()) {
			classDescriptor.addTable(primaryDatabaseTable);
		} else {
			existingTables.set(0, primaryDatabaseTable);
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

	/**
	 * Finds the root of the inheritance hierarchy — the entity that has @Inheritance set.
	 */
	private EDynamicTypeBuilder findInheritanceRoot(EDynamicTypeBuilder builder) {
		while (isNull(builder.getType().getEntity().getInheritance())) {
			EClass ec = (EClass) builder.getType().getEntity().getClass_();
			EClass superType = ec.getESuperTypes().stream().findFirst().orElse(null);
			if (isNull(superType)) {
				break;
			}
			EDynamicTypeBuilder parent = context.getETypeBuilder(superType);
			if (isNull(parent)) {
				break;
			}
			builder = parent;
		}
		return builder;
	}

	// Note: ID mapping inheritance is NOT needed — EclipseLink's InheritancePolicy.initialize()
	// automatically concatenates parent mappings onto child descriptors (see EclipseLink
	// InheritancePolicy.java line 963: concatenateVectors(parent.getMappings(), child.getMappings())).
	// The only fix needed was registering class indicators on the ROOT descriptor (findInheritanceRoot)
	// instead of the direct parent.

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
		DirectToFieldMapping mapping = super.addDirectMapping(name, type, columnName);
		applyCaseSensitiveCollation(mapping, type);
		return mapping;
	}

	/**
	 * EMF string equality is case-sensitive, but the MySQL family compares VARCHAR with a
	 * case-insensitive collation by default — ambient case-insensitivity that would leak
	 * into {@code eq}/{@code IN}/joins and even primary-key uniqueness (issue #158, found
	 * by the TCK's case-sensitivity probe on MariaDB). A binary collation on every String
	 * column restores Java semantics; per-predicate case-insensitivity remains the
	 * {@code STRING_MATCH_CASE_INSENSITIVE} opt-in, never ambient. An explicitly
	 * configured column definition is left alone.
	 */
	private void applyCaseSensitiveCollation(DirectToFieldMapping mapping, Class<?> type) {
		if (type != String.class || isNull(context.getSession())) {
			return;
		}
		Platform platform = context.getSession().getDatasourcePlatform();
		if (!platform.isMySQL() && !platform.isMariaDB()) {
			return;
		}
		DatabaseField field = mapping.getField();
		if (nonNull(field.getColumnDefinition()) && !field.getColumnDefinition().isEmpty()) {
			return;
		}
		int size = field.getLength() > 0 ? field.getLength() : 255;
		field.setColumnDefinition("VARCHAR(" + size + ") CHARACTER SET utf8mb4 COLLATE utf8mb4_bin");
	}

	@Override
	public DatabaseMapping addMapping(DatabaseMapping mapping) {
		return super.addMapping(mapping);
	}

	/**
	 * Mirrors {@link DynamicTypeBuilder#addOneToOneMapping(String, DynamicType, String...)}
	 * but constructs the EMF-aware {@link EOneToOneMapping} variant.
	 */
	@Override
	public OneToOneMapping addOneToOneMapping(String name, DynamicType targetType, String fkName) {
		OneToOneMapping mapping = new EOneToOneMapping();
		mapping.setAttributeName(name);
		mapping.setReferenceClass(targetType.getJavaClass());
		String targetField = targetType.getDescriptor().getPrimaryKeyFields().get(0).getName();
		mapping.addForeignKeyFieldName(fkName, targetField);
		return (OneToOneMapping) addMapping(mapping);
	}

	/**
	 * Mirrors {@link DynamicTypeBuilder#addOneToManyMapping(String, DynamicType, String...)}
	 * but constructs the EMF-aware {@link EOneToManyMapping} variant.
	 */
	@Override
	public CollectionMapping addOneToManyMapping(String name, DynamicType targetType, String fkName) {
		OneToManyMapping mapping = new EOneToManyMapping();
		mapping.setAttributeName(name);
		mapping.setReferenceClass(targetType.getJavaClass());
		String targetField = getType().getDescriptor().getPrimaryKeyFields().get(0).getName();
		mapping.addTargetForeignKeyFieldName(fkName, targetField);
		mapping.useTransparentList();
		return (CollectionMapping) addMapping(mapping);
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
