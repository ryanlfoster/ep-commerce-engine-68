package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductBundleService;

/**
 * Product bundle locator class.
 */
public class ProductBundleLocatorImpl extends AbstractEntityLocator {

	private ProductBundleService productBundleService;
	private FetchGroupLoadTuner productBundleSortLoadTuner;

	/**
	 * Set product bundle service.
	 *
	 * @param productBundleService the product bundle service
	 */
	public void setProductBundleService(final ProductBundleService productBundleService) {
		this.productBundleService = productBundleService;
	}

	@Override
    public Persistable locatePersistence(final String guid, final Class< ? > clazz) {
        return productBundleService.findByGuid(guid, null);
    }

    @Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return ProductBundle.class.isAssignableFrom(clazz);
	}

    @Override
    public boolean entityExists(final String guid, final Class<?> clazz) {
         return productBundleService.guidExists(guid);
    }

	@Override
	public Persistable locatePersistenceForSorting(final String guid, final Class<?> clazz) {
		return productBundleService.findByGuidWithFetchGroupLoadTuner(guid, productBundleSortLoadTuner);
	}
	
	@Override
	public Persistable locatePersistentReference(final String guid, final Class<?> clazz) {
		return productBundleService.findByGuidWithFetchGroupLoadTuner(guid, getEmptyFetchGroupLoadTuner());
	}

	/**
	 * Sets the product bundle load tuner.
	 *
	 * @param productBundleSortLoadTuner the new product bundle load tuner
	 */
	public void setProductBundleSortLoadTuner(final FetchGroupLoadTuner productBundleSortLoadTuner) {
		this.productBundleSortLoadTuner = productBundleSortLoadTuner;
	}
}
