/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.catalog.ProductXmlService;

/**
 * The Spring MVC controller for product lookup required by PowerReviews. 
 */
public class ProductLookupControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(ProductLookupControllerImpl.class);
	
	private ProductXmlService productXmlService;

	/**
	 * Outputs a stream of product data formatted as XML as required by PowerReviews.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("entering 'handleRequest' method...");
		}

		final String productXml = getProductXml(request);

		response.setContentType("application/xml");
		ServletOutputStream out = response.getOutputStream();
		out.print(productXml);
		
		return null;
	}

	/**
	 * Determines the product to lookup and formats the base data for that product into xml.
	 *
	 * @param request - The request
	 * @return A string of xml with the base product data in it.
	 */
	private String getProductXml(final HttpServletRequest request) {
		// Determine the base url from the request
		StringBuffer requestURL = request.getRequestURL();
		String servletPath = request.getServletPath();
		String baseUrl = requestURL.toString().replaceAll(servletPath, "");
				
		final Catalog catalog = getRequestHelper().getStoreConfig().getStore().getCatalog();
		final String productGuid = getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_POWER_REVIEWS_PGUID, null);
		final String productXml = this.productXmlService.getProductMinimalXml(catalog, baseUrl, productGuid, getRequestHelper().getStoreConfig().
				getSetting("COMMERCE/STORE/seoEnabled").getBooleanValue());

		return productXml;
	}

	/**
	 * Sets the product xml service.
	 * 
	 * @param productXmlService - The product xml service
	 */
	public void setProductXmlService(final ProductXmlService productXmlService) {
		this.productXmlService = productXmlService;
	}
}
