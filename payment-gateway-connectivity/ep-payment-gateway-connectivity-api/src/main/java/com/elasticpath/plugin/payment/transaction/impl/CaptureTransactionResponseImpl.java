package com.elasticpath.plugin.payment.transaction.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;

/**
 * Implementation of the {@link CaptureTransactionResponse}.
 */
public class CaptureTransactionResponseImpl extends PaymentTransactionResponseImpl 
		implements CaptureTransactionResponse {
	
	/**
	 * No-args constructor.
	 */
	public CaptureTransactionResponseImpl() {
		//Empty consttructor.
	}
	
	/**
	 * Constructor.
	 *
	 * @param money the {@link Money} to set on this {@link CaptureTransactionResponseImpl}
	 */
	public CaptureTransactionResponseImpl(final Money money) {
		this.money = money;
	}
	
	private Money money;
	
	@Override
	public Money getMoney() {
		return money;
	}

	@Override
	public void setMoney(final Money money) {
		this.money = money;
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
