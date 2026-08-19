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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.dynamic.ValuesAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Carries the value of a <em>synthetic</em> primary key — one the model does not have a feature
 * for (issue #184).
 * <p>
 * An EClass without an id attribute still needs a key, and {@code CompositeIdAnalyzer} answers
 * that with a synthetic {@code pk_<name>} column. EclipseLink insists that a primary key field
 * has a writable mapping ({@code EclipseLink-46}, and {@code EclipseLink-41} for the sequence
 * field), so the column needs somewhere to live on the object — and the object is an
 * {@link EObject} whose features come from a model we do not own. A map entry class is the case
 * that forces this: EMF puts {@code key} and {@code value} in it and nothing else, ever.
 * <p>
 * The value therefore rides on an {@link Adapter} attached to the object rather than in a
 * feature. That keeps the model untouched, survives for as long as the object does, and is
 * per-object rather than per-thread. It also means the synthetic key is <b>not part of the
 * object's identity in EMF</b> — two loads of the same row are equal to EMF exactly as they were
 * before, and nothing serializes it. It exists for the database and nowhere else.
 *
 * @author Mark Hoffmann
 * @since 19.08.2026
 */
public class ESyntheticKeyAccessor extends ValuesAccessor {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	private final String keyName;

	/**
	 * Creates an accessor for a synthetic key column.
	 *
	 * @param mapping the mapping the accessor will be set on
	 * @param keyName the synthetic key's name — the discriminator between two synthetic keys
	 *        on one object, should a composite synthetic key ever exist
	 * @return a new accessor instance
	 */
	public static ESyntheticKeyAccessor create(DatabaseMapping mapping, String keyName) {
		return new ESyntheticKeyAccessor(mapping, keyName);
	}

	private ESyntheticKeyAccessor(DatabaseMapping mapping, String keyName) {
		super(mapping);
		this.keyName = keyName;
	}

	@Override
	public Class<?> getAttributeClass() {
		return Object.class;
	}

	@Override
	public Object getAttributeValueFromObject(Object object) throws DescriptorException {
		if (object instanceof EObject eObject) {
			SyntheticKey holder = holderOf(eObject, false);
			return holder == null ? null : holder.value;
		}
		return null;
	}

	@Override
	public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
		if (object instanceof EObject eObject) {
			holderOf(eObject, true).value = value;
		}
	}

	private SyntheticKey holderOf(EObject object, boolean create) {
		for (Adapter adapter : object.eAdapters()) {
			if (adapter instanceof SyntheticKey key && key.keyName.equals(keyName)) {
				return key;
			}
		}
		if (!create) {
			return null;
		}
		SyntheticKey key = new SyntheticKey(keyName);
		object.eAdapters().add(key);
		return key;
	}

	/**
	 * The holder itself — an adapter that observes nothing. It is a value slot attached to the
	 * object, deliberately not a notifying feature: a change to a synthetic key is not a model
	 * change and must not produce a notification.
	 */
	private static final class SyntheticKey extends AdapterImpl {

		private final String keyName;
		private Object value;

		private SyntheticKey(String keyName) {
			this.keyName = keyName;
		}

		@Override
		public boolean isAdapterForType(Object type) {
			return type == SyntheticKey.class;
		}
	}
}
