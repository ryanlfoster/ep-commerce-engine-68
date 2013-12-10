/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.SCCMCurrencyMissingException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.evaluator.impl.ShoppingCartShipmentTypeEvaluator;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMemento;
import com.elasticpath.domain.shoppingcart.ShoppingCartMementoHolder;
import com.elasticpath.domain.shoppingcart.ShoppingCartVisitor;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingList;
import com.elasticpath.domain.shoppingcart.ViewHistory;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.plugin.payment.exceptions.GiftCertificateCurrencyMismatchException;
import com.elasticpath.plugin.payment.exceptions.GiftCertificateZeroBalanceException;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;
import com.elasticpath.service.pricing.PriceLookupService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.EpRuleEngine;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.impl.DiscountApportioningCalculator;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.ConditionEvaluatorService;

/**
 * A Shopping Cart contains both transient and persistable data. Since some persistence layers, upon merge or update, will return a brand new
 * instance of the object being merged or updated with the database records, we don't want to lose the transient data every time we do a save or
 * update. Therefore we introduce with this implementation the concept of a ShoppingCartMemento, which represents the persistable portion of the
 * ShoppingCart. When the shopping cart is saved to the database, in reality we only have to save the Memento portion and leave the transient fields
 * in the class instance, simply re-injecting the Memento portion that is returned from the persistence layer. Note: The guid of a shopping cart is
 * actually the guid of the customer session it belongs to.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessiveClassLength", "PMD.TooManyMethods",
					"PMD.ExcessivePublicCount", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports" })
public class ShoppingCartImpl extends AbstractEpDomainImpl implements ShoppingCart, ShoppingCartMementoHolder {

	private static final long serialVersionUID = 5000000001L;

	private static final Logger LOG = Logger.getLogger(ShoppingCartImpl.class.getName());

	private CustomerSession customerSession;

	private BigDecimal subtotalDiscount = BigDecimal.ZERO;

	private Address shippingAddress;

	private Address billingAddress;

	private Order completedOrder;

	private Category lastCategory;

	private CatalogViewResultHistory searchResultHistory;

	private List<ShippingServiceLevel> shippingServiceLevelList = new ArrayList<ShippingServiceLevel>();

	private ShippingServiceLevel selectedShippingServiceLevel;

	private CatalogViewResultHistory browsingResultHistory;

	/** The map of rule id to promotion codes added to the cart. */
	private final Map<Long, Set<String>> promotionCodes = new HashMap<Long, Set<String>>();

	/** The map of limited usage promotion rule codes to rule ids. */
	private final Map<String, Long> limitedUsagePromotionRuleCodes = new HashMap<String, Long>();

	/** Specifies if the gift certificate or promotion code entered is valid. */
	private boolean codeValid = true;

	private String ipAddress;

	private ViewHistory viewHistory;

	private final Set<Long> appliedRuleIds = new HashSet<Long>();

	private boolean estimateMode;

	private final Set<GiftCertificate> appliedGiftCertificates = new HashSet<GiftCertificate>();

	private BigDecimal appliedGiftCertificateTotal = BigDecimal.ZERO;

	private TaxCalculationResult taxCalculationResult;

	private transient TaxCalculationService taxCalculationService;

	private Long cmUserUID;

	private BigDecimal shippingCost;

	private boolean exchangeOrderShoppingCart = false;

	private ShoppingCartMemento shoppingCartMemento;

	private transient GiftCertificateService giftCertificateService;

	private transient RuleService ruleService;
	private transient ConditionEvaluatorService conditionEvaluatorService;

	/**
	 * Holds references to cart items manually removed by customers. Used by promotion rules so that a free cart item could be removed manually by
	 * customer. Used also by templates (viewCart.vm)
	 */
	private final Collection<String> removedCartItemSkus = new HashSet<String>();

	/**
	 * Holds references to cart items that were automatically removed during the cart merge process in case
	 * if they are not purchasable anymore (the product became not available for example).
	 */
	private final Collection<String> notPurchasableCartItemSkus = new HashSet<String>();

	private transient CouponUsageService couponUsageService;

	private transient CouponService couponService;

	private final List<Coupon> couponUsagesToCreate = new ArrayList<Coupon>();

	private Shopper shopper;
	private Store store;

	@Transient
	private final Map<Long, Map<Long, DiscountRecord>> ruleDiscountRecordMap = new HashMap<Long, Map<Long, DiscountRecord>>();

	private boolean mergedNotification;

	private boolean itemWithNoTierOneFromWishList;

	private ShoppingItemHasRecurringPricePredicate shoppingItemHasRecurringPricePredicate;

	/**
	 * Default Constructor.
	 */
	public ShoppingCartImpl() {
		super();
		shoppingItemHasRecurringPricePredicate = new ShoppingItemHasRecurringPricePredicate();
	}

	/**
	 * Gets the cmUserUID.
	 *
	 * @return CmUserUID the cmUser's uid
	 */
	@Override
	public Long getCmUserUID() {
		return cmUserUID;
	}

	/**
	 * Sets the CmUserUID.
	 *
	 * @param cmUserUID the cmUser's uid
	 */
	@Override
	public void setCmUserUID(final Long cmUserUID) {
		this.cmUserUID = cmUserUID;
	}

	/**
	 * Get the applied gift certificate.
	 *
	 * @return the appliedGiftCertificates
	 */
	@Override
	public Set<GiftCertificate> getAppliedGiftCertificates() {
		return appliedGiftCertificates;
	}

	/**
	 * Get the gift certificate service.
	 *
	 * @return GiftCertificateService
	 */
	protected GiftCertificateService getGiftCertificateService() {
		// Note that the service field must be transient and included in ShoppingCartImplIntegrationTest.testSerialization().
		if (giftCertificateService == null) {
			giftCertificateService = this.getBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE);
		}
		return giftCertificateService;
	}

	/**
	 * Get the rule service.
	 *
	 * @return RuleService
	 */
	protected RuleService getRuleService() {
		// Note that the service field must be transient and included in ShoppingCartImplIntegrationTest.testSerialization().
		if (ruleService == null) {
			ruleService = this.getBean(ContextIdNames.RULE_SERVICE);
		}
		return ruleService;
	}

	/**
	 * @return the {@link ConditionEvaluatorService}
	 */
	protected ConditionEvaluatorService getConditionEvaluatorService() {
		if (conditionEvaluatorService == null) {
			conditionEvaluatorService = getBean(ContextIdNames.TAG_CONDITION_EVALUATOR_SERVICE);
		}
		return conditionEvaluatorService;
	}

	/**
	 * Add the gift certificate to the set which will be redeemed.
	 *
	 * @param giftCertificate the gift certificate to be redeemed.
	 * @throws EpDomainException when the currency mismatch or balance is zero.
	 */
	@Override
	public void applyGiftCertificate(final GiftCertificate giftCertificate) {
		if (giftCertificate == null || !ObjectUtils.equals(giftCertificate.getStore(), getStore())) {
			return;
		}
		if (!getCurrency().getCurrencyCode().equals(giftCertificate.getCurrencyCode())) {
			throw new GiftCertificateCurrencyMismatchException("Currency mismatch, the current shopping cart can't accept this gift certificate.");
		}

		if (getGiftCertificateService().getBalance(giftCertificate).compareTo(BigDecimal.ZERO) <= 0) {
			throw new GiftCertificateZeroBalanceException("This gift certificate has a zero balance.");
		}
		if (!getAppliedGiftCertificates().contains(giftCertificate)) {
			getAppliedGiftCertificates().add(giftCertificate);
		}

		// Calculate the gift certificate values.
		calculateAppliedGcTotal();

	}

	private void calculateAppliedGcTotal() {
		appliedGiftCertificateTotal = BigDecimal.ZERO;
		for (GiftCertificate appliedGc : getAppliedGiftCertificates()) {
			appliedGiftCertificateTotal = appliedGiftCertificateTotal.add(getGiftCertificateService().getBalance(appliedGc));
		}
	}

	/**
	 * Return the <code>CustomerSession</code>. instance. Customer sessions track information about sessions where the customer may not be logged
	 * in.
	 *
	 * @return the <code>CustomerSession</code> instance
	 * @deprecated
	 */
	@Override
	@Deprecated
	public CustomerSession getCustomerSession() {
		return customerSession;
	}

	/**
	 * Set the <code>CustomerSession</code>. instance. Customer sessions track information about sessions where the customer may not be logged in.
	 *
	 * @param customerSession the <code>CustomerSession</code> instance
	 * @deprecated
	 */
	@Override
	@Deprecated
	public void setCustomerSession(final CustomerSession customerSession) {
		this.customerSession = customerSession;
		if (customerSession != null) {
			setShopper(customerSession.getShopper());
		}
	}

	/**
	 * Get the locale of the customer corresponding to the shopping cart.
	 *
	 * @return the <code>Locale</code>
	 */
	@Override
	public Locale getLocale() {
		if (getCustomerSession() != null && getCustomerSession().getLocale() != null) {
			return getCustomerSession().getLocale();
		}
		return getStore().getDefaultLocale();
	}

	/**
	 * Set the locale of the customer corresponding to the shopping cart.
	 *
	 * @param locale the <code>Locale</code>
	 */
	@Override
	public void setLocale(final Locale locale) {
		getCustomerSession().setLocale(locale);
	}

	/**
	 * Get the currency of the customer corresponding to the shopping cart.
	 *
	 * @return the <code>Currency</code>
	 */
	@Override
	public Currency getCurrency() {
		if (getCustomerSession() != null) {
			return getCustomerSession().getCurrency();
		}
		return getStore().getDefaultCurrency();
	}

	/**
	 * Set the currency of the customer corresponding to the shopping cart.
	 *
	 * @param currency the <code>Currency</code>
	 */
	@Override
	public void setCurrency(final Currency currency) {
			getCustomerSession().setCurrency(currency);
	}

	/**
	 * Get the cart items in the shopping cart.
	 *
	 * @return the cart items in the shopping cart
	 */
	@Override
	public List<ShoppingItem> getCartItems() {
		return getCartMementoItems(getShoppingCartMemento());
	}

	/**
	 * Get the products in the shopping cart.
	 *
	 * @return the products in the shopping cart
	 */
	@Override
	public List<Product> getCartProducts() {
		List<Product> cartProducts = new ArrayList<Product>();
		for (ShoppingItem cartItem : getCartItems()) {
			cartProducts.add(cartItem.getProductSku().getProduct());
		}
		return cartProducts;
	}

	/**
	 * {@inheritDoc}
	 * @deprecated use addShoppingCartItem instead
	 */
	@Override
	@Deprecated
	public ShoppingItem addCartItem(final ShoppingItem cartItem) {
		return addShoppingCartItem(cartItem);
	}

	@Override
	public ShoppingItem addShoppingCartItem(final ShoppingItem cartItem) {
		if (isCartItem(cartItem)) {
			((CartItem) cartItem).setCartUid(Long.valueOf(getShoppingCartMemento().getUidPk()));
		}
		this.getCartItems().add(cartItem);

		// if we get a cart item manually added by a customer we should remove it from the list of manually removed cart items
		getRemovedCartItemSkus().remove(cartItem.getProductSku().getSkuCode());

		// should clear the shipping and tax estimation.
		estimateMode = false;

		fireRules();
		return cartItem;
	}

	private boolean isCartItem(final ShoppingItem cartItem) {
		return cartItem instanceof CartItem;
	}

	/**
	 * Remove an item from the cart.
	 *
	 * @param itemUid the uidPk of the <code>CartItem</code> to remove
	 */
	@Override
	public void removeCartItem(final long itemUid) {
		for (ShoppingItem cartItem : getCartItems()) {
			if (cartItem.getUidPk() == itemUid) {
				internalRemoveCartItem(cartItem, true);
				break;
			}
		}

		// should clear the shipping and tax estimation.
		estimateMode = false;

		fireRules();
	}

	/**
	 * Get the cart items that have been removed from the shopping cart.
	 *
	 * @return the removed cart items in the shopping cart
	 */
	protected Collection<String> getRemovedCartItemSkus() {
		return removedCartItemSkus;
	}

	private void internalRemoveCartItem(final ShoppingItem currItem, final boolean addToRemovedList) {
		// Remove any cross-referenced dependent items
		for (ShoppingItem item : getCartItems()) {
			item.getChildren().remove(currItem);
		}
		// cleanup warranties and other dependent cart items
		for (ShoppingItem item : currItem.getChildren()) {
			getCartItems().remove(item);
		}
		// TOD0: write test to determine if JPA will delete dependent items, when the parent is deleted
		currItem.getChildren().clear();
		getCartItems().remove(currItem);

		if (addToRemovedList) {
			// saves the removed cart item so that it does not get back if a promotion has a rule to add a free product
			getRemovedCartItemSkus().add(currItem.getProductSku().getSkuCode());
		}
	}

	/**
	 * Return the number of items in the shopping cart.
	 *
	 * @return the number of items
	 */
	@Override
	public int getNumItems() {
		int numItems = 0;

		for (ShoppingItem currCartItem : getCartItems()) {
			numItems += currCartItem.getQuantity();
		}
		return numItems;
	}

	/**
	 * Get the subtotal of all items in the cart.
	 *
	 * @return a <code>Money</code> object representing the subtotal
	 */
	@Override
	public Money getSubtotalMoney() {
		return getTaxCalculationResult().getSubtotal();
	}

	/**
	 * Get the subtotal of all items in the cart.
	 *
	 * @return a <code>BigDecimal</code> object representing the subtotal
	 */
	@Override
	public BigDecimal getSubtotal() {
		return getTaxCalculationResult().getSubtotal().getAmount();
	}

	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc.
	 *
	 * @return a <code>Money</code> object representing the total
	 */
	@Override
	public Money getTotalMoney() {
		return MoneyFactory.createMoney(getTotal(), getCurrency());
	}

	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc.
	 *
	 * @return a <code>BigDecimal</code> object representing the total
	 */
	@Override
	public BigDecimal getTotal() {
		final BigDecimal totalBeforeRedeem = getTotalBeforeRedeem();
		BigDecimal total = BigDecimal.ZERO;
		if (totalBeforeRedeem.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal redeemAmount = getGiftCertificateDiscount();
			total = totalBeforeRedeem.subtract(redeemAmount);
			if (total.compareTo(BigDecimal.ZERO) < 0) {
				return BigDecimal.ZERO;
			}
			return total;
		}
		return BigDecimal.ZERO;
	}

	/**
	 * The total amount before redeem.
	 *
	 * @return total amount before redeem.
	 */
	BigDecimal getTotalBeforeRedeem() {
		BigDecimal subtotal = getTaxCalculationResult().getSubtotal().getAmount();
		BigDecimal shippingCost = getShippingCost().getAmount();
		BigDecimal totalTaxes = getTaxCalculationResult().getTotalTaxes().getAmount();

		if (getTaxCalculationResult().isTaxInclusive()) {
			return subtotal.subtract(getSubtotalDiscount()).add(shippingCost);
		}
		return subtotal.subtract(getSubtotalDiscount()).add(shippingCost).add(totalTaxes);
	}

	/**
	 * Applies a discount to the shopping cart subtotal.
	 *
	 * @param discountAmount the amount to discount the subtotal by as a BigInteger
	 * @param ruleId The rule which caused this subtotal discount.
	 * @param actionId The rule action which caused this subtotal discount.
	 */
	@Override
	public void setSubtotalDiscount(final BigDecimal discountAmount, final long ruleId, final long actionId) {

		if (discountAmount == null) {
			throw new EpServiceException("Cannot set discount to NULL");
		}

		BigDecimal actualDiscountAmount = discountAmount;

		// Don't set the discount to a smaller value than the previously existing discount
		if (getSubtotalDiscount().compareTo(discountAmount) > 0) {
			markDiscountSuperceded(discountAmount, ruleId, actionId);

			return;
		}

		// Prevent clients from setting the discount greater than the subtotal
		if (actualDiscountAmount.compareTo(getSubtotal()) > 0) {
			actualDiscountAmount = getSubtotal();
			LOG.warn("Attempt to set subtotal discount greater than subtotal");

			if (actualDiscountAmount.compareTo(BigDecimal.ZERO) == 0) {
				// Mark as superceded.
				markDiscountSuperceded(discountAmount, ruleId, actionId);
				return;
			}
		}

		if (!isExchangeOrderShoppingCart()) {
			recordDiscount(ruleId, actionId, actualDiscountAmount);
		}

		subtotalDiscount = actualDiscountAmount;
	}

	private void recordDiscount(final long ruleId, final long actionId,
			final BigDecimal actualDiscountAmount) {
		SubtotalDiscountRecordImpl subtotalDiscountRecordImpl = new SubtotalDiscountRecordImpl(actualDiscountAmount);

		recordRuleApplied(ruleId);

		supercedePreviousDiscountRecordOfType(SubtotalDiscountRecordImpl.class);

		Map<Long, DiscountRecord> actionIdMap = getActionIdMap(ruleId);
		actionIdMap.put(actionId, subtotalDiscountRecordImpl);
	}

	private void markDiscountSuperceded(final BigDecimal discountAmount,
			final long ruleId, final long actionId) {
		SubtotalDiscountRecordImpl subtotalDiscountRecordImpl = new SubtotalDiscountRecordImpl(discountAmount);
		subtotalDiscountRecordImpl.setSuperceded(true);
		Map<Long, DiscountRecord> actionIdMap = getActionIdMap(ruleId);
		actionIdMap.put(actionId, subtotalDiscountRecordImpl);
	}

	private void supercedePreviousDiscountRecordOfType(final Class<?> clazz) {
		// Mark any previous subtotal discount record as superceded because we are overriding it.
		// Note that drools appears to do much of the conflict resolution for us.
		// However, this code allows the DiscountRecords to follow what the code itself does.
		for (Map.Entry<Long, Map<Long, DiscountRecord>> mapEntry : ruleDiscountRecordMap.entrySet()) {
			Map<Long, DiscountRecord> submap = mapEntry.getValue();
			for (Map.Entry<Long, DiscountRecord> submapEntry : submap.entrySet()) {
				DiscountRecord discountRecord = submapEntry.getValue();
				if (clazz.isInstance(discountRecord)
				    && discountRecord instanceof AbstractDiscountRecordImpl) {
					((AbstractDiscountRecordImpl) discountRecord).setSuperceded(true);
				}
			}
		}
	}

	/**
	 * Ensures the action submap exists.
	 * @param ruleId The rule to return the action submap for.
	 * @return The action submap.
	 */
	private Map<Long, DiscountRecord> getActionIdMap(
			final long ruleId) {
		Map<Long, DiscountRecord> actionIdMap = ruleDiscountRecordMap.get(ruleId);
		if (actionIdMap == null) {
			actionIdMap = new HashMap<Long, DiscountRecord>();
			ruleDiscountRecordMap.put(ruleId, actionIdMap);
		}
		return actionIdMap;
	}

	private void recordRuleApplied(final long ruleId) {
		appliedRuleIds.add(new Long(ruleId));
	}

	/**
	 * Get the discount to the shopping cart subtotal.
	 *
	 * @return the amount discounted from the subtotal
	 */
	@Override
	public BigDecimal getSubtotalDiscount() {
		return subtotalDiscount;
	}

	/**
	 * Get the amount discounted from the order subtotal.
	 *
	 * @return the order subtotal discount as a <code>Money</code> object
	 */
	@Override
	public Money getSubtotalDiscountMoney() {
		return getMoney(getSubtotalDiscount());
	}

	/**
	 * Get the amount redeemed from gift certificate.
	 *
	 * @return the gift certificate discounted from the total
	 */
	@Override
	public BigDecimal getGiftCertificateDiscount() {
		BigDecimal totalBeforeRedeem = getTotalBeforeRedeem();
		if (appliedGiftCertificateTotal.compareTo(totalBeforeRedeem) > 0) {
			return totalBeforeRedeem;
		}
		return appliedGiftCertificateTotal;
	}

	/**
	 * Get the amount redeemed from gift certificate.
	 *
	 * @return the gift certificate discount as a <code>Money</code> object
	 */
	@Override
	public Money getGiftCertificateDiscountMoney() {
		return getMoney(getGiftCertificateDiscount());
	}

	/**
	 * Returns true if an order subtotal discount has been applied.
	 *
	 * @return true if an order subtotal discount has been applied
	 */
	@Override
	public boolean hasSubtotalDiscount() {
		return BigDecimal.ZERO.compareTo(getSubtotalDiscount()) < 0;
	}

    private void clearPromotionsInternal() {
        // Clear promotion information
        appliedRuleIds.clear();
        ruleDiscountRecordMap.clear();

        // Clear cart subtotal discount
        subtotalDiscount = BigDecimal.ZERO;

        // Clear shipping discounts
        for (ShippingServiceLevel currServiceLevel : getShippingServiceLevelList()) {
            currServiceLevel.clearPromotions();
        }

        // Clear discounts associated with any items that may be in the cart
        for (ShoppingItem currCartItem : getCartItems()) {
            currCartItem.clearDiscount();
        }
    }

	/**
	 * Empties the shopping cart (e.g. after a checkout)
	 */
	@Override
	public void clearItems() {
		resetForTaxCalculation();
		getCartItems().clear();
		promotionCodes.clear();
		getLimitedUsagePromotionRuleCodes().clear();
		clearPromotionsInternal();
		getAppliedGiftCertificates().clear();
		appliedGiftCertificateTotal = BigDecimal.ZERO;
	}

	private void resetForTaxCalculation() {
		taxCalculationResult = getBean(ContextIdNames.TAX_CALCULATION_RESULT);
		taxCalculationResult.setDefaultCurrency(getCurrency());
	}

	/**
	 * Removes shipping and tax estimates from the shopping cart.
	 */
	@Override
	public void clearEstimates() {
		if (!estimateMode) {
			estimateMode = false;
			setShippingAddress(null);
			setBillingAddress(null);
			getShippingServiceLevelList().clear();
			selectedShippingServiceLevel = null;
			updateTaxCalculationResult();
		}
	}

	/**
	 * Set the shipping address.
	 *
	 * @param address the <code>Address</code>
	 */
	@Override
	public void setShippingAddress(final Address address) {
		shippingAddress = address;
	}

	/**
	 * Get the shipping address.
	 *
	 * @return the preferred shipping address
	 */
	@Override
	public Address getShippingAddress() {
		return shippingAddress;
	}

	/**
	 * Set the billing address.
	 *
	 * @param address the <code>Address</code>
	 */
	@Override
	public void setBillingAddress(final Address address) {
		billingAddress = address;
	}

	/**
	 * Get the billing address.
	 *
	 * @return the billing address
	 */
	@Override
	public Address getBillingAddress() {
		return billingAddress;
	}

	/**
	 * Returns the last category.
	 *
	 * @return the last category
	 */
	@Override
	public Category getLastCategory() {
		return lastCategory;
	}

	/**
	 * Sets the last category.
	 *
	 * @param category the category to set.
	 */
	@Override
	public void setLastCategory(final Category category) {
		lastCategory = category;
	}

	/**
	 * Set a reference to the completed order for the items previously checked out.
	 *
	 * @param order the completed order
	 */
	@Override
	public void setCompletedOrder(final Order order) {
		completedOrder = order;
	}

	/**
	 * Get a reference to the completed order for the items previously checked out.
	 *
	 * @return the completed Order, or null if no completed order has been set.
	 */
	@Override
	public Order getCompletedOrder() {
		return completedOrder;
	}

	/**
	 * Returns the <code>CatalogViewResultHistory</code> instance stored. If none is stored, a new one will be created and returned.
	 *
	 * @return the <code>CatalogViewResultHistory</code> instance stored
	 */
	@Override
	public CatalogViewResultHistory getSearchResultHistory() {
		if (searchResultHistory == null) {
			searchResultHistory = getBean(ContextIdNames.CATALOG_VIEW_RESULT_HISTORY);
		}

		// To help figuring out how a customer reaches to a product, we only maintain one kind of catalog view result history,
		// either search or browsing. This policy also happens to release memory earlier.
		browsingResultHistory = null;
		return searchResultHistory;
	}

	/**
	 * Returns the browsing result history stored. If none is stored, a new one will be created and returned.
	 *
	 * @return the browsing result history
	 */
	@Override
	public CatalogViewResultHistory getBrowsingResultHistory() {
		if (browsingResultHistory == null) {
			browsingResultHistory = getBean(ContextIdNames.CATALOG_VIEW_RESULT_HISTORY);
		}

		// To help figuring out how a customer reaches to a product, we only maintain one kind of catalog view result history,
		// either search or browsing. This policy also happens to release memory earlier.
		searchResultHistory = null;
		return browsingResultHistory;
	}

	/**
	 * Returns the catalog view result history stored. A catalog view result history might be a search or a browsing. If none is stored, return
	 * <code>null</code>
	 *
	 * @return the catalog view result history
	 */
	@Override
	public CatalogViewResultHistory getCatalogViewResultHistory() {
		if (browsingResultHistory != null) {
			return browsingResultHistory;
		}

		if (searchResultHistory != null) {
			return searchResultHistory;
		}

		return null;
	}

	/**
	 * Return the shippingCost of the <code>ShoppingCart</code>.
	 *
	 * @return the shippingCost of the <code>ShoppingCart</code>
	 */
	@Override
	public Money getShippingCost() {
		if (shippingCost != null) {
			return getMoney(shippingCost);
		}

		if (!requiresShipping() || getShippingServiceLevelList().isEmpty()) {
			return getMoney(BigDecimal.ZERO);
		}

		// update the shippingCost in cart for the selected service level
		Collection<ShoppingItem> apportionedLeafItems = getApportionedLeafItems();
		if (getSelectedShippingServiceLevel() != null) {
			return getSelectedShippingServiceLevel().calculateShippingCost(apportionedLeafItems, getCurrency());
		}
		throw new EpDomainException("No valid shipping service level was selected");
	}

	/**
	 * Return the list of shippingServiceLevel list available based on the current shopping cart info.
	 *
	 * @return the list of shippingServiceLevel list available based on the current shopping cart info.
	 */
	@Override
	public List<ShippingServiceLevel> getShippingServiceLevelList() {
		return shippingServiceLevelList;
	}

	/**
	 * Set the list of shippingServiceLevel list available based on the current shopping cart info.
	 *
	 * @param shippingServiceLevelList the list of shippingServiceLevel list available based on the current shopping cart info.
	 */
	@Override
	public void setShippingServiceLevelList(final List<ShippingServiceLevel> shippingServiceLevelList) {

		if (shippingServiceLevelList == null || shippingServiceLevelList.isEmpty()) {
			getShippingServiceLevelList().clear();
		} else {
			this.shippingServiceLevelList = shippingServiceLevelList;
			List <ShippingServiceLevel> toRemove = new ArrayList<ShippingServiceLevel>();
			// calculate the shippingCost for each shippingServiceLevel
			Collection<ShoppingItem> apportionedLeafItems = getApportionedLeafItems();
			for (ShippingServiceLevel element : getShippingServiceLevelList()) {
				try {
					element.calculateShippingCost(apportionedLeafItems, getCurrency());
				} catch (SCCMCurrencyMissingException s) {
					toRemove.add(element);
					LOG.warn(s.getMessage());
				}
			}
			if (!toRemove.isEmpty()) {
				this.shippingServiceLevelList.removeAll(toRemove);
			}
			if (!this.shippingServiceLevelList.isEmpty() && !validShippingServiceLevelSelected()) {
				// Set the first service level as the default if no service level has been set
				selectedShippingServiceLevel = shippingServiceLevelList.get(0);
			}
		}

		fireRules();
	}

	/**
	 * Returns true if the shopping cart currently has a shipping service level selected that is in the list of valid shipping service levels.
	 *
	 * @return true if a valid service level is selected
	 */
	private boolean validShippingServiceLevelSelected() {
		if (selectedShippingServiceLevel != null) {
			for (ShippingServiceLevel currServiceLevel : getShippingServiceLevelList()) {
				if (currServiceLevel.getUidPk() == selectedShippingServiceLevel.getUidPk()) {
					selectedShippingServiceLevel = currServiceLevel;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the selectedShippingServiceLevel.
	 *
	 * @return the selected ShippingServiceLevel.
	 */

	@Override
	public ShippingServiceLevel getSelectedShippingServiceLevel() {
		return selectedShippingServiceLevel;
	}

	/**
	 * Set the selectedShippingServiceLevelUid and update the shippingCost correspondingly.
	 *
	 * @param selectedShippingServiceLevelUid - the selected ShippingServiceLevel uid.
	 * @throws EpDomainException - if something goes wrong.
	 */
	@Override
	public void setSelectedShippingServiceLevelUid(final long selectedShippingServiceLevelUid) throws EpDomainException {
		if (getShippingServiceLevelList().isEmpty()) {
			throw new EpDomainException("Must set available shippingServiceLevelList first");
		}

		// Check that the specified shipping service level is valid
		// boolean shippingServiceLevelFound = false;
		selectedShippingServiceLevel = null;
		for (ShippingServiceLevel shippingServiceLevel : getShippingServiceLevelList()) {
			if (shippingServiceLevel.getUidPk() == selectedShippingServiceLevelUid) {
				selectedShippingServiceLevel = shippingServiceLevel;
				break;
			}
		}

		if (selectedShippingServiceLevel == null) {
			throw new EpDomainException("No valid shipping service level was selected");
		}
		getShippingCost();
	}

	/**
	 * Resets the selected <code>ShippingServiceLevel</code> to null.
	 */
	@Override
	public void clearSelectedShippingServiceLevel() {
		selectedShippingServiceLevel = null;
	}

	/**
	 * Get the totalWeight of items in <code>ShoppingCart</code>.
	 *
	 * @return totalWeight
	 */

	@Override
	public BigDecimal getTotalWeight() {
		BigDecimal totWeight = BigDecimal.ZERO;

		for (ShoppingItem currItem : this.getCartItems()) {
			if (currItem.getProductSku().isShippable()) {
				BigDecimal currItemWeight = BigDecimal.ZERO;
				currItemWeight = currItem.getProductSku().getWeight();
				if (currItemWeight != null && currItemWeight.compareTo(BigDecimal.ZERO) > 0) {
					totWeight = totWeight.add(currItemWeight.multiply(BigDecimal.valueOf(currItem.getQuantity())));
				}
			}
		}

		return totWeight;
	}

	/**
	 * Returns true if the cart contains items that must be shipped to the customer.
	 *
	 * @return true if the cart contains items that must be shipped to the customer.
	 */
	@Override
	public boolean requiresShipping() {
		boolean shoppingCartShippable = false;
		for (ShoppingItem currItem : this.getCartItems()) {
			if (currItem.isShippable()) {
				shoppingCartShippable = true;
				break;
			}
		}
		return shoppingCartShippable;
	}

	/**
	 * Get a cart item by the sku code of its SKU.
	 *
	 * @param skuCode the sku code of the SKU in the cart item to be retrieved.
	 * @return the corresponding item or null if not found
	 */
	@Override
	public ShoppingItem getCartItem(final String skuCode) {
		for (ShoppingItem cartItem : getCartItems()) {
			if (cartItem.getProductSku().getSkuCode().equals(skuCode)) {
				return cartItem;
			}
		}
		return null;
	}

	/**
	 * Get the all cart items by the code of its SKU.
	 *
	 * @param skuCode the code of the SKU in the cart item to be retrieved.
	 * @return the corresponding list of <code>ShoppingItem</code> found
	 */
	@Override
	public List<ShoppingItem> getCartItems(final String skuCode) {
		List<ShoppingItem> existingItems = new ArrayList<ShoppingItem>();
		for (ShoppingItem cartItem : getCartItems()) {
			if (cartItem.getProductSku().getSkuCode().equals(skuCode)) {
				existingItems.add(cartItem);
			}
		}
		return existingItems;
	}


	/**
	 * Return true if the "inclusive" tax calculation method is in use; otherwise false. This is based on the shippingAddress. If there is no
	 * taxJurisdiction set, return false by default.
	 *
	 * @return true if the "inclusive" tax calculation method is in use; otherwise false.
	 */
	@Override
	public boolean isInclusiveTaxCalculationInUse() {
		return getTaxCalculationResult().isTaxInclusive();
	}

	/**
	 * Return the <code>TaxCategory</code> -> tax value (<code>Money</code>) map for this <code>ShoppingCart</code>.
	 *
	 * @return the <code>TaxCategory</code> -> tax value (<code>Money</code>) map. Never <code>null</code>.
	 */

	@Override
	public Map<TaxCategory, Money> getTaxMap() {
		Map<TaxCategory, Money> taxMap = getTaxCalculationResult().getTaxMap();

		if (null == taxMap) {
			taxMap = Collections.emptyMap();
		}
		return taxMap;
	}

	/**
	 * Return the localized tax category name -> tax value (<code>Money</code>) map for this <code>ShoppingCart</code>.
	 *
	 * @return the localized tax category name -> tax value (<code>Money</code>) map.
	 */

	@Override
	public Map<String, Money> getLocalizedTaxMap() {
		final Map<TaxCategory, Money> taxMap = getTaxMap();

		final Map<String, Money> sortedMap = new TreeMap<String, Money>();
		for (TaxCategory taxCategory : taxMap.keySet()) {
			sortedMap.put(taxCategory.getDisplayName(getLocale()), taxMap.get(taxCategory));
		}
		return sortedMap;
	}

	/**
	 * Return the before-tax subtotal.
	 *
	 * @return the before-tax subtotal.
	 */
	@Override
	public Money getBeforeTaxSubTotal() {
		return getTaxCalculationResult().getBeforeTaxSubTotal();
	}

	/**
	 * Return the before-tax shippingCost.
	 *
	 * @return the before-tax shippingCost.
	 */
	@Override
	public Money getBeforeTaxShippingCost() {
		return getTaxCalculationResult().getBeforeTaxShippingCost();
	}

	/**
	 * Return the before-tax total.
	 *
	 * @return the before-tax total.
	 */
	@Override
	public Money getBeforeTaxTotal() {
		Money total = getMoney(getTotalBeforeRedeem());

		return total.subtract(getTaxCalculationResult().getTotalTaxes());
	}

	/**
	 * Calculate the taxes and before-tax prices for the giving shoppingCart, including cart item and shipping cost. The calculation results will be
	 * set in the given shopping cart. In case of exclusive tax calculation, the order discount is deducted before-tax; however, in case of inclusive
	 * tax calculation, the order discount is deducted after-tax. To meet the needs of real life requirement and simplifiy the calcualtion, it is
	 * assumed that the tax rate are same for different tax codes (BOOKS, CLOTHING and etc) in case of exclusive tax calculation. Will not charge tax
	 * on the gift certificate.
	 */
	@Override
	public void calculateShoppingCartTaxAndBeforeTaxPrices() {
		fireRules();
	}

	/**
	 * Updates the tax calculation result by triggering a call to the tax calculation service with the data of the shopping cart.
	 */
	protected void updateTaxCalculationResult() {
		TaxCalculationResult taxCalculationResult = getBean(ContextIdNames.TAX_CALCULATION_RESULT);
		taxCalculationResult.setDefaultCurrency(getCurrency());

		final List<OrderSku> physicalCartItems = new ArrayList<OrderSku>();
		final List<OrderSku> electronicCartItems = new ArrayList<OrderSku>();

		// splits cart items depending on their shippable state into electronic and physical
		Collection<ShoppingItem> apportionedLeafItems = getApportionedLeafItems();
		splitCartItems(apportionedLeafItems, physicalCartItems, electronicCartItems);

		boolean isSplitShipment = !physicalCartItems.isEmpty() && !electronicCartItems.isEmpty();

		Map<String, BigDecimal> discountByShoppingItem = null;
		if (isSplitShipment) {
			DiscountApportioningCalculator discountCalculator = new DiscountApportioningCalculator();
			discountByShoppingItem = discountCalculator.apportionDiscountToShoppingItems(getSubtotalDiscountMoney(), apportionedLeafItems);
		}

		//Calculate for physical items
		taxCalculationResult = calculateTaxesForItems(taxCalculationResult, isSplitShipment,
				discountByShoppingItem, physicalCartItems, getShippingAddress(), getShippingCost());

		//Calculate for electronic items
		taxCalculationResult = calculateTaxesForItems(taxCalculationResult, isSplitShipment,
				discountByShoppingItem, electronicCartItems, getElectronicTaxAddress(), getMoney(BigDecimal.ZERO));

		setTaxCalculationResult(taxCalculationResult);
	}

	/**
	 * For electronic shipments we use the billing address, but
	 * will fall back to the shipping address if we don't have a billing address.
	 */
	private Address getElectronicTaxAddress() {
		Address address = getBillingAddress();
		if (address == null) {
			address = getShippingAddress();
		}
		return address;
	}

	/**
	 * Calculate the discount and taxes for the specified set of items, apply the results to the
	 * items and return the updated tax calculation result.
	 *
	 * @param taxCalculationResult the current tax calculation result which will be updated
	 * @param splitShipmentMode whether this is a split shipment
	 * @param discountByShoppingItem map of discounts per shopping item
	 * @param shoppingItems the shopping items
	 * @param taxAddress address of customer for tax calculation purposes
	 * @param shippingCost the shipping cost
	 * @return new TaxCalculationResult which included results for supplied items, and existing results from taxCalculationResult parameter
	 */
	private TaxCalculationResult calculateTaxesForItems(final TaxCalculationResult taxCalculationResult, final boolean splitShipmentMode,
			final Map<String, BigDecimal> discountByShoppingItem, final List<OrderSku> shoppingItems, final Address taxAddress,
			final Money shippingCost) {
		if (shoppingItems.isEmpty()) {
			return taxCalculationResult;
		}
		final Money discountForShipment = getDiscountForShipment(discountByShoppingItem,
				shoppingItems, splitShipmentMode, getSubtotalDiscountMoney());

		final TaxCalculationResult newTaxCalculationResult = getTaxCalculationService().calculateTaxesAndAddToResult(
				taxCalculationResult, getStore().getCode(), taxAddress, getCurrency(),
				shippingCost, shoppingItems, discountForShipment);
		newTaxCalculationResult.applyTaxes(shoppingItems);
		return newTaxCalculationResult;
	}

	private Money getDiscountForShipment(final Map<String, BigDecimal> discountByShoppingItem, final List<OrderSku> shoppingItems,
			final boolean splitShipmentMode, final Money cartSubtotalDiscount) {
		if (!splitShipmentMode) {
			return cartSubtotalDiscount;
		}
		BigDecimal discount = BigDecimal.ZERO;
		for (OrderSku sku : shoppingItems) {
			if (discountByShoppingItem.containsKey(sku.getGuid())) {
				discount = discount.add(discountByShoppingItem.get(sku.getGuid()));
			}
		}
		return MoneyFactory.createMoney(discount, cartSubtotalDiscount.getCurrency());
	}

	private void splitCartItems(final Collection< ? extends ShoppingItem> leafItems,
								final List<OrderSku> physicalSkus,
								final List<OrderSku> electronicSkus) {
		for (final ShoppingItem shoppingItem : leafItems) {
			OrderSku orderSku = (OrderSku) shoppingItem;
			if (orderSku.isBundle()) {
				throw new IllegalArgumentException("Split cart items expects only leaf items.");
			} else if (shoppingItem.getProductSku().isShippable()) {
				physicalSkus.add(orderSku);
			} else {
				electronicSkus.add(orderSku);
			}
		}
	}

	private void flattenOrderSkuTree(final Collection< ? extends ShoppingItem> rootItems, final Collection<ShoppingItem> leafCollection) {
		for (ShoppingItem orderSku : rootItems) {
			if (orderSku.isBundle()) {
				getLeafItems(orderSku, leafCollection);
			} else {
				leafCollection.add(orderSku);
			}
		}
	}

	/**
	 * @param currCartItem the cart item
	 * @return product code for the cart item
	 */
	protected String getProductCodeFromCartItem(final ShoppingItem currCartItem) {
		return currCartItem.getProductSku().getProduct().getCode();
	}

	/**
	 * Checks that ProductAssociation's target product is not hidden, and that
	 * the current time is within the association's valid date range.
	 * @param association the association to check
	 * @return true if it meets the criteria, false if not
	 */
	boolean isValidProductAssociation(final ProductAssociation association) {
		return !association.getTargetProduct().isHidden() //target product is not hidden
				&& (association.getStartDate().before(new Date())
				&& (association.getEndDate() == null || association.getEndDate().after(new Date())));
	}

	/**
	 * Sorts the given product associations in descending order by how well the association's target product is selling, and returns the top X from
	 * the list where X is the max number of associations to return.
	 *
	 * @param allProductAssociations the set of product associations from which to compute the top selling target products
	 * @param maxAssociations the maximum number of associations to return
	 * @return the list of associations requested
	 */
	protected List<ProductAssociation> computeTopProductAssociations(final Set<ProductAssociation> allProductAssociations,
			final int maxAssociations) {
		// Return only up to maxAssociations associations
		List<ProductAssociation> topProductAssociations = new ArrayList<ProductAssociation>();
		for (int i = 0; i < maxAssociations; i++) {
			ProductAssociation bestSeller = getAssociationWithBestSellingTargetProduct(allProductAssociations);
			if (bestSeller != null) {
				allProductAssociations.remove(bestSeller);
				topProductAssociations.add(bestSeller);
			}
		}
		return topProductAssociations;
	}

	/**
	 * Computes whether the given ProductAssociation has a targetProduct that is already in the shopping cart.
	 *
	 * @param association the ProductAssociation to check
	 * @return true if the association's target product is in the cart, false if not.
	 */
	protected boolean associatedProductAlreadyInCart(final ProductAssociation association) {
		return getCartItem(association.getTargetProduct().getDefaultSku().getSkuCode()) != null;
	}

	/**
	 * Gets the product associations of the given type for a given cartItem's product in the Catalog that this shopping cart's Store is using. This
	 * implementation delegates to the ProductAssociationService.
	 *
	 * @param sourceProductCode the source product's code
	 * @param associationType the type of ProductAssociation to find
	 * @return the requested product associations
	 */
	protected Set<ProductAssociation> getProductAssociations(final String sourceProductCode, final int associationType) {
		ProductAssociationService productAssociationService = getBean(ContextIdNames.PRODUCT_ASSOCIATION_SERVICE);
		return productAssociationService.getAssociationsByType(sourceProductCode, associationType, getStore().getCatalog().getCode(), true);
	}

	/**
	 * Return the <code>ProductAssociation</code> with the target product that has the highest sales volume. This is a helper method for
	 * <code>getProductAssociationsByType()</code>.
	 *
	 * @param productAssociations the set of <code>ProductAssociation</code>s to search. Size must be > 0.
	 * @return the <code>ProductAssociation</code> with the target product that has the highest sales volume
	 */
	private ProductAssociation getAssociationWithBestSellingTargetProduct(final Set<ProductAssociation> productAssociations) {
		ProductAssociation bestSeller = null;
		for (ProductAssociation currAssociation : productAssociations) {
			if (bestSeller == null || currAssociation.getTargetProduct().getSalesCount() > bestSeller.getTargetProduct().getSalesCount()) {
				bestSeller = currAssociation;
			}
		}
		return bestSeller;
	}

	/**
	 * Returns the cart item with the given id.
	 *
	 * @param cartItemId the cart item id
	 * @return the cart item with the given id
	 */
	@Override
	public ShoppingItem getCartItemById(final long cartItemId) {
		for (ShoppingItem currCartItem : this.getCartItems()) {
			if (currCartItem.getUidPk() == cartItemId) {
				return currCartItem;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @deprecated call {{@link #getShoppingItemByGuid(String)} instead.
	 */
	@Override
	@Deprecated
	public ShoppingItem getCartItemByGuid(final String cartItemGuid) {
		return getShoppingItemByGuid(cartItemGuid);
	}

	@Override
	public ShoppingItem getShoppingItemByGuid(final String itemGuid) {
		for (ShoppingItem item : this.getCartItems()) {
			if (item.getGuid().equals(itemGuid)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Indicates that the given rule was applied by the promotion rule engine.
	 *
	 * @param ruleId the uidPk of the <code>Rule</code>
	 * @param actionId the id of the action
	 * @param discountedItem The item that was discounted
	 * @param discountAmount The amount of the discount.
	 * @param quantityAppliedTo The quantity of the item that the discount was applied to.
	 */
	@Override
	public void ruleApplied(final long ruleId, final long actionId,
			final ShoppingItem discountedItem, final BigDecimal discountAmount,
			final int quantityAppliedTo) {
		recordRuleApplied(ruleId);
		Map<Long, DiscountRecord> actionIdMap = getActionIdMap(ruleId);
		DiscountRecord discountRecord = actionIdMap.get(actionId);
		if (discountRecord == null) {
			discountRecord = new ItemDiscountRecordImpl(discountedItem, discountAmount, quantityAppliedTo);
			actionIdMap.put(actionId, discountRecord);
		} else if (discountRecord instanceof ItemDiscountRecordImpl) {
			ItemDiscountRecordImpl itmeDiscountRecord = (ItemDiscountRecordImpl) discountRecord;
			itmeDiscountRecord.setQuantityAppliedTo(itmeDiscountRecord.getQuantityAppliedTo() + quantityAppliedTo);
		}
	}

	/**
	 * Indicates that the given rule was applied by the promotion rule engine.
	 *
	 * @param ruleId the uidPk of the <code>Rule</code>
	 * @param actionId the id of the action
	 * @param discountAmount The amount of the discount.
	 */
	@Override
	public void shippingRuleApplied(final long ruleId, final long actionId,
			final BigDecimal discountAmount) {
		recordRuleApplied(ruleId);

		supercedePreviousDiscountRecordOfType(ShippingDiscountRecordImpl.class);

		Map<Long, DiscountRecord> actionIdMap = getActionIdMap(ruleId);
		ShippingDiscountRecordImpl discountRecord = new ShippingDiscountRecordImpl(discountAmount);
		actionIdMap.put(actionId, discountRecord);
	}

	/**
	 * Returns the discount record for the items that were discounted based on {@code rule}.
	 * @param rule The rule to use.
	 * @param action The action to use.
	 * @return The matching discount record or null.
	 */
	@Override
	public DiscountRecord getDiscountRecordForRuleAndAction(final Rule rule, final RuleAction action) {
		Map<Long, DiscountRecord> actionIdMap = ruleDiscountRecordMap.get(rule.getUidPk());
		if (actionIdMap != null) {
			return actionIdMap.get(action.getUidPk());
		}
		return null;
	}

	/**
	 * Get the set of rules that have been applied to the cart.
	 *
	 * @return a set of <code>Long</code> Rule UidPks
	 */
	@Override
	public Set<Long> getAppliedRules() {
		return appliedRuleIds;
	}

	/**
	 * Gets the list of promotion codes added to the cart.
	 *
	 * @return the promotion codes
	 */
	@Override
	public Set<String> getPromotionCodes() {
		Set<String> allCodes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		for (Set<String> codes : promotionCodes.values()) {
			allCodes.addAll(codes);
		}
		return Collections.unmodifiableSet(allCodes);
	}

	/**
	 * Add a new promotion code to the list of promotion codes added to the shopping cart.
	 *
	 * @param promotionCode the promotion code to add to the list
	 * @return true if promotion code is valid
	 */
	@Override
	public boolean applyPromotionCode(final String promotionCode) {
		if (promotionCode != null && !promotionCode.equals("") && !getPromotionCodes().contains(promotionCode)) {
			Rule rule = getRuleFromPromoCode(promotionCode);

			if (rule == null) {
				return false;
			}

			// So we have a valid promotion code so now we want to
			// ensure we have a coupon usage record (with a use set to 0)
			// if required.
			for (Coupon coupon : couponUsagesToCreate) {
				CouponUsage couponUsage = getBean(ContextIdNames.COUPON_USAGE);
				couponUsage.setCustomerEmailAddress(getCustomerEmailAddress());
				couponUsage.setCoupon(coupon);
				couponUsage.setUseCount(0);
				getCouponUsageService().add(couponUsage);
			}
			couponUsagesToCreate.clear();

			Set<String> codes = promotionCodes.get(rule.getUidPk());
			if (codes == null) {
				codes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				codes.add(promotionCode);
				promotionCodes.put(rule.getUidPk(), codes);
			} else {
				codes.add(promotionCode);
			}

			// should clear the shipping and tax estimation.
			estimateMode = false;

			fireRules();
		}

		return true;
	}

	/**
	 * Remove a promotion code from the list of promotion codes added to the shopping cart.
	 *
	 * @param promotionCode the promotion code to remove from the list
	 */
	@Override
	public void removePromotionCode(final String promotionCode) {
		if (!StringUtils.isEmpty(promotionCode)) {

			for (Coupon coupon : couponUsagesToCreate) {
				if (promotionCode.equals(coupon.getCouponCode())) {
					couponUsagesToCreate.remove(coupon);
				}
			}

			for (Long id : promotionCodes.keySet()) {
				Set<String> codes = promotionCodes.get(id);
				if (codes.remove(promotionCode)) {
					if (codes.isEmpty()) {
						promotionCodes.remove(id);
					}
					break;
				}
			}

			// should clear the shipping and tax estimation.
			estimateMode = false;

			fireRules();
		}
	}

	/**
	 * Validates if the code matches a promotion code in the system.
	 *
	 * @param code the code to validate
	 * @return true if the code is a valid promotion code
	 */
	@Override
	public boolean isValidPromoCode(final String code) {
		return getRuleFromPromoCode(code) != null;
	}

	private Rule getRuleFromPromoCode(final String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}

		Rule rule = getRuleService().findByPromoCode(code);
		if (rule == null) {
			LOG.info(String.format("Promotion code '%s' cannot be found", code));
			return null;
		}
		if (!rule.isEnabled()) {
			LOG.info(String.format("Promotion code '%s' is not enabled", code));
			return null;
		}
		if (rule.getStore() != null && !rule.getStore().getCode().equals(getStore().getCode())) {
			LOG.info(String.format("Promotion code '%s' does not belong to this store (%s)", code, getStore().getCode()));
			return null;
		}

		/*
		 * A rule could have no selling context if it doesn't have any conditions or if the promotion was created before
		 * tags were introduced. We fallback to the current time to be consistent.
		 */
		if (rule.getSellingContext() == null) {
			if (!rule.isWithinDateRange()) {
				LOG.info(String.format("Promotion code '%s' is out of active time scope", code));
				return null;
			}
		} else {
			/*
			 * We probably should be using a customer's tag set here, but we have legacy behaviour which depends
			 * on the current time instead.
			 */
			TagSet tagSet = new TagSet();
			tagSet.addTag("SHOPPING_START_TIME", new Tag(new Date().getTime()));
			if (!rule.getSellingContext().isSatisfied(getConditionEvaluatorService(), tagSet, TagDictionary.DICTIONARY_TIME_GUID)) {
				LOG.info(String.format("Promotion code '%s' is out of active time scope", code));
				return null;
			}
		}

		if (!isCouponUsageValid(code, rule)) {
			LOG.info(String.format("Promotion code '%s' usage is not valid", code));
			return null;
		}

		return rule;
	}

	/**
	 * Returns true if the coupon usage is valid.
	 * @param code The code
	 * @param rule The rule
	 * @return The result.
	 */
	boolean isCouponUsageValid(final String code, final Rule rule) {
		String customerEmailAddress = getCustomerEmailAddress();

		for (RuleCondition ruleCondition : rule.getConditions()) {
			if (RuleElementType.LIMITED_USE_COUPON_CODE_CONDITION.equals(ruleCondition.getElementType())) {
				Coupon coupon = getCouponService().findByCouponCode(code);
				CouponConfig couponConfig = coupon.getCouponConfig();
				CouponUsage couponUsage = getCouponUsageService().findByCodeAndType(couponConfig, code, customerEmailAddress);

				boolean returnValue = getCouponUsageService().isValidPromoCode(customerEmailAddress, coupon,
						couponConfig, couponUsage);
				if (returnValue && couponUsage == null && couponConfig.getUsageType().equals(CouponUsageType.LIMIT_PER_ANY_USER)) {
					couponUsagesToCreate.add(coupon);
				}

				return returnValue;
			}
		}
		return true;
	}

	/**
	 *
	 * @return The email address from the customer session.
	 */
	String getCustomerEmailAddress() {
		if (getShopper().getCustomer() == null) {
			return null;
		}
		return getShopper().getCustomer().getEmail();
	}

	/**
	 * Indicates if the promotion or gift certificate code entered by the user is valid.
	 *
	 * @return true if the code is valid
	 */
	@Override
	public boolean isCodeValid() {
		return codeValid;
	}

	/**
	 * Set whether or not the promotion or gift certificate code entered by the user is valid.
	 *
	 * @param codeValid set to true if the code is valid
	 */
	@Override
	public void setCodeValid(final boolean codeValid) {
		this.codeValid = codeValid;
	}

	/**
	 * Get the ipAddress of the user from the shopping cart.
	 *
	 * @return the ipAddress
	 */
	@Override
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Set the users ip Address into the shopping cart.
	 *
	 * @param ipAddress the ipAddress of the user.
	 */
	@Override
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Forces the shopping cart to apply promotion rules. Promotion rules will usually be applied by the cart automatically as required. However, it
	 * is sometimes necessary to force the cart to fire rules when the cart is loaded without a state change.
	 */
	@Override
	public void fireRules() {
		if (!isExchangeOrderShoppingCart()) {
			clearPromotionsInternal();
			ruleDiscountRecordMap.clear();
			getRuleEngine().fireOrderPromotionRules(this);
			updateTaxCalculationResult();
			getRuleEngine().fireOrderPromotionSubtotalRules(this);
			for (Iterator<Map.Entry<String, Long>> entryIterator =
				getLimitedUsagePromotionRuleCodes().entrySet().iterator(); entryIterator.hasNext();) {
				Map.Entry<String, Long> entry = entryIterator.next();
				if (!getAppliedRules().contains(entry.getValue())) {
					entryIterator.remove();
				}
			}
		}
		updateTaxCalculationResult();
	}

	private EpRuleEngine getRuleEngine() {
		return getBean(ContextIdNames.EP_RULE_ENGINE);
	}

	/**
	 * Get the View History of the user from the shopping cart.
	 *
	 * @return the ViewHistory
	 */
	@Override
	public ViewHistory getViewHistory() {
		if (viewHistory == null) {
			viewHistory = getBean("viewHistory");
		}
		return viewHistory;
	}

	/**
	 * Get the indicator of whether in the estimate shipping and taxes mode.
	 *
	 * @return true when estimating shipping and taxes; otherwise, false.
	 */
	@Override
	public boolean isEstimateMode() {
		return estimateMode;
	}

	/**
	 * Set the indicator of whether in the estimate shipping and taxes mode. Disabling estimate mode cleards the billing and shipping addresses
	 * (because they may not be full, valid addresses), but tax and shipping calculations are not cleared. If you wish to clear the estimated
	 * calculations/values, then call clearEstimates().
	 *
	 * @param estimateMode true when estimating shipping and taxes; otherwise, false.
	 */
	@Override
	public void setEstimateMode(final boolean estimateMode) {
		this.estimateMode = estimateMode;
		if (!estimateMode) {
			setShippingAddress(null);
			setBillingAddress(null);
		}
	}

	/**
	 * Get all the items in the shopping cart.
	 *
	 * @return an unmodifiable list of all the items in the shopping cart
	 */
	@Override
	public List<ShoppingItem> getAllItems() {
		return Collections.unmodifiableList(getCartItems());
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	public String getGuid() {
		return getShoppingCartMemento().getGuid();
	}

	/**
	 * Retrieves the tax calculation result.
	 *
	 * @return the current tax values
	 */
	@Override
	public TaxCalculationResult getTaxCalculationResult() {
		if (taxCalculationResult == null) {
			resetForTaxCalculation();
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("TaxCalculationResult: " + taxCalculationResult);
		}
		return taxCalculationResult;
	}

	/**
	 * Sets the tax calculation result.
	 *
	 * @param taxCalculationResult the tax calculation result to set
	 */
	@Override
	public void setTaxCalculationResult(final TaxCalculationResult taxCalculationResult) {
		this.taxCalculationResult = taxCalculationResult;
	}

	/**
	 * Sets the tax calculation service.
	 *
	 * @param taxCalculationService the tax calculation service
	 */
	public void setTaxCalculationService(final TaxCalculationService taxCalculationService) {
		this.taxCalculationService = taxCalculationService;
	}

	/**
	 * Gets the tax calculation service.
	 *
	 * @return the tax calculation service
	 */
	protected TaxCalculationService getTaxCalculationService() {
		// Note that the service field must be transient and included in ShoppingCartImplIntegrationTest.testSerialization().
		if (taxCalculationService == null) {
			taxCalculationService = getBean(ContextIdNames.TAX_CALCULATION_SERVICE);
		}
		return taxCalculationService;
	}

	@Override
	public void setShippingCost(final BigDecimal shippingCost) {
		this.shippingCost = shippingCost;
	}

	@Override
	public boolean isExchangeOrderShoppingCart() {
		return exchangeOrderShoppingCart;
	}

	@Override
	public void setExchangeOrderShoppingCart(final boolean isExchangeOrderShoppingCart) {
		exchangeOrderShoppingCart = isExchangeOrderShoppingCart;
	}

	/**
	 * Gets the {@link Store} this object belongs to.
	 *
	 * @return the {@link Store}
	 */
	@Override
	public Store getStore() {
		return store;
	}

	/**
	 * Sets the {@link Store} this object belongs to.
	 *
	 * @param store the {@link Store} to set
	 */
	@Override
	public void setStore(final Store store) {
		this.store = store;
	}

	/**
	 * Gets the persistent shopping cart object.
	 *
	 * @return the <code>ShoppingCartMemento</code> for this cart
	 */
	@Override
	public ShoppingCartMemento getShoppingCartMemento() {
		if (shoppingCartMemento == null) {
			shoppingCartMemento = getBean(ContextIdNames.SHOPPING_CART_MEMENTO);

			if (shopper != null) {
				shoppingCartMemento.setShopper(shopper);
			}

		}
		return shoppingCartMemento;
	}

	/**
	 * @param shoppingCartMemento the <code>ShoppingCartMemento</code> to set
	 */
	@Override
	public void setShoppingCartMemento(final ShoppingCartMemento shoppingCartMemento) {
		this.shoppingCartMemento = shoppingCartMemento;
	}

	/**
	 * Checks if a specific cart item SKU code was previously removed.
	 *
	 * @param skuCode the SKU code to check
	 * @return true if the cart item was removed
	 */
	@Override
	public boolean isCartItemRemoved(final String skuCode) {
		return getRemovedCartItemSkus().contains(skuCode);
	}

	/**
	 * Gets the list of limited usage promotion codes added to the cart.
	 *
	 * @return the limitedUsagePromotionCodes
	 */
	@Override
	public Map<String, Long> getLimitedUsagePromotionRuleCodes() {
		return limitedUsagePromotionRuleCodes;
	}

	/**
	 * Add a new limited usage promotion code to the list of promotion codes added to the shopping cart.
	 *
	 * @param ruleCode the promotion code to add to the list
	 * @param ruleId the id of the rule that owns the promo code.
	 */
	@Override
	public void applyLimitedUsagePromotionRuleCode(final String ruleCode, final long ruleId) {
		if (!StringUtils.isEmpty(ruleCode)) {
			getLimitedUsagePromotionRuleCodes().put(ruleCode, ruleId);
		}
	}

	/**
	 * Remove a rule code from the list of promotion rule codes added to the shopping cart.
	 *
	 * @param ruleCode the code to remove from the list
	 */
	@Override
	public void removeLimitedUsagePromotionRuleCode(final String ruleCode) {
		if (ruleCode != null && !ruleCode.equals("")) {
			getLimitedUsagePromotionRuleCodes().remove(ruleCode);
		}
	}

	/**
	 * @return the cartItemFactory
	 */
	public ShoppingItemFactory getCartItemFactory() {
		return getBean("cartItemFactory");
	}

	/**
	 * @return the priceLookupService
	 */
	public PriceLookupService getPriceLookupService() {
		return getBean("priceLookupService");
	}

	@Override
	public Collection< ? extends ShoppingItem> getRootShoppingItems() {
		return Collections.unmodifiableList(getCartItems());
	}

	@Override
	public Collection< ? extends ShoppingItem> getLeafShoppingItems() {
		List<ShoppingItem> leaves = new ArrayList<ShoppingItem>();
		for (ShoppingItem item : getCartItems()) {
			if (item.isBundle()) {
				getLeafItems(item, leaves);
			} else {
				leaves.add(item);
			}
		}
		return Collections.unmodifiableList(leaves);
	}

	private void getLeafItems(final ShoppingItem bundleItem, final Collection<ShoppingItem> leafCollection) {
		for (ShoppingItem item : bundleItem.getBundleItems()) {
			if (item.isBundle()) {
				getLeafItems(item, leafCollection);
			} else {
				leafCollection.add(item);
			}
		}
	}

	/**
	 * Get the leaf items which have had prices apportioned to them, if they are bundle constituents.
	 *
	 * @return a collection of leaf shopping items with apportioned prices.
	 */
	@Override
	public Collection<ShoppingItem> getApportionedLeafItems() {
		OrderSkuFactory orderSkuFactory = getBean(ContextIdNames.ORDER_SKU_FACTORY);
		Collection<OrderSku> rootItems = orderSkuFactory.createOrderSkus(getCartItems(), getLocale());
		Collection<ShoppingItem> leafItems = new ArrayList<ShoppingItem>();
		flattenOrderSkuTree(rootItems, leafItems);
		return leafItems;
	}

	private List<ShoppingItem> getCartMementoItems(final ShoppingList memento) {
		List<ShoppingItem> items = memento.getAllItems();
		for (ShoppingItem item : items) {
			if (isCartItem(item)) {
				((CartItem) item).setCartUid(memento.getUidPk());
			}
		}
		return items;
	}

	/**
	 * Create a new money bean using the shopping cart currency.
	 */
	private Money getMoney(final BigDecimal amount) {
		return MoneyFactory.createMoney(amount, getCurrency());
	}

	/**
	 * Get the coupon usage service.
	 *
	 * @return the coupon usage service
	 */
	protected CouponUsageService getCouponUsageService() {
		// Note that the service field must be transient and included in ShoppingCartImplIntegrationTest.testSerialization().
		if (couponUsageService == null) {
			couponUsageService = this.getBean(ContextIdNames.COUPON_USAGE_SERVICE);
		}
		return couponUsageService;
	}

	/**
	 * Get the coupon usage service.
	 *
	 * @return the coupon usage service
	 */
	protected CouponService getCouponService() {
		// Note that the service field must be transient and included in ShoppingCartImplIntegrationTest.testSerialization().
		if (couponService == null) {
			couponService = this.getBean(ContextIdNames.COUPON_SERVICE);
		}
		return couponService;
	}

	@Override
	public boolean hasLUCCForRule(final long ruleId) {
		return promotionCodes.containsKey(ruleId);
	}

	@Override
	public void setShopper(final Shopper shopper) {
		if (shopper == null) {
			throw new EpServiceException("Shopper should not be null.");
		}
		this.shopper = shopper;
		getShoppingCartMemento().setShopper(shopper);
	}

	@Override
	public Shopper getShopper() {
		return shopper;
	}

	@Override
	public boolean isMergedNotification() {
		return mergedNotification;
	}

	@Override
	public boolean hasRecurringPricedShoppingItems() {
        List<ShoppingItem> items = getCartItems();
        for (ShoppingItem item : items) {
            if (getShoppingItemHasRecurringPricePredicate().evaluate(item)) {
                return true;
            }
        }
        return false;
	}

	@Override
	public void setMergedNotification(final boolean merged) {
		mergedNotification = merged;
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
		return itemWithNoTierOneFromWishList;
	}

	@Override
	public void setItemWithNoTierOneFromWishList(
			final boolean itemWithNoTierOneFromWishList) {
		this.itemWithNoTierOneFromWishList = itemWithNoTierOneFromWishList;

	}

	@Override
	@Transient
	public void accept(final ShoppingCartVisitor visitor) {
		visitor.visit(this);
		for (ShoppingItem item : getCartItems()) {
			item.accept(visitor);
		}
	}

	@Override
	public Collection<String> getNotPurchasableCartItemSkus() {
		return notPurchasableCartItemSkus;
	}

	@Override
	public Set<ShipmentType> getShipmentTypes() {
		ShoppingCartShipmentTypeEvaluator evaluator = getBean(ContextIdNames.SHOPPING_CART_SHIPMENT_TYPE_EVALUATOR);

		for (ShoppingItem item : getLeafShoppingItems()) {
			item.accept(evaluator);
		}

		return evaluator.getShipmentTypes();
	}

	@Override
	public Date getLastModifiedDate() {
		return getShoppingCartMemento().getLastModifiedDate();
	}
}

