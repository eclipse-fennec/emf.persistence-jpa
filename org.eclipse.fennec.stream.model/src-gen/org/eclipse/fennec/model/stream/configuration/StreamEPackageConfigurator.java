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
package org.eclipse.fennec.model.stream.configuration;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;

import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;

import org.eclipse.fennec.model.stream.StreamPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>EPackageConfiguration</b> and <b>ResourceFactoryConfigurator</b> for the model.
 * The package will be registered into a OSGi base model registry.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Fennec change-stream metamodel (the 'diff-Ecore'). Promoted from the unified-persistence concept draft — see docs/unified-persistence/concept.md (§5, §20).
 * 
 * DISCIPLINE (concept §12.3): this package is the ONLY schema sender and receiver must share. It cannot fingerprint-bootstrap itself, so the protobuf doctrine applies manually: evolution is additive-only (append, never remove/reorder), the semantics of an existing feature are never rebound. Keep it small and boring — convenience belongs in projections, not here.
 * 
 * Values (valueOld/valueNew/value) are encoded literals or object IDs as strings; their typing resolves EXCLUSIVELY from (contextFingerprint, featureId) via the snapshot registry. Deliberately no type flags in this model.
 * <!-- end-model-doc -->
 * @see EPackageConfigurator
 * @generated
 */
public class StreamEPackageConfigurator implements EPackageConfigurator {
	
	private StreamPackage ePackage;

	protected StreamEPackageConfigurator(StreamPackage ePackage){
		this.ePackage = ePackage;
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.osgi.EPackageRegistryConfigurator#configureEPackage(org.eclipse.emf.ecore.EPackage.Registry)
	 * @generated
	 */
	@Override
	public void configureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {
		registry.put(StreamPackage.eNS_URI, ePackage);
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.osgi.EPackageRegistryConfigurator#unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry)
	 * @generated
	 */
	@Override
	public void unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {
		registry.remove(StreamPackage.eNS_URI);
	}
	
	/**
	 * A method providing the Properties the services around this Model should be registered with.
	 * @generated
	 */
	public Map<String, Object> getServiceProperties() {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(EMFNamespaces.EMF_NAME, StreamPackage.eNAME);
		properties.put(EMFNamespaces.EMF_MODEL_NSURI, StreamPackage.eNS_URI);
		properties.put(EMFNamespaces.EMF_MODEL_REGISTRATION, EMFNamespaces.MODEL_REGISTRATION_PROVIDED);
		properties.put(EMFNamespaces.EMF_MODEL_FILE_EXT, "stream");
		properties.put(EMFNamespaces.EMF_MODEL_VERSION, "1.0");
		return properties;
	}
}