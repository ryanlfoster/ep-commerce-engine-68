/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.domain.shoppingcart;

/**
 * The <code>ShoppingCartVisitor</code> interface is implemented by classes which will visit a
 * <code>ShoppingCart</code> or <code>ShoppingItem</code>s.
 */
public interface ShoppingCartVisitor {
	
	/**
	 * Visit the given ShoppingCart.
	 * @param cart The cart. Cannot be null.
	 */
	void visit(ShoppingCart cart);
	
	/**
	 * Visit the given ShoppingItem.
	 * @param item The item. Cannot be null.
	 */
	void visit(ShoppingItem item);
}
