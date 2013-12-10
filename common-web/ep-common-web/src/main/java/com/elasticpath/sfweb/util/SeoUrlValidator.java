/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.util;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.Filter;

/**
 * SEO URL validator validates the requested URLs.
 * Its goal is to catch URLs that are invalid in terms that there could be
 * some extra or changed parts of the SEO URL and it could still be valid 
 * although it shouldn't.
 * 
 * The validator makes sure that the requested URL is the one that was set 
 * by the {@link com.elasticpath.domain.catalogview.SeoUrlBuilder}.
 */
public interface SeoUrlValidator {

	/**
	 * Validates a product SEO URL against the requested one set in the request.
	 * 
	 * @param product the product to validate against
	 * @param locale the current locale
	 * @param request the servlet request
	 * @return true if the URL saved in the request is valid
	 */
	boolean validateProductUrl(Product product, Locale locale, 
			HttpServletRequest request);

	/**
	 * Validates a category SEO URL against the requested one set in the request.
	 * 
	 * @param category the category
	 * @param filters the browsing filters 
	 * @param locale the current locale
	 * @param request the servlet request
	 * @return true if the URL saved in the request is valid
	 */
	boolean validateCategoryUrl(Category category, List<Filter< ? >> filters, Locale locale, 
			HttpServletRequest request);

	/**
	 * Validates a sitemap SEO URL against the requested one set in the request.
	 * 
	 * @param category the category
	 * @param brand the brand
	 * @param locale the current locale
	 * @param request the servlet request
	 * @return true if the URL saved in the request is valid
	 */
	boolean validateSitemapUrl(Category category, Brand brand, Locale locale, 
			HttpServletRequest request);

}
