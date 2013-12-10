package com.elasticpath.plugin.payment.transaction.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.transaction.PaymentTransactionResponse;

/**
 * Basic POJO implementation of {@link PaymentTransactionResponse}.  
 */
public class PaymentTransactionResponseImpl implements PaymentTransactionResponse {
	private String referenceId;
	private String authorizationCode;
	private String requestToken;
	private String email;
	
	@Override
	public String getReferenceId() {
		return referenceId;
	}
	
	@Override
	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
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
	public String getRequestToken() {
		return requestToken;
	}
	
	@Override
	public void setRequestToken(final String requestToken) {
		this.requestToken = requestToken;
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
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
