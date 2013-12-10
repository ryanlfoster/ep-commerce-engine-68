package com.elasticpath.domain.catalogview.browsing;

import java.util.List;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalogview.CatalogViewRequest;

/**
 * Represents a catalog browsing request.
 */
public interface BrowsingRequest extends CatalogViewRequest {


	/**
	 * Returns the SEO(Search Engine Optimized) url for this request.
	 *
	 * @return the SEO(Search Engine Optimized) url for this request.
	 */
	String getSeoUrl();

	/**
	 * Returns the SEO(Search Engine Optimized) url for this request.
	 *
	 * @param pageNumber the page number
	 * @return the SEO(Search Engine Optimized) url for this request.
	 */
	String getSeoUrl(int pageNumber);


	/**
	 * Composes and returns a list of <code>FilterSeoUrl</code>s for all filters specified the search request based on the given category.
	 * @return a list of <code>FilterSeoUrl</code>s
	 */
	List<Breadcrumb> getFilterSeoUrls();

	/**
	 * Composes and returns a title for the browsing page. A title will follow this structure:<br>
	 * <tt>Brand Name</tt> in the correct langauge, only if it exists<br>
	 * <tt>Category SEO Title</tt> in the correct langauge<br>
	 * <tt>Price Filter</tt> with currency symbol, only show the lowest applied filter<br>
	 * e.g. Kodak - Digital Cameras - $120 - $140<br>
	 * e.g. Digital Cameras - $200 - $400<br>
	 * e.g. Digital Cameras<br>
	 *
	 * @param category the category
	 * @return a title for the browsing page
	 */
	String getTitle(final Category category);
	
}
