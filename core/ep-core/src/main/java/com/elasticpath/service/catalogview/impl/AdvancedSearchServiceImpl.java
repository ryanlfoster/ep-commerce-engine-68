package com.elasticpath.service.catalogview.impl;

import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.search.AdvancedSearchRequest;
import com.elasticpath.domain.catalogview.search.SearchResult;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalogview.AdvancedSearchService;
import com.elasticpath.service.search.ProductCategorySearchCriteria;

/**
 * 
 * This class provides the advanced search functionality to search products by providing mutliple filters such as pricing, attributes, etc.
 */
public class AdvancedSearchServiceImpl extends AbstractSearchServiceImpl implements AdvancedSearchService {

	/**
	 * Creates a {@link ProductCategorySearchCriteria} for the given search request.
	 * 
	 * @param request the search request
	 * @param includeSubCategories whether or not to include subcategories
	 * @return an instance of {@link ProductCategorySearchCriteria}
	 */
	@Override
	protected ProductCategorySearchCriteria createCriteriaForProductSearch(final CatalogViewRequest request, final boolean includeSubCategories) {
		return getSearchCriteriaFactory().createProductSearchCriteria(request);
	}

	@Override
	public SearchResult search(final AdvancedSearchRequest searchRequest, final ShoppingCart shoppingCart,
			final StoreProductLoadTuner productLoadTuner, final int pageNumber) {

		SearchResult result = (SearchResult) createCatalogViewResult();
		result.setCatalogViewRequest(searchRequest);
		searchForProducts(searchRequest, shoppingCart, productLoadTuner, pageNumber, result);

		return result;
	}

}
