/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.controller.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.importexport.common.dto.products.ProductDTO;
import com.elasticpath.importexport.common.dto.products.ProductSkuDTO;
import com.elasticpath.importexport.importer.changesetsupport.BusinessObjectDescriptorLocator;
import com.elasticpath.importexport.importer.controller.RelatedObjectsResolver;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.Importer;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.service.changeset.ChangeSetPolicy;

/**
 * Resolves product SKUs of a product and identifies them for adding to a change set.
 */
public class ProductRelatedObjectsResolverImpl implements RelatedObjectsResolver<Product, ProductDTO> {

	private ChangeSetPolicy changeSetPolicy;

	private ProductDao productDao;
	private BusinessObjectDescriptorLocator businessObjectDescriptorLocator;

	/**
	 * Collects all the SKUs from the product DTO and figures out if there will be any SKUs removed when the product
	 * gets imported.
	 * 
	 * @param productDto the product DTO
	 * @param importer the product DTO importer
	 * @param multiSkuProductTypeNames The set of product type names which are multi-skued
	 * @return a collection of business object descriptors for the SKUs that should be added to a change set
	 */
	public Collection<BusinessObjectDescriptor> resolveRelatedObjects(
			final ProductDTO productDto, final Importer<Product, ProductDTO> importer,
			final Set<String> multiSkuProductTypeNames) {
		Collection<BusinessObjectDescriptor> result = new HashSet<BusinessObjectDescriptor>();
		Product product = getProductDao().findByGuid(productDto.getCode());

		// product might not exist in the system
		if (product != null) {
			Set<ProductSku> skusHistory = new HashSet<ProductSku>(product.getProductSkus().values());
			
			CollectionsStrategy<Product, ProductDTO> collectionStrtegy = importer.getSavingStrategy().getCollectionsStrategy();
			collectionStrtegy.prepareCollections(product, productDto);
			
			for (ProductSku sku : skusHistory) {
				if (product.getSkuByCode(sku.getSkuCode()) == null) { 
					// the SKU will be removed from the product so add it to the change set
					BusinessObjectDescriptor descriptor = getChangeSetPolicy().resolveObjectDescriptor(sku);
					result.add(descriptor);
				}
			}
			// WORKAROUND add back the skus to the original product
			// It seems that when the transaction is active all the changes to objects done within that
			// transaction get committed
			for (ProductSku productSku : skusHistory) {
				product.addOrUpdateSku(productSku);
			}
			// END
		}
		// Single-skued products are managed by change sets only by their Product. No sku needs to be added to the change set.
		if (multiSkuProductTypeNames.contains(productDto.getType())) {
			// add all the SKUs defined by their DTOs because they all will be imported
			for (ProductSkuDTO productSkuDto : productDto.getProductSkus()) {
				BusinessObjectDescriptor descriptor = getBusinessObjectDescriptorLocator().locateObjectDescriptor(productSkuDto);
				result.add(descriptor);
			}
		}
		return result;
	}

	/**
	 *
	 * @param productDao the productDao to set
	 */
	public void setProductDao(final ProductDao productDao) {
		this.productDao = productDao;
	}

	/**
	 *
	 * @return the productDao
	 */
	public ProductDao getProductDao() {
		return productDao;
	}

	/**
	 *
	 * @return the changeSetPolicy
	 */
	public ChangeSetPolicy getChangeSetPolicy() {
		return changeSetPolicy;
	}

	/**
	 *
	 * @param changeSetPolicy the changeSetPolicy to set
	 */
	public void setChangeSetPolicy(final ChangeSetPolicy changeSetPolicy) {
		this.changeSetPolicy = changeSetPolicy;
	}

	/**
	 *
	 * @param businessObjectDescriptorLocator the businessObjectDescriptorLocator to set
	 */
	public void setBusinessObjectDescriptorLocator(final BusinessObjectDescriptorLocator businessObjectDescriptorLocator) {
		this.businessObjectDescriptorLocator = businessObjectDescriptorLocator;
	}

	/**
	 *
	 * @return the businessObjectDescriptorLocator
	 */
	public BusinessObjectDescriptorLocator getBusinessObjectDescriptorLocator() {
		return businessObjectDescriptorLocator;
	}

}
