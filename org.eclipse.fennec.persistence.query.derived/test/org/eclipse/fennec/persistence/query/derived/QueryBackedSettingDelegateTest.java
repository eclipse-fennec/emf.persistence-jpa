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
package org.eclipse.fennec.persistence.query.derived;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.fennec.persistence.query.support.QueryResults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Object-specific routing of the query-backed delegate (#71/#72): plain owners evaluate
 * locally, owners attached to a {@link QueryableResource} with unresolved proxies push
 * down with id-IN correlation.
 *
 * @author Juergen Albert
 */
class QueryBackedSettingDelegateTest {

	private EPackage tckPackage;
	private EClass person;
	private EAttribute pid;
	private EAttribute name;
	private EAttribute age;
	private EReference friends;
	private EReference adultFriends;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		tckPackage = ecore.createEPackage();
		tckPackage.setName("derived");
		tckPackage.setNsPrefix("derived");
		tckPackage.setNsURI("https://eclipse.org/fennec/test/derived-delegate");
		EAnnotation packageAnnotation = ecore.createEAnnotation();
		packageAnnotation.setSource(EcorePackage.eNS_URI);
		packageAnnotation.getDetails().put("settingDelegates", DerivedReferenceCompiler.DELEGATE_URI);
		tckPackage.getEAnnotations().add(packageAnnotation);

		person = ecore.createEClass();
		person.setName("Person");
		pid = ecore.createEAttribute();
		pid.setName("pid");
		pid.setEType(EcorePackage.Literals.EINT);
		pid.setID(true);
		name = ecore.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		age = ecore.createEAttribute();
		age.setName("age");
		age.setEType(EcorePackage.Literals.EINT);
		person.getEStructuralFeatures().add(pid);
		person.getEStructuralFeatures().add(name);
		person.getEStructuralFeatures().add(age);

		friends = ecore.createEReference();
		friends.setName("friends");
		friends.setEType(person);
		friends.setUpperBound(-1);
		person.getEStructuralFeatures().add(friends);

		adultFriends = ecore.createEReference();
		adultFriends.setName("adultFriends");
		adultFriends.setEType(person);
		adultFriends.setUpperBound(-1);
		adultFriends.setTransient(true);
		adultFriends.setVolatile(true);
		adultFriends.setDerived(true);
		adultFriends.setChangeable(false);
		EAnnotation annotation = ecore.createEAnnotation();
		annotation.setSource(DerivedReferenceCompiler.DELEGATE_URI);
		annotation.getDetails().put(DerivedReferenceCompiler.DETAIL_DERIVATION,
				"self.friends->select(f | f.age >= 18)");
		adultFriends.getEAnnotations().add(annotation);
		person.getEStructuralFeatures().add(adultFriends);

		tckPackage.getEClassifiers().add(person);

		EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE
				.put(DerivedReferenceCompiler.DELEGATE_URI, new QueryBackedSettingDelegateFactory());
	}

	@AfterEach
	void tearDown() {
		EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE
				.remove(DerivedReferenceCompiler.DELEGATE_URI);
	}

	private EObject newPerson(int id, String personName, int personAge) {
		EObject object = EcoreUtil.create(person);
		object.eSet(pid, id);
		object.eSet(name, personName);
		object.eSet(age, personAge);
		return object;
	}

	@Test
	void unattachedOwnerEvaluatesLocally() {
		EObject alice = newPerson(1, "Alice", 30);
		EObject kid = newPerson(2, "Kid", 8);
		EObject owner = newPerson(3, "Owner", 40);
		@SuppressWarnings("unchecked")
		List<EObject> ownerFriends = (List<EObject>) owner.eGet(friends);
		ownerFriends.add(alice);
		ownerFriends.add(kid);

		@SuppressWarnings("unchecked")
		List<EObject> result = (List<EObject>) owner.eGet(adultFriends);
		assertThat(result).containsExactly(alice);
		assertThatThrownBy(() -> result.add(kid)).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void attachedOwnerWithProxiesPushesDownWithIdCorrelation() throws Exception {
		EObject adult = newPerson(1, "Adult", 30);
		RecordingQueryableResource resource = new RecordingQueryableResource(
				URI.createURI("fake://store/Person"), List.of(adult));
		EObject owner = newPerson(3, "Owner", 40);
		resource.getContents().add(owner);
		@SuppressWarnings("unchecked")
		List<EObject> ownerFriends = (List<EObject>) owner.eGet(friends);
		ownerFriends.add(proxy(1));
		ownerFriends.add(proxy(2));

		@SuppressWarnings("unchecked")
		List<EObject> result = (List<EObject>) owner.eGet(adultFriends);
		assertThat(result).containsExactly(adult);
		assertThat(resource.lastQuery).isNotNull();
		assertThat(resource.lastQuery.getFrom()).isSameAs(person);
		// the predicate is AND(pid IN (1, 2), age >= 18)
		assertThat(resource.lastQuery.getPredicate().eClass().getName()).isEqualTo("And");
	}

	@Test
	void memoryOnlyDerivationWithoutEngineIsRefusedOnAccess() {
		EAnnotation annotation = adultFriends.getEAnnotation(DerivedReferenceCompiler.DELEGATE_URI);
		annotation.getDetails().put(DerivedReferenceCompiler.DETAIL_DERIVATION,
				"self.friends->reject(f | f.age >= 18)");
		EObject owner = newPerson(1, "Owner", 40);
		assertThatThrownBy(() -> owner.eGet(adultFriends))
				.isInstanceOf(WrappedException.class)
				.hasMessageContaining("m2x.ocl.engine");
	}

	/** A dynamic proxy of the Person class carrying only the proxy URI (unresolved). */
	private EObject proxy(int id) {
		InternalEObject personProxy = (InternalEObject) EcoreUtil.create(person);
		personProxy.eSetProxyURI(URI.createURI("fake://store/Person").appendFragment(String.valueOf(id)));
		return personProxy;
	}

	/** A minimal QueryableResource stub: records the query, answers with fixed objects. */
	private static final class RecordingQueryableResource extends ResourceImpl implements QueryableResource {

		private final List<EObject> answer;
		Query lastQuery;

		private RecordingQueryableResource(URI uri, List<EObject> answer) {
			super(uri);
			this.answer = new ArrayList<>(answer);
		}

		@Override
		public QueryResult query(Query query) throws IOException {
			return query(query, null, null);
		}

		@Override
		public QueryResult query(Query query, Map<String, Object> parameters, Map<?, ?> options)
				throws IOException {
			lastQuery = query;
			return QueryResults.objects(answer.stream());
		}

		@Override
		public QueryResult query(String queryName, Map<String, Object> parameters, Map<?, ?> options)
				throws IOException {
			throw new IOException("no catalog in the stub");
		}
	}
}
