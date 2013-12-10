package com.elasticpath.service.tax.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.Test;


/** */
public class ApportioningCalculatorTest {
	private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");
	private static final BigDecimal ONE_CENT = new BigDecimal(".01");
	
	private static final BigDecimal THIRTY = new BigDecimal("30.00");

	private static final BigDecimal TEN = new BigDecimal("10.00");
	
	private static final BigDecimal FIVE = new BigDecimal("5.00");
	private static final BigDecimal MINUS_ONE = BigDecimal.ONE.negate();

	private final ApportioningCalculator apportioningCalculator = new ApportioningCalculator();  
	/**
	 * Simple no rounding calculation of proportional discount.
	 */
	@Test
	public void testProportionalDiscount() {
		BigDecimal expectedProportionalDiscount = new BigDecimal("0.50");
		BigDecimal discountResult = apportioningCalculator.calculateProportion(FIVE, TEN, ONE_HUNDRED);
		assertEquals(expectedProportionalDiscount, discountResult);
	}
	
	/**
	 * Test with zero price, should get zero discount. 
	 */
	@Test
	public void testProportionalDiscountZeroPrice() {
		BigDecimal expectedProportionalDiscount = BigDecimal.ZERO.setScale(2);
		BigDecimal discountResult = apportioningCalculator.calculateProportion(FIVE, BigDecimal.ZERO, ONE_HUNDRED);
		assertEquals(expectedProportionalDiscount, discountResult);
	}
	
	/**
	 * Supplying a zero sum fails with a divide by zero exception. See method
	 * invariant.
	 */
	@Test (expected = ArithmeticException.class)
	public void testProportionalDiscountZeroSum() {
		apportioningCalculator.calculateProportion(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
	}

	/**
	 * Compute a proportional discount with rounding (should round half up to a scale of 2).
	 */
	@Test
	public void testProportionalDiscountRounding() {
		BigDecimal expectedProportionalDiscount = new BigDecimal("1.67");
		BigDecimal discountResult = apportioningCalculator.calculateProportion(FIVE, TEN, THIRTY);
		assertEquals(expectedProportionalDiscount, discountResult);
	}

	/** */
	@Test
	public void testNegativeProportionAfterAdjustment() {
		BigDecimal adjustment = apportioningCalculator.calculateErrorAdjustment(BigDecimal.TEN, ONE_CENT, MINUS_ONE);
		assertEquals(ONE_CENT.negate(), adjustment);
	}
	
	/** */
	@Test
	public void testAdjustment() {
		BigDecimal adjustment = apportioningCalculator.calculateErrorAdjustment(BigDecimal.TEN, BigDecimal.ONE, ONE_CENT);
		assertEquals(0, ONE_CENT.compareTo(adjustment));
	}
	
	/** */
	@Test
	public void testAdjustmentForZeroPortion() {
		BigDecimal adjustment = apportioningCalculator.calculateErrorAdjustment(BigDecimal.ZERO, BigDecimal.ZERO, ONE_CENT);
		assertEquals(BigDecimal.ZERO, adjustment);
	}
	
	/** */
	@Test
	public void testAmountToApportionHigherThanTotal() {
		HashMap<String, BigDecimal> amounts = new HashMap<String, BigDecimal>();
		amounts.put("1", BigDecimal.ONE);
		apportioningCalculator.calculateApportionedAmounts(BigDecimal.TEN, amounts);
	}
}
