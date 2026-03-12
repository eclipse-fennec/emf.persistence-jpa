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
package org.eclipse.fennec.persistence.orm.helper;

import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.persistence.eorm.Attributes;
import org.eclipse.fennec.persistence.eorm.Base;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.EFeatureObject;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.ElementCollection;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.Table;
import org.eclipse.fennec.persistence.eorm.util.EORMResourceFactoryImpl;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.orm.processor.EntityProcessor;
import org.eclipse.fennec.persistence.orm.processor.MappingProcessor;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author Mark Hoffmann
 * @since 13.12.2024
 */
public class EntityMapperTests {

	@Test
	public void testCreateEntity() {

		assertThrows(NullPointerException.class, ()->EntityProcessor.create(null));
		// test abstract EClass
		EntityProcessor processor = EntityProcessor.create(EcorePackage.Literals.ESTRUCTURAL_FEATURE);
		processor.process();
		assertFalse(processor.isProcessed());

		processor = EntityProcessor.create(EcorePackage.Literals.EREFERENCE);
		processor.process();
		Entity e = processor.getTarget();
		assertNotNull(e);
		assertNotNull(e.getTable());
		Table t = e.getTable();
		assertEquals(EcorePackage.Literals.EREFERENCE, e.getClass_());
		assertEquals("EReference", e.getName());
		assertEquals("EREFERENCE", t.getName());
		//		assertEquals("ECORE", t.getSchema());
		assertNull(e.getDescription());

		EPackage p = EcoreFactory.eINSTANCE.createEPackage();
		p.setName("Bar");
		EClass c = EcoreFactory.eINSTANCE.createEClass();
		c.setName("Foo");
		p.getEClassifiers().add(c);
		EAnnotation a = EcoreFactory.eINSTANCE.createEAnnotation();
		a.setSource("http://www.eclipse.org/emf/2002/GenModel");
		a.getDetails().put("documentation", "foo-bar");
		c.getEAnnotations().add(a);

		processor = EntityProcessor.create(c);
		processor.process();
		e = processor.getTarget();
		assertNotNull(e);
		assertNotNull(e.getTable());
		t = e.getTable();
		assertEquals(c, e.getClass_());
		assertEquals("Foo", e.getName());
		assertEquals("FOO", t.getName());
		//		assertEquals("BAR", t.getSchema());
		// Does only work with
		assertNotNull(e.getDescription());
	}

	@Test
	public void testCreateEClassifierMappings() {
		EntityMapper mapper = new EntityMapper();
		EntityMappings mapping = mapper.createMappings(null);
		assertNotNull(mapping);
		assertTrue(mapping.getEntity().isEmpty());
		List<EClassifier> eClassifiers = new ArrayList<>();
		mapping = mapper.createMappings(eClassifiers);
		assertNotNull(mapping);
		assertTrue(mapping.getEntity().isEmpty());
		EPackage p = EcoreFactory.eINSTANCE.createEPackage();
		p.setName("Bar");
		EClass c1 = EcoreFactory.eINSTANCE.createEClass();
		c1.setName("Foo");
		p.getEClassifiers().add(c1);
		eClassifiers.add(c1);
		mapping = mapper.createMappings(eClassifiers);
		assertNotNull(mapping);
		assertEquals(1, mapping.getEntity().size());

		Entity e = mapping.getEntity().get(0);
		assertNotNull(e);
		assertNotNull(e.getTable());
		Table t = e.getTable();
		assertEquals(c1, e.getClass_());
		assertEquals("Foo", e.getName());
		assertEquals("FOO", t.getName());
		//		assertEquals("BAR", t.getSchema());

		EClass c2 = EcoreFactory.eINSTANCE.createEClass();
		c2.setAbstract(true);
		c2.setName("Foo2");
		p.getEClassifiers().add(c2);
		eClassifiers.add(c2);
		mapping = mapper.createMappings(eClassifiers);
		assertNotNull(mapping);
		assertEquals(1, mapping.getEntity().size());

		EClass c3 = EcoreFactory.eINSTANCE.createEClass();
		c3.setName("Foo3");
		p.getEClassifiers().add(c3);
		eClassifiers.add(c3);
		mapping = mapper.createMappings(eClassifiers);
		assertNotNull(mapping);
		assertEquals(2, mapping.getEntity().size());

		e = mapping.getEntity().get(1);
		assertNotNull(e);
		assertNotNull(e.getTable());
		t = e.getTable();
		assertEquals(c3, e.getClass_());
		assertEquals("Foo3", e.getName());
		assertEquals("FOO3", t.getName());
		//		assertEquals("BAR", t.getSchema());
	}

