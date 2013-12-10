package com.elasticpath.domain.shoppingcart.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Represents a discount to the shipping amount.
 */
public class ShippingDiscountRecordImpl extends AbstractDiscountRecordImpl {

	/** Serial Version ID. **/
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Default constructor.
	 * 
	 * @param discountAmount The amount of this discount per discount record.
	 */
	public ShippingDiscountRecordImpl(final BigDecimal discountAmount) {
		super(discountAmount);
	}

	/**
	 * A coupon is used once for a shipping discount.
	 * 
	 * @param action The action (ignored).
	 * @param shoppingCart the cart.
	 * @return The number of coupon uses.
	 */
	@Override
	public int getCouponUsesRequired(final RuleAction action, final ShoppingCart shoppingCart) {
		if (isSuperceded() || shoppingCart.getSelectedShippingServiceLevel() == null) {
			return 0;
		}
		String uidShippingLevel = action.getParamValue(RuleParameter.SHIPPING_SERVICE_LEVEL_CODE_KEY);
		String shippingCode = shoppingCart.getSelectedShippingServiceLevel().getCode();
		if (!shippingCode.equals(uidShippingLevel)) {
			return 0;
		}
		return 1;
	}
}
