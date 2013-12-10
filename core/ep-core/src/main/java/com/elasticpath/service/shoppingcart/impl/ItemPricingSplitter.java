/**
 * 
 */
package com.elasticpath.service.shoppingcart.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Splits an amount among a number of items. The amounts assigned to each item should not differ in more than one cent.
 */
public class ItemPricingSplitter {
	/**
	 * A splitter that splits price and discount against quantity.
	 */
	protected static final class PriceDiscountQuantitySplitter {
		private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

		private static final BigDecimal ONE_CENT = new BigDecimal(".01");

		private final BigDecimal priceLow;
		private final BigDecimal priceHigh; // priceLow + .01c

		private final BigDecimal priceLowQuantity;
		private final BigDecimal priceHighQuantity;

		/**
		 * Splits price and discount according to quantity.
		 * 
		 * @param price the price.
		 * @param discount the discount.
		 * @param quantity the quantity.
		 * @return a bean that contains all the information.
		 */
		public static PriceDiscountQuantitySplitter split(final BigDecimal price, final BigDecimal discount, final int quantity) {
			return new PriceDiscountQuantitySplitter(price, quantity);
		}

		/**
		 * @param price
		 * @param quantity
		 */
		private PriceDiscountQuantitySplitter(final BigDecimal price, final int quantity) {
			IntegerDivisionCalculator priceDiv = new IntegerDivisionCalculator(price, quantity);
			priceLow = priceDiv.getQuotient();
			priceHigh = priceLow.add(ONE_CENT);

			BigDecimal priceRemainder = priceDiv.getRemainder().multiply(ONE_HUNDRED);
			priceHighQuantity = priceRemainder;
			priceLowQuantity = BigDecimal.valueOf(quantity).subtract(priceRemainder);
		}

		/**
		 * @return the highPriceHighDiscountQuantity
		 */
		public BigDecimal getPriceHighQuantity() {
			return priceHighQuantity;
		}

		/**
		 * @return the lowPriceLowDiscountQuantity
		 */
		public BigDecimal getPriceLowQuantity() {
			return priceLowQuantity;
		}

		/**
		 * @return the priceHigh
		 */
		public BigDecimal getPriceHigh() {
			return priceHigh;
		}

		/**
		 * @return the priceLow
		 */
		public BigDecimal getPriceLow() {
			return priceLow;
		}
	}

	/**
	 * Splits {@link ItemPricing}.
	 * 
	 * @param pricing {@link ItemPricing} to split
	 * @return a collection of split {@link ItemPricing}
	 */
	public Collection<ItemPricing> split(final ItemPricing pricing) {
		PriceDiscountQuantitySplitter splitter = PriceDiscountQuantitySplitter.split(pricing.getPrice(), pricing.getDiscount(), pricing
				.getQuantity());

		List<ItemPricing> splittedItemPricing = new ArrayList<ItemPricing>();
		addLinePricing(splittedItemPricing, splitter.priceLow, BigDecimal.ZERO, splitter.priceLowQuantity.intValue());
		addLinePricing(splittedItemPricing, splitter.priceHigh,  BigDecimal.ZERO, splitter.priceHighQuantity.intValue());
		return splittedItemPricing;
	}

	private void addLinePricing(final List<ItemPricing> pricings, final BigDecimal price, final BigDecimal discount, final int quantity) {
		if (quantity > 0) {
			ItemPricing itemPricing = new ItemPricing(price, discount, quantity);
			pricings.add(itemPricing);
		}
	}
}