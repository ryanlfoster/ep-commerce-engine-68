/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.payment;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.plugin.payment.PaymentGatewayPluginInvoker;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.transaction.service.PaymentGatewayTransactionService;

/**
 * Represents a payment processing gateway such as Verisign or Cybersource.
 */
public interface PaymentGateway extends Persistable {

	/**
	 * Get the type of this payment gateway.
	 * 
	 * @return the payment type
	 */
	PaymentType getPaymentType();
	
	/**
	 * Get the type of this payment gateway - accessor for the discriminator value.
	 * 
	 * @return the discriminator value of this gateway
	 */
	String getType();

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	void setType(String type);

	/**
	 * Get the name of the payment gateway (e.g. CyberSource).
	 * 
	 * @return the gateway name
	 */
	String getName();

	/**
	 * Set the name of the payment gateway (e.g. CyberSource).
	 * 
	 * @param name the gateway name
	 */
	void setName(final String name);

	/**
	 * Get the properties map of the payment gateway (e.g. merchantID, keysDirectory).
	 * 
	 * @return the gateway properties map
	 */
	Map<String, PaymentGatewayProperty> getPropertiesMap();

	/**
	 * Set the properties map of the payment gateway (e.g. merchantID, keysDirectory).
	 * 
	 * @param properties the gateway properties map
	 */
	void setPropertiesMap(final Map<String, PaymentGatewayProperty> properties);

	/**
	 * Get the currencies supported by this payment gateway.
	 * 
	 * @return a List of currency code strings (e.g. CAD)
	 */
	List<String> getSupportedCurrencies();

	/**
	 * Set the currencies supported by this payment gateway.
	 * 
	 * @param currencies a List of currency code strings (e.g. CAD)
	 */
	void setSupportedCurrencies(List<String> currencies);

	/**
	 * Pre-authorize a payment.
	 * 
	 * @param payment the payment to be preauthorized
	 * @param billingAddress the name and address of the person being billed
	 */
	void preAuthorize(final OrderPayment payment, final Address billingAddress);

	/**
	 * Captures a payment on a previously authorized card.
	 * 
	 * @param payment the payment to be captured
	 */
	void capture(final OrderPayment payment);

	/**
	 * Marks a transaction for immediate fund transfer without any pre-authorization. Note that
	 * Visa and Mastercard regulations prohibit capturing CC transaction funds until a product or
	 * service has been shipped to the buyer.
	 * 
	 * @param payment the payment to be immediately processed
	 * @param billingAddress the name and address of the person being billed
	 */
	void sale(final OrderPayment payment, final Address billingAddress);

	/**
	 * Void a previous capture or credit. Can usually only be executed on the same day of the
	 * original transaction.
	 * 
	 * @param payment the payment to be voided
	 */
	void voidCaptureOrCredit(final OrderPayment payment);

	/**
	 * Reverse a previous pre-authorization. This can only be executed on Visas using the "Vital"
	 * processor and authorizations cannot be reversed using the test server and card info because
	 * the auth codes are not valid (Cybersource).
	 * 
	 * @param payment the payment that was previously pre-authorized
	 */
	void reversePreAuthorization(final OrderPayment payment);

	/**
	 * Refunds a previous capture or refunds to a stand-alone transaction.
	 * 
	 * There are two type of refunds:
	 * - stand-alone - no previous capture is needed
	 * - follow-up - refunds towards a past capture
	 * 
	 * @param payment the payment to be refunded
	 * @param billingAddress the billing address if the refund is of stand-alone type or null otherwise
	 */
	void refund(final OrderPayment payment, Address billingAddress);

	/**
	 * Builds a properties object from the properties map. One difference from this and the
	 * properties map is that this will be a direct <code>String</code> -> <code>String</code>
	 * relationship. Changes in this object will not be reflected within the original properties
	 * map.
	 * 
	 * @return A clone of the properties map in <code>String</code> -> <code>String</code> format
	 */
	Properties buildProperties();

	/**
	 * Merges the given properties with the existing properties map by adding each property to the
	 * property map. Each key and value will be casted to String via their <code>toString()</code>
	 * method.
	 * 
	 * @param properties a properties object
	 */
	void mergeProperties(final Properties properties);

	/**
	 * Sets the properties map with the given properties by overwriting the existing properties
	 * map. Each key and value will be casted to a <code>String</code> via their
	 * <code>toString()</code> method.
	 * 
	 * @param properties a properties object
	 */
	void setProperties(final Properties properties);
	
	/**
	 * Gateways should implement this if they need to finalize a shipment
	 * once all payment process has been completed.  This may include, for 
	 * example, sending confirmation emails from external checkouts.
	 *
	 * @param orderShipment <CODE>OrderShipment</CODE> to be finalized.
	 */
	void finalizeShipment(final OrderShipment orderShipment);

	/**
	 * The payment gateway plugin for this payment gateway.
	 * 
	 * @return the payment gateway plugin
	 */
	PaymentGatewayPluginInvoker getPaymentGatewayPlugin();
	
	/**
	 * Gets the {@link PaymentGatewayTransactionService} used to execute transactions on a configured payment gateway.
	 * 
	 * @return the {@link PaymentGatewayTransactionService}
	 */
	PaymentGatewayTransactionService getPaymentGatewayTransactionService();
	
	/**
	 * Determines if the payment gateway plugin is installed.
	 * 
	 * @return Boolean value representing whether the plugin is installed.
	 */
	boolean isPaymentGatewayPluginInstalled();
}
