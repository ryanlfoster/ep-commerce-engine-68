/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.util.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMemento;
import com.elasticpath.domain.shoppingcart.ShoppingCartMementoHolder;
import com.elasticpath.domain.shoppingcart.ShoppingCartVisitor;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ViewHistory;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * To help with the transition of Velocity templates (that currently rely on having ShoppingCart in the request session), we will proxy the
 * ShoppingCart from the Customer Session to keep it in sync. This class is only a temporary stop-gap measure to ease the transition of Velocity
 * templates over to use CustomerSession instead of ShoppingCart right off the request. (Created: 2011-01-18) Do not extend, propagate or otherwise
 * use this class anywhere else than in RequestHelper.
 */
@SuppressWarnings({  "PMD.ExcessiveClassLength", "PMD.TooManyMethods", "PMD.ExcessivePublicCount", "PMD.ExcessiveImports" })
@Deprecated
final class TransitionalShoppingCartProxy implements ShoppingCart, ShoppingCartMementoHolder {

	private static final long serialVersionUID = 641L;

	private final Shopper shopper;
	private ShoppingItemHasRecurringPricePredicate shoppingItemHasRecurringPricePredicate;

	/**
	 * The fallback locale if there is no shopping cart attached, ensures
	 * a sensible locale is available to velocity.
	 */
	private final Locale defaultLocale;

	/**
	 * Creates a new TransitionalShoppingCartProxy that is bound to a particular CustomerSession.
	 *
	 * @param shopper The shopper.
	 * @param defaultLocale The default locale.
	 */
	public TransitionalShoppingCartProxy(final Shopper shopper, final Locale defaultLocale) {
		if (shopper == null) {
			throw new IllegalArgumentException("TransiationalShoppingCartProxy cannot be bound to a null customerSession!");
		}
		this.shopper = shopper;
		this.defaultLocale = defaultLocale;
	}

	private ShoppingCart getCart() {
		return shopper.getCurrentShoppingCart();
	}

	@Override
	public ShoppingItem addCartItem(final ShoppingItem cartItem) {
		return getCart().addCartItem(cartItem);
	}

	@Override
	public ShoppingItem addShoppingCartItem(final ShoppingItem cartItem) {
		return getCart().addShoppingCartItem(cartItem);
	}

	@Override
	public void applyGiftCertificate(final GiftCertificate giftCertificate) {
		getCart().applyGiftCertificate(giftCertificate);
	}

	@Override
	public void applyLimitedUsagePromotionRuleCode(final String ruleCode, final long ruleId) {
		getCart().applyLimitedUsagePromotionRuleCode(ruleCode, ruleId);
	}

	@Override
	public boolean applyPromotionCode(final String promotionCode) {
		return getCart().applyPromotionCode(promotionCode);
	}

	@Override
	public void calculateShoppingCartTaxAndBeforeTaxPrices() {
		getCart().calculateShoppingCartTaxAndBeforeTaxPrices();
	}

	@Override
	public void clearEstimates() {
		getCart().clearEstimates();
	}

	@Override
	public void clearItems() {
		getCart().clearItems();
	}

	@Override
	public void clearSelectedShippingServiceLevel() {
		getCart().clearSelectedShippingServiceLevel();
	}

	@Override
	public void fireRules() {
		getCart().fireRules();
	}

	@Override
	public List<ShoppingItem> getAllItems() {
		return getCart().getAllItems();
	}

	@Override
	public Set<GiftCertificate> getAppliedGiftCertificates() {
		return getCart().getAppliedGiftCertificates();
	}

	@Override
	public Set<Long> getAppliedRules() {
		return getCart().getAppliedRules();
	}

	@Override
	public Collection<ShoppingItem> getApportionedLeafItems() {
		return getCart().getApportionedLeafItems();
	}

	@Override
	public Money getBeforeTaxShippingCost() {
		return getCart().getBeforeTaxShippingCost();
	}

	@Override
	public Money getBeforeTaxSubTotal() {
		return getCart().getBeforeTaxSubTotal();
	}

