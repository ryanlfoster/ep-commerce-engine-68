package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests BundleApportioningCalculator.
 */
public class BundleApportioningCalculatorTest {
	private static final BigDecimal EIGHTY = new BigDecimal("80.00");

	private static final BigDecimal HUNDRED = new BigDecimal("100.00");

	private static final BigDecimal THREE = new BigDecimal("3.00");

	private static final BigDecimal TWENTY = new BigDecimal("20.00");

	private static final BigDecimal TWENTY_FIVE = new BigDecimal("25.00");

	private BundleApportioningCalculator calculator;

	/** */
	@Before
	public void setUp() {
		calculator = new BundleApportioningCalculator();
	}

	/** */
	@Test
	public void testApportionPricing() {
		ItemPricing pricingToApportion = new ItemPricing(EIGHTY, HUNDRED, 1);

		ItemPricing const1 = new ItemPricing(BigDecimal.ONE, null, 1);
		ItemPricing const2 = new ItemPricing(THREE, null, 1);

		Map<String, ItemPricing> constituents = new HashMap<String, ItemPricing>();
		constituents.put("1", const1);
		constituents.put("2", const2);

		Map<String, ItemPricing> result = calculator.apportion(pricingToApportion, constituents);
		ItemPricing result1 = result.get("1");
		assertEquals(TWENTY, result1.getPrice());
		assertEquals(TWENTY_FIVE, result1.getDiscount());
	}
	
	/** */
	@Test
	public void testSplittingApportionPricing() {
		ItemPricing pricingToApportion = new ItemPricing(new BigDecimal("13.71"), BigDecimal.ZERO, 1);

		final int qty = 87;
		ItemPricing const1 = new ItemPricing(new BigDecimal("75.00"), BigDecimal.ZERO, qty);

		Map<String, ItemPricing> constituents = new HashMap<String, ItemPricing>();
		constituents.put("1", const1);

		Map<String, ItemPricing> result = calculator.apportion(pricingToApportion, constituents);
		ItemPricing result1 = result.get("1");
		
		ItemPricingSplitter splitter = new ItemPricingSplitter();
		Collection<ItemPricing> itemPricings = splitter.split(result1);
		List<ItemPricing> ipArrary = new ArrayList<ItemPricing>();
		ipArrary.addAll(itemPricings);
		
		assertEquals(2, ipArrary.size());
		
		final int qty0 = 21;
		assertEquals(new BigDecimal("0.15"), ipArrary.get(0).getPrice());
		assertEquals(qty0, ipArrary.get(0).getQuantity());
		
		final int qty1 = 66;
		assertEquals(new BigDecimal("0.16"), ipArrary.get(1).getPrice());
		assertEquals(qty1, ipArrary.get(1).getQuantity());
	}
	
	/** */
	@Test
	public void testExtractPricesWithPreservedOrder() {
		Map<String, ItemPricing> orderedItemPricingMap = new HashMap<String, ItemPricing>();
		orderedItemPricingMap.put("1", new ItemPricing(THREE, THREE, 1));
		orderedItemPricingMap.put("2", new ItemPricing(THREE, THREE, 1));
		orderedItemPricingMap.put("3", new ItemPricing(THREE, THREE, 1));

		Map<String, BigDecimal> extractedPricesMap = calculator.extractPricesWithPreservedOrder(orderedItemPricingMap);

		assertEquals(extractedPricesMap.size(), orderedItemPricingMap.size());

		Iterator<String> orderedItemPricingIterator = orderedItemPricingMap.keySet().iterator();
		Iterator<String> extractedPricesIterator = extractedPricesMap.keySet().iterator();

		while (orderedItemPricingIterator.hasNext()) {
			String orderedItemPriceingID = orderedItemPricingIterator.next();
			String extractedPricesID = extractedPricesIterator.next();
			assertEquals(extractedPricesID, orderedItemPriceingID);
		}
	}
}
