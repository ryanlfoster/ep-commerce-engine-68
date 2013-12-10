/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * The coupon config locator class.
 */
public class ProductAssociationLocatorImpl extends AbstractEntityLocator {

	
	private ProductAssociationService productAssociationService;
	private LoadTuner productAssociationLoadTuner;
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return ProductAssociation.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) throws SyncToolConfigurationException {
		return productAssociationService.findByGuid(guid, productAssociationLoadTuner);
	}

	/**
	 *
	 * @return the productAssociationService
	 */
	public ProductAssociationService getProductAssociationService() {
		return productAssociationService;
	}

	/**
	 *
	 * @param productAssociationService the productAssociationService to set
	 */
	public void setProductAssociationService(final ProductAssociationService productAssociationService) {
		this.productAssociationService = productAssociationService;
	}

	/**
	 *
	 * @return the productAssociationLoadTuner
	 */
	public LoadTuner getProductAssociationLoadTuner() {
		return productAssociationLoadTuner;
	}

	/**
	 *
	 * @param productAssociationLoadTuner the productAssociationLoadTuner to set
	 */
	public void setProductAssociationLoadTuner(final LoadTuner productAssociationLoadTuner) {
		this.productAssociationLoadTuner = productAssociationLoadTuner;
	}

}
