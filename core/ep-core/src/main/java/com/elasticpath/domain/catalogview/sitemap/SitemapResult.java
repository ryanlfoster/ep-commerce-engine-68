package com.elasticpath.domain.catalogview.sitemap;

import java.util.List;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalogview.StoreProduct;

/**
 * Represents the result returned for a certain <code>SitemapRequest</code>.
 */
public interface SitemapResult extends EpDomain {

	/**
	 * Returns a list of products as the search result.
	 *
	 * @return a list of products as the search result.
	 */
	List<StoreProduct> getProducts();

	/**
	 * Sets a list of products as the search result.
	 *
	 * @param products a list of products as the search result.
	 */
	void setProducts(final List<StoreProduct> products);

	/**
	 * Returns the sitemap request which generated this search result.
	 *
	 * @return the sitemap request
	 */
	SitemapRequest getSitemapRequest();

	/**
	 * Sets the sitemap request which generated this search result.
	 *
	 * @param sitemapRequest the sitemap request
	 */
	void setSitemapRequest(final SitemapRequest sitemapRequest);

	/**
	 * Sets the category.
	 *
	 * @param category the category to set
	 */
	void setCategory(final Category category);

	/**
	 * Returns the category.
	 *
	 * @return the category.
	 */
	Category getCategory();

	/**
	 * Sets the brand.
	 *
	 * @param brand the brand to set
	 */
	void setBrand(final Brand brand);

	/**
	 * Returns the brand.
	 *
	 * @return the brand.
	 */
	Brand getBrand();

	/**
	 * Returns the amount of results returned.
	 *
	 * @return the number of results
	 */
	int getResultCount();

	/**
	 * Sets the amount of results returned.
	 *
	 * @param resultCount the number of results
	 */
	void setResultCount(int resultCount);

	/**
	 *
	 * @return the brandListing
	 */
	List<Brand> getBrandListing();

	/**
	 *
	 * @param brandListing the brandListing to set
	 */
	void setBrandListing(List<Brand> brandListing);

	/**
	 *
	 * @return the categoryListing
	 */
	List<Category> getCategoryListing();

	/**
	 *
	 * @param categoryListing the categoryListing to set
	 */
	void setCategoryListing(List<Category> categoryListing);
}