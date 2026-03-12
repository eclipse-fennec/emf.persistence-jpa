/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.persistence.orm.processor;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.GeneratedValue;
import org.eclipse.fennec.persistence.eorm.GenerationType;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.eorm.SequenceGenerator;
import org.eclipse.fennec.persistence.orm.CompositeIdAnalyzer;
import org.eclipse.fennec.persistence.orm.IdConfiguration;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;

/**
 * Enhanced ID processor that supports composite primary keys.
 * 
 * This processor extends the standard ID creation logic to handle:
 * <ul>
 * <li>Multiple ID attributes (EmbeddedId strategy)</li>
 * <li>Reference-based ID classes (IdClass strategy)</li>
 * <li>Single ID attributes (standard strategy)</li>
 * <li>Synthetic ID generation (fallback)</li>
 * </ul>
 * 
 * @author Mark Hoffmann
 * @since 28.01.2025
 */
public class CompositeIdProcessor {
    
    private final CompositeIdAnalyzer analyzer;
    private final boolean strict;
    
    public CompositeIdProcessor(boolean strict) {
        this.analyzer = new CompositeIdAnalyzer();
        this.strict = strict;
    }
    
    /**
     * Creates ID mappings for the given EClass based on its ID structure.
     * 
     * @param eClass the EClass to process
     * @param context the mapping context
     * @return list of ID mappings (may contain multiple IDs for composite keys)
     */
    public List<Id> createIds(EClass eClass, MappingContext context) {
        IdConfiguration config = analyzer.analyzeIdStructure(eClass);
        
        System.out.printf("ID Analysis for %s: %s\n", eClass.getName(), config);
        
        return switch (config.getStrategy()) {
            case SINGLE_ID -> List.of(createSingleId(config.getIdAttributes().get(0)));
            case EMBEDDED_ID -> createEmbeddedIds(config.getIdAttributes());
            case ID_CLASS -> createIdClassIds(config.getIdClassReference());
            case SYNTHETIC_ID -> List.of(createSyntheticId(eClass, config.getSyntheticIdName()));
        };
    }
    
    /**
     * Creates a single ID mapping for standard single-attribute IDs.
     */
    private Id createSingleId(EAttribute idAttribute) {
        Id id = EORMFactory.eINSTANCE.createId();
        MappingHelper.createBase(id, idAttribute, strict);
        
        // Add generation strategy if not strict mode
        if (!strict) {
            addGenerationStrategy(id, idAttribute);
        }
        
        return id;
    }
    
    /**
     * Creates multiple ID mappings for EmbeddedId strategy.
     * 
     * TODO: This needs EORM model support for @EmbeddedId
     * For now, we'll create individual @Id mappings as a temporary solution.
     */
    private List<Id> createEmbeddedIds(List<EAttribute> idAttributes) {
        System.out.printf("CREATING EMBEDDED IDS: Processing %d attributes\n", idAttributes.size());
        
        return idAttributes.stream()
            .map(attr -> {
                Id id = EORMFactory.eINSTANCE.createId();
                MappingHelper.createBase(id, attr, strict);
                
                // Note: In a full implementation, these would be part of an @EmbeddedId
                // but for now we create separate @Id mappings
                
                if (!strict) {
                    addGenerationStrategy(id, attr);
                }
                
                System.out.printf("  Created ID mapping: %s (%s)\n", 
                    id.getName(), attr.getEAttributeType().getName());
                    
                return id;
            })
            .toList();
    }
    
    /**
     * Creates ID mappings for IdClass strategy.
     * 
     * TODO: This needs EORM model support for @IdClass
     * For now, we'll flatten the referenced composite key.
     */
    private List<Id> createIdClassIds(EReference idClassReference) {
        System.out.printf("CREATING ID CLASS IDS: Processing reference %s\n", 
            idClassReference.getName());
            
        // Analyze the components of the referenced class
        List<EAttribute> components = analyzer.analyzeIdClassComponents(idClassReference);
        
        return components.stream()
            .map(attr -> {
                Id id = EORMFactory.eINSTANCE.createId();
                
                // Flatten the attribute name with reference prefix
                String flattenedName = idClassReference.getName() + "_" + attr.getName();
                id.setName(flattenedName);
                
                // Create column mapping
                Column column = EORMFactory.eINSTANCE.createColumn();
                column.setName(flattenedName.toLowerCase());
                column.setNullable(false);
                column.setUnique(false); // Part of composite key
                column.setInsertable(true);
                column.setUpdatable(true);
                id.setColumn(column);
                
                // Note: In a full implementation, this would use @IdClass annotation
                // but for now we create flattened @Id mappings
                
                if (!strict) {
                    addGenerationStrategyForType(id, attr.getEAttributeType().getInstanceClass());
                }
                
                System.out.printf("  Created flattened ID mapping: %s -> %s\n", 
                    flattenedName, attr.getName());
                    
                return id;
            })
            .toList();
    }
    
    /**
     * Creates a synthetic ID when no explicit IDs are found.
     */
    private Id createSyntheticId(EClass eClass, String syntheticIdName) {
        Id id = EORMFactory.eINSTANCE.createId();
        id.setName(syntheticIdName);
        
        Column column = EORMFactory.eINSTANCE.createColumn();
        column.setName(syntheticIdName.toUpperCase());
        column.setNullable(false);
        column.setUnique(true);
        column.setInsertable(true);
        column.setUpdatable(true);
        id.setColumn(column);
        
        if (!strict) {
            SequenceGenerator seqGen = EORMFactory.eINSTANCE.createSequenceGenerator();
            seqGen.setName("SEQ_" + eClass.getName().toUpperCase() + "_" + syntheticIdName.toUpperCase() + "_ID");
            seqGen.setSequenceName(seqGen.getName());
            id.setSequenceGenerator(seqGen);
        }
        
        return id;
    }
    
    /**
     * Adds appropriate generation strategy based on attribute type.
     */
    private void addGenerationStrategy(Id id, EAttribute attribute) {
        Class<?> idType = attribute.getEAttributeType().getInstanceClass();
        addGenerationStrategyForType(id, idType);
    }
    
    /**
     * Adds appropriate generation strategy based on Java type.
     */
    private void addGenerationStrategyForType(Id id, Class<?> idType) {
        if (Number.class.isAssignableFrom(idType) || 
            Long.TYPE.isAssignableFrom(idType) || 
            Integer.TYPE.isAssignableFrom(idType)) {
            
            // Numeric types use sequence generation
            SequenceGenerator seqGen = EORMFactory.eINSTANCE.createSequenceGenerator();
            seqGen.setName("SEQ_" + id.getName().toUpperCase() + "_ID");
            seqGen.setSequenceName(seqGen.getName());
            id.setSequenceGenerator(seqGen);
            
        } else if (String.class.isAssignableFrom(idType)) {
            
            // String types use UUID generation
            GeneratedValue gv = EORMFactory.eINSTANCE.createGeneratedValue();
            gv.setStrategy(GenerationType.UUID);
            id.setGeneratedValue(gv);
            
            // Note: Remove conflicting sequence generator for UUIDs
            // (this fixes the UUID + Sequence inconsistency from the original code)
        }
    }
    
    /**
     * Provides detailed analysis of the ID structure for debugging.
     */
    public String analyzeIdStructureDetailed(EClass eClass) {
        return analyzer.getDetailedAnalysis(eClass);
    }
}