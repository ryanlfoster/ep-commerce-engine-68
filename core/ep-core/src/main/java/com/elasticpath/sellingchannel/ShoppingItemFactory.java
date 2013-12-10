package com.elasticpath.sellingchannel;

import java.util.Map;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * A factory which creates {@code ShoppingItem}s ready for adding to a {@code ShoppingCart}.
 * This class is designed to be sub-classed for handling special requirements.
 */
public interface ShoppingItemFactory {

	/**
	 * Creates a shopping cart item from {@code sku} and {@code quantity}.
	 * 
	 * @param sku The sku to create the shopping cart item for.
	 * @param price The price of the shopping cart item.
	 * @param quantity The quantity
	 * @param ordering TODO
	 * @param itemFields Map which contains customizable fields.
	 * @return The new shopping cart item.
	 * @throws ProductUnavailableException If shoppingItem.productCode is empty or the product cannot be found.
	 * @throws ProductNotPurchasableException If the product is not purchasable.
	 * @throws IllegalArgumentException if the given ShoppingItem has a null Price.
	 */
	ShoppingItem createShoppingItem(final ProductSku sku, final Price price, int quantity,
			int ordering, Map<String, String> itemFields)
			throws ProductUnavailableException, ProductNotPurchasableException;

}