	@Override
	public Money getBeforeTaxTotal() {
		return getCart().getBeforeTaxTotal();
	}

	@Override
	public Address getBillingAddress() {
		return getCart().getBillingAddress();
	}

	@Override
	public CatalogViewResultHistory getBrowsingResultHistory() {
		return getCart().getBrowsingResultHistory();
	}

	@Override
	public ShoppingItem getCartItem(final String skuGuid) {
		return getCart().getCartItem(skuGuid);
	}

	@Override
	public ShoppingItem getCartItemByGuid(final String cartItemGuid) {
		return getCart().getCartItemByGuid(cartItemGuid);
	}

	@Override
	public ShoppingItem getCartItemById(final long cartItemId) {
		return getCart().getCartItemById(cartItemId);
	}

	@Override
	public List<ShoppingItem> getCartItems() {
		return getCart().getCartItems();
	}

	@Override
	public List<ShoppingItem> getCartItems(final String skuGuid) {
		return getCart().getCartItems(skuGuid);
	}

	@Override
	public List<Product> getCartProducts() {
		return getCart().getCartProducts();
	}

	@Override
	public CatalogViewResultHistory getCatalogViewResultHistory() {
		return getCart().getCatalogViewResultHistory();
	}

	@Override
	public Long getCmUserUID() {
		return getCart().getCmUserUID();
	}

	@Override
	public Order getCompletedOrder() {
		return getCart().getCompletedOrder();
	}

	@Override
	public Currency getCurrency() {
		return getCart().getCurrency();
	}

	@Override
	public CustomerSession getCustomerSession() {
		return getCart().getCustomerSession();
	}

	@Override
	public DiscountRecord getDiscountRecordForRuleAndAction(final Rule rule, final RuleAction action) {
		return getCart().getDiscountRecordForRuleAndAction(rule, action);
	}

	@Override
	public BigDecimal getGiftCertificateDiscount() {
		return getCart().getGiftCertificateDiscount();
	}

	@Override
	public Money getGiftCertificateDiscountMoney() {
		return getCart().getGiftCertificateDiscountMoney();
	}

	@Override
	public String getGuid() {
		return getCart().getGuid();
	}

	@Override
	public String getIpAddress() {
		return getCart().getIpAddress();
	}

	@Override
	public Category getLastCategory() {
		return getCart().getLastCategory();
	}

	@Override
	public Map<String, Long> getLimitedUsagePromotionRuleCodes() {
		return getCart().getLimitedUsagePromotionRuleCodes();
	}

	@Override
	public Locale getLocale() {
		if (getCart() == null || getCart().getLocale() == null) {
			return defaultLocale;
		}
		return getCart().getLocale();
	}

	@Override
	public Map<String, Money> getLocalizedTaxMap() {
		return getCart().getLocalizedTaxMap();
	}

	@Override
	public int getNumItems() {
		return getCart().getNumItems();
	}

	@Override
	public Set<String> getPromotionCodes() {
		return getCart().getPromotionCodes();
	}

	@Override
	public CatalogViewResultHistory getSearchResultHistory() {
		return getCart().getSearchResultHistory();
	}

	@Override
	public ShippingServiceLevel getSelectedShippingServiceLevel() {
		return getCart().getSelectedShippingServiceLevel();
	}

	@Override
	public Address getShippingAddress() {
		return getCart().getShippingAddress();
	}

	@Override
	public Money getShippingCost() {
		return getCart().getShippingCost();
	}

	@Override
	public List<ShippingServiceLevel> getShippingServiceLevelList() {
		return getCart().getShippingServiceLevelList();
	}

	@Override
	public ShoppingCartMemento getShoppingCartMemento() {
		return ((ShoppingCartMementoHolder) getCart()).getShoppingCartMemento();
	}

	@Override
	public Shopper getShopper() {
		return getCart().getShopper();
	}

	@Override
	public BigDecimal getSubtotal() {
		return getCart().getSubtotal();
	}

	@Override
	public BigDecimal getSubtotalDiscount() {
		return getCart().getSubtotalDiscount();
	}

