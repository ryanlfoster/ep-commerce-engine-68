package com.elasticpath.service.payment.gateway;

import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;

/**
 * Gift certificate authorization transaction request.
 */
public interface GiftCertificateAuthorizationRequest extends AuthorizationTransactionRequest, GiftCertificateTransactionRequest {
	
}
