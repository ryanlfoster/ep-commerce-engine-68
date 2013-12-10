/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.changeset.ChangeSetUserView;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.changeset.ChangeSetSearchCriteria;
import com.elasticpath.service.changeset.dao.ChangeSetDao;
import com.elasticpath.service.changeset.dao.ChangeSetUserFinder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * This DAO deals with ChangeSet objects and all the CRUD operations related to them.
 */
public class ChangeSetDaoImpl implements ChangeSetDao {

	private PersistenceEngine persistenceEngine;
	private ChangeSetUserFinder changeSetUserFinder;

	@Override
	public ChangeSet add(final ChangeSet changeSet) {
		return persistenceEngine.saveOrUpdate(changeSet);
	}

	@Override
	public Collection<ChangeSet> findAllChangeSets() {
		return persistenceEngine.retrieveByNamedQuery("SELECT_ALL_CHANGESETS");
	}

	@Override
	public ChangeSet findByGuid(final String guid) {
		List<ChangeSet> result = persistenceEngine.retrieveByNamedQuery("FIND_CHANGESET_BY_GUID", guid);
		if (CollectionUtils.isNotEmpty(result)) {
			return result.iterator().next();
		}
		return null;
	}

	@Override
	public Collection<ChangeSet> findAllChangeSetsByUserGuid(final String userGuid) {
		return persistenceEngine.retrieveByNamedQuery("SELECT_ALL_CHANGESETS_BY_USERGUID", userGuid);
	}

	@Override
	public void remove(final String changeSetGuid) {
		// we need to load the change set so that
		// the persistence engine deletes it.
		// using JPQL does not work here as
		// it does not obey the foreign key cascades
		ChangeSet changeSet = findByGuid(changeSetGuid);
		persistenceEngine.delete(changeSet);
	}

