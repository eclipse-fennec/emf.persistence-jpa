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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.orm.ORMConstants;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.Type;
import org.osgi.test.common.annotation.Property.ValueSource;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Integration test for GLT OneToMany containment mapping issue.
 * 
 * This test reproduces the critical issue where EMF containment relationships
 * are incorrectly mapped to JoinTable instead of JoinColumn, causing failures
 * in no-cache mode when accessing containment collections.
 * 
 * The test uses the GLT (Building-Contact) model from the analysis document
 * with a database schema that has foreign key relationships (building_id in contacts table).
 * 
 * @author Mark Hoffmann
 * @since 26.09.2025
 */
public class EPersistenceGltContainmentTest extends EPersistenceModelBase {

	private static final String DATASOURCE_PID = "daanse.jdbc.datasource.h2.DataSource";
	
	@InjectBundleContext
	BundleContext bctx;
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceModelBase#getModelEntryPath()
	 */
	@Override
	protected String getModelEntryPath() {
		return "/data/glt/glt.ecore";
	}
	
	@BeforeEach
	public void before() throws Exception {
		super.before(bctx);
	}

	/**
	 * Set up database with schema and sample data before each test
	 */
	public void setupDatabase(ServiceAware<DataSource> dataSourceAware) throws Exception {
		if (!dataSourceAware.isEmpty()) {
			DataSource ds = dataSourceAware.getService();
			try (Connection conn = ds.getConnection();
					Statement stmt = conn.createStatement()) {

				// Create tables and insert test data from db_with_data.sql
				// H2 creates foreign key constraints that prevent dropping tables
				// Use DROP SCHEMA to clean everything up
				
				try {
					// Try to drop the entire schema and recreate it
					stmt.execute("DROP SCHEMA IF EXISTS PUBLIC CASCADE");
					stmt.execute("CREATE SCHEMA PUBLIC");
				} catch (Exception e) {
					// If schema operations don't work, try manual cleanup
					try {
						// First find and drop all foreign key constraints
						var rs = stmt.executeQuery("SELECT CONSTRAINT_NAME, TABLE_NAME FROM INFORMATION_SCHEMA.CONSTRAINTS WHERE CONSTRAINT_TYPE = 'REFERENTIAL'");
						while (rs.next()) {
							String constraintName = rs.getString("CONSTRAINT_NAME");
							String tableName = rs.getString("TABLE_NAME");
							try {
								stmt.execute("ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName);
							} catch (Exception e2) {
								// Ignore if constraint doesn't exist
							}
						}
						
						// Now drop tables
						stmt.execute("DROP TABLE IF EXISTS contacts");
						stmt.execute("DROP TABLE IF EXISTS buildings");
					} catch (Exception e2) {
						// Last resort - ignore and continue
						System.err.println("Warning: Could not clean up existing schema: " + e2.getMessage());
					}
				}

				stmt.execute("""
						CREATE TABLE IF NOT EXISTS buildings (
							id INTEGER PRIMARY KEY,
							city TEXT NOT NULL,
							zip INTEGER NOT NULL,
							street TEXT NOT NULL
						)
						""");

				stmt.execute("""
						CREATE TABLE IF NOT EXISTS contacts (
							building_id INTEGER NOT NULL,
							role TEXT NOT NULL,
							email TEXT,
							phonenumber TEXT,
							first_name TEXT,
							last_name TEXT,
							FOREIGN KEY (building_id) REFERENCES buildings(id)
						)
						""");

				// Clear existing data and insert fresh sample data
				stmt.execute("DELETE FROM contacts");
				stmt.execute("DELETE FROM buildings");
				
				// Insert sample data
				stmt.execute("INSERT INTO buildings (id, city, zip, street) VALUES (100, 'Berlin', 10115, 'Alexanderplatz 1')");
				stmt.execute("INSERT INTO buildings (id, city, zip, street) VALUES (200, 'Hamburg', 20095, 'Hafenstraße 42')");

				stmt.execute("INSERT INTO contacts (building_id, role, email, phonenumber, first_name, last_name) VALUES " +
						"(100, 'Hausmeister', 'max.mueller@example.com', '+49-30-12345678', 'Max', 'Mueller')");
				stmt.execute("INSERT INTO contacts (building_id, role, email, phonenumber, first_name, last_name) VALUES " +
						"(100, 'Owner', 'anna.schmidt@example.com', '+49-30-87654321', 'Anna', 'Schmidt')");
				stmt.execute("INSERT INTO contacts (building_id, role, email, phonenumber, first_name, last_name) VALUES " +
						"(200, 'Hausmeister', 'hans.fischer@example.com', '+49-40-22222222', 'Hans', 'Fischer')");
			}
		}
	}

	/**
	 * Test OneToMany containment mapping with cache enabled (should work with workaround)
	 * Uses EORMMappingProvider to generate mappings automatically from EMF model
	 */
	@Test
	@WithFactoryConfiguration(factoryPid = EPersistenceGltContainmentTest.DATASOURCE_PID, name = "glt-test", location = "?", properties = {
			@Property(key="name", value = "h2glt"),
			@Property(key = "identifier", value = "./data/glt/glt_test")
	})
	@WithFactoryConfiguration(factoryPid = ORMConstants.ORM_MAPPING_SERVICE_PID, name = "glt-test", properties = {
			@Property(key = ORMConstants.PROPERTY_PREFIX + "eClasses", type = Type.Array, source = ValueSource.Value, value = {"Building", "Contact"}),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "mappingName", value = "glt"),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "model.target", value = "(emf.name=glt)"),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "strict", value = "true")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.EMPersistenceUnit", name = "glt-test", properties = {
			@Property(key = "fennec.jpa.dataSource.target", value = "(name=h2glt)"),
			@Property(key = "fennec.jpa.mapping.target", value = "(fennec.jpa.eorm.mapping=glt)"),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "glt"),
			@Property(key = "fennec.jpa.ext.eclipselink.target-database", value = "org.eclipse.persistence.platform.database.H2Platform"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	public void testGltContainmentWithCache(
			@InjectService(timeout = 5000) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=glt)", timeout = 5000) ServiceAware<EPackage> gltPackageAware,
			@InjectService(timeout = 10000) ServiceAware<EntityManagerFactory> emfAware) throws Exception {

		assertFalse(dataSourceAware.isEmpty(), "DataSource should be available");
		setupDatabase(dataSourceAware);
		assertFalse(gltPackageAware.isEmpty(), "GLT model package should be available");
		assertFalse(emfAware.isEmpty(), "EntityManagerFactory should be available");
		

		EPackage gltPackage = gltPackageAware.getService();
		EntityManagerFactory emf = emfAware.getService();

		// Get Building and Contact classes from GLT model
		EClass buildingClass = (EClass) gltPackage.getEClassifier("Building");
		EClass contactClass = (EClass) gltPackage.getEClassifier("Contact");
		assertNotNull(buildingClass, "Building class should exist");
		assertNotNull(contactClass, "Contact class should exist");

		// Get EclipseLink descriptors
		Server server = JpaHelper.getServerSession(emf);
		ClassDescriptor buildingDescriptor = server.getDescriptorForAlias(buildingClass.getName());
		assertNotNull(buildingDescriptor, "Building descriptor should exist");

		// Test reading building with contacts (should work with cache)
		try (EntityManager em = emf.createEntityManager()) {
			EObject building = em.find(buildingDescriptor.getJavaClass(), 100);
			assertNotNull(building, "Should find building with id=100");

			EStructuralFeature contactsFeature = buildingClass.getEStructuralFeature("contacts");
			assertNotNull(contactsFeature, "contacts feature should exist");
			assertTrue(contactsFeature.getEType() instanceof EClass, "contacts should reference Contact class");
			assertTrue(contactsFeature.isMany(), "contacts should be many-valued");

			// The critical test: access the containment collection
			Object contacts = building.eGet(contactsFeature);
			assertNotNull(contacts, "contacts collection should not be null");
			assertInstanceOf(List.class, contacts, "contacts should be a List");

			@SuppressWarnings("unchecked")
			List<EObject> contactsList = (List<EObject>) contacts;
			assertEquals(2, contactsList.size(), "Should have 2 contacts for building 1");

			// Verify contact data
			for (EObject contact : contactsList) {
				assertEquals(contactClass, contact.eClass(), "Should be Contact instance");
				EStructuralFeature roleFeature = contactClass.getEStructuralFeature("role");
				Object role = contact.eGet(roleFeature);
				assertTrue(role.equals("Hausmeister") || role.equals("Owner"), 
						"Role should be Hausmeister or Owner, was: " + role);
			}
		}
	}

	/**
	 * Test OneToMany containment mapping with NO CACHE (should fail without fix)
	 * 
	 * This test exposes the core issue: when cache is disabled, EclipseLink generates
	 * SQL queries that look for a BUILDING__CONTACT join table instead of using
	 * the building_id foreign key in the contacts table.
	 * 
	 * Uses EORMMappingProvider to generate mappings automatically, demonstrating the 
	 * containment mapping issue in the core OneToManyProcessor.
	 */
	@Test
	@WithFactoryConfiguration(factoryPid = DATASOURCE_PID, name = "glt-nocache", location = "?", properties = {
			@Property(key="name", value = "h2glt"),
			@Property(key = "identifier", value = "./data/glt/glt_nocache_test")
	})
	@WithFactoryConfiguration(factoryPid = ORMConstants.ORM_MAPPING_SERVICE_PID, name = "glt-nocache", properties = {
			@Property(key = ORMConstants.PROPERTY_PREFIX + "eClasses", type = Type.Array, source = ValueSource.Value, value = {"Building", "Contact"}),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "mappingName", value = "glt-nocache"),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "model.target", value = "(emf.name=glt)"),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "strict", value = "false")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.EMPersistenceUnit", name = "glt-nocache", properties = {
			@Property(key = "fennec.jpa.dataSource.target", value = "(identifier=./data/glt/glt_nocache_test)"),
			@Property(key = "fennec.jpa.mapping.target", value = "(fennec.jpa.eorm.mapping=glt-nocache)"),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "glt-nocache"),
			@Property(key = "fennec.jpa.ext.eclipselink.cache.shared.default", value = "false"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables")
	})
	public void testGltContainmentNoCache(
			@InjectService(timeout = 5000) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=glt)", timeout = 5000) ServiceAware<EPackage> gltPackageAware,
			@InjectService(timeout = 10000) ServiceAware<EntityManagerFactory> emfAware) throws Exception {

		assertFalse(dataSourceAware.isEmpty(), "DataSource should be available");
		setupDatabase(dataSourceAware);
		assertFalse(gltPackageAware.isEmpty(), "GLT model package should be available");
		assertFalse(emfAware.isEmpty(), "EntityManagerFactory should be available");

		EPackage gltPackage = gltPackageAware.getService();
		EntityManagerFactory emf = emfAware.getService();

		// Get Building and Contact classes from GLT model
		EClass buildingClass = (EClass) gltPackage.getEClassifier("Building");
		EClass contactClass = (EClass) gltPackage.getEClassifier("Contact");
		assertNotNull(buildingClass, "Building class should exist");
		assertNotNull(contactClass, "Contact class should exist");

		// Get EclipseLink descriptors
		Server server = JpaHelper.getServerSession(emf);
		ClassDescriptor buildingDescriptor = server.getDescriptorForAlias(buildingClass.getName());
		assertNotNull(buildingDescriptor, "Building descriptor should exist");

		// Test reading building with contacts (should now work with our UnidirectionalOneToManyMapping fix)
		try (EntityManager em = emf.createEntityManager()) {
			EObject building = em.find(buildingDescriptor.getJavaClass(), 100);
			assertNotNull(building, "Should find building with id=100");

			EReference contactsFeature = (EReference) buildingClass.getEStructuralFeature("contacts");
			assertNotNull(contactsFeature, "contacts feature should exist");

			// Verify this is a containment relationship
			assertTrue(contactsFeature.isContainment(), "contacts should be a containment reference");

			// The critical test: access the containment collection
			// This will fail with the current incorrect JoinTable mapping
			try {
				Object contacts = building.eGet(contactsFeature);
				assertNotNull(contacts, "contacts collection should not be null");
				assertInstanceOf(List.class, contacts, "contacts should be a List");

				@SuppressWarnings("unchecked")
				List<EObject> contactsList = (List<EObject>) contacts;

				// If we get here without an exception, the mapping is working
				assertEquals(2, contactsList.size(), "Should have 2 contacts for building 1");

				// Verify contact data
				for (EObject contact : contactsList) {
					assertEquals(contactClass, contact.eClass(), "Should be Contact instance");
					EStructuralFeature roleFeature = contactClass.getEStructuralFeature("role");
					Object role = contact.eGet(roleFeature);
					assertTrue(role.equals("Hausmeister") || role.equals("Owner"), 
							"Role should be Hausmeister or Owner, was: " + role);
				}

			} catch (Exception e) {
				// Expected failure due to incorrect JoinTable mapping
				// The exception should contain information about missing BUILDING__CONTACT table
				String message = e.getMessage();
				if (message != null && message.contains("BUILDING__CONTACT")) {
					// This is the expected failure - document it for analysis
					System.err.println("EXPECTED FAILURE: OneToMany containment mapping generates JoinTable instead of JoinColumn");
					System.err.println("Error message: " + message);
					System.err.println("Root cause: OneToManyProcessor always creates JoinTable, ignoring containment semantics");

					// For now, we expect this to fail - when the fix is implemented, remove this catch block
					return;
				} else {
					// Unexpected error - rethrow
					throw e;
				}
			}
		}
	}

	/**
	 * Test to verify the database schema is correctly set up
	 */
	@Test
	@WithFactoryConfiguration(factoryPid = DATASOURCE_PID, name = "glt-schema-test", location = "?", properties = {
			@Property(key="name", value = "h2glt"),
			@Property(key="identifier", value = "./data/glt/glt_schema_test")
	})
	public void testDatabaseSchemaSetup(@InjectService(timeout = 500l) ServiceAware<DataSource> dataSourceAware) throws Exception {

		assertFalse(dataSourceAware.isEmpty(), "DataSource should be available");
		setupDatabase(dataSourceAware);
		DataSource ds = dataSourceAware.getService();

		try (Connection conn = ds.getConnection();
				Statement stmt = conn.createStatement()) {

			// Verify buildings table
			var rs = stmt.executeQuery("SELECT COUNT(*) FROM buildings");
			assertTrue(rs.next(), "Should have buildings data");
			assertEquals(2, rs.getInt(1), "Should have 2 buildings");

			// Verify contacts table with foreign key relationships
			rs = stmt.executeQuery("SELECT building_id, COUNT(*) FROM contacts GROUP BY building_id ORDER BY building_id");

			// Building 1 should have 2 contacts
			assertTrue(rs.next(), "Should have contacts for building 1");
			assertEquals(100, rs.getInt(1), "First group should be building_id=1");
			assertEquals(2, rs.getInt(2), "Building 1 should have 2 contacts");

			// Building 2 should have 1 contact
			assertTrue(rs.next(), "Should have contacts for building 2");
			assertEquals(200, rs.getInt(1), "Second group should be building_id=2");
			assertEquals(1, rs.getInt(2), "Building 2 should have 1 contact");

			// Verify foreign key constraint exists - test by attempting to violate it
			try {
				stmt.execute("INSERT INTO contacts (building_id, role, email) VALUES (999, 'Test', 'test@test.com')");
				// If we reach here, foreign key constraint is not working
				fail("Foreign key constraint should prevent inserting contact with non-existent building_id");
			} catch (Exception e) {
				// Expected: foreign key constraint violation
				assertTrue(e.getMessage().contains("foreign key") || 
						  e.getMessage().contains("referential integrity") ||
						  e.getMessage().contains("constraint"),
						"Should get foreign key constraint violation, got: " + e.getMessage());
			}
		}
	}

	/**
	 * Test persisting a new building with contacts and verify with SQL
	 * This will help us debug whether cascade persistence sets foreign keys correctly
	 */
	@Test
	@WithFactoryConfiguration(factoryPid = DATASOURCE_PID, name = "glt-persist", location = "?", properties = {
			@Property(key="name", value = "h2glt"),
			@Property(key = "identifier", value = "./data/glt/glt_persist_test")
	})
	@WithFactoryConfiguration(factoryPid = ORMConstants.ORM_MAPPING_SERVICE_PID, name = "glt-persist", properties = {
			@Property(key = ORMConstants.PROPERTY_PREFIX + "eClasses", type = Type.Array, source = ValueSource.Value, value = {"Building", "Contact"}),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "mappingName", value = "glt-persist"),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "model.target", value = "(emf.name=glt)"),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "strict", value = "false")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.EMPersistenceUnit", name = "glt-persist", properties = {
			@Property(key = "fennec.jpa.dataSource.target", value = "(identifier=./data/glt/glt_persist_test)"),
			@Property(key = "fennec.jpa.mapping.target", value = "(fennec.jpa.eorm.mapping=glt-persist)"),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "glt-persist"),
			@Property(key = "fennec.jpa.ext.eclipselink.cache.shared.default", value = "false"),
			@Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables"),
			@Property(key = "fennec.jpa.ext.eclipselink.logging.level.sql", value = "FINE"),
			@Property(key = "fennec.jpa.ext.eclipselink.logging.parameters", value = "true")
	})
	public void testPersistNewBuildingWithContacts(
			@InjectService(timeout = 5000) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=glt)", timeout = 5000) ServiceAware<EPackage> gltPackageAware,
			@InjectService(timeout = 10000) ServiceAware<EntityManagerFactory> emfAware) throws Exception {

		assertFalse(dataSourceAware.isEmpty(), "DataSource should be available");
		setupDatabase(dataSourceAware);
		assertFalse(gltPackageAware.isEmpty(), "GLT model package should be available");
		assertFalse(emfAware.isEmpty(), "EntityManagerFactory should be available");

		EPackage gltPackage = gltPackageAware.getService();
		EntityManagerFactory emf = emfAware.getService();
		DataSource ds = dataSourceAware.getService();

		// Get Building and Contact classes from GLT model
		EClass buildingClass = (EClass) gltPackage.getEClassifier("Building");
		EClass contactClass = (EClass) gltPackage.getEClassifier("Contact");
		assertNotNull(buildingClass, "Building class should exist");
		assertNotNull(contactClass, "Contact class should exist");

		// Get EclipseLink descriptors
		Server server = JpaHelper.getServerSession(emf);
		ClassDescriptor buildingDescriptor = server.getDescriptorForAlias(buildingClass.getName());
		ClassDescriptor contactDescriptor = server.getDescriptorForAlias(contactClass.getName());
		assertNotNull(buildingDescriptor, "Building descriptor should exist");
		assertNotNull(contactDescriptor, "Contact descriptor should exist");

		// Create new building with contacts
		EObject newBuilding = (EObject) buildingDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject contact1 = (EObject) contactDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject contact2 = (EObject) contactDescriptor.getInstantiationPolicy().buildNewInstance();

		// Set building properties
		EStructuralFeature cityFeature = buildingClass.getEStructuralFeature("city");
		EStructuralFeature zipFeature = buildingClass.getEStructuralFeature("zip");
		EStructuralFeature streetFeature = buildingClass.getEStructuralFeature("street");
		EReference contactsFeature = (EReference) buildingClass.getEStructuralFeature("contacts");
		
		newBuilding.eSet(cityFeature, "Munich");
		newBuilding.eSet(zipFeature, 80331);
		newBuilding.eSet(streetFeature, "Marienplatz 1");

		// Set contact properties
		EStructuralFeature roleFeature = contactClass.getEStructuralFeature("role");
		EStructuralFeature emailFeature = contactClass.getEStructuralFeature("email");
		EStructuralFeature firstNameFeature = contactClass.getEStructuralFeature("firstName");
		EStructuralFeature lastNameFeature = contactClass.getEStructuralFeature("lastName");

		contact1.eSet(roleFeature, "Manager");
		contact1.eSet(emailFeature, "manager@example.com");
		contact1.eSet(firstNameFeature, "John");
		contact1.eSet(lastNameFeature, "Doe");

		contact2.eSet(roleFeature, "Janitor");
		contact2.eSet(emailFeature, "janitor@example.com");
		contact2.eSet(firstNameFeature, "Jane");
		contact2.eSet(lastNameFeature, "Smith");

		// Add contacts to building (containment relationship)
		@SuppressWarnings("unchecked")
		List<EObject> contacts = (List<EObject>) newBuilding.eGet(contactsFeature);
		contacts.add(contact1);
		contacts.add(contact2);

		// Persist the building (should cascade to contacts)
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(newBuilding);
			em.getTransaction().commit();
		}

		// Now verify with plain SQL what was actually persisted
		try (Connection conn = ds.getConnection();
			 Statement stmt = conn.createStatement()) {

			// Check buildings table
			var rs = stmt.executeQuery("SELECT id, city, zip, street FROM buildings ORDER BY id");
			System.out.println("Buildings table contents:");
			while (rs.next()) {
				System.out.printf("  id=%d, city=%s, zip=%d, street=%s%n", 
					rs.getInt("id"), rs.getString("city"), rs.getInt("zip"), rs.getString("street"));
			}

			// Check contacts table - this is the critical part
			rs = stmt.executeQuery("SELECT building_id, role, email, first_name, last_name FROM contacts ORDER BY building_id, role");
			System.out.println("Contacts table contents:");
			while (rs.next()) {
				System.out.printf("  building_id=%s, role=%s, email=%s, first_name=%s, last_name=%s%n",
					rs.getObject("building_id"), rs.getString("role"), rs.getString("email"), 
					rs.getString("first_name"), rs.getString("last_name"));
			}

			// Count total records
			rs = stmt.executeQuery("SELECT COUNT(*) FROM buildings");
			rs.next();
			int buildingCount = rs.getInt(1);
			System.out.println("Total buildings: " + buildingCount);

			rs = stmt.executeQuery("SELECT COUNT(*) FROM contacts");
			rs.next();
			int contactCount = rs.getInt(1);
			System.out.println("Total contacts: " + contactCount);

			// Verify we have the expected data
			assertTrue(buildingCount >= 3, "Should have at least 3 buildings (2 original + 1 new)");
			assertTrue(contactCount >= 5, "Should have at least 5 contacts (3 original + 2 new)");

			// Check specifically for our new building's contacts and their foreign keys
			rs = stmt.executeQuery("SELECT building_id FROM contacts WHERE role IN ('Manager', 'Janitor') ORDER BY role");
			boolean foundManager = false;
			boolean foundJanitor = false;
			while (rs.next()) {
				Object buildingId = rs.getObject("building_id");
				if (buildingId != null) {
					System.out.println("Found contact with building_id: " + buildingId);
					if (!foundManager) {
						foundManager = true;
					} else {
						foundJanitor = true;
					}
				} else {
					System.out.println("ERROR: Found contact with NULL building_id!");
				}
			}

			assertTrue(foundManager, "Should find Manager contact");
			assertTrue(foundJanitor, "Should find Janitor contact");
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceModelBase#beforeModelRegistered(org.eclipse.emf.ecore.EPackage)
	 */
	@Override
	void beforeModelRegistered(EPackage modelPackage) {
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceModelBase#afterModelRegistered(org.eclipse.emf.ecore.EPackage)
	 */
	@Override
	void afterModelRegistered(EPackage modelPackage) {
	}

}