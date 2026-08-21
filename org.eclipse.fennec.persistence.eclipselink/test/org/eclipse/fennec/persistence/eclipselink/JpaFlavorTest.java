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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

/**
 * The flavor axis on the relational side (issue #172) — probed from the driver, never
 * configured, so the declaration cannot disagree with the database behind it.
 */
class JpaFlavorTest {

	@Test
	void resolvesIdsCaseInsensitively() {
		assertThat(JpaFlavor.byId("postgres")).contains(JpaFlavor.POSTGRES);
		assertThat(JpaFlavor.byId("  MariaDB ")).contains(JpaFlavor.MARIADB);
		assertThat(JpaFlavor.byId(null)).contains(JpaFlavor.UNKNOWN);
		assertThat(JpaFlavor.byId("")).contains(JpaFlavor.UNKNOWN);
	}

	@Test
	void unknownIdIsEmptyRatherThanADefault() {
		// a typo must be reported, not silently answered with the wrong capability set
		assertThat(JpaFlavor.byId("oracle")).isEmpty();
	}

	@Test
	void detectsTheProductNamesTheDriversReport() {
		assertThat(JpaFlavor.byProductName("H2")).isEqualTo(JpaFlavor.H2);
		assertThat(JpaFlavor.byProductName("PostgreSQL")).isEqualTo(JpaFlavor.POSTGRES);
		assertThat(JpaFlavor.byProductName("MariaDB")).isEqualTo(JpaFlavor.MARIADB);
	}

	@Test
	void unmeasuredProductIsUnknownRatherThanAGuess() {
		assertThat(JpaFlavor.byProductName("Oracle")).isEqualTo(JpaFlavor.UNKNOWN);
		assertThat(JpaFlavor.byProductName(null)).isEqualTo(JpaFlavor.UNKNOWN);
		assertThat(JpaFlavor.byProductName("  ")).isEqualTo(JpaFlavor.UNKNOWN);
	}

	@Test
	void probesTheDataSource() throws SQLException {
		assertThat(JpaFlavor.detect(dataSourceReporting("PostgreSQL"))).isEqualTo(JpaFlavor.POSTGRES);
	}

	@Test
	void probeClosesTheConnectionItOpened() throws SQLException {
		Connection connection = mock(Connection.class);
		DatabaseMetaData metaData = mock(DatabaseMetaData.class);
		when(metaData.getDatabaseProductName()).thenReturn("H2");
		when(connection.getMetaData()).thenReturn(metaData);
		DataSource dataSource = mock(DataSource.class);
		when(dataSource.getConnection()).thenReturn(connection);

		assertThat(JpaFlavor.detect(dataSource)).isEqualTo(JpaFlavor.H2);
		org.mockito.Mockito.verify(connection).close();
	}

	@Test
	void unreachableDatabaseYieldsTheBaselineRatherThanFailing() throws SQLException {
		// a declaration must always be available: no answer means the portable baseline
		DataSource dataSource = mock(DataSource.class);
		when(dataSource.getConnection()).thenThrow(new SQLException("no route to host"));

		assertThat(JpaFlavor.detect(dataSource)).isEqualTo(JpaFlavor.UNKNOWN);
		assertThat(JpaFlavor.detect(null)).isEqualTo(JpaFlavor.UNKNOWN);
	}

	@Test
	void everyFlavorRoundTripsThroughItsId() {
		for (JpaFlavor flavor : JpaFlavor.values()) {
			assertThat(JpaFlavor.byId(flavor.id())).isEqualTo(Optional.of(flavor));
		}
	}

	private static DataSource dataSourceReporting(String productName) throws SQLException {
		DatabaseMetaData metaData = mock(DatabaseMetaData.class);
		when(metaData.getDatabaseProductName()).thenReturn(productName);
		Connection connection = mock(Connection.class);
		when(connection.getMetaData()).thenReturn(metaData);
		DataSource dataSource = mock(DataSource.class);
		when(dataSource.getConnection()).thenReturn(connection);
		return dataSource;
	}
}
