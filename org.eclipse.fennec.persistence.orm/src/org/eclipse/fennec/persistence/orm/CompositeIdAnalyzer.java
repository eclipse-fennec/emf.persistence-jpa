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
package org.eclipse.fennec.persistence.orm;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

/**
 * Analyzer for detecting composite ID patterns in ECore models.
 * 
 * This class implements the enhanced ID detection logic that can identify:
 * <ul>
 * <li>Single attribute IDs (standard case)</li>
 * <li>Multiple attribute composite IDs (EmbeddedId strategy)</li>
 * <li>Reference-based composite IDs (IdClass strategy)</li>
 * <li>Synthetic ID requirements</li>
 * </ul>
 * 
 * @author Mark Hoffmann
 * @since 28.01.2025
 */
public class CompositeIdAnalyzer {
    
    private static final String EXTENDED_METADATA_URI = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
    private static final String ID_ANNOTATION_KEY = "id";
    
    /**
     * Analyzes the ID structure of the given EClass and returns the optimal ID configuration.
     * 
     * The analysis follows this priority order:
     * 1. Reference-based ID class (EAnnotation with id="true")
     * 2. Multiple direct ID attributes (EmbeddedId)
     * 3. Single direct ID attribute (standard)
     * 4. Synthetic ID generation (fallback)
     * 
     * @param eClass the EClass to analyze
     * @return the determined ID configuration
     */
    public IdConfiguration analyzeIdStructure(EClass eClass) {
        if (isNull(eClass)) {
            throw new IllegalArgumentException("EClass cannot be null");
        }
        
        // Step 1: Check for reference-based ID class (highest priority)
        EReference idClassReference = findIdClassReference(eClass);
        if (idClassReference != null) {
            return IdConfiguration.createIdClass(idClassReference);
        }
        
        // Step 2: Find all direct ID attributes
        List<EAttribute> directIdAttributes = findDirectIdAttributes(eClass);
        
        // Step 3: Determine strategy based on ID attribute count
        if (directIdAttributes.size() > 1) {
            // Multiple ID attributes -> EmbeddedId strategy
            return IdConfiguration.createEmbeddedId(directIdAttributes);
        } else if (directIdAttributes.size() == 1) {
            // Single ID attribute -> standard strategy
            return IdConfiguration.createSingleId(directIdAttributes.get(0));
        } else {
            // No explicit ID attributes -> synthetic ID
            String syntheticName = "pk_" + eClass.getName();
            return IdConfiguration.createSyntheticId(syntheticName);
        }
    }
    
    /**
     * Finds all attributes marked with iD="true" in the EClass.
     * 
     * @param eClass the EClass to search
     * @return list of ID attributes (may be empty)
     */
    public List<EAttribute> findDirectIdAttributes(EClass eClass) {
        return eClass.getEAllAttributes().stream()
            .filter(EAttribute::isID)
            .collect(Collectors.toList());
    }
    
    /**
     * Searches for a reference marked as ID class via EAnnotation.
     * 
     * Looks for EAnnotation with source="http:///org/eclipse/emf/ecore/util/ExtendedMetaData"
     * and detail key "id" with value "true".
     * 
     * @param eClass the EClass to search
     * @return the ID class reference, or null if not found
     */
    public EReference findIdClassReference(EClass eClass) {
        // Check class-level annotations first
        EAnnotation extendedMetadata = eClass.getEAnnotation(EXTENDED_METADATA_URI);
        if (extendedMetadata != null && isIdAnnotation(extendedMetadata)) {
            // Look for a containment reference that could serve as ID class
            return eClass.getEAllReferences().stream()
                .filter(EReference::isContainment)
                .findFirst()
                .orElse(null);
        }
        
        // Check reference-level annotations
        for (EReference reference : eClass.getEAllReferences()) {
            EAnnotation refAnnotation = reference.getEAnnotation(EXTENDED_METADATA_URI);
            if (refAnnotation != null && isIdAnnotation(refAnnotation)) {
                return reference;
            }
        }
        
        return null;
    }
    
    /**
     * Checks if an EAnnotation marks something as an ID.
     * 
     * @param annotation the annotation to check
     * @return true if the annotation has id="true"
     */
    private boolean isIdAnnotation(EAnnotation annotation) {
        EMap<String, String> details = annotation.getDetails();
        String idValue = details.get(ID_ANNOTATION_KEY);
        return "true".equals(idValue);
    }
    
    /**
     * Analyzes the eKeys of an ID class reference to determine component attributes.
     * 
     * @param idClassReference the reference marked as ID class
     * @return list of attributes that form the composite key
     */
    public List<EAttribute> analyzeIdClassComponents(EReference idClassReference) {
        if (isNull(idClassReference) || isNull(idClassReference.getEReferenceType())) {
            return List.of();
        }
        
        // If eKeys are specified, use those
        if (!idClassReference.getEKeys().isEmpty()) {
            return idClassReference.getEKeys().stream()
                .filter(key -> key instanceof EAttribute)
                .map(key -> (EAttribute) key)
                .collect(Collectors.toList());
        }
        
        // Otherwise, use all ID attributes from the referenced type
        return findDirectIdAttributes(idClassReference.getEReferenceType());
    }
    
    /**
     * Provides detailed analysis information for debugging and logging.
     * 
     * @param eClass the EClass to analyze
     * @return detailed analysis string
     */
    public String getDetailedAnalysis(EClass eClass) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("ID Analysis for ").append(eClass.getName()).append(":\n");
        
        // Direct ID attributes
        List<EAttribute> directIds = findDirectIdAttributes(eClass);
        analysis.append("  Direct ID attributes: ").append(directIds.size()).append("\n");
        for (EAttribute attr : directIds) {
            analysis.append("    - ").append(attr.getName())
                     .append(" (").append(attr.getEAttributeType().getName()).append(")\n");
        }
        
        // ID class reference
        EReference idClassRef = findIdClassReference(eClass);
        if (idClassRef != null) {
            analysis.append("  ID class reference: ").append(idClassRef.getName()).append("\n");
            analysis.append("    Target type: ").append(idClassRef.getEReferenceType().getName()).append("\n");
            analysis.append("    eKeys: ").append(idClassRef.getEKeys().size()).append("\n");
            
            List<EAttribute> components = analyzeIdClassComponents(idClassRef);
            analysis.append("    Components: ").append(components.size()).append("\n");
            for (EAttribute comp : components) {
                analysis.append("      - ").append(comp.getName()).append("\n");
            }
        }
        
        // Final configuration
        IdConfiguration config = analyzeIdStructure(eClass);
        analysis.append("  Recommended strategy: ").append(config.getStrategy()).append("\n");
        
        return analysis.toString();
    }
}