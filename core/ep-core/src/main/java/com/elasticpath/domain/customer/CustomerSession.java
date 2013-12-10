/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer;

import java.util.Currency;
import java.util.Date;

import com.elasticpath.base.GloballyIdentifiable;
import com.elasticpath.domain.shopper.ShopperReference;
import com.elasticpath.domain.shoppingcart.ShoppingCart;


/**
 * A customer session keeps track of information about customers
 * who may not be logged in (using cookies).
 * 
 */
public interface CustomerSession extends GloballyIdentifiable<String>, CustomerSessionTransientData, ShopperReference {

	/**
	 * Get the date when the customer session was created.
	 * 
	 * @return the creation date
	 */
	Date getCreationDate();
	
	/**
	 * Set the creation date.
	 * 
	 * @param creationDate the creation date.
	 */
	void setCreationDate(final Date creationDate);
	
	/**
	 * Get the date when the customer session was last accessed.
	 * 
	 * @return the last accessed date.
	 */
	Date getLastAccessedDate();
	
	/**
	 * Set the last access date.
	 * 
	 * @param lastAccessedDate the last access date.
	 */
	void setLastAccessedDate(final Date lastAccessedDate);	

	/**
	 * Get the currency of the customer corresponding to the shopping cart.
	 * 
	 * @return the <code>Currency</code>
	 */
	Currency getCurrency();

	/**
	 * Set the currency of the customer corresponding to the shopping cart.
	 * 
	 * @param currency the <code>Currency</code>
	 */
	void setCurrency(final Currency currency);	
	
	/**
	 * Get the ipAddress of the user from the shopping cart.
	 *
	 * @return the ipAddress
	 */
	String getIpAddress();

	/**
	 * Set the users ip Address into the shopping cart.
	 *
	 * @param ipAddress the ipAddress of the user.
	 */
	void setIpAddress(final String ipAddress);
	
	/**
	 * Gets the persisted memento for this CustomerSession.
	 *
	 * @return a CustomerSessionMemento.
	 */
	CustomerSessionMemento getCustomerSessionMemento();
	
	/**
	 * Sets the persisted memento for this CustomerSession.
	 *
	 * @param customerSessionMemento the memento to set.
	 */
	void setCustomerSessionMemento(CustomerSessionMemento customerSessionMemento);
	
	/**
	 * Gets the ShoppingCart.
	 * @return the ShoppingCart
	 * @deprecated Use {@link com.elasticpath.domain.shopper.Shopper#getCurrentShoppingCart} instead.
	 */
	@Deprecated
	ShoppingCart getShoppingCart();
	
	/**
	 * Sets the shopping cart being used by this CustomerSession.
	 * @param shoppingCart the cart for this session
	 * @deprecated Use {@link com.elasticpath.domain.shopper.Shopper#setCurrentShoppingCart} instead.
	 */
	@Deprecated
	void setShoppingCart(ShoppingCart shoppingCart);
	
}
