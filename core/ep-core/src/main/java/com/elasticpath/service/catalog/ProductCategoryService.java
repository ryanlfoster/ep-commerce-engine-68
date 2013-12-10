package com.elasticpath.service.catalog;

import java.util.Collection;

import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Service for working with {@link ProductCategory}s. These are encapsulated inside {@link Product} and not intended for use from application code.
 */
public interface ProductCategoryService extends EpPersistenceService {

	/**
	 * Saves or updates a given <code>ProductCategoryService</code>.
	 * 
	 * @param productCategory catalog the <code>ProductCategoryService</code> to save or update.
	 * @return the updated object instance
	 * @throws EpServiceException in case of any errors.
	 */
	ProductCategory saveOrUpdate(final ProductCategory productCategory) throws EpServiceException;

	/**
	 * Deletes the productCategory.
	 * 
	 * @param productCategory The ProductCategoryService to remove
	 * @throws EpServiceException in case of any errors
	 */
	void remove(final ProductCategory productCategory) throws EpServiceException;

	/**
	 * Lookup of {@link ProductCategory} by a category UID.
	 * 
	 * @param categoryUid The category UID
	 * @return The ProductCategory for the given category UID.
	 */
	Collection<ProductCategory> findByCategoryUid(final long categoryUid);

	/**
	 * Lookup of {@link ProductCategory} by a category code and catalog code.
	 * 
	 * @param categoryCode The category code
	 * @param catalogCode The category code
	 * @return The ProductCategory for the given category in the given catalog.
	 */
	Collection<ProductCategory> findByCategoryAndCatalog(final String catalogCode, final String categoryCode);

	/**
	 * Lookup of {@link ProductCategory} by a catalog, category and product.
	 * 
	 * @param catalogCode The category code
	 * @param categoryCode The category code
	 * @param productCode The product code
	 * @return The ProductCategory.
	 */
	ProductCategory findByCategoryAndProduct(final String catalogCode, final String categoryCode, final String productCode);
}