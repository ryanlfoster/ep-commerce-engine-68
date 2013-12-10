package com.elasticpath.plugin.payment.transaction.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;

/**
 * Implementation of the {@link AuthorizationTransactionRequest}.
 */
public class AuthorizationTransactionRequestImpl extends PaymentTransactionRequestImpl 
			implements AuthorizationTransactionRequest {
	
	private PaymentMethod paymentMethod;
	private Money money;
	
	/**
	 * No-args constructor.
	 */
	public AuthorizationTransactionRequestImpl() {
		//Empty constructor
	}
	
	/**
	 * Constructor.
	 *
	 * @param paymentMethod the {@link PaymentMethod}
	 * @param money the {@link Money}
	 * @param merchantReferenceId the merchant's reference id
	 */
	public AuthorizationTransactionRequestImpl(final PaymentMethod paymentMethod, 
			final Money money, final String merchantReferenceId) {
		super(merchantReferenceId);
		this.paymentMethod = paymentMethod;
		this.money = money;
	}
	
	@Override
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	@Override
	public void setPaymentMethod(final PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

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
