package com.elasticpath.plugin.payment.transaction.command;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;

/**
 * Command used to execute an authorization transaction against a specified payment gateway.
 */
public interface AuthorizationTransactionCommand extends PaymentTransactionCommand<AuthorizationTransactionResponse> {
	
	/**
	 * Builder to build {@link AuthorizationTransactionCommand}s.
	 */
	interface Builder extends PaymentTransactionCommand.Builder<AuthorizationTransactionCommand> {
		/**
		 * Sets the {@link OrderShipmentDto} to use for the transaction.
		 *
		 * @param orderShipment the order shipment
		 * @return this {@link Builder}
		 */
		Builder setOrderShipment(OrderShipmentDto orderShipment);
		
		/**
		 * Sets the billing {@link AddressDto} for the transaction.
		 *
		 * @param billingAddress the billing address
		 * @return this {@link Builder}
		 */
		Builder setBillingAddress(AddressDto billingAddress);
		
		/**
		 * sets the {@link AuthorizationTransactionRequest} for the {@link AuthorizationTransactionCommand}.
		 *
		 * @param authorizationTransactionRequest the {@link AuthorizationTransactionRequest}
		 * @return this {@link Builder}
		 */
		Builder setAuthorizationTransactionRequest(AuthorizationTransactionRequest authorizationTransactionRequest);
	}
}
