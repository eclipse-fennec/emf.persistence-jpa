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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import org.eclipse.fennec.persistence.helper.CompositeIds;
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
 *   <li>ManyToOne + reverse OneToMany references with EOpposite; a composite FK is one reference</li>
 *   <li>Composite PKs declared via {@link CompositeIds#ID_FEATURES}, never several {@code isID} attributes</li>
 *   <li>Junction table detection (exactly two FKs) → ManyToMany references</li>
 *   <li>Unique feature names — collisions get a deterministic fallback and an INFO diagnostic</li>
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
		String escape = metaData.getSearchStringEscape();
		loadTables(metaData, schema, escape, ePackage);
		if (config.includeViews()) {
			loadViews(metaData, schema, escape, ePackage);
		}

		// table metadata in package order: the produced model must not depend on hash order
		List<TableInfo> tables = new ArrayList<>();
		for (EClassifier c : ePackage.getEClassifiers()) {
			if (c instanceof EClass eClass) {
				String tableName = getOriginalTableName(eClass);
				tables.add(new TableInfo(eClass, tableName, loadColumns(metaData, schema, tableName, escape),
						loadPrimaryKeys(metaData, schema, tableName), loadForeignKeys(metaData, schema, tableName)));
			}
		}

		// junction tables become ManyToMany references, not classes
		List<TableInfo> junctions = tables.stream().filter(DatabaseEcoreParser::isJunctionTable).toList();
		for (TableInfo junction : junctions) {
			ePackage.getEClassifiers().remove(junction.eClass());
		}

		// Pass 1: attributes and forward references of every table. Reverse references wait
		// for pass 2, so that each class already holds its own features when a reverse name
		// is chosen and a later column can never collide with it.
		List<Link> links = new ArrayList<>();
		for (TableInfo ti : tables) {
			if (!junctions.contains(ti)) {
				processTable(ti, schema, ePackage, links, diagnostics);
			}
		}
		// Pass 2: reverse references and ManyToMany pairs
		for (Link link : links) {
			addReverseReference(link, diagnostics);
		}
		for (TableInfo junction : junctions) {
			processJunctionTable(junction, schema, ePackage, diagnostics);
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

	private void loadTables(DatabaseMetaData metaData, String schema, String escape, EPackage ePackage)
			throws SQLException {
		try (ResultSet rs = metaData.getTables(null, pattern(schema, escape), "%", new String[]{"TABLE"})) {
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				EClass eClass = createEClass(transformClassName(tableName));
				addAnnotation(eClass, ANNOTATION_TABLE_NAME, tableName);
				ePackage.getEClassifiers().add(eClass);
			}
		}
	}

	private void loadViews(DatabaseMetaData metaData, String schema, String escape, EPackage ePackage)
			throws SQLException {
		try (ResultSet rs = metaData.getTables(null, pattern(schema, escape), "%", new String[]{"VIEW"})) {
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

	private void processTable(TableInfo ti, String schema, EPackage ePackage, List<Link> links,
			List<Diagnostic> diagnostics) {
		// resolve the foreign keys first: a resolved FK column is carried by its reference
		List<ForeignKeyInfo> resolved = new ArrayList<>();
		List<EClass> targets = new ArrayList<>();
		Set<String> referenceColumns = new HashSet<>();
		for (ForeignKeyInfo fk : ti.foreignKeys()) {
			EClass target = resolveTarget(ePackage, schema, fk);
			if (isNull(target)) {
				boolean composite = fk.fkColumns().size() > 1;
				diagnostics.add(warning("FK target '" + fk.pkTable() + "' not found for column"
						+ (composite ? "s" : "") + " '" + qualifiedColumns(ti, fk) + "'; mapped as plain attribute"
						+ (composite ? "s" : ""), ti.eClass()));
			} else {
				resolved.add(fk);
				targets.add(target);
				referenceColumns.addAll(fk.fkColumns());
			}
		}

		// Attributes. A PK column stays an attribute even when it is also an FK column
		// (identifying relationship): the id is declared over attributes. A composite PK is
		// declared through idFeatures — several isID attributes are invalid Ecore.
		boolean compositeId = ti.pkColumns().size() > 1;
		Map<String, String> pkAttributes = new HashMap<>();
		for (ColumnInfo column : ti.columns()) {
			boolean isPK = ti.pkColumns().contains(column.name());
			if (referenceColumns.contains(column.name()) && !isPK) {
				continue;
			}
			String name = uniqueName(ti.eClass(), transformAttributeName(column.name()), null, diagnostics);
			addAttribute(ti.eClass(), name,
					resolveType(column.dataType(), ti.eClass(), ti.tableName(), column.name(), diagnostics),
					isPK && !compositeId, !column.nullable());
			if (isPK) {
				pkAttributes.put(column.name(), name);
			}
		}
		if (compositeId) {
			List<String> idFeatures = ti.pkColumns().stream().map(pkAttributes::get).toList();
			addAnnotation(ti.eClass(), CompositeIds.ANNOTATION_SOURCE, CompositeIds.ID_FEATURES,
					String.join(",", idFeatures));
		}

		// Forward references, one per FK — a composite FK is one reference, not one per column
		for (int i = 0; i < resolved.size(); i++) {
			ForeignKeyInfo fk = resolved.get(i);
			boolean identifying = fk.fkColumns().stream().anyMatch(ti.pkColumns()::contains);
			String preferred = fk.fkColumns().size() == 1 && !identifying
					? transformAttributeName(fk.fkColumns().get(0))
					: transformAttributeName(fk.pkTable());
			String name = uniqueName(ti.eClass(), preferred, qualifier(fk), diagnostics);
			EReference forward = addReference(ti.eClass(), name, targets.get(i));
			links.add(new Link(ti, fk, forward, targets.get(i)));
		}
	}

	/**
	 * The reverse side of a forward reference: many-valued, containment when every FK column
	 * is NOT NULL and the FK deletes on cascade (the parent owns the child).
	 */
	private void addReverseReference(Link link, List<Diagnostic> diagnostics) {
		TableInfo owner = link.owner();
		String preferred = transformAttributeName(owner.tableName()) + "s";
		String name = uniqueName(link.target(), preferred, link.forward().getName(), diagnostics);
		EReference reverse = addManyReference(link.target(), name, owner.eClass());
		boolean notNull = link.fk().fkColumns().stream().allMatch(c -> owner.columns().stream()
				.anyMatch(col -> col.name().equals(c) && !col.nullable()));
		if (notNull && link.fk().deleteRule() == DatabaseMetaData.importedKeyCascade) {
			reverse.setContainment(true);
		}
		setOpposite(link.forward(), reverse);
	}

	// --- Junction table detection & ManyToMany ---

	/**
	 * A junction table has exactly two foreign keys, its columns are exactly their columns,
	 * and its primary key (if any) lies within them. A third foreign key or a payload column
	 * makes it an entity of its own.
	 */
	static boolean isJunctionTable(TableInfo ti) {
		if (ti.foreignKeys().size() != 2) {
			return false;
		}
		Set<String> fkColumns = new HashSet<>();
		ti.foreignKeys().forEach(fk -> fkColumns.addAll(fk.fkColumns()));
		return fkColumns.containsAll(ti.pkColumns())
				&& ti.columns().stream().allMatch(c -> fkColumns.contains(c.name()));
	}

	private void processJunctionTable(TableInfo ti, String schema, EPackage ePackage, List<Diagnostic> diagnostics) {
		ForeignKeyInfo fkA = ti.foreignKeys().get(0);
		ForeignKeyInfo fkB = ti.foreignKeys().get(1);
		EClass classA = resolveTarget(ePackage, schema, fkA);
		EClass classB = resolveTarget(ePackage, schema, fkB);
		if (isNull(classA) || isNull(classB)) {
			diagnostics.add(warning("Junction table '" + ti.tableName() + "' references a table outside the package ('"
					+ (isNull(classA) ? fkA.pkTable() : fkB.pkTable()) + "'); no ManyToMany created", ti.eClass()));
			return;
		}
		// A self-referencing junction puts both sides on the same class — the second side
		// then takes the qualified name, and the two stay distinct opposites.
		String nameAB = uniqueName(classA, transformAttributeName(fkB.pkTable()) + "s", qualifier(fkB), diagnostics);
		EReference refAB = addManyReference(classA, nameAB, classB);
		String nameBA = uniqueName(classB, transformAttributeName(fkA.pkTable()) + "s", qualifier(fkA), diagnostics);
		EReference refBA = addManyReference(classB, nameBA, classA);
		setOpposite(refAB, refBA);
	}

	// --- Metadata loading ---

	private List<ColumnInfo> loadColumns(DatabaseMetaData metaData, String schema, String tableName, String escape)
			throws SQLException {
		List<ColumnInfo> columns = new ArrayList<>();
		// getColumns takes patterns: an unescaped '_' would also match USERXACCOUNT for USER_ACCOUNT
		try (ResultSet rs = metaData.getColumns(null, pattern(schema, escape), pattern(tableName, escape), "%")) {
			while (rs.next()) {
				columns.add(new ColumnInfo(rs.getString("COLUMN_NAME"), rs.getInt("DATA_TYPE"),
						rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable));
			}
		}
		return columns;
	}

	/**
	 * Foreign keys of a table, one entry per constraint with its columns in key order.
	 * Grouped by {@code FK_NAME}; a driver that reports no name gets one group per
	 * {@code KEY_SEQ} run towards the same target table.
	 */
	private List<ForeignKeyInfo> loadForeignKeys(DatabaseMetaData metaData, String schema, String tableName)
			throws SQLException {
		Map<String, List<KeyColumn>> groups = new LinkedHashMap<>();
		Map<String, ForeignKeyInfo> heads = new LinkedHashMap<>();
		Map<String, Integer> unnamedRuns = new HashMap<>();
		try (ResultSet rs = metaData.getImportedKeys(null, schema, tableName)) {
			while (rs.next()) {
				String fkName = rs.getString("FK_NAME");
				String pkSchema = rs.getString("PKTABLE_SCHEM");
				String pkTable = rs.getString("PKTABLE_NAME");
				int keySeq = rs.getInt("KEY_SEQ");
				String key;
				if (nonNull(fkName)) {
					key = "name:" + fkName;
				} else {
					String target = pkSchema + "." + pkTable;
					int run = keySeq == 1 ? unnamedRuns.merge(target, 1, Integer::sum) : unnamedRuns.getOrDefault(target, 1);
					key = "unnamed:" + target + "#" + run;
				}
				groups.computeIfAbsent(key, k -> new ArrayList<>())
						.add(new KeyColumn(keySeq, rs.getString("FKCOLUMN_NAME"), rs.getString("PKCOLUMN_NAME")));
				heads.putIfAbsent(key, new ForeignKeyInfo(fkName, pkSchema, pkTable, rs.getInt("DELETE_RULE"),
						List.of(), List.of()));
			}
		}
		List<ForeignKeyInfo> foreignKeys = new ArrayList<>();
		for (var entry : groups.entrySet()) {
			List<KeyColumn> keyColumns = new ArrayList<>(entry.getValue());
			keyColumns.sort(Comparator.comparingInt(KeyColumn::keySeq));
			ForeignKeyInfo head = heads.get(entry.getKey());
			foreignKeys.add(new ForeignKeyInfo(head.name(), head.pkSchema(), head.pkTable(), head.deleteRule(),
					keyColumns.stream().map(KeyColumn::fkColumn).toList(),
					keyColumns.stream().map(KeyColumn::pkColumn).toList()));
		}
		return foreignKeys;
	}

	/** Primary key columns in key order — the driver returns them ordered by column name. */
	private List<String> loadPrimaryKeys(DatabaseMetaData metaData, String schema, String tableName) throws SQLException {
		List<KeyColumn> keyColumns = new ArrayList<>();
		try (ResultSet rs = metaData.getPrimaryKeys(null, schema, tableName)) {
			while (rs.next()) {
				keyColumns.add(new KeyColumn(rs.getInt("KEY_SEQ"), rs.getString("COLUMN_NAME"), null));
			}
		}
		keyColumns.sort(Comparator.comparingInt(KeyColumn::keySeq));
		return keyColumns.stream().map(KeyColumn::fkColumn).toList();
	}

	/**
	 * Escapes a name for a {@link DatabaseMetaData} pattern argument, where {@code _} and
	 * {@code %} are wildcards.
	 */
	static String pattern(String name, String escape) {
		if (isNull(name) || isNull(escape) || escape.isEmpty()) {
			return name;
		}
		return name.replace(escape, escape + escape).replace("_", escape + "_").replace("%", escape + "%");
	}

	// --- Naming ---

	/**
	 * Returns {@code preferred} when the class has no feature of that name yet, otherwise a
	 * deterministic fallback — {@code preferred + "Via" + qualifier}, then a counter — and
	 * reports the fallback as an info diagnostic.
	 */
	private String uniqueName(EClass eClass, String preferred, String qualifier, List<Diagnostic> diagnostics) {
		if (!hasFeature(eClass, preferred)) {
			return preferred;
		}
		String base = isNull(qualifier) ? preferred : preferred + "Via" + capitalize(qualifier);
		String candidate = base;
		for (int n = 2; hasFeature(eClass, candidate); n++) {
			candidate = base + n;
		}
		diagnostics.add(new BasicDiagnostic(Diagnostic.INFO, DIAGNOSTIC_SOURCE, 0, "Feature name '" + preferred
				+ "' is already taken on '" + eClass.getName() + "'; using '" + candidate + "'", new Object[] { eClass }));
		return candidate;
	}

	private static boolean hasFeature(EClass eClass, String name) {
		return eClass.getEStructuralFeatures().stream().anyMatch(f -> name.equals(f.getName()));
	}

	/** The FK columns as one name part: {@code ORDER_ID, LINE_NO} → {@code orderIdLineNo}. */
	private String qualifier(ForeignKeyInfo fk) {
		return transformAttributeName(String.join("_", fk.fkColumns()));
	}

	private static String capitalize(String name) {
		return name.isEmpty() ? name : Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	private static String qualifiedColumns(TableInfo ti, ForeignKeyInfo fk) {
		return fk.fkColumns().stream().map(c -> ti.tableName() + "." + c).collect(Collectors.joining(", "));
	}

	/**
	 * The class of the FK target table in this package, {@code null} when it lives in
	 * another schema or is not part of the package.
	 */
	private EClass resolveTarget(EPackage ePackage, String schema, ForeignKeyInfo fk) {
		if (nonNull(fk.pkSchema()) && !fk.pkSchema().equals(schema)) {
			return null;
		}
		return findClassByTableName(ePackage, fk.pkTable());
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
		addAnnotation(element, ANNOTATION_SOURCE, key, value);
	}

	static void addAnnotation(EModelElement element, String source, String key, String value) {
		EAnnotation ann = element.getEAnnotation(source);
		if (isNull(ann)) {
			ann = EcoreFactory.eINSTANCE.createEAnnotation();
			ann.setSource(source);
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

	/** One column of a table, in ordinal order. */
	record ColumnInfo(String name, int dataType, boolean nullable) {}

	/** One key column with its position; {@code pkColumn} is unset for primary keys. */
	record KeyColumn(int keySeq, String fkColumn, String pkColumn) {}

	/** One foreign key constraint, its columns in key order. {@code name} may be {@code null}. */
	record ForeignKeyInfo(String name, String pkSchema, String pkTable, int deleteRule, List<String> fkColumns,
			List<String> pkColumns) {}

	record TableInfo(EClass eClass, String tableName, List<ColumnInfo> columns, List<String> pkColumns,
			List<ForeignKeyInfo> foreignKeys) {}

	/** A forward reference created in pass 1, waiting for its reverse side. */
	record Link(TableInfo owner, ForeignKeyInfo fk, EReference forward, EClass target) {}

	/** A mapped EDataType plus, on fallback, the problem to report ({@code null} if clean). */
	record TypeMapping(EDataType type, String problem) {}
}
