/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.catalog.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang.ArrayUtils;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.SimplePrice;
import com.elasticpath.domain.misc.Money;

/**
 * Simple {@link PricingScheme} backed by a map of schedule to price.
 */
public class PricingSchemeImpl implements PricingScheme {

	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private final Map<PriceSchedule, SimplePrice> priceSchedules = new TreeMap<PriceSchedule, SimplePrice>(); 
	private Currency currency;
	@Override
	public void setPriceForSchedule(final PriceSchedule schedule, final SimplePrice skuPrice) {
		priceSchedules.put(schedule, skuPrice);
		if (currency == null && skuPrice != null) {
			currency = skuPrice.getCurrency();
		}
	}

	@Override
	public Collection<PriceSchedule> getSchedules(final PriceScheduleType... types) {
		Set<PriceSchedule> schedules = new TreeSet<PriceSchedule>();
		for (PriceSchedule schedule : priceSchedules.keySet()) {
			if (types == null || types.length == 0 || ArrayUtils.contains(types, schedule.getType())) {
				schedules.add(schedule);
			}
		}
		
		return schedules;
	}

	@Override
	public Collection<PriceSchedule> getRecurringSchedules() {
		return getSchedules(PriceScheduleType.RECURRING);
	}

	@Override
	public Collection<PriceSchedule> getPurchaseTimeSchedules() {
		return getSchedules(PriceScheduleType.PURCHASE_TIME);
	}

	@Override
	public SimplePrice getSimplePriceForSchedule(final PriceSchedule schedule) {
		return priceSchedules.get(schedule);
	}

	@Override
	public Currency getCurrency() {
		return currency;
	}

	@Override
	public PriceSchedule getScheduleForLowestPrice() {
		PriceSchedule lowestSchedule = null;
		BigDecimal lowestPrice = null;
		for (PriceSchedule priceSchedule : priceSchedules.keySet()) {
			SimplePrice price = getSimplePriceForSchedule(priceSchedule);
			BigDecimal current = price.getLowestPrice(price.getFirstPriceTierMinQty()).getAmount();
			if (lowestPrice == null || current.compareTo(lowestPrice) < 0) {
				lowestPrice = current;
				lowestSchedule = priceSchedule;
			}
		}
		return lowestSchedule;
	}

	@Override
	public Money getLowestPrice() {
		SimplePrice price = getSimplePriceForSchedule(getScheduleForLowestPrice());
		return price.getLowestPrice(price.getFirstPriceTierMinQty());
	}

	@Override
	public Map<PriceSchedule, SimplePrice> getPriceSchedules() {
		return priceSchedules;
	}


	@Override
	public Set<Integer> getPriceTiersMinQuantities() {
		
		Set<Integer> result = new TreeSet<Integer>();
		//iterate through each Price in the scheme, and for each price, add its min qtys to the result set
		for (SimplePrice price : priceSchedules.values()) {
			Set<Integer> priceTiersForSchedle = price.getPriceTiersMinQuantities();
			result.addAll(priceTiersForSchedle);
		}
		return Collections.unmodifiableSet(result);
	}



}
