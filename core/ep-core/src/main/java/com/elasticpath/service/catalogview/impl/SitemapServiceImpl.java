package com.elasticpath.service.catalogview.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.sitemap.SitemapRequest;
import com.elasticpath.domain.catalogview.sitemap.SitemapResult;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalogview.SitemapService;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.query.ProductSearchCriteria;

/**
 * Represents a default implementation of <code>SitemapService</code>.
 */
public class SitemapServiceImpl extends AbstractCatalogViewServiceImpl implements SitemapService {

	/**
	 * A comparator implementation for sorting <code>Brand</code> objects based on their display names.
	 */
	class BrandNameComparatorImpl implements Comparator<Brand>, Serializable {

		private static final long serialVersionUID = 1L;

		private final Locale locale;

		/**
		 * @param locale the desired locale of the display name to compare
		 */
		public BrandNameComparatorImpl(final Locale locale) {
			super();
			this.locale = locale;
		}

		/**
		 * @param firstBrand a <code>Brand</code> to compare to another <code>Brand</code>
		 * @param secondBrand the <code>Brand</code> to be compared with the first <code>Brand</code>
		 * @return the int value resulting from the comparison
		 */
		public int compare(final Brand firstBrand, final Brand secondBrand) {
			final String firstBrandValue = firstBrand.getDisplayName(locale, true);
			final String secondBrandValue = secondBrand.getDisplayName(locale, true);

			return firstBrandValue.compareToIgnoreCase(secondBrandValue);
		}

	}


	private CategoryService categoryService;

	private BrandService brandService;

	private StoreProductService storeProductService;

	@Override
	public SitemapResult sitemap(final SitemapRequest sitemapRequest, final ShoppingCart shoppingCart,
			final StoreProductLoadTuner productLoadTuner, final int pageNumber) {
		SitemapResult result = getBean(ContextIdNames.SITEMAP_RESULT);
		result.setSitemapRequest(sitemapRequest);
		final PriceListStack priceListStack = shoppingCart.getShopper().getPriceListStack();

		if (sitemapRequest.getCategoryUid() == 0 && sitemapRequest.getBrandUid() == 0) {
			final IndexSearchResult searchResults = searchProducts(sitemapRequest, false, true,
					priceListStack);
			searchResults.getAllResults();
			final List<FilterOption<BrandFilter>> brandFilterList = searchResults.getBrandFilterOptions();
			final List<Brand> brandList = new ArrayList<Brand>();
			for (final FilterOption<BrandFilter> filter : brandFilterList) {
				brandList.add(filter.getFilter().getBrand());
			}

			Collections.sort(brandList, new BrandNameComparatorImpl(shoppingCart.getLocale()));
			final List<Category> categories = categoryService.listRootCategoriesWithTree(getStoreConfig().getStore().getCatalog(), true);

			result.setBrandListing(brandList);
			result.setCategoryListing(categories);
		} else {
			result.setCategory(retrieveCategory(sitemapRequest));
			result.setBrand(retrieveBrand(sitemapRequest));
			// get results within sub categories if we're doing a category search
			final IndexSearchResult searchResults = searchProducts(sitemapRequest, sitemapRequest.getCategoryUid() != 0, true, priceListStack);
			result.setResultCount(searchResults.getNumFound());	//set the total to calculate pagination

			// get the products for current page
			final int sitemapPagination =
				NumberUtils.toInt(getStoreConfig().getSetting("COMMERCE/STORE/CATALOG/catalogSitemapPagination").getValue());
			final List<Long> productUids = getPagedResults(searchResults, pageNumber, sitemapPagination);
			final List<StoreProduct> products = storeProductService.getProductsForStore(productUids, shoppingCart.getStore(), productLoadTuner);
			result.setProducts(products);
		}

		return result;
	}

	private Category retrieveCategory(final SitemapRequest sitemapRequest) {
		final long categoryUid = sitemapRequest.getCategoryUid();
		if (categoryUid > 0) {
			return categoryService.get(categoryUid);
		}

		return null;
	}

	private Brand retrieveBrand(final SitemapRequest sitemapRequest) {
		final long brandUid = sitemapRequest.getBrandUid();
		if (brandUid > 0) {
			return brandService.get(brandUid);
		}

		return null;
	}

	/**
	 * Sets the category service.
	 *
	 * @param categoryService the category service.
	 */
	@Override
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * Sets the <code>StoreProductService</code>.
	 *
	 * @param storeProductService the product retrieve strategy
	 */
	@Override
	public void setStoreProductService(final StoreProductService storeProductService) {
		this.storeProductService = storeProductService;
	}

	/**
	 * Not implemented.
	 *
	 * @param uid not used
	 * @return nothing
	 */
	public Object getObject(final long uid) {
		throw new EpUnsupportedOperationException("Not implemented.");
	}

	/**
	 * @param brandService the brand service to set
	 */
	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}

	@Override
	protected CatalogViewResult createCatalogViewResult() {
		return getBean(ContextIdNames.SITEMAP_RESULT);
	}

	@Override
	protected ProductCategorySearchCriteria createCriteriaForProductSearch(
			final CatalogViewRequest request, final boolean includeSubCategories) {
		final SitemapRequest sitemapRequest = (SitemapRequest) request;
		final ProductSearchCriteria searchCriteria = getBean(ContextIdNames.PRODUCT_SEARCH_CRITERIA);

		// only want exact matches when browsing
		searchCriteria.setFuzzySearchDisabled(true);
		searchCriteria.setLocale(request.getLocale());
		searchCriteria.setOnlyWithinDirectCategory(!includeSubCategories);
		searchCriteria.setDirectCategoryUid(sitemapRequest.getCategoryUid());
		searchCriteria.setStoreCode(getStoreConfig().getStoreCode());
		searchCriteria.setBrandCode(brandService.get(sitemapRequest.getBrandUid()).getCode());

		if (includeSubCategories) {
			final Set<Long> categoryUids = new HashSet<Long>();
			categoryUids.add(sitemapRequest.getCategoryUid());
			searchCriteria.setAncestorCategoryUids(categoryUids);
		}

		searchCriteria.setCatalogCode(getStoreConfig().getStore().getCatalog().getCode());
		return searchCriteria;
	}

}
