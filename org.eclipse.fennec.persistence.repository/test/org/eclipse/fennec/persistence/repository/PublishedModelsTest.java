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
package org.eclipse.fennec.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.fennec.model.command.CommandPackage;
import org.eclipse.fennec.model.expression.ExpressionPackage;
import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.model.stream.StreamPackage;
import org.eclipse.fennec.persistence.query.api.QueryApiPackage;
import org.eclipse.fennec.persistence.repository.api.RepositoryApiPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Every model that references another one does so by namespace URI (issue #359), so each
 * {@code .ecore} can be published on its own — uploaded into a schema registry, copied next to a
 * configuration — and still resolve, given the packages it depends on are registered.
 * <p>
 * A workspace-relative path such as
 * {@code ../../org.eclipse.fennec.expression.model/model/expression.ecore} resolves only inside
 * this workspace. Here each file is loaded under a URI with nothing beside it, with every
 * <em>other</em> package registered by namespace URI and nothing else, which is exactly the
 * situation of a published copy.
 *
 * @since 16.10.2026
 */
class PublishedModelsTest {

	/** Model file, relative to the workspace root, and the namespace URI of its package. */
	static Stream<String[]> models() {
		return Stream.of(
				new String[] { "org.eclipse.fennec.query.model/model/query.ecore", QueryPackage.eNS_URI },
				new String[] { "org.eclipse.fennec.command.model/model/command.ecore", CommandPackage.eNS_URI },
				new String[] { "org.eclipse.fennec.persistence.query/model/query-api.ecore", QueryApiPackage.eNS_URI },
				new String[] { "org.eclipse.fennec.persistence.repository/model/repository-api.ecore",
						RepositoryApiPackage.eNS_URI });
	}

	@ParameterizedTest
	@MethodSource("models")
	void aPublishedCopyResolvesByNamespaceUri(String file, String nsUri) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl());
		for (EPackage dependency : List.of(ExpressionPackage.eINSTANCE, QueryPackage.eINSTANCE,
				CommandPackage.eINSTANCE, StreamPackage.eINSTANCE, QueryApiPackage.eINSTANCE)) {
			if (!dependency.getNsURI().equals(nsUri)) {
				resourceSet.getPackageRegistry().put(dependency.getNsURI(), dependency);
			}
		}
		// a location with no sibling files: what a relative path names cannot exist here
		Resource resource = resourceSet.createResource(URI.createURI("urn:published/" + Path.of(file).getFileName()));
		try (InputStream in = Files.newInputStream(workspaceRoot().resolve(file))) {
			resource.load(in, Map.of());
		}
		EcoreUtil.resolveAll(resourceSet);

		assertThat(EcoreUtil.UnresolvedProxyCrossReferencer.find(resource))
				.as("unresolved references of %s", file).isEmpty();
		EPackage published = (EPackage) resource.getContents().get(0);
		assertThat(published.getNsURI()).isEqualTo(nsUri);
		List<EClassifier> foreignTypes = foreignTypes(published);
		assertThat(foreignTypes).as("%s references other packages", file).isNotEmpty();
		assertThat(foreignTypes).allSatisfy(type -> {
			EPackage owner = type.getEPackage();
			assertThat(owner).as("%s is resolved", type).isNotNull();
			assertThat(owner.getNsURI()).isNotEqualTo(nsUri);
			assertThat(resourceSet.getPackageRegistry().getEPackage(owner.getNsURI()))
					.as("%s comes from the registered package", type).isSameAs(owner);
		});
	}

	/** The types of other packages the model's references and operations name. */
	private static List<EClassifier> foreignTypes(EPackage published) {
		List<EClassifier> types = new ArrayList<>();
		for (var iterator = published.eAllContents(); iterator.hasNext();) {
			EObject object = iterator.next();
			for (EReference reference : object.eClass().getEAllReferences()) {
				if (reference.isContainment() || reference.isMany() || reference.isDerived()
						|| !EcorePackage.Literals.ECLASSIFIER.isSuperTypeOf(reference.getEReferenceType())) {
					continue;
				}
				if (object.eGet(reference) instanceof EClassifier type && type.eResource() != published.eResource()
						&& type.getEPackage() != EcorePackage.eINSTANCE) {
					types.add(type);
				}
			}
		}
		return types;
	}

	/** The test runs in the project directory; the models of the other bundles are its siblings. */
	private static Path workspaceRoot() {
		Path here = Path.of("").toAbsolutePath();
		return Files.exists(here.resolve("cnf")) ? here : here.getParent();
	}
}
