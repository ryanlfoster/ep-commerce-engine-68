package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;

/**
 * Custom locator can recognise <code>PriceListAssignment</code> objects.
 */
public class DynamicContentDeliveryLocatorImpl extends AbstractEntityLocator {

	private DynamicContentDeliveryService dynamicContentDeliveryService;

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) {
		return dynamicContentDeliveryService.findByGuid(guid);
	}


	/**
	 * Sets the {@link DynamicContentDeliveryService} instance.
	 * @param dynamicContentDeliveryService {@link DynamicContentDeliveryService} 
	 */
	public void setDynamicContentDeliveryService(final DynamicContentDeliveryService dynamicContentDeliveryService) {
		this.dynamicContentDeliveryService = dynamicContentDeliveryService;
	}

	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return DynamicContentDelivery.class.isAssignableFrom(clazz);
	}
}
