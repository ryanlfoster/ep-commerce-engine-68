/**
 * 
 */
package com.elasticpath.domain.discounts;

import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * A shopping cart discount item container where the discounts can be applied to.
 * It inherits helper methods to get cart items and calculate subtotal for discount calculation.   
 */
public interface ShoppingCartDiscountItemContainer extends DiscountItemContainer {
	
	/**
	 * Sets the shopping cart.
	 * @param cart the cart to set
	 */
	void setShoppingCart(ShoppingCart cart);
}