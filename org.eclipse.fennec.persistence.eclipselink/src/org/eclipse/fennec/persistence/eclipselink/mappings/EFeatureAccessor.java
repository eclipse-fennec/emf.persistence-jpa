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
package org.eclipse.fennec.persistence.eclipselink.mappings;

import static java.util.Objects.nonNull;

import java.util.Collection;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.dynamic.ValuesAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Handles getting and setting data from and to an {@link EObject} for a given feature.
 *
 * <p>Extends {@link ValuesAccessor} (EclipseLink's dynamic-entity accessor) instead of the
 * plain {@code AttributeAccessor}: EclipseLink identifies dynamic types by
 * {@code instanceof ValuesAccessor} — most importantly the JPA metamodel initialization
 * ({@code ManagedTypeImpl.initialize}), which otherwise produces null attribute members for
 * collection mappings and breaks the Jakarta Criteria API. The value access itself is fully
 * overridden with EMF {@code eGet}/{@code eSet} semantics.
 * @author Mark Hoffmann
 * @since 09.12.2024
 */
public class EFeatureAccessor extends ValuesAccessor {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private final EStructuralFeature feature;
	private final TypeConverter converter;

	/**
	 * Creates a new accessor for the given feature without a converter.
	 * @param mapping the mapping the accessor will be set on
	 * @param feature the structural feature
	 * @return a new accessor instance
	 */
	public static EFeatureAccessor create(DatabaseMapping mapping, EStructuralFeature feature) {
		return new EFeatureAccessor(mapping, feature, null);
	}

	/**
	 * Creates a new accessor for the given feature with a converter.
	 * @param mapping the mapping the accessor will be set on
	 * @param feature the structural feature
	 * @param converter the type converter, may be {@code null}
	 * @return a new accessor instance
	 */
	public static EFeatureAccessor create(DatabaseMapping mapping, EStructuralFeature feature,
			TypeConverter converter) {
		return new EFeatureAccessor(mapping, feature, converter);
	}

	private EFeatureAccessor(DatabaseMapping mapping, EStructuralFeature feature, TypeConverter converter) {
		super(mapping);
		this.feature = feature;
		this.converter = converter;
	}

	/**
	 * Preserves the pre-{@link ValuesAccessor} behavior ({@code AttributeAccessor} returned
	 * {@code Object.class}): the mapping-classification-based refinement the superclass offers
	 * is a deliberate follow-up, not part of the metamodel-marker change.
	 */
	@Override
	public Class<?> getAttributeClass() {
		return Object.class;
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.mappings.AttributeAccessor#getAttributeValueFromObject(java.lang.Object)
	 */
	@Override
	public Object getAttributeValueFromObject(Object object) throws DescriptorException {
		if (object instanceof EObject eo && nonNull(feature)) {
			Object value = eo.eGet(feature);
			if (isEEnumFeature()) {
				if (value instanceof Enumerator enumerator) {
					return enumerator.getLiteral();
				}
			}
			if (nonNull(converter)) {
				EClassifier type = feature instanceof EReference ? 
						((EReference)feature).getEReferenceType() : 
							((EAttribute)feature).getEAttributeType();
				value = converter.convertEMFToValue(type, value);
			} 
			return value;
		}
		return object;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.mappings.AttributeAccessor#setAttributeValueInObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
		if (object instanceof EObject eo &&
				!isDefaultValue(value) &&
				nonNull(feature)) {
			if (feature instanceof EReference ref) {
				if (nonNull(converter)) {
					value = converter.convertValueToEMF(ref.getEReferenceType(), value);
				}
			} else {
				EDataType dataType = ((EAttribute)feature).getEAttributeType();
				if (feature.isMany() && value instanceof Collection) {
					value = ((Collection<?>)value).stream().map(v->this.dataTypeConvert(v, dataType)).toList();
				} else {
					value = dataTypeConvert(value, dataType);
				}
			}
			eo.eSet(feature, value);
		}
	}

	/**
	 * Converts data using the EMF data type conversion 
	 * @param value the value to convert
	 * @param dataType the data type
	 * @return the converted value or the original one
	 */
	private Object dataTypeConvert(Object value, EDataType dataType) {
		if (value instanceof String &&
				EcorePackage.Literals.ESTRING != dataType) {
			value = EcoreUtil.createFromString(dataType, value.toString());
		} else if (nonNull(converter) && converter.isConverterForType(dataType)) {
			value = converter.convertValueToEMF(dataType, value);
		}
		return value;
	}

	/**
	 * Return <code>true</code>, if the {@link EStructuralFeature} is an enum
	 * @return <code>true</code>, if the {@link EStructuralFeature} is an enum
	 */
	private boolean isEEnumFeature() {
		return (feature instanceof EAttribute && ((EAttribute)feature).getEAttributeType() instanceof EEnum);
	}

	/**
	 * Returns <code>true</code>, if the given value is equals to the default literal of the feature
	 * @param value the value to verify
	 * @return <code>true</code>, if the given value is equals to the default literal of the feature
	 */
	private boolean isDefaultValue(Object value) {
		return nonNull(feature.getDefaultValueLiteral()) && feature.getDefaultValueLiteral().equals(value);
	}

}
