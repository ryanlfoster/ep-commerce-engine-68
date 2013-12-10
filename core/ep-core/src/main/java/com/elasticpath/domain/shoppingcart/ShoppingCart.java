/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart; //NOPMD

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.ShoppingItemContainer;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.StoreObject;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * <code>ShoppingCart</code> represents a shopping cart of a <code>Customer</code>.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface ShoppingCart extends EpDomain, StoreObject, ShoppingItemContainer {

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	String getGuid();

	/**
	 * Get the set of applied gift certificates.
	 *
	 * @return the giftCertificateRedeems
	 */
	Set<GiftCertificate> getAppliedGiftCertificates();

	/**
	 * Adds the given gift certificate to the set which will be redeemed / applied to this shopping cart.
	 *
	 * @param giftCertificate the gift certificate.
	 */
	void applyGiftCertificate(final GiftCertificate giftCertificate);

	/**
	 * Get the amount redeemed from gift certificate.
	 *
	 * @return the gift certificate discounted from the total
	 */
	BigDecimal getGiftCertificateDiscount();

	/**
	 * Get the amount redeemed from gift certificate.
	 *
	 * @return the gift certificate discount as a <code>Money</code> object
	 */
	Money getGiftCertificateDiscountMoney();

	/**
	 * Return the <code>CustomerSession</code>. instance. Customer sessions track information about sessions where the customer may not be logged
	 * in.
	 *
	 * @return the <code>CustomerSession</code> instance
 	 * @deprecated {@link CustomerSession}s should be attached to {@link ShoppingCart}s through {@link Shopper.}
	 * Use ShoppingCart.
	 */
	@Deprecated
	CustomerSession getCustomerSession();

	/**
	 * Set the <code>CustomerSession</code>. instance. Customer sessions track information about sessions where the customer may not be logged in.
	 *
	 * @param customerSession the <code>CustomerSession</code> instance
	 * @deprecated {@link CustomerSession}s should be attached to {@link ShoppingCart}s through {@link Shopper}.
	 * To attach a {@link CustomerSession} to a {@link ShoppingCart}, use this.setShopper(customerSession.getShopper().
	 */
	@Deprecated
	void setCustomerSession(final CustomerSession customerSession);

	/**
	 * Get the locale of the customer corresponding to the shopping cart.
	 *
	 * @return the <code>Locale</code>
	 */
	Locale getLocale();

	/**
	 * Set the locale of the customer corresponding to the shopping cart.
	 *
	 * @param locale the <code>Locale</code>
	 */
	void setLocale(final Locale locale);

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
	 * Get the cart items in the shopping cart.
	 *
	 * @return the cart items in the shopping cart
	 */
	List<ShoppingItem> getCartItems();

	/**
	 * Get the products in the shopping cart.
	 *
	 * @return the products in the shopping cart
	 */
	List<Product> getCartProducts();

	/**
	 * Add an item to the cart. If a cart item exists the the same SKU as the given cart item, then the existing cart item will be updated to reflect
	 * the sum of the previous cart item quantity and the quantity of the new cart item.
	 *
	 * @param cartItem the <code>CartItem</code> to add
	 * @return the added cart item.
	 * @deprecated Use CartDirector.addToCart.
	 */
	@Deprecated
	ShoppingItem addCartItem(final ShoppingItem cartItem);

	/**
	 * Add an item to the cart. No update of the quantity will be done, nor will any checking of the isConfigurable flag be done.
	 * This method should only be used by CartDirector.
	 *
	 * @param cartItem the <code>CartItem</code> to add
	 * @return the added cart item
	 */
	ShoppingItem addShoppingCartItem(final ShoppingItem cartItem);

	/**
	 * Remove an item from the cart.
	 *
	 * @param itemUid the uidPk of the <code>CartItem</code> to remove
	 */
	void removeCartItem(final long itemUid);

	/**
	 * Get the first cart item by the GUID of its SKU.
	 *
	 * @param skuGuid the GUID of the SKU in the cart item to be retrieved.
	 * @return the corresponding <code>ShoppingItem</code> or null if not found
	 */
	ShoppingItem getCartItem(final String skuGuid);

	/**
	 * Get the all cart items by the GUID of its SKU.
	 *
	 * @param skuGuid the GUID of the SKU in the cart item to be retrieved.
	 * @return the corresponding list of <code>ShoppingItem</code> found
	 */
	 List<ShoppingItem> getCartItems(String skuGuid);

	/**
	 * Return the number of items in the shopping cart.
	 *
	 * @return the number of items
	 */
	int getNumItems();

	/**
	 * Get the subtotal of all items in the cart.
	 *
	 * @return a <code>BigDecimal</code> object representing the subtotal
	 */
	BigDecimal getSubtotal();

	/**
	 * Get the subtotal of all items in the cart.
	 *
	 * @return a <code>Money</code> object representing the subtotal
	 */
	Money getSubtotalMoney();

	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc.
	 *
	 * @return a <code>Money</code> object representing the total
	 */
	Money getTotalMoney();

	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc.
	 *
	 * @return a <code>BigDecimal</code> object representing the total
	 */
	BigDecimal getTotal();

	/**
	 * Applies a discount to the shopping cart subtotal.
	 *
	 * @param discountAmount the amount to discount the subtotal by as a BigInteger
	 * @param ruleId TODO
	 * @param actionId TODO
	 */
	void setSubtotalDiscount(BigDecimal discountAmount, long ruleId, long actionId);

	/**
	 * Get the discount to the shopping cart subtotal.
	 *
	 * @return the amount discounted from the subtotal
	 */
	BigDecimal getSubtotalDiscount();

	/**
	 * Get the amount discounted from the order subtotal.
	 *
	 * @return the order subtotal discount as a <code>Money</code> object
	 */
	Money getSubtotalDiscountMoney();

	/**
	 * Returns true if an order subtotal discount has been applied.
	 *
	 * @return true if an order subtotal discount has been applied
	 */
	boolean hasSubtotalDiscount();

	/**
	 * Empties the shopping cart (e.g. after a checkout)
	 */
	void clearItems();

	/**
	 * Set the preferred billing address.
	 *
	 * @param address the <code>Address</code>
	 */
	void setBillingAddress(final Address address);

	/**
	 * Get the preferred billing address.
	 *
	 * @return the preferred shipping address
	 */
	Address getBillingAddress();

	/**
	 * Set the preferred shipping address.
	 *
	 * @param address the <code>Address</code>
	 */
	void setShippingAddress(final Address address);

	/**
	 * Get the preferred shipping address.
	 *
	 * @return the preferred shipping address
	 */
	Address getShippingAddress();

	/**
	 * Returns the last category.
	 *
	 * @return the last category
	 */
	Category getLastCategory();

	/**
	 * Sets the last category.
	 *
	 * @param category the category to set.
	 */
	void setLastCategory(Category category);

	/**
	 * Set a reference to the completed order for the items previously checked out.
	 *
	 * @param order the completed order
	 */
	void setCompletedOrder(Order order);

	/**
	 * Get a reference to the completed order for the items previously checked out.
	 *
	 * @return the completed Order, or null if no completed order has been set.
	 */
	Order getCompletedOrder();

	/**
	 * Returns the search result history stored. If none is stored, a new one will be created and returned.
	 *
	 * @return the search result history
	 */
	CatalogViewResultHistory getSearchResultHistory();

	/**
	 * Returns the browsing result history stored. If none is stored, a new one will be created and returned.
	 *
	 * @return the browsing result history
	 */
	CatalogViewResultHistory getBrowsingResultHistory();

	/**
	 * Returns the catalog view result history stored. A catalog view result history might be a search or a browsing. If none is stored, return
	 * <code>null</code>
	 *
	 * @return the catalog view result history
	 */
	CatalogViewResultHistory getCatalogViewResultHistory();

	/**
	 * Return the shippingCost of the <code>ShoppingCart</code>.
	 *
	 * @return the shippingCost of the <code>ShoppingCart</code>
	 */
	Money getShippingCost();


	/**
	 * Return the list of shippingServiceLevel list available based on the current shopping cart info.
	 *
	 * @return the list of shippingServiceLevel list available
	 *         based on the current shopping cart info.
     *         It is guaranteed to be not null.
	 */
	List<ShippingServiceLevel> getShippingServiceLevelList();

	/**
	 * Set the list of shippingServiceLevel list available based on the current shopping cart info.
	 *
	 * @param shippingServiceLevelList the list of shippingServiceLevel
	 *        list available based on the current shopping cart info.
     *        If parameter value is null the list will be cleared.
	 */
	void setShippingServiceLevelList(final List<ShippingServiceLevel> shippingServiceLevelList);

	/**
	 * Get the selectedShippingServiceLevel.
	 *
	 * @return the selected ShippingServiceLevel.
	 */
	ShippingServiceLevel getSelectedShippingServiceLevel();

	/**
	 * Set the selectedShippingServiceLevelUid and update the shippingCost correspondingly.
	 *
	 * @param selectedSSLUid - the selected ShippingServiceLevel uid.
	 */
	void setSelectedShippingServiceLevelUid(final long selectedSSLUid);

	/**
	 * Resets the selected <code>ShippingServiceLevel</code> to null.
	 */
	void clearSelectedShippingServiceLevel();

	/**
	 * Get the totalWeight of items in <code>ShoppingCart</code>.
	 *
	 * @return totalWeight
	 */
	BigDecimal getTotalWeight();

	/**
	 * Returns true if the cart contains items that must be shipped to the customer.
	 *
	 * @return true if the cart contains items that must be shipped to the customer.
	 */
	boolean requiresShipping();

	/**
	 * Return true if the "inclusive" tax calculation method is in use; otherwise false. This is based on the shippingAddress.
	 *
	 * @return true if the "inclusive" tax calculation method is in use; otherwise false.
	 */
	boolean isInclusiveTaxCalculationInUse();

	/**
	 * Return the localized tax category name -> tax value (<code>Money</code>) map for this <code>ShoppingCart</code>.
	 *
	 * @return the localized tax category name -> tax value (<code>Money</code>) map.
	 */
	Map<String, Money> getLocalizedTaxMap();

	/**
	 * Return the before-tax shippingCost.
	 *
	 * @return the before-tax shippingCost.
	 */
	Money getBeforeTaxShippingCost();

	/**
	 * Return the before-tax subtotal.
	 *
	 * @return the before-tax subtotal.
	 */
	Money getBeforeTaxSubTotal();

	/**
	 * Return the before-tax total.
	 *
	 * @return the before-tax total.
	 */
	Money getBeforeTaxTotal();

	/**
	 * Calculate the taxes and before-tax prices for the giveing shoppingCart, including cart item and shipping cost. The calculation results will be
	 * set in the given shopping cart.
	 */
	void calculateShoppingCartTaxAndBeforeTaxPrices();

	/**
	 * Returns the cart item with the given id.
	 *
	 * @param cartItemId the cart item id
	 * @return the cart item with the given id
	 */
	ShoppingItem getCartItemById(final long cartItemId);

	/**
	 * Returns the cart item which matches {@code cartItemGuid}.
	 *
	 * @param cartItemGuid The guid to find.
	 * @return The cart item.
	 */
	ShoppingItem getCartItemByGuid(String cartItemGuid);

	/**
	 * Gets the list of promotion codes successfully applied to the cart.
	 *
	 * @return the promotion codes
	 */
	Set<String> getPromotionCodes();

	/**
	 * Add a new promotion code to the list of promotion codes added to the shopping cart.
	 *
	 * @param promotionCode the promotion code to add to the list
	 * @return if code is a valid promotion code
	 */
	boolean applyPromotionCode(final String promotionCode);

	/**
	 * Remove a promotion code from the list of promotion codes added to the shopping cart.
	 *
	 * @param promotionCode the promotion code to remove from the list
	 */
	void removePromotionCode(final String promotionCode);

	/**
	 * Validates if the code matches a promotion code in the system.
	 *
	 * @param code the code to validate
	 * @return true if the code is a valid promotion code
	 */
	boolean isValidPromoCode(final String code);

	/**
	 * Indicates if the promotion or gift certificate code entered by the user is valid.
	 *
	 * @return true if the code is valid
	 */
	boolean isCodeValid();

	/**
	 * Set whether or not the promotion or gift certificate code entered by the user is valid.
	 *
	 * @param codeValid set to true if the code is valid
	 */
	void setCodeValid(final boolean codeValid);

	/**
	 * Removes shipping and tax estimates from the shopping cart.
	 */
	void clearEstimates();

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
	 * Get the View History of the user from the shopping cart.
	 *
	 * @return the ViewHistory
	 */
	ViewHistory getViewHistory();

	/**
	 * Get the indicator of whether in the estimate shipping and taxes mode.
	 *
	 * @return true when estimating shipping and taxes; otherwise, false.
	 */
	boolean isEstimateMode();

	/**
	 * Set the indicator of whether in the estimate shipping and taxes mode. Disabling estimate mode cleards the billing and shipping addresses
	 * (because they may not be full, valid addresses), but tax and shipping calculations are not cleared. If you wish to clear the estimated
	 * calculations/values, then call clearEstimates().
	 *
	 * @param estimateMode true when estimating shipping and taxes; otherwise, false.
	 */
	void setEstimateMode(final boolean estimateMode);

	/**
	 * Get all the items in the shopping cart, including the
	 * ShoppingCartItems, WishListItems, GiftCertificateItems.
	 *
	 * @return an unmodifiable list of all the items in the shopping cart
	 */
	List<ShoppingItem> getAllItems();

	/**
	 * Forces the shopping cart to apply promotion rules. Promotion rules will usually be applied by the cart automatically as required. However, it
	 * is sometimes necessary to force the cart to fire rules when the cart is loaded without a state change.
	 */
	void fireRules();

	/**
	 * Indicates that the given rule was applied by the promotion rule engine.
	 *
	 * @param ruleId the uidPk of the <code>Rule</code>
	 * @param actionId The action id of the rule.
	 * @param discountedItem The item that was discounted
	 * @param discountAmount The amount of the discount.
	 * @param quantityAppliedTo The item quantity that the discount was applied to.
	 */
	void ruleApplied(final long ruleId, long actionId, ShoppingItem discountedItem, BigDecimal discountAmount, int quantityAppliedTo);

	/**
	 * Indicates that the given rule was applied by the promotion rule engine and updates shipping.
	 *
	 * @param ruleId the uidPk of the <code>Rule</code>
	 * @param actionId The action id of the rule.
	 * @param discountAmount The amount of the discount.
	 */
	void shippingRuleApplied(final long ruleId, final long actionId,
			final BigDecimal discountAmount);

	/**
	 * Get the set of rules that have been applied to the cart.
	 *
	 * @return a set of <code>Long</code> Rule UidPks
	 */
	Set<Long> getAppliedRules();

	/**
	 * Retrieves the current tax values.
	 * @return the current tax values
	 */
	TaxCalculationResult getTaxCalculationResult();

	/**
	 * Retrieves the mapping of TaxCategories to values.
	 * @return the map of TaxCategories to values. Never <code>null</code>.
	 */
	Map<TaxCategory, Money> getTaxMap();

	/**
	 * Sets the tax calculation result.
	 * @param taxCalculationResult the tax calculation result to set
	 */
	void setTaxCalculationResult(
			final TaxCalculationResult taxCalculationResult);

	/**
	 * Gets the cmUserUID.
	 *
	 * @return CmUserUID the cmUser's uid
	 */
	Long getCmUserUID();

	/**
	 * Sets the CmUserUID.
	 *
	 * @param cmUserUID the cmUser's uid
	 */
	void setCmUserUID(final Long cmUserUID);

	/**
	 * Set shipping cost. Basically shipping cost is a calculated value. This setter is required for exchange order.
	 *
	 * @param shippingCost the shipping cost
	 */
	void setShippingCost(BigDecimal shippingCost);

	/**
	 * Returns true is this shopping cart will be used for checking out exchange order, false
	 * for ordinary shopping cart.
	 *
	 * @return true if this cart is exchange shopping cart, false otherwise.
	 */
	boolean isExchangeOrderShoppingCart();

	/**
	 * Sets the flag depending if this shopping cart will be used for checking out exchange order,
	 * or for ordinary shopping cart.
	 *
	 * @param isExchangeOrderShoppingCart exchange shopping cart flag.
	 */
	void setExchangeOrderShoppingCart(boolean isExchangeOrderShoppingCart);

	/**
	 * Checks if a cart item with specific SKU code was previously removed.
	 *
	 * @param skuCode the SKU code to check
	 * @return true if a cart item with this SKU code was removed
	 */
	boolean isCartItemRemoved(String skuCode);

	/**
	 * Gets the map of limited usage promotion rule codes to rule ids.
	 *
	 * @return the map of limited usage promotion rule codes to rule ids
	 */
	Map<String, Long> getLimitedUsagePromotionRuleCodes();

	/**
	 * Add a new limited usage promotion rule code to the list of rule codes checked against the shopping cart.
	 *
	 * @param ruleCode the rule code to add to the list
	 * @param ruleId the id of the rule the promotion code belongs to
	 */
	void applyLimitedUsagePromotionRuleCode(final String ruleCode, final long ruleId);

	/**
	 * Remove a rule code from the list of limited use promotion rule codes added to the shopping cart.
	 *
	 * @param ruleCode the code to remove from the list
	 */
	void removeLimitedUsagePromotionRuleCode(final String ruleCode);

	/**
	 * Get the leaf items which have had prices apportioned to them, if they are bundle constituents.
	 *
	 * @return a collection of leaf shopping items with apportioned prices.
	 */
	Collection<ShoppingItem> getApportionedLeafItems();

	/**
	 * Returns the discount record for the items that were discounted based on {@code rule}.
	 * @param rule The rule to sue.
	 * @param action TODO
	 * @return The matching discount record or null.
	 */
	DiscountRecord getDiscountRecordForRuleAndAction(final Rule rule, RuleAction action);

	/**
	 * Returns if cart has limited usage coupon code applied for the given rule id.
	 * @param ruleId the rule id to check against
	 * @return true if lucc exists in the cart for the rule
	 */
	boolean hasLUCCForRule(long ruleId);

	/**
	 * Gets the {@link Shopper} for this ShoppingCart.
	 *
	 * @return Shopper
	 */
	Shopper getShopper();

	/**
	 * Sets the {@link Shopper} for this ShoppingCart.
	 *
	 * @param shopper the {@link Shopper}.
	 */
	void setShopper(Shopper shopper);


	/**
	 * Returns the shopping cart merged state notification.
	 *
	 * @return true if the cart was recently merged and a notification is desired, false otherwise
	 */
	boolean isMergedNotification();

	/**
	 * Set shopping cart merged state notification.
	 *
	 * @param merged use true if the cart was recently merged and a notification is desired, false otherwise
	 */
	void setMergedNotification(boolean merged);

	/**
	 * Sets the shopping item recurring item predicate.
	 * @param shoppingItemHasRecurringPricePredicate the predicate
	 * */
	void setShoppingItemHasRecurringPricePredicate(final ShoppingItemHasRecurringPricePredicate shoppingItemHasRecurringPricePredicate);

	/**
	 * Gets the shopping item recurring item predicate.
	 * @return the predicate
	 * */
	ShoppingItemHasRecurringPricePredicate getShoppingItemHasRecurringPricePredicate();

	/**
	 * Indicates whether it has recurring prices shopping items in the cart.
	 *
	 * @return true or false.
	 */
	boolean hasRecurringPricedShoppingItems();

	/**
	 * Indicates whether a product has been moved from the wishlist to shopping cart without a price tier of 1.
	 *
	 * @return true the first time the shopping cart is viewed with an item moved from the wishlist without a tier 1 price.
	 */
	boolean hasItemWithNoTierOneFromWishList();


	/**
	 * Sets the flag which indicates that the shopping cart has an item added from wishlist without a tier 1 price.
	 * @param itemWithNoTierOneFromWishList set to true if the shopping cart has an item from wishlist that has no tier 1 price, false otherise.
	 */
	void setItemWithNoTierOneFromWishList(final boolean itemWithNoTierOneFromWishList);

	/**
	 * Accepts a ShoppingCartVisitor and passes it to this cart's child ShoppingItems.
	 *
	 * @param visitor The visitor.
	 */
	void accept(ShoppingCartVisitor visitor);

	/**
	 * @return a collection of automatically removed cart items that became non-purchasable
	 */
	Collection<String> getNotPurchasableCartItemSkus();

	/**
	 * Returns the date when the object was last modified.
	 *
	 * @return the date when the object was last modified
	 */
	Date getLastModifiedDate();
}
