package com.elasticpath.sfweb.search;

import java.util.Currency;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.store.Store;

/**
 * Interface class for creating a SearchRequest.
 *
 */
public interface StoreSearchRequestFactory {

	/**
	 * Method that creates the search request given the input parameters.
	 * @param request The HttpServletRequest
	 * @param store The Store
	 * @param locale The Locale
	 * @param currency The currency
	 * @return SearchRequest A search request
	 */
	CatalogViewRequest build(final HttpServletRequest request,
			final Store store, final Locale locale, final Currency currency);

}
