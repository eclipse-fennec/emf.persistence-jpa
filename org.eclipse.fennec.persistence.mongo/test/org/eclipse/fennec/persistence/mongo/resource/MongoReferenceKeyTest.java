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
package org.eclipse.fennec.persistence.mongo.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.client.MongoDatabase;

/**
 * The BSON key policy for non-containment references (issue #277).
 * <p>
 * The codec's {@code REF_KEY} default is {@code $ref} — the JSON Reference convention, and
 * unusable in BSON: MongoDB reserves a {@code $ref} field for DBRefs and requires a
 * {@code $id} beside it. {@code doSave} writes every root with a {@code ReplaceOneModel},
 * and a replacement document is exactly where the server enforces that rule
 * (<em>The DBRef $ref field must be followed by a $id field</em>, code 55, on MongoDB up to
 * 4.4 — 5.0 relaxed {@code $}-prefixed field names, which is why the container-backed suite
 * on mongo:7 never saw this). So the Mongo layer pins a BSON-safe key next to the
 * {@code _id} / {@code _type} / id-format settings it already pins.
 * <p>
 * These cases run without a server: the encode/decode bridge is where the key is decided,
 * and a round trip through it is what proves read and write agree.
 *
 * @author Ilenia Salvadori
 * @since 08.09.2026
 */
@ExtendWith(MockitoExtension.class)
class MongoReferenceKeyTest {

	@Mock
	MongoDatabase database;

	private MetadataWhiteboard metadataService;
	private ResourceSet resourceSet;
	private EPackage refPackage;
	private EClass bookClass;
	private EClass authorClass;
	private EReference bookAuthor;
	private EReference bookReviewers;

	@BeforeEach
	void setUp() {
		buildModel();
		metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(refPackage);
		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(refPackage.getNsURI(), refPackage);
	}

	private void buildModel() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		authorClass = ecore.createEClass();
		authorClass.setName("Author");
		addId(authorClass, "aid");

		bookClass = ecore.createEClass();
		bookClass.setName("Book");
		addId(bookClass, "bid");
		bookAuthor = reference("author", authorClass, 1);
		bookClass.getEStructuralFeatures().add(bookAuthor);
		bookReviewers = reference("reviewers", authorClass, -1);
		bookClass.getEStructuralFeatures().add(bookReviewers);

