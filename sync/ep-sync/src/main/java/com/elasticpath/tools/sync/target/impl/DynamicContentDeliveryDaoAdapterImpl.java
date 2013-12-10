package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * PriceListAssignmentDao adapter.
 */
public class DynamicContentDeliveryDaoAdapterImpl extends AbstractDaoAdapter<DynamicContentDelivery> {

	private DynamicContentDeliveryService dynamicContentDeliveryService;

	private SellingContextService sellingContextService;
	
	private BeanFactory beanFactory;

	@Override
	public boolean remove(final String guid) {
		DynamicContentDelivery foundByGuid = dynamicContentDeliveryService.findByGuid(guid);
		if (foundByGuid == null) {
			return false;
		}
		dynamicContentDeliveryService.remove(foundByGuid);
		sellingContextService.remove(foundByGuid.getSellingContext());
		return true;
	}

	@Override
	public DynamicContentDelivery update(final DynamicContentDelivery mergedPersistence) throws SyncToolRuntimeException {
		SellingContext updatedSellingContext = sellingContextService.saveOrUpdate(mergedPersistence.getSellingContext());
		mergedPersistence.setSellingContext(updatedSellingContext);
		return dynamicContentDeliveryService.saveOrUpdate(mergedPersistence);
	}

	@Override
	public void add(final DynamicContentDelivery newPersistence) throws SyncToolRuntimeException {
		SellingContext updatedSellingContext = sellingContextService.saveOrUpdate(newPersistence.getSellingContext());
		newPersistence.setSellingContext(updatedSellingContext);
		dynamicContentDeliveryService.saveOrUpdate(newPersistence);
	}

	@Override
	public DynamicContentDelivery createBean(final DynamicContentDelivery priceListAssignment) {
		return beanFactory.getBean(ContextIdNames.DYNAMIC_CONTENT_DELIVERY);
	}

	@Override
	public DynamicContentDelivery get(final String guid) {
		try {
			return (DynamicContentDelivery) getEntityLocator().locatePersistence(guid, DynamicContentDelivery.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}		
	}

	/**
	 * Sets the {@link DynamicContentDeliveryService} instance.
	 * @param dynamicContentDeliveryService {@link DynamicContentDeliveryService} 
	 */
	public void setDynamicContentDeliveryService(final DynamicContentDeliveryService dynamicContentDeliveryService) {
		this.dynamicContentDeliveryService = dynamicContentDeliveryService;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 *
	 * @param sellingContextService the sellingContextService to set
	 */
	public void setSellingContextService(final SellingContextService sellingContextService) {
		this.sellingContextService = sellingContextService;
	}

	/**
	 *
	 * @return the sellingContextService
	 */
	public SellingContextService getSellingContextService() {
		return sellingContextService;
	}
}
