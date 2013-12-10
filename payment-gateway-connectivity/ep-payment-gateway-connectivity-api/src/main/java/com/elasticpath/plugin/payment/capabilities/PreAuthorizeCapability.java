package com.elasticpath.plugin.payment.capabilities;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;

/**
 * A {@link PaymentGatewayCapability} that Payment Gateways which pre-authorize payments on credit cards should implement.
 */
public interface PreAuthorizeCapability extends PaymentGatewayCapability {
	/**
	 * Pre-authorize a payment.
	 *
	 *@param authorizationTransactionRequest the {@link AuthorizationTransactionRequest}
	 * @param billingAddress the name and address of the person being billed
	 * @param shipment the {@link OrderShipmentDto} for this transaction
	 * @return the {@link AuthorizationTransactionResponse} from the gateway 
	 */
	AuthorizationTransactionResponse preAuthorize(AuthorizationTransactionRequest authorizationTransactionRequest, AddressDto billingAddress, 
			OrderShipmentDto shipment);
	
}
