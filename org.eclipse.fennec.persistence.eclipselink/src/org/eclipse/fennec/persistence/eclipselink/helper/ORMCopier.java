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
package org.eclipse.fennec.persistence.eclipselink.helper;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;

/**
 * A simple copy into - mechanisms
 * @author Mark Hoffmann
 * @since 09.01.2025
 */
public class ORMCopier extends Copier {
	
	  /**
	   * Returns a self-contained copy of the eObject.
	   * @param source the object to copy.
	   * @param target the target object to copy into
	   * @return the target Oopy.
	   * @see Copier
	   */
	  public static <T extends EObject> T copyInto(T source, T target)
	  {
		Copier copier = new ORMCopier(target);
	    EObject result = copier.copy(source);
	    copier.copyReferences();
	    
	    @SuppressWarnings("unchecked")T t = (T)result;
	    return t;
	  }
	
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	final EObject source;

	/**
	 * Creates a new instance.
	 */
	public ORMCopier(EObject source) {
		this.source = source;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.util.EcoreUtil.Copier#createCopy(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected EObject createCopy(EObject eObject) {
		requireNonNull(eObject);
		EClass eClass = getTarget(eObject);
		if (Objects.isNull(source) || !source.eClass().equals(eClass)) {
			return super.createCopy(eObject);
		} else {
			return source;
		}
	}
}
