package com.elasticpath.domain.attribute;

import java.util.Set;

import com.elasticpath.persistence.api.Persistable;

/**
 * Represents a group of <code>Attribute</code>s. 
 * It can be aggregated to a <code>ProductType</code>, or <code>CategoryType</code>, etc.
 */
public interface AttributeGroup extends Persistable {
	/**
	 * Get the set of attributes associated with this attribute group.
	 * 
	 * @return the set of attributes associated with this product type.
	 */
	Set<AttributeGroupAttribute> getAttributeGroupAttributes();

	/**
	 * Set the set of attributes associated with this attribute group.
	 * 
	 * @param attributeGroupAttributes the attributes to set
	 */
	void setAttributeGroupAttributes(Set<AttributeGroupAttribute> attributeGroupAttributes);

	/**
	 * Add an attribute to belong to this attribute group.
	 * 
	 * @param attributeGroupAttribute the attribute group attribute to add.
	 */
	void addAttributeGroupAttribute(final AttributeGroupAttribute attributeGroupAttribute);

	/**
	 * Compares attribute sets on this and attribute group attribute set before an update and
	 * returns a set of attributes that have been deleted.
	 * @param before attributeGroupAttribute set before an update.
	 * @return set of attributes that have been deleted.
	 */
	Set<Attribute> getRemovedAttributes(Set<AttributeGroupAttribute> before);
}
