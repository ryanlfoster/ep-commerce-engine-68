package com.elasticpath.plugin.payment.capabilities;

import java.util.Map;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.CardDetailsPaymentMethod;
import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;
import com.elasticpath.plugin.payment.transaction.PaymentTransactionResponse;

/**
 * A capability that Payment Gateways which support PaypalExpress should implement.
 */
public interface PaypalExpressCapability extends PaymentGatewayCapability {
	/**
	 * Start the ExpressCheckout for Authorization using the EC Mark method. Redirect customer to paypal site to log in and select fund source.
	 *
	 * @param shoppingCart the shopping cart
	 * @param returnUrl the url to return from paypal upon completion the payment action.
	 * @param cancelUrl the url to return to if the user decide to cancel the payment action.
	 * @return the token string.
	 */
	String setExpressMarkCheckout(ShoppingCartDto shoppingCart, String returnUrl, String cancelUrl);

	/**
	 * Start the ExpressCheckout for Authorization using the EC Shortcut method. Redirect customer to paypal site to log in and select fund source.
	 *
	 * @param shoppingCart the shopping cart
	 * @param returnUrl the url to return from paypal upon completion the payment action.
	 * @param cancelUrl the url to return to if the user decide to cancel the payment action.
	 * @return the token string.
	 */
	String setExpressShortcutCheckout(ShoppingCartDto shoppingCart, String returnUrl, String cancelUrl);

	/**
	 * Get the payerinfo before start the payment.
	 *
	 * @param token the token from paypal when setting up express checkout.
	 * @return Map of the returned key-value pairs.
	 * @throws com.elasticpath.plugin.payment.exceptions.PaymentGatewayException if anything goes wrong.
	 */
	Map<String, String> getExpressCheckoutDetails(String token);

	/**
	 * Pre-authorize a payment on an existing order.
	 *
	 * @param orderPayment the payment to be captured
	 * @param inResponse the response object to update
	 * @param money the money to charge
	 * @return PaymentTransactionResponse the gateway response
	 * @throws com.elasticpath.plugin.payment.exceptions.PaymentGatewayException if the payment processing fails
	 */
	PaymentTransactionResponse authorizeOrder(CardDetailsPaymentMethod orderPayment, PaymentTransactionResponse inResponse,
			Money money);

	/**
	 * Verify sufficient funds exist for a payment, but don't put a hold on them.
	 *
	 * @param payment the payment to be verified
	 * @param billingAddress the name and address of the person being billed
	 * @param inResponse the response object to update
	 * @param money the money to charge
	 * @return PaymentTransactionResponse the response from the gateway
	 * @throws com.elasticpath.plugin.payment.exceptions.CardDeclinedException if the card is declined
	 * @throws com.elasticpath.plugin.payment.exceptions.CardExpiredException if the card has expired
	 * @throws com.elasticpath.plugin.payment.exceptions.CardErrorException if there was an error processing the given information
	 * @throws com.elasticpath.plugin.payment.exceptions.PaymentGatewayException if the payment processing fails
	 */
	PaymentTransactionResponse order(CardDetailsPaymentMethod payment, Money money, PaymentTransactionResponse inResponse,
			AddressDto billingAddress);
}
