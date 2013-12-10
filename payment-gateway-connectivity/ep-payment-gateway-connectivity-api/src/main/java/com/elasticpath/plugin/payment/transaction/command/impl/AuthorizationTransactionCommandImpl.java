package com.elasticpath.plugin.payment.transaction.command.impl;

import com.elasticpath.plugin.payment.PaymentGatewayPluginInvoker;
import com.elasticpath.plugin.payment.capabilities.PreAuthorizeCapability;
import com.elasticpath.plugin.payment.capabilities.TokenAuthorizationCapability;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.CardDetailsPaymentMethod;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.plugin.payment.dto.TokenPaymentMethod;
import com.elasticpath.plugin.payment.exceptions.PaymentOperationNotSupportedException;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.plugin.payment.transaction.command.AuthorizationTransactionCommand;

/**
 * Command used to execute an authorization transaction against a specified gateway.
 */
public final class AuthorizationTransactionCommandImpl implements AuthorizationTransactionCommand {
	private final PaymentGatewayPluginInvoker paymentGatewayPluginInvoker;
	private final AddressDto billingAddress;
	private final OrderShipmentDto orderShipment;
	private final AuthorizationTransactionRequest authorizationTransactionRequest;
	
	/**
	 * Constructor.
	 *
	 * @param builder the {@link AuthorizationTransactionCommandImpl} builder.
	 */
	AuthorizationTransactionCommandImpl(final BuilderImpl builder) {
		paymentGatewayPluginInvoker = builder.paymentGatewayPlugin;
		billingAddress = builder.billingAddress;
		orderShipment = builder.orderShipment;
		authorizationTransactionRequest = builder.authorizationTransactionRequest;
	}

	@Override
	public AuthorizationTransactionResponse execute() {
		PaymentMethod paymentMethod = authorizationTransactionRequest.getPaymentMethod();
		
		if (paymentMethod instanceof CardDetailsPaymentMethod) {
			PreAuthorizeCapability authorizationCapability = paymentGatewayPluginInvoker.getCapability(PreAuthorizeCapability.class);
			if (authorizationCapability == null) {
				throw new PaymentOperationNotSupportedException("Authorization capability is not supported.");
			}

			return authorizationCapability.preAuthorize(authorizationTransactionRequest, billingAddress, orderShipment);
		} else if (paymentMethod instanceof TokenPaymentMethod) {
			TokenAuthorizationCapability tokenAuthCapability = paymentGatewayPluginInvoker.getCapability(TokenAuthorizationCapability.class);
			if (tokenAuthCapability == null) {
				throw new PaymentOperationNotSupportedException("Token authorization capability is not supported.");
			}
			
			return tokenAuthCapability.preAuthorize(authorizationTransactionRequest);
		} else {
			throw new PaymentOperationNotSupportedException("Payment method is not supported for authorization");
		}
	}
	
	/**
	 * {@link AuthorizationTransactionCommandImpl} builder.
	 */
	static class BuilderImpl implements AuthorizationTransactionCommand.Builder {
		private PaymentGatewayPluginInvoker paymentGatewayPlugin;
		private AddressDto billingAddress;
		private OrderShipmentDto orderShipment;
		private AuthorizationTransactionRequest authorizationTransactionRequest;
		
		@Override
		public Builder setPaymentGatewayPlugin(final PaymentGatewayPluginInvoker paymentGatewayPlugin) {
			this.paymentGatewayPlugin = paymentGatewayPlugin;
			return this;
		}

		@Override
		public Builder setOrderShipment(final OrderShipmentDto orderShipment) {
			this.orderShipment = orderShipment;
			return this;
		}

		@Override
		public Builder setBillingAddress(final AddressDto billingAddress) {
			this.billingAddress = billingAddress;
			return this;
		}
		
		@Override
		public Builder setAuthorizationTransactionRequest(final AuthorizationTransactionRequest authorizationTransactionRequest) {
			this.authorizationTransactionRequest = authorizationTransactionRequest;
			return this;
		}
		
		@Override
		public AuthorizationTransactionCommand build() {
			AuthorizationTransactionCommandImpl command = new AuthorizationTransactionCommandImpl(this);
			
			if (command.authorizationTransactionRequest == null) {
				throw new IllegalStateException("The Authorization Request needs to be set");
			}
			
			if (command.paymentGatewayPluginInvoker == null) { 
				throw new IllegalStateException("The payment gateway plugin invoker needs to be set");
			}

			return command;
		}

	}
}
