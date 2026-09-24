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
package org.eclipse.fennec.persistence.tck;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * The relational {@link StoreProbe} (issue #329): plain JDBC on a connection of its own. The
 * EclipseLink descriptors only name the tables, key columns, foreign keys and relation tables;
 * the rows themselves are read with SQL, past the ORM and its caches.
 */
final class JpaStoreProbe implements StoreProbe {

	private final Session session;
	private final Map<String, Object> jdbc;

	/**
	 * @param session the unit's session, for the table and column names
	 * @param jdbcProperties the unit's JDBC url, user and password
	 */
	JpaStoreProbe(Session session, Map<String, Object> jdbcProperties) {
		this.session = session;
		this.jdbc = jdbcProperties;
	}

	@Override
	public Set<String> ids(EClass type) {
		ClassDescriptor descriptor = descriptor(type);
		String table = descriptor.getTables().get(0).getQualifiedName();
		String pk = descriptor.getPrimaryKeyFields().get(0).getName();
		List<Object> indicators = classIndicators(descriptor, type);
		StringBuilder sql = new StringBuilder("SELECT ").append(pk).append(" FROM ").append(table);
		if (!indicators.isEmpty()) {
			sql.append(" WHERE ").append(descriptor.getInheritancePolicy().getClassIndicatorFieldName())
					.append(" IN (").append("?,".repeat(indicators.size() - 1)).append("?)");
		}
		return new LinkedHashSet<>(strings(sql.toString(), indicators.toArray()));
	}

	@Override
	public Optional<String> reference(EClass type, String id, EReference reference) {
		ClassDescriptor descriptor = descriptor(type);
		DatabaseMapping mapping = mapping(descriptor, reference);
		if (!(mapping instanceof OneToOneMapping oneToOne)) {
			throw new IllegalStateException("No single-valued relational mapping for " + reference.getName()
					+ ": " + mapping);
		}
		String table = descriptor.getTables().get(0).getQualifiedName();
		String pk = descriptor.getPrimaryKeyFields().get(0).getName();
		if (!oneToOne.getForeignKeyFields().isEmpty()) {
			// the foreign key sits in the owner's row
			DatabaseField fk = oneToOne.getForeignKeyFields().get(0);
			String fkTable = fk.hasTableName() ? fk.getTable().getQualifiedName() : table;
			List<String> values = strings("SELECT " + fk.getName() + " FROM " + fkTable + " WHERE " + pk + " = ?", id);
			return values.stream().filter(v -> nonNull(v)).findFirst();
		}
		// the foreign key sits in the target's row, pointing back at the owner
		Map<DatabaseField, DatabaseField> targetToSource = oneToOne.getTargetToSourceKeyFields();
		DatabaseField targetFk = targetToSource.keySet().iterator().next();
		ClassDescriptor target = oneToOne.getReferenceDescriptor();
		String targetPk = target.getPrimaryKeyFields().get(0).getName();
		String targetTable = targetFk.hasTableName() ? targetFk.getTable().getQualifiedName()
				: target.getTables().get(0).getQualifiedName();
		return strings("SELECT " + targetPk + " FROM " + targetTable + " WHERE " + targetFk.getName() + " = ?", id)
				.stream().findFirst();
	}

	@Override
	public List<String> references(EClass type, String id, EReference reference) {
		ClassDescriptor descriptor = descriptor(type);
		DatabaseMapping mapping = mapping(descriptor, reference);
		if (mapping instanceof ManyToManyMapping manyToMany) {
			String relation = manyToMany.getRelationTable().getQualifiedName();
			String source = manyToMany.getSourceRelationKeyFields().get(0).getName();
			String target = manyToMany.getTargetRelationKeyFields().get(0).getName();
			return strings("SELECT " + target + " FROM " + relation + " WHERE " + source + " = ?"
					+ orderBy(manyToMany), id);
		}
		if (mapping instanceof OneToManyMapping oneToMany) {
			DatabaseField targetFk = oneToMany.getTargetForeignKeyFields().get(0);
			ClassDescriptor target = oneToMany.getReferenceDescriptor();
			String targetPk = target.getPrimaryKeyFields().get(0).getName();
			String targetTable = targetFk.hasTableName() ? targetFk.getTable().getQualifiedName()
					: target.getTables().get(0).getQualifiedName();
			return strings("SELECT " + targetPk + " FROM " + targetTable + " WHERE " + targetFk.getName() + " = ?"
					+ orderBy(oneToMany), id);
		}
		throw new IllegalStateException("No many-valued relational mapping for " + reference.getName() + ": " + mapping);
	}

	@Override
	public boolean storesContainedObjectsSeparately() {
		return true;
	}

	private ClassDescriptor descriptor(EClass type) {
		ClassDescriptor descriptor = session.getDescriptorForAlias(type.getName());
		if (isNull(descriptor)) {
			throw new IllegalStateException("No descriptor for " + type.getName());
		}
		return descriptor;
	}

	private static DatabaseMapping mapping(ClassDescriptor descriptor, EReference reference) {
		DatabaseMapping mapping = descriptor.getMappingForAttributeName(reference.getName());
		if (isNull(mapping)) {
			throw new IllegalStateException("No mapping for " + descriptor.getAlias() + "." + reference.getName());
		}
		return mapping;
	}

	private static String orderBy(CollectionMapping mapping) {
		DatabaseField order = mapping.getListOrderField();
		return isNull(order) ? "" : " ORDER BY " + order.getName();
	}

	/**
	 * The class indicator values of the type and its subtypes, when its descriptor shares a table
	 * with others; empty when every row of the table is one of them.
	 */
	private List<Object> classIndicators(ClassDescriptor descriptor, EClass type) {
		if (!descriptor.hasInheritance()) {
			return List.of();
		}
		InheritancePolicy policy = descriptor.getInheritancePolicy();
		InheritancePolicy root = policy.getRootParentDescriptor().getInheritancePolicy();
		if (!root.hasClassIndicator()) {
			return List.of();
		}
		Map<?, ?> indicatorMapping = root.getClassIndicatorMapping();
		List<Object> values = new ArrayList<>();
		for (ClassDescriptor candidate : session.getDescriptors().values()) {
			String alias = candidate.getAlias();
			if (isNull(alias) || isNull(type.getEPackage().getEClassifier(alias))) {
				continue;
			}
			EClass candidateType = (EClass) type.getEPackage().getEClassifier(alias);
			Object value = indicatorMapping.get(candidate.getJavaClass());
			if (type.isSuperTypeOf(candidateType) && nonNull(value)) {
				values.add(value);
			}
		}
		return values;
	}

	private List<String> strings(String sql, Object... parameters) {
		try (Connection connection = DriverManager.getConnection(
				(String) jdbc.get(PersistenceUnitProperties.JDBC_URL),
				(String) jdbc.get(PersistenceUnitProperties.JDBC_USER),
				(String) jdbc.get(PersistenceUnitProperties.JDBC_PASSWORD));
				PreparedStatement statement = connection.prepareStatement(sql)) {
			for (int i = 0; i < parameters.length; i++) {
				statement.setObject(i + 1, parameters[i]);
			}
			List<String> values = new ArrayList<>();
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					values.add(rs.getString(1));
				}
			}
			return values;
		} catch (SQLException e) {
			throw new IllegalStateException("Store probe failed: " + sql, e);
		}
	}
}
