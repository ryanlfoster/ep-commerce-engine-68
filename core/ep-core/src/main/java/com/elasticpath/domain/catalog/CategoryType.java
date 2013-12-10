/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.persistence.api.Entity;

/**
 * Represents the type of a <code>Category</code>, which determines the set of attributes that it has. An example of a category type is "Shoe."
 * Note that this differs from a category category, which might also be called "Shoes" because this describes the characteristics of the category
 * rather than how they are displayed and organized in the store.
 */
public interface CategoryType extends Entity, CatalogObject {

	/**
	 * Get the category type name.
	 * 
	 * @return the category type name
	 */
	String getName();

	/**
	 * Set the category type name.
	 * 
	 * @param name the category type name
	 */
	void setName(String name);

	/**
	 * Get the category type description.
	 * 
	 * @return the category type description
	 */
	String getDescription();

	/**
	 * Set the category type description.
	 * 
	 * @param description the category type description
	 */
	void setDescription(String description);

	/**
	 * Get the display template of this category.
	 * 
	 * @return the template
	 */
	String getTemplate();

	/**
	 * Set the template.
	 * 
	 * @param template the template
	 */
	void setTemplate(String template);

	/**
	 * Sets the category attribute group.
	 * 
	 * @param attributeGroup the category attribute group.
	 */
	void setAttributeGroup(AttributeGroup attributeGroup);

	/**
	 * Returns the category attribute group.
	 * 
	 * @return the category attribute group
	 */
	AttributeGroup getAttributeGroup();

}
