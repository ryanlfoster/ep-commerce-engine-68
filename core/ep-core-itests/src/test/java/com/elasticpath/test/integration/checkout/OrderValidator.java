/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.integration.checkout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderStatus;

/**
 * Validates that an order has the specified status and exactly the specified amount of payments in a given status.
 */
public class OrderValidator extends TypeSafeDiagnosingMatcher<Order> {
	private final List<OrderPaymentMatcher> orderPaymentMatchers;
	private final OrderStatus status;

	private OrderValidator(final OrderValidatorBuilder builder) {
		this.status = builder.status;
		this.orderPaymentMatchers = builder.orderPaymentMatchers;
	}

	/**
	 * Builds an immutable OrderValidator.
	 */
	public static class OrderValidatorBuilder {
		private OrderStatus status;
		private final List<OrderPaymentMatcher> orderPaymentMatchers = new ArrayList<OrderPaymentMatcher>();

		/**
		 * Sets the OrderStatus which the OrderValidator will use to verify the order.
		 * @param status the status
		 * @return this
		 */
		public OrderValidatorBuilder withStatus(final OrderStatus status) {
			this.status = status;
			return this;
		}

		/**
		 * Add the order payment matchers to check on the order.
		 *
		 * @param matchers the matchers
		 * @return this
		 */
		public OrderValidatorBuilder withPaymentMatchers(final OrderPaymentMatcher ... matchers) {
			orderPaymentMatchers.addAll(Arrays.asList(matchers));
			return this;
		}
		
		/**
		 * Builds the OrderValidator.
		 * @return the OrderValidator
		 */
		@SuppressWarnings("PMD.AccessorClassGeneration")
		public OrderValidator build() {
			return new OrderValidator(this);
		}
	}
	
	public static OrderValidatorBuilder builder() {
		return new OrderValidatorBuilder();
	}
	
	
	private boolean validPayments(final Order order) {
		Set<OrderPayment> paymentsFromOrder = new HashSet<OrderPayment>(order.getOrderPayments());
		
		if (paymentsFromOrder.size() != orderPaymentMatchers.size()) {
			return false;
		}
		
		for (OrderPaymentMatcher orderPaymentMatcher : orderPaymentMatchers) {
			boolean matchingPaymentFound = false;
			Iterator<OrderPayment> orderIterator = paymentsFromOrder.iterator();
			
			while (orderIterator.hasNext() && !matchingPaymentFound) {
				OrderPayment orderFromPayment = orderIterator.next();
				if (orderPaymentMatcher.matches(orderFromPayment)) {
					orderIterator.remove();
					matchingPaymentFound = true;
				}
			}
			
			if (!matchingPaymentFound) {
				return false;
			}
		}
		return true;
	}
	

	@Override
	public void describeTo(Description description) {
		description.appendText("Order Status:");
		description.appendValue(status);
		description.appendText("Order Payments:");
		description.appendValue(orderPaymentMatchers);
	}

	@Override
	protected boolean matchesSafely(Order order, Description mismatchDescription) {
		
		boolean valid = true;
		if (!status.equals(order.getStatus())) {
			mismatchDescription.appendText("Order Status:")
					.appendValue(order.getStatus());
			valid = false;
		}
		
		
		if (!validPayments(order)) {
			mismatchDescription.appendText("Order Payments:")
					.appendValue(order.getOrderPayments());	
			valid = false;
		}
		return valid;
	}
}
