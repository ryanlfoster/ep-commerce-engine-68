/**
 * 
 */
package com.elasticpath.service.shoppingcart.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;
import com.elasticpath.service.tax.impl.ApportioningCalculator;

/**
 * Creates new {@link OrderSku} objects from ordinary {@link ShoppingItem}s, doing nothing with any associated data.
 */
public class OrderSkuFactoryImpl implements OrderSkuFactory {
	private BeanFactory beanFactory;

	private Locale locale = null;

	private ItemPricingSplitter pricingSpliter;

	private BundleApportioningCalculator bundleApportioner;

	private ApportioningCalculator discountApportioner;

	@Override
	public Collection<OrderSku> createOrderSkus(final Collection<ShoppingItem> rootItems, final Locale locale) {
		this.locale = locale;

		Map<String, ItemPricing> rootPricingMap = extractRootPricing(rootItems);

		Map<String, Map<String, ItemPricing>> bundleApportionedPriceMap = getBundleApportionedPriceMap(rootItems, rootPricingMap);

		Map<String, Map<String, List<ItemPricing>>> bundleQuantityApportionedPriceMap = splitByQuantity(bundleApportionedPriceMap);

		applyApportionedDiscount(bundleQuantityApportionedPriceMap, extractDiscount(rootPricingMap));

		return createOrderSkusWithApportionedPrices(rootItems, extractAllLeavesItemPricings(bundleQuantityApportionedPriceMap));
	}

	private Map<String, Map<String, ItemPricing>> getBundleApportionedPriceMap(final Collection<ShoppingItem> rootItems,
			final Map<String, ItemPricing> rootPricingMap) {
		// To make apportioning consistent for the cases of 1 bundle and multiple bundles,
		// we apportion price for a single bundle and later multiply the result
		// by the root bundle quantity.
		// see call to multiplyConstituentPricesByRootQuantities below.
		divideRootPricesByRootQuantities(rootPricingMap);

		Map<String, Map<String, ItemPricing>> constituentPricingMap = extractConstituentPricingInOrder(rootItems);

		Map<String, Map<String, ItemPricing>> pricingMap = getApportionedPriceMap(rootPricingMap, constituentPricingMap);

		// see above the comment to divideRootPricesByRootQuantities
		multiplyConstituentPricesByRootQuantities(pricingMap, rootPricingMap);
		return pricingMap;
	}

	private Map<String, List<ItemPricing>> extractAllLeavesItemPricings(final Map<String, Map<String, List<ItemPricing>>> splitPricingMap) {
		Map<String, List<ItemPricing>> result = new HashMap<String, List<ItemPricing>>();
		for (Map<String, List<ItemPricing>> leavesItemPricings : splitPricingMap.values()) {
			result.putAll(leavesItemPricings);
		}

		return result;
	}

	/**
	 * Applies discount on the price map.
	 * 
	 * @param pricingMap the price map that needs to be applied discount to
	 * @param discountMap the discount map.
	 */
	protected void applyApportionedDiscount(final Map<String, Map<String, List<ItemPricing>>> pricingMap, final Map<String, BigDecimal> discountMap) {
		for (String rootGuid : pricingMap.keySet()) {
			Map<String, List<ItemPricing>> splitChildPricing = pricingMap.get(rootGuid);

			Map<String, BigDecimal> allSplitPricing = getAllSplitPricing(splitChildPricing);
			allSplitPricing = sortByAmount(allSplitPricing);
			BigDecimal rootDiscount = discountMap.get(rootGuid);

			Map<String, BigDecimal> allSplitDiscount = getDiscountApportioner().calculateApportionedAmounts(rootDiscount, allSplitPricing);
			setApportionedDiscount(splitChildPricing, allSplitDiscount);
		}
	}

