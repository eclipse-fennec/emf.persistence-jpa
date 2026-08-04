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
 *   Data In Motion - initial API and implementation
 ********************************************************************/
package org.eclipse.fennec.persistence.mongo.query;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.TypeCheck;
import org.eclipse.fennec.persistence.query.QueryException;

import com.mongodb.client.model.Filters;

/**
 * Translates type predicates against the codec type discriminator (issue #88).
 * <p>
 * The codec writes the discriminator into every document — <b>which</b> fields and
 * values is decided by the codec configuration (resolvable per EPackage/EClass, e.g.
 * via EAnnotations), so the translation resolves the <b>effective</b> config through
 * the same {@link ConfigurationResolver} the writer uses and mirrors its value
 * rendering:
 * <ul>
 * <li>with serialized supertypes ({@code superTypeSerialize=true}) a kind-of test is a
 * direct match on the type field or the supertype array — no subtype closure, robust
 * against subtypes added after the query was written;</li>
 * <li>without, the type and its concrete subtypes within its EPackage form the match
 * set, rendered per the effective {@code typeStrategy};</li>
 * <li>a configuration without a stored discriminator ({@code typeInclude=false} or
 * strategy {@code NONE}) is refused with a diagnostic.</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 04.08.2026
 */
final class MongoTypePredicates {

	private MongoTypePredicates() {
	}

	/** Kind-of type test (issue #80 semantics): unset source tests the query root. */
	static Bson typeCheck(TypeCheck check, ConfigurationResolver resolver) throws QueryException {
		String prefix = check.getSource() == null ? ""
				: MongoFieldNames.render(check.getSource()) + ".";
		return typeGuard(check.getType(), prefix, resolver);
	}

	/** Treat guard for a castBase path: the query root must be an instance of the subtype. */
	static Bson castGuard(PropertyPath path, ConfigurationResolver resolver) throws QueryException {
		return typeGuard(path.getCastBase(), "", resolver);
	}

	private static Bson typeGuard(EClass type, String prefix, ConfigurationResolver resolver)
			throws QueryException {
		DiagnosticCollector diagnostics = new DiagnosticCollector();
		TypeConfig typeConfig = resolver.resolveTypeConfig(type, diagnostics);
		TypeStrategy strategy = typeConfig.getStrategy() == null ? TypeStrategy.URI
				: typeConfig.getStrategy();
		if (!typeConfig.isInclude() || strategy == TypeStrategy.NONE) {
			throw new QueryException("Type predicates need a stored type discriminator, but the codec"
					+ " configuration for " + type.getName()
					+ " disables it (typeInclude=false or typeStrategy NONE)");
		}
		String typeField = prefix + typeConfig.getTypeKey();
		SuperTypeConfig superTypes = resolver.resolveSuperTypeConfig(type, diagnostics);
		if (superTypes.isSerialize() && strategy != TypeStrategy.SCHEMA_AND_TYPE) {
			// the documents carry the supertype list — a direct match, no closure;
			// Mongo's eq matches array containment on the supertype field
			String superField = prefix + superTypes.getEffectiveSuperTypeKey(superTypes.getFormat());
			String value = typeValue(type, strategy, resolver, diagnostics);
			return Filters.or(Filters.eq(typeField, value), Filters.eq(superField, value));
		}
		List<EClass> concrete = new ArrayList<>();
		for (EClassifier classifier : type.getEPackage().getEClassifiers()) {
			if (classifier instanceof EClass candidate && !candidate.isAbstract()
					&& type.isSuperTypeOf(candidate)) {
				concrete.add(candidate);
			}
		}
		if (concrete.isEmpty()) {
			// abstract-only hierarchy can never match: field in empty set is always false
			return Filters.in(typeField, List.of());
		}
		if (strategy == TypeStrategy.SCHEMA_AND_TYPE) {
			List<String> names = concrete.stream().map(EClass::getName).toList();
			return Filters.and(
					Filters.eq(prefix + typeConfig.getSchemaKey(), type.getEPackage().getNsURI()),
					Filters.in(typeField, names));
		}
		List<String> values = new ArrayList<>(concrete.size());
		for (EClass candidate : concrete) {
			values.add(typeValue(candidate, strategy, resolver, diagnostics));
		}
		return values.size() == 1 ? Filters.eq(typeField, values.get(0))
				: Filters.in(typeField, values);
	}

	/** Mirrors the writer's value rendering ({@code TypeSerializationEntry.resolveTypeValue}). */
	private static String typeValue(EClass eClass, TypeStrategy strategy,
			ConfigurationResolver resolver, DiagnosticCollector diagnostics) {
		String discriminator = resolver.resolveTypeConfig(eClass, diagnostics).getDiscriminatorValue();
		if (discriminator != null && !discriminator.isEmpty()) {
			return discriminator;
		}
		return switch (strategy) {
		case NAME -> eClass.getName();
		case CLASS -> eClass.getInstanceClass() != null ? eClass.getInstanceClass().getName()
				: eClass.getName();
		case NUMERIC -> String.valueOf(eClass.getClassifierID());
		default -> EcoreUtil.getURI(eClass).toString();
		};
	}
}
