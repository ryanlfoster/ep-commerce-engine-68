package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Dao adapter for {@link com.elasticpath.domain.attribute.Attribute}; used in Data Sync Tool.
 */
public class AttributeDaoAdapter extends AbstractDaoAdapter<Attribute> {

	private AttributeService attributeService;

	private BeanFactory beanFactory;
	
	@Override
	public Attribute update(final Attribute mergedPersistence) throws SyncToolRuntimeException {
		return attributeService.update(mergedPersistence);
	}

	@Override
	public void add(final Attribute newPersistence) throws SyncToolRuntimeException {
		attributeService.add(newPersistence);
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final Attribute attribute = get(guid);
		if (attribute == null) {
			return false;
		}
		attributeService.remove(attribute);
		return true;
	}

	@Override
	public Attribute get(final String guid) {
		try {
			return (Attribute) getEntityLocator().locatePersistence(guid, Attribute.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}
	}

	@Override
	public Attribute createBean(final Attribute bean) {
		return beanFactory.getBean(ContextIdNames.ATTRIBUTE);
	}

	/**
	 * @return the attributeService
	 */
	protected AttributeService getAttributeService() {
		return attributeService;
	}

	/**
	 * @param attributeService the attributeService to set
	 */
	public void setAttributeService(final AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	/**
	 * @return the beanFactory
	 */
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
