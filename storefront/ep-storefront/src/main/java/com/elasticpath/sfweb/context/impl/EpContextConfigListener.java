/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.context.impl;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.sfweb.EpSfWebException;

/**
 * Bootstrap listener to set up various context config after the springframework context has been loaded.
 * <p>
 * This listener should be registered after ContextLoaderListener in web.xml.
 * </p>
 * <p>
 * For Servlet 2.2 containers and Servlet 2.3 ones that do not initalize listeners before servlets, use ContextLoaderServlet. See the latter's
 * Javadoc for details.
 * </p>
 */
public class EpContextConfigListener extends com.elasticpath.web.context.impl.EpContextConfigListener {
	private static final Logger LOG = Logger.getLogger(EpContextConfigListener.class);

	/**
	 * Complete the context initialization for ElasticPath.
	 *
	 * @param event the servlet context event
	 * @throws EpSfWebException in case of any error happens
	 */
	@Override
	public void contextInitialized(final ServletContextEvent event) throws EpSfWebException {
		try {
			super.contextInitialized(event);

			// load top categories
			doLoadTopCategory(event.getServletContext());
			// Set the SeoUrlBuilder so it's available to the presentation layer.
			doConfigureSeoUrlBuilder(event.getServletContext());
			//load settings to see if seo is enabled or not.
			doLoadSeoEnabled(event.getServletContext());
			//load setting to see if power reviews is enabled.
			doLoadPowerReviewsEnabled(event.getServletContext());
			//load render mediator for content spaces
			doLoadRenderMediator(event.getServletContext());
			//load auto complete search settings
			doAutoCompleteSearchConfigurations(event.getServletContext());
			
		} catch (final Exception e) {
			LOG.error("Caught an exception.", e);
			throw new EpSfWebException("Listener initialization failed.", e);
		}
	}
	
	private void doAutoCompleteSearchConfigurations(final ServletContext servletContext) {
		servletContext.setAttribute(WebConstants.AUTO_COMPLETE_SEARCH_PRICE_ENABLED, getBean("autoCompleteSearchPriceEnabledHelper"));
		servletContext.setAttribute(WebConstants.AUTO_COMPLETE_SEARCH_THUMB_ENABLED, getBean("autoCompleteSearchThumbnailEnabledHelper"));
		servletContext.setAttribute(WebConstants.AUTO_COMPLETE_SEARCH_ENABLED, getBean("autoCompleteSearchEnabledHelper"));
		servletContext.setAttribute(WebConstants.AUTO_COMPLETE_SEARCH_MAX_RESULTS, getBean("autoCompleteSearchNumberOfResultsHelper"));
	}

	private void doLoadPowerReviewsEnabled(final ServletContext servletContext) {
		servletContext.setAttribute(WebConstants.POWERREVIEWSENABLEDHELPER, getBean("powerReviewsEnabledHelper"));
	}

	private void doLoadTopCategory(final ServletContext servletContext) {		
		servletContext.setAttribute(WebConstants.TOPCATEGORIESHELPER, getBean("topCategoriesHelper"));
	}
	
	private void doConfigureSeoUrlBuilder(final ServletContext servletContext) {
		servletContext.setAttribute(WebConstants.SEOURLBUILDER, getBean("seoUrlBuilderProxy"));
		servletContext.setAttribute(WebConstants.LOCALE_URL_TOOL, getBean("localeUrlTool"));
		servletContext.setAttribute(WebConstants.REQUEST_LOCALE_URL_TOOL, getBean("requestScopedLocaleTool"));
	}
	
	private void doLoadSeoEnabled(final ServletContext servletContext) {		
		servletContext.setAttribute(WebConstants.SEOENABLEDHELPER, getBean("seoEnabledHelper"));
	}
	
	private void doLoadRenderMediator(final ServletContext servletContext) {		
		servletContext.setAttribute(WebConstants.RENDER_MEDIATOR, getBean("renderMediator"));
	}
	
}
