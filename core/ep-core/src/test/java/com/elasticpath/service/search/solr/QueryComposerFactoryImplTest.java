package com.elasticpath.service.search.solr;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SearchHint;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;

/**
 * Test case for {@link QueryComposerFactoryImpl}.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class QueryComposerFactoryImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private QueryComposerFactoryImpl queryComposerFactory;

	@Before
	public void setUp() throws Exception {
		queryComposerFactory = new QueryComposerFactoryImpl();
	}
	
	/**
	 * Test method for {@link QueryComposerFactoryImpl#getComposerForCriteria(SearchCriteria)}.
	 */
	@Test
	public void testGetComposerForCriteria() {
		final SearchCriteria mockSearchCriteria = context.mock(SearchCriteria.class, "SearchCriteria1");
		final SearchCriteria searchCriteria = mockSearchCriteria;
		
		final QueryComposer mockQueryComposer = context.mock(QueryComposer.class, "Composer1");
		final QueryComposer queryComposer = mockQueryComposer;
		
		final Map<Class< ? extends SearchCriteria>, QueryComposer> map = new HashMap<Class< ? extends SearchCriteria>, QueryComposer>();
		map.put(searchCriteria.getClass(), queryComposer);
		
		queryComposerFactory.setQueryComposerMappings(map);
		assertSame(queryComposer, queryComposerFactory.getComposerForCriteria(searchCriteria));
		try {
			queryComposerFactory.getComposerForCriteria(constructNewSearchCriteria());
			fail("Expected EpSystemException");
		} catch (EpSystemException e) { //NOPMD -- AvoidEmptyCatchBlocks
			// success
		}
	}

	private SearchCriteria constructNewSearchCriteria() {
		return new SearchCriteria() {
			public Currency getCurrency() {
				return null;
			}
			public Set<Long> getFilteredUids() {
				return null;
			}
			public List<Filter< ? >> getFilters() {
				return null;
			}
			public IndexType getIndexType() {
				return null;
			}
			public Locale getLocale() {
				return null;
			}
			public SortOrder getSortingOrder() {
				return null;
			}
			public SortBy getSortingType() {
				return null;
			}
			public boolean isFuzzySearchDisabled() {
				return false;
			}
			public boolean isMatchAll() {
				return false;
			}
			public void optimize() {
				// stub
			}
			public void setCurrency(final Currency currency) {
				// stub
			}
			public void setFilterUids(final Set<Long> filterUids) {
				// stub
			}
			public void setFilters(final List<Filter< ? >> filters) {
				// stub
			}
			public void setFuzzySearchDisabled(final boolean fuzzySearchDisabled) {
				// stub
			}
			public void setLocale(final Locale locale) {
				// stub
			}
			public void setMatchAll(final boolean matchAll) {
				// stub
			}
			public void setSortingOrder(final SortOrder sortingOrder) {
				// stub
			}
			public void setSortingType(final SortBy sortingType) {
				// stub
			}
			public ElasticPath getElasticPath() {
				return null;
			}
			public Utility getUtility() {
				return null;
			}
			public void initialize() {
				// stub
			}
			public void setElasticPath(final ElasticPath elasticpath) {
				// stub
			}
			@Override
			public SearchCriteria clone() throws CloneNotSupportedException {
				return null;
			}
			public <T> void addSearchHint(final SearchHint<T> searchHint) {
				// TODO Auto-generated method stub
				
			}
			public <T> SearchHint<T> getSearchHint(final String hintId) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}
