/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.integration.checkout;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.test.integration.checkout.OrderPaymentMatcher.OrderPaymentMatcherBuilder;

public class OrderPaymentMatcherTest {
	private OrderPaymentMatcher matcher;
	
	@Before
	public void setUp() {
		OrderPaymentMatcherBuilder builder = OrderPaymentMatcher.builder();
		matcher = builder.withStatus(OrderPaymentStatus.APPROVED)
				.withTransaction(OrderPayment.AUTHORIZATION_TRANSACTION)											 
				.withType(PaymentType.PAYMENT_TOKEN)
				.build();
	}
	
	@Test
	public void ensureMatchingPaymentReturnsTrue() {
		OrderPayment matchingOrderPayment = new OrderPaymentImpl();
		matchingOrderPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		matchingOrderPayment.setStatus(OrderPaymentStatus.APPROVED);
		matchingOrderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);			
		assertTrue("Matcher should match OrderPayment", matcher.matches(matchingOrderPayment));
	}

	@Test
	public void ensureWrongStatusReturnsFalse() {	
		OrderPayment orderPaymentWithWrongStatus = new OrderPaymentImpl();
		orderPaymentWithWrongStatus.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		orderPaymentWithWrongStatus.setStatus(OrderPaymentStatus.FAILED);
		orderPaymentWithWrongStatus.setPaymentMethod(PaymentType.PAYMENT_TOKEN);		
		assertFalse("Matcher should not match status", matcher.matches(orderPaymentWithWrongStatus));
	}
	
	@Test
	public void ensureWrongTransactionReturnsFalse() {		
		OrderPayment orderPaymentWithWrongTransactionType = new OrderPaymentImpl();
		orderPaymentWithWrongTransactionType.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		orderPaymentWithWrongTransactionType.setStatus(OrderPaymentStatus.APPROVED);
		orderPaymentWithWrongTransactionType.setPaymentMethod(PaymentType.PAYMENT_TOKEN);		
		assertFalse("Matcher should not match transaction type", matcher.matches(orderPaymentWithWrongTransactionType));		
	}
	
	@Test
	public void ensureWrongPaymentReturnsFalse() {		
		OrderPayment orderPaymentWithWrongPaymentType = new OrderPaymentImpl();
		orderPaymentWithWrongPaymentType.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		orderPaymentWithWrongPaymentType.setStatus(OrderPaymentStatus.APPROVED);
		orderPaymentWithWrongPaymentType.setPaymentMethod(PaymentType.CREDITCARD);		
		assertFalse("Matcher should not match payment", matcher.matches(orderPaymentWithWrongPaymentType));		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ensureMissingFieldThrowsCorrectException() {
		OrderPaymentMatcherBuilder builder = OrderPaymentMatcher.builder();
		matcher = builder.withStatus(OrderPaymentStatus.APPROVED)
				.withTransaction(OrderPayment.AUTHORIZATION_TRANSACTION)											 
				.build();		
	}
	
	@Test
	public void ensureMissingFieldExceptionHasMeaningfulMessage() {
		OrderPaymentMatcherBuilder builder = OrderPaymentMatcher.builder();
		
		try {
			matcher = builder.build();
			fail("Exception should be thrown on missing parameters.");
		} catch (IllegalArgumentException e) {
			assertExceptionErrorMessagesHasMissingFieldnames(e);
		}		
	}

	private void assertExceptionErrorMessagesHasMissingFieldnames(IllegalArgumentException e) {
		String error = e.getMessage();
		List<Field> matcherFields = Arrays.asList(matcher.getClass().getDeclaredFields());
		for (Field field: matcherFields) {
			assertThat(error, Matchers.containsString(field.getName()));
		}
	}

}
