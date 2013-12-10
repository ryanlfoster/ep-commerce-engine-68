/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;

/**
 * Represents a filter on product category.
 */
public interface CategoryFilter extends Filter<CategoryFilter> {
	/**
	 * Get the category.
	 * 
	 * @return the category
	 */
	Category getCategory();

	/**
	 * Set the category.
	 * 
	 * @param category the category to set
	 */
	void setCategory(Category category);
	
	/**
	 * Gets the {@link Catalog}.
	 *
	 * @return the {@link Catalog}
	 */
	Catalog getCatalog();

	/**
	 * Sets the {@link Catalog}.
	 *
	 * @param catalog the {@link Catalog}
	 */
	void setCatalog(Catalog catalog);

	/**
	 * Initializes the filter with a category code / catalog combination to
	 * uniquely identify the category.
	 *
	 * @param categoryCode the category code
	 * @param catalog the catalog in which the filter applies
	 * @throws EpCatalogViewRequestBindException if no category can be found
	 */
	void initializeWithCode(final String categoryCode, final Catalog catalog) throws EpCatalogViewRequestBindException;
	
}
