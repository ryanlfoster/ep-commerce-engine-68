/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.SortUtility;
import com.elasticpath.domain.catalogview.sitemap.SitemapRequest;
import com.elasticpath.domain.catalogview.sitemap.SitemapResult;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalogview.PaginationService;
import com.elasticpath.service.catalogview.SitemapService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.sfweb.util.SeoUrlValidator;
import com.elasticpath.sfweb.viewbean.SitemapResultBean;

/**
 * The Spring MVC controller for sitemap browsing.
 */
public class SitemapControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(SitemapControllerImpl.class);

	private BrandService brandService;
	
	private CategoryService categoryService;

	private String successView;

	private SitemapService sitemapService;

	private StoreProductLoadTuner productLoadTuner;

	private SeoUrlValidator seoUrlValidator;

	private PaginationService paginationService;

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
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final int pageNumber = getRequestHelper().getIntParameterOrAttribute(request, WebConstants.REQUEST_PAGE_NUM, 1);
		final SitemapRequest sitemapRequest = populateSitemapRequest(request, shoppingCart);
		final SitemapResult sitemapResult = sitemapService.sitemap(sitemapRequest, shoppingCart, productLoadTuner, pageNumber);

		final boolean valid = seoUrlValidator.validateSitemapUrl(sitemapResult.getCategory(), sitemapResult.getBrand(), shoppingCart.getLocale(),
				request);
		if (!valid) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		return new ModelAndView(successView, populateModel(pageNumber, sitemapResult));
	}

	/**
	 * @param pageNumber
	 * @param sitemapResult
	 * @return
	 */
	private Map<String, Object> populateModel(final int pageNumber, final SitemapResult sitemapResult) {
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final SitemapResultBean sitemapResultBean = populateSitemapResultBean(pageNumber, sitemapResult);
		final StoreConfig storeConfig = getRequestHelper().getStoreConfig();
		final Store store = storeConfig.getStore();
		
		model.put("sitemapResultBean", sitemapResultBean);
		model.put("catalog", store.getCatalog());
		model.put("warehouse", store.getWarehouse());
		
		return model;
	}

	private SitemapResultBean populateSitemapResultBean(final int pageNumber, final SitemapResult sitemapResult) {
		final SitemapResultBean sitemapResultBean = this.getBean("sitemapResultBean");
		sitemapResultBean.setCurrentSitemapResult(sitemapResult);
		sitemapResultBean.setCurrentPageNumber(pageNumber);
		sitemapResultBean.setTotalPageNumber(getPaginationService().getLastPageNumber(sitemapResult.getResultCount(),
				getRequestHelper().getStoreConfig().getStoreCode()));
		return sitemapResultBean;
	}

	/**
	 * Build up the SitemapRequest catalog view request. Can be for category or brand.<br>
	 * Populated with request filters from the URL. Filters may be used later by search faceting to retrieve list of products.
	 * 
	 * @param request the http request
	 * @param shoppingCart the shopping cart
	 * @return populated SitemapRequest
	 */
	private SitemapRequest populateSitemapRequest(final HttpServletRequest request, final ShoppingCart shoppingCart) {
		final SitemapRequest sitemapRequest = getBean(ContextIdNames.SITEMAP_REQUEST);

		final String filterIdStr = getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_FILTERS, null);
		final String sorterIdStr = getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_SORTER,
				SortUtility.constructSortTypeOrderString(StandardSortBy.PRODUCT_NAME, SortOrder.DESCENDING));

		sitemapRequest.setFiltersIdStr(filterIdStr, shoppingCart.getStore());
		sitemapRequest.parseSorterIdStr(sorterIdStr);
		sitemapRequest.setCurrency(shoppingCart.getCurrency());
		sitemapRequest.setLocale(shoppingCart.getLocale());
		sitemapRequest.setBrandUid(getBrandUid(request));
		sitemapRequest.setCategoryUid(getCategoryUid(request));

		return sitemapRequest;
	}

	private long getCategoryUid(final HttpServletRequest request) {
		final String cateogryId = getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_CID, null);
		long categoryUid = 0;

		if (cateogryId != null && !"".equals(cateogryId)) {
			categoryUid = categoryService.findByGuid(cateogryId).getUidPk();
			if (categoryUid == 0L) {
				throw new ProductUnavailableException("Category Not Available for id :" + cateogryId);
			}
		}

		return categoryUid;
	}

	private long getBrandUid(final HttpServletRequest request) {
		final String filters = getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_FILTERS, null);
		long brandUid = 0;

		if (filters != null && !"".equals(filters)) {
			final String[] tokens = filters.split(" ");
			for (String token : tokens) {
				if (token.startsWith(SeoConstants.BRAND_FILTER_PREFIX)) {
					String brandGuidString = token.substring(SeoConstants.BRAND_FILTER_PREFIX.length(), token.length());
					brandUid = brandService.findByCode(brandGuidString).getUidPk();
				}
			}
		}

		return brandUid;
	}

	/**
	 * Sets the brand service.
	 * 
	 * @param brandService The brand service.
	 */
	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
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
	 * Sets the product load tuner.
	 * 
	 * @param productLoadTuner the product load tuner
	 */
	public void setStoreProductLoadTuner(final StoreProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
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
	 * Sets the sitemap service.
	 * 
	 * @param sitemapService the sitemap service
	 */
	public void setSitemapService(final SitemapService sitemapService) {
		this.sitemapService = sitemapService;
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
	 * @param paginationService service
	 */
	public void setPaginationService(final PaginationService paginationService) {
		this.paginationService = paginationService;
	}

	/**
	 * @return pagination service
	 */
	public PaginationService getPaginationService() {
		return paginationService;
	}

}
