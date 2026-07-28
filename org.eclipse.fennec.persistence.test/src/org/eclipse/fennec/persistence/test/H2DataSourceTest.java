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
package org.eclipse.fennec.persistence.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.sql.Statement;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.junit.jupiter.api.BeforeEach;
//import org.eclipse.daanse.jdbc.datasource.metatype.h2.api.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
class H2DataSourceTest {
	
	@TempDir
	private File storage;
	
	@BeforeEach
	public void before() {
		System.setProperty(TestAnnotations.PROP_MODEL_PATH, storage.getAbsolutePath().replace('\\', '/'));
	}
	
    @Test
    void noConfigurationNoServiceTest(
            @InjectService(cardinality = 0, timeout = 500) ServiceAware<DataSource> saDataSource, //
            @InjectService(cardinality = 0, timeout = 500) ServiceAware<XADataSource> saXaDataSource, //
            @InjectService(cardinality = 0, timeout = 500) ServiceAware<ConnectionPoolDataSource> saCpDataSource)
            throws Exception {

        assertThat(saDataSource.getServices()).isEmpty();
        assertThat(saXaDataSource.getServices()).isEmpty();
        assertThat(saCpDataSource.getServices()).isEmpty();

    }

    @Test
    @TestAnnotations.DefaultEPersistenceSetup
    void serviceWithConfigurationTest(@InjectService(timeout = 500) ServiceAware<DataSource> serviceAwareDataSource, //
            @InjectService(timeout = 500) ServiceAware<XADataSource> serviceAwareXaDataSource, //
            @InjectService(timeout = 500) ServiceAware<ConnectionPoolDataSource> serviceAwareCpDataSource)
            throws Exception {

        assertThat(serviceAwareDataSource.getServices()).hasSize(1);
        assertThat(serviceAwareXaDataSource.getServices()).hasSize(1);
        assertThat(serviceAwareCpDataSource.getServices()).hasSize(1);

        DataSource dataSource = serviceAwareDataSource.waitForService(0);
        Statement statement = dataSource.getConnection().createStatement();
        boolean execute = statement.execute("CREATE TABLE CONTACT (pk_Contact BIGINT NOT NULL, context VARCHAR, type VARCHAR, falue VARCHAR, PRIMARY KEY (pk_Contact));");
        assertThat(execute).isFalse();
        execute = statement.execute("SELECT COUNT(*) FROM CONTACT;");
        assertThat(execute).isTrue();
        
        XADataSource xaDataSource = serviceAwareXaDataSource.waitForService(0);
        ConnectionPoolDataSource cpDataSource = serviceAwareCpDataSource.waitForService(0);
        cpDataSource.getPooledConnection();

        // Singleton
        assertThat(dataSource).isEqualTo(xaDataSource).isEqualTo(cpDataSource);

    }
}