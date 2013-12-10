package com.elasticpath.service.search;


/**
 * Represents a search criteria that has objects that are within a category.
 */
public interface ProductCategorySearchCriteria extends CatalogAwareSearchCriteria {

	/**
	 * Gets the UID of the category objects that are searched should be in.
	 * 
	 * @return the UID of the category objects that are searched should be in
	 */
	Long getCategoryUid();
	
	/**
	 * Gets whether faceting is enabled for the search criteria.
	 *
	 * @return whether faceting is enabled for the search criteria
	 */
	boolean isFacetingEnabled();
	
	/**
	 * Sets whether faceting is enabled for the search criteria.
	 * 
	 * @param facetingEnabled whether faceting is enabled for the search criteria
	 */
	void setFacetingEnabled(final boolean facetingEnabled);
	
	/**
	 * Gets whether to search for displayable products only. Displayable products are:
	 * <ul>
	 * <li>Not hidden</li>
	 * <li>Current date falls within a product's start date and end date (if an end date is
	 * defined)</li>
	 * <li>in stock (any of the product SKUs have at least 1 inventory or if the product is
	 * displayable when out of stock)</li>
	 * </ul>
	 * 
	 * @return whether to search for displayable products only
	 */
	boolean isDisplayableOnly();
	
	/**
	 * Sets whether to search for displayable products only. Displayable products are:
	 * <ul>
	 * <li>Not hidden</li>
	 * <li>Current date falls within a product's start date and end date (if an end date is
	 * defined)</li>
	 * <li>in stock (any of the product SKUs have at least 1 inventory or if the product is
	 * displayable when out of stock)</li>
	 * </ul>
	 * 
	 * @param displayableOnly whether to search for displayable products only
	 */
	void setDisplayableOnly(final boolean displayableOnly);
	
	/**
	 * Returns <code>true</code> if searching only for active products.
	 * 
	 * @return <code>true</code> if searching only for active products
	 */
	boolean isActiveOnly();

	/**
	 * Sets the active-only flag to <code>true</code> if only searching for active products.
	 * 
	 * @param activeOnlyFlag the active-only flag
	 */
	void setActiveOnly(final boolean activeOnlyFlag);
}
