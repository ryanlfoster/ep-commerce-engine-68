package com.elasticpath.test.persister.testscenarios;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.test.persister.StoreTestPersister;

/**
 * This simple scenario populates database with a common test data such as store, catalog, warehouse etc.
 */
public class SimpleStoreScenario extends AbstractScenario {
	/** The reference to the single store in this scenario. */
	protected Store store;

	/** The warehouse used by the store. */
	protected Warehouse warehouse;

	/** The catalog used by the store. */
	protected Catalog catalog;

	/** A single category within the store's catalog. */
	protected Category category;

	/** The only shipping region supported by this store. */
	protected ShippingRegion shippingRegion;

	/** The only shipping service level supported by this store. */
	protected ShippingServiceLevel shippingServiceLevel;

	public SimpleStoreScenario() {
		super();
	}

	/**
	 * Populate database with default common data.
	 */
	@Override
	public void initialize() {
		catalog = getDataPersisterFactory().getCatalogTestPersister().persistDefaultMasterCatalog();
		warehouse = getDataPersisterFactory().getStoreTestPersister().persistDefaultWarehouse();
		store = getDataPersisterFactory().getStoreTestPersister().persistDefaultStore(catalog, warehouse);
		category = getDataPersisterFactory().getCatalogTestPersister().persistDefaultCategories(catalog);
		shippingRegion = getDataPersisterFactory().getStoreTestPersister().getShippingRegion(StoreTestPersister.DEFAULT_SHIPPING_REGION_NAME);
		shippingServiceLevel = getDataPersisterFactory().getStoreTestPersister().persistDefaultShippingServiceLevel(store);
		store.setCatalog(catalog);
	}

	/**
	 * Get the populated store.
	 *
	 * @return store
	 */
	public Store getStore() {
		return store;
	}

	/**
	 * Get the populated catalog.
	 *
	 * @return catalog
	 */
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * Get the populated warehouse.
	 *
	 * @return warehouse
	 */
	public Warehouse getWarehouse() {
		return warehouse;
	}

	/**
	 * Get the populated category.
	 *
	 * @return catagory
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @return the shippingRegion
	 */
	public ShippingRegion getShippingRegion() {
		return shippingRegion;
	}

	/**
	 * @return the shippingServiceLevel
	 */
	public ShippingServiceLevel getShippingServiceLevel() {
		return shippingServiceLevel;
	}
}
