package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.pricing.PriceListAssignmentService;

/**
 * Custom locator can recognise <code>PriceListAssignment</code> objects.
 */
public class PriceListAssignmentLocatorImpl extends AbstractEntityLocator {

	private PriceListAssignmentService priceListAssignmentService;

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) {
		return priceListAssignmentService.findByGuid(guid);
	}

	/**
	 * @param priceListAssignmentService price assignment service
	 */
	public void setPriceListAssignmentService(final PriceListAssignmentService priceListAssignmentService) {
		this.priceListAssignmentService = priceListAssignmentService;
	}

	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return PriceListAssignment.class.isAssignableFrom(clazz);
	}
}
