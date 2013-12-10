/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.paymentgateways.cybersource;

import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_CITY;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_COUNTRY;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_EMAIL;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_FIRSTNAME;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_LASTNAME;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_POSTALCODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_STATE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BILL_TO_STREET1;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.BUSINESS_RULES_IGNORE_AVS_RESULT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CARD_ACCOUNT_NUMBER;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CARD_CVNUMBER;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CARD_EXPIRATION_MONTH;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CARD_EXPIRATION_YEAR;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CARD_TYPE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_AUTH_REVERSAL_SERVICE_AUTH_REQUEST_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_AUTH_REVERSAL_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.MERCHANT_REFERENCE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PAY_SUBSCRIPTION_CREATE_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PURCHASE_TOTALS_CURRENCY;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.RECURRING_SUBSCRIPTION_INFO_FREQUENCY;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.REQUEST_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.cybersource.ws.client.Client;
import com.cybersource.ws.client.ClientException;
import com.cybersource.ws.client.FaultException;

import com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.CardDetailsPaymentMethod;
import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;

/**
 * Test Cybersource client to execute common transactions against a cybersource gateway.
 */
public final class TestCybersourceClient {

	private final Properties cybersourceProperties;
	
	/**
	 * Constructor to initialize cybersource system properties.
	 *
	 * @param cybersourceProperties the Cybersource properties
	 */
	private TestCybersourceClient(final Properties cybersourceProperties) {
		this.cybersourceProperties = cybersourceProperties;
	}
	
	/**
	 * Run a Cybersource transaction.
	 * 
	 * @param request a Map of request key value pairs
	 * @return a result Map of key value pairs
	 * @throws ClientException client exception error
	 * @throws FaultException fault exception error
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> runTransaction(final Map<String, String> request) {
		try {
			return Client.runTransaction(request, cybersourceProperties);
		} catch (final ClientException e) {
			throw new PaymentGatewayException(CyberSourceResponseConstants.CLIENT_EXCEPTION + e.getInnerException(), e);
		} catch (final FaultException e) {
			throw new PaymentGatewayException(CyberSourceResponseConstants.FAULT_EXCEPTION, e);
		}
	}
	
	/**
	 * Create a Cybersource subscriber.
	 *
	 * @param billingAddress the billing address
	 * @param paymentMethod the payment method
	 * @param currencyCode the currency code
	 * @return a subscriber id which represents a token that can be used to replace sensitive 
	 * data required for a transaction (i.e credit card information,billing address, etc).
	 */
	public String createCybersourceSubscriber(final AddressDto billingAddress, final CardDetailsPaymentMethod paymentMethod, 
			final String currencyCode) {
		HashMap<String, String> subscriptionRequest = new HashMap<String, String>();
		
		subscriptionRequest.put(PAY_SUBSCRIPTION_CREATE_SERVICE_RUN, "true");
		subscriptionRequest.put(BUSINESS_RULES_IGNORE_AVS_RESULT, "true");
		subscriptionRequest.put(BILL_TO_CITY, billingAddress.getCity());
		subscriptionRequest.put(BILL_TO_COUNTRY, billingAddress.getCountry());
		subscriptionRequest.put(BILL_TO_EMAIL, paymentMethod.getEmail());
		subscriptionRequest.put(BILL_TO_FIRSTNAME, billingAddress.getFirstName());
		subscriptionRequest.put(BILL_TO_LASTNAME, billingAddress.getLastName());
		subscriptionRequest.put(BILL_TO_POSTALCODE, billingAddress.getZipOrPostalCode());
		
		subscriptionRequest.put(BILL_TO_STATE, billingAddress.getSubCountry());
		subscriptionRequest.put(BILL_TO_STREET1, billingAddress.getStreet1());
		subscriptionRequest.put(PURCHASE_TOTALS_CURRENCY, currencyCode);
		
		subscriptionRequest.put(RECURRING_SUBSCRIPTION_INFO_FREQUENCY, "on-demand");

		// Card Information
		subscriptionRequest.put(CARD_TYPE, paymentMethod.getCardType());
		subscriptionRequest.put(CARD_ACCOUNT_NUMBER, paymentMethod.getUnencryptedCardNumber());
		subscriptionRequest.put(CARD_EXPIRATION_MONTH, paymentMethod.getExpiryMonth());
		subscriptionRequest.put(CARD_EXPIRATION_YEAR, paymentMethod.getExpiryYear());
		subscriptionRequest.put(CARD_CVNUMBER, paymentMethod.getCvv2Code());
		subscriptionRequest.put(MERCHANT_REFERENCE_CODE, paymentMethod.getReferenceId());
		
		Map<String, String> subscriptionTransactionResponse = runTransaction(subscriptionRequest);
		
		return subscriptionTransactionResponse.get(CyberSourceResponseConstants.SUBSCRIPTION_REPLY_ID);
	}

	/**
	 * Perform an authorization reversal for the given transaction ID and monetary total. Requires the transaction ID
	 * from a previous authorization.
	 *
	 * @param transactionId the transaction id that will be reversed
	 * @param amount the monetary amount to reverse
	 */
	public void authorizationReversal(final String transactionId, final Money amount) {
		final HashMap<String, String> request = new HashMap<String, String>();

		request.put(CC_AUTH_REVERSAL_SERVICE_RUN, Boolean.TRUE.toString());

		request.put(CC_AUTH_REVERSAL_SERVICE_AUTH_REQUEST_ID, transactionId);
		request.put(REQUEST_ID, transactionId);
		request.put(PURCHASE_TOTALS_CURRENCY, amount.getCurrencyCode());
		request.put(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT, amount.getAmount().toString());
		request.put(MERCHANT_REFERENCE_CODE, "doesn't matter");

		Map<String, String> reply = runTransaction(request);

		final String decision = reply.get(CyberSourceResponseConstants.DECISION);
		if (!decision.equalsIgnoreCase(CyberSourceResponseConstants.DECISION_ACCEPT)) {
			throw new AuthorizationReversalException(reply);
		}
	}

	/**
	 * Create a new client.
	 * @param cybersourceProperties the configuration values to provide the underlying CyberSource library
	 * @return the new client instance
	 */
	public static TestCybersourceClient create(final Properties cybersourceProperties) {
		return new TestCybersourceClient(cybersourceProperties);
	}
}
