/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.ajax.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.MoneyFormatter;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.store.Store;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.sellingchannel.inventory.ProductInventoryShoppingService;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.MultiSkuProductConfigurationService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.rules.EpRuleEngine;
import com.elasticpath.sfweb.ajax.bean.GuidedSkuSelectionBean;
import com.elasticpath.sfweb.ajax.bean.PriceTierBean;
import com.elasticpath.sfweb.ajax.service.SkuConfigurationService;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Provides services relating to Sku Configuration.
 */
public class SkuConfigurationServiceImpl extends AbstractEpServiceImpl implements SkuConfigurationService {

	private static final String MESSAGE_SOURCE_PREFIX = "productTemplate.recurringPrice.";

	private static final String SPACE = " ";
	
	private StoreProductService storeProductService;

	private StoreProductLoadTuner storeProductLoadTuner;

	private StoreConfig storeConfig;

	private EpRuleEngine epRuleEngine;

	private SfRequestHelper requestHelper;

	private PriceLookupFacade priceLookupFacade;

	private ProductInventoryShoppingService productInventoryShoppingService;

	private MessageSource messageSource;
	
	private PriceBuilder priceBuilder;

	private MoneyFormatter moneyFormatter;
	
	private MultiSkuProductConfigurationService multiSkuProductConfigurationService;

	/**
	 * Returns the set of SkuOptions with option values for which there are skus available given the selected when the primary option value.<br>
	 * The product's first option is considered the "primary" option and all of its option values are always returned.
	 * 
	 * @param productUid the UID of the product whose options are being selected
	 * @param selectedOptionValueCodes the option value codes that have already been selected
	 * @return A set of SkuOptions with each having the available SkuOptionValues in them
	 */
	public Set<SkuOption> getAvailableOptionValues(final long productUid, final List<String> selectedOptionValueCodes) {
		final StoreProduct product = storeProductService.getProductForStore(productUid, getStore(), storeProductLoadTuner);

		List<SkuOption> sortedSkuOptions = product.getProductType().getSortedSkuOptionListForRecurringItems(product.getDefaultSku());
		
		Set<SkuOption> result = new HashSet<SkuOption>();
		Collection<SkuOptionValue> selectedOptions = new ArrayList<SkuOptionValue>(selectedOptionValueCodes.size()); 
		
		for (SkuOption skuOption : sortedSkuOptions) {
			Collection<SkuOptionValue> filteredOptionValues = getMultiSkuProductConfigurationService().getAvailableOptionValuesForOption(product, 
					skuOption.getOptionKey(), selectedOptions);
			SkuOption clonedSkuOption = cloneSkuOption(skuOption);
			clonedSkuOption.setOptionValues(new HashSet<SkuOptionValue>(filteredOptionValues));
			result.add(clonedSkuOption);
			
			selectedOptions.add(findOptionValueForOption(skuOption, selectedOptionValueCodes));
		}
		
		return result;
	}


	private SkuOptionValue findOptionValueForOption(final SkuOption skuOption, final List<String> selectedOptionValueKeys) {
		for (String optionValueCode : selectedOptionValueKeys) {
			SkuOptionValue optionValue = skuOption.getOptionValue(optionValueCode);
			if (optionValue != null) {
				return optionValue;
			}
		}
		return null;
	}


