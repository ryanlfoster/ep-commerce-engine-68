/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import java.math.BigDecimal;
import java.util.Date;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.PayerAuthValidationValue;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Represents customer payment information.
 */
public interface OrderPayment extends Persistable {

	/**
	 * The orderPayment transaction type of fund authorization.
	 */
	String AUTHORIZATION_TRANSACTION = "Authorization";

	/**
	 * The orderPayment transaction type of fund capture.
	 */
	String CAPTURE_TRANSACTION = "Capture";

	/**
	 * The orderPayment transaction type of fund verification.
	 */
	String ORDER_TRANSACTION = "Order";

	/**
	 * The orderPayment transaction type of refund.
	 */
	String CREDIT_TRANSACTION = "Credit";

	/**
	 * The orderPayment transaction type of reverse authorization.
	 */
	String REVERSE_AUTHORIZATION = "Authorization Reversal";

	/**
	 * Get the related orderShipment. The ordershipment maybe null when the payment is on the order level and not related to a shipment.
	 * 
	 * @return the orderShipment
	 */
	OrderShipment getOrderShipment();

	/**
	 * Set the orderShipment for the payment.
	 * 
	 * @param orderShipment the orderShipment to set
	 */
	void setOrderShipment(final OrderShipment orderShipment);

	/**
	 * Get the date that this order was created on.
	 * 
	 * @return the created date
	 */
	Date getCreatedDate();

	/**
	 * Set the date that the order is created.
	 * 
	 * @param createdDate the start date
	 */
	void setCreatedDate(Date createdDate);

	/**
	 * Get the date that this was last modified on.
	 * 
	 * @return the last modified date
	 */
	Date getLastModifiedDate();
	
	/**
	 * Get the type/brand of the credit card (e.g. VISA).
	 * 
	 * @return the cardType
	 */
	String getCardType();

	/**
	 * Set the vendor/brand of the credit card (e.g. VISA).
	 * 
	 * @param cardType the cardType
	 */
	void setCardType(final String cardType);

	/**
	 * Set the card holder name.
	 * 
	 * @return the name on the card
	 */
	String getCardHolderName();

	/**
	 * Get the card holder name.
	 * 
	 * @param cardHolderName the name on the card
	 */
	void setCardHolderName(final String cardHolderName);

	/**
	 * Set the credit card number.
	 * 
	 * @param number the credit card number
	 */
	void setUnencryptedCardNumber(final String number);

	/**
	 * Decrypts and returns the full credit card number. Access to this method should be restricted.
	 * 
	 * @return the decrypted credit card number
	 */
	String getUnencryptedCardNumber();

	/**
	 * Decrypts and returns the masked credit card number: ************5381. Useful for displaying in receipts, GUI, order history, etc.
	 *
	 * <strong>This method is now deprecated and simply delegates to {@link #getDisplayValue()}.</strong>
	 *
	 * @see {@link #getDisplayValue()}
	 *
	 * @return the masked credit card number
	 */
	@Deprecated
	String getMaskedCardNumber();

	/**
	 * Returns the display value for the payment method used to create this order payment, for example the display value
	 * of a token, or a masked credit card number.
	 *
	 * @return the display value of the payment method
	 */
	String getDisplayValue();

	/**
	 * Sets the display value for the payment method used to create this order payment, for example the display value
	 * of a token, or a masked credit card number.
	 *
	 * @param displayValue the display value of the payment method
	 */
	void setDisplayValue(final String displayValue);
	
	/**
	 * Get the two-digit expiry date year.
	 * 
	 * @return the expiry date year
	 */
	String getExpiryYear();

	/**
	 * Set the two-digit expiry date year.
	 * 
	 * @param expiryYear the expiry date year
	 */
	void setExpiryYear(final String expiryYear);

	/**
	 * Get the two-digit expiry date month.
	 * 
	 * @return the two-digit expiry date month
	 */
	String getExpiryMonth();

	/**
	 * Set the expiry two-digit date month.
	 * 
	 * @param expiryMonth the two digit expiry date month
	 */
	void setExpiryMonth(final String expiryMonth);

	/**
	 * Get the card start date Used by some U.K. cards.
	 * 
	 * @return the start date
	 */
	Date getStartDate();

	/**
	 * Set the cart start date used by some U.K. cards.
	 * 
	 * @param startDate the start date
	 */
	void setStartDate(final Date startDate);

