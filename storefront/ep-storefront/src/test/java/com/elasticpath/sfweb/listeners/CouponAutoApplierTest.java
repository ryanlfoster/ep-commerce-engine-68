package com.elasticpath.sfweb.listeners;

import static org.junit.Assert.assertEquals;


import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShopperFactoryForTestApplication;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.impl.CouponConfigImpl;
import com.elasticpath.domain.rules.impl.CouponImpl;
import com.elasticpath.domain.rules.impl.CouponUsageImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;

/**
 * Coupon auto applier test.
 */
public class CouponAutoApplierTest {
	/**
	 * Tests apply coupon to cart.
	 */
	@Test
	public void testApplyCouponToCart() {
		final CouponAutoApplier applier = new CouponAutoApplier();
		final CustomerSession customerSession = createCustomerSession();
		final Collection<CouponUsage> usages = Arrays.asList(createCouponUsage("code1"), createCouponUsage("code2"));

		applier.autoApplyCouponCodes(customerSession.getShopper().getCurrentShoppingCart(), usages);

		assertEquals("Expects that 2 coupons were applied to the cart", 2, 
				customerSession.getShopper().getCurrentShoppingCart().getPromotionCodes().size());
	}

	/**
	 * Tests remove user specific coupons.
	 */
	@Test
	public void testRemoveUserSpecificCoupons() {
		final CustomerSession customerSession = createCustomerSession();
		final ShoppingCart cart = customerSession.getShopper().getCurrentShoppingCart();
		cart.applyPromotionCode("Coupon");

		assertEquals("Expects 1 coupon in the cart", 1, cart.getPromotionCodes().size());

		final CouponAutoApplier applier = new CouponAutoApplier() {
			@Override
			protected boolean isUserSpecificCoupon(final String couponCode) {
				// mocking that all coupons are use specific.
				return true;
			}
		};

		applier.removeUserSpecificCoupons(cart);

		assertEquals("Expects 0 coupon in the cart since the any user specific coupon should be removed", 0, cart.getPromotionCodes().size());
	}

	/**
	 * Tests whether a coupon is user specific.
	 */
	@Test
	public void testIsUserSpecificCoupon() {
		CouponAutoApplier applier = new CouponAutoApplier() {
			@Override
			protected Coupon getCouponByCode(final String couponCode) {
				return createCoupon("user_specific_coupon",	CouponUsageType.LIMIT_PER_SPECIFIED_USER);
			}
		};

		assertEquals("Expects true since there is at least one user specific coupon", true, applier.isUserSpecificCoupon("user_specific_coupon"));
	}

	private Coupon createCoupon(final String couponCode, final CouponUsageType type) {
		CouponConfig config = new CouponConfigImpl();
		config.setUsageType(type);

		Coupon coupon = new CouponImpl();
		coupon.setCouponConfig(config);
		coupon.setCouponCode(couponCode);

		return coupon;
	}

	
	private CustomerSession createCustomerSession() {
		final ShoppingCart cart = new ShoppingCartImpl() {

			private static final long serialVersionUID = -4855082756391799003L;

			{
				setShoppingItemHasRecurringPricePredicate(new ShoppingItemHasRecurringPricePredicate());
			}

			private Set<String> codes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			
			@Override
			public void fireRules() {
				// do nothing
			}

			@Override
			public boolean isValidPromoCode(final String code) {
				return true;
			}
			
			@Override
			public boolean applyPromotionCode(final String promotionCode) {
				codes.add(promotionCode);
				return true;
			}
			
			public void removePromotionCode(final String promotionCode) {
				codes.remove(promotionCode);
			}
			
			@Override
			public Set<String> getPromotionCodes() {
				return codes;
			}
		};

		final Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		final CustomerSession customerSession = 
			TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(shopper);
		customerSession.setShopper(shopper);
		shopper.setCurrentShoppingCart(cart);
		
		return customerSession;
	}

	private CouponUsage createCouponUsage(final String couponCode) {
		Coupon coupon = new CouponImpl();
		coupon.setCouponCode(couponCode);
		CouponUsage usage = new CouponUsageImpl();
		usage.setCoupon(coupon);
		usage.setActiveInCart(true);
		return usage;
	}
}
