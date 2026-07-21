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
package org.eclipse.fennec.persistence.eclipselink.mappings;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeContext;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.FetchType;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.junit.jupiter.api.Test;

/**
 * Tests for the type-mapping diagnostics of {@link EMappingSupport#configureEMF}
 * (issue #19): inconsistent fetch/batch declarations are reported as EMF diagnostics
 * on the {@link EDynamicTypeContext}, not only logged — and are corrected as
 * documented.
 */
class EMappingSupportDiagnosticsTest {

	private EReference createReference(String name, boolean containment, boolean many) {
		EClass target = EcoreFactory.eINSTANCE.createEClass();
		target.setName("Target");
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(name);
		reference.setContainment(containment);
		reference.setEType(target);
		if (many) {
			reference.setUpperBound(-1);
		}
		return reference;
	}

	@Test
	void containmentDeclaredLazyIsReportedAndKeptEager() {
		EReference reference = createReference("children", true, false);
		OneToOne baseRef = EORMFactory.eINSTANCE.createOneToOne();
		baseRef.setFetch(FetchType.LAZY);
		EDynamicTypeContext context = new EDynamicTypeContext();

		EOneToOneMapping mapping = new EOneToOneMapping();
		EMappingSupport.configureEMF(mapping, reference, baseRef, null, context);

		assertThat(context.getDiagnostics()).hasSize(1);
		Diagnostic diagnostic = context.getDiagnostics().get(0);
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.WARNING);
		assertThat(diagnostic.getSource()).isEqualTo(EDynamicTypeContext.DIAGNOSTIC_SOURCE);
		assertThat(diagnostic.getMessage()).contains("containment").contains("children");
		assertThat(diagnostic.getData()).first().isEqualTo(reference);
		// the documented correction: containment stays eager
		assertThat(mapping.isLazy()).isFalse();
	}

	@Test
	void batchOnEagerReferenceIsReportedAsIneffective() {
		EReference reference = createReference("tags", false, true);
		OneToOne baseRef = EORMFactory.eINSTANCE.createOneToOne();
		baseRef.setFetch(FetchType.EAGER);
		baseRef.setBatch(true);
		EDynamicTypeContext context = new EDynamicTypeContext();

		EMappingSupport.configureEMF(new EOneToOneMapping(), reference, baseRef, null, context);

		assertThat(context.getDiagnostics()).hasSize(1);
		Diagnostic diagnostic = context.getDiagnostics().get(0);
		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.WARNING);
		assertThat(diagnostic.getMessage()).contains("batch").contains("no effect").contains("tags");
		assertThat(diagnostic.getData()).first().isEqualTo(reference);
	}

	@Test
	void batchOnContainmentIsReportedAsIneffective() {
		EReference reference = createReference("children", true, true);
		OneToOne baseRef = EORMFactory.eINSTANCE.createOneToOne();
		// explicit EAGER, as the generator emits for containments — only the batch
		// finding must be reported
		baseRef.setFetch(FetchType.EAGER);
		baseRef.setBatch(true);
		EDynamicTypeContext context = new EDynamicTypeContext();

		EMappingSupport.configureEMF(new EOneToOneMapping(), reference, baseRef, null, context);

		assertThat(context.getDiagnostics()).hasSize(1);
		assertThat(context.getDiagnostics().get(0).getMessage()).contains("batch").contains("no effect");
	}

	@Test
	void consistentDeclarationProducesNoDiagnostics() {
		EReference reference = createReference("owner", false, false);
		OneToOne baseRef = EORMFactory.eINSTANCE.createOneToOne();
		baseRef.setFetch(FetchType.EAGER);
		EDynamicTypeContext context = new EDynamicTypeContext();

		EMappingSupport.configureEMF(new EOneToOneMapping(), reference, baseRef, null, context);

		assertThat(context.getDiagnostics()).isEmpty();
	}
}
