/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.rules;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * This enumerator lists all types of rule elements. The property key of each <code>RuleElementType</code> must match the corresponding
 * <code>RuleElement</code>'s discriminator-value and the spring context bean id.
 */
public class RuleElementType extends AbstractExtensibleEnum<RuleElementType> {

	private static final long serialVersionUID = 1L;

	/**
	 * Rule Eligibilities. -----------------------------
	 */

	/** Customer group eligibility ordinal. */
	public static final int CUSTOMER_GROUP_ELEGIBILITY_ORDINAL = 1;

	/**
	 * Customer group eligibility.
	 */
	public static final RuleElementType CUSTOMER_GROUP_ELIGIBILITY = new RuleElementType(CUSTOMER_GROUP_ELEGIBILITY_ORDINAL, 
			"customerGroupEligibility");

	/** Everyone eligibility ordinal. */
	public static final int EVERYONE_ELIGIBILITY_ORDINAL = 2;
	
	/** Everyone eligibility. */
	public static final RuleElementType EVERYONE_ELIGIBILITY = new RuleElementType(EVERYONE_ELIGIBILITY_ORDINAL,
			"everyoneEligibility");

	/** Existing customer eligibility ordinal. */
	public static final int EXISTING_CUSTOMER_ELIGIBILITY_ORDINAL = 3;

	/**
	 * Existing customer eligibility.
	 */
	public static final RuleElementType EXISTING_CUSTOMER_ELIGIBILITY = new RuleElementType(EXISTING_CUSTOMER_ELIGIBILITY_ORDINAL,
			"existingCustomerEligibility");

	/**	First time buyer eligibility. */
	public static final int FIRST_TIME_BUYER_ELIGIBILITY_ORDINAL = 4;

	/**
	 * First time buyer eligibility.
	 */
	public static final RuleElementType FIRST_TIME_BUYER_ELIGIBILITY = new RuleElementType(FIRST_TIME_BUYER_ELIGIBILITY_ORDINAL,
			"firstTimeBuyerEligibility");
	

	/**
	 * Rule Conditions. -----------------------------
	 */

	/** Brand condition ordinal. */
	public static final int BRAND_CONDITION_ORDINAL = 100;

	/**
	 * Brand condition.
	 */
	public static final RuleElementType BRAND_CONDITION = new RuleElementType(BRAND_CONDITION_ORDINAL,
			"brandCondition");

	/** Cart contains items of category condition ordinal. */
	public static final int CART_CONTAINS_ITEMS_OF_CATEGORY_CONDITION_ORDINAL = 101;
	
	/**	Cart contains items of category condition. */
	public static final RuleElementType CART_CONTAINS_ITEMS_OF_CATEGORY_CONDITION = new RuleElementType(
			CART_CONTAINS_ITEMS_OF_CATEGORY_CONDITION_ORDINAL, "cartContainsItemsOfCategoryCondition");
	
	/** Cart currency condition ordinal. */
	public static final int CART_CURRENCY_CONDITION_ORDINAL = 102;
	
	/** Cart currency condition. */
	public static final RuleElementType CART_CURRENCY_CONDITION = new RuleElementType(CART_CURRENCY_CONDITION_ORDINAL,
			"cartCurrencyCondition");
	
	/** Cart subtotal condition ordinal. */
	public static final int CART_SUBTOTAL_CONDITION_ORDINAL = 103;

	/**
	 * Cart subtotal condition.
	 */
	public static final RuleElementType CART_SUBTOTAL_CONDITION = new RuleElementType(CART_SUBTOTAL_CONDITION_ORDINAL,
			"cartSubtotalCondition");

	/** Product category condition ordinal. */
	public static final int PRODUCT_CATEGORY_CONDITION_ORDINAL = 104;

	/**
	 * Product category condition.
	 */
	public static final RuleElementType PRODUCT_CATEGORY_CONDITION = new RuleElementType(PRODUCT_CATEGORY_CONDITION_ORDINAL,
			"productCategoryCondition");

	/** Product condition ordinal. */
	public static final int PRODUCT_CONDITION_ORDINAL = 105;

	/**
	 * Product condition.
	 */
	public static final RuleElementType PRODUCT_CONDITION = new RuleElementType(PRODUCT_CONDITION_ORDINAL, "productCondition");

