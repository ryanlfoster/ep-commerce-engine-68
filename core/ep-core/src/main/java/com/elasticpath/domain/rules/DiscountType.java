/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.rules;

import java.util.Collection;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Discounts are classified into different types depending on what is discounted.
 */
public class DiscountType extends AbstractExtensibleEnum<DiscountType> {

	private static final long serialVersionUID = 1L;

	/** Cart item discount ordinal. */
	public static final int CART_ITEM_DISCOUNT_ORDINAL = 0;

	/**
	 * A promotion discount applied against a specific product, sku, or category of products.
	 */
	public static final DiscountType CART_ITEM_DISCOUNT = new DiscountType(CART_ITEM_DISCOUNT_ORDINAL, "CART_ITEM_DISCOUNT");

	/** Cart subtotal discount ordinal. */
	public static final int CART_SUBTOTAL_DISCOUNT_ORDINAL = 1;

	/**
	 * A promotion discount applied against an entire shopping cart.
	 */
	public static final DiscountType CART_SUBTOTAL_DISCOUNT = new DiscountType(CART_SUBTOTAL_DISCOUNT_ORDINAL, "CART_SUBTOTAL_DISCOUNT");

	/** Shipping discount ordinal. */
	public static final int SHIPPING_DISCOUNT_ORDINAL = 2;

	/**
	 * A promotion discount applied against a shipping service level.
	 */
	public static final DiscountType SHIPPING_DISCOUNT = new DiscountType(SHIPPING_DISCOUNT_ORDINAL, "SHIPPING_DISCOUNT");

	/** Catalog discount ordinal. */
	public static final int CATALOG_DISCOUNT_ORDINAL = 3;

	/**
	 * A promotion discount applied against a catalog item.
	 */
	public static final DiscountType CATALOG_DISCOUNT = new DiscountType(CATALOG_DISCOUNT_ORDINAL, "CATALOG_DISCOUNT");

	/** coupon discount ordinal. */
	public static final int COUPON_DISCOUNT_ORDINAL = 4;

	/**
	 * A promotion discount applied against a shopping cart through a coupon.
	 */
	public static final DiscountType COUPON_DISCOUNT = new DiscountType(COUPON_DISCOUNT_ORDINAL, "COUPON_DISCOUNT");

	/**
	 * Instantiates a new discount type.
	 *
	 * @param ordinal the ordinal
	 * @param name the name
	 */
	public DiscountType(final int ordinal, final String name) {
		super(ordinal, name, DiscountType.class);
	}

	@Override
	protected Class<DiscountType> getEnumType() {
		return DiscountType.class;
	}


    /**
     * Find all enum values.
     * @return the enum values
     */
	public static Collection<DiscountType> values() {
		return values(DiscountType.class);
	}

}
