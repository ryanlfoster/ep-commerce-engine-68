package com.elasticpath.service.catalog.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductCharacteristicsImpl;
import com.elasticpath.service.catalog.ProductBundleService;
import com.elasticpath.service.catalog.ProductCharacteristicsService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.catalogview.ProductWrapper;

/**
 * Determine the characteristics of a product.
 */
public class ProductCharacteristicsServiceImpl implements ProductCharacteristicsService {

	private ProductBundleService productBundleService;
	private ProductTypeService productTypeService;
	
	@Override
	public ProductCharacteristics getProductCharacteristics(final Product product) {
		Product wrappedProduct = getWrappedProduct(product);
		ProductBundle bundle = asProductBundle(wrappedProduct);
		
		if (bundle == null) {
			return getCharacteristicsForNonBundle(wrappedProduct);
		} 
		
		return getCharacteristicsForBundle(bundle);
	}

	@Override
	public ProductCharacteristics getProductCharacteristics(final ProductSku productSku) {
		return getProductCharacteristics(productSku.getProduct());
	}

	@Override
	public ProductCharacteristics getProductCharacteristicsForSkuCode(final String skuCode) {
		Long bundleUid = getProductBundleService().findBundleUidBySkuCode(skuCode);
		if (bundleUid == null) {
			ProductType productType = getProductTypeService().findBySkuCode(skuCode);
			return getCharacteristicsForNonBundle(productType);
		}

		ProductBundle bundle = (ProductBundle) getProductBundleService().load(bundleUid);
		return getCharacteristicsForBundle(bundle);
	}
	
	/**
	 * Gets the characteristics for bundle.
	 *
	 * @param bundle the bundle
	 * @return the characteristics for bundle
	 */
	protected ProductCharacteristics getCharacteristicsForBundle(final ProductBundle bundle) {
		ProductCharacteristicsImpl productCharacteristics = createProductCharacteristics();
		productCharacteristics.setBundleUid(bundle.getUidPk());
		productCharacteristics.setBundle(true);
		productCharacteristics.setCalculatedBundle(bundle.isCalculated());
		productCharacteristics.setDynamicBundle(isDynamicBundle(bundle));
		
		productCharacteristics.setMultipleConfigurations(productCharacteristics.isDynamicBundle() || hasMultiConfigurationConstituent(bundle));
		
		return productCharacteristics;
	}

	/**
	 * Checks for selection rule greater than zero.
	 *
	 * @param bundle the bundle
	 * @return true, if successful
	 */
	private boolean hasSelectionRuleGreaterThanZero(final ProductBundle bundle) {
		return bundle.getSelectionRule() != null && bundle.getSelectionRule().getParameter() > 0;
	}

	/**
	 * Checks for dynamic bundle.
	 *
	 * @param constituents the constituents
	 * @return true, if successful
	 */
	private boolean hasDynamicBundle(final List<BundleConstituent> constituents) {
		boolean isDynamic = false;

		for (BundleConstituent bundleConstituent : constituents) {
			ConstituentItem bundleItem = bundleConstituent.getConstituent();
			if (bundleItem.isBundle()) {
				isDynamic |= isDynamicBundle(asProductBundle(bundleItem.getProduct())); 
			}
		}

		return isDynamic;
	}
	
	/**
	 * Gets the characteristics for non bundle.
	 *
	 * @param product the product
	 * @return the characteristics for non bundle
	 */
	protected ProductCharacteristics getCharacteristicsForNonBundle(final Product product) {
		return getCharacteristicsForNonBundle(product.getProductType());
	}

	/**
	 * Gets the characteristics for non bundle.
	 *
	 * @param productType the product type
	 * @return the characteristics for non bundle
	 */
	protected ProductCharacteristics getCharacteristicsForNonBundle(final ProductType productType) {
		ProductCharacteristicsImpl productCharacteristics = createProductCharacteristics();
		productCharacteristics.setBundle(false);
		productCharacteristics.setCalculatedBundle(false);
		productCharacteristics.setDynamicBundle(false);
		productCharacteristics.setMultipleConfigurations(isConfigurableProductType(productType));
		return productCharacteristics;
	}
	
