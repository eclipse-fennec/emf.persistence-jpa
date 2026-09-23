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
package org.eclipse.fennec.persistence.ecore;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * What the schema says about one table or view, linked to the model elements it became
 * (issue #295). The Ecore alone cannot carry these facts — an original column name, a
 * default expression, an index — yet a mapping onto the existing schema needs every one
 * of them.
 *
 * @param schema the schema the table lives in
 * @param name the table name as the database reports it
 * @param view whether this is a view
 * @param remarks the table comment, {@code null} if none
 * @param eClass the class the table became
 * @param columns the columns in ordinal order
 * @param primaryKey the primary key columns in key order, empty if the table has none
 * @param foreignKeys the foreign keys of the table
 * @param indexes the indexes of the table, the primary key's own index excluded
 * @author Mark Hoffmann
 * @since 23.09.2026
 */
public record TableFacts(String schema, String name, boolean view, String remarks, EClass eClass,
		List<Column> columns, List<String> primaryKey, List<ForeignKey> foreignKeys, List<Index> indexes) {

	/**
	 * Creates the table facts.
	 */
	public TableFacts {
		columns = List.copyOf(columns);
		primaryKey = List.copyOf(primaryKey);
		foreignKeys = List.copyOf(foreignKeys);
		indexes = List.copyOf(indexes);
	}

	/**
	 * Returns the column of the given name.
	 *
	 * @param columnName the column name as the database reports it
	 * @return the column, or {@code null}
	 */
	public Column column(String columnName) {
		return columns.stream().filter(c -> c.name().equals(columnName)).findFirst().orElse(null);
	}

	/**
	 * One column.
	 *
	 * @param name the column name as the database reports it
	 * @param jdbcType the {@link java.sql.Types} code
	 * @param typeName the vendor type name
	 * @param size the column size: length for character and binary types, precision for numeric ones
	 * @param decimalDigits the scale of numeric types
	 * @param nullable whether the column accepts {@code NULL}
	 * @param autoIncrement whether the database generates the value on insert
	 * @param generated whether the column is computed by the database
	 * @param defaultValue the raw default as the database reports it, {@code null} if none
	 * @param remarks the column comment, {@code null} if none
	 * @param feature the feature that carries the column — its attribute, or the reference
	 *            of the foreign key it belongs to
	 */
	public record Column(String name, int jdbcType, String typeName, int size, int decimalDigits,
			boolean nullable, boolean autoIncrement, boolean generated, String defaultValue, String remarks,
			EStructuralFeature feature) {
	}

	/**
	 * One foreign key constraint.
	 *
	 * @param name the constraint name, {@code null} if the driver reports none
	 * @param targetSchema the schema of the referenced table
	 * @param targetTable the referenced table
	 * @param columns the FK columns in key order
	 * @param targetColumns the referenced columns, aligned with {@code columns}
	 * @param deleteRule the {@link java.sql.DatabaseMetaData} delete rule
	 * @param reference the forward reference the key became; {@code null} when the target is
	 *            not part of the package and the columns stayed attributes, and for the keys
	 *            of a junction table, whose pair is on {@link JunctionFacts}
	 */
	public record ForeignKey(String name, String targetSchema, String targetTable, List<String> columns,
			List<String> targetColumns, int deleteRule, EReference reference) {

		/**
		 * Creates the foreign key facts.
		 */
		public ForeignKey {
			columns = List.copyOf(columns);
			targetColumns = List.copyOf(targetColumns);
		}
	}

	/**
	 * One index. A unique constraint appears here as a unique index — JDBC reports no
	 * constraints of its own.
	 *
	 * @param name the index name
	 * @param columns the indexed columns in index order
	 * @param unique whether the index is unique
	 */
	public record Index(String name, List<String> columns, boolean unique) {

		/**
		 * Creates the index facts.
		 */
		public Index {
			columns = List.copyOf(columns);
		}
	}

}
