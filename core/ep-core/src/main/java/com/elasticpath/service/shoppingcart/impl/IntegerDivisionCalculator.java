package com.elasticpath.service.shoppingcart.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Integer devision calculator.
 */
public class IntegerDivisionCalculator {
	private static final int DEFAULT_SCALE = 2;

	private BigDecimal remainder;

	private BigDecimal quotient;

	private BigDecimal lastQuotient;

	/**
	 * Constructor.
	 * 
	 * @param dividend the dividend.
	 * @param quantity the quantity.
	 */
	public IntegerDivisionCalculator(final BigDecimal dividend, final int quantity) {
		if (dividend == null || quantity <= 0) {
			emptyResult();
		} else {
			calculateResult(dividend, quantity);
		}
	}

	private void emptyResult() {
		remainder = null;
		quotient = null;
		lastQuotient = null;
	}

	private void calculateResult(final BigDecimal dividend, final int quantity) {
		BigDecimal divisor = new BigDecimal(quantity);
		quotient = dividend.divide(divisor, DEFAULT_SCALE, RoundingMode.DOWN);
		remainder = dividend.subtract(getQuotient().multiply(divisor));
		lastQuotient = getQuotient().add(remainder);
	}

	/**
	 * Gets quotient.
	 * 
	 * @return the quotient.
	 */
	public BigDecimal getQuotient() {
		return quotient;

	}

	/**
	 * Gets quotient of the last item.
	 * 
	 * @return the last quotient.
	 */
	public BigDecimal getLastQuotient() {
		return lastQuotient;
	}

	/**
	 * Gets the remainder.
	 * 
	 * @return the remainder.
	 */
	public BigDecimal getRemainder() {
		return remainder;
	}
}