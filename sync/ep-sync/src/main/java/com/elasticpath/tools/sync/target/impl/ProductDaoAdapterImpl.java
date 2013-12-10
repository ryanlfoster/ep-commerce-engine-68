package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Product Dao adapter.
 */
public class ProductDaoAdapterImpl extends AbstractDaoAdapter<Product> {

	private ProductService productService;

	private BeanFactory beanFactory;

	@Override
	public boolean remove(final String guid) {
		Product foundByGuid = productService.findByGuid(guid);
		if (foundByGuid == null) {
			// TODO: think of error collection to receive a notification about inexisting product here.
			return false;
		}
		productService.removeProductTree(foundByGuid.getUidPk());
		return true;
	}

	@Override
	public Product update(final Product mergedPersistence) throws SyncToolRuntimeException {
		Product saveOrUpdate = productService.saveOrUpdate(mergedPersistence);
		// The is an issue (bug?) with OpenJPA:
		// First job entry: a multi-sku product is updated and a new sku1 in inserted
		// Second job entry: the sku1 is inserted. The sku1 is attempted to be looked up by JPQL which should end up with flushing from the previous
		// step, which is not occurred by some reason. Thus, explicit flush is required.		
		return saveOrUpdate;
	}

	@Override
	public void add(final Product newPersistence) throws SyncToolRuntimeException {
		productService.saveOrUpdate(newPersistence);
	}

	@Override
	public Product createBean(final Product product) {
		return beanFactory.getBean(ContextIdNames.PRODUCT);
	}

	@Override
	public Product get(final String guid) {
		try {
			return (Product) getEntityLocator().locatePersistence(guid, Product.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}		
	}

	/**
	 * @param productService the productService to set
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