	private SkuOption cloneSkuOption(final SkuOption skuOption) {
		try {
			return (SkuOption) skuOption.clone();
		} catch (CloneNotSupportedException e) {
			throw new EpSystemException("cannot clone SkuOption", e);
		}
	}
	
	
	/**
	 * Gets a SKU matches the specific option value codes. <br>
	 * Also returns display information required by the client including the SKU price and the URL to the SKU's image.
	 * 
	 * @param productUid the SKU's product UID
	 * @param optionValues a list of option value codes the SKU must have
	 * @param currencyCode the code for the currency
	 * @param quantity The quantity of selected sku.
	 * @param request {@link HttpServletRequest}
	 * @return a bean containing the selected SKU and other display information.
	 */
	public GuidedSkuSelectionBean getSkuWithMatchingOptionValuesAndQuantity(final long productUid, final List<String> optionValues,
			final String currencyCode, final int quantity, final HttpServletRequest request) {
		final StoreProduct storeProduct = storeProductService.getProductForStore(productUid, getStore(), storeProductLoadTuner);
		final List<String> optionValueCodes = new ArrayList<String>(optionValues);

		final CustomerSession customerSession = requestHelper.getCustomerSession(request);

		// Try matching all option value codes.
		long skuUid = getMostAppropriateSkuUidByOptionValues(storeProduct, optionValueCodes);

		final GuidedSkuSelectionBean skuSelectionBean = getBean(ContextIdNames.SKU_SELECTION_BEAN);

		if (skuUid != 0) {
			StoreProduct product = storeProductService.getProductForStore(productUid, skuUid, getStore(), storeProductLoadTuner);
			final ProductSku selectedSku = getSkuInProduct(skuUid, product);
			skuSelectionBean.setProductSku(selectedSku);
			skuSelectionBean.setImageUrl(selectedSku.getImage());

			final ShoppingCart shoppingCart = customerSession.getShoppingCart();
			addPriceInfoToBean(shoppingCart, skuSelectionBean, selectedSku, quantity);

			InventoryKey key = new InventoryKey(selectedSku.getSkuCode(), getStore().getWarehouse().getUidPk());
			InventoryDto inventoryDto = getProductInventoryShoppingService().getInventory(key);
			skuSelectionBean.setInventory(inventoryDto);
			skuSelectionBean.setAvailabilityCode(product.getMessageCode(skuUid).toString());
			skuSelectionBean.setInfiniteQuantity(product.getAvailabilityCriteria() == AvailabilityCriteria.ALWAYS_AVAILABLE);
			skuSelectionBean.setAvailable(product.isSkuAvailable(selectedSku.getSkuCode()));
			skuSelectionBean.setPurchasable(product.isPurchasable());
			skuSelectionBean.setMinOrderQty(product.getMinOrderQty());
		}

		return skuSelectionBean;
	}

	private long getMostAppropriateSkuUidByOptionValues(final StoreProduct storeProduct, final List<String> optionValueCodes) {
		final List<String> filteredOptionValueCodes = new ArrayList<String>(optionValueCodes);

		// Try matching all option value codes.
		long skuUid = getFirstSkuUidByOptionValues(storeProduct, filteredOptionValueCodes);

		while (skuUid == 0 && !filteredOptionValueCodes.isEmpty()) {
			filteredOptionValueCodes.remove(filteredOptionValueCodes.size() - 1);
			skuUid = getFirstSkuUidByOptionValues(storeProduct, filteredOptionValueCodes);
		}
		
		return skuUid;
	}

	
	private Collection<SkuOptionValue> getOptionValuesForValueCodes(final StoreProduct storeProduct, final List<String> optionValueCodes) {
		Collection<SkuOptionValue> optionValues = new ArrayList<SkuOptionValue>(optionValueCodes.size());
		for (SkuOption skuOption : storeProduct.getProductType().getSortedSkuOptionList(storeProduct.getDefaultSku())) {
			SkuOptionValue sov = findOptionValueForOption(skuOption, optionValueCodes);
			if (sov != null) {
				optionValues.add(sov);
			}
		}
		return optionValues;
	}
	
