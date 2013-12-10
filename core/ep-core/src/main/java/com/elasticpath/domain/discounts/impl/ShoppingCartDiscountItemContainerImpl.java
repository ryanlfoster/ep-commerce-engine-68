/**
 *
 */
package com.elasticpath.domain.discounts.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.discounts.ShoppingCartDiscountItemContainer;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.sellingchannel.director.CartDirector;

/**
 * A shopping cart discount item container where the discounts can be applied
 * to. It also provides helper methods to get cart items and calculate subtotal
 * for discount calculation.
 */
public class ShoppingCartDiscountItemContainerImpl extends AbstractEpDomainImpl implements ShoppingCartDiscountItemContainer {

	private static final long serialVersionUID = 1L;

	private ShoppingCart cart;
	private static final Logger LOG = Logger.getLogger(ShoppingCartDiscountItemContainerImpl.class);
	@Override
	public void recordRuleApplied(final long ruleId, final long actionId,
			final ShoppingItem discountedItem, final BigDecimal discountAmount,
			final int quantityAppliedTo) {
		cart.ruleApplied(ruleId, actionId, discountedItem, discountAmount, quantityAppliedTo);
	}

	@Override
	public Catalog getCatalog() {
		return cart.getStore().getCatalog();
	}

	@Override
	public List<ShoppingItem> getItemsLowestToHighestPrice() {
		return sort(filteredCartItems(cart.getCartItems()), new LowestToHighestPriceComparator());
	}

	/**
	 * Sort the cart item based on price lowest to highest.
	 */
	private static class LowestToHighestPriceComparator implements Comparator<ShoppingItem>, Serializable {

		private static final long serialVersionUID = 1L;

		public int compare(final ShoppingItem cartItem1, final ShoppingItem cartItem2) {
			return cartItem1.getTotal().compareTo(cartItem2.getTotal());
		}
	}

	private List<ShoppingItem> sort(final List< ? extends ShoppingItem> cartItems,
			final Comparator<ShoppingItem> comparator) {
		final List<ShoppingItem> sortedCartItems = new ArrayList<ShoppingItem>();
		sortedCartItems.addAll(cartItems);
		Collections.sort(sortedCartItems, comparator);
		return sortedCartItems;
	}

	/**
	 * Gets the <code>ShoppingItem</code> calculated total price amount
	 * divided by total quantity,
	 * with discounts applied if any have been set.
	 *
	 * @param cartItem cartItem holds price, discount and quantity info.
	 *
	 * @return calculated price amount for the <code>ShoppingItem</code>.
	 *
	 * @see com.elasticpath.domain.shoppingcart.ShoppingItem#applyDiscount(BigDecimal)
	 *
	 * @see com.elasticpath.domain.impl.AbstractShoppingItemImpl#getTotal()
	 * @see com.elasticpath.domain.order.impl.OrderSkuImpl#getTotal()
	 */
	public BigDecimal getPriceAmount(final ShoppingItem cartItem) {
		final int calcScale = 10;
		return cartItem.getTotal().getAmount().divide(BigDecimal.valueOf(cartItem.getQuantity()), calcScale, RoundingMode.HALF_UP);
	}

	@Override
	public String getCartItemProductCode(final ShoppingItem cartItem) {
		return cartItem.getProductSku().getProduct().getCode();
	}

	@Override
	public String getCartItemSkuCode(final ShoppingItem cartItem) {
		return cartItem.getProductSku().getSkuCode();
	}

	@Override
	public ShoppingItem addCartItem(final String skuCode, final int numItems) {
		ShoppingItem cartItem = cart.getCartItem(skuCode);
		if (cartItem == null && !cart.isCartItemRemoved(skuCode)) {
			final CartDirector cartDirector = (getBean("cartDirector"));
			final ShoppingItemDtoFactory shoppingItemDtoFactory = (getBean("shoppingItemDtoFactory"));
			ShoppingItemDto dto;
			dto = shoppingItemDtoFactory.createDto(skuCode, numItems);
			try {
				cartItem = cartDirector.addItemToCart(getShoppingCart(), dto);
			} catch (final ProductNotPurchasableException e) {
				LOG.error(e.getMessage());
				return null;
			}
		}

		return cartItem;
	}

	/**
	 * Applies a discount to the shopping cart subtotal.
	 *
	 * @param discountAmount
	 *            the amount to discount the subtotal by as a BigInteger
	 * @param ruleId The rule which caused the discount
	 * @param actionId The action which caused the discount
	 */
	public void applySubtotalDiscount(final BigDecimal discountAmount, final long ruleId, final long actionId) {
		cart.setSubtotalDiscount(discountAmount, ruleId, actionId);
	}

	/**
	 * Calculates the subtotal of the cart minus the amount of all the items
	 * representing a gift certificate. This is required so that we do not apply
	 * promotions on gift certificates in the cart.
	 *
	 * @return the subtotal
	 */
	public BigDecimal calculateSubtotal() {

		BigDecimal nonDiscountableAmount = BigDecimal.ZERO;
		for (final ShoppingItem cartItem : cart.getAllItems()) {
			if (!cartItem.isDiscountable()) {
				nonDiscountableAmount = nonDiscountableAmount.add(cartItem.getTotal().getAmount());
			}
		}

		return cart.getSubtotal().subtract(nonDiscountableAmount);

	}

	public ShoppingCart getShoppingCart() {
		return cart;
	}

	/**
	 * Sets the shopping cart.
	 *
	 * @param cart the cart to set
	 */
	public void setShoppingCart(final ShoppingCart cart) {
		this.cart = cart;
	}

	private List<ShoppingItem> filteredCartItems(final List<ShoppingItem> unfiltered) {
		final List<ShoppingItem> filtered = new java.util.LinkedList<ShoppingItem>();
		for (final ShoppingItem item : unfiltered) {
			if (item.canReceiveCartPromotion()) {
				filtered.add(item);
			}
		}
		return filtered;
	}
}
