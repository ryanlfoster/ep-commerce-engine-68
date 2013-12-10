package com.elasticpath.plugin.payment;

import com.elasticpath.plugin.payment.capabilities.PaymentGatewayCapability;

/**
 * API for invoking payment gateway calls via a configured payment gateway plugin.
 */
public interface PaymentGatewayPluginInvoker {
	/**
	 * Provides access to payment gateway capabilities not exposed through the base API.
	 * @param capability the requested capability
	 * @param <T> a class or interface implementing {@link PaymentGatewayCapability}
	 * @return the capability requested, if available on this instance. Null otherwise.
	 */
	<T extends PaymentGatewayCapability> T getCapability(Class<T> capability);

	/**
	 * Get the method type of this payment gateway. e.g. CREDIT CARD, Paypal, Debit ...
	 * 
	 * @return the payment type
	 */
	PaymentType getPaymentType();

	/**
	 * Determines if this {@link PaymentGatewayPluginInvoker} is resolved.
	 * 
	 * @return boolean value representing whether the plugin is resolved.
	 */
	boolean isResolved();
}
