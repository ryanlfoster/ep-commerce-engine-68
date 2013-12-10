package com.elasticpath.plugin.payment.dto.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.TokenPaymentMethod;

/**
 * POJO implementation of the {@link TokenPaymentMethod}. 
 */
public class TokenPaymentMethodImpl implements TokenPaymentMethod {
	
	private String token;
	
	/**
	 * No-arg constructor.
	 */
	public TokenPaymentMethodImpl() {
		//Empty Constructor
	}
	
	/**
	 * Constructor.
	 *
	 * @param token the token to set
	 */
	public TokenPaymentMethodImpl(final String token) {
		this.token = token;
	}
	
	@Override
	public String getValue() {
		return token;
	}

	public void setValue(final String token) {
		this.token = token;
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
