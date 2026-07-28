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

import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicSettingDelegate;
import org.eclipse.fennec.persistence.query.QueryException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Decorator over the m2x OCL setting-delegate factory (concept P1): registered on the
 * same emf.osgi whiteboard under the same delegate URI with a higher service ranking.
 * Per feature it compiles the derivation once (fail fast on broken annotations, P8) and
 * decides:
 * <ul>
 * <li>pushdown-capable shape → a {@link QueryBackedSettingDelegate} (which still routes
 * object-specifically per access — XMI-loaded owners evaluate locally),</li>
 * <li>anything else → the delegate of the wrapped m2x factory, i.e. untouched standard
 * OCL semantics; without the engine such features are refused on access with a
 * diagnostic instead of answering silently.</li>
 * </ul>
 *
 * @author Juergen Albert
 * @since 28.07.2026
 */
@Component(service = EStructuralFeature.Internal.SettingDelegate.Factory.class, property = {
		"emf.configuratorName=" + DerivedReferenceCompiler.DELEGATE_URI,
		"emf.name=fennec-query-derived",
		"emf.configuratorType=SETTING_DELEGATE_FACTORY",
		"service.ranking:Integer=100" })
public class QueryBackedSettingDelegateFactory implements EStructuralFeature.Internal.SettingDelegate.Factory {

	/** Configuration property for the id-IN correlation limit (concept D4). */
	public static final String MAX_CORRELATION_IDS = "derived.maxCorrelationIds";

	/** Default id-IN correlation limit. */
	public static final int DEFAULT_MAX_CORRELATION_IDS = 10_000;

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, //
			policyOption = ReferencePolicyOption.GREEDY, target = "(emf.name=fennec-ocl)")
	private volatile EStructuralFeature.Internal.SettingDelegate.Factory memoryFactory;

	private final DerivedReferenceCompiler compiler;
	private volatile int maxCorrelationIds = DEFAULT_MAX_CORRELATION_IDS;

	/** DS and plain-Java default constructor. */
	public QueryBackedSettingDelegateFactory() {
		this(new DerivedReferenceCompiler(), null);
	}

	/**
	 * Programmatic constructor (tests, non-OSGi registration).
	 *
	 * @param compiler the compiler to use, must not be {@code null}
	 * @param memoryFactory the wrapped in-memory OCL factory; may be {@code null}
	 */
	public QueryBackedSettingDelegateFactory(DerivedReferenceCompiler compiler,
			EStructuralFeature.Internal.SettingDelegate.Factory memoryFactory) {
		this.compiler = Objects.requireNonNull(compiler, "compiler must not be null");
		this.memoryFactory = memoryFactory;
	}

	@Activate
	void activate(Map<String, Object> properties) {
		Object limit = properties.get(MAX_CORRELATION_IDS);
		if (limit instanceof Number number && number.intValue() > 0) {
			maxCorrelationIds = number.intValue();
		}
	}

	@Override
	public SettingDelegate createSettingDelegate(EStructuralFeature feature) {
		DerivedPlan plan;
		try {
			plan = compiler.compile(feature);
		} catch (QueryException e) {
			// broken annotation: fail loudly at model registration, not on first access (P8)
			throw new WrappedException(new RuntimeException(e.getMessage(), e));
		}
		if (plan instanceof DerivedPlan.Pushdown pushdown) {
			return new QueryBackedSettingDelegate(feature, pushdown, maxCorrelationIds);
		}
		EStructuralFeature.Internal.SettingDelegate.Factory memory = memoryFactory;
		if (memory != null) {
			return memory.createSettingDelegate(feature);
		}
		return new RefusingSettingDelegate(feature, ((DerivedPlan.Memory) plan).reason());
	}

	/** Serves a memory-only derivation in an engine-less runtime: refusal, not silence. */
	private static final class RefusingSettingDelegate extends BasicSettingDelegate.Stateless {

		private final String reason;

		private RefusingSettingDelegate(EStructuralFeature feature, String reason) {
			super(feature);
			this.reason = reason;
		}

		@Override
		protected Object get(InternalEObject owner, boolean resolve, boolean coreType) {
			throw new WrappedException(new RuntimeException("Derivation of '"
					+ eStructuralFeature.getName() + "' is not pushdown-capable (" + reason
					+ ") and no in-memory OCL delegate is available — install m2x.ocl.engine"));
		}

		@Override
		protected boolean isSet(InternalEObject owner) {
			return false;
		}
	}
}
