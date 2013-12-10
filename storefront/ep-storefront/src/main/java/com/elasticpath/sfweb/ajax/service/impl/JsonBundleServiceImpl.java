/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.ajax.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.InvalidBundleTreeStructureException;
import com.elasticpath.commons.util.CacheInvalidationStrategy;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.shopper.LocaleProvider;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.pricing.PriceProvider;
import com.elasticpath.sfweb.ajax.bean.JsonBundleItemBean;
import com.elasticpath.sfweb.ajax.bean.PriceTierBean;
import com.elasticpath.sfweb.ajax.bean.impl.AggregatedPrice;
import com.elasticpath.sfweb.ajax.bean.impl.JsonBundleItemBeanImpl;
import com.elasticpath.sfweb.ajax.service.JsonBundleFactory;
import com.elasticpath.sfweb.ajax.service.JsonBundleService;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Provides services relating to Sku Configuration.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class JsonBundleServiceImpl extends AbstractEpServiceImpl implements JsonBundleService {
	/**
	 * prefix for message properties.
	 */
	public static final String MESSAGE_SOURCE_PREFIX = "productTemplate.recurringPrice.";

	private SfRequestHelper requestHelper;
	private PriceLookupFacade priceLookupFacade;
	private ProductSkuService productSkuService;
	private JsonBundleFactory jsonBundleFactory;
	private PriceBuilder priceBuilder;
	private MessageSource messageSource;
	private BeanFactory beanFactory;
	private Currency currency;
	private CacheInvalidationStrategy cacheInvalidationStrategy;

	/**
	 * @param selectedSku {@link ProductSku}
	 * @param shopper CustomerSession
	 * @return price found
	 */
	private Price getSkuPrice(final ProductSku selectedSku, final Shopper shopper) {
		return priceLookupFacade.getPromotedPriceForSku(selectedSku, shopper.getCurrentShoppingCart().getStore(),
				shopper, shopper.getCurrentShoppingCart().getAppliedRules());
	}

	/**
	 * @param priceLookupFacade the priceLookupFacade to set
	 */
	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}

	/**
	 * @param requestHelper {@link SfRequestHelper}
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

	/**
	 * @param productSkuService ProductSkuService to be injected.
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

	@Override
	public JsonBundleItemBeanImpl updateJsonBundle(final JsonBundleItemBeanImpl bundle, final HttpServletRequest request) {
		final CustomerSession customerSession = requestHelper.getCustomerSession(request);
		return updateJsonBundleUsingSession(bundle, customerSession.getShopper());
	}

	@Override
	public JsonBundleItemBeanImpl updateJsonBundleUsingSession(final JsonBundleItemBeanImpl bundle, final Shopper shopper) {
		this.currency = shopper.getCurrency();

		// skuMap will be populated by key value pair of (skuCode => Price)
		// Its purpose is to improve performance, instead we have to use PriceLookupFacade.
		Map<String, Price> skuMap = new java.util.HashMap<String, Price>();
		// traverse the bundle tree
		updateItemPrice(null, bundle, shopper, bundle.getQuantity(), skuMap);

		return bundle;
	}

	/**
	 * @param parent parent bundle item, used to retrieve ProductBundle.
	 * @param item current bundle item.
	 * @param shopper CustomerSession.
	 * @param quantity quantity of this current item, multiplied layer by layer.
	 * @param skuMap a map of skucode => Price.
	 */
	protected void updateItemPrice(final JsonBundleItemBean parent,
			final JsonBundleItemBean item,
			final Shopper shopper,
			final int quantity,
			final Map<String, Price> skuMap) {

		// 1. the current item is a single item,
		// !item.isCalculatedBundle() is not to check nested bundle.
		// update the price according to sku.
		if (!item.isCalculatedBundle() && item.isCalculatedBundleItem()) {
			ProductSku sku = productSkuService.findBySkuCode(item.getSkuCode());
			Price skuPrice = getSkuPrice(sku, shopper);
			skuMap.put(item.getSkuCode(), skuPrice);
			Money lowestPrice = null;

			if (skuPrice == null) {
				// clean all price information
				item.setPrice(BigDecimal.ZERO);
				item.setPriceTiers(Collections.<PriceTierBean>emptyList());
				item.setPriceAdjustment(BigDecimal.ZERO);
				item.setPaymentSchedule("");
				item.setRecurringPrice(BigDecimal.ZERO);
				item.setRecurringPriceTiers(Collections.<PriceTierBean>emptyList());
				return;
			}

			int effectiveQty = getPriceBuilder().getEffectiveQuantity(skuPrice, quantity);

			lowestPrice = skuPrice.getLowestPrice(effectiveQty);

			item.setPrice(lowestPrice.getAmount());
			item.setPriceTiers(getPriceBuilder().getPriceTiers(skuPrice));

			// set recurring price
			Collection<PriceSchedule> recurringSchedules = skuPrice.getPricingScheme().getRecurringSchedules();
			if (recurringSchedules.isEmpty()) {
				// needs to clean up recurring fields
				item.setRecurringPrice(BigDecimal.ZERO);
				item.setPaymentSchedule("");
				item.setRecurringPriceTiers(Collections.<PriceTierBean>emptyList());
				updateItemPriceAdjustment(parent, item, skuPrice, effectiveQty);
			} else {
				PriceSchedule schedule = recurringSchedules.iterator().next();
				Price priceForSchedule = (Price) skuPrice.getPricingScheme().getSimplePriceForSchedule(schedule);
				effectiveQty = getPriceBuilder().getEffectiveQuantity(priceForSchedule, quantity);
				item.setRecurringPrice(priceForSchedule.getLowestPrice(effectiveQty).getAmount());
				item.setRecurringPriceTiers(getPriceBuilder().getPriceTiers(priceForSchedule));
				String scheduleName = schedule.getPaymentSchedule().getName();
				item.setPaymentSchedule(messageSource.getMessage(MESSAGE_SOURCE_PREFIX + scheduleName,
						null, scheduleName, shopper.getLocale()));
				updateItemPriceAdjustment(parent, item, priceForSchedule, effectiveQty);
			}

			// job done for this case.
			return;
		} else if (item.isCalculatedBundle()) {

			// 2. the current item is a nested bundle.
			// 2.1 update its children's prices.
			for (JsonBundleItemBean child : item.getConstituents()) {
				updateItemPrice(item, child, shopper, quantity * child.getQuantity(), skuMap);
			}

			// 2.2 if current item is calculated bundle, its price is calculated from its selected children.
			// in 2.1, all child and descendant item price are updated.

			Price calculatedPrice = calculateBundlePrice(item, skuMap);
			// we shall put the calculated price for up level bundle
			updateItemWithCalculatedPrice(item, calculatedPrice, skuMap, quantity, shopper);
		}

	}

	private void updateItemPriceAdjustment(final JsonBundleItemBean parent, final JsonBundleItemBean item, final Price skuPrice,
											final int quantity) {

		ProductBundle bundle = (ProductBundle) productSkuService.findBySkuCode(parent.getSkuCode()).getProduct();

		//determine that product from cache matches (same number and same order of constituents) the product from database
		if (!hasTheSameTreeStructure(bundle.getConstituents(), parent.getConstituents())) {
			//evict from cache and redirect
			cacheInvalidationStrategy.invalidateCachesForObject(bundle.getUidPk());
			throw new InvalidBundleTreeStructureException("ProductBundle from cache is not valid.");
		}
		BundleConstituent constituent = getBundleConstituent(bundle, parent, item);
		//clear the existing price adjustment
		item.setPriceAdjustment(null);

		if (constituent != null) {
			PriceTier priceTier = skuPrice.getPriceTierByQty(quantity);
			// price tier may be null if quantity < first tier quantity.
			if (priceTier != null) {
				PriceAdjustment priceAdjustment = constituent.getPriceAdjustmentForPriceList(priceTier.getPriceListGuid());
				if (priceAdjustment != null) {
					if (bundle.isCalculated()) {
						// Use the minimum of either 0 or the adjustment because positive price adjustments are not
						// allowed in calculated bundles.
						item.setPriceAdjustment(priceAdjustment.getAdjustmentAmount().min(BigDecimal.ZERO));
					} else {
						item.setPriceAdjustment(priceAdjustment.getAdjustmentAmount());
					}
				}
			}
		}

	}

	/**
	 * This function will not be accurate if the bundle has 2 of the same product or productSku or productBundles.
	 * @param constituentsList1
	 * @param constituentsList2
	 * @return
	 */
	private boolean hasTheSameTreeStructure(final List<BundleConstituent> constituentsList1, final List<JsonBundleItemBean> constituentsList2) {
		if (constituentsList1.size() != constituentsList2.size()) {
			return false;
		}

		for (int constituentIndex = 0; constituentIndex < constituentsList1.size(); constituentIndex++) {
			ConstituentItem bundleConstituent =  constituentsList1.get(constituentIndex).getConstituent();
			JsonBundleItemBean correspondingBundleConstituent = constituentsList2.get(constituentIndex);
			if (bundleConstituent.isBundle()) {
				if (!hasTheSameTreeStructure(((ProductBundle) bundleConstituent.getProduct()).getConstituents(),
							correspondingBundleConstituent.getConstituents())) {
					return false;
				}
			} else if (bundleConstituent.isProductSku()) {
				if (!correspondingBundleConstituent.getSkuCode().equals(bundleConstituent.getProductSku().getSkuCode())) {
					return false;
				}
			} else if (!correspondingBundleConstituent.getProductCode().equals(bundleConstituent.getProduct().getCode())) {
					return false;
			}
		}
		return true;
	}

	private BundleConstituent getBundleConstituent(final ProductBundle bundle, final JsonBundleItemBean parent, final JsonBundleItemBean item) {
		int index = parent.getConstituents().indexOf(item);

		return bundle.getConstituents().get(index);

	}

	/**
	 * Update item with the calculated price.
	 *
	 * @param item current item, which is either top level or nested calculated bundle.
	 * @param calculatedPrice calculated price for this calculated bundle.
	 * @param skuMap contains skuCode => Price mapping.
	 * @param quantity accumulated quantity of the current item.
	 * @param localeProvider CustomerSession.
	 */
	protected void updateItemWithCalculatedPrice(final JsonBundleItemBean item, final Price calculatedPrice, final Map<String, Price> skuMap,
			final int quantity, final LocaleProvider localeProvider) {

		if (calculatedPrice == null) {
			return;
		}

		skuMap.put(item.getSkuCode(), calculatedPrice);

		int effectiveQty = getPriceBuilder().getEffectiveQuantity(calculatedPrice, quantity);
		Money lowestPrice = calculatedPrice.getLowestPrice(effectiveQty);
		if (lowestPrice != null) {
			item.setPrice(lowestPrice.getAmount());
		}

		item.setPriceTiers(getPriceBuilder().getPriceTiers(calculatedPrice));

		List<AggregatedPrice> aggregatedPrices = getPriceBuilder().getAggregatedPrices(
				calculatedPrice.getPricingScheme(), messageSource, localeProvider);
		item.setAggregatedPrices(aggregatedPrices);

	}

