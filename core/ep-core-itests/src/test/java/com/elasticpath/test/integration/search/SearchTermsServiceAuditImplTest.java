package com.elasticpath.test.integration.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.query.SearchTerms;
import com.elasticpath.domain.search.query.SearchTermsActivity;
import com.elasticpath.domain.search.query.SearchTermsMemento.SearchTermsId;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.search.query.SearchTermsActivityStrategy;
import com.elasticpath.service.search.query.impl.SearchTermsServiceImpl;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Integration tests for {@link com.elasticpath.service.search.query.impl.SearchTermsServiceImpl}.
 */
public class SearchTermsServiceAuditImplTest extends BasicSpringContextTest {
	private static final String KEYWORDS1 = "keyword1 keyword2";

	@Autowired
	private SearchTermsServiceImpl searchTermsService;

	@Autowired
	private PersistenceEngine persistenceEngine;

	/**
	 * Sets up each test.
	 */
	@Before
	public void setUp() {
		SearchTermsActivityStrategy searchTermsActivityStrategy = getBeanFactory().getBean("loggingSearchTermsActivityStrategy");
		searchTermsService.setSearchTermsActivityStrategy(searchTermsActivityStrategy);
	}

	private SearchTerms createSearchTerms(final String keywords) {
		SearchTerms searchTerms = getBeanFactory().getBean(ContextIdNames.SEARCH_TERMS);
		searchTerms.setKeywords(keywords);
		return searchTerms;
	}

	/**
	 * Activity should be logged when saving.
	 */
	@DirtiesDatabase
	@Test
	public void testActivityLoggedSave() {
		SearchTerms searchTerms1 = createSearchTerms(KEYWORDS1);
		SearchTermsId id1 = searchTermsService.saveIfNotExists(searchTerms1);

		List<SearchTermsActivity> searchActivity = getSearchTermsActivity(id1);
		assertFalse("Acvitity not logged", searchActivity.isEmpty());
		assertEquals("1 save should only have a single log entry", 1, searchActivity.size());
	}

	private List<SearchTermsActivity> getSearchTermsActivity(final SearchTermsId identifier) {
		return persistenceEngine.retrieve("SELECT s FROM SearchTermsActivityImpl s WHERE s.searchTerms.guid = ?1", identifier.getValue());
	}

	/**
	 * Activity should be logged even if a memento already exists for a search term.
	 */
	@DirtiesDatabase
	@Test
	public void testActivityLoggedSaveTwice() {
		SearchTerms searchTerms1 = createSearchTerms(KEYWORDS1);
		SearchTermsId id1 = searchTermsService.saveIfNotExists(searchTerms1);
		SearchTerms searchTerms2 = createSearchTerms(KEYWORDS1);
		SearchTermsId id2 = searchTermsService.saveIfNotExists(searchTerms2);
		assertTrue("Equal keywords for search terms should give back the same ID", id1.equals(id2));

		List<SearchTermsActivity> searchActivity = getSearchTermsActivity(id1);
		assertFalse("Activity not logged", searchActivity.isEmpty());
		assertEquals("2 saves should have 2 log entries", 2, searchActivity.size());
	}

	/**
	 * Activity should be logged when a load occurs.
	 */
	@DirtiesDatabase
	@Test
	public void testActivityLoggedLoad() {
		SearchTerms searchTerms1 = createSearchTerms(KEYWORDS1);
		SearchTermsId id1 = searchTermsService.saveIfNotExists(searchTerms1);

		List<SearchTermsActivity> searchActivity = getSearchTermsActivity(id1);
		assertFalse("Acvitity not logged", searchActivity.isEmpty());
		assertEquals("1 save should only have a single log entry", 1, searchActivity.size());

		searchTermsService.load(id1);
		searchActivity = getSearchTermsActivity(id1);
		assertEquals("Load didn't trigger a log entry", 2, searchActivity.size());
	}
}
