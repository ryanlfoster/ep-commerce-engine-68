package com.elasticpath.sfweb.ajax.bean;

import java.util.List;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * A convenience wrapper for sending the <code>ShoppingCart</code> and a <code>List</code> of <code>CartItemBean</code> objects via dwr.
 */
public interface ShoppingCartBean {

	/**
	 * @return the cartItems
	 */
	List<ShoppingItem> getCartItems();

	/**
	 * @param cartItems the cartItems to set
	 */
	void setCartItems(final List<ShoppingItem> cartItems);

	/**
	 * @return the shoppingCart
	 */
	ShoppingCart getShoppingCart();

	/**
	 * @param shoppingCart the shoppingCart to set
	 */
	void setShoppingCart(final ShoppingCart shoppingCart);

	/**
	 * @return the orderSummary
	 */
	String getOrderSummary();

	/**
	 * @param orderSummary the orderSummary to set
	 */
	void setOrderSummary(final String orderSummary);

	/**
	 * @return the customer
	 */
	Customer getCustomer();

	/**
	 * @return true if the customer is currently signed in
	 */
	boolean isCustomerSignedIn();

	/**
	 * @return the uidPk of the selected billing address
	 */
	long getSelectedBillingAddressUidPk();

	/**
	 * @return the uidPk of the selected shipping address
	 */
	long getSelectedShippingAddressUidPk();

	/**
	 * @return the uidPk of the selected shipping service level
	 */
	long getSelectedShippingServiceLevelUidPk();

	/**
	 * Returns true if the cart contains items that must be shipped to the customer.
	 *
	 * @return true if the cart contains items that must be shipped to the customer.
	 */
	boolean isShippingRequired();

	/**
	 * Return the list of shippingServiceLevel list available based on the current shopping cart info.
	 *
	 * @return the list of shippingServiceLevel list available based on the current shopping cart info.
	 */
	List<ShippingServiceLevel> getShippingServiceLevelList();

	/**
	 * @return the total cost of the order as a <code>String</code> including the currency symbol.
	 */
	String getTotal();

	/**
	 * @return true if the customer is authenticated
	 */
	boolean isCustomerAuthenticated();
}
