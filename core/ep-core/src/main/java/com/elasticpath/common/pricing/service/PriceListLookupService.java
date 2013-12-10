package com.elasticpath.common.pricing.service;

import java.util.Currency;

import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.tags.TagSet;

/**
 *	Service for looking up price list descriptor GUIDs.
 */
public interface PriceListLookupService {

	/**
	 * 
	 * Get the (@link PriceListStack}, that match given catalog and currency.
	 * Price list assignments must satisfy the selling context associated with them.
	 * 
	 * @param catalogCode the code of the Catalog to get the price list for
	 * @param currency for the currency of the price list
	 * @param tagSet set of tags within customer session
	 * @return instance of (@link PriceListStack}, that match given catalog and currency
	 */
	PriceListStack getPriceListStack(String catalogCode, Currency currency, TagSet tagSet);
	
}
