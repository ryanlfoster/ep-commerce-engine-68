/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.sfweb.listeners;

import java.math.BigDecimal;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.BrowsingBehaviorEventListener;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * 
 * Applies a CART_SUBTOTAL and subtotal value to the customer tag set.
 * The value of the current subtotal will be taken from shopping cart. 
 * 
 */
public class CartSubtotalTagger implements BrowsingBehaviorEventListener {

	private static final String CART_SUBTOTAL = "CART_SUBTOTAL";

	/**
	 * Apply cart subtotal tag to the given session.
	 *  
	 * @param session instance of CustomerSession
	 * @param request the originating HttpServletRequest
	 */	
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
		final ShoppingCart cart = session.getShopper().getCurrentShoppingCart();
		BigDecimal subtotal = null;
		if (cart != null) {
			subtotal = cart.getSubtotal();
			if (subtotal.intValue() == 0 && cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
				subtotal = null;
			} 
		}	
		final TagSet tagSet = session.getCustomerTagSet();		
		
		tagSet.addTag(CART_SUBTOTAL, new Tag(subtotal));
		
	}
}
