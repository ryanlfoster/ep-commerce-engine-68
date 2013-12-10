package com.elasticpath.service.payment.gateway.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.capabilities.CaptureCapability;
import com.elasticpath.plugin.payment.capabilities.PreAuthorizeCapability;
import com.elasticpath.plugin.payment.capabilities.ReversePreAuthorizationCapability;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.plugin.payment.exceptions.GiftCertificateException;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.plugin.payment.spi.AbstractPaymentGatewayPluginSPI;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;
import com.elasticpath.plugin.payment.transaction.PaymentTransactionRequest;
import com.elasticpath.service.payment.GiftCertificateTransactionService;
import com.elasticpath.service.payment.gateway.GiftCertificateAuthorizationRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateCaptureRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateOrderPaymentDto;
import com.elasticpath.service.payment.gateway.GiftCertificateTransactionRequest;
import com.elasticpath.service.payment.impl.GiftCertificateTransactionResponse;

/**
 * Implementation of the gift certificate payment gateway which allows customers to purchase using gift certificates.
 */
public class GiftCertificatePaymentGatewayPluginImpl extends AbstractPaymentGatewayPluginSPI
		implements PreAuthorizeCapability, CaptureCapability, ReversePreAuthorizationCapability {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final String GATEWAY_TYPE = "paymentGatewayGiftCertificate";

	private transient GiftCertificateTransactionService giftCertificateTransactionService;

	@Override
	public String getPluginType() {
		return GATEWAY_TYPE;
	}

	@Override
	public PaymentType getPaymentType() {
		return PaymentType.GIFT_CERTIFICATE;
	}

	@Override
	public AuthorizationTransactionResponse preAuthorize(final AuthorizationTransactionRequest authorizationTransactionRequest,
			final AddressDto billingAddress, final OrderShipmentDto shipment) {
		sanitizePaymentTransactionRequest(authorizationTransactionRequest);
		GiftCertificateTransactionResponse giftCertifcationTransactionResponse = null;
		try {
			giftCertifcationTransactionResponse = getGiftCertificateTransactionService().
					preAuthorize((GiftCertificateAuthorizationRequest) authorizationTransactionRequest, billingAddress);
			
		} catch (GiftCertificateException e) {
			throw new PaymentGatewayException("Failed in GiftCertificatePaymentGateway.preAuthorize ", e);
		}
		
		return createAuthorizationResponseFromGiftCertificateTransactionResponse(giftCertifcationTransactionResponse);
	}
	
	
	/**
	 * Reverse a previous pre-authorization.
	 * 
	 * @param payment the payment that was previously pre-authorized
	 */
	@Override
	public void reversePreAuthorization(final OrderPaymentDto payment) {
		sanitizeOrderPayment(payment);
		try {
			getGiftCertificateTransactionService().reversePreAuthorization((GiftCertificateOrderPaymentDto) payment);
		} catch (GiftCertificateException e) {
			throw new PaymentGatewayException("Failed in GiftCertificatePaymentGateway.reversePreAuthorization ", e);
		}
	}

	@Override
	public CaptureTransactionResponse capture(final CaptureTransactionRequest captureTransactionRequest) {
		sanitizePaymentTransactionRequest(captureTransactionRequest);
		try {
			GiftCertificateTransactionResponse response = getGiftCertificateTransactionService().capture(
					(GiftCertificateCaptureRequest) captureTransactionRequest);
			return createCaptureResponse(null, null, response.getAuthorizationCode(), response.getGiftCertificateCode());
		} catch (GiftCertificateException e) {
			throw new PaymentGatewayException("Failed in GiftCertificatePaymentGateway.capture ", e);
		}
	}
	
	@Override
	public Collection<String> getConfigurationParameters() {
		return new ArrayList<String>();
	}

	
	/**
	 * Get the gift certificate transaction service.
	 * 
	 * @return giftCertificateTransactionService 
	 */
	protected GiftCertificateTransactionService getGiftCertificateTransactionService() {
		return giftCertificateTransactionService;
	}

	/**
	 * Set the gift certificate transaction service.
	 * 
	 * @param giftCertificateTransactionService the giftCertificateTransactionService to set
	 */
	public void setGiftCertificateTransactionService(final GiftCertificateTransactionService giftCertificateTransactionService) {
		this.giftCertificateTransactionService = giftCertificateTransactionService;
	}

	private void sanitizePaymentTransactionRequest(final PaymentTransactionRequest transactionRequest) {
		if (!(transactionRequest instanceof GiftCertificateTransactionRequest)) {
			throw new GiftCertificateException(
					"Payment object passed to gift certificate payment gateway plugin is not of type GiftCertificateTransactionRequest.");
		}
		
		if (((GiftCertificateTransactionRequest) transactionRequest).getGiftCertificate() == null) {
			throw new GiftCertificateException("No gift certificate found in the request.");
		}
	}
	
	private void sanitizeOrderPayment(final PaymentMethod payment) {
		if (!(payment instanceof GiftCertificateOrderPaymentDto)) {
			throw new GiftCertificateException(
					"Payment object passed to gift certificate payment gateway plugin is not of type GiftCertificateOrderPaymentDto.");
		}
		
		if (((GiftCertificateOrderPaymentDto) payment).getGiftCertificate() == null) {
			throw new GiftCertificateException("No gift certificate found in the payment.");
		}
	}
	
	private AuthorizationTransactionResponse createAuthorizationResponseFromGiftCertificateTransactionResponse(
			final GiftCertificateTransactionResponse giftCertifcationTransactionResponse) {
		return createAuthorizationResponse(giftCertifcationTransactionResponse.getGiftCertificateCode(), 
				giftCertifcationTransactionResponse.getAuthorizationCode(), null, null, null);
	}
}
