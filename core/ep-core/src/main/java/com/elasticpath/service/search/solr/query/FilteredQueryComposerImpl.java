package com.elasticpath.service.search.solr.query;

import java.util.Collection;
import java.util.Map.Entry;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.index.QueryComposerFactory;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.FilteredSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * A query composer for filtered search criteria's. Makes a best attempt at constructing a query
 * for a filtered search criteria's inner criteria, but results might not be exact.
 */
public class FilteredQueryComposerImpl extends AbstractQueryComposerImpl implements QueryComposer {
	
	private QueryComposerFactory queryComposerFactory;
	
	@Override
	protected boolean isValidSearchCriteria(final SearchCriteria searchCriteria) {
		return searchCriteria instanceof FilteredSearchCriteria;
	}

	@Override
	protected Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		return doComposeQueryInternal(searchCriteria, searchConfig, true);
	}

	@Override
	protected Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		return doComposeQueryInternal(searchCriteria, searchConfig, false);
	}
	
	private Query doComposeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig, final boolean fuzzy) {
		final FilteredSearchCriteria< ? extends SearchCriteria> filteredSearchCriteria = (FilteredSearchCriteria< ? >) searchCriteria;
		
		if (filteredSearchCriteria.isEmpty()) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}
		
		final QueryComposer composer = queryComposerFactory.getComposerForCriteria(getInnerCriteria(filteredSearchCriteria));
		final BooleanQuery booleanQuery = new BooleanQuery();
		Occur occur = null;
		switch (filteredSearchCriteria.getRelationship()) {
		case AND:
			occur = Occur.MUST;
			break;
		case OR:
			occur = Occur.SHOULD;
			break;
		default:
			// should never get here
			throw new UnsupportedOperationException("Not implemented.");
		}
		
		for (Entry< ? extends SearchCriteria, ? extends Collection< ? extends SearchCriteria>> entry : filteredSearchCriteria) {
			final BooleanQuery conditionQuery = new BooleanQuery();
			Query searchQuery, filterQuery;
			
			// first add the initial search to the query
			if (fuzzy) {
				searchQuery = composer.composeFuzzyQuery(entry.getKey(), searchConfig);
			} else {
				searchQuery = composer.composeQuery(entry.getKey(), searchConfig);
			}
			// they must be ANDed as ORed filters make no sense
			conditionQuery.add(searchQuery, Occur.MUST);
			
			// now add the filters to that same query
			for (SearchCriteria filter : entry.getValue()) {
				if (fuzzy) {
					filterQuery = composer.composeFuzzyQuery(filter, searchConfig);
				} else {
					filterQuery = composer.composeQuery(filter, searchConfig);
				}
				// they must be ANDed as ORed filters make no sense
				conditionQuery.add(filterQuery, Occur.MUST_NOT);
			}
			
			booleanQuery.add(conditionQuery, occur);
		}
		
		return booleanQuery;
	}
	
	private SearchCriteria getInnerCriteria(final FilteredSearchCriteria< ? extends SearchCriteria> filteredSearchCriteria) {
		if (!filteredSearchCriteria.isEmpty()) {
			// all criteria's are defined to be the same by rules of filtered search criteria
			return filteredSearchCriteria.getCriteria(0).getKey();
		}
		return null;
	}
	
	/**
	 * Sets the {@link QueryComposerFactory} instance to use.
	 *
	 * @param queryComposerFactory the {@link QueryComposerFactory} instance to use
	 */
	public void setQueryComposerFactory(final QueryComposerFactory queryComposerFactory) {
		this.queryComposerFactory = queryComposerFactory;
	}
}
