package com.elasticpath.search.index.loader.impl;

import java.util.Collection;

import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.catalogview.IndexProduct;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.store.StoreService;

/**
 * Fetches a batch of {@link IndexProduct}s.
 */
public class BatchProductLoader extends AbstractEntityLoader<IndexProduct> {

	private StoreProductService storeProductService;

	private StoreService storeService;

	private FetchGroupLoadTuner productLoadTuner;

	/**
	 * Loads the {@link IndexProduct}s for the batched ids and loads each batch in bulk.
	 * 
	 * @return the loaded {@link IndexProduct}s
	 */
	public Collection<IndexProduct> loadBatch() {

		Collection<Store> stores = getStoreService().findAllCompleteStores(getProductLoadTuner());
		Collection<IndexProduct> loadedIndexProductBatch = getStoreProductService().getIndexProducts(getUidsToLoad(), stores, getProductLoadTuner());

		return loadedIndexProductBatch;
	}

	public StoreProductService getStoreProductService() {
		return storeProductService;
	}

	public void setStoreProductService(final StoreProductService storeProductService) {
		this.storeProductService = storeProductService;
	}

	/**
	 * @param storeService the storeService to set
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * @return the storeService
	 */
	public StoreService getStoreService() {
		return storeService;
	}

	/**
	 * @param loadTuner the loadTuner to set
	 */
	public void setProductLoadTuner(final FetchGroupLoadTuner loadTuner) {
		this.productLoadTuner = loadTuner;
	}

	/**
	 * @return the loadTuner
	 */
	public FetchGroupLoadTuner getProductLoadTuner() {
		return productLoadTuner;
	}
}
