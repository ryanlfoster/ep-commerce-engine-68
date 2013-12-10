package com.elasticpath.service.payment.gateway.impl;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpDateBindException;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.impl.NewInstanceCustomerCreditCardBuilder;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.test.BeanFactoryExpectationsFactory;



/**
 * Test for {@link CustomerCreditCardTransformer}.
 */
public class CustomerCreditCardTransformerTest {
	private static final int TEST_ISSUE_NUMBER = 11;

	private static final String MASKED_NUMBER = "************1111";

	private static final String CARD_NUMBER = "4111111111111111";

	private CustomerCreditCardTransformer customerCreditCardTransformer;
	
	/** The context. */
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	
	/**
	 * Set up and mocking.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ORDER_PAYMENT,
				new OrderPaymentImpl() {
			private static final long serialVersionUID = -7638043786071701838L;
			
		});
		
		final CreditCardEncrypter cardEncrypter = context.mock(CreditCardEncrypter.class);
		context.checking(new Expectations() { {
			allowing(cardEncrypter).decrypt(with(any(String.class)));
			will(returnValue(CARD_NUMBER));
			allowing(cardEncrypter).encrypt(with(any(String.class)));
			will(returnValue(CARD_NUMBER));
			allowing(cardEncrypter).mask(with(any(String.class)));
			will(returnValue(MASKED_NUMBER));
		} });
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CREDIT_CARD_ENCRYPTER, cardEncrypter);
		
		customerCreditCardTransformer = new CustomerCreditCardTransformer();
		customerCreditCardTransformer.setBeanFactory(beanFactory);
	}


	/**
	 * Clean up the bean factory.
	 */
	@After
	public void tearDown() {
		expectationsFactory.close();
	}
	
	/**
	 * Test transform to order payment.
	 */
	@Test
	public void testTransformToOrderPayment()  {
		String startYear = "2012";
		String startMonth = "12";
		CustomerCreditCard card = new NewInstanceCustomerCreditCardBuilder()
			.withCardHolderName("Card Name")
			.withCardNumber(CARD_NUMBER)
			.withCardType("VISA")
			.withExpiryMonth(startMonth)
			.withExpiryYear("2100")
			.withStartMonth(startMonth)
			.withStartYear(startYear)
			.withIssueNumber(TEST_ISSUE_NUMBER)
			.withSecurityCode("789")
			.build();
		
		final Date expectedStartDate = createStartDate(startMonth, startYear);
		
		PaymentMethod method = card;
		OrderPayment orderPayment = customerCreditCardTransformer.transformToOrderPayment(method);
		
		assertEquals("Payment method should be " + PaymentType.CREDITCARD, PaymentType.CREDITCARD, orderPayment.getPaymentMethod());
		assertEquals("Card holder name on OrderPayment should match card. ", card.getCardHolderName(), orderPayment.getCardHolderName());
		assertEquals("Card holder number on OrderPayment should match card. ", card.getCardNumber(), orderPayment.getCardNumber());
		assertEquals("Card type on OrderPayment should match card.  ", card.getCardType(), orderPayment.getCardType());
		assertEquals("Expiry month on OrderPayment should match card. ", card.getExpiryMonth(), orderPayment.getExpiryMonth());
		assertEquals("Expiry year on OrderPayment should match card.  ", card.getExpiryYear(), orderPayment.getExpiryYear());
		assertEquals("Start date on OrderPayment should be " + expectedStartDate, expectedStartDate, orderPayment.getStartDate());
		assertEquals("Issue number on OrderPayment should match card.", String.valueOf(card.getIssueNumber()), orderPayment.getIssueNumber());
		assertEquals("Security code on OrderPayment should match card.", card.getSecurityCode(), orderPayment.getCvv2Code());
	}

	private Date createStartDate(final String month, final String year) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MMyyyy", Locale.getDefault());
			return sdf.parse(month + year);
		} catch (ParseException pe) {
			throw new EpDateBindException("Invalid month or year strings. Month:" + month + " Year:" + year, pe);
		}
	}
	
}
