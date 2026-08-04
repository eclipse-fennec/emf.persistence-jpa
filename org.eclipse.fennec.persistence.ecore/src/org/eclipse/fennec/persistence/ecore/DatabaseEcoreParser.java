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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.diagnostic.Diagnostics;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Reverse-engineers EMF Ecore models from existing database schemas via standard JDBC metadata.
 * <p>
 * Features:
 * <ul>
 *   <li>Vendor-independent type mapping via {@link JDBCType}</li>
 *   <li>ManyToOne + reverse OneToMany references with EOpposite</li>
 *   <li>Junction table detection → ManyToMany references</li>
 *   <li>Containment heuristic (NOT NULL FK + ON DELETE CASCADE)</li>
 *   <li>View support (read-only EClasses)</li>
 *   <li>Multi-schema support (one EPackage per schema)</li>
 *   <li>Naming convention transformation (SNAKE_CASE → CamelCase)</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 12.12.2024
 */
@Component(service = DatabaseEcoreParser.class, name = DatabaseEcoreParser.PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class DatabaseEcoreParser {

	private static final Logger LOG = Logger.getLogger(DatabaseEcoreParser.class.getName());

	static final String PID = "fennec.ecore.DatabaseParser";

	/** Diagnostic source of this parser (issue #19): the bundle namespace. */
	public static final String DIAGNOSTIC_SOURCE = "org.eclipse.fennec.persistence.ecore";

	static final String ANNOTATION_SOURCE = "http://eclipse.org/fennec/persistence/database";
	static final String ANNOTATION_READ_ONLY = "readOnly";
	static final String ANNOTATION_TABLE_NAME = "tableName";
	static final String ANNOTATION_SCHEMA = "schema";

	@Reference
	DataSource datasource;

	private DatabaseParserConfig config;

	@ObjectClassDefinition
	@interface DatabaseParserConfig {
		@AttributeDefinition(name = "Package name", description = "Mandatory base package name")
		String packageName();

		@AttributeDefinition(name = "Package uri prefix", description = "Mandatory package uri prefix")
		String uriPrefix();

		@AttributeDefinition(name = "Package version", description = "Optional package version")
		String version() default "1.0";

		@AttributeDefinition(name = "Schemas", description = "Database schemas to parse. Empty for default schema. Multiple schemas produce one EPackage each.")
		String[] schemas() default {};

		@AttributeDefinition(name = "Include views", description = "Whether to include database views as read-only EClasses")
		boolean includeViews() default false;

		@AttributeDefinition(name = "Transform names", description = "Transform DB names to Java naming conventions (SNAKE_CASE → CamelCase)")
		boolean transformNames() default true;
	}

	public void activate(DatabaseParserConfig config) {
		this.config = config;
	}

	/**
	 * Parses the database schema(s) and returns the packages together with every
	 * diagnostic collected on the way (issue #19). Nothing is logged here — callers
	 * decide what to do with the diagnostics; the legacy {@link #parseAll()} boundary
	 * derives JUL logging from them.
	 *
	 * @return the parse result, never {@code null}
	 */
	public ParseResult parseAllWithDiagnostics() throws SQLException {
		List<EPackage> packages = new ArrayList<>();
		List<Diagnostic> diagnostics = new ArrayList<>();
		try (Connection con = datasource.getConnection()) {
			DatabaseMetaData metaData = con.getMetaData();
			List<String> schemas = resolveSchemas(con);

			for (String schema : schemas) {
				String pkgName = schemas.size() == 1
						? config.packageName()
						: config.packageName() + "." + schema.toLowerCase();
				String uri = config.uriPrefix() + "/" + pkgName + "/" + config.version();
				EPackage ePackage = createPackage(pkgName, pkgName, uri);
				addAnnotation(ePackage, ANNOTATION_SCHEMA, schema);

				parseSchema(metaData, schema, ePackage, diagnostics);
				packages.add(ePackage);
			}
		}
		return new ParseResult(packages, diagnostics);
	}

	/**
	 * Parses the database schema(s) and returns one or more EPackages.
	 * If multiple schemas are configured, each schema gets its own EPackage.
	 * If no schema is configured, the default schema is used.
	 * <p>
	 * Logging boundary: diagnostics collected during the run are logged through JUL
	 * here; use {@link #parseAllWithDiagnostics()} to consume them programmatically.
	 *
	 * @return list of EPackages (one per schema)
	 */
	public List<EPackage> parseAll() throws SQLException {
		ParseResult result = parseAllWithDiagnostics();
		Diagnostics.log(LOG, result.diagnostics());
		return result.ePackages();
	}

	/**
	 * Parses a single schema (convenience method, returns the first/only package).
	 */
	public EPackage parse() throws SQLException {
		List<EPackage> packages = parseAll();
		return packages.isEmpty() ? createPackage(config.packageName(), config.packageName(),
				config.uriPrefix() + "/" + config.packageName() + "/" + config.version()) : packages.get(0);
	}

	private void parseSchema(DatabaseMetaData metaData, String schema, EPackage ePackage,
			List<Diagnostic> diagnostics) throws SQLException {
		loadTables(metaData, schema, ePackage);
		if (config.includeViews()) {
			loadViews(metaData, schema, ePackage);
		}

		// Collect table metadata for junction table detection
		Map<String, TableInfo> tableInfos = new HashMap<>();
		for (EClassifier c : ePackage.getEClassifiers()) {
			if (c instanceof EClass eClass) {
				String tableName = getOriginalTableName(eClass);
				Set<String> pkCols = loadPrimaryKeys(metaData, schema, tableName);
				Map<String, ForeignKeyInfo> fkCols = loadForeignKeys(metaData, schema, tableName);
				tableInfos.put(tableName, new TableInfo(eClass, pkCols, fkCols));
			}
		}

		// Detect junction tables and process columns
		Set<String> junctionTables = new HashSet<>();
		for (var entry : tableInfos.entrySet()) {
			if (isJunctionTable(entry.getValue(), metaData, schema)) {
				junctionTables.add(entry.getKey());
			}
		}

		// Process junction tables first → ManyToMany
		for (String jtName : junctionTables) {
			TableInfo ti = tableInfos.get(jtName);
			processJunctionTable(ti, ePackage);
			// Remove the junction EClass from the package
			ePackage.getEClassifiers().remove(ti.eClass);
		}

		// Process regular tables
		for (var entry : tableInfos.entrySet()) {
			if (junctionTables.contains(entry.getKey())) {
				continue;
			}
			processTable(entry.getValue(), metaData, schema, ePackage, diagnostics);
		}
	}

	private List<String> resolveSchemas(Connection con) throws SQLException {
		String[] configured = config.schemas();
		if (nonNull(configured) && configured.length > 0) {
			return List.of(configured);
		}
		String schema = con.getSchema();
		return List.of(isNull(schema) ? "PUBLIC" : schema);
	}

	// --- Table/View loading ---

	private void loadTables(DatabaseMetaData metaData, String schema, EPackage ePackage) throws SQLException {
		try (ResultSet rs = metaData.getTables(null, schema, "%", new String[]{"TABLE"})) {
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				EClass eClass = createEClass(transformClassName(tableName));
				addAnnotation(eClass, ANNOTATION_TABLE_NAME, tableName);
				ePackage.getEClassifiers().add(eClass);
			}
		}
	}

	private void loadViews(DatabaseMetaData metaData, String schema, EPackage ePackage) throws SQLException {
		try (ResultSet rs = metaData.getTables(null, schema, "%", new String[]{"VIEW"})) {
			while (rs.next()) {
				String viewName = rs.getString("TABLE_NAME");
				EClass eClass = createEClass(transformClassName(viewName));
				addAnnotation(eClass, ANNOTATION_TABLE_NAME, viewName);
				addAnnotation(eClass, ANNOTATION_READ_ONLY, "true");
				ePackage.getEClassifiers().add(eClass);
			}
		}
	}

	// --- Column processing ---

	private void processTable(TableInfo ti, DatabaseMetaData metaData, String schema, EPackage ePackage,
			List<Diagnostic> diagnostics) throws SQLException {
		String tableName = getOriginalTableName(ti.eClass);

		try (ResultSet rs = metaData.getColumns(null, schema, tableName, "%")) {
			while (rs.next()) {
				String colName = rs.getString("COLUMN_NAME");
				int dataType = rs.getInt("DATA_TYPE");
				boolean nullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
				boolean isPK = ti.pkColumns.contains(colName);

				ForeignKeyInfo fkInfo = ti.fkColumns.get(colName);
				if (nonNull(fkInfo)) {
					EClass refClass = findClassByTableName(ePackage, fkInfo.pkTableName);
					if (nonNull(refClass)) {
						// ManyToOne: this → referenced
						EReference fwdRef = addReference(ti.eClass, transformAttributeName(colName), refClass);
						boolean isContainmentCandidate = !nullable && fkInfo.deleteRule == DatabaseMetaData.importedKeyCascade;
						if (isContainmentCandidate) {
							// Containment: the referenced class "owns" this class
							// Reverse direction: parent (refClass) contains children (this)
							EReference revRef = addManyReference(refClass,
									transformAttributeName(tableName) + "s", ti.eClass);
							revRef.setContainment(true);
							setOpposite(fwdRef, revRef);
						} else {
							// Non-containment: plain bidirectional
							EReference revRef = addManyReference(refClass,
									transformAttributeName(tableName) + "s", ti.eClass);
							setOpposite(fwdRef, revRef);
						}
					} else {
						diagnostics.add(warning("FK target '" + fkInfo.pkTableName + "' not found for column '"
								+ tableName + "." + colName + "'; mapped as plain attribute", ti.eClass));
						addAttribute(ti.eClass, transformAttributeName(colName),
								resolveType(dataType, ti.eClass, tableName, colName, diagnostics), isPK, !nullable);
					}
				} else {
					addAttribute(ti.eClass, transformAttributeName(colName),
							resolveType(dataType, ti.eClass, tableName, colName, diagnostics), isPK, !nullable);
				}
			}
		}
	}

	// --- Junction table detection & ManyToMany ---

	/**
	 * A junction table has exactly 2 FK columns that together form the composite PK,
	 * and no other non-FK columns (or only the FK columns).
	 */
	private boolean isJunctionTable(TableInfo ti, DatabaseMetaData metaData, String schema) throws SQLException {
		if (ti.fkColumns.size() < 2) {
			return false;
		}
		// All PK columns must be FK columns
		if (!ti.fkColumns.keySet().containsAll(ti.pkColumns)) {
			return false;
		}
		// Count total columns — junction tables have only FK columns (which are also PKs)
		String tableName = getOriginalTableName(ti.eClass);
		int totalColumns = 0;
		try (ResultSet rs = metaData.getColumns(null, schema, tableName, "%")) {
			while (rs.next()) {
				totalColumns++;
			}
		}
		return totalColumns == ti.fkColumns.size();
	}

	private void processJunctionTable(TableInfo ti, EPackage ePackage) {
		// Find the two referenced tables
		List<ForeignKeyInfo> fks = new ArrayList<>(ti.fkColumns.values());
		if (fks.size() < 2) {
			return;
		}
		EClass classA = findClassByTableName(ePackage, fks.get(0).pkTableName);
		EClass classB = findClassByTableName(ePackage, fks.get(1).pkTableName);
		if (isNull(classA) || isNull(classB)) {
			return;
		}

		// Create ManyToMany: A → B and B → A with eOpposite
		String refNameAtoB = transformAttributeName(getOriginalTableName(classB)) + "s";
		String refNameBtoA = transformAttributeName(getOriginalTableName(classA)) + "s";

		EReference refAB = addManyReference(classA, refNameAtoB, classB);
		EReference refBA = addManyReference(classB, refNameBtoA, classA);
		setOpposite(refAB, refBA);
	}

	// --- Foreign key loading with delete rule ---

	private Map<String, ForeignKeyInfo> loadForeignKeys(DatabaseMetaData metaData, String schema, String tableName) throws SQLException {
		Map<String, ForeignKeyInfo> fkColumns = new HashMap<>();
		try (ResultSet rs = metaData.getImportedKeys(null, schema, tableName)) {
			while (rs.next()) {
				String fkColName = rs.getString("FKCOLUMN_NAME");
				String pkTableName = rs.getString("PKTABLE_NAME");
				int deleteRule = rs.getInt("DELETE_RULE");
				fkColumns.putIfAbsent(fkColName, new ForeignKeyInfo(pkTableName, deleteRule));
			}
		}
		return fkColumns;
	}

	private Set<String> loadPrimaryKeys(DatabaseMetaData metaData, String schema, String tableName) throws SQLException {
		Set<String> pkColumns = new HashSet<>();
		try (ResultSet rs = metaData.getPrimaryKeys(null, schema, tableName)) {
			while (rs.next()) {
				pkColumns.add(rs.getString("COLUMN_NAME"));
			}
		}
		return pkColumns;
	}

	// --- Naming transformation ---

	/**
	 * Transforms a database table name to a Java class name.
	 * {@code USER_ACCOUNT} → {@code UserAccount}, {@code users} → {@code Users}
	 */
	String transformClassName(String dbName) {
		if (!config.transformNames()) {
			return dbName;
		}
		return snakeToPascalCase(dbName);
	}

	/**
	 * Transforms a database column name to a Java field name.
	 * {@code FIRST_NAME} → {@code firstName}, {@code user_id} → {@code userId}
	 */
	String transformAttributeName(String dbName) {
		if (!config.transformNames()) {
			return dbName;
		}
		return snakeToCamelCase(dbName);
	}

	static String snakeToPascalCase(String input) {
		if (isNull(input) || input.isEmpty()) {
			return input;
		}
		StringBuilder sb = new StringBuilder();
		boolean capitalizeNext = true;
		for (char c : input.toCharArray()) {
			if (c == '_') {
				capitalizeNext = true;
			} else {
				sb.append(capitalizeNext ? Character.toUpperCase(c) : Character.toLowerCase(c));
				capitalizeNext = false;
			}
		}
		return sb.toString();
	}

	static String snakeToCamelCase(String input) {
		String pascal = snakeToPascalCase(input);
		if (isNull(pascal) || pascal.isEmpty()) {
			return pascal;
		}
		return Character.toLowerCase(pascal.charAt(0)) + pascal.substring(1);
	}

	// --- Type mapping ---

	static EDataType convertType(int sqlType) {
		return mapType(sqlType).type();
	}

	/**
	 * Maps a JDBC type code to an EDataType. Unknown or unmapped types fall back to
	 * EString; the fallback is reported in {@link TypeMapping#problem()} instead of
	 * being logged, so callers can turn it into a diagnostic with column context.
	 */
	static TypeMapping mapType(int sqlType) {
		JDBCType jdbcType;
		try {
			jdbcType = JDBCType.valueOf(sqlType);
		} catch (IllegalArgumentException e) {
			return new TypeMapping(EcorePackage.Literals.ESTRING,
					"Unknown JDBC type code " + sqlType + ", mapping to EString");
		}
		EDataType mapped = switch (jdbcType) {
			case TINYINT, SMALLINT -> EcorePackage.Literals.ESHORT_OBJECT;
			case INTEGER -> EcorePackage.Literals.EINTEGER_OBJECT;
			case BIGINT -> EcorePackage.Literals.ELONG_OBJECT;
			case FLOAT, REAL -> EcorePackage.Literals.EFLOAT_OBJECT;
			case DOUBLE -> EcorePackage.Literals.EDOUBLE_OBJECT;
			case DECIMAL, NUMERIC -> EcorePackage.Literals.EBIG_DECIMAL;
			case BOOLEAN, BIT -> EcorePackage.Literals.EBOOLEAN_OBJECT;
			case CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR -> EcorePackage.Literals.ESTRING;
			case DATE -> EcorePackage.Literals.EDATE;
			case TIME, TIME_WITH_TIMEZONE -> EcorePackage.Literals.EDATE;
			case TIMESTAMP, TIMESTAMP_WITH_TIMEZONE -> EcorePackage.Literals.EDATE;
			case BINARY, VARBINARY, LONGVARBINARY, BLOB -> EcorePackage.Literals.EBYTE_ARRAY;
			case CLOB, NCLOB -> EcorePackage.Literals.ESTRING;
			case SQLXML -> EcorePackage.Literals.ESTRING;
			default -> null;
		};
		return nonNull(mapped) ? new TypeMapping(mapped, null)
				: new TypeMapping(EcorePackage.Literals.ESTRING,
						"Unmapped JDBC type " + jdbcType + ", mapping to EString");
	}

	private EDataType resolveType(int sqlType, EClass eClass, String tableName, String colName,
			List<Diagnostic> diagnostics) {
		TypeMapping mapping = mapType(sqlType);
		if (nonNull(mapping.problem())) {
			diagnostics.add(warning(mapping.problem() + " for column '" + tableName + "." + colName + "'", eClass));
		}
		return mapping.type();
	}

	private static Diagnostic warning(String message, EClass affected) {
		return new BasicDiagnostic(Diagnostic.WARNING, DIAGNOSTIC_SOURCE, 0, message, new Object[] { affected });
	}

	// --- Lookup helpers ---

	private EClass findClassByTableName(EPackage ePackage, String tableName) {
		for (EClassifier c : ePackage.getEClassifiers()) {
			if (c instanceof EClass eClass) {
				if (tableName.equals(getOriginalTableName(eClass))) {
					return eClass;
				}
			}
		}
		return null;
	}

	private String getOriginalTableName(EClass eClass) {
		EAnnotation ann = eClass.getEAnnotation(ANNOTATION_SOURCE);
		if (nonNull(ann)) {
			String name = ann.getDetails().get(ANNOTATION_TABLE_NAME);
			if (nonNull(name)) {
				return name;
			}
		}
		return eClass.getName();
	}

	// --- EMF factory helpers ---

	static void addAttribute(EClass eClass, String name, EClassifier type, boolean isId) {
		addAttribute(eClass, name, type, isId, false);
	}

	static void addAttribute(EClass eClass, String name, EClassifier type, boolean isId, boolean required) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		eClass.getEStructuralFeatures().add(attribute);
		attribute.setName(name);
		attribute.setEType(type);
		attribute.setID(isId);
		if (required) {
			attribute.setLowerBound(1);
		}
	}

	static EReference addReference(EClass eClass, String name, EClassifier type) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		eClass.getEStructuralFeatures().add(reference);
		reference.setName(name);
		reference.setEType(type);
		return reference;
	}

	static EReference addManyReference(EClass eClass, String name, EClassifier type) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		eClass.getEStructuralFeatures().add(reference);
		reference.setName(name);
		reference.setEType(type);
		reference.setUpperBound(-1);
		return reference;
	}

	static void setOpposite(EReference ref1, EReference ref2) {
		ref1.setEOpposite(ref2);
		ref2.setEOpposite(ref1);
	}

	static void addAnnotation(EClass eClass, String key, String value) {
		addAnnotation((EModelElement) eClass, key, value);
	}

	static void addAnnotation(EModelElement element, String key, String value) {
		EAnnotation ann = element.getEAnnotation(ANNOTATION_SOURCE);
		if (isNull(ann)) {
			ann = EcoreFactory.eINSTANCE.createEAnnotation();
			ann.setSource(ANNOTATION_SOURCE);
			element.getEAnnotations().add(ann);
		}
		ann.getDetails().put(key, value);
	}

	static EPackage createPackage(String name, String prefix, String uri) {
		EPackage epackage = EcoreFactory.eINSTANCE.createEPackage();
		epackage.setName(name);
		epackage.setNsPrefix(prefix);
		epackage.setNsURI(uri);
		return epackage;
	}

	static EClass createEClass(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		return eClass;
	}

	// --- Internal data holders ---

	record ForeignKeyInfo(String pkTableName, int deleteRule) {}

	record TableInfo(EClass eClass, Set<String> pkColumns, Map<String, ForeignKeyInfo> fkColumns) {}

	/** A mapped EDataType plus, on fallback, the problem to report ({@code null} if clean). */
	record TypeMapping(EDataType type, String problem) {}
}
