package com.elasticpath.sfweb.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.shoppingcart.impl.AbstractCheckoutEventHandlerImpl;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.CustomerLoginEventListener;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;

/**
 * Automatically applies coupon to shopping cart whenever a new customer session is created.
 */
public class CouponAutoApplier extends AbstractCheckoutEventHandlerImpl implements NewHttpSessionEventListener, CustomerLoginEventListener {
	
	private CouponUsageService couponUsageService;
	
	private CouponService couponService;

	@Override
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
		final Shopper shopper = session.getShopper();
		reapplyCoupons(shopper.getCurrentShoppingCart());
	}

	/**
	 * Re apply all valid coupons for cart.
	 * @param shoppingCart customer session
	 */
	protected void reapplyCoupons(final ShoppingCart shoppingCart) {
		removeUserSpecificCoupons(shoppingCart);		
		filterInvalidCoupons(shoppingCart);
		applyUserSpecificCoupons(shoppingCart);
	}


	@Override
	public void postCheckout(final ShoppingCart shoppingCart, final OrderPayment orderPayment, final Order completedOrder) {
		reapplyCoupons(shoppingCart);
	}

	/**
	 * @param shoppingCart {@link ShoppingCart}
	 */
	protected void filterInvalidCoupons(final ShoppingCart shoppingCart) {
		final List <String> couponsToRemove = new ArrayList <String>();
		for (String coupon : shoppingCart.getPromotionCodes()) {
			if (!shoppingCart.isValidPromoCode(coupon)) {
				couponsToRemove.add(coupon);
			}
		}
		for (String coupon : couponsToRemove) {
			shoppingCart.removePromotionCode(coupon);
		}
	}


	/**
	 * Removes all user specific coupons.
	 * 
	 * @param shoppingCart {@link ShoppingCart}
	 */
	protected void removeUserSpecificCoupons(final ShoppingCart shoppingCart) {
		final List<String> toRemove = new ArrayList<String>();
		for (String couponCode : shoppingCart.getPromotionCodes()) {
			if (isUserSpecificCoupon(couponCode)) {
				toRemove.add(couponCode);
			}
		}

		for (String toRemoveCouponCode : toRemove) {
			shoppingCart.removePromotionCode(toRemoveCouponCode);
		}
	}

	/**
	 * Checks that whether a coupon code is the code of a user specific coupon.
	 * 
	 * @param couponCode the coupon code to check
	 * @return true if it is.
	 */
	protected boolean isUserSpecificCoupon(final String couponCode) {
		Coupon coupon = getCouponByCode(couponCode);

		if (CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(coupon.getCouponConfig().getUsageType())) {
			return true;
		}

		return false;
	}


	/**
	 * Gets coupon by a coupon code.
	 * 
	 * @param couponCode the coupon code.
	 * @return the found coupons.
	 */
	protected Coupon getCouponByCode(final String couponCode) {
		return couponService.findByCouponCode(couponCode);
	}

	/**
	 * Applies coupon to cart if the customer is eligible.
	 * 
	 * @param shoppingCart {@link ShoppingCart}
	 */
	protected void applyUserSpecificCoupons(final ShoppingCart shoppingCart) {
		final String customerEmailAddress = shoppingCart.getShopper().getCustomer().getEmail();
		final Collection<CouponUsage> eligibleUsages = 
			couponUsageService.findEligibleUsagesByEmailAddress(customerEmailAddress, shoppingCart.getStore().getUidPk());
		autoApplyCouponCodes(shoppingCart, eligibleUsages);
	}

	/**
	 * Auto applies active in cart coupon codes to shopping cart.
	 * 
	 * @param shoppingCart the shopping cart.
	 * @param usages the coupon usages which contains the coupon code.
	 */
	protected void autoApplyCouponCodes(final ShoppingCart shoppingCart, final Collection<CouponUsage> usages) {
		for (CouponUsage usage : usages) {
			final String couponCode = usage.getCoupon().getCouponCode();
			if (usage.isActiveInCart() && shoppingCart.isValidPromoCode(couponCode)) {
				shoppingCart.applyPromotionCode(couponCode);
			}
		}
	}

	/**
	 * Setter for {@link CouponUsageService}.
	 * 
	 * @param couponUsageService {@link CouponUsageService}.
	 */
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}
	
	/**
	 * Setters for {@link CouponService}.
	 * 
	 * @param couponService {@link CouponService}.
	 */
	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}
}

