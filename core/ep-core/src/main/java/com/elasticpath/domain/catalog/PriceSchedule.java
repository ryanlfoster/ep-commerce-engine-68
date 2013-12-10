/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.catalog;

import java.io.Serializable;

import com.elasticpath.domain.subscriptions.PaymentSchedule;

/**
 * The schedule for a price - e.g. one time, recurring, etc.
 */
public interface PriceSchedule extends Serializable, Comparable<PriceSchedule> {
	
	/**
	 * Get the type of the price schedule.
	 * 
	 * @return the {@link PriceScheduleType}
	 */
	PriceScheduleType getType();
	
	/**
	 * Set the price schedule type.
	 * 
	 * @param priceScheduleType the price schedule type
	 */
	void setType(PriceScheduleType priceScheduleType);
	
	/**
	 * Get the payment schedule for this price schedule. 
	 * 
	 * @return the {@link PaymentSchedule}
	 */
	PaymentSchedule getPaymentSchedule();

	/**
	 * Set the payment schedule.
	 * 
	 * @param paymentSchedule the payment schedule
	 */
	void setPaymentSchedule(PaymentSchedule paymentSchedule);
}
