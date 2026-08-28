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

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Spike for issue #238 slice 3 (per-parent {@code top}/{@code skip}) and, with the same
 * mechanism, for issue #214 on JPA.
 * <p>
 * {@code JpaQueryProcessor} refuses group representatives with <em>"JPQL has no window functions,
 * so the route — native SQL or a two-pass execution — is its own decision"</em>. That is a false
 * dichotomy: it holds for standard JPQL, not for EclipseLink's. This test measures the third
 * route, which needs neither native SQL nor a second pass:
 * <ul>
 * <li>{@code SQL('<fragment>', args)} splices raw SQL into the generated statement, each
 *     {@code ?} replaced by the <em>translated argument</em> — the same extension the backend
 *     already relies on for {@code CAST(… AS DATE)} (issue #240);</li>
 * <li>a <strong>subquery in the FROM clause</strong>, which EclipseLink's 2.4 grammar allows
 *     ({@code addChildBNF(RangeDeclarationBNF.ID, SubqueryBNF.ID)}). It is what makes the window
 *     usable at all: SQL forbids filtering a window function in the WHERE of the same level, and
 *     a derived table is the standard way around it.</li>
 * </ul>
 * The question the spike answers is not whether the SQL runs — h2, MariaDB and PostgreSQL all
 * have {@code ROW_NUMBER}, and EclipseLink emits it itself for SQL Server pagination. It is
 * whether <em>EclipseLink's JPQL layer</em> carries this shape: parses it, keeps the parameter
 * binding, and returns usable rows on every flavor.
 * <p>
 * Run against the other flavors with
 * {@code -Djpa.test.flavor=postgres -Djpa.container.cli=docker}.
 *
 * @author Mark Hoffmann
 */
class JpaWindowFunctionSpikeTest {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "windowspike";

	/** Three parents, four children each — enough that a per-parent cap differs from a global one. */
	private static final int PARENTS = 3;
	private static final int CHILDREN = 4;

	private EPackage ePackage;
	private EClass parentClass;
	private EClass childClass;
	private EReference parentChildren;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() throws Exception {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;

		childClass = ecore.createEClass();
		childClass.setName("Child");
		addId(childClass, "cid");
		addString(childClass, "label");

		parentClass = ecore.createEClass();
		parentClass.setName("Parent");
		addId(parentClass, "pid");
		addString(parentClass, "name");
		parentChildren = ecore.createEReference();
		parentChildren.setName("children");
		parentChildren.setEType(childClass);
		parentChildren.setUpperBound(-1);
		parentChildren.setContainment(true);
		parentClass.getEStructuralFeatures().add(parentChildren);

		ePackage = ecore.createEPackage();
		ePackage.setName("windowspike");
		ePackage.setNsURI("urn:windowspike:test/1.0");
		ePackage.setNsPrefix("ws");
		ePackage.getEClassifiers().add(parentClass);
		ePackage.getEClassifiers().add(childClass);

		emf = JpaTckSupport.bootstrap(PU_NAME, List.<EClassifier>of(parentClass, childClass));
		saveFixture();
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
			emf = null;
		}
	}

	/**
	 * Step one: does EclipseLink's JPQL accept {@code SQL(...)} rendering a window function in
	 * the select list? No filtering yet — this isolates the splice from the derived table, so a
	 * failure says which of the two is missing.
	 */
	@Test
	void sqlFunctionRendersAWindowFunction() {
		EntityManager em = emf.createEntityManager();
		try {
			List<?> rows = em.createQuery(
					"SELECT c.cid,"
							+ " SQL('ROW_NUMBER() OVER (PARTITION BY ? ORDER BY ?)', p.pid, c.label)"
							+ " FROM Parent p JOIN p.children c")
					.getResultList();

			assertThat(rows)
					.as("every child gets a row number")
					.hasSize(PARENTS * CHILDREN);
			assertThat(rows).allSatisfy(row -> {
				Object[] cells = (Object[]) row;
				assertThat(((Number) cells[1]).intValue()).isBetween(1, CHILDREN);
			});
		} finally {
			em.close();
		}
	}

	/**
	 * Step two: the shape slice 3 would actually use — the window in a derived table, filtered
	 * on the outside. This is where a per-parent {@code top} becomes expressible.
	 * <p>
	 * The derived table cannot be the <em>first</em> declaration of the FROM clause: EclipseLink's
	 * semantic validator refuses that outright ({@code AbstractFromClause_InvalidFirstIdentification
	 * VariableDeclaration}, and its own tests pin both the refused and the accepted form). An
	 * entity therefore anchors the clause and is correlated to the derived table on the parent
	 * key — a join, not a cartesian product.
	 */
	@Test
	void aDerivedTableMakesTheWindowFilterable() {
		EntityManager em = emf.createEntityManager();
		try {
			List<?> rows = em.createQuery(
					"SELECT sub.cid FROM Parent anchor, ("
							+ "SELECT p.pid AS pid, c.cid AS cid,"
							+ " SQL('ROW_NUMBER() OVER (PARTITION BY ? ORDER BY ?)', p.pid, c.label) AS rn"
							+ " FROM Parent p JOIN p.children c) sub"
							+ " WHERE anchor.pid = sub.pid AND sub.rn <= :top")
					.setParameter("top", 2)
					.getResultList();

			assertThat(rows)
					.as("two children per parent, not two overall — the point of PARTITION BY")
					.hasSize(PARENTS * 2);
		} finally {
			em.close();
		}
	}

	/**
	 * Step three: the ids that come back must be the right ones — the two lowest labels per
	 * parent. A window that partitions but orders wrong would pass the count assertion above.
	 */
	@Test
	void theWindowSelectsTheRightChildrenPerParent() {
		EntityManager em = emf.createEntityManager();
		try {
			List<?> rows = em.createQuery(
					"SELECT sub.cid FROM Parent anchor, ("
							+ "SELECT p.pid AS pid, c.cid AS cid,"
							+ " SQL('ROW_NUMBER() OVER (PARTITION BY ? ORDER BY ?)', p.pid, c.label) AS rn"
							+ " FROM Parent p JOIN p.children c) sub"
							+ " WHERE anchor.pid = sub.pid AND sub.rn <= :top")
					.setParameter("top", 2)
					.getResultList();

			List<String> ids = new ArrayList<>();
			rows.forEach(row -> ids.add(String.valueOf(row)));
			// labels are "p<parent>-c<index>", so the two lowest per parent are c0 and c1
			List<String> expected = new ArrayList<>();
			for (int p = 0; p < PARENTS; p++) {
				expected.add("p" + p + "-c0");
				expected.add("p" + p + "-c1");
			}
			assertThat(ids).containsExactlyInAnyOrderElementsOf(expected);
		} finally {
			em.close();
		}
	}

	/**
	 * Step four: the form slice 3 actually needs — <strong>entities</strong> out, not columns.
	 * <p>
	 * A derived table yields columns, so a window alone would hand back ids and the targets would
	 * still have to be read separately. Joining the reference a second time on the outside and
	 * equating it with the windowed id keeps the whole thing one query <em>and</em> returns
	 * managed entities, which is what registers them in the persistence context and makes the
	 * subsequent proxy resolution free.
	 */
	@Test
	void theWindowCanReturnEntitiesRatherThanColumns() {
		EntityManager em = emf.createEntityManager();
		try {
			List<?> rows = em.createQuery(
					"SELECT e FROM Parent anchor JOIN anchor.children e, ("
							+ "SELECT p.pid AS pid, c.cid AS cid,"
							+ " SQL('ROW_NUMBER() OVER (PARTITION BY ? ORDER BY ?)', p.pid, c.label) AS rn"
							+ " FROM Parent p JOIN p.children c) sub"
							+ " WHERE anchor.pid = sub.pid AND e.cid = sub.cid AND sub.rn <= :top")
					.setParameter("top", 2)
					.getResultList();

			assertThat(rows).hasSize(PARENTS * 2);
			assertThat(rows).allSatisfy(row -> assertThat(row)
					.as("managed entities, so the persistence context is warm afterwards")
					.isInstanceOf(EObject.class));

			List<String> labels = new ArrayList<>();
			rows.forEach(row -> labels
					.add(String.valueOf(((EObject) row).eGet(childClass.getEStructuralFeature("label")))));
			List<String> expected = new ArrayList<>();
			for (int p = 0; p < PARENTS; p++) {
				expected.add("p" + p + "-c0");
				expected.add("p" + p + "-c1");
			}
			assertThat(labels).containsExactlyInAnyOrderElementsOf(expected);
		} finally {
			em.close();
		}
	}

	/**
	 * Step five: {@code skip} rides on the same window — the range is a band, not just a cap.
	 * Per-parent paging needs both ends.
	 */
	@Test
	void theWindowSupportsSkipAsWellAsTop() {
		EntityManager em = emf.createEntityManager();
		try {
			List<?> rows = em.createQuery(
					"SELECT e FROM Parent anchor JOIN anchor.children e, ("
							+ "SELECT p.pid AS pid, c.cid AS cid,"
							+ " SQL('ROW_NUMBER() OVER (PARTITION BY ? ORDER BY ?)', p.pid, c.label) AS rn"
							+ " FROM Parent p JOIN p.children c) sub"
							+ " WHERE anchor.pid = sub.pid AND e.cid = sub.cid"
							+ " AND sub.rn > :skip AND sub.rn <= :upper")
					.setParameter("skip", 1)
					.setParameter("upper", 3)
					.getResultList();

			List<String> labels = new ArrayList<>();
			rows.forEach(row -> labels
					.add(String.valueOf(((EObject) row).eGet(childClass.getEStructuralFeature("label")))));
			List<String> expected = new ArrayList<>();
			for (int p = 0; p < PARENTS; p++) {
				// skip 1, take 2 — the second and third child of every parent
				expected.add("p" + p + "-c1");
				expected.add("p" + p + "-c2");
			}
			assertThat(labels).containsExactlyInAnyOrderElementsOf(expected);
		} finally {
			em.close();
		}
	}

	/** {@code PARENTS} parents, each with {@code CHILDREN} children whose labels sort per parent. */
	private void saveFixture() throws Exception {
		ResourceSet writeSet = resourceSet();
		Resource resource = writeSet.createResource(uriFor("Parent"));
		for (int p = 0; p < PARENTS; p++) {
			EObject parent = EcoreUtil.create(parentClass);
			parent.eSet(parentClass.getEStructuralFeature("pid"), "p" + p);
			parent.eSet(parentClass.getEStructuralFeature("name"), "Parent " + p);
			@SuppressWarnings("unchecked")
			List<EObject> children = (List<EObject>) parent.eGet(parentChildren);
			for (int c = 0; c < CHILDREN; c++) {
				EObject child = EcoreUtil.create(childClass);
				String label = "p" + p + "-c" + c;
				child.eSet(childClass.getEStructuralFeature("cid"), label);
				child.eSet(childClass.getEStructuralFeature("label"), label);
				children.add(child);
			}
			resource.getContents().add(parent);
		}
		resource.save(null);
	}

	private static void addId(EClass eClass, String name) {
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName(name);
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		eClass.getEStructuralFeatures().add(id);
	}

	private static void addString(EClass eClass, String name) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		eClass.getEStructuralFeatures().add(attribute);
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}
}
