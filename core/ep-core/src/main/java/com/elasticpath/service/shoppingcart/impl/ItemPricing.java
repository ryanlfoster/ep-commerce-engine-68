/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.shoppingcart.impl;

import java.math.BigDecimal;

/**
 * Keeps line item price and discount amounts.
 */
public class ItemPricing {
	private final BigDecimal price;

	private final BigDecimal discount;

	private final int quantity;

	/**
	 * @param price line item price
	 * @param discount line item discount
	 * @param quantity the quantity
	 */
	public ItemPricing(final BigDecimal price, final BigDecimal discount, final int quantity) {
		this.price = price;
		this.discount = discount;
		this.quantity = quantity;
	}

	/**
	 * @return the line price
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * @return the line discount
	 */
	public BigDecimal getDiscount() {
		return discount;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		if (price != null) {
			result = prime * result + price.hashCode();
		}

		if (discount != null) {
			result = prime * result + discount.hashCode();
		}

		result = prime * result + quantity;

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		ItemPricing other = (ItemPricing) obj;
		if (price == null) {
			if (other.price != null) {
				return false;
			}
		} else if (!price.equals(other.price)) {
			return false;
		}

		if (discount == null) {
			if (other.discount != null) {
				return false;
			}
		} else if (!discount.equals(other.discount)) {
			return false;
		}

		if (quantity != other.quantity) {
			return false;
		}
		return true;
	}
}