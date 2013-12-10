/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.catalog;

/**
 * Methods that are specific to a Product constituent.
 */
public interface ProductConstituent extends ConstituentItem {
	
	/**
	 * Set the product that this constituent represents.
	 * 
	 * @param product the {@code Product}
	 */
	void setProduct(final Product product);

}
