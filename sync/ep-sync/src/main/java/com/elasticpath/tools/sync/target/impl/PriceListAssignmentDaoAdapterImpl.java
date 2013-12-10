package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * PriceListAssignmentDao adapter.
 */
public class PriceListAssignmentDaoAdapterImpl extends AbstractDaoAdapter<PriceListAssignment> {

	private PriceListAssignmentService priceListAssignmentService;

	private BeanFactory beanFactory;

	@Override
	public boolean remove(final String guid) {
		PriceListAssignment foundByGuid = priceListAssignmentService.findByGuid(guid);
		if (foundByGuid == null) {
			return false;
		}
		priceListAssignmentService.delete(foundByGuid);
		return true;
	}

	@Override
	public PriceListAssignment update(final PriceListAssignment mergedPersistence) throws SyncToolRuntimeException {
		return priceListAssignmentService.saveOrUpdate(mergedPersistence);
	}

	@Override
	public void add(final PriceListAssignment newPersistence) throws SyncToolRuntimeException {
		priceListAssignmentService.saveOrUpdate(newPersistence);
	}

	@Override
	public PriceListAssignment createBean(final PriceListAssignment priceListAssignment) {
		return beanFactory.getBean(ContextIdNames.PRICE_LIST_ASSIGNMENT);
	}

	@Override
	public PriceListAssignment get(final String guid) {
		try {
			return (PriceListAssignment) getEntityLocator().locatePersistence(guid, PriceListAssignment.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}		
	}

	/**
	 * @param priceListAssignementService the priceListAssignmentService to set
	 */
	public void setPriceListAssignmentService(final PriceListAssignmentService priceListAssignementService) {
		this.priceListAssignmentService = priceListAssignementService;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
