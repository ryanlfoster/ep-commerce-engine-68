package com.elasticpath.service.shoppingcart.actions;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Interface for defining a CheckoutAction which can be Spring injected into the CheckoutService
 * for use by checkout in the setup and finalize flows (these actions cannot be rolled back).
 */
public interface FinalizeCheckoutAction {
	/**
	 * Action to execute during normal checkout flow.
	 * 
	 * @param context object containing data required for execution
	 * @throws EpSystemException exception object which could be thrown by execution
	 */
	void execute(final FinalizeCheckoutActionContext context) throws EpSystemException;
}