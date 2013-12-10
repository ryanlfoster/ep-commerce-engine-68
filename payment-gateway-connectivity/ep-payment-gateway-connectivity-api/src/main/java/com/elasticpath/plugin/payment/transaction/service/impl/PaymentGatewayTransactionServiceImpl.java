package com.elasticpath.plugin.payment.transaction.service.impl;

import com.elasticpath.plugin.payment.PaymentGatewayPluginInvoker;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;
import com.elasticpath.plugin.payment.transaction.command.factory.PaymentGatewayTransactionCommandBuilderFactory;
import com.elasticpath.plugin.payment.transaction.command.impl.PaymentGatewayTransactionCommandBuilderFactoryImpl;
import com.elasticpath.plugin.payment.transaction.service.PaymentGatewayTransactionService;

/**
 * Implementation of {@link PaymentGatewayTransactionService}.
 */
public class PaymentGatewayTransactionServiceImpl implements PaymentGatewayTransactionService {

	private final PaymentGatewayTransactionCommandBuilderFactory paymentGatewayTransactionCommandBuilderFactory = 
			new PaymentGatewayTransactionCommandBuilderFactoryImpl();
	
	@Override
	public AuthorizationTransactionResponse authorize(final AuthorizationTransactionRequest authorizationTransactionRequest,
			final AddressDto addressDto, final OrderShipmentDto orderShipmentDto, final PaymentGatewayPluginInvoker paymentGatewayPlugin) {
		return paymentGatewayTransactionCommandBuilderFactory.getAuthorizationTransactionCommandBuilder()
				.setAuthorizationTransactionRequest(authorizationTransactionRequest)
				.setOrderShipment(orderShipmentDto)
				.setBillingAddress(addressDto)
				.setPaymentGatewayPlugin(paymentGatewayPlugin)
				.build()
				.execute();
	}

	@Override
	public CaptureTransactionResponse capture(final CaptureTransactionRequest captureTransactionRequest, 
			final PaymentGatewayPluginInvoker paymentGatewayPlugin) {
		return paymentGatewayTransactionCommandBuilderFactory.getCaptureTransactionCommandBuilder()
				.setCaptureTransactionRequest(captureTransactionRequest)
				.setPaymentGatewayPlugin(paymentGatewayPlugin)
				.build()
				.execute();
	}
}
