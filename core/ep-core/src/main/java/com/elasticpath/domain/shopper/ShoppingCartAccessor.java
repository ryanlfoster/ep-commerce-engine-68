package com.elasticpath.domain.shopper;

import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Gives access to a {@link ShoppingCart} on the implementing object. 
 */
public interface ShoppingCartAccessor {

	/**
	 * Gets the current {@link ShoppingCart}.
	 *
	 * @return a {@link ShoppingCart}.
	 */
	ShoppingCart getCurrentShoppingCart();

	/**
	 * Sets the current {@link ShoppingCart}.
	 *
	 * @param shoppingCart the {@link ShoppingCart}.
	 */
	void setCurrentShoppingCart(ShoppingCart shoppingCart);

}