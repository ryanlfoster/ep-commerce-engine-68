package com.elasticpath.plugin.payment.capabilities;

import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;

/**
 * A {@link PaymentGatewayCapability} that Payment Gateways which authorize payments on a token should implement.
 */
public interface TokenAuthorizationCapability extends PaymentGatewayCapability {
	/**
	 * Authorize a payment. 
	 * In the case of physical shipments for example, funds should not be captured until the items have been shipped.
	 * The purpose of an authorization is to reserve funding on a credit card prior to capture.
	 *
	 * @param authorizationTransactionRequest the {@link AuthorizationTransactionRequest}
	 * @return the {@link AuthorizationTransactionResponse} from the gateway 
	 */
	AuthorizationTransactionResponse preAuthorize(AuthorizationTransactionRequest authorizationTransactionRequest);
}
