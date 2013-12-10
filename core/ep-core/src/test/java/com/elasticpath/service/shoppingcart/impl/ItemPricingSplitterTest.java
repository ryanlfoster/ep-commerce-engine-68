package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.elasticpath.service.shoppingcart.impl.ItemPricingSplitter.PriceDiscountQuantitySplitter;

/** */
public class ItemPricingSplitterTest {
	private static final BigDecimal ONE_CENT = new BigDecimal("0.01");

	private static final BigDecimal TWO_CENTS = new BigDecimal("0.02");

	private static final BigDecimal SIX_CENTS = new BigDecimal("0.06");

	private static final BigDecimal THREE_CENTS = new BigDecimal("0.03");

	private static final BigDecimal FOUR_CENTS = new BigDecimal("0.04");

	private static final BigDecimal EIGHT_CENTS = new BigDecimal("0.08");

	private static final int ONE = 1;

	private static final int TWO = 2;

	private static final int THREE = 3;

	private static final int FIVE = 5;

	private static final int FOUR = 4;

	/**	 */
	@Test
	public void testPriceDiscountQuantitySplittingWithNoPriceRemainderNoDiscountRemainder() {
		PriceDiscountQuantitySplitter splitter = PriceDiscountQuantitySplitter.split(EIGHT_CENTS, FOUR_CENTS, FOUR);

		assertEquals(FOUR, splitter.getPriceLowQuantity().intValue());
		assertEquals(TWO_CENTS, splitter.getPriceLow());
	}

	/**	 */
	@Test
	public void testPriceDiscountQuantitySplittingWithPriceRemainderHigherThanDiscountRemainder() {
		PriceDiscountQuantitySplitter splitter = PriceDiscountQuantitySplitter.split(FOUR_CENTS, THREE_CENTS, FIVE);
		assertEquals(FOUR, splitter.getPriceHighQuantity().intValue());
		assertEquals(ONE, splitter.getPriceLowQuantity().intValue());
	}

	/** */
	@Test
	public void testSimpleDiscountSplit() {
		PriceDiscountQuantitySplitter splitter = PriceDiscountQuantitySplitter.split(EIGHT_CENTS, FOUR_CENTS, FIVE);

		assertEquals(THREE, splitter.getPriceHighQuantity().intValue());
		assertEquals(TWO, splitter.getPriceLowQuantity().intValue());
		assertEquals(TWO_CENTS, splitter.getPriceHigh());
		assertEquals(ONE_CENT, splitter.getPriceLow());
	}

	/** */
	@Test
	public void testRoundingErrorDiscountSplit() {
		PriceDiscountQuantitySplitter splitter = PriceDiscountQuantitySplitter.split(SIX_CENTS, THREE_CENTS, FIVE);

		assertEquals(ONE, splitter.getPriceHighQuantity().intValue());
		assertEquals(FOUR, splitter.getPriceLowQuantity().intValue());
		assertEquals(TWO_CENTS, splitter.getPriceHigh());
		assertEquals(ONE_CENT, splitter.getPriceLow());
	}
}
