/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean.impl;

import com.elasticpath.sfweb.formbean.CreditCardFormBean;

/**
 * Form bean for creating and editing customer credit cards.
 */
public class CreditCardFormBeanImpl extends EpFormBeanImpl implements CreditCardFormBean {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private String cardType;

	private String cardHolderName;

	private String cardNumber;

	private String maskedCardNumber;

	private String expiryYear;

	private String expiryMonth;

	private String startYear;

	private String startMonth;

	private String securityCode;

	private int issueNumber;

	private boolean defaultCard = false;

	private long cardUidPk;

	private boolean requestFromCheckout = false;

	@Override
	public String getCardHolderName() {
		return cardHolderName;
	}

	@Override
	public void setCardHolderName(final String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	@Override
	public String getCardNumber() {
		return cardNumber;
	}

	@Override
	public void setCardNumber(final String cardNumber) {
		this.cardNumber = cardNumber;
	}

	@Override
	public String getCardType() {
		return cardType;
	}

	@Override
	public void setCardType(final String cardType) {
		this.cardType = cardType;
	}

	@Override
	public boolean isDefaultCard() {
		return defaultCard;
	}

	@Override
	public void setDefaultCard(final boolean defaultCard) {
		this.defaultCard = defaultCard;
	}

	@Override
	public String getExpiryMonth() {
		return expiryMonth;
	}

	@Override
	public void setExpiryMonth(final String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	@Override
	public String getExpiryYear() {
		return expiryYear;
	}

	@Override
	public void setExpiryYear(final String expiryYear) {
		this.expiryYear = expiryYear;
	}

	@Override
	public int getIssueNumber() {
		return issueNumber;
	}

	@Override
	public void setIssueNumber(final int issueNumber) {
		this.issueNumber = issueNumber;
	}

	@Override
	public String getStartMonth() {
		return startMonth;
	}

	@Override
	public void setStartMonth(final String startMonth) {
		this.startMonth = startMonth;
	}

	@Override
	public String getStartYear() {
		return startYear;
	}

	@Override
	public void setStartYear(final String startYear) {
		this.startYear = startYear;
	}

	@Override
	public long getCardUidPk() {
		return cardUidPk;
	}

	@Override
	public void setCardUidPk(final long uidPk) {
		this.cardUidPk = uidPk;
	}

	@Override
	public String getSecurityCode() {
		return securityCode;
	}

	@Override
	public void setSecurityCode(final String securityCode) {
		this.securityCode = securityCode;
	}

	@Override
	public String getMaskedCardNumber() {
		return this.maskedCardNumber;
	}

	@Override
	public void setMaskedCardNumber(final String maskedCardNumber) {
		this.maskedCardNumber = maskedCardNumber;
	}

	@Override
	public boolean isRequestFromCheckout() {
		return this.requestFromCheckout;
	}

	@Override
	public void setRequestFromCheckout(final boolean requestFromCheckout) {
		this.requestFromCheckout = requestFromCheckout;
	}
}
