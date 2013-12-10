/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.misc.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.Money;

/**
 * Represents an amount of money in a particular currency. This class was migrated from EP4.1 and methods should be uncommented as
 * required.
 */
public class MoneyImpl extends AbstractEpDomainImpl implements Money {

	private static final long serialVersionUID = 5000000002L;

	private final BigDecimal amount;
	private final Currency currency;

	/**
	 * Constructor.  This class should not be instantiated directly from service and domain classes.  Instead, use the {@link MoneyFactory}
	 * to instantiate Money objects.
	 *
	 * @param amount the amount of money
	 * @param currency the currency
	 */
	protected MoneyImpl(final BigDecimal amount, final Currency currency) {
		this.amount = amount;
		this.currency = currency;
	}

	/**
	 * Get the amount of money as a <code>BigDecimal</code>, in the scale
	 * dictated by this object's Currency (if one has been set).
	 *
	 * @return the <code>BigDecimal</code> amount, or null if it has not been set.
	 */
	@Override
	public BigDecimal getAmount() {
		if (amount == null || getCurrency() == null) {
			return amount;
		}
		return amount.setScale(getCurrency().getDefaultFractionDigits(), RoundingMode.HALF_UP);
	}

	/**
	 * @return the amount of money as a <code>BigDecimal</code> in the scale it
	 * currently has.
	 */
	@Override
	public BigDecimal getAmountUnscaled() {
		return amount;
	}

	/**
	 * Get the <code>Currency</code> of the money.
	 *
	 * @return the <code>Currency</code>
	 */
	@Override
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Returns a new <code>Money</code> object representing the the amount of this object less the amount of the
	 * <code>otherMoney</code> object.
	 *
	 * @param otherMoney the <code>Money</code> object whose value is to be subtracted from this object
	 * @return the new <code>Money</code> object representing this value less the other object's value.
	 */
	@Override
	public Money subtract(final Money otherMoney) {
		return add(((MoneyImpl) otherMoney).negate());
	}

	/**
	 * Returns a new <code>Money</code> object representing the sum of this object and otherMoney's values.
	 *
	 * @param otherMoney the <code>Money</code> object whose value is to be added to this object
	 * @return the new <code>Money</code> object representing the sum.
	 */
	@Override
	public Money add(final Money otherMoney) {
		sanityCheck(otherMoney);
		checkCurrencyMatch(otherMoney);

		return MoneyFactory.createMoney(amount.add(otherMoney.getAmount()), currency);
	}

	/**
	 * Checks whether the Money object is consistent and has all the required fields set.
	 *
	 * @param money the Money implementation
	 */
	private void sanityCheck(final Money money) {
		if (money == null) {
			throw new EpDomainException("Money object can not be null.");
		}
		if (money.getCurrency() == null) {
			throw new EpDomainException("Currency can not be null.");
		}
	}

	/**
	 * Returns a new <code>Money</code> object with the negated value of this object.
	 *
	 * @return the negated <code>Money</code> object
	 */
	Money negate() {
		return MoneyFactory.createMoney(amount.negate(), currency);
	}

	/**
	 * Returns the money that would be saved by buying at the sale price instead of the (this) price.  If the sale price is greater than
	 * the current price returns 0.
	 *
	 * @param salePrice the sale price
	 * @return the amount saved by buying at the sale price, or zero if the sale is not so good
	 */
	@Override
	public Money getSaleSavings(final Money salePrice) {
		if (salePrice.lessThan(this)) {
			return subtract(salePrice);
		}

		return MoneyFactory.createMoney(BigDecimal.ZERO, getCurrency());
	}

	/**
	 * Returns the percentage amount that would be saved by buying at the sale price instead of the (this) price, rounded to the nearest
	 * whole number.  If the sale price is greater than the current price, then this method returns 0.
	 *
	 * @param salePrice the sale price
	 * @return the percentage saved by buying at the sale price, or zero if the sale is not so good
	 */
	@Override
	public double getSalePercentage(final Money salePrice) {
		final double oneHundredPercent = 100.0;

		if (salePrice.lessThan(this)) {
			double percentageSaving = 1.0 - (salePrice.getAmount().doubleValue() / getAmount().doubleValue());
			return Math.round(percentageSaving * oneHundredPercent) / oneHundredPercent;
		}

		return 0;
	}

