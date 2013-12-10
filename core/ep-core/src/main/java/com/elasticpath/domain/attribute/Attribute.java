/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute;

import com.elasticpath.domain.catalog.CatalogObject;
import com.elasticpath.persistence.api.Entity;

/**
 * An <code>Attribute</code> represents a customized property of an object like <code>Category</code> or
 * <code>Product</code>.
 */
public interface Attribute extends Entity, Comparable<Attribute>, CatalogObject {
	/**
	 * Get the attribute key.
	 * @return the attribute key
	 */
	String getKey();
	
	/**
	 * Set the attribute key.
	 * @param key the key to set
	 */
	void setKey(String key);
	
	/**
	 * Return <code>true</code> if the attribute is dependent on the locale.
	 * @return <code>true</code> if the attribute is dependent on the locale
	 */
	boolean isLocaleDependant();
	
	/**
	 * Set the locale-dependent flag.
	 * @param localeDependent the locale-dependent flag.
	 */
	void setLocaleDependant(boolean localeDependent);
	
	/**
	 * Return the <code>AttributeType</code> of this attribute.
	 * @return the <code>AttributeType</code> of this attribute
	 */
	AttributeType getAttributeType();
	
	/**
	 * Set the <code>AttributeType</code> of this attribute.
	 * @param attributeType the attribute type.
	 */
	void setAttributeType(AttributeType attributeType);
	
	
	/**
	 * Get the product system name.
	 * @return the product system name
	 */
	String getName();
	
	/**
	 * Set the product system name.
	 * @param name the product system name
	 */
	void setName(String name);
	
	/**
	 * Return the <code>AttributeUsage</code> of this attribute.
	 * 
	 * @return the <code>AttributeUsage</code> of this attribute
	 */
	AttributeUsage getAttributeUsage();

	/**
	 * Set the <code>AttributeUsage</code> of this attribute.
	 * 
	 * @param attributeUsage the attribute usage
	 */
	void setAttributeUsage(AttributeUsage attributeUsage);
	
	/**
	 * Return <code>true</code> if the attribute is required.
	 * 
	 * @return <code>true</code> if the attribute is required
	 */
	boolean isRequired();

	/**
	 * Set the required flag.
	 * 
	 * @param required the required flag
	 */
	void setRequired(boolean required);
	
	/**
	 * Return <code>true</code> if the value lookup for this attribute is enabled.
	 * If value-lookup is enabled, users have the option of selecting from existing
	 * previously existing attribute values when setting the attribute's value.
	 * @return <code>true</code> if the value lookup for this attribute is enabled.
	 */
	boolean isValueLookupEnabled();
	
	/**
	 * Sets whether or not the user will have the option of selecting from previously
	 * existing values when editing an attribute.
	 * @param valueLookupEnabled set to <code>true</code> if lookup is to be enabled.
	 */
	void setValueLookupEnabled(final boolean valueLookupEnabled);

	/**
	 * Return true if the attribute can have multi value.
	 *
	 * @return true if the attribute can have multi value.
	 */
	boolean isMultiValueEnabled();

	/**
	 * Set the attribute can have multi value.
	 *
	 * @param multiValueEnabled the multiValueEnabled to set
	 */
	void setMultiValueEnabled(final boolean multiValueEnabled);

	/**
	 * Return <code>true</code> if the attribute is system attribute.
	 * 
	 * @return <code>true</code> if the attribute is system attribute
	 */
	boolean isSystem();

	/**
	 * Set the system flag.
	 * 
	 * @param system the system flag
	 */
	void setSystem(final boolean system);

	/**
	 * Set the attribute's usage id.
	 * @param usageId the attribute's usage Id to be set.
	 */
	void setAttributeUsageId(final int usageId);
	
	/**
	 * Gets whether this attribute is global.
	 *
	 * @return whether this attribute is global
	 */
	boolean isGlobal();
	
	/**
	 * Sets whether this attribute is global.
	 * 
	 * @param global whether this attribute is global
	 */
	void setGlobal(final boolean global);
}
