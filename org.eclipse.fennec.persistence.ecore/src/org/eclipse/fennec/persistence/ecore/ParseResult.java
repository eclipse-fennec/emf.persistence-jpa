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
package org.eclipse.fennec.persistence.ecore;

import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EPackage;

/**
 * Result of one reverse-engineering run: the produced packages plus every diagnostic
 * collected on the way (problems and silent corrections such as type fallbacks).
 * Follows the Fennec result pattern of {@code MappingResult}: diagnostics use the
 * {@code source} {@link DatabaseEcoreParser#DIAGNOSTIC_SOURCE}, the affected model
 * element is the first entry of {@link Diagnostic#getData()}.
 *
 * @param ePackages the produced packages (one per schema), possibly empty, never {@code null}
 * @param diagnostics the collected diagnostics in report order, never {@code null}
 * @author Mark Hoffmann
 * @since 04.08.2026
 */
public record ParseResult(List<EPackage> ePackages, List<Diagnostic> diagnostics) {

	public ParseResult {
		ePackages = List.copyOf(ePackages);
		diagnostics = List.copyOf(diagnostics);
	}

	/**
	 * Returns {@code true} if no diagnostic of severity {@link Diagnostic#ERROR} or
	 * higher was reported.
	 */
	public boolean isSuccess() {
		return getSeverity() < Diagnostic.ERROR;
	}

	/**
	 * Returns the highest reported severity, {@link Diagnostic#OK} if none.
	 */
	public int getSeverity() {
		return diagnostics.stream().mapToInt(Diagnostic::getSeverity).max().orElse(Diagnostic.OK);
	}
}
