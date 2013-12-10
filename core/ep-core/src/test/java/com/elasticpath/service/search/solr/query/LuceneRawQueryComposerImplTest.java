package com.elasticpath.service.search.solr.query;

import static org.junit.Assert.fail;

import org.junit.Test;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.search.Query;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.LuceneRawSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Test case for {@link LuceneRawQueryComposerImpl}.
 */
public class LuceneRawQueryComposerImplTest extends QueryComposerTestCase {

	private LuceneRawQueryComposerImpl luceneRawQueryComposerImpl;

	private LuceneRawSearchCriteria searchCriteria;


	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception in case of any errors
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		luceneRawQueryComposerImpl = new LuceneRawQueryComposerImpl();
		luceneRawQueryComposerImpl.setLuceneAnalyzer(new SimpleAnalyzer());
		
		searchCriteria = new LuceneRawSearchCriteria();
		searchCriteria.setIndexType(IndexType.PRODUCT);
	}

	/**
	 * Test method for
	 * {@link LuceneRawQueryComposerImpl#composeFuzzyQueryInternal(SearchCriteria, com.elasticpath.domain.misc.SearchConfig)}
	 * and
	 * {@link LuceneRawQueryComposerImpl#composeQueryInternal(SearchCriteria, com.elasticpath.domain.misc.SearchConfig)}.
	 */
	@Test
	public void testComposeWithNullIndexType() {
		searchCriteria.setIndexType(null);
		
		try {
			luceneRawQueryComposerImpl.composeFuzzyQueryInternal(searchCriteria, getSearchConfig());
			fail("EpServiceException expected");
		} catch (EpServiceException e) { // NOPMD -- AvoidEmptyCatchBlocks
			// success
		}
		
		try {
			luceneRawQueryComposerImpl.composeQueryInternal(searchCriteria, getSearchConfig());
			fail("EpServiceException expected");
		} catch (EpServiceException e) { // NOPMD -- AvoidEmptyCatchBlocks
			// success
		}
	}
	
	/**
	 * Test method for {@link LuceneRawSearchCriteria#setQuery(String)}.
	 */
	@Test
	public void testQuery() {
		final String queryString = "some:code";
		searchCriteria.setQuery(queryString);

		Query query = luceneRawQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, "some", "code");
		query = luceneRawQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, "some", "code");
	}

	@Override
	protected QueryComposer getComposerUnderTest() {
		return luceneRawQueryComposerImpl;
	}

	@Override
	protected SearchCriteria getCriteriaUnderTest() {
		return searchCriteria;
	}
}
