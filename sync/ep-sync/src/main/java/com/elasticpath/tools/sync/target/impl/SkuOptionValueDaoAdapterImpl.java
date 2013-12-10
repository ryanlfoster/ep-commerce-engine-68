package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Dao adapter for sku option value.
 */
public class SkuOptionValueDaoAdapterImpl extends AbstractDaoAdapter<SkuOptionValue> {
	
	private BeanFactory beanFactory;

	private SkuOptionService skuOptionService;
	
	@Override
	public void add(final SkuOptionValue skuOptionValue) throws SyncToolRuntimeException {
		skuOptionService.add(skuOptionValue);
	}

	@Override
	public SkuOptionValue createBean(final SkuOptionValue skuOptionValue) {
		return beanFactory.getBean(ContextIdNames.SKU_OPTION_VALUE);
	}

	@Override
	public SkuOptionValue get(final String guid) {
		try {
			return (SkuOptionValue) getEntityLocator().locatePersistence(guid, SkuOptionValue.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final SkuOptionValue skuOptionValue = get(guid);
		if (skuOptionValue == null) {
			return false;
		}
		skuOptionService.remove(skuOptionValue);
		return true;
	}

	@Override
	public SkuOptionValue update(final SkuOptionValue skuOptionValue) throws SyncToolRuntimeException {
		return skuOptionService.update(skuOptionValue);
	}

	/**
	 * Sets the bean factory.
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Sets the sku option service.
	 * @param skuOptionService the sku option service. 
	 */
	public void setSkuOptionService(final SkuOptionService skuOptionService) {
		this.skuOptionService = skuOptionService;
	}

}
