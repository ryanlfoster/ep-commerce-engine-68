package com.elasticpath.sellingchannel.director.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.tree.impl.PreOrderTreeTraverser;
import com.elasticpath.commons.tree.impl.ProductPriceMemento;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.impl.CartItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.WishListService;

/**
 * Business domain delegate of the functionality required to add a cart item to a shopping cart. This object is delegated to from the CartDirector.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class CartDirectorImpl implements CartDirector {

	private static final Logger LOG = Logger.getLogger(CartDirectorImpl.class);

	private ProductSkuService productSkuService;

	private ShoppingCartService shoppingCartService;

	private PriceLookupFacade priceLookupFacade;

	private StoreProductService storeProductService;

	private ShoppingItemAssembler shoppingItemAssembler;

	private ShoppingItemDtoFactory shoppingItemDtoFactory;

	private StoreProductLoadTuner storeProductLoadTuner;

	private WishListService wishListService;

	private TimeService timeService;

	/**
	 * @param shoppingItem The shoppingItem to add.
	 * @param shoppingCart The cart to add the items to.
	 * @param parentItem is the item dependent
	 * @return The cart item that was added. Null if it could not be found.
	 */
	protected ShoppingItem addToCart(final ShoppingItem shoppingItem, final ShoppingCart shoppingCart, final ShoppingItem parentItem) {
		ShoppingItem cartItemToAdd = shoppingItem;
		int quantity = cartItemToAdd.getQuantity();

		final ShoppingItem existingItem = getExistingItemWithSameParent(shoppingCart, shoppingItem, parentItem);

		if (existingItem == null || shoppingItem.isConfigurable()) {
			priceShoppingItemWithAdjustments(shoppingCart, cartItemToAdd);
			// can't add null priced items to the cart
			if (cartItemToAdd.getLowestUnitPrice() == null) {
				LOG.warn("Sku has no price, cannot add to cart: " + cartItemToAdd.getProductSku().getSkuCode());
				return null;
			}
			cartItemToAdd = shoppingCart.addShoppingCartItem(cartItemToAdd);
			cartItemToAdd.setOrdering(shoppingCart.getCartItems().size());
			if (parentItem != null) {
				parentItem.addChildItem(cartItemToAdd);
			}
		} else if (existingItem != null && parentItem == null) { // non-dependent item
			quantity = quantity + existingItem.getQuantity();
			cartItemToAdd = changeQuantityForCartItem(existingItem, quantity, shoppingCart);
		} else {
			// else do nothing when existing dependent item found
			LOG.debug("dependent sku already in cart: " + cartItemToAdd.getProductSku().getSkuCode());
		}

		return cartItemToAdd;
	}

	/**
	 * @param currentSkuGuid sku guid.
	 * @return product sku.
	 */
	protected ProductSku getProductSku(final String currentSkuGuid) {
		final ProductSku sku = productSkuService.findBySkuCode(currentSkuGuid);
		if (sku == null) { // not found.
			throw new EpServiceException("ProductSku with the specified sku code [" + currentSkuGuid + "] does not exist");
		}
		return sku;
	}

	private ShoppingItem createShoppingItem(final ShoppingItemDto shoppingItemDto) {
		return shoppingItemAssembler.createShoppingItem(shoppingItemDto);
	}

	/**
	 * Applies the appropriate prices to the given {@code ShoppingItem}. This implementation uses a {@link PricingFunctor} created with this
	 * instance's {@link PriceLookupFacade}, along with a {@code PreOrderTreeTraverser} to walk the {@code ShoppingItem} tree and set the prices.
	 *
	 * @param shoppingItem the root shopping item upon which the price should be set
	 * @param store the store in which the shoppingItem is being used
	 * @param shopper {@code CustomerSession}
	 * @param ruleTracker applied rules
	 */
	protected void priceShoppingItem(final ShoppingItem shoppingItem, final Store store, final Shopper shopper,
			final Set<Long> ruleTracker) {

		final PreOrderTreeTraverser<ShoppingItem, ProductPriceMemento> pricingTraverser =
			new PreOrderTreeTraverser<ShoppingItem, ProductPriceMemento>();
		final PricingFunctor functor = new PricingFunctor(priceLookupFacade, store, shopper, ruleTracker);
		if (shoppingItem.isBundle()) {
			final ProductPriceMemento memento = pricingTraverser.traverseTree(shoppingItem, null, null, functor, 0);
			checkPricingResults(memento);
		} else {
			functor.processNode(shoppingItem, null, null, 0);
		}
	}

	private void checkPricingResults(final ProductPriceMemento productPriceStackMemento) {
		for (final Map.Entry<String, Price> entry : productPriceStackMemento.getStack().entrySet()) {
			if (entry.getValue() == null) {
				throw new ProductNotPurchasableException("Null price found for " + entry.getKey());
			}
		}
	}

	@Override
	public ShoppingCart saveShoppingCart(final ShoppingCart shoppingCart) {
		final Comparator<ShoppingItem> comparator = new Comparator<ShoppingItem>() {
			public int compare(final ShoppingItem item1, final ShoppingItem item2) {
				final Integer order1 = Integer.valueOf(item1.getOrdering());
				final Integer order2 = Integer.valueOf(item2.getOrdering());
				return order1.compareTo(order2);
			}
		};
		int order = 0;
		Collections.sort(shoppingCart.getCartItems(), comparator);
		for (final ShoppingItem item : shoppingCart.getCartItems()) {
			item.setOrdering(++order);
		}
		return shoppingCartService.saveOrUpdate(shoppingCart);
	}

	/**
	 * Changes the quantity of {@code shoppingItem} to be {@code quantity}. Note that this method will ensure that a new price is looked up and
	 * promotions calculated for the new quantity. This implementation creates a brand new shoppingItem from the given shoppingItem.
	 *
	 * @param shoppingItem The shoppingItem to update.
	 * @param quantity The quantity to set.
	 * @param shoppingCart the shoppingCart within which the shoppingItem is contained
	 * @return the ShoppingItem that replaces the given ShoppingItem
	 */
	protected ShoppingItem changeQuantityForCartItem(final ShoppingItem shoppingItem, final int quantity, final ShoppingCart shoppingCart) {
		final ShoppingItemDto dto = getShoppingItemAssembler().assembleShoppingItemDtoFrom(shoppingItem);
		dto.setQuantity(quantity);
		return updateCartItem(shoppingCart, shoppingItem.getUidPk(), dto);
	}

	@Override
	public ShoppingItem addItemToCart(final ShoppingCart shoppingCart, final ShoppingItemDto dto) {
		return addItemToCart(shoppingCart, dto, null);
	}

	@Override
	public ShoppingItem addItemToCart(final ShoppingCart shoppingCart, final ShoppingItemDto dto, final ShoppingItem parentItem) {
        final boolean isDependent = parentItem != null;
          final ProductSku sku = getProductSku(dto.getSkuCode());
         if (isProductPurchasableInStore(shoppingCart.getStore(), sku.getProduct(), isDependent, sku.getUidPk())) {
             final ShoppingItem shoppingItem = createShoppingItem(dto);
             return addToCart(shoppingItem, shoppingCart, parentItem);
          }
         LOG.warn("Sku not purchasable: " + dto.getSkuCode());
         return null;
	}

	/**
	 * Updates a {@code ShoppingItem} having the given itemId with the data from the given {@code ShoppingItemDto}. This implementation creates a new
	 * ShoppingItem from the DTO, adds it to the shopping cart, and deletes the old one. Calls {@link #addToCart(ShoppingItem, ShoppingCart)}.
	 *
	 * @param shoppingCart {@code ShoppingCart}
	 * @param itemId id of cart item for update
	 * @param dto of new item
	 * @return the ShoppingItem that's replacing the one with the given id
	 */
	public ShoppingItem updateCartItem(final ShoppingCart shoppingCart, final long itemId, final ShoppingItemDto dto) {
		getShoppingItemAssembler().validateShoppingItemDto(dto);

		// find the ShoppingItem for this id
		final ShoppingItem item = getCartItem(shoppingCart, itemId);

		// get all the dependent items such as warranties for this item
		final List<ShoppingItem> dependentItems = ((CartItem) item).getDependentItems();

		// if this is a dependent item, get it's parent
		final ShoppingItem parentItem = getParentOfDependentItem(shoppingCart.getCartItems(), item);

		// delete/re-create (delete's all it's dependents!) to "update" existing item from dto
		shoppingCart.removeCartItem(itemId);
		final boolean isDependent = parentItem != null;
		final Store store = shoppingCart.getStore();
		if (!isProductPurchasableInStore(store, item.getProductSku().getProduct(), isDependent, item.getProductSku().getUidPk())) {
			throw new ProductNotPurchasableException("Product is not purchasable.");
		}
		final int ordering = item.getOrdering();
		final ShoppingItem newShoppingItem = getShoppingItemAssembler().createShoppingItem(dto);
		retainShoppingItemIdentity(item, newShoppingItem);

		// re-connect with the parent item, if this item is dependent
		if (isDependent) {
			parentItem.addChildItem(newShoppingItem);
		}

		// add the new updated item back to the cart
		final ShoppingItem updatedItem = addToCart(newShoppingItem, shoppingCart, parentItem);
		if (updatedItem != null) {
			updatedItem.setOrdering(ordering);

			// put back dependent items with the new item quantity
			for (final ShoppingItem dependent : dependentItems) {
				final ShoppingItemDto dependentDto = getShoppingItemAssembler().assembleShoppingItemDtoFrom(dependent);
				dependentDto.setQuantity(updatedItem.getQuantity());
				final ShoppingItem newDependentItem = getShoppingItemAssembler().createShoppingItem(dependentDto);
				addToCart(newDependentItem, shoppingCart, updatedItem);
			}
		}
		return updatedItem;
	}

    /**
     * Makes sure the new shopping item can retain the identity of the existing shopping item.
     *
     * When an item is being updated in the cart, a new shopping item will be created, and the new shopping item will
     * substitute the existing one. This method can decide to either keep the old shopping item's identity (e.g. GUID),
     * or use a new one.

     * @param existingShoppingItem the old shopping item
     * @param newShoppingItem the new shopping item
     */
	protected void retainShoppingItemIdentity(final ShoppingItem existingShoppingItem, final ShoppingItem newShoppingItem) {
		newShoppingItem.setGuid(existingShoppingItem.getGuid());
    }

	/**
	 * @param shoppingCart shoppingCart
	 * @param itemId itemId
	 * @return ShoppingItem if found in cart
	 */
	protected ShoppingItem getCartItem(final ShoppingCart shoppingCart, final long itemId) {
		for (final ShoppingItem cartItem : shoppingCart.getCartItems()) {
			if (cartItem.getUidPk() == itemId) {
				return cartItem;
			}
		}
		return null;
	}

	/**
	 * If the child is a dependent item return the parent, otherwise returns null.
	 *
	 * @param cartItems shoppingcart
	 * @param child shoppingItme
	 * @return parent item
	 */
	protected ShoppingItem getParentOfDependentItem(final List<ShoppingItem> cartItems, final ShoppingItem child) {
		for (final ShoppingItem item : cartItems) {
			if (((CartItem) item).getDependentItems().contains(child)) {
				return item;
			}
		}
		return null;
	}

	private ShoppingItem getExistingItemWithSameParent(final ShoppingCart shoppingCart, final ShoppingItem shoppingItem,
			final ShoppingItem parentItem) {
		final List<ShoppingItem> cartItems = shoppingCart.getCartItems();
		final String skuCode = shoppingItem.getProductSku().getSkuCode();
		final List<ShoppingItem> existingItems = shoppingCart.getCartItems(skuCode);
		for (final ShoppingItem existingItem : existingItems) {
			if (shoppingItem.equals(existingItem)) {
				continue;
			}
			final ShoppingItem existingParentItem = getParentOfDependentItem(cartItems, existingItem);
			if (parentItem != null && parentItem.equals(existingParentItem)) {
				return existingItem;
			}
			if (parentItem == null && existingParentItem == null) {
				return existingItem;
			}
		}
		return null;
	}

	/**
	 * Check if the specified child is dependent on an element in the list.
	 *
	 * @param cartItems list to check for the parent
	 * @param child the child
	 * @return true if there is a parent in the list
	 */
	public boolean isDependent(final List<ShoppingItem> cartItems, final ShoppingItem child) {
		return getParentOfDependentItem(cartItems, child) != null;
	}

	@Override
	public void refresh(final ShoppingCart shoppingCart) throws EpServiceException {
		refreshShoppingItems(shoppingCart.getCartItems(), shoppingCart);
	}

	/**
	 * Adds the ProductSku represented by the given SkuCode to the given shopping cart as a wish list item.
	 *
	 * @param skuCode the code representing the ProductSku to be added to the cart
	 * @param shopper the customer session
	 * @param store the store
	 * @return the {@link ShoppingItem} that's added to the cart
	 */
	public ShoppingItem addSkuToWishList(final String skuCode, final Shopper shopper, final Store store) {
		if (skuCode == null) {
			throw new IllegalArgumentException("Sku code cannot be null");
		}

		final ProductSku selectedProductSku = productSkuService.findBySkuCode(skuCode);
		if (selectedProductSku == null) { // not found.
			throw new EpServiceException("ProductSku with the specified sku code [" + skuCode + "] does not exist");
		}

		if (!isProductDisplayableInStore(store, selectedProductSku.getProduct())) {
			throw new EpServiceException("Product SKU[" + skuCode + "] is not available.");
		}

		final ShoppingItemDto dto = getShoppingItemDtoFactory().createDto(skuCode, 1);
		final ShoppingItem item = createShoppingItem(dto);

		final WishList wishList = getWishListService().findOrCreateWishListByShopper(shopper);
		wishList.addItem(item);
		getWishListService().save(wishList);

		return item;
	}

	/**
	 * Refreshed pricing information for shopping items.
	 * @param shoppingCart cart
	 * @param items shopping items
	 */
	protected void refreshShoppingItems(final List<ShoppingItem> items, final ShoppingCart shoppingCart) {
		final List<ShoppingItem> nullPricedItems = new ArrayList<ShoppingItem>();
		for (final ShoppingItem item : items) {
			try {
				priceShoppingItemWithAdjustments(shoppingCart, item);
			} catch (final ProductNotPurchasableException exception) {
				nullPricedItems.add(item);
			}
		}
		// we must remove null priced items from the cart
		for (final ShoppingItem item : nullPricedItems) {
			LOG.warn("Sku has no price, removing from cart: " + item.getProductSku().getSkuCode());
			items.remove(item);
		}
	}

	@Override
	public void removeAnyNonPurchasableItems(final List<ShoppingItem> items, final ShoppingCart cart) {
		refreshShoppingItems(items, cart);
		CollectionUtils.filter(items, new Predicate() {
			/**
			 * If the product is purchasable, it stays in the cart, otherwise it gets removed and it's sku code is added to the
			 * shopping cart list of non purchasable items for further processing.
			 */
			@Override
			public boolean evaluate(final Object object) {
				ShoppingItem item = (ShoppingItem) object;
				if (isProductPurchasableInStore(cart.getStore(), item.getProductSku().getProduct(), false, item.getProductSku().getUidPk())
						&& itemHasPrice(item)) {
					return true;
				}
				cart.getNotPurchasableCartItemSkus().add(item.getProductSku().getSkuCode());
				return false;
			}
		});
	}

	private boolean itemHasPrice(final ShoppingItem item) {
		if (item.getPrice() == null || item.getPrice().getPriceTiers() == null || item.getPrice().getPriceTiers().size() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * This method, in sequence, prices the shopping item, applies bundle adjustments, if applicable, and finally promotes the prices.
	 *
	 * @param shoppingCart the cart
	 * @param item the item to price and adjust
	 */
	protected void priceShoppingItemWithAdjustments(final ShoppingCart shoppingCart, final ShoppingItem item) {
		final Shopper shopper = shoppingCart.getShopper();
		priceShoppingItem(item, shoppingCart.getStore(), shopper, shoppingCart.getAppliedRules());

		if (item.isBundle()) {
			final Price adjustedPrice = priceLookupFacade.getShoppingItemPrice(item, shoppingCart.getStore(),
					shopper, shoppingCart.getAppliedRules());
			item.setPrice(item.getQuantity(), adjustedPrice);
		}
	}

	/**
	 * Determines whether the given Product is purchasable in the given Store.
	 *
	 * @param store the store
	 * @param product the product
	 * @param isDependent true if this product is a dependent product
	 * @param skuUid The uid of the sku that is attempting to be purchased.
	 * @return true if purchasable
	 */
	protected boolean isProductPurchasableInStore(final Store store, final Product product, final boolean isDependent, final long skuUid) {
		final StoreProduct storeProduct = storeProductService.getProductForStore(product.getUidPk(), skuUid, store, storeProductLoadTuner);
		return storeProduct.isPurchasable() && (isDependent || !storeProduct.isNotSoldSeparately());
	}

	/**
	 * Determines whether the given Product is displayable in the given Store.
	 *
	 * @param store the store
	 * @param product the product
	 * @return true if displayable
	 */
	protected boolean isProductDisplayableInStore(final Store store, final Product product) {
		final StoreProduct storeProduct = getStoreProductService().getProductForStore(product, store);
		return storeProduct.isDisplayable() && !storeProduct.isNotSoldSeparately();
	}

	/**
	 * Sets the productSkuService.
	 *
	 * @param productSkuService The ProductSkuService.
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

	/**
	 * Sets the shoppingCartService.
	 *
	 * @param shoppingCartService The ShoppingCartService.
	 */
	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	/**
	 * @param priceLookupFacade The price lookup facade to set.
	 */
	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}

	/**
	 * @param shoppingItemAssembler The assembler to set.
	 */
	public void setShoppingItemAssembler(final ShoppingItemAssembler shoppingItemAssembler) {
		this.shoppingItemAssembler = shoppingItemAssembler;
	}

	/**
	 * @return the shoppingItemAssembler
	 */
	ShoppingItemAssembler getShoppingItemAssembler() {
		return shoppingItemAssembler;
	}

	/**
	 * Getter for store product service.
	 *
	 * @return storeProductService The store product service.
	 */
	public StoreProductService getStoreProductService() {
		return this.storeProductService;
	}

	/**
	 * Setter for store product service.
	 *
	 * @param storeProductService The store product service to set.
	 */
	public void setStoreProductService(final StoreProductService storeProductService) {
		this.storeProductService = storeProductService;
	}

	/**
	 * @param shoppingItemDtoFactory The factory to set.
	 */
	public void setShoppingItemDtoFactory(final ShoppingItemDtoFactory shoppingItemDtoFactory) {
		this.shoppingItemDtoFactory = shoppingItemDtoFactory;
	}

	/**
	 * @return the shoppingItemDtoFactory
	 */
	ShoppingItemDtoFactory getShoppingItemDtoFactory() {
		return shoppingItemDtoFactory;
	}

	/**
	 * Set the <code>StoreProductLoadTuner</code> for retrieving multi-sku products.
	 *
	 * @param storeProductLoadTuner the <code>StoreProductLoadTuner</code>
	 */
	public void setStoreProductLoadTuner(final StoreProductLoadTuner storeProductLoadTuner) {
		this.storeProductLoadTuner = storeProductLoadTuner;
	}

	/**
	 * Get the time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Set the time service.
	 *
	 * @param timeService the time service
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * @param wishListService the wishListService to set
	 */
	public void setWishListService(final WishListService wishListService) {
		this.wishListService = wishListService;
	}

	/**
	 * @return the wishListService
	 */
	public WishListService getWishListService() {
		return wishListService;
	}

	@Override
	public ShoppingCart moveItemFromWishListToCart(final ShoppingCart shoppingCart, final ShoppingItemDto dto) {

		if (!isSkuAllowedAddToCart(dto.getSkuCode(), shoppingCart)) {
			throw new ProductUnavailableException("product is not allowed to be added into cart");
		}

		final WishList wishList = wishListService.findOrCreateWishListByShopper(shoppingCart.getShopper());
		wishList.removeItem(dto.getSkuCode());
		wishListService.save(wishList);

		addItemToCart(shoppingCart, dto);

		if (dto.getQuantity() > 1) {
			shoppingCart.setItemWithNoTierOneFromWishList(true);
		}

		return saveShoppingCart(shoppingCart);
	}

	/**
	 * If the sku is allowed to add to cart.
	 *
	 * @param skuCode the sku code
	 * @param shoppingCart the shopping cart
	 * @return true if the sku is allowed to add to cart
	 */
	protected boolean isSkuAllowedAddToCart(final String skuCode, final ShoppingCart shoppingCart) {
		final ProductSku sku = productSkuService.findBySkuCode(skuCode);

		if (sku == null) {
			return false;
		}

		final Product product = sku.getProduct();

		return product != null && !product.isHidden() && product.isWithinDateRange(timeService.getCurrentTime())
				&& product.isInCatalog(shoppingCart.getStore().getCatalog()) && sku.isWithinDateRange(timeService.getCurrentTime());
	}

	@Override
	public void validateShoppingItemDto(final ShoppingItemDto shoppingItemDto) {
		shoppingItemAssembler.validateShoppingItemDto(shoppingItemDto);
	}
}
