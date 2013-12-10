/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.plugin.payment;

/**
 * Represents the payment type.
 *
 */
public enum PaymentType {
	/**
	 * The <code>PaymentType</code> instance for paying with a credit card.
	 */
	CREDITCARD("paymentType.creditCard"),

	/**
	 * The <code>PaymentType</code> instance for paying with PayPal Express.
	 */
	PAYPAL_EXPRESS("paymentType.payPalExpress"),

	/**
	 * The <code>PaymentType</code> instance for paying with a Gift Certificate.
	 */
	GIFT_CERTIFICATE("paymentType.giftCertificate"),
	
	/**
	 * The <code>PaymentType</code> instance for paying with Google Checkout.
	 */
	GOOGLE_CHECKOUT("paymentType.googleCheckout"),
	
	/**
	 * The <code>PaymentType</code> instance for return and exchanges.
	 */
	RETURN_AND_EXCHANGE("paymentType.returnAndExchange"),

	/**
	 * The <code>PaymentType</code> instance for paying with a token.
	 */
	PAYMENT_TOKEN("paymentType.payment_token");
	
	private String propertyKey = "";

	/**
	 * Constructor.
	 * 
	 * @param propertyKey the property key.
	 */
	PaymentType(final String propertyKey) {
		this.propertyKey = propertyKey;
	}

	/**
	 * Get the localization property key.
	 * 
	 * @return the localized property key
	 */
	public String getPropertyKey() {
		return this.propertyKey;
	}
	
}
