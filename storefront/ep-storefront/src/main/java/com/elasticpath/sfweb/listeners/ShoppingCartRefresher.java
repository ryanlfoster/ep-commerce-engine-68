package com.elasticpath.sfweb.listeners;


import org.apache.log4j.Logger;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.CustomerLoginEventListener;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;

/**
 * Refreshes the shopping cart when a new http session event comes.
 */
public class ShoppingCartRefresher implements NewHttpSessionEventListener, CustomerLoginEventListener {
	private static final Logger LOG = Logger.getLogger(ShoppingCartRefresher.class);

	private CartDirector cartDirector;

	@Override
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
		Shopper shopper = session.getShopper();
		ShoppingCart shoppingCart = shopper.getCurrentShoppingCart();
		if (shoppingCart.getNumItems() > 0) {
			LOG.debug("Refreshing cart");
			getCartDirector().refresh(shoppingCart);
			ShoppingCart updatedCart = getCartDirector().saveShoppingCart(shoppingCart);
			shopper.setCurrentShoppingCart(updatedCart);
		}
	}

	/**
	 * @param cartDirector cartDirector
	 */
	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

	/**
	 * @return cartDirector
	 */
	public CartDirector getCartDirector() {
		return cartDirector;
	}
}