	/**
	 * Checks if is configurable product type.
	 *
	 * @param productType the product type
	 * @return true, if is configurable product type
	 */
	protected boolean isConfigurableProductType(final ProductType productType) {
		return GiftCertificate.KEY_PRODUCT_TYPE.equals(productType.getName()) || productType.isWithMultipleSkus();
	}

	/**
	 * Creates the product characteristics.
	 *
	 * @return the product characteristics impl
	 */
	protected ProductCharacteristicsImpl createProductCharacteristics() {
		return new ProductCharacteristicsImpl();
	}

	/**
	 * Get the original product instance. If the product is a product wrapper, e.g. an IndexProduct, 
	 * this method returns the wrapped product.
	 * @param product the product
	 * @return the inner-most product in a product wrapper
	 */
	protected Product getWrappedProduct(final Product product) {
		Product wrappedProduct = product;
		while (wrappedProduct instanceof ProductWrapper) {
			wrappedProduct = ((ProductWrapper) wrappedProduct).getWrappedProduct();
		}
		return wrappedProduct;
	}
	
	/**
	 * Checks if the product is a bundle.
	 *
	 * @param product the product
	 * @return true, if it is a bundle
	 */
	protected boolean isBundle(final Product product) {
		Product wrappedProduct = getWrappedProduct(product);
		return asProductBundle(wrappedProduct) != null;
	}
	
	/**
	 * Checks if the product is a dynamic bundle.
	 *
	 * @param bundle the bundle
	 * @return true, if it is a dynamic bundle
	 */
	protected boolean isDynamicBundle(final ProductBundle bundle) {
		return hasSelectionRuleGreaterThanZero(bundle) || hasDynamicBundle(bundle.getConstituents());
	}
	
	/**
	 * Casts the product to productBundle.
	 * @param product the product
	 * @return <code>null</code> if the product is not a bundle, the ProductBundle otherwise.
	 */
	protected ProductBundle asProductBundle(final Product product) {
		if (product instanceof ProductBundle) {
			return (ProductBundle) product;
		}
		return null;
	}
	
	/**
	 * Checks whether the product has multiple configurations.
	 *
	 * @param product the product
	 * @return <code>true</code>, iff it has multiple configurations.
	 */
	protected boolean hasMultipleConfigurations(final Product product) {
		if (isBundle(product)) {
			ProductBundle bundle = asProductBundle(product);
			return isDynamicBundle(bundle) || hasMultiConfigurationConstituent(bundle);
		}
		return product.getProductType().isWithMultipleSkus();
	}

	/**
	 * Checks whether the bundle contains any product with configurations.
	 *
	 * @param bundle the bundle to be checked
	 * @return <code>true</code> iff there is at least one constituent with a configuration.
	 */
	protected boolean hasMultiConfigurationConstituent(final ProductBundle bundle) {
		for (BundleConstituent constituent : bundle.getConstituents()) {
			ConstituentItem constituentItem = constituent.getConstituent();
			if (constituentItem.isProduct() && hasMultipleConfigurations(constituentItem.getProduct())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, ProductCharacteristics> getProductCharacteristicsMap(final Collection<? extends Product> products) {
		Map<String, ProductCharacteristics> characteristics = new HashMap<String, ProductCharacteristics>();
		if (products != null) {
			for (Product product : products) {
				characteristics.put(product.getCode(), getProductCharacteristics(product));
			}
		}
		return characteristics;
	}
	
	public void setProductBundleService(final ProductBundleService productBundleService) {
		this.productBundleService = productBundleService;
	}

	protected ProductBundleService getProductBundleService() {
		return productBundleService;
	}

	public void setProductTypeService(final ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}

	protected ProductTypeService getProductTypeService() {
		return productTypeService;
	}


}
