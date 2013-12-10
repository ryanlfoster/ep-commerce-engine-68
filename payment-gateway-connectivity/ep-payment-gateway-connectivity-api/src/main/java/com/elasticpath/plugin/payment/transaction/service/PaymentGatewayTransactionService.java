package com.elasticpath.plugin.payment.transaction.service;

import com.elasticpath.plugin.payment.PaymentGatewayPluginInvoker;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;

/**
 * Service that performs various transactions against a payment gateway given a payment transaction request. 
 */
public interface PaymentGatewayTransactionService {
	
	/**
	 * Authorizes an order given payment information. 
	 * Specifically, the payment method provided is used to create an authorization request against a configured payment gateway.
	 *
	 *@param authorizationTransactionRequest the {@link AuthorizationTransactionRequest}
	 * @param addressDto the address dto
	 * @param orderShipmentDto the order shipment dto
	 * @param paymentGatewayPlugin the {@link PaymentGatewayPluginInvoker}
	 * @return the {@link AuthorizationTransactionResponse} from the transaction executed on the payment gateway.
	 */
	AuthorizationTransactionResponse authorize(AuthorizationTransactionRequest authorizationTransactionRequest, AddressDto addressDto, 
			OrderShipmentDto orderShipmentDto, PaymentGatewayPluginInvoker paymentGatewayPlugin);
	
	/**
	 * Captures an order given payment information.
	 * Speciifcally, the payment method provided is to create a capture reqeust against a configured payment gateway.
	 *
	 * @param captureTransactionRequest the {@link CaptureTransactionRequest}
	 * @param paymentGatewayPlugin the {@link PaymentGatewayPluginInvoker}
	 * @return the {@link CaptureTransactionResponse} from the transaction executed on the payment gateway.
	 */
	CaptureTransactionResponse capture(CaptureTransactionRequest captureTransactionRequest, PaymentGatewayPluginInvoker paymentGatewayPlugin);
}
