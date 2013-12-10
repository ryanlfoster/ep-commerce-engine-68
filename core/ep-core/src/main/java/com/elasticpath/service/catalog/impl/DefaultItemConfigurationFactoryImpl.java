package com.elasticpath.service.catalog.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.catalog.impl.ItemConfigurationImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.DefaultSkuStrategy;
import com.elasticpath.service.catalog.ItemConfigurationFactory;

/**
 * Creates item configurations for products. Chooses the default SKU for any product in the bundles, given the {@link DefaultSkuStrategy}.
 */
public class DefaultItemConfigurationFactoryImpl implements ItemConfigurationFactory {

	private BundleIdentifier bundleIdentifier;

	private DefaultSkuStrategy defaultSkuStrategy;

	/**
	 * Creates the child item.
	 * @param skuCode the SKU code
	 * @param children the children
	 * @param selected whether the item is selected
	 * @param childId the child id
	 *
	 * @return the child item
	 */
	protected ItemConfigurationImpl createChildItem(final String skuCode, final Map<String, ItemConfiguration> children,
			final boolean selected, final String childId) {
		return new ItemConfigurationImpl(skuCode, children, selected, childId);
	}

	/**
	 * Gets the item configurations for the children of a product.
	 *
	 * @param product the product
	 * @param shopper the shopper
	 * @return a map from child IDs to child. A child ID is the constituent GUID for the case of bundle constituent.
	 */
	protected Map<String, ItemConfiguration> getChildrenForProduct(final Product product, final Shopper shopper) {
		if (getBundleIdentifier().isBundle(product)) {
			return createConstituentItemConfigurations(getBundleIdentifier().asProductBundle(product), shopper);
		}
		return Collections.emptyMap();
	}

	@Override
	public ItemConfigurationImpl createItemConfiguration(final Product product, final Shopper shopper) {
		Map<String, ItemConfiguration> children = getChildrenForProduct(product, shopper);
		return createChildItem(getDefaultSkuStrategy().getDefaultSkuCode(product, shopper), children, true, null);
	}

	/**
	 * Creates the item configurations for the constituents of a bundle, and populates the selections of the bundle and the default SKUs.
	 *
	 * @param bundle the bundle
	 * @param shopper the shopper
	 * @return a map from bundle constituent GUID to child.
	 */
	protected Map<String, ItemConfiguration> createConstituentItemConfigurations(final ProductBundle bundle, final Shopper shopper) {
		Map<String, ItemConfiguration> children = new HashMap<String, ItemConfiguration>();
		int selectedCount = 0;
		for (BundleConstituent bundleConstituent : bundle.getConstituents()) {
			boolean shouldSelect = shouldBeSelected(bundle.getSelectionRule(), selectedCount);
			ItemConfigurationImpl child = createItemConfiguration(bundleConstituent, shopper, shouldSelect);
			children.put(child.getItemId(), child);
			if (shouldSelect) {
				++selectedCount;
			}
		}
		return children;
	}

	private ItemConfigurationImpl createItemConfiguration(final BundleConstituent bundleConstituent, final Shopper shopper,
			final boolean shouldSelect) {
		String skuCode = getDefaultSkuStrategy().getDefaultSkuCode(bundleConstituent.getConstituent(), shopper);
		Product product = bundleConstituent.getConstituent().getProduct();
		Map<String, ItemConfiguration> children = getChildrenForProduct(product, shopper);
		return createChildItem(skuCode, children, shouldSelect, bundleConstituent.getGuid());
	}

	private boolean shouldBeSelected(final SelectionRule selectionRule, final int selectedItems) {
		final int selectionRuleParam;
		if (selectionRule == null) {
			selectionRuleParam = 0;
		} else {
			selectionRuleParam = selectionRule.getParameter();
		}
		return (selectionRuleParam == 0 || selectedItems < selectionRuleParam);
	}

	public void setDefaultSkuStrategy(final DefaultSkuStrategy defaultSkuStrategy) {
		this.defaultSkuStrategy = defaultSkuStrategy;
	}

	protected DefaultSkuStrategy getDefaultSkuStrategy() {
		return defaultSkuStrategy;
	}

	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	protected BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}
}