	private long getFirstSkuUidByOptionValues(final StoreProduct storeProduct, final List<String> optionValueCodes) {
		Collection<SkuOptionValue> optionValues = getOptionValuesForValueCodes(storeProduct, optionValueCodes);
		Collection<Long> uids = getMultiSkuProductConfigurationService().findSkuUidsMatchingSelectedOptions(storeProduct, optionValues);
		if (uids.isEmpty()) {
			return 0;
		}
		return uids.iterator().next();
	}
	
	
	/**
	 * Updates the {@code skuSelectionBean} with the pricing info for the selected sku.
	 * 
	 * @param cart {@link ShoppingCart}
	 * @param skuSelectionBean The sku selection bean to update.
	 * @param selectedSku The selected sku.
	 * @param quantity The quantity of selected sku.
	 */
	void addPriceInfoToBean(final ShoppingCart cart, final GuidedSkuSelectionBean skuSelectionBean, 
			final ProductSku selectedSku, final int quantity) {
		
		Price skuPrice = getSkuPrice(selectedSku, cart);
		if (skuPrice == null) { 
			skuSelectionBean.setPriceTierContents(new String[0]);
			skuSelectionBean.setPriceTiers(Collections.<PriceTierBean>emptyList());
			return;
		}

		//now need to check for recurring prices
		Collection<PriceSchedule> priceSchedules = skuPrice.getPricingScheme().getRecurringSchedules();
		String scheduleText = "";
		if (!priceSchedules.isEmpty()) {
			PriceSchedule schedule = priceSchedules.iterator().next(); 
			String scheduleName = schedule.getPaymentSchedule().getName();
			scheduleText = SPACE + (messageSource.getMessage(MESSAGE_SOURCE_PREFIX + scheduleName, null, scheduleName, cart.getLocale()));
			
			//also now replace skuPrice with the recurringSchedule price
			skuPrice = (Price) skuPrice.getPricingScheme().getSimplePriceForSchedule(schedule);		
		}
		
		int effectiveQty = getPriceBuilder().getEffectiveQuantity(skuPrice, quantity);
		final Money lowestPrice = skuPrice.getLowestPrice(effectiveQty);
		if (lowestPrice != null) {
			skuSelectionBean.setLowestPrice(getMoneyFormatter().formatCurrency(lowestPrice, cart.getLocale()) + scheduleText);
		}
		final Money listPrice = skuPrice.getListPrice(effectiveQty);
		if (listPrice != null) {
			skuSelectionBean.setListPrice(getMoneyFormatter().formatCurrency(listPrice, cart.getLocale()) + scheduleText);
		}
		final Money dollarSavings = skuPrice.getDollarSavings(effectiveQty);
		if (dollarSavings != null) {
			skuSelectionBean.setDollarSavings(getMoneyFormatter().formatCurrency(dollarSavings, cart.getLocale()) + scheduleText);
		}
		
		skuSelectionBean.setLowestLessThanList(skuPrice.isLowestLessThanList(effectiveQty));
		
		configurePriceTiers(skuSelectionBean, skuPrice, scheduleText, cart.getLocale());
	}	

	/**
	 * Run through rule engine.
	 * 
	 * @param sku sku
	 * @param skuPrice price to modify
	 * @param currency the storefront is in
	 */
	protected void runThroughRuleEngine(final ProductSku sku, final Price skuPrice, final Currency currency) {
		String productCode = sku.getProduct().getCode();
		Map<String, List<Price>> promoPriceHolder = Collections.singletonMap(productCode, Arrays.asList(skuPrice));
		epRuleEngine.fireCatalogPromotionRules(Arrays.asList(sku.getProduct()), new HashSet<Long>(), currency, getStore(), promoPriceHolder);
	}

	/**
	 * Get the productSku with the given UID. <br>
	 * Return null if no matching record exists.
	 * 
	 * @param productSkuUid the ProductSku UID.
	 * @param product the Product
	 * @return the productSku if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public ProductSku getSkuInProduct(final long productSkuUid, final Product product) throws EpServiceException {
		for (ProductSku currSku : product.getProductSkus().values()) {
			if (productSkuUid == currSku.getUidPk()) {
				return currSku;
			}
		}

		throw new EpServiceException("Can't find sku " + productSkuUid + " in product " + product.getCode());
	}

	/**
	 * Sets the rule engine.
	 * 
	 * @param epRuleEngine the rule engine
	 */
	public void setEpRuleEngine(final EpRuleEngine epRuleEngine) {
		this.epRuleEngine = epRuleEngine;
	}

