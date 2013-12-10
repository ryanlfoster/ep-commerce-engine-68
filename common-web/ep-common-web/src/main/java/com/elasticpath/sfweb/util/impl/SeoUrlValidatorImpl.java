/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.util.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.UrlUtility;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.sfweb.util.SeoUrlValidator;

/**
 * The default implementation of the {@link SeoUrlValidator}.
 * This implementation relies on the algorithms built in the {@link SeoUrlBuilder} and 
 * the fact that the {@link UrlRewriteResolverImpl} is being used.
 */
public class SeoUrlValidatorImpl implements SeoUrlValidator {

	private static final Logger LOG = Logger.getLogger(SeoUrlValidatorImpl.class);
	
	private static final int GROUP_MAIN_SEO_PART = 1;
	
	private SeoUrlBuilder seoUrlBuilder;

	private Pattern categoryUrlPattern;

	private Pattern sitemapUrlPattern;

	/**
	 * A field that assigns whether this validator should be enabled/disabled. 
	 */
	private boolean enabled;

	private SfRequestHelper requestHelper;

	private UrlUtility urlUtility;


	/**
	 * Validates a product request URL against its original SEO URL.
	 * 
	 * @param product the product
	 * @param locale the locale the product is being viewed in
	 * @param request the servlet request
	 * @return true if the URL saved in the request is valid
	 */
	public boolean validateProductUrl(final Product product, final Locale locale, final HttpServletRequest request) {
		final String requestUrl = getSeoUrl(request);
		
		if (!enabled || request == null || requestUrl == null) {
			// if the request does not come with the expected SEO URL attribute 
			// then we cannot perform a SEO URL check
			return true;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Validating SEO URL for product(code:" + product.getCode() + "), URL: " + requestUrl);
		}

		final String contextPath = getContextPath(request);
		final UrlSegmentValidator validator = createProductUrlValidator(product, contextPath);

		ensureLocaleIsAllowed(requestUrl, contextPath, validator);
		return validator.validate(requestUrl);
	}

	/**
	 * Gets the web application context path.
	 * 
	 * @param request the http request
	 * @return the context path
	 */
	protected String getContextPath(final HttpServletRequest request) {
		return request.getContextPath();
	}

	/**
	 * Creates a product URL validator.
	 * 
	 * @param product the product instance
	 * @param context the web application context
	 * @return an instance of {@link UrlSegmentValidator}
	 */
	protected UrlSegmentValidator createProductUrlValidator(final Product product, final String context) {
		final UrlSegmentValidator validator = newUrlSegmentValidator();
		final Store store = requestHelper.getStoreConfig().getStore();

		for (Locale locale : store.getSupportedLocales()) {
			final String productSeoUrl = seoUrlBuilder.productSeoUrl(product, locale);
			validator.addAllowedSegments(productSeoUrl, locale.toString());
		}
		
		// the context is a valid URL segment
		validator.addAllowedSegments(context);
		
		return validator;
	}

	/**
	 * Retrieves the original SEO URL string from a request.
	 * 
	 * @param request the request object
	 * @return the SEO URL string
	 */
	protected String getSeoUrl(final HttpServletRequest request) {
		return (String) request.getAttribute(WebConstants.SEO_URL_STRING);
	}

	/**
	 * 
	 * @param seoUrlBuilder the SeoUrlBuilder instance
	 */
	public void setSeoUrlBuilder(final SeoUrlBuilder seoUrlBuilder) {
		this.seoUrlBuilder = seoUrlBuilder;
	}

	/**
	 * Validates the category URL against the original SEO URL.
	 * 
	 * @param category the category
	 * @param filters browsing filters or null if none
	 * @param locale the locale
	 * @param request the request
	 * @return true if the URL saved in the request is valid
	 */
	public boolean validateCategoryUrl(final Category category, final List<Filter< ? >>  filters, final Locale locale, 
			final HttpServletRequest request) {

		final String requestUrl = getSeoUrl(request);

		if (!enabled || request == null || requestUrl == null) {
			// if the request does not come with the expected SEO URL attribute 
			// then we cannot perform a SEO URL check
			return true;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Validate SEO URL for category(code:" + category.getCode() + "), URL: " + requestUrl);
		}
		
		final String contextPath = getContextPath(request);
		final String requestSeoPart = extractCategorySeoPart(requestUrl);
		final UrlSegmentValidator validator = createCategoryUrlValidator(category, filters, contextPath);

		ensureLocaleIsAllowed(requestUrl, contextPath, validator);

		return validator.validate(requestSeoPart);
	}

