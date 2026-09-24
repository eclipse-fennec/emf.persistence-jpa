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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.UUID;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * An embedded H2 closing on its last connection is reported (issue #316): without a pool it is
 * closed and reopened around every operation, which H2 2.x has been seen to lose rows in.
 */
class H2CloseDelayCheckTest {

	@TempDir
	Path dir;

	@Test
	void fileDatabaseWithoutCloseDelayIsReported() {
		String url = "jdbc:h2:file:" + dir.resolve("bath").toAbsolutePath();

		assertThat(H2CloseDelayCheck.warning(h2(url), "bath")).hasValueSatisfying(message -> assertThat(message)
				.contains("'bath'", "DB_CLOSE_DELAY=0", ";DB_CLOSE_DELAY=-1", "#316"));
	}

	@Test
	void fileDatabaseKeptOpenIsNotReported() {
		String url = "jdbc:h2:file:" + dir.resolve("bath").toAbsolutePath() + ";DB_CLOSE_DELAY=-1";

		assertThat(H2CloseDelayCheck.warning(h2(url), "bath")).isEmpty();
	}

	@Test
	void aPositiveCloseDelayIsNotReported() {
		String url = "jdbc:h2:file:" + dir.resolve("bath").toAbsolutePath() + ";DB_CLOSE_DELAY=10";

		assertThat(H2CloseDelayCheck.warning(h2(url), "bath")).isEmpty();
	}

	@Test
	void inMemoryDatabaseWithoutCloseDelayIsReported() {
		assertThat(H2CloseDelayCheck.warning(h2("jdbc:h2:mem:" + UUID.randomUUID()), "mem")).isPresent();
	}

	@Test
	void inMemoryDatabaseKeptOpenIsNotReported() {
		String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1";

		assertThat(H2CloseDelayCheck.warning(h2(url), "mem")).isEmpty();
	}

	@Test
	void serverModeIsNotReported() throws Exception {
		assertThat(H2CloseDelayCheck.warning(probe("H2", "jdbc:h2:tcp://localhost/~/bath"), "bath")).isEmpty();
	}

	@Test
	void otherDatabasesAreNotReported() throws Exception {
		assertThat(H2CloseDelayCheck.warning(probe("PostgreSQL", "jdbc:postgresql://localhost/bath"), "bath"))
				.isEmpty();
	}

	@Test
	void noDataSourceIsNotReported() {
		assertThat(H2CloseDelayCheck.warning(null, "bath")).isEmpty();
	}

	private static DataSource h2(String url) {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL(url);
		dataSource.setUser("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	private static DataSource probe(String product, String url) throws Exception {
		DatabaseMetaData metaData = mock(DatabaseMetaData.class);
		when(metaData.getDatabaseProductName()).thenReturn(product);
		when(metaData.getURL()).thenReturn(url);
		Connection connection = mock(Connection.class);
		when(connection.getMetaData()).thenReturn(metaData);
		DataSource dataSource = mock(DataSource.class);
		when(dataSource.getConnection()).thenReturn(connection);
		return dataSource;
	}
}
