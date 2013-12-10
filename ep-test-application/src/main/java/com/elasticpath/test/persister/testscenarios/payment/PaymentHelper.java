package com.elasticpath.test.persister.testscenarios.payment;

import java.math.BigDecimal;
import java.util.Date;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * PaymentHelper class spans templates for various order payments and amounts.
 */
public final class PaymentHelper {

	public static final BigDecimal AMOUNT_10 = BigDecimal.valueOf(10d);

	public static final BigDecimal AMOUNT_20 = BigDecimal.valueOf(20d);

	public static final BigDecimal AMOUNT_30 = BigDecimal.valueOf(30d);

	public static final BigDecimal AMOUNT_40 = BigDecimal.valueOf(40d);

	public static final BigDecimal AMOUNT_50 = BigDecimal.valueOf(50d);

	public static final BigDecimal AMOUNT_60 = BigDecimal.valueOf(60d);

	private final ElasticPath elasticPath;

	private PaymentHelper(final ElasticPath elasticPath) {
		this.elasticPath = elasticPath;
	}

	/**
	 * Creates the PaymentHelper. Elastic Path is required
	 * 
	 * @param elasticPath the ElasticPath
	 * @return the instance of PaymentHelper
	 */
	public static PaymentHelper createPaymentHelper(final ElasticPath elasticPath) {
		PaymentHelper paymentFactory = new PaymentHelper(elasticPath);
		return paymentFactory;
	}

	/**
	 * Produces template for Credit Cart payment.
	 * 
	 * @return the order payment.
	 */
	public OrderPayment creditCardTemplatePayment() {
		OrderPayment payment = getOrderPayment(PaymentType.CREDITCARD);
		return payment;
	}

	/**
	 * Produces template for Gift Certificate payment.
	 * 
	 * @param giftCertificate the gift certificate to be used by the gc payment.
	 * @return the order payment.
	 */
	public OrderPayment giftCertificateTemplatePayment(final GiftCertificate giftCertificate) {
		OrderPayment payment = getOrderPayment(PaymentType.GIFT_CERTIFICATE);
		payment.setGiftCertificate(giftCertificate);
		return payment;
	}

	/**
	 * Produces Credit Cart authorization payment for the specified amount.
	 * 
	 * @param amount the auth amount.
	 * @return the order payment.
	 */
	public OrderPayment creditCartAuthPayment(final BigDecimal amount) {
		OrderPayment payment = getOrderPayment(PaymentType.CREDITCARD);
		payment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		payment.setAmount(amount);
		return payment;
	}

	/**
	 * Produces Gift Certificate authorization payment for the specified amount.
	 * 
	 * @param amount the auth amount.
	 * @return the order payment.
	 */
	public OrderPayment giftCertificateAuthPayment(final BigDecimal amount) {
		OrderPayment payment = getOrderPayment(PaymentType.GIFT_CERTIFICATE);
		payment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		payment.setAmount(amount);
		return payment;
	}

	/**
	 * Produces Credit Cart authorization payment for the specified amount.
	 * 
	 * @param amount the auth amount.
	 * @return the order payment.
	 */
	public OrderPayment creditCartAuthReversedPayment(final BigDecimal amount) {
		OrderPayment payment = getOrderPayment(PaymentType.CREDITCARD);
		payment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		payment.setAmount(amount);
		return payment;
	}

	/**
	 * Produces Gift Certificate authorization payment for the specified amount.
	 * 
	 * @param amount the auth amount.
	 * @return the order payment.
	 */
	public OrderPayment giftCertificateAuthReversedPayment(final BigDecimal amount) {
		OrderPayment payment = getOrderPayment(PaymentType.GIFT_CERTIFICATE);
		payment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		payment.setAmount(amount);
		return payment;
	}

	/**
	 * Produces Credit Cart capture payment for the specified amount.
	 * 
	 * @param amount the capture amount.
	 * @return the order payment.
	 */
	public OrderPayment creditCartCapturePayment(final BigDecimal amount) {
		OrderPayment payment = getOrderPayment(PaymentType.CREDITCARD);
		payment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		payment.setAmount(amount);
		return payment;
	}

	/**
	 * Produces Gift Certificate capture payment for the specified amount.
	 * 
	 * @param amount the capture amount.
	 * @return the order payment.
	 */
	public OrderPayment giftCertificateCapturePayment(final BigDecimal amount) {
		OrderPayment payment = getOrderPayment(PaymentType.GIFT_CERTIFICATE);
		payment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		payment.setAmount(amount);
		return payment;
	}

	/**
	 * Produces Credit Cart reverse payment for the specified amount.
	 * 
	 * @param amount the reverse amount.
	 * @return the order payment.
	 */
	public OrderPayment creditCartReversePayment(final BigDecimal amount) {
		OrderPayment payment = getOrderPayment(PaymentType.CREDITCARD);
		payment.setTransactionType(OrderPayment.REVERSE_AUTHORIZATION);
		payment.setAmount(amount);
		return payment;
	}

	/**
	 * Produces Gift Certificate reverse payment for the specified amount.
	 * 
	 * @param amount the reverse amount.
	 * @return the order payment.
	 */
	public OrderPayment giftCertificateReversePayment(final BigDecimal amount) {
		OrderPayment payment = getOrderPayment(PaymentType.GIFT_CERTIFICATE);
		payment.setTransactionType(OrderPayment.REVERSE_AUTHORIZATION);
		payment.setAmount(amount);
		return payment;
	}

	private OrderPayment getOrderPayment(final PaymentType paymentType) {
		OrderPayment orderPayment = null;
		switch (paymentType) {
		case CREDITCARD:
			orderPayment = newCCOrderPayment();
			break;
		case GIFT_CERTIFICATE: // CC fits also here
			orderPayment = newCCOrderPayment();
			break;
		default:
			break;
		}
		if (orderPayment != null) {
			orderPayment.setPaymentMethod(paymentType);
			orderPayment.setStatus(OrderPaymentStatus.APPROVED);
		}
		return orderPayment;
	}

	// *********************************
	private OrderPayment newCCOrderPayment() {
		OrderPayment orderPayment = elasticPath.getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.setCardHolderName("test test");
		orderPayment.setCardType("001");
		orderPayment.setCreatedDate(new Date());
		orderPayment.setCurrencyCode("USD");
		orderPayment.setEmail("myemail@yahoo.com");
		orderPayment.setExpiryMonth("09");
		orderPayment.setExpiryYear("10");
		orderPayment.setCvv2Code("1111");
		orderPayment.setUnencryptedCardNumber("4111111111111111");
		return orderPayment;
	}
}
