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
package org.eclipse.fennec.persistence.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link EMFHelper}
 */
public class EMFHelperTest {

	@Nested
	class GetResponseTests {

		@Test
		void testNullOptionsReturnsEmptyMap() {
			Map<Object, Object> response = EMFHelper.getResponse(null);
			assertThat(response).isNotNull().isEmpty();
		}

		@Test
		void testEmptyOptionsReturnsEmptyMap() {
			Map<Object, Object> response = EMFHelper.getResponse(Collections.emptyMap());
			assertThat(response).isNotNull().isEmpty();
		}

		@Test
		void testOptionsWithoutResponseReturnsEmptyMap() {
			Map<String, Object> options = Map.of("someKey", "someValue");
			Map<Object, Object> response = EMFHelper.getResponse(options);
			assertThat(response).isNotNull().isEmpty();
		}

		@Test
		void testOptionsWithResponseReturnsIt() {
			Map<Object, Object> responseMap = new HashMap<>();
			responseMap.put("result", "ok");
			Map<Object, Object> options = new HashMap<>();
			options.put(URIConverter.OPTION_RESPONSE, responseMap);

			Map<Object, Object> response = EMFHelper.getResponse(options);
			assertThat(response).isSameAs(responseMap);
			assertThat(response).containsEntry("result", "ok");
		}
	}

	@Nested
	class MergeMapsTests {

		@Test
		void testBothNullReturnsNull() {
			assertThat(EMFHelper.mergeMaps(null, null)).isNull();
		}

		@Test
		void testFirstNullReturnsSecond() {
			Map<String, String> map2 = Map.of("a", "1");
			assertThat(EMFHelper.mergeMaps(null, map2)).isSameAs(map2);
		}

		@Test
		void testSecondNullReturnsFirst() {
			Map<String, String> map1 = Map.of("a", "1");
			assertThat(EMFHelper.mergeMaps(map1, null)).isSameAs(map1);
		}

		@Test
		void testFirstEmptyReturnsSecond() {
			Map<String, String> map2 = Map.of("a", "1");
			assertThat(EMFHelper.mergeMaps(Collections.emptyMap(), map2)).isSameAs(map2);
		}

		@Test
		void testSecondEmptyReturnsFirst() {
			Map<String, String> map1 = Map.of("a", "1");
			assertThat(EMFHelper.mergeMaps(map1, Collections.emptyMap())).isSameAs(map1);
		}

		@Test
		void testMergeDisjointMaps() {
			Map<String, String> map1 = Map.of("a", "1");
			Map<String, String> map2 = Map.of("b", "2");

			Map<?, ?> result = EMFHelper.mergeMaps(map1, map2);
			assertThat(result.get("a")).isEqualTo("1");
			assertThat(result.get("b")).isEqualTo("2");
			assertThat(result).hasSize(2);
		}

		@Test
		void testMap2OverridesMap1OnConflict() {
			// Implementation: putAll(map1) then putAll(map2) → map2 wins
			Map<String, String> map1 = new HashMap<>();
			map1.put("key", "from-map1");
			Map<String, String> map2 = new HashMap<>();
			map2.put("key", "from-map2");

			Map<?, ?> result = EMFHelper.mergeMaps(map1, map2);
			assertThat(result.get("key")).isEqualTo("from-map2");
		}

		@Test
		void testOriginalMapsNotModified() {
			Map<String, String> map1 = new HashMap<>();
			map1.put("a", "1");
			Map<String, String> map2 = new HashMap<>();
			map2.put("b", "2");

			EMFHelper.mergeMaps(map1, map2);

			assertThat(map1).hasSize(1);
			assertThat(map1.get("a")).isEqualTo("1");
			assertThat(map2).hasSize(1);
			assertThat(map2.get("b")).isEqualTo("2");
		}
	}

	@Nested
	class GetEffectiveOptionsTests {

		@Test
		void testEffectiveOptionsContainsResponse() {
			Map<Object, Object> options = new HashMap<>();
			Map<Object, Object> defaults = new HashMap<>();
			defaults.put("default.key", "default.value");

			Map<Object, Object> result = EMFHelper.getEffectiveOptions(options, defaults);

			assertThat(result).containsKey(URIConverter.OPTION_RESPONSE);
			assertThat(result).containsEntry("default.key", "default.value");
		}

