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
package org.eclipse.fennec.persistence.eclipselink.indirection;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopier;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.queries.ReadQuery;

/**
 * 
 * @author Mark Hoffmann
 * @since 12.01.2025
 */
public class EBasicIndirectionPolicy extends BasicIndirectionPolicy {

	private static final Logger LOG = Logger.getLogger(EBasicIndirectionPolicy.class.getName());

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private final EReference reference;
	private final EDynamicType type;
	private final boolean indirection;

	/**
	 * Creates a new instance.
	 */
	public EBasicIndirectionPolicy(DatabaseMapping mapping, EReference reference, EDynamicType type) {
		requireNonNull(mapping);
		requireNonNull(reference);
		this.type = type;
		this.reference = reference;
		this.indirection = !MappingHelper.isContainmentReference(reference);
		setMapping(mapping);
	}

	/**
	 * Overrides the default lazy-load hook: instead of returning a QueryBasedValueHolder
	 * that will trigger a SELECT on first access, we read the FK straight from the source
	 * row, build an EclipseLink-managed dynamic proxy for the target EClass, set the PK
	 * attribute + an {@code eProxyURI} that targets the JPA resource, and return the proxy
	 * directly. Subsequent {@code eGet} on the proxy flows through
	 * {@code ResourceSet.getEObject} → {@code JPAResourceImpl.getEObject} →
	 * {@code em.find}, at which point the one-and-only DB round-trip for the target runs.
	 * <p>
	 * For containment references we delegate to the parent policy (the containment
	 * branch is never configured with indirection anyway — {@link #usesIndirection()}
	 * returns {@code false} — but we guard defensively).
	 */
	@Override
	public Object valueFromQuery(ReadQuery query, AbstractRecord row, Object sourceObject, AbstractSession session) {
		if (!usesIndirection() || !(mapping instanceof ObjectReferenceMapping refMapping)) {
			return super.valueFromQuery(query, row, sourceObject, session);
		}
		EObject proxy = buildLazyProxy(refMapping, row);
		if (isNull(proxy)) {
			return super.valueFromQuery(query, row, sourceObject, session);
		}
		return proxy;
	}

	@Override
	public Object valueFromQuery(ReadQuery query, AbstractRecord row, AbstractSession session) {
		if (!usesIndirection() || !(mapping instanceof ObjectReferenceMapping refMapping)) {
			return super.valueFromQuery(query, row, session);
		}
		EObject proxy = buildLazyProxy(refMapping, row);
		if (isNull(proxy)) {
			return super.valueFromQuery(query, row, session);
		}
		return proxy;
	}

	/**
	 * Reads the FK from the source row (no DB call) and materialises a minimal EMF proxy
	 * for the target. Returns {@code null} if the FK is unavailable or the proxy cannot
	 * be instantiated — callers must then fall back to the eager/VH path.
	 */
	private EObject buildLazyProxy(ObjectReferenceMapping refMapping, AbstractRecord row) {
		Object targetPk = refMapping.extractPrimaryKeysForReferenceObjectFromRow(row);
		if (isNull(targetPk)) {
			return null;
		}
		// Extract the primitive id value from a CacheId composite, if present.
		Object idValue = unwrapSinglePk(targetPk);
		return buildTargetProxy(idValue);
	}

	/**
	 * Materialises a minimal EMF proxy (id attribute + {@code eProxyURI}) for the target
	 * of this policy's reference, identified by the given id value. Returns {@code null}
	 * if the target descriptor, id attribute or base URI required for a resolvable proxy
	 * is unavailable — callers must then fall back to full materialisation.
	 */
	protected EObject buildTargetProxy(Object idValue) {
		if (isNull(idValue)) {
			return null;
		}
		ClassDescriptor targetDescriptor = getForeignReferenceMapping().getReferenceDescriptor();
		if (isNull(targetDescriptor)) {
			return null;
		}
		Object instance;
		try {
			instance = targetDescriptor.getInstantiationPolicy().buildNewInstance();
		} catch (DescriptorException e) {
			LOG.log(Level.FINE, "Unable to build proxy instance for lazy non-containment ref", e);
			return null;
		}
		if (!(instance instanceof EObject proxy)) {
			return null;
		}
		EAttribute idAttr = reference.getEReferenceType().getEIDAttribute();
		if (isNull(idAttr)) {
			return null;
		}
		proxy.eSet(idAttr, idValue);
		URI baseURI = type.getContext().getBaseURI();
		if (isNull(baseURI)) {
			return null;
		}
		URI proxyURI = baseURI
				.appendSegment(reference.getEReferenceType().getName())
				.appendFragment("//" + reference.getName() + "/" + idAttr.getName() + "/" + idValue);
		((InternalEObject) proxy).eSetProxyURI(proxyURI);
		return proxy;
	}

