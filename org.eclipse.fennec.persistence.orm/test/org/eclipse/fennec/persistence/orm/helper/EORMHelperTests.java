/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.orm.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author Mark Hoffmann
 * @since 13.12.2024
 */
public class EORMHelperTests {

	@Test
	public void testGetEClassifiers() {
		assertThat(EORMHelper.getEClassifier(null).isEmpty()).isTrue();
		PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		assertThat(EORMHelper.getEClassifier(pu).isEmpty()).isTrue();
		EntityMappings m1 = EORMFactory.eINSTANCE.createEntityMappings();
		pu.getEntityMappings().add(m1);
		assertThat(EORMHelper.getEClassifier(pu).isEmpty()).isTrue();
		Entity e1 = EORMFactory.eINSTANCE.createEntity();
		m1.getEntity().add(e1);
		assertThat(EORMHelper.getEClassifier(pu).isEmpty()).isTrue();
		e1.setClass(EcorePackage.Literals.ESTRUCTURAL_FEATURE);
		assertThat(EORMHelper.getEClassifier(pu).isEmpty()).isFalse();
		assertThat(EORMHelper.getEClassifier(pu).size()).isEqualTo(1);
		Entity e2 = EORMFactory.eINSTANCE.createEntity();
		m1.getEntity().add(e2);
		e2.setClass(EcorePackage.Literals.ECLASS);
		assertThat(EORMHelper.getEClassifier(pu).isEmpty()).isFalse();
		assertThat(EORMHelper.getEClassifier(pu).size()).isEqualTo(2);
		assertThat(EORMHelper.getEClassifier(pu).contains(EcorePackage.Literals.ECLASS)).isTrue();
		assertThat(EORMHelper.getEClassifier(pu).contains(EcorePackage.Literals.ESTRUCTURAL_FEATURE)).isTrue();
		Entity e3 = EORMFactory.eINSTANCE.createEntity();
		m1.getEntity().add(e3);
		e3.setClass(EcorePackage.Literals.ESTRUCTURAL_FEATURE);
		assertThat(EORMHelper.getEClassifier(pu).isEmpty()).isFalse();
		assertThat(EORMHelper.getEClassifier(pu).size()).isEqualTo(2);
		assertThat(EORMHelper.getEClassifier(pu).contains(EcorePackage.Literals.ECLASS)).isTrue();
		assertThat(EORMHelper.getEClassifier(pu).contains(EcorePackage.Literals.ESTRUCTURAL_FEATURE)).isTrue();
	}
	
