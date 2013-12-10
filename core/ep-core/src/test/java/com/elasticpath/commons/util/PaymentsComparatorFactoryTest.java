/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>PaymentsComparatorFactory</code>.
 */
public class PaymentsComparatorFactoryTest {

	private static final String GIFT_CERTIFICATE_CODE = "giftCertificateCode";
	private static final String TOKEN_DISPLAY_VALUE = "token-display-value";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private final CreditCardEncrypter mockEncrypter = new MaskingOnlyCreditCardEncrypterImpl();
	
	/** Time displacement. **/
	private static final int TWO_SECONDS = 2000;
	private static final int FOUR_SECONDS = 4000;
	
	/** The amount of the OrderPayment. **/
	public static final double ORDER_AMOUNT = 19.99;
	
	/** Size of lists. **/
	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;
	
	/** Create the information pertinent for 3 credit cards. **/
	private static final String UNENCRYPTED_CARD_NUMBER = "4012888888881881";
	private static final String UNENCRYPTED_CARD_NUMBER_TWO = "4007000000027";
	private static final String UNENCRYPTED_CARD_NUMBER_THREE = "4111111111111111";
	private static final String CARD_HOLDER_NAME = "Elastic Path";
	private static final String CARD_HOLDER_NAME_TWO = "Elastic Path2";
	private static final String CARD_HOLDER_NAME_THREE = "Elastic Pather";
	private static final String CARD_TYPE = "VISA";
	private static final String CARD_TYPE_TWO = "MASTERCARD";
	private static final String CARD_TYPE_THREE = "DISCOVERY";
	private static final String EXPIRY_YEAR = "2019";
	private static final String EXPIRY_YEAR_TWO = "2219";
	private static final String EXPIRY_YEAR_THREE = "2319";
	private static final String EXPIRY_MONTH = "01";
	private static final String EXPIRY_MONTH_TWO = "03";
	private static final String EXPIRY_MONTH_THREE = "04";
	private static final String CARD_CVV2_CODE = "123";
	private static final String CARD_CVV2_CODE_TWO = "145";
	private static final String CARD_CVV2_CODE_THREE = "154";
	private static final Date START_DATE = new Date();
	private static final String ISSUE_NUMBER = "134";
	private static final String ISSUE_NUMBER_TWO = "123";
	private static final String ISSUE_NUMBER_THREE = "111";


