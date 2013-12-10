package com.elasticpath.service.payment.gateway.impl;


import static org.junit.Assert.assertEquals;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for {@link PaymentTokenTransformer}.
 */
public class PaymentTokenTransformerTest {

	private PaymentTokenTransformer paymentTokenTransformer;
	
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
		paymentTokenTransformer = new PaymentTokenTransformer();
		paymentTokenTransformer.setBeanFactory(beanFactory);
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
		PaymentToken token = new PaymentTokenImpl.TokenBuilder()
			.withValue("test value")
			.withDisplayValue("display value")
			.build();
		
		PaymentMethod method = token;
		OrderPayment orderPayment = paymentTokenTransformer.transformToOrderPayment(method);
		
		assertEquals("Token created from OrderPayment should be: " + token,
				token, orderPayment.extractPaymentToken());
		assertEquals("Payment method should be " + PaymentType.PAYMENT_TOKEN, PaymentType.PAYMENT_TOKEN, orderPayment.getPaymentMethod());
	}


}
