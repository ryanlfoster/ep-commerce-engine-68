package com.elasticpath.service.shoppingcart.actions;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Container class for data required by finalizeCheckoutActions.
 */
public interface FinalizeCheckoutActionContext {

	/**
	 * Gets the order.
	 * @return the order
	 * */
	Order getOrder();

	/**
	 * Gets the {@link Shopper}.
	 * 
	 * @return the {@link Shopper}
	 * */
	Shopper getShopper();
	
	/**
	 * Gets the {@link ShoppingCart}.
	 * 
	 * @return the {@link ShoppingCart}
	 * */
	ShoppingCart getShoppingCart();

	/**
	 * Gets the order payment template.
	 * @return the order payment template
	 * */
	OrderPayment getOrderPaymentTemplate();

	/**
	 * Gets the is order exchange.
	 * @return the is order exchange
	 * */
	boolean isOrderExchange();

	/**
	 * Sets the email failed.
	 * @param isEmailFailed the email failed flag
	 * */
	void setEmailFailed(boolean isEmailFailed);

	/**
	 * Gets the email failed.
	 * @return the email failed
	 * */
	boolean isEmailFailed();
}