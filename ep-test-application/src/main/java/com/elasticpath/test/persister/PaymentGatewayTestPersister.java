package com.elasticpath.test.persister;

import java.util.Properties;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * Persister for persisting {@link PaymentGateway}s and associated properties.
 */
public class PaymentGatewayTestPersister {

	private final PaymentGatewayService paymentGatewayService;

	/**
	 * Constructor initializes necessary services and beanFactory.
	 *
	 * @param beanFactory Elastic Path factory for creating instances of beans.
	 */
	public PaymentGatewayTestPersister(final BeanFactory beanFactory) {
		paymentGatewayService = beanFactory.getBean(ContextIdNames.PAYMENT_GATEWAY_SERVICE);
	}

	/**
	 * Creates persisted payment gateway.
	 *
	 * @param name the name of the payment gateway
	 * @param type the type of the payment gateway
	 * @param properties the properties of the payment gateway
	 */
	public void persistPaymentGateway(final String name, final String type, final Properties properties) {
		paymentGatewayService.addPaymentGateway(name, type, properties);
	}
}
