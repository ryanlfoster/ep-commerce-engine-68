/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.misc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.misc.Money;

/**
 * Test for <code>MoneyImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports", "PMD.TooManyMethods", "PMD.AvoidDecimalLiteralsInBigDecimalConstructor" })
public class MoneyImplTest {

	private static final Currency CAD = Currency.getInstance("CAD");
	private static final Currency JPY = Currency.getInstance("JPY");
	private static final float FLOAT_DELTA = 0.001F;

	/**
	 * Prepare for each test.
	 * 
	 * @throws Exception in case of error
	 */
	@Before
	public void setUp() throws Exception {
		Locale.setDefault(Locale.US);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.MoneyImpl.hashCode()'.
	 */
	@Test
	public void testHashCode() {

		final BigDecimal amount1 = new BigDecimal(12.34F);
		final BigDecimal amount2 = new BigDecimal(55.55F);

		Money money1 = new MoneyImpl(amount1, CAD);
		Money money2 = new MoneyImpl(amount2, CAD);
		assertNotSame(money1.hashCode(), money2.hashCode());

		money2 = new MoneyImpl(amount1, CAD);
		assertEquals(money1.hashCode(), money2.hashCode());
	}

	private static void assertMoneyValuesNotEqual(final String message, final Currency currency, final float largerValue, final float smallerValue) {
		MoneyImpl larger = new MoneyImpl(new BigDecimal(largerValue), currency);
		MoneyImpl smaller = new MoneyImpl(new BigDecimal(smallerValue), currency);

		assertTrue(message, larger.compareTo(smaller) > 0);
		assertTrue(message, smaller.compareTo(larger) < 0);
	}

	private static void assertMoneyValuesNotEqual(final Currency currency, final float largerValue, final float smallerValue) {
		assertMoneyValuesNotEqual(null, currency, largerValue, smallerValue);
	}

	private static void assertMoneyValuesEqual(final String message, final Currency currency, final float value1, final float value2) {
		MoneyImpl money1 = new MoneyImpl(new BigDecimal(value1), currency);
		MoneyImpl money2 = new MoneyImpl(new BigDecimal(value2), currency);

		assertEquals(message, 0, money1.compareTo(money2));
		assertEquals(message, 0, money2.compareTo(money1));
	}

	private static void assertMoneyValuesEqual(final Currency currency, final float value1, final float value2) {
		assertMoneyValuesEqual(null, currency, value1, value2);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.MoneyImpl.getCurrency()'.
	 */
	@Test
	public void testGetCurrency() {
		final MoneyImpl moneyImpl = new MoneyImpl(BigDecimal.ZERO, CAD);
		assertSame(moneyImpl.getCurrency(), CAD);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.MoneyImpl.subtract(Money)'.
	 */
	@Test
	public void testSubtract() {
		// CHECKSTYLE:OFF  Magic Numbers OK!
		final BigDecimal amount1 = new BigDecimal(11F);
		final BigDecimal amount2 = new BigDecimal(5.5F);

		MoneyImpl money1 = new MoneyImpl(amount1, CAD);
		MoneyImpl money2 = new MoneyImpl(amount2, CAD);

		Money subtractedMoney = money1.subtract(money2);

		assertEquals(amount2.setScale(2), subtractedMoney.getAmount());
		// CHECKSTYLE:ON
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.MoneyImpl.add(Money)'.
	 */
	@Test
	public void testAdd() {
		// CHECKSTYLE:OFF  Magic Numbers OK!
		final BigDecimal amount1 = new BigDecimal(5.5F);
		MoneyImpl money1 = new MoneyImpl(amount1, CAD);
		MoneyImpl money2 = new MoneyImpl(amount1, CAD);

		Money sum = money1.add(money2);

		final BigDecimal amount2 = new BigDecimal(11F).setScale(2);
		assertEquals(amount2, sum.getAmount());
		// CHECKSTYLE:ON
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.MoneyImpl.getSaleSavings(Money)'.
	 */
	@Test
	public void testGetSaleSavings() {
		// CHECKSTYLE:OFF  Magic Numbers OK!
		MoneyImpl money1 = new MoneyImpl(new BigDecimal(10F), CAD);
		MoneyImpl money2 = new MoneyImpl(new BigDecimal(5F), CAD);

		assertEquals(new MoneyImpl(new BigDecimal(5F), CAD), money1.getSaleSavings(money2));
		assertEquals(new MoneyImpl(new BigDecimal(0F), CAD), money1.getSaleSavings(money1));
		assertEquals(new MoneyImpl(new BigDecimal(0F), CAD), money2.getSaleSavings(money1));
		// CHECKSTYLE:ON
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.MoneyImpl.displaySalePercentage(Money, Locale)'.
	 */
	@Test
	public void testGetSalePercentage() {
		// CHECKSTYLE:OFF  Magic Numbers OK!
		MoneyImpl money1 = new MoneyImpl(new BigDecimal(10F), CAD);
		MoneyImpl money2 = new MoneyImpl(new BigDecimal(5F), CAD);

		assertEquals(0.50, money1.getSalePercentage(money2), FLOAT_DELTA);
		assertEquals(0.0, money1.getSalePercentage(money1), FLOAT_DELTA);
		assertEquals(0.0, money2.getSalePercentage(money1), FLOAT_DELTA);
		// CHECKSTYLE:ON
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.MoneyImpl.greaterThan(Money)'.
	 */
	@Test
	public void testGreaterThan() {
		// CHECKSTYLE:OFF  Magic Numbers OK!
		final BigDecimal amount1 = new BigDecimal(12.34F);
		MoneyImpl money1 = new MoneyImpl(amount1, CAD);
		final BigDecimal amount2 = new BigDecimal(55.55F);
		MoneyImpl money2 = new MoneyImpl(amount2, CAD);

		assertTrue(money2.greaterThan(money1));
		assertFalse(money2.greaterThan(money2));
		// CHECKSTYLE:ON
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.MoneyImpl.lessThan(Money)'.
	 */
	@Test
	public void testLessThan() {
		// CHECKSTYLE:OFF  Magic Numbers OK!
		final BigDecimal amount1 = new BigDecimal(12.34F);
		MoneyImpl money1 = new MoneyImpl(amount1, CAD);
		final BigDecimal amount2 = new BigDecimal(55.55F);
		MoneyImpl money2 = new MoneyImpl(amount2, CAD);

		assertTrue(money1.lessThan(money2));
		assertFalse(money1.lessThan(money1));
		// CHECKSTYLE:ON
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.MoneyImpl.equals(Object)'.
	 */
	@Test
	public void testEqualsObject() {
		final BigDecimal amount1 = new BigDecimal(12.34);
		Money money1 = new MoneyImpl(amount1, CAD);
		Money money2 = new MoneyImpl(amount1, CAD);

		assertEquals(money1, money2);

		money2 = new MoneyImpl(amount1, Currency.getInstance("USD"));

		assertFalse(money1.equals(money2));
		
		Money nullMoney = new MoneyImpl(null, null);
		assertFalse(money1.equals(nullMoney));

		nullMoney = null;
		assertFalse(money1.equals(nullMoney));

		money1 = new MoneyImpl(BigDecimal.ZERO, CAD);
		money2 = new MoneyImpl(BigDecimal.ZERO.setScale(2), CAD);
		assertEquals(money1, money2);
		
		money1 = new MoneyImpl(BigDecimal.ZERO, null);
		money2 = new MoneyImpl(BigDecimal.ZERO.setScale(2), CAD);
		assertFalse(money1.equals(money2));
		
	}
	
	/** Tests {@link MoneyImpl#compareTo(Money)}. */
	@Test
	public void testCompareTo() {
		// CHECKSTYLE:OFF -- we want magic number here
		assertMoneyValuesEqual(CAD, 3.00f, 3f);
		assertMoneyValuesNotEqual(CAD, 3.12f, 3.00f);
		assertMoneyValuesEqual(CAD, 9.3f, 9.3f);
		assertMoneyValuesEqual(CAD, 7.8f, 7.8f);
		assertMoneyValuesEqual("CAD has 2 decimals, should be rounded the same", CAD, 7.8f, 7.804f);
		assertMoneyValuesEqual("JPY has 0 decimals, should round to same value", JPY, 7.6f, 7.904f);
		assertMoneyValuesNotEqual("Money values should be rounded to different values", CAD, 7.806f, 7.801f);
		assertMoneyValuesEqual("JPY has 0 decimals, round limits", JPY, 23.49337f, 23f);
		assertMoneyValuesNotEqual("JPY has 0 decimals, rounds to different values", JPY, 23.5f, 23f);
		assertMoneyValuesEqual("JPY has 0 decimals, rounds to same values", JPY, 23.49f, 23f);
		// CHECKSTYLE:ON
	}

	/**
	 * Tests the multiply() method.
	 */
	@Test
	public void testMultiply() {
		Currency currency = Currency.getInstance(Locale.CANADA);
		Money money1 = new MoneyImpl(new BigDecimal("3.00"), currency);
		
		MoneyImpl expectedResult = new MoneyImpl(new BigDecimal("6.00"), currency);
		assertEquals(expectedResult, money1.multiply(new BigDecimal("2")));
		assertEquals(expectedResult, money1.multiply(2));
	}
	
	/**
	 * Tests that toString() returns a non-null result.
	 */
	@Test
	public void testToString() {
		Money money1 = new MoneyImpl(null, null);
		assertNotNull(money1.toString());

		Currency currency = Currency.getInstance(Locale.CANADA);
		money1 = new MoneyImpl(null, currency);
		assertNotNull(money1.toString());
		
		money1 = new MoneyImpl(BigDecimal.ONE, currency);
		assertNotNull(money1.toString());
	}
}
