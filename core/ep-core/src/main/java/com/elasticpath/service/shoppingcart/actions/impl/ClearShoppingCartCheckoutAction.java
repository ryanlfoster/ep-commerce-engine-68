package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutAction;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutActionContext;

/**
 * CheckoutAction to remove all items from the shoppingCart after converting the cart to an order. It alse removes 
 * the cart order.
 */
public class ClearShoppingCartCheckoutAction implements FinalizeCheckoutAction {

	private ShoppingCartService shoppingCartService;
	
	private CartOrderService cartOrderService;

	@Override
	public void execute(final FinalizeCheckoutActionContext context) throws EpSystemException {
		ShoppingCart shoppingCart = context.getShoppingCart();
		shoppingCart.clearItems();
		if (!context.isOrderExchange()) {
			shoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);
		}
		cartOrderService.removeIfExistsByShoppingCart(shoppingCart);
	}
	
	protected ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

    public void setCartOrderService(final CartOrderService cartOrderService) {
        this.cartOrderService = cartOrderService;
    }

    protected CartOrderService getCartOrderService() {
        return cartOrderService;
    }
}
