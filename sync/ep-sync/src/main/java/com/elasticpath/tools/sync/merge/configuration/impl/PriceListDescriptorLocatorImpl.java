package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * Custom locator can recognise <code>PriceListDescriptor</code> objects.
 */
 // ---- DOCPriceListDescriptorLocatorImpl
public class PriceListDescriptorLocatorImpl extends AbstractEntityLocator {

	private PriceListDescriptorService priceListDescriptorService;

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) {
		return priceListDescriptorService.findByGuid(guid);
	}

	/**
	 * @param priceListDescriptorService price list descriptor service
	 */
	public void setPriceListDescriptorService(final PriceListDescriptorService priceListDescriptorService) {
		this.priceListDescriptorService = priceListDescriptorService;
	}

	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		//This class should be responsible for both PriceListDescriptorDTO and PriceListDescriptor
		return PriceListDescriptorDTO.class.isAssignableFrom(clazz) || PriceListDescriptor.class.isAssignableFrom(clazz);
	}
}
// ---- DOCPriceListDescriptorLocatorImpl
