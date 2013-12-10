package com.elasticpath.plugin.payment.transaction.command.impl;

import com.elasticpath.plugin.payment.PaymentGatewayPluginInvoker;
import com.elasticpath.plugin.payment.capabilities.CaptureCapability;
import com.elasticpath.plugin.payment.exceptions.PaymentOperationNotSupportedException;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;
import com.elasticpath.plugin.payment.transaction.command.CaptureTransactionCommand;

/**
 * Command used to execute a capture transaction against a specified payment gateway.
 */
public class CaptureTransactionCommandImpl implements CaptureTransactionCommand {
	private final PaymentGatewayPluginInvoker paymentGatewayPluginInvoker;
	private final CaptureTransactionRequest captureTransactionRequest;
	
	/**
	 * Constructor.
	 *
	 * @param builder the {@link CaptureTransactionCommandImpl} builder
	 */
	public CaptureTransactionCommandImpl(final BuilderImpl builder) {
		paymentGatewayPluginInvoker = builder.paymentGatewayPlugin;
		captureTransactionRequest = builder.captureTransactionRequest;
	}
	
	@Override
	public CaptureTransactionResponse execute() {
		CaptureCapability captureCapability = paymentGatewayPluginInvoker.getCapability(CaptureCapability.class);
		if (captureCapability == null) {
			throw new PaymentOperationNotSupportedException("Capture capability is not supported");
		}
		
		return captureCapability.capture(captureTransactionRequest);
	}
	
	/**
	 * The {@link CaptureTransactionCommand} builder.
	 */
	static class BuilderImpl implements CaptureTransactionCommand.Builder {
		private PaymentGatewayPluginInvoker paymentGatewayPlugin;
		private CaptureTransactionRequest captureTransactionRequest;
		
		@Override
		public Builder setPaymentGatewayPlugin(final PaymentGatewayPluginInvoker paymentGatewayPlugin) {
			this.paymentGatewayPlugin = paymentGatewayPlugin;
			return this;
		}

		@Override
		public Builder setCaptureTransactionRequest(final CaptureTransactionRequest captureTransactionRequest) {
			this.captureTransactionRequest = captureTransactionRequest;
			return this;
		}

		@Override
		public CaptureTransactionCommand build() {
			CaptureTransactionCommandImpl command = new CaptureTransactionCommandImpl(this);
			
			if (command.captureTransactionRequest == null) {
				throw new IllegalStateException("The Capture Request needs to be set");
			}
			
			if (command.paymentGatewayPluginInvoker == null) {
				throw new IllegalStateException("The payment gateway plugin invoker needs to be set");
			}
			
			return command;
		}

	}
}
