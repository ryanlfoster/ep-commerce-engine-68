/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */

package com.elasticpath.domain.catalogview.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.util.UrlUtility;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.browsing.impl.FilterSeoUrl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;

/**
 * Encapsulates the logic on how to build SEO urls for various components of
 * the system.
 * 
 * This implementation creates a human readable url path (not used to find the 
 * product or category), and appends an encoded filename which can be decoded 
 * to identify a specific product or category.
 * 
 * SeoUrlBuilder should NOT be used directly, use SeoUrlBuilderProxy
 * or StoreSeoUrlBuilderFactory instead.
 */
public class SeoUrlBuilderImpl implements SeoUrlBuilder {
	
	private Utility utility;
	
	/**
	 * Sets up the default field separator, used in the filename part of the url
	 * to actually identify the category or product.  By default it is set to 
	 * the value of {@link SeoConstants.SEPARATOR_BETWEEN_TOKENS}, but can be 
	 * overridden by {@link #setFieldSeparator(String)}
	 */
	private String fieldSeparator;

	private Store store;

	private UrlUtility urlUtility;

	private String urlPathSeparator = "/";
	
	
	/**
	 * Set the utility (used to make friendly urls).
	 * @param utility the instance of utility to use.
	 */
	public void setUtility(final Utility utility) {
		this.utility = utility;
	}
	
	/**
	 * Get the utility (used to make friendly urls).
	 * @return the utility
	 */
	protected Utility getUtility() {
		return utility;
	}

	/**
	 * Sets the URL utility.
	 * 
	 * @param urlUtility the instance of utility to use.
	 */
	public void setUrlUtility(final UrlUtility urlUtility) {
		this.urlUtility = urlUtility;
	}
	
	/**
	 * Get the urlUtility.
	 * @return the urlUtility
	 */
	protected UrlUtility getUrlUtility() {
		return urlUtility;
	}

	/**
	 * Sets the store.
	 * 
	 * @param store the store instance
	 */
	public void setStore(final Store store) {
		this.store = store;
	}
	
	/**
	 * Get store.
	 * @return the store
	 */
	Store getStore() {
		return this.store;
	}
	
	/**
	 * Returns the seo url of the given locale in the product's default category.
	 * @param product the product to build the url for.
	 * @param locale the locale the url should be readable in
	 * @return the seo url for the product
	 */
	public String productSeoUrl(final Product product, final Locale locale) {
		return productSeoUrl(product, locale, null);
	}
	
	/**
	 * Returns the seo url of the given locale following the given category. Since there might be multiple categories to reach a product, the
	 * category you give will be a part of the seo url generated. If you give <code>null</code>, the default category of the product will be used
	 * instead. If the given locale is not the store's default locale it will be added to the SEO URL.
	 * @param product the product to build the url for.
	 * @param locale the locale the url should be readable in
	 * @param category the category to reach the product, give <code>null</code> to use the default category.
	 * @return the seo url for the product
	 */
	public String productSeoUrl(final Product product, final Locale locale, final Category category) {
		StringBuffer url = new StringBuffer();
		
		addLocale(locale, url);
		
		if (category == null) {			
			Catalog storeSpecificCatalog = getStore().getCatalog();
			buildCategoryHierarchy(product.getDefaultCategory(storeSpecificCatalog), locale, url);
		} else {
			buildCategoryHierarchy(category, locale, url);
		}
		url.append(getPathSeparator());
		url.append(findMostPreferableSeoName(product, locale, product.getLocaleDependantFields(locale)));
		url.append(getPathSeparator());
		url.append(SeoConstants.PRODUCT_PREFIX);
		url.append(getProductSeoId(product));
		url.append(SeoConstants.SUFFIX);
		
		return url.toString();
	}
	

	/**
	 * Returns the seo url, E.g. cars/bwm/convertibles/cat-356-all.html.
	 * 
	 * @param category the category to build a seo url for.
	 * @param locale the locale the url fragments should be in.
	 * @param pageNumber the page number
	 * @return the seo url for the category
	 */
	public String categorySeoUrl(final Category category, final Locale locale, final int pageNumber) {

		final StringBuffer url = new StringBuffer();
		
		addLocale(locale, url);
	
		url.append(categorySeoUrlWithoutSuffix(category, locale));
		url.append(getPathSeparator());
		url.append(getCategorySeoId(category));

		url.append(SeoConstants.PAGE_NUMBER_PREFIX);
		if (pageNumber == 0) {
			 url.append(SeoConstants.ALL_PAGE_TOKEN);
		} else {
			url.append(pageNumber);
		}
		url.append(SeoConstants.SUFFIX);
		
		return url.toString();
	}

