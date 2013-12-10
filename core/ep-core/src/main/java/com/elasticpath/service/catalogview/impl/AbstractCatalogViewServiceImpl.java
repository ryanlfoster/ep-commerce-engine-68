package com.elasticpath.service.catalogview.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.FeaturedProductFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.FilterOptionCompareToComparator;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.misc.FilterBucketComparator;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.TopSellerService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.StoreAwareSearchCriteria;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.service.search.query.SearchHint;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.settings.SettingsReader;

/**
 * Abstract service class from which catalog view services inherit.
 */
public abstract class AbstractCatalogViewServiceImpl extends AbstractEpServiceImpl  {
	
	private CategoryService categoryService;

	private TopSellerService topSellerService;

	private StoreProductService storeProductService;

	private IndexSearchService indexSearchService;
	
	private IndexUtility indexUtility;
	
	private SeoUrlBuilder seoUrlBuilder;
	
	private StoreConfig storeConfig;
	
	private SettingsReader settingsReader;
	
	/**
	 * Searches for featured products with search criteria constructed from
	 * {@link #createCriteriaForProductSearch(CatalogViewRequest, boolean)} and sets the
	 * appropriate field within the result. Featured products are ordered by
	 * {@link StandardSortBy#FEATURED_CATEGORY}, {@link SortOrder#DESCENDING}. Optionally specify
	 * whether to include sub-category feature products in the search.
	 * 
	 * @param request the current request
	 * @param result the new result
	 * @param shoppingCart the shopping cart
	 * @param storeProductLoadTuner the load tuner to use to load products
	 * @param includeSubCategories whether to include sub-categories
	 */
	protected void searchAndSetFeaturedProducts(final CatalogViewRequest request, final CatalogViewResult result,
			final ShoppingCart shoppingCart, final StoreProductLoadTuner storeProductLoadTuner, final boolean includeSubCategories) {
		final List<Long> featuredProductsUids = searchFeaturedProducts(request, includeSubCategories, 
				shoppingCart.getShopper().getPriceListStack());
		List<StoreProduct> featuredProducts = storeProductService.getProductsForStore(featuredProductsUids, shoppingCart.getStore(),
				storeProductLoadTuner);
		featuredProducts = indexUtility.sortDomainList(featuredProductsUids, featuredProducts);
		result.setFeaturedProducts(featuredProducts);
	}
	
	/**
	 * Retrieves the list of UIDs for featured products in the current category and
	 * sub-categories. The category is given via the {@link CatalogViewRequest}. Featured
	 * products in other categories will also be retrieved as well (although sorted below those in
	 * the current category). Order is given by {@link StandardSortBy#FEATURED_CATEGORY},
	 * {@link SortOrder#DESCENDING}.
	 * 
	 * @param request the current request
	 * @param includeSubCategories whether to include sub-categories
	 * @param priceListStack the price list stack
	 * @return the list of UIDs for featured products in the current category and sub-categories
	 */
	protected List<Long> searchFeaturedProducts(final CatalogViewRequest request, final boolean includeSubCategories, 
			final PriceListStack priceListStack) {
		ProductCategorySearchCriteria searchCriteria = constructSearchCriteria(request, includeSubCategories, priceListStack);
		
		// want to make a new copy of the list so that our featured product filter isn't added
		// permanently to the mutable list
		List<Filter< ? >> previousFilters = Collections.emptyList();
		if (searchCriteria.getFilters() != null) {
			previousFilters = searchCriteria.getFilters();
		}
		searchCriteria.setFilters(new ArrayList<Filter< ? >>(previousFilters.size() + 1));
		searchCriteria.getFilters().addAll(previousFilters);
		
		FeaturedProductFilter featuredProductFilter = getBean(ContextIdNames.FEATURED_PRODUCT_FILTER);
		featuredProductFilter.setCategoryUid(request.getCategoryUid());
		searchCriteria.getFilters().add(featuredProductFilter);
		
		// filter out products with start and end date out of range
		searchCriteria.setActiveOnly(true);
		// filter out product not visible in the store being viewed
		searchCriteria.setDisplayableOnly(true);
		searchCriteria.setFuzzySearchDisabled(true);
		searchCriteria.setSortingOrder(SortOrder.DESCENDING);
		searchCriteria.setSortingType(StandardSortBy.FEATURED_CATEGORY);
		searchCriteria.setFacetingEnabled(false);
		
		return indexSearchService.search(searchCriteria).getResults(0, getCatalogFeaturedProductCount());
	}
	
