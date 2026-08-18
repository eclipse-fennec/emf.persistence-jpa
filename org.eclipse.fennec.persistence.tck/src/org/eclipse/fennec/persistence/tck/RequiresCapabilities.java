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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.capabilities.StoreFeature;

/**
 * The capabilities a TCK case exercises (issue #160, contract §10 step 3).
 * <p>
 * {@link CapabilityGate} compares the listed features against the binding's
 * {@link AbstractPersistenceTCK#declaredCapabilities() declaration} and skips the case when
 * any of them is undeclared — the skip reason names the undeclared features, so a skip in the
 * report is a capability statement, never a silent hole. The refusal direction of contract
 * §2B is not this annotation's job: {@code undeclaredFeaturesAreRefusedWithADiagnostic}
 * asserts per undeclared feature that the backend refuses with a Diagnostic.
 * <p>
 * Cases without this annotation are conformance core: they run on every backend,
 * unconditionally.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequiresCapabilities {

	/** Query vocabulary the case needs — checked against {@link PersistenceCapabilities#query()}. */
	QueryFeature[] query() default {};

	/** Command verbs the case needs — checked against {@link PersistenceCapabilities#command()}. */
	CommandFeature[] command() default {};

	/** Store features the case needs — checked against {@link PersistenceCapabilities#store()}. */
	StoreFeature[] store() default {};
}
