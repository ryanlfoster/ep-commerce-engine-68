package com.elasticpath.service.payment.gateway.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.elasticpath.plugin.payment.capabilities.CaptureCapability;
import com.elasticpath.plugin.payment.capabilities.PreAuthorizeCapability;
import com.elasticpath.plugin.payment.capabilities.RefundCapability;
import com.elasticpath.plugin.payment.capabilities.ReversePreAuthorizationCapability;
import com.elasticpath.plugin.payment.capabilities.SaleCapability;
import com.elasticpath.plugin.payment.capabilities.VoidCaptureCapability;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.CardDetailsPaymentMethod;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.PayerAuthenticationEnrollmentResultDto;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;
import com.elasticpath.plugin.payment.dto.impl.PayerAuthenticationEnrollmentResultDtoImpl;
import com.elasticpath.plugin.payment.exceptions.CardDeclinedException;
import com.elasticpath.plugin.payment.exceptions.CardErrorException;
import com.elasticpath.plugin.payment.exceptions.CardExpiredException;
import com.elasticpath.plugin.payment.spi.AbstractCreditCardPaymentGatewayPluginSPI;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;

/**
 * Implementation of the "null" payment gateway which does not connect to any external service and is used mainly for testing.
 */
public class NullPaymentGatewayPluginImpl extends AbstractCreditCardPaymentGatewayPluginSPI
		implements RefundCapability, VoidCaptureCapability, CaptureCapability,
					ReversePreAuthorizationCapability, SaleCapability,
					PreAuthorizeCapability {
	private static final String TESTING_EXCEPTION_MESSAGE = "Testing exception";

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final String NULL_AUTH_CODE_PREFIX = "AuthCode";

	private static final String NULL_REFERENCE_ID_PREFIX = "RefId";

	private static final String NULL_REFUND_ID_PREFIX = "RefundId";

	private List<String> supportedCardTypes;

	private static boolean preAuthorizeFailFlag;

	private static boolean captureFailFlag;

	private static boolean reversePreAuthorizeFailFlag;

	private static boolean saleFailFlag;

	private static final String GATEWAY_TYPE = "paymentGatewayNull";

	private final Random randomGenerator = new Random(System.currentTimeMillis());

	@Override
	public String getPluginType() {
		return GATEWAY_TYPE;
	}

	@Override
	public AuthorizationTransactionResponse preAuthorize(final AuthorizationTransactionRequest authorizationTransactionRequest,
			final AddressDto billingAddress, final OrderShipmentDto shipment) {
		CardDetailsPaymentMethod paymentMethod = (CardDetailsPaymentMethod) authorizationTransactionRequest.getPaymentMethod();
		
		if (preAuthorizeFailFlag) {
			throw new CardExpiredException("credit card has expired");
		}

		if ("EXP_AUTH".equals(paymentMethod.getCardHolderName())) {
			throw new CardExpiredException(TESTING_EXCEPTION_MESSAGE);
		}
		if ("DEC_AUTH".equals(paymentMethod.getCardHolderName())) {
			throw new CardDeclinedException(TESTING_EXCEPTION_MESSAGE);
		}
		if ("COM_AUTH".equals(paymentMethod.getCardHolderName())) {
			throw new CardErrorException(TESTING_EXCEPTION_MESSAGE);
		}
		
		return createAuthorizationResponse(getMerchantReferenceId(), getAuthCode(), null, null, authorizationTransactionRequest.getMoney());
	}

	/**
	 * Reverse a previous pre-authorization. This can only be executed on Visas using the "Vital" processor and authorizations cannot be reversed
	 * using the test server and card info because the auth codes are not valid (Cybersource).
	 * 
	 * @param payment the payment that was previously pre-authorized
	 */
	@Override
	public void reversePreAuthorization(final OrderPaymentDto payment) {
		if (reversePreAuthorizeFailFlag) {
			throw new CardExpiredException("Card has expired meanwhile");
		}

		if ("EXP_REV_AUTH".equals(payment.getCardHolderName())) {
			throw new CardExpiredException(TESTING_EXCEPTION_MESSAGE);
		}
		if ("DEC_REV_AUTH".equals(payment.getCardHolderName())) {
			throw new CardDeclinedException(TESTING_EXCEPTION_MESSAGE);
		}
		if ("COM_REV_AUTH".equals(payment.getCardHolderName())) {
			throw new CardErrorException(TESTING_EXCEPTION_MESSAGE);
		}

		payment.setReferenceId(getMerchantReferenceId());
	}

	/**
	 * Void a previous capture or credit. Can usually only be executed on the same day of the original transaction.
	 * 
	 * @param payment the payment to be voided
	 */
	@Override
	public void voidCaptureOrCredit(final OrderPaymentDto payment) {
		payment.setReferenceId(getMerchantReferenceId());
	}

	@Override
	public CaptureTransactionResponse capture(final CaptureTransactionRequest captureTransactionRequest) {
		if (captureFailFlag) {
			throw new CardErrorException("credit card reported stolen");
		}
		return createCaptureResponse(captureTransactionRequest.getMoney(), captureTransactionRequest.getRequestToken(), 
				captureTransactionRequest.getAuthorizationCode(), captureTransactionRequest.getReferenceId());
	}

	@Override
	public void sale(final OrderPaymentDto payment, final AddressDto billingAddress, final OrderShipmentDto shipment) {
		if (saleFailFlag) {
			throw new CardErrorException("credit card reported stolen");
		}
		payment.setAuthorizationCode(getAuthCode());
		payment.setReferenceId(getMerchantReferenceId());
	}

	/**
	 * Refunds a previous capture.
	 * 
	 * @param payment the payment to be refunded
	 * @param billingAddress the billing address
	 */
	@Override
	public void refund(final OrderPaymentDto payment, final AddressDto billingAddress) {
		if ("EXP_REFUND".equals(payment.getCardHolderName())) {
			throw new CardExpiredException(TESTING_EXCEPTION_MESSAGE);
		}
		if ("DEC_REFUND".equals(payment.getCardHolderName())) {
			throw new CardDeclinedException(TESTING_EXCEPTION_MESSAGE);
		}
		if ("COM_REFUND".equals(payment.getCardHolderName())) {
			throw new CardErrorException(TESTING_EXCEPTION_MESSAGE);
		}
		payment.setReferenceId(getMerchantReferenceId());
		payment.setAuthorizationCode(NULL_REFUND_ID_PREFIX + randomGenerator.nextInt());
	}

	/**
	 * Get the card types supported by this payment gateway.
	 * 
	 * @return a List of card type strings (e.g. VISA)
	 */
	@Override
	public List<String> getSupportedCardTypes() {
		if (supportedCardTypes == null) {
			supportedCardTypes = this.getDefaultSupportedCardTypes();
		}
		return supportedCardTypes;
	}

	/**
	 * Get the card types supported by this payment gateway.
	 * 
	 * @return a List of all credit card types supported by NullPaymentGateway.
	 */
	private List<String> getDefaultSupportedCardTypes() {
		final List<String> cardTypesList = new ArrayList<String>();
		cardTypesList.add("Visa");
		cardTypesList.add("MasterCard");
		cardTypesList.add("American Express");
		return cardTypesList;
	}

	private String getAuthCode() {
		return NULL_AUTH_CODE_PREFIX + randomGenerator.nextInt();
	}

	private String getMerchantReferenceId() {
		return NULL_REFERENCE_ID_PREFIX + randomGenerator.nextInt();
	}

	/**
	 * Sets whether to fail on pre-authorize.
	 * 
	 * @param preAuthorizeFailFlag whether to fail
	 */
	public static void setFailOnPreAuthorize(final boolean preAuthorizeFailFlag) {
		NullPaymentGatewayPluginImpl.preAuthorizeFailFlag = preAuthorizeFailFlag;
	}

	/**
	 * Sets whether to fail on capture.
	 * 
	 * @param captureFailFlag whether to fail
	 */
	public static void setFailOnCapture(final boolean captureFailFlag) {
		NullPaymentGatewayPluginImpl.captureFailFlag = captureFailFlag;
	}

	/**
	 * Sets whether to fail on reverse pre-authorize.
	 * 
	 * @param reversePreAuthorizeFailFlag whether to fail
	 */
	public static void setFailOnReversePreAuthorization(final boolean reversePreAuthorizeFailFlag) {
		NullPaymentGatewayPluginImpl.reversePreAuthorizeFailFlag = reversePreAuthorizeFailFlag;
	}

	/**
	 * Sets whether to fail on sale.
	 * 
	 * @param saleFailFlag whether to fail
	 */
	public static void setFailOnSale(final boolean saleFailFlag) {
		NullPaymentGatewayPluginImpl.saleFailFlag = saleFailFlag;
	}

	@Override
	public PayerAuthenticationEnrollmentResultDto checkEnrollment(final ShoppingCartDto shoppingCart, final OrderPaymentDto payment) {
		return new PayerAuthenticationEnrollmentResultDtoImpl();
	}

	@Override
	public Collection<String> getConfigurationParameters() {
		return new ArrayList<String>();
	}
}
