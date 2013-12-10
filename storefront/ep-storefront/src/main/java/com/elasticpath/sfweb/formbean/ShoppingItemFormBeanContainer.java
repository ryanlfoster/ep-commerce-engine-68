package com.elasticpath.sfweb.formbean;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for form beans which hold additional form beans. 
 */
public interface ShoppingItemFormBeanContainer extends Serializable {

	/**
	 * Returns the form beans for the cart items.
	 * @return The form beans.
	 */
	List<ShoppingItemFormBean> getCartItems();

	/**
	 * @param shoppingItemFormBean The item form bean to be added to the cart.
	 */
	void addShoppingItemFormBean(ShoppingItemFormBean shoppingItemFormBean);

}