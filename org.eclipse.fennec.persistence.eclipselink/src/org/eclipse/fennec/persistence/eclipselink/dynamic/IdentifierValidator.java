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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.fennec.persistence.processor.ProcessingContext;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

/**
 * Reports table and column names that are no regular SQL identifier as
 * {@link org.eclipse.emf.common.util.Diagnostic#ERROR} diagnostics (issue #314).
 * <p>
 * Such a name — {@code marker-color}, say — breaks the {@code CREATE TABLE} of its table, and
 * {@code create-or-extend-tables} swallows that failure: the unit comes up, and every query then
 * fails with a missing table that names nothing about the cause. Reported here, before any DDL,
 * the deploy fails instead, naming entity, attribute and column.
 * <p>
 * A regular identifier starts with a letter or {@code _} and continues with letters, digits,
 * {@code _} or {@code $}. Nothing is reported for a delimited name, nor when the mapping declares
 * delimited identifiers for the whole unit.
 *
 * @author Mark Hoffmann
 * @since 24.09.2026
 */
final class IdentifierValidator {

	private IdentifierValidator() {
	}

	/**
	 * Reports every table and column name of the type that is no regular identifier.
	 * @param type the dynamic type, its mappings complete
	 * @param diagnostics the channel to report to
	 */
	static void validate(EDynamicType type, ProcessingContext diagnostics) {
		ClassDescriptor descriptor = type.getDescriptor();
		String entityName = type.getEClass().getName();
		for (DatabaseTable table : descriptor.getTables()) {
			if (!table.shouldUseDelimiters() && !isRegularIdentifier(table.getName())) {
				diagnostics.error(EDynamicTypeContext.DIAGNOSTIC_SOURCE, String.format(
						"Table name '%s' of entity '%s' is no SQL identifier. Declare a table name "
								+ "in the eorm mapping, or delimited identifiers for the unit.",
						table.getName(), entityName), type.getEClass());
			}
		}
		Set<String> reported = new LinkedHashSet<>();
		for (DatabaseMapping mapping : descriptor.getMappings()) {
			for (DatabaseField field : fieldsOf(mapping)) {
				if (field.shouldUseDelimiters() || isRegularIdentifier(field.getName())
						|| !reported.add(field.getName())) {
					continue;
				}
				diagnostics.error(EDynamicTypeContext.DIAGNOSTIC_SOURCE, String.format(
						"Column name '%s' of attribute '%s' in entity '%s' is no SQL identifier. "
								+ "Declare a column name in the eorm mapping, or delimited identifiers "
								+ "for the unit.",
						field.getName(), mapping.getAttributeName(), entityName), type.getEClass());
			}
		}
	}

	/**
	 * The fields a mapping names. {@code DatabaseMapping.getFields()} is only filled when the
	 * descriptor initializes, which is after this check, so they are read from the mapping kind.
	 */
	private static List<DatabaseField> fieldsOf(DatabaseMapping mapping) {
		List<DatabaseField> fields = new ArrayList<>();
		if (mapping instanceof AbstractDirectMapping direct) {
			fields.add(direct.getField());
		} else if (mapping instanceof OneToOneMapping oneToOne) {
			fields.addAll(oneToOne.getForeignKeyFields());
		} else if (mapping instanceof DirectCollectionMapping collection) {
			fields.add(collection.getDirectField());
			fields.addAll(collection.getReferenceKeyFields());
		}
		fields.removeIf(Objects::isNull);
		return fields;
	}

	/**
	 * @param name a table or column name
	 * @return {@code true} if the name can be used undelimited
	 */
	static boolean isRegularIdentifier(String name) {
		if (isNull(name) || name.isEmpty()) {
			// an unnamed table or field is resolved by EclipseLink, not by this name
			return true;
		}
		int first = name.codePointAt(0);
		if (!Character.isLetter(first) && first != '_') {
			return false;
		}
		return name.codePoints().skip(1)
				.allMatch(c -> Character.isLetterOrDigit(c) || c == '_' || c == '$');
	}
}
