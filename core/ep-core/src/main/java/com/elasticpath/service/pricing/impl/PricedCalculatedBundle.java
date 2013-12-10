package com.elasticpath.service.pricing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.catalog.SimplePrice;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.service.pricing.PriceProvider;
import com.elasticpath.service.pricing.Priced;

/**
 * A calculated product bundle that has been priced.
 */
public class PricedCalculatedBundle implements Priced {
	private final ProductBundle bundle;
	private final BeanFactory beanFactory;
	private final PriceProvider priceProvider;



	/**
	 * Default constructor.
	 * @param bundle the bundle to calculate the price for, should be a Calculated Bundle.
	 * @param beanFactory the bean factory instance
	 * @param priceProvider the price provider
	 */
	public PricedCalculatedBundle(final ProductBundle bundle, final BeanFactory beanFactory,
			final PriceProvider priceProvider) {
		this.bundle = bundle;
		this.beanFactory = beanFactory;
		this.priceProvider = priceProvider;
	}

	/**
	 * @return the price
	 */
	public Price getPrice() {
		PricingScheme pricingScheme = createPricingScheme();
		Price price = null;
		if (pricingScheme != null) {
			Collection<PriceSchedule> purchaseTimeSchedules = pricingScheme.getPurchaseTimeSchedules();
			//making sure there is always a purchase-time price value.
			if (CollectionUtils.isEmpty(purchaseTimeSchedules)) {
				//it will be a fake 0.00 if the price is all recurring.
				price = createZeroPrice();

				//a bundle with no constituents will have a real 0.00 list price.
				if (CollectionUtils.isEmpty(pricingScheme.getSchedules())) {
					PriceSchedule purchaseTimeSchedule = getBeanFactory().getBean(ContextIdNames.PRICE_SCHEDULE);
					purchaseTimeSchedule.setType(PriceScheduleType.PURCHASE_TIME);
					pricingScheme.setPriceForSchedule(purchaseTimeSchedule, price);
				}
			} else {
				//use the actual purchase-time if there is one.
				price = (Price) pricingScheme.getSimplePriceForSchedule(purchaseTimeSchedules.iterator().next());
			}
			price.setPricingScheme(pricingScheme);
		}
		return price;
	}


	/**
	 * Calculates the lowest possible price of a calculated bundle.
	 *
	 * The following behavior should be noted :
	 *
	 * 1. For a calculated bundle with zero constituents, a non null price with
	 * a list price of ZERO will be returned.
	 *
	 * This is in agreement with the operation of Assigned bundles.
	 *
	 * 2. For a calculated bundle with at least one constituent, if one of the
	 * constituent prices is null, the price returned will be null
	 *
	 * This can then indicate that evasive action can be taken to prevent
	 * display or other usage of bundles with constituent items that have a null price.
	 *
	 *
	 * @return the price containing price tiers
	 */
	protected PricingScheme createPricingScheme() {
		if (!bundle.isCalculated()) {
			throw new EpSystemException("operation is not supported on non-calculated bundles.");
		}

		List<BundleConstituent> constituents = bundle.getConstituents();
		PricingScheme pricingScheme = getBeanFactory().getBean(ContextIdNames.PRICING_SCHEME);
		if (CollectionUtils.isEmpty(constituents)) {
			return pricingScheme;
		}

		int itemsToSelect = getNumberOfItemsToSelect(bundle);
		List<Pair<BundleConstituent, Price>> prices = getSelectedConstituentsAndPrices(constituents, itemsToSelect);

		if (prices == null) {
			return null;
		}

		Integer bundleMinTierQuantity = getBundleMinTierQuantity(prices);
		if (bundleMinTierQuantity == null) {
			return null;
		}

		Set<PriceSchedule> priceSchedules = getPriceSchedules(prices);

		for (PriceSchedule schedule : priceSchedules) {
			SimplePrice simplePrice = calculatePriceForSchedule(prices, schedule, bundleMinTierQuantity);
			pricingScheme.setPriceForSchedule(schedule, simplePrice);
		}
		return pricingScheme;
	}

	/**
	 * @param prices
	 */
	private Integer getBundleMinTierQuantity(
			final List<Pair<BundleConstituent, Price>> prices) {
		int bundleMinTier = -1;

		for (Pair<BundleConstituent, Price> pair : prices) {
			Set<Integer> constituentTiers = pair.getSecond().getPricingScheme().getPriceTiersMinQuantities();
			int constituentQuantity = pair.getFirst().getQuantity();
			if (CollectionUtils.isEmpty(constituentTiers)) {
				return null;
			}
			int minTierMin = constituentTiers.iterator().next();
			int constituentBundleMinTier = getBundleTierFromConstituentTierAndQuantity(minTierMin, constituentQuantity);
			if (bundleMinTier < constituentBundleMinTier) {
				bundleMinTier = constituentBundleMinTier;
			}
		}
		return bundleMinTier;
	}

