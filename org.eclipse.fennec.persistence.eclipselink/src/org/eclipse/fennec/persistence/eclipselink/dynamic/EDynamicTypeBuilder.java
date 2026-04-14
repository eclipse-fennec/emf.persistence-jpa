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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.eclipse.fennec.persistence.eclipselink.descriptors.EClassDescriptor;
import org.eclipse.fennec.persistence.eclipselink.indirection.EBasicIndirectionPolicy;
import org.eclipse.fennec.persistence.eclipselink.mappings.EFeatureAccessor;
import org.eclipse.fennec.persistence.eclipselink.mappings.EReferenceAccessor;
import org.eclipse.fennec.persistence.eorm.Attributes;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.CascadeType;
import org.eclipse.fennec.persistence.eorm.CollectionTable;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.Convert;
import org.eclipse.fennec.persistence.eorm.DiscriminatorColumn;
import org.eclipse.fennec.persistence.eorm.EFeatureObject;
import org.eclipse.fennec.persistence.eorm.ElementCollection;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.ForeignKey;
import org.eclipse.fennec.persistence.eorm.GenerationType;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.eorm.Inheritance;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.JoinTable;
import org.eclipse.fennec.persistence.eorm.ManyToMany;
import org.eclipse.fennec.persistence.eorm.ManyToOne;
import org.eclipse.fennec.persistence.eorm.MappedByRef;
import org.eclipse.fennec.persistence.eorm.OneToMany;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.eclipse.fennec.persistence.eorm.SecondaryTable;
import org.eclipse.fennec.persistence.eorm.Table;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.ContainerMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.UnidirectionalOneToManyMapping;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.UUIDSequence;

/**
 * Extended {@link DynamicTypeBuilder} for {@link EClass} handling
 * @author Mark Hoffmann
 * @since 16.12.2024
 */
public class EDynamicTypeBuilder extends JPADynamicTypeBuilder {

	private static final Logger LOG = Logger.getLogger(EDynamicTypeBuilder.class.getName());

	private final EDynamicTypeContext context;
	
	/**
	 * Creates a new instance.
	 * @param dynamicClass
	 * @param parentType
	 * @param tableNames
	 */
	public EDynamicTypeBuilder(Entity entity, Class<?> javaClass, DynamicType parentType, String[] tableNames, EDynamicTypeContext context) {
		super(javaClass, parentType);
		this.context = context;
		
		EClassDescriptor descriptor = initClassDescriptor(entity);
		initDynamicType(descriptor);
		configureEntity(entity);
	}

	/**
	 * Creates a new instance.
	 * @param dynamicClass
	 * @param parentType
	 * @param tableNames
	 */
	public EDynamicTypeBuilder(Entity entity, String[] tableNames, EDynamicTypeContext context) {
		this(entity, Object.class, null, tableNames, context);
	}
	
