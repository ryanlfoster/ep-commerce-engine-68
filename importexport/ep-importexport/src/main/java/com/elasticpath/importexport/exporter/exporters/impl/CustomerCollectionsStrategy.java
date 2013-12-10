package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;

import com.elasticpath.common.dto.customer.CustomerDTO;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.service.customer.CustomerService;

/**
 * Strategy which determines how collections in {@link Customer} are managed.
 */
public class CustomerCollectionsStrategy implements CollectionsStrategy<Customer, CustomerDTO> {

	private final boolean clearAddresses, clearGroups, clearCreditCards;

	private final CustomerService customerService;

	/**
	 * Constructor which sets individual collection strategies.
	 * 
	 * @param importerConfiguration the {@link ImporterConfiguration} to set.
	 * @param customerService the {@link CustomerService} to set.
	 */
	public CustomerCollectionsStrategy(final ImporterConfiguration importerConfiguration, final CustomerService customerService) {

		this.customerService = customerService;

		clearAddresses = importerConfiguration.getCollectionStrategyType(DependentElementType.ADDRESSES).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		clearGroups = importerConfiguration.getCollectionStrategyType(DependentElementType.CUSTOMER_GROUPS).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		clearCreditCards = importerConfiguration.getCollectionStrategyType(DependentElementType.CREDIT_CARDS).equals(
				CollectionStrategyType.CLEAR_COLLECTION);

		// clearSessions = importerConfiguration.getCollectionStrategyType(DependentElementType.CUSTOMER_SESSIONS).equals(
		// CollectionStrategyType.CLEAR_COLLECTION);

	}

	@Override
	public void prepareCollections(final Customer customer, final CustomerDTO dto) {

		if (clearAddresses) {
			customer.getAddresses().clear();
		}

		if (clearCreditCards) {
			customer.setCreditCards(new ArrayList<CustomerCreditCard>());
		}

		if (clearGroups) {
			customer.getCustomerGroups().clear();
		}
		customerService.update(customer);
	}

	public boolean isForPersistentObjectsOnly() {
		return true;
	}
}