	/**
	 * Finds all the price schedules in all the prices in the given list.
	 *
	 * @param prices prices
	 * @return all price schedules in a set
	 */
	private Set<PriceSchedule> getPriceSchedules(final List<Pair<BundleConstituent, Price>> prices) {
		Set<PriceSchedule> priceSchedules = new HashSet<PriceSchedule>();
		for (Pair<BundleConstituent, Price> pair : prices) {
			Collection<PriceSchedule> schedules = pair.getSecond().getPricingScheme().getSchedules();
			priceSchedules.addAll(schedules);
		}
		return priceSchedules;
	}


	/**
	 * Calculates the price for the given schedule. It will use the {@link shouldApplyAdkustments} method to
	 * determine whether the adjustments should be applied.
	 *
	 * @param prices the constituents and their prices
	 * @param priceSchedule the schedule
	 * @param bundleMinTierQuantity the minimum tier for the bundle.
	 * @return the calculated price in a {@code PriceImpl}
	 */
	protected SimplePrice calculatePriceForSchedule(final List<Pair<BundleConstituent, Price>> prices,
			final PriceSchedule priceSchedule, final Integer bundleMinTierQuantity) {
		List<Pair<BundleConstituent, SimplePrice>> simplePrices = preparePricesForSchedule(prices, priceSchedule);
		final Set<Integer> tierMins = getTierMinQuantities(simplePrices);
		tierMins.add(bundleMinTierQuantity);
		Price price = getBeanFactory().getBean(ContextIdNames.PRICE);
		price.setCurrency(getPriceProvider().getCurrency());
		PriceTier prevTier = null;
		boolean applyAdjustments = shouldApplyAdjustments(priceSchedule);

		for (Integer minQty : tierMins) {
			if (bundleMinTierQuantity.compareTo(minQty) > 0) {
				continue;
			}
			PriceTier newTier = createPriceTierFromBundleConstituents(simplePrices, minQty, applyAdjustments);
			if (newTier != null && !isRedundant(prevTier, newTier)) {
				price.addOrUpdatePriceTier(newTier);
				prevTier = newTier;
			}
		}
		return price;
	}

	/**
	 * Determines whether the adjustments should be applied to prices of the given price schedule.
	 *
	 * @param priceSchedule The price schedule.
	 * @return Always returns <code>true</code>.
	 */
	protected boolean shouldApplyAdjustments(final PriceSchedule priceSchedule) {
		return true;
	}

	private int getNumberOfItemsToSelect(final ProductBundle bundle) {
		int itemsToSelect;
		SelectionRule selectionRule = bundle.getSelectionRule();
		if (selectionRule == null || selectionRule.getParameter() == 0) {
			itemsToSelect = bundle.getConstituents().size();
		} else {
			itemsToSelect = selectionRule.getParameter();
		}
		return itemsToSelect;
	}

	/**
	 * Find the selected constituent for the bundle. In the current implementation, it will return the
	 * default selection, i.e. for any bundle, the first <code>itemsToSelect</code> items are selected.
	 *
	 * @param constituents bundle constituents
	 * @param itemsToSelect number of items to be selected
	 * @return selected items as a list of pairs of bundle constituents and their prices.
	 * Will return <code>null</code> if any of the prices are null.
	 */
	protected List<Pair<BundleConstituent, Price>> getSelectedConstituentsAndPrices(final List<BundleConstituent> constituents,
			final int itemsToSelect) {
		List<Pair<BundleConstituent, Price>> prices = new ArrayList<Pair<BundleConstituent, Price>>(itemsToSelect);
		Collection<BundleConstituent> selectedConstituents = getBundleDefaultSelection(constituents, itemsToSelect);

		for (BundleConstituent item : selectedConstituents) {
			Price price = getConstituentPrice(item);
			if (price == null) {
				return null;
			}
			prices.add(new Pair<BundleConstituent, Price>(item, price));
		}
		return prices;
	}


	/**
	 * Gets the list of bundle constituents that will be selected by default.
	 *
	 * @param constituents all the bundle constituents
	 * @param itemsToSelect items to select
	 * @return selected items in an ArrayList.
	 */
	protected Collection<BundleConstituent> getBundleDefaultSelection(final List<BundleConstituent> constituents, final int itemsToSelect) {
		List<BundleConstituent> selectedConstituents = new ArrayList<BundleConstituent>();
		for (int i = 0; i < itemsToSelect; ++i) {
			selectedConstituents.add(constituents.get(i));
		}
		return selectedConstituents;
	}

