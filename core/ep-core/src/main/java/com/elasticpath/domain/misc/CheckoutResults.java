package com.elasticpath.domain.misc;

import java.io.Serializable;

import com.elasticpath.domain.order.Order;

/**
 * Holds parameters/errors that are returned by the checkout method of the checkout service.
 */
public interface CheckoutResults extends Serializable {

	/**
	 * Gets whether the email failed to send for any reason.
	 * 
	 * @return whether the email failed to send for any reason
	 */
	boolean isEmailFailed();

	/**
	 * Sets whether the email failed to send for any reason.
	 * 
	 * @param emailFailed whether the email failed to send for any reason
	 */
	void setEmailFailed(final boolean emailFailed);
	
	
	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	Order getOrder();
	
	/**
	 * Sets the order.
	 *
	 * @param order the new order
	 */
	void setOrder(Order order);
	
	/**
	 * Checks if is order failed.
	 *
	 * @return true, if is order failed
	 */
	boolean isOrderFailed();
	
	/**
	 * Sets the order failed.
	 *
	 * @param orderFailed the new order failed
	 */
	void setOrderFailed(boolean orderFailed);
	
	/**
	 * Gets the exception that caused the failure.
	 *
	 * @return the failure cause
	 */
	RuntimeException getFailureCause();
	
	/**
	 * Sets the exception that caused the failure.
	 *
	 * @param exception the new failure cause
	 */
	void setFailureCause(RuntimeException exception);
	
	/**
	 * Throws the failure cause if the order is failed.
	 */
	void throwCauseIfFailed();
}
