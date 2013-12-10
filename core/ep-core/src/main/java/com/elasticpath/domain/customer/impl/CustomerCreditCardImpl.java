/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openjpa.persistence.jdbc.VersionStrategy;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.persistence.api.EntityUtils;

/**
 * The default implementation of <code>Customer</code>.
 */
@Entity
@VersionStrategy("state-comparison")
@Table (name = CustomerCreditCardImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CustomerCreditCardImpl extends AbstractPaymentMethodImpl implements CustomerCreditCard {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final int MEDIUM_TEXT_LENGTH = 100;
	private static final int YEAR_LENGTH = 4;
	private static final int CARD_TYPE_LENGTH = 50;
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCUSTOMERCREDITCARD";

	private String cardType;

	private String cardHolderName;

	private String cardNumber;

	private String expiryYear;

	private String expiryMonth;

	private String startYear;

	private String startMonth;

	private int issueNumber;

	private boolean defaultCard = false;

	private CustomerAddress billingAddress;

	private String securityCode;

	private String guid;

	/**
	 * The default constructor.
	 */
	public CustomerCreditCardImpl() {
		super();
	}

	/**
	 * Set default values for those fields need default values.
	 */
	@Override
	public void initialize() {
		initializeGuid();
	}

	/**
	 * Initializes the GUID.
	 */
	protected void initializeGuid() {
		EntityUtils.initializeGuid(this);
	}

	/**
	 * @return the billingAddress
	 */
	@ManyToOne (targetEntity = CustomerAddressImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "BILLING_ADDRESS_UID")
	@ForeignKey(name = "tcustomercreditcard_ibfk_2")
	public CustomerAddress getBillingAddress() {
		return billingAddress;
	}

	/**
	 * @param billingAddress the billingAddress to set
	 */
	public void setBillingAddress(final CustomerAddress billingAddress) {
		this.billingAddress = billingAddress;
	}

	/**
	 * @return the cardHolderName
	 */
	@Basic
	@Column (name = "CARD_HOLDER_NAME", length = MEDIUM_TEXT_LENGTH, nullable = false)
	public String getCardHolderName() {
		return cardHolderName;
	}

	/**
	 * @param cardHolderName the cardHolderName to set
	 */
	public void setCardHolderName(final String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	/**
	 * @return the cardNumber
	 */
	@Basic
	@Column (name = "CARD_NUMBER")
	public String getCardNumber() {
		return cardNumber;
	}

	/**
	 * @param cardNumber the cardNumber to set
	 */
	public void setCardNumber(final String cardNumber) {
		this.cardNumber = cardNumber;
	}

	/**
	 * @return the cardType
	 */
	@Basic
	@Column (name = "CARD_TYPE", length = CARD_TYPE_LENGTH)
	public String getCardType() {
		return cardType;
	}

	/**
	 * @param cardType the cardType to set
	 */
	public void setCardType(final String cardType) {
		this.cardType = cardType;
	}

	/**
	 * Note: Use {@link com.elasticpath.domain.customer.Customer#getPreferredCreditCard()} to get the default credit card directly from the Customer.
	 * @return true if the default flag is set on this card, false otherwise.
	 */
	@Basic
	@Column (name = "DEFAULT_CARD")
	public boolean isDefaultCard() {
		return defaultCard;
	}

	/**
	 * @param defaultCard the defaultCard to set
	 */
	public void setDefaultCard(final boolean defaultCard) {
		this.defaultCard = defaultCard;
	}

	/**
	 * @return the expiryMonth
	 */
	@Basic
	@Column (name = "EXPIRY_MONTH", length = 2)
	public String getExpiryMonth() {
		return expiryMonth;
	}

	/**
	 * @param expiryMonth the expiryMonth to set
	 */
	public void setExpiryMonth(final String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	/**
	 * @return the expiryYear
	 */
	@Basic
	@Column (name = "EXPIRY_YEAR", length = YEAR_LENGTH)
	public String getExpiryYear() {
		return expiryYear;
	}

	/**
	 * @param expiryYear the expiryYear to set
	 */
	public void setExpiryYear(final String expiryYear) {
		this.expiryYear = expiryYear;
	}

	/**
	 * @return the issueNumber
	 */
	@Basic
	@Column (name = "ISSUE_NUMBER")
	public int getIssueNumber() {
		return issueNumber;
	}

	/**
	 * @param issueNumber the issueNumber to set
	 */
	public void setIssueNumber(final int issueNumber) {
		this.issueNumber = issueNumber;
	}

	/**
	 * @return the startMonth
	 */
	@Basic
	@Column (name = "START_MONTH", length = 2)
	public String getStartMonth() {
		return startMonth;
	}

	/**
	 * @param startMonth the startMonth to set
	 */
	public void setStartMonth(final String startMonth) {
		this.startMonth = startMonth;
	}

	/**
	 * @return the startYear
	 */
	@Basic
	@Column (name = "START_YEAR", length = YEAR_LENGTH)
	public String getStartYear() {
		return startYear;
	}

	/**
	 * @param startYear the startYear to set
	 */
	public void setStartYear(final String startYear) {
		this.startYear = startYear;
	}

	/**
	 * Decrypts and returns the full credit card number. Access to this method should be restricted!
	 *
	 * @return the decrypted credit card number
	 */
	@Transient
	public String getUnencryptedCardNumber() {
		return getCreditCardEncrypter().decrypt(getCardNumber());
	}

	/**
	 * Decrypts and returns the masked credit card number: ************5381. Useful for displaying in receipts, GUI, order history, etc.
	 *
	 * @return the masked credit card number
	 */
	@Transient
	public String getMaskedCardNumber() {
		return getCreditCardEncrypter().decryptAndMask(getCardNumber());
	}

	/**
	 * Returns the credit card encrypter.
	 *
	 * @return the credit card encrypter
	 */
	@Transient
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	private CreditCardEncrypter getCreditCardEncrypter() {
		return ElasticPathImpl.getInstance().getBean(ContextIdNames.CREDIT_CARD_ENCRYPTER);
	}

	/**
	 * Encrypts the credit card number.
	 */
	public void encrypt() {
		if (getCardNumber() != null) {
			setCardNumber(getCreditCardEncrypter().encrypt(getCardNumber()));
		}
	}

	/**
	 * Set the 3 or 4 digit security code from the back of the card.
	 * This value IS NOT persistent.
	 * @param securityCode the security code
	 */
	public void setSecurityCode(final String securityCode) {
		this.securityCode = securityCode;
	}

	/**
	 * Get the 3 or 4 digit security code from the back of the card.
	 * This value cannot be persisted and will not be available unless
	 * the user has specified it.
	 * @return the security code
	 */
	@Transient
	public String getSecurityCode() {
		return securityCode;
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Return the guid.
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	/**
	 * Returns <code>true</code> if this category equals to the given object.
	 *
	 * @param obj the given object
	 * @return <code>true</code> if this category equals to the given object
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CustomerCreditCardImpl)) {
			return false;
		}

		// Compare object id
		if (this == obj) {
			return true;
		}

		final CustomerCreditCardImpl other = (CustomerCreditCardImpl) obj;
		final boolean detailsSame = StringUtils.equals(cardNumber, other.cardNumber)
				&& StringUtils.equals(cardHolderName, other.cardHolderName)
				&& StringUtils.equals(expiryMonth, other.expiryMonth)
				&& StringUtils.equals(expiryYear, other.expiryYear)
				&& StringUtils.equals(cardType, other.cardType);

		return detailsSame || ObjectUtils.equals(getGuid(), other.getGuid());
	}

	/**
	 * Generate the hash code.
	 *
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ObjectUtils.hashCode(cardNumber);
		result = prime * result + ObjectUtils.hashCode(cardHolderName);
		result = prime * result + ObjectUtils.hashCode(expiryMonth);
		result = prime * result + ObjectUtils.hashCode(expiryYear);
		result = prime * result + ObjectUtils.hashCode(cardType);

		return result;
	}

	/**
	 * Copies all the fields from another credit card into this credit card.
	 * Card number is optional, as it may have already been encrypted.
	 *
	 * @param creditCard the credit card from which to copy fields
	 * @param includeNumber specify whether to include the card number in the copy
	 */
	public void copyFrom(final CustomerCreditCard creditCard, final boolean includeNumber) {
		setBillingAddress(creditCard.getBillingAddress());
		setCardHolderName(creditCard.getCardHolderName());
		setCardType(creditCard.getCardType());
		setExpiryMonth(creditCard.getExpiryMonth());
		setExpiryYear(creditCard.getExpiryYear());
		setIssueNumber(creditCard.getIssueNumber());
		setSecurityCode(creditCard.getSecurityCode());
		setStartMonth(creditCard.getStartMonth());
		setStartYear(creditCard.getStartYear());
		if (includeNumber) {
			setCardNumber(creditCard.getCardNumber());
		}
	}
}