	/**
	 * Sorts the price map by amount.
	 * 
	 * @param allSplitPricing the split pricing
	 * @return a map of sorted price map.
	 */
	protected Map<String, BigDecimal> sortByAmount(final Map<String, BigDecimal> allSplitPricing) {
		List<Entry<String, BigDecimal>> sortedPairs = new ArrayList<Entry<String, BigDecimal>>();
		for (Entry<String, BigDecimal> entry : allSplitPricing.entrySet()) {
			sortedPairs.add(entry);
		}
		Collections.sort(sortedPairs, new Comparator<Entry<String, BigDecimal>>() {
			public int compare(final Entry<String, BigDecimal> entry1, final Entry<String, BigDecimal> entry2) {
				return entry2.getValue().compareTo(entry1.getValue());
			}
		});

		Map<String, BigDecimal> sortedMap = new LinkedHashMap<String, BigDecimal>();
		for (Entry<String, BigDecimal> entry : sortedPairs) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	/**
	 * Sets the apportioned discount on the item pricing.
	 * 
	 * @param itemPricings item pricing map.
	 * @param apportionedDiscountMap apportioned discount map.
	 */
	protected void setApportionedDiscount(final Map<String, List<ItemPricing>> itemPricings, final Map<String, BigDecimal> apportionedDiscountMap) {
		for (Entry<String, List<ItemPricing>> pricingEntry : itemPricings.entrySet()) {
			String pricingKey = pricingEntry.getKey();
			List<ItemPricing> leavesPricing = pricingEntry.getValue();
			for (Entry<String, BigDecimal> discountEntry : apportionedDiscountMap.entrySet()) {
				String discountKey = discountEntry.getKey();
				if (discountKey.startsWith(pricingKey)) {
					int itemPricingIndex = Integer.valueOf(discountKey.substring(pricingKey.length(), discountKey.length()));
					ItemPricing itemPricing = leavesPricing.get(itemPricingIndex);
					pricingEntry.getValue().set(itemPricingIndex,
							new ItemPricing(itemPricing.getPrice(), discountEntry.getValue(), itemPricing.getQuantity()));
				}
			}
		}
	}

	private Map<String, BigDecimal> getAllSplitPricing(final Map<String, List<ItemPricing>> splitChildPricing) {
		Map<String, BigDecimal> pricings = new HashMap<String, BigDecimal>();
		for (Entry<String, List<ItemPricing>> entry : splitChildPricing.entrySet()) {
			int childIndex = 0;
			for (ItemPricing pricing : entry.getValue()) {
				pricings.put(entry.getKey() + childIndex, pricing.getPrice().multiply(BigDecimal.valueOf(pricing.getQuantity())));
				childIndex++;
			}
		}

		return pricings;
	}

	private void multiplyConstituentPricesByRootQuantities(final Map<String, Map<String, ItemPricing>> pricingMap,
			final Map<String, ItemPricing> rootPricingMap) {
		for (String rootGuid : pricingMap.keySet()) {
			BigDecimal quantity = BigDecimal.valueOf(rootPricingMap.get(rootGuid).getQuantity());

			Map<String, ItemPricing> childPricingMap = pricingMap.get(rootGuid);
			for (Entry<String, ItemPricing> entry : childPricingMap.entrySet()) {
				BigDecimal singleBundlePrice = entry.getValue().getPrice();
				BigDecimal price = singleBundlePrice.multiply(quantity);
				entry.setValue(new ItemPricing(price, entry.getValue().getDiscount(), entry.getValue().getQuantity()));
			}
		}

	}

	private void divideRootPricesByRootQuantities(final Map<String, ItemPricing> rootPricingMap) {
		for (Entry<String, ItemPricing> pricing : rootPricingMap.entrySet()) {
			BigDecimal discount = pricing.getValue().getDiscount();
			final int quantity = pricing.getValue().getQuantity();
			final int calcScale = 10;
			BigDecimal decimalQuantity = BigDecimal.valueOf(quantity);
			BigDecimal multipleBundlePrice = pricing.getValue().getPrice();
			BigDecimal price = multipleBundlePrice.divide(decimalQuantity, calcScale, RoundingMode.HALF_UP);
			pricing.setValue(new ItemPricing(price, discount, quantity));
		}
	}

	/**
	 * @param cartItem the cart item from which the data should be copied
	 * @param orderSku the order sku into which the data should be copied
	 */
	protected void copyData(final ShoppingItem cartItem, final OrderSku orderSku) {
		Map<String, String> itemData = cartItem.getFields();
		for (String key : itemData.keySet()) {
			orderSku.setFieldValue(key, itemData.get(key));
		}
	}

	/**
	 * @param shoppingItem {@code ShoppingItem}
	 * @param orderSku {@code OrderSku}
	 */
	protected void copyFields(final ShoppingItem shoppingItem, final OrderSku orderSku) {
		final ProductSku productSku = shoppingItem.getProductSku();
		final Product product = productSku.getProduct();

		final Date now = new Date();
		orderSku.setCreatedDate(now);
		orderSku.setProductSku(productSku);
		orderSku.setSkuCode(productSku.getSkuCode());
		orderSku.setDigitalAsset(productSku.getDigitalAsset());
		orderSku.setTaxCode(product.getTaxCode().getCode());
		orderSku.setDisplaySkuOptions(getSkuOptionsDisplayString(shoppingItem));
		orderSku.setDisplayName(product.getDisplayName(locale));
		if (productSku.getImage() != null) {
			orderSku.setImage(productSku.getImage());
		}

		orderSku.setOrdering(shoppingItem.getOrdering());
	}

	/**
	 * @param orderSku {@link OrderSku}.
	 * @param pricing {@link ItemPricing}.
	 */
	protected void setItemPricing(final OrderSku orderSku, final ItemPricing pricing) {
		orderSku.setQuantity(pricing.getQuantity());
		orderSku.setUnitPrice(pricing.getPrice());
		orderSku.setDiscountBigDecimal(pricing.getDiscount());
	}

	/**
	 * @param item {@link ShoppingItem}.
	 * @param orderSku {@link OrderSku}.
	 */
	protected void copyPrices(final ShoppingItem item, final OrderSku orderSku) {
		// need null checks below, as nested bundles need not be priced to purchase the root bundle
		orderSku.setPrice(item.getQuantity(), item.getPrice());
		if (item.getLowestUnitPrice() != null) {
			orderSku.setUnitPrice(item.getLowestUnitPrice().getAmountUnscaled());
		}
		if (item.getTax() != null) {
			orderSku.setTax(item.getTax().getAmount());
		}
		orderSku.setDiscountBigDecimal(item.getDiscount().getAmountUnscaled());
	}

	private void createDependants(final ShoppingItem parentItem, final OrderSku parentSku, final Map<String, List<ItemPricing>> leavesItemPricings) {
		Collection<OrderSku> childrenSku = createOrderSkusWithApportionedPrices(parentItem.getBundleItems(), leavesItemPricings);
		for (OrderSku childSku : childrenSku) {
			parentSku.addChildItem(childSku);
		}
	}

	private OrderSku createOrderSkuWithApportionedPrices(final ShoppingItem item, final ItemPricing apportionedPricing) {
		final OrderSku orderSku = createSimpleOrderSku();
		copyFields(item, orderSku);
		copyData(item, orderSku);
		copyPrices(item, orderSku);

		if (apportionedPricing != null) {
			setItemPricing(orderSku, apportionedPricing);
		}

		return orderSku;
	}

	/**
	 * Creates order skus and set the unit price/discount/tax according to the {@link ItemPricing} in the price map. If the {@link ItemPricing} 
	 * doesn't exist in the price map, use the {@link ItemPricing} from the {@link ShoppingItem}.
	 * 
	 * @param shoppingItems a collection of {@link ShoppingItem}.
	 * @param leavesPricingsMap a price map.
	 * @return a collection of {@link OrderSku}.
	 */
	protected Collection<OrderSku> createOrderSkusWithApportionedPrices(final Collection<ShoppingItem> shoppingItems,
			final Map<String, List<ItemPricing>> leavesPricingsMap) {
		List<OrderSku> orderSkus = new ArrayList<OrderSku>();
		for (ShoppingItem shoppingItem : shoppingItems) {
			OrderSku parentOrderSku = null;

			List<ItemPricing> leavesPricings = leavesPricingsMap.get(shoppingItem.getGuid());
			if (leavesPricings == null) {
				parentOrderSku = createOrderSkuWithApportionedPrices(shoppingItem, null);
				orderSkus.add(parentOrderSku);
			} else {
				for (ItemPricing leafPricing : leavesPricings) {
					orderSkus.add(createOrderSkuWithApportionedPrices(shoppingItem, leafPricing));
				}
			}

			if (parentOrderSku != null) {
				createDependants(shoppingItem, parentOrderSku, leavesPricingsMap);
			}
		}

		return orderSkus;
	}

	/**
	 * Creates an {@link OrderSku}.
	 * 
	 * @return an {@link OrderSku}.
	 */
	protected OrderSku createSimpleOrderSku() {
		return beanFactory.getBean(ContextIdNames.ORDER_SKU);
	}

	private List<ShoppingItem> createSortedConstituentShoppingItems(final ShoppingItem root) {
		List<ShoppingItem> sortedItems = new ArrayList<ShoppingItem>();
		if (root.isBundle()) {
			populateConstituents(sortedItems, root.getBundleItems());
		}
		Collections.sort(sortedItems, new Comparator<ShoppingItem>() {
			public int compare(final ShoppingItem item1, final ShoppingItem item2) {
				int result = item1.getLinePricing().getPrice().compareTo(item2.getLinePricing().getPrice());
				if (result == 0) {
					result = item1.getProductSku().getSkuCode().compareTo(item2.getProductSku().getSkuCode());
				}

				return -result;
			}
		});
		return sortedItems;
	}

	/**
	 * Extracts the pricing of the constituent/leaf {@link ShoppingItem} from a collection of root {@link ShoppingItem}s. The pricing is sorted by
	 * price and skucode.
	 * 
	 * @param rootItems a collection of root {@link ShoppingItem}.
	 * @return a map of pricing with root {@link ShoppingItem} guid as key and pricing as value.
	 */
	protected Map<String, Map<String, ItemPricing>> extractConstituentPricingInOrder(final Collection<ShoppingItem> rootItems) {
		Map<String, Map<String, ItemPricing>> pricingMap = new HashMap<String, Map<String, ItemPricing>>();
		for (final ShoppingItem root : rootItems) {
			List<ShoppingItem> sortedItems = createSortedConstituentShoppingItems(root);

			Map<String, ItemPricing> childPricing = new LinkedHashMap<String, ItemPricing>();
			pricingMap.put(root.getGuid(), childPricing);
			populateItemPricing(childPricing, sortedItems);
		}

		return pricingMap;
	}

	/**
	 * Extracts the pricing of a collection of {@link ShoppingItem}.
	 * 
	 * @param shoppingItems a collection of {@link ShoppingItem}.
	 * @return the price map.
	 */
	protected Map<String, ItemPricing> extractRootPricing(final Collection<ShoppingItem> shoppingItems) {
		Map<String, ItemPricing> pricing = new HashMap<String, ItemPricing>();
		for (ShoppingItem item : shoppingItems) {
			pricing.put(item.getGuid(), item.getLinePricing());
		}
		return pricing;
	}

	private BundleApportioningCalculator getBundleApportioner() {
		if (bundleApportioner == null) {
			bundleApportioner = new BundleApportioningCalculator();
		}

		return bundleApportioner;
	}

	private ApportioningCalculator getDiscountApportioner() {
		if (discountApportioner == null) {
			discountApportioner = new ApportioningCalculator();
		}

		return discountApportioner;
	}

	private Map<String, Map<String, ItemPricing>> getApportionedPriceMap(final Map<String, ItemPricing> rootPricingMap,
			final Map<String, Map<String, ItemPricing>> constituentPricingMap) {
		Map<String, Map<String, ItemPricing>> result = new HashMap<String, Map<String, ItemPricing>>();
		for (String rootGuid : rootPricingMap.keySet()) {
			ItemPricing pricingToApportion = rootPricingMap.get(rootGuid);
			Map<String, ItemPricing> constituents = constituentPricingMap.get(rootGuid);
			result.put(rootGuid, getBundleApportioner().apportion(pricingToApportion, constituents));
		}

		return result;
	}

	/**
	 * Gets the {@link ItemPricingSplitter} singleton.
	 * 
	 * @return {@link ItemPricingSplitter}.
	 */
	protected ItemPricingSplitter getPricingSplitter() {
		if (pricingSpliter == null) {
			pricingSpliter = new ItemPricingSplitter();
		}

		return pricingSpliter;
	}

	/**
	 * Generates the string representation of the sku option values on a cart item.
	 * 
	 * @param shoppingItem the cart item
	 * @return the generated string
	 */
	private String getSkuOptionsDisplayString(final ShoppingItem shoppingItem) {
		final StringBuffer skuOptionValues = new StringBuffer();
		Collection<SkuOptionValue> optionValues = shoppingItem.getProductSku().getOptionValues();
		if (!optionValues.isEmpty()) {

			for (final Iterator<SkuOptionValue> optionValueIter = optionValues.iterator(); optionValueIter.hasNext();) {
				final SkuOptionValue currOptionValue = optionValueIter.next();
				skuOptionValues.append(currOptionValue.getDisplayName(locale, true));
				if (optionValueIter.hasNext()) {
					skuOptionValues.append(", ");
				}
			}
		}
		return skuOptionValues.toString();
	}

	/**
	 * Populates constituents (the leaves).
	 * 
	 * @param container the result container.
	 * @param items a collection of {@link ShoppingItem}.
	 */
	protected void populateConstituents(final List<ShoppingItem> container, final Collection<ShoppingItem> items) {
		for (ShoppingItem item : items) {
			if (item.isBundle()) {
				populateConstituents(container, item.getBundleItems());
			} else {
				container.add(item);
			}
		}
	}

	private void populateItemPricing(final Map<String, ItemPricing> childPricing, final Collection<ShoppingItem> items) {
		for (ShoppingItem item : items) {
			childPricing.put(item.getGuid(), item.getLinePricing());
		}
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Splits an {@link OrderSku} by its quantity.
	 * 
	 * @param orderSku {@link OrderSku}.
	 * @return a collection of split {@link OrderSku}.
	 */
	protected Collection<OrderSku> splitOrderSku(final OrderSku orderSku) {
		List<OrderSku> splittedOrderSkus = new ArrayList<OrderSku>();

		int quantity = orderSku.getQuantity();
		// orderSku.getUnitPrice is holding the apportioned line total (unit * quantity) to avoid rounding, until we properly
		// factor in the rounding of the unit price against quantity below by splitting into multiple line items.
		ItemPricing itemPricing = new ItemPricing(orderSku.getUnitPrice(), // .multiply(BigDecimal.valueOf(quantity)),
				orderSku.getDiscount().getAmount(), quantity);
		Collection<ItemPricing> splittedPricings = getPricingSplitter().split(itemPricing);
		for (ItemPricing splittedPricing : splittedPricings) {
			OrderSku splittedOrderSku = createOrderSkuWithApportionedPrices(orderSku, splittedPricing);
			splittedOrderSkus.add(splittedOrderSku);
		}

		return splittedOrderSkus;
	}

	/**
	 * Splits the prices by quantity using the {@link ItemPricingSplitter}.
	 * 
	 * @param pricingMap the pricing map.
	 * @return a split pricing map.
	 */
	protected Map<String, Map<String, List<ItemPricing>>> splitByQuantity(final Map<String, Map<String, ItemPricing>> pricingMap) {
		Map<String, Map<String, List<ItemPricing>>> result = new HashMap<String, Map<String, List<ItemPricing>>>();

		for (String rootGuid : pricingMap.keySet()) {
			Map<String, List<ItemPricing>> splitChildPricingMap = new HashMap<String, List<ItemPricing>>();
			Map<String, ItemPricing> childPricingMap = pricingMap.get(rootGuid);
			for (Entry<String, ItemPricing> entry : childPricingMap.entrySet()) {
				Collection<ItemPricing> splitPricings = getPricingSplitter().split(entry.getValue());
				splitChildPricingMap.put(entry.getKey(), new ArrayList<ItemPricing>(splitPricings));
			}

			result.put(rootGuid, splitChildPricingMap);
		}

		return result;
	}

	/**
	 * Extracts discount {@link BigDecimal} from a price map.
	 * 
	 * @param pricingMap a price map.
	 * @return the discount {@link BigDecimal} map.
	 */
	protected Map<String, BigDecimal> extractDiscount(final Map<String, ItemPricing> pricingMap) {
		Map<String, BigDecimal> discountMap = new HashMap<String, BigDecimal>();
		for (Entry<String, ItemPricing> entry : pricingMap.entrySet()) {
			discountMap.put(entry.getKey(), entry.getValue().getDiscount());
		}

		return discountMap;
	}
}
