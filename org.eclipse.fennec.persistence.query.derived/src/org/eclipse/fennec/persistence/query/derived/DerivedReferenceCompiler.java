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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.expression.ocl.OclToExpr;
import org.eclipse.fennec.m2x.model.ocl.IteratorExp;
import org.eclipse.fennec.m2x.model.ocl.OclExpression;
import org.eclipse.fennec.m2x.model.ocl.PropertyCallExp;
import org.eclipse.fennec.m2x.model.ocl.VariableExp;
import org.eclipse.fennec.m2x.ocl.api.OclExpressionParser;
import org.eclipse.fennec.m2x.ocl.api.OclParseException;
import org.eclipse.fennec.m2x.ocl.parser.OclParserSupport;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Variable;
import org.eclipse.fennec.model.expression.VariableRef;
import org.eclipse.fennec.persistence.query.QueryException;

/**
 * Turns a {@code derivation} annotation into a {@link DerivedPlan}, once per feature at
 * delegate creation (concept §5): parse the OCL text, recognize the v1 pushdown shape
 * {@code self.<manyReference>->select(v | <predicate>)}, bridge the select body via
 * {@link OclToExpr}. A broken annotation fails loudly here (P8); a valid-but-unbridgeable
 * derivation degrades to a {@link DerivedPlan.Memory} plan.
 *
 * @author Juergen Albert
 * @since 28.07.2026
 */
public final class DerivedReferenceCompiler {

	/** The m2x OCL delegate URI — annotation source of the derivation (concept P1). */
	public static final String DELEGATE_URI = "http://www.eclipse.org/fennec/m2x/ocl/1.0";

	/** The annotation detail key carrying the OCL text. */
	public static final String DETAIL_DERIVATION = "derivation";

	/** The fallback detail key, mirroring the m2x delegate's lookup order. */
	public static final String DETAIL_INITIAL = "initial";

	private final OclExpressionParser parser;

	/** Creates a compiler with a plain (non-OSGi) parser instance. */
	public DerivedReferenceCompiler() {
		this(new OclParserSupport());
	}

	/**
	 * @param parser the OCL parser to use, must not be {@code null}
	 */
	public DerivedReferenceCompiler(OclExpressionParser parser) {
		this.parser = Objects.requireNonNull(parser, "parser must not be null");
	}

	/**
	 * Compiles the feature's derivation annotation.
	 *
	 * @param feature the annotated derived feature
	 * @return the prepared plan
	 * @throws QueryException if the annotation is missing or its OCL does not parse
	 */
	public DerivedPlan compile(EStructuralFeature feature) throws QueryException {
		String derivation = annotationText(feature);
		OclExpression ast;
		try {
			ast = parser.parse(derivation, feature.getEContainingClass());
		} catch (OclParseException e) {
			throw new QueryException("Derivation of '" + featureLabel(feature) + "' does not parse: "
					+ e.getMessage(), e);
		}
		return recognize(feature, ast);
	}

	private String annotationText(EStructuralFeature feature) throws QueryException {
		EAnnotation annotation = feature.getEAnnotation(DELEGATE_URI);
		String text = annotation == null ? null : annotation.getDetails().get(DETAIL_DERIVATION);
		if (text == null && annotation != null) {
			text = annotation.getDetails().get(DETAIL_INITIAL);
		}
		if (text == null || text.isBlank()) {
			throw new QueryException("Feature '" + featureLabel(feature) + "' carries no '" + DETAIL_DERIVATION
					+ "' annotation under " + DELEGATE_URI);
		}
		return text;
	}

	private DerivedPlan recognize(EStructuralFeature feature, OclExpression ast) {
		if (!(feature instanceof EReference derived) || !feature.isMany()) {
			return new DerivedPlan.Memory("only many-valued references push down");
		}
		if (!(ast instanceof IteratorExp select) || !"select".equals(select.getName())) {
			return new DerivedPlan.Memory("derivation is no select iterator");
		}
		if (select.getOwnedIterators().size() != 1) {
			return new DerivedPlan.Memory("select uses " + select.getOwnedIterators().size()
					+ " iterator variables");
		}
		if (!(select.getOwnedSource() instanceof PropertyCallExp sourceCall)
				|| !(sourceCall.getReferredProperty() instanceof EReference base)
				|| !base.isMany()) {
			return new DerivedPlan.Memory("select source is no many-valued reference navigation");
		}
		if (!rootedInSelf(sourceCall.getOwnedSource())) {
			return new DerivedPlan.Memory("select source does not navigate directly from self");
		}
		if (derived.getEReferenceType() != base.getEReferenceType()
				&& !derived.getEReferenceType().isSuperTypeOf(base.getEReferenceType())) {
			return new DerivedPlan.Memory("the feature type does not accept the select source's elements");
		}
		Variable iterator = ExpressionFactory.eINSTANCE.createVariable();
		iterator.setName(select.getOwnedIterators().get(0).getName());
		Expression predicate;
		try {
			predicate = OclToExpr.toExpr(select.getOwnedBody(),
					Map.of(select.getOwnedIterators().get(0), iterator));
			rootify(predicate, iterator);
		} catch (QueryException e) {
			return new DerivedPlan.Memory(e.getMessage());
		}
		return new DerivedPlan.Pushdown(base, predicate);
	}

	private static boolean rootedInSelf(OclExpression source) {
		if (source == null) {
			return true; // implicit self
		}
		return source instanceof VariableExp variable
				&& variable.getReferredVariable() != null
				&& "self".equals(variable.getReferredVariable().getName());
	}

	/**
	 * The bridged body addresses everything relative to the iterator variable; in the
	 * pushdown query the iterated element IS the query root — paths based on the
	 * iterator become root paths. A remaining reference to the element itself (e.g.
	 * {@code v = other}) is not expressible and degrades the plan.
	 */
	private static void rootify(Expression predicate, Variable iterator) throws QueryException {
		List<EObject> all = new ArrayList<>();
		all.add(predicate);
		predicate.eAllContents().forEachRemaining(all::add);
		for (EObject object : all) {
			if (object instanceof PropertyPath path && path.getBase() == iterator) {
				path.setBase(null);
			} else if (object instanceof VariableRef ref && ref.getVariable() == iterator) {
				throw new QueryException("the iterator variable itself is not addressable in a pushdown predicate");
			}
		}
	}

	private static String featureLabel(EStructuralFeature feature) {
		return (feature.getEContainingClass() == null ? "?" : feature.getEContainingClass().getName())
				+ "." + feature.getName();
	}
}
