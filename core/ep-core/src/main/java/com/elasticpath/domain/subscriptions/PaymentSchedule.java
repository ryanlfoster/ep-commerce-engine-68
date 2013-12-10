package com.elasticpath.domain.subscriptions;

import java.io.Serializable;

import com.elasticpath.domain.quantity.Quantity;

/**
 * Represents a payment schedule - specifying a payment frequency and duration.
 */
public interface PaymentSchedule extends Serializable, Comparable<PaymentSchedule> {
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
	void setPaymentFrequency(Quantity paymentFrequency);
	
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
	String getName();

	/**
	 * Set the name of this payment schedule.
	 * 
	 * @param name the name to set
	 */
	void setName(String name);

	/**
	 * Get the ordering of the payment schedule.
	 * @return the ordering
	 */
	int getOrdering();
	
	/**
	 * Set the ordering.
	 * @param ordering the ordering to set
	 */
	void setOrdering(final int ordering);
	
}
