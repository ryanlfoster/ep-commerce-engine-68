/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalogview.BranchNode;
import com.elasticpath.domain.misc.Orderable;
import com.elasticpath.persistence.api.Entity;

/**
 * A <code>Category</code> represents a collection of related <code>Product</code>s. A
 * <code>Category</code> is likely to have one or more products in it.
 */
public interface Category extends Comparable<Category>, Entity, ObjectWithLocaleDependantFields, BranchNode<Category>,
		CatalogObject, Orderable {
	
	/** Category guid delimiter. */
	String CATEGORY_GUID_DELIMITER = "|";

	/**
	 * Get the parent category of this category. Returns null if this category doesn't have a parent.
	 * 
	 * @return the parent category(or null no parent)
	 */
	Category getParent();

	/**
	 * Set the parent category. This method maintains the bidirectional relationships 
	 * between the parent category and this category. So it will add this category to 
	 * the given category's children collection.
	 * 
	 * @param category the parent category
	 */
	void setParent(Category category);
	
	/**
	 * Set the parent category without adding this category to the given parent category's children
	 * collection. This should only be called if the category is going to be persisted and then not
	 * used without being reloaded from the database. Otherwise use setParent which will maintain
	 * the bidirectional relationship. 
	 * 
	 * @param newParent the new parent category
	 */
	void setParentOnly(final Category newParent);

	/**
	 * Get the direct children categories of this category,
	 * one level deep.
	 * 
	 * @return the children categories as a set
	 */
	Set<Category> getChildren();

	/**
	 * Get the available children categories.
	 * 
	 * @return the available children categories as a set
	 */
	Set<Category> getAvailableChildren();

	/**
	 * Add the given category as a child.
	 * 
	 * @param category the category to be added as a child
	 * @throws com.elasticpath.domain.EpDomainException if the given category is not in the same
	 *             catalog
	 */
	void addChild(Category category);

	/**
	 * Remove the given category from the children list.
	 * 
	 * @param category the category to be removed
	 */
	void removeChild(Category category);

	/**
	 * Get the start date that this category will become available to customers.
	 * 
	 * @return the start date
	 */
	Date getStartDate();

	/**
	 * Set the start date that this category will become valid.
	 * 
	 * @param startDate the start date
	 */
	void setStartDate(Date startDate);

	/**
	 * Get the end date. After the end date, the category will change to unavailable to customers.
	 * 
	 * @return the end date
	 */
	Date getEndDate();

	/**
	 * Set the end date.
	 * 
	 * @param endDate the end date
	 */
	void setEndDate(Date endDate);

	/**
	 * Get the display template name of this category.
	 * 
	 * @param defaultTemplate the default template name
	 * @return the template name if it is defined for this category, otherwise the given default template name
	 */
	String getTemplateWithFallBack(String defaultTemplate);

	/**
	 * Get the display template name of this category.
	 * 
	 * @return the template
	 */
	String getTemplate();

	/**
	 * Get the attribute value group.
	 * 
	 * @return the domain model's <code>AttributeValueGroup</code>
	 */
	AttributeValueGroup getAttributeValueGroup();

	/**
	 * Set the attribute value group.
	 * 
	 * @param attributeValueGroup the <code>AttributeValueGroup</code>
	 */
	void setAttributeValueGroup(AttributeValueGroup attributeValueGroup);

	/**
	 * Returns <code>true</code> if the category is available.
	 * 
	 * @return <code>true</code> if the category is available.
	 */
	boolean isAvailable();

	/**
	 * Returns the category type.
	 * 
	 * @return the category type
	 */
	CategoryType getCategoryType();

	/**
	 * Set the <code>CategoryType</code>.
	 * 
	 * @param categoryType the <code>CategoryType</code>
	 */
	void setCategoryType(CategoryType categoryType);

	/**
	 * Returns the category path as a <code>List</code>. The root category will be the first.
	 * 
	 * @return the category path as a <code>List</code>.
	 */
	List<Category> getPathAsList();

	/**
	 * Returns true if the product should not be displayed (e.g. in its category or as a search result).
	 * 
	 * @return true if the product should not be displayed
	 */
	boolean isHidden();

	/**
	 * Set to true if the product should not be displayed.
	 * 
	 * @param hidden true if the product should not be displayed
	 */
	void setHidden(final boolean hidden);

	/**
	 * Sets the top sellers for this category.
	 * 
	 * @param topSellers the top sellers for this category
	 */
	void setTopSellers(final Set<TopSeller> topSellers);

	/**
	 * Returns the top sellers for this category.
	 * 
	 * @return top sellers for this category
	 */
	Set<TopSeller> getTopSellers();
	
	/**
	 * Returns the category code.
	 * @return the category code
	 */
	String getCode();
	
	/**
	 * Sets the category code.
	 * @param code the category code
	 */
	void setCode(String code);
	
	/**
	 * Return the compound category guid based on category code and appropriate catalog code.
	 *
	 * @return the compound guid.
	 */
	String getCompoundGuid();
	
	/**
	 * Returns the date when the category was last modified.
	 * 
	 * @return the date when the category was last modified
	 */
	Date getLastModifiedDate();
	
	/**
	 * Return the flag to indicate whether this category has subCategories or not.
	 * @return the flag to indicate whether this category has subCategories or not.
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	boolean getHasSubCategories();
	
	/**
	 * Get the indicator of whether this is a virtual category.
	 * @return true if this is a virtual category
	 */
	boolean isVirtual();

	/**
	 * Set the indicator of whether this is a virtual category.
	 * @param virtual true if the category is virtual
	 */
	void setVirtual(boolean virtual);
	
	/**
	 * Returns true if this category is linked (i.e. derived from a master category); false if it is a master category.
	 *
	 * @return true if this category is linked (i.e. derived from a master category); false if it is a master category
	 */
	boolean isLinked();
	
	/**
	 * Get the master category this virtual category is derived from (null if this category is a master).
	 * 
	 * @return the master category.
	 */
	Category getMasterCategory();
	
	/**
	 * Set the master category this virtual category is derived from (null if this a master category).
	 * 
	 * @param masterCategory the master category
	 */
	void setMasterCategory(final Category masterCategory);
	
	/**
	 * Set the attribute value map.
	 *
	 * @param attributeValueMap the map
	 */
	void setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap);
	
	/**
	 * Get the attribute value map.
	 *
	 * @return the map
	 */
	Map<String, AttributeValue> getAttributeValueMap();
	
	/**
	 * Get the indicator of whether or not this category has been included.
	 * 
	 * @return true if this category has been included
	 */
	boolean isIncluded();

	/**
	 * Set the indicator of whether or not this category has been included.
	 * 
	 * @param include true if this category has been included
	 */
	void setIncluded(final boolean include);
	
	/**
	 * Set the child categories.
	 * 
	 * @param children the child category set.
	 */
	void setChildren(final Set<Category> children);
}
