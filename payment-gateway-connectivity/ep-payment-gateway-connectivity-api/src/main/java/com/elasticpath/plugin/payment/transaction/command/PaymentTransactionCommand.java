package com.elasticpath.plugin.payment.transaction.command;

import com.elasticpath.plugin.payment.PaymentGatewayPluginInvoker;
import com.elasticpath.plugin.payment.transaction.PaymentTransactionResponse;

/**
 * Command interface with inner builder interface for building payment transaction requests for
 * a payment gateway. 
 * 
 * @param <T> type of {@link PaymentTransactionResponse} that executed transaction should return
 */
public interface PaymentTransactionCommand<T extends PaymentTransactionResponse> {
	/**
	 *  Initiates the command execution for a payment gateway request.
	 *
	 * @return the PaymentResponse resulting from the execution.
	 */
	T execute();
	
	/**
	 * Builder interface to construct a new {@link PaymentTransactionCommand}.
	 * 
	 * @param <C> type of {@link PaymentTransactionCommand} to build
	 */
	interface Builder<C extends PaymentTransactionCommand<?>> {
		/**
		 * Sets the {@link PaymentGatewayPluginInvoker} to use for the transaction.
		 *
		 * @param paymentGatewayPlugin the {@link PaymentGatewayPluginInvoker}
		 * @return this {@link Builder}
		 */
		Builder<C> setPaymentGatewayPlugin(PaymentGatewayPluginInvoker paymentGatewayPlugin);

		/**
		 * Constructs the {@link PaymentTransactionCommand} from the parameters set in builder.
		 *
		 * @return A {@link PaymentTransactionCommand}.
		 */
		C build();
	}
}
