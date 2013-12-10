package com.elasticpath.plugin.payment;

import java.util.Collection;
import java.util.Map;

import com.elasticpath.plugin.payment.capabilities.PaymentGatewayCapability;

/**
 * API for setting up an unconfigured payment gateway plugin.
 */
public interface PaymentGatewayPluginManagement {
	/**
	 * Provides access to payment gateway capabilities not exposed through the base API.
	 * @param capability the requested capability
	 * @param <T> a class or interface implementing {@link PaymentGatewayCapability}
	 * @return the capability requested, if available on this instance. Null otherwise.
	 */
	<T extends PaymentGatewayCapability> T getCapability(Class<T> capability);

	/**
	 * Get the type of this payment gateway - a discriminator to other plugin classes.
	 * 
	 * @return the discriminator value of this gateway
	 */
	String getPluginType();

	/**
	 * Sets the configurations of the payment gateway via map.
	 *
	 * @param configurations a map of configuration keys to values
	 */
	void setConfigurationValues(final Map<String, String> configurations);

	/**
	 * Gets the configuration parameter names.
	 *
	 * @return the configuration parameters
	 */
	Collection<String> getConfigurationParameters();

	/**
	 * Sets the certificate path prefix.
	 *
	 * @param certificatePathPrefix the certificate path prefix
	 */
	void setCertificatePathPrefix(String certificatePathPrefix);
}
