/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.test.integration.checkout;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.actions.CheckoutAction;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * {@link CheckoutAction} to ensure a {@link ShoppingCart} with physical goods cannot have a tokenized payment.
 */
public class FailingCheckoutAction implements ReversibleCheckoutAction {

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		throw new EpServiceException(this.getClass().getName() + " Failing order : "	+ context.getOrder().getGuid());
	}

	@Override
	public void rollback(CheckoutActionContext context) throws EpSystemException {
		// No-op method (nothing to rollback)
	}
}
