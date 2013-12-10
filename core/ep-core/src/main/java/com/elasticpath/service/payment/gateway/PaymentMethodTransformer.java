package com.elasticpath.service.payment.gateway;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Transforms a {@link PaymentMethod} to a {@link OrderPayment}.
 */
public interface PaymentMethodTransformer {
	
	/**
	 * Transform the submitted {@link PaymentMethod} to a {@link OrderPayment}.
	 * @param paymentMethod the {@link PaymentMethod}
	 * @return the {@link OrderPayment}.
	 */
	OrderPayment transformToOrderPayment(PaymentMethod paymentMethod);
}
