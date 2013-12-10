package com.elasticpath.service.search.solr;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.index.QueryComposerFactory;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Default implementation of {@link QueryComposerFactory}.
 */
public class QueryComposerFactoryImpl implements QueryComposerFactory {
	
	/**
	 * Mappings from the general search criteria class to the specific query composer for Lucene.
	 */
	private Map<Class< ? extends SearchCriteria>, QueryComposer> composerMappings;

	/**
	 * Retrieves the {@link QueryComposer} that should be used for the given
	 * {@link SearchCriteria}. The search criteria is <i>not</i> modified.
	 * 
	 * @param searchCriteria the search criteria
	 * @return a {@link QueryComposer} for the given {@link SearchCriteria}
	 */
	public QueryComposer getComposerForCriteria(final SearchCriteria searchCriteria) {
		if (composerMappings == null) {
			composerMappings = new HashMap<Class< ? extends SearchCriteria>, QueryComposer>();
		}
		QueryComposer composer = composerMappings.get(searchCriteria.getClass());
		if (composer == null) {
			throw new EpSystemException("Unable to find composer mapping for class " + searchCriteria);
		}
		return composer;
	}

	/**
	 * Sets the mapping of {@link SearchCriteria} classes to instances of {@link QueryComposer}.
	 * 
	 * @param queryComposers the mapping of {@link SearchCriteria} classes to
	 *            {@link QueryComposer} instances.
	 */
	public void setQueryComposerMappings(final Map<Class< ? extends SearchCriteria>, QueryComposer> queryComposers) {
		composerMappings = queryComposers;
	}
}
