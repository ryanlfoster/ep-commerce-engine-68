/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPrice;
import com.elasticpath.sfweb.formbean.BillingAndReviewFormBean;
import com.elasticpath.sfweb.formbean.OrderPaymentFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;

/**
 * Bean used to provide and capture information from the Billing and Review screen.
 */
@SuppressWarnings("PMD.UseStringBufferForStringAppends")
public class BillingAndReviewFormBeanImpl extends EpFormBeanImpl implements BillingAndReviewFormBean {

	private Locale locale;
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private static final int MONTHS_IN_YEAR = 12;

	private static final int NUM_EXPIRY_YEARS = 18;

	private static Map<String, String> monthMap = null;

	private Address shippingAddress;

	private Address billingAddress;

	private boolean validateCvv2;

	private OrderPaymentFormBean orderPaymentFormBean;

	private List<String> cardTypes;

	private Customer customer;
	
	private long selectedExistingCardUid;
	
	private String selectedPaymentOption;
	
	private List<CustomerCreditCard> existingCreditCards;
	
	private boolean payPalEnabled;
	
	private boolean saveCreditCard = false;

	private final List<ShoppingItemFormBean> shoppingItems = new ArrayList<ShoppingItemFormBean>();
	
	private Map<Quantity, FrequencyAndRecurringPrice> frequencyMap;
	
	static {
		monthMap = new LinkedHashMap<String, String>();
		for (int i = 1; i <= MONTHS_IN_YEAR; i++) {
			String month = String.valueOf(i);
			if (month.length() == 1) {
				month = "0" + month;
			}
			monthMap.put(month, month);
		}
	}

	/**
	 * Default constructor.
	 */
	public BillingAndReviewFormBeanImpl() {
		super();
		this.selectedExistingCardUid = 0;
		this.selectedPaymentOption = BillingAndReviewFormBean.PAYMENT_OPTION_NEW_CREDIT_CARD;
		this.existingCreditCards = new ArrayList<CustomerCreditCard>();
	}
	
