/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.Persistable;

/**
 * <code>ProductCategory</code> represents an association between a <code>Category</code> and a <code>Product</code>.
 */
public interface ProductCategory extends Persistable, Comparable<ProductCategory> {
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
	 * Get the featured product order.
	 *
	 * @return the featured product order
	 */
	int getFeaturedProductOrder();

	/**
	 * Set the featured product order.
	 *
	 * @param featuredProductOrder the featured product order to set
	 */
	void setFeaturedProductOrder(int featuredProductOrder);

	/**
	 * Get the product.
	 * @return the product
	 */
	Product getProduct();

	/**
	 * Set the product.
	 * @param product the product to set
	 */
	void setProduct(final Product product);

	/**
	 * Get whether this is the default category.
	 * @return true if this category is a default.
	 */
	boolean isDefaultCategory();

	/**
	 * Set whether this is the default category.
	 * @param defaultCategory flag indicating whether this category is the default.
	 */
	void setDefaultCategory(final boolean defaultCategory);

}