	/**
	 * Creates a new instance.
	 * @param entity the entity
	 * @param context the context
	 */
	public EDynamicTypeBuilder(Entity entity, EDynamicTypeContext context) {
		this(entity, Object.class, null, null, context);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.dynamic.DynamicTypeBuilder#getType()
	 */
	@Override
	public EDynamicType getType() {
		return (EDynamicType)super.getType();
	}

	/**
	 * Configures the entity
	 * @param entity the {@link Entity}
	 */
	protected void configureEntity(Entity entity) {
		/*
		 * Configure table for dynamic use
		 */
		String tableName = isNull(entity.getTable()) ? entity.getName().toUpperCase() : entity.getTable().getName();
		configure(getType().getDescriptor(), tableName);
		/*
		 * Configure EORM stuff
		 */
		Attributes attributes = entity.getAttributes();
		configureDatabase(getType());
		configureIds(getType(), attributes.getId());
		configureSingleAttributes(getType(), attributes.getBasic());
		configureManyAttributes(getType(), attributes.getElementCollection());
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
	 * Sets-up the entities databases
	 */
	public void configureDatabase(EDynamicType eDynamicType) {
		requireNonNull(eDynamicType);
		EClass eClassifier = eDynamicType.getEClass();
		Entity entity = eDynamicType.getEntity();
		ClassDescriptor classDescriptor = eDynamicType.getDescriptor();

		// primary Table
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
		// secondary Tables
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

	/**
	 * Configures the {@link EAttribute} of the {@link EClass} from the {@link EDynamicTypeOld}
	 * Enhanced to support composite primary keys with multiple ID fields.
	 * @param eDynamicType the dynamic Type
	 * @param ids a list of Ids
	 */
	public void configureIds(EDynamicType eDynamicType, List<Id> ids) {
		EClass eClass = eDynamicType.getEClass();
		
		if (ids.isEmpty()) {
			// No IDs specified - this shouldn't happen with our enhanced processor
			LOG.log(Level.WARNING, "No IDs specified for entity {0}", eClass.getName());
			return;
		}
		
		if (ids.size() == 1) {
			// Single ID - use original logic for backward compatibility
			configureSingleId(eDynamicType, ids.get(0));
		} else {
			// Multiple IDs - composite key logic
			configureCompositeIds(eDynamicType, ids);
		}
	}
	
	/**
	 * Configures a single ID field (backward compatible with original implementation).
	 */
	private void configureSingleId(EDynamicType eDynamicType, Id id) {
		EClass eClass = eDynamicType.getEClass();
		String seqName = "SEQ_" + eClass.getName().toUpperCase();
		String idName = nonNull(id.getColumn()) ? id.getColumn().getName() : id.getName();
		String seqGenName = null;
		Sequence sequence = null;
		
		if (nonNull(id.getSequenceGenerator())) {
			seqGenName = id.getSequenceGenerator().getSequenceName();
		}
		if (nonNull(id.getGeneratedValue()) && 
				GenerationType.UUID.equals(id.getGeneratedValue().getStrategy())) {
			sequence = new UUIDSequence();
		}
		
		setPrimaryKeyFields(idName);
		configureSequencing("SEQ_GEN", idName);
		if (nonNull(seqGenName)) {
			configureSequencing(seqGenName, idName);
			if (nonNull(sequence)) {
				configureSequencing(sequence, seqName, idName);
			}
		}
		
		LOG.log(Level.FINE, "Configured single ID: {0} for entity {1}", new Object[]{idName, eClass.getName()});
	}
	
	/**
	 * Configures composite primary key with multiple ID fields.
	 * 
	 * This creates multiple primary key fields in EclipseLink, which represents
	 * a composite key. In a full implementation, this would use @EmbeddedId or @IdClass,
	 * but for now we use multiple primary key fields.
	 */
	private void configureCompositeIds(EDynamicType eDynamicType, List<Id> ids) {
		EClass eClass = eDynamicType.getEClass();
		LOG.log(Level.FINE, "Configuring composite ID with {0} fields for entity {1}",
			new Object[]{ids.size(), eClass.getName()});
			
		// Collect all ID field names
		String[] idFieldNames = new String[ids.size()];
		for (int i = 0; i < ids.size(); i++) {
			Id id = ids.get(i);
			String fieldName = nonNull(id.getColumn()) ? id.getColumn().getName() : id.getName();
			idFieldNames[i] = fieldName;
			
			LOG.log(Level.FINER, "ID field {0}: {1}", new Object[]{i + 1, fieldName});
			
			// Configure sequencing for each ID field that has generation strategy
			configureIdSequencing(id, fieldName, eClass);
		}
		
		// Set all primary key fields in EclipseLink
		// This tells EclipseLink that these fields together form the primary key
		setPrimaryKeyFields(idFieldNames);
		
		LOG.log(Level.FINE, "Successfully configured composite primary key: [{0}] for {1}",
			new Object[]{String.join(", ", idFieldNames), eClass.getName()});
	}
	
	/**
	 * Configures sequencing for an individual ID field.
	 */
	private void configureIdSequencing(Id id, String fieldName, EClass eClass) {
		// Handle sequence generation
		if (nonNull(id.getSequenceGenerator())) {
			String seqGenName = id.getSequenceGenerator().getSequenceName();
			configureSequencing(seqGenName, fieldName);
			LOG.log(Level.FINER, "Configured sequence: {0} for field {1}", new Object[]{seqGenName, fieldName});
		}
		
		// Handle UUID generation
		if (nonNull(id.getGeneratedValue()) && 
			GenerationType.UUID.equals(id.getGeneratedValue().getStrategy())) {
			
			String seqName = "SEQ_" + eClass.getName().toUpperCase() + "_" + fieldName.toUpperCase();
			Sequence sequence = new UUIDSequence();
			configureSequencing(sequence, seqName, fieldName);
			LOG.log(Level.FINER, "Configured UUID generation for field {0}", fieldName);
		}
	}

	/**
	 * Configures {@link Basic} (single value) mappings 
	 * @param eType the {@link EDynamicType}
	 * @param basics the {@link Basic} list
	 */
	public void configureSingleAttributes(EDynamicType eType, List<Basic> basics) {
		if (isNull(basics) || basics.isEmpty()) {
			return;
		}
		basics.forEach(this::processBasic);
	}

	/**
	 * Configures many attributes
	 * @param eType the {@link EDynamicType}
	 * @param elementCollections the {@link ElementCollection} list
	 */
	public void configureManyAttributes(EDynamicType eType, List<ElementCollection> elementCollections) {
		if (isNull(elementCollections) || elementCollections.isEmpty()) {
			return;
		}
		elementCollections.forEach(this::processElementCollection);
	}

	public void configureReferences() {
		Attributes attrs = getAttributes();
		List<ManyToOne> m2oMappings = attrs.getManyToOne();
		m2oMappings.stream().filter(not(this::isMappedBy)).forEach(this::processManyToOne);
		List<OneToOne> o2oMappings = attrs.getOneToOne();
		o2oMappings.stream().filter(not(this::isMappedBy)).forEach(this::processOneToOne);
		List<OneToMany> o2mMappings = attrs.getOneToMany();
		o2mMappings.stream().filter(not(this::isMappedBy)).forEach(this::processOneToMany);
		List<ManyToMany> m2mMappings = attrs.getManyToMany();
		m2mMappings.stream().filter(not(this::isMappedBy)).forEach(this::processManyToMany);
	}

	public void configureMappedByReferences() {
		Attributes attrs = getAttributes();
		//		List<ManyToOne> m2oMappings = attrs.getManyToOne();
		//		m2oMappings.stream().filter(this::isMappedBy).forEach(this::processManyToOne);
		List<OneToOne> o2oMappings = attrs.getOneToOne();
		o2oMappings.stream().filter(this::isMappedBy).forEach(this::processOneToOne);
		List<OneToMany> o2mMappings = attrs.getOneToMany();
		o2mMappings.stream().filter(this::isMappedBy).forEach(this::processOneToMany);
		List<ManyToMany> m2mMappings = attrs.getManyToMany();
		m2mMappings.stream().filter(this::isMappedBy).forEach(this::processManyToMany);
	}

	private boolean isMappedBy(BaseRef ref) {
		return ref instanceof MappedByRef mbr && 
				Objects.nonNull(mbr.getMappedBy()); 
	}

	private Attributes getAttributes() {
		Entity entity = getType().getEntity();
		return entity.getAttributes();
	}
	
	/**
	 * Creates the class descriptor for the {@link Entity} and takes care
	 * of Java class creation during the descriptor initialization
	 * @param entity the {@link Entity}
	 * @return the {@link EClassDescriptor}
	 */
	private EClassDescriptor initClassDescriptor(Entity entity) {
		requireNonNull(entity, "An Entity is required to create a EClassDescriptor");
		EClassDescriptor descriptor = new EClassDescriptor(entity);
		/*
		 * Eclipselink API to create Java classes out of the class name strings
		 */
		descriptor.convertClassNamesToClasses(context.getClassloader());
		return descriptor;
	}

	/**
	 * Creates and initializes our own {@link EDynamicType}
	 * @param descriptor the corresponding {@link EClassDescriptor}
	 */
	private void initDynamicType(EClassDescriptor descriptor) {
		EDynamicTypeImpl edti = new EDynamicTypeImpl(descriptor, null, context);
		edti.setDynamicPropertiesManager(this.entityType.getDynamicPropertiesManager());
		this.entityType = edti;
	}

	/**
	 * Processes {@link Basic} setup definitions
	 * @see {@link BasicAccessor#process()}
	 * @param basic the basic definition
	 */
	private void processBasic(Basic basic) {
		if (isNull(basic)) {
			return;
		}
		EFeatureObject efo = (EFeatureObject) basic.getAccessibleObject();
		EStructuralFeature feature = efo.getFeature();
		Class<?> typeClass;
		if (feature instanceof EReference) {
			typeClass = String.class;
		} else {
			EAttribute ea = (EAttribute) efo.getFeature();
			Class<?> originalTypeClass = ea.getEAttributeType().getInstanceClass();
			typeClass = originalTypeClass;
			LOG.log(Level.FINER, "[processBasic] Processing {0} with original typeClass: {1}", new Object[]{ea.getName(), originalTypeClass});

			// Map enums into Strings (EnumType.STRING is the default strategy)
			if (ea.getEAttributeType() instanceof EEnum) {
				typeClass = String.class;
				LOG.log(Level.FINER, "[processBasic] {0} is enum, mapping as String (EnumType.STRING)", ea.getName());
			}
			// Fallback to String for custom types (UUID, arrays, etc.) that don't have instance classes
			if (typeClass == null) {
				typeClass = String.class;
				LOG.log(Level.FINER, "[processBasic] {0} has null typeClass, setting to String.class", ea.getName());
			}

			LOG.log(Level.FINER, "[processBasic] Final typeClass for {0}: {1}", new Object[]{ea.getName(), typeClass});
		}
		Column c = basic.getColumn();
		String colName = nonNull(c) ? c.getName() : basic.getName();
		DirectToFieldMapping mapping = addDirectMapping(basic.getName(), typeClass, colName);
		mapping.setIsLazy(false);
		mapping.setIsOptional(basic.isOptional());
		if (nonNull(c)) {
			mapping.setIsMutable(c.isSetUpdatable());
		}
		/**
		 * Converter handling - both explicit and automatic
		 */
		TypeConverter converter = null;

		// First check for explicit converter configuration
		Convert convert = basic.getConvert();
		if (nonNull(convert) && nonNull(convert.getConverter())) {
			String name = convert.getConverter();
			converter = context.getConverter(name);
		}
		// If no explicit converter, try automatic detection for non-standard database types
		else if (feature instanceof EAttribute ea) {
			Class<?> originalTypeClass = ea.getEAttributeType().getInstanceClass();
			// Check if this is a type that needs conversion (not null and not standard database types)
			if (originalTypeClass != null && !isStandardDatabaseType(originalTypeClass)) {
				try {
					LOG.log(Level.FINER, "Trying to find converter for {0} (instanceClass: {1})",
						new Object[]{ea.getEAttributeType().getName(), originalTypeClass});
					converter = context.getConverter(ea.getEAttributeType());
					if (converter != null) {
						LOG.log(Level.FINER, "Found converter: {0} for {1}",
							new Object[]{converter.getName(), ea.getEAttributeType().getName()});
					} else {
						LOG.log(Level.FINER, "No converter found for {0}", ea.getEAttributeType().getName());
					}
				} catch (Exception e) {
					LOG.log(Level.WARNING, e, () -> "Exception finding converter for " + ea.getEAttributeType().getName());
				}
			}
		}

		EFeatureAccessor efa = EFeatureAccessor.create(feature, converter);
		mapping.setAttributeAccessor(efa);
	}

	/**
	 * Configures an element collection / many attribute  
	 * @see {@link BasicCollectionAccessor#process()}
	 * @param elementCollection the {@link ElementCollection}
	 */
	private void processElementCollection(ElementCollection elementCollection) {
		EFeatureObject efo = (EFeatureObject) elementCollection.getAccessibleObject();
		EAttribute ea = (EAttribute) efo.getFeature();
		CollectionTable ct = elementCollection.getCollectionTable();
		String tableName = nonNull(ct) ? 
				ct.getName() : 
					ea.getEContainingClass().getName().toUpperCase() + "__" + ea.getName().toUpperCase();
		Column c = elementCollection.getColumn();
		String valueColumn = nonNull(c) ? 
				c.getName() : 
					"VAL_" + ea.getName().toUpperCase();
		String foreignKeyName = nonNull(ct) && nonNull(ct.getForeignKey()) ? ct.getForeignKey().getName() : null;
		EDataType attrType = ea.getEAttributeType();
		Class<?> typeClass = String.class;
		if (!(attrType instanceof EEnum) && nonNull(attrType.getInstanceClass())) {
			typeClass = attrType.getInstanceClass();
		}
		typeClass = isNull(typeClass) ? String.class : typeClass;
		DirectCollectionMapping mapping = addDirectCollectionMapping(elementCollection.getName(), tableName, valueColumn, typeClass, foreignKeyName);
		mapping.setIsLazy(false);
		if (nonNull(c)) {
			mapping.setIsOptional(false);
			mapping.setCascadeAll(true);
		}
		mapping.setShouldExtendPessimisticLockScope(true);
		setContainerIndirectionPolicy(mapping, null, false, ea);
		
		/**
		 * Automatic converter detection for element collections with custom types
		 */
		TypeConverter converter = null;
		if (typeClass == String.class && attrType.getInstanceClass() != String.class) {
			// This is a type that fell back to String due to unsupported instance class - try to find a converter
			try {
				LOG.log(Level.FINER, "[ElementCollection] Trying to find converter for {0} (instanceClass: {1})",
					new Object[]{attrType.getName(), attrType.getInstanceClass()});
				converter = context.getConverter(attrType);
				if (nonNull(converter)) {
					LOG.log(Level.FINER, "[ElementCollection] Found converter: {0} for {1}",
						new Object[]{converter.getName(), attrType.getName()});
				} else {
					LOG.log(Level.FINER, "[ElementCollection] No converter found for {0}", attrType.getName());
				}
			} catch (Exception e) {
				LOG.log(Level.WARNING, e, () -> "[ElementCollection] Exception finding converter for " + attrType.getName());
			}
		}
		EFeatureAccessor efa = EFeatureAccessor.create(ea, converter);
		mapping.setAttributeAccessor(efa);
	}

	/**
	 * Process {@link OneToOne} mappings
	 * @see {@link OneToOneAccessor#process()}
	 * @param oneToOne the {@link OneToOne}
	 */
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
			//			throw new IllegalStateException(String.format("No type builder available for EClass '%s'", refType.getName()));
		}
		OneToOneMapping mapping = null;
		ForeignKey fk = oneToOne.getForeignKey();
		if (nonNull(mappedBy)) {
			mapping = new OneToOneMapping();
			mapping.setIsOneToOneRelationship(true);
			addMapping(mapping);
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
				mapping = addOneToOneMapping(name, refTypeBuilder.getType(), fk.getName());
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

	/**
	 * Process {@link OneToMany} mappings.
	 * We have different constellations within EMF context:
	 * * uni-directional containment uses fetch type EAGER
	 * * bi-directional containment uses mapped by on the many-to-one side also uses fetch type eager
	 * * uni-directional non containment uses a Many-To-Many mapping with mapping table and fetch type LAZY
	 * 
	 * @see {@link OneToManyAccessor#process()}
	 * @param oneToMany the {@link OneToOne}
	 */
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
			//			throw new IllegalStateException(String.format("No type builder available for EClass '%s'", refType.getName()));
		}
		CollectionMapping mapping = null;
		ForeignKey fk = oneToMany.getForeignKey();
		JoinTable jt = oneToMany.getJoinTable();
		List<JoinColumn> joinColumns = oneToMany.getJoinColumn(); // NEW: Get JoinColumns
		
		if (nonNull(mappedBy)) {
			mapping = new OneToManyMapping();
			addMapping(mapping);
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
			// NEW: Handle JoinColumn before JoinTable or ForeignKey
			if (nonNull(jt)) {
				// Use JoinTable approach (many-to-many style)
				ManyToManyMapping m2mMapping = new ManyToManyMapping();
				m2mMapping.setAttributeName(name);
				addMapping(m2mMapping);
				m2mMapping.setDefinedAsOneToManyMapping(true);
				mapping = createM2MJoinTable(m2mMapping, jt, refTypeBuilder.getType());
			} else if (nonNull(joinColumns) && !joinColumns.isEmpty()) {
				// NEW: Use JoinColumn approach (standard OneToMany with FK in target table)
				mapping = createOneToManyWithJoinColumns(name, refTypeBuilder.getType(), joinColumns);
			} else if (nonNull(fk)) {
				// Fallback to ForeignKey approach
				mapping = addOneToManyMapping(name, refTypeBuilder.getType(), fk.getName());
			} else {
				// No explicit mapping configuration - use default OneToMany
				mapping = new OneToManyMapping();
				mapping.setAttributeName(name);
				addMapping(mapping);
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
	
	/**
	 * Creates a OneToMany mapping using JoinColumn strategy.
	 * This is the correct approach for EMF containment relationships.
	 * 
	 * @param attributeName the attribute name for the mapping
	 * @param targetType the target EDynamicType 
	 * @param joinColumns the JoinColumn configuration from EORM
	 * @return the configured OneToManyMapping
	 */
	private OneToManyMapping createOneToManyWithJoinColumns(String attributeName, EDynamicType targetType, List<JoinColumn> joinColumns) {
		// Use UnidirectionalOneToManyMapping for unidirectional OneToMany with JoinColumn
		// This is EclipseLink's specialized mapping for this exact use case
		UnidirectionalOneToManyMapping mapping = new UnidirectionalOneToManyMapping();
		mapping.setAttributeName(attributeName);
		addMapping(mapping);
		
		// Configure join columns - map source PK to target FK
		for (JoinColumn joinColumn : joinColumns) {
			String fkColumnName = joinColumn.getName(); // FK column in target table
			String pkColumnName = joinColumn.getReferencedColumnName(); // PK column in source table (this table)
			
			// Handle referenced column name format "TABLE.COLUMN" -> "COLUMN"
			if (nonNull(pkColumnName) && pkColumnName.contains(".")) {
				pkColumnName = pkColumnName.substring(pkColumnName.indexOf(".") + 1);
			}
			
			// Default to primary key if not specified
			if (isNull(pkColumnName) || pkColumnName.trim().isEmpty()) {
				// Use the primary key field of this entity
				DatabaseField primaryKeyField = getType().getDescriptor().getPrimaryKeyFields().get(0);
				pkColumnName = primaryKeyField.getName();
			}
			
			// Create database fields
			DatabaseField sourceField = new DatabaseField(pkColumnName);
			sourceField.setTable(getType().getDescriptor().getDefaultTable());
			
			DatabaseField targetField = new DatabaseField(fkColumnName);  
			targetField.setTable(targetType.getDescriptor().getDefaultTable());
			
			// Use addTargetForeignKeyField to properly configure the unidirectional relationship
			// This is the key method that tells EclipseLink how to manage FK values during persistence
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
			//			throw new IllegalStateException(String.format("No type builder available for EClass '%s'", refType.getName()));
		}
		ManyToOneMapping mapping = new ManyToOneMapping();
		addMapping(mapping);
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
			DatabaseTable fkTable = getType().getDescriptor().getDefaultTable();
			fkField.setTable(fkTable);
			mapping.addForeignKeyField(fkField, pkField);
			mapping.setIsReadOnly(false);
		}
		setMappingDefaults(mapping, reference);
		mapping.setUsesIndirection(false);
		mapping.dontUseIndirection();
	}

	/**
	 * Process {@link OneToMany} mappings
	 * @see {@link ManyToManyAccessor#process()}
	 * @param manyToMany the {@link ManyToMany}
	 */
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
			//			throw new IllegalStateException(String.format("No type builder available for EClass '%s'", refType.getName()));
		}
		ManyToManyMapping mapping = new ManyToManyMapping();
		addMapping(mapping);
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

	/**
	 * INTERNAL:
	 * Set the correct indirection policy on a collection mapping. Method
	 * assume that the reference class / type has been set on the mapping before
	 * calling this method.
	 * 
	 * Indirection is equal to the resolving proxies in EMF
	 */
	private void setContainerIndirectionPolicy(DatabaseMapping mapping, String mapKey, boolean resolveProxy, EStructuralFeature feature) {
		if (mapping instanceof ContainerMapping) {
			ContainerMapping cMapping = (ContainerMapping) mapping;
			if (resolveProxy && (mapping instanceof ForeignReferenceMapping)) {
				CollectionMapping collectionMapping = (CollectionMapping)mapping;
				if (feature.isUnique()) {
					collectionMapping.useTransparentSet();
				} else  {
					collectionMapping.useTransparentList();
				}
			} else {
				if (mapping instanceof CollectionMapping) {
					((CollectionMapping)mapping).dontUseIndirection();
				}
			}
			//			if (feature.isUnique()) {
			//				// This will cause it to use a CollectionContainerPolicy type
			//				cMapping.useCollectionClass(java.util.HashSet.class);
			//			} else {
			//				// This will cause a ListContainerPolicy type to be used or
			//				// OrderedListContainerPolicy if ordering is specified.
			//				cMapping.useCollectionClass(java.util.Vector.class);
			//			}
			cMapping.useCollectionClass(java.util.LinkedList.class);
		} else if (mapping instanceof ForeignReferenceMapping) {
			ForeignReferenceMapping frMapping = (ForeignReferenceMapping) mapping;
			if (resolveProxy) {
				frMapping.setIndirectionPolicy(new BasicIndirectionPolicy());
				//				frMapping.setIndirectionPolicy(new WeavedObjectBasicIndirectionPolicy(getGetMethodName(), getSetMethodName(), actualAttributeType, true));
			} else {
				frMapping.dontUseIndirection();
			}
		}

	}
	
	ManyToManyMapping createM2MJoinTable(ManyToManyMapping mapping, JoinTable joinTable, EDynamicType refType) {
		mapping.setRelationTableName(joinTable.getName().toUpperCase());
		for (DatabaseField sourcePK : getType().getDescriptor().getPrimaryKeyFields()) {
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
	
	/**
	 * Sets our mapping defaults
	 * @param mapping the {@link ForeignReferenceMapping}
	 * @param reference the {@link EReference}
	 */
	void setMappingDefaults(ForeignReferenceMapping mapping, EReference reference) {
		mapping.setJoinFetch(ForeignReferenceMapping.NONE);
		mapping.setIsCascadeOnDeleteSetOnDatabase(false);
		mapping.setDerivesId(false);
		mapping.setIsPrivateOwned(false);
		mapping.setIsCacheable(true);
		mapping.setIsLazy(false);
		mapping.setAttributeAccessor(EReferenceAccessor.create(reference, context));
	}

	/**
	 * Sets our indirection / EMF proxy handling
	 * @param mapping the {@link ForeignReferenceMapping}
	 * @param reference the {@link EReference}
	 */
	void setIndirection(ForeignReferenceMapping mapping, EReference reference) {
		requireNonNull(reference);
		requireNonNull(mapping);
		if (reference.isContainment()) {
			mapping.dontUseIndirection();
		} else {
			mapping.setIndirectionPolicy(new EBasicIndirectionPolicy(mapping, reference, getType()));
		}
	}
	
	/**
	 * Sets cascading
	 * @param mapping the {@link ForeignReferenceMapping}
	 * @param reference the {@link EReference}
	 * @param cascadeType the {@link CascadeType}, can be <code>null</code>
	 */
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
	
	/**
	 * Check if the given class is a standard database type that doesn't need conversion.
	 * Standard database types include: primitives, String, standard numeric types (but not BigDecimal/BigInteger),
	 * Date, Time, Timestamp, byte[], etc.
	 * 
	 * @param clazz the class to check
	 * @return true if it's a standard database type, false if it needs conversion
	 */
	private boolean isStandardDatabaseType(Class<?> clazz) {
		if (clazz == null) {
			return true; // null types are handled by fallback logic
		}
		
		// Primitive types and their wrappers (except BigDecimal/BigInteger which need conversion)
		if (clazz.isPrimitive() ||
			clazz == String.class ||
			clazz == Integer.class || clazz == Long.class || clazz == Double.class || clazz == Float.class ||
			clazz == Short.class || clazz == Byte.class || clazz == Character.class || clazz == Boolean.class ||
			clazz == java.sql.Date.class || clazz == java.sql.Time.class || clazz == java.sql.Timestamp.class ||
			clazz == java.util.Date.class ||
			clazz == byte[].class) {
			return true;
		}
		
		return false;
	}

}
