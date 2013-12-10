package com.elasticpath.domain.misc.impl;

import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.Order;

/**
 * Default implementation of {@link CheckoutResults}.
 */
public class CheckoutResultsImpl implements CheckoutResults {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private boolean emailFailed = false;
	
	private Order order;
	
	private RuntimeException failureCause;
	
	private boolean orderFailed = false;

	@Override
	public boolean isEmailFailed() {
		return emailFailed;
	}

	@Override
	public void setEmailFailed(final boolean emailFailed) {
		this.emailFailed = emailFailed;
	}

	@Override
	public Order getOrder() {
		return order;
	}

	@Override
	public void setOrder(final Order order) {
		this.order = order;
	}

	@Override
	public boolean isOrderFailed() {
		return orderFailed;
	}

	@Override
	public void setOrderFailed(final boolean orderFailed) {
		this.orderFailed = orderFailed;
	}

	@Override
	public RuntimeException getFailureCause() {
		return failureCause;
	}

	@Override
	public void setFailureCause(final RuntimeException failureCause) {
		this.failureCause = failureCause;
	}

	@Override
	public void throwCauseIfFailed() {
		if (isOrderFailed()) {
			throw getFailureCause();
		}
	}
}
