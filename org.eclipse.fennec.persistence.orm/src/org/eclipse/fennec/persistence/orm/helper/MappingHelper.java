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
package org.eclipse.fennec.persistence.orm.helper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAnnotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.fennec.persistence.eorm.AccessType;
import org.eclipse.fennec.persistence.eorm.Base;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.EFeatureObject;
import org.eclipse.fennec.persistence.eorm.ENamedBase;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.processor.ProcessingContext;

/**
 * Helper class for the {@link EntityMapper}
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public class MappingHelper {

	private static final Logger LOG = Logger.getLogger(MappingHelper.class.getName());

	enum MappingType {
		ONE_TO_ONE,
		ONE_TO_MANY,
		MANY_TO_MANY,
		MANY_TO_ONE
	}

	class MappedBy {
		EReference reference;
		MappingType mappingType = null;
		String mappedByName = null;
	}

	private static final List<String> RESERVED_WORDS = List.of(
		// SQL-92/99 reserved keywords that are strictly reserved across
		// H2, PostgreSQL, MySQL, and Oracle as unquoted identifiers
		"select", "from", "where", "update", "insert", "delete",
		"create", "drop", "alter", "grant", "revoke",
		"table", "column", "index", "key", "value",
		"order", "group", "having", "distinct",
		"join", "left", "right", "inner", "outer", "cross", "natural", "on",
		"and", "or", "not", "in", "is", "between", "like", "exists",
		"null", "true", "false", "all", "any",
		"primary", "foreign", "unique", "check", "default", "constraint",
		"references", "cascade", "restrict",
		"user",
		// SQL data type keywords reserved as identifiers
		"bigint", "integer", "float", "double", "decimal", "numeric",
		"char", "varchar", "boolean", "interval",
		// Temporal keywords reserved in SQL
		"year", "month", "day", "hour", "minute", "second",
		// Other strictly reserved keywords
		"end", "limit", "offset", "row", "rows",
		"trigger", "view", "sequence", "function", "procedure",
		"cross"
	);

	/**
	 * Checks if the given name is a SQL reserved word and logs a warning if so.
	 * The name is returned unchanged — the user is responsible for choosing
	 * a non-conflicting name or providing an explicit column name via ExtendedMetaData annotation.
	 * @param name the name to check
	 * @return the name, unchanged
	 */
	public static String checkReservedName(String name) {
		return checkReservedName(name, "Column", null, null);
	}

	/**
	 * Checks if the given name is a SQL reserved word and logs a warning if so.
	 * The name is returned unchanged.
	 * @param name the name to check
	 * @param context additional context for the warning message (e.g. "attribute", "entity")
	 * @return the name, unchanged
	 */
	public static String checkReservedName(String name, String context) {
		return checkReservedName(name, context, null, null);
	}

	/**
	 * Checks if the given name is a SQL reserved word and reports a warning diagnostic
	 * if so. The name is returned unchanged — the user is responsible for choosing a
	 * non-conflicting name or providing an explicit column name via ExtendedMetaData
	 * annotation.
	 *
	 * @param name the name to check
	 * @param context additional context for the warning message (e.g. "Feature", "Entity")
	 * @param diagnostics the diagnostic channel; {@code null} falls back to JUL
	 * @param element the affected model element, may be {@code null}
	 * @return the name, unchanged
	 */
	public static String checkReservedName(String name, String context, ProcessingContext diagnostics, Object element) {
		if (isNull(name)) {
			return null;
		}
		if (RESERVED_WORDS.contains(name.toLowerCase())) {
			String message = String.format("%s name '%s' is a SQL reserved word and may cause issues with some databases. "
					+ "Consider renaming or using an ExtendedMetaData annotation to set an explicit column/table name.",
					context, name);
			if (nonNull(diagnostics)) {
				if (nonNull(element)) {
					diagnostics.warning(MappingContext.DIAGNOSTIC_SOURCE, message, element);
				} else {
					diagnostics.warning(MappingContext.DIAGNOSTIC_SOURCE, message);
				}
			} else {
				LOG.log(Level.WARNING, message);
			}
		}
		return name;
	}
	
	/**
	 * Verifies of the given name is a reserved {@link String} in JPA
	 * @param name the name to verify
	 * @return the escaped version of this name 
	 */
	public static boolean isReservedName(String name) {
		if (isNull(name)) {
			return false;
		}
		return RESERVED_WORDS.contains(name.toLowerCase());
	}

	/**
	 * Creates a {@link ENamedBase} out of an {@link EStructuralFeature}
	 * @param <T> the base type
	 * @param base the base instance
	 * @param feature the {@link EStructuralFeature}
	 * @return the {@link ENamedBase} instance
	 */
	public static <T extends ENamedBase> T createNamedBase(T base, EStructuralFeature feature) {
		return createNamedBase(base, feature, null);
	}

	/**
	 * Creates a {@link ENamedBase} out of an {@link EStructuralFeature}, reporting
	 * reserved-name findings to the given diagnostic channel.
	 * @param <T> the base type
	 * @param base the base instance
	 * @param feature the {@link EStructuralFeature}
	 * @param diagnostics the diagnostic channel; {@code null} falls back to JUL
	 * @return the {@link ENamedBase} instance
	 */
	public static <T extends ENamedBase> T createNamedBase(T base, EStructuralFeature feature, ProcessingContext diagnostics) {
		String name = checkReservedName(getFeatureName(feature), "Feature", diagnostics, feature);
		base.setName(name);
		EFeatureObject efa = EORMFactory.eINSTANCE.createEFeatureObject();
		efa.setFeature(feature);
		efa.setName(feature.getName());
		base.setAccessibleObject(efa);
		base.setAccess(AccessType.FIELD);
		return base;
	}

	/**
	 * Creates a {@link Base} out of an {@link EStructuralFeature}
	 * @param <T> the base type
	 * @param base the base instance
	 * @param feature the {@link EStructuralFeature}
	 * @return the {@link Base} instance
	 */
	public static <T extends Base> T createBase(T base, EStructuralFeature feature, boolean strict) {
		return createBase(base, feature, strict, null);
	}

	/**
	 * Creates a {@link Base} out of an {@link EStructuralFeature}, reporting
	 * reserved-name findings to the given diagnostic channel.
	 * @param <T> the base type
	 * @param base the base instance
	 * @param feature the {@link EStructuralFeature}
	 * @param strict {@code true} takes names as-is without reserved-word checks
	 * @param diagnostics the diagnostic channel; {@code null} falls back to JUL
	 * @return the {@link Base} instance
	 */
	public static <T extends Base> T createBase(T base, EStructuralFeature feature, boolean strict, ProcessingContext diagnostics) {
		base = createNamedBase(base, feature, diagnostics);
		Column column = EORMFactory.eINSTANCE.createColumn();
		String name = strict ? getFeatureName(feature) : checkReservedName(getFeatureName(feature), "Column", diagnostics, feature);
		column.setName(name);
		column.setNullable(!feature.isRequired());
		column.setUnique(feature.isUnique());
		column.setInsertable(true);
		column.setUpdatable(feature.isChangeable());
		base.setColumn(column);
		return base;
	}
	
	public static <T extends Base> String getBaseName(T base) {
		requireNonNull(base);
		Column c = base.getColumn();
		return nonNull(c) ? c.getName() : base.getName();
	}
	
	public static String getFeatureName(EStructuralFeature feature) {
		requireNonNull(feature);
		String name = getAnnotation(feature, ExtendedMetaData.ANNOTATION_URI, "name");
		if (isNull(name)) {
			name = feature.getName();
		}
		return name;
	}

	/**
	 * Creates a {@link BaseRef} out of an {@link EStructuralFeature}
	 * @param <T> the base type
	 * @param baseRef the baseRef instance
	 * @param feature the {@link EStructuralFeature}
	 * @return the {@link BaseRef} instance
	 */
	public static <T extends BaseRef> T createBaseRef(T baseRef, EStructuralFeature feature) {
		baseRef = createNamedBase(baseRef, feature);
		return baseRef;
	}

	/**
	 * Tries to unwrap the given object an turn it into a {@link List} of {@link EObject}
	 * @param object the object, can be <code>null</code>
	 * @return a {@link List} of {@link EObject}'s or an empty list
	 */
	public static List<EObject> unwrapEObject(Object object) {
		return unwrapObject(object).stream().
				filter(EObject.class::isInstance).
				map(EObject.class::cast).
				toList();
	}

	/**
	 * Tries to unwrap the given object an turn it into a {@link List} of {@link Object}
	 * @param object the object, can be <code>null</code>
	 * @return a {@link List} of {@link Object}'s or an empty list
	 */
	public static List<?> unwrapObject(Object object) {
		if (isNull(object)) {
			return Collections.emptyList();
		} else if (object instanceof Collection<?>) {
			return new ArrayList<>((Collection<?>)object);
		} else {
			return Collections.singletonList(object);
		}
	}

	/**
	 * Returns <code>true</code>, if the toCompare value or {@link Collection} is / are elements of the 
	 * object's reference value
	 * @param toCompare the object or collection to compare
	 * @param object the {@link EObject}, to get the reference value from
	 * @param reference the {@link EReference}, to get the value from the object
	 * @return <code>true</code>, if the toCompare value or {@link Collection} is / are elements of the 
	 * object's reference value, otherwise false
	 */
	public static boolean refValueEquals(Object toCompare, EObject object, EStructuralFeature reference) {
		if (isNull(object) || isNull(reference) || isNull(toCompare)) {
			return false;
		}
		Object objectRefValue = object.eGet(reference);
		if (isNull(objectRefValue)) {
			return false;
		}
		List<?> unwrapped = MappingHelper.unwrapObject(objectRefValue);
		List<?> unwrappedToCompare = MappingHelper.unwrapObject(toCompare);
		return unwrapped.containsAll(unwrappedToCompare);
	}

	/**
	 * Returns <code>true</code>, if the toCompare value or {@link Collection} is / are elements of the 
	 * object's reference value list
	 * @param toCompare the object or collection to compare
	 * @param objects the {@link List} of {@link EObject}, to get the reference value from
	 * @param reference the {@link EReference}, to get the value from the object
	 * @return <code>true</code>, if the toCompare value or {@link Collection} is / are elements of the 
	 * object's reference value, otherwise false
	 */
	public static boolean refValueEqualsAll(Object toCompare, Object object, EStructuralFeature reference) {
		if (object instanceof Collection<?> collection) {
			return collection.stream().filter(EObject.class::isInstance).map(EObject.class::cast).allMatch(eo->refValueEquals(toCompare, eo, reference));
		} else if (object instanceof EObject eObject) {
				return refValueEquals(toCompare, eObject, reference);
		} else {
			return false;
		}
	}

	/**
	 * Returns <code>true</code>, if the toCompare value or {@link Collection} is / are elements of the 
	 * object's reference value
	 * @param toCompare the object or collection to compare
	 * @param object the {@link EObject}, to get the reference value from
	 * @param reference the {@link EReference}, to get the value from the object
	 * @return <code>true</code>, if the toCompare value or {@link Collection} is / are elements of the 
	 * object's reference value, otherwise false
	 */
	public static boolean refValueContains(Object toCompare, EObject object, EStructuralFeature reference) {
		if (isNull(object) || isNull(reference) || isNull(toCompare)) {
			return false;
		}
		Object objectRefValue = object.eGet(reference);
		if (isNull(objectRefValue)) {
			return false;
		}
		List<EObject> unwrapped = MappingHelper.unwrapEObject(objectRefValue);//liste mit
		List<EObject> unwrappedToCompare = MappingHelper.unwrapEObject(toCompare);
		for (EObject refValue : unwrapped) {
			for (EObject compareValue : unwrappedToCompare) {
				EObject refValuecopy = EcoreUtil.copy(refValue);
				if (EcoreUtil.equals(refValuecopy, compareValue)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code>, if the toCompare value or {@link Collection} is / are elements of the 
	 * object's reference value {@link List}
	 * @param toCompare the object or collection to compare
	 * @param object the object value that can be a list, to get the reference value from
	 * @param reference the {@link EReference}, to get the value from the object
	 * @return <code>true</code>, if the toCompare value or {@link Collection} is / are elements of the 
	 * object's reference value, otherwise false
	 */
	public static boolean refValueContainsAll(Object toCompare, Object object, EStructuralFeature reference) {
		if (object instanceof Collection<?> collection) {
			return collection.
					stream().
					filter(EObject.class::isInstance).
					map(EObject.class::cast).
					allMatch(eo->refValueContains(toCompare, eo, reference));
		} else if (object instanceof EObject eObject) {
				return refValueContains(toCompare, eObject, reference);
		} else {
			return false;
		}
	}

	/**
	 * Returns <code>true</code>, if the given child belongs to the given parent, otherwise <code>false</code>
	 * @param parent the parent
	 * @param child the child
	 * @return <code>true</code>, if the given child belongs to the given parent, otherwise <code>false</code>
	 */
	public static boolean isContainmentParentChild(EObject parent, Object child) {
		return  nonNull(parent) && 
				nonNull(child) && 
				child instanceof EObject eChild && 
				parent.equals(eChild.eContainer());
	}
	
	/**
	 * Returns <code>true</code>, if the given reference defines some kind of containment relation, the given {@link EObject}'s belong to.
	 * In an non-containing reference, also the opposite reference is inspected for containment definition. 
	 * @param object the {@link EObject} parent
	 * @param value the value to be set
	 * @param reference the {@link EReference}
	 * @return <code>true</code>, if the given reference defines some kind of containment relation
	 */
	public static boolean areInContainmentRelation(EObject object, EObject value, EReference reference) {
		requireNonNull(reference);
		EReference opposite = reference.getEOpposite();
		/*
		 * We stop here, if one of the EObjects is null or neither the references nor their opposite (if they exist) are containments 
		 */
		if (isNull(object) || 
				isNull(value) || 
				!(reference.isContainment() || 
				nonNull(opposite) && opposite.isContainment())) {
			return false;
		}
		EClass objectType = object.eClass();
		EClass valueType = value.eClass();
		EClass refType = reference.getEReferenceType();
		EClass containerType = reference.getEContainingClass();
		return objectType.equals(containerType) && 
				valueType.equals(refType) && 
				value.equals(object.eGet(reference));
	}

	/**
	 * When we have a containment with opposites defined, this method returns <code>true</code>,
	 * if we are currently on the child side. Otherwise <code>false</code> will returned. 
	 * @param reference the {@link EReference}
	 * @return <code>true</code>, if we are on the child side of a bi-directional containment relation.
	 */
	public static boolean isContainmentChild(EReference reference) {
		return nonNull(reference) && 
				isOppositeRelation(reference) && 
				reference.getEOpposite().isContainment();
	}


	/**
	 * @param object
	 * @param value
	 * @param reference
	 */
	public static void setValue(EObject object, Object value, EReference reference) {
		/*
		 * Set the child to the parent. The parent might be a cloned one.
		 * We need to distinguish between unary and many values
		 */
		if (reference.isMany()) {
			@SuppressWarnings("unchecked")
			Collection<Object> c = (Collection<Object>) object.eGet(reference);
			if (value instanceof Collection) {
				c.addAll((Collection<?>) value);
			} else {
				c.add(value);
			}
		} else {
			object.eSet(reference, value);
		}
	}

	/**
	 * Returns <code>true</code>, if the {@link EReference} is part of a non-containment bi-directional relation, 
	 * otherwise <code>false</code>
	 * @param reference the {@link EReference}
	 * @return <code>true</code>, if the {@link EReference} is part of a non-containment bi-directional relation
	 */
	public static boolean isNonContainmentOppositeRelation(EReference reference) {
		return nonNull(reference) && 
				isOppositeRelation(reference) && 
				!reference.isContainment() && 
				!reference.getEOpposite().isContainment();
	}

	/**
	 * When we have an opposites defined, this method returns <code>true</code>, otherwise <code>false</code>
	 * @return <code>true</code>, if we are in an bi-directional relation.
	 */
	public static boolean isOppositeRelation(EReference reference) {
		return nonNull(reference) && 
				nonNull(reference.getEOpposite());
	}
	
	/**
	 * Returns <code>true</code> if the {@link EReference} or its opposite, if it exists, is a containment 
	 * @param reference the {@link EReference}
	 * @return <code>true</code> if the {@link EReference} or its opposite, if it exists, is a containment 
	 */
	public static boolean isContainmentReference(EReference reference) {
		if (isNull(reference)) {
			return false;
		}
		return reference.isContainment() || 
				isOppositeRelation(reference) && 
				reference.getEOpposite().isContainment();
		
	}
	
}
