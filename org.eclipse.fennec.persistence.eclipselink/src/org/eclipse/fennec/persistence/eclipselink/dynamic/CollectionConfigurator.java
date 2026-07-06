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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.eclipse.fennec.persistence.eclipselink.mappings.EFeatureAccessor;
import org.eclipse.fennec.persistence.eorm.CollectionTable;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.EFeatureObject;
import org.eclipse.fennec.persistence.eorm.ElementCollection;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.ContainerMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * Configures @ElementCollection mappings on EclipseLink descriptors.
 * Handles collection tables, value columns, and container indirection policies.
 *
 * @author Mark Hoffmann
 * @since 15.04.2026
 */
class CollectionConfigurator {

	private static final Logger LOG = Logger.getLogger(CollectionConfigurator.class.getName());

	private final BuilderOperations ops;
	private final EDynamicTypeContext context;

	CollectionConfigurator(BuilderOperations ops, EDynamicTypeContext context) {
		this.ops = ops;
		this.context = context;
	}

	/**
	 * Configures multi-valued attribute mappings (element collections).
	 */
	void configureManyAttributes(EDynamicType eType, List<ElementCollection> elementCollections) {
		if (isNull(elementCollections) || elementCollections.isEmpty()) {
			return;
		}
		elementCollections.forEach(this::processElementCollection);
	}

	private void processElementCollection(ElementCollection elementCollection) {
		EFeatureObject efo = (EFeatureObject) elementCollection.getAccessibleObject();
		EAttribute ea = (EAttribute) efo.getFeature();
		CollectionTable ct = elementCollection.getCollectionTable();
		String tableName = nonNull(ct) ?
				ct.getName() :
					ea.getEContainingClass().getName().toUpperCase() + "__" + ea.getName().toUpperCase();
		Column c = elementCollection.getColumn();
		String valueColumn = nonNull(c) ?
				c.getName() :
					"VAL_" + ea.getName().toUpperCase();
		String foreignKeyName = nonNull(ct) && nonNull(ct.getForeignKey()) ? ct.getForeignKey().getName() : null;
		EDataType attrType = ea.getEAttributeType();
		Class<?> typeClass = String.class;
		if (!(attrType instanceof EEnum) && nonNull(attrType.getInstanceClass())) {
			typeClass = attrType.getInstanceClass();
		}
		typeClass = isNull(typeClass) ? String.class : typeClass;
		DirectCollectionMapping mapping = ops.addDirectCollectionMapping(elementCollection.getName(), tableName, valueColumn, typeClass, foreignKeyName);
		mapping.setIsLazy(false);
		if (nonNull(c)) {
			mapping.setIsOptional(false);
			mapping.setCascadeAll(true);
		}
		mapping.setShouldExtendPessimisticLockScope(true);
		setContainerIndirectionPolicy(mapping, null, false, ea);

		/**
		 * Automatic converter detection for element collections with custom types
		 */
		TypeConverter converter = null;
		if (typeClass == String.class && attrType.getInstanceClass() != String.class) {
			try {
				LOG.log(Level.FINER, "[ElementCollection] Trying to find converter for {0} (instanceClass: {1})",
					new Object[]{attrType.getName(), attrType.getInstanceClass()});
				converter = context.getConverter(attrType);
				if (nonNull(converter)) {
					LOG.log(Level.FINER, "[ElementCollection] Found converter: {0} for {1}",
						new Object[]{converter.getName(), attrType.getName()});
				} else {
					LOG.log(Level.FINER, "[ElementCollection] No converter found for {0}", attrType.getName());
				}
			} catch (Exception e) {
				LOG.log(Level.WARNING, e, () -> "[ElementCollection] Exception finding converter for " + attrType.getName());
			}
		}
		EFeatureAccessor efa = EFeatureAccessor.create(mapping, ea, converter);
		mapping.setAttributeAccessor(efa);
	}

	void setContainerIndirectionPolicy(DatabaseMapping mapping, String mapKey, boolean resolveProxy, EStructuralFeature feature) {
		if (mapping instanceof ContainerMapping cMapping) {
			if (resolveProxy && (mapping instanceof ForeignReferenceMapping)) {
				CollectionMapping collectionMapping = (CollectionMapping) mapping;
				if (feature.isUnique()) {
					collectionMapping.useTransparentSet();
				} else {
					collectionMapping.useTransparentList();
				}
			} else {
				if (mapping instanceof CollectionMapping collMapping) {
					collMapping.dontUseIndirection();
				}
			}
			cMapping.useCollectionClass(LinkedList.class);
		} else if (mapping instanceof ForeignReferenceMapping frMapping) {
			if (resolveProxy) {
				frMapping.setIndirectionPolicy(new BasicIndirectionPolicy());
			} else {
				frMapping.dontUseIndirection();
			}
		}
	}
}
