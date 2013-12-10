/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.util.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HttpServletBean;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.UrlUtility;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;

/**
 * Used in urlrewritefilter to map a locale present in the URL information
 * into a request attribute, and redirect to a locale-free URL when the
 * default locale or a non-supported locale is specified.
 */
public class UrlRewriteLocaleResolverImpl extends HttpServletBean {

	private static final long serialVersionUID = -3323390174583928280L;

	private StoreConfig storeConfig;
	private final UrlUtility urlUtility;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	public UrlRewriteLocaleResolverImpl() {
		final ElasticPath elasticPath = ElasticPathImpl.getInstance();
		this.urlUtility = elasticPath.getBean("urlUtility");
	}
	
	/**
	 * Constructor with params.
	 * 
	 * @param urlUtility an UrlUtility class instance
	 */
	public UrlRewriteLocaleResolverImpl(final UrlUtility urlUtility) {
		this.urlUtility = urlUtility;
	}

	/**
	 * Resolve the URL that we have a get request for.
	 * 
	 * @param req the request
	 * @param resp the response
	 * @throws ServletException if the request for the GET could not be handled
	 * @throws IOException If an input or output exception occurs
	 */
	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Received request :" + req.getRequestURI());
		}
		Store store = getRequestStore(req);
		Locale defaultLocale = store.getDefaultLocale();
		Collection<Locale> supportedLocales = store.getSupportedLocales();
		
		Locale locale = getLocaleFromRequestUri(req);
		
		if (locale == null) {
			setLocaleAttribute(req, defaultLocale);
		} else if (defaultLocale.equals(locale) || !supportedLocales.contains(locale)) {
			redirectForLocale(req, resp, locale);
			setLocaleAttribute(req, defaultLocale);
		} else {
			setLocaleAttribute(req, locale);
		}
		
	}

	/**
	 * Get a locale from the request's URI. Returns null if none found.
	 * 
	 * @param req the request
	 * @return a <code>Locale</code> extracted from the URI or null if none present.
	 */
	protected Locale getLocaleFromRequestUri(final HttpServletRequest req) {
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		return urlUtility.getLocaleFromUrl(uri, contextPath);
	}

	/**
	 * Set a redirect on the response for the given request with the specified locale.
	 * 
	 * @param req the request
	 * @param resp the response
	 * @param locale the locale that is in the request URI that needs to be redircted
	 * @throws IOException If an input or output exception occurs
	 */
	@SuppressWarnings("PMD.UseStringBufferForStringAppends")
	protected void redirectForLocale(final HttpServletRequest req, final HttpServletResponse resp, final Locale locale) throws IOException {
		String newURI = req.getRequestURI().replaceFirst("/" + locale + "/", "/");
		if (req.getQueryString() != null && req.getQueryString().length() > 0) {
			newURI = newURI + "?" + req.getQueryString();
		}
		resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		resp.setHeader("Location", newURI);
		resp.setHeader("Connection", "close");
	}
	
	/**
	 * Set the given locale into the appropriate request attribute.
	 * 
	 * @param req the request whose attribute should be set
	 * @param locale the locale to set in the attribute
	 */
	protected void setLocaleAttribute(final HttpServletRequest req, final Locale locale) {
		req.setAttribute(WebConstants.URL_REQUEST_LOCALE, locale.toString());
	}
	
	/**
	 * Get the store for the current request.
	 * 
	 * @param req the request (not used in this implementation). 
	 * @return the <code>Store</code> in the current request
	 */
	protected Store getRequestStore(final HttpServletRequest req) {
		return getStoreConfig().getStore();
	}
	
	/**
	 * Get the store config.
	 * 
	 * @return a <code>StoreConfig</code> object.
	 */
	protected StoreConfig getStoreConfig() {
		return storeConfig;
	}
	
	/**
	 * Initialize the servlet bean.
	 * 
	 * Get the Spring web application context and use it to get the request helper.
	 * 
	 * @throws ServletException in case of servlet exception
	 */
	@Override
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	protected void initServletBean() throws ServletException {
		super.initServletBean();
		ServletContext servletContext = this.getServletContext();
		if (servletContext == null) {
			storeConfig = ElasticPathImpl.getInstance().getBean("threadLocalStorage");
		} else {
			WebApplicationContext webAppContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			storeConfig = (StoreConfig) webAppContext.getBean("threadLocalStorage");
		}
	}

}
