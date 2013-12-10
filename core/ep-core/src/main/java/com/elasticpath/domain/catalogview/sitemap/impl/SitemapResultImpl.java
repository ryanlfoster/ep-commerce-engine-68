/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview.sitemap.impl;

import java.util.List;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.sitemap.SitemapRequest;
import com.elasticpath.domain.catalogview.sitemap.SitemapResult;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * Default implementation of <code>SitemapResult</code>.
 */
public class SitemapResultImpl extends AbstractEpDomainImpl implements SitemapResult {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private List<StoreProduct> products;

	private SitemapRequest sitemapRequest;

	private Category category;

	private Brand brand;

	private List<Brand> brandListing;

	private List<Category> categoryListing;

	private int resultCount;

	/**
	 * Default constructor.
	 */
	public SitemapResultImpl() {
		category = null;
		brand = null;
		products = null;
	}

	/**
	 * Do the sanity check.
	 */
	protected void sanityCheck() {
		if (products == null || sitemapRequest == null) {
			throw new EpDomainException("Not initialized correctly.");
		}
	}

	/**
	 * Returns a list of products as the search result.
	 *
	 * @return a list of products as the search result.
	 */
	public List<StoreProduct> getProducts() {
		return products;
	}

	/**
	 * Sets a list of products as the search result.
	 *
	 * @param products a list of products as the search result.
	 */
	public void setProducts(final List<StoreProduct> products) {
		if (this.products == null) {
			this.products = products;
		} else {
			throw new EpDomainException("Products can only be set once.");
		}
	}

	/**
	 * Returns the sitemap request which generated this search result.
	 *
	 * @return the sitemap request
	 */
	public SitemapRequest getSitemapRequest() {
		return sitemapRequest;
	}

	/**
	 * Sets the sitemap request which generated this search result.
	 *
	 * @param sitemapRequest the sitemap request
	 */
	public void setSitemapRequest(final SitemapRequest sitemapRequest) {
		this.sitemapRequest = sitemapRequest;
	}

	/**
	 * Sets the category.
	 *
	 * @param category the category to set
	 */
	public void setCategory(final Category category) {
		this.category = category;
	}

	/**
	 * Returns the category.
	 *
	 * @return the category.
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * Sets the brand.
	 *
	 * @param brand the brand to set
	 */
	public void setBrand(final Brand brand) {
		this.brand = brand;
	}

	/**
	 * Returns the brand.
	 *
	 * @return the brand.
	 */
	public Brand getBrand() {
		return brand;
	}

	/**
	 * Returns the amount of results returned.
	 *
	 * @return the number of results
	 */
	public int getResultCount() {
		return resultCount;
	}

	/**
	 * Sets the amount of results returned.
	 *
	 * @param resultCount the number of results
	 */
	public void setResultCount(final int resultCount) {
		this.resultCount = resultCount;
	}

	/**
	 *
	 * @return the brandListing
	 */
	public List<Brand> getBrandListing() {
		return brandListing;
	}

	/**
	 *
	 * @param brandListing the brandListing to set
	 */
	public void setBrandListing(final List<Brand> brandListing) {
		this.brandListing = brandListing;
	}

	/**
	 *
	 * @return the categoryListing
	 */
	public List<Category> getCategoryListing() {
		return categoryListing;
	}

	/**
	 *
	 * @param categoryListing the categoryListing to set
	 */
	public void setCategoryListing(final List<Category> categoryListing) {
		this.categoryListing = categoryListing;
	}
}
