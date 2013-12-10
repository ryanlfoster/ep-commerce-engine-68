package com.elasticpath.sellingchannel;

import java.io.Serializable;
import java.util.Set;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;

/**
 * Assembles {@link ShoppingItemRecurringPrice}s from {@link Price} and vice versa.  
 */
public interface ShoppingItemRecurringPriceAssembler extends Serializable {

	/**
	 * Creates a list of {@link ShoppingItemRecurringPrice}s from a {@link Price}.
	 *
	 * @param price the price
	 * @param quantity the quantity of the shopping item
	 * @return the list
	 */
	Set<ShoppingItemRecurringPrice> createShoppingItemRecurringPrices(Price price, int quantity);

	/**
	 * Puts the recurring prices from a list of {@link ShoppingItemRecurringPrice}s to a {@link Price} object. 
	 * 
	 * @param price the price object: shouldn't be null.
	 * @param recurringPrices the list
	 */
	void assemblePrice(Price price, Set<ShoppingItemRecurringPrice> recurringPrices);

}