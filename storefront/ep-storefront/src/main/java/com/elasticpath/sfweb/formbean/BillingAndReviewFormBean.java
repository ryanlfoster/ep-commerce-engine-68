/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPrice;

/**
 * Bean used to provide and capture information from the 
 * Billing and Review screen.
 */
public interface BillingAndReviewFormBean extends EpFormBean, ShoppingItemFormBeanContainer  {

	/** 
	 * Indicates that the user wishes to enter new credit card information. 
	 */
	String PAYMENT_OPTION_NEW_CREDIT_CARD = "NewCreditCard";
	
	/** 
	 * Indicates the user wishes to pay with an existing card. When this option is
	 * used, the card UID must be specified by calling 
	 * <code>setSelectedExistingCreditCardUid</code>.
	 */ 
	String PAYMENT_OPTION_EXISTING_CREDIT_CARD = "ExistingCreditCard";
	
	/**
	 * Indicates that the user wishes to pay via paypal.
	 */
	String PAYMENT_OPTION_PAYPAL_EXPRESS = "PayPalExpress";
	
	/**
	 * Set the shipping address.
	 * @param shippingAddress the shipping address
	 */
	void setShippingAddress(final Address shippingAddress);
	
	/**
	 * Get the shipping address.
	 * @return the shipping address
	 */
	Address getShippingAddress();
	
	/**
	 * Set the billing address.
	 * @param billingAddress the billing address
	 */
	void setBillingAddress(final Address billingAddress);
	
	/**
	 * Get the billing address.
	 * @return the billing address
	 */
	Address getBillingAddress();
	
	/**
	 * Get the orderPayment form bean used to collect payment information.
	 * 
	 * @return the orderPayment
	 */
	OrderPaymentFormBean getOrderPaymentFormBean();
	
	/**
	 * Set the orderPayment form bean used to collect payment information.
	 * 
	 * @param orderPaymentFormBean the credit card
	 */
	void setOrderPaymentFormBean(final OrderPaymentFormBean orderPaymentFormBean);
	
	/**
	 * Get a map of the numbers of months of the year for the 
	 * Spring form input for credit card expiry.
	 * @return map of month digit strings
	 */
	Map<String, String> getMonthMap();
	
	/**
	 * Get a map of years for the Spring form input for
	 * credit card expiry.
	 * @return map of year strings
	 */
	Map<String, String> getYearMap();
	
	/**
	 * Get a map of the card types for the Spring input form.
	 * @return the credit card type map
	 */
	Map<String, String> getCardTypeMap();
	
	/**
	 * Set the card types available as a list of Strings.
	 * @param cardTypes a list of String card types
	 */
	void setCardTypes(final List<String> cardTypes);
	
	/**
	 * True if the cvv2 code (Card Security Code) should
	 * be validated.
	 * @return see above
	 */
	boolean isValidateCvv2();
	
	/**
	 * Set whether or not the cvv2 code should be validated.
	 * @param validate true if the cvv2 should be validated.
	 */
	void setValidateCvv2(final boolean validate);
	
	/**
	 * Gets the Customer.
	 * @return the Customer.
	 */
	Customer getCustomer();

	/**
	 * Sets the Customer.
	 * @param customer Customer.
	 */
	void setCustomer(final Customer customer);
	
	/**
	 * Returns the id of the existing credit card selected by the user.
	 * @return the credit card UID
	 */
	long getSelectedExistingCreditCardUid();
	
	/**
	 * Sets the id of the existing credit card selected by the user.
	 * Setting this value sets the payment option to EXISTING_CREDIT_CARD.
	 * @param selectedExistingCardUid the credit card UID
	 */
	void setSelectedExistingCreditCardUid(final long selectedExistingCardUid);
	
	/**
	 * Get the payment option that the user has selected.
	 * @return a constant value indicating the user's selection
	 */
	String getSelectedPaymentOption();
	
	/**
	 * Set the payment option that the user has selected. This value will be
	 * re-set to EXISTING_CREDIT_CARD if setSelectedExistingCreditCardUid(card) is invoked.
	 * @param paymentOption a payment option constant defined in this interface
	 */
	void setSelectedPaymentOption(final String paymentOption);
	
	/**
	 * Retrieve the collection of existing credit cards that the customer may
	 * use for payment.
	 * @return a list of <code>CustomerCreditCard</code>s
	 */
	List<CustomerCreditCard> getExistingCreditCards();

	/**
	 * Set the collection of existing credit cards that the customer may
	 * use for payment.
	 * @param existingCreditCards a list of <code>CustomerCreditCard</code>s
	 */
	void setExistingCreditCards(final List<CustomerCreditCard> existingCreditCards);
	
	/**
	 * Returns the existing credit card selected by the user. 
	 * @return the <code>CustomerCreditCard</code> or null if there
	 * is no valid selected credit card.
	 */
	CustomerCreditCard getSelectedExistingCreditCard();
	
	/**
	 * Indicates whether the user wishes to save a new credit card.
	 * 
	 * @return true if the card information is to be saved, false otherwise
	 */
	boolean isSaveCreditCardForFutureUse();

	/**
	 * Set whether new credit card information should be saved for future use.
	 * 
	 * @param saveCreditCard set to true to indicate that the new credit card information should be saved
	 */
	void setSaveCreditCardForFutureUse(boolean saveCreditCard);

	/**
	 *
	 * @return true if PayPal Express is enabled
	 */
	boolean isPayPalEnabled();

	/**
	 *
	 * @param payPalEnabled flag indicating if PayPal Express is enabled or not
	 */
	void setPayPalEnabled(final boolean payPalEnabled);

	/**
	 * Get the locale of the customer corresponding to the shopping cart.
	 * 
	 * @return the <code>Locale</code>
	 */
	Locale getLocale();

	/**
	 * Sets the locale.
	 * 
	 * @param locale a locale
	 */
	void setLocale(final Locale locale);
	
	/**
	 * @param uidPk the item uidPk
	 * @return the shopping item form bean corresponding to the given uidPk
	 */
	ShoppingItemFormBean getShoppingItemFormBeanBy(final long uidPk);
	
	/**
	 *	get the frequency map.
	 * @return frequency/money  map.
	 */
	Map<Quantity, FrequencyAndRecurringPrice> getFrequencyMap();

	/**
	 * set the frequency map.
	 * @param frequencyMap  a map.
	 */
	void setFrequencyMap(final Map<Quantity, FrequencyAndRecurringPrice> frequencyMap);
}
