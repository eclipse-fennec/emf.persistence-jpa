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
package org.eclipse.fennec.persistence.eclipselink.copying;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeBuilder;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeContext;

/**
 * Enhanced EMF object copier with EclipseLink entity support, scenario-specific optimizations, and high-performance collection copying.
 * 
 * <p>ECopier extends EMF's standard {@link Copier} to provide specialized copying behavior optimized for the three main 
 * usage scenarios in the Eclipse Fennec Persistence framework: EclipseLink internal operations, JPARepository operations, 
 * and general EMF copying. Each scenario uses an optimized priority chain for maximum performance.</p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 * <li><strong>Scenario-Specific Factory Methods</strong> - {@link #forEclipseLinkInternal}, {@link #forJPARepository}, {@link #forGeneralEMF}</li>
 * <li><strong>Optimized Priority Chain</strong> - Context → Factory → Standard EMF with proper fallback handling</li>
 * <li><strong>High-Performance Collection Copying</strong> - O(N) {@link #copyCollection} method with shared mapping cache</li>
 * <li><strong>EclipseLink Entity Creation</strong> - Creates proper EclipseLink entities when EDynamicTypeContext is available</li>
 * <li><strong>Factory Function Support</strong> - Custom object creation via factory functions for scenarios outside EclipseLink</li>
 * <li><strong>Intelligent Containment Handling</strong> - Choice between standard copying and advanced merge logic for containment references</li>
 * </ul>
 * 
 * <h3>Three Optimized Usage Scenarios:</h3>
 * <ol>
 * <li><strong>EclipseLink Internal Operations</strong> - When EDynamicTypeContext is available (inside EclipseLink)
 *     <br>Priority: EDynamicTypeContext → Factory Function → Standard EMF</li>
 * <li><strong>JPARepository Operations</strong> - Converting DynamicEObjects to EclipseLink entities (doCopy/save methods)
 *     <br>Priority: Factory Function → Standard EMF</li>
 * <li><strong>General EMF Copying</strong> - Pure EMF object copying without EclipseLink dependencies
 *     <br>Priority: Standard EMF copying only</li>
 * </ol>
 * 
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * // Scenario 1: EclipseLink Internal (with context)
 * ECopier copier = ECopier.forEclipseLinkInternal(targetEntity, dynamicTypeContext);
 * EObject result = copier.copy(sourceObject);
 * copier.copyReferences();
 * 
 * // Scenario 2: JPARepository (with factory function)
 * ECopier copier = ECopier.forJPARepository(targetEntity, factoryFunction, true);
 * EObject result = copier.copy(sourceObject);
 * copier.copyReferences();
 * 
 * // Scenario 3: General EMF copying
 * ECopier copier = ECopier.forGeneralEMF(targetObject);
 * EObject result = copier.copy(sourceObject);
 * 
 * // High-performance collection copying (any scenario)
 * Map<EObject, EObject> mappings = ECopier.copyCollection(sourceObjects, factoryFunction);
 * 
 * // Simple static method for quick copying
 * EObject result = ECopier.copyInto(sourceObject, targetObject);
 * }</pre>
 * 
 * <h3>Performance Notes:</h3>
 * <ul>
 * <li><strong>Collection Copying</strong>: Uses O(N) shared mapping instead of O(N²) individual mapping updates</li>
 * <li><strong>Priority Chain</strong>: Optimized with direct conditional checks instead of Optional chains</li>
 * <li><strong>Scenario Methods</strong>: Pre-configured for optimal performance in each usage scenario</li>
 * </ul>
 * 
 * @author Mark Hoffmann
 * @since 09.01.2025
 * @see org.eclipse.emf.ecore.util.EcoreUtil.Copier
 * @see EDynamicTypeContext
 * @see #forEclipseLinkInternal(EObject, EDynamicTypeContext)
 * @see #forJPARepository(EObject, Function, boolean)
 * @see #forGeneralEMF(EObject)
 * @see #copyCollection(Collection, Function)
 */
public class ECopier extends Copier {

