/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.search.solr;

import java.util.HashSet;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.domain.misc.impl.SearchConfigImpl;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.SpellSuggestionSearchCriteria;

/**
 * Test <code>SpellIndexSearcherImpl</code>.
 */
public class SolrSpellIndexSearcherImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private SpellSuggestionSearchCriteria spellSuggestionSearchCriteria;

	private SolrProvider solrProvider;

	private SolrServer solrServer;

	private SolrQueryFactory queryFactory;

	private Set<String> spellSuggestions;

	private SolrSpellIndexSearcherImpl spellIndexSearcherImpl;

	/**
	 * Prepare for test.
	 * 
	 * @throws Exception in case of error
	 */
	@Before
	public void setUp() throws Exception {
		spellSuggestionSearchCriteria = context.mock(SpellSuggestionSearchCriteria.class);
		spellSuggestions = new HashSet<String>();
		// no spaces so we don't have to escape them
		spellSuggestions.add("someString");
		spellSuggestions.add("anotherString");
		
		solrServer = context.mock(SolrServer.class);
		solrProvider = context.mock(SolrProvider.class);
		queryFactory = context.mock(SolrQueryFactory.class);
		final SearchConfig searchConfig = new SearchConfigImpl();
		
		context.checking(new Expectations() {
			{
				allowing(spellSuggestionSearchCriteria).getPotentialMisspelledStrings(); will(returnValue(spellSuggestions));
				allowing(spellSuggestionSearchCriteria).getIndexType(); will(returnValue(IndexType.PRODUCT));
				
				allowing(solrProvider).getServer(with(any(IndexType.class))); will(returnValue(solrServer));
				allowing(solrProvider).getSearchConfig(with(any(IndexType.class))); will(returnValue(searchConfig));
				
				allowing(queryFactory).composeSpellingQuery(spellSuggestionSearchCriteria, searchConfig); will(returnValue(new SolrQuery()));
			}
		});

		spellIndexSearcherImpl = new SolrSpellIndexSearcherImpl();
		spellIndexSearcherImpl.setSolrProvider(solrProvider);
		spellIndexSearcherImpl.setSolrQueryFactory(queryFactory);

	}

	/**
	 * Test method for
	 * 'com.elasticpath.persistence.impl.SpellIndexSearcherImpl.search(SpellSuggestionSearchCriteria)'.
	 * @throws SolrServerException in case of a solr server exception
	 */
	@Test
	public void testSuggest() throws SolrServerException {
		final QueryResponse response = new QueryResponse();
		response.setResponse(new NamedList<Object>());
		context.checking(new Expectations() {
			{
				oneOf(solrServer).query(with(any(SolrParams.class))); will(returnValue(response));
			}
		});
		spellIndexSearcherImpl.suggest(spellSuggestionSearchCriteria);
	}
}
