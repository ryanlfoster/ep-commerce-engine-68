/**
 * 
 */
package com.elasticpath.search.index.loader.impl;

import java.util.Collection;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Fetches a batch of {@link Category}s.
 */
public class BatchCategoryLoader extends AbstractEntityLoader<Category> {

	private CategoryService categoryService;

	private FetchGroupLoadTuner categoryFetchGroupLoadTuner;

	/**
	 * Loads the {@link Category}s for the batched ids and loads each batch in bulk.
	 * 
	 * @return the loaded {@link Category}s
	 */
	public Collection<Category> loadBatch() {

		Collection<Category> loadedProductSkuBatch = getCategoryService().findByUidsWithFetchGroupLoadTuner(getUidsToLoad(),
				categoryFetchGroupLoadTuner);

		return loadedProductSkuBatch;
	}

	/**
	 * @param categoryService the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * @return the categoryService
	 */
	public CategoryService getCategoryService() {
		return categoryService;
	}

	/**
	 * @param categoryFetchGroupLoadTuner the categoryFetchGroupLoadTuner to set
	 */
	public void setCategoryFetchGroupLoadTuner(final FetchGroupLoadTuner categoryFetchGroupLoadTuner) {
		this.categoryFetchGroupLoadTuner = categoryFetchGroupLoadTuner;
	}

	/**
	 * @return the categoryFetchGroupLoadTuner
	 */
	public FetchGroupLoadTuner getCategoryFetchGroupLoadTuner() {
		return categoryFetchGroupLoadTuner;
	}

}
