package com.elasticpath.test.persister.testscenarios;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;

public class CustomerScenario extends AbstractScenario {
	private Store store;

	private Customer customer;

	private CustomerAddress address;

	private CustomerCreditCard creditCard;

	private CustomerSession customerSession;

	@Override
	public void initialize() {
		Catalog catalog = getDataPersisterFactory().getCatalogTestPersister().persistDefaultMasterCatalog();
		Warehouse warehouse = getDataPersisterFactory().getStoreTestPersister().persistDefaultWarehouse();

		store = getDataPersisterFactory().getStoreTestPersister().persistDefaultStore(catalog, warehouse);
		customer = getDataPersisterFactory().getStoreTestPersister().createDefaultCustomer(store);
		address = customer.getAddresses().get(0);
		creditCard = customer.getCreditCards().get(0);
		customerSession = getDataPersisterFactory().getStoreTestPersister().persistCustomerSessionWithAssociatedEntities(customer);
	}

	/**
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @return the address
	 */
	public CustomerAddress getAddress() {
		return address;
	}

	/**
	 * @return the creditCard
	 */
	public CustomerCreditCard getCreditCard() {
		return creditCard;
	}

	/**
	 * @return the customerSession
	 */
	public CustomerSession getCustomerSession() {
		return customerSession;
	}
}
