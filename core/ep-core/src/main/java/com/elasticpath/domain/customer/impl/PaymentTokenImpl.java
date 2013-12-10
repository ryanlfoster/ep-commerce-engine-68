/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.domain.customer.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.elasticpath.domain.customer.PaymentToken;

/**
 * Mutable {@link PaymentToken} that allows update of fields.
 */
@Entity
@Table(name = PaymentTokenImpl.TABLE_NAME)
public final class PaymentTokenImpl extends AbstractPaymentMethodImpl implements PaymentToken {
	private static final long serialVersionUID = 4781442984950882768L;

	/** Table to store PaymentTokens. */
	public static final String TABLE_NAME = "TPAYMENTTOKEN";

	private String value;

	private String gatewayGuid;

	private String displayValue;

	private PaymentTokenImpl(final TokenBuilder builder) {
		this.value = builder.value;
		this.gatewayGuid = builder.gatewayGuid;
		this.displayValue = builder.displayValue;
	}

	@Override
	@Basic(optional = false)
	@Column(name = "VALUE", nullable = false)
	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	@Basic
	@Column(name = "GATEWAY_GUID")
	public String getGatewayGuid() {
		return gatewayGuid;
	}

	public void setGatewayGuid(final String gatewayGuid) {
		this.gatewayGuid = gatewayGuid;
	}

	@Override
	@Basic(optional = false)
	@Column(name = "DISPLAY_VALUE", nullable = false)
	public String getDisplayValue() {
		return displayValue;
	}
	
	public void setDisplayValue(final String displayValue) {
		this.displayValue = displayValue;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	/**
	 * Builder for {@link PaymentToken}s.
	 */
	public static class TokenBuilder {
		private String value;
		
		private String gatewayGuid;

		private String displayValue;

		/**
		 * Creates a new TokenBuilder.
		 */
		public TokenBuilder() {
			// empty constructor
		}

		private TokenBuilder(final TokenBuilder builder) {
			this.value = builder.value;
			this.gatewayGuid = builder.gatewayGuid;
			this.displayValue = builder.displayValue;
		}

		/**
		 * With value.
		 *
		 * @param value the value
		 * @return the token builder
		 */
		public TokenBuilder withValue(final String value) {
			TokenBuilder tokenBuilder = new TokenBuilder(this);
			tokenBuilder.value = value;
			return tokenBuilder;
		}
		
		/**
		 * With gateway guid.
		 *
		 * @param gatewayGuid the gateway guid
		 * @return the token builder
		 */
		public TokenBuilder withGatewayGuid(final String gatewayGuid) {
			TokenBuilder tokenBuilder = new TokenBuilder(this);
			tokenBuilder.gatewayGuid = gatewayGuid;
			return tokenBuilder;
		}
		
		/**
		 * With display value.
		 *
		 * @param displayValue the display value
		 * @return the token builder
		 */
		public TokenBuilder withDisplayValue(final String displayValue) {
			TokenBuilder tokenBuilder = new TokenBuilder(this);
			tokenBuilder.displayValue = displayValue;
			return tokenBuilder;
		}
		
		/**
		 * Builds the token.
		 *
		 * @return the token
		 */
		@SuppressWarnings("PMD.AccessorClassGeneration")
		public PaymentToken build() {
			return new PaymentTokenImpl(this);
		}
	}
}