	/**
	 * Copies all attributes and references from the source object into the target object.
	 * This is a convenience method for simple EMF object copying without EclipseLink-specific features.
	 * 
	 * <p>This method performs a complete copy operation including references, making it suitable 
	 * for general EMF object copying scenarios where you need to transfer all data from one 
	 * object to another of the same type.</p>
	 * 
	 * <p><strong>Note:</strong> This method uses standard EMF copying behavior and does not 
	 * utilize EclipseLink entity creation or advanced containment merging features.</p>
	 * 
	 * @param <T> the type of EObject being copied
	 * @param source the object to copy from (must not be null)
	 * @param target the object to copy into (must not be null)
	 * @return the target object after copying (same instance as the target parameter)
	 * @throws NullPointerException if source or target is null
	 * @see Copier#copy(EObject)
	 * @see Copier#copyReferences()
	 */
	public static <T extends EObject> T copyInto(T source, T target) {
		requireNonNull(source, "Source object cannot be null");
		requireNonNull(target, "Target object cannot be null");
		
		Copier copier = new ECopier(target, null);
		EObject result = copier.copy(source);
		copier.copyReferences();

		@SuppressWarnings("unchecked")T t = (T)result;
		return t;
	}

	/**
	 * Copies a collection of objects using two-phase copying to properly handle cross-references.
	 * 
	 * <p>This method implements the same pattern used in JPARepository.doCopy() and is designed 
	 * for scenarios where you need to copy multiple objects that may reference each other. The 
	 * two-phase approach ensures that all cross-references are resolved correctly.</p>
	 * 
	 * <h4>Two-Phase Process:</h4>
	 * <ol>
	 * <li><strong>Phase 1 - Attribute Copying:</strong> Creates target objects and copies all attributes 
	 *     and containment references. Builds a mapping cache of source → target objects.</li>
	 * <li><strong>Phase 2 - Reference Copying:</strong> Resolves all cross-references using the mapping 
	 *     cache to ensure proper object identity preservation.</li>
	 * </ol>
	 * 
	 * <h4>Use Cases:</h4>
	 * <ul>
	 * <li>Converting multiple EMF DynamicEObjects to EclipseLink entities</li>
	 * <li>Copying object graphs with circular or bidirectional references</li>
	 * <li>Bulk operations that require consistent object identity mapping</li>
	 * </ul>
	 * 
	 * <h4>Example Usage:</h4>
	 * <pre>{@code
	 * // Copy multiple related objects
	 * List<EObject> sourceObjects = Arrays.asList(person1, person2, company);
	 * Function<EObject, EObject> factory = source -> createEclipseLinkEntity(source);
	 * Map<EObject, EObject> mappings = ECopier.copyCollection(sourceObjects, factory);
	 * 
	 * // Access copied objects
	 * EObject copiedPerson1 = mappings.get(person1);
	 * EObject copiedCompany = mappings.get(company);
	 * }</pre>
	 * 
	 * @param sources the collection of source objects to copy (must not be null or contain null elements)
	 * @param factoryFunction function to create target objects from source objects (must not be null)
	 * @return a map containing source → target object mappings for all copied objects
	 * @throws NullPointerException if sources or factoryFunction is null
	 * @throws IllegalArgumentException if sources contains null elements
	 * @since 1.0.0
	 */

	// ==================== SCENARIO-SPECIFIC FACTORY METHODS ====================
	
	/**
	 * Creates an ECopier optimized for EclipseLink internal operations.
	 * 
	 * <p>This factory method sets up ECopier for scenarios where EDynamicTypeContext is available
	 * and should be the primary object creation mechanism. Ideal for operations inside EclipseLink.</p>
	 * 
	 * @param targetObject the target object for copying, or null for collection copying
	 * @param context the EDynamicTypeContext for EclipseLink entity creation
	 * @return ECopier configured for EclipseLink internal usage
	 */
	public static ECopier forEclipseLinkInternal(EObject targetObject, EDynamicTypeContext context) {
		return new ECopier(targetObject, context);
	}
	
	/**
	 * Creates an ECopier optimized for JPARepository operations.
	 * 
	 * <p>This factory method sets up ECopier for JPARepository scenarios where factory function
	 * should be the primary object creation mechanism. Use for doCopy() and save() operations.</p>
	 * 
	 * @param targetObject the target object for copying, or null for collection copying
	 * @param factoryFunction function to create EclipseLink entities from source objects
	 * @param copyContainments true for initial conversion (doCopy), false for updates (save)
	 * @return ECopier configured for JPARepository usage
	 */
	public static ECopier forJPARepository(EObject targetObject, Function<EObject, EObject> factoryFunction, boolean copyContainments) {
		ECopier copier = new ECopier(targetObject, null);
		copier.setCopyFunction(factoryFunction);
		copier.setCopyContainments(copyContainments);
		return copier;
	}
	