	/**
	 * Retrieve the {@link IndexSearchResult} for products.
	 * <p>
	 * Calls {@link #createCriteriaForProductSearch(CatalogViewRequest, boolean)} for the
	 * {@link ProductCategorySearchCriteria} to use for searching. Note: some values may be
	 * overridden.
	 * </p>
	 * 
	 * @param request the current request
	 * @param includeSubCategories whether to include sub-categories
	 * @param requiresFaceting whether to add faceting information to the result
	 * @param priceListStack the price list stack
	 * @return an {@link IndexSearchResult}
	 */
	protected IndexSearchResult searchProducts(final CatalogViewRequest request, final boolean includeSubCategories,
			final boolean requiresFaceting, final PriceListStack priceListStack) {
		ProductCategorySearchCriteria searchCriteria = constructSearchCriteria(request, includeSubCategories, priceListStack);
		searchCriteria.setFacetingEnabled(requiresFaceting);
		// filter out products with start and end date out of range
		searchCriteria.setActiveOnly(true);
		// filter out product not visible in the store being viewed
		searchCriteria.setDisplayableOnly(true);
		return indexSearchService.search(searchCriteria);
	}
	
	/**
	 * Loads the previous history into an instance of {@link CatalogViewResult}. If there was no
	 * history, return an new instance of {@link CatalogViewResult}. If <code>history</code> is
	 * <code>null</code>, we assume there was no previous history.
	 * 
	 * @param request the current request
	 * @param history the previous history or <code>null</code>
	 * @return a {@link CatalogViewResult} with previous history (if any)
	 */
	protected CatalogViewResult loadHistory(final CatalogViewRequest request, final CatalogViewResultHistory history) {
		CatalogViewResult result;
		if (history == null) {
			result = createCatalogViewResult();
			result.setCatalogViewRequest(request);
		} else {
			result = history.addRequest(request);
		}
		return result;
	}
	
	/**
	 * Sets the filter options of the given {@link CatalogViewResult} from the given
	 * {@link CatalogViewRequest}.
	 * 
	 * @param request the initial request
	 * @param result the result
	 * @param indexSearchResult the index search result to get the filter options from
	 */
	protected void setFilterOptions(final CatalogViewRequest request, final CatalogViewResult result, final IndexSearchResult indexSearchResult) {
		final FilterBucketComparator hitsNumberComparator = getBean(ContextIdNames.FILTER_BUCKET_COMPARATOR);
		
		// remove categories that are not part of this store's catalog
		List<FilterOption<CategoryFilter>> categoryFilterOptions = indexSearchResult.getCategoryFilterOptions();
		for (Iterator<FilterOption<CategoryFilter>> catIter = categoryFilterOptions.iterator(); catIter.hasNext();) {
			if (!(this.getStoreConfig().getStore().getCatalog().equals(catIter.next().getFilter().getCategory().getCatalog()))) {
				catIter.remove();
			}
		}
		
		// category filter options
		prepareFilterOptions(indexSearchResult.getCategoryFilterOptions(), request);
		result.setCategoryFilterOptions(indexSearchResult.getCategoryFilterOptions());
		result.collapseCategoryFilterOptions(true);
		Collections.sort(result.getCategoryFilterOptions(), hitsNumberComparator);

		// brand filter options
		prepareFilterOptions(indexSearchResult.getBrandFilterOptions(), request);
		result.setBrandFilterOptions(indexSearchResult.getBrandFilterOptions());
		Collections.sort(result.getBrandFilterOptions(), hitsNumberComparator);

		// price filter options
		prepareFilterOptions(indexSearchResult.getPriceFilterOptions(), request);
		result.setPriceFilterOptions(indexSearchResult.getPriceFilterOptions());
		result.collapsePriceFilterOptions();
		FilterOptionCompareToComparator<PriceFilter> priceFilterComparator = getCompareToComparator();
		Collections.sort(result.getPriceFilterOptions(), priceFilterComparator);
		
		if (isAttributeFilterEnabled()) {
			// attribute value filter options
			for (List<FilterOption<AttributeValueFilter>> filterOptions : indexSearchResult.getAttributeValueFilterOptions().values()) {
				prepareFilterOptions(filterOptions, request);
				Collections.sort(filterOptions, hitsNumberComparator);
			}
			result.setAttributeValueFilterOptions(indexSearchResult.getAttributeValueFilterOptions());
		}
		
		// attribute range filter options
		for (List<FilterOption<AttributeRangeFilter>> filterOptions : indexSearchResult.getAttributeRangeFilterOptions().values()) {
			prepareFilterOptions(filterOptions, request);
		}
		result.setAttributeRangeFilterOptions(indexSearchResult.getAttributeRangeFilterOptions());
		result.collapseAttributeRangeFilterOptions();
		// do this after collapse so that we don't have to do as much work
		FilterOptionCompareToComparator<AttributeRangeFilter> attrRangeComparator = getCompareToComparator();
		for (List<FilterOption<AttributeRangeFilter>> filterOptions : indexSearchResult.getAttributeRangeFilterOptions().values()) {
			Collections.sort(filterOptions, attrRangeComparator);
		}
	}

	
	/**
	 * Returns the number catalog featured products that are displayed in the store front with store
	 * specific setting.
	 * @return the number of catalog featured products to display
	 */
	int getCatalogFeaturedProductCount() {
		return Integer.parseInt(this.getStoreConfig().getSetting("COMMERCE/STORE/CATALOG/featuredProductCountToDisplay").getValue());
	}
	