	/** Product in cart condition ordinal. */
	public static final int PRODUCT_IN_CART_CONDITION_ORDINAL = 106;

	/**
	 * Product in cart condition.
	 */
	public static final RuleElementType PRODUCT_IN_CART_CONDITION = new RuleElementType(PRODUCT_IN_CART_CONDITION_ORDINAL,
	"productInCartCondition");

	/** SKU in cart condition ordinal. */
	public static final int SKU_IN_CART_CONDITION_ORDINAL = 107;
	
	/**	SKU in cart condition. */
	public static final RuleElementType SKU_IN_CART_CONDITION = new RuleElementType(SKU_IN_CART_CONDITION_ORDINAL,
		"skuInCartCondition");
	
	/**	Any SKU in cart condition ordinal. */
	public static final int ANY_SKU_IN_CART_CONDITION_ORDINAL = 108;
	
	/**	Any SKU in cart condition. */
	public static final RuleElementType ANY_SKU_IN_CART_CONDITION = new RuleElementType(ANY_SKU_IN_CART_CONDITION_ORDINAL,
		"anySkuInCartCondition");
	
	/**	Limited Usage Promotion condition ordinal. */
	public static final int LIMITED_USAGE_PROMOTION_CONDITION_ORDINAL = 109;

	/**	Limited Usage Promotion condition. */
	public static final RuleElementType LIMITED_USAGE_PROMOTION_CONDITION = new RuleElementType(LIMITED_USAGE_PROMOTION_CONDITION_ORDINAL,
			"limitedUsagePromotionCondition");
	
	/**
	 * Rule Actions. -----------------------------
	 */

	/**	Cart category amount discount action ordinal. */
	public static final int CART_CATEGORY_AMOUNT_DISCOUNT_ACTION_ORDINAL = 200;

	/**
	 * Cart category amount discount action.
	 */
	public static final RuleElementType CART_CATEGORY_AMOUNT_DISCOUNT_ACTION = new RuleElementType(CART_CATEGORY_AMOUNT_DISCOUNT_ACTION_ORDINAL,
			"cartCategoryAmountDiscountAction");

	/**	Cart category percent discount action ordinal. */
	public static final int CART_CATEGORY_PERCENT_DISCOUNT_ACTION_ORDINAL = 201;

	/**
	 * Cart category percent discount action.
	 */
	public static final RuleElementType CART_CATEGORY_PERCENT_DISCOUNT_ACTION = new RuleElementType(CART_CATEGORY_PERCENT_DISCOUNT_ACTION_ORDINAL,
			"cartCategoryPercentDiscountAction");

	/**	Cart N free SKUs action ordinal. */
	public static final int CART_N_FREE_SKUS_ACTION_ORDINAL = 202;

	/**
	 * Cart N free SKUs action.
	 */
	public static final RuleElementType CART_N_FREE_SKUS_ACTION = new RuleElementType(CART_N_FREE_SKUS_ACTION_ORDINAL,
			"cartNFreeSkusAction");

	/**	Cart Nth product percent discount action ordinal. */
	public static final int CART_NTH_PRODUCT_PERCENT_DISCOUNT_ACTION_ORDINAL = 203;

	/**
	 * Cart Nth product percent discount action.
	 */
	public static final RuleElementType CART_NTH_PRODUCT_PERCENT_DISCOUNT_ACTION = new RuleElementType(
			CART_NTH_PRODUCT_PERCENT_DISCOUNT_ACTION_ORDINAL, "cartNthProductPercentDiscountAction");

	/**	Cart product amount discount action ordinal. */
	public static final int CART_PRODUCT_AMOUNT_DISCOUNT_ACTION_ORDINAL = 204;

	/**
	 * Cart product amount discount action.
	 */
	public static final RuleElementType CART_PRODUCT_AMOUNT_DISCOUNT_ACTION = new RuleElementType(CART_PRODUCT_AMOUNT_DISCOUNT_ACTION_ORDINAL,
			"cartProductAmountDiscountAction");

	/**	Cart product percent discount action ordinal. */
	public static final int CART_PRODUCT_PERCENT_DISCOUNT_ACTION_ORDINAL = 205;

	/**
	 * Cart product percent discount action.
	 */
	public static final RuleElementType CART_PRODUCT_PERCENT_DISCOUNT_ACTION = new RuleElementType(CART_PRODUCT_PERCENT_DISCOUNT_ACTION_ORDINAL,
			"cartProductPercentDiscountAction");

