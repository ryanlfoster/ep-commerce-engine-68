/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * A locator for the {@link SellingContext} instances.
 */
public class SellingContextLocatorImpl extends AbstractEntityLocator {

	private SellingContextService sellingContextService;
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return SellingContext.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) throws SyncToolConfigurationException {
		return sellingContextService.getByGuid(guid);
	}

	/**
	 *
	 * @return the selling context service
	 */
	protected SellingContextService getSellingContextService() {
		return sellingContextService;
	}

	/**
	 *
	 * @param sellingContextService the sellingContextService to set
	 */
	public void setSellingContextService(final SellingContextService sellingContextService) {
		this.sellingContextService = sellingContextService;
	}

}