	/**
	 * @return true if attribute filtering is enabled for the store in this thread,
	 * false if not.
	 */
	boolean isAttributeFilterEnabled() {
		return this.getStoreConfig().getSetting("COMMERCE/STORE/FILTEREDNAVIGATION/attributeFilterEnabled").getBooleanValue();
	}
	
	private <T extends Filter<T>> void prepareFilterOptions(final Collection<FilterOption<T>> filterOptions, final CatalogViewRequest request) {
		addSeoAndQueryString(filterOptions, request);
		removeDuplicateFilters(request.getFilters(), filterOptions);
	}

	private <T extends Filter<T>> FilterOptionCompareToComparator<T> getCompareToComparator() {
		return getBean(ContextIdNames.FILTER_OPTION_COMPARETO_COMPARATOR);
	}

	/**
	 * Adds the SEO URL and query string to the collection of filter options.
	 *
	 * @param <T> the filter type
	 * @param filterOptions the collection of filter options
	 * @param request the original request
	 */
	private <T extends Filter<T>> void addSeoAndQueryString(final Collection<FilterOption<T>> filterOptions, final CatalogViewRequest request) {
		for (FilterOption<T> filterOption : filterOptions) {
			final CatalogViewRequest newRequest = request.createRefinedRequest(filterOption.getFilter());
			filterOption.setSeoUrl(seoUrlBuilder.filterSeoUrl(newRequest.getLocale(), newRequest.getFilters(),
					newRequest.getSortType(), newRequest.getSortOrder(), 1));
			filterOption.setQueryString(newRequest.getQueryString());
		}
	}
	
	/**
	 * Removes from the new filters options, filters that are already in use. For instance, if a
	 * price filter was already in use, it removes that same price filter options from the
	 * collection of new filters options.
	 * 
	 * @param <T> the type of filter
	 * @param filtersInUse filters that are currently in use
	 * @param newFiltersOptions the current collection of new filter options
	 */
	private <T extends Filter<T>> void removeDuplicateFilters(final Collection<Filter< ? >> filtersInUse,
			final Collection<FilterOption<T>> newFiltersOptions) {
		for (Filter< ? > filter : filtersInUse) {
			for (Iterator<FilterOption<T>> filterOptionIter = newFiltersOptions.iterator(); filterOptionIter.hasNext();) {
				FilterOption<T> filterOption = filterOptionIter.next();
				if (filterOption.getFilter().equals(filter)) {
					filterOptionIter.remove();
				}
			}
		}
	}
	
	/**
	 * Returns the actual start index given the page number and the page size.
	 *
	 * @param searchResult the search result to get results from
	 * @param pageNumber the page number
	 * @param pagination the pagination size
	 * @return a list of UIDs for the given page number
	 */
	protected List<Long> getPagedResults(final IndexSearchResult searchResult, final int pageNumber, final int pagination) {
		if (pageNumber == 0) {
			return searchResult.getAllResults();
		}
		
		final int startIndex = (pageNumber - 1) * pagination;
		return searchResult.getResults(startIndex, pagination);
	}
	
