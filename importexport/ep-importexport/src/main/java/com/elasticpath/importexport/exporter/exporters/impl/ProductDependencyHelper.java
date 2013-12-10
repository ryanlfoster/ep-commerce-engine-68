package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.importexport.common.util.assets.AssetFileManager;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.util.ApplicationPropertiesHelper;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Helper class for registering dependencies for Product. Dependencies may be to external types,
 * i.e. Brand or Catalog or they may be to Products (through ProductAssociations)
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
class ProductDependencyHelper {

	private final DependencyRegistry dependencyRegistry;

	private final List<Long> productUidPkList;

	private final ProductAssociationService productAssociationService;

	private final List<Long> futureNonDependant;
	
	private final ApplicationPropertiesHelper applicationPropertiesHelper;

	/**
	 * Constructs ProductDependencyHelper and sets the data it depends on.
	 * 
	 * @param dependencyRegistry registry of dependencies
	 * @param productUidPkList List of <code>Long</code> UIDpks of Products to export
	 * @param futureNonDependant the futureNonDependant
	 * @param productAssociationService the product association service
	 * @param applicationPropertiesHelper the applicationPropertiesHelper
	 */
	ProductDependencyHelper(final DependencyRegistry dependencyRegistry,
			final List<Long> productUidPkList,
			final List<Long> futureNonDependant,
			final ProductAssociationService productAssociationService,
			final ApplicationPropertiesHelper applicationPropertiesHelper) {
		this.dependencyRegistry = dependencyRegistry;
		this.productUidPkList = productUidPkList;
		this.futureNonDependant = futureNonDependant;
		this.productAssociationService = productAssociationService;
		this.applicationPropertiesHelper = applicationPropertiesHelper;
	}

	/**
	 * Adds dependencies for a given List of Products.
	 * 
	 * @param products List of Products
	 */
	void addDependencies(final List<Product> products) {
		// NOTE: product list must be updated before adding other dependencies.
		if (dependencyRegistry.supportsDependency(ProductAssociation.class)) {
			updateProductsAndAddProductAssociationsIntoRegistry(products);
		}

		addProductRelatedDependencies(products);
	
		if (dependencyRegistry.supportsDependency(DigitalAsset.class)) {
			addAssetsIntoRegistry(products);
		}
		if (dependencyRegistry.supportsDependency(Brand.class)) {
			addBrandsIntoRegistry(products);
		}
		if (dependencyRegistry.supportsDependency(ProductBundle.class)) {
			addProductBundlesIntoRegistry(products);
		}
	}

	private void addProductRelatedDependencies(final List<Product> products) {
		if (dependencyRegistry.supportsDependency(Attribute.class)) {
			addAttributesIntoRegistry(products);
		}
		if (dependencyRegistry.supportsDependency(ProductType.class)) {
			addProductTypesIntoRegistry(products);
		}
		if (dependencyRegistry.supportsDependency(SkuOption.class)) {
			addSkuOptionsIntoRegistry(products);
		}
		if (dependencyRegistry.supportsDependency(Product.class)) {
			addProductsIntoRegistry(products);
		}
		if (dependencyRegistry.supportsDependency(ProductSku.class)) {
			addProductSkuIntoRegistry(products);
		}
	}

	private void addProductSkuIntoRegistry(final List<Product> products) {
		final Set<Long> productSkuSet = new HashSet<Long>();
		for (Product product : products) {
			for (Entry<String, ProductSku> skuEtry : product.getProductSkus().entrySet()) {
				productSkuSet.add(skuEtry.getValue().getUidPk());
			}
		}
		dependencyRegistry.addUidDependencies(ProductSku.class, productSkuSet);
	}

	private void addProductsIntoRegistry(final List<Product> products) {
		final Set<Long> productsSet = new HashSet<Long>();
		for (Product product : products) {
			productsSet.add(product.getUidPk());
		}
		dependencyRegistry.addUidDependencies(Product.class, productsSet);
	}

	/*
	 * Add UIDs of product associations if its source product is in the list of products to be exported.
	 * 
	 * @param products the list of products to be exported
	 */
	private void updateProductsAndAddProductAssociationsIntoRegistry(final List<Product> products) {
		for (Product product : products) {
			if (futureNonDependant == null	|| !futureNonDependant.contains(product.getUidPk())) {
				addAssociationUids(product);
			}
		}
	}

	/*
	 * Adds UIDs of associations into dependency registry.
	 * Updates the list of product UIDs used for determination of product associations to be exported
	 */
	private void addAssociationUids(final Product product) {
		final Set<Long> dependentProductAssociations = new HashSet<Long>();
		final ProductAssociationSearchCriteria associationCriteria = new ProductAssociationSearchCriteria();
		associationCriteria.setSourceProduct(product);
		for (ProductAssociation productAssociation : productAssociationService.findByCriteria(associationCriteria)) {
			dependentProductAssociations.add(productAssociation.getUidPk());
			final Long targetProductUid = productAssociation.getTargetProduct().getUidPk();
			if (!productUidPkList.contains(targetProductUid)) {
				productUidPkList.add(targetProductUid);
				if (futureNonDependant != null) {
					futureNonDependant.add(targetProductUid);
				}
			}
		}
		dependencyRegistry.addUidDependencies(ProductAssociation.class, dependentProductAssociations);
	}

