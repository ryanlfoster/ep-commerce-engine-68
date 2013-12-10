/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.tools.impl;


import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.sfweb.tools.LocaleLinksTool;

/**
 * Presentation support class that can create SEO friendly urls for
 * pages in many locales.
 */
public class LocaleLinksToolImpl implements LocaleLinksTool {
	private SeoUrlBuilder seoBuilderVar = null;
	private String localeControllerUrl = null;

	/**
	 * Gets a map with the changing locale url, for the category page.
	 * For the current locale the value is null.
	 *
	 * @param supportedLocalesByStore - the locale for which to construct the URL
	 * @param currentLocale - the locale for which will not create the URL (usually shopping cart locale)
	 * @param category - the category for which to get the urls
	 * @param pageNumber - the page number
	 * @param isSeoEnabled - if seo is enabled
	 * @return a map
	 */
	@Override
	public Map<Locale, String> getLocaleLinksForCategory(
			final Collection<Locale> supportedLocalesByStore,
			final Locale currentLocale,
			final Category category,
			final Integer pageNumber,
			final boolean isSeoEnabled) {
		Map<Locale, String> categLocaleUrls = new LinkedHashMap<Locale, String>();

		for (Locale localeVar : supportedLocalesByStore) {
			//is the current locale
			if (localeVar.toString().equals(currentLocale.toString())) {
				categLocaleUrls.put(localeVar, null);
			} else {
				categLocaleUrls.put(localeVar, getLocaleLink(localeVar, isSeoEnabled, category, null, pageNumber, null, UrlType.CATEGORY));
			}
		}
		return categLocaleUrls;
	}

	/**
	 * Gets a map with the changing locale url, for the product page.
	 * For the current locale the value is null.
	 *
	 * @param supportedLocalesByStore - the locale for which to construct the URL
	 * @param currentLocale - the locale for which will not create the URL (usually shopping cart locale)
	 * @param category - the category
	 * @param product - the product for which to get the urls
	 * @param isSeoEnabled - if seo is enabled
	 * @return a map
	 */
	@Override
	public Map<Locale, String> getLocaleLinksForProduct(
			final Collection<Locale> supportedLocalesByStore,
			final Locale currentLocale,
			final Category category,
			final Product product,
			final boolean isSeoEnabled) {
		Map<Locale, String> prodLocaleUrls = new LinkedHashMap<Locale, String>();

		for (Locale localeVar : supportedLocalesByStore) {
			//is the current locale
			if (localeVar.toString().equals(currentLocale.toString())) {
				prodLocaleUrls.put(localeVar, null);
			} else {
				prodLocaleUrls.put(localeVar, getLocaleLink(localeVar, isSeoEnabled, category, product, null, null, UrlType.PRODUCT));
			}
		}
		return prodLocaleUrls;
	}

	/**
	 * Gets a map with the changing locale url, for the product page.
	 * For the current locale the value is null.
	 *
	 * @param supportedLocalesByStore - the locale for which to construct the URL
	 * @param currentLocale - the locale for which will not create the URL (usually shopping cart locale)
	 * @param category - the category
	 * @param isSeoEnabled - if seo is enabled
	 * @param pageNumber - the page number we are on
	 * @param brand - the brand
	 * @return a map
	 */
	@Override
	public Map<Locale, String> getLocaleLinksForSitemap(
			final Collection<Locale> supportedLocalesByStore,
			final Locale currentLocale,
			final Category category,
			final Integer pageNumber,
			final Brand brand,
			final boolean isSeoEnabled) {
		Map<Locale, String> sitemapLocaleUrls = new LinkedHashMap<Locale, String>();

		for (Locale localeVar : supportedLocalesByStore) {
			//is the current locale
			if (localeVar.toString().equals(currentLocale.toString())) {
				sitemapLocaleUrls.put(localeVar, null);
			} else {
				sitemapLocaleUrls.put(localeVar, getLocaleLink(localeVar, isSeoEnabled, category, null, pageNumber, brand, UrlType.SITEMAP));
			}
		}
		return sitemapLocaleUrls;
	}

	/**
	 * Gets a map with the changing locale url.
	 * For the current locale the value is null.
	 *
	 * @param supportedLocalesByStore - the locale for which to construct the URL
	 * @param currentLocale - the locale for which will not create the URL (usually shopping cart locale)
	 * @return a map
	 */
	@Override
	public Map<Locale, String> getLocaleLinks(final Collection<Locale> supportedLocalesByStore, final Locale currentLocale) {
		Map<Locale, String> localeUrls = new LinkedHashMap<Locale, String>();

		for (Locale localeVar : supportedLocalesByStore) {
			//is the current locale
			if (localeVar.toString().equals(currentLocale.toString())) {
				localeUrls.put(localeVar, null);
			} else {
				localeUrls.put(localeVar, getLocaleLink(localeVar, false, null, null, null, null, UrlType.OTHER));
			}
		}
		return localeUrls;
	}

	/**
	 * Returns the locale URL for the specified language.
	 *
	 * @param locale - the locale for  which to get the URL
	 * @param isSeoEnabled - is it SEO firendly
	 * @param category - the category
	 * @param product - the product
	 * @param pageNumber - page number
	 * @param brand - brand
	 * @param urlType - what type of URL we need: product, category, sitemap
	 * @return - locale URL for the specified language
	 */
	private String getLocaleLink(
			final Locale locale,
			final boolean isSeoEnabled,
			final Category category,
			final Product product,
			final Integer pageNumber,
			final Brand brand,
			final UrlType urlType) {

		if (isSeoEnabled) {
			switch (urlType) {
				case CATEGORY:
					return seoBuilderVar.categorySeoUrl(category, locale, pageNumber);
				case PRODUCT:
					return seoBuilderVar.productSeoUrl(product, locale);
				case SITEMAP:
					return seoBuilderVar.sitemapSeoUrl(category, brand, locale, pageNumber);
				default:
					break;
			}
		}
		return localeControllerUrl + "?" + WebConstants.LOCALE_PARAMETER_NAME + "=" + locale.toString();
	}

	/**
	 * The SEO creates  differnt URL for this types.
	 */
	private enum UrlType {
		PRODUCT,
		CATEGORY,
		SITEMAP,
		OTHER
	}

	/**
	 * @param seoBuilderVar the seoBuilderVar to set
	 */
	public void setSeoBuilderVar(final SeoUrlBuilder seoBuilderVar) {
		this.seoBuilderVar = seoBuilderVar;
	}

	/**
	 * @param localeControllerUrl the localeControllerUrl to set
	 */
	public void setLocaleControllerUrl(final String localeControllerUrl) {
		this.localeControllerUrl = localeControllerUrl;
	}

}