	/**
	 * Get the price for the bundle constituent.
	 * @param constituent the bundle constituent item
	 * @return the price for the item
	 */
	protected Price getConstituentPrice(final BundleConstituent constituent) {
		ConstituentItem constituentItem = constituent.getConstituent();
		if (constituentItem.isProduct()) {
			return getPriceProvider().getProductPrice(constituentItem.getProduct());
		}
		return getPriceProvider().getProductSkuPrice(constituentItem.getProductSku());
	}

	/**
	 * Looks at the prices of the bundle constituents, and finds the cheapest first <code>itemsToSelect</code> items, given the fact
	 * that <code>minQty</code> of the bundle itself will be purchased. Then creates a <code>PriceTier</code> based on that.
	 *
	 * @param prices a list of pairs of <code>BundleConstituent</code> and their <code>Price</code>.
	 * @param itemsToSelect the number of items to be selected in the bundle
	 * @param minQty minimum number of bundles to be purchased
	 * @param applyAdjustments determines whether the adjustment should be applied
	 * @return the price tier
	 */
	private PriceTier createPriceTierFromBundleConstituents(final List<Pair<BundleConstituent, SimplePrice>> prices,
			final Integer minQty, final boolean applyAdjustments) {
		BigDecimal totalListPrice = BigDecimal.ZERO;
		BigDecimal totalSalePrice = BigDecimal.ZERO;
		BigDecimal totalComputedPrice = BigDecimal.ZERO;

		boolean foundSalePrice = false;

		// iterating through the number of items to include in the bundle pricing

		for (Pair<BundleConstituent, SimplePrice> pair : prices) {
			BundleConstituent constituent = pair.getFirst();
			SimplePrice bundleItemPrice = pair.getSecond();
			final int quantity = constituent.getQuantity() * minQty;

			final Money listPrice = bundleItemPrice.getListPrice(quantity);
			if (listPrice == null) {
				return null;
			}
			Money salePrice = bundleItemPrice.getSalePrice(quantity);


			// add to the bundle list price the items list price multiplied by the bundle item quantity
			BigDecimal constituentQuantity = BigDecimal.valueOf(constituent.getQuantity());
			totalListPrice = totalListPrice.add(listPrice.getAmount().multiply(constituentQuantity));

			if (salePrice == null) {
				salePrice = listPrice;
			} else {
				foundSalePrice = true;
			}

			// set the sale price in the same manner.
			totalSalePrice = totalSalePrice.add(salePrice.getAmount().multiply(constituentQuantity));

			BigDecimal bundleItemComputedAmount = getConstituentComputedAmount(constituent, (Price) bundleItemPrice, quantity, applyAdjustments);

			// add result to totalComputedPrice price
			totalComputedPrice = totalComputedPrice.add(bundleItemComputedAmount);
		}

		// finally compose a new price tier and set the list and sale price and return the price tier for the bundle for this price tier level
		PriceTier newTier = getBeanFactory().getBean(ContextIdNames.PRICE_TIER);

		newTier.setMinQty(minQty);
		newTier.setListPrice(totalListPrice);
		if (foundSalePrice) {
			newTier.setSalePrice(totalSalePrice);
		}
		newTier.setComputedPrice(totalComputedPrice);

		return newTier;
	}

	private List<Pair<BundleConstituent, SimplePrice>> preparePricesForSchedule(final List<Pair<BundleConstituent, Price>> prices,
			final PriceSchedule priceSchedule) {
		final List<Pair<BundleConstituent, SimplePrice>> preparedPrices = new ArrayList<Pair<BundleConstituent, SimplePrice>>();
		for (Pair<BundleConstituent, Price> pair : prices) {
			if (pair.getSecond() == null) {
				return null;
			}

			SimplePrice price = pair.getSecond().getPricingScheme().getSimplePriceForSchedule(priceSchedule);
			if (price == null) {
				price = createZeroPrice();
			}
			preparedPrices.add(new Pair<BundleConstituent, SimplePrice>(pair.getFirst(), price));
		}
		return preparedPrices;
	}

