package com.elasticpath.cmweb.jobs.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.query.SearchTerms;
import com.elasticpath.domain.search.query.SearchTermsActivity;
import com.elasticpath.domain.search.query.SearchTermsActivitySummary;
import com.elasticpath.domain.search.query.SearchTermsMemento.SearchTermsId;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.query.SearchTermsService;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.integration.junit.DatabaseHandlingTestExecutionListener;
import com.elasticpath.test.persister.TestApplicationContext;

@ContextConfiguration("/integration-context.xml")
@TestExecutionListeners({
		DatabaseHandlingTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
})
public class SearchTermsAggregatorJobImplTest extends AbstractJUnit4SpringContextTests {

	private static final String KEYWORDS1 = "keyword1 keyword2";
	private static final String KEYWORDS2 = "keyword3 keyword4";
	@Autowired
	private TestApplicationContext tac;
	@Autowired
	private BeanFactory beanFactory;
	@Autowired
	@Qualifier("searchTermsService")
	private SearchTermsService searchTermsService;
	@Autowired
	@Qualifier(ContextIdNames.PERSISTENCE_ENGINE)
	private PersistenceEngine persistenceEngine;
	
	private SearchTermsAggregatorJobImpl searchTermsAggregatorJob;

	@Before
	public void setUp() throws Exception {
		TimeService timeService = beanFactory.getBean("timeService");

		searchTermsAggregatorJob = new SearchTermsAggregatorJobImpl();
		searchTermsAggregatorJob.setPersistenceEngine(persistenceEngine);
		searchTermsAggregatorJob.setTimeService(timeService);
	}

