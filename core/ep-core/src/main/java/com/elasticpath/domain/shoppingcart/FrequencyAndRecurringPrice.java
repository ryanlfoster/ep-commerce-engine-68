package com.elasticpath.domain.shoppingcart;

import java.io.Serializable;

import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.quantity.Quantity;

/**
 * Holds the total price of all items for a single frequency.
 * For example, several items could have a frequency of "Every 2 Weeks" and their total price could be "$34.27".
 */
public class FrequencyAndRecurringPrice implements Serializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private final Quantity frequency;
	private Money amount;
	private final String name;

	/**
	 * Constructor.
	 * Note: the NAME represents both the frequency and the duration.
	 * But the frequencies add together and ignore the duration.
	 * So "Every 2 Weeks for 1 Year" and "Every 2 Weeks for 2 Years" will add together, but
	 * the NAME could be "Every2WeeksFor1Year".
	 *
	 * @param frequency The frequency (unit/amount) of the recurring price scheme.
	 * @param amount The total price of all items for the given frequency.
	 * @param name The TPAYMENTSCHEDULE.NAME field from the database.
	 */
	public FrequencyAndRecurringPrice(final Quantity frequency, final Money amount, final String name) {
		this.frequency = frequency;
		this.amount = amount;
		this.name = name;
	}

	/**
	 * Adds the given price to the internal total price.
	 * @param price The price to add.
	 */
	public void addPrice(final Money price) {
		amount = amount.add(price);
	}

	/**
	 *
	 * @return frequency The frequency.
	 */
	public Quantity getFrequency() {
		return frequency;
	}

	/**
	 *
	 * @return name.
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return amount
	 */
	public Money getAmount() {
		return amount;
	}
}
