/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.domain.cartorder;

import com.elasticpath.persistence.api.Entity;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * A CartOrder contains information that is required by the workflow that a customer goes through on the way to creating an order.
 * CartOrder should not be used in versions of EP prior to 6.4.
 */
public interface CartOrder extends Entity {
	
	/**
	 * @return The billing address GUID.
	 */
	String getBillingAddressGuid();
	
	/**
	 * @param guid The billing address GUID.
	 */
	void setBillingAddressGuid(String guid);
	
	/**
	 * @return The shopping cart GUID.
	 */
	String getShoppingCartGuid();
	
	/**
	 * @param guid The shopping cart GUID.
	 * @throws IllegalArgumentException If the given guid is null.
	 */
	void setShoppingCartGuid(String guid);
	
	/**
	 * @return The shipping address GUID.
	 */
	String getShippingAddressGuid();

	/**
	 * @param shippingAddressGuid The shipping address GUID.
	 */
	void setShippingAddressGuid(String shippingAddressGuid);

	/**
	 * @return The shipping service level GUID.
	 */
	String getShippingServiceLevelGuid();

	/**
	 * @param shippingServiceLevelGuid The shipping service level GUID.
	 */
	void setShippingServiceLevelGuid(String shippingServiceLevelGuid);

	/**
	 * Gets the payment method.
	 * @return the payment method
	 */
	PaymentMethod getPaymentMethod();

	/**
	 * Sets the payment method.
	 * @param paymentMethod the payment method
	 */
	void setPaymentMethod(PaymentMethod paymentMethod);

	/**
	 * Gets the payment method guid.
	 *
	 * @return the payment method guid
	 */
	String getPaymentMethodGuid();

	/**
	 * Sets the payment method guid.
	 *
	 * @param paymentMethodGuid the new payment method guid
	 */
	void setPaymentMethodGuid(String paymentMethodGuid);
}
