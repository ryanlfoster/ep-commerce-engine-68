package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * 
 * The product sku locator class.
 *
 */
public class ProductSkuLocatorImpl extends AbstractEntityLocator {
	
	private ProductSkuService productSkuService;

	/**
	 * @param productSkuService product SKU service
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return ProductSku.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {
		return productSkuService.findByGuid(guid);
	}


}
