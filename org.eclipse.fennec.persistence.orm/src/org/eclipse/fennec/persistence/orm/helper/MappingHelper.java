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
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.ECollections;
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
import org.eclipse.fennec.persistence.helper.DatabaseAnnotations;
import org.eclipse.fennec.persistence.helper.EMaps;
import org.eclipse.fennec.persistence.helper.ReservedWords;
import org.eclipse.fennec.persistence.helper.ReservedWords.Kind;
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
		if (isReservedName(name)) {
			String message = String.format("%s name '%s' is a SQL reserved word and may cause issues with some databases. "
					+ "Consider renaming, declaring an explicit column/table name in the eorm mapping, "
					+ "or enabling useNamesFromExtendedMetaData with an ExtendedMetaData name.",
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
	 * @param name the name to verify, any case
	 * @return {@code true} if a supported database refuses the name unquoted as a table or a
	 *         column name ({@link ReservedWords})
	 */
	public static boolean isReservedName(String name) {
		return ReservedWords.isReserved(name, Kind.COLUMN) || ReservedWords.isReserved(name, Kind.TABLE);
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
	 * Creates a {@link ENamedBase} out of an {@link EStructuralFeature}.
	 * @param <T> the base type
	 * @param base the base instance
	 * @param feature the {@link EStructuralFeature}
	 * @param diagnostics the diagnostic channel; unused, the attribute name is no SQL name
	 * @return the {@link ENamedBase} instance
	 */
	public static <T extends ENamedBase> T createNamedBase(T base, EStructuralFeature feature, ProcessingContext diagnostics) {
		// the JPA attribute name is the feature name — the runtime resolves features by it. It
		// never reaches the SQL; the column it is stored in is named by createBase.
		base.setName(feature.getName());
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
	 * @param strict {@code true} takes derived names as-is, without escaping reserved words
	 * @param diagnostics the diagnostic channel; {@code null} falls back to JUL
	 * @return the {@link Base} instance
	 */
	public static <T extends Base> T createBase(T base, EStructuralFeature feature, boolean strict, ProcessingContext diagnostics) {
		base = createNamedBase(base, feature, diagnostics);
		Column column = EORMFactory.eINSTANCE.createColumn();
		String name = getBaseColumnName(feature, diagnostics, strict);
		String derived = getBaseColumnName(feature, diagnostics, true);
		if (!name.equals(derived)) {
			reportEscaped("Column", derived, name, Kind.COLUMN, diagnostics, feature);
		}
		column.setName(name);
		column.setNullable(!feature.isRequired());
		column.setUnique(feature.isUnique());
		column.setInsertable(true);
		column.setUpdatable(feature.isChangeable());
		base.setColumn(column);
		return base;
	}
	
	/**
	 * The column a feature of a {@link Base} mapping is stored in, as a non-strict mapping names
	 * it — see {@link #getBaseColumnName(EStructuralFeature, ProcessingContext, boolean)}.
	 * @param feature the {@link EStructuralFeature}
	 * @param context the mapping run, decides about {@code ExtendedMetaData} names; may be {@code null}
	 * @return the column name, never {@code null}
	 */
	public static String getBaseColumnName(EStructuralFeature feature, ProcessingContext context) {
		return getBaseColumnName(feature, context, false);
	}

	/**
	 * The column a feature of a {@link Base} mapping is stored in: the fixed name of a map entry's
	 * {@code key}/{@code value}, the name an existing schema declares for it
	 * ({@link DatabaseAnnotations#COLUMN_NAME}, issue #296), or the derived one. A derived name
	 * that a supported database refuses unquoted is {@link ReservedWords#escape escaped}
	 * ({@code value} becomes {@code value_}, issue #399) unless the mapping is strict; a fixed or
	 * declared name is taken as it is.
	 * @param feature the {@link EStructuralFeature}
	 * @param context the mapping run, decides about {@code ExtendedMetaData} names; may be {@code null}
	 * @param strict {@code true} takes a derived name as it is
	 * @return the column name, never {@code null}
	 */
	public static String getBaseColumnName(EStructuralFeature feature, ProcessingContext context, boolean strict) {
		String name = mapEntryColumnName(feature);
		if (isNull(name)) {
			name = DatabaseAnnotations.columnName(feature);
		}
		if (nonNull(name)) {
			return name;
		}
		name = getColumnName(feature, useNamesFromExtendedMetaData(context));
		return strict ? name : ReservedWords.escape(name, Kind.COLUMN);
	}

	/**
	 * The table an entity derives: the name upper-cased, {@link ReservedWords#escape escaped}
	 * unless the mapping is strict (issue #399).
	 * @param name the derived table name, any case
	 * @param strict {@code true} takes the name as it is
	 * @return the table name
	 */
	public static String deriveTableName(String name, boolean strict) {
		String table = name.toUpperCase(Locale.ROOT);
		return strict ? table : ReservedWords.escape(table, Kind.TABLE);
	}

	/**
	 * Reports a derived name the mapping escaped (issue #399) as an info: the schema differs from
	 * the model there, which matters to whoever creates or reads the tables outside the unit.
	 * @param what {@code Column} or {@code Table}
	 * @param derived the name as derived
	 * @param escaped the name used instead
	 * @param kind where the name is used
	 * @param diagnostics the diagnostic channel; {@code null} falls back to JUL
	 * @param element the affected model element
	 */
	public static void reportEscaped(String what, String derived, String escaped, Kind kind,
			ProcessingContext diagnostics, Object element) {
		String message = String.format("%s name '%s' is reserved on %s and is named '%s' instead. "
				+ "Declare a name in the eorm mapping or as database name to choose another.",
				what, derived, ReservedWords.reservingDialects(derived, kind), escaped);
		if (nonNull(diagnostics)) {
			diagnostics.info(MappingContext.DIAGNOSTIC_SOURCE, message, element);
		} else {
			LOG.log(Level.INFO, message);
		}
	}

	/**
	 * The column name for a map entry's {@code key}/{@code value} feature, or {@code null} for
	 * anything else (issue #185).
	 * <p>
	 * Both names are SQL reserved words, and the ordinary answer to that — {@code
	 * checkReservedName} warns and hands the name back, the model author renames or annotates
	 * — cannot apply here: EMF fixes these two names, and the model belongs to the user. So the
	 * mapping renames them, once and for every dialect, rather than leaving a
	 * {@code CREATE TABLE} that {@code create-or-extend-tables} swallows and a missing table
	 * that surfaces at the first insert.
	 */
	private static String mapEntryColumnName(EStructuralFeature feature) {
		if (!EMaps.isMapEntry(feature.getEContainingClass())) {
			return null;
		}
		return switch (feature.getName()) {
		case EMaps.KEY_FEATURE -> "MAP_KEY";
		case EMaps.VALUE_FEATURE -> "MAP_VALUE";
		default -> null;
		};
	}

	public static <T extends Base> String getBaseName(T base) {
		requireNonNull(base);
		Column c = base.getColumn();
		return nonNull(c) ? c.getName() : base.getName();
	}
	
	/**
	 * The column name derived for a feature: its name, or — only when asked to (issue #314) —
	 * its {@code ExtendedMetaData} {@code name}. That name describes an XML/JSON serialization
	 * and is no SQL identifier by guarantee ({@code marker-color}), so it is opt-in.
	 * @param feature the feature
	 * @param useNamesFromExtendedMetaData whether the annotated name wins
	 * @return the column name, never {@code null}
	 */
	public static String getColumnName(EStructuralFeature feature, boolean useNamesFromExtendedMetaData) {
		requireNonNull(feature);
		String name = useNamesFromExtendedMetaData
				? getAnnotation(feature, ExtendedMetaData.ANNOTATION_URI, "name") : null;
		return isNull(name) ? feature.getName() : name;
	}

	/**
	 * The table name derived for an EClass: its name, or — only when asked to (issue #314) —
	 * its {@code ExtendedMetaData} {@code name}.
	 * @param eClass the EClass
	 * @param useNamesFromExtendedMetaData whether the annotated name wins
	 * @return the table name, never {@code null}
	 */
	public static String getTableName(EClass eClass, boolean useNamesFromExtendedMetaData) {
		requireNonNull(eClass);
		String name = useNamesFromExtendedMetaData
				? getAnnotation(eClass, ExtendedMetaData.ANNOTATION_URI, "name") : null;
		return isNull(name) ? eClass.getName() : name;
	}

	/**
	 * @return whether the mapping run behind the given context takes names from
	 *         {@code ExtendedMetaData}; {@code false} without a mapping context
	 */
	public static boolean useNamesFromExtendedMetaData(ProcessingContext context) {
		return context instanceof MappingContext mappingContext
				&& mappingContext.isUseNamesFromExtendedMetaData();
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
			if (value instanceof Collection<?> values) {
				// a merged collection can carry a null for an element whose original is not in
				// the target session (EclipseLink's ordered-list merge, issue #330); an EMF list
				// holds no nulls, and adding one fails in the inverse maintenance
				List<Object> content = new ArrayList<>(values.size());
				for (Object element : values) {
					if (nonNull(element)) {
						content.add(element);
					}
				}
				if (c instanceof EList<Object> list) {
					// the value is the complete content: set it, removals and order included —
					// adding only left removed children and the old order in the shared cache (#338)
					ECollections.setEList(list, content);
				} else {
					c.addAll(content);
				}
			} else if (nonNull(value)) {
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