	/**
	 * Set a reference to the store product service.
	 * 
	 * @param storeProductService the store product service
	 */
	public void setStoreProductService(final StoreProductService storeProductService) {
		this.storeProductService = storeProductService;
	}

	/**
	 * Set a reference to the product sku service.
	 * 
	 * @param productSku the product sku
	 * @param cart {@link ShoppingCart}
	 * @return the String Array that contains the price tier information. Some examples of the format are: <br>
	 *         "1 - 5 @ $10.99" <br>
	 *         "6 + @ $8.99" <br>
	 */
	public String[] getPriceTierContents(final ProductSku productSku, final ShoppingCart cart) {
		final Price price = getSkuPrice(productSku, cart);
		if (price == null) {
			String emptyTier = "0 @ 0.00"; // [BB-1171] this is passed to the DWR layer
			// to prevent validation failures in case of no price for sku is configured
			return new String[] { emptyTier };
		}
		final Collection<PriceTier> priceTiers = price.getPriceTiers().values();
		final String[] priceTierContents = new String[priceTiers.size()];

		int index = 0;
		int nextTierMin = 0;
		for (final PriceTier priceTier : priceTiers) {
			nextTierMin = getNextTierMin(priceTier.getMinQty(), price);
			final StringBuffer strBuf = new StringBuffer();
			if (nextTierMin == 0) {
				strBuf.append(priceTier.getMinQty()).append(" +  @ ").append(
						getMoneyFormatter().formatCurrency(price.getLowestPrice(priceTier.getMinQty()), cart.getLocale()));
			} else {
				strBuf.append(priceTier.getMinQty()).append(" - ").append(nextTierMin - 1).append(" @ ").append(// NOPMD
						getMoneyFormatter().formatCurrency(price.getLowestPrice(priceTier.getMinQty()), cart.getLocale()));
			}
			priceTierContents[index] = strBuf.toString();
			index++;
		}
		return priceTierContents;
	}

	/**
	 * Get the minimum quantity of the next price tier.
	 * 
	 * @param curMinQty the minimum quantity for current price tier
	 * @param price the Sku price
	 * @return the minimum quantity of the next price tier
	 */
	protected int getNextTierMin(final int curMinQty, final Price price) {
		int nextTierMin = 0;
		for (final PriceTier priceTier : price.getPriceTiers().values()) { // the priceTiers should be sorted by JPA
			if (priceTier.getMinQty() > curMinQty) {
				nextTierMin = priceTier.getMinQty();
				break;
			}
		}
		return nextTierMin;
	}

	/**
	 * Set the <code>StoreProductLoadTuner</code> for retrieving multi-sku products.
	 * 
	 * @param storeProductLoadTuner the <code>StoreProductLoadTuner</code>
	 */
	public void setProductLoadTuner(final StoreProductLoadTuner storeProductLoadTuner) {
		this.storeProductLoadTuner = storeProductLoadTuner;
	}

	/**
	 * Sets the store configuration that provides context for this service.
	 * 
	 * @param storeConfig the store configuration.
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}

	/**
	 * @param selectedSku {@link ProductSku}
	 * @param cart {@link ShoppingCart}
	 * @return price found
	 */
	protected Price getSkuPrice(final ProductSku selectedSku, final ShoppingCart cart) {
		return priceLookupFacade.getPromotedPriceForSku(selectedSku, getStore(), cart.getShopper(), cart.getAppliedRules());
	}

