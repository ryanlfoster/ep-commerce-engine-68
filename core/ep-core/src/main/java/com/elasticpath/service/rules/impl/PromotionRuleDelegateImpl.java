/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.RuleParameterNumItemsQuantifier;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.catalogview.ProductRetrieveStrategy;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.PromotionRuleDelegate;
import com.elasticpath.service.rules.PromotionRuleExceptions;
import com.elasticpath.service.rules.RuleService;

/**
 * This interface provides helper methods that can be invoked from Drools code to make queries on the system. The intent of this
 * interface/implementation is to move as much logic as possible out of the rule code so that the drools code is as simple as possible.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class PromotionRuleDelegateImpl extends AbstractEpServiceImpl implements PromotionRuleDelegate {

	private static final Logger LOG = Logger.getLogger(PromotionRuleDelegateImpl.class);

	private static final String PERCENT_DIVISOR = "100";

	private static final String ANY_BRAND_CODE = "ANY";

	private RuleService ruleService;

	private CouponUsageService couponUsageService;

	private CouponConfigService couponConfigService;

	private ProductRetrieveStrategy productRetrieveStrategy;

	@Override
	public boolean catalogProductInCategory(final Product product, final boolean isIn, final String compoundCategoryGuid, final String exceptionStr) {
		boolean isInCategoryAndNotExcluded = false;

		if (compoundCategoryGuid != null && !product.getProductType().isExcludedFromDiscount()) {
			isInCategoryAndNotExcluded = isProductInCategory(product, compoundCategoryGuid)
							&& !isProductExcludedFromRule(product, getPromotionRuleExceptions(exceptionStr));
		}
		if (!isIn) {
			return !isInCategoryAndNotExcluded;
		}
		return isInCategoryAndNotExcluded;
	}

	/**
	 * Package private purely for test to override.
	 * @param product the product
	 * @param ruleExceptions the rule exception
	 * @return true if product is excluded from rule
	 */
	boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
		return ruleExceptions.isProductExcluded(product);
	}

	/**
	 * Package private purely for test to override.
	 * @param product the product
	 * @param compoundCategoryGuid the compound category guid
	 * @return true if product is in category
	 */
	boolean isProductInCategory(final Product product, final String compoundCategoryGuid) {
		Product productWithCategories = loadProductWithFullCategoryTree(product);
		return productWithCategories.isInCategory(compoundCategoryGuid);
	}

	private Product loadProductWithFullCategoryTree(final Product product) {
		return productRetrieveStrategy.retrieveProduct(product.getUidPk(), getParentCategoryLoadTuner());
	}

	/**
	 * Gets the load tuner to be used when loading a product's Category
	 * for indexing purposes.
	 *
	 * @return the load tuner to use when loading categories for the purpose of building the search index
	 */
	FetchGroupLoadTuner getParentCategoryLoadTuner() {
		FetchGroupLoadTuner parentCategoryLoadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		parentCategoryLoadTuner.addFetchGroup(
				FetchGroupConstants.CATEGORY_INDEX, //Force loading of linked category's master category
				FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH, //Force loading of category parents
				FetchGroupConstants.LINK_PRODUCT_CATEGORY, //Force loading of ancestor Categories
				FetchGroupConstants.CATEGORY_HASH_MINIMAL); //Force loading of category code
		return parentCategoryLoadTuner;
	}

	/**
	 * Get a <code>PromotionRuleException</code> object populated with the given exception string.
	 *
	 * @param exceptionStr the exception string passed in by the rule
	 * @return the populated <code>PromotionRuleExceptions</code> object
	 */
	PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
		PromotionRuleExceptions promotionRuleExceptions = getBean(ContextIdNames.PROMOTION_RULE_EXCEPTIONS);
		promotionRuleExceptions.populateFromExceptionStr(exceptionStr);
		return promotionRuleExceptions;
	}

	@Override
	public boolean catalogProductIs(final Product product, final boolean isProduct, final String productCode, final String exceptionStr) {

		boolean productIdMatches = false;

		if (productCode != null && !product.getProductType().isExcludedFromDiscount()) {
			productIdMatches = (product.getCode().equals(productCode));
		}
		if (!isProduct) {
			return !productIdMatches;
		}
		return productIdMatches;
	}

	/**
	 * Returns true if the given product is/is not of the specified brand.
	 *
	 * @param product the product whose condition is to be checked
	 * @param isBrand set to true to check that the brand is the one with the specified Id, or false to check that it is not the brand with the
	 *            specified id.
	 * @param brandCode the code of the brand to check for
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if the product is of the specified brand
	 */
	public boolean catalogBrandIs(final Product product, final boolean isBrand, final String brandCode, final String exceptionStr) {

		boolean brandMatches = false;

		if (product.getBrand() != null && !product.getProductType().isExcludedFromDiscount()) {
			brandMatches = (brandCode.equalsIgnoreCase(ANY_BRAND_CODE) || product.getBrand().getCode().equals(brandCode));
		}
		if (!isBrand) {
			return !brandMatches;
		}
		return brandMatches;
	}

	/**
	 * Checks if the currency of a shopping cart matches the specified currency code.
	 *
	 * @param shoppingCart The shopping cart to check
	 * @param currencyCode The currency code, e.g. CAD
	 * @return true if the cart currency code matches the supplied code
	 */
	public boolean cartCurrencyMatches(final ShoppingCart shoppingCart, final String currencyCode) {
		return shoppingCart.getCurrency().getCurrencyCode().equals(currencyCode);
	}

	/**
	 * Checks if the given <code>ShoppingCart</code> contains the specified <code>quantity</code> of the Product indicated by the given
	 * <code>skuCode</code>.
	 *
	 * @param shoppingCart the shopping cart to check
	 * @param skuCode the SKU code that must be in the cart
	 * @param numItemsQuantifier the <code>String</code> number-of-items quantifier; indicates whether the quantity specified is a minimum quantity
	 *            or an exact quantity
	 * @param quantity the quantity of the SKU
	 * @return true if there are <code>quantity</code> items with <code>skuCode</code> in the cart
	 */
	public boolean cartContainsSku(final ShoppingCart shoppingCart, final String skuCode, final String numItemsQuantifier, final int quantity) {
		int qualifyingQuantity = 0;

		for (ShoppingItem currItem : shoppingCart.getCartItems()) {
			// The current item qualifies if the item matches the rule's SKU code
			if (currItem.getProductSku().getSkuCode().equals(skuCode)) {
				qualifyingQuantity = currItem.getQuantity() + qualifyingQuantity;
			}
		}

		if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.AT_LEAST.toString()) && (qualifyingQuantity >= quantity)) {
			return true;
		} else if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.EXACTLY.toString()) && (qualifyingQuantity == quantity)) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if the given <code>ShoppingCart</code> contains the specified <code>quantity</code> of products with ANY SKU.
	 *
	 * @param shoppingCart the shopping cart to check
	 * @param numItemsQuantifier the <code>String</code> number-of-items quantifier; indicates whether the quantity specified is a minimum quantity
	 *            or an exact quantity
	 * @param quantity the quantity of the SKU
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if there are <code>quantity</code> items with <code>skuCode</code> in the cart
	 */
	public boolean cartContainsAnySku(final ShoppingCart shoppingCart, final String numItemsQuantifier, final int quantity,
			final String exceptionStr) {
		int qualifyingQuantity = 0;

		for (ShoppingItem currCartItem : shoppingCart.getCartItems()) {
			// The current item qualifies if the item does not fall under any of the rule's SKU exceptions
			if (cartItemContributesToPromotionCondition(currCartItem, shoppingCart.getStore().getCatalog(),
					getPromotionRuleExceptions(exceptionStr))) {
				qualifyingQuantity = currCartItem.getQuantity() + qualifyingQuantity;
			}
		}

		if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.AT_LEAST.toString()) && (qualifyingQuantity >= quantity)) {
			return true;
		} else if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.EXACTLY.toString()) && (qualifyingQuantity == quantity)) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if the given <code>ShoppingCart</code> contains the specified <code>quantity</code> of the Product indicated by the given
	 * <code>productUid</code>.
	 *
	 * @param shoppingCart the shopping cart to check
	 * @param productCode the code of the product that must be in the cart
	 * @param numItemsQuantifier the <code>String</code> number-of-items quantifier; indicates whether the quantity specified is a minimum quantity
	 *            or an exact quantity
	 * @param quantity the quantity of the product
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if there are <code>quantity</code> items with <code>productUid</code> in the cart
	 */
	public boolean cartContainsProduct(final ShoppingCart shoppingCart, final String productCode, final String numItemsQuantifier, final int quantity,
			final String exceptionStr) {
		final PromotionRuleExceptions promotionRuleExceptions = getPromotionRuleExceptions(exceptionStr);
		int qualifyingQuantity = 0;

		for (ShoppingItem currItem : shoppingCart.getCartItems()) {
			// The current item qualifies if the item matches the rule's product and if the item does not
			// fall under any of the rule's SKU exceptions
			if ((currItem.getProductSku().getProduct().getCode().equals(productCode))
					&& !promotionRuleExceptions.isSkuExcluded(currItem.getProductSku())) {
				qualifyingQuantity = currItem.getQuantity() + qualifyingQuantity;
			}
		}

		if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.AT_LEAST.toString()) && (qualifyingQuantity >= quantity)) {
			return true;
		} else if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.EXACTLY.toString()) && (qualifyingQuantity == quantity)) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if the given <code>ShoppingCart</code> contains the specified <code>numItems</code> of products in the Category indicated by the
	 * given <code>categoryUid</code>.
	 *
	 * @param shoppingCart the shopping cart to check
	 * @param compoundCategoryGuid the compound category guid based on catalog code and category code
	 * @param numItemsQuantifier the <code>String</code> number-of-items quantifier; indicates whether the quantity specified is a minimum quantity
	 *            or an exact quantity
	 * @param numItems the quantity of products required to be in the category
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if there are <code>numItems</code> items in the category indicated by <code>categoryUid</code> in the shopping cart
	 */
	@SuppressWarnings({ "PMD.CyclomaticComplexity" })
	public boolean cartContainsItemsOfCategory(final ShoppingCart shoppingCart, final String compoundCategoryGuid, final String numItemsQuantifier,
			final int numItems, final String exceptionStr) {

		int qualifyingQuantity = 0;

		for (ShoppingItem currCartItem : shoppingCart.getCartItems()) {
			// The current item qualifies if the item's category matches the rule's category and if the item does not fall under any of the rule's
			// exceptions
			if (catalogProductInCategory(currCartItem.getProductSku().getProduct(), true, compoundCategoryGuid, exceptionStr)
					&& cartItemContributesToPromotionCondition(currCartItem, shoppingCart.getStore().getCatalog(),
							getPromotionRuleExceptions(exceptionStr))) {
				qualifyingQuantity = currCartItem.getQuantity() + qualifyingQuantity;
			}
		}

		if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.AT_LEAST.toString()) && (qualifyingQuantity >= numItems)) {
			return true;
		} else if (numItemsQuantifier.equalsIgnoreCase(RuleParameterNumItemsQuantifier.EXACTLY.toString()) && (qualifyingQuantity == numItems)) {
			return true;
		}

		return false;
	}

	/**
	 * Applies a discount to the specified price.
	 *
	 * @param discountAmount the amount of the discount
	 * @param price the <code>Price</code> to be discounted
	 */
	protected void discountPriceByAmount(final BigDecimal discountAmount, final Price price) {
		for (PriceTier priceTier : price.getPriceTiers().values()) {
			Money prePromotionPriceMoney = MoneyFactory.createMoney(priceTier.getPrePromotionPrice(), price.getCurrency());

			Money discountMoney = MoneyFactory.createMoney(discountAmount, price.getCurrency());

			Money discountedPriceMoney = prePromotionPriceMoney.subtract(discountMoney);
			priceTier.setComputedPrice(discountedPriceMoney.getAmount());
		}
	}

	/**
	 * @param discountPercent BigDecimal value for percent to discount
	 * @param price to apply discount
	 */
	protected void discountPriceByPercent(final BigDecimal discountPercent, final Price price) {
		for (PriceTier priceTier : price.getPriceTiers().values()) {
			BigDecimal prePromotionPrice = priceTier.getPrePromotionPrice();
			BigDecimal discountedPrice = prePromotionPrice.subtract(prePromotionPrice.multiply(discountPercent));

			Money discountedPriceMoney = MoneyFactory.createMoney(discountedPrice, price.getCurrency());

			priceTier.setComputedPrice(discountedPriceMoney.getAmount());
		}
	}

	/**
	 * @param discountPercent {@link BigDecimal}
	 * @return amount with scale set
	 */
	protected BigDecimal setDiscountPercentScale(final BigDecimal discountPercent) {
		return discountPercent.setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(PERCENT_DIVISOR), BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Reduces the price of a catalog item by the specified percent.
	 *
	 * @param ruleTracker the customer's shopping cart
	 * @param ruleId the id of the rule executing this action
	 * @param listOfPrices the list of prices to apply discount to
	 * @param cartCurrencyCode The currency code of the shopping cart
	 * @param ruleCurrencyCode The currency code of the rule
	 * @param percent the percentage of the promotion X 100 (e.g. 50 means 50% off).
	 */
	public void applyCatalogCurrencyDiscountPercent(final Set <Long> ruleTracker, final long ruleId, final Object listOfPrices,
			final String cartCurrencyCode, final String ruleCurrencyCode, final String percent) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("applyCatalogCurrencyDiscountPercent rule" + ruleId + " prices " + listOfPrices + "cartCurrency "
					+ cartCurrencyCode + " ruleCurrency " + ruleCurrencyCode + " percent " + percent);
		}
		@SuppressWarnings("unchecked")
		List<Price> prices = (List<Price>) listOfPrices;
		if (!cartCurrencyCode.equals(ruleCurrencyCode)) {
			return;
		}

		BigDecimal discountPercent = setDiscountPercentScale(new BigDecimal(percent));

		// Apply the discount to the product
		for (Price price : prices) {
			discountPriceByPercent(discountPercent, price);
		}
		ruleTracker.add(ruleId);
	}



	/**
	 * Reduces the price of a catalog item by the specified amount.
	 *
	 * @param ruleTracker the customer's shopping cart
	 * @param ruleId the id of the rule executing this action
	 * @param listOfPrices the list of prices to apply discount to
	 * @param cartCurrencyCode The currency code of the shopping cart
	 * @param ruleCurrencyCode The currency code of the rule
	 * @param amount the amount by which the price is to be reduced
	 */
	public void applyCatalogCurrencyDiscountAmount(final Set <Long> ruleTracker, final long ruleId, final Object listOfPrices,
			final String cartCurrencyCode, final String ruleCurrencyCode, final String amount) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("applyCatalogCurrencyDiscountAmount rule" + ruleId + " prices " + listOfPrices + "cartCurrency "
					+ cartCurrencyCode + " ruleCurrency " + ruleCurrencyCode + " amount " + amount);
		}
		@SuppressWarnings("unchecked")
		List<Price> prices = (List<Price>) listOfPrices;
		if (!cartCurrencyCode.equals(ruleCurrencyCode)) {
			return;
		}

		final Money discount = stringAmountToMoney(amount, Currency.getInstance(cartCurrencyCode));
		// Apply the discount to the product
		for (Price price : prices) {
			discountPriceByAmount(discount.getAmount(), price);
		}
		ruleTracker.add(ruleId);
	}




	/**
	 * Checks whether a CartItem help the cart satisfy the promotion conditions, given exceptions to the promotion
	 * rule and the catalog in which the promotion is being applied.
	 *
	 * @param cartItem the cart item in question
	 * @param catalog the catalog in which the promotion is being applied
	 * @param exceptions the exclusions to the promotion
	 * @return true if the CartItem is eligible for a promotion with the given exceptions, false if not
	 */
	protected boolean cartItemContributesToPromotionCondition(final ShoppingItem cartItem, final Catalog catalog,
			final PromotionRuleExceptions exceptions) {
		if (!exceptions.isSkuExcluded(cartItem.getProductSku()) && !exceptions.isProductExcluded(cartItem.getProductSku().getProduct())
				&& !productIsInCategoryExcludedFromPromotion(cartItem.getProductSku().getProduct(), catalog, exceptions)
				&& cartItem.isDiscountable()) {
			return true;
		}
		return false;
	}

	private boolean productIsInCategoryExcludedFromPromotion(final Product product, final Catalog catalog, final PromotionRuleExceptions exceptions) {
		for (Category category : product.getCategories(catalog)) {
			if (exceptions.isCategoryExcluded(category)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the shopping cart subtotal is at least equal to the specified amount.
	 *
	 * @param shoppingCart the shopping cart to check
	 * @param amount the amount the shopping cart must exceed
	 * @param exceptionStr exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @return true if the shopping cart subtotal is greater than or equal to the specified amount
	 */
	public boolean cartSubtotalAtLeast(final ShoppingCart shoppingCart, final String amount, final String exceptionStr) {
		PromotionRuleExceptions promotionRuleExceptions = getPromotionRuleExceptions(exceptionStr);
		Catalog catalog = shoppingCart.getStore().getCatalog();
		BigDecimal subTotalAfterException = BigDecimal.ZERO;
		for (ShoppingItem currCartItem : shoppingCart.getCartItems()) {
			if (cartItemContributesToPromotionCondition(currCartItem, catalog, promotionRuleExceptions)) {
				subTotalAfterException = subTotalAfterException.add(currCartItem.getTotal().getAmount());
			}
		}
		BigDecimal subTotal = subTotalAfterException.subtract(shoppingCart.getSubtotalDiscount());
		return subTotal.compareTo(new BigDecimal(amount)) != -1;
	}

	/**
	 * Reduces the shipping cost of the shopping cart by the specified amount. (If the specified shipping method is in use)
	 *
	 * @param shoppingCart the shopping cart that the shipping discount is to be applied to
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the id of the action providing the discount
	 * @param amount the amount by which to reduce the shipping cost
	 * @param shippingLevelCode the code of shipping service level that this discount applies to
	 */
	public void applyShippingDiscountAmount(final ShoppingCart shoppingCart, final long ruleId, final long actionId, final String amount,
			final String shippingLevelCode) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("applyShippingDiscountAmount rule" + ruleId + " actionId: " + actionId + " shippingLevelCode: " + shippingLevelCode
					+ " amount " + amount);
		}
		boolean ruleApplied = false;
		BigDecimal amountBigDecimal = new BigDecimal(amount);
		ShippingServiceLevel currShippingServiceLevel = shoppingCart.getSelectedShippingServiceLevel();

		if (currentShippingServiceLevelSelected(shippingLevelCode, currShippingServiceLevel)) {

			ruleApplied = currShippingServiceLevel.setShippingDiscount(
					MoneyFactory.createMoney(amountBigDecimal, shoppingCart.getCurrency()));
		} else {
			for (ShippingServiceLevel shippingServiceLevel : shoppingCart.getShippingServiceLevelList()) {
				if (shippingLevelCode.equals(shippingServiceLevel.getCode())) {
					shippingServiceLevel.setShippingDiscount(
							MoneyFactory.createMoney(amountBigDecimal, shoppingCart.getCurrency()));
				}
			}

		}

		if (ruleApplied) {
			shoppingCart.shippingRuleApplied(ruleId, actionId, amountBigDecimal);
		}
	}

	/**
	 * Reduces the shipping cost of the shopping cart by the specified percent. (If the specified shipping method is in use)
	 *
	 * @param shoppingCart the shopping cart that the shipping discount is to be applied to
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the id of the action providing the discount
	 * @param percent the percent by which to reduce the shipping cost (String value from 1 to 100)
	 * @param shippingLevelCode the code of shipping service level that this discount applies to
	 */
	public void applyShippingDiscountPercent(final ShoppingCart shoppingCart, final long ruleId, final long actionId, final String percent,
			final String shippingLevelCode) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("applyShippingDiscountPercent rule" + ruleId + " actionId: " + actionId + " shippingLevelCode: " + shippingLevelCode
					+ " percent " + percent);
		}
		boolean ruleApplied = false;
		BigDecimal discount = null;

		BigDecimal discountPercent = new BigDecimal(percent);
		discountPercent = setDiscountPercentScale(discountPercent);

		ShippingServiceLevel currShippingServiceLevel = shoppingCart.getSelectedShippingServiceLevel();

		if (currentShippingServiceLevelSelected(shippingLevelCode, currShippingServiceLevel)) {
			discount = calculateDiscount(discountPercent, currShippingServiceLevel, shoppingCart);
			ruleApplied = currShippingServiceLevel.setShippingDiscount(MoneyFactory.createMoney(discount, shoppingCart.getCurrency()));
		} else {
			for (ShippingServiceLevel shippingServiceLevel : shoppingCart.getShippingServiceLevelList()) {
				if (shippingLevelCode.equals(shippingServiceLevel.getCode())) {
					discount = calculateDiscount(discountPercent, shippingServiceLevel, shoppingCart);
					shippingServiceLevel.setShippingDiscount(MoneyFactory.createMoney(discount, shoppingCart.getCurrency()));
				}
			}
		}

		if (ruleApplied) {
			shoppingCart.shippingRuleApplied(ruleId, actionId, discount);
		}
	}

	private boolean currentShippingServiceLevelSelected(final String shippingLevelCode, final ShippingServiceLevel currShippingServiceLevel) {
		return "0".equals(shippingLevelCode) || (currShippingServiceLevel != null && currShippingServiceLevel.getCode().equals(shippingLevelCode));
	}

	private BigDecimal calculateDiscount(final BigDecimal discountPercent, final ShippingServiceLevel shippingServiceLevel,
			final ShoppingCart shoppingCart) {

		BigDecimal originalShippingCost = shippingServiceLevel.calculateRegularPriceShippingCost(shoppingCart.getApportionedLeafItems(),
				shoppingCart.getCurrency()).getAmount();
		return originalShippingCost.multiply(discountPercent);
	}



	/**
	 * Checks that the current date is between the specified dates.
	 *
	 * @param startDateString the start date represented as a long (milliseconds) in a string. 0 means no start date restriction on the date range.
	 * @param endDateString the end date represented as a long (milliseconds) in a string. 0 means no end date. Longs are not supported by Drools so
	 *            the dates are passed as strings.
	 * @return true if the current date is between the specified dates
	 */
	public boolean checkDateRange(final String startDateString, final String endDateString) {
		long startDate = Long.parseLong(startDateString);
		long endDate = Long.parseLong(endDateString);

		long currentDate = new Date().getTime();
		if (currentDate < startDate) {
			return false;
		} else if (endDate != 0 && currentDate > endDate) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the state of the rule is ACTIVE or DISABLED.
	 *
	 * @param state the <code>long</code> state value of the promotion rule; 1 if ACTIVE, 0 is DISABLED
	 * @return true if the state is ACTIVE, false if it is DISABLED
	 */
	public boolean checkEnabled(final String state) {
		if (state.equalsIgnoreCase("true")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean customerInGroup(final CustomerSession customerSession, final long customerGroup) {
		Customer customer = customerSession.getShopper().getCustomer();
		if (customer == null) {
			return false;
		}

		for (CustomerGroup currCustomerGroup : customer.getCustomerGroups()) {
			if (currCustomerGroup.getUidPk() == customerGroup) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isExistingCustomer(final Customer customer) {
		boolean isExistingCustomer = false;
		if (customer != null && customer.getUidPk() > 0 && !customer.isAnonymous()) {
			isExistingCustomer = true;
		}
		return isExistingCustomer;
	}

	/**
	 * Helper method to create a <code>Money</code> object from a string amount and currency.
	 *
	 * @param amount the amount to set as the value of the <code>Money</code> object
	 * @param currency the currency
	 * @return a new initialized <code>Money</code> object
	 */
	private Money stringAmountToMoney(final String amount, final Currency currency) {
		return MoneyFactory.createMoney(new BigDecimal(amount), currency);
	}

	/**
	 * Sets the rule service.
	 *
	 * @param ruleService the rule service
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * Checks Limited Usage Promotion.
	 *
	 * @param shoppingCart the shopping cart to check for the promo code
	 * @param allowedLimitParm the limit of the promotion
	 * @param ruleCode to search for <code>Rule</code> containing current LUP number
	 * @param ruleId the id of the rule being applied
	 * @return true if the rule is passed, else false
	 */
	public boolean checkLimitedUsagePromotion(final ShoppingCart shoppingCart, final String allowedLimitParm,
			final String ruleCode, final long ruleId) {
		if (!shoppingCart.getLimitedUsagePromotionRuleCodes().containsKey(ruleCode)) {
			try {
				if (Long.parseLong(allowedLimitParm) > ruleService.findLupByRuleCode(ruleCode)) {
					shoppingCart.applyLimitedUsagePromotionRuleCode(ruleCode, ruleId);
				} else {
					return false;
				}

			} catch (NumberFormatException pe) {
				LOG.debug(" Could not parse allowedLimit.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks the cart for a limited use coupon code.
	 *
	 * @param shoppingCart the cart to check
	 * @param ruleId the id of the rule that requires a coupon code
	 * @return true if there is a valid coupon code in the cart
	 */
	public boolean cartHasValidLimitedUseCouponCode(final ShoppingCart shoppingCart, final long ruleId) {
		return shoppingCart.hasLUCCForRule(ruleId);
	}

	@Override
	public int calculateAvailableDiscountQuantity(final ShoppingCart shoppingCart, final long ruleId,
			final int discountQuantityPerCoupon) {
		final String ruleCode = ruleService.findRuleCodeById(ruleId);
		String customerEmailAddress;
		if (shoppingCart.getShopper().getCustomer() == null) {
			customerEmailAddress = null;
		} else {
			customerEmailAddress = shoppingCart.getShopper().getCustomer().getEmail();
		}

		if (!isCouponDrawdownValid(ruleCode, customerEmailAddress)) {
			return discountQuantityPerCoupon;
		}

		int couponUsesRemaining = 0;
		Set<String> shoppingCartPromoCodes = shoppingCart.getPromotionCodes();
		Collection<CouponUsage> couponUses = couponUsageService.findByRuleCodeAndEmail(ruleCode, customerEmailAddress);

		// There may be no couponUsage yet because the coupon hasn't been used by this customer.
		if (couponUses.isEmpty()) {
			return discountQuantityPerCoupon;
		}
		for (CouponUsage couponUsage : couponUses) {
			if (shoppingCartPromoCodes.contains(couponUsage.getCoupon().getCouponCode())) {
				int usesRemaining = couponUsage.getCoupon().getCouponConfig().getUsageLimit() - couponUsage.getUseCount();
				if (CouponUsageType.LIMIT_PER_COUPON.equals(couponUsage.getCoupon().getCouponConfig().getUsageType())
						|| couponUsage.getCoupon().getCouponConfig().isMultiUsePerOrder()) {
					couponUsesRemaining += usesRemaining;
				} else {
					if (usesRemaining > 0) {
						couponUsesRemaining++;
					}
				}
			}
		}

		return discountQuantityPerCoupon * couponUsesRemaining;
	}

	/**
	 * Check whether a drawdown of the quantity based on coupon availability is valid.
	 *
	 * @param ruleCode the code of the rule to check
	 * @param customerEmailAddress the customer email address
	 * @return true if coupon usage can multiply the discount quantity
	 */
	protected boolean isCouponDrawdownValid(final String ruleCode, final String customerEmailAddress) {
		CouponConfig couponConfig = couponConfigService.findByRuleCode(ruleCode);
		return couponConfig != null
		       && !CouponUsageType.LIMIT_PER_COUPON.equals(couponConfig.getUsageType())
		       && !couponConfig.isUnlimited()
		       && customerEmailAddress != null;
	}


	@Override
	public void assignCouponToCustomer(final ShoppingCart shoppingCart, final long ruleId) {
		// Mark the rule as applied so that the CheckoutService gets it in the ruleApplied list.
		shoppingCart.ruleApplied(ruleId, 0, null, null, 0);
	}

	/**
	 *
	 * @param couponUsageService The coupon usage service to set.
	 */
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}

	/**
	 *
	 * @param couponConfigService the couponConfigService to set
	 */
	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}

	/**
	 * Get the Product Retrieve Strategy.
	 *
	 * @return the Product Retrieve Strategy
	 */
	public ProductRetrieveStrategy getProductRetrieveStrategy() {
		return productRetrieveStrategy;
	}

	/**
	 * Set the Product Retrieve Strategy.
	 *
	 * @param productRetrieveStrategy the Product Retrieve Strategy
	 */
	public void setProductRetrieveStrategy(
			final ProductRetrieveStrategy productRetrieveStrategy) {
		this.productRetrieveStrategy = productRetrieveStrategy;
	}

}
