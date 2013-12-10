package com.elasticpath.service.catalogview;

import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.sitemap.SitemapRequest;
import com.elasticpath.domain.catalogview.sitemap.SitemapResult;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.EpService;

/**
 * Provide sitemap service.
 */
public interface SitemapService extends EpService {

	/**
	 * Retrieves sitemap listing based on the given sitemap request and returns the sitemap result.
	 * 
	 * @param sitemapRequest the sitemap request
	 * @param shoppingCart the shopping cart, give <code>null</code> if you don't have it
	 * @param productLoadTuner the product load tuner, give <code>null</code> to populate all data
	 * @param pageNumber the current page number of the result
	 * @return a <code>SitemapResult</code> instance
	 */
	SitemapResult sitemap(final SitemapRequest sitemapRequest, final ShoppingCart shoppingCart, 
			final StoreProductLoadTuner productLoadTuner, int pageNumber);
	
}