	/**
	 * Creates an ECopier for general EMF object copying.
	 * 
	 * <p>This factory method sets up ECopier for pure EMF copying scenarios without EclipseLink
	 * context or factory functions. Uses standard EMF copying mechanisms.</p>
	 * 
	 * @param targetObject the target object for copying, or null for collection copying
	 * @return ECopier configured for general EMF usage
	 */
	public static ECopier forGeneralEMF(EObject targetObject) {
		return new ECopier(targetObject, null);
	}

	public static Map<EObject, EObject> copyCollection(Collection<? extends EObject> sources, 
													   Function<EObject, EObject> factoryFunction) {
		requireNonNull(sources, "Sources collection cannot be null");
		requireNonNull(factoryFunction, "Factory function cannot be null");
		
		// Validate that sources contains no null elements
		if (sources.stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("Sources collection cannot contain null elements");
		}
		
		Map<EObject, EObject> mappingCache = new LinkedHashMap<>();
		List<ECopier> copiers = new ArrayList<>(sources.size());
		
		// Phase 1: Copy attributes and containments, build mapping cache
		for (EObject source : sources) {
			EObject target = factoryFunction.apply(source);
			ECopier copier = new ECopier(target, null);
			copier.setCopyContainments(true);
			copier.setCopyFunction(factoryFunction);
			copiers.add(copier);
			
			// Copy attributes and containments
			EObject result = copier.copy(source);
			mappingCache.put(source, result);
		}
		
		// Phase 2: Copy cross-references using shared mapping cache (O(N) instead of O(N²))
		// Share the mapping cache with all copiers so they can find each other's copied objects
		for (ECopier copier : copiers) {
			copier.setSharedMapping(mappingCache);
			copier.copyReferences();
		}
		
		return mappingCache;
	}

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private final EObject source;
	private final EDynamicTypeContext context;
	private boolean copyContainments = false;
	private boolean mergeContainments = false;
	private Function<EObject, EObject> factoryFunction = null;
	private Map<EObject, EObject> sharedMapping = null;

	/**
	 * Creates a new ECopier instance with the specified source object and optional EclipseLink context.
	 * 
	 * <p><strong>Recommendation:</strong> Consider using the scenario-specific factory methods 
	 * ({@link #forEclipseLinkInternal}, {@link #forJPARepository}, {@link #forGeneralEMF}) 
	 * for better performance and cleaner code.</p>
	 * 
	 * <p>This constructor sets up the copier for advanced copying scenarios where you need control 
	 * over object creation and containment handling. The source object serves as the target when 
	 * the source and target have the same EClass, while the context enables EclipseLink entity creation.</p>
	 * 
	 * <h4>Parameter Usage:</h4>
	 * <ul>
	 * <li><strong>source:</strong> Used as the copy target when source.eClass() == target.eClass() during copying.
	 *     Can be null if this optimization is not needed.</li>
	 * <li><strong>context:</strong> Enables EclipseLink entity creation when available (inside EclipseLink operations).
	 *     Should be null when used outside EclipseLink (e.g., in JPARepository scenarios).</li>
	 * </ul>
	 * 
	 * <h4>Object Creation Priority Chain:</h4>
	 * <ol>
	 * <li><strong>EDynamicTypeContext</strong> (if available and returns non-null)</li>
	 * <li><strong>Factory Function</strong> (if available)</li>
	 * <li><strong>Standard EMF Copying</strong> (fallback)</li>
	 * </ol>
	 * 
	 * <h4>Typical Usage Patterns:</h4>
	 * <pre>{@code
	 * // Recommended: Use scenario-specific factory methods
	 * ECopier copier = ECopier.forJPARepository(targetEntity, factoryFunction, true);
	 * ECopier copier = ECopier.forEclipseLinkInternal(targetEntity, context);
	 * ECopier copier = ECopier.forGeneralEMF(targetEntity);
	 * 
	 * // Manual configuration (legacy approach)
	 * ECopier copier = new ECopier(targetEntity, null);
	 * copier.setCopyFunction(source -> createEclipseLinkEntity(source));
	 * copier.setCopyContainments(true);
	 * }</pre>
	 * 
	 * @param source the source object to use as target when EClasses match, or null for standard behavior
	 * @param context the EDynamicTypeContext for EclipseLink entity creation, or null for non-EclipseLink scenarios
	 */
	public ECopier(EObject source, EDynamicTypeContext context) {
		this.context = context;
		this.source = source;
	}

