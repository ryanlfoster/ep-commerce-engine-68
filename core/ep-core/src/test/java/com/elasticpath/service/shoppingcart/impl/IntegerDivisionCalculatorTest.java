package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Test;

/** */
public class IntegerDivisionCalculatorTest {
	private static final int FIVE_INT = 5;
	private IntegerDivisionCalculator calculator;
	
	/** */
	@Test
	public void testDividendIsNull() {
		calculator = new IntegerDivisionCalculator(null, 1);
		assertNull(calculator.getQuotient());
		assertNull(calculator.getLastQuotient());
		assertNull(calculator.getRemainder());
	}
	
	/** */
	@Test
	public void testQuantityIsZero() {
		calculator = new IntegerDivisionCalculator(BigDecimal.ONE, 0);
		assertNull(calculator.getQuotient());
		assertNull(calculator.getLastQuotient());
		assertNull(calculator.getRemainder());
	}
	
	/** */
	@Test
	public void testDevidenNotNull() {
		calculator = new IntegerDivisionCalculator(new BigDecimal("0.09"), FIVE_INT);
		
		assertEquals(new BigDecimal("0.01"), calculator.getQuotient());
		assertEquals(new BigDecimal("0.05"), calculator.getLastQuotient());
	}
}
