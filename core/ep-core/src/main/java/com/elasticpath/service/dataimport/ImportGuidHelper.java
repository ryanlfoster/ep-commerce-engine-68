package com.elasticpath.service.dataimport;

import java.util.List;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * This helper service provides the ability to load various entity by th given guid.
 */
public interface ImportGuidHelper extends EpPersistenceService {
	/**
	 * Retrieve the product with the given guid.
	 * 
	 * @param guid the guid of the product
	 * @param flagLoadCategories set it to <code>true</code> to load categories
	 * @param flagLoadAttributes set it to <code>true</code> to load attributes values
	 * @param flagLoadSkusAndPrices set it to <code>true</code> to load product SKUs and product/catalog prices
	 * @return the product with the given guid
	 * @throws EpServiceException in case of any error
	 */
	Product findProductByGuid(String guid, boolean flagLoadCategories, 
			boolean flagLoadAttributes, boolean flagLoadSkusAndPrices) throws EpServiceException;

	/**
	 * Return <code>true</code> if the given product guid exists.
	 * 
	 * @param guid the guid of the product
	 * @return <code>true</code> if the given product guid exists
	 * @throws EpServiceException in case of any error
	 */
	boolean isProductGuidExist(String guid) throws EpServiceException;

	/**
	 * Retrieve the customer with the given guid.
	 * 
	 * @param guid the guid of the Customer
	 * @return the <code>Customer</code> with the given guid
	 * @throws EpServiceException in case of any error
	 */
	Customer findCustomerByGuid(String guid) throws EpServiceException;

	/**
	 * Return <code>true</code> if the given Customer guid exists.
	 * 
	 * @param guid the guid of the Customer
	 * @return <code>true</code> if the given Customer guid exists
	 * @throws EpServiceException in case of any error
	 */
	boolean isCustomerGuidExist(String guid) throws EpServiceException;

	/**
	 * Retrieve the category with the given guid.
	 * 
	 * @param guid the guid of the category
	 * @param catalogGuid the catalog GUID
	 * @param flagLoadAttributes set it to <code>true</code> to load attributes values
	 * @param flagLoadChildren set it to <code>true</code> to load children of the category
	 * @return the category with the given guid
	 * @throws EpServiceException in case of any error
	 */
	Category findCategoryByGuidAndCatalogGuid(String guid, final String catalogGuid, 
			boolean flagLoadAttributes, boolean flagLoadChildren)
			throws EpServiceException;

	/**
	 * Return <code>true</code> if the given category guid exist for the catalog with the given UID.
	 * 
	 * @param guid the guid of the category
	 * @param catalogGuid the catalog GUID
	 * @return <code>true</code> if the given category guid exist
	 * @throws EpServiceException in case of any error
	 */
	boolean isCategoryGuidExist(String guid, String catalogGuid) throws EpServiceException;

	/**
	 * Retrieve the product sku with the given guid.
	 * 
	 * @param guid the guid of the product sku
	 * @return the product sku with the given guid
	 * @throws EpServiceException in case of any error
	 */
	ProductSku findProductSkuByGuid(String guid) throws EpServiceException;

	/**
	 * Return <code>true</code> if the given productsku guid exist.
	 * 
	 * @param guid the guid of the productsku
	 * @return <code>true</code> if the given productsku guid exist
	 * @throws EpServiceException in case of any error
	 */
	boolean isProductSkuGuidExist(String guid) throws EpServiceException;

	/**
	 * Retrieve the brand with the given guid for the catalog with the given catalog UID.
	 * 
	 * @param guid the guid of the brand
	 * @param catalogGuid the catalog GUID
	 * @return the brand with the given guid
	 * @throws EpServiceException in case of any error
	 */
	Brand findBrandByGuidAndCatalogGuid(String guid, String catalogGuid) throws EpServiceException;

	/**
	 * Return <code>true</code> if the given brand guid exist for the catalog with the given catalog UID.
	 * 
	 * @param guid the guid of the brand
	 * @param catalogGuid the catalog GUID
	 * @return <code>true</code> if the given brand guid exist
	 * @throws EpServiceException in case of any error
	 */
	boolean isBrandGuidExist(String guid, String catalogGuid);

	/**
	 * Retrieve the <code>SkuOptionValue</code> with the given key.
	 * 
	 * @param key the key of the SKU option value
	 * @return the <code>SkuOptionValue</code>
	 */
	SkuOptionValue findSkuOptionValueByKey(String key);

	/**
	 * Returns a <code>List</code> of Category objects linked to the Category indicated by the given <code>masterCategoryUid</code>.
	 * 
	 * @param masterCategoryUid the master category uid to look up
	 * @return a <code>List</code> of all UIDs of all Category objects linked to the Category indicated by the given <code>masterCategoryUid</code>
	 */
	List<Category> findLinkedCategories(long masterCategoryUid);
	
	/**
	 * Delete all product associations from the specified product in the 
	 * specified catalog. 
	 * 
	 * @param sourceProductCode the unique code for the product whose
	 *        associations are to be removed.
	 * @param catalogCode the code for the catalog containing the product whose
	 *        associations are to be removed.
	 */
	void deleteProductAssociations(String sourceProductCode, String catalogCode);

	/**
	 * Return true if the given tax code exists.
	 * 
	 * @param taxCode the tax code to check
	 * @return true if the tax code exists
	 */
	boolean isTaxCodeExist(final String taxCode);
	
}