	/**	Cart SKU amount discount action ordinal. */
	public static final int CART_SKU_AMOUNT_DISCOUNT_ACTION_ORDINAL = 206;
	
	/**	Cart SKU amount discount action. */
	public static final RuleElementType CART_SKU_AMOUNT_DISCOUNT_ACTION = new RuleElementType(CART_SKU_AMOUNT_DISCOUNT_ACTION_ORDINAL,
			"cartSkuAmountDiscountAction");
	
	/**	Cart any SKU amount discount action ordinal. */
	public static final int CART_ANY_SKU_AMOUNT_DISCOUNT_ACTION_ORDINAL = 207;
	
	/**
	 * Cart any SKU amount discount action.
	 */
	public static final RuleElementType CART_ANY_SKU_AMOUNT_DISCOUNT_ACTION = new RuleElementType(CART_ANY_SKU_AMOUNT_DISCOUNT_ACTION_ORDINAL,
			"cartAnySkuAmountDiscountAction");

	/**	Cart SKU percent discount action ordinal. */
	public static final int CART_SKU_PERCENT_DISCOUNT_ACTION_ORDINAL = 208;
	
	/**	Cart SKU percent discount action. */
	public static final RuleElementType CART_SKU_PERCENT_DISCOUNT_ACTION = new RuleElementType(CART_SKU_PERCENT_DISCOUNT_ACTION_ORDINAL,
			"cartSkuPercentDiscountAction");
	
	/**	Cart any SKU percent discount action ordinal. */
	public static final int CART_ANY_SKU_PERCENT_DISCOUNT_ACTION_ORDINAL = 209;
	
	/**
	 * Cart any SKU percent discount action.
	 */
	public static final RuleElementType CART_ANY_SKU_PERCENT_DISCOUNT_ACTION = new RuleElementType(CART_ANY_SKU_PERCENT_DISCOUNT_ACTION_ORDINAL,
			"cartAnySkuPercentDiscountAction");

	/**	Cart subtotal amount discount action ordinal. */
	public static final int CART_SUBTOTAL_AMOUNT_DISCOUNT_ACTION_ORDINAL = 210;

	/**
	 * Cart subtotal amount discount action.
	 */
	public static final RuleElementType CART_SUBTOTAL_AMOUNT_DISCOUNT_ACTION = new RuleElementType(CART_SUBTOTAL_AMOUNT_DISCOUNT_ACTION_ORDINAL,
			"cartSubtotalAmountDiscountAction");

	/**	Cart subtotal percent discount action ordinal. */
	public static final int CART_SUBTOTAL_PERCENT_DISCOUNT_ACTION_ORDINAL = 211;

	/**
	 * Cart subtotal percent discount action.
	 */
	public static final RuleElementType CART_SUBTOTAL_PERCENT_DISCOUNT_ACTION = new RuleElementType(CART_SUBTOTAL_PERCENT_DISCOUNT_ACTION_ORDINAL,
			"cartSubtotalPercentDiscountAction");

	/**	Cart subtotal discount action ordinal. */
	public static final int CART_SUBTOTAL_DISCOUNT_ACTION_ORDINAL = 212;

	/**
	 * Cart subtotal discount action.
	 */
	public static final RuleElementType CART_SUBTOTAL_DISCOUNT_ACTION = new RuleElementType(CART_SUBTOTAL_DISCOUNT_ACTION_ORDINAL,
			"cartSubtotalDiscountAction");

	/**	Cart SKU amount discount action ordinal. */
	public static final int CATALOG_SKU_AMOUNT_DISCOUNT_ACTION_ORDINAL = 213;

	/**
	 * Cart SKU amount discount action.
	 */
	public static final RuleElementType CATALOG_SKU_AMOUNT_DISCOUNT_ACTION = new RuleElementType(CATALOG_SKU_AMOUNT_DISCOUNT_ACTION_ORDINAL,
			"skuAmountDiscountAction");

	/**	Cart SKU percent discount action ordinal. */
	public static final int CATALOG_SKU_PERCENT_DISCOUNT_ACTION_ORDINAL = 214;

