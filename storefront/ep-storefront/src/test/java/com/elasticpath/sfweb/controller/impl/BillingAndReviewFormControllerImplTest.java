/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.misc.PayerAuthenticationEnrollmentResult;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.payment.PayPalExpressSession;
import com.elasticpath.domain.payment.PayerAuthenticationSession;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.impl.CachedSettingsReaderImpl;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.sfweb.formbean.BillingAndReviewFormBean;
import com.elasticpath.sfweb.formbean.OrderPaymentFormBean;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.impl.HttpServletFacadeFactoryImpl;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.sfweb.util.impl.IpAddressResolverImpl;
import com.elasticpath.sfweb.util.impl.RequestHelperImpl;

/**
 * Test the BillingAndReviewFormController for desired functionality.
 */
@SuppressWarnings("PMD.ExcessiveMethodLength")
public class BillingAndReviewFormControllerImplTest {

	private static final int ERROR_CODE = 12;

	private static final int TOTAL = 9;
	private static final String SAVE_CUSTOMER_CREDIT_CARDS_KEY = "saveCustomerCreditCards";
	public static final String MOCK_PREFIX_CUSTOMER_SESSION = "CustomerSession ";
	public static final String MOCK_PREFIX_SHOPPING_CART = "ShoppingCart ";

	private BillingAndReviewFormControllerImpl billingController;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	private HttpServletFacadeFactory httpServletFacadeFactory;

	private SettingsReader reader;

	private SfRequestHelper requestHelper;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private OrderPayment mockOrderPayment;

	@Before
	public void setUp() throws Exception {
		// Create a mock HTTP request and response for the controller calls
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		mockOrderPayment = context.mock(OrderPayment.class);

		httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(new RequestHelperImpl(), new IpAddressResolverImpl(), null);

		// Create a billingController that overrides formBackingObject as it is
		// not one of the methods under test
		billingController = new BillingAndReviewFormControllerImpl() {
			@Override
			public Object formBackingObject(final HttpServletRequest request) {
				return 0;
			}

			@SuppressWarnings("unchecked")
			@Override
			public <T> T getBean(final String name) {
				if (name.equals(ContextIdNames.ORDER_PAYMENT)) {
					return (T) mockOrderPayment;
				} else if ("httpServletFacadeFactory".equals(name)) {
					return (T) httpServletFacadeFactory;
				}
				return null;
			}
		};
		// sample disallowed fields
		billingController.setDisallowedFormFields("uidPk, code");
	}

