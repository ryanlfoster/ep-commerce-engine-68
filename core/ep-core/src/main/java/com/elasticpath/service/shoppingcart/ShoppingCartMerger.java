package com.elasticpath.service.shoppingcart;

import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Interface class for merging two shopping lists into a single list.
 */
public interface ShoppingCartMerger {

	/**
	 * Merges two <code>ShoppingCart</code>s into a single <code>ShoppingCart</code>.
	 *
	 * @param recipient is the <code>ShoppingCart</code> that will get all the items from the donor cart.
	 * @param donor is a <code>ShoppingCart</code> that is giving away the shopping items to the recipient.
	 * @return the recipient cart, now also containing the items from the donor cart.
	 */
	ShoppingCart merge(ShoppingCart recipient, ShoppingCart donor);
}
