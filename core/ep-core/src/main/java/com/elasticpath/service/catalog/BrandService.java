package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides brand-related business services.
 *
 */
public interface BrandService extends EpPersistenceService {

	/**
	 * Adds the given brand.
	 *
	 * @param brand the brand to add
	 * @return the persisted instance of brand
	 * @throws EpServiceException - in case of any errors
	 */
	Brand add(final Brand brand) throws EpServiceException;

	/**
	 * Get the brand with the given UID. Return null if no matching record exists.
	 *
	 * @param brandUid
	 *            the Brand UID.
	 *
	 * @return the brand if UID exists, otherwise null
	 *
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	Brand get(final long brandUid) throws EpServiceException;

	/**
	 * Save or update the given brand.
	 *
	 * @param brand
	 *            the brand to save or update
	 * @return the persisted or updated brand
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	Brand saveOrUpdate(final Brand brand) throws EpServiceException;

	/**
	 * Deletes the brand.
	 *
	 * @param brand the brand to remove
	 * @throws EpServiceException in case of any errors
	 */
	void remove(final Brand brand) throws EpServiceException;

	/**
	 * Lists all brand stored in the database.
	 *
	 * @return a list of brand
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	List<Brand> list() throws EpServiceException;
	
	/**
	 * Checks whether the given UID is in use.
	 *
	 * @param uidToCheck the UID to check that is in use
	 * @return whether the UID is currently in use or not
	 * @throws EpServiceException in case of any errors
	 */
	boolean isInUse(final long uidToCheck) throws EpServiceException;
	
	/**
	 * Finds all the {@link Brand}s for the specified catalog UID.
	 *
	 * @param catalogUid the catalog UID
	 * @return a {@link List} of {@link Brand}s
	 * @throws EpServiceException in case of any errors
	 */
	List<Brand> findAllBrandsFromCatalog(final long catalogUid) throws EpServiceException;

	/**
	 * Finds all the {@link Brand}s for the specified catalog UIDs.
	 *
	 * @param catalogs in a list
	 * @return a {@link List} of {@link Brand}s
	 * @throws EpServiceException in case of any errors
	 */
	List<Brand> findAllBrandsFromCatalogList(final Collection<Catalog> catalogs) throws EpServiceException;
	
	/**
	 * Return a list of uids for all brands in use.
	 *
	 * @return a list of uids for all brands in use
	 */
	List<Long> getBrandInUseUidList();
	
	/**
	 * Return a {@link List} of brand UIDs for only those brand UIDs that are in use.
	 * 
	 * @param brandUids brand UIDs to check
	 * @return {@link List} of brand UIDs for only those brand UIDs that are in use
	 * @throws EpServiceException in case of any errors
	 */
	List<Long> getBrandInUseUidList(final Collection<Long> brandUids) throws EpServiceException;

	/**
	 * Return a list of all brands in use.
	 *
	 * @return a list of all brands in use
	 */
	List<Brand> getBrandInUseList();

	/**
	 * Updates the given brand.
	 *
	 * @param brand the brand to update
	 * @return the persisted or updated brand
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	Brand update(final Brand brand) throws EpServiceException;

	/**
	 * Checks whether the given brand code exists or not.
	 *
	 * @param code the brand code.
	 * @return true if the given code exists.
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	boolean codeExists(final String code) throws EpServiceException;

	/**
	 * Check whether the given brand's code exists or not.
	 *
	 * @param brand the brand to check
	 * @return true if a different brand with the given brand's code exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean codeExists(final Brand brand) throws EpServiceException;

	/**
	 * Find the brand with the given code.
	 *
	 * @param code the brand code.
	 * @return the brand that matches the given code, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Brand findByCode(final String code) throws EpServiceException;

	/**
	 * Return a list of brand code and default locale display name of all brands.
	 *
	 * @return a list of brand code and default locale display name of all brands
	 */
	List<String[]> getBrandCodeNameList();
}