	/**
	 * Sets the copyContainments flag. When true, containment references are copied using 
	 * standard EMF copying behavior. Default is <code>false</code>.
	 * 
	 * <p><strong>Note:</strong> If both copyContainments and mergeContainments are true, 
	 * both operations will be performed sequentially (copy first, then merge).</p>
	 * 
	 * @param copyContainments true to enable standard EMF containment copying
	 */
	public void setCopyContainments(boolean copyContainments) {
		this.copyContainments = copyContainments;
	}
	
	/**
	 * Sets the mergeContainments flag. When true, containment references are merged using 
	 * custom logic that handles EclipseLink entity creation and ID management. 
	 * Default is <code>false</code>.
	 * 
	 * <p><strong>Note:</strong> If both copyContainments and mergeContainments are true, 
	 * both operations will be performed sequentially (copy first, then merge).</p>
	 * 
	 * @param mergeContainments true to enable custom containment merging
	 */
	public void setMergeContainments(boolean mergeContainments) {
		this.mergeContainments = mergeContainments;
	}
	
	/**
	 * Sets a custom factory function for creating copies of EObjects during the copy process.
	 * This function is used as a fallback when EDynamicTypeContext is not available (e.g., outside EclipseLink).
	 * 
	 * <p><strong>Object creation priority order:</strong></p>
	 * <ol>
	 * <li><strong>EDynamicTypeContext</strong> - Used when available (inside EclipseLink for proper entity instantiation)</li>
	 * <li><strong>Factory function</strong> - Used when context is null (e.g., JPARepository scenarios needing EclipseLink entities)</li>
	 * <li><strong>Standard EMF copying</strong> - Used when both context and factory function are null</li>
	 * </ol>
	 * 
	 * <p><strong>Behavior when factory function returns null:</strong></p>
	 * <ul>
	 * <li>If the factory function returns null, null is directly returned (no further fallback)</li>
	 * <li>This creates null entries in the copy graph, which may cause issues during reference copying</li>
	 * <li>The copy operation will continue, but the resulting object graph may be incomplete</li>
	 * </ul>
	 * 
	 * @param factoryFunction the function to create EObject copies, or null to use standard EMF copying
	 */
	public void setCopyFunction(Function<EObject, EObject> factoryFunction) {
		this.factoryFunction  = factoryFunction;
		
	}

	/**
	 * Sets a shared mapping cache for cross-reference resolution.
	 * 
	 * <p>When set, this shared mapping will be consulted first during cross-reference copying,
	 * allowing multiple ECopier instances to share object mappings efficiently. This is used
	 * by {@link #copyCollection(Collection, Function)} to avoid O(N²) performance issues.</p>
	 * 
	 * @param sharedMapping the shared mapping cache, or null to disable shared mapping
	 */
	public void setSharedMapping(Map<EObject, EObject> sharedMapping) {
		this.sharedMapping = sharedMapping;
	}

	/**
	 * Overrides the get method to check shared mapping first before falling back to local mapping.
	 * This enables efficient cross-reference resolution in copyCollection scenarios.
	 */
	@Override
	public EObject get(Object key) {
		// Check shared mapping first if available
		if (sharedMapping != null) {
			EObject sharedResult = sharedMapping.get(key);
			if (sharedResult != null) {
				return sharedResult;
			}
		}
		// Fall back to local mapping
		return super.get(key);
	}

	/**
	 * Optimized object creation for EclipseLink internal scenarios.
	 * 
	 * <p>This method provides a fast path when EDynamicTypeContext is available and should be 
	 * the primary object creation mechanism. It bypasses factory function checks for optimal 
	 * performance in EclipseLink internal operations.</p>
	 * 
	 * @param eObject the source object
	 * @return the created EclipseLink entity, or null if context cannot create it
	 */
	protected EObject createContextCopy(EObject eObject) {
		if (context != null) {
			EClass eClass = getTarget(eObject);
			return createFromContext(eClass);
		}
		return null;
	}

	/**
	 * Optimized object creation for JPARepository scenarios.
	 * 
	 * <p>This method provides a fast path when factory function is available and EDynamicTypeContext 
	 * is not needed. It's designed for JPARepository.doCopy() and save() operations where the factory 
	 * function should be the primary creation mechanism.</p>
	 * 
	 * @param eObject the source object
	 * @return the created object using factory function, or null if no factory function available
	 */
	protected EObject createRepositoryCopy(EObject eObject) {
		if (factoryFunction != null) {
			return factoryFunction.apply(eObject);
		}
		return null;
	}

