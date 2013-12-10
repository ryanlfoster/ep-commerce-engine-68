/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.support.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.persistence.support.DistinctAttributeValueCriterion;

/**
 * Creates criterion for querying the persistence layer for distinct lists of attribute values.
 */
public class DistinctAttributeValueCriterionImpl implements DistinctAttributeValueCriterion {

	private static final String PRODUCT_ATTRIBUTE_VALUE_QUERY_SECTION = "from Product as p inner join p.attributeValueGroup.attributeValueMap "
		+ "as av where av.attribute.uidPk = ";

	private static final String CATEGORY_ATTRIBUTE_VALUE_QUERY_SECTION = "from Category as c inner join c.attributeValueGroup.attributeValueMap "
		+ "as av where av.attribute.uidPk = ";

	private static final String SKU_ATTRIBUTE_VALUE_QUERY_SECTION = "from ProductSku as p inner join p.attributeValueGroup.attributeValueMap "
		+ "as av where av.attribute.uidPk = ";

	/** Maps attribute types to the fields in which the values for that type are stored in an AttributeValue object. */
	private static Map<Integer, String> attributeTypeToPersistenceFieldMap = null;

	private static Map<Integer, String> attributeUsageToQueryStringMap = null;

	static {

		attributeTypeToPersistenceFieldMap = new HashMap<Integer, String>();

		for (final AttributeType type : AttributeType.values()) {
			attributeTypeToPersistenceFieldMap.put(Integer.valueOf(type.getTypeId()), type.getStorageType());
		}

		attributeUsageToQueryStringMap = new HashMap<Integer, String>();
		attributeUsageToQueryStringMap.put(Integer.valueOf(AttributeUsage.PRODUCT), PRODUCT_ATTRIBUTE_VALUE_QUERY_SECTION);
		attributeUsageToQueryStringMap.put(Integer.valueOf(AttributeUsage.SKU), SKU_ATTRIBUTE_VALUE_QUERY_SECTION);
		attributeUsageToQueryStringMap.put(Integer.valueOf(AttributeUsage.CATEGORY), CATEGORY_ATTRIBUTE_VALUE_QUERY_SECTION);

	}

	/**
	 * Creates a criterion String for querying the persistence layer for a distinct list of values that are present for a given
	 * <code>AttributeValue</code>.
	 *
	 * @param attribute the <code>Attribute</code> whose values are to be returned
	 * @return a distinct list of attribute values for that attribute
	 */
	public String getDistinctAttributeValueCriterion(final Attribute attribute) {
		final StringBuffer query = new StringBuffer();
		query.append("select distinct av.");
		query.append(attributeTypeToPersistenceFieldMap.get(Integer.valueOf(attribute.getAttributeType().getTypeId())));
		query.append(", av.localizedAttributeKey ");
		query.append(attributeUsageToQueryStringMap.get(Integer.valueOf(attribute.getAttributeUsage().getValue())));
		query.append('\'');
		query.append(attribute.getUidPk());
		query.append('\'');
		return query.toString();
	}

	/**
	 * Creates a criterion String for querying the persistence layer for a distinct list of values that are present for a given
	 * <code>AttributeValue</code>.
	 * These search all the multi values for short text type.
	 *
	 * @param attribute the <code>Attribute</code> whose values are to be returned
	 * @return a distinct list of attribute values for that attribute
	 */
	public String getDistinctAttributeMultiValueCriterion(final Attribute attribute) {
		final StringBuffer query = new StringBuffer();
		query.append("select distinct av.longTextValue, av.localizedAttributeKey ");
		query.append(attributeUsageToQueryStringMap.get(Integer.valueOf(attribute.getAttributeUsage().getValue())));
		query.append("'" + attribute.getUidPk() + "'");
		return query.toString();
	}

}