	/**
	 * @return store in use
	 */
	protected Store getStore() {
		return storeConfig.getStore();
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
	 * Set the {@link ProductInventoryShoppingService} to be used for retrieving inventory.
	 * 
	 * @param productInventoryShoppingService the service implementation.
	 */
	public void setProductInventoryShoppingService(final ProductInventoryShoppingService productInventoryShoppingService) {
		this.productInventoryShoppingService = productInventoryShoppingService;
	}

	/**
	 * Get the {@link ProductInventoryShoppingService} for inventory retrieval.
	 * 
	 * @return the inventoryService
	 */
	public ProductInventoryShoppingService getProductInventoryShoppingService() {
		return productInventoryShoppingService;
	}

	@Override
	public GuidedSkuSelectionBean getSkuWithMatchingOptionValues(final long productUid, final List<String> optionValueCodes,
			final String currencyCode, final HttpServletRequest request) {
		return this.getSkuWithMatchingOptionValuesAndQuantity(productUid, optionValueCodes, currencyCode, 1, request);
	}

	private void configurePriceTiers(final GuidedSkuSelectionBean skuSelectionBean, final Price skuPrice, 
			final String scheduleText, final Locale locale) {
		if (skuPrice == null) {
			String emptyTier = "0 @ 0.00"; //[BB-1171]this is passed to the DWR layer 
															//to prevent validation failures in case of no price for sku is configured
			skuSelectionBean.setPriceTierContents(new String[] {emptyTier});
			return;
		}
		
		List<PriceTierBean> priceTierBeans = getPriceBuilder().getPriceTiers(skuPrice);
		final String[] priceTierContents = new String[priceTierBeans.size()];
		int nextTierMin = 0;
		for (int index = 0; index < priceTierBeans.size(); index++) {
			PriceTierBean priceTierBean = priceTierBeans.get(index);
			final StringBuffer strBuf = new StringBuffer();
			String cssClassPrice = "reg-price";
			if (skuPrice.isLowestLessThanList(priceTierBean.getMinQty())) { 
				cssClassPrice = "sale-price";
			}
			
			Money priceMoney = MoneyFactory.createMoney(priceTierBean.getPrice(), skuPrice.getCurrency());
			
			strBuf.append("<span class=\"tier-level\">")
			.append(priceTierBean.getMinQty());
			if (index == priceTierBeans.size() - 1) {
				strBuf.append(" + ");
			} else {
				nextTierMin = priceTierBeans.get(index + 1).getMinQty();
				if (nextTierMin - 1 > priceTierBean.getMinQty()) {
					strBuf.append(" - ")
					.append(nextTierMin - 1);
				}
			}
			strBuf.append(" @&nbsp </span> <span class=\"")
			.append(cssClassPrice)
			.append("\" id=\"tier-price-")
			.append(index)
			.append("\">")
//			.append(skuPrice.getCurrency().getSymbol())
//			.append(priceTierBean.getPrice())
			.append(getMoneyFormatter().formatCurrency(priceMoney, locale))
			.append(scheduleText)
			.append("</span>");
			
			priceTierContents[index] = strBuf.toString();
		}

		skuSelectionBean.setPriceTierContents(priceTierContents);
		skuSelectionBean.setPriceTiers(priceTierBeans);
	}

	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setMultiSkuProductConfigurationService(final MultiSkuProductConfigurationService multiSkuProductConfigurationService) {
		this.multiSkuProductConfigurationService = multiSkuProductConfigurationService;
	}

	protected MultiSkuProductConfigurationService getMultiSkuProductConfigurationService() {
		return multiSkuProductConfigurationService;
	}

	public void setPriceBuilder(final PriceBuilder priceBuilder) {
		this.priceBuilder = priceBuilder;
	}

	protected PriceBuilder getPriceBuilder() {
		return priceBuilder;
	}

	public void setMoneyFormatter(final MoneyFormatter moneyFormatter) {
		this.moneyFormatter = moneyFormatter;
	}

	protected MoneyFormatter getMoneyFormatter() {
		return moneyFormatter;
	}
}
