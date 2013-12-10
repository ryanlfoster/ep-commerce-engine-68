package com.elasticpath.service.shoppingcart.actions;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Interface for defining a CheckoutAction which can be Spring injected into the CheckoutService
 * for use by checkout.
 */
public interface ReversibleCheckoutAction extends CheckoutAction {
	/**
	 * Rollback procedure to execute if later checkout actions fail.
	 * 
	 * @param context object containing data required for execution
	 * @throws EpSystemException exception object which could be thrown by execution
	 */
	void rollback(final CheckoutActionContext context) throws EpSystemException;
}