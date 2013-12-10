package com.elasticpath.plugin.payment.transaction.command;

import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;

/**
 * Command used to execute a capture transaction against a specified payment gateway. 
 */
public interface CaptureTransactionCommand extends PaymentTransactionCommand<CaptureTransactionResponse> {

	/**
	 * Builder to build {@link CaptureTransactionCommand}s.
	 */
	interface Builder extends PaymentTransactionCommand.Builder<CaptureTransactionCommand> {
		/**
		 * Sets the {@link CaptureTransactionRequest} for the {@link CaptureTransactionCommand}.
		 *
		 * @param captureTransactionRequest the {@link CaptureTransactionRequest}
		 * @return this {@link Builder}
		 */
		Builder setCaptureTransactionRequest(CaptureTransactionRequest captureTransactionRequest);
	}
}
