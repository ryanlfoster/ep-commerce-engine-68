package com.elasticpath.domain.payment;

import java.util.List;

import com.elasticpath.domain.misc.PayerAuthenticationEnrollmentResult;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Represents a credit card payment processing gateway such as Verisign or Cybersource.
 */
public interface CreditCardPaymentGateway extends PaymentGateway {
	/**
	 * Get the card types supported by this payment gateway.
	 *
	 * @return a List of card type strings (e.g. VISA)
	 */
	List<String> getSupportedCardTypes();

	/**
	 * True if this gateway will validate the Cvv2 (Security Code).
	 *
	 * @return True if this gateway will validate the Cvv2 (Security Code)
	 */
	boolean isCvv2ValidationEnabled();

	/**
	 * Set whether the payment gateway should validate the Cvv2.
	 *
	 * @param validate true if the payment gateway should validate the Cvv2.
	 */
	void setValidateCvv2(boolean validate);
	
	/**
	 * Check the card account enrollment.
	 * @param shoppingCart the shoppingCart.
	 * @param payment orderPayment.
	 * @return result of enrollment checking.
	 */
	PayerAuthenticationEnrollmentResult checkEnrollment(final ShoppingCart shoppingCart, final OrderPayment payment);

	/**
	 * Validate the authentication. 
	 * @param payment orderPayment.
	 * @param paRes the paRes from issuing bank.
	 * @return boolean validation.
	 */
	boolean validateAuthentication(final OrderPayment payment, final String paRes);

}
