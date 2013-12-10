/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Dao adapter for brands.
 */
public class BrandDaoAdapterImpl extends AbstractDaoAdapter<Brand> {

	private BrandService brandService;

	private BeanFactory beanFactory;

	@Override
	public void add(final Brand newPersistence) throws SyncToolRuntimeException {
		brandService.add(newPersistence);
	}

	@Override
	public Brand createBean(final Brand brand) {
		return beanFactory.getBean(ContextIdNames.BRAND);
	}

	@Override
	public Brand get(final String guid) {
		try {
			return (Brand) getEntityLocator().locatePersistence(guid, Brand.class);
		} catch (final SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final Brand brand = get(guid);
		if (brand == null) {
			return false;
		}
		brandService.remove(brand);
		return true;
	}

	@Override
	public Brand update(final Brand mergedPersistence) throws SyncToolRuntimeException {
		return brandService.update(mergedPersistence);
	}

	/**
	 * Sets the brand service.
	 * @param brandService the service
	 */
	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}

	/**
	 * Sets the bean factory.
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


}
