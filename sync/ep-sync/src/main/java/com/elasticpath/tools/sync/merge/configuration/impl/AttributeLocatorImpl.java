package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * 
 * The attribute locator class.
 *
 */
public class AttributeLocatorImpl extends AbstractEntityLocator {
	
	private AttributeService attributeService;
	
	/**
	 * @param attributeService the attributeService to set
	 */
	public void setAttributeService(final AttributeService attributeService) {
		this.attributeService = attributeService;
	}
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return Attribute.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {
		return attributeService.findByKey(guid);
	}


}
