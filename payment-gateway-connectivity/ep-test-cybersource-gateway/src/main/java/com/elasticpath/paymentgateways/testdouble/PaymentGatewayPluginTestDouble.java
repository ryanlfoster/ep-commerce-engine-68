/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.paymentgateways.testdouble;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.capabilities.CaptureCapability;
import com.elasticpath.plugin.payment.capabilities.FinalizeShipmentCapability;
import com.elasticpath.plugin.payment.capabilities.PreAuthorizeCapability;
import com.elasticpath.plugin.payment.capabilities.RefundCapability;
import com.elasticpath.plugin.payment.capabilities.ReversePreAuthorizationCapability;
import com.elasticpath.plugin.payment.capabilities.SaleCapability;
import com.elasticpath.plugin.payment.capabilities.TokenAuthorizationCapability;
import com.elasticpath.plugin.payment.capabilities.VoidCaptureCapability;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.plugin.payment.spi.AbstractCreditCardPaymentGatewayPluginSPI;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;

/**
 * A test payment gateway plugin implementation for recording transactions done against the gateway.
 */
public class PaymentGatewayPluginTestDouble extends AbstractCreditCardPaymentGatewayPluginSPI
	implements RefundCapability, VoidCaptureCapability, TokenAuthorizationCapability, PreAuthorizeCapability,
				CaptureCapability, SaleCapability, ReversePreAuthorizationCapability, FinalizeShipmentCapability {

	private static final long serialVersionUID = 1L;

	private static final String TEST_REFERENCE_ID = "testReferenceId";
	private static final String TEST_EMAIL = "testEmail";
	private static final String TEST_REQUEST_TOKEN = "testRequestToken";
	private static final String TEST_AUTHORIZATION_CODE = "testAuthorizationCode";
	private List<String> supportedCardTypes;
	private static final String GATEWAY_TYPE = "paymentGatewayPluginTestDouble";
	
	private static List<String> transactionRequestLog;
	
	/** The Constant VOID_TRANSACTION. */
	public static final String VOID_TRANSACTION = "VOID TRANSACTION";
	
	/** The Constant SALE_TRANSACTION. */
	public static final String SALE_TRANSACTION = "SALE TRANSACTION";
	
	/** The Constant AUTHORIZATION_TRANSACTION. */
	public static final String AUTHORIZATION_TRANSACTION = "Authorization";
	
	/** The Constant CAPTURE_TRANSACTION. */
	public static final String CAPTURE_TRANSACTION = "Capture";
	
	/** The Constant CREDIT_TRANSACTION. */
	public static final String CREDIT_TRANSACTION = "Credit";
	
	/** The Constant REVERSE_AUTHORIZATION. */
	public static final String REVERSE_AUTHORIZATION = "Authorization Reversal";
	
	private static Boolean failFinalizeShipment;

	/**
	 * Static initializer for the transaction request log. 
	 */
	static {
		transactionRequestLog = new ArrayList<String>();
		failFinalizeShipment = Boolean.FALSE;
	}
	
	@Override
	public PaymentType getPaymentType() {
		return PaymentType.PAYMENT_TOKEN;
	}
	
	@Override
	public String getPluginType() {
		return GATEWAY_TYPE;
	}

	@Override
	public Collection<String> getConfigurationParameters() {
		return new ArrayList<String>();
	}

	@Override
	public AuthorizationTransactionResponse preAuthorize(final AuthorizationTransactionRequest authorizationTransactionRequest) {
		logPaymentGatewayTransaction(AUTHORIZATION_TRANSACTION);
		return createAuthorizationResponse(TEST_REFERENCE_ID, TEST_AUTHORIZATION_CODE, TEST_REQUEST_TOKEN, 
				TEST_EMAIL, authorizationTransactionRequest.getMoney());
	}

	@Override
	public AuthorizationTransactionResponse preAuthorize(final AuthorizationTransactionRequest authorizationTransactionRequest, 
			final AddressDto billingAddress,
			final OrderShipmentDto shipment) {
		logPaymentGatewayTransaction(AUTHORIZATION_TRANSACTION);
		return createAuthorizationResponse(TEST_REFERENCE_ID, TEST_AUTHORIZATION_CODE, TEST_REQUEST_TOKEN, 
				TEST_EMAIL, authorizationTransactionRequest.getMoney());
	}

	@Override
	public CaptureTransactionResponse capture(final CaptureTransactionRequest captureTransactionRequest) {
		logPaymentGatewayTransaction(CAPTURE_TRANSACTION);
		return createCaptureResponse(captureTransactionRequest.getMoney(), TEST_REQUEST_TOKEN,
				TEST_AUTHORIZATION_CODE, TEST_REFERENCE_ID);
	}

	@Override
	public void sale(final OrderPaymentDto payment, final AddressDto billingAddress, final OrderShipmentDto shipment) {
		logPaymentGatewayTransaction(SALE_TRANSACTION);
	}

	@Override
	public void reversePreAuthorization(final OrderPaymentDto payment) {
		logPaymentGatewayTransaction(REVERSE_AUTHORIZATION);
	}

	@Override
	public void voidCaptureOrCredit(final OrderPaymentDto payment) {
		logPaymentGatewayTransaction(VOID_TRANSACTION);
	}

	@Override
	public void refund(final OrderPaymentDto payment, final AddressDto billingAddress) {
		logPaymentGatewayTransaction(CREDIT_TRANSACTION);
	}

	@Override
	public List<String> getSupportedCardTypes() {
		if (supportedCardTypes == null) {
			supportedCardTypes = this.getDefaultSupportedCardTypes();
		}
		return supportedCardTypes;
	}
	
	/**
	 * Gets the payment gateway transactions that have been executed on this payment gateway since 
	 * the log was initialized (or re-initialized after a flush).
	 *
	 * @return a map of paymentgateway transactions and their associated transaction types.
	 */
	public static List<String> getPaymentGatewayTransactions() {
		return new ArrayList<String>(transactionRequestLog);
	}

	/**
	 * Configures the gateway to fail on finalizing the shipment.
	 *
	 * @param isFinalizeShipmentFailure true to cause failure, false otherwise.
	 */
	public static void setFailFinalizeShipment(final Boolean isFinalizeShipmentFailure) {
		failFinalizeShipment = isFinalizeShipmentFailure;
	}

	/**
	 * Clears all of the payment gateway transaction requests in the map.
	 */
	public static void clearPaymentGatewayTransactions() {
		transactionRequestLog.clear();
	}
	
	/**
	 * Verify transactions.
	 *
	 * @param expectedTransactionTypes the expected transaction types
	 * @return true, if all expected transaction types were performed against the gateway, false otherwise 
	 */
	public static boolean verifyTransactions(final List<String> expectedTransactionTypes) {
		List<String> actualTransactionTypesSorted = new ArrayList<String>(getPaymentGatewayTransactions());
		Collections.sort(actualTransactionTypesSorted);
		List<String> expectedTransactionTypesSorted = new ArrayList<String>(expectedTransactionTypes);
		Collections.sort(expectedTransactionTypesSorted);
		return actualTransactionTypesSorted.equals(expectedTransactionTypesSorted);
	}

	@Override
	public void finalizeShipment(final OrderShipmentDto orderShipment) {
		if (failFinalizeShipment) {
			throw new PaymentGatewayException("Finalizing a shipment fails.");
		}
	}
	
	private List<String> getDefaultSupportedCardTypes() {
		final List<String> cardTypesList = new ArrayList<String>();
		cardTypesList.add("Visa");
		cardTypesList.add("MasterCard");
		cardTypesList.add("American Express");
		return cardTypesList;
	}

	private void logPaymentGatewayTransaction(final String transactionType) {
		transactionRequestLog.add(transactionType);
	}

}
