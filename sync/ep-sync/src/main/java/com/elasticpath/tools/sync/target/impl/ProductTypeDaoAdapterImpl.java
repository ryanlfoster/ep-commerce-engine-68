/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Dao adapter for product type.
 */
public class ProductTypeDaoAdapterImpl extends AbstractDaoAdapter<ProductType> {

	private ProductTypeService productTypeService;

	private BeanFactory beanFactory;

	@Override
	public void add(final ProductType newPersistence) throws SyncToolRuntimeException {
		productTypeService.add(newPersistence);
	}

	@Override
	public ProductType createBean(final ProductType productType) {
		return beanFactory.getBean(ContextIdNames.PRODUCT_TYPE);
	}

	@Override
	public ProductType get(final String guid) {
		try {
			return (ProductType) getEntityLocator().locatePersistence(guid, ProductType.class);
		} catch (final SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final ProductType productType = get(guid);
		if (productType == null) {
			return false;
		}
		productTypeService.remove(productType);
		return true;
	}

	@Override
	public ProductType update(final ProductType mergedPersistence) throws SyncToolRuntimeException {
		return productTypeService.update(mergedPersistence);
	}

	/**
	 * Sets the product type service.
	 * @param productTypeService the service
	 */
	public void setProductTypeService(final ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}

	/**
	 * Sets the bean factory.
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


}