	/**
	 * Standard EMF object creation for general scenarios.
	 * 
	 * <p>This method creates objects using standard EMF mechanisms without EclipseLink context 
	 * or factory functions. It's used for pure EMF copying scenarios.</p>
	 * 
	 * @param eObject the source object
	 * @return the created EMF object
	 */
	protected EObject createStandardCopy(EObject eObject) {
		return super.createCopy(eObject);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.copying.ECopier#createCopy(org.eclipse.emf.ecore.EObject)
	 */
	protected EObject createFactoryCopy(EObject eObject) {
		if (Objects.nonNull(factoryFunction)) {
			return factoryFunction.apply(eObject);
		}
		return super.createCopy(eObject);
	}
	

	/**
	 * Creates a copy of the specified EObject using the optimized priority chain.
	 * 
	 * <p>This method implements the core object creation logic with an optimized priority chain 
	 * that avoids unnecessary object allocation and method chaining. The priority order ensures 
	 * the most appropriate creation mechanism is used for each scenario.</p>
	 * 
	 * <h4>Optimized Priority Chain:</h4>
	 * <ol>
	 * <li><strong>Source Object Return</strong> - If source object matches target EClass, return it directly (fast path)</li>
	 * <li><strong>EDynamicTypeContext</strong> - Creates EclipseLink entities when context is available (EclipseLink internal scenarios)</li>
	 * <li><strong>Factory Function</strong> - Uses custom object creation function (JPARepository scenarios)</li>
	 * <li><strong>Standard EMF Copying</strong> - Falls back to EMF's default copying mechanism (general scenarios)</li>
	 * </ol>
	 * 
	 * <h4>Performance Optimizations:</h4>
	 * <ul>
	 * <li>Direct conditional checks instead of Optional chains</li>
	 * <li>Proper fallback handling when context returns null</li>
	 * <li>No temporary object allocation in hot code paths</li>
	 * </ul>
	 * 
	 * @param eObject the source object to create a copy of
	 * @return the created copy object using the most appropriate creation mechanism
	 * @throws NullPointerException if eObject is null
	 * @see #createContextCopy(EObject)
	 * @see #createRepositoryCopy(EObject)  
	 * @see #createStandardCopy(EObject)
	 */
	@Override
	protected EObject createCopy(EObject eObject) {
		requireNonNull(eObject);
		EClass eClass = getTarget(eObject);
		
		// Fast path: Return source if it matches the target class (existing behavior)
		if (source != null && source.eClass().equals(eClass)) {
			return source;
		}
		
		// Optimized priority chain for object creation
		// Priority 1: EDynamicTypeContext (EclipseLink internal scenarios)
		if (context != null) {
			EObject contextResult = createFromContext(eClass);
			if (contextResult != null) {
				return contextResult;
			}
			// Fall through to factory function if context returns null
		}
		
		// Priority 2: Factory Function (JPARepository scenarios)  
		if (factoryFunction != null) {
			return factoryFunction.apply(eObject);
		}
		
		// Priority 3: Standard EMF copying (general scenarios)
		return super.createCopy(eObject);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.util.EcoreUtil.Copier#copyContainment(org.eclipse.emf.ecore.EReference, org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected void copyContainment(EReference eReference, EObject eObject, EObject copyEObject) {
		if (copyContainments) {
			super.copyContainment(eReference, eObject, copyEObject);
		}
		if (mergeContainments) {
			mergeContainments(eReference, eObject, copyEObject);
		}
	}

	/**
	 * Performs intelligent merging of containment references using ID-based matching and entity creation.
	 * This method provides sophisticated handling of containment relationships, particularly useful 
	 * for updating existing managed entities with changes from modified EMF objects.
	 * 
	 * <p>This method is called automatically when {@code mergeContainments} is enabled and provides 
	 * more advanced behavior than standard EMF copying for containment references.</p>
	 * 
	 * <h4>Merge Logic:</h4>
	 * <ul>
	 * <li><strong>ID-based Matching:</strong> Uses {@link EcoreUtil#getID(EObject)} to match existing objects</li>
	 * <li><strong>Update Existing:</strong> When objects with same ID exist, copies attributes from source to existing managed target</li>
	 * <li><strong>Create New:</strong> When no matching ID exists, creates new EclipseLink entity via factory function</li>
	 * <li><strong>Preserve Order:</strong> Maintains the order of objects as specified in the source list</li>
	 * <li><strong>Clear Missing:</strong> Removes objects from target that are not present in source</li>
	 * </ul>
	 * 
	 * <h4>Use Cases:</h4>
	 * <ul>
	 * <li>JPARepository save operations where you need to merge changes into existing managed entities</li>
	 * <li>Updating containment collections while preserving JPA entity relationships</li>
	 * <li>Scenarios where standard EMF copying would break JPA entity management</li>
	 * </ul>
	 * 
	 * <h4>Requirements:</h4>
	 * <ul>
	 * <li>Objects must have IDs set via {@link EcoreUtil#setID(EObject, String)} for matching to work</li>
	 * <li>Factory function should be configured for creating new EclipseLink entities</li>
	 * <li>Works with both single-valued and multi-valued containment references</li>
	 * </ul>
	 * 
	 * @param eReference the containment reference being processed
	 * @param eObject the source object containing the reference values
	 * @param copyEObject the target object where reference values will be merged
	 */
	protected void mergeContainments(EReference eReference, EObject eObject, EObject copyEObject) {
		if (eObject.eIsSet(eReference))
	      {
	        EStructuralFeature.Setting targetSetting = getTarget(eReference, eObject, copyEObject);
	        if (targetSetting != null)
	        {
	          Object sourceValue = eObject.eGet(eReference);
	          Object targetValue = copyEObject.eGet(eReference);
	          
	          if (eReference.isMany())
	          {
	            @SuppressWarnings("unchecked")
	            List<EObject> sourceList = (List<EObject>)sourceValue;
	            @SuppressWarnings("unchecked")
	            List<EObject> targetList = (List<EObject>)targetValue;
	            
	            // Create ID maps for efficient lookup
	            Map<String, EObject> sourceById = new HashMap<>();
	            Map<String, EObject> targetById = new HashMap<>();
	            
	            for (EObject obj : sourceList) {
	              String id = org.eclipse.emf.ecore.util.EcoreUtil.getID(obj);
	              if (id != null) sourceById.put(id, obj);
	            }
	            
	            for (EObject obj : targetList) {
	              String id = org.eclipse.emf.ecore.util.EcoreUtil.getID(obj);
	              if (id != null) targetById.put(id, obj);
	            }
	            
	            // Clear the target list and rebuild it
	            targetList.clear();
	            
	            // Add/update elements from source
	            for (EObject sourceObj : sourceList) {
	              String id = org.eclipse.emf.ecore.util.EcoreUtil.getID(sourceObj);
	              EObject targetObj = targetById.get(id);
	              
	              if (targetObj != null) {
	                // Existing element - copy attributes from source to existing managed target
	                copyInto(sourceObj, targetObj);
	                targetList.add(targetObj);
	              } else {
	                // New element - create EclipseLink entity and copy attributes
	                EObject copiedObj = createFactoryCopy(sourceObj);
	                // Copy all attributes including ID from the source
	                copyInto(sourceObj, copiedObj);
	                targetList.add(copiedObj);
	              }
	            }
	          }
	          else
	          {
	            // Single-valued reference
	            if (sourceValue == null) {
	              targetSetting.set(null);
	            } else {
	              EObject sourceObj = (EObject)sourceValue;
	              EObject targetObj = (EObject)targetValue;
	              
	              String sourceId = org.eclipse.emf.ecore.util.EcoreUtil.getID(sourceObj);
	              String targetId = targetObj != null ? org.eclipse.emf.ecore.util.EcoreUtil.getID(targetObj) : null;
	              
	              if (targetObj != null && Objects.equals(sourceId, targetId)) {
	                // Same object - copy attributes from source to existing managed target
	                copyInto(sourceObj, targetObj);
	              } else {
	                // Different or new object - create EclipseLink entity and replace it
	                EObject copiedObj = createFactoryCopy(sourceObj);
	                // Copy all attributes including ID from the source
	                copyInto(sourceObj, copiedObj);
	                targetSetting.set(copiedObj);
	              }
	            }
	          }
	        }
	      }
	      else
	      {
	        // Source is not set - clear the target
	        EStructuralFeature.Setting targetSetting = getTarget(eReference, eObject, copyEObject);
	        if (targetSetting != null)
	        {
	          if (eReference.isMany())
	          {
	            @SuppressWarnings("unchecked")
	            List<EObject> targetList = (List<EObject>)copyEObject.eGet(eReference);
	            targetList.clear();
	          }
	          else
	          {
	            targetSetting.set(null);
	          }
	        }
	      }
	}

	/**
	 * Called to handle the copying of a cross reference;
	 * this adds values or sets a single value as appropriate for the multiplicity
	 * while omitting any bidirectional reference that isn't in the copy map.
	 * @param eReference the reference to copy.
	 * @param eObject the object from which to copy.
	 * @param copyEObject the object to copy to.
	 */
	@Override
	protected void copyReference(EReference eReference, EObject eObject, EObject copyEObject)
	{
		if (eObject.eIsSet(eReference))
		{
			EStructuralFeature.Setting setting = getTarget(eReference, eObject, copyEObject);
			if (setting != null)
			{
				Object value = eObject.eGet(eReference, resolveProxies);
				if (eReference.isMany())
				{
					@SuppressWarnings("unchecked") InternalEList<EObject> source = (InternalEList<EObject>)value;
					@SuppressWarnings("unchecked") InternalEList<EObject> target = (InternalEList<EObject>)setting;
					if (source.isEmpty())
					{
						target.clear();
					}
					else
					{
						boolean isBidirectional = eReference.getEOpposite() != null;
						int index = 0;
						for (Iterator<EObject> k = resolveProxies ? source.iterator() : source.basicIterator(); k.hasNext();)
						{
							EObject referencedEObject = k.next();
							EObject copyReferencedEObject = get(referencedEObject);
							if (copyReferencedEObject == null)
							{
								if (useOriginalReferences && !isBidirectional)
								{
									target.addUnique(index, referencedEObject);
									++index;
								}
							}
							else
							{
								if (isBidirectional)
								{
									int position = target.indexOf(copyReferencedEObject);
									if (position == -1)
									{
										target.addUnique(index, copyReferencedEObject);
									}
									else if (index != position)
									{
										target.move(index, copyReferencedEObject);
									}
								}
								else
								{
									boolean exists = index < target.size() && copyReferencedEObject.equals(target.get(index));
									if (!exists) {
										target.addUnique(index, copyReferencedEObject);
									}
								}
								++index;
							}
						}
					}
				}
				else
				{
					if (value == null)
					{
						setting.set(null);
					}
					else
					{
						Object copyReferencedEObject = get(value);
						if (copyReferencedEObject == null)
						{
							if (useOriginalReferences && eReference.getEOpposite() == null)
							{
								setting.set(value);
							}
						}
						else
						{
							setting.set(copyReferencedEObject);
						}
					}
				}
			}
		}
	}

	/**
	 * Creates a new EclipseLink entity instance for the given EClass using the EDynamicTypeContext.
	 * This method is the preferred way to create objects when running inside EclipseLink, as it
	 * ensures proper entity instantiation with all EclipseLink-specific initialization.
	 * 
	 * <p><strong>Process:</strong></p>
	 * <ol>
	 * <li>Looks up the {@link EDynamicTypeBuilder} for the EClass from the context</li>
	 * <li>Gets the {@link EDynamicType} from the builder</li>
	 * <li>Extracts the EclipseLink {@link org.eclipse.persistence.descriptors.ClassDescriptor}</li>
	 * <li>Uses {@link EDynamicHelper#createInstance} to create a properly initialized EclipseLink entity</li>
	 * </ol>
	 * 
	 * <p><strong>When this is used:</strong></p>
	 * <ul>
	 * <li>Inside EclipseLink operations where the EDynamicTypeContext is available</li>
	 * <li>When creating contained objects that need to be proper EclipseLink entities</li>
	 * <li>Takes priority over factory functions in the object creation hierarchy</li>
	 * </ul>
	 * 
	 * @param eClass the EClass to create an EclipseLink entity instance for
	 * @return a new EclipseLink entity instance, or null if the context cannot create an instance for this EClass
	 */
	private EObject createFromContext(EClass eClass) {
		Objects.requireNonNull(context);
		return context.getOptionalETypeBuilder(eClass).
				map(EDynamicTypeBuilder::getType).
				map(EDynamicType::getDescriptor).
				map(EDynamicHelper::createInstance).
				orElse(null);
	}
}
