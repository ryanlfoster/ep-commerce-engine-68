package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * Customer locator can recognise <code>BaseAmount</code> objects.
 */
public class BaseAmountLocatorImpl extends AbstractEntityLocator {

	private BaseAmountService baseAmountService;


	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {
		return baseAmountService.findByGuid(guid);
	}

	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return BaseAmountDTO.class.isAssignableFrom(clazz) || BaseAmount.class.isAssignableFrom(clazz);
	}

	/**
	 * @param baseAmountService the base amount service
	 */
	public void setBaseAmountService(final BaseAmountService baseAmountService) {
		this.baseAmountService = baseAmountService;
	}

    @Override
    public boolean entityExists(final String guid, final Class<?> clazz) {
        return baseAmountService.guidExists(guid);
    }

}