	/**
	 *
	 * Return the constituent item computed amount for the given price and quantity.
	 *
	 * 1. Get the correct price tier object for the given quantity from the bundle item price.
	 *
	 * 2. Determine the correct price adjustment for the chosen price tier.
	 *
	 * 3. Determine the base computed amount which will be the lowest price for that item quantity multiplied by the actual item quantity.
	 *
	 * 4. Finally apply the adjustment to this amount, and return.
	 *
	 *
	 * @param constituent the bundle constituent
	 * @param bundleItemPrice the bundle item price
	 * @param quantity the bundle quantity multiplied by the bundle item quantity
	 * @param applyAdjustment determines whether the adjustment should be applied
	 * @return the computed amount for the bundle constituent
	 */
	protected BigDecimal getConstituentComputedAmount(final BundleConstituent constituent, final Price bundleItemPrice,
			final int quantity, final boolean applyAdjustment) {
		Money lowestItemPrice = bundleItemPrice.getLowestPrice(quantity);
		BigDecimal bundleItemComputedAmount = lowestItemPrice.getAmount();

		if (applyAdjustment) {
			// get the nearest price tier for the given bundle item quantity, there may not exist a specific price tier for any given quantity
			PriceTier bundleItemPriceTierForMinQty = bundleItemPrice.getPriceTierByQty(quantity);

			String priceListGuid = bundleItemPriceTierForMinQty.getPriceListGuid();

			// get the correct adjustment amount for this bundle item
			PriceAdjustment bundleItemPriceAdj = constituent.getPriceAdjustmentForPriceList(priceListGuid);

			// Use the minimum of either 0 or the adjustment because postive priceadjustments are not
			// allowed in calculated bundles.
			// Apply the adjustment to the lowest price to result in the computed price.
			BigDecimal adjAmount = BigDecimal.ZERO;
			if (bundleItemPriceAdj != null) {
				adjAmount = bundleItemPriceAdj.getAdjustmentAmount().min(BigDecimal.ZERO);
			}

			bundleItemComputedAmount = bundleItemComputedAmount.add(adjAmount);

			if (bundleItemComputedAmount.compareTo(BigDecimal.ZERO) < 0) {
				bundleItemComputedAmount = BigDecimal.ZERO;
			}
		}
		BigDecimal constituentItemQty = BigDecimal.valueOf(constituent.getQuantity());
		bundleItemComputedAmount = bundleItemComputedAmount.multiply(constituentItemQty);
		return bundleItemComputedAmount;
	}

	private Set<Integer> getTierMinQuantities(final List<Pair<BundleConstituent, SimplePrice>> simplePrices) {
		final Set<Integer> tierMins = new TreeSet<Integer>();
		for (Pair<BundleConstituent, SimplePrice> pair : simplePrices) {
			addBundlePriceTierMinQuantitiesBasedOnConstituent(tierMins, pair.getFirst().getQuantity(), pair.getSecond());
		}
		return tierMins;
	}

	/**
	 * Adds new tiers (if applicable) to the price tiers of the bundle, based on the tiers of the constituent.
	 *
	 * For each bundle item, we want to figure out from its price tiers and bundle item quantity, the quantities of the owning bundle for which
	 * the bundle item price tiers will be applicable.
	 *
	 * The mathematical approach for this is to determine the ceiling of the ratio of constituent item quantity to bundle quantity.
	 *
	 *
	 * @param tiersQuantities set of minimum quantities for tiers
	 * @param constituentQuantity the quantity of this constituent
	 * @param constituentPrice the price of this constituent
	 */
	protected void addBundlePriceTierMinQuantitiesBasedOnConstituent(final Set<Integer> tiersQuantities, final Integer constituentQuantity,
			final SimplePrice constituentPrice) {
		for (Integer tierMin : constituentPrice.getPriceTiersMinQuantities()) {
			Integer ceilingOfItemTierQuantityToBundleConstituentQuantity = getBundleTierFromConstituentTierAndQuantity(tierMin, constituentQuantity);
			tiersQuantities.add(ceilingOfItemTierQuantityToBundleConstituentQuantity);
		}
	}

	private int getBundleTierFromConstituentTierAndQuantity(final int tierMin, final int quantity) {
		return (tierMin + quantity - 1) / quantity;
	}

	private boolean isRedundant(final PriceTier prevTier, final PriceTier newTier) {
		if (prevTier == null) {
			return false;
		}

		return ObjectUtils.equals(prevTier.getListPrice(), newTier.getListPrice())
			&& ObjectUtils.equals(prevTier.getSalePrice(), newTier.getSalePrice())
			&& ObjectUtils.equals(prevTier.getComputedPrice(), newTier.getComputedPrice());
	}


	private Price createZeroPrice() {
		Price price = getBeanFactory().getBean(ContextIdNames.PRICE);
		Money zero = MoneyFactory.createMoney(BigDecimal.ZERO, getPriceProvider().getCurrency());
		price.setListPrice(zero);
		return price;
	}

	public ProductBundle getBundle() {
		return bundle;
	}


	/**
	 * @return the bean factory
	 */
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @return the price provider
	 */
	protected PriceProvider getPriceProvider() {
		return priceProvider;
	}
}
