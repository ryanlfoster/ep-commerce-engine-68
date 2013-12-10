package com.elasticpath.domain.catalogview.browsing.impl;

import java.util.List;

import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.browsing.BrowsingResult;
import com.elasticpath.domain.catalogview.impl.AbstractCatalogViewResultImpl;

/**
 * Represents a default implementation of <code>BrowsingResult</code>.
 */
public class BrowsingResultImpl extends AbstractCatalogViewResultImpl implements BrowsingResult {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private List<StoreProduct> topSellers;

	/**
	 * Replicate the data to this <code>CatalogViewResult</code> from the given <code>CatalogViewResult</code>.
	 *
	 * @param catalogViewResult the <code>CatalogViewResult</code> to be replicated
	 */
	@Override
	public void replicateData(final CatalogViewResult catalogViewResult) {
		super.replicateData(catalogViewResult);

		final BrowsingResult browsingResult = (BrowsingResult) catalogViewResult;
		this.setCategory(browsingResult.getCategory());
	}

	/**
	 * Returns a list of top selling products.
	 *
	 * @return a list of top selling products
	 */
	public List<StoreProduct> getTopSellers() {
		return topSellers;
	}

	/**
	 * Sets a list of top selling products.
	 *
	 * @param topSellers a list of top selling products
	 */
	public void setTopSellers(final List<StoreProduct> topSellers) {
		this.topSellers = topSellers;
	}
}
