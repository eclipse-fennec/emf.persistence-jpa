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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * Result of one reverse-engineering run: the produced packages, the schema facts behind
 * them (issue #295) and every diagnostic collected on the way (problems and silent
 * corrections such as type fallbacks). Follows the Fennec result pattern of
 * {@code MappingResult}: diagnostics use the {@code source}
 * {@link DatabaseEcoreParser#DIAGNOSTIC_SOURCE}, the affected model element is the first
 * entry of {@link Diagnostic#getData()}.
 *
 * @param ePackages the produced packages (one per schema), possibly empty, never {@code null}
 * @param diagnostics the collected diagnostics in report order, never {@code null}
 * @param tables the facts of every table and view that became a class, in package order
 * @param junctions the junction tables that became ManyToMany pairs
 * @author Mark Hoffmann
 * @since 04.08.2026
 */
public record ParseResult(List<EPackage> ePackages, List<Diagnostic> diagnostics, List<TableFacts> tables,
		List<JunctionFacts> junctions) {

	public ParseResult {
		ePackages = List.copyOf(ePackages);
		diagnostics = List.copyOf(diagnostics);
		tables = List.copyOf(tables);
		junctions = List.copyOf(junctions);
	}

	/**
	 * Creates a result without schema facts.
	 *
	 * @param ePackages the produced packages
	 * @param diagnostics the collected diagnostics
	 */
	public ParseResult(List<EPackage> ePackages, List<Diagnostic> diagnostics) {
		this(ePackages, diagnostics, List.of(), List.of());
	}

	/**
	 * Returns the facts of the table a class was produced from.
	 *
	 * @param eClass the produced class
	 * @return the table facts, or {@code null} if the class was not produced from a table
	 */
	public TableFacts table(EClass eClass) {
		return tables.stream().filter(t -> t.eClass() == eClass).findFirst().orElse(null);
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
