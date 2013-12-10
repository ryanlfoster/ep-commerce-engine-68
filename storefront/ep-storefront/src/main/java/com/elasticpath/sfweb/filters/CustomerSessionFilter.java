/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.exception.EpLicensingInvalidException;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sfweb.service.WebCustomerSessionService;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestResponseFacade;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.web.ajax.dwrconverter.EpCurrencyConverter;

/**
 * This filter manages customer session.<br>
 * If a new customer send a request without customer session id, a new customer session will be created and stored. <br>
 * If a return customer send a request with a valid customer session id, the customer information will be loaded for controllers' usage. The
 * customer-information-load operation will only be done <b>once</b> for a session.<br>
 * The customer session id is written back to customer browser as a cookie.
 *
 */
public class CustomerSessionFilter implements Filter {

    private BeanFactory beanFactory;

	private WebCustomerSessionService webCustomerSessionService;

	private AssetRepository assetRepository;
	
	private SfRequestHelper requestHelper;
	
	private String mergedCartUrl;

	/**
	 * Initialize the filter.
	 *
	 * @param arg0 not used
	 * @throws ServletException in case of error.
	 */
	public void init(final FilterConfig arg0) throws ServletException {
		// No init required
	}

	/**
	 * Filter the request.
	 *
	 * @param inRequest the request
	 * @param inResponse the response
	 * @param inFilterChain the filter chain
	 * @throws IOException in case of error
	 * @throws ServletException in case of error
	 * @throws EpLicensingInvalidException in case of error
	 */
	public void doFilter(final ServletRequest inRequest, final ServletResponse inResponse, final FilterChain inFilterChain) throws IOException,
			ServletException, EpLicensingInvalidException {

		if (!isHttpServletRequest(inRequest)) {
			inFilterChain.doFilter(inRequest, inResponse);
			return;
		}

		StringBuffer httpUrl = ((HttpServletRequest) inRequest).getRequestURL();
		// Handle license key check
		if (httpUrl.indexOf("/licensing-error.ep") == -1) {
			EpCurrencyConverter licenseChecker = new EpCurrencyConverter();
			licenseChecker.setAssetRepository(getAssetRepository());
			licenseChecker.checkLicense();
		}

        final HttpServletRequestResponseFacade requestResponseFacade = createRequestResponseFacade(inRequest, inResponse);
		webCustomerSessionService.handleFilterRequest(requestResponseFacade);
		
		HttpServletRequest request = (HttpServletRequest) inRequest;
		Object customer = request.getSession().getAttribute(WebConstants.AUTHENTICATED_CUSTOMER);
		if (customer != null) {
			webCustomerSessionService.handleCustomerSignIn(requestResponseFacade, (Customer) customer);
			request.getSession().setAttribute(WebConstants.AUTHENTICATED_CUSTOMER, null);
			
			if (mergedCartRedirect(request)) {
				HttpServletResponse response = ((HttpServletResponse) inResponse);
				response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + mergedCartUrl));
				return;
			}
		}

		inFilterChain.doFilter(inRequest, inResponse);
	}

	/**
	 * Apply a merged cart page redirection URL.
	 * @param request the request
	 * 
	 * @return the merged cart landing page for a merged cart scenario, otherwise the original targetUrl is returned
	 */
	protected boolean mergedCartRedirect(final HttpServletRequest request) {
		final CustomerSession customerSession = requestHelper.getCustomerSession(request);
		if (customerSession != null) {
			final ShoppingCart shoppingCart = customerSession.getShoppingCart();
			if (shoppingCart.isMergedNotification()) {
				return true;
			}
		}
		return false;
	}
	
	private HttpServletRequestResponseFacade createRequestResponseFacade(final ServletRequest inRequest, final ServletResponse inResponse) {
		if (!(inRequest instanceof HttpServletRequest)) {
			throw new IllegalArgumentException("ServletRequest must be instance of HttpServletRequest");
		}
		if (!(inResponse instanceof HttpServletResponse)) {
			throw new IllegalArgumentException("ServletResponse must be instance of HttpServletResponse");
		}

		final HttpServletRequest request = (HttpServletRequest) inRequest;
		final HttpServletResponse response = (HttpServletResponse) inResponse;
		final HttpServletFacadeFactory httpServletFacadeFactory = beanFactory.getBean("httpServletFacadeFactory");

        return httpServletFacadeFactory.createRequestResponseFacade(request, response);
	}

	private boolean isHttpServletRequest(final ServletRequest inRequest) {
		return (inRequest instanceof HttpServletRequest);
	}

	/**
	 * Destroy the filter.
	 */
	public void destroy() {
		//No action needed
	}

	/**
	 * Set the web customer session service.
	 *
	 * @param webCustomerSessionService the web customer session service to set.
	 */
	public void setWebCustomerSessionService(final WebCustomerSessionService webCustomerSessionService) {
		this.webCustomerSessionService = webCustomerSessionService;
	}

	/**
	 * @return the assetRepository
	 */
	public AssetRepository getAssetRepository() {
		return assetRepository;
	}

	/**
	 * @param assetRepository the assetRepository to set
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}

	/**
	 * Sets the BeanFactory.
	 * @param beanFactory the BeanFactory
	 */
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
	/**
	 * @param requestHelper the requestHelper to set
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}
    
	/**
	 * @param mergedCartUrl the merged cart landing page Url
	 */
	public void setMergedCartUrl(final String mergedCartUrl) {
		this.mergedCartUrl = mergedCartUrl;
	}

}
