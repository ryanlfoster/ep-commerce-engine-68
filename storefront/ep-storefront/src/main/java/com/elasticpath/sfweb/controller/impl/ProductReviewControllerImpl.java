/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalogview.ProductViewService;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.sfweb.viewbean.ProductViewBean;

/**
 * The Spring MVC controller for product reviews.
 */
public class ProductReviewControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(ProductReviewControllerImpl.class);

	private ProductService productService;

	private String successView;

	private ProductViewService productViewService;

	/**
	 * Loads the current product uid and breadcrumb data into the model and displays the product review view.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		LOG.debug("entering 'handleRequestInternal' method...");

		// Get the product view bean with breadcrumb history in it and add it to the model
		final String productGuid = getProductGuid(request);
		final Product currentProduct = getProduct(productGuid);
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final ProductViewBean productViewBean = createProductViewBean(currentProduct, shoppingCart);
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put("productViewBean", productViewBean);

		// Get the PowerReviews merchant id from setting service and pass to model.
		final SettingValue merchantIdSetting = getRequestHelper().getStoreConfig().getSetting("COMMERCE/STORE/POWERREVIEWS/powerReviewsMerchantid");
		final int merchantId = Integer.parseInt(merchantIdSetting.getValue());
		model.put("merchantID", Integer.valueOf(merchantId));

		// Load the success view, passing it the model
		return new ModelAndView(successView, model);
	}

	/**
	 * Gets the product guid from the request.
	 * 
	 * @param request - The request containing the product uid
	 * @return The product guid as a string.
	 */
	private String getProductGuid(final HttpServletRequest request) {

		final String productGuid = getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_PGUID, null);

		if (StringUtils.isEmpty(productGuid)) {
			throw new ProductUnavailableException("Product guid is not given.");
		}

		return productGuid;
	}

	/**
	 * Returns the product with the given <code>productGuid</code>.
	 * 
	 * @param productGuid - The guid of the product
	 * @return The product with the given <code>productGuid</code>.
	 */
	private Product getProduct(final String productGuid) {

		final long productUid = productService.findUidById(productGuid);
		final Product product = productService.get(productUid);

		if (product == null || !product.isInCatalog(getRequestHelper().getStoreConfig().getStore().getCatalog())) {
			throw new ProductUnavailableException("Product is not available, product id:" + productUid);
		}

		return product;
	}

	/**
	 * Gets the product view bean and loads the user view history into it.
	 * 
	 * @param shoppingCart - The shopping cart for the user
	 * @return The product view bean with the user view history in it.
	 */
	private ProductViewBean createProductViewBean(final Product currentProduct, final ShoppingCart shoppingCart) {

		// Get the product view bean and set the current product
		final ProductViewBean productViewBean = getBean("productViewBean");
		productViewBean.setProduct(currentProduct);
		productViewBean.setProductCategory(productViewService.getProductCategory(currentProduct));

		// Get the user's view history and set it into the product view bean
		final CatalogViewResultHistory catalogViewResultHistory = shoppingCart.getCatalogViewResultHistory();

		CatalogViewResult catalogViewResult = null;
		if (catalogViewResultHistory != null) {
			catalogViewResult = catalogViewResultHistory.getLastResult();
		}

		productViewBean.setCurrentCatalogViewResult(catalogViewResult);

		return productViewBean;
	}

	/**
	 * Sets the product service.
	 * 
	 * @param productService the product view service
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Sets the product view service.
	 * 
	 * @param productViewService the product view service
	 */
	public void setProductViewService(final ProductViewService productViewService) {
		this.productViewService = productViewService;
	}

	/**
	 * Sets the static view name.
	 * 
	 * @param successView - Name of the success view
	 */
	public final void setSuccessView(final String successView) {
		this.successView = successView;
	}
}
