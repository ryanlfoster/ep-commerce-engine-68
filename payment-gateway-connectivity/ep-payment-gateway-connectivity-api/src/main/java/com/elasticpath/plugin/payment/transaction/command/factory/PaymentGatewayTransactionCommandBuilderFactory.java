package com.elasticpath.plugin.payment.transaction.command.factory;

import com.elasticpath.plugin.payment.transaction.command.AuthorizationTransactionCommand;
import com.elasticpath.plugin.payment.transaction.command.CaptureTransactionCommand;

/**
 * Factory to create PaymentTransactionCommandBuilders. 
 */
public interface PaymentGatewayTransactionCommandBuilderFactory {
	
	/**
	 * Returns an {@link AuthorizationTransactionCommand} builder.
	 *
	 * @return an {@link AuthorizationTransactionCommand} builder
	 */
	AuthorizationTransactionCommand.Builder getAuthorizationTransactionCommandBuilder();
	
	/**
	 * Returns a {@link CaptureTransactionCommand} builder.
	 *
	 * @return a {@link CaptureTransactionCommand} builder
	 */
	CaptureTransactionCommand.Builder getCaptureTransactionCommandBuilder();
}
