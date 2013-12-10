package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 *
 * The product type locator class.
 *
 */
public class ProductTypeLocatorImpl extends AbstractEntityLocator {

	private ProductTypeService productTypeService;

	/**
	 * @param productTypeService the productTypeService to set
	 */
	public void setProductTypeService(final ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}

	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return ProductType.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {
		return productTypeService.findByGuid(guid);
	}

}
