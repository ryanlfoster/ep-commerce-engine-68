package com.elasticpath.persistence.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.advancedsearch.AdvancedQueryType;
import com.elasticpath.domain.advancedsearch.AdvancedSearchQuery;
import com.elasticpath.domain.advancedsearch.QueryVisibility;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.AdvancedSearchQueryDao;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.FetchPlanHelper;

/**
 * Provides <code>AdvancedSearchQuery</code> data access methods.
 */
public class AdvancedSearchQueryDaoImpl implements AdvancedSearchQueryDao {

	private PersistentBeanFinder persistentBeanFinder = new PersistentBeanFinder();

	private PersistenceEngine persistenceEngine;

	private BeanFactory beanFactory;

	private FetchPlanHelper fetchPlanHelper;
	
	/**
	 * Save or update the given query.
	 * 
	 * @param searchQuery the query to save or update
	 * @return the merged object if it is merged, or the persisted object for save action
	 * @throws EpServiceException - in case of any errors
	 */
	public AdvancedSearchQuery saveOrUpdate(final AdvancedSearchQuery searchQuery) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().saveOrMerge(searchQuery);
	}

	/**
	 * Get the query with the given UIDPK. Return null if no matching records exist.
	 * 
	 * @param queryUidPk the Query UIDPK.
	 * @return the advanced search query if UIDPK exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public AdvancedSearchQuery get(final long queryUidPk) {
		sanityCheck();

		AdvancedSearchQuery searchQuery = null;
		if (queryUidPk <= 0) {
			searchQuery = getBean(ContextIdNames.ADVANCED_SEARCH_QUERY);
		} else {
			prepareFetchPlanWithDetails();
			searchQuery = getPersistentBeanFinder().get(ContextIdNames.ADVANCED_SEARCH_QUERY, queryUidPk);
			fetchPlanHelper.clearFetchPlan();
		}

		return searchQuery;
	}

	/**
	 * Finds all visible queries (all public queries and all owner`s queries) with given query types.
	 * 
	 * @param owner the query owner
	 * @param queryTypes the query types
	 * @param withDetails is indicator which define what fetch plan must be used. If it is true then fetch plan with query details is used
	 *            {@link #prepareFetchPlanWithDetails()}. Otherwise, simple fetch plan is used {@link #prepareFetchPlan()}
	 * @return list of obtained queries
	 */
	public List<AdvancedSearchQuery> findAllVisibleQueriesWithTypes(final CmUser owner, final List<AdvancedQueryType> queryTypes,
			final boolean withDetails) {
		sanityCheck();

		if (withDetails) {
			prepareFetchPlanWithDetails();
		} else {
			prepareFetchPlan();
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("uidPk", owner.getUidPk());
		parameters.put("queryVisibility", QueryVisibility.PUBLIC);
		parameters.put("listQueryType", queryTypes);
		final List<AdvancedSearchQuery> retrieveByNamedQuery = getPersistenceEngine().retrieveByNamedQuery("ADVANCED_QUERY_FIND_VISIBLE_WITH_TYPES",
				parameters);
		fetchPlanHelper.clearFetchPlan();
		return retrieveByNamedQuery;
	}

	/**
	 * Finds all queries with given query types.
	 * 
	 * @param queryTypes the query types
	 * @param withDetails is indicator which define what fetch plan must be used. If it is true then fetch plan with query details is used
	 *            {@link #prepareFetchPlanWithDetails()}. Otherwise, simple fetch plan is used {@link #prepareFetchPlan()}
	 * @return list of obtained queries
	 */
	public List<AdvancedSearchQuery> findAllQueriesWithTypes(final List<AdvancedQueryType> queryTypes, final boolean withDetails) {
		sanityCheck();

		if (withDetails) {
			prepareFetchPlanWithDetails();
		} else {
			prepareFetchPlan();
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("list", queryTypes);

		final List<AdvancedSearchQuery> retrieveByNamedQuery = getPersistenceEngine().retrieveByNamedQuery("ADVANCED_QUERY_FIND_ALL_WITH_TYPES",
				parameters);
		fetchPlanHelper.clearFetchPlan();
		return retrieveByNamedQuery;
	}
	
	/**
	 * Finds queries with the given name.
	 * 
	 * @param queryName the query name
	 * @param withDetails is indicator which define what fetch plan must be used.
	 * @return list of queries matching the given name
	 */
	public List<AdvancedSearchQuery> findByName(final String queryName, final boolean withDetails) {
		sanityCheck();

		if (withDetails) {
			prepareFetchPlanWithDetails();
		} else {
			prepareFetchPlan();
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", queryName.toLowerCase(Locale.getDefault()));

		final List<AdvancedSearchQuery> retrieveByNamedQuery = getPersistenceEngine().retrieveByNamedQuery("ADVANCED_QUERY_FIND_BY_NAME",
				parameters);
		fetchPlanHelper.clearFetchPlan();
		return retrieveByNamedQuery;
	}

	private void prepareFetchPlan() {
		Class< ? extends Persistable> queryClass = beanFactory.getBeanImplClass(ContextIdNames.ADVANCED_SEARCH_QUERY);
		fetchPlanHelper.addField(queryClass, "name");
		fetchPlanHelper.addField(queryClass, "description");
		fetchPlanHelper.addField(queryClass, "queryVisibility");
		fetchPlanHelper.addField(queryClass, "queryType");
	}

	private void prepareFetchPlanWithDetails() {
		prepareFetchPlan();
		Class< ? extends Persistable> queryClass = beanFactory.getBeanImplClass(ContextIdNames.ADVANCED_SEARCH_QUERY);
		fetchPlanHelper.addField(queryClass, "queryContent");
		fetchPlanHelper.addField(queryClass, "owner");
	}

	/**
	 * Removes the given query form database.
	 * 
	 * @param query the query to remove
	 */
	public void remove(final AdvancedSearchQuery query) {
		sanityCheck();
		getPersistenceEngine().delete(query);
	}

	/**
	 * Convenience method for getting a bean instance.
	 * 
	 * @param <T> the type of bean to return
	 * @param beanName the name of the bean to get and instance of.
	 * @return an instance of the requested bean.
	 */
	public <T> T getBean(final String beanName) {
		return beanFactory.<T> getBean(beanName);
	}

	/**
	 * Gets the persistent bean finder.
	 * 
	 * @return the persistentBeanFinder
	 */
	public PersistentBeanFinder getPersistentBeanFinder() {
		return persistentBeanFinder;
	}

	/**
	 * Sets the persistent bean finder.
	 * 
	 * @param persistentBeanFinder the persistentBeanFinder to set
	 */
	public void setPersistentBeanFinder(final PersistentBeanFinder persistentBeanFinder) {
		this.persistentBeanFinder = persistentBeanFinder;
	}

	/**
	 * Sanity check of this service instance.
	 * 
	 * @throws EpServiceException - if something goes wrong.
	 */
	public void sanityCheck() throws EpServiceException {
		if (getPersistenceEngine() == null) {
			throw new EpServiceException("The persistence engine is not correctly initialized.");
		}
	}

	/**
	 * Gets the persistence engine.
	 * 
	 * @return The persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Sets the persistence engine to use.
	 * 
	 * @param persistenceEngine The persistence engine.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Sets the bean factory object.
	 * 
	 * @param beanFactory the bean factory instance.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Sets fetch plan helper.
	 * 
	 * @param fetchPlanHelper the fetchPlanHelper to set
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	/**
	 * Helper for finding persistent beans which determines the bean implementation class from a bean factory and then delegates the loading to a
	 * {@link PersistenceEngine}.
	 */
	public final class PersistentBeanFinder {

		/**
		 * Private constructor to prevent external instantiation.
		 */
		PersistentBeanFinder() {
			// Prevent external instantiation.
		}

		/**
		 * Get a persistent instance with the given id. Return null if no matching record exists.
		 * 
		 * @param <T> the type of the object
		 * @param beanName the name of the bean to find the implementation class for.
		 * @param uidPk the persistent instance id.
		 * @return the persistent instance
		 * @throws com.elasticpath.persistence.api.EpPersistenceException - in case of persistence errors
		 */
		public <T extends Persistable> T get(final String beanName, final long uidPk) {
			return getPersistenceEngine().get(beanFactory.<T> getBeanImplClass(beanName), uidPk);
		}

		/**
		 * Load a persistent instance with a given id. The persistent class to load will be determined from the beanName Throw an unrecoverable
		 * exception if there is no matching database row.
		 * 
		 * @param <T> the type of the object
		 * @param beanName the name of the bean to find the implementation class for.
		 * @param uidPk the persistent instance id.
		 * @return the persistent instance
		 * @throws com.elasticpath.persistence.api.EpPersistenceException - in case of persistence errors
		 */
		public <T extends Persistable> T load(final String beanName, final long uidPk) {
			return getPersistenceEngine().load(beanFactory.<T> getBeanImplClass(beanName), uidPk);
		}

		/**
		 * Load a persistent instance with the given id. Throw an unrecoverable exception if there is no matching database row. This method will
		 * create a new session (EntityManager) to execute the query, and close the new session when completed.
		 * 
		 * @param <T> the type of the object
		 * @param beanName the name of the bean to find the implementation class for.
		 * @param uidPk the persistent instance id.
		 * @return the persistent instance
		 * @throws com.elasticpath.persistence.api.EpPersistenceException in case of persistence errors
		 */
		public <T extends Persistable> T loadWithNewSession(final String beanName, final long uidPk) {
			return getPersistenceEngine().loadWithNewSession(beanFactory.<T> getBeanImplClass(beanName), uidPk);
		}
	}

}