	/**
	 * Calls HandleRequestInternal method and checks that execution runs as expected, if the case does not have any errors the model map should
	 * contain the "saveCustomerCreditCards" setting that will be referenced by the velocity templates.
	 *
	 * @throws Exception exception could be thrown when calling handleRequestInternal
	 */
	@Test
	public void testHandleRequestInternal() throws Exception {

		// Handles the setup for the test case for the methods testing handleRequestInternal
		handleRequestInternalSetup();

		// Override a HTTP session that has the getAttribute method overridden and will return null
		// if getAttribute is called or do nothing if removeAttribute is called, these methods
		// do not need to be tested
		HttpSession session = new MockHttpSession() {
			@Override
			public Object getAttribute(final String name) {
				return null;
			}

			@Override
			public void removeAttribute(final String name) {
				// do nothing
			}
		};

		// Override the selected methods in request helper to return mocks as appropriate
		requestHelper = new RequestHelperImpl() {

			private CustomerSession mockCustomerSession;

			private void setupMockCustomerSession() {
				if (mockCustomerSession == null) {
					// Creates the mock objects
				    mockCustomerSession = context.mock(CustomerSession.class);

					final ShoppingCart mockShoppingCart = context.mock(ShoppingCart.class);
					final Store mockStore = context.mock(Store.class);
					final Warehouse mockWarehouse = context.mock(Warehouse.class);

					context.checking(new Expectations() {
						{
							atLeast(1).of(mockCustomerSession).getShoppingCart();
							will(returnValue(mockShoppingCart));

							// Sets expectations on methods and returns the appropriate objects
							atLeast(1).of(mockShoppingCart).requiresShipping();
							will(returnValue(false));
							atLeast(1).of(mockShoppingCart).getNumItems();
							will(returnValue(2));
							atLeast(1).of(mockShoppingCart).getCartItems();
							will(returnValue(new LinkedList<ShoppingItem>()));
							atLeast(1).of(mockShoppingCart).getStore();
							will(returnValue(mockStore));
							atLeast(1).of(mockShoppingCart).getSelectedShippingServiceLevel();
							will(returnValue(null));
							atLeast(1).of(mockShoppingCart).getTotal();
							will(returnValue(BigDecimal.ZERO));
							atLeast(1).of(mockShoppingCart).hasRecurringPricedShoppingItems();
							will(returnValue(false));
							// NOPMD


							atLeast(1).of(mockStore).getWarehouse();
							will(returnValue(mockWarehouse));
						}
					});
					// Returns a mock shopping cart
				}
			}

			@Override
			public CustomerSession getCustomerSession(final HttpServletRequest request) {
				setupMockCustomerSession();
				return mockCustomerSession;
			}
		};

		// Set the mockSession and then set the controller with a mock request helper, finally call
		// handleRequestInternal method and check to see if the modelMap contains the "saveCustomerCreditCards"
		// setting information
		request.setSession(session);
		billingController.setRequestHelper(requestHelper);
		billingController.setSettingsReader(reader);
		ModelAndView modelAndView = billingController.handleRequestInternal(request, response);

		// "saveCustomerCreditCards" should be consistent with what is returned by the settingsReader,
		// the initial call to getSettingValue will return false
		assertEquals(modelAndView.getModelMap().get(SAVE_CUSTOMER_CREDIT_CARDS_KEY), false);

		modelAndView = billingController.handleRequestInternal(request, response);
		// "saveCustomerCreditCards" should be consistent with what is returned by the settingsReader,
		// consecutive calls to getSettingsValue will return true
		assertEquals(modelAndView.getModelMap().get(SAVE_CUSTOMER_CREDIT_CARDS_KEY), true);
	}

	/**
	 * Calls HandleRequestInternal method and checks that execution runs as expected, if the case will use a PayerAuthenticationSession and does not
	 * have any errors then the model map should contain the "saveCustomerCreditCards" setting that will be referenced by the velocity templates.
	 *
	 * @throws Exception exception could be thrown when calling handleRequestInternal
	 */
	@Test
	public void testHandleRequestPayerAuthenticationSession() throws Exception {

		// Handles the setup for the test case for the methods testing handleRequestInternal
		handleRequestInternalSetup();

		// Override a HTTP session that has the getAttribute method overridden and will return the appropriate
		// mocked objects when called, these mocked objects also have expectations on what methods will be called
		// for the objects they are mocking
		HttpSession session = new MockHttpSession() {
			@Override
			public Object getAttribute(final String name) {
				if (name.equals(WebConstants.PAYER_AUTHENTICATION_SESSION)) {
					final PayerAuthenticationSession mockPayerAuthenticationSession =
							context.mock(PayerAuthenticationSession.class, "PayerAuthenticationSession " + System.nanoTime());
					context.checking(new Expectations() {
						{
							allowing(mockPayerAuthenticationSession).getStatus();
							will(returnValue(ERROR_CODE));
						}
					});
					return mockPayerAuthenticationSession;
				}
				return null;
			}

			@Override
			public void removeAttribute(final String name) {
				// do nothing
			}
		};

		// Override the selected methods in request helper to return mocks as appropriate
		requestHelper = new RequestHelperImpl() {

			@Override
			public CustomerSession getCustomerSession(final HttpServletRequest request) {

				// Creates the mock objects
			    final CustomerSession mockCustomerSession = context.mock(CustomerSession.class, MOCK_PREFIX_CUSTOMER_SESSION + System.nanoTime());
				final ShoppingCart mockShoppingCart = context.mock(ShoppingCart.class, MOCK_PREFIX_SHOPPING_CART + System.nanoTime());
				final Store mockStore = context.mock(Store.class, "Store " + System.nanoTime());
				final Warehouse mockWarehouse = context.mock(Warehouse.class, "Warehouse " + System.nanoTime());

				// Sets expectations on methods and returns the appropriate objects
				context.checking(new Expectations() {
					{
						allowing(mockCustomerSession).getShoppingCart();
						will(returnValue(mockShoppingCart));
						allowing(mockShoppingCart).requiresShipping();
						will(returnValue(false));
						allowing(mockShoppingCart).getNumItems();
						will(returnValue(2));
						allowing(mockShoppingCart).getCartItems();
						will(returnValue(new LinkedList<ShoppingItem>()));
						allowing(mockShoppingCart).getStore();
						will(returnValue(mockStore));
						allowing(mockShoppingCart).getSelectedShippingServiceLevel();
						will(returnValue(null));
						allowing(mockShoppingCart).getTotal();
						will(returnValue(BigDecimal.ZERO));
						allowing(mockShoppingCart).hasRecurringPricedShoppingItems();
						will(returnValue(false));

						allowing(mockStore).getWarehouse();
						will(returnValue(mockWarehouse));
					}
				});
				// Returns a mock shopping cart
				return mockCustomerSession;
			}
		};

		// Set the mockSession and then set the controller with a mock request helper, finally call
		// handleRequestInternal method and check to see if the modelMap contains the "saveCustomerCreditCards"
		// setting information
		request.setSession(session);
		billingController.setRequestHelper(requestHelper);
		billingController.setSettingsReader(reader);
		ModelAndView modelAndView = billingController.handleRequestInternal(request, response);

		// "saveCustomerCreditCards" should be consistent with what is returned by the settingsReader,
		// the initial call to getSettingValue will return false
		assertEquals(modelAndView.getModelMap().get(SAVE_CUSTOMER_CREDIT_CARDS_KEY), false);

		modelAndView = billingController.handleRequestInternal(request, response);
		// "saveCustomerCreditCards" should be consistent with what is returned by the settingsReader,
		// consecutive calls to getSettingsValue will return true
		assertEquals(modelAndView.getModelMap().get(SAVE_CUSTOMER_CREDIT_CARDS_KEY), true);
	}

