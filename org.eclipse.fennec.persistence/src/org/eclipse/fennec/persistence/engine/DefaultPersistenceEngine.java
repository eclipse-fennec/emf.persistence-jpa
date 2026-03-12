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
package org.eclipse.fennec.persistence.engine;

import org.eclipse.fennec.persistence.Countable;
import org.eclipse.fennec.persistence.Deletable;
import org.eclipse.fennec.persistence.Readable;
import org.eclipse.fennec.persistence.Updateable;

/**
 * Interface for a default persistence engine
 * @author Mark Hoffmann
 * @since 10.12.2024
 */
public interface DefaultPersistenceEngine extends PersistenceEngine, Countable, Readable, Deletable, Updateable {

}
