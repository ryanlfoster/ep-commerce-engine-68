package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.service.pricing.BaseAmountFactory;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.dao.BaseAmountDao;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * BaseAmount adapter. 
 */
public class BaseAmountDaoAdapterImpl extends AbstractDaoAdapter<BaseAmount> {

	private BaseAmountService baseAmountService;
	private BaseAmountDao baseAmountDao;

	private BeanFactory beanFactory;
	
	@Override
	public void add(final BaseAmount newPersistence) throws SyncToolRuntimeException {
		baseAmountService.add(newPersistence);
	}

	@Override
	public BaseAmount createBean(final BaseAmount bean) {
		BaseAmountFactory baseAmountFactory = beanFactory.getBean(ContextIdNames.BASE_AMOUNT_FACTORY);
		return baseAmountFactory.createBaseAmount();
	}

	@Override
	public BaseAmount get(final String guid) {
		try {
			return (BaseAmount) getEntityLocator().locatePersistence(guid, BaseAmount.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence.", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		BaseAmount found = baseAmountService.findByGuid(guid);
		if (found == null) {
			return false;
		}
		baseAmountService.delete(found);
		return true;
		
	}

	@Override
	public BaseAmount update(final BaseAmount mergedPersistence) throws SyncToolRuntimeException {
		return baseAmountDao.update(mergedPersistence);
	}
	
	/**
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param baseAmountService the baseAmountService 
	 */
	public void setBaseAmountService(final BaseAmountService baseAmountService) {
		this.baseAmountService = baseAmountService;
	}
	
	
	/**
	 * Sets the BaseAmountDao.
	 *
	 * @param baseAmountDao the BaseAmountDao
	 */
	public void setBaseAmountDao(final BaseAmountDao baseAmountDao) {
		this.baseAmountDao = baseAmountDao;
	}

}
