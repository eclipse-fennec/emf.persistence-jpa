/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.converter;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Value converter for {@link XMLGregorianCalendar}
 * @author Mark Hoffmann
 * @since 16.03.2020
 */
public class XMLGregorianCalendarConverter implements TypeConverter {
	
	private static final Logger logger = Logger.getLogger(XMLGregorianCalendarConverter.class.getName());

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.converter.TypeConverter#convertValueToEMF(org.eclipse.emf.ecore.EClassifier, java.lang.Object)
	 */
	@Override
	public Object convertValueToEMF(EClassifier eDataType, Object databaseValue) {
		Class<?> instanceClass = eDataType.getInstanceClass();
		if (instanceClass != null && instanceClass.equals(XMLGregorianCalendar.class)) {
			Date date;
			if (databaseValue instanceof Long) {
				date = new Date((long) databaseValue);
			} else if (databaseValue instanceof Date) {
				date = (Date) databaseValue;
			} else {
				logger.log(Level.WARNING, String.format("Cannot convert '%s' into XMLGregorianCalendar", databaseValue));
				return null;
			}
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date);
			XMLGregorianCalendar c;
			try {
				c = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
				return c;
			} catch (DatatypeConfigurationException e) {
				logger.log(Level.SEVERE, "Cannot instanciate XMLGregorianCalendar", e);
			}
		}
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.converter.TypeConverter#convertEMFToValue(org.eclipse.emf.ecore.EClassifier, java.lang.Object)
	 */
	@Override
	public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
		Class<?> instanceClass = eDataType.getInstanceClass();
		if (instanceClass != null && instanceClass.equals(XMLGregorianCalendar.class)) {
			XMLGregorianCalendar c = (XMLGregorianCalendar) emfValue;
			return c.toGregorianCalendar().getTime();
		}
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.converter.TypeConverter#isConverterForType(org.eclipse.emf.ecore.EClassifier)
	 */
	@Override
	public boolean isConverterForType(EClassifier eDataType) {
		if (eDataType instanceof EDataType) {
			Class<?> instanceClass = eDataType.getInstanceClass();
			if (instanceClass != null && instanceClass.equals(XMLGregorianCalendar.class)) {
				return true;
			}
		}
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.api.TypeConverter#getName()
	 */
	@Override
	public String getName() {
		return "XMLGregorianCalendar";
	}

}
