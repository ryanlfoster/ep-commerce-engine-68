package com.elasticpath.sfweb.tools;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;

/**
 * Generates maps of Locale to Corresponding URLs based on the properties of the
 * current user's store, as returned from the request info.
 */
public interface RequestScopedLocaleTool {
	/**
	 * Gets a map of Supported Locales to String URLs which specifies the URL the user needs to hit to switch to the given locale.
	 * For the current locale the value, the url in the map is null.
	 *
	 * @param currentLocale the current locale
	 * @return a map
	 */
	Map<Locale, String> getLocaleLinksForCurrentStore(Locale currentLocale);

	/**
	 * Gets a map with the changing locale url, for the category page.
	 * For the current locale the value is null.
	 *
	 *
	 * @param currentLocale - the locale for which will not create the URL (usually shopping cart locale)
	 * @param category - the category for which to get the urls
	 * @param pageNumber - the page number
	 * @param seoEnabled - if seo is enabled
	 * @return a map
	 */
	Map<Locale, String> getLocaleLinksForCategory(
			Locale currentLocale, Category category, Integer pageNumber, boolean seoEnabled);

	/**
	 * Gets a map with the changing locale url, for the product page.
	 * For the current locale the value is null.
	 *
	 * @param currentLocale - the locale for which will not create the URL (usually shopping cart locale)
	 * @param category - the category
	 * @param product - the product for which to get the urls
	 * @param seoEnabled - if seo is enabled
	 * @return a map
	 */
	Map<Locale, String> getLocaleLinksForProduct(
			Locale currentLocale, Category category, Product product, boolean seoEnabled);
}
