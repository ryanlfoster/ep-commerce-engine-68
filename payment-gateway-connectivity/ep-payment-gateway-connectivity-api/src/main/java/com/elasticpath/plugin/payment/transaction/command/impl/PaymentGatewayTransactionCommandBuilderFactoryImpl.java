package com.elasticpath.plugin.payment.transaction.command.impl;

import com.elasticpath.plugin.payment.transaction.command.AuthorizationTransactionCommand;
import com.elasticpath.plugin.payment.transaction.command.CaptureTransactionCommand;
import com.elasticpath.plugin.payment.transaction.command.factory.PaymentGatewayTransactionCommandBuilderFactory;

/**
 * Implementation of {@link PaymentGatewayTransactionCommandBuilderFactory}.
 */
public class PaymentGatewayTransactionCommandBuilderFactoryImpl implements PaymentGatewayTransactionCommandBuilderFactory {
	
	@Override
	public AuthorizationTransactionCommand.Builder getAuthorizationTransactionCommandBuilder() {
		return new AuthorizationTransactionCommandImpl.BuilderImpl();
	}

	@Override
	public CaptureTransactionCommand.Builder getCaptureTransactionCommandBuilder() {
		return new CaptureTransactionCommandImpl.BuilderImpl();
	}
}
