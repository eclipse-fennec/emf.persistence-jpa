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
package org.eclipse.fennec.persistence.orm.helper;

import static java.util.Objects.isNull;
import static java.util.function.Predicate.not;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eorm.EClassObject;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;

/**
 * Helper class to map {@link EPackage} into an eorm model instance
 * @author Mark Hoffmann
 * @since 10.12.2024
 */
public class EORMHelper {
	
    /**
     * Returns the {@link Class} types for an {@link EAttribute}'s type
     * @param eAttribute the {@link EAttribute}
     * @return the data type as {@link Class} or <code>null</code>;
     */
    public static Class<?> convertType(EAttribute eAttribute) {
        EClassifier eClassifier = eAttribute.getEType();
        if (eClassifier == EcorePackage.Literals.ESTRING) {
            return String.class;
        } else if (eClassifier == EcorePackage.Literals.EINTEGER_OBJECT) {
            return Integer.class;
        } else if (eClassifier == EcorePackage.Literals.EINT) {
            return int.class;
        } else if (eClassifier == EcorePackage.Literals.EDATE) {
            return Date.class;
        } else if (eClassifier == EcorePackage.Literals.EBIG_DECIMAL) {
            return BigDecimal.class;
        } else if (eClassifier == EcorePackage.Literals.EBOOLEAN) {
            return boolean.class;
        } else if (eClassifier == EcorePackage.Literals.EBOOLEAN_OBJECT) {
            return Boolean.class;
        } else if (eClassifier == EcorePackage.Literals.EBYTE) {
            return byte.class;
        } else if (eClassifier == EcorePackage.Literals.EBYTE_OBJECT) {
            return Byte.class;
        } else if (eClassifier == EcorePackage.Literals.ECHAR) {
            return char.class;
        } else if (eClassifier == EcorePackage.Literals.ECHARACTER_OBJECT) {
            return Character.class;
        } else if (eClassifier == EcorePackage.Literals.ECLASS) {
            return Class.class;
        } else if (eClassifier == EcorePackage.Literals.EDOUBLE) {
            return double.class;
        } else if (eClassifier == EcorePackage.Literals.EDOUBLE_OBJECT) {
            return Double.class;
        } else if (eClassifier == EcorePackage.Literals.EE_LIST) {
            return List.class;
        } else if (eClassifier == EcorePackage.Literals.EFLOAT) {
            return float.class;
        } else if (eClassifier == EcorePackage.Literals.EFLOAT_OBJECT) {
            return Float.class;
        } else if (eClassifier == EcorePackage.Literals.ELONG) {
            return long.class;
        } else if (eClassifier == EcorePackage.Literals.ELONG_OBJECT) {
            return Long.class;
        } else if (eClassifier == EcorePackage.Literals.ESHORT) {
            return short.class;
        } else if (eClassifier == EcorePackage.Literals.ESHORT_OBJECT) {
            return Short.class;
        }
        return null;
    }

	/**
	 * Extracts all classifiers from all {@link EntityMappings} in the {@link PersistenceUnit} and returns
	 * a distinct list of {@link EClassifier} or an empty {@link List}
	 * @param persistenceUnit the {@link PersistenceUnit}
	 * @return the list of {@link EClassifier} or an empty {@link List}
	 */
	public static Collection<EClassifier> getEClassifier(PersistenceUnit persistenceUnit) {
		if (isNull(persistenceUnit) || persistenceUnit.getEntityMappings().isEmpty()) {
			return Collections.emptyList();
		}
		if (persistenceUnit.
				getEntityMappings().
				stream().
				filter(EObject::eIsProxy).
				findFirst().
				isPresent()) {
			Resource resource = persistenceUnit.eResource();
			if (isNull(resource)) {
				throw new IllegalStateException("Persistence unit is detachted and contains non-resolvable EntityMappings proxies");
			} else {
				EcoreUtil.resolveAll(persistenceUnit);
			}
		}
		return persistenceUnit.
				getEntityMappings().
				stream().
				map(EORMHelper::getEClassifierFromMapping).
				flatMap(Collection::stream).
				collect(Collectors.toList());
	}
	