	private ProductCategorySearchCriteria constructSearchCriteria(final CatalogViewRequest request,
			final boolean includeSubCategories, final PriceListStack priceListStack) {
		ProductCategorySearchCriteria searchCriteria = createCriteriaForProductSearch(request, includeSubCategories);

		searchCriteria.setCurrency(request.getCurrency());
		searchCriteria.setFilters(request.getFilters());
		searchCriteria.setLocale(request.getLocale());
		searchCriteria.setSortingOrder(request.getSortOrder());
		searchCriteria.setSortingType(request.getSortType());
		searchCriteria.setCatalogCode(getStoreConfig().getStore().getCatalog().getCode());
		searchCriteria.addSearchHint(new SearchHint<PriceListStack>("priceListStack", priceListStack));

		if (searchCriteria instanceof StoreAwareSearchCriteria) {
			((StoreAwareSearchCriteria) searchCriteria).setStoreCode(getStoreConfig().getStoreCode());
		}
		return searchCriteria;
	}
	
	/**
	 * This method should return a specific {@link CatalogViewResult} for the given type of action
	 * (browse or search).
	 * 
	 * @return specific {@link CatalogViewResult} for the given type of action
	 */
	protected abstract CatalogViewResult createCatalogViewResult();
	
	/**
	 * Creates a search criteria object with proprietary fields (for browsing/search) filled.
	 *
	 * @param request the current request
	 * @param includeSubCategories whether to include sub-categories
	 * @return a search criteria object
	 */
	protected abstract ProductCategorySearchCriteria createCriteriaForProductSearch(final CatalogViewRequest request,
			final boolean includeSubCategories);
	
	/**
	 * Gets the {@link TopSellerService} instance.
	 *
	 * @return the {@link TopSellerService} instance
	 */
	protected TopSellerService getTopSellerService() {
		return topSellerService;
	}
	
	/**
	 * Gets the {@link CategoryService} instance.
	 *
	 * @return the {@link CategoryService} instance
	 */
	protected CategoryService getCategoryService() {
		return categoryService;
	}
	
	/**
	 * Gets the {@link StoreProductService} instance.
	 *
	 * @return the {@link StoreProductService} instance
	 */
	protected StoreProductService getStoreProductService() {
		return storeProductService;
	}
	
	/**
	 * Gets the {@link IndexSearchService} instance.
	 *
	 * @return the {@link IndexSearchService} instance
	 */
	protected IndexSearchService getIndexSearchService() {
		return indexSearchService;
	}
	
	/**
	 * Gets the {@link IndexUtility} instance.
	 *
	 * @return the {@link IndexUtility} instance
	 */
	protected IndexUtility getIndexUtility() {
		return indexUtility;
	}

	/**
	 * Sets the {@link TopSellerService} instance to use.
	 *
	 * @param topSellerService the {@link TopSellerService instance to use
	 */
	public void setTopSellerService(final TopSellerService topSellerService) {
		this.topSellerService = topSellerService;
	}

	/**
	 * Sets the {@link CategoryService} instance to use.
	 *
	 * @param categoryService the {@link CategoryService instance to use
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * Sets the {@link StoreProductService} instance to use.
	 *
	 * @param storeProductService the {@link StoreProductService instance to use
	 */
	public void setStoreProductService(final StoreProductService storeProductService) {
		this.storeProductService = storeProductService;
	}

	/**
	 * Sets the {@link IndexSearchService} instance to use.
	 *
	 * @param indexSearchService the {@link IndexSearchService} instance to use
	 */
	public void setIndexSearchService(final IndexSearchService indexSearchService) {
		this.indexSearchService = indexSearchService;
	}
	
	/**
	 * Sets the {@link IndexUtility} instance to use.
	 *
	 * @param indexUtility the {@link IndexUtility} instance to use
	 */
	public void setIndexUtility(final IndexUtility indexUtility) {
		this.indexUtility = indexUtility;
	}
	
	/**
	 * Sets the {@link SeoUrlBuilder} instance to use.
	 *
	 * @param seoUrlBuilder the {@link SeoUrlBuilder} instance to use
	 */
	public void setSeoUrlBuilder(final SeoUrlBuilder seoUrlBuilder) {
		this.seoUrlBuilder = seoUrlBuilder;
	}
	
	/**
	 * Sets the store configuration to be used as a context for searching.
	 * 
	 * @param storeConfig the store configuration.
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}
	
	/**
	 * Returns the store configuration that provides the context for the
	 * catalog to view.
	 * 
	 * @return the store configuration.
	 */
	protected StoreConfig getStoreConfig() {
		return storeConfig;
	}

	
	/**
	 * Returns the settingsService.
	 * 
	 * @return the settings service.
	 */
	protected SettingsReader getSettingsService() {
		return settingsReader;
	}
	
	/**
	 * Set the settingsService.
	 * 
	 * @param settingsService the settingsServiceR
	 */
	public void setSettingsService(final SettingsReader settingsService) {
		this.settingsReader = settingsService;
	}

}