	/**
	 * Returns true if this money object is greater than the specified other money object.
	 *
	 * @param otherMoney the other money object
	 * @return true if this object is greater
	 */
	@Override
	public boolean greaterThan(final Money otherMoney) {
		return (compareTo(otherMoney) == 1);
	}

	/**
	 * Returns true if this money object is less than the specified other money object.
	 *
	 * @param otherMoney the other money object
	 * @return true if this object is less
	 */
	@Override
	public boolean lessThan(final Money otherMoney) {
		return (compareTo(otherMoney) == -1);
	}

	/**
	 * Implements equals semantics.<br>
	 * This class more than likely would be extended to add functionality that would not effect the equals method in comparisons, and as such would
	 * act as an entity type. In this case, content is not crucial in the equals comparison. Using instanceof within the equals method enables
	 * comparison in the extended classes where the equals method can be reused without violating symmetry conditions. If getClass() was used in the
	 * comparison this could potentially cause equality failure when we do not expect it. If when extending additional fields are included in the
	 * equals method, then the equals needs to be overridden to maintain symmetry.
	 *
	 * @param obj the other object to compare
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof MoneyImpl)) {
			return false;
		}

		final MoneyImpl other = (MoneyImpl) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(getAmount(), other.getAmount())
				.append(getCurrency(), other.getCurrency())
				.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(amount)
				.append(currency)
				.toHashCode();
	}

	/**
	 * Checks the currency of the parameter <code>Money</code> object matches this <code>Money</code> object.
	 *
	 * @param otherMoney the other Money object to be checked
	 * @throws EpDomainException if the currencies don't match
	 */
	private void checkCurrencyMatch(final Money otherMoney) throws EpDomainException {
		sanityCheck(otherMoney);
		sanityCheck(this);
		if (currency.getCurrencyCode().equalsIgnoreCase(otherMoney.getCurrency().getCurrencyCode())) {
			return;
		}

		throw new EpDomainException("Money currency mismatch: " + currency.getCurrencyCode()
				+ " != " + otherMoney.getCurrency().getCurrencyCode());
	}

	/**
	 * Return a new Money object whose value is this money object's value times the specified multiplier.
	 *
	 * @param multiplier the amount to multiply by
	 * @return a Money object representing the result
	 */
	@Override
	public Money multiply(final BigDecimal multiplier) {
		return MoneyFactory.createMoney(amount.multiply(multiplier), currency);
	}

	/**
	 * Return a new Money object whose value is this money object's value times the specified multiplier.
	 *
	 * @param multiplier the amount to multiply by
	 * @return a Money object representing the result
	 */
	@Override
	public Money multiply(final int multiplier) {
		return multiply(BigDecimal.valueOf(multiplier));
	}

	/**
	 * Compares this money with the specified object for order.
	 *
	 * @param money the given object
	 * @return a negative integer, zero, or a positive integer if this object is less than, equal to, or greater than the
	 *         specified object.
	 * @throws EpDomainException if the given object is not a <code>Money</code>
	 */
	@Override
	public int compareTo(final Money money) throws EpDomainException {
		if (money == null) {
			throw new EpDomainException("Null object to compare with.");
		}

		// TODO: we may need to change this after multi-currency design session
		checkCurrencyMatch(money);

		// Just Compare amount
		return getAmount().compareTo(money.getAmount());
	}

	/**
	 * Returns string representation of Money objects to facilitate debugging.
	 *
	 * @return a string representation of the Money object
	 */
	@Override
	public String toString() {
		StringBuffer moneyString = new StringBuffer();
		if (getAmount() != null) {
			moneyString.append(getAmount().setScale(2).toString());
		}
		if (getCurrency() != null) {
			moneyString.append(' ').append(getCurrency().toString());
		}
		return moneyString.toString();
	}
}
