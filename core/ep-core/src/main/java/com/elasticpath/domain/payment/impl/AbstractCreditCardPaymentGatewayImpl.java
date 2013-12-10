package com.elasticpath.domain.payment.impl;

import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.PayerAuthenticationEnrollmentResult;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Abstract credit card payment processing gateway. Extend this to implement specific gateways such as CyberSource or Verisign.
 */
public abstract class AbstractCreditCardPaymentGatewayImpl extends PaymentGatewayImpl implements CreditCardPaymentGateway {

	private static final long serialVersionUID = 661L;

	private List<String> supportedCardTypes;

	private boolean shouldValidateCvv2;

	/**
	 * Get the card types supported by this payment gateway.
	 *
	 * @return a List of card type strings (e.g. VISA)
	 */
	@Override
	public List<String> getSupportedCardTypes() {
		return this.supportedCardTypes;
	}

	/**
	 * Set the card types supported by this payment gateway.
	 *
	 * @param cardTypes a List of card type strings (e.g. VISA)
	 */
	public void setSupportedCardTypes(final List<String> cardTypes) {
		this.supportedCardTypes = cardTypes;
	}

	/**
	 * True if this gateway will validate the Cvv2 (Security Code).
	 *
	 * @return True if this gateway will validate the Cvv2 (Security Code)
	 */
	@Override
	public boolean isCvv2ValidationEnabled() {
		return this.shouldValidateCvv2;
	}

	/**
	 * Set whether the payment gateway should validate the Cvv2.
	 *
	 * @param validate true if the payment gateway should validate the Cvv2.
	 */
	@Override
	public void setValidateCvv2(final boolean validate) {
		this.shouldValidateCvv2 = validate;
	}

	/**
	 * Check the card account enrollment.
	 * @param shoppingCart the shoppingCart
	 * @param payment orderPayment.
	 * @return result of enrollment checking.
	 */
	@Override
	public PayerAuthenticationEnrollmentResult checkEnrollment(final ShoppingCart shoppingCart, final OrderPayment payment) {
		return getBean(ContextIdNames.PAYER_AUTHENTICATION_ENROLLMENT_RESULT);
	}
	
	/**
	 * Validate the authentication. 
	 * @param payment orderPayment.
	 * @param paRes from issuing bank.
	 * @return boolean successful value for validation.
	 */
	@Override
	public boolean validateAuthentication(final OrderPayment payment, final String paRes) {
		return true;
	}

}
