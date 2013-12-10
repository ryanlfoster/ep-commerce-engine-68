package com.elasticpath.service.catalog.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.BundleConstituentFactory;

/**
 * Default implementation of {@link BundleConstituentFactory}.
 */
public class BundleConstituentFactoryImpl implements BundleConstituentFactory {
	private BeanFactory beanFacotry;

	@Override
	public BundleConstituent createBundleConstituent(final Product product, final int quantity) {
		BundleConstituent bundleConstituent = createBundleConstituentInternal();
		bundleConstituent.setConstituent(product);
		bundleConstituent.setQuantity(quantity);

		return bundleConstituent;
	}

	@Override
	public BundleConstituent createBundleConstituent(final ProductSku sku, final int quantity) {
		BundleConstituent bundleConstituent = createBundleConstituentInternal();
		bundleConstituent.setConstituent(sku);
		bundleConstituent.setQuantity(quantity);

		return bundleConstituent;
	}

	/**
	 * Creates a bundle constituent.
	 * 
	 * @return {@link BundleConstituent}
	 */
	protected BundleConstituent createBundleConstituentInternal() {
		return getBeanFactory().getBean(ContextIdNames.BUNDLE_CONSTITUENT);
	}

	/**
	 * Setter for {@link BeanFactory}.
	 * 
	 * @param beanFacotry {@link BeanFactory}
	 */
	public void setBeanFactory(final BeanFactory beanFacotry) {
		this.beanFacotry = beanFacotry;
	}

	/**
	 * Getter for {@link BeanFactory}.
	 * 
	 * @return {@link BeanFactory}
	 */
	public BeanFactory getBeanFactory() {
		return this.beanFacotry;
	}

}
