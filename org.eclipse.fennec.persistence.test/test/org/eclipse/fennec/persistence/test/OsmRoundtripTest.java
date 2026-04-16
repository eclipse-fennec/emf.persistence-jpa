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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicPersistenceUnitInfo;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeGenerator;
import org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceImpl;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Non-OSGi XMI → DB → XMI roundtrip test using the OSM domain model.
 * Uses in-memory H2 to avoid file corruption with large SINGLE_TABLE schemas.
 */
class OsmRoundtripTest {

	@TempDir
	File tempDir;

	private ResourceSet rs;
	private EPackage domainPackage;
	private EntityManagerFactory emf;
	private Server serverSession;

	@BeforeEach
	void setUp() throws IOException {
		long t0 = System.currentTimeMillis();

		rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

		// Load domain.ecore
		File ecoreFile = new File("data/osm/domain.ecore");
		assertThat(ecoreFile).as("domain.ecore must exist").exists();
		Resource ecoreResource = rs.createResource(URI.createFileURI(ecoreFile.getAbsolutePath()));
		ecoreResource.load(null);
		domainPackage = (EPackage) ecoreResource.getContents().get(0);
		rs.getPackageRegistry().put(domainPackage.getNsURI(), domainPackage);
		long t1 = System.currentTimeMillis();
		System.out.printf("[TIMING] Load Ecore (435 classes): %d ms%n", t1 - t0);

		// Create EORM mappings
		List<EClassifier> allClasses = domainPackage.getEClassifiers().stream()
				.filter(EClass.class::isInstance)
				.collect(Collectors.toList());
		EntityMapper mapper = new EntityMapper();
		EntityMappings mappings = mapper.createMappings(allClasses);
		long t2 = System.currentTimeMillis();
		System.out.printf("[TIMING] Create EORM mappings: %d ms%n", t2 - t1);

		// Create empty EntityManagerFactory
		DynamicClassLoader dcl = new DynamicClassLoader(getClass().getClassLoader());
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		props.put(PersistenceUnitProperties.DDL_GENERATION, "create-or-extend-tables");
		props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, "database");
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:mem:osmtest_" + UUID.randomUUID());
		props.put(PersistenceUnitProperties.JDBC_USER, "sa");
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
		props.put(PersistenceUnitProperties.BATCH_WRITING, "JDBC");
		props.put(PersistenceUnitProperties.BATCH_WRITING_SIZE, "500");

		PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		pu.setName("osm");
		pu.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
		URL puRoot = getClass().getProtectionDomain().getCodeSource().getLocation();
		EDynamicPersistenceUnitInfo pui = new EDynamicPersistenceUnitInfo(pu, puRoot, props);

		PersistenceProvider provider = new PersistenceProvider();
		emf = provider.createContainerEntityManagerFactory(pui, props);
		serverSession = JpaHelper.getServerSession(emf);
		long t3 = System.currentTimeMillis();
		System.out.printf("[TIMING] Create EMF (empty): %d ms%n", t3 - t2);