	/*
	 * Add UIDs of brands exported products depend on into dependency register.
	 */
	private void addBrandsIntoRegistry(final List<Product> products) {
		final Set<Long> dependentBrands = new HashSet<Long>();
		for (Product product : products) {
			if (product.getBrand() != null) {
				dependentBrands.add(product.getBrand().getUidPk());
			}
		}
		dependencyRegistry.addUidDependencies(Brand.class, dependentBrands);
	}
	
	/*
	 * Add GUIDs of product bundles exported products depend on into dependency register.
	 */
	private void addProductBundlesIntoRegistry(final List<Product> products) {
		final Set<String> dependentGuids = new HashSet<String>();
		for (Product product : products) {
			if (product instanceof ProductBundle) {
				addBoudlesUids((ProductBundle) product);
				dependentGuids.add(product.getGuid());
			}
		}
		dependencyRegistry.addGuidDependencies(ProductBundle.class, dependentGuids);		
	}
	
	private void addBoudlesUids(final ProductBundle productBundle) { 
		for (BundleConstituent bundleConstituent : productBundle.getConstituents()) { 
			final Long targetProductUid = bundleConstituent.getConstituent().getProduct().getUidPk(); 
			if (!productUidPkList.contains(targetProductUid)) { 
				productUidPkList.add(targetProductUid);
			} 
		} 
	}	

	private void addSkuOptionsIntoRegistry(final List<Product> products) {
		final Set<Long> dependents = new HashSet<Long>();
		for (Product product : products) {
			for (Entry<String, ProductSku> entry : product.getProductSkus().entrySet()) {
				for (SkuOptionValue skuOptionValue : entry.getValue().getOptionValues()) {
					dependents.add(skuOptionValue.getSkuOption().getUidPk());
				}
			}
		}
		dependencyRegistry.addUidDependencies(SkuOption.class, dependents);

	}

	private void addProductTypesIntoRegistry(final List<Product> products) {
		final Set<Long> dependents = new HashSet<Long>();
		for (Product product : products) {
			if (product.getProductType() != null) {
				dependents.add(product.getProductType().getUidPk());
			}
		}
		dependencyRegistry.addUidDependencies(ProductType.class, dependents);
	}

	private void addAttributesIntoRegistry(final List<Product> products) {
		final Set<Long> dependents = new HashSet<Long>();
		for (Product product : products) {
			for (Entry<String, AttributeValue> entry : product.getAttributeValueMap().entrySet()) {
				dependents.add(entry.getValue().getAttribute().getUidPk());
			}
		}
		dependencyRegistry.addUidDependencies(Attribute.class, dependents);
	}

	/*
	 * Memorize file names of assets to be exported.
	 */
	private void addAssetsIntoRegistry(final List<Product> products) {
		Map<String, String> assetsProperties = applicationPropertiesHelper.getPropertiesWithNameStartsWith("asset");
		String imagesFolder = assetsProperties.get(AssetFileManager.PROPERTY_IMAGE_ASSET_SUBFOLDER);
		String digitalsFolder = assetsProperties.get(AssetFileManager.PROPERTY_DIGITALGOODS_ASSET_SUBFOLDER);
		
		for (Product product : products) {
			dependencyRegistry.addAsset(imagesFolder, product.getImage());
			for (AttributeValue attributeValue : product.getAttributeValueMap().values()) {
				if (attributeValue.getValue() == null) {
					continue;
				}
				final AttributeType attributeType = attributeValue.getAttributeType();
				if (attributeType == AttributeType.IMAGE) {
					dependencyRegistry.addAsset(imagesFolder, attributeValue.getStringValue());
				}
				if (attributeType == AttributeType.FILE && attributeValue.getValue() != null) {
					dependencyRegistry.addAsset(digitalsFolder, attributeValue.getStringValue());
				}				
			}
			addAssetsForProductSkusIntoRegistry(product, imagesFolder, digitalsFolder);
		}
	}
	
	private void addAssetsForProductSkusIntoRegistry(final Product product, final String imagesFolder, final String digitalAssetsFolder) {
		for (ProductSku productSku : product.getProductSkus().values()) {			
			dependencyRegistry.addAsset(imagesFolder, productSku.getImage());
			if (productSku.isDigital()) {
				DigitalAsset digitalAsset = productSku.getDigitalAsset();
				if (digitalAsset != null) {	//GiftCertificates are digital, but have nothing to download
					dependencyRegistry.addAsset(digitalAssetsFolder, digitalAsset.getFileName());
				}
			}
			for (SkuOptionValue skuOptionValue : productSku.getOptionValues()) {
				dependencyRegistry.addAsset(imagesFolder, skuOptionValue.getImage());
			}
			for (AttributeValue attributeValue : productSku.getAttributeValueMap().values()) {
				if (attributeValue.getValue() == null) {
					continue;
				}
				final AttributeType attributeType = attributeValue.getAttributeType();
				if (attributeType == AttributeType.IMAGE) {
					dependencyRegistry.addAsset(imagesFolder, attributeValue.getStringValue());
				}
				if (attributeType == AttributeType.FILE) {
					dependencyRegistry.addAsset(digitalAssetsFolder, attributeValue.getStringValue());
				}	
			}
		}
	}

}
