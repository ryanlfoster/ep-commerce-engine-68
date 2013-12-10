/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean;

import java.util.Map;

import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPrice;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Form bean for creating new delivery options during the checkout.
 */
public interface DeliveryOptionsFormBean extends EpFormBean {

	/**
	 * Get the frequency map.
	 * 
	 * @return The frequency/money map.
	 */
	Map<Quantity, FrequencyAndRecurringPrice> getFrequencyMap();

	/**
	 * Gets the shopping cart.
	 * 
	 * @return The shopping cart.
	 */
	ShoppingCart getShoppingCart();
	
	/**
	 * Sets the frequency map.
	 * 
	 * @param frequencyMap The frequency map.
	 */
	void setFrequencyMap(Map<Quantity, FrequencyAndRecurringPrice> frequencyMap);
	
	/**
	 * Sets the shopping cart.
	 * 
	 * @param shoppingCart The shopping cart.
	 */
	void setShoppingCart(ShoppingCart shoppingCart);
	
}
