package com.elasticpath.domain.cmuser;

import java.util.Collection;
import java.util.Set;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;

/**
 * 
 * Grantable objects for <code>CmUser</code>. At this moment supported:
 * Price Lists;
 * Stores;
 * Warehouses;
 * Catalogs. 
 * 
 */
public interface GrantableObjects {
	
	/**
	 * @return true if user has access to all stores.
	 */
	boolean isAllStoresAccess();
	
	/**
	 * Sets whether this <code>CmUser</code> has access to all stores.
	 * 
	 * @param allStoresAccess Set to true if the customer has access to all stores
	 */
	void setAllStoresAccess(boolean allStoresAccess);
	
	/**
	 * True if this has access to all price lists.
	 * @return true if this cmUser has access to all price lists.
	 */
	boolean isAllPriceListsAccess();

	/**
	 * Sets whether this <code>CmUser</code> has access to all price lists.
	 * 
	 * @param allPriceListsAccess Set to true if access to all price listst.
	 */
	void setAllPriceListsAccess(boolean allPriceListsAccess);	

	/**
	 * @return true if user has access to all stores.
	 */
	boolean isAllWarehousesAccess();
	
	/**
	 * Sets whether this <code>CmUser</code> has access to all warehouses.
	 * 
	 * @param allWarehousesAccess Set to true if access to all warehouses.
	 */
	void setAllWarehousesAccess(boolean allWarehousesAccess);

	/**
	 * @return true if user has access to all catalogs.
	 */
	boolean isAllCatalogsAccess();
	
	/**
	 * Sets whether this <code>CmUser</code> has access to all catalogs.
	 *
	 * @param allCatalogsAccess boolean.
	 */
	void setAllCatalogsAccess(boolean allCatalogsAccess);

	/**
	 * Return all COMPLETE stores to which the user has access.
	 * IMPORTANT: The returned objects are READONLY.
	 * 
	 * @return set of stores.
	 */
	Set<Store> getStores();	

	/**
	 * Return all warehouses to which the user has access.
	 * IMPORTANT: The returned objects are READONLY.
	 * 
	 * @return set of warehouses.
	 */
	Set<Warehouse> getWarehouses();

	/**
	 * Return all catalogs to which the user has access.
	 * IMPORTANT: The returned objects are READONLY.
	 * 
	 * @return set of catalogs.
	 */
	Set<Catalog> getCatalogs();
	
	/**
	 * Get the collection of accessable price lists guids.
	 * @return collection of accessable price lists guids.
	 */
	Collection<String> getPriceLists();
	
	/**
	 * Adds an accessible price list to this user.
	 * 
	 * @param priceListGuid price list guid
	 */
	void addPriceList(String priceListGuid);
	
	/**
	 * Removes an accessible price list from this user.
	 * 
     * @param priceListGuid price list guid
	 */
	void removePriceList(String priceListGuid);	

	/**
	 * @param store store.
	 */
	void removeStore(Store store);

	/**
	 * @param warehouse warehouse.
	 */
	void removeWarehouse(Warehouse warehouse);

	/**
	 * @param catalog catalog.
	 */
	void removeCatalog(Catalog catalog);

	/**
	 * @param store store.
	 */
	void addStore(Store store);

	/**
	 * @param warehouse warehouse.
	 */
	void addWarehouse(Warehouse warehouse);

	/**
	 * @param catalog catalog.
	 */
	void addCatalog(Catalog catalog);	

}
