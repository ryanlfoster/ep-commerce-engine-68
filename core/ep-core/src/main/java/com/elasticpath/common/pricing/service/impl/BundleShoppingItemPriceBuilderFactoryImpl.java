package com.elasticpath.common.pricing.service.impl;

import com.elasticpath.common.pricing.service.BundleShoppingItemPriceBuilder;
import com.elasticpath.common.pricing.service.BundleShoppingItemPriceBuilderFactory;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.BundleIdentifier;

/**
 * Implementation of BundleShoppingItemPriceBuilderFactory.
 * 
 *
 */
public class BundleShoppingItemPriceBuilderFactoryImpl implements BundleShoppingItemPriceBuilderFactory {
	private BundleIdentifier bundleIdentifier;
	@Override
	public BundleShoppingItemPriceBuilder createBundleShoppingItemPriceBuilder(
			final ShoppingItem bundleShoppingItem,
			final PriceLookupFacade priceLookupFacade, 
			final BeanFactory beanFactory) {
		if (!bundleShoppingItem.isBundle()) {
			return null;
		}
		
		ProductBundle bundle = (ProductBundle) bundleShoppingItem.getProductSku().getProduct();
		
		if (bundle.isCalculated()) {
			CalculatedBundleShoppingItemPriceBuilder priceBuilder = new CalculatedBundleShoppingItemPriceBuilder(priceLookupFacade, beanFactory);
			priceBuilder.setBundleIdentifier(getBundleIdentifier());
			return priceBuilder;
		}
		
		return new AssignedBundleShoppingItemPriceBuilder(priceLookupFacade);
	}

	/**
	 * @return the BundleIdentifier instance
	 */
	protected BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}
	
	/**
	 * Set the {@link BundleIdentifier} instance.
	 * @param bundleIdentifier the bundleIdentifier instance to set
	 */
	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

}
