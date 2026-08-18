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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.capabilities.StoreFeature;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * The declarative capability gate (issue #160, contract §10 step 3): evaluates a case's
 * {@link RequiresCapabilities} against the binding's
 * {@link AbstractPersistenceTCK#declaredCapabilities() declaration} and skips the case when a
 * required feature is undeclared.
 * <p>
 * The skip reason enumerates the undeclared features, which makes "skip means undeclared
 * capability" mechanical: the test report itself states what the backend refused to declare.
 * A case without the annotation is conformance core and always enabled — the gate cannot
 * spell "skip this core case".
 */
public class CapabilityGate implements ExecutionCondition {

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		Optional<RequiresCapabilities> annotation = context.getTestMethod()
				.map(method -> method.getAnnotation(RequiresCapabilities.class));
		if (annotation.isEmpty()) {
			return ConditionEvaluationResult.enabled("conformance core — not gateable");
		}
		RequiresCapabilities requires = annotation.get();
		PersistenceCapabilities declared = ((AbstractPersistenceTCK) context.getRequiredTestInstance())
				.declaredCapabilities();
		List<String> undeclared = new ArrayList<>();
		for (QueryFeature feature : requires.query()) {
			if (!declared.query().supports(feature)) {
				undeclared.add(feature.getName());
			}
		}
		for (CommandFeature feature : requires.command()) {
			if (!declared.command().supports(feature)) {
				undeclared.add(feature.getName());
			}
		}
		for (StoreFeature feature : requires.store()) {
			if (!declared.store().supports(feature)) {
				undeclared.add(feature.getName());
			}
		}
		if (undeclared.isEmpty()) {
			return ConditionEvaluationResult.enabled("all required capabilities declared");
		}
		return ConditionEvaluationResult.disabled(
				"undeclared capabilities: " + String.join(", ", undeclared));
	}
}
