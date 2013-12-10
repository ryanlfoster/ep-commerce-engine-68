/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.SortUtility;
import com.elasticpath.domain.catalogview.browsing.BrowsingRequest;
import com.elasticpath.domain.catalogview.browsing.BrowsingResult;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductCharacteristicsService;
import com.elasticpath.service.catalogview.BrowsingService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.sfweb.search.impl.CatalogViewResultBeanCreator;
import com.elasticpath.sfweb.util.PriceFinderForCart;
import com.elasticpath.sfweb.util.SeoUrlValidator;
import com.elasticpath.sfweb.viewbean.CatalogViewResultBean;

/**
 * The Spring MVC controller for catalog browsing.
 */
public class BrowsingControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(BrowsingControllerImpl.class);

	private static final SortOrder DEFAULT_SORT_ORDER = SortOrder.DESCENDING;

	private static final SortBy DEFAULT_SORT_TYPE = StandardSortBy.FEATURED_CATEGORY;

	private CategoryService categoryService;

	private BrowsingService browsingService;

	private StoreProductLoadTuner productLoadTuner;

	private SeoUrlValidator seoUrlValidator;

	private PriceFinderForCart priceFinderForCart;

	private CatalogViewResultBeanCreator catalogViewResultBeanCreator;
	
	private ProductCharacteristicsService productCharacteristicsService;

	/**
	 * Return the ModelAndView for the configured static view page.
	 *
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		LOG.debug("entering 'handleRequestInternal' method...");

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Shopper shopper = customerSession.getShopper();
		final ShoppingCart shoppingCart = shopper.getCurrentShoppingCart();
		final int pageNumber = getRequestHelper().getIntParameterOrAttribute(request, WebConstants.REQUEST_PAGE_NUM, 1);

		final BrowsingRequest browsingRequest = populateBrowsingRequest(request, response, shoppingCart);

		if (browsingRequest == null) {
			return null;
		}
		final BrowsingResult browsingResult = browsingService.browsing(browsingRequest, null, shoppingCart, this.productLoadTuner, pageNumber);

		final Map<String, Object> modelMap = createModelMap(shopper, shoppingCart, browsingResult, pageNumber);

		final String categoryView = browsingResult.getCategory().getTemplateWithFallBack("categoryTemplate");
		return new ModelAndView("catalog" + File.separatorChar + "category" + File.separatorChar + categoryView, modelMap);
	}

	/**
	 * Create the Model used to display this browsing request.
	 *
	 * @param shopper the shopper
	 * @param shoppingCart the shopping cart/session glob
	 * @param browsingResult the browsing data returned from the service
	 * @param pageNumber the page number
	 *
	 * @return A populated map of data for the view
	 */
	protected Map<String, Object> createModelMap(
			final Shopper shopper, final ShoppingCart shoppingCart, final BrowsingResult browsingResult, final int pageNumber) {
		final CatalogViewResultBean catalogViewResultBean = getCatalogViewResultBeanCreator().createCatalogViewResultBean(pageNumber,
				browsingResult, null, shopper, getRequestHelper().getStoreConfig());
		catalogViewResultBean.addPrices(getPriceFinderForCart().findPrices(browsingResult.getTopSellers(), shopper));
		catalogViewResultBean.addProductCharacteristics(
				getProductCharacteristicsService().getProductCharacteristicsMap(browsingResult.getTopSellers()));

		final Map<String, Boolean> adjustmentsMap = getPriceFinderForCart().findAdjustments(catalogViewResultBean.getProducts(), shopper);
		final Map<String, Object> modelMap = new HashMap<String, Object>();
		final Store store = getRequestHelper().getStoreConfig().getStore();

		modelMap.put("catalogFeaturedProductCount", getCatalogFeaturedProductCount());
		modelMap.put("catalogViewResultBean", catalogViewResultBean);
		modelMap.put("catalog", store.getCatalog());
		modelMap.put("warehouse", store.getWarehouse());
		modelMap.put("adjustmentsMap", adjustmentsMap);
		modelMap.put("recentlyViewedProducts", shoppingCart.getViewHistory().getViewedProducts());
		return modelMap;
	}

	/**
	 * Populate the browsing request with category information.
	 *
	 * @param request is the http request
	 * @param response is the http response
	 * @param shoppingCart is the cart
	 * @return the browsing request
	 * @throws IOException an IO exception
	 */
	protected BrowsingRequest populateBrowsingRequest(final HttpServletRequest request, final HttpServletResponse response,
			final ShoppingCart shoppingCart) throws IOException {

		final BrowsingRequest browsingRequest = getBean(ContextIdNames.BROWSING_REQUEST);
		long categoryUid = 0;
		try {
			categoryUid = getCategoryUid(request);
		} catch (Exception e) { // NOPMD
			// Ignore it. Maybe it is set by the filter.
		}

		final String filterIdStr = getSeoRequestFilters(request);
		final String sorterIdStr = getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_SORTER,
				SortUtility.constructSortTypeOrderString(DEFAULT_SORT_TYPE, DEFAULT_SORT_ORDER));

		browsingRequest.setFiltersIdStr(filterIdStr, getStoreFromRequest());
		browsingRequest.parseSorterIdStr(sorterIdStr);
		browsingRequest.setCurrency(shoppingCart.getCurrency());
		browsingRequest.setLocale(shoppingCart.getLocale());

		Category category = null;
		for (Filter<?> filter : browsingRequest.getFilters()) {
			if (filter instanceof CategoryFilter) {
				category = ((CategoryFilter) filter).getCategory();
			}
		}
		if (category == null && categoryUid == 0) {
			throw new ProductUnavailableException("Category id is not given or is unavailable.");
		}

		if (category != null) {
			// validate the request URL against the category SEO URL
			final boolean valid = seoUrlValidator.validateCategoryUrl(category, browsingRequest.getFilters(), shoppingCart.getLocale(), request);

			if (!valid) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			categoryUid = category.getUidPk();
		}

		browsingRequest.setCategoryUid(categoryUid);
		return browsingRequest;
	}

	/**
	 * Extract and return the seo request filters from the http request.
	 *
	 * @param request the request to extract the filters from.
	 * @return the request filter string, or null if it is not present.
	 */
	protected String getSeoRequestFilters(final HttpServletRequest request) {
		return getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_FILTERS, null);
	}

	/**
	 * Returns the number catalog featured products that are displayed in the store front with store specific setting.
	 *
	 * @return the number of catalog featured products to display
	 */
	int getCatalogFeaturedProductCount() {
		return Integer.parseInt(getRequestHelper().getStoreConfig().getSetting("COMMERCE/STORE/CATALOG/featuredProductCountToDisplay").getValue());
	}

	/**
	 * Get the Store that the user is browsing, from the HttpRequest object.
	 *
	 * @return the Store that the user is browsing
	 */
	Store getStoreFromRequest() {
		return getRequestHelper().getStoreConfig().getStore();
	}

	/**
	 * Due to URL rewriting we cannot be 100% sure that the request contains the category UID; there's a chance that it contains the GUID.<br>
	 * This method will remove the ambiguity and return the category's UID.
	 *
	 * @param request the incoming request
	 * @return the UID of the category referenced in the request object
	 */
	protected long getCategoryUid(final HttpServletRequest request) {
		final String categoryId = getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_CID, null);
		if (StringUtils.isEmpty(categoryId)) {
			throw new ProductUnavailableException("Category id is not given.");
		}

		// TODO: Fortification: This should be changed to retrieve the category guid (category code)
		// rather than the uid to remain independent from the database. This
		// will also allow for removal of parentCategoryUids from the Category
		// search index, since parentCategoryCodes can then be used here instead.
		final StoreConfig storeConfig = getRequestHelper().getStoreConfig();
		final Store store = storeConfig.getStore();
		final long categoryUid = categoryService.findUidById(categoryId, store.getCatalog());
		if (categoryUid == 0L) {
			throw new ProductUnavailableException("Category Not Available for id :" + categoryId);
		}

		return categoryUid;
	}

	/**
	 * Sets the browsing service.
	 *
	 * @param browsingService the browsing service
	 */
	public void setBrowsingService(final BrowsingService browsingService) {
		this.browsingService = browsingService;
	}

	/**
	 * Sets the product load tuner.
	 *
	 * @param productLoadTuner the product load tuner
	 */
	public void setStoreProductLoadTuner(final StoreProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}

	/**
	 * Sets the category service.
	 *
	 * @param categoryService the category service
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * Sets the Seo Url validator.
	 *
	 * @param seoUrlValidator the SEO URL validator
	 */
	public void setSeoUrlValidator(final SeoUrlValidator seoUrlValidator) {
		this.seoUrlValidator = seoUrlValidator;
	}

	/**
	 * Gets the PriceFinderForCart.
	 *
	 * @return PriceFinderForCart
	 */
	protected PriceFinderForCart getPriceFinderForCart() {
		return priceFinderForCart;
	}

	/**
	 * Sets the PriceFinder for Cart.
	 *
	 * @param priceFinderForCart the PriceFinderForCart to set
	 */
	public void setPriceFinderForCart(final PriceFinderForCart priceFinderForCart) {
		this.priceFinderForCart = priceFinderForCart;
	}

	/**
	 * Gets the CatalogViewResultBeanCreator.
	 *
	 * @return CatalogViewResultBeanCreator
	 */
	protected CatalogViewResultBeanCreator getCatalogViewResultBeanCreator() {
		return catalogViewResultBeanCreator;
	}

	/**
	 * Sets the CatalogViewResultBeanCreator.
	 *
	 * @param creator The factory to set
	 */
	public void setCatalogViewResultBeanCreator(final CatalogViewResultBeanCreator creator) {
		this.catalogViewResultBeanCreator = creator;
	}

	public ProductCharacteristicsService getProductCharacteristicsService() {
		return productCharacteristicsService;
	}

	public void setProductCharacteristicsService(final ProductCharacteristicsService productCharacteristicsService) {
		this.productCharacteristicsService = productCharacteristicsService;
	}
}
