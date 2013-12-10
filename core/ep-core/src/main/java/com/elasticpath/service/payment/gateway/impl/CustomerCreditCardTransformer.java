package com.elasticpath.service.payment.gateway.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformer;

/**
 * Transformer for {@link CustomerCreditCard}. 
 */
public class CustomerCreditCardTransformer implements PaymentMethodTransformer {
	private BeanFactory beanFactory;
	
	@Override
	public OrderPayment transformToOrderPayment(final PaymentMethod paymentMethod) {
		CustomerCreditCard creditCard = (CustomerCreditCard) paymentMethod;
		OrderPayment orderPayment = beanFactory.getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.useCreditCard(creditCard);
		return orderPayment;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