	/**
	 * Extracts all classifiers from all {@link EntityMappings} and returns
	 * a distinct list of {@link EClassifier} or an empty {@link List}
	 * @param mapping the {@link EntityMappings}
	 * @return the list of {@link EClassifier} or an empty {@link List}
	 */
	public static Collection<EClassifier> getEClassifierFromMapping(EntityMappings mapping) {
		if (isNull(mapping) || mapping.getEntity().isEmpty()) {
			return Collections.emptyList();
		}
		return getEClassifierFromEntities(mapping.getEntity());
	}
	
	/**
	 * Extracts all classifiers from a {@link List} of {@link Entity} and returns
	 * a distinct list of {@link EClassifier} or an empty {@link List}
	 * @param entities the {@link Entity} list
	 * @return the list of {@link EClassifier} or an empty {@link List}
	 */
	public static Collection<EClassifier> getEClassifierFromEntities(List<Entity> entities) {
		if (isNull(entities) || entities.isEmpty()) {
			return Collections.emptyList();
		}
		return entities.
				stream().
				map(Entity::getClass_).
				filter(Objects::nonNull).
				distinct().
				collect(Collectors.toList());
	}
	
	/**
	 * Extracts the EClass from an {@link Entity} and returns
	 * an {@link EClass} or <code>null</code>
	 * @param entity the {@link Entity}
	 * @return the list of {@link EClass} or <code>null</code>
	 */
	public static EClass getEClass(Entity entity) {
		if (isNull(entity)) {
			return null;
		}
		EClassObject eco = (EClassObject) entity.getAccessibleObject();
		return isNull(eco) ? null : eco.getEclass();
	}
	public static String getEClassName(Entity entity) {
		if (isNull(entity)) {
			return null;
		}
		EClassObject eco = (EClassObject) entity.getAccessibleObject();
		return isNull(eco) ? null : eco.getName();
	}
	
	/**
	 * Filters all concrete {@link EClass} from the list of {@link EClassifier}
	 * @param eClassifiers the {@link EClassifier} list
	 * @return the {@link List} with {@link EClass} or an empty list
	 */
	public static Collection<EClass> filterEClasses(List<EClassifier> eClassifiers) {
		if (isNull(eClassifiers) || eClassifiers.isEmpty()) {
			return Collections.emptyList();
		}
		return eClassifiers.
				stream().
				filter(EClass.class::isInstance).
				map(EClass.class::cast).
				filter(not(EClass::isAbstract)).
				collect(Collectors.toList());
	}
	
	/**
	 * Returns a distict {@link List} of {@link EPackage} from the {@link EClassifier} list
	 * @param eClassifiers the {@link EClassifier} list
	 * @returnthe list of {@link EPackage} or an empty {@link List}
	 */
	public static Collection<EPackage> getEPackagesFromEClass(Collection<EClass> eClasses) {
		if (isNull(eClasses) || eClasses.isEmpty()) {
			return Collections.emptyList();
		}
		return eClasses.stream().map(EClassifier::getEPackage).distinct().collect(Collectors.toSet());
	}
	
	/**
	 * Returns a distict {@link List} of {@link EPackage} from the {@link EClassifier} list
	 * @param eClassifiers the {@link EClassifier} list
	 * @returnthe list of {@link EPackage} or an empty {@link List}
	 */
	public static Collection<EPackage> getEPackages(Collection<? extends EClassifier> eClassifiers) {
		if (isNull(eClassifiers) || eClassifiers.isEmpty()) {
			return Collections.emptyList();
		}
		return eClassifiers.stream().map(EClassifier::getEPackage).distinct().collect(Collectors.toSet());
	}
    
}
