package com.elasticpath.plugin.payment.capabilities;

import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;

/**
 * A capability that Payment Gateways which support the subsequent capture of a previously authorized amount should implement.
 */
public interface CaptureCapability extends PaymentGatewayCapability {
	/**
	 * Captures a payment on a previously authorized card.
	 *
	 * @param captureTransactionRequest the {@link CaptureTransactionRequest}
	 * @return the {@link CaptureTransactionResponse} resulting from the capture
	 */
	CaptureTransactionResponse capture(CaptureTransactionRequest captureTransactionRequest);
}
