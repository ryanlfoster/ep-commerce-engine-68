package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Methods for servicing the <code>Catalog</code> domain object.
 */
public interface CatalogService extends EpPersistenceService {

	/**
	 * Saves or updates a given <code>Catalog</code>.
	 * 
	 * @param catalog the <code>Catalog</code> to save or update.
	 * @return the updated object instance
	 * @throws EpServiceException in case of any errors.
	 */
	Catalog saveOrUpdate(final Catalog catalog) throws EpServiceException;

	/**
	 * Deletes the catalog.
	 *
	 * @param catalog the catalog to remove
	 * @throws EpServiceException in case of any errors
	 */
	void remove(final Catalog catalog) throws EpServiceException;
	
	/**
	 * Gets a <code>Catalog</code> with the given UID. Returns null if no matching records exist.
	 * 
	 * @param catalogUid the <code>Catalog</code> UID
	 * @return a <code>Catalog</code> with the given UID.
	 * @throws EpServiceException in case of any errors.
	 */
	Catalog getCatalog(final long catalogUid) throws EpServiceException;

	/**
	 * Gets a list of all the <code>Catalog</code> UIDs.
	 * 
	 * @return a list of all <code>Catalog</code> UIDs.
	 * @throws EpServiceException in case of any errors.
	 */
	List<Long> findAllCatalogUids() throws EpServiceException;

	/**
	 * Gets a list of all the Catalogs.
	 * 
	 * @return a list of all Catalogs.
	 * @throws EpServiceException in case of any errors.
	 */
	List<Catalog> findAllCatalogs() throws EpServiceException;

	/**
	 * Gets a list of all the master <code>Catalog</code> UIDs.
	 * 
	 * @return a list of all master Catalogs.
	 * @throws EpServiceException in case of any errors.
	 */
	List<Long> findMasterCatalogUids() throws EpServiceException;
	
	/**
	 * Find the catalog with the given name.
	 *
	 * @param name the catalog name
	 * @return the Catalog with the given name if it exists, null otherwise
	 * @throws EpServiceException - in case of any errors
	 */
	Catalog findByName(final String name) throws EpServiceException;
	
	/**
	 * Find the catalog with the given code.
	 *
	 * @param code the catalog code
	 * @return the Catalog with the given code if it exists, null otherwise
	 * @throws EpServiceException - in case of any errors
	 */
	Catalog findByCode(final String code) throws EpServiceException;
	
	/**
	 * Check whether or not the given catalog name already exists.
	 *
	 * @param catalogName the the catalog name to check
	 * @return true if a different shipping region with the same name exists
	 * @throws EpServiceException in case of any errors
	 */
	boolean nameExists(final String catalogName) throws EpServiceException;
	
	/**
	 * Checks whether or not the given catalog code already exists.
	 *
	 * @param code the catalog code to check
	 * @return whether or not the given catalog code already exists
	 * @throws EpServiceException in case of any errors
	 */
	boolean codeExists(final String code) throws EpServiceException;
	
	/**
	 * Loads the catalog with the given UID.
	 * 
	 * @param catalogUid the catalog UID
	 * @param fetchGroupLoadTuner the load tuner to use, or <code>null</code> to not use a load tuner
	 * @param cleanExistingGroups indicate whether to clean the existing groups. Set to true for cleaning up, 
	 * otherwise keep the active fetch groups.
	 * @return the catalog if it exists, otherwise <code>null</code>
	 * @throws EpServiceException in case of any errors
	 */
	Catalog load(final long catalogUid, final FetchGroupLoadTuner fetchGroupLoadTuner, final boolean cleanExistingGroups) 
			throws EpServiceException;
	
	/**
	 * Checks if the Catalog indicated by the given catalog uid is in use. 
	 * 
	 * @param catalogUid the catalog uidpk
	 * @return true if the Catalog is in use; false otherwise
	 * @throws EpServiceException - in case of any errors
	 */
	boolean catalogInUse(final long catalogUid) throws EpServiceException;

	/**
	 * Gets a list of all master Catalogs.
	 * 
	 * @return a list of all master Catalogs.
	 * @throws EpServiceException in case of any errors.
	 */
	List<Catalog> findMasterCatalogs() throws EpServiceException;

	/**
	 * Finds all master catalog locales.
	 * @return a collection of all catalog locales
	 */
	Collection<Locale> findAllCatalogLocales();

	/**
	 * Looks for a catalog by it code and only loads the fields specified by the load tuner.
	 * 
	 * @param code the code
	 * @param loadTuner the load tuner or null if none
	 * @return the catalog instance or null if not found
	 */
	Catalog findByGuid(String code, LoadTuner loadTuner);

	/**
	 * Lists all catalogs whose codes are in the list.
	 * 
	 * @param codes catalog codes
	 * @return list of catalogs whose codes are in the list
	 */
	List<Catalog> listAllCatalogsWithCodes(List<String> codes);

	/**
	 * Find the master catalogs that are associated with the virtual catalog
	 * with the given code.
	 * @since 6.2.2
	 * 
	 * @param catalogCode the code of a virtual catalog
	 * @return a list of associated master catalogs
	 */
	List<Catalog> findMastersUsedByVirtualCatalog(final String catalogCode);

}