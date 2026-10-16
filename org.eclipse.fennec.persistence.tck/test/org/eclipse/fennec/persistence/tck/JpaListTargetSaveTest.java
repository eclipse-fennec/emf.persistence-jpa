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
package org.eclipse.fennec.persistence.tck;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * A list of new objects of the same type, saved in one resource (issue #381), checked with
 * JDBC. The write-path TCK covers targets in another resource; a self-referencing list adds
 * the orders only one resource has: a target behind its referrer, a cycle between two new
 * objects, a target listed twice. Each must give one row per object and one link per entry.
 *
 * @since 16.10.2026
 */
class JpaListTargetSaveTest {

	static {
		// H2 caches the JVM zone statically at first use (issue #79)
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private EPackage ePackage;
	private EClass tagClass;
	private EReference related;
	private EntityManagerFactory emf;
	private String puName;
	private Map<String, Object> props;
	private String url;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		tagClass = ecore.createEClass();
		tagClass.setName("Tag");
		attribute("id", true);
		attribute("label", false);
		related = ecore.createEReference();
		related.setName("related");
		related.setEType(tagClass);
		related.setUpperBound(-1);
		tagClass.getEStructuralFeatures().add(related);
		ePackage = ecore.createEPackage();
		ePackage.setName("listsave");
		ePackage.setNsURI("urn:listsave:" + UUID.randomUUID());
		ePackage.setNsPrefix("listsave");
		ePackage.getEClassifiers().add(tagClass);

		puName = "listsave";
		props = new HashMap<>(JpaTestSupport.jdbcProperties(puName));
		url = (String) props.get(PersistenceUnitProperties.JDBC_URL);
		if (!JpaTestSupport.isPostgres() && !JpaTestSupport.isMariaDb()) {
			url = url + ";DB_CLOSE_DELAY=-1";
			props.put(PersistenceUnitProperties.JDBC_URL, url);
		}
		emf = JpaTckSupport.bootstrap(puName, new EntityMapper().createMappings(List.of(tagClass)), props,
				"create-or-extend-tables");
		emf.createEntityManager().close();
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
		}
	}

	@Test
	void aTargetBehindItsReferrerInTheSameResource() throws Exception {
		EObject t1 = tag("t1");
		EObject t2 = tag("t2");
		related(t1).add(t2);

		save(t1, t2);

		assertThat(rows()).containsExactlyInAnyOrder(List.of("t1", "tag t1"), List.of("t2", "tag t2"));
		assertThat(links()).containsExactly(List.of("t1", "t2"));
	}

	@Test
	void aCycleBetweenTwoNewObjects() throws Exception {
		EObject t1 = tag("t1");
		EObject t2 = tag("t2");
		related(t1).add(t2);
		related(t2).add(t1);

		save(t1, t2);

		assertThat(rows()).containsExactlyInAnyOrder(List.of("t1", "tag t1"), List.of("t2", "tag t2"));
		assertThat(links()).containsExactlyInAnyOrder(List.of("t1", "t2"), List.of("t2", "t1"));
	}

	@Test
	void aTargetListedTwiceIsOneRow() throws Exception {
		EObject t1 = tag("t1");
		EObject t2 = tag("t2");
		EObject t3 = tag("t3");
		related(t1).add(t3);
		related(t2).add(t3);

		save(t1, t2, t3);

		assertThat(rows()).containsExactlyInAnyOrder(List.of("t1", "tag t1"), List.of("t2", "tag t2"),
				List.of("t3", "tag t3"));
		assertThat(links()).containsExactlyInAnyOrder(List.of("t1", "t3"), List.of("t2", "t3"));
	}

	/** And it reads back as it was written. */
	@Test
	void aCycleReadsBack() throws Exception {
		EObject t1 = tag("t1");
		EObject t2 = tag("t2");
		related(t1).add(t2);
		related(t2).add(t1);
		save(t1, t2);

		Resource loaded = resourceSet().getResource(URI.createURI("jpa://" + puName + "/Tag"), true);
		EObject read1 = loaded.getContents().stream().filter(t -> "t1".equals(EcoreUtil.getID(t))).findFirst()
				.orElseThrow();
		EObject read2 = related(read1).get(0);
		assertThat(EcoreUtil.getID(read2)).isEqualTo("t2");
		assertThat(related(read2)).containsExactly(read1);
	}

	// ── Helpers ─────────────────────────────────────────────────────────────

	private void attribute(String name, boolean id) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		attribute.setID(id);
		tagClass.getEStructuralFeatures().add(attribute);
	}

	private EObject tag(String id) {
		EObject tag = EcoreUtil.create(tagClass);
		tag.eSet(tagClass.getEStructuralFeature("id"), id);
		tag.eSet(tagClass.getEStructuralFeature("label"), "tag " + id);
		return tag;
	}

	@SuppressWarnings("unchecked")
	private List<EObject> related(EObject tag) {
		return (List<EObject>) tag.eGet(related);
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private void save(EObject... tags) throws Exception {
		Resource resource = resourceSet().createResource(URI.createURI("jpa://" + puName + "/Tag"));
		resource.getContents().addAll(List.of(tags));
		resource.save(null);
	}

	private List<List<String>> rows() throws SQLException {
		return sql("SELECT id, label FROM TAG");
	}

	/** The join table of the generated mapping: the key column, the target's prefixed (#297). */
	private List<List<String>> links() throws SQLException {
		return sql("SELECT id, Tag_id FROM TAG__TAG");
	}

	private List<List<String>> sql(String query) throws SQLException {
		List<List<String>> rows = new ArrayList<>();
		try (Connection con = DriverManager.getConnection(url, (String) props.get(PersistenceUnitProperties.JDBC_USER),
				(String) props.get(PersistenceUnitProperties.JDBC_PASSWORD));
				Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			int columns = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				List<String> row = new ArrayList<>();
				for (int i = 1; i <= columns; i++) {
					row.add(rs.getString(i));
				}
				rows.add(row);
			}
		}
		return rows;
	}
}
