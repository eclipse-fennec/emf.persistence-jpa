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
package org.eclipse.fennec.persistence.eclipselink;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * The database behind the JPA backend — the {@code flavor} half of the
 * {@code backend × flavor} axis on the relational side (issue #172).
 * <p>
 * The counterpart of {@code MongoFlavor}, and it exists for the same reason: what a backend
 * serves is not a property of the backend but of the concrete database. PostgreSQL can serve
 * fuzzy matching, H2 cannot; identifier length, timestamp precision and collation differ.
 * While the JPA processor declared one static capability set for every database, such a
 * difference could not be expressed at all.
 * <p>
 * The flavor is <b>probed</b>, never configured: the JDBC driver already knows which product
 * it is talking to, and a configuration key would only add a second truth that can disagree
 * with reality — the failure mode where a deployment declares a capability its database does
 * not have. {@link #UNKNOWN} is the honest answer for a database nobody has measured: it
 * declares the portable baseline rather than guessing.
 *
 * @author Mark Hoffmann
 * @since 21.08.2026
 */
public enum JpaFlavor {

	/** H2, the in-memory database the default test setup and the examples run on. */
	H2("h2", "h2"),

	/** PostgreSQL. */
	POSTGRES("postgres", "postgresql"),

	/** MariaDB (issue #158) — MySQL-compatible wire protocol, distinct product name. */
	MARIADB("mariadb", "mariadb"),

	/**
	 * A database no measurement covers. Declares the portable baseline, which is what every
	 * relational store the translation targets can serve.
	 */
	UNKNOWN("unknown", null);

	private static final Logger LOG = Logger.getLogger(JpaFlavor.class.getName());

	private final String id;
	private final String productMarker;

	JpaFlavor(String id, String productMarker) {
		this.id = id;
		this.productMarker = productMarker;
	}

	/** @return the stable service-property id of this flavor */
	public String id() {
		return id;
	}

	/**
	 * Resolves a flavor from its {@link #id()}, case-insensitively.
	 *
	 * @param id the id; {@code null} or blank yields {@link #UNKNOWN}
	 * @return the flavor, or {@link Optional#empty()} for an unknown non-blank id — callers
	 *         report that rather than silently degrading to a wrong capability set
	 */
	public static Optional<JpaFlavor> byId(String id) {
		if (id == null || id.isBlank()) {
			return Optional.of(UNKNOWN);
		}
		String normalized = id.trim().toLowerCase(Locale.ROOT);
		for (JpaFlavor flavor : values()) {
			if (flavor.id.equals(normalized)) {
				return Optional.of(flavor);
			}
		}
		return Optional.empty();
	}

	/**
	 * Detects the flavor from the JDBC product name, as
	 * {@link DatabaseMetaData#getDatabaseProductName()} reports it ("H2", "PostgreSQL",
	 * "MariaDB").
	 *
	 * @param productName the reported product name; may be {@code null}
	 * @return the matching flavor, or {@link #UNKNOWN} when nothing matches
	 */
	public static JpaFlavor byProductName(String productName) {
		if (productName == null || productName.isBlank()) {
			return UNKNOWN;
		}
		String normalized = productName.trim().toLowerCase(Locale.ROOT);
		for (JpaFlavor flavor : values()) {
			if (flavor.productMarker != null && normalized.contains(flavor.productMarker)) {
				return flavor;
			}
		}
		return UNKNOWN;
	}

	/**
	 * Probes the flavor by asking the data source's metadata once.
	 * <p>
	 * Failure is not fatal: a database that cannot be reached or refuses metadata yields
	 * {@link #UNKNOWN}, so a capability declaration is always available — a backend that
	 * cannot say what it is declares the baseline rather than nothing.
	 *
	 * @param dataSource the data source to ask; may be {@code null}
	 * @return the detected flavor, never {@code null}
	 */
	public static JpaFlavor detect(DataSource dataSource) {
		if (dataSource == null) {
			return UNKNOWN;
		}
		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			JpaFlavor flavor = byProductName(metaData == null ? null : metaData.getDatabaseProductName());
			if (flavor == UNKNOWN && metaData != null) {
				LOG.info(() -> String.format(
						"No JPA flavor is known for database product '%s' — declaring the portable baseline",
						safeProductName(metaData)));
			}
			return flavor;
		} catch (SQLException e) {
			LOG.log(Level.INFO, e,
					() -> "Could not probe the database flavor — declaring the portable baseline");
			return UNKNOWN;
		}
	}

	private static String safeProductName(DatabaseMetaData metaData) {
		try {
			return metaData.getDatabaseProductName();
		} catch (SQLException e) {
			return "<unavailable>";
		}
	}

	@Override
	public String toString() {
		return id;
	}
}
