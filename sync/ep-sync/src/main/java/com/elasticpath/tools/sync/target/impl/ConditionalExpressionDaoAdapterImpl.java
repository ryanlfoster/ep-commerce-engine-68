/**
 * 
 */
package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Dao adapter for ConditionalExpression.
 *
 */
public class ConditionalExpressionDaoAdapterImpl extends AbstractDaoAdapter<ConditionalExpression> {

	private BeanFactory beanFactory;
	
	private TagConditionService tagConditionService;
	
	@Override
	public void add(final ConditionalExpression newPersistence) throws SyncToolRuntimeException {
		this.tagConditionService.saveOrUpdate(newPersistence);
	}

	@Override
	public ConditionalExpression createBean(final ConditionalExpression bean) {
		return beanFactory.getBean(ContextIdNames.CONDITIONAL_EXPRESSION);
	}

	@Override
	public ConditionalExpression get(final String guid) {
		try {
			return (ConditionalExpression) getEntityLocator().locatePersistence(guid, ConditionalExpression.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}		
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		ConditionalExpression foundByGuid = tagConditionService.findByGuid(guid);
		if (foundByGuid == null) {
			// TODO: think of error collection to receive a notification about non-existing conditional expression here.
			return false;
		}
		tagConditionService.delete(foundByGuid);
		return true;
	}

	@Override
	public ConditionalExpression update(final ConditionalExpression mergedPersistence) throws SyncToolRuntimeException {
		return this.tagConditionService.saveOrUpdate(mergedPersistence);
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param tagConditionService the tagConditionService to set
	 */
	public void setTagConditionService(final TagConditionService tagConditionService) {
		this.tagConditionService = tagConditionService;
	}

}
