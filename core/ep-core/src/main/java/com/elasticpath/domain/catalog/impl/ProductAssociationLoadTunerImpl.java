/**
 *
 */
package com.elasticpath.domain.catalog.impl;

import com.elasticpath.domain.catalog.ProductAssociationLoadTuner;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control product association load. A product association load tuner can be used in some services to
 * fine control what data to be loaded for a product association. The main purpose is to achieve maximum performance
 * for some specific performance-critical pages.
 */
public class ProductAssociationLoadTunerImpl extends AbstractEpDomainImpl implements ProductAssociationLoadTuner {

	private static final long serialVersionUID = 1L;

	private boolean loadingCatalog;

	private ProductLoadTuner productLoadTuner;

	/**
	 * Return <code>true</code> if the catalog is requested.
	 *
	 * @return true if the catalog should be loaded.
	 */
	public boolean isLoadingCatalog() {
		return loadingCatalog;
	}

	/**
	 * Set the flag indicating whether catalog should be loaded.
	 *
	 * @param loadingCatalog should be set to true if the catalog should be loaded
	 */
	public void setLoadingCatalog(final boolean loadingCatalog) {
		this.loadingCatalog = loadingCatalog;
	}

	/**
	 * Get the <code>ProductLoadTuner</code> that should be used for loading the products.
	 *
	 * @return the productLoadTuner
	 */
	public ProductLoadTuner getProductLoadTuner() {
		return productLoadTuner;
	}

	/**
	 * Set the <code>ProductLoadTuner</code> that should be used for loading the products in the association.
	 *
	 * @param productLoadTuner the productLoadTuner to set
	 */
	public void setProductLoadTuner(final ProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}

	@Override
	public boolean contains(final LoadTuner loadTuner) {
		return false;
	}

	@Override
	public LoadTuner merge(final LoadTuner loadTuner) {
		return this;
	}

}
