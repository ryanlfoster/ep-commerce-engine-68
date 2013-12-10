/*
 * 
 */
package com.elasticpath.service.payment.gateway.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformer;

/**
 * Transformer for {@link PaymentToken}.
 */
public class PaymentTokenTransformer implements PaymentMethodTransformer {
    private BeanFactory beanFactory;
	
	@Override
	public OrderPayment transformToOrderPayment(final PaymentMethod paymentMethod) {
		PaymentToken token = (PaymentToken) paymentMethod;
		OrderPayment orderPayment = beanFactory.getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.usePaymentToken(token);
		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		return orderPayment;
	}
	
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
