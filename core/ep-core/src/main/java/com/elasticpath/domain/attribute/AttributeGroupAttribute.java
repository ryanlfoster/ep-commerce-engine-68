package com.elasticpath.domain.attribute;

/**
 * Represents an association between an <code>Attribute</code> and an <code>AttributeGroup</code>.  
 */
public interface AttributeGroupAttribute extends Comparable<AttributeGroupAttribute> {

	/**
	 * Get the ordering number.
	 *
	 * @return the ordering number
	 */
	int getOrdering();

	/**
	 * Set the ordering number.
	 *
	 * @param ordering the ordering number
	 */
	void setOrdering(int ordering);

	/**
	 * Get the attribute.
	 *
	 * @return the attribute
	 */
	Attribute getAttribute();

	/**
	 * Set the attribute.
	 *
	 * @param attribute the attribute to set
	 */
	void setAttribute(Attribute attribute);
}
