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
package org.eclipse.fennec.persistence.tck;

import java.io.IOException;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.persistence.tck.generated.writepathgen.WritepathgenPackage;

/**
 * The write-path suite on the Mongo backend against the <em>generated</em> write-path model
 * (issue #311), plus the dynamic classes of {@code writepath-mixed.ecore} built on top of it:
 * every case the dynamic model passes, the generated one has to pass as well.
 */
class MongoGeneratedWritePathTckTest extends MongoWritePathTckTest {

	@Override
	protected EPackage loadModel() {
		return WritepathgenPackage.eINSTANCE;
	}

	@Override
	protected EPackage loadMixedModel() throws IOException {
		return loadEcore("writepath-mixed.ecore", wp);
	}
}
