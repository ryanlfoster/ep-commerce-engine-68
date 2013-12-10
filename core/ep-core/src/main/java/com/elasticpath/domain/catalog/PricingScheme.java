/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.catalog;

import java.io.Serializable;
import java.util.Collection;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.misc.Money;

/**
 * Represents a collection of prices for a particular item with different pricing
 * contexts (e.g. schedules).
 */
public interface PricingScheme extends Serializable {

	/**
	 * Get the simple price for a given price schedule.
	 * 
	 * @param schedule the price schedule to get the price of
	 * @return the simple price
	 */
	SimplePrice getSimplePriceForSchedule(PriceSchedule schedule);
	
	/**
	 * Get the collection of price schedules that are in this pricing scheme.
	 *
	 * @param types the types of schedules to return
	 * @return the price schedules
	 */
	Collection<PriceSchedule> getSchedules(PriceScheduleType... types);

	/**
	 * Convenience method to retrieve the <code>PriceScheduleType.RECURRING</code> schedules. 
	 * used by velocity.
	 *
	 * @return the price schedules
	 */
	Collection<PriceSchedule> getRecurringSchedules();

	/**
	 * Convenience method to retrieve the <code>PriceScheduleType.PURCHASE_TIME</code> schedules.
	 *
	 * @return the price schedules
	 */
	Collection<PriceSchedule> getPurchaseTimeSchedules();
	
	/**
	 * Set the price for the given schedule.
	 * 
	 * @param schedule the schedule to set the price for
	 * @param price the price to set
	 */
	void setPriceForSchedule(final PriceSchedule schedule, final SimplePrice price);	

	/**
	 * Get the currency for this scheme.
	 *
	 * @return the <code>Currency</code>
	 */
	Currency getCurrency();
	
	
	/**
	 * Get the price schedule for the lowest price. 
	 * @see {@link getLowestPrice()}
	 * @return the {@link PriceSchedule} for the lowest price
	 */
	PriceSchedule getScheduleForLowestPrice();
	
	
	/**
	 * Get the lowest price amount for the pricing scheme. It will be the lowest monetary value of the minimum
	 * tier among all the schedules. 
	 * @return the lowest price
	 */
	Money getLowestPrice();
	
	/**
	 * 
	 * @return Map<PriceSchedule, SimplePrice>
	 */
	Map<PriceSchedule, SimplePrice> getPriceSchedules();


	/**
	 * Get the minimum quantities for available price tiers for all schedules.
	 *
	 * @return set of integers for tier minimum quantities of each schedule. The iterator on the set will return values in an ascending order.
	 */
	Set<Integer> getPriceTiersMinQuantities();




}
