package com.elasticpath.service.rules.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerProfileImpl;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.impl.CouponConfigImpl;
import com.elasticpath.domain.rules.impl.CouponImpl;
import com.elasticpath.domain.rules.impl.CouponUsageImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.factory.TestCustomerSessionFactory;

/**
 * Tests {@code PromotionRuleDelegateImpl} without extending elastic path test case.
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals" })
public class PromotionRuleDelegateImplNewTest {

	private static final int THREE = 3;
	private static final int FIVE = 5;
	private static final String RULE_CODE = "promo5";
	private static final int DISCOUNT_QUANTITY_PER_COUPON = 7;
	private static final int RULE_ID = FIVE;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactoryExpectationsFactory expectationsFactory;
	private BeanFactory beanFactory;


	/**
	 * Setup objects and mocks required by tests.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {

		beanFactory = context.mock(BeanFactory.class);
        expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_PROFILE, CustomerProfileImpl.class);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests that when calculateAvailableDiscountQuantity is called but there
	 * is no coupon config that the NUM_ITEMS parameter is returned.
	 */
	@Test
	public void testCalculateAvailableDiscountQuantityNoCouponConfig() {
		final PromotionRuleDelegateImpl delegate = new PromotionRuleDelegateImpl();

		final RuleService ruleService = context.mock(RuleService.class);
		delegate.setRuleService(ruleService);

		final CouponConfigService couponConfigService = context.mock(CouponConfigService.class);
		delegate.setCouponConfigService(couponConfigService);

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);

		context.checking(new Expectations() { {
			oneOf(ruleService).findRuleCodeById(RULE_ID); will(returnValue(RULE_CODE));
			oneOf(couponConfigService).findByRuleCode(RULE_CODE); will(returnValue(null));
			allowing(shoppingCart).getShopper(); will(returnValue(createShopper()));
		} });

		final int actualResult = delegate.calculateAvailableDiscountQuantity(shoppingCart, RULE_ID, DISCOUNT_QUANTITY_PER_COUPON);
		assertEquals("The discountQuantityPerCoupon should be returned", DISCOUNT_QUANTITY_PER_COUPON, actualResult);
	}

	private Shopper createShopper() {
		final CustomerSession session = TestCustomerSessionFactory.getInstance().createNewCustomerSession();
		session.getShopper().setCustomer(new CustomerImpl());
		return session.getShopper();
	}

	/**
	 * Tests that when calculateAvailableDiscountQuantity is called but there
	 * is no coupon usage that the NUM_ITEMS parameter is returned.
	 */
	@Test
	public void testCalculateAvailableDiscountQuantityNoCouponUsage() {
		final PromotionRuleDelegateImpl delegate = new PromotionRuleDelegateImpl();

		final RuleService ruleService = context.mock(RuleService.class);
		delegate.setRuleService(ruleService);

		final CouponConfigService couponConfigService = context.mock(CouponConfigService.class);
		delegate.setCouponConfigService(couponConfigService);

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Shopper shopper = createShopper();
		final CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_ANY_USER);

		final CouponUsageService couponUsageService = context.mock(CouponUsageService.class);
		delegate.setCouponUsageService(couponUsageService);

		context.checking(new Expectations() { {
			oneOf(ruleService).findRuleCodeById(RULE_ID); will(returnValue(RULE_CODE));
			oneOf(couponConfigService).findByRuleCode(RULE_CODE); will(returnValue(couponConfig));
			allowing(shoppingCart).getShopper(); will(returnValue(shopper));
		} });

		final int actualResult = delegate.calculateAvailableDiscountQuantity(shoppingCart, RULE_ID, DISCOUNT_QUANTITY_PER_COUPON);
		assertEquals("The discountQuantityPerCoupon should be returned", DISCOUNT_QUANTITY_PER_COUPON, actualResult);
	}

	/**
	 * Tests that when calculateAvailableDiscountQuantity is called with
	 * a usage type of limit per coupon then the parameter is returned.
	 */
	@Test
	public void testCalculateAvailableDiscountQuantityLimitPerCoupon() {
		final PromotionRuleDelegateImpl delegate = new PromotionRuleDelegateImpl();

		final RuleService ruleService = context.mock(RuleService.class);
		delegate.setRuleService(ruleService);

		final CouponConfigService couponConfigService = context.mock(CouponConfigService.class);
		delegate.setCouponConfigService(couponConfigService);

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Shopper shopper = createShopper();

		final CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);

		final CouponUsageService couponUsageService = context.mock(CouponUsageService.class);
		delegate.setCouponUsageService(couponUsageService);


		context.checking(new Expectations() { {
			oneOf(ruleService).findRuleCodeById(RULE_ID); will(returnValue(RULE_CODE));
			oneOf(couponConfigService).findByRuleCode(RULE_CODE); will(returnValue(couponConfig));
			allowing(shoppingCart).getShopper(); will(returnValue(shopper));
		} });

		final int actualResult = delegate.calculateAvailableDiscountQuantity(shoppingCart, RULE_ID, DISCOUNT_QUANTITY_PER_COUPON);
		assertEquals("The discountQuantityPerCoupon should be returned", DISCOUNT_QUANTITY_PER_COUPON, actualResult);
	}

	/**
	 * Tests that when calculateAvailableDiscountQuantity is called with
	 * multi use per order set then the remaining coupon uses
	 * are returned.
	 */
	@Test
	public void testCalculateAvailableDiscountQuantityMultiUsePerOrder() {
		final PromotionRuleDelegateImpl delegate = new PromotionRuleDelegateImpl();

		final RuleService ruleService = context.mock(RuleService.class);
		delegate.setRuleService(ruleService);

		final CouponConfigService couponConfigService = context.mock(CouponConfigService.class);
		delegate.setCouponConfigService(couponConfigService);

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Shopper shopper = createShopper();

		final CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_ANY_USER);
		couponConfig.setUsageLimit(FIVE);
		couponConfig.setMultiUsePerOrder(true);

		final CouponUsageService couponUsageService = context.mock(CouponUsageService.class);
		delegate.setCouponUsageService(couponUsageService);

		final Set<String> promotionCodes = new HashSet<String>();
		promotionCodes.add("ABC");

		final List<CouponUsage> couponUsageList = new ArrayList<CouponUsage>();
		final Coupon coupon = new CouponImpl();
		coupon.setCouponCode("ABC");
		coupon.setCouponConfig(couponConfig);
		final CouponUsage couponUsage = new CouponUsageImpl();
		couponUsage.setCoupon(coupon);
		couponUsage.setUseCount(2);
		couponUsageList.add(couponUsage);
		final Customer customer = context.mock(Customer.class);
		shopper.setCustomer(customer);

		context.checking(new Expectations() { {
			oneOf(ruleService).findRuleCodeById(RULE_ID); will(returnValue(RULE_CODE));
			oneOf(couponConfigService).findByRuleCode(RULE_CODE); will(returnValue(couponConfig));
			allowing(shoppingCart).getShopper(); will(returnValue(shopper));
			allowing(shoppingCart).getPromotionCodes(); will(returnValue(promotionCodes));
			oneOf(couponUsageService).findByRuleCodeAndEmail(RULE_CODE, ""); will(returnValue(couponUsageList));
			allowing(customer).getEmail(); will(returnValue(""));
		} });

		final int actualResult = delegate.calculateAvailableDiscountQuantity(shoppingCart, RULE_ID, 2);
		assertEquals("2 * (Coupon Config max - Coupon usage)", 2 * THREE, actualResult);
	}

	/**
	 * Tests that when calculateAvailableDiscountQuantity is called with
	 * multi use per order set then the remaining coupon uses
	 * are returned.
	 */
	@Test
	public void testCalculateAvailableDiscountQuantitySingleUsePerOrder() {
		final PromotionRuleDelegateImpl delegate = new PromotionRuleDelegateImpl();

		final RuleService ruleService = context.mock(RuleService.class);
		delegate.setRuleService(ruleService);

		final CouponConfigService couponConfigService = context.mock(CouponConfigService.class);
		delegate.setCouponConfigService(couponConfigService);

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Shopper shopper = createShopper();

		final CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_ANY_USER);
		couponConfig.setUsageLimit(FIVE);
		couponConfig.setMultiUsePerOrder(false);

		final CouponUsageService couponUsageService = context.mock(CouponUsageService.class);
		delegate.setCouponUsageService(couponUsageService);

		final Set<String> promotionCodes = new HashSet<String>();
		promotionCodes.add("ABC");

		final List<CouponUsage> couponUsageList = new ArrayList<CouponUsage>();
		final Coupon coupon = new CouponImpl();
		coupon.setCouponCode("ABC");
		coupon.setCouponConfig(couponConfig);
		final CouponUsage couponUsage = new CouponUsageImpl();
		couponUsage.setCoupon(coupon);
		couponUsage.setUseCount(2);
		couponUsageList.add(couponUsage);

		context.checking(new Expectations() { {
			oneOf(ruleService).findRuleCodeById(RULE_ID); will(returnValue(RULE_CODE));
			oneOf(couponConfigService).findByRuleCode(RULE_CODE); will(returnValue(couponConfig));
			allowing(shoppingCart).getShopper(); will(returnValue(shopper));
		} });

		final int actualResult = delegate.calculateAvailableDiscountQuantity(shoppingCart, RULE_ID, 2);
		assertEquals("1 Coupon usage * parameter", 2, actualResult);
	}
}
