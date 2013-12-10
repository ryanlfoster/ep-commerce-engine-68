package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.contentspace.ContentSpaceService;

/**
 * Custom locator can recognise <code>PriceListAssignment</code> objects.
 */
public class ContentSpaceLocatorImpl extends AbstractEntityLocator {

	private ContentSpaceService contentSpaceService;

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) {
		return contentSpaceService.findByGuid(guid);
	}


	/**
	 * Sets the {@link ContentSpaceService} instance.
	 * @param contentSpaceService {@link ContentSpaceService}
	 */
	public void setContentSpaceService(final ContentSpaceService contentSpaceService) {
		this.contentSpaceService = contentSpaceService;
	}

	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return ContentSpace.class.isAssignableFrom(clazz);
	}
}
