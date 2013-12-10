package com.elasticpath.service.order.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;

/**
 * Junit4 unit test for {@code OrderServiceImpl} which does not extend {@code ElasticPathTestCase}.
 */
public class OrderServiceImplJUnit4Test {
	/**
	 * Tests that if no payments are captured then the captured total is zero.
	 */
	@Test
	public void testCalculateTotalCapturedNoneCaptured() {
		OrderServiceImpl service = new OrderServiceImpl();
		
		// Setup an order with only an authorization transaction.
		Order order = new OrderImpl();
		OrderPayment authPayment = new OrderPaymentImpl();
		authPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		order.addOrderPayment(authPayment);
		
		BigDecimal capturedTotal = service.calculateTotalCaptured(order);
		assertEquals("Nothing captured", BigDecimal.ZERO, capturedTotal);
	}
	
	/**
	 * Tests that if no payments are authorised then the captured total is zero.
	 */
	@Test
	public void testCalculateTotalCapturedNoPayments() {
		OrderServiceImpl service = new OrderServiceImpl();
		
		// Setup an order with only an authorization transaction.
		Order order = new OrderImpl();
		
		BigDecimal capturedTotal = service.calculateTotalCaptured(order);
		assertEquals("Nothing captured", BigDecimal.ZERO, capturedTotal);
	}
	
	/**
	 * Tests that if one payments are captured then the captured total is the total
	 * of that payment.
	 */
	@Test
	public void testCalculateTotalCapturedOneCaptured() {
		OrderServiceImpl service = new OrderServiceImpl();
		
		Order order = new OrderImpl();
		OrderPayment authPayment = new OrderPaymentImpl();
		authPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		order.addOrderPayment(authPayment);
		
		OrderPayment capturePayment = new OrderPaymentImpl();
		capturePayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		capturePayment.setAmount(new BigDecimal("12.34"));
		order.addOrderPayment(capturePayment);
		
		BigDecimal capturedTotal = service.calculateTotalCaptured(order);
		assertEquals("The total of one capture", new BigDecimal("12.34"), capturedTotal);
	}
	
	/**
	 * Tests that if two payments are captured then the captured total is the total
	 * of that payment.
	 */
	@Test
	public void testCalculateTotalCapturedTwoCaptured() {
		OrderServiceImpl service = new OrderServiceImpl();
				
		Order order = new OrderImpl();
		OrderPayment authPayment = new OrderPaymentImpl();
		authPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		order.addOrderPayment(authPayment);
		
		OrderPayment capturePayment = new OrderPaymentImpl();
		capturePayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		capturePayment.setAmount(new BigDecimal("12.34"));
		order.addOrderPayment(capturePayment);
		
		OrderPayment authPayment2 = new OrderPaymentImpl();
		authPayment2.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		order.addOrderPayment(authPayment2);
		
		OrderPayment capturePayment2 = new OrderPaymentImpl();
		capturePayment2.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		capturePayment2.setAmount(new BigDecimal("56.78"));
		order.addOrderPayment(capturePayment2);
		
		BigDecimal capturedTotal = service.calculateTotalCaptured(order);
		assertEquals("Two capture transactions", new BigDecimal("69.12"), capturedTotal);
	}
}
