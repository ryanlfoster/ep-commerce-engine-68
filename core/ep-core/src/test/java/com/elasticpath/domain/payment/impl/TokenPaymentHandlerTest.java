package com.elasticpath.domain.payment.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.impl.ElectronicOrderShipmentImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.payment.PaymentServiceException;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for {@link TokenPaymentHandler}.
 */
public class TokenPaymentHandlerTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private TokenPaymentHandler tokenPaymentHandler;
	/**
	 * Mock out bean factory and set up object under test.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ORDER_PAYMENT,
				new OrderPaymentImpl() {
					private static final long serialVersionUID = -7638043786071701838L;

		});
		
		tokenPaymentHandler = new TokenPaymentHandler();
	}

	/**
	 * Clean up expectations factory.
	 */
	@After
	public void tearDown() {
		expectationsFactory.close();
	}
	
	/**
	 * Ensure auth order payment is correctly created from template order payment.
	 */
	@Test
	public void ensureAuthOrderPaymentIsCorrectlyCreatedFromTemplateOrderPayment() {
		OrderShipment orderShipment = createOrderShipment();
		OrderPayment templateOrderPayment = createTemplateOrderPayment();
		BigDecimal amount = BigDecimal.TEN;
		
		OrderPayment authOrderPayment = tokenPaymentHandler.createAuthOrderPayment(orderShipment, templateOrderPayment, amount);
		assertEquals("Payment method should be: " + PaymentType.PAYMENT_TOKEN, PaymentType.PAYMENT_TOKEN, 
				authOrderPayment.getPaymentMethod());
		assertEquals("Amount should match the template.", amount, authOrderPayment.getAmount());
		assertEquals("Currency should match the template." , orderShipment.getShipmentNumber(), authOrderPayment.getReferenceId());
		assertEquals("Reference ID should match the shipment number." , templateOrderPayment.getCurrencyCode(), 
				authOrderPayment.getCurrencyCode());
		assertEquals("Transaction type should be: " + OrderPayment.AUTHORIZATION_TRANSACTION, OrderPayment.AUTHORIZATION_TRANSACTION,
				authOrderPayment.getTransactionType());
		assertEquals("Order Shipment should be set.", orderShipment, authOrderPayment.getOrderShipment());
		assertEquals("Token field should match the template.",
				templateOrderPayment.extractPaymentToken().getValue(), authOrderPayment.extractPaymentToken().getValue());
		assertEquals("Token display value field should match the template.", templateOrderPayment.extractPaymentToken().getDisplayValue(),
				authOrderPayment.extractPaymentToken().getDisplayValue());
		assertEquals("IP address should match the order's ip address.", orderShipment.getOrder().getIpAddress(), 
				authOrderPayment.getIpAddress());
		assertEquals("Email should match the template's .", templateOrderPayment.getEmail(), 
				authOrderPayment.getEmail());
		assertNotNull("Date should be set", authOrderPayment.getCreatedDate());
	}

	/**
	 * Ensure getPreAuthorizedPayments throws exception if amount is zero or below.
	 */
	@Test
	public void ensureGetPreAuthorizedPaymentsThrowsExceptionIfAmountIsZeroOrBelow() {
		BigDecimal amount = BigDecimal.ZERO;
		exception.expect(PaymentServiceException.class);
		exception.expectMessage("Can not create");
		exception.expectMessage("less than or equal to 0");
		tokenPaymentHandler.getPreAuthorizedPayments(null, null, amount);
	}
	
	/**
	 * Ensure payment type is payment token.
	 */
	@Test
	public void ensureHandlerPaymentTypeIsPaymentToken() {
		assertEquals(PaymentType.PAYMENT_TOKEN, tokenPaymentHandler.getPaymentType());
	}
	
	private OrderShipment createOrderShipment() {
		OrderShipment orderShipment = new ElectronicOrderShipmentImpl();
		orderShipment.setShipmentNumber("test shipment");
		Order order = new OrderImpl();
		order.setIpAddress("test ip Address");
		orderShipment.setOrder(order);
		return orderShipment;
	}

	private OrderPayment createTemplateOrderPayment() {
		OrderPayment templateOrderPayment = new OrderPaymentImpl();
		PaymentToken token = new PaymentTokenImpl.TokenBuilder()
				.withValue("test token")
				.withDisplayValue("test token display value")
				.build();
		templateOrderPayment.usePaymentToken(token);
		templateOrderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		String expectedCurrencyCode = Currency.getInstance(Locale.CANADA).getCurrencyCode();
		templateOrderPayment.setCurrencyCode(expectedCurrencyCode);
		templateOrderPayment.setEmail("test@ep.com");
		return templateOrderPayment;
	}

}