//	private void populateAggregatedPriceTiers(
//			final Map<String, List<PriceTierBean>> aggregatedPriceTiers,
//			final Price calculatedPrice) {
//		calculatedPrice.getPricingScheme();
//
//	}

	/**
	 * This method only does the calculation. <br>
	 * Updating of the bundle object is done in updateItemPrice().
	 *
	 * Right now this method sums the lowest price of each item, multiplied by consituent quantity, added by consitituent price adjustment.
	 * The result was set to bundle list price.
	 *
	 * It may be required in the future, to display list value and the sale value,
	 * for which we should refactor the JsonBundleItemBean and the algorithom here.
	 *
	 * @param bundle calculated bundle.
	 * @param skuMap a map of skuCode => Price.
	 * @return calculated price of the calculated bundle.
	 */
	protected Price calculateBundlePrice2(final JsonBundleItemBean bundle, final Map<String, Price> skuMap) {
		// 1. get all possible tiers from child items.
		Set<Integer> tiers = getBundleTiers(bundle, skuMap);

		// 2. calculate for each tier
		Price calculatedPrice = getBean(ContextIdNames.PRICE);
		for (Integer tier : tiers) {

			// calculate the price for this tier:
			BigDecimal tieredValue = calculateTierPrice(bundle, skuMap, calculatedPrice, tier);
			if (tieredValue != null) {
				// get an instance of PriceTier
				PriceTier priceTier = getBean(ContextIdNames.PRICE_TIER);
				// set tier quantity
				priceTier.setMinQtyAsInteger(tier);
				// set the tier value
				priceTier.setListPrice(tieredValue);
				// add this PriceTier to the result calculated price
				calculatedPrice.addOrUpdatePriceTier(priceTier);
			}
		}

		return calculatedPrice;
	}

	/**
	 *
	 * @param bundle JsonBundleItemBean.
	 * @param skuMap price map.
	 * @return calculated price of bundle.
	 */
	protected Price calculateBundlePrice(final JsonBundleItemBean bundle, final Map<String, Price> skuMap) {

		ProductBundle productBundle = (ProductBundle) productSkuService.findBySkuCode(bundle.getSkuCode()).getProduct();
		PricedCalculatedBundleForJson priced = new PricedCalculatedBundleForJson(bundle, productBundle,
				beanFactory, getPriceProvider(skuMap), productSkuService);

		return priced.getPrice();
	}

	private PriceProvider getPriceProvider(final Map<String, Price> skuMap) {
		return new PriceProvider() {

			public Price getProductSkuPrice(final ProductSku productSku) {
				return skuMap.get(productSku.getSkuCode());
			}

			public Price getProductPrice(final Product product) {
				return null;
			}

			public Currency getCurrency() {
				return currency;
			}
		};
	}

	/**
	 * Calculate the price for the bundle for the specific tier.
	 *
	 * For each child item,
	 *  - retrieve its lowest price according to the accumulated quantity
	 *  - multiplied by the constituent quantity
	 *  - added by the constituent price adjustment
	 *
	 *
	 * @param bundle the bundle
	 * @param skuMap the sku map
	 * @param calculatedPrice the calculated price
	 * @param tier the price tier
	 * @return the price for the specific tier
	 */
	protected BigDecimal calculateTierPrice(final JsonBundleItemBean bundle,
			final Map<String, Price> skuMap, final Price calculatedPrice,
			final Integer tier) {

		BigDecimal updatedTieredValue = BigDecimal.ZERO;

		for (JsonBundleItemBean item : bundle.getConstituents()) {
			// if current item is not selected, just omit
			if (!item.isSelected()) {

				continue;
			}

			// accumulate item price
			Price itemPrice = skuMap.get(item.getSkuCode());

			if (itemPrice != null && itemPrice.getPriceTiers() != null) {

				int lineQty = tier * item.getQuantity();

				Money lowestPrice = itemPrice.getLowestPrice(lineQty);

				if (lowestPrice == null) {
					// Bundle constituent item is not purchasable at this quantity
					return null;
				}

				// apply price adjustment to unit price
				BigDecimal adjustedLinePrice = lowestPrice.getAmount().add(item.getPriceAdjustment())
						.multiply(BigDecimal.valueOf(item.getQuantity()));

				// set adjusted line price to zero if it's < 0
				if (adjustedLinePrice.compareTo(BigDecimal.ZERO) < 0) {
					adjustedLinePrice = BigDecimal.ZERO;
				}

				// add the item price multiplied by the item quantity
				updatedTieredValue = updatedTieredValue.add(adjustedLinePrice);

				if (calculatedPrice.getCurrency() == null) {

					calculatedPrice.setCurrency(itemPrice.getCurrency());
				}
			}

		}
		return updatedTieredValue;
	}

	private Set<Integer> getBundleTiers(final JsonBundleItemBean bundle, final Map<String, Price> skuMap) {
		Set<Integer> tiers = new TreeSet<Integer>();
		for (JsonBundleItemBean item : bundle.getConstituents()) {
			if (item.isSelected()) {
				Price itemPrice = skuMap.get(item.getSkuCode());
				if (itemPrice != null
				   && itemPrice.getPriceTiers() != null) {
					// It shall be converted to bundle tier
					for (Integer itemTier : itemPrice.getPriceTiers().keySet()) {
						int bundleTier = (int) Math.ceil(itemTier.doubleValue() / item.getQuantity());
						tiers.add(bundleTier);
					}
				}
			}
		}
		return tiers;
	}

	/**
	 * @return JsonBundleFactory object.
	 */
	public JsonBundleFactory getJsonBundleFactory() {
		return jsonBundleFactory;
	}

	/**
	 * @param jsonBundleFactory JsonBundleFactory object.
	 */
	public void setJsonBundleFactory(final JsonBundleFactory jsonBundleFactory) {
		this.jsonBundleFactory = jsonBundleFactory;
	}

	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 *
	 * @param beanFactory to set.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param cacheInvalidationStrategy the cacheInvalidationStrategy to set.
	 */
	public void setCacheInvalidationStrategy(final CacheInvalidationStrategy cacheInvalidationStrategy) {
		this.cacheInvalidationStrategy = cacheInvalidationStrategy;
	}

	public void setPriceBuilder(final PriceBuilder priceBuilder) {
		this.priceBuilder = priceBuilder;
	}

	protected PriceBuilder getPriceBuilder() {
		return priceBuilder;
	}
}