	@Override
	public Money getSubtotalDiscountMoney() {
		return getCart().getSubtotalDiscountMoney();
	}

	@Override
	public Money getSubtotalMoney() {
		return getCart().getSubtotalMoney();
	}

	@Override
	public TaxCalculationResult getTaxCalculationResult() {
		return getCart().getTaxCalculationResult();
	}

	@Override
	public Map<TaxCategory, Money> getTaxMap() {
		return getCart().getTaxMap();
	}

	@Override
	public BigDecimal getTotal() {
		return getCart().getTotal();
	}

	@Override
	public Money getTotalMoney() {
		return getCart().getTotalMoney();
	}

	@Override
	public BigDecimal getTotalWeight() {
		return getCart().getTotalWeight();
	}

	@Override
	public ViewHistory getViewHistory() {
		return getCart().getViewHistory();
	}

	@Override
	public boolean hasLUCCForRule(final long ruleId) {
		return getCart().hasLUCCForRule(ruleId);
	}

	@Override
	public boolean hasSubtotalDiscount() {
		return getCart().hasSubtotalDiscount();
	}

	@Override
	public boolean isCartItemRemoved(final String skuCode) {
		return getCart().isCartItemRemoved(skuCode);
	}

	@Override
	public boolean isCodeValid() {
		return getCart().isCodeValid();
	}

	@Override
	public boolean isEstimateMode() {
		return getCart().isEstimateMode();
	}

	@Override
	public boolean isExchangeOrderShoppingCart() {
		return getCart().isExchangeOrderShoppingCart();
	}

	@Override
	public boolean isInclusiveTaxCalculationInUse() {
		return getCart().isInclusiveTaxCalculationInUse();
	}

	@Override
	public boolean isValidPromoCode(final String code) {
		return getCart().isValidPromoCode(code);
	}

	@Override
	public void removeCartItem(final long itemUid) {
		getCart().removeCartItem(itemUid);
	}

	@Override
	public void removeLimitedUsagePromotionRuleCode(final String ruleCode) {
		getCart().removeLimitedUsagePromotionRuleCode(ruleCode);
	}

	@Override
	public void removePromotionCode(final String promotionCode) {
		getCart().removePromotionCode(promotionCode);
	}

	@Override
	public boolean requiresShipping() {
		return getCart().requiresShipping();
	}

	@Override
	public void ruleApplied(final long ruleId, final long actionId, final ShoppingItem discountedItem, final BigDecimal discountAmount,
			final int quantityAppliedTo) {
		getCart().ruleApplied(ruleId, actionId, discountedItem, discountAmount, quantityAppliedTo);
	}

	@Override
	public void setBillingAddress(final Address address) {
		getCart().setBillingAddress(address);
	}

	@Override
	public void setCmUserUID(final Long cmUserUID) {
		getCart().setCmUserUID(cmUserUID);
	}

	@Override
	public void setCodeValid(final boolean codeValid) {
		getCart().setCodeValid(codeValid);

	}

	@Override
	public void setCompletedOrder(final Order order) {
		getCart().setCompletedOrder(order);
	}

	@Override
	public void setCurrency(final Currency currency) {
		getCart().setCurrency(currency);
	}

	@Override
	public void setCustomerSession(final CustomerSession customerSession) {
		throw new UnsupportedOperationException("TransitionalShoppingCartProxy does not support updating the CustomerSession.");
	}

	@Override
	public void setEstimateMode(final boolean estimateMode) {
		getCart().setEstimateMode(estimateMode);
	}

	@Override
	public void setExchangeOrderShoppingCart(final boolean isExchangeOrderShoppingCart) {
		getCart().setExchangeOrderShoppingCart(isExchangeOrderShoppingCart);
	}

	@Override
	public void setIpAddress(final String ipAddress) {
		getCart().setIpAddress(ipAddress);
	}

	@Override
	public void setLastCategory(final Category category) {
		getCart().setLastCategory(category);
	}