	/**
	 * Append the locale to the given url is the locale isn't the same as the 
	 * store's default locale. 
	 * 
	 * @param locale current locale to be appended
	 * @param url StringBuffer to append the locale to the end
	 */
	protected void addLocale(final Locale locale, final StringBuffer url) {
		
		if (getStore() == null) {
			throw new IllegalArgumentException("Store is null when adding locale");
		}
		
		if (!getStore().getDefaultLocale().equals(locale)) {
			url.append(locale.toString().toLowerCase());
			url.append(getPathSeparator());
		}
	}	

	/**
	 * Returns the seo without suffix for the specified category,
	 * e.g. cars/bwm/convertibles .
	 * 
	 * @param category the category to build the seo url for.
	 * @param locale the locale the url fragments should be in.
	 * @return the SEO URL without suffix
	 */
	String categorySeoUrlWithoutSuffix(final Category category, final Locale locale) {
		StringBuffer buffer = new StringBuffer();
		buildCategoryHierarchy(category, locale, buffer);
		return buffer.toString();
	}
	

	/**
	 * Get the seo url for the specified brand.
	 *
	 * @param brand the brand to build a seo url for.
	 * @param locale the current <code>Locale</code>
	 * @return the seo url for this brand
	 */
	String brandSeoUrlWithoutSuffix(final Brand brand, final Locale locale) {
		String displayName = brand.getDisplayName(locale, false);

		if (displayName == null) {
			return "";
		}
		return getUtility().escapeName2UrlFriendly(displayName, locale);
	}
	
	
	/**
	 * Create an seo url that will use the specified locale
	 * and sorter objects during the URL construction
	 * and add all the specified filters (in the order provided).
	 * 
	 * @param locale the locale the url fragments should be in.
	 * @param filters the filters to build  seo url for.
	 * @param sortType the type of sorting to perform
	 * @param sortOrder the order of the sorting
	 * @param pageNumber the page number to include in the url.  This is
	 *        ignored if it is less than zero.
	 * @return the seo url for the filters specified
	 */
	public String filterSeoUrl(final Locale locale, final List<Filter< ? >> filters, final SortBy sortType, final SortOrder sortOrder,
			final int pageNumber) {
		StringBuffer url = new StringBuffer();
		addLocale(locale, url);
		url.append(
				new FilterSeoUrl(locale, filters, sortType, sortOrder, pageNumber, getFieldSeparator()).asString());
		return url.toString();
	}
	
	/**
	 * Returns the localized seo url for this sitemap result corresponding to the desired page number.
	 * 
	 * @param category the category to build a sitemap url for.
	 * @param brand the brand to build a sitemap url for.
	 * @param locale the current locale.
	 * @param pageNumber the page number of the url.
	 * @return the seo url as a <code>String</code>
	 */
	public String sitemapSeoUrl(final Category category, final Brand brand, final Locale locale, final int pageNumber) {

		String path = null;
		String itemId = null;
		
		if (category != null && category.getUidPk() > 0) {
			path = categorySeoUrlWithoutSuffix(category, locale);  
			itemId = "c" + category.getCode();
		} else if (brand != null && brand.getUidPk() > 0) {
			path = brandSeoUrlWithoutSuffix(brand, locale);
			itemId = "b" + brand.getGuid();
		}

		StringBuffer seoUrl = new StringBuffer();
		if (itemId != null) {
			addLocale(locale, seoUrl);
			
			if (path != null && !"".equals(path)) {
				seoUrl.append(path);				
				seoUrl.append(getPathSeparator());
			}
			
			seoUrl.append("sitemap");
			seoUrl.append(getFieldSeparator()); 
			seoUrl.append(itemId);
			seoUrl.append(getFieldSeparator());
			seoUrl.append(SeoConstants.PAGE_NUMBER_PREFIX);
			seoUrl.append(pageNumber);
			seoUrl.append(SeoConstants.SUFFIX);
		}

		return seoUrl.toString();
	}
	
