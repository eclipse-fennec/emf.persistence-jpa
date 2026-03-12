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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapped Superclass</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         Defines the settings and mappings for a mapped superclass. Is 
 *         allowed to be sparsely populated and used in conjunction with 
 *         the annotations. Alternatively, the metadata-complete attribute 
 *         can be used to indicate that no annotations are to be processed 
 *         If this is the case then the defaulting rules will be recursively 
 *         applied.
 * 
 *         @Target(TYPE) @Retention(RUNTIME)
 *         public @interface MappedSuperclass{}
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getIdClass <em>Id Class</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getExcludeDefaultListeners <em>Exclude Default Listeners</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getExcludeSuperclassListeners <em>Exclude Superclass Listeners</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getEntityListeners <em>Entity Listeners</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPrePersist <em>Pre Persist</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostPersist <em>Post Persist</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPreRemove <em>Pre Remove</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostRemove <em>Post Remove</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPreUpdate <em>Pre Update</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostUpdate <em>Post Update</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostLoad <em>Post Load</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getAccess <em>Access</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getClass_ <em>Class</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#isMetadataComplete <em>Metadata Complete</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass()
 * @model extendedMetaData="name='mapped-superclass' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface MappedSuperclass extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_Description()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='description' namespace='##targetNamespace'"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Id Class</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id Class</em>' containment reference.
	 * @see #setIdClass(IdClass)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_IdClass()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='id-class' namespace='##targetNamespace'"
	 * @generated
	 */
	IdClass getIdClass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getIdClass <em>Id Class</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Class</em>' containment reference.
	 * @see #getIdClass()
	 * @generated
	 */
	void setIdClass(IdClass value);

	/**
	 * Returns the value of the '<em><b>Exclude Default Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclude Default Listeners</em>' containment reference.
	 * @see #setExcludeDefaultListeners(EmptyType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_ExcludeDefaultListeners()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='exclude-default-listeners' namespace='##targetNamespace'"
	 * @generated
	 */
	EmptyType getExcludeDefaultListeners();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getExcludeDefaultListeners <em>Exclude Default Listeners</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclude Default Listeners</em>' containment reference.
	 * @see #getExcludeDefaultListeners()
	 * @generated
	 */
	void setExcludeDefaultListeners(EmptyType value);

	/**
	 * Returns the value of the '<em><b>Exclude Superclass Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclude Superclass Listeners</em>' containment reference.
	 * @see #setExcludeSuperclassListeners(EmptyType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_ExcludeSuperclassListeners()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='exclude-superclass-listeners' namespace='##targetNamespace'"
	 * @generated
	 */
	EmptyType getExcludeSuperclassListeners();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getExcludeSuperclassListeners <em>Exclude Superclass Listeners</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclude Superclass Listeners</em>' containment reference.
	 * @see #getExcludeSuperclassListeners()
	 * @generated
	 */
	void setExcludeSuperclassListeners(EmptyType value);

	/**
	 * Returns the value of the '<em><b>Entity Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entity Listeners</em>' containment reference.
	 * @see #setEntityListeners(EntityListeners)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_EntityListeners()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='entity-listeners' namespace='##targetNamespace'"
	 * @generated
	 */
	EntityListeners getEntityListeners();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getEntityListeners <em>Entity Listeners</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Entity Listeners</em>' containment reference.
	 * @see #getEntityListeners()
	 * @generated
	 */
	void setEntityListeners(EntityListeners value);

	/**
	 * Returns the value of the '<em><b>Pre Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pre Persist</em>' containment reference.
	 * @see #setPrePersist(PrePersist)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_PrePersist()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='pre-persist' namespace='##targetNamespace'"
	 * @generated
	 */
	PrePersist getPrePersist();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPrePersist <em>Pre Persist</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pre Persist</em>' containment reference.
	 * @see #getPrePersist()
	 * @generated
	 */
	void setPrePersist(PrePersist value);

	/**
	 * Returns the value of the '<em><b>Post Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Post Persist</em>' containment reference.
	 * @see #setPostPersist(PostPersist)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_PostPersist()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='post-persist' namespace='##targetNamespace'"
	 * @generated
	 */
	PostPersist getPostPersist();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostPersist <em>Post Persist</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Post Persist</em>' containment reference.
	 * @see #getPostPersist()
	 * @generated
	 */
	void setPostPersist(PostPersist value);

	/**
	 * Returns the value of the '<em><b>Pre Remove</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pre Remove</em>' containment reference.
	 * @see #setPreRemove(PreRemove)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_PreRemove()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='pre-remove' namespace='##targetNamespace'"
	 * @generated
	 */
	PreRemove getPreRemove();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPreRemove <em>Pre Remove</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pre Remove</em>' containment reference.
	 * @see #getPreRemove()
	 * @generated
	 */
	void setPreRemove(PreRemove value);

	/**
	 * Returns the value of the '<em><b>Post Remove</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Post Remove</em>' containment reference.
	 * @see #setPostRemove(PostRemove)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_PostRemove()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='post-remove' namespace='##targetNamespace'"
	 * @generated
	 */
	PostRemove getPostRemove();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostRemove <em>Post Remove</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Post Remove</em>' containment reference.
	 * @see #getPostRemove()
	 * @generated
	 */
	void setPostRemove(PostRemove value);

	/**
	 * Returns the value of the '<em><b>Pre Update</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pre Update</em>' containment reference.
	 * @see #setPreUpdate(PreUpdate)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_PreUpdate()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='pre-update' namespace='##targetNamespace'"
	 * @generated
	 */
	PreUpdate getPreUpdate();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPreUpdate <em>Pre Update</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pre Update</em>' containment reference.
	 * @see #getPreUpdate()
	 * @generated
	 */
	void setPreUpdate(PreUpdate value);

	/**
	 * Returns the value of the '<em><b>Post Update</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Post Update</em>' containment reference.
	 * @see #setPostUpdate(PostUpdate)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_PostUpdate()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='post-update' namespace='##targetNamespace'"
	 * @generated
	 */
	PostUpdate getPostUpdate();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostUpdate <em>Post Update</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Post Update</em>' containment reference.
	 * @see #getPostUpdate()
	 * @generated
	 */
	void setPostUpdate(PostUpdate value);

	/**
	 * Returns the value of the '<em><b>Post Load</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Post Load</em>' containment reference.
	 * @see #setPostLoad(PostLoad)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_PostLoad()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='post-load' namespace='##targetNamespace'"
	 * @generated
	 */
	PostLoad getPostLoad();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostLoad <em>Post Load</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Post Load</em>' containment reference.
	 * @see #getPostLoad()
	 * @generated
	 */
	void setPostLoad(PostLoad value);

	/**
	 * Returns the value of the '<em><b>Attributes</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attributes</em>' containment reference.
	 * @see #setAttributes(Attributes)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_Attributes()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='attributes' namespace='##targetNamespace'"
	 * @generated
	 */
	Attributes getAttributes();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getAttributes <em>Attributes</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Attributes</em>' containment reference.
	 * @see #getAttributes()
	 * @generated
	 */
	void setAttributes(Attributes value);

	/**
	 * Returns the value of the '<em><b>Access</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.persistence.eorm.AccessType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Access</em>' attribute.
	 * @see org.eclipse.fennec.persistence.eorm.AccessType
	 * @see #isSetAccess()
	 * @see #unsetAccess()
	 * @see #setAccess(AccessType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_Access()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='attribute' name='access'"
	 * @generated
	 */
	AccessType getAccess();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getAccess <em>Access</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Access</em>' attribute.
	 * @see org.eclipse.fennec.persistence.eorm.AccessType
	 * @see #isSetAccess()
	 * @see #unsetAccess()
	 * @see #getAccess()
	 * @generated
	 */
	void setAccess(AccessType value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getAccess <em>Access</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetAccess()
	 * @see #getAccess()
	 * @see #setAccess(AccessType)
	 * @generated
	 */
	void unsetAccess();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getAccess <em>Access</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Access</em>' attribute is set.
	 * @see #unsetAccess()
	 * @see #getAccess()
	 * @see #setAccess(AccessType)
	 * @generated
	 */
	boolean isSetAccess();

	/**
	 * Returns the value of the '<em><b>Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Class</em>' reference.
	 * @see #setClass(EClass)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_Class()
	 * @model required="true"
	 *        extendedMetaData="kind='attribute' name='class'"
	 * @generated
	 */
	EClass getClass_();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getClass_ <em>Class</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Class</em>' reference.
	 * @see #getClass_()
	 * @generated
	 */
	void setClass(EClass value);

	/**
	 * Returns the value of the '<em><b>Metadata Complete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Metadata Complete</em>' attribute.
	 * @see #isSetMetadataComplete()
	 * @see #unsetMetadataComplete()
	 * @see #setMetadataComplete(boolean)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getMappedSuperclass_MetadataComplete()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='metadata-complete'"
	 * @generated
	 */
	boolean isMetadataComplete();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#isMetadataComplete <em>Metadata Complete</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Metadata Complete</em>' attribute.
	 * @see #isSetMetadataComplete()
	 * @see #unsetMetadataComplete()
	 * @see #isMetadataComplete()
	 * @generated
	 */
	void setMetadataComplete(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#isMetadataComplete <em>Metadata Complete</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMetadataComplete()
	 * @see #isMetadataComplete()
	 * @see #setMetadataComplete(boolean)
	 * @generated
	 */
	void unsetMetadataComplete();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#isMetadataComplete <em>Metadata Complete</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Metadata Complete</em>' attribute is set.
	 * @see #unsetMetadataComplete()
	 * @see #isMetadataComplete()
	 * @see #setMetadataComplete(boolean)
	 * @generated
	 */
	boolean isSetMetadataComplete();

} // MappedSuperclass
