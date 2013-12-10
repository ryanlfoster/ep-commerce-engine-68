package com.elasticpath.service.dataimport.impl;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.persistence.openjpa.PersistenceInterceptor;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.service.customer.CustomerService;

/**
 * An import runner to import Customers.
 */
public class ImportJobRunnerCustomerImpl extends AbstractImportJobRunnerImpl {
	
	private CustomerService customerService;
	
	/**
	 * Sets the CustomerService.
	 * @param service the customer service
	 */
	public void setCustomerService(final CustomerService service) {
		this.customerService = service;
	}
	
	/**
	 * Find the entity with the given guid.
	 * 
	 * @param guid the guid
	 * @return the entity with the given guid if it exists, otherwise <code>null</code>.
	 */
	protected Entity findEntityByGuid(final String guid) {
		return getImportGuidHelper().findCustomerByGuid(guid);
	}

	/**
	 * Creates a new entity.
	 * 
	 * @param baseObject the base object
	 * @return the newly created entity
	 */
	protected Entity createNewEntity(final Object baseObject) {
		return getBean(ContextIdNames.CUSTOMER);
	}
	
	/**
	 * Returns the commit unit.
	 * 
	 * @return the commit unit.
	 */
	protected int getCommitUnit() {
		return ImportConstants.COMMIT_UNIT;
	}

	/**
	 * Update the entity before it gets saved.
	 * 
	 * @param entity the entity to save
	 */
	@Override
	protected void updateEntityBeforeSave(final Entity entity) {
		Customer customer = (Customer) entity;
		customer.setStoreCode(getImportJob().getStore().getCode());
		if (customer instanceof PersistenceInterceptor) {
			((PersistenceInterceptor) customer).executeBeforePersistAction();
		}
		customerService.setCustomerDefaultGroup(customer);
		customer.setLastEditDate(getTimeService().getCurrentTime());
	}

	/**
	 * This method does nothing in order to avoid change set processing.
	 */
	@Override
	protected void prepareChangeSetProcessing() {
		// change set processing is not supported for the customers import
	}

}
