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
package org.eclipse.fennec.persistence.orm;

import java.util.List;
import java.util.Objects;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;

/**
 * Configuration object representing different ID mapping strategies for ECore classes.
 * 
 * This class encapsulates the analysis of ECore ID structures and provides
 * strategy recommendations for JPA mapping generation.
 * 
 * @author Mark Hoffmann
 * @since 28.01.2025
 */
public class IdConfiguration {
    
    /**
     * Enumeration of supported ID mapping strategies.
     */
    public enum IdStrategy {
        /** Single attribute ID - standard JPA @Id */
        SINGLE_ID,
        
        /** Multiple attributes as composite key - JPA @EmbeddedId */
        EMBEDDED_ID,
        
        /** Reference-based composite key - JPA @IdClass */
        ID_CLASS,
        
        /** Generated synthetic ID when no explicit ID found */
        SYNTHETIC_ID
    }
    
    private final IdStrategy strategy;
    private final List<EAttribute> idAttributes;
    private final EReference idClassReference;
    private final String syntheticIdName;
    
    private IdConfiguration(IdStrategy strategy, List<EAttribute> idAttributes, 
                           EReference idClassReference, String syntheticIdName) {
        this.strategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
        this.idAttributes = idAttributes;
        this.idClassReference = idClassReference;
        this.syntheticIdName = syntheticIdName;
    }
    
    /**
     * Creates configuration for single attribute ID.
     */
    public static IdConfiguration createSingleId(EAttribute idAttribute) {
        return new IdConfiguration(IdStrategy.SINGLE_ID, List.of(idAttribute), null, null);
    }
    
    /**
     * Creates configuration for composite embedded ID.
     */
    public static IdConfiguration createEmbeddedId(List<EAttribute> idAttributes) {
        if (idAttributes == null || idAttributes.size() <= 1) {
            throw new IllegalArgumentException("EmbeddedId requires multiple ID attributes");
        }
        return new IdConfiguration(IdStrategy.EMBEDDED_ID, List.copyOf(idAttributes), null, null);
    }
    
    /**
     * Creates configuration for reference-based ID class.
     */
    public static IdConfiguration createIdClass(EReference idClassReference) {
        return new IdConfiguration(IdStrategy.ID_CLASS, null, idClassReference, null);
    }
    
    /**
     * Creates configuration for synthetic ID generation.
     */
    public static IdConfiguration createSyntheticId(String syntheticIdName) {
        return new IdConfiguration(IdStrategy.SYNTHETIC_ID, null, null, syntheticIdName);
    }
    
    // Getters
    
    public IdStrategy getStrategy() {
        return strategy;
    }
    
    public List<EAttribute> getIdAttributes() {
        return idAttributes != null ? idAttributes : List.of();
    }
    
    public EReference getIdClassReference() {
        return idClassReference;
    }
    
    public String getSyntheticIdName() {
        return syntheticIdName;
    }
    
    // Utility methods
    
    public boolean isSingleId() {
        return strategy == IdStrategy.SINGLE_ID;
    }
    
    public boolean isComposite() {
        return strategy == IdStrategy.EMBEDDED_ID || strategy == IdStrategy.ID_CLASS;
    }
    
    public boolean isEmbeddedId() {
        return strategy == IdStrategy.EMBEDDED_ID;
    }
    
    public boolean isIdClass() {
        return strategy == IdStrategy.ID_CLASS;
    }
    
    public boolean isSynthetic() {
        return strategy == IdStrategy.SYNTHETIC_ID;
    }
    
    public int getIdAttributeCount() {
        return idAttributes != null ? idAttributes.size() : 0;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("IdConfiguration{");
        sb.append("strategy=").append(strategy);
        
        switch (strategy) {
            case SINGLE_ID:
                sb.append(", attribute=").append(idAttributes.get(0).getName());
                break;
            case EMBEDDED_ID:
                sb.append(", attributes=").append(idAttributes.stream().map(EAttribute::getName).toList());
                break;
            case ID_CLASS:
                sb.append(", reference=").append(idClassReference.getName());
                break;
            case SYNTHETIC_ID:
                sb.append(", syntheticName=").append(syntheticIdName);
                break;
        }
        
        sb.append('}');
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        IdConfiguration that = (IdConfiguration) obj;
        return strategy == that.strategy &&
               Objects.equals(idAttributes, that.idAttributes) &&
               Objects.equals(idClassReference, that.idClassReference) &&
               Objects.equals(syntheticIdName, that.syntheticIdName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(strategy, idAttributes, idClassReference, syntheticIdName);
    }
}