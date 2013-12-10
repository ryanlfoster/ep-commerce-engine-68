package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Product sku dao adapter.
 */
public class ProductSkuDaoAdapterImpl extends AbstractDaoAdapter<ProductSku> {
	
	private BeanFactory beanFactory;
	
	private ProductSkuService productSkuService;

	@Override
	public void add(final ProductSku newPersistence) throws SyncToolRuntimeException {
		productSkuService.add(newPersistence);
	}

	@Override
	public ProductSku createBean(final ProductSku productSku) {
		return beanFactory.getBean(ContextIdNames.PRODUCT_SKU);
	}

	@Override
	public ProductSku get(final String guid) {
		try {
			return (ProductSku) getEntityLocator().locatePersistence(guid, ProductSku.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}
		
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final ProductSku findBySkuCode = productSkuService.findBySkuCode(guid);
		if (findBySkuCode == null) {
			// TODO: think of error collection to receive a notification about inexisting product here.
			return false;
		}
		productSkuService.removeProductSkuTree(findBySkuCode.getUidPk());
		return true;
	}

	@Override
	public ProductSku update(final ProductSku mergedPersistence) throws SyncToolRuntimeException {
		return productSkuService.saveOrUpdate(mergedPersistence);
	}
	

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param productSkuService the productSkuService to set
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}
}
