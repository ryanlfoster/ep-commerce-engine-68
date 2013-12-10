package com.elasticpath.sfweb.ajax.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;

import org.springframework.context.MessageSource;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.SimplePrice;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.MoneyFormatter;
import com.elasticpath.domain.shopper.LocaleProvider;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sfweb.ajax.bean.PriceTierBean;
import com.elasticpath.sfweb.ajax.bean.impl.AggregatedPrice;
import com.elasticpath.sfweb.ajax.bean.impl.PriceTierBeanImpl;

/**
 * 
 * Builds  map of prices applicable to provided collection of store product. 
 *
 */
public class PriceBuilder {
	
	
	private static final String NOW = "NOW";
	private MoneyFormatter moneyFormatter;

	/**
	 * Get prices for products.
	 * @param products the products
	 * @param cart {@link ShoppingCart}
	 * @param shopper {@link Shopper}
	 * @param priceLookupFacade {@link PriceLookupFacade}  
	 * @return list of Prices
	 */
	Map<String, Price> build(final Collection<Product> products, final ShoppingCart cart,
			final Shopper shopper, final PriceLookupFacade priceLookupFacade
			) {
		Map <String, Price> prices = new HashMap<String, Price>();
		if (products != null) {
			for (Product product : products) {
				Price price = getPrice(cart, shopper, product, priceLookupFacade);
				prices.put(product.getCode(), price);
			}
		}
		return prices;
	}


	/**
	 * @param cart {@link ShoppingCart}
	 * @param shopper {@link Shopper}
	 * @param product {@link Product}
	 * @param priceLookupFacade {@link PriceLookupFacade} 
	 * @return price found
	 */
	Price getPrice(final ShoppingCart cart, final Shopper shopper, final Product product, final PriceLookupFacade priceLookupFacade) {
		return priceLookupFacade.getPromotedPriceForProduct(product, cart.getStore(), shopper, cart.getAppliedRules());
	}	

	/**
	 * Get price tiers from price object.
	 * @param itemPrice Price object.
	 * @return List of price tier.
	 */
	List<PriceTierBean> getPriceTiers(final Price itemPrice) {
		List<PriceTierBean> res = new ArrayList<PriceTierBean>();
		if (itemPrice == null) {
			return res;
		}
		SortedMap<Integer, PriceTier> priceTiers = itemPrice.getPriceTiers();
		if (priceTiers != null) {
			PriceTierBeanImpl previousPriceTier = null; 
			for (PriceTier priceTier : priceTiers.values()) {
				PriceTierBeanImpl priceTierBean = createPriceTierBean(priceTier);
				if (previousPriceTier == null || !previousPriceTier.getPrice().equals(priceTierBean.getPrice())) {
					res.add(priceTierBean);
				}
				previousPriceTier = priceTierBean;
			}
		}
		return res;
	}
	
	/**
	 * Convert a domain PriceTier object to ajax PriceTierBeanImpl.
	 * @param priceTier PriceTier to be converted.
	 * @return PriceTierBeanImpl
	 */
	PriceTierBeanImpl createPriceTierBean(final PriceTier priceTier) {
		PriceTierBeanImpl priceTierBean = new PriceTierBeanImpl();
		priceTierBean.setMinQty(priceTier.getMinQty());
		priceTierBean.setPrice(priceTier.getLowestPrice());
		return priceTierBean;
	}
	
	/**
	 * @param price Price object.
	 * @param quantity passed in.
	 * @return the lower one between the passed in quantity and the first tier quantity.
	 */
	int getEffectiveQuantity(final Price price, final int quantity) {
		if (price.getPriceTiers() == null) {
			return quantity;
		}
		
		int minQty = price.getPriceTiers().keySet().iterator().next();
		
		if (minQty > quantity) {
			return minQty;
		}
		
		return quantity;
	}
	
	/**
	 * 
	 * @param pricingScheme PricingScheme
	 * @param messageSource MessageSource
	 * @param localeProvider CustomerSession
	 * @return price strings.
	 */
	List<AggregatedPrice> getAggregatedPrices(final PricingScheme pricingScheme, 
			final MessageSource messageSource, 
			final LocaleProvider localeProvider) {
		
		List<AggregatedPrice> result = new ArrayList<AggregatedPrice>();
		Map<PriceSchedule, SimplePrice> schedules = pricingScheme.getPriceSchedules();
		
		// 1. collect price tiers
		Set<Integer> tiers = new TreeSet<Integer>();		
		for (Map.Entry<PriceSchedule, SimplePrice> schedule : schedules.entrySet()) {
			tiers.addAll(schedule.getValue().getPriceTiersMinQuantities());
		}
		
		Map<String, AggregatedPrice> tierStringMap = new HashMap<String, AggregatedPrice>();
		
		StringBuilder buf = new StringBuilder();
		for (Integer tier : tiers) {
			buf.delete(0, buf.length());
			int index = 0;
			boolean available = true; // whether this teir is available
			for (Map.Entry<PriceSchedule, SimplePrice> schedule : schedules.entrySet()) {
				PriceSchedule priceSchedule = schedule.getKey();
				SimplePrice price = schedule.getValue();
				Money lowestPrice = price.getLowestPrice(tier);
				// current schedule does't have price for that
				if (lowestPrice == null) {
					available = false;
					continue;
				}
				buf.append(getMoneyFormatter().formatCurrency(lowestPrice, localeProvider.getLocale()));
								
				// if there is no recurring charges, shall not display NOW in the price.
				boolean notDisplayNow = schedules.size() == 1;
				String scheduleDisplayText = getScheduleDisplayText(
						messageSource, localeProvider, priceSchedule, notDisplayNow);
				buf.append(scheduleDisplayText);
				
				if (index < schedules.size() - 1) {
					buf.append(", ");
				}
				index++;
			}
			String priceString = buf.toString();
			// avoid duplicate tier price string.
			if (available && !tierStringMap.containsKey(priceString)) {				
				AggregatedPrice aggregatedPrice = new AggregatedPrice();
				aggregatedPrice.setMinQty(tier);
				aggregatedPrice.setPriceString(priceString);
				tierStringMap.put(priceString, aggregatedPrice);
				result.add(aggregatedPrice);
			}
		}		
		
		return result;
	}


	private String getScheduleDisplayText(final MessageSource messageSource,
			final LocaleProvider localeProvider, final PriceSchedule priceSchedule, 
			final boolean notDisplayNow) {
		String scheduleName = null;
		if (priceSchedule.getType().equals(PriceScheduleType.PURCHASE_TIME)) {					
			if (!notDisplayNow) {
				scheduleName = NOW;
			}
		} else {
			scheduleName = priceSchedule.getPaymentSchedule().getName();
		}
		
		String scheduleDisplayText = "";
		
		if (scheduleName != null) {
			scheduleDisplayText = " " + messageSource.getMessage(JsonBundleServiceImpl.MESSAGE_SOURCE_PREFIX + scheduleName,
				null, scheduleName, localeProvider.getLocale());
		}
		return scheduleDisplayText;
	}

	public void setMoneyFormatter(final MoneyFormatter formatter) {
		this.moneyFormatter = formatter;
	}

	protected MoneyFormatter getMoneyFormatter() {
		return moneyFormatter;
	}
}
