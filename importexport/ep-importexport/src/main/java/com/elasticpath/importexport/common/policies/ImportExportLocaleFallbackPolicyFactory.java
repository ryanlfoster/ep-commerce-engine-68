package com.elasticpath.importexport.common.policies;

import java.util.Locale;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.CatalogLocaleFallbackPolicyFactory;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import com.elasticpath.domain.localization.impl.SimpleLocaleFallbackPolicy;

/**
 * Factory to create LocaleFallbackPolicies for product and category for import export related objects.
 *
 */
public class ImportExportLocaleFallbackPolicyFactory extends CatalogLocaleFallbackPolicyFactory {

	/**
	 * Creates a LocaleFallbackPolicy for Product.
	 * 
	 * @param primaryLocale the primary Locale
	 * @param fallback whether to fallback to master catalog default or not
	 * @param product the product
	 * 
	 * @return policy the product locale fallback poloicy
	 */
	@Override
	public LocaleFallbackPolicy createProductLocaleFallbackPolicy(final Locale primaryLocale, final boolean fallback, final Product product) {
		LocaleFallbackPolicy policy = new SimpleLocaleFallbackPolicy();
		policy.addLocale(primaryLocale);
		if (fallback) {
			policy.addLocale(product.getMasterCatalog().getDefaultLocale());
		}
		return policy;
	}
}
