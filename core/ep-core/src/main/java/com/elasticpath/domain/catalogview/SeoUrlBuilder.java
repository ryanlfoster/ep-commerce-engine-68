package com.elasticpath.domain.catalogview;

import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;

/**
 * Implementations can provide different ways of encoding SEO Urls.
 */
public interface SeoUrlBuilder {

	/**
	 * Returns the seo url of the given locale in the product's default category.
	 * @param product the product to build the url for.
	 * @param locale the locale the url should be readable in
	 * @return the seo url for the product
	 */
	String productSeoUrl(final Product product, final Locale locale);
	
	/**
	 * Returns the seo url of the given locale following the given category. Since there might be multiple categories to reach a product, the
	 * category you give will be a part of the seo url generated. If you give <code>null</code>, the default category of the product will be used
	 * instead.
	 * @param product the product to build the url for.
	 * @param locale the locale the url should be readable in
	 * @param category the category to reach the product, give <code>null</code> to use the default category.
	 * @return the seo url for the product
	 */
	String productSeoUrl(final Product product, final Locale locale, final Category category);

	/**
	 * Returns the seo url for the specified category,
	 * e.g. cars/bwm/convertibles/cat-356-all.html.
	 * 
	 * @param category the category to get the url for.
	 * @param locale the locale the url fragments should be in.
	 * @param pageNumber the page number
	 * @return the seo url for the category.
	 */
	String categorySeoUrl(final Category category, final Locale locale, final int pageNumber);

	/**
	 * Create an seo url that will use the specified locale and sorter objects during the URL
	 * construction and add all the specified filters (in the order provided).
	 * 
	 * @param locale the locale the url fragments should be in.
	 * @param filters the filters to build seo url for.
	 * @param sortType the type of sorting to perform
	 * @param sortOrder the order of the sorting
	 * @param pageNumber the page number to include in the url. This is ignored if it is less than
	 *            zero.
	 * @return the seo url for the filters specified
	 */
	String filterSeoUrl(final Locale locale, final List<Filter< ? >> filters, final SortBy sortType, final SortOrder sortOrder,
			final int pageNumber);

	/**
	 * Returns the localized seo url for this sitemap result corresponding to the desired page number.
	 * 
	 * @param category the category this sitemap url is for.
	 * @param brand the brand this sitemap url is for.
	 * @param locale the locale the sitemap url fragments should be in.
	 * @param pageNumber the page number of the url.
	 * @return the seo url as a <code>String</code>
	 */
	String sitemapSeoUrl(final Category category, final Brand brand,
			final Locale locale, final int pageNumber);
	
	/**
	 * Sets the store.
	 * 
	 * @param store the store instance
	 */
	void setStore(Store store);
	
	/**
	 * Returns the url path separator used by the implementation.
	 *
	 * @return the url path separator used by the implementation.
	 */
	String getPathSeparator();
	
	/**
	 * Set the field separator - used to encode the filename part of the url
	 * so that the specific product or category can be identified.
	 * 
	 * @param fieldSeparator the string to separate the filename part of the
	 *        url with.
	 */
	void setFieldSeparator(final String fieldSeparator);
}