	/**
	 * Set the shipping address.
	 * 
	 * @param shippingAddress the shipping address
	 */
	public void setShippingAddress(final Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	/**
	 * Get the shipping address.
	 * 
	 * @return the shipping address
	 */
	public Address getShippingAddress() {
		return this.shippingAddress;
	}

	/**
	 * Set the billing address.
	 * 
	 * @param billingAddress the billing address
	 */
	public void setBillingAddress(final Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	/**
	 * Get the billing address.
	 * 
	 * @return the billing address
	 */
	public Address getBillingAddress() {
		return this.billingAddress;
	}

	/**
	 * Get the orderPayment used to collect payment information.
	 * 
	 * @return the orderPayment
	 */
	public OrderPaymentFormBean getOrderPaymentFormBean() {
		return this.orderPaymentFormBean;
	}

	/**
	 * Set the orderPayment used to collect payment information.
	 * 
	 * @param orderPaymentFormBean the orderPayment
	 */
	public void setOrderPaymentFormBean(final OrderPaymentFormBean orderPaymentFormBean) {
		this.orderPaymentFormBean = orderPaymentFormBean;
	}

	/**
	 * Get a map of the numbers of months of the year for the Spring form input for credit card expiry.
	 * 
	 * @return map of month digit strings
	 */
	public Map<String, String> getMonthMap() {
		return monthMap;
	}

	/**
	 * Get a map of years for the Spring form input for credit card expiry.
	 * 
	 * @return map of year strings
	 */
	public Map<String, String> getYearMap() {
		final Map<String, String> yearMap = new LinkedHashMap<String, String>();
		final GregorianCalendar calendar = new GregorianCalendar();
		final int currentYear = calendar.get(Calendar.YEAR);
		for (int i = 0; i <= NUM_EXPIRY_YEARS; i++) {
			final String yearToAdd = String.valueOf(currentYear + i);
			yearMap.put(yearToAdd, yearToAdd);
		}
		return yearMap;
	}

	/**
	 * Get a map of the card types for the Spring input form.
	 * 
	 * @return the credit card type map
	 */
	public Map<String, String> getCardTypeMap() {
		final Map<String, String> cardTypeMap = new LinkedHashMap<String, String>();
		if (cardTypes == null) {
			return cardTypeMap;
		}

		for (final String currCardType : this.cardTypes) {
			cardTypeMap.put(currCardType, currCardType);
		}

		return cardTypeMap;
	}

	/**
	 * Set the card types available as a list of Strings.
	 * 
	 * @param cardTypes a list of String card types
	 */
	public void setCardTypes(final List<String> cardTypes) {
		this.cardTypes = cardTypes;
	}

	/**
	 * True if the cvv2 code (Card Security Code) should be validated.
	 * 
	 * @return see above
	 */
	public boolean isValidateCvv2() {
		return validateCvv2;
	}

	/**
	 * Set whether or not the cvv2 code should be validated.
	 * 
	 * @param validate true if the cvv2 should be validated.
	 */
	public void setValidateCvv2(final boolean validate) {
		this.validateCvv2 = validate;
	}

	/**
	 * Gets the Customer.
	 * 
	 * @return the Customer.
	 */
	public Customer getCustomer() {
		return this.customer;
	}

	/**
	 * Sets the Customer.
	 * 
	 * @param customer Customer.
	 */
	public void setCustomer(final Customer customer) {
		this.customer = customer;
	}

	/**
	 * Returns the id of the existing credit card selected by the user.
	 * @return the credit card UID
	 */
	public long getSelectedExistingCreditCardUid() {
		return this.selectedExistingCardUid;
	}
	
	/**
	 * Sets the id of the existing credit card selected by the user.
	 * Setting this value sets the payment option to EXISTING_CREDIT_CARD.
	 * @param selectedExistingCardUid the credit card UID
	 */
	public void setSelectedExistingCreditCardUid(final long selectedExistingCardUid) {
		this.selectedExistingCardUid = selectedExistingCardUid;
		this.selectedPaymentOption = BillingAndReviewFormBean.PAYMENT_OPTION_EXISTING_CREDIT_CARD;
	}
	
	/**
	 * Get the payment option that the user has selected.
	 * @return a constant value indicating the user's selection
	 */
	public String getSelectedPaymentOption() {
		return this.selectedPaymentOption;
	}
	
	/**
	 * Set the payment option that the user has selected. This value will be
	 * re-set to EXISTING_CREDIT_CARD if setSelectedExistingCreditCardUid(card) is invoked.
	 * @param selectedPaymentOption a payment option constant defined in this interface
	 */
	public void setSelectedPaymentOption(final String selectedPaymentOption) {
		this.selectedPaymentOption = selectedPaymentOption;
	}
	
	/**
	 * Retrieve the collection of existing credit cards that the customer may
	 * use for payment.
	 * @return a list of <code>CustomerCreditCard</code>s
	 */
	public List<CustomerCreditCard> getExistingCreditCards() {
		return existingCreditCards;
	}

	/**
	 * Set the collection of existing credit cards that the customer may
	 * use for payment.
	 * @param existingCreditCards a list of <code>CustomerCreditCard</code>s
	 */
	public void setExistingCreditCards(final List<CustomerCreditCard> existingCreditCards) {
		this.existingCreditCards = existingCreditCards;
	}
	
	/**
	 * Returns the existing credit card selected by the user. 
	 * @return the <code>CustomerCreditCard</code> or null if there
	 * is no valid selected credit card.
	 */
	public CustomerCreditCard getSelectedExistingCreditCard() {
		for (final CustomerCreditCard currCreditCard : this.existingCreditCards) {
			if (currCreditCard.getUidPk() == this.selectedExistingCardUid) {
				return currCreditCard;
			}
		}
		return null;
	}
	
	/**
	 * Indicates whether the user wishes to save a new credit card.
	 * 
	 * @return true if the card information is to be saved, false otherwise
	 */
	public boolean isSaveCreditCardForFutureUse() {
		return this.saveCreditCard;
	}

	/**
	 * Set whether new credit card information should be saved for future use.
	 * 
	 * @param saveCreditCard set to true to indicate that the new credit card information should be saved
	 */
	public void setSaveCreditCardForFutureUse(final boolean saveCreditCard) {
		this.saveCreditCard = saveCreditCard;
	}

	/**
	 *
	 * @return true if PayPal Express is enabled
	 */
	public boolean isPayPalEnabled() {
		return payPalEnabled;
	}

	/**
	 *
	 * @param payPalEnabled flag indicating if PayPal Express is enabled or not
	 */
	public void setPayPalEnabled(final boolean payPalEnabled) {
		this.payPalEnabled = payPalEnabled;
	}
	
	@Override
	public List<ShoppingItemFormBean> getCartItems() {
		return shoppingItems; 
	}
	
	@Override
	public void addShoppingItemFormBean(final ShoppingItemFormBean shoppingItemFormBean) {
		shoppingItems.add(shoppingItemFormBean);
	}
	
	/**
	 * Get the locale of the customer corresponding to the shopping cart.
	 * 
	 * @return the <code>Locale</code>
	 */
	public Locale getLocale() {
		return locale;
	}
	
	/**
	 * Sets the locale.
	 * 
	 * @param locale a locale
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	@Override
	public ShoppingItemFormBean getShoppingItemFormBeanBy(final long uidPk) {
		for (ShoppingItemFormBean bean : getCartItems()) {
			if (bean.getUpdateShoppingItemUid() == uidPk) {
				return bean;
			}
		}
		
		return null;
	}
	
	@Override
	public Map<Quantity, FrequencyAndRecurringPrice> getFrequencyMap() {
	
		return this.frequencyMap;
	}
	

	@Override
	public void setFrequencyMap(final Map<Quantity, FrequencyAndRecurringPrice> frequencyMap) {
		this.frequencyMap = frequencyMap;
	}
	
}