	/**
	 * Sets the up mock credit card encrypter.
	 */
	@Before
	public void setUpMockCreditCardEncrypter() {
		BeanFactoryExpectationsFactory bfef = new BeanFactoryExpectationsFactory(context, beanFactory);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.CREDIT_CARD_ENCRYPTER, mockEncrypter);
	}

	/**
	 * Test that creates an OrderPaymentDateComparator, obtains it and
	 * compares two OrderPayments by order date. Returns a value of 0
	 * if the dates are the same, 1 if the first date is less than the second date
	 * and -1 if the second date is greater than the first date.
	 * @throws InterruptedException if the thread can't sleep
	 */
	@Test
	public void testGetOrderPaymentDateComparator() throws InterruptedException {
		
		//Create two order payments and initially set them with the same date and the same amount.
		OrderPayment orderOne = new OrderPaymentImpl();
		OrderPayment orderTwo = new OrderPaymentImpl();
		Date date = new Date();
		orderOne.setCreatedDate(date);
		orderTwo.setCreatedDate(date);
		orderOne.setAmount(new BigDecimal(ORDER_AMOUNT));
		orderTwo.setAmount(new BigDecimal(ORDER_AMOUNT));
		
		int returnedValue = PaymentsComparatorFactory.getOrderPaymentDateCompatator().compare(orderOne, orderTwo);
		assertEquals("Dates and amounts should match.", 0, returnedValue);
		
		orderTwo.setCreatedDate(new Date(System.currentTimeMillis() + TWO_SECONDS));
		returnedValue = PaymentsComparatorFactory.getOrderPaymentDateCompatator().compare(orderOne, orderTwo);
		assertEquals("Later date for second order payment should result in 1.", 1, returnedValue);
		
		orderOne.setCreatedDate(new Date(System.currentTimeMillis() + FOUR_SECONDS));
		returnedValue = PaymentsComparatorFactory.getOrderPaymentDateCompatator().compare(orderOne, orderTwo);
		assertEquals("Later date for second order payment should result in -1.", -1, returnedValue);
	}
	
	
	/**
	 * Test that creates a PaymentSourceComparator, obtains it and compares
	 * a list two OrderPayments. Returns a value of 0 if the two payments are
	 * the same objects, 1 if the first payment is null, -1 if the second payment is null.
	 */
	@Test
	public void testPaymentSourceComparatorForCreditCards() {
		
		//Create two duplicate credit card order payments in two different
		//order payment objects.
		OrderPayment orderPayment = createCreditCardOrderPayment();
		
		OrderPayment orderPaymentTwo = createCreditCardOrderPayment();
		
		//Make sure the comparator returns one when the first order payment argument is null
		int returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(null, orderPaymentTwo);
		assertEquals("Null first order payment should return 1.", 1, returnedValue);
		
		//Make sure the comparator returns negative one when the second order payment argument is null
		returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(orderPayment, null);
		assertEquals("Null second order payment should return 1.", -1, returnedValue);
		
		//Make sure the comparator returns zero when the comparator has two arguments that are the same objects
		returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(orderPayment, orderPayment);
		assertEquals("Identical order payments should return 0.", 0, returnedValue);
		
		//Make sure the comparator returns zero when the two arguments have the same payment source (same credit card information)
		returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(orderPayment, orderPaymentTwo);
		assertEquals("Identical credit card source order payments should return 0.", 0, returnedValue);
		
		//Make sure that the comparator returns a number less than zero if the second order payment argument is lexicographically
		//less than the first order payment argument.
		orderPaymentTwo.setUnencryptedCardNumber(UNENCRYPTED_CARD_NUMBER_TWO);
		returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(orderPayment, orderPaymentTwo);
		assertThat("The first masked card number should have been greater than the second", returnedValue, lessThan(0));

		//Make sure that the comparator returns a number greater than zero if the second order payment argument is lexicographically 
		//greater than the first order payment argument.
		returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(orderPaymentTwo, orderPayment);
		assertThat("The first masked card number should be greater than the second", returnedValue, greaterThan(0));
	}

	/**
	 * Ensure correct ordering of different payment types.
	 */
	@Test
	public void ensureCorrectOrderingOfDifferentPaymentTypes() {
		OrderPayment captureGiftCertificateOrderPayment = createGiftCertificateOrderPaymentWithTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		OrderPayment creditCardOrderPayment = createThirdCreditCardOrderPayment();
		
		int returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator()
				.compare(captureGiftCertificateOrderPayment, creditCardOrderPayment);
		assertThat("Gift Certificate types should be greater than credit card", returnedValue, greaterThan(0));
		
		returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator()
				.compare(creditCardOrderPayment, captureGiftCertificateOrderPayment);
		assertThat("Gift Certificate types should be less than credit card", returnedValue, lessThan(0));
		
	}
	
	/**
	 * Ensure equal unknown payment type return zero.
	 */
	@Test
	public void ensureMatchingUnknownPaymentTypeReturnZero() {
		OrderPayment firstOrderPayment = createReturnAndExchangeOrderPayment();
		OrderPayment secondOrderPayment = createReturnAndExchangeOrderPayment();
		
		int returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(firstOrderPayment, secondOrderPayment);
		assertEquals("Identical unknown payment types should be equal", 0, returnedValue);	
	}
	
	/**
	 * Ensure non matching unknown payment type order correctly.
	 */
	@Test
	public void ensureNonMatchingUnknownPaymentTypeOrderCorrectly() {
		OrderPayment firstOrderPayment = createReturnAndExchangeOrderPayment();
		OrderPayment secondOrderPayment = createGiftCertificateOrderPaymentWithTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		
		int returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(firstOrderPayment, secondOrderPayment);
		assertThat("First payment of type return and exchange payment should be first.", returnedValue, greaterThan(0));
		
		returnedValue = PaymentsComparatorFactory.getPaymentSourceComparator().compare(secondOrderPayment, firstOrderPayment);
		assertThat("Last payment of type return and exchange payment should be last.", returnedValue, lessThan(0));
	}
	
	/**
	 * Test that inputs a list of OrderPayments that ensures the method obtains unique payments
	 * when there are duplicates present in the list. Also makes sure than duplicate payment sources
	 * are filtered correctly and finally, makes sure that certain types of transactions are excluded
	 * from the returned list or some types of transactions are the only ones included in the returned list.
	 */
	@Test
	public void testGetListOfUniqueCreditCardPayments() {
		OrderPayment orderPayment = createCreditCardOrderPayment();
		OrderPayment orderPaymentTwo = createSecondCreditCardOrderPayment();
		OrderPayment orderPaymentThree = createThirdCreditCardOrderPayment();
		OrderPayment orderPaymentFour = createPaypalExpressOrderPayment();
		
		//Create a set of order payments using the three order payments created previously
		Set<OrderPayment> orderPayments = new HashSet<OrderPayment>();
		orderPayments.add(orderPayment);
		orderPayments.add(orderPaymentTwo);
		orderPayments.add(orderPaymentThree);
		orderPayments.add(orderPaymentFour);
		
		//Ensure that the set of order payments filters out all transactions that are not "Capture" transactions and 
		//and filters out Paypal Express payment types.
		List<OrderPayment> pmts = 
			PaymentsComparatorFactory.getListOfUniquePayments(OrderPayment.CAPTURE_TRANSACTION, orderPayments, PaymentType.PAYPAL_EXPRESS);
		assertEquals(ONE, pmts.size());
		
		//Ensure that the set of order payments does not filter out any transaction type but still filters out
		//Paypal Express payment types.
		pmts = PaymentsComparatorFactory.getListOfUniquePayments(null, orderPayments, PaymentType.PAYPAL_EXPRESS);
		assertEquals(THREE, pmts.size());
		
		//Remove the second order payment from the set and replace it with a modified version which is a duplicate of the
		//first order payment, add this to the list. Ensure that the the transaction types are not filtered but the 
		//duplicate order payment is removed from the list that is returned
		orderPayments.remove(orderPaymentTwo);
		orderPayments.add(createCreditCardOrderPayment());
		pmts = PaymentsComparatorFactory.getListOfUniquePayments(null, orderPayments, PaymentType.PAYPAL_EXPRESS);
		assertEquals(TWO, pmts.size());
		
		//Ensure that the comparator filters out the duplicate payment source and also filters out any transactions that are not of 
		//of the "Authorization" type from the input set. This should return a list with one element contained in it.
		pmts = PaymentsComparatorFactory.getListOfUniquePayments(OrderPayment.AUTHORIZATION_TRANSACTION, orderPayments, PaymentType.PAYPAL_EXPRESS);
		assertEquals(ONE, pmts.size());
	}

	/**
	 * Ensure filtering with empty excludes returns an empty list.
	 */
	@Test
	public void ensureFilteringWithEmptyExcludesReturnsAnEmptyList() {
		OrderPayment orderPayment = createCreditCardOrderPayment();
		orderPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);

		List<OrderPayment> orderPayments = Arrays.asList(orderPayment);

		List<OrderPayment> payments = PaymentsComparatorFactory.getListOfUniquePayments(OrderPayment.CAPTURE_TRANSACTION, orderPayments);
		assertEquals(0, payments.size());
	}
	
	/**
	 * Ensure unique list of payment token payments.
	 */
	@Test
	public void ensureUniqueListOfPaymentTokenPayments() {
		OrderPayment captureOrderPayment = createPaymentTokenOrderPaymentWithTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		
		OrderPayment authOrderPayment = createPaymentTokenOrderPaymentWithTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);

		OrderPayment additionalAuthOrderPayment  = createPaymentTokenOrderPaymentWithTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		authOrderPayment.setDisplayValue("new-token-display-value");
		
		OrderPayment additionalCaptureOrderPayment = createPaymentTokenOrderPaymentWithTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		
		List<OrderPayment> orderPayments = Arrays.asList(captureOrderPayment, 
				authOrderPayment, additionalAuthOrderPayment, additionalCaptureOrderPayment);
		List<OrderPayment> uniquePayments = PaymentsComparatorFactory.getListOfUniquePayments(OrderPayment.CAPTURE_TRANSACTION, orderPayments);
		assertEquals(1, uniquePayments.size());
		OrderPayment filteredPayment = uniquePayments.get(0);
		assertEquals("Transaction type should match.", filteredPayment.getTransactionType(), OrderPayment.CAPTURE_TRANSACTION);
		assertEquals("Token display value should match.", filteredPayment.getDisplayValue(), TOKEN_DISPLAY_VALUE);
	}

	/**
	 * Ensure unique list of gift certificate payments.
	 */
	@Test
	public void ensureUniqueListOfGiftCertificatePayments() {
		OrderPayment captureOrderPayment = createGiftCertificateOrderPaymentWithTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		
		OrderPayment authOrderPayment = createGiftCertificateOrderPaymentWithTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);

		OrderPayment additionalAuthOrderPayment = createGiftCertificateOrderPaymentWithTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		
		OrderPayment additionalCaptureOrderPayment = createGiftCertificateOrderPaymentWithTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		
		List<OrderPayment> orderPayments = Arrays.asList(captureOrderPayment, authOrderPayment, additionalAuthOrderPayment, 
				additionalCaptureOrderPayment);
		List<OrderPayment> uniquePayments = PaymentsComparatorFactory.getListOfUniquePayments(OrderPayment.CAPTURE_TRANSACTION, orderPayments);
		assertEquals(1, uniquePayments.size());
		OrderPayment filteredPayment = uniquePayments.get(0);
		assertEquals("Transaction type should match.", filteredPayment.getTransactionType(), OrderPayment.CAPTURE_TRANSACTION);
		assertEquals("Gift certificate codes should match.", filteredPayment.getGiftCertificate().getGiftCertificateCode(), GIFT_CERTIFICATE_CODE);
	}
	

	private OrderPayment createPaymentTokenOrderPaymentWithTransactionType(final String transactionType) {
		OrderPayment captureOrderPayment = new OrderPaymentImpl();
		captureOrderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		captureOrderPayment.setTransactionType(transactionType);
		captureOrderPayment.setDisplayValue(TOKEN_DISPLAY_VALUE);
		return captureOrderPayment;
	}
	
	private OrderPayment createGiftCertificateOrderPaymentWithTransactionType(final String transactionType) {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.GIFT_CERTIFICATE);
		orderPayment.setTransactionType(transactionType);
		orderPayment.setGiftCertificate(createGiftCertificate());
		orderPayment.setStatus(OrderPaymentStatus.APPROVED);
		return orderPayment;
	}

	private GiftCertificate createGiftCertificate() {
		GiftCertificate giftCertificate = new GiftCertificateImpl();
		giftCertificate.setGiftCertificateCode(GIFT_CERTIFICATE_CODE);
		return giftCertificate;
	}
	
	private OrderPayment createCreditCardOrderPayment() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		orderPayment.setCardHolderName(CARD_HOLDER_NAME);
		orderPayment.setCardType(CARD_TYPE);
		orderPayment.setExpiryMonth(EXPIRY_MONTH);
		orderPayment.setExpiryYear(EXPIRY_YEAR);
		orderPayment.setCvv2Code(CARD_CVV2_CODE);
		orderPayment.setUnencryptedCardNumber(UNENCRYPTED_CARD_NUMBER);
		orderPayment.setStartDate(START_DATE);
		orderPayment.setIssueNumber(ISSUE_NUMBER);
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		orderPayment.setStatus(OrderPaymentStatus.APPROVED);
		return orderPayment;
	}

	private OrderPayment createSecondCreditCardOrderPayment() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		orderPayment.setCardHolderName(CARD_HOLDER_NAME_TWO);
		orderPayment.setCardType(CARD_TYPE_TWO);
		orderPayment.setExpiryMonth(EXPIRY_MONTH_TWO);
		orderPayment.setExpiryYear(EXPIRY_YEAR_TWO);
		orderPayment.setCvv2Code(CARD_CVV2_CODE_TWO);
		orderPayment.setUnencryptedCardNumber(UNENCRYPTED_CARD_NUMBER_TWO);
		orderPayment.setStartDate(START_DATE);
		orderPayment.setIssueNumber(ISSUE_NUMBER_TWO);
		orderPayment.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
		orderPayment.setStatus(OrderPaymentStatus.APPROVED);
		return orderPayment;
	}

	private OrderPayment createThirdCreditCardOrderPayment() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		orderPayment.setCardHolderName(CARD_HOLDER_NAME_THREE);
		orderPayment.setCardType(CARD_TYPE_THREE);
		orderPayment.setExpiryMonth(EXPIRY_MONTH_THREE);
		orderPayment.setExpiryYear(EXPIRY_YEAR_THREE);
		orderPayment.setCvv2Code(CARD_CVV2_CODE_THREE);
		orderPayment.setUnencryptedCardNumber(UNENCRYPTED_CARD_NUMBER_THREE);
		orderPayment.setStartDate(START_DATE);
		orderPayment.setIssueNumber(ISSUE_NUMBER_THREE);
		orderPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		orderPayment.setStatus(OrderPaymentStatus.APPROVED);
		return orderPayment;
	}
	

	private OrderPayment createPaypalExpressOrderPayment() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.PAYPAL_EXPRESS);
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		return orderPayment;
	}
	
	private OrderPayment createReturnAndExchangeOrderPayment() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.RETURN_AND_EXCHANGE);
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		return orderPayment;
	}
}
