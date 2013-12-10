package com.elasticpath.persistence.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductLoadTuner;

/**
 * A DAO interface for {@link ProductBundle}.
 */
public interface ProductBundleDao extends ProductDao {
	/**
	 * Saves the given {@link ProductBundle} object to DB.
	 *
	 * @param productBundle a {@link ProductBundle} to saved or updated
	 * @return the saved {@link ProductBundle}
	 * @throws DataAccessException might throw a {@link DataAccessException}
	 */
	ProductBundle saveOrUpdate(final ProductBundle productBundle) throws DataAccessException;

	/**
	 * Gets all the {@link ProductBundle}s in the system. Or empty list if none in the system.
	 *
	 * @return a list of {@link ProductBundle}
	 * @throws DataAccessException might throw a {@link DataAccessException}
	 */
	List<ProductBundle> getProductBundles() throws DataAccessException;

	/**
	 * Retrieve a {@link ProductBundle} by guid identifier.
	 *
	 * @param guid the guid of a {@link ProductBundle}
	 * @return the {@link ProductBundle} with given GUID
	 * @throws DataAccessException might throw a {@link DataAccessException}
	 */
	ProductBundle findByGuid(final String guid) throws DataAccessException;

	/**
	 * Retrieve a list of {@link ProductBundle} by list of guid identifiers.
	 *
	 * @param guids the list of guids of a {@link ProductBundle}
	 * @return the list of {@link ProductBundle} with given GUIDs
	 * @throws DataAccessException might throw a {@link DataAccessException}
	 */
	List<ProductBundle> findByGuids(List<String> guids) throws DataAccessException;

	/**
	 * Returns a list of <code>ProductBundles</code> based on the given GUIDs. If a given product GUID is not found, it won't be included in the
	 * return list.
	 *
	 * @param guids the list of GUIDs that needs to find
	 * @param productLoadTuner the load tuner, give <code>null</code> to populate all related data
	 * @throws DataAccessException might throw a {@link DataAccessException}
	 * @return a list of <code>ProductBundle</code>s
	 */
	List<ProductBundle> findByGuids(final List<String> guids, final ProductLoadTuner productLoadTuner) throws DataAccessException;

	/**
	 * Find all {@link ProductBundle} which has the given product directly as constituent. Does not count nested structures.
	 *
	 * @param productCode GUID code of the product.
	 * @return list of {@link ProductBundle}s found. Empty list if none.
	 */
	List<ProductBundle> findByProduct(final String productCode);

	/**
     * Find all {@link ProductBundle} which has the given product sku directly as constituent. Does not count nested structures.
     *
     * @param skuCode of the product sku.
     * @return list of {@link ProductBundle}s found. Empty list if none.
     */
	List<ProductBundle> findByProductSku(final String skuCode);

	/**
	 * Retrieve all descendant bundle UIDs of the given constituent UIDs.
	 *
	 * @param constituentUids the constituent UIDs.
	 * @return the list of UIDs of the direct and indirect parent bundle of the given start
	 *         constituent UIDs.
	 */
	List<Long> findBundleUids(final List<Long> constituentUids);

	/**
	 * Return a <code>ProductBundle</code> based on the guid of a constituent. This is the
	 * guid of the <code>BundleConstituent</code> not of the <code>Product</code>
	 *
	 * @param guid the guid for a bundle constituent
	 * @return the bundle which contains this constituent.
	 */
	ProductBundle findByBundleConstituentGuid(final String guid);

	/**
     * Determines whether a Product Bundle exists for the given GUID.
     *
     * @param guid the guid to check.
     * @return True if the Product Bundle exists, false otherwise.
     */
    boolean bundleExistsWithGuid(String guid);

	/**
	 * Find the uidPk of a bundle with the given bundle sku code.
	 *
	 * @param skuCode the sku code
	 * @return the uidPk of the bundle
	 */
	Long findBundleUidBySkuCode(String skuCode);

}
