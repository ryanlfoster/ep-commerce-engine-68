package com.elasticpath.sfweb.filters;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.tags.TagSet;

/**
 * A servlet filter that calls campaign service which in turn executes actions based on rules and campaign strategy. This filter should never throw
 * exceptions in case of exceptions are thrown by downstream classes in order to not affect user experience when campaign goes wrong.
 */
public class TargetedSellingFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(TargetedSellingFilter.class);

	private SfRequestHelper requestHelper;

	private String storefrontContextUrl;

	/**
	 * used in velocity content wrappers templates.
	 */
	static final String BASE_URL_PARAMETER_NAME = "baseUrl";

	/**
	 * used in velocity content wrappers templates.
	 */
	static final String BASE_DC_ASSET_URL_PARAMETER_NAME = "baseDcAssetUrl";

	/**
	 * if context path is empty it is treated as not being set.
	 */
	static final String NO_SF_CONTEXT_PATH = "";

	/**
	 * terminator of the base urls. gets appended if it is not the final char in url string.
	 */
	static final String PATH_TERMINATOR = "/";

	/**
	 * default url base for unset sf context path.
	 */
	static final String BASE_URL_FOR_NO_SF_CONTEXT_PATH = NO_SF_CONTEXT_PATH;

	/**
	 * default url dynamic content asset base for unset sf context path.
	 */
	static final String BASE_DC_ASSET_URL_PATH = WebConstants.DYNAMIC_CONTENT_REWRITE_PATTERN;

	/**
	 * 
	 */
	public void destroy() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Campaign filter destroying......");
		}
	}

	/**
	 * Filter chain override.
	 * 
	 * @param request servlet request
	 * @param response servlet response
	 * @param filterChain filter chain
	 * @throws IOException IOException
	 * @throws ServletException ServletException
	 */
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException,
			ServletException {

		if (!(request instanceof HttpServletRequest)) {
			filterChain.doFilter(request, response);
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Campaign filter chaining ......");
			LOG.debug("request url = " + ((HttpServletRequest) request).getRequestURL());
			LOG.debug("request content type = " + request.getContentType() + "\r\n");
		}

		try {
			final CustomerSession customerSession = getRequestHelper().getCustomerSession((HttpServletRequest) request);
			final ShoppingCart shoppingCart = customerSession.getShoppingCart();
			final TagSet tagCloud = customerSession.getCustomerTagSet();

			// This looks strange, to get the customer session from the request and then to put it back.
			// However, this is taking the customer session from the HttpSession and putting it into the HttpRequest.
			// If this line is removed then all of the Groovy scripts for dynamic content will start to fail due to NPEs.
			request.setAttribute(WebConstants.CUSTOMER_SESSION, customerSession);

			request.setAttribute(WebConstants.TAG_SET, tagCloud);
			request.setAttribute(WebConstants.RENDER_MEDIATOR_GLOBAL_PARAMETER_VALUES, createGlobalParameterValues(shoppingCart));
		} catch (Exception e) {
			LOG.info("Campaign filter exception: ", e);
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * Find supported language.
	 *
	 * @param locale the locale
	 * @param supportedLocales the supported locales
	 * @param fallbackLanguage the fallback language
	 * @return the language
	 */
	protected String findSupportedLanguage(final Locale locale, final Collection<Locale> supportedLocales, final String fallbackLanguage) {
		if (supportedLocales.contains(locale)) {
			return locale.getLanguage();
		}
		
		return fallbackLanguage;
	}
	
	/**
	 * create global parameter values that are to be injected to render mediator.
	 * 
	 * @param shoppingCart the shopping cart
	 * @return map of global parameters
	 */
	Map<String, String> createGlobalParameterValues(final ShoppingCart shoppingCart) {
		final Map<String, String> globals = new HashMap<String, String>();

		final String baseUrl = resolveBasePathFromSfContextUrl();
		globals.put(BASE_URL_PARAMETER_NAME, baseUrl);
		globals.put(BASE_DC_ASSET_URL_PARAMETER_NAME, resolveBaseDcAssetPathFromBaseUrl(baseUrl));
		return globals;
	}

	/**
	 * Resolve the base path to root of storefront.
	 * 
	 * @return elasticPath.sfContentUrl/ Dc Url pattern or BASE_URL_FOR_NO_SF_CONTEXT_PATH or
	 */
	String resolveBasePathFromSfContextUrl() {
		final String baseUrl = storefrontContextUrl;
		if (StringUtils.isBlank(baseUrl)) {
			return BASE_URL_FOR_NO_SF_CONTEXT_PATH;
		}
		return resolveUrlTerminator(baseUrl);
	}

	/**
	 * Resolve asset path to dynamic content assets using base url.
	 * 
	 * @param baseUrl the base url that does not end with terminator.
	 * @return the asset path base url
	 */
	String resolveBaseDcAssetPathFromBaseUrl(final String baseUrl) {
		return resolveUrlTerminator(baseUrl + BASE_DC_ASSET_URL_PATH);
	}

	/**
	 * Remove terminator from the end of the path.
	 * 
	 * @param urlPath the path to check
	 * @return urlPath if the terminator is not the last chatacter or urlPath - terminator otherwise
	 */
	String resolveUrlTerminator(final String urlPath) {
		if (urlPath.endsWith(PATH_TERMINATOR)) {
			return urlPath.substring(0, urlPath.length() - PATH_TERMINATOR.length());
		}
		return urlPath;
	}

	/**
	 * Filter init override.
	 * 
	 * @param filterConfig filter config
	 * @throws ServletException ServletException
	 */
	public void init(final FilterConfig filterConfig) throws ServletException {
		LOG.debug("Targeted Selling Filter initializing ...");
	}

	/**
	 * Sets the requestHelper instance.
	 * 
	 * @param requestHelper -the requesthelper instance.
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

	/**
	 * Gets the requestHelper instance.
	 * 
	 * @return - the current requestHelper instance.
	 */
	protected SfRequestHelper getRequestHelper() {
		return this.requestHelper;
	}

	/**
	 * @param storefrontContextUrl The context url for the storefront.
	 */
	public void setStorefrontContextUrl(final String storefrontContextUrl) {
		this.storefrontContextUrl = storefrontContextUrl;
	}
}
