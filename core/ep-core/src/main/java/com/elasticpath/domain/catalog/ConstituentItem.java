/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.catalog;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;


/**
 * Interface that represents a constituent item, i.e. an item that can
 * be a constituent of a bundle.
 */
public interface ConstituentItem extends Serializable {

	/**
	 * Get the product associated with this constituent. If the constituent is a SKU, this
	 * will return the SKU's parent product.
	 * 
	 * @return a {@link Product}
	 */
	Product getProduct();
	
	/**
	 * Get the product SKU associated with this constituent. If the constituent is a Product,
	 * this will return the Product's default SKU.
	 * 
	 * @return a {@link ProductSku}
	 */
	ProductSku getProductSku();
	
	/**
	 * Get the code associated with the constituent.
	 * 
	 * @return the constituent code
	 */
	String getCode();
	
	/**
	 * Get the display name of the constituent in the given locale.
	 * 
	 * @param locale the locale to use
	 * @return the display name in the given locale
	 */
	String getDisplayName(Locale locale);

	/**
	 * Get the start date that this constituent item will become available to customers.
	 * This will come from the underlying Product or SKU.
	 *
	 * @return the start date
	 */
	Date getStartDate();
	
	/**
	 * Get the end date. After the end date, the constituent item will be unavailable to customers.
	 * This will come from the underlying Product or SKU.
	 *
	 * @return the end date
	 */
	Date getEndDate();
	
	/**
	 * Indicate whether the constituent is a product.
	 * 
	 * @return true if the constituent is a product
	 */
	boolean isProduct();
	
	/**
	 * Indicate whether the constituent is a SKU.
	 * 
	 * @return true if the constituent is a SKU.
	 */
	boolean isProductSku();
	
	/**
	 * Indicate whether the constituent is a (nested) Product Bundle.
	 * 
	 * @return true if the constituent is a bundle
	 */
	boolean isBundle();
}
