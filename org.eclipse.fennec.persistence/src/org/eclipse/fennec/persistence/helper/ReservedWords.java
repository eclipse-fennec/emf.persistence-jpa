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
package org.eclipse.fennec.persistence.helper;

import static java.util.Objects.isNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The words a supported database refuses as an unquoted table or column name (issue #399).
 * <p>
 * The lists are measured, not copied from documentation: every candidate — the SQL:2016 reserved
 * words, the PostgreSQL keywords ({@code pg_get_keywords()}), the MariaDB keywords
 * ({@code information_schema.KEYWORDS}), the H2 keywords and the JDBC {@code getSQLKeywords()} of
 * each driver — was used as a column and as a table name in {@code CREATE TABLE}, {@code INSERT},
 * {@code SELECT … WHERE … ORDER BY}, {@code UPDATE} and {@code DELETE}. A word is listed where one
 * of these statements fails. Measured against H2 2.3.232, PostgreSQL 17 and MariaDB 11.
 * <p>
 * Tables and columns differ: MariaDB reads {@code value (} or {@code count (} as a function call,
 * so these fail as a table name but serve as a column name; H2 refuses {@code top} only as a column.
 * <p>
 * A derived name is {@link #escape(String, Kind) escaped} against the words of <em>all</em>
 * dialects, so a model gets the same schema on every database. A name the model or the mapping
 * declares is taken literally and only checked against the dialect the unit actually runs on.
 *
 * @author Mark Hoffmann
 * @since 07.11.2026
 */
public final class ReservedWords {

	/** The suffix that escapes a reserved derived name: {@code value} becomes {@code value_}. */
	public static final String ESCAPE_SUFFIX = "_";

	/** The databases whose reserved words are known. */
	public enum Dialect {
		H2, POSTGRESQL, MARIADB
	}

	/** Where a name is used. */
	public enum Kind {
		TABLE, COLUMN
	}

	private static final String H2_COLUMN = """
			_rowid_ all and any array as asymmetric authorization between case cast check constraint
			cross current_catalog current_date current_path current_role current_schema current_time
			current_timestamp current_user day default distinct else end except exists false fetch for
			foreign from full group having hour if in inner intersect interval is join key left like
			limit localtime localtimestamp minus minute month natural not null offset on or order
			primary qualify right row rownum second select session_user set some symmetric system_user
			table to top true uescape union unique unknown user using value values when where window
			with year
			""";

	private static final String H2_TABLE = """
			_rowid_ all and any array as asymmetric authorization between case cast check constraint
			cross current_catalog current_date current_path current_role current_schema current_time
			current_timestamp current_user day default distinct else end except exists false fetch for
			foreign from full group having hour if in inner intersect interval is join key left like
			limit localtime localtimestamp minus minute month natural not null offset on or order
			primary qualify right row rownum second select session_user set some symmetric system_user
			table to true uescape union unique unknown user using value values when where window with
			year
			""";

	private static final String POSTGRESQL_COLUMN = """
			all analyse analyze and any array as asc asymmetric authorization binary both case cast
			check collate collation column concurrently constraint create cross current_catalog
			current_date current_role current_schema current_time current_timestamp current_user
			default deferrable desc distinct do else end except false fetch for foreign freeze from
			full grant group having ilike in initially inner intersect into is isnull join lateral
			leading left like limit localtime localtimestamp natural not notnull null offset on only
			or order outer overlaps placing primary references returning right select session_user
			similar some symmetric system_user table tablesample then to trailing true union unique
			user using variadic verbose when where window with
			""";

	private static final String POSTGRESQL_TABLE = """
			all analyse analyze and any array as asc asymmetric authorization binary both case cast
			check collate collation column concurrently constraint create cross current_catalog
			current_date current_role current_schema current_time current_timestamp current_user
			default deferrable desc distinct do else end except false fetch for foreign freeze from
			full grant group having ilike in initially inner intersect into is isnull join lateral
			leading left like limit localtime localtimestamp natural not notnull null offset on only
			or order outer overlaps placing primary references returning right select session_user
			similar some symmetric system_user table tablesample then to trailing true union unique
			user using variadic verbose when where window with
			""";

	private static final String MARIADB_COLUMN = """
			accessible add all alter analyze and as asc asensitive before between bigint binary blob
			both by call cascade case change char character check collate column condition constraint
			continue convert create cross current_date current_role current_time current_timestamp
			current_user cursor databases day_hour day_microsecond day_minute day_second dec decimal
			declare default delayed delete delete_domain_id desc describe deterministic distinct
			distinctrow div do_domain_ids double drop dual each else elseif enclosed escaped except
			exists exit explain false fetch float float4 float8 for force foreign from fulltext grant
			group having high_priority hour_microsecond hour_minute hour_second if ignore
			ignore_domain_ids in index infile inner inout insensitive insert int int1 int2 int3 int4
			int8 integer intersect interval into is iterate join key keys kill leading leave left like
			limit linear lines load localtime localtimestamp lock long longblob longtext loop
			low_priority master_demote_to_replica master_demote_to_slave match maxvalue mediumblob
			mediumint mediumtext middleint minute_microsecond minute_second mod modifies natural
			no_write_to_binlog not null numeric offset on optimize optionally or order out outer
			outfile over page_checksum parse_vcol_expr partition portion precision primary procedure
			purge range read read_write reads real recursive ref_system_id references regexp release
			rename repeat replace require resignal restrict return returning revoke right rlike
			row_number rows schemas second_microsecond select sensitive separator set show signal
			smallint spatial specific sql sql_after_gtids sql_before_gtids sql_big_result
			sql_buffer_result sql_cache sql_calc_found_rows sql_no_cache sql_small_result sqlexception
			sqlstate sqlwarning ssl starting stats_auto_recalc stats_persistent stats_sample_pages
			straight_join table terminated then tinyblob tinyint tinytext to trailing trigger true
			undo union unique unlock unsigned update usage use using utc_date utc_time utc_timestamp
			values varbinary varchar varcharacter varying vector when where while with write xor
			year_month zerofill
			""";

	private static final String MARIADB_TABLE = """
			accessible add all alter analyze and as asc asensitive before between bigint binary blob
			both by call cascade case cast change char character check collate column condition
			constraint continue convert count create cross cume_dist current_date current_role
			current_time current_timestamp current_user cursor databases day_hour day_microsecond
			day_minute day_second dec decimal declare default delayed delete delete_domain_id
			dense_rank desc describe deterministic distinct distinctrow div do_domain_ids double drop
			dual each else elseif enclosed escaped except exists exit explain extract false fetch
			first_value float float4 float8 for force foreign from fulltext grant group having
			high_priority hour_microsecond hour_minute hour_second if ignore ignore_domain_ids in
			index infile inner inout insensitive insert int int1 int2 int3 int4 int8 integer intersect
			interval into is iterate join json_arrayagg json_objectagg key keys kill lag lead leading
			leave left like limit linear lines load localtime localtimestamp lock long longblob
			longtext loop low_priority master_demote_to_replica master_demote_to_slave match max
			maxvalue mediumblob mediumint mediumtext middleint min minute_microsecond minute_second
			mod modifies natural no_write_to_binlog not nth_value ntile null numeric offset on
			optimize optionally or order out outer outfile over page_checksum parse_vcol_expr
			partition percent_rank percentile_cont percentile_disc portion position precision primary
			procedure purge range rank read read_write reads real recursive ref_system_id references
			regexp release rename repeat replace require resignal restrict return returning revoke
			right rlike row_number rows schemas second_microsecond select sensitive separator set show
			signal smallint spatial specific sql sql_after_gtids sql_before_gtids sql_big_result
			sql_calc_found_rows sql_small_result sqlexception sqlstate sqlwarning ssl starting
			stats_auto_recalc stats_persistent stats_sample_pages stddev_pop stddev_samp straight_join
			substring sum table terminated then tinyblob tinyint tinytext to trailing trigger trim
			true undo union unique unlock unsigned update usage use using utc_date utc_time
			utc_timestamp value values var_pop var_samp varbinary varchar varcharacter varying vector
			when where while with write xor year_month zerofill
			""";

	private static final Map<Dialect, Map<Kind, Set<String>>> WORDS = new EnumMap<>(Dialect.class);

	static {
		register(Dialect.H2, H2_COLUMN, H2_TABLE);
		register(Dialect.POSTGRESQL, POSTGRESQL_COLUMN, POSTGRESQL_TABLE);
		register(Dialect.MARIADB, MARIADB_COLUMN, MARIADB_TABLE);
	}

	private ReservedWords() {
	}

	private static void register(Dialect dialect, String columns, String tables) {
		Map<Kind, Set<String>> words = new EnumMap<>(Kind.class);
		words.put(Kind.COLUMN, parse(columns));
		words.put(Kind.TABLE, parse(tables));
		WORDS.put(dialect, words);
	}

	private static Set<String> parse(String words) {
		return Arrays.stream(words.trim().split("\\s+")).collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * @param dialect the database
	 * @param kind table or column
	 * @return the words the database refuses unquoted, in lower case
	 */
	public static Set<String> words(Dialect dialect, Kind kind) {
		return WORDS.get(dialect).get(kind);
	}

	/**
	 * @param name a table or column name, any case
	 * @param kind where it is used
	 * @param dialect the database
	 * @return {@code true} if the database refuses the name unquoted
	 */
	public static boolean isReserved(String name, Kind kind, Dialect dialect) {
		return !isNull(name) && words(dialect, kind).contains(name.toLowerCase(Locale.ROOT));
	}

	/**
	 * @param name a table or column name, any case
	 * @param kind where it is used
	 * @return {@code true} if any supported database refuses the name unquoted
	 */
	public static boolean isReserved(String name, Kind kind) {
		return !reservingDialects(name, kind).isEmpty();
	}

	/**
	 * @param name a table or column name, any case
	 * @param kind where it is used
	 * @return the databases that refuse the name unquoted, empty if none does
	 */
	public static Set<Dialect> reservingDialects(String name, Kind kind) {
		Set<Dialect> dialects = EnumSet.noneOf(Dialect.class);
		for (Dialect dialect : Dialect.values()) {
			if (isReserved(name, kind, dialect)) {
				dialects.add(dialect);
			}
		}
		return Collections.unmodifiableSet(dialects);
	}

	/**
	 * The name a derived table or column gets: the name itself, or — if any supported database
	 * refuses it unquoted — the name with {@link #ESCAPE_SUFFIX} appended. The rule is the same
	 * for every database, so the schema does not depend on where the unit runs.
	 * @param name the derived name, may be {@code null}
	 * @param kind where it is used
	 * @return the name to use, {@code null} for {@code null}
	 */
	public static String escape(String name, Kind kind) {
		String escaped = name;
		while (isReserved(escaped, kind)) {
			escaped = escaped + ESCAPE_SUFFIX;
		}
		return escaped;
	}
}