	@Test
	public void testCreateEPackageMappings() {
		EntityMapper mapper = new EntityMapper();
		EntityMappings mapping = mapper.createMappingsFromEPackage(null);
		assertNotNull(mapping);
		assertTrue(mapping.getEntity().isEmpty());
		List<EClassifier> eClassifiers = new ArrayList<>();
		EPackage p = EcoreFactory.eINSTANCE.createEPackage();
		p.setName("Bar");
		EClass c1 = EcoreFactory.eINSTANCE.createEClass();
		c1.setName("Foo");
		p.getEClassifiers().add(c1);
		eClassifiers.add(c1);
		mapping = mapper.createMappingsFromEPackage(p);
		assertNotNull(mapping);
		assertEquals(1, mapping.getEntity().size());

		Entity e = mapping.getEntity().get(0);
		assertNotNull(e);
		assertNotNull(e.getTable());
		Table t = e.getTable();
		assertEquals(c1, e.getClass_());
		assertEquals("Foo", e.getName());
		assertEquals("FOO", t.getName());
		//		assertEquals("BAR", t.getSchema());

		EClass c2 = EcoreFactory.eINSTANCE.createEClass();
		c2.setAbstract(true);
		c2.setName("Foo2");
		p.getEClassifiers().add(c2);
		eClassifiers.add(c2);
		mapping = mapper.createMappingsFromEPackage(p);
		assertNotNull(mapping);
		assertEquals(1, mapping.getEntity().size());

		EClass c3 = EcoreFactory.eINSTANCE.createEClass();
		c3.setName("Foo3");
		p.getEClassifiers().add(c3);
		eClassifiers.add(c3);
		mapping = mapper.createMappingsFromEPackage(p);
		assertNotNull(mapping);
		assertEquals(2, mapping.getEntity().size());

		e = mapping.getEntity().get(1);
		assertNotNull(e);
		assertNotNull(e.getTable());
		t = e.getTable();
		assertEquals(c3, e.getClass_());
		assertEquals("Foo3", e.getName());
		assertEquals("FOO3", t.getName());
		//		assertEquals("BAR", t.getSchema());
	}

	@Test
	public void testCreateBasicsSimple() {

		assertThrows(NullPointerException.class, ()->EntityProcessor.create(null));
		// test abstract EClass
		EntityProcessor processor = EntityProcessor.create(EcorePackage.Literals.ESTRUCTURAL_FEATURE);
		processor.process();
		assertFalse(processor.isProcessed());

		processor = EntityProcessor.create(EcorePackage.Literals.EREFERENCE);
		processor.process();
		Entity e = processor.getTarget();
		assertNotNull(e);

		assertNotNull(e.getAttributes());
		Attributes attrs = e.getAttributes();
		List<Basic> basics = attrs.getBasic();
		// done by MappingProcessor
		assertTrue(basics.isEmpty());

		MappingProcessor mappingProcessor = MappingProcessor.create(EcorePackage.Literals.EREFERENCE);
		mappingProcessor.process();
		EntityMappings em = mappingProcessor.getTarget();
		assertEquals(1, em.getEntity().size());
		e = em.getEntity().get(0);
		assertNotNull(e);
		assertNotNull(e.getAttributes());
		attrs = e.getAttributes();
		basics = attrs.getBasic();
		assertFalse(basics.isEmpty());
		assertEquals(2, basics.size());
		assertTrue(basics.stream().
				map(Base::getAccessibleObject).
				filter(EFeatureObject.class::isInstance).
				map(EFeatureObject.class::cast).
				map(EFeatureObject::getFeature).
				filter(not(EAttribute.class::isInstance)).
				findAny().isEmpty());
	}

	@Test
	public void testCreateElementCollectionsSimple() {

		assertThrows(NullPointerException.class, ()->EntityProcessor.create(null));
		// test abstract EClass
		EntityProcessor processor = EntityProcessor.create(EcorePackage.Literals.ESTRUCTURAL_FEATURE);
		processor.process();
		assertFalse(processor.isProcessed());

		processor = EntityProcessor.create(EPersistencePackage.Literals.PERSISTENCE_UNIT);
		processor.process();
		Entity e = processor.getTarget();
		assertNotNull(e);

		assertNotNull(e.getAttributes());
		Attributes attrs = e.getAttributes();
		List<ElementCollection> ec = attrs.getElementCollection();
		// done by MappingProcessor
		assertTrue(ec.isEmpty());

		MappingProcessor mappingProcessor = MappingProcessor.create(EPersistencePackage.Literals.PERSISTENCE_UNIT);
		mappingProcessor.process();
		EntityMappings em = mappingProcessor.getTarget();
		assertEquals(1, em.getEntity().size());
		e = em.getEntity().get(0);
		assertNotNull(e);
		assertNotNull(e.getAttributes());
		attrs = e.getAttributes();
		ec = attrs.getElementCollection();

		assertFalse(ec.isEmpty());
		assertEquals(5, ec.size());
		assertTrue(ec.stream().
				map(Base::getAccessibleObject).
				filter(EFeatureObject.class::isInstance).
				map(EFeatureObject.class::cast).
				map(EFeatureObject::getFeature).
				filter(not(EAttribute.class::isInstance)).
				findAny().isEmpty());
	}

	//	@Test
	public void testSaveMapping() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		rs.getPackageRegistry().put(EORMPackage.eNS_URI, EORMPackage.eINSTANCE);
		rs.getPackageRegistry().put(EPersistencePackage.eNS_URI, EPersistencePackage.eINSTANCE);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("eorm", new EORMResourceFactoryImpl());

		Resource resource = rs.createResource(URI.createURI("/home/mark/tmp/test.eorm"));
		assertNotNull(resource);

		EntityMapper mapper = new EntityMapper();
		EntityMappings mapping = mapper.createMappings(List.of(EcorePackage.Literals.ECLASS, EcorePackage.Literals.EATTRIBUTE));
		resource.getContents().add(mapping);
		assertDoesNotThrow(()->resource.save(null));
	}

}
