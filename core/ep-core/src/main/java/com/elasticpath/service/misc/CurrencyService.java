package com.elasticpath.service.misc;

import java.util.Currency;
import java.util.Map;

import com.elasticpath.service.EpService;

/**
 * This service is responsible for retrieving settings related to Currency.
 * 
 * @author dwu
 *
 */
public interface CurrencyService extends EpService {
	/**
	 * Get all the currencies supported by all catalogues.
	 * 
	 * @return map of all supported currencies keyed on currency code.
	 */
	Map <String, Currency> getAllCurrencies();

}
