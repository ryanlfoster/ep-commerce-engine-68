package com.elasticpath.service.shoppingcart;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Exception thrown when a {@link ShoppingCart} is unexpectedly empty.
 */
public class ShoppingCartEmptyException extends EpServiceException {

	private static final long serialVersionUID = 6129361439895502593L;

	private final ShoppingCart shoppingCart;

	/**
	 * Constructor including the offending {@link ShoppingCart}.
	 *
	 * @param message the exception message
	 * @param emptyShoppingCart the empty Shopping Cart
	 */
	public ShoppingCartEmptyException(final String message, final ShoppingCart emptyShoppingCart) {
		super(message);
		shoppingCart = emptyShoppingCart;
	}

	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

}
