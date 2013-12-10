/*
 * Copyright (c) Elastic Path Software Inc., 2011
 */

package com.elasticpath.service.shopper.impl;

import java.util.Set;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.customer.CustomerSessionShopperUpdateHandler;
import com.elasticpath.service.rules.CouponService;

/**
 * Invalidates the ShoppingCart promotions when the Shopper changes.
 */
public class PromotionCodeInvalidatorForShopperUpdates implements CustomerSessionShopperUpdateHandler {

    private final CouponService couponService;
    
	/**
     * Alternate constructor.
     * @param couponService the couponService
     */
    public PromotionCodeInvalidatorForShopperUpdates(final CouponService couponService) {
    	this.couponService = couponService;
    }
    
    /**
     * Removes promotions from the ShoppingCart when there is a change in the Shopper.
     *
     * {@inheritDoc}
     */
    public void invalidateShopper(final CustomerSession customerSession, final Shopper invalidShopper) {
        
        final Shopper currentShopper = customerSession.getShopper();
        
        // If the invalidated shopping context was for an anonymous session, do not change ANY of the promotions on
        // login because it might result in someone not converting due to the price increase. There cannot be any
        // user specific coupons applied to the anonymous sessions anyways.
        boolean wasAnonymousSession = isAnonymousShopper(invalidShopper);
        if (wasAnonymousSession || currentShopper.equals(invalidShopper)) {
            return;
        }

        final ShoppingCart shoppingCart = currentShopper.getCurrentShoppingCart();
        final Set<String> couponCodes = shoppingCart.getPromotionCodes();
        for (final String couponCode : couponCodes) {
            // Remove all coupons to ensure that we have cleared out any and all promotions that may be linked to the old CustomerSession.
            shoppingCart.removePromotionCode(couponCode);
            
            // If the coupon is a public coupon (ie. Applies to all CustomerSessions) then re-add it. The remove/add will then create a new
            // usage record linked to the correct/current user account.
            final Coupon coupon = couponService.findByCouponCode(couponCode);
            if (!CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(coupon.getCouponConfig().getUsageType())) {
                shoppingCart.applyPromotionCode(couponCode);
            }
        }
    }

	private boolean isAnonymousShopper(final Shopper invalidShopper) {
		if (invalidShopper.getCustomer() == null) {
			return true;
		}
		
		final Customer customer = invalidShopper.getCustomer();
		return customer.isAnonymous();
	}
    
}
