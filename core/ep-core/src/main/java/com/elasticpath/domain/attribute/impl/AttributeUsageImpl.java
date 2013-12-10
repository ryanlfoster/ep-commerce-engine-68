/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.attribute.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Transient;

import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * A default implementation of a specification of <code>AttributeUsage</code>.
 * 
 * Note: this class allows extension of the types that can make use of Attributes.
 * See <code>Attribute</code> for the current static definitions.  Also have a look
 * at <code>AttributeServiceImpl</code> especially getAttributesExcludeCustomerProfile()
 * for an example of code that may assume only a specific set of attributes.
 * 
 * Uses default types specified on interface.
 * Allows type enhancements to be wired in using the setAddedTypes() method.
 */
public class AttributeUsageImpl extends AbstractPersistableImpl implements AttributeUsage {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private static Map<Integer, AttributeUsage> usages = new LinkedHashMap<Integer, AttributeUsage>();

	private int value;

	private String name; // optional name; only used to create the usage map.

	private long uidPk;

	/**
	 * Empty default constructor.
	 */
	public AttributeUsageImpl() {
		// default
	}

	private AttributeUsageImpl(final int value, final String name) {
		this.name = name;
		this.value = value;
	}

	/** The category usage. */
	public static final AttributeUsage CATEGORY_USAGE = create(CATEGORY, "Category");

	/** The product usage. */
	public static final AttributeUsage PRODUCT_USAGE = create(PRODUCT, "Product");

	/** The sku usage. */
	public static final AttributeUsage SKU_USAGE = create(SKU, "SKU");

	/** The customerprofile usage. */
	public static final AttributeUsage CUSTOMERPROFILE_USAGE = create(CUSTOMERPROFILE, "CustomerProfile");
	
	/**
	 * The extensible part of the Extensible Enum.
	 * This allows us to create new enums safely.
	 * Watch the number used.. the parent <code>AttributeUsage</code> interface defines 4.
	 * @param code The int code to use.
	 * @param name The name of the AttributeUsage.
	 * @return return the new Attribute Usage.
	 */
	public static AttributeUsage create(final int code, final String name) {
		final AttributeUsage attributeUsage;
		if (usages.containsKey(code)) {
			attributeUsage = usages.get(code);
		} else {
			attributeUsage = new AttributeUsageImpl(code, name);
			usages.put(code, attributeUsage);			
		}
		return attributeUsage;
	}

	/**
	 * Return a map of attribute usages keyed by their value (int id). Created to get rid of duplicate info in the attribute service.
	 *
	 * @return map of attribute usages.
	 * @Override
	 */
	public Map<String, String> getAttributeUsageMap() {
		return getAttributeUsageMapInternal();
	}

	/**
	 * Return a map of attribute usages keyed by their value (int id). Created to get rid of duplicate info in the attribute service.
	 *
	 * @return map of attribute usages.
	 * @Override
	 */
	public static Map<String, String> getAttributeUsageMapInternal() {
		Map<String, String> usageMap = new LinkedHashMap<String, String>();
		for (AttributeUsage usage : usages.values()) {
			usageMap.put(String.valueOf(usage.getValue()), usage.toString());
		}
		return usageMap;
	}

	/**
	 * Returns an <code>AttributeUsage</code> of the given id.
	 *
	 * @param usageId the attribute usage id
	 * @return an <code>AttributeUsage</code> of the given id
	 */
	public AttributeUsage getAttributeUsageById(final int usageId) {
		return getAttributeUsageByIdInternal(usageId);
	}
	
	/**
	 * Returns an <code>AttributeUsage</code> of the given id.
	 *
	 * @param usageId the attribute usage id
	 * @return an <code>AttributeUsage</code> of the given id
	 */
	public static AttributeUsage getAttributeUsageByIdInternal(final int usageId) {
		AttributeUsage usage = usages.get(usageId);
		if (usage == null) {
			throw new AttributeUsageTypeException("UsageId = " + usageId + " not supported.");
		}
		return usage;
	}
	
	/**
	 * Returns the attribute usage value.
	 *
	 * @return the attribute usage value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Sets the attribute usage value.
	 * 
	 * Used for testing.
	 *
	 * @param value the attribute usage value.
	 */
	public void setValue(final int value) {
		this.value = value;
	}

	/**
	 * Returns the name of object if defined.
	 *
	 * @return attribute usage name.
	 */
	public String toString() {
		if (name == null) {
			return super.toString();
		}
		return name;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Transient
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
	/**
	 * Interface for adding types.
	 * You can use this to wire in changes.
	 * 
	 * @param addedTypes the map to wire in.
	 */
	public void setAddedTypes(final Map<String, String> addedTypes) {
		for (String key : addedTypes.keySet()) {
			create(Integer.valueOf(key), addedTypes.get(key));
		}
	}
}