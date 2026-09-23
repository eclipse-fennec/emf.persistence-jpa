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

import java.math.BigDecimal;
import java.math.BigInteger;
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
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
 *   <li>Vendor-independent type mapping via {@link JDBCType}, refined by the column's
 *       precision, scale and type name: java.time types, UUID, unsigned and integral
 *       decimal columns, inline {@code ENUM(...)} types as {@link EEnum}</li>
 *   <li>ManyToOne + reverse OneToMany references with EOpposite; a composite FK is one reference</li>
 *   <li>Composite PKs declared via {@link CompositeIds#ID_FEATURES}, never several {@code isID} attributes</li>
 *   <li>Junction table detection (exactly two FKs) → ManyToMany references</li>
 *   <li>Unique feature names — collisions get a deterministic fallback and an INFO diagnostic</li>
 *   <li>Containment heuristic (NOT NULL FK + ON DELETE CASCADE)</li>
 *   <li>View support (read-only EClasses)</li>
 *   <li>Multi-schema support (one EPackage per schema)</li>
 *   <li>Naming convention transformation (SNAKE_CASE → CamelCase), references named after
 *       their FK column without the id suffix, plural reverse names, reserved Java names escaped</li>
 *   <li>Literal column defaults as {@code defaultValueLiteral}, comments as GenModel documentation</li>
 *   <li>The schema facts behind the model — original names, sizes, defaults, keys,
 *       indexes — on the {@link ParseResult} ({@link TableFacts}, {@link JunctionFacts})</li>
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
	/** Original column name of an attribute. */
	static final String ANNOTATION_COLUMN_NAME = "columnName";
	/** Comma-separated FK columns of a forward reference, in key order. */
	static final String ANNOTATION_JOIN_COLUMNS = "joinColumns";

	static final String GENMODEL_SOURCE = "http://www.eclipse.org/emf/2002/GenModel";
	static final String GENMODEL_DOCUMENTATION = "documentation";

	/** Java keywords and literals — never a feature or class name. */
	private static final Set<String> JAVA_KEYWORDS = Set.of("abstract", "assert", "boolean", "break", "byte",
			"case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum",
			"extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof",
			"int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return",
			"short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
			"transient", "try", "void", "volatile", "while", "true", "false", "null", "var", "record", "yield");

	/** {@code java.lang} types a generated class must not shadow. */
	private static final Set<String> JAVA_LANG_TYPES = Set.of("Object", "Class", "String", "Integer", "Long",
			"Short", "Byte", "Boolean", "Character", "Double", "Float", "Number", "Enum", "Record", "System",
			"Thread", "Package", "Override", "Math", "Void", "Iterable", "Exception", "Error", "Runtime",
			"Process", "Module", "Comparable", "Runnable");

	private static final Pattern QUOTED_LITERAL = Pattern.compile("'((?:[^']|'')*)'");
	private static final Pattern NUMBER_LITERAL = Pattern.compile("[+-]?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
	private static final Pattern INTEGRAL_LITERAL = Pattern.compile("[+-]?\\d+");
	/** A literal followed by a PostgreSQL cast: {@code 'NEW'::character varying}. */
	private static final Pattern CAST_SUFFIX = Pattern.compile("^('(?:[^']|'')*'|[+-]?[\\d.eE+-]+)::.+$");
	private static final Pattern INLINE_ENUM = Pattern.compile("(?is)^\\s*enum\\s*\\((.*)\\)\\s*$");

	/** Declared precision above this is an unconstrained numeric (PostgreSQL reports 131089). */
	private static final int MAX_DECLARED_PRECISION = 1000;

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
	 * Parses the database schema(s) and returns the packages together with the schema facts
	 * behind them and every diagnostic collected on the way (issues #19, #295). Nothing is
	 * logged here — callers decide what to do with the diagnostics; the legacy
	 * {@link #parseAll()} boundary derives JUL logging from them.
	 *
	 * @return the parse result, never {@code null}
	 */
	public ParseResult parseAllWithDiagnostics() throws SQLException {
		List<EPackage> packages = new ArrayList<>();
		List<Diagnostic> diagnostics = new ArrayList<>();
		List<TableFacts> tables = new ArrayList<>();
		List<JunctionFacts> junctions = new ArrayList<>();
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

				parseSchema(metaData, schema, ePackage, diagnostics, tables, junctions);
				packages.add(ePackage);
			}
		}
		return new ParseResult(packages, diagnostics, tables, junctions);
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
			List<Diagnostic> diagnostics, List<TableFacts> tableFacts, List<JunctionFacts> junctionFacts)
			throws SQLException {
		String escape = metaData.getSearchStringEscape();

		// table metadata in metadata order: the produced model must not depend on hash order
		List<TableInfo> tables = new ArrayList<>();
		for (TableRow row : loadTables(metaData, schema, escape)) {
			EClass eClass = createEClass(classifierName(ePackage, transformClassName(row.name()), diagnostics));
			addAnnotation(eClass, ANNOTATION_TABLE_NAME, row.name());
			if (row.view()) {
				addAnnotation(eClass, ANNOTATION_READ_ONLY, "true");
			}
			addDocumentation(eClass, row.remarks());
			ePackage.getEClassifiers().add(eClass);
			tables.add(new TableInfo(eClass, row.name(), row.view(), row.remarks(),
					loadColumns(metaData, schema, row.name(), escape),
					loadPrimaryKeys(metaData, schema, row.name()), loadForeignKeys(metaData, schema, row.name()),
					row.view() ? List.of() : loadIndexes(metaData, schema, row.name())));
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
		Map<TableInfo, Map<String, EStructuralFeature>> columnFeatures = new LinkedHashMap<>();
		Map<ForeignKeyInfo, EReference> forwardReferences = new HashMap<>();
		for (TableInfo ti : tables) {
			if (!junctions.contains(ti)) {
				columnFeatures.put(ti, processTable(ti, schema, ePackage, links, forwardReferences, diagnostics));
			}
		}
		// Pass 2: reverse references and ManyToMany pairs
		for (Link link : links) {
			addReverseReference(link, diagnostics);
		}
		for (TableInfo junction : junctions) {
			JunctionFacts facts = processJunctionTable(junction, schema, ePackage, diagnostics);
			if (nonNull(facts)) {
				junctionFacts.add(facts);
			}
		}
		for (var entry : columnFeatures.entrySet()) {
			tableFacts.add(toFacts(entry.getKey(), schema, entry.getValue(), forwardReferences));
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

	private List<TableRow> loadTables(DatabaseMetaData metaData, String schema, String escape) throws SQLException {
		List<TableRow> rows = new ArrayList<>();
		readTables(metaData, schema, escape, "TABLE", rows);
		if (config.includeViews()) {
			readTables(metaData, schema, escape, "VIEW", rows);
		}
		return rows;
	}

	private void readTables(DatabaseMetaData metaData, String schema, String escape, String type, List<TableRow> rows)
			throws SQLException {
		try (ResultSet rs = metaData.getTables(null, pattern(schema, escape), "%", new String[] { type })) {
			while (rs.next()) {
				rows.add(new TableRow(rs.getString("TABLE_NAME"), "VIEW".equals(type), blankToNull(rs.getString("REMARKS"))));
			}
		}
	}

	// --- Column processing ---

	/**
	 * Creates attributes and forward references of one table.
	 *
	 * @return the feature per column name
	 */
	private Map<String, EStructuralFeature> processTable(TableInfo ti, String schema, EPackage ePackage,
			List<Link> links, Map<ForeignKeyInfo, EReference> forwardReferences, List<Diagnostic> diagnostics) {
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
		if (ti.pkColumns().isEmpty() && !ti.view()) {
			// no guess (all columns, first column, ...) — whether and how such a table is
			// mapped is the mapping's decision, the model states the fact
			diagnostics.add(warning("Table '" + ti.tableName() + "' has no primary key; class '"
					+ ti.eClass().getName() + "' has no id and cannot be mapped as a JPA entity as is", ti.eClass()));
		}

		// Attributes. A PK column stays an attribute even when it is also an FK column
		// (identifying relationship): the id is declared over attributes. A composite PK is
		// declared through idFeatures — several isID attributes are invalid Ecore.
		boolean compositeId = ti.pkColumns().size() > 1;
		Map<String, EStructuralFeature> features = new HashMap<>();
		for (ColumnInfo column : ti.columns()) {
			boolean isPK = ti.pkColumns().contains(column.name());
			if (referenceColumns.contains(column.name()) && !isPK) {
				continue;
			}
			String name = uniqueName(ti.eClass(), featureName(transformAttributeName(column.name()), ti.eClass(), diagnostics),
					null, diagnostics);
			EClassifier type = resolveType(ePackage, ti, column, diagnostics);
			EAttribute attribute = addAttribute(ti.eClass(), name, type, isPK && !compositeId, !column.nullable());
			addAnnotation(attribute, ANNOTATION_COLUMN_NAME, column.name());
			addDocumentation(attribute, column.remarks());
			applyDefault(attribute, ti, column, diagnostics);
			features.put(column.name(), attribute);
		}
		if (compositeId) {
			List<String> idFeatures = ti.pkColumns().stream().map(c -> features.get(c).getName()).toList();
			addAnnotation(ti.eClass(), CompositeIds.ANNOTATION_SOURCE, CompositeIds.ID_FEATURES,
					String.join(",", idFeatures));
		}

		// Forward references, one per FK — a composite FK is one reference, not one per column
		for (int i = 0; i < resolved.size(); i++) {
			ForeignKeyInfo fk = resolved.get(i);
			boolean identifying = fk.fkColumns().stream().anyMatch(ti.pkColumns()::contains);
			String preferred = fk.fkColumns().size() == 1 && !identifying
					? referenceName(fk.fkColumns().get(0))
					: transformAttributeName(fk.pkTable());
			String name = uniqueName(ti.eClass(), featureName(preferred, ti.eClass(), diagnostics), qualifier(fk),
					diagnostics);
			EReference forward = addReference(ti.eClass(), name, targets.get(i));
			addAnnotation(forward, ANNOTATION_JOIN_COLUMNS, String.join(",", fk.fkColumns()));
			if (fk.fkColumns().size() == 1) {
				ti.columns().stream().filter(c -> c.name().equals(fk.fkColumns().get(0))).findFirst()
						.ifPresent(c -> addDocumentation(forward, c.remarks()));
			}
			for (String column : fk.fkColumns()) {
				features.putIfAbsent(column, forward);
			}
			forwardReferences.put(fk, forward);
			links.add(new Link(ti, fk, forward, targets.get(i)));
		}
		return features;
	}

	/**
	 * The reverse side of a forward reference: many-valued, containment when every FK column
	 * is NOT NULL and the FK deletes on cascade (the parent owns the child).
	 */
	private void addReverseReference(Link link, List<Diagnostic> diagnostics) {
		TableInfo owner = link.owner();
		String preferred = pluralName(owner.tableName());
		String name = uniqueName(link.target(), featureName(preferred, link.target(), diagnostics),
				link.forward().getName(), diagnostics);
		EReference reverse = addManyReference(link.target(), name, owner.eClass());
		boolean notNull = link.fk().fkColumns().stream().allMatch(c -> owner.columns().stream()
				.anyMatch(col -> col.name().equals(c) && !col.nullable()));
		if (notNull && link.fk().deleteRule() == DatabaseMetaData.importedKeyCascade) {
			reverse.setContainment(true);
		}
		setOpposite(link.forward(), reverse);
	}

	private TableFacts toFacts(TableInfo ti, String schema, Map<String, EStructuralFeature> features,
			Map<ForeignKeyInfo, EReference> forwardReferences) {
		List<TableFacts.Column> columns = ti.columns().stream().map(c -> new TableFacts.Column(c.name(),
				c.dataType(), c.typeName(), c.size(), c.decimalDigits(), c.nullable(), c.autoIncrement(),
				c.generated(), c.defaultValue(), c.remarks(), features.get(c.name()))).toList();
		List<TableFacts.ForeignKey> foreignKeys = ti.foreignKeys().stream()
				.map(fk -> toFacts(fk, forwardReferences.get(fk))).toList();
		return new TableFacts(schema, ti.tableName(), ti.view(), ti.remarks(), ti.eClass(), columns,
				ti.pkColumns(), foreignKeys, ti.indexes());
	}

	private static TableFacts.ForeignKey toFacts(ForeignKeyInfo fk, EReference reference) {
		return new TableFacts.ForeignKey(fk.name(), fk.pkSchema(), fk.pkTable(), fk.fkColumns(), fk.pkColumns(),
				fk.deleteRule(), reference);
	}

	// --- Junction table detection & ManyToMany ---

	/**
	 * A junction table has exactly two foreign keys, its columns are exactly their columns,
	 * and its primary key (if any) lies within them. A third foreign key or a payload column
	 * makes it an entity of its own.
	 */
	static boolean isJunctionTable(TableInfo ti) {
		if (ti.view() || ti.foreignKeys().size() != 2) {
			return false;
		}
		Set<String> fkColumns = new HashSet<>();
		ti.foreignKeys().forEach(fk -> fkColumns.addAll(fk.fkColumns()));
		return fkColumns.containsAll(ti.pkColumns())
				&& ti.columns().stream().allMatch(c -> fkColumns.contains(c.name()));
	}

	private JunctionFacts processJunctionTable(TableInfo ti, String schema, EPackage ePackage,
			List<Diagnostic> diagnostics) {
		ForeignKeyInfo fkA = ti.foreignKeys().get(0);
		ForeignKeyInfo fkB = ti.foreignKeys().get(1);
		EClass classA = resolveTarget(ePackage, schema, fkA);
		EClass classB = resolveTarget(ePackage, schema, fkB);
		if (isNull(classA) || isNull(classB)) {
			diagnostics.add(warning("Junction table '" + ti.tableName() + "' references a table outside the package ('"
					+ (isNull(classA) ? fkA.pkTable() : fkB.pkTable()) + "'); no ManyToMany created", ti.eClass()));
			return null;
		}
		// A self-referencing junction puts both sides on the same class — the second side
		// then takes the qualified name, and the two stay distinct opposites.
		String nameAB = uniqueName(classA, featureName(pluralName(fkB.pkTable()), classA, diagnostics),
				qualifier(fkB), diagnostics);
		EReference refAB = addManyReference(classA, nameAB, classB);
		String nameBA = uniqueName(classB, featureName(pluralName(fkA.pkTable()), classB, diagnostics),
				qualifier(fkA), diagnostics);
		EReference refBA = addManyReference(classB, nameBA, classA);
		setOpposite(refAB, refBA);
		return new JunctionFacts(schema, ti.tableName(), toFacts(fkA, null), toFacts(fkB, null), refAB, refBA);
	}

	// --- Metadata loading ---

	private List<ColumnInfo> loadColumns(DatabaseMetaData metaData, String schema, String tableName, String escape)
			throws SQLException {
		List<ColumnInfo> columns = new ArrayList<>();
		// getColumns takes patterns: an unescaped '_' would also match USERXACCOUNT for USER_ACCOUNT
		try (ResultSet rs = metaData.getColumns(null, pattern(schema, escape), pattern(tableName, escape), "%")) {
			while (rs.next()) {
				columns.add(new ColumnInfo(rs.getString("COLUMN_NAME"), rs.getInt("DATA_TYPE"),
						rs.getString("TYPE_NAME"), rs.getInt("COLUMN_SIZE"), rs.getInt("DECIMAL_DIGITS"),
						rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable,
						"YES".equalsIgnoreCase(rs.getString("IS_AUTOINCREMENT")),
						"YES".equalsIgnoreCase(rs.getString("IS_GENERATEDCOLUMN")),
						rs.getString("COLUMN_DEF"), blankToNull(rs.getString("REMARKS"))));
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
	 * Indexes of a table, columns in index order. The primary key's own unique index is left
	 * out — it is the primary key, not an additional fact.
	 */
	private List<TableFacts.Index> loadIndexes(DatabaseMetaData metaData, String schema, String tableName)
			throws SQLException {
		Map<String, List<KeyColumn>> columns = new LinkedHashMap<>();
		Map<String, Boolean> unique = new HashMap<>();
		try (ResultSet rs = metaData.getIndexInfo(null, schema, tableName, false, true)) {
			while (rs.next()) {
				String indexName = rs.getString("INDEX_NAME");
				String columnName = rs.getString("COLUMN_NAME");
				if (rs.getShort("TYPE") == DatabaseMetaData.tableIndexStatistic || isNull(indexName) || isNull(columnName)) {
					continue;
				}
				columns.computeIfAbsent(indexName, k -> new ArrayList<>())
						.add(new KeyColumn(rs.getShort("ORDINAL_POSITION"), columnName, null));
				unique.put(indexName, !rs.getBoolean("NON_UNIQUE"));
			}
		}
		List<String> primaryKey = loadPrimaryKeys(metaData, schema, tableName);
		List<TableFacts.Index> indexes = new ArrayList<>();
		for (var entry : columns.entrySet()) {
			List<KeyColumn> keyColumns = new ArrayList<>(entry.getValue());
			keyColumns.sort(Comparator.comparingInt(KeyColumn::keySeq));
			List<String> names = keyColumns.stream().map(KeyColumn::fkColumn).toList();
			boolean isUnique = unique.get(entry.getKey());
			if (isUnique && names.equals(primaryKey)) {
				continue;
			}
			indexes.add(new TableFacts.Index(entry.getKey(), names, isUnique));
		}
		return indexes;
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

	// --- Defaults ---

	/**
	 * A literal default becomes the attribute's {@code defaultValueLiteral}. An expression —
	 * {@code CURRENT_TIMESTAMP}, a sequence, a function — has no Ecore literal: it stays a
	 * schema fact ({@link TableFacts.Column#defaultValue()}) and is reported, never turned
	 * into a string.
	 */
	private void applyDefault(EAttribute attribute, TableInfo ti, ColumnInfo column, List<Diagnostic> diagnostics) {
		if (isNull(column.defaultValue()) || column.autoIncrement() || column.generated()) {
			return;
		}
		String raw = column.defaultValue().trim();
		if (raw.isEmpty() || "NULL".equalsIgnoreCase(stripDefault(raw))) {
			return;
		}
		String literal = defaultLiteral(raw, attribute.getEAttributeType());
		if (nonNull(literal)) {
			attribute.setDefaultValueLiteral(literal);
		} else {
			diagnostics.add(new BasicDiagnostic(Diagnostic.INFO, DIAGNOSTIC_SOURCE, 0, "Default '" + raw
					+ "' of column '" + ti.tableName() + "." + column.name()
					+ "' is not a literal of the attribute type; kept as schema fact only", new Object[] { ti.eClass() }));
		}
	}

	/**
	 * Returns the Ecore literal for a column default, or {@code null} when the default is an
	 * expression or does not fit the type.
	 */
	static String defaultLiteral(String rawDefault, EDataType type) {
		String value = stripDefault(rawDefault);
		Class<?> instanceClass = type.getInstanceClass();
		Matcher quoted = QUOTED_LITERAL.matcher(value);
		if (quoted.matches()) {
			String text = quoted.group(1).replace("''", "'");
			if (type instanceof EEnum eEnum) {
				return nonNull(eEnum.getEEnumLiteralByLiteral(text)) ? text : null;
			}
			return instanceClass == String.class ? text : null;
		}
		if (isNull(instanceClass)) {
			return null;
		}
		if (instanceClass == Boolean.class || instanceClass == boolean.class) {
			return "TRUE".equalsIgnoreCase(value) || "FALSE".equalsIgnoreCase(value) ? value.toLowerCase(Locale.ROOT)
					: null;
		}
		if (isIntegral(instanceClass)) {
			return INTEGRAL_LITERAL.matcher(value).matches() ? value : null;
		}
		if (isDecimal(instanceClass)) {
			return NUMBER_LITERAL.matcher(value).matches() ? value : null;
		}
		return null;
	}

	/** Removes enclosing parentheses and a PostgreSQL cast: {@code ('NEW'::text)} → {@code 'NEW'}. */
	private static String stripDefault(String raw) {
		String value = raw.trim();
		boolean changed = true;
		while (changed) {
			changed = false;
			if (value.length() > 1 && value.startsWith("(") && value.endsWith(")")) {
				value = value.substring(1, value.length() - 1).trim();
				changed = true;
			}
			Matcher cast = CAST_SUFFIX.matcher(value);
			if (cast.matches()) {
				value = cast.group(1).trim();
				changed = true;
			}
		}
		return value;
	}

	private static boolean isIntegral(Class<?> type) {
		return type == Short.class || type == Integer.class || type == Long.class || type == Byte.class
				|| type == BigInteger.class || type == short.class || type == int.class || type == long.class
				|| type == byte.class;
	}

	private static boolean isDecimal(Class<?> type) {
		return type == Float.class || type == Double.class || type == BigDecimal.class || type == float.class
				|| type == double.class;
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
		diagnostics.add(info("Feature name '" + preferred + "' is already taken on '" + eClass.getName()
				+ "'; using '" + candidate + "'", eClass));
		return candidate;
	}

	/** A Java keyword is no feature name: {@code class} → {@code class_}. */
	private String featureName(String name, EClass eClass, List<Diagnostic> diagnostics) {
		if (!JAVA_KEYWORDS.contains(name)) {
			return name;
		}
		String escaped = name + "_";
		diagnostics.add(info("Feature name '" + name + "' on '" + eClass.getName() + "' is a Java keyword; using '"
				+ escaped + "'", eClass));
		return escaped;
	}

	/**
	 * A class name that is a Java keyword or shadows a {@code java.lang} type gets a
	 * {@code _} suffix; a name taken by another classifier of the package gets a counter.
	 */
	private String classifierName(EPackage ePackage, String name, List<Diagnostic> diagnostics) {
		String candidate = name;
		if (JAVA_KEYWORDS.contains(name) || JAVA_LANG_TYPES.contains(name)) {
			candidate = name + "_";
			diagnostics.add(info("Class name '" + name + "' clashes with Java; using '" + candidate + "'", ePackage));
		}
		String base = candidate;
		for (int n = 2; nonNull(ePackage.getEClassifier(candidate)); n++) {
			candidate = base + n;
		}
		if (!candidate.equals(base)) {
			diagnostics.add(info("Classifier name '" + base + "' is already taken; using '" + candidate + "'", ePackage));
		}
		return candidate;
	}

	private static boolean hasFeature(EClass eClass, String name) {
		return eClass.getEStructuralFeatures().stream().anyMatch(f -> name.equals(f.getName()));
	}

	/**
	 * A forward reference is named after its FK column without the id suffix:
	 * {@code MANAGER_ID} → {@code manager}. Without name transformation the column name stays.
	 */
	String referenceName(String fkColumn) {
		if (!config.transformNames()) {
			return fkColumn;
		}
		String base = fkColumn;
		if (base.length() > 3 && base.toUpperCase(Locale.ROOT).endsWith("_ID")) {
			base = base.substring(0, base.length() - 3);
		}
		return transformAttributeName(base);
	}

	/**
	 * The name of a many-valued reference to a table: its plural, following a small English
	 * rule set. A table name that already ends in {@code s} counts as plural.
	 */
	String pluralName(String tableName) {
		String name = transformAttributeName(tableName);
		if (!config.transformNames()) {
			return name;
		}
		return pluralize(name);
	}

	static String pluralize(String name) {
		if (isNull(name) || name.isEmpty()) {
			return name;
		}
		String lower = name.toLowerCase(Locale.ROOT);
		if (lower.endsWith("is") && lower.length() > 2) {
			// analysis → analyses
			return name.substring(0, name.length() - 2) + "es";
		}
		if (lower.endsWith("ss") || lower.endsWith("us") || lower.endsWith("x") || lower.endsWith("z")
				|| lower.endsWith("ch") || lower.endsWith("sh")) {
			return name + "es";
		}
		if (lower.endsWith("s")) {
			return name;
		}
		if (lower.endsWith("y") && lower.length() > 1 && "aeiou".indexOf(lower.charAt(lower.length() - 2)) < 0) {
			return name.substring(0, name.length() - 1) + "ies";
		}
		return name + "s";
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
	 * Maps a JDBC type code alone, without precision, scale or type name.
	 */
	static TypeMapping mapType(int sqlType) {
		return mapType(new ColumnType(sqlType, null, 0, 0));
	}

	/**
	 * Maps a column type to an Ecore type. The result is either an Ecore literal
	 * ({@link TypeMapping#type()}) or a Java type the package has to declare as data type
	 * ({@link TypeMapping#javaType()} — java.time, UUID), both backed by the core converters.
	 * Unknown or unmapped types fall back to EString; the fallback is reported in
	 * {@link TypeMapping#problem()} instead of being logged, so callers can turn it into a
	 * diagnostic with column context.
	 */
	static TypeMapping mapType(ColumnType column) {
		String typeName = isNull(column.typeName()) ? "" : column.typeName().toUpperCase(Locale.ROOT);
		if ("UUID".equals(typeName)) {
			// PostgreSQL reports OTHER, H2 BINARY — the type name is the reliable signal
			return TypeMapping.java("java.util.UUID");
		}
		JDBCType jdbcType;
		try {
			jdbcType = JDBCType.valueOf(column.jdbcType());
		} catch (IllegalArgumentException e) {
			return new TypeMapping(EcorePackage.Literals.ESTRING, null,
					"Unknown JDBC type code " + column.jdbcType() + ", mapping to EString");
		}
		boolean unsigned = typeName.contains("UNSIGNED");
		EDataType mapped = switch (jdbcType) {
			// an unsigned column needs the next wider type: BIGINT UNSIGNED exceeds Long
			case TINYINT -> EcorePackage.Literals.ESHORT_OBJECT;
			case SMALLINT -> unsigned ? EcorePackage.Literals.EINTEGER_OBJECT : EcorePackage.Literals.ESHORT_OBJECT;
			case INTEGER -> unsigned ? EcorePackage.Literals.ELONG_OBJECT : EcorePackage.Literals.EINTEGER_OBJECT;
			case BIGINT -> unsigned ? EcorePackage.Literals.EBIG_INTEGER : EcorePackage.Literals.ELONG_OBJECT;
			case FLOAT, REAL -> EcorePackage.Literals.EFLOAT_OBJECT;
			case DOUBLE -> EcorePackage.Literals.EDOUBLE_OBJECT;
			case DECIMAL, NUMERIC -> decimalType(column.size(), column.decimalDigits());
			case BOOLEAN, BIT -> EcorePackage.Literals.EBOOLEAN_OBJECT;
			case CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR -> EcorePackage.Literals.ESTRING;
			case BINARY, VARBINARY, LONGVARBINARY, BLOB -> EcorePackage.Literals.EBYTE_ARRAY;
			case CLOB, NCLOB -> EcorePackage.Literals.ESTRING;
			case SQLXML -> EcorePackage.Literals.ESTRING;
			case TIME_WITH_TIMEZONE -> EcorePackage.Literals.EDATE;
			default -> null;
		};
		String javaType = switch (jdbcType) {
			case DATE -> "java.time.LocalDate";
			case TIME -> "java.time.LocalTime";
			case TIMESTAMP -> "java.time.LocalDateTime";
			case TIMESTAMP_WITH_TIMEZONE -> "java.time.OffsetDateTime";
			default -> null;
		};
		if (nonNull(javaType)) {
			return TypeMapping.java(javaType);
		}
		if (jdbcType == JDBCType.TIME_WITH_TIMEZONE) {
			return new TypeMapping(mapped, null,
					"No java.time converter for TIME WITH TIME ZONE, mapping to EDate");
		}
		return nonNull(mapped) ? new TypeMapping(mapped, null, null)
				: new TypeMapping(EcorePackage.Literals.ESTRING, null,
						"Unmapped JDBC type " + jdbcType + ", mapping to EString");
	}

	/**
	 * A decimal without scale is integral: {@code ELongObject} up to 18 digits,
	 * {@code EBigInteger} above. An unconstrained numeric reports no declared precision
	 * (or an implausibly large one) and keeps {@code EBigDecimal}.
	 */
	private static EDataType decimalType(int precision, int scale) {
		if (scale != 0 || precision <= 0 || precision > MAX_DECLARED_PRECISION) {
			return EcorePackage.Literals.EBIG_DECIMAL;
		}
		return precision <= 18 ? EcorePackage.Literals.ELONG_OBJECT : EcorePackage.Literals.EBIG_INTEGER;
	}

	private EClassifier resolveType(EPackage ePackage, TableInfo ti, ColumnInfo column, List<Diagnostic> diagnostics) {
		List<String> enumValues = inlineEnumValues(column.typeName());
		if (nonNull(enumValues)) {
			return createEnum(ePackage, ti, column, enumValues, diagnostics);
		}
		TypeMapping mapping = mapType(new ColumnType(column.dataType(), column.typeName(), column.size(),
				column.decimalDigits()));
		if (nonNull(mapping.problem())) {
			diagnostics.add(warning(mapping.problem() + " for column '" + ti.tableName() + "." + column.name() + "'",
					ti.eClass()));
		}
		return nonNull(mapping.javaType()) ? javaDataType(ePackage, mapping.javaType()) : mapping.type();
	}

	/**
	 * The values of an inline enumeration type name — {@code ENUM('A', 'B')} as H2 reports
	 * it and MySQL/MariaDB spell {@code COLUMN_TYPE} — or {@code null} for any other type.
	 */
	static List<String> inlineEnumValues(String typeName) {
		if (isNull(typeName)) {
			return null;
		}
		Matcher matcher = INLINE_ENUM.matcher(typeName);
		if (!matcher.matches()) {
			return null;
		}
		List<String> values = new ArrayList<>();
		Matcher literal = QUOTED_LITERAL.matcher(matcher.group(1));
		while (literal.find()) {
			values.add(literal.group(1).replace("''", "'"));
		}
		return values.isEmpty() ? null : values;
	}

	/**
	 * An inline enumeration becomes an {@link EEnum} named after table and column. The
	 * database value is the literal's {@code literal}; the name is a Java-safe form of it.
	 */
	private EEnum createEnum(EPackage ePackage, TableInfo ti, ColumnInfo column, List<String> values,
			List<Diagnostic> diagnostics) {
		EEnum eEnum = EcoreFactory.eINSTANCE.createEEnum();
		eEnum.setName(classifierName(ePackage, ti.eClass().getName() + snakeToPascalCase(column.name()), diagnostics));
		Set<String> names = new HashSet<>();
		for (int i = 0; i < values.size(); i++) {
			EEnumLiteral literal = EcoreFactory.eINSTANCE.createEEnumLiteral();
			String name = enumLiteralName(values.get(i));
			String candidate = name;
			for (int n = 2; !names.add(candidate); n++) {
				candidate = name + n;
			}
			literal.setName(candidate);
			literal.setLiteral(values.get(i));
			literal.setValue(i);
			eEnum.getELiterals().add(literal);
		}
		ePackage.getEClassifiers().add(eEnum);
		return eEnum;
	}

	static String enumLiteralName(String value) {
		StringBuilder sb = new StringBuilder();
		for (char c : value.toCharArray()) {
			sb.append(Character.isLetterOrDigit(c) ? Character.toUpperCase(c) : '_');
		}
		if (sb.isEmpty() || !Character.isJavaIdentifierStart(sb.charAt(0))) {
			sb.insert(0, '_');
		}
		return sb.toString();
	}

	/** The package's data type for a Java type, created on first use: {@code java.time.LocalDate} → {@code ELocalDate}. */
	private static EDataType javaDataType(EPackage ePackage, String javaType) {
		for (EClassifier classifier : ePackage.getEClassifiers()) {
			if (classifier instanceof EDataType dataType && !(classifier instanceof EEnum)
					&& javaType.equals(dataType.getInstanceClassName())) {
				return dataType;
			}
		}
		EDataType dataType = EcoreFactory.eINSTANCE.createEDataType();
		dataType.setName("E" + javaType.substring(javaType.lastIndexOf('.') + 1));
		dataType.setInstanceClassName(javaType);
		ePackage.getEClassifiers().add(dataType);
		return dataType;
	}

	private static Diagnostic warning(String message, EModelElement affected) {
		return new BasicDiagnostic(Diagnostic.WARNING, DIAGNOSTIC_SOURCE, 0, message, new Object[] { affected });
	}

	private static Diagnostic info(String message, EModelElement affected) {
		return new BasicDiagnostic(Diagnostic.INFO, DIAGNOSTIC_SOURCE, 0, message, new Object[] { affected });
	}

	private static String blankToNull(String value) {
		return isNull(value) || value.isBlank() ? null : value;
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

	static EAttribute addAttribute(EClass eClass, String name, EClassifier type, boolean isId, boolean required) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		eClass.getEStructuralFeatures().add(attribute);
		attribute.setName(name);
		attribute.setEType(type);
		attribute.setID(isId);
		if (required) {
			attribute.setLowerBound(1);
		}
		return attribute;
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

	/** A table or column comment becomes GenModel documentation. */
	static void addDocumentation(EModelElement element, String remarks) {
		if (nonNull(remarks)) {
			addAnnotation(element, GENMODEL_SOURCE, GENMODEL_DOCUMENTATION, remarks);
		}
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

	/** One row of {@code getTables}. */
	record TableRow(String name, boolean view, String remarks) {}

	/** One column of a table, in ordinal order. */
	record ColumnInfo(String name, int dataType, String typeName, int size, int decimalDigits, boolean nullable,
			boolean autoIncrement, boolean generated, String defaultValue, String remarks) {}

	/** The type facts of a column that drive the type mapping. */
	record ColumnType(int jdbcType, String typeName, int size, int decimalDigits) {}

	/** One key column with its position; {@code pkColumn} is unset for primary keys and indexes. */
	record KeyColumn(int keySeq, String fkColumn, String pkColumn) {}

	/** One foreign key constraint, its columns in key order. {@code name} may be {@code null}. */
	record ForeignKeyInfo(String name, String pkSchema, String pkTable, int deleteRule, List<String> fkColumns,
			List<String> pkColumns) {}

	record TableInfo(EClass eClass, String tableName, boolean view, String remarks, List<ColumnInfo> columns,
			List<String> pkColumns, List<ForeignKeyInfo> foreignKeys, List<TableFacts.Index> indexes) {}

	/** A forward reference created in pass 1, waiting for its reverse side. */
	record Link(TableInfo owner, ForeignKeyInfo fk, EReference forward, EClass target) {}

	/**
	 * A mapped type plus, on fallback, the problem to report ({@code null} if clean).
	 * Exactly one of {@code type} and {@code javaType} is set.
	 */
	record TypeMapping(EDataType type, String javaType, String problem) {

		static TypeMapping java(String javaType) {
			return new TypeMapping(null, javaType, null);
		}
	}
}
