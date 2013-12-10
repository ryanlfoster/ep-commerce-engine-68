package com.elasticpath.domain.shoppingcart;

import com.elasticpath.domain.shoppingcart.impl.ShoppingItemSimplePrice;
import com.elasticpath.domain.quantity.Quantity;

/**
 * Stores a snapshot of recurring prices for a {@link com.elasticpath.domain.shoppingcart.ShoppingItem}.  
 * 
 */
public interface ShoppingItemRecurringPrice {

	/**
	 * Get the payment frequency.
	 *
	 * @return the payment frequency
	 */
	Quantity getPaymentFrequency();

	/**
	 * Set the payment frequency.
	 *
	 * @param paymentFrequency the payment frequency
	 */
	void setPaymentFrequency(final Quantity paymentFrequency);

	/**
	 * Get the duration of the payment schedule.
	 * 
	 * @return the duration
	 */
	Quantity getScheduleDuration();
	
	/**
	 * Set the schedule duration.
	 * 
	 * @param scheduleDuration the duration of the payment schedule
	 */
	void setScheduleDuration(Quantity scheduleDuration);
	

	/**
	 * Get the name of this schedule.
	 * 
	 * @return the name
	 */
	String getPaymentScheduleName();

	/**
	 * Set the name of this payment schedule.
	 * 
	 * @param paymentScheduleName the name to set
	 */
	void setPaymentScheduleName(final String paymentScheduleName);
	
	
	/**
	 * Get the simple price of this recurring price. 
	 *
	 * @return the simple price
	 */
	ShoppingItemSimplePrice getSimplePrice();
	
	/**
	 * Set the simple price for this recurring price.
	 *
	 * @param simplePrice the simple price
	 */
	void setSimplePrice(ShoppingItemSimplePrice simplePrice);
}