		// Generate dynamic types from EORM mappings
		EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl, serverSession, "osm");
		List<EDynamicType> types = generator.createFromMappings(List.of(mappings));
		long t4 = System.currentTimeMillis();
		System.out.printf("[TIMING] Generate dynamic types (%d types): %d ms%n", types.size(), t4 - t3);

		// Add to session + DDL generation
		EDynamicHelper helper = new EDynamicHelper(emf, dcl);
		helper.addETypes(true, true, types);
		long t5 = System.currentTimeMillis();
		System.out.printf("[TIMING] addETypes + DDL generation: %d ms%n", t5 - t4);
		System.out.printf("[TIMING] Total setUp: %d ms%n", t5 - t0);
	}

	@AfterEach
	void tearDown() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}

	@Test
	@DisplayName("XMI → DB → XMI roundtrip: 500 objects (small)")
	void testXmiRoundtripSmall() throws IOException {
		runRoundtrip("data/osm/domain_small.xmi");
	}

	@Test
	@Tag("perf")
	@DisplayName("XMI → DB → XMI roundtrip: 10k objects (full)")
	void testXmiRoundtripFull() throws IOException {
		// Decompress .gz on the fly — keeps the repo small (22 MB → 981 KB)
		File gz = new File("data/osm/domain_full.xmi.gz");
		assertThat(gz).as("domain_full.xmi.gz must exist").exists();
		File xmiFile = new File(tempDir, "domain_full.xmi");
		try (var gzIn = new GZIPInputStream(new FileInputStream(gz));
			 var out = new FileOutputStream(xmiFile)) {
			gzIn.transferTo(out);
		}
		runRoundtrip(xmiFile.getAbsolutePath());
	}

	private void runRoundtrip(String xmiPath) throws IOException {
		long t0 = System.currentTimeMillis();

		// 1. Load XMI from project data directory
		File xmiFile = new File(xmiPath);
		assertThat(xmiFile).as(xmiPath + " must exist").exists();
		Resource xmiResource = rs.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));
		xmiResource.load(null);
		List<EObject> sourceObjects = new ArrayList<>(xmiResource.getContents());
		int totalSourceCount = sourceObjects.size();
		long t1 = System.currentTimeMillis();
		System.out.printf("[TIMING] Load XMI (%s, %d KB): %d ms — %d objects%n",
				xmiFile.getName(), xmiFile.length() / 1024, t1 - t0, totalSourceCount);

		Map<String, List<EObject>> sourceByType = sourceObjects.stream()
				.collect(Collectors.groupingBy(
						eo -> eo.eClass().getName(),
						LinkedHashMap::new,
						Collectors.toList()));
		System.out.println("Distinct entity types: " + sourceByType.size());

		// 2. Persist via JPAResource (uses toManagedEntity for XMI objects)
		long t2 = System.currentTimeMillis();
		try (JPAResourceImpl saveResource = new JPAResourceImpl(
				URI.createURI("jpa://osm/BulkSave"), emf)) {
			saveResource.getContents().addAll(sourceObjects);
			saveResource.save(null);
		} catch (Exception e) {
			fail("Failed to persist XMI objects", e);
		}
		long t3 = System.currentTimeMillis();
		System.out.printf("[TIMING] Persist %d objects: %d ms%n", totalSourceCount, t3 - t2);

		// 3. Read back per entity type
		List<EObject> readBackObjects = new ArrayList<>();
		for (String typeName : sourceByType.keySet()) {
			ClassDescriptor descriptor = serverSession.getDescriptorForAlias(typeName);
			if (descriptor == null) {
				continue;
			}
			try (EntityManager em = emf.createEntityManager()) {
				List<?> results = em.createQuery(
						"SELECT e FROM " + descriptor.getAlias() + " e",
						descriptor.getJavaClass())
						.getResultList();
				for (Object obj : results) {
					if (obj instanceof EObject eo) {
						readBackObjects.add(eo);
					}
				}
			}
		}
		long t4 = System.currentTimeMillis();
		System.out.printf("[TIMING] Read back %d objects: %d ms%n", readBackObjects.size(), t4 - t3);

		// 4. Verify counts per type
		Map<String, List<EObject>> readBackByType = readBackObjects.stream()
				.collect(Collectors.groupingBy(
						eo -> eo.eClass().getName(),
						LinkedHashMap::new,
						Collectors.toList()));

		assertThat(readBackByType.keySet())
				.as("All entity types from XMI should be present in DB")
				.containsAll(sourceByType.keySet());

		for (Map.Entry<String, List<EObject>> entry : sourceByType.entrySet()) {
			String typeName = entry.getKey();
			int expected = entry.getValue().size();
			int actual = readBackByType.getOrDefault(typeName, List.of()).size();
			assertThat(actual)
					.as("Count for type " + typeName)
					.isEqualTo(expected);
		}

		// 5. Deep attribute comparison: match by id, compare all set EAttribute values
		EStructuralFeature idFeature = domainPackage.getEClassifier("GeoFeature") != null
				? ((EClass) domainPackage.getEClassifier("GeoFeature")).getEStructuralFeature("id")
				: null;

		if (idFeature != null) {
			// Build lookup: id → read-back object
			Map<Object, EObject> readBackById = new HashMap<>();
			for (EObject eo : readBackObjects) {
				Object id = eo.eGet(idFeature);
				if (id != null) {
					readBackById.put(id, eo);
				}
			}

			int comparedObjects = 0;
			int comparedAttributes = 0;
			List<String> mismatches = new ArrayList<>();

			for (EObject source : sourceObjects) {
				Object sourceId = source.eGet(idFeature);
				if (sourceId == null) {
					continue;
				}
				EObject readBack = readBackById.get(sourceId);
				assertThat(readBack)
						.as("Object with id=%s (%s) should exist in DB", sourceId, source.eClass().getName())
						.isNotNull();

				// Compare all EAttributes (skip containment references — those are separate entities)
				for (EStructuralFeature feature : source.eClass().getEAllStructuralFeatures()) {
					if (!(feature instanceof EAttribute)) {
						continue;
					}
					if (feature.isDerived() || feature.isTransient() || feature.isVolatile()) {
						continue;
					}
					Object sourceVal = source.eGet(feature);
					Object dbVal = readBack.eGet(feature);

					// Only compare non-default values that were actually set in the XMI
					if (sourceVal != null && !sourceVal.equals(feature.getDefaultValue())) {
						if (!sourceVal.equals(dbVal)) {
							mismatches.add(String.format("%s[id=%s].%s: expected=<%s> actual=<%s>",
									source.eClass().getName(), sourceId, feature.getName(), sourceVal, dbVal));
						}
						comparedAttributes++;
					}
				}
				comparedObjects++;
			}

			long t5a = System.currentTimeMillis();
			System.out.printf("[TIMING] Deep comparison: %d objects, %d attributes — %d ms, %d mismatches%n",
					comparedObjects, comparedAttributes, t5a - t4, mismatches.size());

			if (!mismatches.isEmpty()) {
				// Print first 20 mismatches for debugging
				mismatches.stream().limit(20).forEach(System.err::println);
			}
			assertThat(mismatches)
					.as("All attribute values should survive the DB roundtrip")
					.isEmpty();
		}

		// 6. Write read-back objects to XMI
		long t5 = System.currentTimeMillis();
		File outputFile = new File(tempDir, "roundtrip_output.xmi");
		Resource outputResource = rs.createResource(URI.createFileURI(outputFile.getAbsolutePath()));
		outputResource.getContents().addAll(readBackObjects);
		outputResource.save(null);

		long t6 = System.currentTimeMillis();
		assertThat(outputFile).exists();
		assertThat(outputFile.length()).isGreaterThan(0);
		System.out.printf("[TIMING] Write XMI (%d KB): %d ms%n", outputFile.length() / 1024, t6 - t5);

		// 7. Total count + summary
		assertThat(readBackObjects)
				.as("Total object count should match XMI source")
				.hasSameSizeAs(sourceObjects);

		long tEnd = System.currentTimeMillis();
		System.out.printf("[TIMING] Total roundtrip: %d ms%n", tEnd - t0);
	}
}
