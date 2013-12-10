package com.elasticpath.service.payment.gateway.impl;

import java.util.Map;

import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformer;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformerFactory;

/**
 * Default implementation of {@link PaymentMethodTransformerFactory}.
 * Uses constructor injection.
 */
public class PaymentMethodTransformerFactoryImpl implements PaymentMethodTransformerFactory {

	private final Map<Class<? extends PaymentMethod>, PaymentMethodTransformer> transformerMap;
	
	/**
	 * Instantiates a {@link PaymentMethodTransformerFactory}.
	 *
	 * @param transformerMap the map between {@link PaymentMethod} classes and transformers.
	 */
	public PaymentMethodTransformerFactoryImpl(final Map<Class<? extends PaymentMethod>, PaymentMethodTransformer> transformerMap) {
		this.transformerMap = transformerMap;
	}
	
	@Override
	public PaymentMethodTransformer getTransformerInstance(final PaymentMethod paymentMethod) {
		for (Class<? extends PaymentMethod> paymentMethodClass : transformerMap.keySet()) {
			if (paymentMethodClass.isAssignableFrom(paymentMethod.getClass())) {
				return transformerMap.get(paymentMethodClass);
			}
		}
		
		throw new IllegalArgumentException("No transformer found for " + paymentMethod.getClass());
	}
}
