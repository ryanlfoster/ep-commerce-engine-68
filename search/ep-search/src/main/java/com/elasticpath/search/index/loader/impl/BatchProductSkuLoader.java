package com.elasticpath.search.index.loader.impl;

import java.util.Collection;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.catalog.ProductSkuService;

/**
 * Fetches a batch of {@link ProductSku}s.
 */
public class BatchProductSkuLoader extends AbstractEntityLoader<ProductSku> {

	private ProductSkuService skuService;

	private FetchGroupLoadTuner productSkuLoadTuner;

	/**
	 * Loads the {@link ProductSku}s for the batched ids and loads each batch in bulk.
	 * 
	 * @return the loaded {@link ProductSku}s
	 */
	public Collection<ProductSku> loadBatch() {

		Collection<ProductSku> loadedProductSkuBatch = getSkuService().findByUids(getUidsToLoad(), getProductSkuLoadTuner());

		return loadedProductSkuBatch;
	}

	public void setSkuService(final ProductSkuService skuService) {
		this.skuService = skuService;
	}

	public ProductSkuService getSkuService() {
		return skuService;
	}

	public void setProductSkuLoadTuner(final FetchGroupLoadTuner productSkuLoadTuner) {
		this.productSkuLoadTuner = productSkuLoadTuner;
	}

	public FetchGroupLoadTuner getProductSkuLoadTuner() {
		return productSkuLoadTuner;
	}

}
