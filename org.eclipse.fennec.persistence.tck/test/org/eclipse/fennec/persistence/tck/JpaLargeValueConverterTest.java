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
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * A converter declaring large values makes its attribute a Lob when the converter is found by
 * type — no eorm, no {@code Convert}, the converter registered after the defaults the way the
 * OSGi whiteboard adds it, the attribute on the root of a SINGLE_TABLE hierarchy, and the data
 * type at the classifier index Ecore uses for {@code EChar} (issue #324).
 * On PostgreSQL a plain String column is {@code VARCHAR(255)}, so a long value only round-trips
 * as a Lob.
 *
 * @author Mark Hoffmann
 * @since 24.09.2026
 */
class JpaLargeValueConverterTest {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	/** A value type no default converter claims, like {@code org.geojson.Geometry}. */
	public record Shape(String text) {
	}

	private static final String PU_NAME = "largevalue";

	private EPackage ePackage;
	private EClass assetClass;
	private EClass lawnClass;
	private EAttribute idAttr;
	private EAttribute geometryAttr;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		ePackage = ecore.createEPackage();
		ePackage.setName("largevalue");
		ePackage.setNsURI("urn:largevalue:test/1.0");
		ePackage.setNsPrefix("largevalue");

		// the geometry type sits at classifier index 27, as GeoJsonGeometry does in the bath
		// model of emf.ogc.features — the index of EcorePackage.ECHAR, which the default
		// converter used to claim by id alone (issue #324)
		for (int i = 0; i < EcorePackage.ECHAR; i++) {
			EDataType filler = ecore.createEDataType();
			filler.setName("Filler" + i);
			filler.setInstanceClassName("java.lang.String");
			ePackage.getEClassifiers().add(filler);
		}
		EDataType shapeType = ecore.createEDataType();
		shapeType.setName("ShapeGeometry");
		shapeType.setInstanceClass(Shape.class);
		ePackage.getEClassifiers().add(shapeType);
		assertThat(shapeType.getClassifierID()).isEqualTo(EcorePackage.ECHAR);

		assetClass = ecore.createEClass();
		assetClass.setName("LvAsset");
		assetClass.setAbstract(true);
		ePackage.getEClassifiers().add(assetClass);
		idAttr = attribute(assetClass, "id", EcorePackage.Literals.ESTRING);
		idAttr.setID(true);
		geometryAttr = attribute(assetClass, "geometry", shapeType);

		lawnClass = ecore.createEClass();
		lawnClass.setName("LvLawn");
		lawnClass.getESuperTypes().add(assetClass);
		ePackage.getEClassifiers().add(lawnClass);
		EClass poolClass = ecore.createEClass();
		poolClass.setName("LvPool");
		poolClass.getESuperTypes().add(assetClass);
		ePackage.getEClassifiers().add(poolClass);

		EntityMappings mappings = new EntityMapper()
				.createMappings(new ArrayList<EClassifier>(List.of(assetClass, lawnClass, poolClass)));
		emf = JpaTckSupport.bootstrap(PU_NAME, mappings, JpaTestSupport.jdbcProperties(PU_NAME),
				"create-or-extend-tables", new ShapeConverterService());
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			try (EntityManager em = emf.createEntityManager()) {
				em.getTransaction().begin();
				em.createNativeQuery("DELETE FROM LVASSET").executeUpdate();
				em.getTransaction().commit();
			}
			emf.close();
			emf = null;
		}
	}

	@Test
	void aLongValueOfALargeValueConverterRoundTrips() throws Exception {
		String text = "{\"type\":\"Polygon\",\"coordinates\":[[" + "[11.2805,50.9271],".repeat(400) + "]]}";
		EObject lawn = EcoreUtil.create(lawnClass);
		lawn.eSet(idAttr, "lawn-1");
		lawn.eSet(geometryAttr, new Shape(text));
		Resource resource = resourceSet().createResource(uri("LvLawn"));
		resource.getContents().add(lawn);

		resource.save(null);

		emf.getCache().evictAll();
		EObject reloaded = resourceSet().getEObject(URI.createURI(uri("LvLawn") + "#lawn-1"), true);
		assertThat(reloaded).isNotNull();
		assertThat(reloaded.eGet(geometryAttr)).isEqualTo(new Shape(text));
	}

	private static EAttribute attribute(EClass owner, String name, EDataType type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		owner.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	/** The defaults first, then the registered converter — the order the whiteboard produces. */
	private static final class ShapeConverterService extends DefaultConverterService {
		ShapeConverterService() {
			converters.add(new TypeConverter() {
				@Override
				public String getName() {
					return "shape";
				}

				@Override
				public boolean isConverterForType(EClassifier eDataType) {
					return eDataType instanceof EDataType dataType && dataType.getInstanceClass() == Shape.class;
				}

				@Override
				public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
					return emfValue instanceof Shape shape ? shape.text() : emfValue;
				}

				@Override
				public Object convertValueToEMF(EClassifier eDataType, Object value) {
					return value == null || value instanceof Shape ? value : new Shape(value.toString());
				}

				@Override
				public boolean isLargeValue(EClassifier eDataType) {
					return isConverterForType(eDataType);
				}
			});
		}
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
