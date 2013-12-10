package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * 
 * The tax code locator class.
 *
 */
public class TaxCodeLocatorImpl extends AbstractEntityLocator {
	
	private TaxCodeService taxCodeService;
	
	/**
	 * @param taxCodeService the taxCodeService to set
	 */
	public void setTaxCodeService(final TaxCodeService taxCodeService) {
		this.taxCodeService = taxCodeService;
	}
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return TaxCode.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {
		return taxCodeService.findByCode(guid);
	}


}
