///**
// * Copyright (c) 2012 - 2025 Data In Motion and others.
// * All rights reserved. 
// * 
// * This program and the accompanying materials are made
// * available under the terms of the Eclipse Public License 2.0
// * which is available at https://www.eclipse.org/legal/epl-2.0/
// *
// * SPDX-License-Identifier: EPL-2.0
// * 
// * Contributors:
// *     Data In Motion - initial API and implementation
// */
//package org.eclipse.fennec.persistence.test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.sql.SQLException;
//
//import org.eclipse.emf.common.util.EList;
//import org.eclipse.emf.ecore.EAttribute;
//import org.eclipse.emf.ecore.EClass;
//import org.eclipse.emf.ecore.EClassifier;
//import org.eclipse.emf.ecore.EPackage;
//import org.eclipse.emf.ecore.EReference;
//import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
//import org.eclipse.fennec.persistence.ecore.DatabaseEcoreParser;
//import org.eclipse.fennec.persistence.test.annotations.TestAnnotations.CitizenEPersistenceSetup;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.osgi.service.cm.annotations.RequireConfigurationAdmin;
//import org.osgi.test.common.annotation.InjectService;
//import org.osgi.test.common.annotation.Property;
//import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
//import org.osgi.test.junit5.cm.ConfigurationExtension;
//import org.osgi.test.junit5.context.BundleContextExtension;
//import org.osgi.test.junit5.service.ServiceExtension;
//
///**
// *
// */
//@RequireConfigurationAdmin
//@RequireEMF
//@ExtendWith(ServiceExtension.class)
//@ExtendWith(ConfigurationExtension.class)
//@ExtendWith(BundleContextExtension.class)
//class DatabaseEcoreParserTest {
//
////	@Test
//	@CitizenEPersistenceSetup
//	@WithFactoryConfiguration(factoryPid = DatabaseEcoreParser.PID, name = "P1", location = "?", properties = { //
//			@Property(key = "packageName", value = "citizen"), //
//			@Property(key = "uriPrefix", value = "http://model.data.jena.de/mdo"), //
//			@Property(key = "version", value = "1.1") })
//	void testH2(@InjectService(timeout = 500) DatabaseEcoreParser parser) throws SQLException {
//		EPackage ePackage = parser.parse();
//		assertThat(ePackage).isNotNull() //
//				.extracting(EPackage::getName, EPackage::getNsPrefix, EPackage::getNsURI) //
//				.contains("citizen", "citizen", "http://model.data.jena.de/mdo/citizen/1.1");
//		EList<EClassifier> eClassifiers = ePackage.getEClassifiers();
//		assertThat(eClassifiers).extracting(EClassifier::getName) //
//				.contains("AGEGROUPS", "EINWOHNER", "GENDER", "PLRAUM", "STATBEZ", "TOWN", "YYEAR");
//
//		EClass cPlraum = (EClass) ePackage.getEClassifier("PLRAUM");
//		EList<EAttribute> attributes = cPlraum.getEAllAttributes();
//		assertThat(attributes).extracting(EAttribute::getName) //
//				.contains("GID", "PLRAUM", "THE_GEOM", "PLRAUM_NR", "UUID", "GEOJSON");
//
//		EList<EReference> references = cPlraum.getEReferences();
//		assertThat(references).hasSize(1)
//				.element(0) 
//				.extracting(r -> r.getName(), r -> r.getEReferenceType()
//						.getName()) //
//				.contains("TOWNID", "TOWN");
//
//	}
//}
