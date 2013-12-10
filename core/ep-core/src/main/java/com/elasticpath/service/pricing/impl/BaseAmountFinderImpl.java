package com.elasticpath.service.pricing.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.service.pricing.BaseAmountFinder;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSource;

/**
 * <code>BaseAmountFinderImpl</code> finds the base amounts related to different entities.
 */
public class BaseAmountFinderImpl implements BaseAmountFinder {

	/**
	 * Get the list of GUIDs related to the given products.
	 * @param products a collection of products
	 * @return list A list containing the GUID of the given products, and the GUIDs of the SKUs of the given products
	 */
	protected List<String> getBaseAmountObjectGuids(final Collection<Product> products) {
		final List<String> productAndSkuGuids = new ArrayList<String>();
		for (Product product : products) {
			productAndSkuGuids.addAll(getBaseAmountObjectGuids(product));
		}
		return productAndSkuGuids;
	}

	/**
	 * Get the list of GUIDs related to the product.
	 * @param product the product
	 * @return list A list containing the GUID of the product, and the GUIDs of the SKUs of the product
	 */
	protected List<String> getBaseAmountObjectGuids(final Product product) {
		final List<String> productAndSkuGuids = new ArrayList<String>();
		productAndSkuGuids.add(product.getGuid());
		if (product.getProductSkus() != null) {
			productAndSkuGuids.addAll(product.getProductSkus().keySet());
		}
		return productAndSkuGuids;
	}

	/**
	 * Get the list of GUIDs related to the given sku.
	 * @param productSku the sku
	 * @return list A list containing the GUID of the given sku, and its product
	 */
	protected List<String> getBaseAmountObjectGuids(final ProductSku productSku) {
		List<String> productAndSkuGuids = new ArrayList<String>();
		productAndSkuGuids.add(productSku.getSkuCode());
		productAndSkuGuids.add(productSku.getProduct().getGuid());
		return productAndSkuGuids;
	}

	private Collection<BaseAmount> getBaseAmounts(final List<String> plGuids, final List<String> objectGuids,
			final BaseAmountDataSource baseAmountDataSource) {
		return baseAmountDataSource.getBaseAmounts(plGuids, objectGuids);
	}

	@Override
	public List<BaseAmount> filterBaseAmounts(final Collection<BaseAmount> baseAmounts, final String plGuid, final BaseAmountObjectType objectType,
			final String guid) {
		if (baseAmounts == null) {
			return Collections.emptyList();
		}
		List<BaseAmount> baseAmountsForGuid = new ArrayList<BaseAmount>();
		for (BaseAmount baseAmount : baseAmounts) {
			if (guid.equalsIgnoreCase(baseAmount.getObjectGuid()) && plGuid.equals(baseAmount.getPriceListDescriptorGuid())
					&& objectType.getName().equals(baseAmount.getObjectType())) {
				baseAmountsForGuid.add(baseAmount);
			}
		}
		return baseAmountsForGuid;
	}

	@Override
	public Collection<BaseAmount> getBaseAmounts(final ProductSku productSku, final List<String> plGuids,
			final BaseAmountDataSource baseAmountDataSource) {
		return getBaseAmounts(plGuids, getBaseAmountObjectGuids(productSku), baseAmountDataSource);
	}

	/**
	 * Get the list of base amounts related to the given product in the given price lists.
	 * @param product the product
	 * @param plGuids list of price list GUIDs
	 * @param baseAmountDataSource the data source to be used to retrieve the base amounts
	 * @return a collection containing the related base amounts
	 */
	public Collection<BaseAmount> getBaseAmounts(final Product product, final List<String> plGuids, final BaseAmountDataSource baseAmountDataSource) {
		return getBaseAmounts(plGuids, getBaseAmountObjectGuids(product), baseAmountDataSource);
	}

	/**
	 * Get the list of base amounts related to the given products in the given price lists.
	 * @param products the list of products
	 * @param plGuids list of price list GUIDs
	 * @param baseAmountDataSource the data source to be used to retrieve the base amounts
	 * @return a collection containing the related base amounts
	 */
	public Collection<BaseAmount> getBaseAmounts(final Collection<Product> products,
			final List<String> plGuids, final BaseAmountDataSource baseAmountDataSource) {
		return getBaseAmounts(plGuids, getBaseAmountObjectGuids(products), baseAmountDataSource);
	}

}
