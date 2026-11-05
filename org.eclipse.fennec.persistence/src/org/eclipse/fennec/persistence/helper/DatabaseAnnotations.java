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
package org.eclipse.fennec.persistence.helper;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * The database names of a model: where its types and features live in an <em>existing</em>
 * schema (issue #296). The schema import writes them; the relational mapping honours them, so a
 * model imported from a database maps back onto that database exactly — and a model edited by
 * hand afterwards (an id attribute turned into a reference, say) keeps mapping onto it.
 * <p>
 * All names are taken literally: no case change, no reserved-word escape. An element without
 * the annotation is mapped as before.
 * <table>
 * <caption>Annotation details, source {@value #ANNOTATION_SOURCE}</caption>
 * <tr><th>Element</th><th>Detail</th><th>Meaning</th></tr>
 * <tr><td>EPackage</td><td>{@value #SCHEMA} / {@value #CATALOG}</td><td>the namespace the package
 * was read from — informational</td></tr>
 * <tr><td>EClass</td><td>{@value #TABLE_NAME}</td><td>the table or view</td></tr>
 * <tr><td>EClass</td><td>{@value #READ_ONLY}</td><td>{@code true} for a view</td></tr>
 * <tr><td>EAttribute</td><td>{@value #COLUMN_NAME}</td><td>the column</td></tr>
 * <tr><td>id EAttribute</td><td>{@value #GENERATED}</td><td>who assigns the key:
 * {@value #GENERATED_IDENTITY} — the database on insert; {@value #GENERATED_NONE} — the
 * application. Without it the mapping picks a generator as for any model.</td></tr>
 * <tr><td>single-valued EReference</td><td>{@value #JOIN_COLUMNS}</td><td>the foreign key columns
 * in the owner's table, comma-separated in key order</td></tr>
 * <tr><td>single-valued EReference</td><td>{@value #REFERENCED_COLUMNS}</td><td>the target columns
 * they refer to, in the same order — the target's id columns when absent</td></tr>
 * <tr><td>many-valued EReference</td><td>{@value #JOIN_TABLE}</td><td>the junction table</td></tr>
 * <tr><td>many-valued EReference</td><td>{@value #JOIN_COLUMNS}</td><td>with a join table: its
 * columns referring to the reference's owner</td></tr>
 * <tr><td>many-valued EReference</td><td>{@value #INVERSE_JOIN_COLUMNS}</td><td>with a join
 * table: its columns referring to the reference's target</td></tr>
 * </table>
 *
 * @author Juergen Albert
 * @since 05.11.2026
 */
public final class DatabaseAnnotations {

	/** The annotation source of the database names. */
	public static final String ANNOTATION_SOURCE = "http://eclipse.org/fennec/persistence/database";

	/** EPackage: the schema the package was read from. */
	public static final String SCHEMA = "schema";

	/** EPackage: the catalog the package was read from (MariaDB/MySQL: the database). */
	public static final String CATALOG = "catalog";

	/** EClass: the table or view name. */
	public static final String TABLE_NAME = "tableName";

	/** EClass: {@code true} for a view. */
	public static final String READ_ONLY = "readOnly";

	/** EAttribute: the column name. */
	public static final String COLUMN_NAME = "columnName";

	/** Id EAttribute: who assigns the key, {@value #GENERATED_IDENTITY} or {@value #GENERATED_NONE}. */
	public static final String GENERATED = "generated";

	/** {@value #GENERATED} value: the database assigns the key on insert (identity, auto-increment). */
	public static final String GENERATED_IDENTITY = "identity";

	/** {@value #GENERATED} value: the application assigns the key. */
	public static final String GENERATED_NONE = "none";

	/** EReference: the join columns, comma-separated in key order. */
	public static final String JOIN_COLUMNS = "joinColumns";

	/** Single-valued EReference: the referenced target columns, in the order of the join columns. */
	public static final String REFERENCED_COLUMNS = "referencedColumns";

	/** Many-valued EReference: the junction table. */
	public static final String JOIN_TABLE = "joinTable";

	/** Many-valued EReference with a join table: the columns referring to the target. */
	public static final String INVERSE_JOIN_COLUMNS = "inverseJoinColumns";

	private DatabaseAnnotations() {
	}

	/**
	 * @param eClass the type, must not be {@code null}
	 * @return the table name, or {@code null} if none is declared
	 */
	public static String tableName(EClass eClass) {
		return detail(requireNonNull(eClass, "eClass must not be null"), TABLE_NAME);
	}

	/**
	 * @param feature the feature, must not be {@code null}
	 * @return the column name, or {@code null} if none is declared
	 */
	public static String columnName(EStructuralFeature feature) {
		return detail(requireNonNull(feature, "feature must not be null"), COLUMN_NAME);
	}

	/**
	 * @param attribute the id attribute, must not be {@code null}
	 * @return {@value #GENERATED_IDENTITY}, {@value #GENERATED_NONE}, or {@code null} if none is
	 *         declared
	 * @throws IllegalStateException if another value is declared
	 */
	public static String generated(EAttribute attribute) {
		String value = detail(requireNonNull(attribute, "attribute must not be null"), GENERATED);
		if (isNull(value) || GENERATED_IDENTITY.equals(value) || GENERATED_NONE.equals(value)) {
			return value;
		}
		throw new IllegalStateException("The " + GENERATED + " annotation of '" + attribute.getName() + "' is '"
				+ value + "' — supported are '" + GENERATED_IDENTITY + "' and '" + GENERATED_NONE + "'");
	}

	/**
	 * @param reference the reference, must not be {@code null}
	 * @return the join columns in key order; empty if none are declared
	 */
	public static List<String> joinColumns(EReference reference) {
		return columns(requireNonNull(reference, "reference must not be null"), JOIN_COLUMNS);
	}

	/**
	 * @param reference the reference, must not be {@code null}
	 * @return the referenced target columns in the order of the join columns; empty if none are
	 *         declared
	 */
	public static List<String> referencedColumns(EReference reference) {
		return columns(requireNonNull(reference, "reference must not be null"), REFERENCED_COLUMNS);
	}

	/**
	 * @param reference the reference, must not be {@code null}
	 * @return the junction table, or {@code null} if none is declared
	 */
	public static String joinTable(EReference reference) {
		return detail(requireNonNull(reference, "reference must not be null"), JOIN_TABLE);
	}

	/**
	 * @param reference the reference, must not be {@code null}
	 * @return the junction table's columns referring to the target; empty if none are declared
	 */
	public static List<String> inverseJoinColumns(EReference reference) {
		return columns(requireNonNull(reference, "reference must not be null"), INVERSE_JOIN_COLUMNS);
	}

	/**
	 * @param ePackage the package, must not be {@code null}
	 * @return the schema the package was read from, or {@code null}
	 */
	public static String schema(EPackage ePackage) {
		return detail(requireNonNull(ePackage, "ePackage must not be null"), SCHEMA);
	}

	/**
	 * @param ePackage the package, must not be {@code null}
	 * @return the catalog the package was read from, or {@code null}
	 */
	public static String catalog(EPackage ePackage) {
		return detail(requireNonNull(ePackage, "ePackage must not be null"), CATALOG);
	}

	/**
	 * Declares one detail, creating the annotation when needed.
	 *
	 * @param element the element, must not be {@code null}
	 * @param key the detail key
	 * @param value the value
	 */
	public static void set(EModelElement element, String key, String value) {
		requireNonNull(element, "element must not be null");
		EAnnotation annotation = element.getEAnnotation(ANNOTATION_SOURCE);
		if (isNull(annotation)) {
			annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(ANNOTATION_SOURCE);
			element.getEAnnotations().add(annotation);
		}
		annotation.getDetails().put(key, value);
	}

	/**
	 * Declares a column list, comma-separated.
	 *
	 * @param element the element, must not be {@code null}
	 * @param key the detail key
	 * @param columns the columns in key order
	 */
	public static void set(EModelElement element, String key, List<String> columns) {
		set(element, key, String.join(",", columns));
	}

	private static String detail(EModelElement element, String key) {
		EAnnotation annotation = element.getEAnnotation(ANNOTATION_SOURCE);
		if (isNull(annotation)) {
			return null;
		}
		String value = annotation.getDetails().get(key);
		return isNull(value) || value.isBlank() ? null : value;
	}

	private static List<String> columns(EModelElement element, String key) {
		String value = detail(element, key);
		if (isNull(value)) {
			return List.of();
		}
		return Arrays.stream(value.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
	}
}