	/**
	 * Recursive. Builds the category hierarchy portion of the SEO url.
	 * Calls {@link #findMostPreferableSeoName(Entity, LocaleDependantFields)}.
	 * @param category the category for which the SEO name is required
	 * @param locale the locale in which the URL should be displayed
	 * @param buffer the portion of the URL string to which the SEO name for the given category should be appended.
	 */
	protected void buildCategoryHierarchy(final Category category, final Locale locale, final StringBuffer buffer) {
		if (category == null) {
			return;
		}
		if (category.getParent() != null  && !category.equals(category.getParent())) {
			buildCategoryHierarchy(category.getParent(), locale, buffer);
			buffer.append(getPathSeparator());
		}
		buffer.append(findMostPreferableSeoName(category, locale, category.getLocaleDependantFieldsWithoutFallBack(locale)));
	}

	/**
	 * Deduces the SEO portion of the URL. Tries to obtain the SEO portion from the Locale-
	 * Dependent-Field, falls back to the DisplayName if that value is null or an empty string,
	 * falls back again to the entity's code if the displayName is null.
	 * The string to be returned is URL-escaped.
	 * @param entity the entity whose SEO is being calculated
	 * @param ldf the locale-dependent-field that is the SEO name of the entity
	 * @param locale in which to find the most preferable SEO name
	 * @return the SEO portion of the URL
	 */
	@SuppressWarnings("PMD.ConfusingTernary")
	protected String findMostPreferableSeoName(final Entity entity, final Locale locale, final LocaleDependantFields ldf) {
		if (!StringUtils.isBlank(ldf.getUrl())) {
			return ldf.getUrl();
		} else if (!StringUtils.isBlank(ldf.getDisplayName())) {
			return getUtility().escapeName2UrlFriendly(ldf.getDisplayName(), locale);
		} else {
			return getUtility().escapeName2UrlFriendly(entity.getGuid(), locale);
		}

	}	
	
	private Object getProductSeoId(final Product product) {
		if (product.getGuid() == null) {
			return product.getUidPk();
		}
		return this.getUrlUtility().encodeGuid2UrlFriendly(product.getGuid());
	}
	
	
	private Object getCategorySeoId(final Category category) {
		return buildCategorySeoId(category, category.getGuid() != null);
	}
	
	private StringBuffer buildCategorySeoId(final Category category, final boolean usingGuids) {

		StringBuffer sbf = new StringBuffer();
		if (category.getParent() != null && !category.equals(category.getParent())) {  // double check recursion
			sbf.append(buildCategorySeoId(category.getParent(), usingGuids));
		}
		sbf.append(SeoConstants.CATEGORY_PREFIX);
		if (usingGuids) {
			sbf.append(this.getUrlUtility().encodeGuid2UrlFriendly(category.getGuid()));
		} else {
			sbf.append(category.getUidPk());
		}
		sbf.append(getFieldSeparator());
		return sbf;
	}
	

	/**
	 * Set the field separator - used to encode the filename part of the url
	 * so that the specific product or category can be identified.
	 * 
	 * @param fieldSeparator the string to separate the filename part of the
	 *        url with.
	 */
	public void setFieldSeparator(final String fieldSeparator) {
		if (fieldSeparator == null || "".equals(fieldSeparator)) {
			throw new IllegalArgumentException("SEO field separator cannot be null or empty string");
		}
		this.fieldSeparator = fieldSeparator;
	}

	/**
	 * Get the field separator - used to encode the filename part of the url
	 * so that the specific product or category can be identified.
	 * 
	 * @return fieldSeparator the string to separate the filename part of the url with.
	 */
	protected String getFieldSeparator() {
		return fieldSeparator;
	}

	@Override
	public String getPathSeparator() {
		return urlPathSeparator;
	}
	
	/**
	 * Sets the path separator.
	 * 
	 * @param pathSeparator the path separator
	 */
	public void setPathSeparator(final String pathSeparator) {
		this.urlPathSeparator = pathSeparator;
	}
	
	/**
	 * toString method.
	 * @return a representation of this seoUrlBuilder
	 */
	@Override
	public String toString() {
		StringBuilder desc = new StringBuilder();
		desc.append("Builder for ");
		desc.append(getStore().getCode());
		
		return desc.toString();
	}
}
