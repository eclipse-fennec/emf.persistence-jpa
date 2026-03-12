/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.ecore;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.eclipse.daanse.jdbc.db.api.DatabaseService;
import org.eclipse.daanse.jdbc.db.api.meta.MetaInfo;
import org.eclipse.daanse.jdbc.db.api.meta.StructureInfo;
import org.eclipse.daanse.jdbc.db.api.schema.ColumnDefinition;
import org.eclipse.daanse.jdbc.db.api.schema.ColumnMetaData;
import org.eclipse.daanse.jdbc.db.api.schema.ColumnReference;
import org.eclipse.daanse.jdbc.db.api.schema.ImportedKey;
import org.eclipse.daanse.jdbc.db.api.schema.SchemaReference;
import org.eclipse.daanse.jdbc.db.api.schema.TableDefinition;
import org.eclipse.daanse.jdbc.db.api.schema.TableReference;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = DatabaseEcoreParser.class, name = DatabaseEcoreParser.PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class DatabaseEcoreParser {
	public static final String PID = "fennec.ecore.DatabaseParser";

	@Reference
	DatabaseService databaseService;

	@Reference
	DataSource datasource;

	private DatabaseParserConfig config;

	@ObjectClassDefinition
	@interface DatabaseParserConfig {
		@AttributeDefinition(name = "Package name", description = "Mendatory package name")
		String packageName();

		@AttributeDefinition(name = "Package uri prefix", description = "Mendatory package uri prefix")
		String uriPrefix();

		@AttributeDefinition(name = "Package version", description = "Optional package version")
		String version() default "1.0";
	}

	public void activate(DatabaseParserConfig config) {
		this.config = config;
	}

	public EPackage parse() throws SQLException {
		String name = config.packageName();
		String uri = config.uriPrefix() + "/" + name + "/" + config.version();
		EPackage ePackage = createPackage(name, name, uri);
		try (Connection con = datasource.getConnection()) {
			MetaInfo metaInfo = databaseService.createMetaInfo(con);
			StructureInfo structureInfo = metaInfo.structureInfo();

			loadTablesToEClass(structureInfo, con.getSchema(), ePackage);
			loadColumnsToAttributes(structureInfo, ePackage);
		}
		return ePackage;
	}

	private void loadColumnsToAttributes(StructureInfo structureInfo, EPackage ePackage) {
		List<ColumnDefinition> cds = structureInfo.columns();

		List<ImportedKey> importedKeys = structureInfo.importedKeys();
		Map<String, ImportedKey> pkeys = importedKeys.stream()
				.filter(k -> k.primaryKeyColumn()
						.table()
						.isPresent())
				.collect(Collectors.toMap(k -> k.primaryKeyColumn()
						.table()
						.get()
						.name(), Function.identity()));
		Map<String, ImportedKey> fkeys = importedKeys.stream()
				.filter(k -> k.foreignKeyColumn()
						.table()
						.isPresent())
				.collect(Collectors.toMap(this::getFkKey, Function.identity()));

		for (ColumnDefinition cd : cds) {
			ColumnReference cr = cd.column();
			Optional<TableReference> table = cr.table();
			if (table.isPresent()) {
				String tableName = table.get()
						.name();
				EClassifier tableClass = ePackage.getEClassifier(tableName);
				if (tableClass instanceof EClass tClass) {
					ColumnMetaData cMD = cd.columnMetaData();
					String name = cr.name();
					ImportedKey l = fkeys.get(tableName + "_" + name);
					if (nonNull(l) && l.primaryKeyColumn()
							.table()
							.isPresent()) {
						String refTableName = l.primaryKeyColumn()
								.table()
								.get()
								.name();
						EClassifier eClassifier = ePackage.getEClassifier(refTableName);
						addReference(tClass, name, eClassifier);
					} else {
						JDBCType dataType = cMD.dataType();
						EDataType eDataType = convertType(dataType.getName());
						boolean isPK = isPK(pkeys.get(tableName), name);
						addAttribute(tClass, name, eDataType, isPK);
					}
				}
			}
		}
	}

	private String getFkKey(ImportedKey k) {
		Optional<TableReference> table = k.foreignKeyColumn()
				.table();
		if (table.isPresent()) {
			return table.get()
					.name() + "_"
					+ k.foreignKeyColumn()
							.name();
		} else {
			return "_" + k.foreignKeyColumn()
					.name();
		}
	}

	private void loadTablesToEClass(StructureInfo structureInfo, String schema, EPackage ePackage) {
		if (isNull(schema)) {
			schema = "PUBLIC";
		}
		List<TableDefinition> tds = structureInfo.tables();
		for (TableDefinition td : tds) {
			TableReference tr = td.table();
			Optional<SchemaReference> schemaO = tr.schema();
			if (schemaO.isPresent() && schema.equals(schemaO.get()
					.name())) {
				String tableName = tr.name();
				EClass eClass = createEClass(tableName);
				ePackage.getEClassifiers()
						.add(eClass);
			}
		}
	}

	static boolean isPK(ImportedKey pkey, String name) {
		if (nonNull(pkey)) {
			String pkColumnName = pkey.primaryKeyColumn()
					.name();
			return pkColumnName.equals(name);
		} else {
			return "id".equalsIgnoreCase(name);
		}
	}

	static void addAttribute(EClass customerRow, String name, EClassifier type, boolean isId) {
		final EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		// always add to container first
		customerRow.getEStructuralFeatures()
				.add(attribute);
		attribute.setName(name);
		attribute.setEType(type);
		attribute.setID(isId);
	}

	static void addReference(EClass customerRow, String name, EClassifier type) {
		final EReference reference = EcoreFactory.eINSTANCE.createEReference();
		// always add to container first
		customerRow.getEStructuralFeatures()
				.add(reference);
		reference.setName(name);
		reference.setEType(type);
	}

	static EPackage createPackage(final String name, final String prefix, final String uri) {
		final EPackage epackage = EcoreFactory.eINSTANCE.createEPackage();
		epackage.setName(name);
		epackage.setNsPrefix(prefix);
		epackage.setNsURI(uri);
		return epackage;
	}

	static EClass createEClass(final String name) {
		final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		return eClass;
	}

	static EDataType convertType(String colType) {
		if (colType.equals("int4")) {
			return EcorePackage.Literals.EINTEGER_OBJECT;

		} else if (colType.equals("float8")) {
			return EcorePackage.Literals.EBIG_DECIMAL;

		} else if (colType.equals("text")) {
			return EcorePackage.Literals.ESTRING;

		}
		if (colType.equals("varchar")) {
			return EcorePackage.Literals.ESTRING;

		}
		if (colType.equals("geometry")) {
			return EcorePackage.Literals.ESTRING;

		}

		if (colType.equals("bpchar")) {
			return EcorePackage.Literals.ESTRING;

		}
		if (colType.equals("bool")) {
			return EcorePackage.Literals.EBOOLEAN;

		}
		if (colType.equals("serial")) {
			return EcorePackage.Literals.ESTRING;

		}
		if (colType.equals("xml")) {
			return EcorePackage.Literals.ESTRING;

		}

		return EcorePackage.Literals.ESTRING;
	}
}
