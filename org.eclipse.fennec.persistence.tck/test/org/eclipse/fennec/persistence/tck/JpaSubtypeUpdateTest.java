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

import java.util.List;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.command.CommandFactory;
import org.eclipse.fennec.model.command.UpdateCommand;
import org.eclipse.fennec.model.command.UpdateEntry;
import org.eclipse.fennec.model.command.UpdateKind;
import org.eclipse.fennec.model.command.UpdateTemplate;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.fennec.persistence.query.support.CommandTransaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * An {@code UpdateCommand} on a type with a parent descriptor commits and reports success
 * (issue #395). The post-commit shared-cache drop called {@code initializeIdentityMap} with the
 * subtype's class, which EclipseLink refuses for a child descriptor (EclipseLink-7017: child
 * descriptors share the identity map of their root) — the database held the new values while
 * the caller got an {@code IOException} and the cache kept the old ones.
 * <p>
 * Modelled on the case it was found with — a {@code Pool extends Asset} in one table — and run
 * over all three places that drop the cache: the set-based statement (literal attribute
 * template, issue #228), the load-and-patch path (reference template, issue #107) and the
 * commit of a command bracket (issue #108). Each test reads once before the update so the
 * shared cache holds the old state; a drop that did not happen shows as a stale read.
 *
 * @author Mark Hoffmann
 * @since 06.11.2026
 */
class JpaSubtypeUpdateTest {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "subupd";

	private EPackage ePackage;
	private EClass assetClass;
	private EClass poolClass;
	private EClass operatorClass;
	private EAttribute assetId;
	private EAttribute assetName;
	private EAttribute poolTemperature;
	private EAttribute operatorId;
	private EReference poolOperator;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() throws Exception {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		assetClass = ecore.createEClass();
		assetClass.setName("SuAsset");
		assetId = attribute(assetClass, "id", EcorePackage.Literals.ELONG);
		assetId.setID(true);
		assetName = attribute(assetClass, "name", EcorePackage.Literals.ESTRING);

		operatorClass = ecore.createEClass();
		operatorClass.setName("SuOperator");
		operatorId = attribute(operatorClass, "id", EcorePackage.Literals.ELONG);
		operatorId.setID(true);

		poolClass = ecore.createEClass();
		poolClass.setName("SuPool");
		poolClass.getESuperTypes().add(assetClass);
		poolTemperature = attribute(poolClass, "waterTemperature", EcorePackage.Literals.EDOUBLE);
		poolOperator = ecore.createEReference();
		poolOperator.setName("operator");
		poolOperator.setEType(operatorClass);
		poolClass.getEStructuralFeatures().add(poolOperator);

		ePackage = ecore.createEPackage();
		ePackage.setName("subupd");
		ePackage.setNsURI("urn:subupd:test/1.0");
		ePackage.setNsPrefix("subupd");
		ePackage.getEClassifiers().add(assetClass);
		ePackage.getEClassifiers().add(poolClass);
		ePackage.getEClassifiers().add(operatorClass);

		emf = JpaTckSupport.bootstrap(PU_NAME, List.<EClassifier>of(assetClass, poolClass, operatorClass));
		clearWithSql();

		ResourceSet resourceSet = resourceSet();
		Resource operators = resourceSet.createResource(uri("SuOperator"));
		for (long id = 1; id <= 2; id++) {
			EObject operator = EcoreUtil.create(operatorClass);
			operator.eSet(operatorId, id);
			operators.getContents().add(operator);
		}
		operators.save(null);

		Resource pools = resourceSet.createResource(uri("SuPool"));
		for (long id = 11; id <= 12; id++) {
			EObject pool = EcoreUtil.create(poolClass);
			pool.eSet(assetId, id);
			pool.eSet(assetName, "pool " + id);
			pool.eSet(poolTemperature, 20.0);
			pool.eSet(poolOperator, operators.getContents().get(0));
			pools.getContents().add(pool);
		}
		pools.save(null);
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			clearWithSql();
			emf.close();
			emf = null;
		}
	}

	@Test
	void aLiteralUpdateOnASubtypeCommitsAndDropsTheCache() throws Exception {
		assertThat(pool(11).eGet(poolTemperature)).isEqualTo(20.0);

		CommandResource resource = commands();
		long affected = resource.execute(updatePool(11, set(poolTemperature, "26.5")));

		assertThat(affected).isEqualTo(1);
		assertThat(((Resource) resource).getErrors()).isEmpty();
		assertThat(storedTemperature(11)).isEqualTo(26.5);
		assertThat(storedTemperature(12)).as("only the selected pool").isEqualTo(20.0);
		assertThat(pool(11).eGet(poolTemperature))
				.as("the shared cache was dropped, a fresh read sees the store").isEqualTo(26.5);
	}

	@Test
	void aReferenceUpdateOnASubtypeCommitsAndDropsTheCache() throws Exception {
		assertThat(operatorOf(pool(11))).isEqualTo(1L);

		CommandResource resource = commands();
		long affected = resource.execute(updatePool(11, set(poolOperator, "2")));

		assertThat(affected).isEqualTo(1);
		assertThat(((Resource) resource).getErrors()).isEmpty();
		assertThat(storedOperator(11)).isEqualTo(2L);
		assertThat(operatorOf(pool(11)))
				.as("the shared cache was dropped, a fresh read sees the store").isEqualTo(2L);
	}

	@Test
	void aBracketCommitOfASubtypeUpdateSucceedsAndDropsTheCache() throws Exception {
		assertThat(operatorOf(pool(12))).isEqualTo(1L);
		assertThat(pool(12).eGet(poolTemperature)).isEqualTo(20.0);

		CommandResource resource = commands();
		try (CommandTransaction transaction = resource.begin()) {
			assertThat(resource.execute(updatePool(12, set(poolOperator, "2")))).isEqualTo(1);
			assertThat(resource.execute(updatePool(12, set(poolTemperature, "18.0")))).isEqualTo(1);
			transaction.commit();
		}

		assertThat(((Resource) resource).getErrors()).isEmpty();
		assertThat(storedOperator(12)).isEqualTo(2L);
		assertThat(storedTemperature(12)).isEqualTo(18.0);
		EObject reloaded = pool(12);
		assertThat(operatorOf(reloaded)).isEqualTo(2L);
		assertThat(reloaded.eGet(poolTemperature)).isEqualTo(18.0);
	}

	@Test
	void anUpdateThroughTheRootStillDropsTheCache() throws Exception {
		assertThat(pool(11).eGet(assetName)).isEqualTo("pool 11");

		CommandResource resource = commands();
		UpdateCommand update = updateCommand(QueryBuilder.from(assetClass)
				.where(Expressions.path(assetId).eq(11L)).build(),
				set(assetName, "renamed"));

		assertThat(resource.execute(update)).isEqualTo(1);
		assertThat(((Resource) resource).getErrors()).isEmpty();
		assertThat(pool(11).eGet(assetName)).isEqualTo("renamed");
	}

	private UpdateCommand updatePool(long id, UpdateEntry entry) {
		return updateCommand(QueryBuilder.from(poolClass).where(Expressions.path(assetId).eq(id)).build(), entry);
	}

	private UpdateEntry set(EStructuralFeature feature, String value) {
		UpdateEntry entry = CommandFactory.eINSTANCE.createUpdateEntry();
		entry.setKind(UpdateKind.SET);
		entry.setFeature(feature);
		entry.setValue(value);
		return entry;
	}

	private UpdateCommand updateCommand(Query selector, UpdateEntry entry) {
		UpdateCommand update = CommandFactory.eINSTANCE.createUpdateCommand();
		update.setSelector(selector);
		UpdateTemplate template = CommandFactory.eINSTANCE.createUpdateTemplate();
		template.getEntries().add(entry);
		update.setTemplate(template);
		return update;
	}

	private CommandResource commands() {
		return (CommandResource) resourceSet().createResource(uri("SuPool"));
	}

	/** A read through the resource — served from the shared cache when it holds the row. */
	private EObject pool(long id) {
		return resourceSet().getEObject(URI.createURI(uri("SuPool") + "#" + id), true);
	}

	private Object operatorOf(EObject pool) {
		EObject operator = (EObject) pool.eGet(poolOperator);
		if (operator.eIsProxy()) {
			operator = EcoreUtil.resolve(operator, pool);
		}
		return operator.eGet(operatorId);
	}

	/** Straight from the table — the store, not the cache. */
	private double storedTemperature(long id) {
		return ((Number) single("SELECT WATERTEMPERATURE FROM SUASSET WHERE ID = " + id)).doubleValue();
	}

	private long storedOperator(long id) {
		return ((Number) single("SELECT SUOPERATOR_ID FROM SUASSET WHERE ID = " + id)).longValue();
	}

	private Object single(String sql) {
		try (EntityManager em = emf.createEntityManager()) {
			return em.createNativeQuery(sql).getSingleResult();
		}
	}

	/** Plain SQL, so setup and cleanup do not depend on the paths under test. */
	private void clearWithSql() {
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.createNativeQuery("DELETE FROM SUASSET").executeUpdate();
			em.createNativeQuery("DELETE FROM SUOPERATOR").executeUpdate();
			em.getTransaction().commit();
		}
		emf.getCache().evictAll();
	}

	private static EAttribute attribute(EClass owner, String name, EDataType type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		owner.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uri(String type) {
		return URI.createURI("jpa://" + PU_NAME + "/" + type);
	}
}
