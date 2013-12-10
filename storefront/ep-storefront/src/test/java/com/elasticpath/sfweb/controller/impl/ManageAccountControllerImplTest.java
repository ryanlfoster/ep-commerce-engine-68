/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.impl.CouponUsageByCouponCodeComparatorImpl;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.impl.CouponImpl;
import com.elasticpath.domain.rules.impl.CouponUsageImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.sfweb.formbean.ManageAccountFormBean;
import com.elasticpath.sfweb.formbean.impl.ManageAccountFormBeanImpl;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Unit test the manage account controller.
 */
public class ManageAccountControllerImplTest {

	private static final String UNEXPECTED_EXCEPTION = "Unexpected exception: ";

	private ManageAccountControllerImpl controller;

	private MockHttpServletRequest mockRequest;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SettingsReader settingsReader;

	private SfRequestHelper requestHelper;

	private ShoppingCart shoppingCart;

	private CustomerService customerService;

	private OrderService orderService;

	private CouponUsageService couponUsageService;

	private BeanFactory beanFactory;

	private final Date currentDateTime = new Date();

	private CustomerSession customerSession;

	/**
	 *
	 * Basic ManageAccountController Test Double.
	 */
	class ManageAccountControllerTestDouble1 extends ManageAccountControllerImpl {

		/**
		 * Override getRequestHelper to return a mock object.
		 * @return a mock request helper
		 */
		@Override
		public SfRequestHelper getRequestHelper() {
			return requestHelper;
		}

		@Override
		Date getCurrentDateTime() {
			return currentDateTime;

		}
	}

	/**
	 *
	 * ManageAccountController Test Double that mocks out createModelAndView.
	 */
	class ManageAccountControllerTestDouble2 extends ManageAccountControllerTestDouble1 {

		@Override
		ModelAndView createModelAndView(final String view, final String commandName, final Object command) {
			return null;
		}
	}


	/**
	 * CouponUsage Test Double to allow dummy Coupon and focus on isApplied.
	 */
	class CouponUsageTestDouble extends CouponUsageImpl {
		private static final long serialVersionUID = 4008994751691376419L;

		/**
		 * Constructor.
		 * @param isApplied only parameter.
		 */
		public CouponUsageTestDouble(final boolean isApplied) {
			setActiveInCart(isApplied);
			setCoupon(new CouponTestDouble());
		}
	}

	/**
	 * Coupon Test Double to return hard-coded coupon code.
	 */
	class CouponTestDouble extends CouponImpl {
		private static final long serialVersionUID = 4386647836494699558L;

		/** get coupon code.
		 * @return code for coupon.
		 */
		@Override
		public String getCouponCode() {
			return "ABCD";
		}
	}

	/**
	 * ManageAccountFormBeanTestDouble allowing a customer coupons list only.
	 */
	class ManageAccountFormBeanTestDouble extends ManageAccountFormBeanImpl {

		private static final long serialVersionUID = 1L;

		private final List<CouponUsage> couponUsages;

		/**
		 * Constructor.
		 * @param couponUsages for testing
		 */
		public ManageAccountFormBeanTestDouble(final List<CouponUsage> couponUsages) {
			this.couponUsages = couponUsages;
		}

		/**
		 * Return the coupons.
		 * @return inserted list.
		 */
		@Override
		public List<CouponUsage> getCustomerCoupons() {
			return couponUsages;
		}
	}

	/**
	 * Setup objects required for all tests.
	 *
	 * @throws Exception in case of exception during setup
	 */
	public void setUpForHandleRequestTests() throws Exception {
		// Setup objects required for controller
		mockRequest = new MockHttpServletRequest();
		settingsReader = context.mock(SettingsReader.class);
		requestHelper = context.mock(SfRequestHelper.class);
		shoppingCart = context.mock(ShoppingCart.class);
		customerSession = context.mock(CustomerSession.class);
		customerService = context.mock(CustomerService.class);
		orderService = context.mock(OrderService.class);
		couponUsageService = context.mock(CouponUsageService.class);
		beanFactory = context.mock(BeanFactory.class);

		// Setup controller to test
		controller = new ManageAccountControllerTestDouble1();

		controller.setSettingsReader(settingsReader);
		controller.setCustomerService(customerService);
		controller.setOrderService(orderService);
		controller.setCouponUsageService(couponUsageService);
		controller.setBeanFactory(beanFactory);

		// Set common expectations
		context.checking(new Expectations() {
			{
				allowing(requestHelper).getCustomerSession(mockRequest);
				will(returnValue(customerSession));

				allowing(beanFactory).getBean(ContextIdNames.MANAGE_ACCOUNT_FORM_BEAN);
				will(returnValue(new ManageAccountFormBeanImpl()));

				allowing(beanFactory).getBean(ContextIdNames.COUPON_USAGE_CODE_COMPARATOR);
				will(returnValue(new CouponUsageByCouponCodeComparatorImpl()));
			}
		});
	}

