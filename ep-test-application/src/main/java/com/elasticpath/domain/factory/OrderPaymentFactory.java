/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.domain.factory;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.PaymentType;

public class OrderPaymentFactory {
	@Autowired
	private BeanFactory beanFactory;
	
	public OrderPayment createTemplateTokenizedOrderPayment() {
		return createTemplateTokenizedOrderPaymentWithToken("test-token");
	}
	
	public OrderPayment createTemplateTokenizedOrderPaymentWithToken(final String token) {
		OrderPayment orderPayment = beanFactory.getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.setCreatedDate(new Date());
		orderPayment.setCurrencyCode("USD");
		orderPayment.setEmail("test@ep.com");
		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		PaymentToken paymentToken = new PaymentTokenImpl.TokenBuilder()
				.withValue(token)
				.withDisplayValue("Display value: " + token)
				.build();
		orderPayment.usePaymentToken(paymentToken);
		return orderPayment;
	}
}
