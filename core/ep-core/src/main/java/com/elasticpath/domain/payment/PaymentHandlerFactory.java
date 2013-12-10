/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Payment handler factory class for getting instance of {@link PaymentHandler}.
 */
public interface PaymentHandlerFactory extends EpDomain {

	/**
	 * @param paymentType the payment type
	 * @return PaymentHandler instance
	 */
	PaymentHandler getPaymentHandler(PaymentType paymentType);
}
