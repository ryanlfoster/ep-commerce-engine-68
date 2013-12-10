package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * PriceListDescriptorDao adapter.
 */
public class PriceListDescriptorDaoAdapterImpl extends AbstractDaoAdapter<PriceListDescriptor> {

	private PriceListDescriptorService priceListDescriptorService;

	private BeanFactory beanFactory;

	@Override
	public boolean remove(final String guid) {
		PriceListDescriptor foundByGuid = priceListDescriptorService.findByGuid(guid);
		if (foundByGuid == null) {
			return false;
		}
		priceListDescriptorService.delete(foundByGuid);
		return true;
	}

	@Override
	public PriceListDescriptor update(final PriceListDescriptor mergedPersistence) throws SyncToolRuntimeException {
		return priceListDescriptorService.update(mergedPersistence);
	}

	@Override
	public void add(final PriceListDescriptor newPersistence) throws SyncToolRuntimeException {
		priceListDescriptorService.add(newPersistence);
	}

	@Override
	public PriceListDescriptor createBean(final PriceListDescriptor product) {
		return beanFactory.getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
	}

	@Override
	public PriceListDescriptor get(final String guid) {
		try {
			return (PriceListDescriptor) getEntityLocator().locatePersistence(guid, PriceListDescriptor.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}		
	}

	/**
	 * @param priceListDescriptorService the priceListDescriptorDao to set
	 */
	public void setPriceListDescriptorService(final PriceListDescriptorService priceListDescriptorService) {
		this.priceListDescriptorService = priceListDescriptorService;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
