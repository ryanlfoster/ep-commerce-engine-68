package com.elasticpath.service.catalogview;

import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.browsing.BrowsingRequest;
import com.elasticpath.domain.catalogview.browsing.BrowsingResult;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.EpService;

/**
 * Provide catalog browsing service.
 */
public interface BrowsingService extends EpService {

	/**
	 * Perform browsing based on the given browsing request and returns the browsing result.
	 * <p>
	 * By giving the previous browsing result history, you may get response quicker. If you don't have it, give a <code>null</code>. It doesn't
	 * affect the result.
	 * <p>
	 * By giving a shopping cart, promotion rules will be applied to the returned products.
	 * <p>
	 * By giving the product load tuner, you can fine control what data to be loaded for each product. It is used to improve performance.
	 * 
	 * @param browsingRequest the browsing request
	 * @param previousBrowsingResultHistory the previous browsing results, give <code>null</code> if you don't have it
	 * @param shoppingCart the shopping cart, give <code>null</code> if you don't have it
	 * @param storeProductLoadTuner the product load tuner, give <code>null</code> to populate all data
	 * @param pageNumber the current page number
	 * @return a <code>BrowsingResult</code> instance
	 */
	BrowsingResult browsing(BrowsingRequest browsingRequest, CatalogViewResultHistory previousBrowsingResultHistory, ShoppingCart shoppingCart,
			StoreProductLoadTuner storeProductLoadTuner, final int pageNumber);
	
}
