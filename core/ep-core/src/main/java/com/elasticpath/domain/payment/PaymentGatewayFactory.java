package com.elasticpath.domain.payment;

import java.util.Map;

import com.elasticpath.plugin.payment.PaymentGatewayPluginInvoker;
import com.elasticpath.plugin.payment.PaymentGatewayPluginManagement;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * The factory for handling <code>PaymentGateway</code>s.
 */
public interface PaymentGatewayFactory {

	/**
	 * Create an return a {@link PaymentGateway} with the specified plugin type.
	 *
	 * @param pluginType the plugin type
	 * @return a prototype of a payment gateway with the specified plugin type
	 */
	PaymentGateway getPaymentGateway(String pluginType);

	/**
	 * Creates a new PaymentGateway plugin object.
	 * 
	 * @param pluginType the plugin type
	 * @return the payment gateway plugin
	 */
	PaymentGatewayPluginManagement getPluginGatewayPluginManagement(String pluginType);

	/**
	 * Gets the available gateway plugin classes.
	 * 
	 * @return the available gateway plugin classes
	 */
	Map<String, Class<? extends PaymentGatewayPluginManagement>> getAvailableGatewayPlugins();

	/**
	 * Create a new instance of the specified gateway plugin configured with the given properties.
	 * 
	 * @param pluginType the plugin type
	 * @param properties the properties to use when configuring the plugin
	 * @return the configured payment gateway plugin
	 */
	PaymentGatewayPluginInvoker createConfiguredPaymentGatewayPluginInstance(String pluginType, Map<String, PaymentGatewayProperty> properties);

	/**
	 * Returns the {@link PaymentType} supported by a specific plugin.
	 * @param pluginType the plugin type
	 * @return the supported {@link PaymentType}
	 */
	PaymentType getPaymentTypeForPlugin(String pluginType);

}
