package com.elasticpath.sfweb.controller;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBeanContainer;

/**
 * Factory for creating {@code CartUpdateFormBean}s.
 */
public interface ShoppingItemFormBeanContainerFactory {

	/**
	 * Builds the form bean. Note that this method creates a {@code CartFormBean} with a single root {@code ShoppingCartItemFormBean}. That single
	 * root may have constituents. The updateFlag is set to false.
	 * 
	 * @param storeProduct The product to build it for.
	 * @param quantity The quantity.
	 * @param shoppingCart the shopping cart
	 * @return The form bean.
	 */
	ShoppingItemFormBeanContainer createCartFormBean(final StoreProduct storeProduct, final int quantity, final ShoppingCart shoppingCart);

	/**
	 * Creates the form bean from an existing shopping item.
	 * 
	 * @param existingShoppingItemDto The shopping item to create the bean from.
	 * @param shoppingCart the cart
	 * @param dependent whether or not the supplied dto is a dependent item
	 * @return the new CartUpdateFormBean.
	 */
	ShoppingItemFormBeanContainer createCartFormBean(final ShoppingItemDto existingShoppingItemDto, 
			final ShoppingCart shoppingCart, boolean dependent);

	/**
	 * Builds the form bean.
	 * 
	 * @return The form bean.
	 */
	ShoppingItemFormBeanContainer createCartFormBean();
}