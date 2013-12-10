package com.elasticpath.plugin.payment.capabilities;

import java.util.List;

import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.PayerAuthenticationEnrollmentResultDto;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;

/**
 * A capability that Payment Gateways which support the processing of credit cards should implement.
 */
public interface CreditCardCapability extends PaymentGatewayCapability {
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
	 * @param shoppingCart the the shoppingCartDto which has total amount and currency code.
	 * @param payment orderPayment.
	 * @return result of enrollment checking.
	 */
	PayerAuthenticationEnrollmentResultDto checkEnrollment(ShoppingCartDto shoppingCart, OrderPaymentDto payment);

	/**
	 * Validate the authentication.
	 * @param payment orderPayment.
	 * @param paRes the paRes from issuing bank.
	 * @return boolean validation.
	 */
	boolean validateAuthentication(OrderPaymentDto payment, String paRes);

	/**
	 * Get the card types supported by this payment gateway.
	 *
	 * @return a List of card type strings (e.g. VISA)
	 */
	List<String> getSupportedCardTypes();
}