	@Override
	public void setLocale(final Locale locale) {
		getCart().setLocale(locale);
	}

	@Override
	public void setSelectedShippingServiceLevelUid(final long selectedSSLUid) {
		getCart().setSelectedShippingServiceLevelUid(selectedSSLUid);
	}

	@Override
	public void setShippingAddress(final Address address) {
		getCart().setShippingAddress(address);
	}

	@Override
	public void setShippingCost(final BigDecimal shippingCost) {
		getCart().setShippingCost(shippingCost);
	}

	@Override
	public void setShippingServiceLevelList(final List<ShippingServiceLevel> shippingServiceLevelList) {
		getCart().setShippingServiceLevelList(shippingServiceLevelList);
	}

	@Override
	public void setShoppingCartMemento(final ShoppingCartMemento shoppingCartMemento) {
		((ShoppingCartMementoHolder) getCart()).setShoppingCartMemento(shoppingCartMemento);
	}

	@Override
	public void setShopper(final Shopper shopper) {
		getCart().setShopper(shopper);
	}

	@Override
	public void setSubtotalDiscount(final BigDecimal discountAmount, final long ruleId, final long actionId) {
		getCart().setSubtotalDiscount(discountAmount, ruleId, actionId);
	}

	@Override
	public void setTaxCalculationResult(final TaxCalculationResult taxCalculationResult) {
		getCart().setTaxCalculationResult(taxCalculationResult);
	}

	@Override
	public void shippingRuleApplied(final long ruleId, final long actionId, final BigDecimal discountAmount) {
		getCart().shippingRuleApplied(ruleId, actionId, discountAmount);
	}

	@Override
	public Utility getUtility() {
		return getCart().getUtility();
	}

	@Override
	public void initialize() {
		getCart().initialize();
	}

	@Override
	public Store getStore() {
		return getCart().getStore();
	}

	@Override
	public void setStore(final Store store) {
		getCart().setStore(store);
	}

	@Override
	public Collection<? extends ShoppingItem> getLeafShoppingItems() {
		return getCart().getLeafShoppingItems();
	}

	@Override
	public Collection<? extends ShoppingItem> getRootShoppingItems() {
		return getCart().getRootShoppingItems();
	}

	@Override
	public ShoppingItem getShoppingItemByGuid(final String itemGuid) {
		return getCart().getShoppingItemByGuid(itemGuid);
	}

	@Override
	public boolean isMergedNotification() {
		return getCart().isMergedNotification();
	}

	@Override
	public void setMergedNotification(final boolean merged) {
		getCart().setMergedNotification(merged);
	}

	@Override
	public boolean hasRecurringPricedShoppingItems() {
		return getCart().hasRecurringPricedShoppingItems();
	}

	@Override
	public void setShoppingItemHasRecurringPricePredicate(final ShoppingItemHasRecurringPricePredicate shoppingItemHasRecurringPricePredicate) {
		this.shoppingItemHasRecurringPricePredicate = shoppingItemHasRecurringPricePredicate;
	}

	@Override
	public ShoppingItemHasRecurringPricePredicate getShoppingItemHasRecurringPricePredicate() {
		return shoppingItemHasRecurringPricePredicate;
	}

	@Override
	@Transient
	public boolean hasItemWithNoTierOneFromWishList() {
		return getCart().hasItemWithNoTierOneFromWishList();
	}

	@Override
	public void setItemWithNoTierOneFromWishList(
			final boolean itemWithNoTierOneFromWishList) {
		getCart().setItemWithNoTierOneFromWishList(itemWithNoTierOneFromWishList);
	}

	@Override
	public void accept(final ShoppingCartVisitor visitor) {
		getCart().accept(visitor);
	}

	@Override
	public Collection<String> getNotPurchasableCartItemSkus() {
		return getCart().getNotPurchasableCartItemSkus();
	}

	@Override
	public Set<ShipmentType> getShipmentTypes() {
		return getCart().getShipmentTypes();
	}

	@Override
	public Date getLastModifiedDate() {
		return getCart().getLastModifiedDate();
	}

}