	/**
	 * Cart SKU percent discount action.
	 */
	public static final RuleElementType CATALOG_SKU_PERCENT_DISCOUNT_ACTION = new RuleElementType(CATALOG_SKU_PERCENT_DISCOUNT_ACTION_ORDINAL,
			"skuPercentDiscountAction");

	/**	Product amount discount action ordinal. */
	public static final int PRODUCT_AMOUNT_DISCOUNT_ACTION_ORDINAL = 215;

	/**
	 * Product amount discount action.
	 */
	public static final RuleElementType PRODUCT_AMOUNT_DISCOUNT_ACTION = new RuleElementType(PRODUCT_AMOUNT_DISCOUNT_ACTION_ORDINAL,
			"productAmountDiscountAction");

	/**	Product percent discount action ordinal. */
	public static final int PRODUCT_PERCENT_DISCOUNT_ACTION_ORDINAL = 216;

	/**
	 * Product percent discount action.
	 */
	public static final RuleElementType PRODUCT_PERCENT_DISCOUNT_ACTION = new RuleElementType(PRODUCT_PERCENT_DISCOUNT_ACTION_ORDINAL,
			"productPercentDiscountAction");

	/**	Shipping amount discount action ordinal. */
	public static final int SHIPPING_AMOUNT_DISCOUNT_ACTION_ORDINAL = 217;

	/**	Shipping amount discount action. */
	public static final RuleElementType SHIPPING_AMOUNT_DISCOUNT_ACTION = new RuleElementType(SHIPPING_AMOUNT_DISCOUNT_ACTION_ORDINAL,
			"shippingAmountDiscountAction");

	/**	Shipping percent discount action ordinal. */
	public static final int SHIPPING_PERCENT_DISCOUNT_ACTION_ORDINAL = 218;

	/**
	 * Shipping percent discount action.
	 */
	public static final RuleElementType SHIPPING_PERCENT_DISCOUNT_ACTION = new RuleElementType(SHIPPING_PERCENT_DISCOUNT_ACTION_ORDINAL,
			"shippingPercentDiscountAction");
	
	/**	Currency percent discount action ordinal. */
	public static final int CATALOG_CURRENCY_PERCENT_DISCOUNT_ACTION_ORDINAL = 219;

	/**	Currency percent discount action. */
	public static final RuleElementType CATALOG_CURRENCY_PERCENT_DISCOUNT_ACTION = new RuleElementType(
			CATALOG_CURRENCY_PERCENT_DISCOUNT_ACTION_ORDINAL, "catalogCurrencyPercentDiscountAction");
	
	/**	Currency amount discount action ordinal. */
	public static final int CATALOG_CURRENCY_AMOUNT_DISCOUNT_ACTION_ORDINAL = 220;
	
	/**
	 * Currency amount discount action.
	 */
	public static final RuleElementType CATALOG_CURRENCY_AMOUNT_DISCOUNT_ACTION = new RuleElementType(CATALOG_CURRENCY_AMOUNT_DISCOUNT_ACTION_ORDINAL,
			"catalogCurrencyAmountDiscountAction");

	/**	Limited use coupon code condition ordinal. */
	public static final int LIMITED_USE_COUPON_CODE_CONDITION_ORDINAL = 221;
	
	/**	Limited use coupon code condition. */
	public static final RuleElementType LIMITED_USE_COUPON_CODE_CONDITION = new RuleElementType(LIMITED_USE_COUPON_CODE_CONDITION_ORDINAL,
		"limitedUseCouponCodeCondition");
	
	/**	Coupon assignment action ordinal. */
	public static final int COUPON_ASSIGNMENT_ACTION_ORDINAL = 222;
	
	/**	Coupon assignment action. */
	public static final RuleElementType COUPON_ASSIGNMENT_ACTION = new RuleElementType(COUPON_ASSIGNMENT_ACTION_ORDINAL,
		"couponAssignmentAction");
	
	private final String propertyKey;

	/**
	 * Instantiates a new rule element type.
	 *
	 * @param ordinal the ordinal
	 * @param name the name
	 */
	public RuleElementType(final int ordinal, final String name) {
		super(ordinal, name, RuleElementType.class);
		this.propertyKey = name;
	}

	/**
	 * Get the localization property key.
	 * 
	 * @return the localized property key
	 */
	public String getPropertyKey() {
		return this.propertyKey;
	}

	@Override
	protected Class<RuleElementType> getEnumType() {
		return RuleElementType.class;
	}
}
