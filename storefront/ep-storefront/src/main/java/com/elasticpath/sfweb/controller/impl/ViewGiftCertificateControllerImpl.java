/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.order.Order;
import com.elasticpath.service.catalog.GiftCertificateService;

/**
 * The Spring MVC controller for displaying a Gift Certificate.
 */
public class ViewGiftCertificateControllerImpl extends AbstractEpControllerImpl {
	private String successView;

	private String errorView;

	private GiftCertificateService giftCertificateService;

	/**
	 * Process request for displaying an order.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModelAndView instance
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	
		String giftCertificateCode = getRequestHelper().getStringParameterOrAttribute(request, "giftCertificateCode", null);
		giftCertificateCode = removeHyphen(giftCertificateCode).trim();
		if (giftCertificateCode == null) {
			giftCertificateCode = "";
		}
		
		final GiftCertificate giftCertificate = giftCertificateService.findByGiftCertificateCode(giftCertificateCode, getRequestHelper()
				.getStoreConfig().getStore());
		ModelAndView modelAndView = null;
		Map<String, Object> modelMap = new HashMap<String, Object>();

		// load requested gift certificates and relevant orders from database
		if (giftCertificate == null) {
			// TODO: should this really display success view when we don't have a gift-certificate?
			modelAndView = new ModelAndView(getSuccessView(), modelMap);
			return modelAndView;
		}

		final Map<Order, Money> ordersBalance = giftCertificateService.retrieveOrdersBalances(giftCertificate.getUidPk());

		modelMap.put("giftCertificate", giftCertificate);
		modelMap.put("orders", ordersBalance);
		modelAndView = new ModelAndView(getSuccessView(), modelMap);

		return modelAndView;
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
	 * Sets the success view name.
	 * 
	 * @return name of the success view
	 */
	public String getSuccessView() {
		return this.successView;
	}

	/**
	 * Sets the Gift Certificate Service.
	 * 
	 * @param giftCertificateService the giftCertificateService to set
	 */
	public void setGiftCertificateService(final GiftCertificateService giftCertificateService) {
		this.giftCertificateService = giftCertificateService;
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
	 * @return the errorView
	 */
	public String getErrorView() {
		return errorView;
	}

	/**
	 * To remove hyphens in gift certificate code to match it to code in database.
	 * 
	 * @param str
	 * @return
	 */
	private String removeHyphen(final String str) {
		String resultStr = "";
		if (str == null) {
			return resultStr;
		}
		return str.replace("-", "");
	}
}
