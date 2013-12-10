package com.elasticpath.service.payment.gateway;

import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Returns the appropriate {@link PaymentMethodTransformer} for a given {@link PaymentMethod}.
 */
public interface PaymentMethodTransformerFactory {
	/**
	 * Returns the appropriate {@link PaymentMethodTransformer} for a given {@link PaymentMethod}.
	 * @param paymentMethod the {@link PaymentMethod}
	 * @return the {@link PaymentMethodTransformer}.
	 */
	PaymentMethodTransformer getTransformerInstance(PaymentMethod paymentMethod);
}
