package com.elasticpath.test.persister.testscenarios;

import java.util.List;

import com.elasticpath.domain.catalog.Product;

/**
 * This simple scenario populates database with a common test data such as store, catalog, warehouse, products and others to make common shopping
 * possible.
 */
public class ShoppingCartSimpleStoreScenario extends SimpleStoreScenario {

	private List<Product> shippableProducts;
	private List<Product> nonShippableProducts;
	
	/**
	 * Populate database with default test data using test data persisters.
	 */
	@Override
	public void initialize() {
		super.initialize();
		// Create customers with addresses and shippable/non shippable products.
		getDataPersisterFactory().getStoreTestPersister().persistDefaultCustomers(getStore());
		// Create shippable and non-shippable products products
		shippableProducts = getDataPersisterFactory().getCatalogTestPersister().persistDefaultShippableProducts(getCatalog(), getCategory(), getWarehouse());
		nonShippableProducts = getDataPersisterFactory().getCatalogTestPersister().persistDefaultNonShippableProducts(getCatalog(), getCategory(), getWarehouse());
		// Tax jurisdictions
		getDataPersisterFactory().getTaxTestPersister().persistDefaultTaxJurisdictions();
		// Create shipping service levels
		getDataPersisterFactory().getStoreTestPersister().persistDefaultShippingServiceLevels(getStore());
	}
	
	public List<Product> getShippableProducts() {
		return shippableProducts;
	}
	
	public List<Product> getNonShippableProducts() {
		return nonShippableProducts;
	}
}