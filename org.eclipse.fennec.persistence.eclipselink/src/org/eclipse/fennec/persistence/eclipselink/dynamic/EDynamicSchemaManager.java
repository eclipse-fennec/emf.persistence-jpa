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

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.DynamicSchemaManager;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * A {@link DynamicSchemaManager} whose extend step finds existing columns the way EclipseLink's
 * JPA metadata processing would (issue #320).
 * <p>
 * {@code TableCreator.extendTables} hashes the existing columns with upper-case comparison when
 * the platform forces field names to upper case — which the JPA deploy enables by default
 * ({@code eclipselink.jpa.uppercase-column-names}) — and looks each wanted field up by
 * {@link DatabaseField} equality. The metadata path marks every non-delimited field with
 * {@code useUpperCaseForComparisons(true)} in that case; dynamic types are built without it, so on
 * H2, which folds identifiers to upper case, {@code id} never matched {@code ID} and every column
 * was added again, each {@code ALTER} failing and being swallowed.
 * <p>
 * The field definitions of the default table creator get <em>copies</em> of their fields with the
 * comparison mode set: the originals are hashed into the initialized descriptors, and flipping
 * their mode would break those lookups. The copies only serve the extend lookup; the DDL is
 * written from the field definition's name either way. Delimited identifiers stay case-sensitive.
 *
 * @author Mark Hoffmann
 * @since 24.09.2026
 */
class EDynamicSchemaManager extends DynamicSchemaManager {

	EDynamicSchemaManager(DatabaseSession session) {
		super(session);
	}

	@Override
	protected TableCreator getDefaultTableCreator(boolean generateFKConstraints) {
		TableCreator creator = super.getDefaultTableCreator(generateFKConstraints);
		if (getSession().getPlatform().shouldForceFieldNamesToUpperCase()) {
			creator.getTableDefinitions().forEach(EDynamicSchemaManager::compareCaseInsensitively);
		}
		return creator;
	}

	// FieldDefinition's field accessors are deprecated for removal in 4.0.9 without a
	// replacement, while TableCreator.extendTables still reads exactly that field for its
	// lookup; ExtendExistingTablesTest notices when a later version changes that
	@SuppressWarnings("removal")
	private static void compareCaseInsensitively(TableDefinition table) {
		for (FieldDefinition fieldDefinition : table.getFields()) {
			DatabaseField field = fieldDefinition.getDatabaseField();
			if (isNull(field) || field.shouldUseDelimiters()) {
				continue;
			}
			DatabaseField copy = field.clone();
			copy.useUpperCaseForComparisons(true);
			fieldDefinition.setDatabaseField(copy);
		}
	}
}
