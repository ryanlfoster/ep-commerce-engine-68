package com.elasticpath.persistence.openjpa.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.openjpa.persistence.FetchPlan;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.Query;

/**
 * The JPA implementation of a query in ElasticPath. It is a wrap of <code>javax.persistence.Query</code>.
 */
public class JpaQueryImpl implements Query {

	private final OpenJPAQuery query;

	@Override
	public void setParameter(final int position, final Object val) throws EpPersistenceException {
		this.query.setParameter(position, val);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> list() throws EpPersistenceException {
		return this.query.getResultList();
	}

	@Override
	public void setFetchSize(final int fetchSize) {
	  query.getFetchPlan().setFetchBatchSize(fetchSize);
	}
	
	@Override
	public void setFirstResult(final int startPosistion) {
		this.query.setFirstResult(startPosistion);
	}

	@Override
	public void setMaxResults(final int maxResults) {
		this.query.setMaxResults(maxResults);
	}

	/**
	 * The default constructor.
	 *
	 * @param query the JPA query
	 */
	public JpaQueryImpl(final javax.persistence.Query query) {
		super();
		this.query = OpenJPAPersistence.cast(query);
	}

	/**
	 * Add a fetch group to use for this query.
	 *
	 * @param group the name of a fetch group to add
	 */
	public void addFetchGroup(final String group) {
		  query.getFetchPlan().addFetchGroup(group);
	}

	/**
	 * Clears the set of fetch group names to use when loading data.
	 */
	public void clearFetchGroups() {
		query.getFetchPlan().clearFetchGroups();
	}

	/**
	 * Remove the given fetch group.
	 *
	 * @param group the group to remove
	 */
	public void removeFetchGroup(final String group) {
		query.getFetchPlan().removeFetchGroup(group);
	}

	@Override
	public void setFetchGroups(final Set<String> groups) {
		FetchPlan fetchPlan = query.getFetchPlan();
		fetchPlan.clearFetchGroups();

		if (groups != null) {
			for (String fetchGroup : groups) {
				fetchPlan.addFetchGroup(fetchGroup);
			}
		}
	}

	@Override
	public void setFetchGroupFields(final Collection<String> fields) {
		FetchPlan fetchPlan = query.getFetchPlan();
		fetchPlan.clearFields();
		fetchPlan.addFields(fields);
	}
	
	@Override
	public int executeUpdate() {
		return query.executeUpdate();
	}

}
