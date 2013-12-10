/**
 * 
 */
package com.elasticpath.sellingchannel;

import java.math.BigDecimal;

import com.elasticpath.domain.misc.Money;

/**
 * 
 */
public interface PriceInformation {
	
	/** Descriptor Key for the ListPrice. */
	String KEY_LIST = "LIST_PRICE";
	/** Descriptor Key for the SalePrice. */
	String KEY_SALE = "SALE_PRICE";
	/** Descriptor Key for the PromotedPrice (after catalog promotions applied). */
	String KEY_PROMOTED = "PROMOTED_PRICE";
	
	/** 
	 * @return the list price record.
	 */
	Money getListPrice();
	
	/**
	 * @return the sale price record.
	 */
	Money getSalePrice();
	
	/**
	 * @return the promoted price record.
	 */
	Money getPromotedPrice();
	
	/**
	 * Returns the price mapped by the given descriptor key.
	 * @param descriptor the descriptor key 
	 * @return the requested price
	 */
	Money getPrice(String descriptor);
	
	/**
	 * Sets a price.
	 * @param descriptor the descriptor key signifying the price
	 * @param amount the amount
	 * @param currencyCode the code for the currency in which the price is valid
	 */
	void setPrice(String descriptor, BigDecimal amount, String currencyCode);

}
