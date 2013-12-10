package com.elasticpath.sfweb.controller;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.sfweb.formbean.ShoppingCartFormBean;

/**
 * A factory that creates {@link ShoppingCartFormBean} from {@link ShoppingCart}.
 */
public interface ShoppingCartFormBeanFactory extends ShoppingItemFormBeanContainerFactory {

	/**
	 * Creates a {@link ShoppingCartFormBean} from a shopping cart.
	 *
	 * @param request the  HTTP request
	 * @return {@link ShoppingCartFormBean}
	 */
	ShoppingCartFormBean createShoppingCartFormBean(final HttpServletRequest request);

}
