/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.List;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.shoppingcart.actions.CheckoutAction;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * {@link CheckoutAction} to validate the shipping information on a {@link ShoppingCart}.
 */
public class ShippingInformationCheckoutAction implements CheckoutAction {

	private ShippingServiceLevelService shippingServiceLevelService;  
	
	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		ShoppingCart shoppingCart = context.getShoppingCart();
		
		if (shoppingCart.requiresShipping()) {
			verifyCartHasShippingAddress(shoppingCart);
			verifyCartHasShippingServiceLevel(shoppingCart);
			verifyCartHasValidShippingServiceLevel(shoppingCart);
		}
	}

	private void verifyCartHasShippingAddress(final ShoppingCart shoppingCart) {
		if (shoppingCart.getShippingAddress() == null) {
			throw new MissingShippingAddressException("No shipping address set on shopping cart with guid: " + shoppingCart.getGuid());
		}
	}

	private void verifyCartHasShippingServiceLevel(final ShoppingCart shoppingCart) {
		if (shoppingCart.getSelectedShippingServiceLevel() == null) {
			throw new MissingShippingServiceLevelException("No shipping service level set on shopping cart with guid: " + shoppingCart.getGuid());
		}
	}
	
	private void verifyCartHasValidShippingServiceLevel(final ShoppingCart shoppingCart) {
		ShippingServiceLevel levelFromCart = shoppingCart.getSelectedShippingServiceLevel();
		List<ShippingServiceLevel> validCartLevels = shippingServiceLevelService.retrieveShippingServiceLevel(shoppingCart);
		if (!validCartLevels.contains(levelFromCart)) {
			throw new InvalidShippingServiceLevelException("Invalid shipping service level with guid " + levelFromCart.getGuid() 
					+ " set on shopping cart with guid: " + shoppingCart.getGuid());
		}
	}

	public void setShippingServiceLevelService(final ShippingServiceLevelService shippingServiceLevelService) {
		this.shippingServiceLevelService = shippingServiceLevelService;
	}

	protected ShippingServiceLevelService getShippingServiceLevelService() {
		return shippingServiceLevelService;
	}
}