		@Test
		void testEffectiveOptionsIsUnmodifiable() {
			Map<Object, Object> result = EMFHelper.getEffectiveOptions(new HashMap<>(), new HashMap<>());

			org.assertj.core.api.Assertions.assertThatThrownBy(() -> result.put("new", "value"))
				.isInstanceOf(UnsupportedOperationException.class);
		}

		@Test
		void testEffectiveOptionsMergesBothMaps() {
			Map<Object, Object> options = new HashMap<>();
			options.put("opt", "optValue");
			Map<Object, Object> defaults = new HashMap<>();
			defaults.put("def", "defValue");

			Map<Object, Object> result = EMFHelper.getEffectiveOptions(options, defaults);

			assertThat(result).containsEntry("opt", "optValue");
			assertThat(result).containsEntry("def", "defValue");
		}
	}

	@Nested
	class GetEClassTests {

		@Test
		void testGetEClassFromResourceSet() {
			ResourceSet rs = new ResourceSetImpl();
			EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
			pkg.setName("testpkg");
			pkg.setNsURI("http://test/pkg");
			EClass person = EcoreFactory.eINSTANCE.createEClass();
			person.setName("Person");
			pkg.getEClassifiers().add(person);
			rs.getPackageRegistry().put(pkg.getNsURI(), pkg);

			EClass result = EMFHelper.getEClassFromResourceSet(rs, "http://test/pkg/Person");
			assertThat(result).isSameAs(person);
		}

		@Test
		void testGetEClassWithCacheHit() {
			ResourceSet rs = new ResourceSetImpl();
			EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
			pkg.setName("testpkg");
			pkg.setNsURI("http://test/pkg");
			EClass person = EcoreFactory.eINSTANCE.createEClass();
			person.setName("Person");
			pkg.getEClassifiers().add(person);
			rs.getPackageRegistry().put(pkg.getNsURI(), pkg);

			Map<String, EClass> cache = new HashMap<>();
			String uri = "http://test/pkg/Person";

			// First call — cache miss
			EClass result1 = EMFHelper.getEClass(rs, uri, cache);
			assertThat(result1).isSameAs(person);
			assertThat(cache).containsKey(uri);

			// Second call — cache hit
			EClass result2 = EMFHelper.getEClass(rs, uri, cache);
			assertThat(result2).isSameAs(person);
		}

		@Test
		void testGetEClassWithNullCacheBypassesCache() {
			ResourceSet rs = new ResourceSetImpl();
			EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
			pkg.setName("testpkg");
			pkg.setNsURI("http://test/pkg");
			EClass person = EcoreFactory.eINSTANCE.createEClass();
			person.setName("Person");
			pkg.getEClassifiers().add(person);
			rs.getPackageRegistry().put(pkg.getNsURI(), pkg);

			EClass result = EMFHelper.getEClass(rs, "http://test/pkg/Person", null);
			assertThat(result).isSameAs(person);
		}

		@Test
		void testGetEClassCacheMultipleClasses() {
			ResourceSet rs = new ResourceSetImpl();
			EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
			pkg.setName("testpkg");
			pkg.setNsURI("http://test/pkg");
			EClass person = EcoreFactory.eINSTANCE.createEClass();
			person.setName("Person");
			EClass address = EcoreFactory.eINSTANCE.createEClass();
			address.setName("Address");
			pkg.getEClassifiers().add(person);
			pkg.getEClassifiers().add(address);
			rs.getPackageRegistry().put(pkg.getNsURI(), pkg);

			Map<String, EClass> cache = new HashMap<>();

			EClass r1 = EMFHelper.getEClass(rs, "http://test/pkg/Person", cache);
			EClass r2 = EMFHelper.getEClass(rs, "http://test/pkg/Address", cache);

			assertThat(r1).isSameAs(person);
			assertThat(r2).isSameAs(address);
			assertThat(cache).hasSize(2);
		}
	}
}
