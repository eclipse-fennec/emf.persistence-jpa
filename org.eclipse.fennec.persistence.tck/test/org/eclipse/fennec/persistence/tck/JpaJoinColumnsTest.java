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
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
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
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.JoinTable;
import org.eclipse.fennec.persistence.eorm.ManyToMany;
import org.eclipse.fennec.persistence.eorm.ManyToOne;
import org.eclipse.fennec.persistence.eorm.OneToMany;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * Join columns between the eorm mapping and the store (issues #297, #379), checked with JDBC.
 * <p>
 * Three things are pinned. A generated mapping keeps the columns it has always had — the
 * mapping now names the relation columns the runtime used anyway, so honouring it changes
 * nothing. A reference to a class with a composite id stores the whole key. And a mapping
 * written for an existing schema is read and written through the columns it names, not
 * through derived ones.
 * <p>
 * The writes save a list target before the object that lists it. A unidirectional list
 * pointing at an object not yet in the store fails the save ("not a known Entity type"),
 * with or without named join columns — a write-path defect of its own, not part of this.
 *
 * @since 16.10.2026
 */
class JpaJoinColumnsTest {

	static {
		// H2 caches the JVM zone statically at first use (issue #79)
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String ID_FEATURES = "http://eclipse.org/fennec/persistence/1.0";

	private EntityManagerFactory emf;
	private String puName;
	private Map<String, Object> props;
	private String url;
	private EPackage ePackage;

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
		}
	}

	// ------------------------------------------------------------------ generated mappings

	/**
	 * The schema a generated mapping produces, table by table. These are the columns the
	 * runtime produced before the mapping named them, with one deliberate change: a reference
	 * to the composite-id {@code Lot} carries both key columns (#379), not only the first.
	 */
	@Test
	void aGeneratedMappingKeepsItsColumns() throws Exception {
		EPackage p = generatedModel();
		start("joins_gen", new EntityMapper().createMappings(new ArrayList<>(p.getEClassifiers())),
				"create-or-extend-tables");

		Map<String, TreeSet<String>> schema = schema();

		assertThat(schema).containsEntry("CUSTOMER", columns("ADDRESS_ID", "ID", "NAME"))
				.containsEntry("PURCHASEORDER", columns("CUSTOMER_ID", "ID", "TOTAL"))
				.containsEntry("CUSTOMER__TAG", columns("ID", "TAG_ID"))
				.containsEntry("TAG__TAG", columns("ID", "TAG_ID"))
				.containsEntry("STUDENT__COURSE", columns("CID", "SID"))
				.containsEntry("NOTE", columns("BODY", "CUSTOMER_ID", "ID", "NOTES_ORDER"))
				.containsEntry("BATCH", columns("ID", "LABEL", "LOT_LOTNO", "LOT_PLANT"))
				.containsEntry("BATCH__LOT", columns("ID", "LOTNO", "PLANT"));
	}

	/**
	 * The mapping states the relation columns the runtime uses: the key columns, the target's
	 * prefixed on a clash. A self-reference used to name the same column on both sides.
	 */
	@Test
	void aGeneratedJoinTableNamesTheColumnsTheRuntimeUses() {
		EntityMappings mappings = new EntityMapper()
				.createMappings(new ArrayList<>(generatedModel().getEClassifiers()));

		JoinTable self = oneToMany(mappings, "Tag", "related").getJoinTable();
		assertThat(self.getJoinColumn()).extracting(JoinColumn::getName).containsExactly("id");
		assertThat(self.getInverseJoinColumn()).extracting(JoinColumn::getName).containsExactly("Tag_id");

		JoinTable courses = manyToMany(mappings, "Student", "courses").getJoinTable();
		assertThat(courses.getJoinColumn()).extracting(JoinColumn::getName).containsExactly("sid");
		assertThat(courses.getInverseJoinColumn()).extracting(JoinColumn::getName).containsExactly("cid");

		assertThat(oneToOne(mappings, "Batch", "lot").getJoinColumn())
				.extracting(JoinColumn::getName, JoinColumn::getReferencedColumnName)
				.containsExactly(tuple("LOT_PLANT", "LOT.plant"), tuple("LOT_LOTNO", "LOT.lotNo"));
	}

	/**
	 * Two lots share the plant; only the full key tells them apart. With the first key
	 * column alone, the batch would come back pointing at whichever lot the plant finds.
	 */
	@Test
	@SuppressWarnings("unchecked")
	void aReferenceToACompositeIdStoresTheWholeKey() throws Exception {
		EPackage p = generatedModel();
		start("joins_composite", new EntityMapper().createMappings(new ArrayList<>(p.getEClassifiers())),
				"create-or-extend-tables");
		EClass lot = eClass("Lot");
		EClass batch = eClass("Batch");
		EObject seven = create(lot, "plant", "P1", "lotNo", "7", "grade", "A");
		EObject eight = create(lot, "plant", "P1", "lotNo", "8", "grade", "B");
		EObject b1 = create(batch, "id", "b1", "label", "first");
		b1.eSet(batch.getEStructuralFeature("lot"), eight);
		((List<EObject>) b1.eGet(batch.getEStructuralFeature("lots"))).addAll(List.of(seven, eight));
		ResourceSet writeSet = resourceSet();
		save(writeSet, "Lot", seven, eight);
		save(writeSet, "Batch", b1);

		assertThat(sql("SELECT LOT_PLANT, LOT_LOTNO FROM BATCH WHERE id = 'b1'"))
				.containsExactly(List.of("P1", "8"));

		EObject loaded = load(resourceSet(), "Batch").getContents().get(0);
		EObject referenced = (EObject) loaded.eGet(batch.getEStructuralFeature("lot"));
		assertThat(referenced.eGet(lot.getEStructuralFeature("lotNo"))).isEqualTo("8");
		assertThat((Collection<EObject>) loaded.eGet(batch.getEStructuralFeature("lots")))
				.extracting(l -> l.eGet(lot.getEStructuralFeature("lotNo"))).containsExactlyInAnyOrder("7", "8");
	}

	// ------------------------------------------------------------------ a mapping for an existing schema

	/**
	 * Data written by SQL into the columns the mapping names is read and navigated — the
	 * case of a schema that was there first.
	 */
	@Test
	@SuppressWarnings("unchecked")
	void rowsOfAnExistingSchemaAreReadThroughTheNamedColumns() throws Exception {
		startOnExistingSchema("joins_read");
		execute("INSERT INTO CLIENT (code, name) VALUES ('c1', 'Ada'), ('c2', 'Bob'), ('c3', 'Cy')",
				"INSERT INTO FRIENDS (A_CODE, B_CODE) VALUES ('c1', 'c2'), ('c1', 'c3')",
				"INSERT INTO CONTRACT (code, title, HOLDER_REF) VALUES ('k1', 'lease', 'c2')",
				"INSERT INTO INVOICE (code, amount, PAYER_CODE) VALUES ('i1', '10', 'c3'), ('i2', '20', 'c3')",
				"INSERT INTO SITE (plant, siteNo, label) VALUES ('P1', 'S1', 'north'), ('P1', 'S2', 'south')",
				"INSERT INTO VISIT (code, note, AT_PLANT, AT_SITE) VALUES ('v1', 'check', 'P1', 'S2')",
				"INSERT INTO STUDENT (code, name) VALUES ('s1', 'Eve')",
				"INSERT INTO COURSE (code, title) VALUES ('m1', 'Math'), ('m2', 'Art')",
				"INSERT INTO ENROLMENT (STUDENT_REF, COURSE_REF) VALUES ('s1', 'm1'), ('s1', 'm2')");
		ResourceSet readSet = resourceSet();

		EObject ada = byCode(load(readSet, "Client"), "c1");
		assertThat((Collection<EObject>) ada.eGet(eClass("Client").getEStructuralFeature("friends")))
				.extracting(this::code).containsExactlyInAnyOrder("c2", "c3");
		EObject lease = byCode(load(readSet, "Contract"), "k1");
		assertThat(code((EObject) lease.eGet(eClass("Contract").getEStructuralFeature("holder")))).isEqualTo("c2");
		EObject i1 = byCode(load(readSet, "Invoice"), "i1");
		assertThat(code((EObject) i1.eGet(eClass("Invoice").getEStructuralFeature("payer")))).isEqualTo("c3");
		EObject cy = byCode(load(readSet, "Client"), "c3");
		assertThat((Collection<EObject>) cy.eGet(eClass("Client").getEStructuralFeature("invoices")))
				.extracting(this::code).containsExactlyInAnyOrder("i1", "i2");
		EObject visit = byCode(load(readSet, "Visit"), "v1");
		EObject site = (EObject) visit.eGet(eClass("Visit").getEStructuralFeature("site"));
		assertThat(site.eGet(eClass("Site").getEStructuralFeature("label"))).isEqualTo("south");
		EObject eve = byCode(load(readSet, "Student"), "s1");
		assertThat((Collection<EObject>) eve.eGet(eClass("Student").getEStructuralFeature("courses")))
				.extracting(this::code).containsExactlyInAnyOrder("m1", "m2");
		EObject art = byCode(load(readSet, "Course"), "m2");
		assertThat((Collection<EObject>) art.eGet(eClass("Course").getEStructuralFeature("students")))
				.extracting(this::code).containsExactly("s1");
	}

	/** What the unit writes lands in the columns the mapping names. */
	@Test
	@SuppressWarnings("unchecked")
	void writesLandInTheNamedColumns() throws Exception {
		startOnExistingSchema("joins_write");
		EClass client = eClass("Client");
		EObject ada = create(client, "code", "c1", "name", "Ada");
		EObject bob = create(client, "code", "c2", "name", "Bob");
		((List<EObject>) ada.eGet(client.getEStructuralFeature("friends"))).add(bob);
		EObject lease = create(eClass("Contract"), "code", "k1", "title", "lease");
		lease.eSet(eClass("Contract").getEStructuralFeature("holder"), bob);
		EObject invoice = create(eClass("Invoice"), "code", "i1", "amount", "10");
		invoice.eSet(eClass("Invoice").getEStructuralFeature("payer"), ada);
		EObject north = create(eClass("Site"), "plant", "P1", "siteNo", "S1", "label", "north");
		EObject visit = create(eClass("Visit"), "code", "v1", "note", "check");
		visit.eSet(eClass("Visit").getEStructuralFeature("site"), north);
		EObject eve = create(eClass("Student"), "code", "s1", "name", "Eve");
		EObject math = create(eClass("Course"), "code", "m1", "title", "Math");
		((List<EObject>) eve.eGet(eClass("Student").getEStructuralFeature("courses"))).add(math);
		// every object in its resource first: a save refuses a reference into no resource. A
		// list target goes before its referrer — see the note on the class
		ResourceSet writeSet = resourceSet();
		List<Resource> resources = List.of(attach(writeSet, "Client", bob, ada), attach(writeSet, "Site", north),
				attach(writeSet, "Course", math), attach(writeSet, "Contract", lease),
				attach(writeSet, "Invoice", invoice), attach(writeSet, "Visit", visit),
				attach(writeSet, "Student", eve));
		for (Resource resource : resources) {
			resource.save(null);
		}

		assertThat(sql("SELECT A_CODE, B_CODE FROM FRIENDS")).containsExactly(List.of("c1", "c2"));
		assertThat(sql("SELECT HOLDER_REF FROM CONTRACT WHERE code = 'k1'")).containsExactly(List.of("c2"));
		assertThat(sql("SELECT PAYER_CODE FROM INVOICE WHERE code = 'i1'")).containsExactly(List.of("c1"));
		assertThat(sql("SELECT AT_PLANT, AT_SITE FROM VISIT WHERE code = 'v1'")).containsExactly(List.of("P1", "S1"));
		assertThat(sql("SELECT STUDENT_REF, COURSE_REF FROM ENROLMENT")).containsExactly(List.of("s1", "m1"));
	}

	// ------------------------------------------------------------------ models

	/** Every relationship shape the generator produces, plus a composite-id target (#379). */
	private EPackage generatedModel() {
		EPackage p = pkg("urn:joins:generated/" + UUID.randomUUID());
		EClass address = type(p, "Address", "id", "street");
		EClass tag = type(p, "Tag", "id", "label");
		EClass customer = type(p, "Customer", "id", "name");
		EClass order = type(p, "PurchaseOrder", "id", "total");
		EClass lot = EcoreFactory.eINSTANCE.createEClass();
		lot.setName("Lot");
		attr(lot, "plant", false);
		attr(lot, "lotNo", false);
		attr(lot, "grade", false);
		EcoreUtil.setAnnotation(lot, ID_FEATURES, "idFeatures", "plant,lotNo");
		p.getEClassifiers().add(lot);
		EClass batch = type(p, "Batch", "id", "label");
		EClass note = type(p, "Note", "id", "body");
		EClass student = type(p, "Student", "sid", "name");
		EClass course = type(p, "Course", "cid", "title");
		ref(customer, "address", address, 1);
		ref(customer, "tags", tag, -1);
		ref(tag, "related", tag, -1);
		opposites(ref(customer, "orders", order, -1), ref(order, "customer", customer, 1));
		ref(customer, "notes", note, -1).setContainment(true);
		ref(batch, "lot", lot, 1);
		ref(batch, "lots", lot, -1);
		opposites(ref(student, "courses", course, -1), ref(course, "students", student, -1));
		ePackage = p;
		return p;
	}

	/**
	 * A schema created by SQL, with relation columns of its own naming, and the mapping
	 * written for it: the generated one with every join column renamed, as a user tunes a
	 * pre-serialized {@code .eorm}. The composite join columns are listed in reverse key
	 * order — {@code referencedColumnName} decides the pairing, not the position.
	 */
	private void startOnExistingSchema(String name) throws Exception {
		EPackage p = pkg("urn:joins:existing/" + UUID.randomUUID());
		EClass client = type(p, "Client", "code", "name");
		EClass contract = type(p, "Contract", "code", "title");
		EClass invoice = type(p, "Invoice", "code", "amount");
		EClass site = EcoreFactory.eINSTANCE.createEClass();
		site.setName("Site");
		attr(site, "plant", false);
		attr(site, "siteNo", false);
		attr(site, "label", false);
		EcoreUtil.setAnnotation(site, ID_FEATURES, "idFeatures", "plant,siteNo");
		p.getEClassifiers().add(site);
		EClass visit = type(p, "Visit", "code", "note");
		EClass student = type(p, "Student", "code", "name");
		EClass course = type(p, "Course", "code", "title");
		ref(client, "friends", client, -1);
		ref(contract, "holder", client, 1);
		opposites(ref(invoice, "payer", client, 1), ref(client, "invoices", invoice, -1));
		ref(visit, "site", site, 1);
		opposites(ref(student, "courses", course, -1), ref(course, "students", student, -1));
		ePackage = p;

		EntityMappings mappings = new EntityMapper().createMappings(new ArrayList<>(p.getEClassifiers()));
		JoinTable friends = oneToMany(mappings, "Client", "friends").getJoinTable();
		friends.setName("FRIENDS");
		rename(friends.getJoinColumn(), "A_CODE");
		rename(friends.getInverseJoinColumn(), "B_CODE");
		rename(oneToOne(mappings, "Contract", "holder").getJoinColumn(), "HOLDER_REF");
		rename(manyToOne(mappings, "Invoice", "payer").getJoinColumn(), "PAYER_CODE");
		List<JoinColumn> atSite = oneToOne(mappings, "Visit", "site").getJoinColumn();
		atSite.clear();
		atSite.add(joinColumn("AT_SITE", "SITE.siteNo"));
		atSite.add(joinColumn("AT_PLANT", "SITE.plant"));
		JoinTable enrolment = manyToMany(mappings, "Student", "courses").getJoinTable();
		enrolment.setName("ENROLMENT");
		rename(enrolment.getJoinColumn(), "STUDENT_REF");
		rename(enrolment.getInverseJoinColumn(), "COURSE_REF");

		prepare(name);
		execute("CREATE TABLE CLIENT (code VARCHAR(40) PRIMARY KEY, name VARCHAR(255))",
				"CREATE TABLE FRIENDS (A_CODE VARCHAR(40) NOT NULL, B_CODE VARCHAR(40) NOT NULL)",
				"CREATE TABLE CONTRACT (code VARCHAR(40) PRIMARY KEY, title VARCHAR(255), HOLDER_REF VARCHAR(40))",
				"CREATE TABLE INVOICE (code VARCHAR(40) PRIMARY KEY, amount VARCHAR(255), PAYER_CODE VARCHAR(40))",
				"CREATE TABLE SITE (plant VARCHAR(40) NOT NULL, siteNo VARCHAR(40) NOT NULL, label VARCHAR(255),"
						+ " PRIMARY KEY (plant, siteNo))",
				"CREATE TABLE VISIT (code VARCHAR(40) PRIMARY KEY, note VARCHAR(255), AT_PLANT VARCHAR(40),"
						+ " AT_SITE VARCHAR(40))",
				"CREATE TABLE STUDENT (code VARCHAR(40) PRIMARY KEY, name VARCHAR(255))",
				"CREATE TABLE COURSE (code VARCHAR(40) PRIMARY KEY, title VARCHAR(255))",
				"CREATE TABLE ENROLMENT (STUDENT_REF VARCHAR(40) NOT NULL, COURSE_REF VARCHAR(40) NOT NULL)");
		emf = JpaTckSupport.bootstrap(puName, mappings, props, "none");
		// deploys the unit — objects created before it are no entities yet
		emf.createEntityManager().close();
	}

	// ------------------------------------------------------------------ unit and store

	private void prepare(String name) {
		puName = name;
		props = new HashMap<>(JpaTestSupport.jdbcProperties(name));
		url = (String) props.get(PersistenceUnitProperties.JDBC_URL);
		if (!JpaTestSupport.isPostgres() && !JpaTestSupport.isMariaDb()) {
			// H2 in memory: keep the database while no connection is open
			url = url + ";DB_CLOSE_DELAY=-1";
			props.put(PersistenceUnitProperties.JDBC_URL, url);
		}
	}

	private void start(String name, EntityMappings mappings, String ddlGeneration) {
		prepare(name);
		emf = JpaTckSupport.bootstrap(puName, mappings, props, ddlGeneration);
		// DDL runs when the first entity manager is created
		emf.createEntityManager().close();
	}

	private Connection connection() throws SQLException {
		return DriverManager.getConnection(url, (String) props.get(PersistenceUnitProperties.JDBC_USER),
				(String) props.get(PersistenceUnitProperties.JDBC_PASSWORD));
	}

	private void execute(String... statements) throws SQLException {
		try (Connection con = connection(); Statement stmt = con.createStatement()) {
			for (String statement : statements) {
				stmt.execute(statement);
			}
		}
	}

	private List<List<String>> sql(String query) throws SQLException {
		List<List<String>> rows = new ArrayList<>();
		try (Connection con = connection(); Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
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

	/** Table → columns of the unit's own schema, upper-cased: each flavor folds names its own way. */
	private Map<String, TreeSet<String>> schema() throws SQLException {
		Map<String, TreeSet<String>> schema = new TreeMap<>();
		try (Connection con = connection()) {
			DatabaseMetaData md = con.getMetaData();
			try (ResultSet rs = md.getColumns(con.getCatalog(), con.getSchema(), "%", "%")) {
				while (rs.next()) {
					schema.computeIfAbsent(rs.getString("TABLE_NAME").toUpperCase(), k -> new TreeSet<>())
							.add(rs.getString("COLUMN_NAME").toUpperCase());
				}
			}
		}
		return schema;
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("jpa://" + puName + "/" + typeName);
	}

	private void save(ResourceSet resourceSet, String typeName, EObject... objects) throws Exception {
		Resource resource = resourceSet.createResource(uriFor(typeName));
		resource.getContents().addAll(List.of(objects));
		resource.save(null);
	}

	private Resource attach(ResourceSet resourceSet, String typeName, EObject... objects) {
		Resource resource = resourceSet.createResource(uriFor(typeName));
		resource.getContents().addAll(List.of(objects));
		return resource;
	}

	private Resource load(ResourceSet resourceSet, String typeName) throws Exception {
		Resource resource = resourceSet.createResource(uriFor(typeName));
		resource.load(null);
		return resource;
	}

	private EObject byCode(Resource resource, String code) {
		return resource.getContents().stream().filter(o -> code.equals(code(o))).findFirst().orElseThrow();
	}

	private String code(EObject object) {
		return (String) object.eGet(object.eClass().getEStructuralFeature("code"));
	}

	// ------------------------------------------------------------------ model and mapping helpers

	private EClass eClass(String name) {
		return (EClass) ePackage.getEClassifier(name);
	}

	private static TreeSet<String> columns(String... names) {
		return new TreeSet<>(List.of(names));
	}

	private static EObject create(EClass eClass, String... featureValues) {
		EObject object = EcoreUtil.create(eClass);
		for (int i = 0; i < featureValues.length; i += 2) {
			object.eSet(eClass.getEStructuralFeature(featureValues[i]), featureValues[i + 1]);
		}
		return object;
	}

	private static EPackage pkg(String nsUri) {
		EPackage p = EcoreFactory.eINSTANCE.createEPackage();
		p.setName("joins");
		p.setNsURI(nsUri);
		p.setNsPrefix("joins");
		return p;
	}

	private static EClass type(EPackage p, String name, String id, String attribute) {
		EClass c = EcoreFactory.eINSTANCE.createEClass();
		c.setName(name);
		attr(c, id, true);
		attr(c, attribute, false);
		p.getEClassifiers().add(c);
		return c;
	}

	private static EAttribute attr(EClass c, String name, boolean id) {
		EAttribute a = EcoreFactory.eINSTANCE.createEAttribute();
		a.setName(name);
		a.setEType(EcorePackage.Literals.ESTRING);
		a.setID(id);
		c.getEStructuralFeatures().add(a);
		return a;
	}

	private static EReference ref(EClass c, String name, EClass type, int upper) {
		EReference r = EcoreFactory.eINSTANCE.createEReference();
		r.setName(name);
		r.setEType(type);
		r.setUpperBound(upper);
		c.getEStructuralFeatures().add(r);
		return r;
	}

	private static void opposites(EReference a, EReference b) {
		a.setEOpposite(b);
		b.setEOpposite(a);
	}

	private static Entity entity(EntityMappings mappings, String name) {
		return mappings.getEntity().stream().filter(e -> name.equals(e.getName())).findFirst().orElseThrow();
	}

	private static OneToOne oneToOne(EntityMappings mappings, String entity, String name) {
		return entity(mappings, entity).getAttributes().getOneToOne().stream()
				.filter(m -> name.equals(m.getName())).findFirst().orElseThrow();
	}

	private static ManyToOne manyToOne(EntityMappings mappings, String entity, String name) {
		return entity(mappings, entity).getAttributes().getManyToOne().stream()
				.filter(m -> name.equals(m.getName())).findFirst().orElseThrow();
	}

	private static OneToMany oneToMany(EntityMappings mappings, String entity, String name) {
		return entity(mappings, entity).getAttributes().getOneToMany().stream()
				.filter(m -> name.equals(m.getName())).findFirst().orElseThrow();
	}

	private static ManyToMany manyToMany(EntityMappings mappings, String entity, String name) {
		return entity(mappings, entity).getAttributes().getManyToMany().stream()
				.filter(m -> name.equals(m.getName())).findFirst().orElseThrow();
	}

	private static void rename(List<JoinColumn> joinColumns, String name) {
		assertThat(joinColumns).as("a single-key join column to rename").hasSize(1);
		joinColumns.get(0).setName(name);
	}

	private static JoinColumn joinColumn(String name, String referencedColumnName) {
		JoinColumn jc = EORMFactory.eINSTANCE.createJoinColumn();
		jc.setName(name);
		jc.setReferencedColumnName(referencedColumnName);
		return jc;
	}
}
