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

import org.eclipse.fennec.persistence.helper.ReservedWords;
import org.eclipse.fennec.persistence.helper.ReservedWords.Dialect;
import org.eclipse.fennec.persistence.helper.ReservedWords.Kind;
import org.eclipse.fennec.persistence.processor.ProcessingContext;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.platform.database.MariaDBPlatform;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.sessions.Session;

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
 * <p>
 * A name that is a regular identifier but a reserved word of the database the unit runs on
 * ({@link ReservedWords}) breaks the DDL the same way (issue #399) and is reported as an error
 * too. A derived name never is one — the mapping escapes it — so this catches names the eorm
 * mapping or the model's database names declare. On a database whose reserved words are not
 * known, a name reserved on one of the known ones is a warning.
 *
 * @author Mark Hoffmann
 * @since 24.09.2026
 */
final class IdentifierValidator {

	private IdentifierValidator() {
	}

	/**
	 * Reports every table and column name of the type that is no regular identifier, or a
	 * reserved word of the database.
	 * @param type the dynamic type, its mappings complete
	 * @param diagnostics the channel to report to
	 * @param dialect the database the unit runs on, {@code null} if its reserved words are unknown
	 */
	static void validate(EDynamicType type, ProcessingContext diagnostics, Dialect dialect) {
		ClassDescriptor descriptor = type.getDescriptor();
		String entityName = type.getEClass().getName();
		for (DatabaseTable table : descriptor.getTables()) {
			if (table.shouldUseDelimiters()) {
				continue;
			}
			if (!isRegularIdentifier(table.getName())) {
				diagnostics.error(EDynamicTypeContext.DIAGNOSTIC_SOURCE, String.format(
						"Table name '%s' of entity '%s' is no SQL identifier. Declare a table name "
								+ "in the eorm mapping, or delimited identifiers for the unit.",
						table.getName(), entityName), type.getEClass());
			} else {
				reportReserved(diagnostics, dialect, table.getName(), Kind.TABLE, String.format(
						"Table name '%s' of entity '%s'", table.getName(), entityName), type);
			}
		}
		Set<String> reported = new LinkedHashSet<>();
		for (DatabaseMapping mapping : descriptor.getMappings()) {
			for (DatabaseField field : fieldsOf(mapping)) {
				if (field.shouldUseDelimiters() || !reported.add(field.getName())) {
					continue;
				}
				if (!isRegularIdentifier(field.getName())) {
					diagnostics.error(EDynamicTypeContext.DIAGNOSTIC_SOURCE, String.format(
							"Column name '%s' of attribute '%s' in entity '%s' is no SQL identifier. "
									+ "Declare a column name in the eorm mapping, or delimited identifiers "
									+ "for the unit.",
							field.getName(), mapping.getAttributeName(), entityName), type.getEClass());
				} else {
					reportReserved(diagnostics, dialect, field.getName(), Kind.COLUMN, String.format(
							"Column name '%s' of attribute '%s' in entity '%s'", field.getName(),
							mapping.getAttributeName(), entityName), type);
				}
			}
		}
	}

	/**
	 * Reports a reserved name: an error if the unit's database refuses it, a warning if the
	 * database is unknown and a known one refuses it.
	 */
	private static void reportReserved(ProcessingContext diagnostics, Dialect dialect, String name, Kind kind,
			String subject, EDynamicType type) {
		String advice = "Declare another name in the eorm mapping or as database name, or delimited "
				+ "identifiers for the unit.";
		if (isNull(dialect)) {
			Set<Dialect> dialects = ReservedWords.reservingDialects(name, kind);
			if (!dialects.isEmpty()) {
				diagnostics.warning(EDynamicTypeContext.DIAGNOSTIC_SOURCE, String.format(
						"%s is a reserved word on %s and may be one on this database too. %s", subject,
						dialects, advice), type.getEClass());
			}
		} else if (ReservedWords.isReserved(name, kind, dialect)) {
			diagnostics.error(EDynamicTypeContext.DIAGNOSTIC_SOURCE, String.format(
					"%s is a reserved word on %s. %s", subject, dialect, advice), type.getEClass());
		}
	}

	/**
	 * The database a session runs on, as far as its reserved words are known.
	 * @param session the session, may be {@code null}
	 * @return the dialect, {@code null} for an unknown or not yet detected database
	 */
	static Dialect dialectOf(Session session) {
		if (isNull(session)) {
			return null;
		}
		Object platform = session.getDatasourcePlatform();
		if (platform instanceof H2Platform) {
			return Dialect.H2;
		}
		if (platform instanceof PostgreSQLPlatform) {
			return Dialect.POSTGRESQL;
		}
		if (platform instanceof MariaDBPlatform) {
			return Dialect.MARIADB;
		}
		return null;
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
