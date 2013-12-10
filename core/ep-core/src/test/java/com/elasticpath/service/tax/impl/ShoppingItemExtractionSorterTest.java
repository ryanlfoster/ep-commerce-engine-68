package com.elasticpath.service.tax.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.elasticpath.service.tax.impl.ApportioningCalculator.ShoppingItemExtractionSorter;


/**
 * Tests PriceSkuSortingStrategy.
 */
public class ShoppingItemExtractionSorterTest {

	private static final String ITEM_2 = "item2";
	private static final String ITEM_1 = "item1";

	/** */
	@Test
	public void testSortUniqueIdsWithEqualSkuCode() {
		ShoppingItemExtraction item1 = new ShoppingItemExtraction(ITEM_1,
				"skuCode", BigDecimal.TEN);
		ShoppingItemExtraction item2 = new ShoppingItemExtraction(ITEM_2,
				"skuCode", BigDecimal.ONE);
		ShoppingItemExtractionSorter strategy = new ShoppingItemExtractionSorter();
		List<ShoppingItemExtraction> sortedItems = strategy.sortByPriceSku(Arrays.asList(item1,
				item2));

		assertEquals(ITEM_1, sortedItems.get(0).getGuid());
		assertEquals(ITEM_2, sortedItems.get(1).getGuid());
	}
	
	/** */
	@Test
	public void testSortUniqueIdsWithEqualAmount() {
		ShoppingItemExtraction item1 = new ShoppingItemExtraction(ITEM_1,
				"skuCode2", BigDecimal.TEN);
		ShoppingItemExtraction item2 = new ShoppingItemExtraction(ITEM_2,
				"skuCode1", BigDecimal.TEN);
		ShoppingItemExtractionSorter strategy = new ShoppingItemExtractionSorter();
		List<ShoppingItemExtraction> sortedItems = strategy.sortByPriceSku(Arrays.asList(item1,
				item2));

		assertEquals(ITEM_1, sortedItems.get(0).getGuid());
		assertEquals(ITEM_2, sortedItems.get(1).getGuid());
	}

}