	/**
	 * Setup the requestHelper with overridden methods that will return mock objects and set expectations on method calls so that testing may be
	 * completed easier, abstracting away much of the unnecessary details of these objects.
	 */
	private void handleRequestInternalSetup() {

		// Create a mock Setting value that will be returned when getting a setting
		// reader value, will return false then true on consecutive calls
		final SettingValue mockSettingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockSettingValue).getBooleanValue();
				will(onConsecutiveCalls(returnValue(false), returnValue(true)));
			}
		});

		reader = new CachedSettingsReaderImpl() {
			@Override
			public SettingValue getSettingValue(final String path) {
				return mockSettingValue;
			}
		};

		// Override the selected methods in request helper to return mocks as appropriate
		// when the overridden methods are called
		requestHelper = new RequestHelperImpl() {

			@Override
			public CustomerSession getCustomerSession(final HttpServletRequest request) {
			    final CustomerSession mockCustomerSession = context.mock(CustomerSession.class, MOCK_PREFIX_CUSTOMER_SESSION + System.nanoTime());
				final ShoppingCart mockShoppingCart = context.mock(ShoppingCart.class, MOCK_PREFIX_SHOPPING_CART + System.nanoTime());
				context.checking(new Expectations() {
					{
						atLeast(1).of(mockCustomerSession).getShoppingCart();
						will(returnValue(mockShoppingCart));
						oneOf(mockShoppingCart).requiresShipping();
						will(returnValue(false));
						oneOf(mockShoppingCart).getNumItems();
						will(returnValue(2));
						oneOf(mockShoppingCart).getAllItems();
						will(returnValue(new LinkedList<ShoppingItem>()));
					}
				});
				return mockCustomerSession;
			}
		};
	}

	/**
	 * Ensures that a new credit card is not added to the customer if the saveCustomerCreditCards setting is set to false, but the velocity template
	 * somehow set the saveForFutureUse checkbox is enabled.
	 */
	@Test
	public void testHandlePaymentWithNewCreditCardDisabled() {

		// Set up the request helper with appropriate overridden methods
		handlePaymentNewCreditCardDisabledSetup();

		// Override a HTTP session that has the getAttribute method overridden and will return the appropriate
		// mocked objects when called, these mocked objects also have expectations on what methods will be called
		// for the objects they are mocking
		HttpSession session = new MockHttpSession() {
			@Override
			public Object getAttribute(final String name) {

				// Assume that the attribute that being obtained is a PaypalExpressSession
				final PayPalExpressSession mockPayPalExpressSession = context.mock(PayPalExpressSession.class);
				context.checking(new Expectations() {
					{
						allowing(mockPayPalExpressSession).getStatus();
						will(returnValue(ERROR_CODE));
						allowing(mockPayPalExpressSession).clearSessionInformation();
					}
				});
				return mockPayPalExpressSession;
			}
		};

		final BillingAndReviewFormBean mockBillingReviewBean = context.mock(BillingAndReviewFormBean.class);

		final OrderPaymentFormBean mockOrderPaymentFormBean = context.mock(OrderPaymentFormBean.class);
		final CheckoutService mockCheckoutService = context.mock(CheckoutService.class);
		final CheckoutResults mockCheckoutResults = context.mock(CheckoutResults.class);

		context.checking(new Expectations() {
			{
				atLeast(1).of(mockBillingReviewBean).getSelectedPaymentOption();
				will(returnValue(BillingAndReviewFormBean.PAYMENT_OPTION_NEW_CREDIT_CARD));
				atLeast(1).of(mockBillingReviewBean).getOrderPaymentFormBean();
				will(returnValue(mockOrderPaymentFormBean));
				oneOf(mockOrderPayment).setPaymentMethod(with(any(PaymentType.class)));
				oneOf(mockBillingReviewBean).isSaveCreditCardForFutureUse();
				will(returnValue(true));
				oneOf(mockCheckoutService).checkout(with(any(ShoppingCart.class)), with(any(OrderPayment.class)));
				will(returnValue(mockCheckoutResults));
			}
		});

		setMockOrderPaymentExpectations(mockOrderPaymentFormBean);

		// Do the test by calling the onSubmit method from the BillingAndReviewFormController
		// also need to set mock objects for the RequestHelper and the controller itself to
		// test specific aspects of the controller.
		request.setSession(session);
		billingController.setSettingsReader(reader);
		billingController.setCheckoutService(mockCheckoutService);
		billingController.setRequestHelper(requestHelper);
		billingController.onSubmit(request, response, mockBillingReviewBean, null);

		// The test should not invoke customer.addCreditCard()
	}

	private void setMockOrderPaymentExpectations(final OrderPaymentFormBean mockOrderPaymentFormBean) {
		context.checking(new Expectations() {
			{
				oneOf(mockOrderPaymentFormBean).getCvv2Code();
				will(returnValue("123"));
				oneOf(mockOrderPaymentFormBean).getCardHolderName();
				will(returnValue("john smith"));
				oneOf(mockOrderPaymentFormBean).getCardType();
				will(returnValue("visa"));
				oneOf(mockOrderPaymentFormBean).getExpiryMonth();
				will(returnValue("05"));
				oneOf(mockOrderPaymentFormBean).getExpiryYear();
				will(returnValue("2015"));
				oneOf(mockOrderPaymentFormBean).getUnencryptedCardNumber();
				will(returnValue("4222"));

				oneOf(mockOrderPayment).setCvv2Code("123");
				oneOf(mockOrderPayment).setCardHolderName("john smith");
				oneOf(mockOrderPayment).setCardType("visa");
				oneOf(mockOrderPayment).setExpiryMonth("05");
				oneOf(mockOrderPayment).setExpiryYear("2015");
				oneOf(mockOrderPayment).setUnencryptedCardNumber("4222");
			}
		});
	}

	private void handlePaymentNewCreditCardDisabledSetup() {

		reader = new CachedSettingsReaderImpl() {
			@Override
			public SettingValue getSettingValue(final String path) {
				final SettingValue mockSettingValue = context.mock(SettingValue.class);
				context.checking(new Expectations() {
					{
						oneOf(mockSettingValue).getBooleanValue();
						will(returnValue(false));
					}
				});
				return mockSettingValue;
			}
		};

		// Override the selected methods in request helper to return mocks as appropriate
		requestHelper = new RequestHelperImpl() {

			// Create's a mock shopping cart, gift certificate, and store and
			// sets expectations on what methods will be called.
			private ShoppingCart createMockShoppingCart() {

				// Creates the mock objects
				final ShoppingCart mockShoppingCart = context.mock(ShoppingCart.class, MOCK_PREFIX_SHOPPING_CART + System.nanoTime());
				final GiftCertificate mockGC = context.mock(GiftCertificate.class, MOCK_PREFIX_SHOPPING_CART + System.nanoTime());

				// Creates a set of gift certificates and adds the mock gift certificate to the set, this
				// set will be returned when checking the appliedGiftCertificates to the shopping cart
				final Set<GiftCertificate> giftCerts = new HashSet<GiftCertificate>();
				giftCerts.add(mockGC);

				// Sets expectations on methods and returns the appropriate objects
				context.checking(new Expectations() {
					{
						allowing(mockShoppingCart).getTotal();
						will(returnValue(new BigDecimal(TOTAL)));
						allowing(mockShoppingCart).getAppliedGiftCertificates();
						will(returnValue(giftCerts));
					}
				});

				// Returns a mock shopping cart
				return mockShoppingCart;
			}

			// Creates a mock customer session, gift certificate, customer session and store and
			// sets expectations on what methods will be called.
			@Override
			public CustomerSession getCustomerSession(final HttpServletRequest request) {
				final CustomerSession mockCustomerSession = context.mock(CustomerSession.class, MOCK_PREFIX_CUSTOMER_SESSION + System.nanoTime());
				final Customer mockCustomer = context.mock(Customer.class, "Customer " + System.nanoTime());
				final Shopper mockShopper = context.mock(Shopper.class, "Shopper " + System.nanoTime());

				final ShoppingCart mockShoppingCart = createMockShoppingCart();

				context.checking(new Expectations() {
					{
						allowing(mockCustomerSession).getShoppingCart();
						will(returnValue(mockShoppingCart));
						allowing(mockCustomerSession).getShopper();
						will(returnValue(mockShopper));
						allowing(mockShopper).getCurrentShoppingCart();
						will(returnValue(mockShoppingCart));
						allowing(mockShopper).getCustomer();
						will(returnValue(mockCustomer));
						allowing(mockCustomer).getStoreCode();
						will(returnValue("storeCode"));
					}
				});

				return mockCustomerSession;
			}

			// Return a mock store configuration that has expectations on what methods will be called
			@Override
			public StoreConfig getStoreConfig() {

				// Create a store configuration that has methods overridden to make testing easier
				StoreConfig storeConfig = new ThreadLocalStorageImpl() {

					// Override the getStore method that will create a mock store that also have a
					// mock payment gateway, this payment gateway is then added to the map of payment
					// gateways for the store and expectations are set
					@Override
					public Store getStore() {

						// Creation of the mock objects
						final Store mockStore = context.mock(Store.class);
						final CreditCardPaymentGateway mockPaymentGateway = context.mock(CreditCardPaymentGateway.class);
						final PayerAuthenticationEnrollmentResult mockPayerAuthenticationEnrollmentResult
								= context.mock(PayerAuthenticationEnrollmentResult.class);

						// Creation of the map of PaymentGateways and putting in one paymentGateway that is mapped to credit cards
						final Map<PaymentType, PaymentGateway> map = new HashMap<PaymentType, PaymentGateway>();
						map.put(PaymentType.CREDITCARD, mockPaymentGateway);

						// Set expectations on the store and payment gateway as well as the payerAuthenticationEnrollment
						context.checking(new Expectations() {
							{
								oneOf(mockStore).getPaymentGatewayMap();
								will(returnValue(map));
								oneOf(mockPaymentGateway).checkEnrollment(with(any(ShoppingCart.class)), with(any(OrderPayment.class)));
								will(returnValue(mockPayerAuthenticationEnrollmentResult));
								oneOf(mockPayerAuthenticationEnrollmentResult).is3DSecureEnrolled();
								will(returnValue(false));
							}
						});

						// Return the mock store as a result
						return mockStore;
					}
				};

				// Return the extended version of the store configuration
				return storeConfig;
			}
		};
	}

	/**
	 * Ensures that a new credit card is added to the customer if the saveCustomerCreditCards setting is set to true, and the velocity template's
	 * saveForFutureUse checkbox is enabled.
	 */
	@Test
	public void testHandlePaymentWithNewCreditCardEnabled() {

		// Setup the test with appropriate overridden methods for the request helper
		handlePaymentNewCreditCardEnabledSetup();

		// Override a HTTP session that has the getAttribute method overridden and will return the appropriate
		// mocked objects when called, these mocked objects also have expectations on what methods will be called
		// for the objects they are mocking
		HttpSession session = new MockHttpSession() {

			@Override
			public Object getAttribute(final String name) {

				// Assume that the attribute that being obtained is a PaypalExpressSession
				final PayPalExpressSession mockPayPalExpressSession = context.mock(PayPalExpressSession.class);
				context.checking(new Expectations() {
					{
						allowing(mockPayPalExpressSession).getStatus();
						will(returnValue(ERROR_CODE));
						allowing(mockPayPalExpressSession).clearSessionInformation();
					}
				});
				return mockPayPalExpressSession;
			}
		};

		// Create mock objects
		final BillingAndReviewFormBean mockBillingReviewBean = context.mock(BillingAndReviewFormBean.class);

		final OrderPaymentFormBean mockOrderPaymentFormBean = context.mock(OrderPaymentFormBean.class);
		final CheckoutService mockCheckoutService = context.mock(CheckoutService.class);
		final CheckoutResults mockCheckoutResults = context.mock(CheckoutResults.class);
		final CustomerCreditCard mockCreditCard = context.mock(CustomerCreditCard.class);

		context.checking(new Expectations() {
			{
				atLeast(1).of(mockBillingReviewBean).getSelectedPaymentOption();

				will(returnValue(BillingAndReviewFormBean.PAYMENT_OPTION_NEW_CREDIT_CARD));
				atLeast(1).of(mockBillingReviewBean).getOrderPaymentFormBean();
				will(returnValue(mockOrderPaymentFormBean));
				oneOf(mockOrderPayment).setPaymentMethod(with(any(PaymentType.class)));
				oneOf(mockBillingReviewBean).isSaveCreditCardForFutureUse();
				will(returnValue(true));
				oneOf(mockCheckoutService).checkout(with(any(ShoppingCart.class)), with(any(OrderPayment.class)));
				will(returnValue(mockCheckoutResults));
				oneOf(mockOrderPayment).extractCreditCard();
				will(returnValue(mockCreditCard));
			}
		});

		setMockOrderPaymentExpectations(mockOrderPaymentFormBean);

		// Do the test by calling the onSubmit method from the BillingAndReviewFormController
		// also need to set mock objects for the RequestHelper and the controller itself to
		// test specific aspects of the controller.
		request.setSession(session);
		billingController.setSettingsReader(reader);
		billingController.setCheckoutService(mockCheckoutService);
		billingController.setRequestHelper(requestHelper);
		billingController.onSubmit(request, response, mockBillingReviewBean, null);

		// The test must invoke customer.addCreditCard()
	}

	/**
	 * Tests that the handle gift certificate method is called when the shopping cart returns a scale of zero.
	 */
	@Test
	public void testGiftCertificateHandledWithDifferentDecimalScales() {
		checkGiftCertificateHandled("Total with scale of zero", BigDecimal.ZERO);
		checkGiftCertificateHandled("Total with scale of two", new BigDecimal("0.00"));
	}
		
	private void checkGiftCertificateHandled(final String caseName, final BigDecimal shoppingCartTotal) {
		BillingAndReviewFormControllerGiftCertificateTestDouble billingController = new BillingAndReviewFormControllerGiftCertificateTestDouble();

		// Setup the test with appropriate overridden methods for the request helper
		handlePaymentNewCreditCardEnabledSetup();

		// Override a HTTP session that has the getAttribute method overridden and will return the appropriate
		// mocked objects when called, these mocked objects also have expectations on what methods will be called
		// for the objects they are mocking
		HttpSession session = new MockHttpSession() {

			@Override
			public Object getAttribute(final String name) {

				if (WebConstants.CUSTOMER_SESSION.equalsIgnoreCase(name)) {
					return super.getAttribute(name);
				}
				
				// Assume that the attribute that being obtained is a PaypalExpressSession
				final PayPalExpressSession mockPayPalExpressSession =
						context.mock(PayPalExpressSession.class, "PayPalExpressSession " + System.nanoTime());
				context.checking(new Expectations() {
					{
						allowing(mockPayPalExpressSession).getStatus();
						will(returnValue(ERROR_CODE));
						allowing(mockPayPalExpressSession).clearSessionInformation();
					}
				});
				return mockPayPalExpressSession;
			}
		};

		// Create mock objects
		final BillingAndReviewFormBean mockBillingReviewBean =
				context.mock(BillingAndReviewFormBean.class, "BillingAndReviewFormBean " + System.nanoTime());

		final OrderPaymentFormBean mockOrderPaymentFormBean = context.mock(OrderPaymentFormBean.class, "OrderPaymentFormBean " + System.nanoTime());
		final CheckoutService mockCheckoutService = context.mock(CheckoutService.class, "CheckoutService " + System.nanoTime());
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockBillingReviewBean).getOrderPaymentFormBean();
				will(returnValue(mockOrderPaymentFormBean));
			}
		});

		setMockOrderPaymentExpectations(mockOrderPaymentFormBean);

		final Set<GiftCertificate> giftCertificates = new HashSet<GiftCertificate>();
		giftCertificates.add(new GiftCertificateImpl());
		
		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSession();
		final ShoppingCart mockShoppingCart = context.mock(ShoppingCart.class, MOCK_PREFIX_SHOPPING_CART + System.nanoTime());
		context.checking(new Expectations() {
			{
				oneOf(mockShoppingCart).getTotal();
				will(returnValue(shoppingCartTotal));
				oneOf(mockShoppingCart).getAppliedGiftCertificates();
				will(returnValue(giftCertificates));
			}
		});
		customerSession.getShopper().setCurrentShoppingCart(mockShoppingCart);

		requestHelper = new RequestHelperImpl();
		request.setSession(session);
		final StoreConfig mockStoreConfig = context.mock(StoreConfig.class, "StoreConfigCart " + System.nanoTime());
		context.checking(new Expectations() {
			{
				oneOf(mockStoreConfig).getStore();
				will(returnValue(new StoreImpl()));
			}
		});
		((RequestHelperImpl) requestHelper).setStoreConfig(mockStoreConfig);
		requestHelper.setCustomerSession(request, customerSession);

		final BeanFactory mockBeanFactoryGiftCertificateTestDouble = context.mock(BeanFactory.class, "BeanFactory " + System.nanoTime());
		context.checking(new Expectations() {
			{
				allowing(mockBeanFactoryGiftCertificateTestDouble).getBean(ContextIdNames.ORDER_PAYMENT);
				will(returnValue(mockOrderPayment));
			}
		});

		// Do the test by calling the onSubmit method from the BillingAndReviewFormController
		// also need to set mock objects for the RequestHelper and the controller itself to
		// test specific aspects of the controller.
		billingController.setSettingsReader(reader);
		billingController.setCheckoutService(mockCheckoutService);
		billingController.setRequestHelper(requestHelper);
		billingController.setBeanFactory(mockBeanFactoryGiftCertificateTestDouble);
		billingController.onSubmit(request, response, mockBillingReviewBean, null);

		assertTrue("handlePaymentWithGiftCertificates() must be called for: " + caseName, 
				billingController.handlePaymentWithGiftCertificatesCalled);
	}

	private void handlePaymentNewCreditCardEnabledSetup() {

		reader = new CachedSettingsReaderImpl() {
			@Override
			public SettingValue getSettingValue(final String path) {
				final SettingValue mockSettingValue = context.mock(SettingValue.class);
				context.checking(new Expectations() {
					{
						oneOf(mockSettingValue).getBooleanValue();
						will(returnValue(true));
					}
				});
				return mockSettingValue;
			}
		};

		// Override the selected methods in request helper to return mocks as appropriate
		requestHelper = new RequestHelperImpl() {

			// Return a mock shipping cart that has expectations on what methods will be called
			private ShoppingCart createMockShoppingCart() {

				// Creates the mock objects
				final ShoppingCart mockShoppingCart = context.mock(ShoppingCart.class, MOCK_PREFIX_SHOPPING_CART + System.nanoTime());
				final GiftCertificate mockGC = context.mock(GiftCertificate.class, MOCK_PREFIX_SHOPPING_CART + System.nanoTime());

				// Creates a set of gift certificates and adds the mock gift certificate to the set, this
				// set will be returned when checking the appliedGiftCertificates to the shopping cart
				final Set<GiftCertificate> giftCerts = new HashSet<GiftCertificate>();
				giftCerts.add(mockGC);

				// Sets expectations on methods and returns the appropriate objects
				context.checking(new Expectations() {
					{
						allowing(mockShoppingCart).getTotal();
						will(returnValue(new BigDecimal(TOTAL)));
						allowing(mockShoppingCart).getAppliedGiftCertificates();
						will(returnValue(giftCerts));
					}
				});

				// Returns a mock shopping cart
				return mockShoppingCart;
			}

			// Creates a mock customer session, gift certificate, customer session and store and
			// sets expectations on what methods will be called.
			@Override
			public CustomerSession getCustomerSession(final HttpServletRequest request) {
				final CustomerSession mockCustomerSession =
						context.mock(CustomerSession.class, MOCK_PREFIX_CUSTOMER_SESSION + System.nanoTime());
				final Customer mockCustomer = context.mock(Customer.class, "Customer " + System.nanoTime());
				final Shopper shopper = context.mock(Shopper.class, "Shopper " + System.nanoTime());

				final ShoppingCart mockShoppingCart = createMockShoppingCart();

				context.checking(new Expectations() {
					{
						allowing(mockCustomerSession).getShopper();
						will(returnValue(shopper));
						allowing(mockCustomerSession).getShoppingCart();
						will(returnValue(mockShoppingCart));
						allowing(shopper).getCurrentShoppingCart();
						will(returnValue(mockShoppingCart));
						allowing(shopper).getCustomer();
						will(returnValue(mockCustomer));
						allowing(mockCustomer).getStoreCode();
						will(returnValue("storeCode"));
						allowing(mockCustomer).addCreditCard(with(any(CustomerCreditCard.class)));

						allowing(shopper).getCurrentShoppingCart();
						will(returnValue(mockShoppingCart));
					}
				});
				
				return mockCustomerSession;
			}

			// Return a mock store configuration that has expectations on what methods will be called
			@Override
			public StoreConfig getStoreConfig() {
				StoreConfig storeConfig = new ThreadLocalStorageImpl() {

					// Override the getStore method that will create a mock store that also have a
					// mock payment gateway, this payment gateway is then added to the map of payment
					// gateways for the store and expectations are set
					@Override
					public Store getStore() {

						// Creation of the mock objects
						final Store mockStore = context.mock(Store.class);
						final CreditCardPaymentGateway mockPaymentGateway = context.mock(CreditCardPaymentGateway.class);
						final PayerAuthenticationEnrollmentResult mockPayerAuthenticationEnrollmentResult
								= context.mock(PayerAuthenticationEnrollmentResult.class);

						// Creation of the map of PaymentGateways and putting in one paymentGateway that is mapped to credit cards
						final Map<PaymentType, PaymentGateway> map = new HashMap<PaymentType, PaymentGateway>();
						map.put(PaymentType.CREDITCARD, mockPaymentGateway);

						// Set expectations on the store and payment gateway as well as the payerAuthenticationEnrollment
						context.checking(new Expectations() {
							{
								oneOf(mockStore).getPaymentGatewayMap();
								will(returnValue(map));
								oneOf(mockPaymentGateway).checkEnrollment(with(any(ShoppingCart.class)), with(any(OrderPayment.class)));
								will(returnValue(mockPayerAuthenticationEnrollmentResult));
								oneOf(mockPayerAuthenticationEnrollmentResult).is3DSecureEnrolled();
								will(returnValue(false));
							}
						});

						// Return the mock store as a result
						return mockStore;
					}
				};

				// Return the extended version of the store configuration
				return storeConfig;
			}
		};
	}

	/**
	 * Test double for the BillingAndFormControllerImpl for gift certificate testing.
	 */
	private final class BillingAndReviewFormControllerGiftCertificateTestDouble extends BillingAndReviewFormControllerImpl {
		private boolean handlePaymentWithGiftCertificatesCalled = false;

		@Override
		public Object formBackingObject(final HttpServletRequest request) {
			return 0;
		}

		@Override
		protected ModelAndView handlePaymentWithGiftCertificates(final ShoppingCart shoppingCart, final OrderPayment orderPayment,
				final HttpServletRequest request, final BillingAndReviewFormBean billingAndReviewFormBean) {
			handlePaymentWithGiftCertificatesCalled = true;

			return new ModelAndView();
		}

	}
}
