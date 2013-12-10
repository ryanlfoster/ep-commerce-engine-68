package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.contentspace.DynamicContentService;

/**
 * Custom locator can recognise <code>PriceListAssignment</code> objects.
 */
public class DynamicContentLocatorImpl extends AbstractEntityLocator {

	private DynamicContentService dynamicContentService;

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) {
		return dynamicContentService.findByGuid(guid);
	}


	/**
	 * Sets the {@link DynamicContentService} instance.
	 * @param dynamicContentService {@link DynamicContentService}
	 */
	public void setDynamicContentService(final DynamicContentService dynamicContentService) {
		this.dynamicContentService = dynamicContentService;
	}

	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return DynamicContent.class.isAssignableFrom(clazz);
	}
}
