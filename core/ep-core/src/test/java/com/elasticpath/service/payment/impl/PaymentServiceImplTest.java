/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.payment.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.commons.util.security.impl.CreditCardEncrypterImpl;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.CatalogLocaleImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerAuthenticationImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerProfileImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.ElectronicOrderShipmentImpl;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.PaymentHandler;
import com.elasticpath.domain.payment.PaymentHandlerFactory;
import com.elasticpath.domain.payment.impl.CreditCardPaymentHandler;
import com.elasticpath.domain.payment.impl.GiftCertificatePaymentHandler;
import com.elasticpath.domain.payment.impl.PayPalExpressPaymentHandler;
import com.elasticpath.domain.payment.impl.PaymentHandlerFactoryImpl;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImplTest;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.order.InvalidShipmentStateException;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentServiceException;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.impl.DefaultTaxCalculationServiceImpl;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.test.jmock.AbstractCatalogDataTestCase;

/**
 * Tests PaymentService.
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.TooManyMethods",
	"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects" })
public class PaymentServiceImplTest extends AbstractCatalogDataTestCase {

	private static final int TIMEOUT = 1000;
	private static final String PRODUCT_SKU_CODE = "PRODUCT-SKU-CODE-1";
	private static final Currency CURRENCY = Currency.getInstance("USD");
	private PaymentServiceImpl paymentService;

	@Mock
	private CreditCardPaymentGateway creditCardPaymentGateway;
	private PaymentGateway giftCertificatePaymentGateway;
	private PaymentGateway paypalPaymentGateway;
	private PaymentHandler mockPaymentHandler;
	private PaymentHandlerFactory mockPaymentHandlerFactory;

	@Mock
	private Price mockPrice;
	@Mock
	private StoreService storeService;

	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	@Override
	public void setUp() throws Exception {
		super.setUp();

		stubGetBean(ContextIdNames.CATALOG_LOCALE, CatalogLocaleImpl.class);
		stubGetBean(ContextIdNames.CUSTOMER_AUTHENTICATION, CustomerAuthenticationImpl.class);
		stubGetBean(ContextIdNames.CUSTOMER_PROFILE, CustomerProfileImpl.class);
		stubGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		stubGetBean(ContextIdNames.ORDER_ADDRESS, OrderAddressImpl.class);
		stubGetBean(ContextIdNames.ORDER_PAYMENT, OrderPaymentImpl.class);
		stubGetBean(ContextIdNames.ORDER_SKU, OrderSkuImpl.class);
		stubGetBean(ContextIdNames.PAYMENT_HANDLER_CREDITCARD, CreditCardPaymentHandler.class);
		stubGetBean(ContextIdNames.PAYMENT_HANDLER_GIFTCERTIFICATE, GiftCertificatePaymentHandler.class);
		stubGetBean(ContextIdNames.PAYMENT_HANDLER_PAYPAL, PayPalExpressPaymentHandler.class);
		stubGetBean(ContextIdNames.PAYMENT_HANDLER_FACTORY, PaymentHandlerFactoryImpl.class);
		stubGetBean(ContextIdNames.PAYMENT_RESULT, PaymentResultImpl.class);
		stubGetBean(ContextIdNames.PRODUCT_SKU, ProductSkuImpl.class);
		stubGetBean(ContextIdNames.TAX_CALCULATION_RESULT, TaxCalculationResultImpl.class);
		stubGetBean(ContextIdNames.TAX_CALCULATION_SERVICE, getTaxCalculationService());
		stubGetBean(ContextIdNames.UTILITY, UtilityImpl.class);
		stubGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);

		context.checking(new Expectations() { {
			allowing(storeService).findStoreWithCode(getMockedStore().getCode()); will(returnValue(getMockedStore()));
		} });

		//Not mocked for all tests
		mockPaymentHandlerFactory = context.mock(PaymentHandlerFactory.class);
		mockPaymentHandler = context.mock(PaymentHandler.class);
		context.checking(new Expectations() {
			{
				allowing(mockPaymentHandlerFactory).getPaymentHandler(with(any(PaymentType.class)));
				allowing(mockPaymentHandler).beforePreAuthorizePayment(
						with(any(OrderPayment.class)), with(any(OrderShipment.class)), with(Collections.<OrderPayment>emptyList()));
				will(returnValue(Arrays.<OrderPayment>asList()));

				allowing(mockPaymentHandlerFactory).getPaymentHandler(with(any(PaymentType.class)));
				will(returnValue(mockPaymentHandler));
			}
		});

		paymentService = getPaymentService();
		final CreditCardEncrypter encrypter = context.mock(CreditCardEncrypter.class);
		context.checking(new Expectations() {
			{
				allowing(encrypter).encrypt(with(any(String.class)));
				will(returnValue("11111"));

				allowing(encrypter).decrypt(with(any(String.class)));
				will(returnValue("4111111111111111"));

				allowing(encrypter).mask("4111111111111111");
				will(returnValue("************1111"));
			}
		});
		stubGetBean(ContextIdNames.CREDIT_CARD_ENCRYPTER, encrypter);


		context.checking(new Expectations() {
			{
				allowing(creditCardPaymentGateway).getPaymentType();
				will(returnValue(PaymentType.CREDITCARD));

				allowing(creditCardPaymentGateway).preAuthorize(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(creditCardPaymentGateway).reversePreAuthorization(with(any(OrderPayment.class)));
				allowing(creditCardPaymentGateway).capture(with(any(OrderPayment.class)));
				allowing(creditCardPaymentGateway).sale(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(creditCardPaymentGateway).setValidateCvv2(with(any(boolean.class)));
			}
		});

		Set<PaymentGateway> paymentGateways = new HashSet<PaymentGateway>();
		paymentGateways.add(creditCardPaymentGateway);

		giftCertificatePaymentGateway = context.mock(PaymentGateway.class, "gift cert payment gateway");
		context.checking(new Expectations() {
			{
				allowing(giftCertificatePaymentGateway).getPaymentType();
				will(returnValue(PaymentType.GIFT_CERTIFICATE));

				allowing(giftCertificatePaymentGateway).preAuthorize(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(giftCertificatePaymentGateway).capture(with(any(OrderPayment.class)));
				allowing(giftCertificatePaymentGateway).sale(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(giftCertificatePaymentGateway).reversePreAuthorization(with(any(OrderPayment.class)));
			}
		});
		paymentGateways.add(giftCertificatePaymentGateway);

		paypalPaymentGateway = context.mock(PaymentGateway.class, "paypal payment gateway");
		context.checking(new Expectations() {
			{
				allowing(paypalPaymentGateway).getPaymentType();
				will(returnValue(PaymentType.PAYPAL_EXPRESS));

				allowing(paypalPaymentGateway).preAuthorize(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(paypalPaymentGateway).capture(with(any(OrderPayment.class)));
				allowing(paypalPaymentGateway).sale(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(paypalPaymentGateway).reversePreAuthorization(with(any(OrderPayment.class)));
			}
		});
		paymentGateways.add(paypalPaymentGateway);
		getMockedStore().setPaymentGateways(paymentGateways);

		stubGetBean(ContextIdNames.TAX_CALCULATION_SERVICE, getTaxCalculationService());
		stubGetBean(ContextIdNames.PAYMENT_HANDLER_FACTORY, PaymentHandlerFactoryImpl.class);
		stubGetBean(ContextIdNames.PAYMENT_RESULT, PaymentResultImpl.class);
		stubGetBean(ContextIdNames.CREDIT_CARD_ENCRYPTER, CreditCardEncrypterImpl.class);

		paymentService = getPaymentService();

		setupMockPrice();
	}

	/**
	 * Tests a shipment which has a modified total amount that's been increased.
	 */
	@Test
	public void testShipmentModificationIncreasedAmount() {
		Order order = getOrder();
		OrderShipment orderShipment = addPhysicalOrderShipment(order);

		// 1. Initialises an order payment
		// 2. Pre-auths:
		OrderPayment templatePayment = getCreditCardTemplateOrderPayment();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		BigDecimal oldShipmentTotal = orderShipment.getTotal();
		OrderSku orderSku = getOrderSku();
		orderShipment.addShipmentOrderSku(orderSku); // increase the shipment total amount

		// usually some time should pass before adjust is called
		// NOTE: otherwise we get faulty results when getting the last auth payment
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
		}

		result = paymentService.adjustShipmentPayment(orderShipment); // 2 new order payment should be created
		validateAdjustShipmentPaymentResult(result, orderShipment, oldShipmentTotal);

		// usually some time should pass before shipment is processed
		// NOTE: otherwise we get faulty results when getting the last auth payment
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
		}

		result = paymentService.processShipmentPayment(orderShipment);
		validateProcessShipmentPaymentResult(result, orderShipment);
	}



	/**
	 * Test that an order with a single shipment of a digital good
	 * auths and captures immediately.
	 */
	@Test
	public void testSingleShipmentDigitalGood() {
		// Start with an order
		Order order = getOrder();
		OrderShipment orderShipment = addElectronicOrderShipment(order);

		context.checking(new Expectations() {
			{
				allowing(mockPaymentHandler).beforeInitializePayments(with(any(OrderPayment.class)), with(any(Order.class)));

				allowing(mockPaymentHandler).beforePreAuthorizePayment(
						with(any(OrderPayment.class)), with(any(OrderShipment.class)), with(Collections.<OrderPayment>emptyList()));
				will(returnValue(new PaymentResultImpl()));
			}
		});

		// 1. Initializes an order payment
		// 2. Pre-auths based on the single digital shipment.
		OrderPayment templatePayment = getCreditCardTemplateOrderPayment();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		result = paymentService.processShipmentPayment(orderShipment);
		validateProcessShipmentPaymentResult(result, orderShipment);

		validateOrder(order, templatePayment);

	}

	private OrderPayment getCreditCardTemplateOrderPayment() {
		OrderPayment orderPayment = getBeanFactory().getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		orderPayment.setCardHolderName("john smith");
		orderPayment.setUnencryptedCardNumber("4111111111111111");
		orderPayment.setCardType("MasterCard");
		orderPayment.setCreatedDate(new Date());
		orderPayment.setCurrencyCode("CAD");
		orderPayment.setCvv2Code("000");
		orderPayment.setEmail("test@test.com");
		orderPayment.setExpiryMonth("08");
		orderPayment.setExpiryYear("2010");
		return orderPayment;
	}

	/**
	 * Tests a shipment with one physical good.
	 */
	@Test
	public void testSingleShipmentPhysicalGood() {
		// Start with an order
		Order order = getOrder();
		OrderShipment orderShipment = addPhysicalOrderShipment(order);

		// The checkout service creates an order
		// The order creates a single shipment based on the ordered sku type

		// 1. Initializes an order payment
		// 2. Pre-auths based on the single physical shipment.
		OrderPayment templatePayment = getCreditCardTemplateOrderPayment();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		result = paymentService.processShipmentPayment(orderShipment);
		validateProcessShipmentPaymentResult(result, orderShipment);
		validateOrder(order, templatePayment);

	}

	/**
	 * Tests the split shipment case with one electronic and one physical product.
	 */
	@Test
	public void testSplitShipment() {

		Order order = getOrder();
		OrderShipment digitalGoodOrderShipment = addElectronicOrderShipment(order);
		OrderShipment physicalGoodOrderShipment = addPhysicalOrderShipment(order);
		OrderPayment templateOrderPayment = getCreditCardTemplateOrderPayment();

		// 1. Initialize an order payment
		// 2. Pre-auths:
		//   2a. CC GW: create two auths - one per shipment - store preauths
		//   2b. GC GW: creates single auth - covers both shipments.
		//   2c. PP GW: creates single auth - covers both shipment.
		PaymentResult result = paymentService.initializePayments(order, templateOrderPayment, null);
		validateInitializePaymentsResult(order, templateOrderPayment, result);

		// Release the digital shipment...
		result = paymentService.processShipmentPayment(digitalGoodOrderShipment);
		validateProcessShipmentPaymentResult(result, digitalGoodOrderShipment);

		// ... time passes ...

		// Release the physical shipment
		result = paymentService.processShipmentPayment(physicalGoodOrderShipment);
		validateProcessShipmentPaymentResult(result, physicalGoodOrderShipment);
		validateOrder(order, templateOrderPayment);

	}

	/**
	 * Tests split shipment when order is canceled and partially shipment has been released.
	 */
	@Test
	public void testSplitShipmentOrderCancellationWhenPartiallyShipmentReleased() {
		Order order = getOrder();
		OrderShipment digitalGoodOrderShipment = addElectronicOrderShipment(order);
		OrderShipment physicalGoodOrderShipment = addPhysicalOrderShipment(order);

		// 1. Initialize an order payment
		// 2. Pre-auths:
		OrderPayment templatePayment = getCreditCardTemplateOrderPayment();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		// Release the digital shipment...
		result = paymentService.processShipmentPayment(digitalGoodOrderShipment);
		validateProcessShipmentPaymentResult(result, digitalGoodOrderShipment);

		// Try cancel the order.
		try {
			paymentService.cancelOrderPayments(order);
			fail();
		} catch (PaymentServiceException expected) { //NOPMD
			// good, we shouldn't be able to cancel a partially shipped order.
		}

		// ... time passes ...

		// Release the physical shipment
		result = paymentService.processShipmentPayment(physicalGoodOrderShipment);
		validateProcessShipmentPaymentResult(result, physicalGoodOrderShipment);
	}

	/**
	 * Tests split shipment order cancellation when no shipment has been released.
	 */
	@Test
	public void testSplitShipmentOrderCancellationNoShipmentReleased() {
		Order order = getOrder();
		OrderShipment physicalGoodOrderShipment = addPhysicalOrderShipment(order);
		physicalGoodOrderShipment.setStatus(OrderShipmentStatus.ONHOLD);

		// 1. Initialises an order payment
		// 2. Pre-auths:
		OrderPayment templatePayment = getCreditCardTemplateOrderPayment();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		// No shipment released
		// Try to cancel the order
		try {
			result = paymentService.cancelOrderPayments(order);
			validateCancelOrderPaymentsResult(order, result);
			// Good, we should let the order been cancelled.
			order.cancelOrder();
		} catch (PaymentServiceException exception) {
			fail(exception.getMessage());
		}

		// ... time passes ...

		// Release the physical shipment
		try {
			paymentService.processShipmentPayment(physicalGoodOrderShipment);
			fail();
		} catch (InvalidShipmentStateException expected) { //NOPMD
			// Good, we can do nothing when order is cancelled.
		}
	}

	/**
	 * Tests split shipment order cancellation when no shipment has been released.
	 */
	@Test
	public void testSplitShipmentOrderCancellationWithDigitalGoods() {
		Order order = getOrder();
		OrderShipment digitalGoodOrderShipment = addElectronicOrderShipment(order);
		OrderShipment physicalGoodOrderShipment = addPhysicalOrderShipment(order);

		// 1. Initialises an order payment
		// 2. Pre-auths:
		OrderPayment templatePayment = getCreditCardTemplateOrderPayment();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		// No shipment released
		// Try cancel the order
		try {
			result = paymentService.cancelOrderPayments(order);
			fail();
		} catch (PaymentServiceException expected) {  //NOPMD
			// Good, the order can't be canceled, since it has digital goods.
		}

		// ... time passes ...

		// Release the physical shipment
		try {
			paymentService.processShipmentPayment(physicalGoodOrderShipment);
		} catch (PaymentServiceException expected) {
			fail();
		}

		// Release the electronic shipment
		try {
			paymentService.processShipmentPayment(digitalGoodOrderShipment);
		} catch (PaymentServiceException expected) {
			fail();
		}
	}

	/**
	 * Tests shipment cancellation when shipment hasn't been released yet.
	 */
	@Test
	public void testShipmentCancellationWhenShipmentNotReleasedYet() {
		Order order = getOrder();
		OrderShipment orderShipment = addPhysicalOrderShipment(order);
		orderShipment.setStatus(OrderShipmentStatus.ONHOLD);

		// 1. Initialises an order payment
		// 2. Pre-auths:
		OrderPayment templatePayment = getCreditCardTemplateOrderPayment();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		// No shipment released
		try {
			result = paymentService.cancelShipmentPayment(orderShipment);
			validateCancelShipmentPaymentsResult(result);
			// Good, we should be able to cancel a shipment before it is released.
			orderShipment.setStatus(OrderShipmentStatus.CANCELLED);
		} catch (PaymentServiceException expected) {
			fail();
		}

		// ... time passes ...

		// Release the physical shipment
		try {
			result = paymentService.processShipmentPayment(orderShipment);
			fail();
		} catch (InvalidShipmentStateException expected) { //NOPMD
			// Good, we can do nothing when shipment is cancelled.
		}

	}

	/**
	 * Tests a shipment cancellation that has already been released.
	 */
	@Test
	public void testShipmentCancellationWhenShipmentAlreadyReleased() {
		Order order = getOrder();
		OrderShipment orderShipment = addPhysicalOrderShipment(order);

		// 1. Initialises an order payment
		// 2. Pre-auths:
		OrderPayment templatePayment = getCreditCardTemplateOrderPayment();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		// ... time passes ...

		// Release the physical shipment
		try {
			result = paymentService.processShipmentPayment(orderShipment);
			validateProcessShipmentPaymentResult(result, orderShipment);
			orderShipment.setStatus(OrderShipmentStatus.CANCELLED);
			// Good, we should be able to release a shipment.
		} catch (PaymentServiceException expected) {
			fail();
		}

		try {
			paymentService.cancelShipmentPayment(orderShipment);
			fail();
		} catch (PaymentServiceException expected) { //NOPMD
			// good, we shouldn't be able to cancel a shipped order.
		}
	}

	/**
	 * Test that you can process an order shipment successfully if there were
	 * no conventional payment methods, only gift certificates.
	 */
	@Test
	public void testProcessOrderShipmentSucceedsWithOnlyGiftCertificates() {
		//Make a shipment that has only GCs as payment methods, and they should be already authorized.
		OrderShipment shipment = createOrderShipmentReadyForCaptureFundsPaidByGiftCertificates();
		try {
			paymentService.processShipmentPayment(shipment);
		} catch (Exception e) {
			fail("Should be able to process a shipment paid for entirely with gift certificates");
		}
	}

	private OrderShipment createOrderShipmentReadyForCaptureFundsPaidByGiftCertificates() {
		final GiftCertificate mockGc = context.mock(GiftCertificate.class);
		final OrderPayment mockPayment = context.mock(OrderPayment.class);
		final OrderShipment mockShipment = context.mock(OrderShipment.class);
		final Order mockOrder = context.mock(Order.class);

		final BigDecimal gcAmount = BigDecimal.TEN;
		context.checking(new Expectations() {
			{
				allowing(mockGc).getCurrencyCode();
				will(returnValue("USD"));

				allowing(mockGc).getPurchaseAmount();
				will(returnValue(gcAmount));

				allowing(mockGc).getGiftCertificateCode();
				will(returnValue("Test_Code"));

				allowing(mockShipment).getOrder();
				will(returnValue(mockOrder));

				allowing(mockShipment).getShipmentStatus();
				will(returnValue(OrderShipmentStatus.RELEASED));

				allowing(mockShipment).getOrderShipmentType();
				will(returnValue(ShipmentType.PHYSICAL));

				allowing(mockShipment).getTotal();
				will(returnValue(BigDecimal.TEN));

				allowing(mockShipment).getUidPk();
				will(returnValue((long) 1));

				allowing(mockShipment).isReadyForFundsCapture();
				will(returnValue(true));

				allowing(mockShipment).getShipmentOrderSkus();
				will(returnValue(Collections.emptySet()));

				allowing(mockPayment).getPaymentMethod();
				will(returnValue(PaymentType.GIFT_CERTIFICATE));

				allowing(mockPayment).getGiftCertificate();
				will(returnValue(mockGc));

				allowing(mockPayment).getAmount();
				will(returnValue(gcAmount));

				allowing(mockPayment).setReferenceId(with(any(String.class)));

				allowing(mockPayment).getOrderShipment();
				will(returnValue(mockShipment));

				allowing(mockPayment).setStatus(with(any(OrderPaymentStatus.class)));

				allowing(mockPayment).getTransactionType();
				will(returnValue(OrderPayment.AUTHORIZATION_TRANSACTION));

				allowing(mockPayment).getStatus();
				will(returnValue(OrderPaymentStatus.APPROVED));

				allowing(mockPayment).getAuthorizationCode();
				will(returnValue("Auth_Code1"));
			}
		});

		final Set<OrderPayment> orderPayments = new HashSet<OrderPayment>();
		orderPayments.add(mockPayment);
		context.checking(new Expectations() {
			{

				allowing(mockOrder).getOrderPayments();
				will(returnValue(orderPayments));

				allowing(mockOrder).addOrderPayment(with(any(OrderPayment.class)));

				allowing(mockOrder).isExchangeOrder();
				will(returnValue(Boolean.FALSE));

				allowing(mockOrder).getStoreCode();
				will(returnValue(getMockedStore().getCode()));

				allowing(mockOrder).getCurrency();
				will(returnValue(Currency.getInstance("USD")));

				allowing(mockOrder).getCustomer();
				will(returnValue(getCustomer()));
			}
		});

		return mockShipment;
	}

	/**
	 * Tests a shipment which total amount has gone less.
	 */
	@Test
	public void testShipmentModificationLessAmount() {
		Order order = getOrder();
		OrderShipment orderShipment = addPhysicalOrderShipment(order);

		// 1. Initialises an order payment
		// 2. Pre-auths:
		OrderPayment templatePayment = getCreditCardTemplateOrderPayment();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		BigDecimal oldShipmentTotal = orderShipment.getTotal();
		OrderSku orderSku = orderShipment.getShipmentOrderSkus().iterator().next();
		orderShipment.removeShipmentOrderSku(orderSku); // reduce the shipment total amount

		result = paymentService.adjustShipmentPayment(orderShipment); // no order payments should be changed
		validateAdjustShipmentPaymentResult(result, orderShipment, oldShipmentTotal);

		result = paymentService.processShipmentPayment(orderShipment);
		validateProcessShipmentPaymentResult(result, orderShipment);
	}

	private OrderSku getOrderSku() {
		OrderSku orderSku = getBeanFactory().getBean(ContextIdNames.ORDER_SKU);
		orderSku.setUnitPrice(BigDecimal.TEN);
		orderSku.setPrice(1, mockPrice);
		ProductSku productSku = getBeanFactory().getBean(ContextIdNames.PRODUCT_SKU);
		productSku.setSkuCode(PRODUCT_SKU_CODE);
		productSku.setProduct(getProduct());

		orderSku.setProductSku(productSku);
		return orderSku;
	}

	/**
	 * Tests a shipment released for shipping.
	 */
	@Test
	public void testReleaseShipmentFailed() {

		Order order = getOrder();
		OrderShipment orderShipment = addPhysicalOrderShipment(order);

		// 1. Initialises an order payment
		// 2. Pre-auths:
		OrderPayment templatePayment = getCreditCardTemplateOrderPayment();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		// ... time passes ...

		result = paymentService.processShipmentPayment(orderShipment);
	    // 1. Captures funds against existing pre-auth
		// 2. Applies the error code if such occurred or 0 for ok

		if (result.getResultCode() != PaymentResult.CODE_OK) {
			// notify user
			// fail the invocation process
			paymentService.rollBackPayments(result.getProcessedPayments());
		}

	}

	/**
	 * Tests pre and back order authorizations and payments.
	 */
	@Test
	public void testPreOrBackOrderPayment() {
		// TODO waiting for additional specs
	}


	/**
	 * Tests refund functionality.
	 */
	@Test
	public void testRefund() {
		// TODO
	}

	/**
	 * Test new shipment created on existing order.
	 */
	@Test
	public void testNewShipmentCreated() {

		Order order = getOrder();
		OrderShipment existingOrderShipment = addElectronicOrderShipment(order);

		OrderPayment templatePayment = getCreditCardTemplateOrderPayment();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		// add the new order shipment
		OrderShipment newOrderShipment = addPhysicalOrderShipment(order);

		BigDecimal oldShipmentTotal = existingOrderShipment.getTotal();
		// if the existing products qty has been changed we need to do adjust
		result = paymentService.adjustShipmentPayment(existingOrderShipment);
		validateAdjustShipmentPaymentResult(result, existingOrderShipment, oldShipmentTotal);

		result = paymentService.initializeNewShipmentPayment(newOrderShipment, getCreditCardTemplateOrderPayment());
		validateInitializeNewShipmentPaymentResult(result, newOrderShipment);

		// ... time passes ...

		result = paymentService.processShipmentPayment(newOrderShipment);
		validateProcessShipmentPaymentResult(result, newOrderShipment);
	}

	/**
	 * Create the order for testing.
	 * @return the new order
	 */
	private Order getOrder() {
		OrderImpl order = new OrderImpl() {
			private static final long serialVersionUID = 8527976573666828466L;

			@Override
			public BigDecimal getTotal() {
				return BigDecimal.TEN;
			}
		};
		order.setCurrency(CURRENCY);
		OrderAddress orderAddress = getBeanFactory().getBean(ContextIdNames.ORDER_ADDRESS);
		orderAddress.copyFrom(getBillingAddress());
		order.setBillingAddress(orderAddress);
		order.setStoreCode(getMockedStore().getCode());
		order.setUidPk(1);
		return order;
	}


	/**
	 * Create a electronicOrderShipment.
	 * @return the new electronicOrderShipment
	 */
	private OrderShipment addElectronicOrderShipment(final Order order) {
		ElectronicOrderShipmentImpl orderShipment = new ElectronicOrderShipmentImpl();
		orderShipment.setStatus(OrderShipmentStatus.RELEASED);
		orderShipment.setOrder(order);

		orderShipment.addShipmentOrderSku(getElectronicOrderSku());
		orderShipment.enableRecalculation();
		order.addShipment(orderShipment);
		return orderShipment;
	}

	private OrderSku getElectronicOrderSku() {
		OrderSku orderSku = new OrderSkuImpl();
		orderSku.setUnitPrice(BigDecimal.TEN);
		orderSku.setPrice(1, mockPrice);
		orderSku.setProductSku(getProductSku());
		return orderSku;
	}

	/**
	 * Create a physicalOrderShipment.
	 * @return the new physicalOrderShipment
	 */
	private OrderShipment addPhysicalOrderShipment(final Order order) {
		PhysicalOrderShipmentImpl orderShipment = new PhysicalOrderShipmentImpl();
		orderShipment.setOrder(order);
		orderShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);

		orderShipment.addShipmentOrderSku(getPhysicalOrderSku());
		orderShipment.setShippingCost(BigDecimal.ONE);
		orderShipment.enableRecalculation();
		orderShipment.setShipmentAddress(new OrderAddressImpl());

		orderShipment.setStatus(OrderShipmentStatus.RELEASED);
		order.addShipment(orderShipment);
		return orderShipment;
	}

	private OrderSku getPhysicalOrderSku() {
		OrderSku orderSku = new OrderSkuImpl();
		orderSku.setUnitPrice(BigDecimal.ONE);
		orderSku.setPrice(1, mockPrice);

		ProductSku productSku = getProductSku();
		productSku.setProduct(getProduct());
		orderSku.setProductSku(productSku);

		return orderSku;
	}

	private void setupMockPrice() {
		context.checking(new Expectations() {
			{
				Money money = MoneyFactory.createMoney(BigDecimal.ONE, CURRENCY);
				allowing(mockPrice).getListPrice(with(any(int.class)));
				will(returnValue(money));

				allowing(mockPrice).getSalePrice(with(any(int.class)));
				will(returnValue(money));

				allowing(mockPrice).getComputedPrice(with(any(int.class)));
				will(returnValue(money));

				allowing(mockPrice).getCurrency();
				will(returnValue(CURRENCY));

				allowing(mockPrice).getPricingScheme();
				will(returnValue(null));
			}
		});
	}

	/**
	 * Create the payment service.
	 * @return the payment service.
	 */
	private PaymentServiceImpl getPaymentService() {
		PaymentServiceImpl paymentService = new PaymentServiceImpl();
		//Ideally we would mock this for all tests, but most tests are not in an appropriate state yet
		paymentService.setPaymentHandlerFactory(getPaymentHandlerFactory());
		paymentService.setStoreService(storeService);

		return paymentService;
	}

	private PaymentHandlerFactory getPaymentHandlerFactory() {
		return new PaymentHandlerFactoryImpl();
	}


	private void validateCancelShipmentPaymentsResult(final PaymentResult result) {
		assertEquals(PaymentResult.CODE_OK, result.getResultCode());
		assertEquals(1, result.getProcessedPayments().size());
		for (OrderPayment orderPayment : result.getProcessedPayments()) {
			// Validate the reverse payment is approved.
			validateReversePaymentApproved(orderPayment);
		}
	}

	private void validateCancelOrderPaymentsResult(final Order order, final PaymentResult result) {
		assertEquals(PaymentResult.CODE_OK, result.getResultCode());
		assertEquals(order.getAllShipments().size(), result.getProcessedPayments().size());
		for (OrderPayment orderPayment : result.getProcessedPayments()) {
			// Validate the reverse payment is approved.
			validateReversePaymentApproved(orderPayment);
		}
	}

	private void validateReversePaymentApproved(final OrderPayment orderPayment) {
		assertEquals(OrderPayment.REVERSE_AUTHORIZATION, orderPayment.getTransactionType());
		assertEquals(OrderPaymentStatus.APPROVED, orderPayment.getStatus());
	}

	private void validateCapturePaymentApproved(final OrderPayment capturePayment, final OrderShipment orderShipment) {
		assertEquals(orderShipment.getTotal(), capturePayment.getAmount());
		assertEquals(OrderPayment.CAPTURE_TRANSACTION, capturePayment.getTransactionType());
		assertEquals(OrderPaymentStatus.APPROVED, capturePayment.getStatus());
	}

	private void validateAuthPaymentApproved(final OrderPayment authPayment, final OrderShipment orderShipment) {
		assertEquals(orderShipment.getTotal(), authPayment.getAmount());
		assertEquals(OrderPayment.AUTHORIZATION_TRANSACTION, authPayment.getTransactionType());
		assertEquals(OrderPaymentStatus.APPROVED, authPayment.getStatus());
	}

	private void validateProcessShipmentPaymentResult(final PaymentResult result, final OrderShipment orderShipment) {
		assertEquals(PaymentResult.CODE_OK, result.getResultCode());
		assertEquals(1, result.getProcessedPayments().size());
		validateCapturePaymentApproved(result.getProcessedPayments().iterator().next(), orderShipment);
	}

	private void validateInitializePaymentsResult(final Order order, final OrderPayment templatePayment, final PaymentResult result) {
		assertEquals(PaymentResult.CODE_OK, result.getResultCode());

		if ((templatePayment.getPaymentMethod() == PaymentType.CREDITCARD)
			|| ((templatePayment.getPaymentMethod() == PaymentType.GIFT_CERTIFICATE))) {
			assertEquals(order.getAllShipments().size(), result.getProcessedPayments().size());
			for (OrderPayment authPayment : result.getProcessedPayments()) {
				validateAuthPaymentApproved(authPayment, authPayment.getOrderShipment());
			}
		} else if ((templatePayment.getPaymentMethod() == PaymentType.PAYPAL_EXPRESS)) {
			// For paypal there is one order, and one auth.
			assertEquals(2, result.getProcessedPayments().size());
			OrderPayment authPayment = result.getProcessedPayments().iterator().next();
			assertEquals(order.getTotal(), authPayment.getAmount());
			assertEquals(OrderPayment.ORDER_TRANSACTION, authPayment.getTransactionType());
			assertEquals(OrderPaymentStatus.APPROVED, authPayment.getStatus());
		} else if ((templatePayment.getPaymentMethod() == PaymentType.GOOGLE_CHECKOUT)) {
			assertEquals(order.getAllShipments().size(), result.getProcessedPayments().size());
			OrderPayment authPayment = result.getProcessedPayments().iterator().next();
			assertEquals(order.getTotal(), authPayment.getAmount());
			assertEquals(OrderPayment.AUTHORIZATION_TRANSACTION, authPayment.getTransactionType());
			assertEquals(OrderPaymentStatus.APPROVED, authPayment.getStatus());
		}
	}

	private void validateOrder(final Order order, final OrderPayment templatePayment) {

		if ((templatePayment.getPaymentMethod() == PaymentType.CREDITCARD)
				|| ((templatePayment.getPaymentMethod() == PaymentType.GIFT_CERTIFICATE))
				|| ((templatePayment.getPaymentMethod() == PaymentType.GOOGLE_CHECKOUT))) {
			assertEquals(
					"The order should have 2 payments per shipment when no gift certificate is present",
					order.getAllShipments().size() * 2,
					order.getOrderPayments().size());
		} else if ((templatePayment.getPaymentMethod() == PaymentType.PAYPAL_EXPRESS)) {
			assertEquals(
					"The order should have 1 order payment for whole order, and 1 auth payment for whole order, "
					+ "and 1 capture payment per shipment when no gift certificate is present",
					order.getAllShipments().size() + 2,
					order.getOrderPayments().size());
		}
	}

	private void validateInitializeNewShipmentPaymentResult(final PaymentResult result,
			final OrderShipment orderShipment) {
		assertEquals(PaymentResult.CODE_OK, result.getResultCode());
		assertEquals(1, result.getProcessedPayments().size());
		for (OrderPayment authPayment : result.getProcessedPayments()) {
			validateAuthPaymentApproved(authPayment, orderShipment);
		}
	}

	private void validateAdjustShipmentPaymentResult(final PaymentResult paymentResult,
			final OrderShipment orderShipment, final BigDecimal oldShipmentTotal) {
		assertEquals(PaymentResult.CODE_OK, paymentResult.getResultCode());

		BigDecimal newShipmentTotal = orderShipment.getTotal();
		int result = newShipmentTotal.compareTo(oldShipmentTotal);
		if (result <= 0) { //Amount decreased or stay the same.
			// No payment been processed.
			assertEquals(0, paymentResult.getProcessedPayments().size());
		} else {
			// 1. reverse preauth
			// 2. new preauth requested.
			assertEquals(2, paymentResult.getProcessedPayments().size());
			// TODO validate the payments, one is reverse and one is preauth.
		}
	}

	private TaxCalculationService getTaxCalculationService() {
		DefaultTaxCalculationServiceImpl taxCalculationService = new DefaultTaxCalculationServiceImpl() {

			@Override
			protected TaxJurisdiction findTaxJurisdiction(final Store store, final Address address) {
				return ShoppingCartImplTest.getTaxJurisdictionsListForUS().iterator().next();
			}
		};
		taxCalculationService.setStoreService(storeService);

		return taxCalculationService;
	}

	/**
	 * Initializes a mock billing address.
	 *
	 * @return the Address
	 */
	private Address getBillingAddress() {
		Address billingAddress = new CustomerAddressImpl();
		billingAddress.setFirstName("Billy");
		billingAddress.setLastName("Bob");
		billingAddress.setCountry("US");
		billingAddress.setStreet1("1295 Charleston Road");
		billingAddress.setCity("Mountain View");
		billingAddress.setSubCountry("CA");
		billingAddress.setZipOrPostalCode("94043");

		return billingAddress;
	}

	/**
	 * Tests the split shipment case with one electronic and one physical product.
	 */
	@Test
	public void testSplitShipmentForPaypal() {

		Order order = getOrder();
		OrderShipment digitalGoodOrderShipment = addElectronicOrderShipment(order);
		OrderShipment physicalGoodOrderShipment = addPhysicalOrderShipment(order);
		OrderPayment templateOrderPayment = getTemplateOrderPaymentForPaypal();

		// 1. Initialize an order payment
		// 2. Pre-auths:
		//   2a. CC GW: create two auths - one per shipment - store preauths
		//   2b. GC GW: creates single auth - covers both shipments.
		//   2c. PP GW: creates single auth - covers both shipment.
		PaymentResult result = paymentService.initializePayments(order, templateOrderPayment, null);
		validateInitializePaymentsResult(order, templateOrderPayment, result);

		// Release the digital shipment...
		result = paymentService.processShipmentPayment(digitalGoodOrderShipment);
		validateProcessShipmentPaymentResult(result, digitalGoodOrderShipment);

		// ... time passes ...

		// Release the physical shipment
		result = paymentService.processShipmentPayment(physicalGoodOrderShipment);
		validateProcessShipmentPaymentResult(result, physicalGoodOrderShipment);
		validateOrder(order, templateOrderPayment);

	}

	private OrderPayment getTemplateOrderPaymentForPaypal() {
		OrderPayment orderPayment = getBeanFactory().getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.setPaymentMethod(PaymentType.PAYPAL_EXPRESS);

		return orderPayment;
	}

	/**
	 * Tests shipment cancellation when shipment hasn't been released yet.
	 */
	@Test
	public void testShipmentCancellationWhenShipmentNotReleasedYetForPaypal() {
		Order order = getOrder();
		OrderShipment orderShipment = addPhysicalOrderShipment(order);
		orderShipment.setStatus(OrderShipmentStatus.ONHOLD);

		// 1. Initialises an order payment
		// 2. Pre-auths:
		OrderPayment templatePayment = getTemplateOrderPaymentForPaypal();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		// No shipment released
		try {
			result = paymentService.cancelShipmentPayment(orderShipment);
			validateCancelShipmentPaymentsResult(result);
			// Good, we should be able to cancel a shipment before it is released.
			orderShipment.setStatus(OrderShipmentStatus.CANCELLED);
		} catch (PaymentServiceException expected) {
			fail();
		}

		// ... time passes ...

		// Release the physical shipment
		try {
			result = paymentService.processShipmentPayment(orderShipment);
			fail();
		} catch (InvalidShipmentStateException expected) { //NOPMD
			// Good, we can do nothing when shipment is cancelled.
		}

	}

	/**
	 * Tests a shipment cancellation that has already been released.
	 */
	@Test
	public void testShipmentCancellationWhenShipmentAlreadyReleasedForPaypal() {
		Order order = getOrder();
		OrderShipment orderShipment = addPhysicalOrderShipment(order);

		// 1. Initialises an order payment
		// 2. Pre-auths:
		OrderPayment templatePayment = getTemplateOrderPaymentForPaypal();
		PaymentResult result = paymentService.initializePayments(order, templatePayment, null);
		validateInitializePaymentsResult(order, templatePayment, result);

		// ... time passes ...

		// Release the physical shipment
		try {
			result = paymentService.processShipmentPayment(orderShipment);
			validateProcessShipmentPaymentResult(result, orderShipment);
			orderShipment.setStatus(OrderShipmentStatus.CANCELLED);
			// Good, we should be able to release a shipment.
		} catch (PaymentServiceException expected) {
			fail();
		}

		try {
			paymentService.cancelShipmentPayment(orderShipment);
			fail();
		} catch (PaymentServiceException expected) { //NOPMD
			// good, we shouldn't be able to cancel a shipped order.
		}
	}


	/**
	 * Test adjust order shipment which does not have a payment returns null.
	 */
	@Test
	public void testAdjustShipmentPayment() {
		PhysicalOrderShipmentImpl orderShipment = new PhysicalOrderShipmentImpl();

		assertNull(orderShipment.getTotal());

		assertNull(paymentService.adjustShipmentPayment(orderShipment));
	}

	
	/**
 	 * Ensure {@link com.elasticpath.service.payment.PaymentService#isOrderPaymentRefundable(OrderPayment)} returns false on non capture.
	 */
	@Test
	public void ensureIsOrderPaymentRefundableReturnsFalseOnNonCapture() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		assertFalse("Non capture payment should not be refundable.", paymentService.isOrderPaymentRefundable(orderPayment));
	}
	
	/**
 	 * Ensure {@link com.elasticpath.service.payment.PaymentService#isOrderPaymentRefundable(OrderPayment)} returns false on null.
	 */
	@Test
	public void ensureIsOrderPaymentRefundableReturnsFalseOnNull() {
		assertFalse("Null payment should not be refundable.", paymentService.isOrderPaymentRefundable(null));
	}
	
	/**
	 * Ensure {@link com.elasticpath.service.payment.PaymentService#isOrderPaymentRefundable(OrderPayment)} 
	 * returns false if card number is not persisted.
	 */
	@Test
	public void ensureIsOrderPaymentRefundableReturnsFalseIfCardNumberIsNotPersisted() {
		OrderPayment orderPayment = getCreditCardTemplateOrderPayment();
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		orderPayment.setUnencryptedCardNumber(null);
		assertFalse("Credit Card Payment should not be refundable without encrypted number.", paymentService.isOrderPaymentRefundable(orderPayment));
	}

	/**
	 * Ensure {@link com.elasticpath.service.payment.PaymentService#isOrderPaymentRefundable(OrderPayment)}  returns true if 
	 * {@link PaymentType#PAYMENT_TOKEN} is persisted.
	 */
	@Test
	public void ensureIsOrderPaymentRefundableReturnsTrueIfPaymentToken() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		assertTrue("Captured Payment Token should be refundable.", paymentService.isOrderPaymentRefundable(orderPayment));
	}
	
	/**
	 * Ensure {@link com.elasticpath.service.payment.PaymentService#isOrderPaymentRefundable(OrderPayment)}  returnstrue if card number is persisted.
	 */
	@Test
	public void ensureIsOrderPaymentRefundableReturnsTrueIfCardNumberIsPersisted() {
		OrderPayment orderPayment = getCreditCardTemplateOrderPayment();
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		assertTrue("Null payment should not be refundable.", paymentService.isOrderPaymentRefundable(orderPayment));
	}
	
	/**
	 * @return a new <code>Customer</code> instance.
	 */
	private Customer getCustomer() {
		final Customer customer = new CustomerImpl();
		customer.setGuid((new RandomGuidImpl()).toString());
		customer.initialize();
		return customer;
	}
}
