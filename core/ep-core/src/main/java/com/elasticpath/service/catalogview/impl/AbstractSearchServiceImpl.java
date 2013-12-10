package com.elasticpath.service.catalogview.impl;

import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.catalogview.search.SearchResult;
import com.elasticpath.domain.search.SfSearchLog;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalogview.PaginationService;
import com.elasticpath.service.catalogview.SearchCriteriaFactory;
import com.elasticpath.service.catalogview.SfSearchLogService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.index.IndexSearchResult;

/**
 * 
 * This class is an abstraction over {@link AbstractCatalogViewServiceImpl} which provides common functionality used
 * for search services.
 */
public abstract class AbstractSearchServiceImpl extends AbstractCatalogViewServiceImpl {

	private SearchCriteriaFactory searchCriteriaFactory;

	private PaginationService paginationService;

	private SfSearchLogService sfSearchLogService;

	private TimeService timeService;

	/**
	 * Returns a new instance of {@link SearchResult}.
	 * 
	 * @return a new instance of {@link SearchResult}
	 */
	@Override
	protected CatalogViewResult createCatalogViewResult() {
		return getBean(ContextIdNames.SEARCH_RESULT);
	}

	/**
	 * Searches for products using the given search request and populates the given search result.
	 *
	 * @param request the search request
	 * @param shoppingCart the shopping car
	 * @param storeProductLoadTuner the store product load tuner
	 * @param pageNumber the page number of the results
	 * @param result the search results
	 */
	protected void searchForProducts(final CatalogViewRequest request, final ShoppingCart shoppingCart,
			final StoreProductLoadTuner storeProductLoadTuner, final int pageNumber, final SearchResult result) {
		// get featured product list first
		searchAndSetFeaturedProducts(request, result, shoppingCart, storeProductLoadTuner, true);

		// get normal products, don't include featured products
		final IndexSearchResult productResults = searchProducts(request, true, true, shoppingCart.getShopper().getPriceListStack());
		String storeCode = getStoreConfig().getStoreCode();
		int numberOfItemsPerPage = getPaginationService().getNumberOfItemsPerPage(storeCode);
		final List<Long> productUids = getPagedResults(productResults, pageNumber, numberOfItemsPerPage);

		List<StoreProduct> products = getStoreProductService().getProductsForStore(productUids, shoppingCart.getStore(), storeProductLoadTuner);
		products = getIndexUtility().sortDomainList(productUids, products);
		result.setProducts(products);
		result.setResultsCount(productResults.getLastNumFound());

		if (request.getCategoryUid() > 0) {
			result.setCategory(getCategoryService().get(request.getCategoryUid()));
		}

		if (!products.isEmpty()) {
			setFilterOptions(request, result, productResults);
		}
	}

	/**
	 * Logs the current search request and result using the storefront search logger.
	 *
	 * @param searchRequest the search request
	 * @param result the search result
	 */
	protected void logSearch(final SearchRequest searchRequest, final SearchResult result) {
		SfSearchLog log = getBean(ContextIdNames.SF_SEARCH_LOG);

		log.setCategoryRestriction(searchRequest.getCategoryUid());
		log.setKeywords(searchRequest.getKeyWords());
		log.setResultCount(result.getResultsCount());
		log.setSearchTime(this.timeService.getCurrentTime());
		if (null != result.getSuggestions()) {
			log.setSuggestionsGenerated(true);
		}

		sfSearchLogService.add(log);
	}

	@Override
	protected abstract ProductCategorySearchCriteria createCriteriaForProductSearch(CatalogViewRequest request, boolean includeSubCategories);

	/**
	 * Sets the search criteria factory used to create search criterias for products, categories, etc.
	 * 
	 * @param searchCriteriaFactory the <code>searchCriteriaFactory</code>
	 */
	public void setSearchCriteriaFactory(final SearchCriteriaFactory searchCriteriaFactory) {
		this.searchCriteriaFactory = searchCriteriaFactory;
	}

	/**
	 * Get the search criteria factory used to get a search configuration.
	 * 
	 * @return a {@link SearchCriteriaFactory}
	 */
	protected SearchCriteriaFactory getSearchCriteriaFactory() {
		return searchCriteriaFactory;
	}

	/**
	 * @return a {@link PaginationService}
	 */
	protected PaginationService getPaginationService() {
		return paginationService;
	}

	/**
	 * Sets the pagination service.
	 * 
	 * @param paginationService the {@link PaginationService} to use
	 */
	public void setPaginationService(final PaginationService paginationService) {
		this.paginationService = paginationService;
	}

	/**
	 * Sets the SfSearchLogService.
	 * 
	 * @param sfSearchLogService the SfSearchLogService
	 */
	public void setSfSearchLogService(final SfSearchLogService sfSearchLogService) {
		this.sfSearchLogService = sfSearchLogService;
	}

	/**
	 * Sets the time service.
	 * 
	 * @param timeService the time service
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

}
