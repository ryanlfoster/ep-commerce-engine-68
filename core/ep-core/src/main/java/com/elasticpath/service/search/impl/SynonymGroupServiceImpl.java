package com.elasticpath.service.search.impl;

import java.util.Collection;
import java.util.Locale;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.search.SynonymGroupService;

/**
 * Default implementation of {@link SynonymGroupService}.
 */
public class SynonymGroupServiceImpl extends AbstractEpPersistenceServiceImpl implements SynonymGroupService {

	/**
	 * Saves or updates a given {@link SynonymGroup}.
	 * 
	 * @param synonymGroup the {@link SynonymGroup} to save or update
	 * @return SynonymGroup the updated SynonymGroup
	 * @throws EpServiceException in case of any errors
	 */
	public SynonymGroup saveOrUpdate(final SynonymGroup synonymGroup) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().saveOrUpdate(synonymGroup);
	}

	/**
	 * Deletes a {@link SynonymGroup}.
	 * 
	 * @param synonymGroup the {@link SynonymGroup} to remove
	 * @throws EpServiceException in case of any errors
	 */
	public void remove(final SynonymGroup synonymGroup) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(synonymGroup);
	}

	/**
	 * Gets a {@link SynonymGroup} with the given UID. Return null if no matching records exist.
	 * 
	 * @param synonymGroupUid the {@link SynonymGroup} UID
	 * @return the {@link SynonymGroup} if the UID exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	public SynonymGroup getSynonymGroup(final long synonymGroupUid) throws EpServiceException {
		sanityCheck();
		if (synonymGroupUid <= 0) {
			return getBean(ContextIdNames.SYNONYM_GROUP);
		}
		return getPersistentBeanFinder().get(ContextIdNames.SYNONYM_GROUP, synonymGroupUid);
	}
	
	/**
	 * Gets a collection of {@link SynonymGroup}s for a given catalog UID.
	 *
	 * @param catalogUid the catalog UID
	 * @return a collection of {@link SynonymGroup}
	 * @throws EpServiceException in case of any errors
	 */
	public Collection<SynonymGroup> findAllSynonymGroupForCatalog(final long catalogUid) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("SYNONYM_GROUP_FIND_BY_CATALOG", catalogUid);
	}
	
	/**
	 * Returns whether the given concept term exists within the given catalog/locale.
	 *
	 * @param conceptTerm the concept term to check
	 * @param catalog the catalog to check in
	 * @param locale the language to check in
	 * @return whether the given concept term exists within the given catalog/locale
	 */
	public boolean conceptTermExists(final String conceptTerm, final Catalog catalog, final Locale locale) {
		sanityCheck();
		// can't seem to query on the externalized field locale
		final Collection<SynonymGroup> result = getPersistenceEngine().retrieveByNamedQuery("SYNONYM_GROUP_FIND_BY_CONCEPTTERM_CATALOG",
				catalog.getUidPk(),
				conceptTerm);
		for (SynonymGroup synonymGroup : result) {
			if (synonymGroup.getLocale().equals(locale)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Generic get method for a {@link SynonymGroup}.
	 * 
	 * @param uid the persisted synonym group UID
	 * @return the persisted instance of a {@link SynonymGroup} if it exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return getSynonymGroup(uid);
	}
}
