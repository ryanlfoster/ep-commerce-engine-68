package com.elasticpath.sfweb.tools.impl;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.tools.LocaleLinksTool;
import com.elasticpath.sfweb.tools.RequestScopedLocaleTool;

/**
 * Generates maps of Locale to Corresponding URLs based on the properties of the
 * current user's store, as returned from the request info.
 */
public class RequestScopedLocaleToolImpl implements RequestScopedLocaleTool {

	private StoreConfig storeConfig;
	private LocaleLinksTool localeLinksTool;

	@Override
	public Map<Locale, String> getLocaleLinksForCurrentStore(final Locale currentLocale) {
		Store store = getStoreConfig().getStore();
		return getLocaleLinksTool().getLocaleLinks(store.getSupportedLocales(), currentLocale);
	}

	@Override
	public Map<Locale, String> getLocaleLinksForCategory(
			final Locale currentLocale, final Category category, final Integer pageNumber, final boolean seoEnabled) {
		Store store = getStoreConfig().getStore();
		return getLocaleLinksTool().getLocaleLinksForCategory(store.getSupportedLocales(), currentLocale, category, pageNumber, seoEnabled);
	}

	@Override
	public Map<Locale, String> getLocaleLinksForProduct(
			final Locale currentLocale, final Category category, final Product product, final boolean seoEnabled) {
		Store store = getStoreConfig().getStore();
		return getLocaleLinksTool().getLocaleLinksForProduct(store.getSupportedLocales(), currentLocale, category, product, seoEnabled);
	}

	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}

	protected StoreConfig getStoreConfig() {
		return storeConfig;
	}

	public void setLocaleLinksTool(final LocaleLinksTool localeLinksTool) {
		this.localeLinksTool = localeLinksTool;
	}

	protected LocaleLinksTool getLocaleLinksTool() {
		return localeLinksTool;
	}
}
