/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.misc;

import java.math.BigDecimal;
import java.util.Currency;

import com.elasticpath.domain.EpDomain;

/**
 * An immutable object that represents an amount of money in a particular currency.
 */
public interface Money extends EpDomain, Comparable<Money> {

	/**
	 * Get the <code>Currency</code> of the money.
	 *
	 * @return the <code>Currency</code>
	 */
	Currency getCurrency();

	/**
	 * Get the amount of money as a <code>BigDecimal</code>, in the scale
	 * dictated by this object's Currency (if one has been set).
	 *
	 * @return the <code>BigDecimal</code> amount, or null if it has not been set.
	 */
	BigDecimal getAmount();

	/**
	 * Returns a new <code>Money</code> object representing the the amount of this object less the amount of the
	 * <code>otherMoney</code> object.
	 *
	 * @param otherMoney the <code>Money</code> object whose value is to be subtracted from this object
	 * @return the new <code>Money</code> object representing this value less the other object's value.
	 */
	Money subtract(final Money otherMoney);

	/**
	 * Returns a new <code>Money</code> object representing the sum of this object and otherMoney's values.
	 *
	 * @param otherMoney the <code>Money</code> object whose value is to be added to this object
	 * @return the new <code>Money</code> object representing the sum.
	 */
	Money add(final Money otherMoney);

	/**
	 * Returns true if this money object is greater than the specified other money object.
	 *
	 * @param otherMoney the other money object
	 * @return true if this object is greater
	 */
	boolean greaterThan(final Money otherMoney);

	/**
	 * Returns true if this money object is less than the specified other money object.
	 *
	 * @param otherMoney the other money object
	 * @return true if this object is less
	 */
	boolean lessThan(final Money otherMoney);

	/**
	 * Returns the amount that would be saved by buying at the sale price instead of the (this) price.  If the sale price is greater than
	 * the current price returns 0.
	 *
	 * @param salePrice the sale price
	 * @return the amount saved by buying at the sale price, or zero if the sale is not so good
	 */
	Money getSaleSavings(final Money salePrice);

	/**
	 * Returns the percentage amount that would be saved by buying at the sale price instead of the (this) price, rounded to the nearest
	 * whole percentage (e.g. 50.5% would return 0.50).  If the sale price is greater than the current price, then this method returns 0.
	 *
	 * @param salePrice the sale price
	 * @return the percentage saved by buying at the sale price, or zero if the sale is not so good
	 */
	double getSalePercentage(final Money salePrice);

	/**
	 * Return a new Money object whose value is this money object's value times the specified multiplier.
	 *
	 * @param multiplier the amount to multiply by
	 * @return a Money object representing the result
	 */
	Money multiply(final BigDecimal multiplier);

	/**
	 * Return a new Money object whose value is this money object's value times the specified multiplier.
	 *
	 * @param multiplier the amount to multiply by
	 * @return a Money object representing the result
	 */
	Money multiply(final int multiplier);

	/**
	 * @return the amount of money as a <code>BigDecimal</code> in the scale it
	 * currently has.
	 */
	BigDecimal getAmountUnscaled();
}
