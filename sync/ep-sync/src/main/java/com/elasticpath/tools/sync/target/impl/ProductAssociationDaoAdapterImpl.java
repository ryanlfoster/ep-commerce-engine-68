/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.tools.sync.target.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.target.AssociatedDaoAdapter;

/**
 * Product associations Dao Adapter.
 */
public class ProductAssociationDaoAdapterImpl extends AbstractDaoAdapter<ProductAssociation> implements AssociatedDaoAdapter<ProductAssociation> {

	private BeanFactory beanFactory;
	private ProductAssociationService productAssociationService;
	private LoadTuner productAssociationLoadTuner;
	
	@Override
	public void add(final ProductAssociation newPersistence) throws SyncToolRuntimeException {
		ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		criteria.setAssociationType(newPersistence.getAssociationType());
		criteria.setCatalogCode(newPersistence.getCatalog().getCode());
		criteria.setSourceProduct(newPersistence.getSourceProduct());
		criteria.setTargetProduct(newPersistence.getTargetProduct());
		
		// sanity check.. it may be there already
		boolean exists = CollectionUtils.isNotEmpty(productAssociationService.findByCriteria(criteria));
		if (!exists) {
			productAssociationService.add(newPersistence);
		}
	}

	@Override
	public ProductAssociation createBean(final ProductAssociation bean) {
		ProductAssociation association = beanFactory.getBean(ContextIdNames.PRODUCT_ASSOCIATION);
		association.initialize();
		return association;
	}

	@Override
	public ProductAssociation get(final String guid) {
		try {		
			return (ProductAssociation) getEntityLocator().locatePersistence(guid, ProductAssociation.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate Product Association persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final ProductAssociation association = productAssociationService.findByGuid(guid, productAssociationLoadTuner);
		if (association == null) {
			return false;
		}

		productAssociationService.remove(association);
		return true;
	}

	@Override
	public ProductAssociation update(final ProductAssociation mergedPersistence) throws SyncToolRuntimeException {
		return productAssociationService.update(mergedPersistence);
	}
	
	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	

	@Override
	public List<String> getAssociatedGuids(final Class< ? > clazz, final String guid) {
		if (Product.class.isAssignableFrom(clazz)) {
			ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
			criteria.setSourceProductCode(guid);

			List<ProductAssociation> associations = productAssociationService.findByCriteria(criteria, productAssociationLoadTuner);
			List<String> guids = new ArrayList<String>();
			for (ProductAssociation assoc : associations) {
				guids.add(assoc.getGuid());
			}
			return guids;
		}
		return Collections.emptyList();
	}

	@Override
	public Class< ? > getType() {
		return ProductAssociation.class;
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
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
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
