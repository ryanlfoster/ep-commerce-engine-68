package com.elasticpath.service.catalogview;

import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.catalogview.search.SearchResult;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.EpService;

/**
 * Provide searching service.
 */
public interface SearchService extends EpService {

	/**
	 * Perform searching based on the given search request and returns the search result.
	 * <p>
	 * By giving the previous search result history, you may get response quicker. If you don't have it, give a <code>null</code>. It doesn't
	 * affect search result.
	 * <p>
	 * By giving a shopping cart, promotion rules will be applied to the returned products.
	 * <p>
	 * By giving the product load tuner, you can fine control what data to be loaded for each product. It is used to improve performance.
	 * 
	 * @param searchRequest the search request
	 * @param previousSearchResultHistory the previous search results, give <code>null</code> if you don't have it
	 * @param shoppingCart the shopping cart, give <code>null</code> if you don't have it
	 * @param productLoadTuner the product load tuner, give <code>null</code> to populate all data
	 * @param pageNumber the current page number
	 * @return a <code>SearchResult</code> instance
	 */
	SearchResult search(SearchRequest searchRequest, CatalogViewResultHistory previousSearchResultHistory, ShoppingCart shoppingCart,
			StoreProductLoadTuner productLoadTuner, final int pageNumber);
	
}