	/**
	 * Get the issue number used by some U.K. cards.
	 * 
	 * @return the issue number
	 */
	String getIssueNumber();

	/**
	 * Set the issue number used by some U.K. cards.
	 * 
	 * @param issueNumber the issue number
	 */
	void setIssueNumber(final String issueNumber);

	/**
	 * Get the card security code (found near the signature on the back of the card).
	 * 
	 * @return the card cvv2Code
	 */
	String getCvv2Code();

	/**
	 * Set the security code (found near the signature on the back of the card).
	 * 
	 * @param cvv2Code the security code
	 */
	void setCvv2Code(final String cvv2Code);
	
	/**
	 * Sets whether or not this <code>OrderPayment</code>'s credit card number should
	 * be encrypted before it is persisted. Specify <code>true</code> to encrypt the
	 * credit card number before persisting, <code>false</code> otherwise.
	 * <strong>Note that if false is specified, the number will be masked before it
	 * is stored unencrypted</strong>.
	 * @param shouldEncryptCreditCard whether or not the credit card should be encrypted
	 */
	void setShouldStoreEncryptedCreditCard(final boolean shouldEncryptCreditCard);
	
	/**
	 * Returns <code>true</code> if the credit card number is being persisted encrypted, <code>false</code> otherwise.
	 * Note that if <code>false</code> is returned, the number is being persisted masked. 
	 *
	 * @return whether or not the credit card is being persisted encrypted
	 */
	boolean isShouldStoreEncryptedCreditCard();
	
	/**
	 * Returns <code>true</code> to indicate that this {@link OrderPayment}'s credit card is stored encrypted;
	 * <code>false</code> to indicate that the credit card is stored masked.
	 * 
	 * @return <code>true</code> to indicate that this {@link OrderPayment}'s credit card is stored encrypted;
	 * <code>false</code> to indicate that the credit card is stored masked.
	 */
	boolean isEncryptedCreditCardStored();
	 
	/**
	 * Get the payment method. A payment method could be the name of the payment processor/gateway.
	 * 
	 * @return the payment method
	 */
	PaymentType getPaymentMethod();

	/**
	 * Set the payment method.
	 * 
	 * @param paymentMethod the payment method
	 */
	void setPaymentMethod(final PaymentType paymentMethod);

	/**
	 * Get the amount of this payment.
	 * 
	 * @return the amount as a BigDecimal
	 */
	BigDecimal getAmount();

	/**
	 * Set the amount of this payment.
	 * 
	 * @param amount the amount as a BigDecimal
	 */
	void setAmount(final BigDecimal amount);

	/**
	 * Get the reference id. The reference ID is basically a merchant reference code, and is usually set to the Order Number associated with this
	 * payment.
	 * 
	 * @return the reference id.
	 */
	String getReferenceId();

	/**
	 * Set the reference id. The reference ID is basically a merchant reference code, and is usually set to the Order Number 
	 * or shipment number associated with this payment.
	 * 
	 * @param referenceId the reference id.
	 */
	void setReferenceId(final String referenceId);

	/**
	 * Get the requestToken. The request token is a code returned by the payment processor for every request. It is used to associate any transaction
	 * with its associated follow-on transaction, such as a capture transaction with its preceding preauthorization, much like the requestId.
	 * 
	 * @return the request token.
	 */
	String getRequestToken();

	/**
	 * Set the request token.
	 * 
	 * @param requestToken the request token
	 */
	void setRequestToken(final String requestToken);

	/**
	 * Get the authorization code, returned with every transaction. It is used to associate any transaction with its associated follow-on
	 * transaction, such as a capture transaction with its preceding preauthorization, much like the requestToken.
	 * 
	 * @return the authorization code
	 */
	String getAuthorizationCode();

	/**
	 * Set the authorization code, returned with every transaction.
	 * 
	 * @param authorizationCode the authorization code
	 */
	void setAuthorizationCode(final String authorizationCode);

	/**
	 * Get the currency code (e.g. CAD or USD).
	 * 
	 * @return the currency code
	 */
	String getCurrencyCode();

	/**
	 * Set the currency code.
	 * 
	 * @param currencyCode the currency code code
	 */
	void setCurrencyCode(final String currencyCode);

