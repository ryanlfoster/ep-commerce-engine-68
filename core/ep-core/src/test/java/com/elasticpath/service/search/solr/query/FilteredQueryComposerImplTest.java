package com.elasticpath.service.search.solr.query;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.index.QueryComposerFactory;
import com.elasticpath.service.search.query.FilteredSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Test case for {@link FilteredQueryComposerImpl}.
 */
public class FilteredQueryComposerImplTest extends QueryComposerTestCase {

	private FilteredQueryComposerImpl filteredQueryComposerImpl;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private QueryComposer mockInnerComposer;

	private FilteredSearchCriteria<SearchCriteria> searchCriteria;
	
	private QueryComposerFactory mockQueryComposerFactory;

	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mockInnerComposer = context.mock(QueryComposer.class);
		searchCriteria = new FilteredSearchCriteria<SearchCriteria>();
		
		mockQueryComposerFactory = context.mock(QueryComposerFactory.class);
		context.checking(new Expectations() {
			{
				allowing(mockQueryComposerFactory).getComposerForCriteria(with(any(SearchCriteria.class)));
				will(returnValue(mockInnerComposer));
			}
		});

		filteredQueryComposerImpl = new FilteredQueryComposerImpl();
		filteredQueryComposerImpl.setQueryComposerFactory(mockQueryComposerFactory);
	}

	/**
	 * Test method for wrong search criteria.
	 */
	@Test
	public void testWrongSearchCriteria() {
		final SearchCriteria wrongSearchCriteria = new AbstractSearchCriteriaImpl() {
			private static final long serialVersionUID = -1478075783644843843L;

			public void optimize() {
				// do nothing
			}

			public IndexType getIndexType() {
				return null;
			}
		};

		try {
			filteredQueryComposerImpl.composeQuery(wrongSearchCriteria, getSearchConfig());
			fail("EpServiceException expected for wrong search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		try {
			filteredQueryComposerImpl.composeFuzzyQuery(wrongSearchCriteria, getSearchConfig());
			fail("EpServiceException expected for wrong search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for empty search criteria.
	 */
	@Test
	public void testEmptyCriteria() {
		try {
			filteredQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for no parameters in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		try {
			filteredQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for no parameters in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Tests for a single search criteria, no filters.
	 */
	@Test
	public void testWithCriteriaOnly() {
		final SearchCriteria mockSearchCriteria = context.mock(SearchCriteria.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria).getIndexType();
				will(returnValue(IndexType.PRODUCT));
			}
		});
		final SearchCriteria innerCriteria = mockSearchCriteria;
		searchCriteria.addCriteria(innerCriteria);

		final String queryKey = "sdsdf";
		final String queryValue = "fslfjksljf";
		context.checking(new Expectations() {
			{

				oneOf(mockInnerComposer).composeQuery(with(same(innerCriteria)), with(same(getSearchConfig())));
				will(returnValue(new TermQuery(new Term(queryKey, queryValue))));
			}
		});

		Query query = filteredQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, queryKey, queryValue);
		context.checking(new Expectations() {
			{

				oneOf(mockInnerComposer).composeFuzzyQuery(with(same(innerCriteria)), with(same(getSearchConfig())));

				will(returnValue(new TermQuery(new Term(queryKey, queryValue))));
			}
		});

		query = filteredQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, queryKey, queryValue);
	}

	/**
	 * Tests for two search criteria's, no filters.
	 */
	@Test
	public void testWith2CriteriaOnly() {
		final SearchCriteria mockSearchCriteria = context.mock(SearchCriteria.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria).getIndexType();
				will(returnValue(IndexType.PRODUCT));
			}
		});
		final SearchCriteria innerCriteria = mockSearchCriteria;
		searchCriteria.addCriteria(innerCriteria);

		final SearchCriteria mockSearchCriteria2 = context.mock(SearchCriteria.class, "mockSearchCriteria2");
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria2).getIndexType();
				will(returnValue(IndexType.PRODUCT));
			}
		});
		final SearchCriteria innerCriteria2 = mockSearchCriteria2;
		searchCriteria.addCriteria(innerCriteria2);

		final String queryKey = "1111111";
		final String queryValue = "33333333";
		final String queryKey2 = "aaaaaaa";
		final String queryValue2 = "bbbbbbbbb";
		context.checking(new Expectations() {
			{

				oneOf(mockInnerComposer).composeQuery(with(same(innerCriteria)), with(same(getSearchConfig())));

				will(returnValue(new TermQuery(new Term(queryKey, queryValue))));

				oneOf(mockInnerComposer).composeQuery(with(same(innerCriteria2)), with(same(getSearchConfig())));
				will(returnValue(new TermQuery(new Term(queryKey2, queryValue2))));
			}
		});

		Query query = filteredQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, queryKey, queryValue);
		assertQueryContains(query, queryKey2, queryValue2);
		context.checking(new Expectations() {
			{

				oneOf(mockInnerComposer).composeFuzzyQuery(with(same(innerCriteria)), with(same(getSearchConfig())));

				will(returnValue(new TermQuery(new Term(queryKey, queryValue))));

				oneOf(mockInnerComposer).composeFuzzyQuery(with(same(innerCriteria2)), with(same(getSearchConfig())));
				will(returnValue(new TermQuery(new Term(queryKey2, queryValue2))));
			}
		});

		query = filteredQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, queryKey, queryValue);
		assertQueryContains(query, queryKey2, queryValue2);
	}

	/**
	 * Tests for a single search criteria, one filter.
	 */
	@Test
	public void testWithCriteriaAndFilter() {
		final IndexType indexType = IndexType.PRODUCT;

		final SearchCriteria mockSearchCriteria = context.mock(SearchCriteria.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria).getIndexType();
				will(returnValue(indexType));
			}
		});
		final SearchCriteria innerCriteria = mockSearchCriteria;

		final SearchCriteria mockFilterCriteria = context.mock(SearchCriteria.class, "mockSearchCriteria2");
		context.checking(new Expectations() {
			{
				allowing(mockFilterCriteria).getIndexType();
				will(returnValue(indexType));
			}
		});
		final SearchCriteria innerFilterCriteria = mockFilterCriteria;
		searchCriteria.addCriteria(innerCriteria, innerFilterCriteria);

		final String queryKey = "first key";
		final String queryValue = "first value";
		final String filterQueryKey = "filter key";
		final String filterQueryValue = "filter value";
		context.checking(new Expectations() {
			{

				oneOf(mockInnerComposer).composeQuery(with(same(innerCriteria)), with(same(getSearchConfig())));

				will(returnValue(new TermQuery(new Term(queryKey, queryValue))));

				oneOf(mockInnerComposer).composeQuery(with(same(innerFilterCriteria)), with(same(getSearchConfig())));
				will(returnValue(new TermQuery(new Term(filterQueryKey, filterQueryValue))));
			}
		});

		Query query = filteredQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, queryKey, queryValue);
		assertQueryContains(query, filterQueryKey, filterQueryValue);
		context.checking(new Expectations() {
			{

				oneOf(mockInnerComposer).composeFuzzyQuery(with(same(innerCriteria)), with(same(getSearchConfig())));

				will(returnValue(new TermQuery(new Term(queryKey, queryValue))));

				oneOf(mockInnerComposer).composeFuzzyQuery(with(same(innerFilterCriteria)), with(same(getSearchConfig())));
				will(returnValue(new TermQuery(new Term(filterQueryKey, filterQueryValue))));
			}
		});

		query = filteredQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, queryKey, queryValue);
		assertQueryContains(query, filterQueryKey, filterQueryValue);
	}
	
	/**
	 * Tests for two search criteria's, one filter.
	 */
	@Test
	public void testWith2CriteriaAndFilter() {
		final IndexType indexType = IndexType.PRODUCT;

		final SearchCriteria mockSearchCriteria = context.mock(SearchCriteria.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria).getIndexType();
				will(returnValue(indexType));
			}
		});
		final SearchCriteria innerCriteria = mockSearchCriteria;
		
		final SearchCriteria mockSearchCriteria2 = context.mock(SearchCriteria.class, "mockSearchCriteria2");
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria2).getIndexType();
				will(returnValue(indexType));
			}
		});
		final SearchCriteria innerCriteria2 = mockSearchCriteria2;

		final SearchCriteria mockFilterCriteria = context.mock(SearchCriteria.class, "mockFilterCriteria");
		context.checking(new Expectations() {
			{
				allowing(mockFilterCriteria).getIndexType();
				will(returnValue(indexType));
			}
		});
		final SearchCriteria innerFilterCriteria = mockFilterCriteria;
		
		searchCriteria.addCriteria(innerCriteria, innerFilterCriteria);
		searchCriteria.addCriteria(innerCriteria2, innerFilterCriteria);

		final String queryKey = "first key";
		final String queryValue = "first value";
		final String queryKey2 = "bbbbbbbbbbbbbb";
		final String queryValue2 = "44444444444";
		final String filterQueryKey = "filter key";
		final String filterQueryValue = "filter value";
		context.checking(new Expectations() {
			{

				oneOf(mockInnerComposer).composeQuery(with(same(innerCriteria)), with(same(getSearchConfig())));

				will(returnValue(new TermQuery(new Term(queryKey, queryValue))));

				oneOf(mockInnerComposer).composeQuery(with(same(innerCriteria2)), with(same(getSearchConfig())));
				will(returnValue(new TermQuery(new Term(queryKey2, queryValue2))));

				exactly(2).of(mockInnerComposer).composeQuery(with(same(innerFilterCriteria)), with(same(getSearchConfig())));
				will(returnValue(new TermQuery(new Term(filterQueryKey, filterQueryValue))));
			}
		});

		Query query = filteredQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, queryKey, queryValue);
		assertQueryContains(query, filterQueryKey, filterQueryValue);
		assertQueryContains(query, queryKey2, queryValue2);
		context.checking(new Expectations() {
			{

				oneOf(mockInnerComposer).composeFuzzyQuery(with(same(innerCriteria)), with(same(getSearchConfig())));

				will(returnValue(new TermQuery(new Term(queryKey, queryValue))));

				oneOf(mockInnerComposer).composeFuzzyQuery(with(same(innerCriteria2)), with(same(getSearchConfig())));
				will(returnValue(new TermQuery(new Term(queryKey2, queryValue2))));

				exactly(2).of(mockInnerComposer).composeFuzzyQuery(with(same(innerFilterCriteria)), with(same(getSearchConfig())));
				will(returnValue(new TermQuery(new Term(filterQueryKey, filterQueryValue))));
			}
		});

		query = filteredQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, queryKey, queryValue);
		assertQueryContains(query, filterQueryKey, filterQueryValue);
		assertQueryContains(query, queryKey2, queryValue2);
	}

	@Override
	protected QueryComposer getComposerUnderTest() {
		return filteredQueryComposerImpl;
	}

	@Override
	protected SearchCriteria getCriteriaUnderTest() {
		return searchCriteria;
	}
}
