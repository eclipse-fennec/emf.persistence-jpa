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
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.query.Expand;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expands;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavor;
import org.eclipse.fennec.persistence.eclipselink.query.JpaExpandPlan;
import org.eclipse.fennec.persistence.eclipselink.query.JpaQueryPlan;
import org.eclipse.fennec.persistence.eclipselink.query.JpaQueryProcessor;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.fennec.persistence.query.support.QueryContexts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Issue #429: a model whose id attribute is literally named {@code id}, with an ordinary
 * attribute named {@code version}.
 * <p>
 * JPA 3.2 added {@code ID(...)} and {@code VERSION(...)}, so the EclipseLink 5 grammar reserves
 * both words: {@code SELECT p.id AS id ...} fails to parse. The other tests of this suite use
 * {@code pid}/{@code cid}/{@code aid} and never met it. Every query shape that used to alias a
 * column after its attribute or output key runs here against the database — the per-parent
 * paging window (issue #238), the representative window (issue #259), and the result variables
 * of projections, groupings and terminal computes.
 * <p>
 * Run against the other flavors with
 * {@code -Djpa.test.flavor=postgres -Djpa.container.cli=docker}.
 */
class JpaReservedIdAliasTest {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "reservedid";

	/** Three parents, four children each — enough that a per-parent cap differs from a global one. */
	private static final int PARENTS = 3;
	private static final int CHILDREN = 4;

	private EPackage ePackage;
	private EClass parentClass;
	private EClass childClass;
	private EAttribute parentId;
	private EAttribute parentName;
	private EAttribute parentVersion;
	private EAttribute parentCategory;
	private EAttribute childLabel;
	private EReference parentChildren;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() throws Exception {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;

		childClass = ecore.createEClass();
		childClass.setName("Child");
		addAttribute(childClass, "cid", EcorePackage.Literals.ESTRING).setID(true);
		childLabel = addAttribute(childClass, "label", EcorePackage.Literals.ESTRING);

		parentClass = ecore.createEClass();
		parentClass.setName("Parent");
		parentId = addAttribute(parentClass, "id", EcorePackage.Literals.ESTRING);
		parentId.setID(true);
		parentName = addAttribute(parentClass, "name", EcorePackage.Literals.ESTRING);
		// an ordinary attribute, not an optimistic lock: only the word is reserved
		parentVersion = addAttribute(parentClass, "version", EcorePackage.Literals.EINT);
		parentCategory = addAttribute(parentClass, "category", EcorePackage.Literals.ESTRING);
		parentChildren = ecore.createEReference();
		parentChildren.setName("children");
		parentChildren.setEType(childClass);
		parentChildren.setUpperBound(-1);
		parentChildren.setContainment(true);
		parentClass.getEStructuralFeatures().add(parentChildren);

		ePackage = ecore.createEPackage();
		ePackage.setName("reservedid");
		ePackage.setNsURI("urn:reservedid:test/1.0");
		ePackage.setNsPrefix("rid");
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
	 * The path that broke emf.odata: per-parent paging in {@code $expand}. The resource runs the
	 * window through its keyed second query; the same plan run directly shows it picks the right
	 * band of children for every parent.
	 */
	@Test
	void expandPagingPerParentRunsWithAnIdNamedId() throws Exception {
		Expand paged = Expands.of(parentChildren).orderByAsc(childLabel).top(2).skip(1).build();
		Query query = QueryBuilder.from(parentClass).expand(paged).orderByAsc(parentId).build();

		try (QueryResult result = queryable().query(query)) {
			assertThat(result.objects().toList()).hasSize(PARENTS);
		}

		List<String> labels = labelsOf(expandPlanOf(query));
		List<String> expected = new ArrayList<>();
		for (int p = 0; p < PARENTS; p++) {
			// skip 1, take 2 — the second and third child of every parent
			expected.add("p" + p + "-c1");
			expected.add("p" + p + "-c2");
		}
		assertThat(labels).containsExactlyInAnyOrderElementsOf(expected);
	}

	/** Without an ordering the window falls back to the target id, and top alone still caps. */
	@Test
	void expandTopWithoutOrderingRunsWithAnIdNamedId() throws Exception {
		Query query = QueryBuilder.from(parentClass)
				.expand(Expands.of(parentChildren).top(1).build()).build();

		try (QueryResult result = queryable().query(query)) {
			assertThat(result.objects().toList()).hasSize(PARENTS);
		}
		assertThat(labelsOf(expandPlanOf(query))).containsExactlyInAnyOrder("p0-c0", "p1-c0", "p2-c0");
	}

	/**
	 * A filtered expansion, keyed by {@code p.id IN :expandKeys}. The resource refuses a filter on
	 * a containment reference, so only the translated keyed query runs here.
	 */
	@Test
	void filteredExpandRunsWithAnIdNamedId() throws Exception {
		Query query = QueryBuilder.from(parentClass)
				.expand(Expands.of(parentChildren).filter(Expressions.path(childLabel).eq("p1-c3")).build())
				.build();

		assertThat(labelsOf(expandPlanOf(query))).containsExactly("p1-c3");
	}

	/** The representative window (issue #259) addresses the root by {@code sub.id}. */
	@Test
	void representativesRunWithAnIdNamedId() throws Exception {
		Query query = QueryBuilder.from(parentClass).groupBy(parentCategory).countOf("cnt")
				.representatives("members", 1).build();

		Map<Object, Object[]> byCategory = new TreeMap<>();
		try (QueryResult result = queryable().query(query)) {
			result.rows().forEach(row -> byCategory.put(row.get("category"),
					new Object[] { ((Number) row.get("cnt")).intValue(), row.get("members") }));
		}

		assertThat(byCategory).containsOnlyKeys("a", "b");
		assertThat(byCategory.get("a")[0]).isEqualTo(2);
		assertThat(byCategory.get("b")[0]).isEqualTo(1);
		assertThat((List<?>) byCategory.get("a")[1]).hasSize(1);
		assertThat((List<?>) byCategory.get("b")[1]).hasSize(1)
				.allSatisfy(member -> assertThat(((EObject) member).eGet(parentId)).isEqualTo("p2"));
	}

	/**
	 * {@code select(id)}, sorted by it. An unaliased path projection is an ordinal-only cell, so
	 * the values are read by position.
	 */
	@Test
	void projectingIdAndVersionKeepsTheOutputKeys() throws Exception {
		Query query = QueryBuilder.from(parentClass).select(parentId).select(parentVersion)
				.orderByDesc(parentId).build();

		List<String> ids = new ArrayList<>();
		try (QueryResult result = queryable().query(query)) {
			result.rows().forEach(row -> {
				String id = (String) row.get(0);
				ids.add(id);
				assertThat(((Number) row.get(1)).intValue()).isEqualTo(Integer.parseInt(id.substring(1)) + 10);
			});
		}
		assertThat(ids).containsExactly("p2", "p1", "p0");
	}

	/** A grouping by {@code id} and by {@code version}. */
	@Test
	void groupingByIdAndVersionKeepsTheOutputKeys() throws Exception {
		Query byId = QueryBuilder.from(parentClass).groupBy(parentId).countOf("cnt")
				.orderByAsc(parentId).build();
		List<String> ids = new ArrayList<>();
		try (QueryResult result = queryable().query(byId)) {
			result.rows().forEach(row -> {
				ids.add((String) row.get("id"));
				assertThat(((Number) row.get("cnt")).intValue()).isEqualTo(1);
			});
		}
		assertThat(ids).containsExactly("p0", "p1", "p2");

		Query byVersion = QueryBuilder.from(parentClass).groupBy(parentVersion).countOf("cnt")
				.orderByDesc(parentVersion).build();
		List<Integer> versions = new ArrayList<>();
		try (QueryResult result = queryable().query(byVersion)) {
			result.rows().forEach(row -> versions.add(((Number) row.get("version")).intValue()));
		}
		assertThat(versions).containsExactly(12, 11, 10);
	}

	/** The terminal compute selects every single-valued attribute, {@code id} and {@code version} among them. */
	@Test
	void terminalComputeKeepsTheAttributeKeys() throws Exception {
		Query query = QueryBuilder.from(parentClass)
				.computeAs("next", Expressions.path(parentVersion).plus(1).toExpression())
				.orderByAsc(parentId).build();

		List<QueryResultRow> rows;
		try (QueryResult result = queryable().query(query)) {
			rows = result.rows().toList();
		}
		assertThat(rows).hasSize(PARENTS);
		assertThat(rows.get(0).get("id")).isEqualTo("p0");
		assertThat(((Number) rows.get(0).get("version")).intValue()).isEqualTo(10);
		assertThat(((Number) rows.get(0).get("next")).intValue()).isEqualTo(11);
	}

	/** User aliases {@code id} and {@code version} reach the rows, never the JPQL. */
	@Test
	void userAliasesNamedIdAndVersionKeepTheirKeys() throws Exception {
		Query query = QueryBuilder.from(parentClass)
				.selectAs("id", parentName)
				.selectAs("version", Expressions.path(parentVersion).plus(100).toExpression())
				.orderByAsc(Expressions.aliasRef("version").toExpression())
				.build();

		List<QueryResultRow> rows;
		try (QueryResult result = queryable().query(query)) {
			rows = result.rows().toList();
		}
		assertThat(rows).extracting(row -> row.get("id"))
				.containsExactly("Parent 0", "Parent 1", "Parent 2");
		assertThat(rows).extracting(row -> ((Number) row.get("version")).intValue())
				.containsExactly(110, 111, 112);
	}

	/** The translated expansion plan, run the way the resource runs it — keyed by every parent. */
	private List<String> labelsOf(JpaExpandPlan plan) {
		EntityManager em = emf.createEntityManager();
		try {
			jakarta.persistence.Query query = em.createQuery(plan.jpql());
			query.setParameter(JpaExpandPlan.KEY_PARAMETER, List.of("p0", "p1", "p2"));
			plan.parameters().forEach(query::setParameter);
			List<String> labels = new ArrayList<>();
			for (Object row : query.getResultList()) {
				labels.add((String) ((EObject) row).eGet(childClass.getEStructuralFeature("label")));
			}
			return labels;
		} finally {
			em.close();
		}
	}

	private JpaExpandPlan expandPlanOf(Query query) throws Exception {
		JpaFlavor flavor = JpaFlavor.byId(JpaTestSupport.flavor()).orElseThrow();
		JpaQueryPlan plan = (JpaQueryPlan) new JpaQueryProcessor(flavor)
				.translate(query, QueryContexts.of(parentClass, null));
		assertThat(plan.expandPlans()).hasSize(1);
		JpaExpandPlan expand = plan.expandPlans().get(0);
		assertThat(expand.jpql()).as("no attribute name as an alias").doesNotContain(" AS id");
		return expand;
	}

	/**
	 * {@code PARENTS} parents {@code p0..p2} with version {@code 10 + n}; {@code p0} and
	 * {@code p1} in category {@code a}, {@code p2} in {@code b}; each with {@code CHILDREN}
	 * children whose labels sort per parent.
	 */
	private void saveFixture() throws Exception {
		ResourceSet writeSet = resourceSet();
		Resource resource = writeSet.createResource(uriFor("Parent"));
		for (int p = 0; p < PARENTS; p++) {
			EObject parent = EcoreUtil.create(parentClass);
			parent.eSet(parentId, "p" + p);
			parent.eSet(parentName, "Parent " + p);
			parent.eSet(parentVersion, 10 + p);
			parent.eSet(parentCategory, p < 2 ? "a" : "b");
			@SuppressWarnings("unchecked")
			List<EObject> children = (List<EObject>) parent.eGet(parentChildren);
			for (int c = 0; c < CHILDREN; c++) {
				EObject child = EcoreUtil.create(childClass);
				String label = "p" + p + "-c" + c;
				child.eSet(childClass.getEStructuralFeature("cid"), label);
				child.eSet(childLabel, label);
				children.add(child);
			}
			resource.getContents().add(parent);
		}
		resource.save(null);
	}

	private static EAttribute addAttribute(EClass eClass, String name, EDataType type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		eClass.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private QueryableResource queryable() {
		return (QueryableResource) resourceSet().createResource(uriFor("Parent"));
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
