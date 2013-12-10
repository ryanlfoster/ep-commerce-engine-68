package com.elasticpath.plugin.payment.dto.impl;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.CardDetailsPaymentMethod;
import com.elasticpath.plugin.payment.dto.PayerAuthValidationValueDto;

/**
 * Implementation of {@link CardDetailsPaymentMethod}.
 */
public class CardDetailsPaymentMethodImpl implements CardDetailsPaymentMethod {
	
	private String cardType;

	private String cardHolderName;
	
	private String cardNumber;

	private String expiryMonth;

	private String expiryYear;
	
	private Date startDate;

	private String issueNumber;

	private String cvv2Code;
	
	private String referenceId;
	
	private String email;

	private String transactionType;

	private String gatewayToken;

	private String ipAddress;

	private PayerAuthValidationValueDto payerAuthValidationValue;
	
	@Override
	public String getCardType() {
		return this.cardType;
	}

	@Override
	public void setCardType(final String cardType) {
		this.cardType = cardType;
	}

	@Override
	public String getCardHolderName() {
		return this.cardHolderName;
	}

	@Override
	public void setCardHolderName(final String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	@Override
	public void setUnencryptedCardNumber(final String number) {
		this.cardNumber = number;
	}

	@Override
	public String getUnencryptedCardNumber() {
		return this.cardNumber;
	}

	@Override
	public String getExpiryYear() {
		return this.expiryYear;
	}

	@Override
	public void setExpiryYear(final String expiryYear) {
		this.expiryYear = expiryYear;
	}

	@Override
	public String getExpiryMonth() {
		return this.expiryMonth;
	}

	@Override
	public void setExpiryMonth(final String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	@Override
	public Date getStartDate() {
		return this.startDate;
	}

	@Override
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public String getIssueNumber() {
		return this.issueNumber;
	}

	@Override
	public void setIssueNumber(final String issueNumber) {
		this.issueNumber = issueNumber;
	}

	@Override
	public String getCvv2Code() {
		return this.cvv2Code;
	}

	@Override
	public void setCvv2Code(final String cvv2Code) {
		this.cvv2Code = cvv2Code;
	}

	@Override
	public String getReferenceId() {
		return this.referenceId;
	}

	@Override
	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public void setEmail(final String email) {
		this.email = email;
	}

	@Override
	public String getTransactionType() {
		return this.transactionType;
	}

	@Override
	public void setTransactionType(final String transactionType) {
		this.transactionType = transactionType;
	}

	@Override
	public String getGatewayToken() {
		return this.gatewayToken;
	}

	@Override
	public void setGatewayToken(final String token) {
		this.gatewayToken = token;
	}

	@Override
	public String getIpAddress() {
		return this.ipAddress;
	}

	@Override
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public String getCardNumber() {
		return this.cardNumber;
	}

	@Override
	public PayerAuthValidationValueDto getPayerAuthValidationValueDto() {
		return this.payerAuthValidationValue;
	}

	@Override
	public void setPayerAuthValidationValueDto(final PayerAuthValidationValueDto payerAuthValidationValue) {
		this.payerAuthValidationValue = payerAuthValidationValue;
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
