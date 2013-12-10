package com.elasticpath.service.pricing.datasource.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactory;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactoryBuilder;

/**
 * Builds {@link CollectionBaseAmountDataSourceFactory}s that can provide base amounts for a variety of items and settings.
 */
public class CollectionBaseAmountDataSourceFactoryBuilderImpl implements BaseAmountDataSourceFactoryBuilder {

	private final BundleIdentifier bundleIdentifier;
	
	private final List<String> plGuids = new LinkedList<String>();
	
	private final List<String> objGuids = new LinkedList<String>();
	
	/**
	 * Default c'tor.
	 * @param bundleIdentifier bundle identifier
	 */
	public CollectionBaseAmountDataSourceFactoryBuilderImpl(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}
	
	
	
	/**
	 * Builds a {@link CollectionBaseAmountDataSourceFactory} given the parameters that are passed before.
	 * @return an instance of {@link CollectionBaseAmountDataSourceFactory}.
	 */
	public BaseAmountDataSourceFactory build() {
		return new CollectionBaseAmountDataSourceFactory(getPriceListGuids(), getObjectGuids());
	}
	
	
	@Override
	public BaseAmountDataSourceFactoryBuilder products(final Product...products) {
		for (Product product : products) {
			appendObjectGuids(product, objGuids);
		}
		return this;
	}
	
	@Override
	public BaseAmountDataSourceFactoryBuilder products(final Collection<Product> products) {
		for (Product product : products) {
			appendObjectGuids(product, objGuids);
		}
		return this;
	}
	
	@Override
	public BaseAmountDataSourceFactoryBuilder priceLists(final PriceListDescriptor... priceListDescriptors) {
		for (PriceListDescriptor pld : priceListDescriptors) {
			plGuids.add(pld.getGuid());
		}
		return this;
	}
	
	@Override
	public BaseAmountDataSourceFactoryBuilder priceLists(final Collection<PriceListDescriptor> priceListDescriptors) {
		for (PriceListDescriptor pld : priceListDescriptors) {
			plGuids.add(pld.getGuid());
		}
		return this;
	}
	
	@Override
	public BaseAmountDataSourceFactoryBuilder priceListAssignments(final PriceListAssignment... plas) {
		for (PriceListAssignment pla : plas) {
			plGuids.add(pla.getPriceListDescriptor().getGuid());
		}
		return this;
	}
	
	@Override
	public BaseAmountDataSourceFactoryBuilder priceListAssignments(final Collection<PriceListAssignment> plas) {
		for (PriceListAssignment pla : plas) {
			plGuids.add(pla.getPriceListDescriptor().getGuid());
		}
		return this;
	}
	
	/**
	 * Appends the GUIDs of the given product and all its SKUs to the given list, and returns the list. 
	 * If the product belongs to a calculated bundle, it will add all the related GUIDs of its constituent items.
	 * 
	 * @param product the product
	 * @param objectGuids the initial list of object GUIDs
	 * @return the objectGuids collection passed as the parameter. It now contains the GUIDs related to the given product. 
	 */
	protected List<String> appendObjectGuids(final Product product, final List<String> objectGuids) {
		objectGuids.add(product.getGuid());
		if (getBundleIdentifier().isCalculatedBundle(product)) {
			appendObjectGuidsForCalculatedBundle(getBundleIdentifier().asProductBundle(product), objectGuids);
		} else if (product.getProductSkus() != null) {
			objectGuids.addAll(product.getProductSkus().keySet());
		}
		return objectGuids;
	}
	
	/**
	 * Appends the GUIDs related to the given constituentItem. Based on the type of the constituent item,  
	 * it will delegate to either <code>appendObjectGuids(Product, List<string>)</code> 
	 * or <code>appendObjectGuids(ProductSku, List<String>)</code>.
	 * 
	 * @param constituentItem the constituent item
	 * @param objectGuids the initial list of object GUIDs
	 * @return the objectGuids collection passed as the parameter. It now contains the guids related to the given constituent item. 
	 */
	protected List<String> appendObjectGuids(final ConstituentItem constituentItem, final List<String> objectGuids) {
		if (constituentItem.isProduct() || constituentItem.isBundle()) {
			return appendObjectGuids(constituentItem.getProduct(), objectGuids);
		} 
		return appendObjectGuids(constituentItem.getProductSku(), objectGuids);
	}
	
	/**
	 * Appends the GUIDs of the given SKU and its product to the given list, and returns the list. 
	 * If it is called with a SKU of a calculated bundle, it will NOT add the GUIDs of the constituent items.
	 * 
	 * @param productSku the SKU
	 * @param objectGuids the initial list of object GUIDs
	 * @return the objectGuids collection passed as the parameter. It now contains the GUIDs related to the given SKU. 
	 */
	protected List<String> appendObjectGuids(final ProductSku productSku, final List<String> objectGuids) {
		objectGuids.add(productSku.getSkuCode());
		objectGuids.add(productSku.getProduct().getGuid());
		return objectGuids;
	}
	
	/**
	 * It will add all the related GUIDs of the bundle's constituent items.
	 * 
	 * @param bundle the calculated bundle
	 * @param objectGuids the initial list of object GUIDs
	 * @return the objectGuids collection passed as the parameter. It now contains the GUIDs related to 
	 * the constituents of the given calculated bundle. 
	 */
	protected List<String> appendObjectGuidsForCalculatedBundle(final ProductBundle bundle, final List<String> objectGuids) {
		for (BundleConstituent constituent : bundle.getConstituents()) {
			appendObjectGuids(constituent.getConstituent(), objectGuids);
		}
		return objectGuids;
	}
	
	protected BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}




	@Override
	public BaseAmountDataSourceFactoryBuilder priceListGuids(final List<String> plGuids) {
		this.plGuids.addAll(plGuids);
		return this;
	}




	protected List<String> getPriceListGuids() {
		return plGuids;
	}




	@Override
	public BaseAmountDataSourceFactoryBuilder objectGuids(final List<String> objectGuids) {
		this.objGuids.addAll(objectGuids);
		return this;
	}



	protected List<String> getObjectGuids() {
		return objGuids;
	}




	@Override
	public BaseAmountDataSourceFactoryBuilder priceListStack(final PriceListStack plStack) {
		priceListGuids(plStack.getPriceListStack());
		return this;
	}


	@Override
	public BaseAmountDataSourceFactoryBuilder skus(final ProductSku... skus) {
		for (ProductSku sku : skus) {
			if (getBundleIdentifier().isCalculatedBundle(sku)) {
				appendObjectGuids(sku.getProduct(), objGuids);
			} else {
				appendObjectGuids(sku, objGuids);
			}
		}
		return this;
	}



	@Override
	public BaseAmountDataSourceFactoryBuilder skus(final Collection<ProductSku> skus) {
		for (ProductSku sku : skus) {
			if (getBundleIdentifier().isCalculatedBundle(sku)) {
				appendObjectGuids(sku.getProduct(), objGuids);
			} else {
				appendObjectGuids(sku, objGuids);
			}
		}
		return this;
	}
	
}
