package com.elasticpath.plugin.payment.transaction.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.transaction.PaymentTransactionRequest;

/**
 * Implementation of the {@link PaymentTransactionRequest}.
 */
public class PaymentTransactionRequestImpl implements PaymentTransactionRequest {

	private String merchantReferenceId;
	
	/**
	 * No-args constructor.
	 */
	public PaymentTransactionRequestImpl() {
		//Empty constructor
	}
	
	/**
	 * Constructor.
	 *
	 * @param merchantReferenceId the merchant reference id
	 */
	public PaymentTransactionRequestImpl(final String merchantReferenceId) {
		this.merchantReferenceId = merchantReferenceId;
	}
	
	@Override
	public String getReferenceId() {
		return merchantReferenceId;
	}

	@Override
	public void setReferenceId(final String merchantReferenceId) {
		this.merchantReferenceId = merchantReferenceId;
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
