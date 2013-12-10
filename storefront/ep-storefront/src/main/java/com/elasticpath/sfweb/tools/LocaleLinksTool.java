package com.elasticpath.sfweb.tools;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;

/**
 * Class Description.
 */
public interface LocaleLinksTool {
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
	Map<Locale, String> getLocaleLinksForCategory(
			Collection<Locale> supportedLocalesByStore, Locale currentLocale, Category category, Integer pageNumber, boolean isSeoEnabled);

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
	Map<Locale, String> getLocaleLinksForProduct(
			Collection<Locale> supportedLocalesByStore, Locale currentLocale, Category category, Product product, boolean isSeoEnabled);

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
	Map<Locale, String> getLocaleLinksForSitemap(
			Collection<Locale> supportedLocalesByStore, Locale currentLocale, Category category, Integer pageNumber, Brand brand,
			boolean isSeoEnabled);

	/**
	 * Gets a map with the changing locale url.
	 * For the current locale the value is null.
	 *
	 * @param supportedLocalesByStore - the locale for which to construct the URL
	 * @param currentLocale - the locale for which will not create the URL (usually shopping cart locale)
	 * @return a map
	 */
	Map<Locale, String> getLocaleLinks(Collection<Locale> supportedLocalesByStore, Locale currentLocale);
}