	/**
	 * Get the customer's email address (Required for card processing).
	 * 
	 * @return the customer email address
	 */
	String getEmail();

	/**
	 * Set the customer's email address (Required for card processing).
	 * 
	 * @param email the customer's email address
	 */
	void setEmail(final String email);

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 * 
	 * @return the payment transaction type
	 */
	String getTransactionType();

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 * 
	 * @param transactionType the payment transaction type
	 */
	void setTransactionType(final String transactionType);

	/**
	 * Get the status of the order payment.
	 * 
	 * @return the order payment status
	 */
	OrderPaymentStatus getStatus();

	/**
	 * Set the status of the order payment.
	 * 
	 * @param status the status of the order payment
	 */
	void setStatus(OrderPaymentStatus status);

	/**
	 * Store the temporary token needed by some payment gateway, i.e. PayPal to complete the process.
	 * 
	 * @param token payment gateway token
	 */
	void setGatewayToken(final String token);

	/**
	 * Return the payment gateway token.
	 * 
	 * @return the temproary payment gateway token.
	 */
	String getGatewayToken();

	/**
	 * Get the ipAddress of the user from the Order Payment.
	 * 
	 * @return the ipAddress
	 */
	String getIpAddress();

	/**
	 * Set the users ip Address into the Order Payment.
	 * 
	 * @param ipAddress the ipAddress of the user.
	 */
	void setIpAddress(final String ipAddress);

	/**
	 * Get the gift certificate for the payment.
	 * 
	 * @return the giftCertificate
	 */
	GiftCertificate getGiftCertificate();

	/**
	 * Set the gift certificate for the payment.
	 * 
	 * @param giftCertificate the giftCertificate to set
	 */
	void setGiftCertificate(final GiftCertificate giftCertificate);

	/**
	 * Copy orderPayment's credit card info to <code>this</code> Order Payment.
	 * 
	 * @param orderPayment the source of credit card info.
	 */
	void copyCreditCardInfo(final OrderPayment orderPayment);

	/**
	 * Tells <code>this</code> to use the specified credit card.
	 * 
	 * @param creditCard the card to use for this order payment.
	 */
	void useCreditCard(final CustomerCreditCard creditCard);

	/**
	 * Extract the credit card information in this order payment into a credit card object.
	 * 
	 * @return a new credit card instance populated with the credit card information
	 */
	CustomerCreditCard extractCreditCard();

	/**
	 * Copy follow on transaction information from orderPayment to <code>this</code> Order Payment.
	 * 
	 * @param orderPayment the source of Gateway info.
	 */
	void copyTransactionFollowOnInfo(final OrderPayment orderPayment);

	/**
	 * Get the total amount money.
	 * 
	 * @return a <code>Money</code> object representing the total amount
	 */
	Money getAmountMoney();

	/**
	 * Get Payer Authentication Validate value for transaction.
	 * 
	 * @return the payerAuthValidationValue.
	 */
	PayerAuthValidationValue getPayerAuthValidationValue();

	/**
	 * Set the payerAuthValidationValue for order payment.
	 * 
	 * @param payerAuthValidationValue the payerAuthValidationValue
	 */
	void setPayerAuthValidationValue(final PayerAuthValidationValue payerAuthValidationValue);

	/**
	 * Get the encrypted credit cart number.
	 * 
	 * @return the credit card number.
	 */
	String getCardNumber();

	/**
	 * Copies the date that this was last modified.
	 * 
	 * @param authorizationPayment the payment to copy from
	 */
	void copyLastModifiedDate(OrderPayment authorizationPayment);
	
	/**
	 * Returns true if this payment is done in the subscriptions context. See SUBS-113.
	 * @return payment for subscriptions
	 * */
	boolean isPaymentForSubscriptions();

	/**
	 * Sets true if the payment if for subscriptions.
	 * @param paymentForSubscriptions the payment for subscriptions
	 * */
	void setPaymentForSubscriptions(boolean paymentForSubscriptions);

	/**
	 * Copies the relevant information across from the provided payment token into this object.
	 * @param paymentToken the payment token to use as a source
	 */
	void usePaymentToken(PaymentToken paymentToken);

	/**
	 * Copies relevant fields of this object into a {@link PaymentToken}.
	 * @return a newly created and populated {@link PaymentToken}
	 */
	PaymentToken extractPaymentToken();


}
