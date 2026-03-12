/*
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
 */
package org.eclipse.fennec.persistence.eorm;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Order Column</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({METHOD, FIELD}) @Retention(RUNTIME)
 *         public @interface OrderColumn {
 *           String name() default "";
 *           boolean nullable() default true;
 *           boolean insertable() default true;
 *           boolean updatable() default true;
 *           String columnDefinition() default "";
 *          }
 * 
 *       
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOrderColumn()
 * @model extendedMetaData="name='order-column' kind='empty'"
 * @generated
 */
@ProviderType
public interface OrderColumn extends BaseColumn {
} // OrderColumn
