package com.elasticpath.domain.event.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.MaskingOnlyCreditCardEncrypterImpl;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.OrderEventPaymentDetailFormatter;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.MoneyFormatter;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.ShippingServiceLevelLocalizedPropertyValueImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderEvent;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.OrderEventImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for {@link OrderEventHelperImpl}.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class OrderEventHelperImplTest {
	private static final String TEST_FORMATTED_AMOUNT = "formatted amount";

	private static final String TEST_CREDIT_CARD_TYPE = "Visa";

	private static final String TEST_TOKEN_DISPLAY_VALUE = "testTokenDisplayValue";

	private static final String TEST_CREDIT_CARD_NUMBER = "4111111111111111";

	private static final String TEST_GIFT_CERTIFICATE_CODE = "testGiftCertificateCode";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private OrderEventHelperImpl orderEventHelper;
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private final MoneyFormatter moneyFormatter = context.mock(MoneyFormatter.class);
	private final TimeService timeService = context.mock(TimeService.class);
	private final ShippingServiceLevelService shippingLevelService = context.mock(ShippingServiceLevelService.class);
	private final Order order = context.mock(Order.class);
	private OrderEvent orderEvent;
	private final EventOriginator scapegoat = new EventOriginatorImpl();
	private final CreditCardEncrypter mockCreditCardEncrypter = new MaskingOnlyCreditCardEncrypterImpl();
	
	/**
	 * Sets up mock bean factory, sets up OrderEventHelper with mock services, and mocks an Order for testing.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		orderEvent = new OrderEventImpl();

		BeanFactoryExpectationsFactory bfef = new BeanFactoryExpectationsFactory(context, beanFactory);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.ORDER_EVENT, orderEvent);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.EVENT_ORIGINATOR_HELPER, new EventOriginatorHelperImpl());
		bfef.allowingBeanFactoryGetBean(ContextIdNames.EVENT_ORIGINATOR, new EventOriginatorImpl());
		bfef.allowingBeanFactoryGetBean(ContextIdNames.CREDIT_CARD_ENCRYPTER, mockCreditCardEncrypter);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.CARD_ENCRYPTER, mockCreditCardEncrypter);
		
		context.checking(new Expectations() { {
			allowing(order).getModifiedBy(); will(returnValue(scapegoat));
			allowing(order).getLocale(); will(returnValue(Locale.CANADA));

			allowing(timeService).getCurrentTime(); will(returnValue(new Date()));

			allowing(moneyFormatter).formatCurrency(with(any(Money.class)), with(any(Locale.class)));
			will(returnValue(TEST_FORMATTED_AMOUNT));
		} });

		orderEventHelper = new OrderEventHelperImpl();
		orderEventHelper.setBeanFactory(beanFactory);
		orderEventHelper.setMoneyFormatter(moneyFormatter);
		orderEventHelper.setShippingServiceLevelService(shippingLevelService);
		orderEventHelper.setTimeService(timeService);
		
		Map<PaymentType, OrderEventPaymentDetailFormatter> formatterMap = new HashMap<PaymentType, OrderEventPaymentDetailFormatter>();
		formatterMap.put(PaymentType.CREDITCARD, new OrderEventCreditCardDetailsFormatter());
		formatterMap.put(PaymentType.GIFT_CERTIFICATE, new OrderEventGiftCertificateDetailsFormatter());
		formatterMap.put(PaymentType.PAYMENT_TOKEN, new OrderEventPaymentTokenDetailsFormatter());
		orderEventHelper.setFormatterMap(formatterMap);
	}

	/**
	 * Test logging when order shipment method is changed.
	 */
	@Test
	public void testLogOrderShipmentMethodChanged() {
		// Given
		final ShippingServiceLevelImpl serviceLevel = createShippingServiceLevel();

		PhysicalOrderShipment physicalShipment = new PhysicalOrderShipmentImpl();
		physicalShipment.setShippingServiceLevelGuid(serviceLevel.getGuid());

		// Expectations
		context.checking(new Expectations() { {
			allowing(shippingLevelService).findByGuid(serviceLevel.getGuid()); will(returnValue(serviceLevel));

			oneOf(order).addOrderEvent(orderEvent);
		} });

		// When
		orderEventHelper.logOrderShipmentMethodChanged(order, physicalShipment);

		// Then
		assertTrue("Message should contain shippingServLevel's display name",
				orderEvent.getNote().contains(serviceLevel.getDisplayName(Locale.CANADA, false)));
	}
	
	/**
	 * Ensure logging of order payment refund with credit card has correct format.
	 */
	@Test
	public void ensureLoggingOfOrderPaymentRefundWithCreditCardHasCorrectFormat() {
		Order order = createBlankOrderWithLocale();
		orderEventHelper.logOrderPaymentRefund(order, createCreditCardRefundOrderPayment());
		
		OrderEvent refundOrderEvent = getSingleOrderEventFromOrder(order);
		String refundEventNote = refundOrderEvent.getNote();
		assertNoteIsRefund(refundEventNote);
		assertThatNoteIsForCreditCard(refundEventNote);
		assertThatNoteHasFormattedAmount(refundEventNote);
	}

	/**
	 * Ensure logging of order payment refund with gift certificate has correct format.
	 */
	@Test
	public void ensureLoggingOfOrderPaymentRefundWithGiftCertificateHasCorrectFormat() {
		Order order = createBlankOrderWithLocale();
		orderEventHelper.logOrderPaymentRefund(order, createGiftCertificateRefundOrderPayment());
		OrderEvent refundOrderEvent = getSingleOrderEventFromOrder(order);
		String refundEventNote = refundOrderEvent.getNote();
		assertNoteIsRefund(refundEventNote);
		assertThatNoteIsForGiftCertificate(refundEventNote);	
		assertThatNoteHasFormattedAmount(refundEventNote);
	}


	/**
	 * Ensure logging of order payment refund with payment token has correct format.
	 */
	@Test
	public void ensureLoggingOfOrderPaymentRefundWithPaymentTokenHasCorrectFormat() {
		Order order = createBlankOrderWithLocale();
		orderEventHelper.logOrderPaymentRefund(order, createPaymentTokenRefundOrderPayment());
		OrderEvent refundOrderEvent = getSingleOrderEventFromOrder(order);
		String refundEventNote = refundOrderEvent.getNote();
		assertNoteIsRefund(refundEventNote);
		assertThatNoteIsForPaymentToken(refundEventNote);	
		assertThatNoteHasFormattedAmount(refundEventNote);
	}
	
	/**
	 * Ensure logging of order payment capture with credit card has correct format.
	 */
	@Test
	public void ensureLoggingOfOrderPaymentCaptureWithCreditCardHasCorrectFormat() {
		Order order = createBlankOrderWithLocale();
		orderEventHelper.logOrderPaymentCaptured(order, createCreditCardCaptureOrderPayment());
		OrderEvent captureOrderEvent = getSingleOrderEventFromOrder(order);
		String captureEventNote = captureOrderEvent.getNote();
		assertThatNoteIsCapture(captureEventNote);
		assertThatNoteIsForCreditCard(captureEventNote);	
		assertThatNoteHasFormattedAmount(captureEventNote);
	}

	/**
	 * Ensure logging of order payment capture with payment token has correct format.
	 */
	@Test
	public void ensureLoggingOfOrderPaymentCaptureWithPaymentTokenHasCorrectFormat() {
		Order order = createBlankOrderWithLocale();
		orderEventHelper.logOrderPaymentCaptured(order, createPaymentTokenCaptureOrderPayment());
		OrderEvent captureOrderEvent = getSingleOrderEventFromOrder(order);
		String captureEventNote = captureOrderEvent.getNote();
		assertThatNoteIsCapture(captureEventNote);
		assertThatNoteIsForPaymentToken(captureEventNote);	
		assertThatNoteHasFormattedAmount(captureEventNote);
	}
	
	/**
	 * Ensure logging of order payment capture with gift certificate has correct format.
	 */
	@Test
	public void ensureLoggingOfOrderPaymentCaptureWithGiftCertificateHasCorrectFormat() {
		Order order = createBlankOrderWithLocale();
		orderEventHelper.logOrderPaymentCaptured(order, createGiftCertificateCaptureOrderPayment());
		OrderEvent refundOrderEvent = getSingleOrderEventFromOrder(order);
		String captureEventNote = refundOrderEvent.getNote();
		assertThatNoteIsCapture(captureEventNote);
		assertThatNoteIsForGiftCertificate(captureEventNote);
		assertThatNoteHasFormattedAmount(captureEventNote);
	}

	private OrderPayment createCreditCardRefundOrderPayment() {
		OrderPayment orderPayment = createRefundOrderPayment();
		addCreditCardToOrderPayment(orderPayment);
		return orderPayment;
	}
	
	private OrderPayment createGiftCertificateRefundOrderPayment() {
		OrderPayment orderPayment = createRefundOrderPayment();
		addGiftCertificateToOrderPayment(orderPayment);
		return orderPayment;
	}
	
	private OrderPayment createPaymentTokenRefundOrderPayment() {
		OrderPayment orderPayment = createRefundOrderPayment();
		addPaymentTokenToOrderPayment(orderPayment);
		return orderPayment;
	}
	
	private OrderPayment createPaymentTokenCaptureOrderPayment() {
		OrderPayment orderPayment = createCaptureOrderPayment();
		addPaymentTokenToOrderPayment(orderPayment);
		return orderPayment;
	}
	
	private OrderPayment createCreditCardCaptureOrderPayment() {
		OrderPayment orderPayment = createCaptureOrderPayment();
		addCreditCardToOrderPayment(orderPayment);
		return orderPayment;
	}
	
	private OrderPayment createGiftCertificateCaptureOrderPayment() {
		OrderPayment orderPayment = createCaptureOrderPayment();
		addGiftCertificateToOrderPayment(orderPayment);
		return orderPayment;
	}
	
	private void addCreditCardToOrderPayment(final OrderPayment orderPayment) {
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		orderPayment.setCardType(TEST_CREDIT_CARD_TYPE);
		orderPayment.setUnencryptedCardNumber(TEST_CREDIT_CARD_NUMBER);
	}

	private void addGiftCertificateToOrderPayment(final OrderPayment orderPayment) {
		orderPayment.setPaymentMethod(PaymentType.GIFT_CERTIFICATE);
		GiftCertificate giftCertificate = new GiftCertificateImpl();
		giftCertificate.setGiftCertificateCode(TEST_GIFT_CERTIFICATE_CODE);
		orderPayment.setGiftCertificate(giftCertificate);
	}

	private void addPaymentTokenToOrderPayment(final OrderPayment orderPayment) {
		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		PaymentToken paymentToken = new PaymentTokenImpl.TokenBuilder()
			.withDisplayValue(TEST_TOKEN_DISPLAY_VALUE)
			.build();
		orderPayment.usePaymentToken(paymentToken);
	}

	private OrderPayment createRefundOrderPayment() {
		OrderPayment orderPayment = createApprovedOrderPayment();
		orderPayment.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
		return orderPayment;
	}
	
	private OrderPayment createCaptureOrderPayment() {
		OrderPayment orderPayment = createApprovedOrderPayment();
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		return orderPayment;
	}

	private OrderPayment createApprovedOrderPayment() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setCurrencyCode("USD");
		orderPayment.setStatus(OrderPaymentStatus.APPROVED);
		orderPayment.setAmount(BigDecimal.TEN);
		return orderPayment;
	}

	private Order createBlankOrderWithLocale() {
		Order order = new OrderImpl();
		order.setLocale(Locale.US);
		return order;
	}
	
	private OrderEvent getSingleOrderEventFromOrder(final Order order) {
		Set<OrderEvent> orderEvents = order.getOrderEvents();
		assertEquals("Only one order event should be added.", 1, orderEvents.size());
		return orderEvents.iterator().next();
	}
	
	private ShippingServiceLevelImpl createShippingServiceLevel() {
		ShippingServiceLevelImpl serviceLevel = new ShippingServiceLevelImpl();
		serviceLevel.setGuid("ssl-guid");
		final ShippingServiceLevelLocalizedPropertyValueImpl displayName = new ShippingServiceLevelLocalizedPropertyValueImpl();
		displayName.setValue("DisplayName");
		final LocalizedPropertiesImpl localizedProperties = new LocalizedPropertiesImpl();
		localizedProperties.setLocalizedPropertiesMap(Collections.<String, LocalizedPropertyValue>singletonMap(
				ShippingServiceLevel.LOCALIZED_PROPERTY_NAME + "_en_CA", displayName), "xxx");
		serviceLevel.setLocalizedProperties(localizedProperties);
		return serviceLevel;
	}

	private void assertNoteIsRefund(final String refundEventNote) {
		assertThat("Note should be a refund.", refundEventNote, containsString("Refund"));
	}
	
	private void assertThatNoteIsCapture(final String captureEventNote) {
		assertThat("Note should be a capture.", captureEventNote, containsString("Capture"));
	}
	
	private void assertThatNoteIsForPaymentToken(final String refundEventNote) {
		assertThat("Payment type should be included in note.", refundEventNote, containsString(PaymentType.PAYMENT_TOKEN.name()));
		assertThat("Token display value should be included in note.", refundEventNote, containsString(TEST_TOKEN_DISPLAY_VALUE));
	}

	private void assertThatNoteIsForCreditCard(final String captureEventNote) {
		assertThat("Payment type should be included in note.", captureEventNote, containsString(PaymentType.CREDITCARD.name()));
		assertThat("Credit card type should be included in note.", captureEventNote, containsString(TEST_CREDIT_CARD_TYPE));
		assertThat("Masked credit card value should be included in note.", captureEventNote, 
				containsString(mockCreditCardEncrypter.mask(TEST_CREDIT_CARD_NUMBER)));
	}
	
	private void assertThatNoteIsForGiftCertificate(final String refundEventNote) {
		assertThat("Payment type should be included in note.", refundEventNote, containsString(PaymentType.GIFT_CERTIFICATE.name()));
		assertThat("Masked Gift Certificate code should be included in note.", refundEventNote, 
				containsString(mockCreditCardEncrypter.mask(TEST_GIFT_CERTIFICATE_CODE)));
	}
	
	private void assertThatNoteHasFormattedAmount(final String refundEventNote) {
		assertThat("Payment amount should be included in note.", refundEventNote, containsString(TEST_FORMATTED_AMOUNT));
	}
}
