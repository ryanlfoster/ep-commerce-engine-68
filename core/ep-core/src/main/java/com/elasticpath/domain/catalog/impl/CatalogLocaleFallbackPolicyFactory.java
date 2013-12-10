package com.elasticpath.domain.catalog.impl;

import java.util.Locale;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import com.elasticpath.domain.localization.impl.LocaleBroadeningFallbackPolicy;
/**
 * Factory to create LocaleFallbackPolicies for catalog related items.
 *
 */
public class CatalogLocaleFallbackPolicyFactory {
	/**
	 * Creates a LocaleFallbackPolicy for Product.
	 * 
	 * @param primaryLocale the primary Locale
	 * @param fallback whether to fallback to master catalog default or not
	 * @param product the product
	 * 
	 * @return policy the product locale fallback poloicy
	 */
	public LocaleFallbackPolicy createProductLocaleFallbackPolicy(final Locale primaryLocale, final boolean fallback, final Product product) {
		LocaleFallbackPolicy policy = new LocaleBroadeningFallbackPolicy();
		policy.addLocale(primaryLocale);
		if (fallback) {
			policy.addLocale(product.getMasterCatalog().getDefaultLocale());
		}
		return policy;
	}
	/**
	 * Create a LocaleFallbackPolicy for a Category.
	 * @param primaryLocale the primary Locale
	 * @param fallback whether to fallback to master catalog default or not
	 * @param category the category
	 * @return policy the category locale fallback policy
	 */
	public LocaleFallbackPolicy createCategoryLocaleFallbackPolicy(final Locale primaryLocale, final boolean fallback, final Category category) {
		LocaleFallbackPolicy policy = new LocaleBroadeningFallbackPolicy();
		policy.addLocale(primaryLocale);
		if (fallback) {
			policy.addLocale(category.getCatalog().getDefaultLocale());
		}
		return policy;
	}
	
}