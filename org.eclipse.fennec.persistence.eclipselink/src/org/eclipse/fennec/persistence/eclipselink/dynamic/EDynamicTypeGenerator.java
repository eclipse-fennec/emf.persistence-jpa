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
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults;
import org.eclipse.fennec.persistence.eorm.PersistenceUnitMetadata;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.sessions.server.Server;

/**
 * EDynamicTypeGenerator creates the dynamic types based on the ORM model.
 * It gets more and more just a wrapper for the generation scalls
 * @author Mark Hoffmann
 * @since 16.12.2024
 */
public class EDynamicTypeGenerator {

	private final EDynamicTypeContext context = new EDynamicTypeContext();
	private final List<EntityMappings> mappingModels = new ArrayList<>();

	/**
	 * Creates a new instance.
	 */
	public EDynamicTypeGenerator(DynamicClassLoader dcl, Server session, String puName) {
		context.setClassloader(dcl);
		context.setSession(session);
		context.setBaseURI(URI.createURI("jpa://" + puName + "/"));
	}
	
	/**
	 * Creates a new instance.
	 */
	public EDynamicTypeGenerator(DynamicClassLoader dcl, Server session, String puName, ConverterService converter) {
		this(dcl, session, puName);
		context.setConverter(converter);
	}

	/**
	 * Creates a new instance.
	 */
	public EDynamicTypeGenerator(DynamicClassLoader dcl, Server session, PersistenceUnit persistenceUnit) {
		this(dcl, session, persistenceUnit.getName());
		mappingModels.addAll(persistenceUnit.getEntityMappings());
	}

	/*
	 * Creates {@link EDynamicType} list from a {@link PersistenceUnit}
	 * @param persistenceUnit the {@link PersistenceUnit}
	 * @return the list of {@link EDynamicTypeOld}
	 */
	public List<EDynamicType> createFromPersistenceUnit(PersistenceUnit persistenceUnit) {
		if (isNull(persistenceUnit)) {
			return Collections.emptyList();
		}
		return createFromMappings(persistenceUnit.getEntityMappings());
	}

	/**
	 * Creates {@link EDynamicType} list from a list of {@link EntityMappings}
	 * @param mappings the mappings
	 * @return the list of {@link EDynamicTypeOld}
	 */
	public List<EDynamicType> createFromMappings(List<EntityMappings> mappings) {
		if (isNull(mappings) || mappings.isEmpty()) {
			return Collections.emptyList();
		}
		if (mappings.stream().anyMatch(EDynamicTypeGenerator::hasDelimitedIdentifiers)) {
			context.setUseDelimitedIdentifiers(true);
		}
		return mappings.stream().
				map(this::createFromMapping).
				flatMap(List::stream).
				collect(Collectors.toList());
	}

	/**
	 * @return {@code true} if {@code mapping} declares
	 *         {@code persistenceUnitMetadata/persistenceUnitDefaults/delimitedIdentifiers}.
	 */
	private static boolean hasDelimitedIdentifiers(EntityMappings mapping) {
		PersistenceUnitMetadata pum = mapping.getPersistenceUnitMetadata();
		if (isNull(pum)) {
			return false;
		}
		PersistenceUnitDefaults pud = pum.getPersistenceUnitDefaults();
		return nonNull(pud) && nonNull(pud.getDelimitedIdentifiers());
	}

	/**
	 * Create {@link EDynamicType} list from an {@link EntityMappings}
	 * @param mapping an {@link EntityMappings}
	 * @return the list of {@link EDynamicTypeOld}
	 */
	public List<EDynamicType> createFromMapping(EntityMappings mapping) {
		if (isNull(mapping) || mapping.getEntity().isEmpty()) {
			return Collections.emptyList();
		}
		/*
		 * Create all entities and id's, attribute mappings
		 */
		mapping.getEntity().
			stream().
			map(this::createFromEntity).
			toList();
		/*
		 * Configure inheritance (parents before children)
		 */
		mapping.getEntity().stream()
			.sorted(inheritanceOrder())
			.map(context::get)
			.filter(Objects::nonNull)
			.forEach(EDynamicTypeBuilder::configureInheritance);
		/*
		 * Create all simple reference mappings
		 */
		mapping.getEntity().stream().map(context::get).filter(Objects::nonNull).forEach(EDynamicTypeBuilder::configureReferences);
		/*
		 * Now configure the bi-directional mapping via e.g. mappedby
		 */
		return mapping.
				getEntity().
				stream().
				map(context::get).
				filter(Objects::nonNull).
				map(this::configureDynamicType).
				filter(Objects::nonNull).
				toList();
	}

	/**
	 * Creates an {@link EDynamicTypeBuilder} out of an {@link Entity}.
	 * If we have generated code, we take the generated class for the {@link ClassDescriptor},
	 * otherwise we create a new class that inherits from {@link DynamicEObjectImpl} and take this 
	 * for the {@link ClassDescriptor}
	 * @param entity the {@link Entity}
	 * @return the {@link EDynamicTypeBuilder} or <code>null</code>
	 */
	public EDynamicTypeBuilder createFromEntity(Entity entity) {
		if (isNull(entity)) {
			return null;
		}
		final EDynamicTypeBuilder typeBuilder = context.
				computeIfAbsent(entity, e-> new EDynamicTypeBuilder(e, context));
		return typeBuilder;
	}

	/**
	 * Creates the setup for an {@link EDynamicTypeOld}. It does all the mapping
	 * @param dynamicType the {@link EDynamicTypeOld}
	 * @return the customized {@link EDynamicTypeOld} instance
	 */
	public EDynamicType configureDynamicType(EDynamicTypeBuilder dynamicTypeBuilder) {
		if (isNull(dynamicTypeBuilder)) {
			return null;
		}
		dynamicTypeBuilder.configureMappedByReferences();
		return dynamicTypeBuilder.getType();
	}

	/**
	 * Returns the Java Type for a given {@link EDataType}
	 * @param dataType the Ecore {@link EDataType}
	 * @return the Java type
	 * @TODO Add plugable type converter here
	 */
	protected Class<?> getDataTypeClass(EDataType dataType) {
		requireNonNull(dataType);
		return dataType.getInstanceClass();
	}

	/**
	 * Returns a comparator that orders entities so that parent entities (roots)
	 * come before child entities. Entities without inheritance are ordered first.
	 */
	private Comparator<Entity> inheritanceOrder() {
		return Comparator.comparingInt(entity -> {
			if (isNull(entity.getClass_())) return 0;
			EClass eClass = (EClass) entity.getClass_();
			// Count depth in inheritance hierarchy — roots=0, children=1, grandchildren=2, etc.
			int depth = 0;
			EClass current = eClass;
			while (!current.getESuperTypes().isEmpty()) {
				depth++;
				current = current.getESuperTypes().get(0);
			}
			return depth;
		});
	}

}