	@Override
	public ChangeSet update(final ChangeSet changeSet) {
		return persistenceEngine.update(changeSet);
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Set the instance of change set user finder.
	 *
	 * @param changeSetUserFinder the instance of change set user finder
	 */
	public void setChangeSetUserFinder(final ChangeSetUserFinder changeSetUserFinder) {
		this.changeSetUserFinder = changeSetUserFinder;
	}

	@Override
	public Collection<ChangeSet> findByObjectDescriptor(final BusinessObjectDescriptor objectDescriptor,
			final Collection<ChangeSetStateCode> stateCodes) {

		List<String> result = persistenceEngine.retrieveByNamedQuery("FIND_GROUP_IDS_BY_OBJ_TYPE_AND_ID",
				objectDescriptor.getObjectType(),
				objectDescriptor.getObjectIdentifier());

		Collection<String> availableChangeSetGuids = this.findAvailableChangeSets(result, stateCodes);
		return findByGuid(availableChangeSetGuids);
	}

	/**
	 *
	 * @param changeSetGuids
	 * @return
	 */
	private Collection<ChangeSet> findByGuid(final Collection<String> changeSetGuids) {
		return persistenceEngine.retrieveByNamedQuery("FIND_CHANGESETS_BY_GUIDS", changeSetGuids);
	}

	@Override
	public Collection<ChangeSetUserView> getAvailableUsers(final String... permissions) {
		if (permissions.length == 0) {
			throw new IllegalArgumentException("At least one permission is required");
		}
		List<ChangeSetUserView> users = persistenceEngine.retrieveByNamedQueryWithList("FIND_AVAILABLE_CHANGESETUSERS", "list",
				Arrays.asList(permissions));

		List<ChangeSetUserView> superUsers = persistenceEngine.retrieveByNamedQuery("FIND_AVAILABLE_SUPERUSERS", "SUPERUSER");

		HashSet<ChangeSetUserView> allUsers = new HashSet<ChangeSetUserView>(users);
		allUsers.addAll(superUsers);

		return allUsers;
	}

	@Override
	public Collection<ChangeSetUserView> getChangeSetUserViews(final Collection<String> changeSetUserGuids) {
		return persistenceEngine.retrieveByNamedQuery("FIND_CHANGESETUSERVIEWS_BY_GUIDS", changeSetUserGuids);
	}

	@Override
	public Collection<ChangeSet> findByCriteria(final ChangeSetSearchCriteria criteria) {
		List<Object> parameterList = new ArrayList<Object>();
		String query = buildQueryString(criteria, parameterList, false);
		if (query == null) {
			return Collections.emptyList();
		}

		List<ChangeSet> changeSets = persistenceEngine.retrieve(query, parameterList.toArray());

		return changeSets;

	}

	@Override
	public List<ChangeSet> findByCriteria(final ChangeSetSearchCriteria criteria,
			final int start, final int maxResults) {
		List<Object> parameterList = new ArrayList<Object>();
		String query = buildQueryString(criteria, parameterList, false);
		if (query == null) {
			return Collections.emptyList();
		}

		List<ChangeSet> changeSets = persistenceEngine.retrieve(query, parameterList.toArray(), start, maxResults);

		return changeSets;
	}

	/**
	 * Build a query string from the given criteria.
	 *
	 * @param criteria the criteria to build a query for
	 * @param parameterList a list which will receive the query parameters
	 * @param countOnly if just the count of results is required
	 * @return the query string
	 */
	protected String buildQueryString(final ChangeSetSearchCriteria criteria, final List<Object> parameterList, final boolean countOnly) {
		if (criteria.getAssignedUserName() != null) {
			String userGuid = changeSetUserFinder.findUserGuidByUserName(criteria.getAssignedUserName());
			if (userGuid == null) {
				// no user found with that user name. return no results
				return null;
			}
			criteria.setUserGuid(userGuid);
		}

		StringBuilder queryBuffer = new StringBuilder("SELECT ");
		if (countOnly) {
			queryBuffer.append("count(cs.uidPk) ");
		} else {
			queryBuffer.append("cs ");
		}
		queryBuffer.append("FROM ChangeSetImpl cs");

		if (criteria.getUserGuid() != null) {
			queryBuffer.append(", IN(cs.assignedUsers) assignedUser");
		}

		StringBuilder whereClause = new StringBuilder();
		addParameter(whereClause, criteria.getUserGuid(), "assignedUser.userGuid", parameterList, false);
		if (criteria.getChangeSetStateCode() != null) {
			addParameter(whereClause, criteria.getChangeSetStateCode().getName(), "cs.stateCodeName", parameterList, false);
		}

		if (criteria.isStrictName()) {
			addParameter(whereClause, criteria.getChangeSetName(), "cs.name", parameterList, false);
		} else {
			addLikeParameter(whereClause, criteria.getChangeSetName(), "cs.name", parameterList, false);
		}

		if (!parameterList.isEmpty()) {
			queryBuffer.append(" WHERE ").append(whereClause);
		}

		if (!countOnly && !StandardSortBy.RELEVANCE.equals(criteria.getSortingType())) {
			queryBuffer.append(" ORDER BY cs.");
			queryBuffer.append(criteria.getSortingType().toString());
			queryBuffer.append(" ");
			queryBuffer.append(criteria.getSortingOrder().toString());
		}

		return queryBuffer.toString();
	}

	/**
	 * Generate the JPQL based on the parameters.
	 * @param whereClause the where clause of JPQL
	 * @param object the search criteria
	 * @param paramName parameter name in JPQL
	 * @param parameterList parameter list which contains the values of all parameters
	 * @param beginWhereClause the boolean value whether the "where" already existed in JPQL
	 */
	protected void addParameter(final StringBuilder whereClause, final Object object, final String paramName,
			final List<Object> parameterList, final boolean beginWhereClause) {
		if (object == null) {
			return;
		}
		if (!parameterList.isEmpty() || beginWhereClause) {
			whereClause.append(" AND ");
		}

		parameterList.add(object);

		int index = parameterList.size();
		whereClause.append(paramName).append(" = ?").append(index);
	}

	/**
	 * Generate the JPQL based on the parameters.
	 * @param whereClause the where clause of JPQL
	 * @param object the search criteria
	 * @param paramName parameter name in JPQL
	 * @param parameterList parameter list which contains the values of all parameters
	 * @param beginWhereClause the boolean value whether the "where" already existed in JPQL
	 */
	protected void addLikeParameter(final StringBuilder whereClause, final Object object, final String paramName,
			final List<Object> parameterList, final boolean beginWhereClause) {
		if (object == null) {
			return;
		}
		if (!parameterList.isEmpty() || beginWhereClause) {
			whereClause.append(" AND ");
		}

		parameterList.add("%" + object + "%");

		int index = parameterList.size();
		whereClause.append(paramName).append(" LIKE ?").append(index);
	}

	/**
	 * Filters change set GUIDs that exist and belong to change sets which are in the provided states.
	 *
	 * @param groupIds the group IDs to filter
	 * @param stateCodes the state codes the change sets should have
	 * @return a filtered collection of IDs which holds change sets of the specified state codes
	 */
	public Collection<String> findAvailableChangeSets(final Collection<String> groupIds, final Collection<ChangeSetStateCode> stateCodes) {
		Collection<String> codes = new LinkedList<String>();
		for (ChangeSetStateCode stateCode : stateCodes) {
			codes.add(stateCode.getName());
		}

		return persistenceEngine.retrieveByNamedQuery("FIND_CHANGESET_GUID_BY_LIST_OF_GUIDS_AND_STATES", codes, groupIds);
	}

	/**
	 * Get the persistence engine.
	 *
	 * @return the persistenceEngine
	 */
	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Get the user finder.
	 *
	 * @return the changeSetUserFinder
	 */
	protected ChangeSetUserFinder getChangeSetUserFinder() {
		return changeSetUserFinder;
	}

	@Override
	public Collection<String> findGuidByObjectDescriptor(final BusinessObjectDescriptor objectDescriptor,
			final Collection<ChangeSetStateCode> stateCodes) {
		List<String> result = persistenceEngine.retrieveByNamedQuery("FIND_GROUP_IDS_BY_OBJ_TYPE_AND_ID",
				objectDescriptor.getObjectType(),
				objectDescriptor.getObjectIdentifier());

		return this.findAvailableChangeSets(result, stateCodes);
	}

	@Override
	public long getCountByCriteria(final ChangeSetSearchCriteria searchCriteria) {
		List<Object> parameterList = new ArrayList<Object>();
		String query = buildQueryString(searchCriteria, parameterList, true);
		if (query == null) {
			return 0;
		}

		List<Long> results = persistenceEngine.retrieve(query, parameterList.toArray());
		return results.get(0);
	}

}
