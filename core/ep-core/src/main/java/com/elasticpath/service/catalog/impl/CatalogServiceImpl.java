/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.catalog.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.ListUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.misc.SupportedLocale;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchPlanHelper;

/**
 * Implementation of <code>CatalogService</code> - methods for servicing the <code>Catalog</code> domain object.
 */
public class CatalogServiceImpl extends AbstractEpPersistenceServiceImpl implements CatalogService {

	private FetchPlanHelper fetchPlanHelper;

	/**
	 * Generic method for getting a catalog.
	 * 
	 * @param uid the persisted catalog UID
	 * @return the persisted instance of a <code>Catalog</code> if it exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return getCatalog(uid);
	}

	/**
	 * Saves or updates a given <code>Catalog</code>.
	 * 
	 * @param catalog the <code>Catalog</code> to save or update.
	 * @return the updated instance
	 * @throws EpServiceException in case of any errors.
	 */
	public Catalog saveOrUpdate(final Catalog catalog) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().saveOrUpdate(catalog);
	}
	
	/**
	 * Deletes the catalog.
	 *
	 * @param catalog the catalog to remove
	 * @throws EpServiceException in case of any errors
	 */
	public void remove(final Catalog catalog) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(catalog);
	}

	/**
	 * Gets a <code>Catalog</code> with the given UID. Returns null if no matching records exist.
	 * 
	 * @param catalogUid the <code>Catalog</code> UID
	 * @return a <code>Catalog</code> with the given UID.
	 * @throws EpServiceException in case of any errors.
	 */
	public Catalog getCatalog(final long catalogUid) throws EpServiceException {
		sanityCheck();
		Catalog catalog = null;
		if (catalogUid <= 0) {
			catalog = getBean(ContextIdNames.CATALOG);
		} else {
			catalog = getPersistentBeanFinder().get(ContextIdNames.CATALOG, catalogUid);
		}
		return catalog;
	}

	/**
	 * Gets a list of all the <code>Catalog</code> UIDs.
	 * 
	 * @return a list of all <code>Catalog</code> UIDs.
	 * @throws EpServiceException in case of any errors.
	 */
	public List<Long> findAllCatalogUids() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_CATALOG_UIDS");
	}

	/**
	 * Gets a list of all the Catalogs.
	 * 
	 * @return a list of all Catalogs.
	 * @throws EpServiceException in case of any errors.
	 */
	public List<Catalog> findAllCatalogs() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_CATALOGS");
	}

	/**
	 * Gets a list of all the master <code>Catalog</code> UIDs.
	 * 
	 * @return a list of all master Catalogs.
	 * @throws EpServiceException in case of any errors.
	 */
	public List<Long> findMasterCatalogUids() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_MASTER_CATALOG_UIDS");
	}
	
	/**
	 * Find the catalog with the given name.
	 *
	 * @param name the catalog name
	 * @return the Catalog with the given name if it exists, null otherwise
	 * @throws EpServiceException - in case of any errors
	 */
	public Catalog findByName(final String name) throws EpServiceException {
		sanityCheck();
		if (name == null) {
			throw new EpServiceException("Cannot retrieve Catalog with null name.");
		}

		final List<Catalog> results = getPersistenceEngine().retrieveByNamedQuery("FIND_CATALOG_BY_NAME", name);
		Catalog catalog = null;
		if (results.size() == 1) {
			catalog = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate catalog name exist -- " + name);
		}
		return catalog;
	}
	
	/**
	 * Find the catalog with the given code.
	 *
	 * @param code the catalog code
	 * @return the Catalog with the given code if it exists, null otherwise
	 * @throws EpServiceException - in case of any errors
	 */
	public Catalog findByCode(final String code) throws EpServiceException {
		return this.findByGuid(code, null);
	}
	
	/**
	 * Check whether or not the given catalog name already exists.
	 *
	 * @param catalogName the shippingRegion to check
	 * @return true if a different shipping region with the same name exists
	 * @throws EpServiceException in case of any errors
	 */
	public boolean nameExists(final String catalogName) throws EpServiceException {
		if (catalogName == null) {
			return false;
		}
		final Catalog existingCatalog = this.findByName(catalogName);
		boolean catalogExists = false;
		if (existingCatalog != null) {
			catalogExists = true;
		}
		return catalogExists;
	}
	
	/**
	 * Checks whether or not the given catalog code already exists.
	 *
	 * @param code the catalog code to check
	 * @return whether or not the given catalog code already exists
	 * @throws EpServiceException in case of any errors
	 */
	public boolean codeExists(final String code) throws EpServiceException {
		sanityCheck();
		return !getPersistenceEngine().retrieveByNamedQuery("FIND_CATALOG_UID_BY_CODE", code).isEmpty();
	}
	
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
	public Catalog load(final long catalogUid, final FetchGroupLoadTuner fetchGroupLoadTuner, final boolean cleanExistingGroups) 
		throws EpServiceException {
		sanityCheck();
		Catalog catalog = null;
		if (catalogUid <= 0) {
			return getBean(ContextIdNames.CATALOG);
		}

		fetchPlanHelper.configureFetchGroupLoadTuner(fetchGroupLoadTuner, cleanExistingGroups);
		catalog = getPersistentBeanFinder().load(ContextIdNames.CATALOG, catalogUid);
		fetchPlanHelper.clearFetchPlan();
		return catalog;
	}
	
	/**
	 * Sets the fetch plan helper.
	 * 
	 * @param fetchPlanHelper the fetch plan helper
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}
	
	/**
	 * Checks if the Catalog indicated by the given catalog uid is in use. 
	 * 
	 * @param catalogUid the catalog uidpk
	 * @return true if the Catalog is in use; false otherwise
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean catalogInUse(final long catalogUid) throws EpServiceException {
		sanityCheck();
		Object[] queryParams = new Object[] { catalogUid };
		
		boolean result = !getPersistenceEngine().retrieveByNamedQuery("CATALOG_IN_USE_BY_PRODUCT_TYPE", queryParams).isEmpty();
		
		if (result) {
			return true;
		}
		result |= !getPersistenceEngine().retrieveByNamedQuery("CATALOG_IN_USE_BY_CATEGORY_TYPE", queryParams).isEmpty();
		
		if (result) {
			return true;
		}

		result |= !getPersistenceEngine().retrieveByNamedQuery("CATALOG_IN_USE_BY_BRAND", queryParams).isEmpty();
		
		if (result) {
			return true;
		}

		result |= !getPersistenceEngine().retrieveByNamedQuery("CATALOG_IN_USE_BY_CMUSER", queryParams).isEmpty();
		
		if (result) {
			return true;
		}

		result |= !getPersistenceEngine().retrieveByNamedQuery("CATALOG_IN_USE_BY_ATTRIBUTE", queryParams).isEmpty();
		
		return result;
	}

	/**
	 * Gets all the master catalogs.
	 * 
	 * @return the master catalogs list
	 * @throws EpServiceException on errors
	 */
	public List<Catalog> findMasterCatalogs() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_MASTER_CATALOGS");
	}

	@Override
	public Collection<Locale> findAllCatalogLocales() {
		sanityCheck();
		Set<Locale> locales = new HashSet<Locale>();
		List<SupportedLocale> catalogLocales = getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_CATALOG_LOCALES");
		for (SupportedLocale catalogLocale : catalogLocales) {
			locales.add(catalogLocale.getLocale());
		}
		return locales;
	}

	/**
	 * Looks for a catalog by it code.
	 * 
	 * @param code the code
	 * @param loadTuner the load tuner or null if none
	 * @return the catalog instance or null if not found
	 */
	public Catalog findByGuid(final String code, final LoadTuner loadTuner) {
		sanityCheck();
		if (code == null) {
			throw new EpServiceException("Cannot retrieve Catalog with null code.");
		}

		if (loadTuner != null) {
			fetchPlanHelper.configureLoadTuner(loadTuner);
		}

		final List<Catalog> results = getPersistenceEngine().retrieveByNamedQuery("FIND_CATALOG_BY_CODE", code);
		
		fetchPlanHelper.clearFetchPlan();
		
		Catalog catalog = null;
		if (results.size() == 1) {
			catalog = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate catalog code exist -- " + code);
		}
		return catalog;
	}

	@Override
	public List<Catalog> listAllCatalogsWithCodes(final List<String> codes) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQueryWithList("LIST_CATALOGS_BY_GUIDS", "codes", codes);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Catalog> findMastersUsedByVirtualCatalog(final String catalogCode) {
		// Firstly, find masters associated with linked categories
		List<Catalog> linkedMasters = getPersistenceEngine().retrieveByNamedQuery("FIND_MASTER_CATALOGS_USING_LINKED_CATEGORIES", catalogCode);

		// Now find masters associated with virtual categories
		List<Catalog> virtualMasters = getPersistenceEngine().retrieveByNamedQuery("FIND_MASTER_CATALOGS_USING_VIRTUAL_CATEGORIES", catalogCode);
		
		return ListUtils.union(linkedMasters, virtualMasters);
	}

}
