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
package org.eclipse.fennec.persistence.mongo.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonValue;
import org.eclipse.emf.common.util.Enumerator;
import org.bson.types.Decimal128;

/**
 * Unwraps {@link BsonValue}s from aggregation result documents into plain Java values
 * for {@code QueryResultRow} cells.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class BsonValues {

	private BsonValues() {
	}

	/**
	 * @param value the BSON value; may be {@code null}
	 * @return the corresponding Java value ({@code null} for BSON null/undefined)
	 */
	/**
	 * Normalises an EMF-typed value to its document encoding — the form the driver's codec
	 * writes and the form a filter compares against.
	 * <p>
	 * Shared by the query translator and by the set-based update of issue #228, deliberately:
	 * a value written by {@code $set} must land exactly as the same value written through the
	 * object codec, or the two update paths would disagree about what they stored.
	 *
	 * @param value the EMF value; may be {@code null}
	 * @return the value in its document form
	 */
	public static Object toDocumentValue(Object value) {
		if (value instanceof Enumerator enumerator) {
			return enumerator.getName();
		}
		return value;
	}

	public static Object toJava(BsonValue value) {
		if (value == null) {
			return null;
		}
		switch (value.getBsonType()) {
		case NULL, UNDEFINED:
			return null;
		case STRING:
			return value.asString().getValue();
		case INT32:
			return value.asInt32().getValue();
		case INT64:
			return value.asInt64().getValue();
		case DOUBLE:
			return value.asDouble().getValue();
		case DECIMAL128:
			Decimal128 decimal = value.asDecimal128().getValue();
			return decimal.bigDecimalValue();
		case BOOLEAN:
			return value.asBoolean().getValue();
		case DATE_TIME:
			return new Date(value.asDateTime().getValue());
		case OBJECT_ID:
			return value.asObjectId().getValue().toHexString();
		case ARRAY:
			BsonArray array = value.asArray();
			List<Object> values = new ArrayList<>(array.size());
			array.forEach(element -> values.add(toJava(element)));
			return values;
		case DOCUMENT:
			return value.asDocument();
		default:
			return value;
		}
	}
}
