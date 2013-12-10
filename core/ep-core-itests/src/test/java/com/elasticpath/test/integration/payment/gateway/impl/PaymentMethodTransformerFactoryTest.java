package com.elasticpath.test.integration.payment.gateway.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.customer.impl.AbstractPaymentMethodImpl;
import com.elasticpath.domain.customer.impl.CustomerCreditCardImpl;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformer;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformerFactory;
import com.elasticpath.test.integration.BasicSpringContextTest;

/**
 Integration test for {@link PaymentMethodTransformerFactory}.
 */
public class PaymentMethodTransformerFactoryTest extends BasicSpringContextTest {
	@Autowired
	private PaymentMethodTransformerFactory paymentMethodTransformerFactory;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * Test factory for customer credit card.
	 */
	@Test
	public void testFactoryForCustomerCreditCard() {
		PaymentMethod paymentMethod = new CustomerCreditCardImpl();
		PaymentMethodTransformer transformer = paymentMethodTransformerFactory.getTransformerInstance(paymentMethod);
		assertNotNull(transformer);
	}

	/**
	 * Test factory for payment token.
	 */
	@Test
	public void testFactoryForPaymentToken() {
		PaymentMethod paymentMethod = new PaymentTokenImpl.TokenBuilder().build();
		PaymentMethodTransformer transformer = paymentMethodTransformerFactory.getTransformerInstance(paymentMethod);
		assertNotNull(transformer);
	}
	
	/**
	 * Test factory for non existent payment method.
	 */
	@Test
	public void testFactoryForNonExistentPaymentMethod() {
		PaymentMethod paymentMethod = new UnimplementedPaymentMethod();
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("UnimplementedPaymentMethod");
		exception.expectMessage("No transformer found");
		paymentMethodTransformerFactory.getTransformerInstance(paymentMethod);
	}
	
	class UnimplementedPaymentMethod extends AbstractPaymentMethodImpl  {
		private static final long serialVersionUID = 1L;
	}

}
