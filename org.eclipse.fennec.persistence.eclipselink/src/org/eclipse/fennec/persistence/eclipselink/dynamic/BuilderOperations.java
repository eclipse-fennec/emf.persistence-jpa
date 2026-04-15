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

import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sequencing.Sequence;

/**
 * Callback interface for delegate configurators to access inherited
 * {@link org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder} methods.
 * Package-private — only used within the dynamic package.
 *
 * @author Mark Hoffmann
 * @since 15.04.2026
 */
interface BuilderOperations {

	EDynamicType getType();

	DirectToFieldMapping addDirectMapping(String name, Class<?> type, String columnName);

	DatabaseMapping addMapping(DatabaseMapping mapping);

	OneToOneMapping addOneToOneMapping(String name, DynamicType targetType, String fkName);

	CollectionMapping addOneToManyMapping(String name, DynamicType targetType, String fkName);

	DirectCollectionMapping addDirectCollectionMapping(String name, String tableName,
			String valueColumn, Class<?> typeClass, String foreignKeyName);

	void setPrimaryKeyFields(String... fieldNames);

	void configureSequencing(String seqName, String fieldName);

	void configureSequencing(Sequence sequence, String seqName, String fieldName);
}
