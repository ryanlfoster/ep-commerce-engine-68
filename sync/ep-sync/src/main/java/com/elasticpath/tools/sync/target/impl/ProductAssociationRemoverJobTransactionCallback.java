package com.elasticpath.tools.sync.target.impl;

import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;
import com.elasticpath.tools.sync.job.JobEntry;

/**
 * This class was created because the current ChangeSet ProductAssociation support classes
 * do not remove associations deleted from the sourceObject. They just added new associations
 * or updated existing ones.
 *
 * To solve this problem, this class provides a call back method to remove product associations from the
 * targetObject Product after it is updated.
 * By removing the product associations in the targetObject, this class relies on the fact that the 
 * ADD/UPDATE product association changeSet classes will restore associations coming from the source object in 
 * the next phase of processing. 
 */
public class ProductAssociationRemoverJobTransactionCallback extends AbstractJobTransactionCallbackAdapter {
	
	private static final Logger LOG = Logger.getLogger(ProductAssociationRemoverJobTransactionCallback.class);
	
	private ProductAssociationService productAssociationService;

	private BeanFactory beanFactory;
	
	@Override
	public void postUpdateJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
		if (jobEntry.getSourceObject() instanceof Product) {
			Product product = (Product) jobEntry.getSourceObject();
			LOG.trace("Removing all product associations for product with Guid: " + product.getGuid());

			ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
			criteria.setSourceProduct(product);
			
			productAssociationService.removeByCriteria(criteria);
		}
	}

	/**
	 * Get bean with specified id from bean factory.
	 *
	 * @param beanID id string of the bean to get
	 * @return the bean
	 */
	protected Object getBean(final String beanID) {
		return beanFactory.getBean(beanID);
	}

	@Override
	public String getCallbackID() {
		return "Product Association Remover Callback";
	}

	/**
	 * Set the productAssociationService.
	 * 
	 * @param productAssociationService the service
	 */
	public void setProductAssociationService(final ProductAssociationService productAssociationService) {
		this.productAssociationService = productAssociationService;
	}
	
	/**
	 * Set the spring bean factory to use.
	 * 
	 * @param beanFactory instance
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
}
