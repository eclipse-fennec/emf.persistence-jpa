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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;

import com.mongodb.client.MongoDatabase;

/**
 * The Mongo {@link StoreProbe} (issue #329): the raw BSON documents, read with the driver past the
 * resource layer. A type's objects live in the collection of their type; contained objects are
 * embedded in their container's document; a reference is a {@code {_ref: <uri>}} document or the
 * URI string. A reference the document does not carry but its opposite does is read from the
 * opposite side's documents.
 */
final class MongoStoreProbe implements StoreProbe {

	private final MongoDatabase database;

	MongoStoreProbe(MongoDatabase database) {
		this.database = database;
	}

	@Override
	public Set<String> ids(EClass type) {
		Set<String> ids = new LinkedHashSet<>();
		for (EClass concrete : concreteTypes(type)) {
			for (BsonDocument document : database.getCollection(concrete.getName(), BsonDocument.class).find()) {
				ids.add(idOf(document));
			}
		}
		return ids;
	}

	@Override
	public Optional<String> reference(EClass type, String id, EReference reference) {
		BsonDocument document = document(type, id);
		if (isNull(document)) {
			return Optional.empty();
		}
		BsonValue value = document.get(reference.getName());
		if (isNull(value) || value.isNull()) {
			return reference.isContainment() || isNull(reference.getEOpposite())
					? Optional.empty()
					: fromOpposite(reference, id).stream().findFirst();
		}
		return Optional.ofNullable(targetId(value, reference));
	}

	@Override
	public List<String> references(EClass type, String id, EReference reference) {
		BsonDocument document = document(type, id);
		if (isNull(document)) {
			return List.of();
		}
		BsonValue value = document.get(reference.getName());
		if (isNull(value) || value.isNull()) {
			return reference.isContainment() || isNull(reference.getEOpposite())
					? List.of()
					: fromOpposite(reference, id);
		}
		List<String> ids = new ArrayList<>();
		if (value.isArray()) {
			for (BsonValue element : value.asArray()) {
				String target = targetId(element, reference);
				if (nonNull(target)) {
					ids.add(target);
				}
			}
		}
		return ids;
	}

	@Override
	public boolean storesContainedObjectsSeparately() {
		return false;
	}

	/** The documents of the opposite type whose opposite reference points at the id. */
	private List<String> fromOpposite(EReference reference, String id) {
		EReference opposite = reference.getEOpposite();
		List<String> ids = new ArrayList<>();
		for (EClass concrete : concreteTypes(reference.getEReferenceType())) {
			for (BsonDocument document : database.getCollection(concrete.getName(), BsonDocument.class).find()) {
				BsonValue value = document.get(opposite.getName());
				if (isNull(value) || value.isNull()) {
					continue;
				}
				List<BsonValue> values = value.isArray() ? value.asArray().getValues() : List.of(value);
				if (values.stream().anyMatch(v -> id.equals(targetId(v, opposite)))) {
					ids.add(idOf(document));
				}
			}
		}
		return ids;
	}

	private BsonDocument document(EClass type, String id) {
		for (EClass concrete : concreteTypes(type)) {
			BsonDocument document = database.getCollection(concrete.getName(), BsonDocument.class)
					.find(new BsonDocument(MongoPersistenceConstants.ID_FIELD, new BsonString(id))).first();
			if (nonNull(document)) {
				return document;
			}
		}
		return null;
	}

	/** The type and its concrete subtypes — each has a collection of its own. */
	private static List<EClass> concreteTypes(EClass type) {
		List<EClass> types = new ArrayList<>();
		for (EClassifier classifier : type.getEPackage().getEClassifiers()) {
			if (classifier instanceof EClass candidate && !candidate.isAbstract() && type.isSuperTypeOf(candidate)) {
				types.add(candidate);
			}
		}
		return types;
	}

	private static String idOf(BsonDocument document) {
		BsonValue id = document.get(MongoPersistenceConstants.ID_FIELD);
		return isNull(id) ? null : id.isString() ? id.asString().getValue() : id.toString();
	}

	/** The target id of a stored reference value: an embedded object, a {@code _ref} document or a URI. */
	private static String targetId(BsonValue value, EReference reference) {
		if (value.isDocument()) {
			BsonDocument document = value.asDocument();
			if (document.containsKey(MongoPersistenceConstants.REF_FIELD)) {
				return fragmentId(document.getString(MongoPersistenceConstants.REF_FIELD).getValue());
			}
			for (String key : List.of(MongoPersistenceConstants.ID_FIELD,
					reference.getEReferenceType().getEIDAttribute().getName())) {
				BsonValue id = document.get(key);
				if (nonNull(id) && id.isString()) {
					return id.asString().getValue();
				}
			}
			return null;
		}
		return value.isString() ? fragmentId(value.asString().getValue()) : null;
	}

	/**
	 * The id a stored reference URI names: {@code /WpCustomer#c1} across resources, the bare id
	 * {@code n1} within the same resource. A URI whose fragment is a type ({@code …#//WpStudent})
	 * is returned as that type name — it names no object, and the case comparing it fails.
	 */
	private static String fragmentId(String uri) {
		if (uri.indexOf('#') < 0) {
			return uri;
		}
		String fragment = URI.createURI(uri).fragment();
		if (isNull(fragment)) {
			return null;
		}
		if (fragment.startsWith("//")) {
			String[] parts = fragment.substring(2).split("/");
			return parts[parts.length - 1];
		}
		return fragment;
	}
}
