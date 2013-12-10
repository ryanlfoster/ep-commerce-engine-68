/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules;

import com.elasticpath.persistence.api.Persistable;

/**
 * Represents a parameter of a rule condition, such as the category that a product must belong to to qualify for a promotion.
 */
public interface RuleParameter extends Persistable {

	// Constants for parameter keys

	/** Category UID. */
	String CATEGORY_CODE_KEY = "categoryCode";

	/** Product UID. */
	String PRODUCT_CODE_KEY = "productCode";

	/** Amount of a discount. */
	String DISCOUNT_AMOUNT_KEY = "discountAmount";

	/** Percent of a discount. */
	String DISCOUNT_PERCENT_KEY = "discountPercent";

	/** Currency. */
	String CURRENCY_KEY = "currency";

	/** CustomerGroup id. */
	String CUSTOMERGROUP_ID_KEY = "customerGroupId";

	/** Cart subtotal amount. */
	String SUBTOTAL_AMOUNT_KEY = "subtotalAmount";

	/** Number of items. */
	String NUM_ITEMS_KEY = "numItems";

	/** SKU code. */
	String SKU_CODE_KEY = "skuCode";

	/** Shipping service level code. */
	String SHIPPING_SERVICE_LEVEL_CODE_KEY = "shippingServiceLevelCode";
	
	/** Boolean parameter value must be "true" or "false". */
	String BOOLEAN_KEY = "booleanCondition";
	
	/** NumItems quantifier parameter value must be "at least" or "exactly". */
	String NUM_ITEMS_QUANTIFIER_KEY = "numItemsQuantifier";
	
	/** Brand code. */
	String BRAND_CODE_KEY = "brandCode";

	/** Limited Usage Promotion - allowed limit. */
	String ALLOWED_LIMIT = "allowedLimit";

	/** Limited Usage Promotion - promotion ID. */
	String LIMITED_USAGE_PROMOTION_ID = "lupID";

	/** Coupon code for promotion. */
	String COUPON_CODE = "couponCode";

	/**
	 * Rule code for CouponAssignmentAction.
	 */
	String RULE_CODE_KEY = "ruleCode";
	
	/**
	 * Coupon prefix for CouponAssignmentAction.
	 */
	String COUPON_PREFIX = "couponPrefix";
	
	/**
	 * Get the parameter key.
	 *
	 * @return the parameter key
	 */
	String getKey();

	/**
	 * Set the parameter key.
	 *
	 * @param key the parameter key
	 */
	void setKey(final String key);

	/**
	 * Get the parameter value.
	 *
	 * @return the parameter value
	 */
	String getValue();

	/**
	 * Set the parameter value.
	 *
	 * @param value the parameter value
	 */
	void setValue(final String value);

	/**
	 * Get the display text for this parameter.
	 *
	 * @return the the display text, or the parameter value if there is no display text
	 */
	String getDisplayText();

	/**
	 * Set the text to be displayed for this parameter. For example, the display text for a sku code id long. might be the actual text sku code
	 *
	 * @param displayText the text to display. Set to null to use the parameter value.
	 */
	void setDisplayText(final String displayText);
}
