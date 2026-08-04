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
/**
 * Implementation part of the persistence-unit SPI (issue #65): the DS configurator
 * components and the {@code EPersistenceContext} implementation. Deliberately not
 * exported — consumers bind the registered services and extend the exported
 * {@code spi} package types only.
 */
package org.eclipse.fennec.persistence.eclipselink.spi.impl;
