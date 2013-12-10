package com.elasticpath.importexport.common.caching;

import java.util.List;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Caching service interface provides the ability to load various entity by given guid that necessary for import operations.
 */
public interface CachingService {

	/**
	 * Finds Product Type by given name.
	 * 
	 * @param name the name of product type
	 * @return the obtained ProductType or null if product type with given name does not exist
	 */
	ProductType findProductTypeByName(final String name);
	
	/**
	 * Finds Product by given code.
	 * 
	 * @param code the code of product
	 * @return the obtained Product or null if product with given code does not exist
	 */
	Product findProductByCode(final String code);
	
	/**
	 * Finds ProductSku by given code.
	 * 
	 * @param code the code of productSku
	 * @return the obtained ProductSku or null if productSku with given code does not exist
	 */
	ProductSku findSkuByCode(final String code);

	/**
	 * Finds Product by given code loaded with the given load tuner.
	 * 
	 * @param code the code of product
	 * @param loadTuner the load tuner to use 
	 * @return the obtained Product or null if product with given code does not exist
	 */
	Product findProductByCode(final String code, LoadTuner loadTuner);
	
	/**
	 * Finds ProductBundle by given code.
	 * 
	 * @param code the code of product
	 * @return the obtained ProductBundle or null if product with given code does not exist
	 */
	ProductBundle findProductBundleByCode(final String code);
	
	/**
	 * Finds ProductBundle by given code loaded with the given load tuner.
	 * 
	 * @param code the code of product
	 * @param loadTuner the load tuner to use 
	 * @return the obtained ProductBundle or null if product with given code does not exist
	 */
	ProductBundle findProductBundleByCode(final String code, final LoadTuner loadTuner);

	/**
	 * Finds Tax Code by code.
	 * 
	 * @param code the code
	 * @return the obtained TaxCode or null if tax code with given code does not exist
	 */
	TaxCode findTaxCodeByCode(final String code);

	/**
	 * Finds Brand by code.
	 * 
	 * @param code the brand code
	 * @return the obtained Brand or null if brand with given code does not exist
	 */
	Brand findBrandByCode(final String code);

	/**
	 * Finds Category by code in the catalog with the given code.
	 * 
	 * @param categoryCode the category code.
	 * @param catalogCode the catalog code
	 * @return the obtained Category or null if category with given category code does not exist in a particular catalog
	 */
	Category findCategoryByCode(final String categoryCode, final String catalogCode);

	/**
	 * Finds Catalog by code.
	 * 
	 * @param catalogCode the catalog code
	 * @return the obtained Catalog or null if catalog with given code does not exist
	 */
	Catalog findCatalogByCode(final String catalogCode);

	/**
	 * Finds Warehouse by code.
	 * 
	 * @param code the Warehouse code
	 * @return the obtained Warehouse or null if Warehouse with given code does not exist
	 */
	Warehouse findWarehouseByCode(final String code);
	
	/**
	 * Finds All All Warehouses.
	 *
	 * @return List of All Warehouses
	 */
	List<Warehouse> findAllWarehouses();
	
	/**
	 * Finds attribute by key.
	 * 
	 * @param key the attribute key
	 * @return the obtained Attribute or null if attribute with given key does not exist
	 */
	Attribute findAttribiteByKey(final String key);

	/**
	 * Finds sku option by key.
	 * 
	 * @param key the sku option keu
	 * @return the obtained SkuOption or null if sku option with given key does not exist
	 */
	SkuOption findSkuOptionByKey(final String key);
	
	/**
	 * Finds out if SkuOptionValue is in use.
	 *
	 * @param uid the skuOptionValue Uid.
	 * @return true if skuOptionValue is in use
	 */
	boolean isSkuOptionValueInUse(final long uid);
	
	/**
	 * Finds category type by name.
	 * 
	 * @param typeName the category type name
	 * @return the obtained CategoryType or null if categoryType with given name does not exist
	 */
	CategoryType findCategoryTypeByName(final String typeName);

	/**
	 * Finds Store by code.
	 * 
	 * @param storeCode the store code
	 * @return Store instance or null if given code does not exist
	 */
	Store findStoreByCode(final String storeCode);

	/**
	 * Finds a catalog by its code.
	 * 
	 * @param code the catalog code
	 * @param loadTuner the load tuner
	 * @return the catalog or null if not found
	 */
	Catalog findCatalogByCode(String code, LoadTuner loadTuner);
}
