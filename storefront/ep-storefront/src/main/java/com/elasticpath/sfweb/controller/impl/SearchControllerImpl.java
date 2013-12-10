/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.catalogview.search.SearchResult;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.SearchService;
import com.elasticpath.sfweb.EpSearchKeyWordNotGivenException;
import com.elasticpath.sfweb.EpSearchKeyWordTooLongException;
import com.elasticpath.sfweb.search.impl.CatalogViewResultBeanCreator;
import com.elasticpath.sfweb.search.impl.StoreSearchRequestFactoryImpl;
import com.elasticpath.sfweb.util.PriceFinderForCart;
import com.elasticpath.sfweb.viewbean.CatalogViewResultBean;

/**
 * The Spring MVC controller for catalog search.
 */
public class SearchControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(SearchControllerImpl.class);

	private SearchService searchService;

	private String successView;

	private String errorView;

	private StoreProductLoadTuner productLoadTuner;

	private SeoUrlBuilder seoUrlBuilder;

	private String storefrontContextUrl;

	private PriceFinderForCart priceFinderForCart;

	private CatalogViewResultBeanCreator catalogViewResultBeanCreator;

	private StoreSearchRequestFactoryImpl storeSearchRequestFactory;

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
		final Store store = getRequestHelper().getStoreConfig().getStore();

		final int pageNumber = ServletRequestUtils.getIntParameter(request, WebConstants.REQUEST_PAGE_NUM, 1);

		SearchRequest searchRequest;

		try {
			searchRequest = getStoreSearchRequestFactory().build(request, store, shoppingCart.getLocale(),
					shoppingCart.getCurrency());
		} catch (EpSearchKeyWordNotGivenException e) {
			return new ModelAndView(errorView, WebConstants.ERROR_KEY, e.getClass().getName());
		} catch (EpSearchKeyWordTooLongException e) {
			return new ModelAndView(errorView, WebConstants.ERROR_KEY, e.getClass().getName());
		}

		SearchResult searchResult;
		if (searchRequest.getKeyWords().trim().length() == 0) {
			// don't bother performing the search if we aren't given keywords
			searchResult = getBean(ContextIdNames.SEARCH_RESULT);
		} else {
			searchResult = searchService.search(searchRequest, null, shoppingCart, this.productLoadTuner, pageNumber);
		}

		// did we land a category match?
		if (searchResult.isCategoryMatch()) {
			return new ModelAndView(new RedirectView(composeCategoryUrl(searchResult.getCategory(), shoppingCart.getLocale()), false), null);
		}

		final CatalogViewResultBean catalogViewResultBean = getCatalogViewResultBeanCreator().createCatalogViewResultBean(pageNumber, searchResult,
				shoppingCart.getSearchResultHistory(), shopper, getRequestHelper().getStoreConfig());

		final Map<String, Boolean> adjustmentsMap = getPriceFinderForCart().findAdjustments(catalogViewResultBean.getProducts(), shopper);

		final Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("catalogFeaturedProductCount", Integer.parseInt(getRequestHelper().getStoreConfig().getSetting(
				"COMMERCE/STORE/CATALOG/featuredProductCountToDisplay").getValue()));
		modelMap.put("catalogViewResultBean", catalogViewResultBean);
		modelMap.put("searchRequest", searchRequest);
		modelMap.put("catalog", store.getCatalog());
		modelMap.put("warehouse", store.getWarehouse());
		modelMap.put("adjustmentsMap", adjustmentsMap);

		return new ModelAndView(successView, modelMap);
	}

	/**
	 * Sets the category service.
	 *
	 * @param searchService the category service
	 */
	public void setSearchService(final SearchService searchService) {
		this.searchService = searchService;
	}

	private String composeCategoryUrl(final Category category, final Locale locale) {
		final StringBuffer url = new StringBuffer();
		url.append(storefrontContextUrl).append('/');
		final int pageNumber = 1;
		if (isSeoEnabled()) {
			url.append(seoUrlBuilder.categorySeoUrl(category, locale, pageNumber));
		} else {
			url.append("browse.ep?cID=");
			url.append(category.getUidPk());
			url.append("&filters=c");
			url.append(category.getGuid());
			url.append("&pn=");
			url.append(pageNumber);
		}
		return url.toString();
	}

	/**
	 * Sets the static view name.
	 *
	 * @param successView name of the success view
	 */
	public final void setSuccessView(final String successView) {
		this.successView = successView;
	}

	/**
	 * Sets the product load tuner.
	 *
	 * @param productLoadTuner the product load tuner
	 */
	public void setProductLoadTuner(final StoreProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}

	/**
	 * Sets the error view name.
	 *
	 * @param errorView error view name
	 */
	public void setErrorView(final String errorView) {
		this.errorView = errorView;
	}

	/**
	 * Sets the builder of seo urls to use.
	 *
	 * @param seoUrlBuilder the builder to use.
	 */
	public void setSeoUrlBuilder(final SeoUrlBuilder seoUrlBuilder) {
		this.seoUrlBuilder = seoUrlBuilder;
	}

	/**
	 * Lookup <b>COMMERCE/STORE/seoEnabled</b> from the settings framework. If enabled, SEO-friendly URLs should be used.
	 *
	 * @return value from the setting.
	 */
	private boolean isSeoEnabled() {
		return getRequestHelper().getStoreConfig().getSetting("COMMERCE/STORE/seoEnabled").getBooleanValue();
	}

	/**
	 * @param storefrontContextUrl The context url for the storefront.
	 */
	public void setStorefrontContextUrl(final String storefrontContextUrl) {
		this.storefrontContextUrl = storefrontContextUrl;
	}

	/**
	 * Gets the price finder for cart.
	 *
	 * @return PriceFinderForCart
	 */
	protected PriceFinderForCart getPriceFinderForCart() {
		return priceFinderForCart;
	}

	/**
	 * Sets the price finder for cart.
	 *
	 * @param priceFinderForCart The PriceFinderForCart to set
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
	 * @param catalogViewResultBeanCreator The catalogViewResultBeanCreator to set
	 */
	public void setCatalogViewResultBeanCreator(final CatalogViewResultBeanCreator catalogViewResultBeanCreator) {
		this.catalogViewResultBeanCreator = catalogViewResultBeanCreator;
	}

	/**
	 * @param storeSearchRequestFactory the storeSearchRequestFactory to set
	 */
	public void setStoreSearchRequestFactory(final StoreSearchRequestFactoryImpl storeSearchRequestFactory) {
		this.storeSearchRequestFactory = storeSearchRequestFactory;
	}

	/**
	 * @return the storeSearchRequestFactory
	 */
	protected StoreSearchRequestFactoryImpl getStoreSearchRequestFactory() {
		return storeSearchRequestFactory;
	}

}
