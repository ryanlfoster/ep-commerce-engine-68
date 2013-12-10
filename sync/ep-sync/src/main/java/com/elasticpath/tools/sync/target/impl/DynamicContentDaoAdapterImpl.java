package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.service.contentspace.DynamicContentService;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;


/**
 * Used by DST to perform persistence related activities on the DynamicContent object.
 */
public class DynamicContentDaoAdapterImpl extends AbstractDaoAdapter<DynamicContent> {

	private BeanFactory beanFactory;

	private DynamicContentService dynamicContentService;

	/**
	 * Set bean factory.
	 *
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Set the dynamicContentService.
	 * @param dynamicContentService the service
	 */
	public void setDynamicContentService(final DynamicContentService dynamicContentService) {
		this.dynamicContentService = dynamicContentService;
	}

	@Override
	public void add(final DynamicContent newPersistence) throws SyncToolRuntimeException {
		dynamicContentService.saveOrUpdate(newPersistence);
	}

	@Override
	public DynamicContent createBean(final DynamicContent bean) {
		return beanFactory.getBean(ContextIdNames.DYNAMIC_CONTENT);
	}

	@Override
	public DynamicContent get(final String guid) {
		return dynamicContentService.findByGuid(guid);
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		DynamicContent dynamicContent = dynamicContentService.findByGuid(guid);
		if (dynamicContent == null) {
			return false;
		}
		dynamicContentService.remove(dynamicContent);
		return true;
	}

	@Override
	public DynamicContent update(final DynamicContent mergedPersistence) throws SyncToolRuntimeException {
		return dynamicContentService.saveOrUpdate(mergedPersistence);
	}
}
