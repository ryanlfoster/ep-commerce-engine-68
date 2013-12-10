/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.viewbean.impl;

import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.StoreSeoUrlBuilderFactory;
import com.elasticpath.domain.catalogview.sitemap.SitemapResult;
import com.elasticpath.sfweb.viewbean.SitemapResultBean;

/**
 * Represents a bean implementation for sitemap view.
 */
public class SitemapResultBeanImpl extends EpViewBeanImpl implements SitemapResultBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private SitemapResult sitemapResult;

	private int currentPageNumber;

	private int totalPageNumber;
	
	private StoreSeoUrlBuilderFactory storeSeoUrlBuilderFactory;

	/**
	 * Sets the current sitemap result.
	 * 
	 * @param sitemapResult the current sitemap result
	 */
	public void setCurrentSitemapResult(final SitemapResult sitemapResult) {
		this.sitemapResult = sitemapResult;
	}

	/**
	 * Returns the current sitemap result.
	 * 
	 * @return the current sitemap result
	 */
	public SitemapResult getCurrentSitemapResult() {
		return this.sitemapResult;
	}

	/**
	 * Sets the page number.
	 * 
	 * @param pageNumber the page number
	 */
	public void setCurrentPageNumber(final int pageNumber) {
		if (this.sitemapResult == null) {
			throw new IllegalStateException("The site map result must be set before setting the current page number.");
		}
		this.currentPageNumber = pageNumber;
	}

	/**
	 * Returns the page number.
	 * 
	 * @return the page number
	 */
	public int getCurrentPageNumber() {
		if (this.sitemapResult.getProducts() != null) { 
			return Math.min(this.currentPageNumber, getTotalPageNumber());
		}
		return this.currentPageNumber;
	}

	/**
	 * Returns the total number of pages.
	 * 
	 * @return the total number of pages
	 */
	public int getTotalPageNumber() {
		return this.totalPageNumber;
	}
	
	/**
	 * Sets the total number of pages, if different than what's provided by the sitemapResult set. 
	 * @param totalPages in the results.
	 */
	public void setTotalPageNumber(final int totalPages) {
		this.totalPageNumber = totalPages;
	}

	/**
	 * Returns the current result products. This is already paged.
	 * 
	 * @return products 
	 */
	public List<StoreProduct> getProducts() {
		return this.sitemapResult.getProducts();
	}

	/**
	 * Returns the localized seo url for this sitemap result corresponding to the desired page number.
	 * 
	 * @param locale the current locale.
	 * @param pageNumber the page number of the url.
	 * @return the seo url as a <code>String</code>
	 */
	public String getSeoUrl(final Locale locale, final int pageNumber) {
		final SeoUrlBuilder urlBuilder = getStoreSeoUrlBuilderFactory().getStoreSeoUrlBuilder();
		
		return urlBuilder.sitemapSeoUrl(sitemapResult.getCategory(), sitemapResult.getBrand(), locale, pageNumber);
	}

	protected StoreSeoUrlBuilderFactory getStoreSeoUrlBuilderFactory() {
		return storeSeoUrlBuilderFactory;
	}

	public void setStoreSeoUrlBuilderFactory(final StoreSeoUrlBuilderFactory storeSeoUrlBuilderFactory) {
		this.storeSeoUrlBuilderFactory = storeSeoUrlBuilderFactory;
	}
}