		refPackage = ecore.createEPackage();
		refPackage.setName("refkey");
		refPackage.setNsURI("urn:refkey:test/1.0");
		refPackage.setNsPrefix("refkey");
		refPackage.getEClassifiers().add(authorClass);
		refPackage.getEClassifiers().add(bookClass);
	}

	private static void addId(EClass eClass, String name) {
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName(name);
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		eClass.getEStructuralFeatures().add(id);
	}

	private static EReference reference(String name, EClass type, int upper) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(name);
		reference.setEType(type);
		reference.setContainment(false);
		reference.setUpperBound(upper);
		return reference;
	}

	// ------------------------------------------------------------------ fixture

	/**
	 * A resource that is attached to the set but never loaded, so nothing here touches the
	 * mocked database: the deferred population only runs after a {@code load}.
	 */
	private MongoResourceImpl resource(String typeName) {
		MongoResourceImpl resource = new MongoResourceImpl(
				URI.createURI("mongodb://refkeydb/" + typeName), database, metadataService, null);
		resourceSet.getResources().add(resource);
		return resource;
	}

	/** A book referencing an author that lives in its own resource — the issue's shape. */
	private BsonDocument encodeBookWithCrossResourceAuthor() throws Exception {
		EObject author = EcoreUtil.create(authorClass);
		author.eSet(authorClass.getEStructuralFeature("aid"), "a1");
		resource("Author").getContents().add(author);

		EObject book = EcoreUtil.create(bookClass);
		book.eSet(bookClass.getEStructuralFeature("bid"), "b1");
		book.eSet(bookAuthor, author);
		MongoResourceImpl books = resource("Book");
		books.getContents().add(book);

		return books.encode(book);
	}

	/** Every field name in the document, at any depth, arrays included. */
	private static List<String> fieldNames(BsonValue value) {
		List<String> names = new ArrayList<>();
		collectFieldNames(value, names);
		return names;
	}

	private static void collectFieldNames(BsonValue value, List<String> names) {
		if (value.isDocument()) {
			value.asDocument().forEach((name, child) -> {
				names.add(name);
				collectFieldNames(child, names);
			});
		} else if (value.isArray()) {
			value.asArray().forEach(child -> collectFieldNames(child, names));
		}
	}

	// -------------------------------------------------------------------- cases

	@Test
	@DisplayName("no encoded field name is BSON-reserved — the write the server refuses")
	void encodedDocumentCarriesNoDollarPrefixedFieldName() throws Exception {
		BsonDocument document = encodeBookWithCrossResourceAuthor();

		assertThat(fieldNames(document))
				.as("a $-prefixed field name in a replacement document is rejected by the server")
				.isNotEmpty()
				.noneMatch(name -> name.startsWith("$"));
	}

	@Test
	@DisplayName("the many-valued reference is no exception — the array elements too")
	void encodedArrayElementsCarryNoDollarPrefixedFieldName() throws Exception {
		EObject first = EcoreUtil.create(authorClass);
		first.eSet(authorClass.getEStructuralFeature("aid"), "a1");
		EObject second = EcoreUtil.create(authorClass);
		second.eSet(authorClass.getEStructuralFeature("aid"), "a2");
		MongoResourceImpl authors = resource("Author");
		authors.getContents().add(first);
		authors.getContents().add(second);

		EObject book = EcoreUtil.create(bookClass);
		book.eSet(bookClass.getEStructuralFeature("bid"), "b1");
		@SuppressWarnings("unchecked")
		List<EObject> reviewers = (List<EObject>) book.eGet(bookReviewers);
		reviewers.add(first);
		reviewers.add(second);
		MongoResourceImpl books = resource("Book");
		books.getContents().add(book);

		BsonDocument document = books.encode(book);

		assertThat(document.get("reviewers").asArray()).hasSize(2);
		assertThat(fieldNames(document)).noneMatch(name -> name.startsWith("$"));
	}

	@Test
	@DisplayName("the reference target sits under the pinned BSON-safe key")
	void referenceTargetIsWrittenUnderTheMongoRefField() throws Exception {
		BsonDocument document = encodeBookWithCrossResourceAuthor();

		BsonDocument reference = document.get("author").asDocument();
		assertThat(reference.containsKey(MongoPersistenceConstants.REF_FIELD)).isTrue();
		assertThat(reference.getString(MongoPersistenceConstants.REF_FIELD).getValue())
				.as("the target uri, deresolved against the source resource")
				.contains("a1");
	}

	@Test
	@DisplayName("the query field paths use the very key encode wrote")
	void theResolvedRefKeyMatchesTheEncodedOne() {
		MongoResourceImpl books = resource("Book");

		// refKey() feeds the referential-integrity filters; a mismatch with the written key
		// would make every inbound-reference check silently find nothing
		assertThat(books.getResolver().<String> getGlobalProperty(ConfigProperty.REF_KEY))
				.isEqualTo(MongoPersistenceConstants.REF_FIELD);
	}

	@Test
	@DisplayName("read and write agree: the reference survives the encode/decode round trip")
	void theReferenceRoundTripsThroughTheCodecBridge() throws Exception {
		BsonDocument document = encodeBookWithCrossResourceAuthor();

		EObject decoded = resource("Book").decode(document, bookClass);

		assertThat(decoded).isNotNull();
		Object author = decoded.eGet(bookAuthor, false);
		assertThat(author).as("the reference is restored, not dropped").isInstanceOf(EObject.class);
		assertThat(((EObject) author).eClass()).isEqualTo(authorClass);
	}
}