	/**
	 * Allow for whatever locale was specified in the request URL.
	 * If the locale is not a store supported locale, a fallback to the default one will be performed.
	 * 
	 * @param requestUrl the requested URL
	 * @param contextPath the web application context path
	 * @param validator the validator
	 */
	private void ensureLocaleIsAllowed(final String requestUrl, final String contextPath, final UrlSegmentValidator validator) {
		final Locale requestedLocale = urlUtility.getLocaleFromUrl(requestUrl, contextPath);
		if (requestedLocale != null) {
			validator.addAllowedSegments(requestedLocale.toString());
		}
	}

	/**
	 * Creates a new category URL validator.
	 * 
	 * @param category the category instance
	 * @param filters the filters that apply or null if none
	 * @param context the web application context
	 * @return an instance of the {@link UrlSegmentValidator}
	 */
	protected UrlSegmentValidator createCategoryUrlValidator(final Category category, 
			final List<Filter< ? >> filters, final String context) {
		
		final UrlSegmentValidator result = newUrlSegmentValidator();
		final Store store = requestHelper.getStoreConfig().getStore();

		for (Locale locale : store.getSupportedLocales()) {
			final String allowedCategoryUrl;
			if (CollectionUtils.isEmpty(filters)) {
				allowedCategoryUrl = seoUrlBuilder.categorySeoUrl(category, locale, 0);
			} else {
				allowedCategoryUrl = seoUrlBuilder.filterSeoUrl(locale, filters, null, null, 0);
			}
			final String categorySeoPart = extractCategorySeoPart(allowedCategoryUrl);

			result.addAllowedSegments(categorySeoPart, locale.toString());
		}
		// the context is a valid URL segment
		result.addAllowedSegments(context);
		
		return result;
	}

	/**
	 * Creates a new instance of the {@link UrlSegmentValidator}.
	 * 
	 * @return an instance of UrlValidator
	 */
	protected UrlSegmentValidator newUrlSegmentValidator() {
		return new UrlSegmentValidatorImpl();
	}

	/**
	 * Validates the sitemap URL by building it again and then comparing it to the one passed in the request.
	 * 
	 * @param category the category
	 * @param brand the brand
	 * @param locale the current locale
	 * @param request the servlet request
	 * @return true if the URL saved in the request is valid
	 */
	public boolean validateSitemapUrl(final Category category, final Brand brand, final Locale locale, 
			final HttpServletRequest request) {
		
		final String requestUrl = getSeoUrl(request);
		
		if (!enabled || request == null || requestUrl == null) {
			// if the request does not come with the expected SEO URL attribute 
			// then we cannot perform a SEO URL check
			return true;
		}

		if (LOG.isDebugEnabled()) {
			String categoryStr = null;
			if (category != null) {
				categoryStr = category.getCode();
			}
			String brandStr = null;
			if (brand != null) {
				brandStr = brand.getCode();
			}

			LOG.debug("Validating SEO URL for sitemap(categoryCode:" + categoryStr + ", brandCode: " + brandStr + "), URL: " + requestUrl);
		}

		final String contextPath = getContextPath(request);
		final String requestSeoPart = extractSitemapSeoPart(requestUrl);
		
		final UrlSegmentValidator validator = createSitemapUrlValidator(category, brand, contextPath);

		ensureLocaleIsAllowed(requestUrl, contextPath, validator);

		return validator.validate(requestSeoPart);
	}
	
	/**
	 * Creates a new validator for all the supported locales in <code>store</code>.
	 * 
	 * @param category the category instance
	 * @param brand the brand instance
	 * @param context the web application context
	 * @return an instance of the {@link UrlSegmentValidator}
	 */
	protected UrlSegmentValidator createSitemapUrlValidator(final Category category, 
			final Brand brand, final String context) {
		
		final UrlSegmentValidator result = newUrlSegmentValidator();
		final Store store = requestHelper.getStoreConfig().getStore();

		for (Locale locale : store.getSupportedLocales()) {
			final String allowedSitemapUrl = seoUrlBuilder.sitemapSeoUrl(category, brand, locale, 0);
			final String sitemapSeoPart = extractSitemapSeoPart(allowedSitemapUrl);

			result.addAllowedSegments(sitemapSeoPart, locale.toString());
		}
		
		// the context is a valid URL segment
		result.addAllowedSegments(context);
		return result;
	}