	@Test
	public void testGetEClassifiersFromMapping() {
		assertThat(EORMHelper.getEClassifierFromMapping(null).isEmpty()).isTrue();
		EntityMappings mapping = EORMFactory.eINSTANCE.createEntityMappings();
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).isEmpty()).isTrue();
		Entity e1 = EORMFactory.eINSTANCE.createEntity();
		mapping.getEntity().add(e1);
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).isEmpty()).isTrue();
		e1.setClass(EcorePackage.Literals.ESTRUCTURAL_FEATURE);
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).isEmpty()).isFalse();
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).size()).isEqualTo(1);
		Entity e2 = EORMFactory.eINSTANCE.createEntity();
		mapping.getEntity().add(e2);
		e2.setClass(EcorePackage.Literals.ECLASS);
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).isEmpty()).isFalse();
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).size()).isEqualTo(2);
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).contains(EcorePackage.Literals.ECLASS)).isTrue();
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).contains(EcorePackage.Literals.ESTRUCTURAL_FEATURE)).isTrue();
		Entity e3 = EORMFactory.eINSTANCE.createEntity();
		mapping.getEntity().add(e3);
		e3.setClass(EcorePackage.Literals.ESTRUCTURAL_FEATURE);
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).isEmpty()).isFalse();
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).size()).isEqualTo(2);
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).contains(EcorePackage.Literals.ECLASS)).isTrue();
		assertThat(EORMHelper.getEClassifierFromMapping(mapping).contains(EcorePackage.Literals.ESTRUCTURAL_FEATURE)).isTrue();
	}
	
	@Test
	public void testGetEClassifiersFromEntities() {
		assertThat(EORMHelper.getEClassifierFromEntities(null).isEmpty()).isTrue();
		List<Entity> entities = new ArrayList<>();
		assertThat(EORMHelper.getEClassifierFromEntities(entities).isEmpty()).isTrue();
		Entity e1 = EORMFactory.eINSTANCE.createEntity();
		entities.add(e1);
		assertThat(EORMHelper.getEClassifierFromEntities(entities).isEmpty()).isTrue();
		e1.setClass(EcorePackage.Literals.ESTRUCTURAL_FEATURE);
		assertThat(EORMHelper.getEClassifierFromEntities(entities).isEmpty()).isFalse();
		assertThat(EORMHelper.getEClassifierFromEntities(entities).size()).isEqualTo(1);
		Entity e2 = EORMFactory.eINSTANCE.createEntity();
		entities.add(e2);
		e2.setClass(EcorePackage.Literals.ECLASS);
		assertThat(EORMHelper.getEClassifierFromEntities(entities).isEmpty()).isFalse();
		assertThat(EORMHelper.getEClassifierFromEntities(entities).size()).isEqualTo(2);
		assertThat(EORMHelper.getEClassifierFromEntities(entities).contains(EcorePackage.Literals.ECLASS)).isTrue();
		assertThat(EORMHelper.getEClassifierFromEntities(entities).contains(EcorePackage.Literals.ESTRUCTURAL_FEATURE)).isTrue();
		Entity e3 = EORMFactory.eINSTANCE.createEntity();
		entities.add(e3);
		e3.setClass(EcorePackage.Literals.ESTRUCTURAL_FEATURE);
		assertThat(EORMHelper.getEClassifierFromEntities(entities).isEmpty()).isFalse();
		assertThat(EORMHelper.getEClassifierFromEntities(entities).size()).isEqualTo(2);
		assertThat(EORMHelper.getEClassifierFromEntities(entities).contains(EcorePackage.Literals.ECLASS)).isTrue();
		assertThat(EORMHelper.getEClassifierFromEntities(entities).contains(EcorePackage.Literals.ESTRUCTURAL_FEATURE)).isTrue();
	}
	
	@Test
	public void testGetEPackages() {
		assertThat(EORMHelper.getEPackages(null).isEmpty()).isTrue();
		List<EClassifier> eClassifier = new ArrayList<>();
		assertThat(EORMHelper.getEPackages(eClassifier).isEmpty()).isTrue();
		eClassifier.add(EcorePackage.Literals.ESTRUCTURAL_FEATURE);
		assertThat(EORMHelper.getEPackages(eClassifier).isEmpty()).isFalse();
		assertThat(EORMHelper.getEPackages(eClassifier).size()).isEqualTo(1);
		assertThat(EORMHelper.getEPackages(eClassifier).contains(EcorePackage.eINSTANCE)).isTrue();
		eClassifier.add(EcorePackage.Literals.ECLASS);
		assertThat(EORMHelper.getEPackages(eClassifier).isEmpty()).isFalse();
		assertThat(EORMHelper.getEPackages(eClassifier).size()).isEqualTo(1);
		assertThat(EORMHelper.getEPackages(eClassifier).contains(EcorePackage.eINSTANCE)).isTrue();
		eClassifier.add(EORMPackage.eINSTANCE.getBasic());
		assertThat(EORMHelper.getEPackages(eClassifier).isEmpty()).isFalse();
		assertThat(EORMHelper.getEPackages(eClassifier).size()).isEqualTo(2);
		assertThat(EORMHelper.getEPackages(eClassifier).contains(EcorePackage.eINSTANCE)).isTrue();
		assertThat(EORMHelper.getEPackages(eClassifier).contains(EORMPackage.eINSTANCE)).isTrue();
	}
	
	@Test
	public void testFilterEClasss() {
		assertThat(EORMHelper.filterEClasses(null).isEmpty()).isTrue();
		List<EClassifier> eClassifier = new ArrayList<>();
		assertThat(EORMHelper.filterEClasses(eClassifier).isEmpty()).isTrue();
		eClassifier.add(EcorePackage.Literals.ECLASS);
		assertThat(EORMHelper.filterEClasses(eClassifier).isEmpty()).isFalse();
		assertThat(EORMHelper.filterEClasses(eClassifier).size()).isEqualTo(1);
		assertThat(EORMHelper.filterEClasses(eClassifier).contains(EcorePackage.Literals.ECLASS)).isTrue();
		assertThat(EORMHelper.filterEClasses(eClassifier).isEmpty()).isFalse();
		eClassifier.add(EcorePackage.Literals.EREFERENCE);
		assertThat(EORMHelper.filterEClasses(eClassifier).size()).isEqualTo(2);
		assertThat(EORMHelper.filterEClasses(eClassifier).contains(EcorePackage.Literals.EREFERENCE)).isTrue();
		assertThat(EORMHelper.filterEClasses(eClassifier).contains(EcorePackage.Literals.ECLASS)).isTrue();
		// add a non Eclass
		EDataType d1 = EcoreFactory.eINSTANCE.createEDataType();
		d1.setName("Test");
		eClassifier.add(d1);
		assertThat(EORMHelper.filterEClasses(eClassifier).isEmpty()).isFalse();
		assertThat(EORMHelper.filterEClasses(eClassifier).size()).isEqualTo(2);
		assertThat(EORMHelper.filterEClasses(eClassifier).contains(EcorePackage.Literals.EREFERENCE)).isTrue();
		assertThat(EORMHelper.filterEClasses(eClassifier).contains(EcorePackage.Literals.ECLASS)).isTrue();
		// add an abstract EClass
		eClassifier.add(EcorePackage.Literals.ESTRUCTURAL_FEATURE);
		assertThat(EORMHelper.filterEClasses(eClassifier).isEmpty()).isFalse();
		assertThat(EORMHelper.filterEClasses(eClassifier).size()).isEqualTo(2);
		assertThat(EORMHelper.filterEClasses(eClassifier).contains(EcorePackage.Literals.EREFERENCE)).isTrue();
		assertThat(EORMHelper.filterEClasses(eClassifier).contains(EcorePackage.Literals.ECLASS)).isTrue();
	}
}
