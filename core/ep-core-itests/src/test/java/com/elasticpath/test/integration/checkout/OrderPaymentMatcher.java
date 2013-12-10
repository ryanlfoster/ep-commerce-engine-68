/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.integration.checkout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.plugin.payment.PaymentType;

public class OrderPaymentMatcher {
	private PaymentType type;
	private OrderPaymentStatus status;
	private String transaction;
	
	private OrderPaymentMatcher(OrderPaymentMatcherBuilder orderPaymentMatcherBuilder) {
		this.type = orderPaymentMatcherBuilder.type;
		this.status = orderPaymentMatcherBuilder.status;
		this.transaction = orderPaymentMatcherBuilder.transaction;		
	}
	
	public boolean matches(final OrderPayment orderPayment) {
		if (!type.equals(orderPayment.getPaymentMethod())) {
			return false;
		}
		
		if (!status.equals(orderPayment.getStatus())) {
			return false;
		}
		
		if (!transaction.equals(orderPayment.getTransactionType())) {
			return false;
		}
		
		return true;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	static OrderPaymentMatcherBuilder builder() {
		return new OrderPaymentMatcherBuilder();
	}
	
	public static class OrderPaymentMatcherBuilder {
		private PaymentType type;
		private OrderPaymentStatus status;
		private String transaction;
		
		/**
		 * Sets the OrderPayment status which the OrderPaymentMatcher will use to match the order.
		 * @param status the status
		 * @return this
		 */
		OrderPaymentMatcherBuilder withStatus(final OrderPaymentStatus status) {
			this.status = status;
			return this;
		}
		
		OrderPaymentMatcherBuilder withType(final PaymentType type) {
			this.type = type;
			return this;
		}

		OrderPaymentMatcherBuilder withTransaction(final String transaction) {
			this.transaction = transaction;
			return this;
		}
		
		/**
		 * Builds the OrderPaymentMatcher.
		 * @return the OrderPaymentMatcher
		 */
		@SuppressWarnings("PMD.AccessorClassGeneration")
		OrderPaymentMatcher build() {
			OrderPaymentMatcher orderPaymentMatcher = new OrderPaymentMatcher(this);
			
			List<Field> matcherFields = Arrays.asList(orderPaymentMatcher.getClass().getDeclaredFields());
			
			List<String> missingFields = new ArrayList<String>(matcherFields.size());
			for (Field matcherField : matcherFields) {
				Object fieldValue = getFieldValue(orderPaymentMatcher, matcherField);
				if (fieldValue == null) {
					missingFields.add(matcherField.getName());
				}
			}
			
			if (!missingFields.isEmpty()) {
				throw new IllegalArgumentException("All matcher criteria must be supplied. Please fill in values for: " + missingFields);
			}
			
			return orderPaymentMatcher;
		}
		
		private Object getFieldValue(OrderPaymentMatcher orderPaymentMatcher, Field matcherField) {
			Object fieldValue = null;
			try {
				matcherField.setAccessible(true);
				fieldValue = matcherField.get(orderPaymentMatcher);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
			return fieldValue;
		}
		
	}
}
