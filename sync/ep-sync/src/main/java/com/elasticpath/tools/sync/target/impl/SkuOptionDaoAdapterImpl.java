package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Dao adapter for sku option.
 */
public class SkuOptionDaoAdapterImpl extends AbstractDaoAdapter<SkuOption> {
	
	private BeanFactory beanFactory;

	private SkuOptionService skuOptionService;
	
	@Override
	public void add(final SkuOption newPersistence) throws SyncToolRuntimeException {
		skuOptionService.add(newPersistence);
	}

	@Override
	public SkuOption createBean(final SkuOption category) {
		return beanFactory.getBean(ContextIdNames.SKU_OPTION);
	}

	@Override
	public SkuOption get(final String guid) {
		try {
			return (SkuOption) getEntityLocator().locatePersistence(guid, SkuOption.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final SkuOption skuOption = get(guid);
		if (skuOption == null) {
			return false;
		}
		skuOptionService.remove(skuOption);
		return true;
	}

	@Override
	public SkuOption update(final SkuOption mergedPersistence) throws SyncToolRuntimeException {
		return skuOptionService.update(mergedPersistence);
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
