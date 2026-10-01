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
package org.eclipse.fennec.persistence.converter;

import static java.util.Objects.isNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Stores a containment child as one value of its parent: the child's whole subtree, encoded as
 * XMI (issue #363).
 * <p>
 * A relational store maps a containment child as a row of its own, which needs the child's
 * class in the persistence unit. A child whose class is not — the GeoJSON {@code Geometry} of a
 * model that only references the GeoJSON package, say — would otherwise be dropped. Encoding it
 * keeps it with no entity, no foreign package in the unit and no key of its own: the child is
 * an atomic value of the parent's feature.
 * <p>
 * XMI because it is the persisted form the model itself declares: it writes exactly the features
 * that are not transient, including a volatile or derived one the model keeps its state in (the
 * coordinates of a GeoJSON geometry live in its volatile {@code data} attribute). A
 * cross-reference out of the subtree is written as an absolute {@code href} and comes back as a
 * proxy.
 * <p>
 * The converter is addressed by name only — {@link #isConverterForType(EClassifier)} claims no
 * type, so a lookup by type never mistakes an ordinary reference for an encoded one. There are
 * two instances, one per cardinality, because a converter learns the target type but not the
 * feature: {@value #NAME} for a single-valued reference, {@value #NAME_MANY} for a many-valued
 * one, which reads a missing value back as an empty list.
 *
 * @author Juergen Albert
 * @since 01.10.2026
 */
public class ContainedObjectConverter implements TypeConverter {

	/** The name of the converter for a single-valued containment reference. */
	public static final String NAME = "containedObject";
	/** The name of the converter for a many-valued containment reference. */
	public static final String NAME_MANY = "containedObjects";

	/**
	 * The URI of the scratch resource the value is written and read through. Opaque on purpose:
	 * against a hierarchical one XMI would deresolve a cross-reference into a relative
	 * {@code href} that means nothing once the value is stored.
	 */
	private static final URI SCRATCH_URI = URI.createURI("fennec:contained-object");
	private static final Map<Object, Object> SAVE_OPTIONS = Map.of(
			XMLResource.OPTION_ENCODING, StandardCharsets.UTF_8.name());

	private final boolean many;

	/**
	 * Creates the converter for one cardinality.
	 * @param many {@code true} for a many-valued containment reference
	 */
	public ContainedObjectConverter(boolean many) {
		this.many = many;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.api.TypeConverter#getName()
	 */
	@Override
	public String getName() {
		return many ? NAME_MANY : NAME;
	}

	/**
	 * Claims no type: the converter is selected by its name in the mapping, never by type.
	 */
	@Override
	public boolean isConverterForType(EClassifier eDataType) {
		return false;
	}

	/**
	 * An encoded subtree has no bound — a polygon carries any number of coordinates.
	 */
	@Override
	public boolean isLargeValue(EClassifier eDataType) {
		return true;
	}

	/**
	 * Encodes the child, or the children of a many-valued reference, as one XMI document.
	 * {@code null} and an empty list are stored as no value.
	 */
	@Override
	public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
		List<EObject> children = new ArrayList<>();
		if (emfValue instanceof EObject child) {
			children.add(child);
		} else if (emfValue instanceof Collection<?> collection) {
			collection.stream()
					.filter(EObject.class::isInstance)
					.map(EObject.class::cast)
					.forEach(children::add);
		}
		if (children.isEmpty()) {
			return null;
		}
		return encode(children);
	}

	/**
	 * Decodes the stored XMI into the child, or the list of children for the many-valued
	 * converter. No value reads back as {@code null}, or as an empty list for the many-valued
	 * converter.
	 */
	@Override
	public Object convertValueToEMF(EClassifier eDataType, Object value) {
		List<EObject> children = value instanceof String text && !text.isBlank()
				? decode(text, eDataType)
				: List.of();
		if (many) {
			return children;
		}
		return children.isEmpty() ? null : children.get(0);
	}

	/**
	 * Writes copies, never the children themselves: adding an object to a resource takes it out
	 * of its container.
	 */
	private static String encode(List<EObject> children) {
		XMIResourceImpl resource = new XMIResourceImpl(SCRATCH_URI);
		resource.getContents().addAll(EcoreUtil.copyAll(children));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			resource.save(out, SAVE_OPTIONS);
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot encode the contained object as XMI", e);
		}
		return out.toString(StandardCharsets.UTF_8);
	}

	/**
	 * Reads the children back. The packages of the target type and its super types are
	 * registered with the scratch resource set, so a dynamic package that is in no global
	 * registry resolves as well; a subtype from yet another package needs that package in the
	 * global registry.
	 */
	private static List<EObject> decode(String text, EClassifier type) {
		ResourceSet resourceSet = new ResourceSetImpl();
		if (type instanceof EClass eClass) {
			register(resourceSet, eClass.getEPackage());
			eClass.getEAllSuperTypes().forEach(superType -> register(resourceSet, superType.getEPackage()));
		}
		XMIResourceImpl resource = new XMIResourceImpl(SCRATCH_URI);
		resourceSet.getResources().add(resource);
		try {
			resource.load(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), null);
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot decode the contained object from XMI", e);
		}
		List<EObject> children = new ArrayList<>(resource.getContents());
		// detach: the children go into their parent's containment, not into a scratch resource
		resource.getContents().clear();
		resourceSet.getResources().remove(resource);
		return children;
	}

	private static void register(ResourceSet resourceSet, EPackage ePackage) {
		if (isNull(ePackage) || isNull(ePackage.getNsURI())) {
			return;
		}
		resourceSet.getPackageRegistry().putIfAbsent(ePackage.getNsURI(), ePackage);
	}

}
