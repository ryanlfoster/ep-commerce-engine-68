package com.elasticpath.plugin.payment.dto.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.PayerAuthValidationValueDto;

/**
 * Implementation of {@link OrderPaymentDto}. Used in Payment Gateways.
 */
public class OrderPaymentDtoImpl implements OrderPaymentDto {
	
	private String cardType;

	private String cardHolderName;
	
	private String cardNumber;

	private String expiryMonth;

	private String expiryYear;

	private Date startDate;

	private String issueNumber;

	private String cvv2Code;

	private BigDecimal amount;

	private String authorizationCode;

	private String referenceId;

	private String requestToken;

	private String currencyCode;

	private String email;

	private String transactionType;

	private String gatewayToken;
	
	private String token;

	private String ipAddress;

	private PayerAuthValidationValueDto payerAuthValidationValue;
	
	@Override
	public String getCardType() {
		return cardType;
	}

	@Override
	public void setCardType(final String cardType) {
		this.cardType = cardType;
	}

	@Override
	public String getCardHolderName() {
		return cardHolderName;
	}

	@Override
	public void setCardHolderName(final String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	@Override
	public void setUnencryptedCardNumber(final String number) {
		cardNumber = number;
	}

	@Override
	public String getUnencryptedCardNumber() {
		return cardNumber;
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
	public String getExpiryMonth() {
		return expiryMonth;
	}

	@Override
	public void setExpiryMonth(final String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public String getIssueNumber() {
		return issueNumber;
	}

	@Override
	public void setIssueNumber(final String issueNumber) {
		this.issueNumber = issueNumber;
	}

	@Override
	public String getCvv2Code() {
		return cvv2Code;
	}

	@Override
	public void setCvv2Code(final String cvv2Code) {
		this.cvv2Code = cvv2Code;
	}

	@Override
	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	@Override
	public String getReferenceId() {
		return referenceId;
	}

	@Override
	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	@Override
	public String getRequestToken() {
		return requestToken;
	}

	@Override
	public void setRequestToken(final String requestToken) {
		this.requestToken = requestToken;
	}

	@Override
	public String getAuthorizationCode() {
		return authorizationCode;
	}

	@Override
	public void setAuthorizationCode(final String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	@Override
	public String getCurrencyCode() {
		return currencyCode;
	}

	@Override
	public void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(final String email) {
		this.email = email;
	}

	@Override
	public String getTransactionType() {
		return transactionType;
	}

	@Override
	public void setTransactionType(final String transactionType) {
		this.transactionType = transactionType;
	}

	@Override
	public void setGatewayToken(final String token) {
		gatewayToken = token;
	}

	@Override
	public String getGatewayToken() {
		return gatewayToken;
	}

	@Override
	public String getIpAddress() {
		return ipAddress;
	}

	public void setValue(final String token) {
		this.token = token;
	}

	@Override
	public String getValue() {
		return token;
	}
	
	@Override
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public String getCardNumber() {
		return cardNumber;
	}

	@Override
	public PayerAuthValidationValueDto getPayerAuthValidationValueDto() {
		return payerAuthValidationValue;
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