	private void updateSearchTermsActivity() {
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		/*
		 * Since we instantiate searchTermsAggregatorJob (instead of having spring do it), it won't be wrapped in a
		 * transaction proxy --- help it out.
		 */
		tac.getTxTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(final TransactionStatus arg0) {
				searchTermsAggregatorJob.updateSearchTermsActivity();
				return null;
			}
		});
	}

	/**
	 * Test aggregation of counts with a single log entry.
	 */
	@Test
	@DirtiesDatabase
	public void singleSearch() {
		SearchTerms searchTerms = createSearchTerms(KEYWORDS1);
		SearchTermsId identifier = searchTermsService.saveIfNotExists(searchTerms);

		List<SearchTermsActivity> searchTermsActivity = getSearchTermsActivity(identifier);
		assertEquals("Single search log expected", 1, searchTermsActivity.size());

		updateSearchTermsActivity();

		SearchTermsActivitySummary summary = getSearchTermsActivitySummary(identifier);
		assertNotNull("Summary not updated!", summary);
		assertEquals("Count didn't update", 1, summary.getSearchCount());
		assertEquals("Dates don't match!", searchTermsActivity.get(0).getLastAccessDate(), summary.getLastAccessDate());
	}

	/**
	 * Test aggregation of counts with multiple log entries.
	 */
	@Test
	@DirtiesDatabase
	public void multipleSearchesSingleTerm() {
		SearchTerms searchTerms = createSearchTerms(KEYWORDS1);
		SearchTermsId identifier = searchTermsService.saveIfNotExists(searchTerms);
		searchTermsService.load(identifier);

		List<SearchTermsActivity> searchTermsActivity = getSearchTermsActivity(identifier);
		assertEquals("2 search logs expected", 2, searchTermsActivity.size());

		updateSearchTermsActivity();

		SearchTermsActivitySummary summary = getSearchTermsActivitySummary(identifier);
		assertNotNull("Summary not updated!", summary);
		assertEquals("Count didn't update after save then load", 2, summary.getSearchCount());
		assertEquals("Incorrect date updated", searchTermsActivity.get(1).getLastAccessDate(), summary.getLastAccessDate());
	}

	/**
	 * Test aggregation of counts with multiple log entries with more than one search terms.
	 */
	@Test
	@DirtiesDatabase
	public void multipleSearchesMultipleTerms() {
		SearchTerms searchTerms = createSearchTerms(KEYWORDS1);
		SearchTerms searchTerms2 = createSearchTerms(KEYWORDS2);
		SearchTermsId identifier = searchTermsService.saveIfNotExists(searchTerms);
		searchTermsService.saveIfNotExists(searchTerms2);
		searchTermsService.load(identifier);

		List<SearchTermsActivity> searchTermsActivity = getSearchTermsActivity(identifier);
		assertEquals("2 search logs expected", 2, searchTermsActivity.size());

		updateSearchTermsActivity();

		SearchTermsActivitySummary summary = getSearchTermsActivitySummary(identifier);
		assertNotNull("Summary not updated!", summary);
		assertEquals("Count didn't update after save then load", 2, summary.getSearchCount());
		assertEquals("Incorrect date updated", searchTermsActivity.get(1).getLastAccessDate(), summary.getLastAccessDate());
	}

	/**
	 * Test aggregation of counts with multiple log entries when a count needs to be updated.
	 */
	@Test
	@DirtiesDatabase
	public void singleMultipleUpdate() {
		SearchTerms searchTerms = createSearchTerms(KEYWORDS1);
		SearchTermsId identifier = searchTermsService.saveIfNotExists(searchTerms);

		List<SearchTermsActivity> searchTermsActivity = getSearchTermsActivity(identifier);
		assertEquals("Single search log expected", 1, searchTermsActivity.size());

		updateSearchTermsActivity();

		searchTermsService.load(identifier);
		searchTermsActivity = getSearchTermsActivity(identifier);
		assertEquals("One saerch log expected", 1, searchTermsActivity.size());

		updateSearchTermsActivity();

		SearchTermsActivitySummary summary = getSearchTermsActivitySummary(identifier);
		assertNotNull("Summary not updated!", summary);
		assertEquals("Count didn't update after save then load", 2, summary.getSearchCount());
		assertEquals("Incorrect date updated", searchTermsActivity.get(0).getLastAccessDate(), summary.getLastAccessDate());
	}

	/**
	 * Test aggregation of counts with multiple log entries when a count needs to be updated when there is more than a
	 * single search term.
	 */
	@Test
	@DirtiesDatabase
	public void singleMultipleUpdateMultipleTerms() {
		SearchTerms searchTerms = createSearchTerms(KEYWORDS1);
		SearchTerms searchTerms2 = createSearchTerms(KEYWORDS2);
		SearchTermsId identifier = searchTermsService.saveIfNotExists(searchTerms);
		SearchTermsId identifier2 = searchTermsService.saveIfNotExists(searchTerms2);

		List<SearchTermsActivity> searchTermsActivity1 = getSearchTermsActivity(identifier);
		List<SearchTermsActivity> searchTermsActivity2 = getSearchTermsActivity(identifier2);
		
		assertEquals("Single search log expected", 1, searchTermsActivity1.size());

		updateSearchTermsActivity();

		searchTermsService.load(identifier);
		searchTermsActivity1 = getSearchTermsActivity(identifier);
		assertEquals("One search log expected", 1, searchTermsActivity1.size());

		updateSearchTermsActivity();

		SearchTermsActivitySummary summary = getSearchTermsActivitySummary(identifier);
		assertNotNull("Summary not updated!", summary);
		assertEquals("Count didn't update after save then load", 2, summary.getSearchCount());
		assertEquals("Incorrect date updated", searchTermsActivity1.get(0).getLastAccessDate(), summary.getLastAccessDate());

		SearchTermsActivitySummary summary2 = getSearchTermsActivitySummary(identifier2);
		assertNotNull("Summary not updated!", summary2);
		assertEquals("Count wasn't accurate", 1, summary2.getSearchCount());
		assertEquals("Incorrect date updated", searchTermsActivity2.get(0).getLastAccessDate(), summary2.getLastAccessDate());
	}

	@DirtiesDatabase
	@Test
	public void testCleanup() {
		SearchTerms searchTerms = createSearchTerms(KEYWORDS1);
		SearchTermsId identifier = searchTermsService.saveIfNotExists(searchTerms);
		for (int i = 0; i < 10; ++i) {
			searchTermsService.load(identifier);
		}

		assertTrue("There should be a bunch of activities.", getSearchTermsActivity(identifier).size() > 10);
		updateSearchTermsActivity();
		assertEquals("Old activity should be cleaned up.", 0, getSearchTermsActivity(identifier).size());
	}

	private SearchTermsActivitySummary getSearchTermsActivitySummary(final SearchTermsId identifier) {
		// Need to wrap the call in a transaction as the workaround for TSR-372 to avoid a ClassCastException in the persitence engine evict call
		return tac.getTxTemplate().execute(
				new TransactionCallback<SearchTermsActivitySummary>() {
					@Override
					public SearchTermsActivitySummary doInTransaction(final TransactionStatus arg0) {
				List<SearchTermsActivitySummary> result = persistenceEngine.retrieve(
						"SELECT s FROM SearchTermsActivitySummaryImpl s WHERE s.searchTermsInternal.guid = ?1", identifier.getValue());
						if (result == null || result.isEmpty()) {
							return null;
						}
						return result.get(0);
					}
				});
	}

	private List<SearchTermsActivity> getSearchTermsActivity(final SearchTermsId identifier) {
		// Need to wrap the call in a transaction as the workaround for TSR-372 to avoid a ClassCastException in the persitence engine evict call
		return tac.getTxTemplate().execute(
				new TransactionCallback<List<SearchTermsActivity>>() {
					@Override
					public List<SearchTermsActivity> doInTransaction(final TransactionStatus arg0) {
				return persistenceEngine.retrieve("SELECT s FROM SearchTermsActivityImpl s WHERE s.searchTerms.guid = ?1 ORDER BY s.lastAccessDate",
						identifier.getValue());
					}
				});
	}

	private SearchTerms createSearchTerms(final String keywords) {
		SearchTerms searchTerms = beanFactory.getBean(ContextIdNames.SEARCH_TERMS);
		searchTerms.setKeywords(keywords);
		return searchTerms;
	}
	
}
