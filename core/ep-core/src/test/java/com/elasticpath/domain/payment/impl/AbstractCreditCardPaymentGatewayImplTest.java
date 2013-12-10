/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.payment.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Test cases for <code>AbstractCreditCardPaymentGatewayImpl</code>.
 */
public class AbstractCreditCardPaymentGatewayImplTest {
	private AbstractCreditCardPaymentGatewayImpl gatewayImpl;

	@Before
	public void setUp() throws Exception {
		gatewayImpl = new AbstractCreditCardPaymentGatewayImpl() {
			private static final long serialVersionUID = 8239095928673034744L;

			/**
			 * Get the type of this payment gateway.
			 * 
			 * @return the payment type
			 */
			public PaymentType getPaymentType() {
				return PaymentType.CREDITCARD;
			}

			/**
			 * Pre-authorize a payment.
			 * 
			 * @param payment the payment to be preauthorized
			 */
			public void preAuthorize(final OrderPayment payment, final Address billingAddress) {
				// Tested in subclass test cases
			}

			/**
			 * Captures a payment on a previously authorized card.
			 * 
			 * @param payment the payment to be captured
			 */
			public void capture(final OrderPayment payment) {
				// Tested in subclass test cases
			}

			/**
			 * Make a payment without pre-authorization.
			 * 
			 * @param orderPayment the payment to be preauthorized
			 * @param billingAddress the name and address of the person being billed
			 */
			public void sale(final OrderPayment orderPayment, final Address billingAddress) {
				// Tested in subclass test cases
			}

			/**
			 * Void a previous authorization or capture.
			 * 
			 * @param payment the payment to be voided
			 */
			public void voidCaptureOrCredit(final OrderPayment payment) {
				// Tested in subclass test cases
			}

			/**
			 * Reverse a previous pre-authorization. This can only be executed on Visas using the
			 * "Vital" processor and authorizations cannot be reversed using the test server and
			 * card info because the auth codes are not valid (Cybersource).
			 * 
			 * @param payment the payment that was previously pre-authorized
			 * @return a result code from <code>TransactionResult</code>.
			 */
			public void reversePreAuthorization(final OrderPayment payment) {
				// Tested in subclass test cases
			}

			/**
			 * Gets the list of default property keys for a payment gateway.
			 * 
			 * @return the list of default property keys for a payment gateway
			 */
			protected Set<String> getDefaultPropertyKeys() {
				// Tested in subclass test cases
				return null;
			}
			
			/**
			 * Return the type of this payment gateway - accessor for the discriminator value.
			 * @return the type of this gateway
			 */
			public String getType() {
				return "paymentGatewayTest";
			}

		};
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.impl.AbstractPaymentGatewayImpl.getSupportedCardTypes()'.
	 */
	@Test
	public void testGetSetSupportedCardTypes() {
		List<String> cardTypeList = new ArrayList<String>();
		cardTypeList.add("VISA");
		cardTypeList.add("MasterCard");
		gatewayImpl.setSupportedCardTypes(cardTypeList);
		assertEquals(cardTypeList, gatewayImpl.getSupportedCardTypes());
	}

	/**
	 * Test for validateCvv2.
	 */
	@Test
	public void testValidateCvv2() {
		gatewayImpl.setValidateCvv2(false);
		assertFalse(gatewayImpl.isCvv2ValidationEnabled());
	}
}
