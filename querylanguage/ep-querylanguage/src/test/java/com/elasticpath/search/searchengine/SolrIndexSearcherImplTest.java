/**
 * 
 */
package com.elasticpath.search.searchengine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.domain.misc.impl.SearchConfigImpl;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.query.NativeQuery;

/**
 * Test cases for SolrIndexSearcherImpl.
 */
public class SolrIndexSearcherImplTest {

	/**
	 * Test that a Solr query is correctly constructed from an EP query; the start index is set, the limit is set, and the EpQuery's Lucene query is
	 * set to the Solr query's query string.
	 */
	@Test
	public void testCreateSolrQuery() {
		SolrIndexSearcherImpl searcher = new SolrIndexSearcherImpl();
		final int epQueryLimit = 50;
		final int epQueryStartIndex = 5;
		final int paramLimit = 100;
		final int paramStartIndex = 10;
		final String queryString = "The query string.";
		EpQuery epQuery = new EpQuery();
		NativeQuery luceneQuery = new NativeQuery() {
			public String getNativeQuery() {
				return queryString;
			}
		};
		epQuery.setNativeQuery(luceneQuery);

		SearchConfig mockSearchConfig = new SearchConfigImpl();

		// case 1. epQuery.limit is blank, epQuery.start is blank
		SolrIndexSearchResult<Long> result = new SolrIndexSearchResult<Long>();
		SolrQuery solrQuery = searcher.createSolrQuery(epQuery, paramStartIndex, paramLimit, mockSearchConfig, result);

		assertEquals(String.valueOf(paramStartIndex), solrQuery.get(CommonParams.START));
		assertEquals(String.valueOf(paramLimit), solrQuery.get(CommonParams.ROWS));
		assertEquals(queryString, solrQuery.get(CommonParams.Q));

		// case 2. epQuery.limit is blank, epQuery.start is set
		epQuery.setStartIndex(epQueryStartIndex);
		result = new SolrIndexSearchResult<Long>();
		solrQuery = searcher.createSolrQuery(epQuery, paramStartIndex, paramLimit, mockSearchConfig, result);

		assertEquals(String.valueOf(epQueryStartIndex + paramStartIndex), solrQuery.get(CommonParams.START));
		assertEquals(String.valueOf(paramLimit), solrQuery.get(CommonParams.ROWS));

		// case 3. epQuery.limit is set, epQuery.start is blank
		epQuery.setStartIndex(0);
		epQuery.setLimit(epQueryLimit);
		result = new SolrIndexSearchResult<Long>();
		solrQuery = searcher.createSolrQuery(epQuery, paramStartIndex, paramLimit, mockSearchConfig, result);

		assertEquals(String.valueOf(paramStartIndex), solrQuery.get(CommonParams.START));
		assertEquals(String.valueOf(epQueryLimit - paramStartIndex), solrQuery.get(CommonParams.ROWS));

		// case 4. epQuery.limit is blank, epQuery.start is blank, paramLimit < 0
		epQuery.setStartIndex(0);
		epQuery.setLimit(0);
		result = new SolrIndexSearchResult<Long>();
		solrQuery = searcher.createSolrQuery(epQuery, paramStartIndex, -1, mockSearchConfig, result);

		assertEquals(String.valueOf(paramStartIndex), solrQuery.get(CommonParams.START));
		assertEquals("0", solrQuery.get(CommonParams.ROWS));
	}
}
