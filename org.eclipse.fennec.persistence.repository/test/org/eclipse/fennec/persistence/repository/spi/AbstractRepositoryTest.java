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
package org.eclipse.fennec.persistence.repository.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIOException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.repository.api.PreparedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The generic repository mechanics that need no backend: URI arithmetic, attach/detach,
 * proxies, lifecycle, prepare. Everything data-touching is covered by the backend ITs.
 */
public class AbstractRepositoryTest {

	private static final URI BASE = URI.createURI("test://unit");

	private EClass personClass;
	private EAttribute idAttribute;
	private AbstractRepository repository;

	@BeforeEach
	void setUp() {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("test");
		ePackage.setNsPrefix("test");
		ePackage.setNsURI("http://fennec/test");
		personClass = EcoreFactory.eINSTANCE.createEClass();
		personClass.setName("Person");
		idAttribute = EcoreFactory.eINSTANCE.createEAttribute();
		idAttribute.setName("pid");
		idAttribute.setEType(EcorePackage.Literals.ESTRING);
		idAttribute.setID(true);
		personClass.getEStructuralFeatures().add(idAttribute);
		ePackage.getEClassifiers().add(personClass);
		repository = new AbstractRepository("test-repo", BASE, AbstractRepositoryTest::newResourceSet, null, null,
				null) {
		};
	}

	private static ResourceSetImpl newResourceSet() {
		ResourceSetImpl set = new ResourceSetImpl();
		set.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test",
				new org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl());
		return set;
	}

	private EObject person(String id) {
		EObject person = personClass.getEPackage().getEFactoryInstance().create(personClass);
		if (id != null) {
			person.eSet(idAttribute, id);
		}
		return person;
	}

	@Test
	void identity() {
		assertThat(repository.id()).isEqualTo("test-repo");
		assertThat(repository.baseUri()).isEqualTo(BASE);
		assertThat(repository.isDisposed()).isFalse();
	}

	@Test
	void createUriFromObject() {
		assertThat(repository.createUri(person("p1"))).isEqualTo(URI.createURI("test://unit/Person#p1"));
	}

	@Test
	void createUriWithoutIdIsNull() {
		assertThat(repository.createUri(person(null))).isNull();
	}

	@Test
	void createUriFromClassAndId() {
		assertThat(repository.createUri(personClass, "p2")).isEqualTo(URI.createURI("test://unit/Person#p2"));
	}

	@Test
	void createProxyCarriesObjectUri() {
		EObject proxy = repository.createProxy(personClass, "p3");
		assertThat(proxy.eIsProxy()).isTrue();
		assertThat(proxy.eClass()).isEqualTo(personClass);
	}

	@Test
	void attachUsesOneCollectionResourcePerType() {
		EObject first = person("p1");
		EObject second = person("p2");
		Resource resource = repository.attach(first);
		assertThat(resource.getURI()).isEqualTo(URI.createURI("test://unit/Person"));
		assertThat(repository.attach(second)).isSameAs(resource);
		assertThat(resource.getContents()).containsExactly(first, second);
		// attaching an attached object answers its resource unchanged
		assertThat(repository.attach(first)).isSameAs(resource);
	}

	@Test
	void detachRemovesAndCleansEmptyResource() {
		EObject person = person("p1");
		Resource resource = repository.attach(person);
		assertThat(repository.detach(person)).isSameAs(person);
		assertThat(person.eResource()).isNull();
		assertThat(repository.getResourceSet().getResources()).doesNotContain(resource);
	}

	@Test
	void proxifyDetachesAndMarksProxy() {
		EObject person = person("p1");
		repository.attach(person);
		repository.proxify(person);
		assertThat(person.eIsProxy()).isTrue();
		assertThat(person.eResource()).isNull();
	}

	@Test
	void proxifyWithoutIdRefuses() {
		assertThatIllegalStateException().isThrownBy(() -> repository.proxify(person(null)));
	}

	@Test
	void disposeIsIdempotentAndBlocksAccess() {
		repository.attach(person("p1"));
		repository.dispose();
		repository.dispose();
		assertThat(repository.isDisposed()).isTrue();
		assertThatIllegalStateException().isThrownBy(() -> repository.attach(person("p2")));
	}

	@Test
	void closeDisposes() throws Exception {
		repository.close();
		assertThat(repository.isDisposed()).isTrue();
	}

	@Test
	void createResourceSetIsIndependent() {
		assertThat(repository.createResourceSet()).isNotSameAs(repository.getResourceSet());
	}

	@Test
	void prepareValidQuery() throws IOException {
		Query query = QueryBuilder.from(personClass).build();
		PreparedQuery prepared = repository.prepare(query);
		assertThat(prepared.query()).isNotNull();
		assertThat(prepared.query()).isNotSameAs(query); // defensive copy
		assertThat(prepared.parameterDeclarations()).isEmpty();
	}

	@Test
	void prepareWithoutRootRefuses() {
		Query rootless = org.eclipse.fennec.model.query.QueryFactory.eINSTANCE.createQuery();
		assertThatIOException().isThrownBy(() -> repository.prepare(rootless));
	}

	@Test
	void preparedByNameHasNoQuery() throws IOException {
		PreparedQuery prepared = repository.prepare("by-name");
		assertThat(prepared.name()).isEqualTo("by-name");
		assertThat(prepared.query()).isNull();
		assertThat(prepared.parameterDeclarations()).isEmpty();
	}

	@Test
	void findWithoutQueryableBackendRefuses() {
		// a plain ResourceSetImpl yields plain resources — not QueryableResource
		assertThatIOException()
				.isThrownBy(() -> repository.find(QueryBuilder.from(personClass).build()))
				.withMessageContaining("does not support queries");
	}

	@Test
	void findByUnknownNameExplainsRootResolution() {
		assertThatIOException()
				.isThrownBy(() -> repository.find("unknown", null, null))
				.withMessageContaining("root type");
	}

	@Test
	void countWithoutPersistenceBackendRefuses() {
		assertThatIOException()
				.isThrownBy(() -> repository.count(personClass))
				.withMessageContaining("PersistenceResource");
	}
}
