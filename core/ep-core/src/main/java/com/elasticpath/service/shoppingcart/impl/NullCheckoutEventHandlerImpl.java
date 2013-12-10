/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart.impl;

import com.elasticpath.service.shoppingcart.CheckoutEventHandler;

/**
 * This implementation of <code>CheckoutEventHandler</code> does not perform any action
 * as a result of a checkout event. To extend the checkout process, extend
 * <code>AbstractCheckoutEventHandler</code> and replace this handler in the Spring configuration
 * with your new handler.
 *
 */
public class NullCheckoutEventHandlerImpl extends AbstractCheckoutEventHandlerImpl implements CheckoutEventHandler {

}
