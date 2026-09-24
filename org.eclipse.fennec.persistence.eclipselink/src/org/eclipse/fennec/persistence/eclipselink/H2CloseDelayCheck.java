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

import static java.util.Objects.isNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * Detects an embedded H2 database that closes whenever its last connection closes
 * ({@code DB_CLOSE_DELAY=0}, H2's default) — issue #316.
 * <p>
 * Handed a {@code DataSource}, EclipseLink holds no connection between operations, so without a
 * pool in front H2 closes and reopens such a database around every operation. That is slow, an
 * in-memory database loses its content with every close, and H2 2.3 has been observed to lose
 * committed rows of a file database in these open/close cycles: the store is truncated on close,
 * with no write in between (reproduced with plain JDBC, see emf.ogc.features#8). A server-mode
 * URL ({@code tcp:}, {@code ssl:}) is not affected — the server keeps the database open.
 *
 * @author Mark Hoffmann
 * @since 24.09.2026
 */
public final class H2CloseDelayCheck {

	private static final Logger LOG = Logger.getLogger(H2CloseDelayCheck.class.getName());

	private H2CloseDelayCheck() {
	}

	/**
	 * @param dataSource the unit's data source
	 * @param unitName the persistence unit name, for the message
	 * @return the warning to log, or empty when the database is no embedded H2 closing on the
	 *         last connection, or cannot be probed
	 */
	public static Optional<String> warning(DataSource dataSource, String unitName) {
		if (isNull(dataSource)) {
			return Optional.empty();
		}
		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			if (isNull(metaData) || !"H2".equalsIgnoreCase(metaData.getDatabaseProductName())) {
				return Optional.empty();
			}
			String url = metaData.getURL();
			if (isNull(url) || isServerMode(url) || closeDelay(connection) != 0) {
				return Optional.empty();
			}
			return Optional.of(String.format("Persistence unit '%s' runs on an embedded H2 database (%s) "
					+ "with DB_CLOSE_DELAY=0. Without a connection pool H2 closes and reopens it around "
					+ "every operation, which is slow and, with H2 2.x, can lose committed rows (issue #316). "
					+ "Add ;DB_CLOSE_DELAY=-1 to the URL or put a pool in front of the data source.",
					unitName, url));
		} catch (SQLException e) {
			LOG.log(Level.FINE, e, () -> "Could not probe the H2 close delay of unit '" + unitName + "'");
			return Optional.empty();
		}
	}

	private static boolean isServerMode(String url) {
		String lower = url.toLowerCase(Locale.ROOT);
		return lower.startsWith("jdbc:h2:tcp:") || lower.startsWith("jdbc:h2:ssl:");
	}

	/**
	 * @return the database's {@code DB_CLOSE_DELAY} in seconds; {@code -1} keeps it open
	 */
	private static int closeDelay(Connection connection) throws SQLException {
		try (Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("SELECT SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS "
						+ "WHERE SETTING_NAME = 'DB_CLOSE_DELAY'")) {
			return rs.next() ? Integer.parseInt(rs.getString(1).trim()) : 0;
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}
