package com.elasticpath.domain.catalogview.impl;

import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.StoreSeoUrlBuilderFactory;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;

/**
 * SeoUrlBuilderProxy creates appropriate seoUrlBuilder delegating calls to the seoUrlBuilder. 
 * Primarily used by the templates. 
 */
public class SeoUrlBuilderProxy implements SeoUrlBuilder {

	private StoreSeoUrlBuilderFactory storeSeoUrlBuilderFactory;

	/**
	 * Sets the store, redundant as this could lead to thread safe issues.
	 * 
	 * @deprecated
	 * @param store the store instance
	 */
	@Deprecated
	public void setStore(final Store store) {
		// Should not be used
	}

	/**
	 * Returns the url path separator used by the implementation.
	 * 
	 * @return the url path separator used by the implementation.
	 */
	public String getPathSeparator() {
		SeoUrlBuilder seoUrlBuilder = storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();
		return seoUrlBuilder.getPathSeparator();
	}

	/**
	 * Returns the seo url of the given locale in the product's default category.
	 * 
	 * @param product the product to build the url for.
	 * @param locale the locale the url should be readable in
	 * @return the seo url for the product
	 */
	public String productSeoUrl(final Product product, final Locale locale) {
		SeoUrlBuilder seoUrlBuilder = storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();
		return seoUrlBuilder.productSeoUrl(product, locale);
	}

	/**
	 * Returns the seo url of the given locale following the given category. Since there might be multiple categories to reach a
	 * product, the category you give will be a part of the seo url generated. If you give <code>null</code>, the default
	 * category of the product will be used instead.
	 * 
	 * @param product the product to build the url for.
	 * @param locale the locale the url should be readable in
	 * @param category the category to reach the product, give <code>null</code> to use the default category.
	 * @return the seo url for the product
	 */
	public String productSeoUrl(final Product product, final Locale locale, final Category category) {
		SeoUrlBuilder seoUrlBuilder = storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();
		return seoUrlBuilder.productSeoUrl(product, locale, category);
	}

	/**
	 * Returns the seo url for the specified category, e.g. cars/bwm/convertibles/cat-356-all.html.
	 * 
	 * @param category the category to get the url for.
	 * @param locale the locale the url fragments should be in.
	 * @param pageNumber the page number
	 * @return the seo url for the category.
	 */
	public String categorySeoUrl(final Category category, final Locale locale, final int pageNumber) {
		SeoUrlBuilder seoUrlBuilder = storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();
		return seoUrlBuilder.categorySeoUrl(category, locale, pageNumber);
	}

	/**
	 * Create an seo url that will use the specified locale and sorter objects during the URL construction and add all the
	 * specified filters (in the order provided).
	 * 
	 * @param locale the locale the url fragments should be in.
	 * @param filters the filters to build seo url for.
	 * @param sortType the type of sorting to perform
	 * @param sortOrder the order of the sorting
	 * @param pageNumber the page number to include in the url. This is ignored if it is less than zero.
	 * @return the seo url for the filters specified
	 */
	public String filterSeoUrl(final Locale locale, final List<Filter< ? >> filters, 
			                   final SortBy sortType, final SortOrder sortOrder,
			                   final int pageNumber) {
		SeoUrlBuilder seoUrlBuilder = storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();
		return seoUrlBuilder.filterSeoUrl(locale, filters, sortType, sortOrder, pageNumber);
	}

	/**
	 * Returns the localized seo url for this sitemap result corresponding to the desired page number.
	 * 
	 * @param category the category this sitemap url is for.
	 * @param brand the brand this sitemap url is for.
	 * @param locale the locale the sitemap url fragments should be in.
	 * @param pageNumber the page number of the url.
	 * @return the seo url as a <code>String</code>
	 */
	public String sitemapSeoUrl(final Category category, final Brand brand, final Locale locale, final int pageNumber) {
		SeoUrlBuilder seoUrlBuilder = storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();
		return seoUrlBuilder.sitemapSeoUrl(category, brand, locale, pageNumber);
	}

	/**
	 * Set storeSeoUrlBuilderFactory.
	 * 
	 * @param storeSeoUrlBuilderFactory is the store seoUrlBuilder factory
	 */
	public void setStoreSeoUrlBuilderFactory(final StoreSeoUrlBuilderFactory storeSeoUrlBuilderFactory) {
		this.storeSeoUrlBuilderFactory = storeSeoUrlBuilderFactory;
	}

	@Override
	public void setFieldSeparator(final String fieldSeparator) {
		storeSeoUrlBuilderFactory.resetFieldSeparator(fieldSeparator);	
	}

	
}
