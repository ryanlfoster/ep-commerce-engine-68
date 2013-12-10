package com.elasticpath.plugin.payment.transaction.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;

/**
 * Implementation of {@link AuthorizationTransactionResponse}. 
 */
public class AuthorizationTransactionResponseImpl extends PaymentTransactionResponseImpl 
							implements AuthorizationTransactionResponse {
	
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