	/**
	 * Extracts the first SEO part of this URL by following the category SEO convention.
	 * 
	 * @param requestUrl the request URL
	 * @return the page number requested in this URL
	 */
	protected String extractCategorySeoPart(final String requestUrl) {
		return getUrlRegularExpressionGroup(requestUrl, categoryUrlPattern, GROUP_MAIN_SEO_PART);
	}

	/**
	 * Gets a specific group parsed by a pattern applied on the requestUrl.
	 */
	private String getUrlRegularExpressionGroup(final String requestUrl, final Pattern pattern, final int groupNumber) {
		final Matcher matcher = pattern.matcher(requestUrl);
		final int groupCount = matcher.groupCount();
		if (matcher.matches() && groupCount >= groupNumber) {
			return matcher.group(groupNumber);
		}

		return null;
	}

	/**
	 * Sets whether the {@link SeoUrlValidator} should be activated.
	 * 
	 * @param enabled the value of the variable
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Extracts the sitemap SEO URL main part.
	 * 
	 * @param requestUrl the request URL
	 * @return the first part of the any sitemap URL
	 */
	protected String extractSitemapSeoPart(final String requestUrl) {
		return getUrlRegularExpressionGroup(requestUrl, sitemapUrlPattern, GROUP_MAIN_SEO_PART);
	}
	
	/**
	 * Sets the category URL pattern.
	 * 
	 * @param categoryUrlPatternStr the category URL pattern
	 */
	public void setCategoryUrlPattern(final String categoryUrlPatternStr) {
		this.categoryUrlPattern = Pattern.compile(categoryUrlPatternStr);
	}
	
	/**
	 * Sets the sitemap URL pattern.
	 * 
	 * @param sitemapUrlPatternStr the sitemap URL pattern
	 */
	public void setSitemapUrlPattern(final String sitemapUrlPatternStr) {
		this.sitemapUrlPattern = Pattern.compile(sitemapUrlPatternStr);
	}
	
	/**
	 * Sets the request helper.
	 * 
	 * @param requestHelper the request helper
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}
	
	/**
	 * Sets the URL utility instance.
	 * 
	 * @param urlUtility the instance of {@link UrlUtility}
	 */
	public void setUrlUtility(final UrlUtility urlUtility) {
		this.urlUtility = urlUtility;
	}
	
	/**
	 * A validator for checking whether a URL is correct.
	 */
	public interface UrlSegmentValidator {

		/**
		 * Validates a URL.
		 * 
		 * @param url the URL to validate
		 * @return true if the URL is valid
		 */
		boolean validate(String url);
		
		/**
		 * Adds allowed segments to validate against. A segment is considered to be either of the following:
		 * <br>
		 * <li> a {@link SeoUrlBuilder#getPathSeparator()} separated path elements such as: "/context/pathElement1/pathElement2"</li>
		 * <li> just one path element such as: "pathElement" </li>
		 * 
		 * @param allowedSegments a list of segments that are allowed
		 */
		void addAllowedSegments(String... allowedSegments);
	}
	
	/**
	 * Validates a URL against its path segments.
	 */
	protected class UrlSegmentValidatorImpl implements UrlSegmentValidator {

		private final Set<String> allowedUrlSegments = new HashSet<String>();
		
		/**
		 * Validates a URL by splitting it into path segments 
		 * and checking whether each of them is part of the list of
		 * allowed segments.
		 * 
		 * @param url the URL to check
		 * @return true if all the segments of the <code>url</code> are contained in the allowed URL segments
		 */
		public boolean validate(final String url) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Validation requestedURL: " + url);
			}
			for (String segment : url.trim().split(seoUrlBuilder.getPathSeparator())) {
				if (!StringUtils.isEmpty(segment) && !allowedUrlSegments.contains(segment)) {
					return false;
				}
			}
			return true;
		}
		
		/**
		 * Adds an allowed URL by making it into path segments and 
		 * adding them to the allowed URL path segments list.
		 * 
		 * @param allowedSegments the segments that are allowed
		 */
		public void addAllowedSegments(final String... allowedSegments) {
			for (String allowedSegment : allowedSegments) {
				for (String segment : allowedSegment.split(seoUrlBuilder.getPathSeparator())) {
					this.allowedUrlSegments.add(segment);
				}
			}
		}
	}

}
