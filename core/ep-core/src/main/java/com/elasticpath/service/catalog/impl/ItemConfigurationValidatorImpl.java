package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ItemConfigurationValidationResult;
import com.elasticpath.service.catalog.ItemConfigurationValidationResult.ItemConfigurationValidationStatus;
import com.elasticpath.service.catalog.ItemConfigurationValidator;
import com.elasticpath.service.catalog.ProductCharacteristicsService;
import com.elasticpath.service.catalog.ProductSkuService;

/**
 * Validates item configurations.
 */
public class ItemConfigurationValidatorImpl implements ItemConfigurationValidator {

	private ProductSkuService productSkuService;
	private ProductCharacteristicsService productCharacteristicsService;

	private BundleIdentifier bundleIdentifier;

	@Override
	public ItemConfigurationValidationResult validate(final ItemConfiguration itemConfiguration) {
		long skuUid = productSkuService.findUidBySkuCode(itemConfiguration.getSkuCode());
		List<String> rootPath = Collections.<String>emptyList();
		if (skuUid == 0) {
			return new ItemConfigurationValidationResult(ItemConfigurationValidationStatus.INVALID_SKU_CODE, rootPath);
		}
		ProductCharacteristics productCharacteristics = getProductCharacteristicsService().getProductCharacteristicsForSkuCode(
				itemConfiguration.getSkuCode());
		if (productCharacteristics.isBundle()) {
			ProductSku sku = getProductSkuService().findBySkuCode(itemConfiguration.getSkuCode());
			return validateBundle(bundleIdentifier.asProductBundle(sku.getProduct()), itemConfiguration, rootPath, true);
		}
		return ItemConfigurationValidationResult.SUCCESS;
	}

	/**
	 * Validate a child item configuration.
	 *
	 * @param constituent the constituent related to the child item
	 * @param itemConfiguration the child item configuration
	 * @param currentPath the path to this child
	 * @return the item configuration validation result
	 */
	protected ItemConfigurationValidationResult validateChild(final BundleConstituent constituent,
			final ItemConfiguration itemConfiguration,
			final List<String> currentPath) {
		ConstituentItem constituentItem = constituent.getConstituent();
		if (constituentItem.isProductSku()) {
			if (!constituentItem.getCode().equals(itemConfiguration.getSkuCode())) {
				return new ItemConfigurationValidationResult(ItemConfigurationValidationStatus.INVALID_SKU_CODE_FOR_SKU_CONSTITUENT, currentPath);
			}
		} else {
			Product product = constituentItem.getProduct();
			ProductSku sku = product.getSkuByCode(itemConfiguration.getSkuCode());
			if (sku == null) {
				return new ItemConfigurationValidationResult(ItemConfigurationValidationStatus.INVALID_SKU_CODE, currentPath);
			}
			if (constituentItem.isBundle()) {
				return validateBundle(bundleIdentifier.asProductBundle(product), itemConfiguration, currentPath, itemConfiguration.isSelected());
			}
		}
		return ItemConfigurationValidationResult.SUCCESS;
	}

	/**
	 * Validate the configuration of a bundle.
	 *
	 * @param bundle the bundle
	 * @param itemConfiguration the item configuration to be validated
	 * @param currentPath the path to this child
	 * @param selected whether the bundle is selected
	 * @return the item configuration validation result
	 */
	protected ItemConfigurationValidationResult validateBundle(final ProductBundle bundle,
			final ItemConfiguration itemConfiguration,
			final List<String> currentPath,
			final boolean selected) {
		if (!validateBundleStructure(bundle, itemConfiguration)) {
			return new ItemConfigurationValidationResult(ItemConfigurationValidationStatus.BUNDLE_DEFINITION_CHANGED, currentPath);
		}

		if (selected && !validateBundleSelectionRule(bundle, itemConfiguration)) {
			return new ItemConfigurationValidationResult(ItemConfigurationValidationStatus.SELECTION_RULE_VIOLATED, currentPath);
		}

		final List<String> constituentPath = new ArrayList<String>(currentPath);
		final int constituentIndex = currentPath.size();

		for (BundleConstituent constituent : bundle.getConstituents()) {
			String childId = constituent.getGuid();
			constituentPath.add(childId);
			ItemConfigurationValidationResult childResult = validateChild(constituent, itemConfiguration.getChildById(childId), constituentPath);
			if (!childResult.getStatus().isSuccessful()) {
				return childResult;
			}
			constituentPath.remove(constituentIndex);
		}

		return ItemConfigurationValidationResult.SUCCESS;
	}


	/**
	 * Validate bundle structure to make sure the configuration structure matches the bundle's.
	 *
	 * @param bundle the bundle
	 * @param configuration the configuration
	 * @return <code>true</code>, iff successful
	 */
	protected boolean validateBundleStructure(final ProductBundle bundle, final ItemConfiguration configuration) {
		List<BundleConstituent> constituents = bundle.getConstituents();
		if (constituents.size() != configuration.getChildren().size()) {
			return false;
		}
		for (BundleConstituent constituent : constituents) {
			if (configuration.getChildById(constituent.getGuid()) == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Validate bundle selection rule.
	 *
	 * @param bundle the bundle
	 * @param configuration the configuration
	 * @return <code>true</code>, iff successful
	 */
	protected boolean validateBundleSelectionRule(final ProductBundle bundle, final ItemConfiguration configuration) {
		int selected = 0;
		for (ItemConfiguration child : configuration.getChildren()) {
			if (child.isSelected()) {
				++selected;
			}
		}
		return selected == getNumberOfRequiredSelections(bundle);
	}

	/**
	 * Gets the number of required selections for a bundle.
	 *
	 * @param bundle the bundle
	 * @return the number of required selections
	 */
	protected int getNumberOfRequiredSelections(final ProductBundle bundle) {
		SelectionRule selectionRule = bundle.getSelectionRule();
		if (selectionRule == null || selectionRule.getParameter() == 0) {
			return bundle.getConstituents().size();
		}
		return selectionRule.getParameter();
	}

	/**
	 * Finds the SKU given its code.
	 *
	 * @param skuCode the SKU code
	 * @return the SKU
	 */
	protected ProductSku getProductSku(final String skuCode) {
		return getProductSkuService().findBySkuCode(skuCode);
	}

	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}


	protected ProductSkuService getProductSkuService() {
		return productSkuService;
	}

	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	public BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}

	public void setProductCharacteristicsService(final ProductCharacteristicsService productCharacteristicsService) {
		this.productCharacteristicsService = productCharacteristicsService;
	}

	public ProductCharacteristicsService getProductCharacteristicsService() {
		return productCharacteristicsService;
	}
}
