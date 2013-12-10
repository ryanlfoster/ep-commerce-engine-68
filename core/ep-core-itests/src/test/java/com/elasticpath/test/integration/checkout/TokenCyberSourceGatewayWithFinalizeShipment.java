/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.integration.checkout;

import com.elasticpath.paymentgateways.cybersource.CybersourceTokenPaymentGatewayPluginImpl;
import com.elasticpath.plugin.payment.capabilities.FinalizeShipmentCapability;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;

/**
 * Token Cybersource gateway that implements a {@link FinalizeShipmentCapability}.
 */
public class TokenCyberSourceGatewayWithFinalizeShipment extends CybersourceTokenPaymentGatewayPluginImpl implements
		FinalizeShipmentCapability {

	private static final long serialVersionUID = 1L;
	private static Boolean failFinalizeShipment;
	
	/**
	 * Static initializer to set default behaviour of finalize shipment capability to succeed.
	 */
	static {
		failFinalizeShipment = Boolean.FALSE;
	}
	
	@Override
	public void finalizeShipment(final OrderShipmentDto orderShipment) {
		if (failFinalizeShipment) {
			throw new PaymentGatewayException("Finalizing a shipment fails.");
		}
	}
	
	public static void setFailFinalizeShipment(final Boolean isFinalizeShipmentFailure) {
		failFinalizeShipment = isFinalizeShipmentFailure;
	}
}
