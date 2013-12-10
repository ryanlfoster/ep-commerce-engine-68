/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment.impl;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.payment.PaymentHandler;
import com.elasticpath.domain.payment.PaymentHandlerFactory;
import com.elasticpath.plugin.payment.PaymentType;


/**
 * Payment handler factory class for getting instance of {@link PaymentHandler}.
 */
public class PaymentHandlerFactoryImpl extends AbstractEpDomainImpl implements PaymentHandlerFactory {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 *
	 * @param paymentType the payment type
	 * @return PaymentHandler instance
	 */
	public PaymentHandler getPaymentHandler(final PaymentType paymentType) {
		PaymentHandler handler;
		if (paymentType == null) {
			return null;
		}
		switch (paymentType) {
			case CREDITCARD :
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_CREDITCARD);
				break;
			case PAYMENT_TOKEN :
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_TOKEN);
				break;
			case PAYPAL_EXPRESS :
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_PAYPAL);
				break;
			case GOOGLE_CHECKOUT :
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_GOOGLE);
				break;
			case RETURN_AND_EXCHANGE :
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_EXCHANGE);
				break;
			case GIFT_CERTIFICATE:
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_GIFTCERTIFICATE);
				break;
				default: 
					throw new IllegalArgumentException("Payment handler id is not valid");
		}
		return handler;
	}

}
