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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Table;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Integration tests verifying that {@link EDynamicHelper#addETypes} creates
 * non-default schemas before calling
 * {@link org.eclipse.persistence.tools.schemaframework.DynamicSchemaManager#createTables},
 * so that entities mapped to schema-qualified tables work on a fresh in-memory
 * H2 database (which has no pre-existing schemas).
 */
class EDynamicHelperSchemaTest {

    private EntityManagerFactory emf;
    private Server serverSession;

    @AfterEach
    void tearDown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    @DisplayName("addETypes with entity in non-default schema does not throw")
    void testAddETypes_withNonDefaultSchema_doesNotThrow() {
        assertThatNoException().isThrownBy(() -> initEclipseLink(mappingWithSchema("Order", "ORDERS", "SALES")));
    }

    @Test
    @DisplayName("Non-default schema is created in the database")
    void testAddETypes_nonDefaultSchema_isCreatedInDb() throws Exception {
        initEclipseLink(mappingWithSchema("Order", "ORDERS", "SALES"));

        assertThat(schemaExists("SALES")).isTrue();
    }

    @Test
    @DisplayName("Table is created inside the non-default schema")
    void testAddETypes_tableInNonDefaultSchema_isCreatedInDb() throws Exception {
        initEclipseLink(mappingWithSchema("Order", "ORDERS", "SALES"));

        assertThat(tableExists("SALES", "ORDERS")).isTrue();
    }

    @Test
    @DisplayName("Multiple non-default schemas are all created")
    void testAddETypes_multipleSchemas_allCreated() throws Exception {
        EPackage pkg = newPackage("multi");
        EClass invoiceClass = newEClassWithLongId(pkg, "Invoice");
        EClass contractClass = newEClassWithLongId(pkg, "Contract");

        EntityMappings mappings = new EntityMapper().createMappings(List.of(invoiceClass, contractClass));
        setSchema(mappings, "Invoice", "FINANCE");
        setSchema(mappings, "Contract", "HR");

        initEclipseLink(mappings);

        assertThat(schemaExists("FINANCE")).isTrue();
        assertThat(schemaExists("HR")).isTrue();
    }

    @Test
    @DisplayName("Mix of default-schema and non-default-schema entities all get their tables created")
    void testAddETypes_mixedSchemas_allTablesCreated() throws Exception {
        EPackage pkg = newPackage("mixed");
        EClass employeeClass = newEClassWithLongId(pkg, "Employee");
        EClass invoiceClass = newEClassWithLongId(pkg, "Invoice");

        EntityMappings mappings = new EntityMapper().createMappings(List.of(employeeClass, invoiceClass));
        setSchema(mappings, "Invoice", "FINANCE");

        initEclipseLink(mappings);

        assertThat(tableExists(null, "EMPLOYEE")).isTrue();
        assertThat(schemaExists("FINANCE")).isTrue();
        assertThat(tableExists("FINANCE", "INVOICE")).isTrue();
    }

    @Test
    @DisplayName("Entity in non-default schema can be persisted and retrieved")
    void testAddETypes_entityInNonDefaultSchema_persistAndFind() {
        EPackage pkg = newPackage("persist");
        EClass orderClass = newEClassWithLongId(pkg, "PersistOrder");

        EntityMappings mappings = new EntityMapper().createMappings(List.of(orderClass));
        setSchema(mappings, "PersistOrder", "SALES");

        initEclipseLink(mappings);

        ClassDescriptor desc = serverSession.getDescriptorForAlias("PersistOrder");
        EObject order = (EObject) desc.getInstantiationPolicy().buildNewInstance();
        order.eSet(orderClass.getEStructuralFeature("id"), 1L);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(order);
            em.getTransaction().commit();
        }

        try (EntityManager em = emf.createEntityManager()) {
            Object found = em.find(desc.getJavaClass(), 1L);
            assertThat(found).isNotNull();
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private EntityMappings mappingWithSchema(String entityName, String tableName, String schema) {
        EPackage pkg = newPackage(entityName.toLowerCase());
        EClass eClass = newEClassWithLongId(pkg, entityName);
        EntityMappings mappings = new EntityMapper().createMappings(List.of(eClass));
        setSchemaAndTable(mappings, entityName, tableName, schema);
        return mappings;
    }

    private EPackage newPackage(String name) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName(name);
        pkg.setNsURI("http://test/" + name);
        pkg.setNsPrefix(name);
        return pkg;
    }

    private EClass newEClassWithLongId(EPackage pkg, String name) {
        EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName(name);
        pkg.getEClassifiers().add(eClass);

        EAttribute idAttr = EcoreFactory.eINSTANCE.createEAttribute();
        idAttr.setName("id");
        idAttr.setEType(EcorePackage.Literals.ELONG);
        idAttr.setID(true);
        eClass.getEStructuralFeatures().add(idAttr);

        return eClass;
    }

    private void setSchemaAndTable(EntityMappings mappings, String entityName, String tableName, String schema) {
        for (Entity entity : mappings.getEntity()) {
            if (entityName.equals(entity.getName())) {
                Table table = entity.getTable();
                if (table == null) {
                    table = EORMFactory.eINSTANCE.createTable();
                    entity.setTable(table);
                }
                table.setName(tableName);
                table.setSchema(schema);
                return;
            }
        }
    }

    private void setSchema(EntityMappings mappings, String entityName, String schema) {
        setSchemaAndTable(mappings, entityName, entityName.toUpperCase(), schema);
    }

    private void initEclipseLink(EntityMappings mappings) {
        DynamicClassLoader dcl = new DynamicClassLoader(getClass().getClassLoader());

        Map<String, Object> props = new HashMap<>();
        props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
        props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
        props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
        props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:mem:schema_test_" + UUID.randomUUID() + ";DATABASE_TO_UPPER=TRUE");
        props.put(PersistenceUnitProperties.JDBC_USER, "sa");
        props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
        props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
        props.put(PersistenceUnitProperties.WEAVING, "false");
        props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
        props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");

        PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
        pu.setName("schema_test");
        pu.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
        URL puRoot = getClass().getProtectionDomain().getCodeSource().getLocation();
        EDynamicPersistenceUnitInfo pui = new EDynamicPersistenceUnitInfo(pu, puRoot, props);

        PersistenceProvider provider = new PersistenceProvider();
        emf = provider.createContainerEntityManagerFactory(pui, props);
        serverSession = JpaHelper.getServerSession(emf);

        EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl, serverSession, "schema_test");
        List<EDynamicType> types = generator.createFromMapping(mappings);

        EDynamicHelper helper = new EDynamicHelper(emf, dcl);
        helper.addETypes(true, true, types);
    }

    // ── DB metadata helpers ─────────────────────────────────────────────────

    private boolean schemaExists(String schemaName) {
        try (EntityManager em = emf.createEntityManager()) {
            Number count = (Number) em.createNativeQuery(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?1")
                    .setParameter(1, schemaName)
                    .getSingleResult();
            return count.intValue() > 0;
        }
    }

    private boolean tableExists(String schemaName, String tableName) {
        try (EntityManager em = emf.createEntityManager()) {
            if (schemaName != null) {
                Number count = (Number) em.createNativeQuery(
                        "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2")
                        .setParameter(1, schemaName)
                        .setParameter(2, tableName)
                        .getSingleResult();
                return count.intValue() > 0;
            } else {
                Number count = (Number) em.createNativeQuery(
                        "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?1"
                        + " AND TABLE_SCHEMA NOT IN ('INFORMATION_SCHEMA', 'SYS')")
                        .setParameter(1, tableName)
                        .getSingleResult();
                return count.intValue() > 0;
            }
        }
    }
}
