package com.elasticpath.sellingchannel.director;

import java.util.List;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Business domain delegate of the functionality required to add a cart item to a shopping cart. This object is delegated to from the CartDirector.
 */
public interface CartDirector {

	/**
	 * @param shoppingCart {@code ShoppingCart}
	 * @param dto of new item
	 * @return the {@link ShoppingItem} that's added to the cart
	 */
	ShoppingItem addItemToCart(ShoppingCart shoppingCart, ShoppingItemDto dto);

	/**
	 * @param shoppingCart {@code ShoppingCart}
	 * @param dto of new item
	 * @param parentItem whether the item is a dependent or not
	 * @return the {@link ShoppingItem} that's added to the cart
	 */
	ShoppingItem addItemToCart(ShoppingCart shoppingCart, ShoppingItemDto dto, ShoppingItem parentItem);

	/**
	 * @param shoppingCart {@code ShoppingCart}
	 * @param itemId id of cart item for update
	 * @param dto of new item
	 * @return the ShoppingItem that's updated as a result of the given dto
	 */
	ShoppingItem updateCartItem(ShoppingCart shoppingCart, long itemId, ShoppingItemDto dto);

	/**
	 * Adds the ProductSku represented by the given SkuCode to the given shopping cart as a wish list item.
	 * @param skuCode the code representing the ProductSku to be added to the cart
	 * @param shopper the customer session
	 * @param store the store
	 * @return the {@link ShoppingItem} that's added to the cart
	 */
	ShoppingItem addSkuToWishList(String skuCode, Shopper shopper, Store store);

	/**
	 * Check if the specified child is dependent on an element in the list.
	 *
	 * @param cartItems list to check for the parent
	 * @param child the child
	 * @return true if there is a parent in the list
	 */
	boolean isDependent(final List<ShoppingItem> cartItems, final ShoppingItem child);

	/**
	 * Refreshes the given shopping cart. For example, updating all its {@link ShoppingItem} with the latest prices.
	 *
	 * @param shoppingCart shopping cart
	 * @throws EpServiceException - in case of any errors
	 */
	void refresh(ShoppingCart shoppingCart) throws EpServiceException;

	/**
	 * Persists {@code shoppingCart} in the database.
	 *
	 * @param shoppingCart The shopping cart to save.
	 * @return The now updated shopping cart.
	 */
	ShoppingCart saveShoppingCart(ShoppingCart shoppingCart);


	/**
	 * move item from wish list to shopping cart.
	 * the method should save wish list and shopping cart in one transaction.
	 *
	 * @param shoppingCart the shopping cart
	 * @param dto the shopping item dto
	 * @return the shopping cart updated
	 */
	ShoppingCart moveItemFromWishListToCart(ShoppingCart shoppingCart, ShoppingItemDto dto);

	/**
	 *	Determines if the user selection are valid, using the product from the database.
	 * @param shoppingItemDto - item received from the user
	 */
	void validateShoppingItemDto(final ShoppingItemDto shoppingItemDto);

	/**
	 * Removes any non-purchasable shopping items from the cart.
	 * @param items cart items
	 * @param cart shopping cart
	 */
	void removeAnyNonPurchasableItems(List<ShoppingItem> items, ShoppingCart cart);
}