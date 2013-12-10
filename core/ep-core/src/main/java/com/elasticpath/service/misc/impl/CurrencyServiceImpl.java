package com.elasticpath.service.misc.impl;

import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.misc.SupportedCurrency;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.CurrencyService;

/**
 * This service is responsible for retrieving settings related to Currency.
 * 
 * @author dwu
 *
 */
public class CurrencyServiceImpl extends AbstractEpPersistenceServiceImpl implements
		CurrencyService {
	
	/**
	 * Get all the currencies supported by all catalogues.
	 * 
	 * @return map of all supported currencies keyed on currency code.
	 */
	public Map<String, Currency> getAllCurrencies() {
		Map <String, Currency> result = new HashMap<String, Currency>();
		List <SupportedCurrency> currencyList = getPersistenceEngine().
			<SupportedCurrency> retrieveByNamedQuery("CATALOG_CURRENCY_SELECT_ALL_SUPPORTED");
		for (SupportedCurrency cur : currencyList) {
			result.put(cur.getCurrency().getCurrencyCode(), cur.getCurrency());
		}
		return result;
	}

	/**
	 * Stubbed. 
	 * 
	 * @param uid of the catalog currency object
	 * @return catalogCurrency found
	 * @throws EpServiceException on error
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return null;
	}

}
