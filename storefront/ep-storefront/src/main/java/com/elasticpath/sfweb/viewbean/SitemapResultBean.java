/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.viewbean;

import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.sitemap.SitemapResult;

/**
 * Represents a bean for sitemap.
 */
public interface SitemapResultBean extends EpViewBean {

	/**
	 * Sets the current sitemap result.
	 * 
	 * @param sitemapResult the current sitemap result
	 */
	void setCurrentSitemapResult(SitemapResult sitemapResult);

	/**
	 * Returns the current sitemap result.
	 * 
	 * @return the current sitemap result
	 */
	SitemapResult getCurrentSitemapResult();

	/**
	 * Sets the current page number.
	 * 
	 * @param pageNumber the current page number
	 */
	void setCurrentPageNumber(int pageNumber);
	
	/**
	 * Returns the current page number.
	 * 
	 * @return the current page number
	 */
	int getCurrentPageNumber();

	/**
	 * Returns the total page number.
	 * 
	 * @return the total page number
	 */
	int getTotalPageNumber();

	/**
	 * Sets the total number of pages, if different than what's provided by the sitemapResult set. 
	 * @param totalPages in the results.
	 */
	void setTotalPageNumber(int totalPages);
	
	/**
	 * Returns all resulting products. This is already paged.
	 * 
	 * @return products in the result bean
	 */
	List<StoreProduct> getProducts();

	/**
	 * Returns the localized seo url for this sitemap result corresponding to the desired page number.
	 * 
	 * @param locale the current locale.
	 * @param pageNumber the page number of the url.
	 * @return the seo url as a <code>String</code>
	 */
	String getSeoUrl(final Locale locale, final int pageNumber);
}