	/**
	 * Builds a fresh lightweight proxy for the given materialised target object using
	 * its EMF id attribute value. Returns {@code null} when the object carries no usable
	 * id — callers should then keep the original value.
	 */
	protected EObject proxify(EObject target) {
		EAttribute idAttr = target.eClass().getEIDAttribute();
		if (isNull(idAttr)) {
			return null;
		}
		Object idValue = target.eGet(idAttr);
		return buildTargetProxy(idValue);
	}

	/**
	 * Returns the {@link EReference} this policy handles.
	 * @return the reference
	 */
	protected EReference getReference() {
		return reference;
	}

	/**
	 * Returns the owning dynamic type.
	 * @return the type
	 */
	protected EDynamicType getDynamicType() {
		return type;
	}

	/**
	 * EclipseLink returns a single PK value directly when the descriptor's cache key type
	 * is ID_VALUE, or wraps composite PKs in a CacheId. We only support single-field PKs
	 * here — that's the case for every model EClass with exactly one EID attribute.
	 */
	private Object unwrapSinglePk(Object targetPk) {
		if (targetPk instanceof CacheId cacheId) {
			Object[] pk = cacheId.getPrimaryKey();
			return pk.length == 1 ? pk[0] : null;
		}
		return targetPk;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#buildIndirectObject(org.eclipse.persistence.indirection.ValueHolderInterface)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object buildIndirectObject(ValueHolderInterface valueHolder) {
		if (isNull(valueHolder)) {
			return null;
		}
		if (nonNull(valueHolder.getValue()) &&
				valueHolder.getValue() instanceof EObject eValue &&
				!eValue.eIsProxy()) {
			URI baseURI = type.getBaseURI();
			String id = EcoreUtil.getID(eValue);
			EAttribute idAttribute = eValue.eClass().getEIDAttribute();
			if (nonNull(id) &&
					nonNull(idAttribute)) {
				baseURI = baseURI.appendFragment("//" + reference.getName() + "/" + idAttribute.getName() + "/" + id);
			}
			// Create a proxy copy instead of modifying the cached object directly.
			// Setting eSetProxyURI on the original would corrupt EclipseLink's cache,
			// causing other lookups to receive a proxy instead of the real object.
			EObject proxyCopy = EcoreUtil.copy(eValue);
			((InternalEObject) proxyCopy).eSetProxyURI(baseURI);
			valueHolder.setValue(proxyCopy);
		}
		return valueHolder;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#backupCloneAttribute(java.lang.Object, java.lang.Object, java.lang.Object, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl)
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public Object backupCloneAttribute(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
		requireNonNull(reference);
		Object attrObject = attributeValue instanceof ValueHolderInterface<?> avh ? avh.getValue() : attributeValue;
		if (attrObject instanceof EObject attrEO && 
				clone instanceof EObject cloneEO && 
				backup instanceof EObject && 
				MappingHelper.areInContainmentRelation(cloneEO, attrEO, reference)) {
			EObject backupAttr = (EObject) getForeignReferenceMapping().getReferenceDescriptor().getCopyPolicy().buildClone(attrObject, unitOfWork);
			new ECopier(backupAttr, null).copy(attrEO);
			if (usesIndirection()) {
				if (attributeValue instanceof ValueHolderInterface avh) {
					avh.setValue(backupAttr);
				} else {
					return new ValueHolder<>(backupAttr);
				}
			} else {
				return backupAttr;
			}
		}
		if (!usesIndirection()) {
			return mapping.buildBackupCloneForPartObject(attributeValue, clone, backup, unitOfWork);
		} else {
			return super.backupCloneAttribute(attributeValue, clone, backup, unitOfWork);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#validateAttributeOfInstantiatedObject(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object validateAttributeOfInstantiatedObject(Object attributeValue) {
		if (!usesIndirection()) {
			return attributeValue;
		}
		// Lazy proxy (from valueFromQuery): leave untouched. Wrapping it in a VH would
		// force EReferenceAccessor.unwrapValueHolder to call vh.getValue() on access,
		// which would still return the proxy — but the EMF eIsProxy() contract on the
		// raw slot value would be lost and eGet(feature, false) would see the VH, not
		// the proxy.
		if (attributeValue instanceof EObject eo && eo.eIsProxy()) {
			return attributeValue;
		}
		if (attributeValue instanceof EObject eo) {
			return new ValueHolder<EObject>(eo);
		}
		if (attributeValue instanceof Collection attrCollection) {
			return new ValueHolder<>(new LinkedList<>(attrCollection));
		}
		return super.validateAttributeOfInstantiatedObject(attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#objectIsInstantiated(java.lang.Object)
	 */
	@Override
	public boolean objectIsInstantiated(Object object) {
		if (!usesIndirection()) {
			return true;
		}
		// AP-46 lazy proxy: the attribute slot holds an EObject directly (not a VH).
		// Such an object already represents a materialised identity — tell EclipseLink
		// it is instantiated so change-tracking does not try to cast it to VH.
		if (object instanceof EObject) {
			return true;
		}
		return super.objectIsInstantiated(object);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.IndirectionPolicy#usesTransparentIndirection()
	 */
	@Override
	public boolean usesTransparentIndirection() {
		return true;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.IndirectionPolicy#usesIndirection()
	 */
	@Override
	public boolean usesIndirection() {
		return indirection;
		//		return super.usesIndirection();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#reset(java.lang.Object)
	 */
	@Override
	public void reset(Object target) {
		if (!usesIndirection()) {
			return;
		}
		super.reset(target);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#setRealAttributeValueInObject(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setRealAttributeValueInObject(Object target, Object attributeValue) {
		if (!usesIndirection()) {
			// Pattern-matching instanceof guards both casts: a non-EObject owner
			// or a null attribute value makes containment-check impossible, so skip.
			if (target instanceof EObject targetEO
					&& (isNull(attributeValue) || attributeValue instanceof EObject)
					&& !MappingHelper.areInContainmentRelation(targetEO, (EObject) attributeValue, reference)) {
				mapping.setAttributeValueInObject(target, attributeValue);
			}
		} else {
			Object attrValue = this.mapping.getAttributeValueFromObject(target);
			if (attributeValue instanceof ValueHolderInterface vh) {
				vh.setValue(attributeValue);
			} else {
				attrValue = new ValueHolder<>(attributeValue);
			}
			mapping.setAttributeValueInObject(target, attrValue);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#cloneAttribute(java.lang.Object, java.lang.Object, org.eclipse.persistence.internal.identitymaps.CacheKey, java.lang.Object, java.lang.Integer, org.eclipse.persistence.internal.sessions.AbstractSession, boolean)
	 */
	@Override
	public Object cloneAttribute(Object attributeValue, Object original, CacheKey cacheKey, Object clone,
			Integer refreshCascade, AbstractSession cloningSession, boolean buildDirectlyFromRow) {
		if (!usesIndirection()) {
			// Since valueFromRow was called with the UnitOfWork, attributeValue
	        // is already a registered result.
	        if (buildDirectlyFromRow) {
	            return attributeValue;
	        }
	        if (!cloningSession.isUnitOfWork()){
	            return mapping.buildContainerClone(attributeValue, cloningSession);
	        }
	        boolean isExisting = !cloningSession.isUnitOfWork() || (((UnitOfWorkImpl) cloningSession).isObjectRegistered(clone) && (!(((UnitOfWorkImpl)cloningSession).isOriginalNewObject(original))));
	        return this.getMapping().buildCloneForPartObject(attributeValue, original, cacheKey, clone, cloningSession, refreshCascade, isExisting, isExisting);// only assume from shared cache if it is existing
		}
		// Lazy non-containment: valueFromQuery already returned an EMF proxy; the UoW
		// clone slot should hold a proxy (lightweight — just ID + eProxyURI).
		// Resolution happens on first eGet, not during cloning.
		if (attributeValue instanceof EObject eo) {
			return proxyForClone(eo);
		}
		// The indirection normalisation wraps materialised values in a plain ValueHolder
		// before cloning — unwrap, proxify and re-wrap so the clone still sees a holder.
		if (attributeValue instanceof ValueHolderInterface<?> vh
				&& vh.isInstantiated()
				&& vh.getValue() instanceof EObject eo) {
			return new ValueHolder<>(proxyForClone(eo));
		}
		return super.cloneAttribute(attributeValue, original, cacheKey, clone, refreshCascade, cloningSession,
				buildDirectlyFromRow);
	}

	/**
	 * Returns the value a UnitOfWork clone slot should hold for the given non-containment
	 * target: proxies pass through, materialised values (e.g. the shared-cache original)
	 * are replaced by a fresh lightweight proxy. Sharing the materialised instance would
	 * hand out an object without {@code eResource} (nested proxies could never resolve)
	 * and let EMF opposite maintenance link clones into the shared cache.
	 */
	private EObject proxyForClone(EObject eo) {
		if (eo.eIsProxy()) {
			return eo;
		}
		EObject proxy = proxify(eo);
		return nonNull(proxy) ? proxy : eo;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#getRealAttributeValueFromObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object getRealAttributeValueFromObject(Object object, Object attribute) {
		// When the slot holds an EMF proxy (our lazy non-containment case), there is
		// no ValueHolder to unwrap — return the proxy itself. Default impl would do
		// ((ValueHolderInterface) attribute).getValue() and throw ClassCastException.
		if (attribute instanceof EObject) {
			return attribute;
		}
		return super.getRealAttributeValueFromObject(object, attribute);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy#extractPrimaryKeyForReferenceObject(java.lang.Object, org.eclipse.persistence.internal.sessions.AbstractSession)
	 */
	@Override
	public Object extractPrimaryKeyForReferenceObject(Object referenceObject, AbstractSession session) {
		// The proxy carries the id attribute value directly — don't force resolution.
		if (referenceObject instanceof EObject eo && eo.eIsProxy()) {
			return mapping.getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(eo, session);
		}
		return super.extractPrimaryKeyForReferenceObject(referenceObject, session);
	}

}
