package com.elasticpath.common.pricing.service.impl;

import java.util.Currency;

import com.elasticpath.common.pricing.service.PriceListLookupService;
import com.elasticpath.common.pricing.service.PriceListStackLookupStrategy;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.tags.TagSet;

/**
 *	Implementation of the Service for looking up price list descriptor GUIDs.
 */
public class PriceListLookupServiceImpl implements PriceListLookupService {
	
	private PriceListStackLookupStrategy plStackLookupStrategy;

	@Override	
	public PriceListStack getPriceListStack(final String catalogCode, final Currency currency, final TagSet tagSet) {
		return getPlStackLookupStrategy().getPriceListStack(catalogCode, currency, tagSet);
	}

	/**
	 * This method needs to be changed in the future to actually take into
	 * account some sort of mapping between catalog and price lists.
	 * 
	 * @param catalog to get the price list for
	 * @param currency for the currency of the price list
	 * @return GUID string of the price list
	 */
//	public String lookupPriceListGuid(final Catalog catalog, final Currency currency) {
//		return catalog.getGuid() + "_" + currency.getCurrencyCode();
//	}

	/**
	 *
	 * @param plStackLookupStrategy the plStackLookupStrategy to set
	 */
	public void setPlStackLookupStrategy(final PriceListStackLookupStrategy plStackLookupStrategy) {
		this.plStackLookupStrategy = plStackLookupStrategy;
	}

	/**
	 *
	 * @return the plStackLookupStrategy
	 */
	public PriceListStackLookupStrategy getPlStackLookupStrategy() {
		return plStackLookupStrategy;
	}
	
}