	/**
	 * Setup objects required for all tests.
	 *
	 * @throws Exception in case of exception during setup
	 */
	public void setUpForAppliedStateChangeTests() throws Exception {
		// Setup objects required for controller
		mockRequest = new MockHttpServletRequest();
		requestHelper = context.mock(SfRequestHelper.class);
		shoppingCart = context.mock(ShoppingCart.class);
		customerSession = context.mock(CustomerSession.class);
		couponUsageService = context.mock(CouponUsageService.class);

		// Setup controller to test
		controller = new ManageAccountControllerTestDouble2();
		controller.setCouponUsageService(couponUsageService);

		// Set common expectations
		context.checking(new Expectations() {
			{
				allowing(requestHelper).getCustomerSession(mockRequest);
				will(returnValue(customerSession));

				allowing(customerSession).getShoppingCart();
				will(returnValue(shoppingCart));
			}
		});


	}

	/**
	 * Test that changing the state of a coupon's applied state is immediately visible in the Shopping Cart.
	 */
	@Test
	public void testTurningAppliedStateOnChangesCart() {

		try {
			setUpForAppliedStateChangeTests();
		} catch (final Exception e) {
			fail(UNEXPECTED_EXCEPTION + e.getMessage());
		}

		// Setup controller to test
		final CouponUsage couponUsage = new CouponUsageTestDouble(true);
		final List<CouponUsage> couponUsages = new ArrayList<CouponUsage>();
		couponUsages.add(couponUsage);
		final ManageAccountFormBean formBean = new ManageAccountFormBeanTestDouble(couponUsages);

		// Expectations
		context.checking(new Expectations() {
			{
				oneOf(couponUsageService).update(couponUsage);
				oneOf(shoppingCart).applyPromotionCode("ABCD");
			}

		});

		// Test the controller
		controller.onSubmit(mockRequest, null, formBean, null);
	}

	/**
	 * Test that changing the state of a coupon's applied state is immediately visible in the Shopping Cart.
	 */
	@Test
	public void testTurningAppliedStateOffChangesCart() {
		try {
			setUpForAppliedStateChangeTests();
		} catch (final Exception e) {
			fail(UNEXPECTED_EXCEPTION + e.getMessage());
		}

		// Setup controller to test
		final CouponUsage couponUsage = new CouponUsageTestDouble(false);
		final List<CouponUsage> couponUsages = new ArrayList<CouponUsage>();
		couponUsages.add(couponUsage);
		final ManageAccountFormBean formBean = new ManageAccountFormBeanTestDouble(couponUsages);

		// Expectations
		context.checking(new Expectations() {
			{
				oneOf(couponUsageService).update(couponUsage);
				oneOf(shoppingCart).removePromotionCode("ABCD");
			}

		});

		// Test the controller
		controller.onSubmit(mockRequest, null, formBean, null);
	}

	/**
	 * Test that the controller sets a variable on the model when credit card storage
	 * is disabled.
	 */
	@Test
	public void testHandleRequestWhenCCStorageDisabled() {
		try {
			setUpForHandleRequestTests();
		} catch (final Exception e) {
			fail(UNEXPECTED_EXCEPTION + e.getMessage());
		}
		// Mock objects required for this test
		final SettingValue settingValue = context.mock(SettingValue.class);
		final Customer customer = context.mock(Customer.class);
		final StoreConfig storeConfig = context.mock(StoreConfig.class);
		final Store store = context.mock(Store.class);
		final Shopper shopper = context.mock(Shopper.class);


		// Expectations
		context.checking(new Expectations() {
			{
				oneOf(settingValue).getValue();
				will(returnValue("false"));

				oneOf(settingsReader).getSettingValue("COMMERCE/SYSTEM/storeCustomerCreditCards");
				will(returnValue(settingValue));

				oneOf(customerSession).getShopper();
				will(returnValue(shopper));

				oneOf(shopper).getCustomer();
				will(returnValue(customer));

				oneOf(customerSession).getLocale();
				will(returnValue(new Locale("en")));


				oneOf(customer).getUidPk();
				will(returnValue(1L));

				oneOf(customerService).get(1L);
				will(returnValue(customer));

				oneOf(customerService).verifyCustomer(customer);
				oneOf(customerSession).getShopper();
				will(returnValue(shopper));

				oneOf(shopper).setCustomer(with(customer));

				allowing(requestHelper).getStoreConfig();
				will(returnValue(storeConfig));

				oneOf(storeConfig).getStoreCode();
				will(returnValue("test-store"));

				oneOf(storeConfig).getStore();
				will(returnValue(store));

				oneOf(store).getUidPk();
				will(returnValue(1L));

				oneOf(customer).getGuid();
				will(returnValue("guid"));

				oneOf(orderService).findOrdersByCustomerGuidAndStoreCode("guid", "test-store", false);
				will(returnValue(null));

				oneOf(customer).getEmail();
				will(returnValue("email@domain.com"));

				oneOf(couponUsageService).findAllUsagesByEmailAddress("email@domain.com", currentDateTime, 1L);
				will(returnValue(Collections.emptyList()));

				oneOf(customerSession).getShopper();
				will(returnValue(shopper));
				
				oneOf(shopper).getCurrentShoppingCart();
				will(returnValue(new ShoppingCartImpl()));
			}
		});

		// Test the controller
		final ManageAccountFormBean mac = (ManageAccountFormBean) controller.formBackingObject(mockRequest);
		assertEquals("Model should contain the appropriate variable set to false when cc storage is disabled",
				"false", mac.getStoreCustomerCC());
	}
}
